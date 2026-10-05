# 203: Declare Variables as They Are Used

Declare each local variable immediately before its first use, set off by a blank line, rather than grouping declarations at the top of the method or block. A declaration and the statement that uses it form one short paragraph, and the next variable begins the next paragraph.

The exception is a wrapping object: most often the value the method builds and returns, but also any local whose sole purpose is to receive another local through a setter. Declare the wrapper first, build the inner value, and close with the return statement or the setter call. The wrapper envelopes the steps that fill it — the wrapper is the tortilla and the build steps are the filling ("burrito") — and the variables between the opening declaration and the closing return or setter are still ordered as they are used.

The burrito extends to any depth. When A wraps B wraps C, declare every object up front from outer to inner, then configure them from innermost to outermost, rather than interleaving declaration and configuration down the chain. A Mockito test follows the same shape: declare the mocks first, the one that wraps or receives the others first, then write the `Mockito.when` stubs from innermost to outermost. Between stubs, the order is the order in which the system under test exercises the mocks, with alphabetical order breaking ties.

Three refinements follow from declaring as used. A local used only inside a `try` belongs inside it; hoisting it above the `try` is warranted only when the `catch` or `finally` needs it. A default override stays next to the declaration it patches: when a local is declared and then immediately replaced if null or unsuitable, the `if` follows the declaration directly, as one "compute alpha" step, rather than trailing an unrelated block of declarations. And when a block of locals feeds straight into one call, the block is grouped and rule 201's alphabetical order governs it; in practice the call's parameters are themselves sorted per rule 202, so the two orders coincide. Inside a grouped block rule 201 wins; between blocks, this rule's order of use wins.

**Rationale:** A declaration next to its use can be read in place, without scrolling to a separate block, and the variable's span is obvious. Declaring the returned object first and returning it last frames the whole method around what it produces: the reader sees what the method yields, then the steps that build it in running order, then the value handed back, mirroring the data flow. Ordering by use, rather than alphabetically, keeps each value beside the code that needs it.

A violation is a local variable declared far from its first use, or batched with unrelated declarations at the top of the block; or, for a wrapping object (the returned value, or a local that absorbs another local through a setter), the reverse — declaring it in the middle, after the helpers that populate it, instead of first. A local hoisted above a `try` that alone uses it, a default override `if` separated from the declaration it patches, or a containment chain that interleaves declaration and configuration is the same violation.

**Example:** commit `0294083` (`BaseFDSSerializer`) moved the `ownedObjectEntries` and `sharedObjectEntries` lists that the method serializes into its returned `jsonArray` up to the top of the body, and pushed `objectEntryManager` down to just before its use. It corrected an earlier change that had moved the first used helper to the top instead of the returned lists. Commit `aa720a9` extends the burrito to wrapping helpers in test setup: `LanguageUtil languageUtil = new LanguageUtil();` is declared before `Language language = Mockito.mock(...)` is built, then closed by `languageUtil.setLanguage(language);`. The same shape covers `SAXReaderUtil saxReaderUtil = new SAXReaderUtil();` wrapping `SAXReaderImpl secureSAXReaderImpl = new SAXReaderImpl();` via `saxReaderUtil.setSAXReader(secureSAXReaderImpl);`. The diffs below move a local into the `try` that alone uses it, keep a default override beside its declaration, and apply the burrito to a containment chain and to Mockito mocks.

```diff
-Foo foo = makeFoo();
-
 try {
+	Foo foo = makeFoo();
+
 	foo.consume();
 }
 catch (Exception exception) {
 	_log.error("Failed", exception);
 }
```

```diff
 String alpha = source.getAlpha();
+
+if (alpha == null) {
+	alpha = fallback.getAlpha();
+}
+
 String beta = null;
 String gamma = null;
 Date delta = source.getDelta();
-
-if (alpha == null) {
-	alpha = fallback.getAlpha();
-}
```

```diff
+ContainerA containerA = new ContainerA();
+ContainerB containerB = new ContainerB();
+
 ContainerC containerC = new ContainerC();

 containerC.setContent(content);

-ContainerB containerB = new ContainerB();
-
 containerB.setContainerC(containerC);

-ContainerA containerA = new ContainerA();
-
 containerA.setContainerB(containerB);
```

```diff
+OuterMock outerMock = Mockito.mock(OuterMock.class);
+
 InnerMock innerMock = Mockito.mock(InnerMock.class);

 Mockito.when(
 	innerMock.getValue()
 ).thenReturn(
 	value
 );

-OuterMock outerMock = Mockito.mock(OuterMock.class);
-
 Mockito.when(
 	outerMock.getInner()
 ).thenReturn(
 	innerMock
 );
```