# Brian Chan's Code Review Style

This document serializes the code style and review judgment of Brian Chan (GitHub `brianchandotcom`), distilled from three years of his commits to `liferay-portal`. It is written to be read by any model or person who wants to review or write code the way he does. The numbered rules in the `rules` directory (grouped into numbered blocks by category, 0xx through 9xx) are the hard, citable subset; this document adds the philosophy behind them and the matters of taste that resist a fixed rule. Where a point is already a rule, its number is given in brackets.

## The overriding rule

Before any rule below: follow the existing convention. When a file already does something a consistent way, match it, even where that departs from a rule here. These rules are the default for new code and for files with no established pattern; they are not a mandate to rewrite twenty years of evolved code. You do not widen the streets of London to match a planned suburb. [001]

When the existing code is not consistent — two or more forms for the same kind of call — pick the form that complies with these rules and apply it across every similar call in the file. A split convention is no convention; do not compound it by adding to either side. [002]

## Philosophy

Five principles run through everything:

1. **Reduce the chaos factor.** Arbitrary order, naming, and layout raise the entropy of the codebase. Impose one predictable form — sorted, named by a fixed convention, laid out the same way every time — so any reader can find and understand a thing at a glance.

1. **Be relentlessly consistent.** Match the surrounding code, the sibling method, and the established convention. Do not introduce a second way to do something the codebase already does one way. When in doubt, mirror what is next to you.

1. **Remove everything that does not earn its place.** Delete redundant assertions, constants and methods used once, narrating comments, guards for cases that cannot happen, throwaway temporary collections, and verbose messages. Let the code speak.

1. **Declare and order things by use.** A reader should meet each value where it is used, in the order the method actually runs.

1. **Prefer the platform.** Reach for the existing Liferay utility before writing the operation by hand or with a raw JDK call. The platform method is null safe, tested, and consistent with the rest of the code.

## Naming

- Uppercase acronyms, but defer to the real class name: `JSONObject` and `getAPIURL`, yet `JsonNode` stays `JsonNode` because that is the class. [101]
- Prefix a boolean method with `is` or `has` — a variable or field does not take the prefix — and prefer `has` over `contains` or `exists`. [102]
- Name a variable after the type or content it holds, not an incidental role; when one instance of a type is in scope, the plain type name is enough. [103]
- When a class owns the plain name (a `Filter` is `filter`), give a different type that holds the same thing a type suffix: a filter string is `filterString`, and a `JSONObject` is `xyzJSONObject` while its serialized string is `xyzJSON`. [104]
- Name a method for what it actually does, not a convenient paraphrase: a helper that creates an account entry user relation is `_associateUser`, not the inaccurate `_addUserToAccount`. [105]
- Across a parallel set, mirror each type name one to one: a method throwing `PrincipalException` is `principalException`, not `principalDenied`, so no stray word lands on some names but not the type. [106]
- Name a value or test to match the method it comes from: the result of `getStatus()` is `status` (not `statusCode`), and a test of it is `testStatusNotFound`. When the value flows into a destination that uses a different conceptual name (a JSON key, a parameter), match the destination instead — `String startDateString = jobStatus.getStartTime(); patchJSONObject.put("startDate", startDateString);` follows the JSON key with the `String` suffix per rule 104. [107]
- Lead sibling uppercase `static final` constants (the `_SCREAMING_SNAKE_CASE` form, not lowercase fields like `_xyz`) with the shared category and order each name general to specific, so the set sorts together and reads as a family: distinct members are `_ANIMAL_HORSE` and `_ANIMAL_ZEBRA`, not `_HORSE_ANIMAL` beside `_ANIMAL_ZEBRA`; number interchangeable members off the stem (`_HORSE_1`, `_HORSE_2`). [108]
- Name a lone `int` index `index` [109], a map entry loop variable `entry` with its parts `key` and `value` [110], and a lambda parameter after the value it stands for rather than a single letter [113].
- A method that returns a collection takes the plural, `getFoos` [111]; paired methods differ only by the operation noun and share every qualifier, `getFoosByGroupIds` beside `getFoosCountByGroupIds` [112]; and the noun before `Count` is plural, `itemsCount` [114].
- Taste: use the precise domain verb (`delete`, not `remove`, for a database deletion). Drop a qualifier the context already implies, but add one when it is needed to tell two instances apart. Encode the real type in the suffix where it adds clarity (`...Page`, `...JSON`); a collection takes the plural, and an `...Array` or `...List` suffix is only for telling an array and a list of the same content apart within a class. The aim is a name that tells the reader exactly what the value is, with nothing redundant and nothing missing.

