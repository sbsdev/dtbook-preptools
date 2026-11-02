# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

An oXygen XML Editor plugin (Java 17, so oXygen 25 or later; Maven) that helps SBS staff enrich DTBook XML with markup needed for Braille translation (vforms, ordinals, roman numerals, measures, abbreviations, URLs/emails, page breaks, accents, paren balancing). `HACKING.org` has a per-class and per-preptool description — read it before larger changes.

## Commands

```
mvn -B package                              # build jar + plugin zip (target/*-plugin.zip)
mvn -B test                                 # run all JUnit 4 tests
mvn -B test -Dtest=RegexOrdinalTest         # single test class
mvn -B test -Dtest=RegexOrdinalTest#testX   # single test method
mvn -B spotless:apply                       # format (palantir-java-format); run before committing
mvn -B exec:exec                            # regenerate GTAGS
mvn release:prepare && mvn release:perform  # release to Maven Central (tags v<version>)
```

User-visible changes go into `CHANGELOG.md` under `[Unreleased]`; on a release, that section gets the version and date.

The `oxygen` dependency comes from the oxygenxml Maven repo declared in `pom.xml`.

## Architecture

- **Packaging**: `src/main/resources/plugin.xml` is Maven-filtered (`${project.version}` and the dependency versions) and bundled with the jar and the runtime deps into a zip by `src/assembly/plugin-assembly.xml`. The runtime deps are OpenNLP, its German POS model and slf4j-api. A new one needs its own version property in `pom.xml` and a `<library>` entry in `plugin.xml`; any property referenced in `plugin.xml` must exist in `pom.xml`, otherwise the library paths in the installed plugin are wrong. oXygen and log4j 1.x are excluded from the zip.
- **Entry point**: `PrepToolsPlugin` → `PrepToolsPluginExtension` (WorkspaceAccess extension) builds menus/toolbar, wraps oXygen's "Revert"/"Save as" via `MenuPlugger` so per-document prep tool state can be reset, and tracks per-document state in `DocumentMetaInfo`.
- **Prep tools**: `PrepToolLoader` is the single registry: it defines every search regex as a constant and instantiates the tools in menu order (with mnemonics). Tool class hierarchy lives in `PrepTool.java` (`PrepTool` → `AbstractMarkupPrepTool` → `RegexPrepTool`/`FullRegexPrepTool`/`VFormPrepTool`, plus `ParensPrepTool`, `PageBreakPrepTool`, `AccentPrepTool`). The Start/Find/Change Swing actions for all tools are package-private classes in `AbstractPrepToolAction.java`. Adding a tool = regex constant + entry in `loadPrepTools` + (if needed) custom action subclasses.
- **Text, not DOM**: tools operate on the raw `javax.swing.text.Document` text with regexes, never on a parsed XML tree (see HACKING.org for why). XML tags and already-marked-up regions are hidden from matching by `RegionSkipper` (a `BitSet` of skipped characters) and `MarkupUtil`. `PositionMatch` uses Swing `Position`s so matches survive concurrent edits; `OxygenEditGrouper` groups edits into one undo step.
- **Pure logic in `ch.sbs.utils`** (`MarkupUtil`, `DocumentUtils`, `VFormUtil`, `ParensUtil`, …) is oXygen-independent and is where nearly all tests are. Regex behaviour is tested directly against the `PrepToolLoader` constants (`Regex*Test`, `*RegexTest`), so regex changes should come with a test case there.
- VForm word lists and their old/new-spelling rationale (German) are in `VFormUtil` / `vform_specs.txt`. A "Sie" followed by a singular verb ("Sie hatte") is skipped as "she"; the verb is detected with the OpenNLP POS tagger (UD tags), the number by the ending. `tools/EvalMidSentence.java` measures the v-form automation against hand-marked DTBooks.

## Debugging in oXygen

Start oXygen with `oxygen25.0 -J-Xrunjdwp:transport=dt_socket,address=<port>,server=y,suspend=y|n` and attach a remote debugger.
