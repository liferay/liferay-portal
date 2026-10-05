---

paths:
  - "**/src/test/**/*Test.java"
  - "**/src/testIntegration/**/*Test.java"

---

# Java Testing

When creating or editing a JUnit test, apply these conventions before treating the test as done. Every one of them has been flagged in review on a test that was otherwise correct, and every one is cheaper to follow in the first draft than to fix in the second review round. The canonical rules in `pr-reviewer/rules` govern the rest.

## Match the Sibling

Read the nearest existing test in the same package first and match its idiom: naming, mocking depth, and structure. The sibling is usually the canonical example, and rule 001 makes its convention the one to follow.

## Mock Only What the Assertions Exercise

For every stub, ask whether the test still passes when the stub is deleted. When it does, delete the stub. Defensive overmocking is the most common miss.

Declare each mock in the order it is used, the outer consumer first and the leaf last, and write its `Mockito.when` stub only once everything it returns is ready, so the stubs run leaf first and work back outward (rule 203). Never chain a call off a value that is not a builder: assign it to a named local first (rule 402).

## Randomize What Is Not Asserted

Use `RandomTestUtil` for every value the test does not assert on, an exception message or a JSON value included. Keep a literal only for a value the test checks (rule 602).

## Inline Single Use Helpers

A private helper called from one place whose body is a single delegated call is inlined, unless it is a `_setUp*` helper for shared mock setup (rule 504), and so is a local assigned once and passed straight to its only use (rule 503). Name a test method `test` plus the method it tests, keeping its `is` or `has` prefix, rather than a prose description of the scenario (rule 603).

## No Explanatory Comments

Do not explain the test in comments; put the rationale in the commit message instead. The comments that do belong are short labels for scenario blocks, sorted per rule 205, and rule 905 governs their form.