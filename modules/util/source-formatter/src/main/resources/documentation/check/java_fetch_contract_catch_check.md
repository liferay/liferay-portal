## JavaFetchContractCatchCheck

Do not catch `PortalException` or a `NoSuch*Exception` only to return the `null`
or `false` absence sentinel when the try block holds a single local service or
persistence lookup. Using a not-found exception as an absence signal is
exception-based control flow, and every local service and persistence lookup
has a null-tolerant `fetch` sibling.

Call the `fetch` sibling and check the result for `null` instead. For example,
replace

```
try {
	return fooLocalService.getFoo(fooId);
}
catch (NoSuchFooException noSuchFooException) {
	return null;
}
```

with

```
return fooLocalService.fetchFoo(fooId);
```

The check only flags a try block whose sole invocation is the lookup itself,
with no nested invocations in the arguments, so that the lookup is provably the
only statement able to throw the caught exception. Catches that guard multiple
statements, remote permission-checked services, or exceptions that encode more
than absence are deliberately out of scope.