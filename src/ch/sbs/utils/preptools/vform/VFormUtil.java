package ch.sbs.utils.preptools.vform;

import java.util.regex.Pattern;

import ch.sbs.utils.preptools.Match;

public class VFormUtil {
	// Mail von Mischa Kuenzle 12.1.2011 15:09
	// 3. Person PL (obligatorische Abfrage)
	private static final String[] thirdPP = new String[] { "Ihnen", "Ihr",
			"Ihre", "Ihrem", "Ihren", "Ihrer", "Ihrerseits", "Ihres",
			"Ihresgleichen", "Ihrethalben", "Ihretwegen", "Ihretwillen",
			"Ihrige", "Ihrigem", "Ihrigen", "Ihriger", "Ihriges", "Ihrs",
			"Sie", };

	// Mail von Mischa Kuenzle 12.1.2011 15:09
	// 2. Person (optionale Abfrage)
	private static final String[] secondP = new String[] { "Dein", "Deine",
			"Deinem", "Deinen", "Deiner", "Deinerseits", "Deines",
			"Deinesgleichen", "Deinethalben", "Deinetwegen", "Deinetwillen",
			"Deinige", "Deinigem", "Deinigen", "Deiniger", "Deiniges", "Deins",
			"Dich", "Dir", "Du", "Euch", "Euer", "Euere", "Euerem", "Euerer",
			"Eueres", "Euers", "Euerseits", "Eure", "Eurem", "Euren",
			"Eurerseits", "Eures", "Euresgleichen", "Eurethalben",
			"Euretwegen", "Euretwillen", "Eurige", "Eurigem", "Eurigen",
			"Euriger", "Euriges", };

	private static final String[] forms3rdPersonPlural = new String[] { WordHierarchyBuilder
			.createWordTree(thirdPP).toRegex() };

	private static final String[] forms2ndPerson = new String[] { WordHierarchyBuilder
			.createWordTree(secondP).toRegex() };

	private static final String[] allForms;

	static {
		allForms = new String[forms3rdPersonPlural.length
				+ forms2ndPerson.length];
		int i = 0;
		for (final String[] forms : new String[][] { forms3rdPersonPlural,
				forms2ndPerson }) {
			for (final String form : forms) {
				allForms[i++] = form;
			}
		}
	}

	private static Pattern vFormDefaultPattern;
	private static final Pattern vFormPatternAll;
	private static final Pattern vFormPattern3rdPersonPlural;

	static {
		vFormPatternAll = makePattern(allForms);
		vFormPattern3rdPersonPlural = makePattern(forms3rdPersonPlural);
		vFormDefaultPattern = vFormPatternAll;
	}

	public static Pattern get3rdPPPattern() {
		return vFormPattern3rdPersonPlural;
	}

	public static Pattern getAllPattern() {
		return vFormPatternAll;
	}

	private static Pattern makePattern(final String[] forms) {
		final StringBuffer sb = new StringBuffer("(?:"); // non-capturing group,
															// see
		// http://download.oracle.com/javase/1.5.0/docs/api/java/util/regex/Pattern.html#special
		for (final String form : forms) {
			sb.append(form);
			sb.append("|");
		}
		sb.setLength(sb.length() - 1); // chop off last "|"
		sb.append(")\\b"); // make sure we don't match substrings.
		return Pattern.compile(sb.toString());
	}

	/**
	 * Returns Match where the pattern occurs or NULL_MATCH
	 * 
	 * @param text
	 *            Text to search
	 * @param start
	 *            index where to start
	 * @param pattern
	 *            pattern to match
	 * @return Match
	 */
	public static Match find(final String text, int start, final Pattern pattern) {
		return new MarkupUtil("v-form").find(text, start, pattern);
	}

	/**
	 * Does the same as @see find using the default pattern.
	 * 
	 * @param text
	 * @param start
	 * @return
	 */
	public static Match find(final String text, int start) {
		return find(text, start, vFormDefaultPattern);
	}

	/**
	 * Indicates whether pattern matches entire region
	 * 
	 * @param region
	 * @param pattern
	 * @return
	 */
	public static boolean matches(final String region, final Pattern pattern) {
		return region != null && pattern.matcher(region).matches();
	}

	/**
	 * Does the same as matches for the default pattern
	 * 
	 * @param text
	 * @return
	 */
	public static boolean matches(final String text) {
		return matches(text, vFormDefaultPattern);
	}
}
