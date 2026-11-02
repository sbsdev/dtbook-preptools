import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.RegionSkipper;
import ch.sbs.utils.preptools.vform.VFormUtil;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Strip <brl:v-form> from gold DTBooks, run VFormUtil.findMidSentence and
 * compare with where the transcribers had put the markup. Of the candidates
 * that are left to the user, also count those that VFormUtil.isSingularSie
 * skips.
 *
 * CORPUS_LIST is a file with one path per line to DTBooks that already
 * contain hand-made v-form markup. MAX_ERRORS (default 40) limits how many
 * wrong markings are printed.
 *
 * Usage:
 *   mvn -B -q compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
 *   javac --release 17 -cp target/classes:$(cat target/cp.txt) -d target/tools tools/EvalMidSentence.java
 *   java -cp target/tools:target/classes:$(cat target/cp.txt) EvalMidSentence CORPUS_LIST [MAX_ERRORS]
 */
public class EvalMidSentence {
    static final Pattern TAG = Pattern.compile("</?brl:v-form>");

    public static void main(String[] args) throws Exception {
        int maxErrors = args.length > 1 ? Integer.parseInt(args[1]) : 40;
        long candidates = 0, gold = 0, auto = 0, autoWrong = 0, goldMissedByAuto = 0, verbSkipped = 0, verbSkippedGold = 0;
        List<String> errors = new ArrayList<>();
        Map<String, long[]> perBook = new TreeMap<>();
        int books = 0;
        for (String path : Files.readAllLines(Paths.get(args[0]))) {
            String orig = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            // strip the gold markup, remember where each v-form started
            StringBuilder sb = new StringBuilder();
            Set<Integer> goldStarts = new HashSet<>();
            Matcher t = TAG.matcher(orig);
            int last = 0;
            while (t.find()) {
                sb.append(orig, last, t.start());
                if (!t.group().startsWith("</")) goldStarts.add(sb.length());
                last = t.end();
            }
            sb.append(orig.substring(last));
            String text = sb.toString();
            books++;
            gold += goldStarts.size();
            long[] pb = perBook.computeIfAbsent(Paths.get(path).getFileName() + " " + path.hashCode() % 1000, k -> new long[4]);
            pb[0] = goldStarts.size();

            Set<Integer> autoStarts = new HashSet<>();
            for (Match m : VFormUtil.findMidSentence(text, RegionSkipper.getDefaultSkipper())) {
                autoStarts.add(m.startOffset);
            }
            RegionSkipper skipper = RegionSkipper.getDefaultSkipper();
            skipper.findRegionsToSkip(text);
            Matcher c = VFormUtil.get3rdPPPattern().matcher(text);
            while (c.find()) {
                if (skipper.inSkipRegion(c) || (c.start() > 0 && Character.isLetterOrDigit(text.charAt(c.start() - 1))))
                    continue;
                candidates++;
                boolean isGold = goldStarts.contains(c.start());
                if (autoStarts.contains(c.start())) {
                    auto++;
                    pb[1]++;
                    if (!isGold) {
                        pb[2]++;
                        autoWrong++;
                        if (errors.size() < maxErrors) errors.add(Paths.get(path).getFileName() + ": " + context(text, c.start(), c.end()));
                    }
                } else if (VFormUtil.isSingularSie(text, c.start(), c.end())) {
                    verbSkipped++;
                    if (isGold) {
                        verbSkippedGold++;
                        errors.add(Paths.get(path).getFileName() + " (verb rule): " + context(text, c.start(), c.end()));
                    }
                } else if (isGold) {
                    goldMissedByAuto++;
                }
            }
        }
        System.out.printf("%d books, %d candidates, %d gold v-forms%n", books, candidates, gold);
        System.out.printf("auto-marked: %d (%.1f%% of candidates), of which not gold: %d (%.2f%%)%n",
                auto, 100.0 * auto / candidates, autoWrong, 100.0 * autoWrong / auto);
        System.out.printf("skipped by the verb rule: %d (%.1f%% of candidates), of which gold: %d%n",
                verbSkipped, 100.0 * verbSkipped / candidates, verbSkippedGold);
        System.out.printf("left for the user: %d, of which gold: %d%n",
                candidates - auto - verbSkipped, goldMissedByAuto);
        System.out.println("\nper book: gold, auto, auto-not-gold");
        for (var e : perBook.entrySet()) System.out.printf("  %-40s %6d %6d %5d%n", e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2]);
        System.out.println("\nauto-marked but not gold, or gold but skipped by the verb rule:");
        for (String e : errors) System.out.println("  " + e);
    }

    static String context(String text, int s, int e) {
        String before = text.substring(Math.max(0, s - 70), s), after = text.substring(e, Math.min(text.length(), e + 40));
        return (before + "[[" + text.substring(s, e) + "]]" + after).replaceAll("\\s+", " ");
    }
}