## Ordering and declaration

- Sort a grouped block of independent variable assignments alphabetically; place a derived assignment after the variables it depends on, near its use. [201]
- Sort every other sortable sequence the same way — method calls on one object, a method's parameters, sibling declarations, and literal lists. Variable, field, and method names sort case insensitively in natural order (per the source formatter's `JavaTermComparator`, so `_criterions` precedes `_criterionType`); literal strings sort case sensitively in ASCII order, with `null` last. Setter calls on a generated model mirror the defining schema instead — `service.xml` columns for a ServiceBuilder entity, `rest-openapi.yaml` fields for a REST DTO. Constructor body assignments follow the parameter order with derived assignments after (see `JavaConstructorParametersCheck`), and a vararg parameter is forced to the end of a parameter list. [202]
- Declare each local immediately before its first use ("as used"). A wrapping object is the exception — the value the method returns, or a local whose sole purpose is to absorb another local through a setter — declared first and closed with the return statement or the setter, so it wraps the variables that build it ("burrito"). [203]
- Sort scenario blocks introduced by an inline comment alphabetically by the comment's text [205], keep a conditional assignment in its alphabetical slot among the plain ones it sits between [206], sort a run of calls to the same test helper by their flattened statement text [207], and sort sibling assertions by the subject they check rather than by the assertion verb [208].
- Sort `helm unittest` cases by their `it` description [209].

## Prefer Liferay utilities

- `GetterUtil` for coercion [301]; `ListUtil`, `SetUtil`, `MapUtil`, and `ArrayUtil.isEmpty` for empty checks [302]; `ArrayUtil.contains` for array membership [303]; `TransformUtil.transform` for a mapped list [304]; `JSONUtil` for building JSON [305]; `StringBundler` over `StringBuilder`, and over a `+` join of more than three pieces [306]; `SystemBundleUtil.escapeFilterValue` for any value in an OSGi filter string that is neither a code constant nor a number [307].
- The principle behind the list: if Liferay already has a utility for the operation, use it. When you catch yourself writing a null and empty check, a map into a new list, or string concatenation, stop and reach for the util.

## Control flow and form

