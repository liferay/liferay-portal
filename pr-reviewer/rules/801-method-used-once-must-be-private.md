# 801: A Method Used Once Must Be Private

A method called from only one place must be `private`, and, per house style, prefixed with an underscore. The same holds for a method whose every caller lives inside its own class, however many callers it has: nothing outside the class invokes it, so it is `private` and takes the underscore prefix. A `protected` method is justified only when it overrides a base class or base test case method; otherwise make it `private`.

**Rationale:** Visibility should match reach. A method with a single caller that is not an override has no reason to be `protected`, package visible, or `public` — wider visibility advertises an extension point that does not exist and invites callers that should not exist. Narrowing it to `private` states its true scope and keeps the class's surface honest. Default or `public` visibility on a class internal method overstates the contract in the same way: a reader cannot tell from the signature whether external callers exist, and tooling cannot prune the method when its callers go away.

A violation is a nonoverriding method declared `protected`, package visible, or `public` whose callers are all inside its own class, one or many, where it should be `private`.

**Example:** commit `cbe50a17` states the rule directly ("If it's a protected method, it must @Override a base test case method. Otherwise, make it private.") and renames a `protected` helper to a `private` one; `6093ee34` and `752e65f` made the same `protected` to `private` change for single caller methods. A package visible `_doInternal()` that only the class itself calls is the many caller form of the same fix.

```diff
-void _doInternal() {
+private void _doInternal() {
 	// ...
 }
```