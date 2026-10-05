# 802: Annotate Every Override

Every method that overrides a superclass method or implements an interface method carries `@Override`, placed with the method's other annotations directly above its signature. The source formatter's `JavaMissingOverrideCheck` can add the annotation itself, but it is disabled in `sourcechecks.xml`, so a missing `@Override` reaches review and the reviewer catches it. Rule 605 already asks for `@Override @Test` on every override in a `*ResourceTest` that extends a generated `Base*ResourceTestCase`; report a missing annotation there under rule 605, and under this rule everywhere else.

**Rationale:** `@Override` makes the override explicit to a reader, who otherwise has to know the supertype to tell an override from a new method, and it makes the compiler reject the method when the supertype signature drifts; a renamed or retyped parameter upstream would otherwise leave a silently orphaned method that no longer overrides anything. Every other override in the codebase carries it, so an override without it reads as an accident.

A violation is a method whose name and parameter types match an inherited or interface method and that lacks `@Override`. Do not flag a `static` method with the same signature as a superclass `static` method, which hides rather than overrides and cannot take the annotation, or a `main` method.

**Example:** a `public void doSomething()` that called `super.doSomething()` gained `@Override` on the line above it.

```diff
+@Override
 public void doSomething() {
 	super.doSomething();
 }
```