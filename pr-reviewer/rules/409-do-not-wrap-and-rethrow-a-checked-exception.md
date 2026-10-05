# 409: Do Not Wrap and Rethrow a Checked Exception

Do not catch a checked exception only to wrap it in a `RuntimeException`, a `PortalException`, or another broader type and throw that instead. The first choice is to let the original exception propagate by declaring it on the method with `throws`, and to let each caller up the chain do the same until a caller that can actually handle it appears.

When the signature cannot change, because the method overrides an interface, implements a lambda, or serves a callback whose contract forbids the checked type, wrapping is the necessary escape hatch. Use a known utility such as `ReflectionUtil.throwException`, or throw a domain specific runtime type that takes the original as its cause, so the original type and stack frame survive.

**Rationale:** A wrapped exception replaces a precise type with a generic one, so a caller that could have caught `IOException` now has to catch everything and inspect the cause, and the log shows the wrapping frame first. Declaring the exception costs one `throws` clause and keeps the type, the message, and the frame exactly as they were raised. A blanket `try`/`catch` also pads every method with the same five lines, which hides the one line that does the work.

A violation is a `catch` block whose only statement throws a new exception constructed from the caught one, in a method whose `throws` clause could have declared the caught type instead. Do not flag a wrap inside a method whose signature is fixed by a supertype or a functional interface, a `catch` that adds handling beyond rethrowing (logging at the right level and returning a fallback, cleaning up, or translating to a documented API error), or a rethrow through `ReflectionUtil.throwException`.

**Example:** a method that caught the `IOException` from `parser.read(input)` and threw `new PortalException(ioException)` was reduced to the bare `parser.read(input)` call, with `IOException` added to the method's `throws` clause.

```diff
-try {
-	parser.read(input);
-}
-catch (IOException ioException) {
-	throw new PortalException(ioException);
-}
+parser.read(input);
```