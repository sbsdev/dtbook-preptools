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

import ch.sbs.utils.preptools.MarkupUtil;
import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.RegionSkipper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VFormUtil {
    // Mail von Mischa Kuenzle 12.1.2011 15:09
    // 1. Person PL (obligatorische Abfrage)
    private static final String[] thirdPP = new String[] {
        "Ihnen",
        "Ihr",
        "Ihre",
        "Ihrem",
        "Ihren",
        "Ihrer",
        "Ihrerseits",
        "Ihres",
        "Ihresgleichen",
        "Ihrethalben",
        "Ihretwegen",
        "Ihretwillen",
        "Ihrige",
        "Ihrigem",
        "Ihrigen",
        "Ihriger",
        "Ihriges",
        "Ihrs",
        "Sie",
    };

    // Mail von Mischa Kuenzle 12.1.2011 15:09
    // Mail von Mischa Kuenzle 8.6.2011 10:53
    // 2. Person (optionale Abfrage)
    private static final String[] secondP = new String[] {
        "Dein",
        "Deine",
        "Deinem",
        "Deinen",
        "Deiner",
        "Deinerseits",
        "Deines",
        "Deinesgleichen",
        "Deinethalben",
        "Deinetwegen",
        "Deinetwillen",
        "Deinige",
        "Deinigem",
        "Deinigen",
        "Deiniger",
        "Deiniges",
        "Deins",
        "Dich",
        "Dir",
        "Du",
        "Euch",
        "Euer",
        "Euere",
        "Euerem",
        "Euerer",
        "Eueres",
        "Euers",
        "Euerseits",
        "Eure",
        "Eurem",
        "Euren",
        "Eurer",
        "Euerm",
        "Euern",
        "Eueren",
        "Eurerseits",
        "Eures",
        "Euresgleichen",
        "Eurethalben",
        "Euretwegen",
        "Euretwillen",
        "Eurige",
        "Eurigem",
        "Eurigen",
        "Euriger",
        "Euriges",
    };

    private static final Pattern vFormPatternAll;
    private static final Pattern vFormPattern3rdPersonPlural;

    static {
        final Set<String> allForms = new HashSet<String>(thirdPP.length + secondP.length);
        allForms.addAll(Arrays.asList(thirdPP));
        allForms.addAll(Arrays.asList(secondP));
        // the trailing word boundary makes the regex try the next alternative
        // when a shorter form is only a prefix, e.g. "Ihr" in "Ihre"
        vFormPatternAll = Pattern.compile(wrapInWordBoundaries(String.join("|", allForms)));
        vFormPattern3rdPersonPlural = Pattern.compile(wrapInWordBoundaries(String.join("|", thirdPP)));
    }

    private static String wrapInWordBoundaries(final String pattern) {
        return "(?:" + pattern + ")(?U:\\b)";
    }

    public static Pattern get3rdPPPattern() {
        return vFormPattern3rdPersonPlural;
    }

    public static Pattern getAllPattern() {
        return vFormPatternAll;
    }

    /**
     * Does the same as matches for the all pattern.
     *
     * @param text
     *            text to match
     * @return true if text matches all pattern
     */
    public static boolean matchesAll(final String text) {
        return MarkupUtil.matches(text, vFormPatternAll);
    }

    // Elements that don't interrupt a sentence. Any other tag (p, h1, li,
    // line, sent, br, comments, ...) counts as the start of a sentence.
    private static final Set<String> INLINE_ELEMENTS = new HashSet<String>(Arrays.asList(
            "a",
            "abbr",
            "acronym",
            "bdo",
            "cite",
            "code",
            "dfn",
            "em",
            "kbd",
            "samp",
            "span",
            "strong",
            "sub",
            "sup",
            "var",
            "brl:computer",
            "brl:date",
            "brl:emph",
            "brl:homograph",
            "brl:name",
            "brl:num",
            "brl:place",
            "brl:time",
            "brl:v-form"));

    // Inline elements whose content is not part of the sentence
    private static final Set<String> SKIPPED_ELEMENTS =
            new HashSet<String>(Arrays.asList("annoref", "linenum", "noteref", "pagenum"));

    // See vform_specs.txt, 1.1.3.1. Quotes, brackets and dashes are treated
    // like a sentence start as well, so direct speech gets asked about.
    private static final String SENTENCE_END = ".:!?…";
    private static final String OPENERS = "„“”\"'«»‹›‚‘’()[]{}–—-/";

    // "das Ihre", "die Ihren": nominalised, usually "theirs" rather than polite
    private static final Pattern ARTICLE_BEFORE = Pattern.compile("(?i)(?<!\\p{L})(?:das|der|die|dem|den|des)\\s+$");

    /**
     * A capitalised polite form in the middle of a sentence is always a
     * v-form. At the start of a sentence it might as well be "she" or "they".
     * Works on the raw xml text, skipping inline markup and page numbers.
     * Anything unclear counts as start of a sentence, so that the user is
     * asked rather than the text being marked up wrongly.
     *
     * @param text
     *            the text containing the form
     * @param start
     *            offset of the form in text
     * @return true if the form at start is in the middle of a sentence
     */
    public static boolean isMidSentence(final String text, final int start) {
        int i = start - 1;
        if (i >= 0 && Character.isLetterOrDigit(text.charAt(i))) {
            return false; // part of a longer word
        }
        while (i >= 0) {
            final char c = text.charAt(i);
            if (Character.isWhitespace(c) || Character.isSpaceChar(c)) {
                i--;
            } else if (c == '>') {
                final int open = text.lastIndexOf('<', i);
                if (open < 0) {
                    return false;
                }
                final String tag = text.substring(open + 1, i);
                final String name = tag.replaceFirst("^/", "").split("[\\s/]", 2)[0];
                if (tag.startsWith("/") && SKIPPED_ELEMENTS.contains(name)) {
                    // skip the content up to the opening tag
                    final Matcher m = Pattern.compile("<" + Pattern.quote(name) + "[\\s>]")
                            .matcher(text.substring(0, open));
                    int opening = -1;
                    while (m.find()) {
                        opening = m.start();
                    }
                    if (opening < 0) {
                        return false;
                    }
                    i = opening - 1;
                } else if (INLINE_ELEMENTS.contains(name) || SKIPPED_ELEMENTS.contains(name)) {
                    i = open - 1;
                } else {
                    return false;
                }
            } else if (c == ';') {
                final int amp = text.lastIndexOf('&', i);
                if (amp < 0 || !text.substring(amp, i + 1).matches("&(nbsp|#160|#x[aA]0);")) {
                    return false; // quotes or other entities
                }
                i = amp - 1;
            } else {
                return SENTENCE_END.indexOf(c) < 0 && OPENERS.indexOf(c) < 0;
            }
        }
        return false;
    }

    private static boolean isNominalised(final String text, final Matcher m) {
        return m.group().startsWith("Ihr")
                && ARTICLE_BEFORE
                        .matcher(text.substring(Math.max(0, m.start() - 100), m.start()))
                        .find();
    }

    /**
     * Finds all polite forms (3rd person plural) in the middle of a sentence,
     * i.e. those that can be marked up without asking the user.
     *
     * @param text
     *            the text to search
     * @param skipper
     *            regions to leave alone, e.g. existing markup
     * @return the matches, in document order
     */
    public static List<Match> findMidSentence(final String text, final RegionSkipper skipper) {
        skipper.findRegionsToSkip(text);
        final List<Match> matches = new ArrayList<Match>();
        final Matcher m = vFormPattern3rdPersonPlural.matcher(text);
        while (m.find()) {
            // a soft hyphen doesn't end a word ("Sie\u00ADben")
            final boolean hyphenated = m.end() < text.length() && text.charAt(m.end()) == '\u00AD';
            if (!hyphenated && !skipper.inSkipRegion(m) && isMidSentence(text, m.start()) && !isNominalised(text, m)) {
                matches.add(new Match(m.start(), m.end()));
            }
        }
        return matches;
    }
}
