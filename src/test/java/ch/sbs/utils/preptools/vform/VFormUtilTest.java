/*
 * Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
 *
 * This file is part of dtbook-preptools.
 *
 * dtbook-preptools is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package ch.sbs.utils.preptools.vform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ch.sbs.plugin.preptools.VFormActionHelper;
import ch.sbs.utils.preptools.MarkupUtil;
import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.RegionSkipper;
import org.junit.Test;

public class VFormUtilTest {

    @Test
    public void testMatchesNull() {
        assertFalse(VFormUtil.matchesAll(null));
    }

    @Test
    public void testBug1383() {
        assertTrue(VFormUtil.matchesAll("Eurer"));
        assertTrue(VFormUtil.matchesAll("Euerm"));
        assertTrue(VFormUtil.matchesAll("Euern"));
        assertTrue(VFormUtil.matchesAll("Eueren"));
    }

    @Test
    public void testReplaceMatches() {
        assertTrue(VFormUtil.matchesAll("Deine"));
        assertFalse(VFormUtil.matchesAll("Sieb"));
    }

    @Test
    public void testNoMatchesMarkup() {
        assertTrue(VFormUtil.matchesAll("Deine"));
        assertFalse(VFormUtil.matchesAll(MarkupUtil.wrap("Deine", "brl:v-form")));
    }

    @Test
    public void testReplaceKeep() {
        MarkupUtil mu = new MarkupUtil(RegionSkipper.getCommentSkipper());
        assertEquals(Match.NULL_MATCH, mu.find("Sieb", 0, VFormUtil.getAllPattern()));
        final String text = "Sieben können Sie haben.";
        final Match match = mu.find(text, 0, VFormUtil.getAllPattern());
        final String vform = "Sie";
        assertEquals(text.lastIndexOf(vform), match.startOffset);
        assertEquals(text.lastIndexOf(vform) + vform.length(), match.endOffset);
    }

    @Test
    public void testMatch() {

        final String text = "Das können Sie zu Ihren Akten legen.";
        final MarkupUtil mu = new MarkupUtil(RegionSkipper.getCommentSkipper());

        Match match = mu.find(text, 0, VFormUtil.getAllPattern());

        final String vform = "Sie";
        assertEquals(text.indexOf(vform), match.startOffset);
        assertEquals(text.indexOf(vform) + vform.length(), match.endOffset);

        match = mu.find(text, match.endOffset, VFormUtil.getAllPattern());

        final String vform2 = "Ihren";
        assertEquals(text.indexOf(vform2), match.startOffset);
        assertEquals(text.indexOf(vform2) + vform2.length(), match.endOffset);

        match = mu.find(text, match.endOffset, VFormUtil.getAllPattern());

        assertEquals(Match.NULL_MATCH, match);
    }

    @Test
    public void testMatchBoundary() {

        final String text = "Dann können Sie's Ihrem Kollegen geben.";
        final MarkupUtil mu = new MarkupUtil(RegionSkipper.getCommentSkipper());

        Match match = mu.find(text, 0, VFormUtil.getAllPattern());

        final String vform = "Sie";
        assertEquals(text.indexOf(vform), match.startOffset);
        assertEquals(text.indexOf(vform) + vform.length(), match.endOffset);

        match = mu.find(text, match.endOffset, VFormUtil.getAllPattern());

        final String vform2 = "Ihrem";
        assertEquals(text.indexOf(vform2), match.startOffset);
        assertEquals(text.indexOf(vform2) + vform2.length(), match.endOffset);

        match = mu.find(text, match.endOffset, VFormUtil.getAllPattern());

        assertEquals(Match.NULL_MATCH, match);
    }

    @Test
    public void testNoMatch() {

        final MarkupUtil mu = new MarkupUtil(RegionSkipper.getCommentSkipper());
        final String text = "Dann kann Anna es ihrem Kollegen geben.";

        final Match match = mu.find(text, 0, VFormUtil.getAllPattern());

        assertEquals(Match.NULL_MATCH, match);
    }

    @Test
    public void testMatch1() {

        final MarkupUtil mu = new MarkupUtil(RegionSkipper.getCommentSkipper());
        final String text = "Dann kann Anna es Ihrem Kollegen geben.";

        final Match match = mu.find(text, 0, VFormUtil.getAllPattern());

        final String vform = "Ihrem";
        assertEquals(text.indexOf(vform), match.startOffset);
        assertEquals(text.indexOf(vform) + vform.length(), match.endOffset);
    }

    @Test
    public void testNoMatch1() {

        final String tag = "brl:v-form";
        final MarkupUtil mu = new MarkupUtil(RegionSkipper.makeMarkupRegionSkipper(tag));

        assertEquals(
                Match.NULL_MATCH,
                mu.find(
                        "Dann kann Anna es " + MarkupUtil.wrap("Ihrem", tag) + " Kollegen geben.",
                        0,
                        VFormUtil.getAllPattern()));
    }

    @Test
    public void testSettingPatternAll() {

        final String text = "Dann kann Anna es Deinem Kollegen geben.";
        final MarkupUtil mu = new MarkupUtil(RegionSkipper.makeMarkupRegionSkipper(VFormActionHelper.VFORM_TAG));
        final Match match = mu.find(text, 0, VFormUtil.getAllPattern());

        final String vform = "Deinem";
        assertEquals(text.indexOf(vform), match.startOffset);
        assertEquals(text.indexOf(vform) + vform.length(), match.endOffset);
    }

    @Test
    public void testSettingPattern3rdPP() {

        final MarkupUtil mu = new MarkupUtil(RegionSkipper.makeMarkupRegionSkipper(VFormActionHelper.VFORM_TAG));
        assertEquals(
                mu.find("Dann kann Anna es Deinem Kollegen geben.", 0, VFormUtil.get3rdPPPattern()), Match.NULL_MATCH);
    }

    /**
     * Calls {@link VFormUtil#isMidSentence} for the form marked with a "[" in
     * text, e.g. "Er ging. [Sie blieb." asks whether "Sie" is in the middle of
     * a sentence. The "[" is removed before the call.
     */
    private static boolean midSentence(final String text) {
        final int start = text.indexOf("[");
        return VFormUtil.isMidSentence(text.replace("[", ""), start);
    }

    @Test
    public void testMidSentence() {
        assertTrue(midSentence("Wir danken [Ihnen für Ihre Geduld."));
        assertTrue(midSentence("Haben [Sie das gesehen?"));
        assertTrue(midSentence("Was <em>wollen</em> [Sie?"));
        assertTrue(midSentence("Kommen <em>[Sie</em> herein."));
        assertTrue(midSentence("Wir warten&nbsp;[Sie ab."));
        assertTrue(midSentence("Lesen\n        [Sie weiter."));
    }

    @Test
    public void testSentenceStart() {
        assertFalse(midSentence("[Sie kam."));
        assertFalse(midSentence("Er ging. [Sie blieb."));
        assertFalse(midSentence("<p>[Sie blieb."));
        assertFalse(midSentence("<h1>Was</h1> [Sie wissen"));
        assertFalse(midSentence("Er rief: [Sie kommen!"));
        assertFalse(midSentence("Er sagte: »[Sie kommen.«"));
        assertFalse(midSentence("»Ja.« [Sie lachte."));
        assertFalse(midSentence("ins Ohr.) [Sie schien"));
        assertFalse(midSentence("Er sagte &quot;[Sie"));
        assertFalse(midSentence("<!-- x --> [Sie"));
        assertFalse(midSentence("Er hielt inne … [Sie"));
    }

    @Test
    public void testSentenceStartBehindMarkup() {
        // vform_specs.txt 1.1.3.2, example 1 and 2
        assertFalse(midSentence(
                "Er ging zur Arbeit. <pagenum id=\"page-7\" page=\"normal\">7</pagenum>\n»[Sie sind zu spät"));
        assertFalse(midSentence("Er ging zur <em>Migros.</em>\n»[Sie schon wieder"));
        assertFalse(midSentence("Er ging zur Arbeit. <pagenum id=\"page-7\" page=\"normal\">7</pagenum> [Sie kam"));
        assertTrue(midSentence("wollte sie <pagenum id=\"page-95\" page=\"normal\">95</pagenum>mit [Ihnen"));
    }

    @Test
    public void testFindMidSentence() {
        final String text = "<p>Sie kam. Darf ich Ihnen helfen? Das Siegel und das Ihre und Sie\u00ADben, "
                + "<brl:v-form>Sie</brl:v-form> und WieSie, Ihretwegen.</p>";
        final RegionSkipper skipper = RegionSkipper.makeMarkupRegionSkipper(VFormActionHelper.VFORM_TAG);
        final StringBuilder found = new StringBuilder();
        for (final Match m : VFormUtil.findMidSentence(text, skipper)) {
            found.append(text, m.startOffset, m.endOffset).append(' ');
        }
        assertEquals("Ihnen Ihretwegen ", found.toString());
    }
}