- No `switch` or `case`; use an `if` / `else if` / `else` chain. [401]
- Chain method calls only on a builder or a fluent type (`*Builder`, `Stream`, `Optional`, `Instant`, `JSONArray`, `JSONObject`, and the rest of `ChainingCheck`'s allow list); otherwise assign the result to a local and call on it. [402]
- Use an enhanced `for` loop, not an explicit `Iterator` with `while (hasNext())`. [403]
- Hoist a call out of a loop only when it does real work (database, network, non trivial computation), not for cheap invariant calls like `map.get(key)`, a list index access, or a plain getter — those read more directly inline at the use site. [404]
- In bash under `set -o errexit`, preincrement a counter with `((++var))` rather than `((var++))` or `var=$((var + 1))`; postincrementing from `0` makes the arithmetic expression evaluate to `0`, which exits `1` and trips `errexit`. [405]
- Write a shell test as `[` when its operands need no quotes and as `[[` when `[` would need quotes; never combine `[` with quoted operands. [406]
- Prefer `while` to `do` / `while` [407], and leave early with a guard clause rather than nesting the real work inside a positive `if` [408].
- Do not catch a checked exception only to wrap it in a broader one; declare it or let it propagate [409].
- A shell script under `cloud/` opens with `set -o errexit`, `set -o nounset`, and `set -o pipefail` [410].

## Removing the unnecessary ("Simplify")

This is the most taste driven area and the hardest to reduce to a rule, so it deserves the most explanation. The codified pieces are small: remove an assertion a later line already proves [501], inline a private constant used once [502], pass an expression directly instead of naming it for one call [503], name a delegating helper after its delegate or inline it [504], drop a narrative message from `assertTrue` and `assertFalse` [505], drop a redundant `L` suffix [506], and divide whole unit values with integer arithmetic rather than a defensive `Math.ceil` [507]. The broader instinct, which a reviewer should apply by judgment:

- Prefer the form a careful reader grasps fastest: fewer lines, fewer variables, fewer levels of nesting, fewer moving parts.
- If removing something would not be noticed — a variable, a comment, a guard, an assertion, a wrapper, a `finally` — remove it.
- Collapse a redundant boolean (`if (x) { return true; } return false;` becomes `return x;`) only when `x` is a single term: a boolean variable, field, or method call. When the method returns `boolean` and `x` contains a logical or relational operator (`&&`, `||`, `&`, `|`, `^`, `==`, `!=`, `<`, `>`, `<=`, `>=`), leave the expanded `if (x) { return true; } return false;` form: SourceFormatter's `JavaReturnStatementCheck` rewrites such a `return` back into that form, so collapsing it is a guaranteed format failure. Unwrap a needless ternary, and combine nested conditions when the result reads more directly.
- Replace a hand written loop with the library call that does the same thing.
- When two forms are equivalent, choose the one that matches the surrounding code.
- Do not abstract, generalize, or add flexibility beyond what the change in front of you needs.

The measure of "simpler" is not line count. It is how quickly the next reader understands the code with the least to hold in their head. If a change removes a concept the reader would otherwise have to track, it is simpler, even at the same length.

Simplicity never outranks safety: when the two trade off, choose the safer form.

## Tests

- Place a test in its subject's package: a unit test at `xyz.BarTest` for source `xyz.Bar`, an integration test at `xyz.test.BarTest` for the same source. [604]
- Consolidating trivial parallel test methods is a judgment call: fold many trivial variants into one method, but a few well named scenario methods are fine. [601]
- Name a test method `test` plus the method it tests, keeping its `is` or `has` prefix (`testIsQuotaExceeded`, not `testQuotaExceeded`). [603]
- Randomize any test value you do not assert on, an exception message or JSON value included; keep a literal only for a value the test checks. [602]
- In a `*ResourceTest` that extends a generated `Base*ResourceTestCase`, override every base test method with `@Override @Test`, call `super.testX()` first, and add new scenarios as private `_testX*` helpers called from the override. A new top-level `@Test` method whose name does not match a base test is a violation. [605]
- Remove an `assertNotNull` whose next line already dereferences or asserts something stronger. [501]
- Name a test helper after what it asserts. A method called from one place is `private`. [801]
- Consolidate sibling `@Ignore`d test methods on one subject into one `@Test` with private `_test*` helpers [606], set up state in `@Before` and `@After` rather than their class level counterparts [607], and compare a boxed actual value against a boxed expected value instead of unboxing it [608].

## Prose and user facing text ("Wordsmith")

- Do not hyphenate: close up prefixes (`multiarg`) and separate compounds (`long running`). [701]
- A short label is APA title case, per `capitalizemytitle.com/style/APA`; text that is really a description is a complete sentence ending in a period, or in a colon when it introduces a list or other text that follows. [702]
- A log or exception message is a phrase, a title, or two or more sentences — never a lone sentence with a period. [703]
- Spell out contractions [704], include the articles a sentence needs [705], write "ID" in uppercase in prose [706], and use complete sentences in comments and documentation [707].
- In Markdown, put each paragraph on a single line and let the editor wrap it. [708]
- Wrap a literal token in a message in escaped double quotes [709], keep the linking verb in a status or error message (`Foo is not allowed`, not `Foo not allowed`) [710], put one space after a period [711], and write `cleanup` as the noun and `clean up` as the verb [712].
- A `Language.properties` value names a product, feature, or field by its full official name, identically everywhere it appears [713].
- Taste: expand code identifiers into plain English in prose (`DDM` becomes "dynamic data mapping", `classNameId` becomes "class name ID").

## Visibility and structure

- A method used in one place is `private` and underscore prefixed; a `protected` method is justified only when it overrides a base class or base test case method. [801] Every override carries `@Override`. [802]
- Put a class in the package that matches its sole user or its naming sibling.

## General coding

- In runtime logic, prefer an exact check over a loose one — `equals`, not a substring `contains`; a partial check in a test is fine. [901]
- Do not end a URL or path with a trailing slash. [902]
- Do not end a file with a trailing newline; the source formatter strips it, so the `\ No newline at end of file` marker in a diff is the expected state. [908]
- In shell commands, use long form flags in alphabetical order [903], and put each argument of a multiline command on its own line [904].
- In a shell script, define public functions like `main` before private underscore prefixed ones, sorted alphabetically within each group. [907]
- Begin an inline comment with a capital letter and surround it with a blank line before and after. [905]
- Separate logical groups with a blank line and keep the statements of one step adjacent [909]; in a `try` with resources, put a blank line between independent resources and one built from them [910].