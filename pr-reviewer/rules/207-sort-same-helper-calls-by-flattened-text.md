# 207: Sort Same Helper Calls by Flattened Text

When a test method calls the same private helper several times in a row, each time with different arguments, those calls are a sortable sequence. Sort them as if every statement were written on one line: compare the flattened statement text left to right, ASCII and case sensitive, so the first argument that differs decides the order. Although rule 202 already names consecutive method calls and case sensitive ASCII order for literals, this rule spells out how that comparison runs across a multiline call with several positional arguments, because that is the form the reviewer sorts by hand.

The comparison is plain ASCII, so these tiebreaks decide real cases:

- Arguments compare in parameter order and the first difference wins. `_REQUEST_BODY_LENGTH_MAX` sorts before `_REQUEST_BODY_LENGTH_OVERSIZED`, and `HttpMethod.GET` before `HttpMethod.PATCH` before `HttpMethod.POST` before `HttpMethod.PUT`, whatever the later arguments are.
- After a shared literal prefix, the closing quote (`"`, 34) sorts before any letter, so `"v1.0/" + RandomTestUtil.randomString()` precedes `"v1.0/agent-instances/batch"`, which precedes `"v1.0/reports/batch"`.
- A quoted literal (`"`, 34) precedes a bare identifier or call, so `"v1.0/reports"` precedes `_getMessagesPath()`. Among identifiers, uppercase letters (65 to 90) precede the underscore (95), which precedes lowercase letters (97 to 122), so `RandomTestUtil.randomBytes()` precedes `_getMessagesPath()`, which precedes `new byte[...]`.

**Rationale:** The reviewer checks this literally, by joining each call onto one line and running the editor's sort, so a block that does not match that output is sent back. Beyond getting through review, one predictable order lets a reader find the scenario with a given method and path at a glance, gives every new scenario one obvious slot, and keeps the diff to the added lines instead of a reshuffle.

A violation is a run of consecutive calls to one helper, with no blank line between them, whose order is not the order of their flattened text. A blank line ends the sequence: sort within each blank line delimited group and leave the groups where they are. The rule also yields to a real dependency, such as a call that consumes a value an earlier call produced. Do not flag an order that is already correct even when it looks reversed to a case insensitive eye: `RandomTestUtil.randomBytes()` (`R`, 82) correctly precedes `new byte[_LENGTH_MAX]` (`n`, 110). When the statements are assertions whose verb or accessor varies, the flattened text is the wrong key; see rule 208.

**Example:** PR 50573 (`LPD-104430`) in `brianchandotcom/liferay-portal-ee`, where Brian Chan asked for an "ascii/case sensitive sort" of `testFilter` and `testFilterRequestEntityTooLarge`, showed the order he expects by joining each call onto one line and running his editor's sort, and fixed it himself in commit `414327f` when merging. The middle three calls below were in narrative order, the two named resources first and the random path last; the flattened text puts the random path first because its closing quote follows `v1.0/` immediately. In the sibling method the calls differed in the HTTP method first, so `HttpMethod.PATCH` moved ahead of every `HttpMethod.POST` call and `HttpMethod.PUT` moved to the end, whatever the path.

```diff
 	_testFoo(
 		RandomTestUtil.randomBytes(), _LENGTH_OVERSIZED, HttpMethod.GET,
 		_getBarPath());
 	_testFoo(
 		RandomTestUtil.randomBytes(), _LENGTH_OVERSIZED, HttpMethod.POST,
-		"v1.0/bars/batch");
+		"v1.0/" + RandomTestUtil.randomString());
 	_testFoo(
 		RandomTestUtil.randomBytes(), _LENGTH_OVERSIZED, HttpMethod.POST,
-		"v1.0/bazs/batch");
+		"v1.0/bars/batch");
 	_testFoo(
 		RandomTestUtil.randomBytes(), _LENGTH_OVERSIZED, HttpMethod.POST,
-		"v1.0/" + RandomTestUtil.randomString());
+		"v1.0/bazs/batch");
 	_testFoo(
 		RandomTestUtil.randomBytes(), _LENGTH_OVERSIZED, HttpMethod.POST,
 		RandomTestUtil.randomString());
```