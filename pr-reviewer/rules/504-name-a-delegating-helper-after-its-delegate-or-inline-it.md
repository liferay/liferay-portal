# 504: Name a Delegating Helper After Its Delegate or Inline It

A private method with exactly one call site, whose body carries out a single delegated call, takes the name of the method it delegates to, or it goes away and its body moves to that one call site. A body counts as a single delegated call even when the call is wrapped in a `try`/`catch` and its arguments are built inline, as long as nothing else happens around it. With only one caller, inlining is usually the resolution, because a wrapper named after its delegate and called once adds a hop and no meaning; the rename is what saves a helper that has earned its keep some other way, most often by picking up a second caller.

**Rationale:** A helper's name is a promise that reading it saves the reader from reading the body. When the body is one call, the name replaces a precise operation with a looser paraphrase of what that operation means here, so the reader has to open the helper anyway to learn which method actually runs. Taking the delegate's own word keeps one vocabulary from the call site down to the API being called, and deleting the helper outright removes the indirection the reader was paying for. `pr-reviewer/STYLE.md` names the wrapper case in its judgment list ("if removing something would not be noticed — a variable, a comment, a guard, an assertion, a wrapper, a `finally` — remove it") without codifying it, which is the gap this rule fills. The trigger differs from the neighboring rules: rule 502 inlines a `private static final` constant used once and says nothing about methods, rule 801 decides the visibility of a single caller method and is satisfied the moment the method is `private`, and rule 105 fires on a name that misdescribes its body, so it stays silent here, since `_auditFooReceive` accurately describes what the method does and is still the wrong name.

A violation is a `private` method with one caller whose body is a single delegated call, named for the effect the call has in the domain rather than for the call itself, for example `_auditFooReceive` around one `AuditRouterUtil.route(...)`. Do not flag a delegating wrapper named after its delegate that has two or more call sites (a `_invoke` around `TransactionInvokerUtil.invoke`, say, with its own `catch` and `ReflectionUtil.throwException`), which is exactly what this rule asks for; and do not flag a single caller helper whose body is real logic (several statements, a guard, a cast, a comparison), because there the name carries meaning the body does not spell out.

**Example:** PR `50646` on `brianchandotcom/liferay-portal-ee` (LPD-105141) pointed the author at commit `6032879`, titled "Call the method _route or inline it", which deleted `_auditChatMessageReceive` from `AgentInstanceManagerImpl` and inlined its one `AuditRouterUtil.route` call, `try`/`catch` and all, into `invoke`; the same disposition recurs under many authors, in commits titled "Inline the single caller test helpers", "Inline the nested object entry request helper", and "Inline the trivial account entry fetch".

```diff
 	public Object invoke(FooContext fooContext) {

-		_auditFooReceive(fooContext);
+		try {
+			AuditRouterUtil.route(
+				Foo.class.getName(), fooContext.getKey(), new Date(),
+				FooEventTypes.FOO_RECEIVE, fooContext.getUserId());
+		}
+		catch (Exception exception) {
+			if (_log.isWarnEnabled()) {
+				_log.warn(exception);
+			}
+		}

 		Object[] bars = fooContext.getBars();

-	private void _auditFooReceive(FooContext fooContext) {
-		try {
-			AuditRouterUtil.route(
-				Foo.class.getName(), fooContext.getKey(), new Date(),
-				FooEventTypes.FOO_RECEIVE, fooContext.getUserId());
-		}
-		catch (Exception exception) {
-			if (_log.isWarnEnabled()) {
-				_log.warn(exception);
-			}
-		}
-	}
```