# Changelog

What's new for the users of the DTBook preptools. The format follows
[Keep a Changelog](https://keepachangelog.com/).

## [Unreleased]

### Changed

- **oXygen 25 or later is required.** Older versions of oXygen are no
  longer supported. When installing, replace the whole
  `plugins/dtbook-preptools` folder rather than unzipping over it, since
  the bundled libraries have changed.

### Added

- **The VForms preptool marks the obvious polite forms itself.** When
  you press Start, every polite form in the middle of a sentence ("Wir
  danken Ihnen", "Haben Sie …") is marked up right away. You then only
  step through the forms at the start of a sentence, where it isn't
  clear whether they are polite.

- **The VForms preptool skips "Sie" when it means "she".** A "Sie"
  followed by a verb in the singular ("Sie hatte", "Sie lächelte", "Sie
  war") is no longer offered for markup. If the verb could be plural
  ("Sie haben", "Sie sind", "Sie brachten"), you still decide. Together
  with the automatic markup, this cuts the number of stops by more than
  half.

### Fixed

- **The preptools work in documents opened in the Author page.** Current
  oXygen versions open DTBooks in the Author page, which left the
  preptools disabled and caused an error when switching to the Text
  page.

- **No more "the cursor has moved" question after switching pages.**
  Switching to the Author page in the middle of a preptool run and back
  to Text lost the selection.

- **Words with accents and umlauts are found again in oXygen versions
  with a newer Java.** In oXygen versions with Java 19 or later, e.g.
  oXygen 28, the Accents and Abbreviations preptools missed words
  starting or ending with a non-ASCII letter (Évora, café, Üb.). The
  Ordinals, Roman Numerals and Measures preptools were affected in rare
  cases as well.

## [1.4] - 2023-03-30

Earlier versions are only documented in the git history.

[Unreleased]: https://github.com/sbsdev/dtbook-preptools/compare/v1.4...HEAD
[1.4]: https://github.com/sbsdev/dtbook-preptools/releases/tag/v1.4
