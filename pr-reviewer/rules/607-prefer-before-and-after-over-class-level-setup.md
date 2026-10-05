# 607: Prefer Before and After Over Class Level Setup

Set up and tear down test state in instance methods annotated with `@Before` and `@After`, not in static methods annotated with `@BeforeClass` and `@AfterClass`. Each test then starts from a fresh fixture built for it alone.

The exception is a fixture that is expensive to build, such as a new company or a deployed bundle. There `@BeforeClass` is the right call, so the suite pays the cost once rather than per test.

**Rationale:** Instance level setup gives each test its own state. Class level setup shares one static fixture across every test in the class, which couples the tests through it: when one test mutates the fixture, the others fail or pass depending on the order they run in, and nothing in the code points at the cause.

A violation is a `@BeforeClass` or `@AfterClass` method that builds or releases a fixture cheap enough to build per test. Do not flag class level setup of a fixture that is expensive to build, and per rule 001 do not flag a class that already follows the convention of its siblings.

**Example:** a static `setUpClass` that assigns a fixture to a static field should become an instance `setUp` that assigns it to an instance field.

```diff
-@BeforeClass
-public static void setUpClass() throws Exception {
+@Before
+public void setUp() throws Exception {
 	_fixture = createFixture();
 }
```