package ch.sbs.plugin.preptools;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ch.sbs.utils.preptools.FileUtils;
import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.parens.ParensUtil;
import ch.sbs.utils.preptools.vform.VFormUtil;

@SuppressWarnings("serial")
abstract class AbstractPrepToolAction extends AbstractAction {
	/**
	 * 
	 */
	protected final PrepToolsPluginExtension workspaceAccessPluginExtension;

	/**
	 * @param theWorkspaceAccessPluginExtension
	 */
	AbstractPrepToolAction(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		workspaceAccessPluginExtension = theWorkspaceAccessPluginExtension;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		if ((workspaceAccessPluginExtension.getPage()) != null) {
			try {
				doSomething();
			} catch (final BadLocationException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Hook that gets called only when editor, page, document, text have
	 * successfully been retrieved.
	 * 
	 * @throws BadLocationException
	 */
	protected abstract void doSomething() throws BadLocationException;

	/**
	 * Utility method that returns the end position of the current selection.
	 * 
	 * @return The end position of the current selection.
	 */
	protected int getSelectionEnd() {
		final WSEditor editorAccess = workspaceAccessPluginExtension
				.getWsEditor();
		final WSTextEditorPage aWSTextEditorPage = PrepToolsPluginExtension
				.getPage(editorAccess);
		return aWSTextEditorPage.getSelectionEnd();
	}

	protected void select(final Match match) {
		workspaceAccessPluginExtension.getPage().select(match.startOffset,
				match.endOffset);
	}

	protected void select(final Match.PositionMatch pm) {
		workspaceAccessPluginExtension.getPage().select(
				pm.startOffset.getOffset(), pm.endOffset.getOffset());
	}
}

@SuppressWarnings("serial")
class VFormStartAction extends AbstractPrepToolAction {
	VFormStartAction(
			final PrepToolsPluginExtension workspaceAccessPluginExtension) {
		super(workspaceAccessPluginExtension);
	}

	@Override
	protected void doSomething() throws BadLocationException {
		final WSEditor editorAccess = workspaceAccessPluginExtension
				.getWsEditor();
		final WSTextEditorPage aWSTextEditorPage = workspaceAccessPluginExtension
				.getPage();
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo();

		final URL editorLocation = editorAccess.getEditorLocation();
		if (dmi.vform.doneChecking()) {
			if (workspaceAccessPluginExtension
					.showConfirmDialog(
							"v-form: Start Over?",
							"The document "
									+ FileUtils.basename(editorLocation)
									+ " has already been vformed.\n Do you want to Start over?")

			) {
				dmi.vform.setDoneChecking(false);
			}
			else {
				return;
			}
		}
		else if (dmi.vform.hasStartedChecking()) {
			if (workspaceAccessPluginExtension
					.showConfirmDialog(
							"v-form: Start Over?",
							"The document "
									+ FileUtils.basename(editorLocation)
									+ " is currently being vformed.\n Do you want to Start over?")

			) {
				dmi.vform.setDoneChecking(false);
			}
			else {
				return;
			}
		}
		final Document document = aWSTextEditorPage.getDocument();
		final Match match = VFormUtil.find(
				document.getText(0, document.getLength()), 0,
				dmi.vform.getCurrentPattern());
		select(match);

		dmi.vform.setHasStartedChecking(true);
		dmi.vform.setDoneChecking(false);
		dmi.setCurrentPositionMatch(new Match.PositionMatch(document, match));
		workspaceAccessPluginExtension.setCurrentState(dmi);
	}
}

@SuppressWarnings("serial")
abstract class ProceedAction extends AbstractPrepToolAction {

	ProceedAction(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	protected DocumentMetaInfo dmi;

	/**
	 * 
	 * Hook to be implemented by subclasses. Handles selected text and returns
	 * position where to continue with search.
	 * 
	 * @param document
	 *            The document.
	 * @param selText
	 *            The current selection.
	 * @return The position where to continue with search.
	 * @throws BadLocationException
	 */
	protected abstract int handleText(final Document document,
			final String selText) throws BadLocationException;

	/**
	 * Hook to be implemented by subclasses. If true the process is aborted.
	 * 
	 * @param selText
	 * @return True if the process is to be aborted.
	 */
	protected boolean veto(final String selText) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.AbstractVFormAction#doSomething(ro.sync.exml.workspace.api.editor.WSEditor, ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage, javax.swing.text.Document, ch.sbs.plugin.preptools.DocumentMetaInfo)
	 */
	@Override
	protected void doSomething() throws BadLocationException {
		final WSEditor editorAccess = workspaceAccessPluginExtension
				.getWsEditor();
		final WSTextEditorPage aWSTextEditorPage = workspaceAccessPluginExtension
				.getPage();
		final DocumentMetaInfo theDmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo();

		dmi = theDmi;
		final String selText = aWSTextEditorPage.getSelectedText();

		if (veto(selText))
			return;

		handleManualCursorMovement(aWSTextEditorPage, dmi);

		final Document document = aWSTextEditorPage.getDocument();
		final int continueAt = handleText(document, selText);

		searchOn(aWSTextEditorPage, editorAccess, continueAt);
	}

	/**
	 * Utility method to search on in document starting at startAt.
	 * 
	 * @param aWSTextEditorPage
	 * @param editorAccess
	 * @param startAt
	 * 
	 * @throws BadLocationException
	 */
	private void searchOn(final WSTextEditorPage aWSTextEditorPage,
			final WSEditor editorAccess, final int startAt)
			throws BadLocationException {
		final Document document = aWSTextEditorPage.getDocument();
		final String newText = document.getText(0, document.getLength());
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo(editorAccess.getEditorLocation());
		final Pattern currentPattern = dmi.vform.getCurrentPattern();
		final Match match = VFormUtil.find(newText, startAt, currentPattern);
		if (match.equals(Match.NULL_MATCH)) {
			workspaceAccessPluginExtension
					.showDialog("You're done with v-forms!");
			dmi.vform.done();
			match.startOffset = 0;
			match.endOffset = 0;
		}
		workspaceAccessPluginExtension.setCurrentState(dmi);
		select(match);
		dmi.setCurrentPositionMatch(new Match.PositionMatch(document, match));
	}

	/**
	 * Utility method to handle user's manual cursor movement.
	 * 
	 * @param aWSTextEditorPage
	 * @param dmi
	 */
	private void handleManualCursorMovement(
			final WSTextEditorPage aWSTextEditorPage, final DocumentMetaInfo dmi) {
		lastMatchStart = aWSTextEditorPage.getSelectionStart();
		lastMatchEnd = aWSTextEditorPage.getSelectionEnd();
		final Match.PositionMatch pm = dmi.getCurrentPositionMatch();
		if (lastMatchStart != pm.startOffset.getOffset()
				|| lastMatchEnd != pm.endOffset.getOffset()
				|| dmi.manualEditOccurred()) {
			if (workspaceAccessPluginExtension.showConfirmDialog(
					"v-form: Cursor", "Cursor position has changed!\n",
					"Take up where we left off last time", "continue anyway")) {
				select(pm);
			}
		}
	}

	protected int lastMatchStart;
	protected int lastMatchEnd;
}

@SuppressWarnings("serial")
class VFormAcceptAction extends ProceedAction {
	VFormAcceptAction(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected boolean veto(final String selText) {
		return (selText == null || !VFormUtil.matches(selText,
				dmi.vform.getCurrentPattern()));
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.ProceedAction#handleText(javax.swing.text.Document, java.lang.String)
	 */
	@Override
	protected int handleText(final Document document, final String selText)
			throws BadLocationException {
		final String ELEMENT_NAME = "brl:v-form";
		// starting with the end, so the start position doesn't shift
		document.insertString(lastMatchEnd, "</" + ELEMENT_NAME + ">", null);
		document.insertString(lastMatchStart, "<" + ELEMENT_NAME + ">", null);

		final int continueAt = lastMatchStart + ELEMENT_NAME.length() * 2
				+ "<></>".length() + selText.length();
		return continueAt;
	}
}

@SuppressWarnings("serial")
class VFormFindAction extends ProceedAction {
	VFormFindAction(
			final PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	/* (non-Javadoc)
	 * @see ch.sbs.plugin.preptools.ProceedAction#handleText(javax.swing.text.Document, java.lang.String)
	 */
	@Override
	protected int handleText(final Document document, final String selText)
			throws BadLocationException {
		return getSelectionEnd();
	}
}

@SuppressWarnings("serial")
abstract class OrphanParenAbstractAction extends AbstractPrepToolAction {

	@Override
	protected void doSomething() throws BadLocationException {
		init();
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo();
		if (dmi.orphanedParens.hasNext()) {
			select(dmi);
		}
		else {
			handleNoneFound();
		}
	}

	protected void init() {

	}

	protected void handleNoneFound() {

	}

	OrphanParenAbstractAction(
			PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	protected void select(DocumentMetaInfo dmi) {
		final Match.PositionMatch match = dmi.orphanedParens.next();
		select(match);
	}

}

@SuppressWarnings("serial")
class OrphanParenStartAction extends OrphanParenAbstractAction {

	OrphanParenStartAction(
			PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected void init() {
		final WSTextEditorPage aWSTextEditorPage = workspaceAccessPluginExtension
				.getPage();
		final DocumentMetaInfo dmi = workspaceAccessPluginExtension
				.getDocumentMetaInfo();
		final Document document = aWSTextEditorPage.getDocument();
		List<Match> orphans = null;
		try {
			orphans = ParensUtil.findOrphans(document.getText(0,
					document.getLength()));
		} catch (BadLocationException e) {
			workspaceAccessPluginExtension.showMessage(e.getMessage());
			e.printStackTrace();
		}
		dmi.orphanedParens.set(orphans);
	}

	@Override
	protected void handleNoneFound() {
		workspaceAccessPluginExtension.showDialog("No orphaned parens found.");
	}

}

@SuppressWarnings("serial")
class OrphanParenFindNextAction extends OrphanParenAbstractAction {

	OrphanParenFindNextAction(
			PrepToolsPluginExtension theWorkspaceAccessPluginExtension) {
		super(theWorkspaceAccessPluginExtension);
	}

	@Override
	protected void handleNoneFound() {
		workspaceAccessPluginExtension
				.showDialog("You're done with orphaned parens.");
	}
}