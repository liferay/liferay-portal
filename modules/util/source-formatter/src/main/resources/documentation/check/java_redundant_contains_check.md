## JavaRedundantContainsCheck

Do not guard a map or collection operation with a `contains` or `containsKey`
check on the same key or element. The operation already reports presence
through its return value, so the guard costs a second hash lookup.

Combine the two calls into one:

```java
if (map.containsKey(key)) {
    return map.get(key);
}
```

becomes

```java
Value value = map.get(key);

if (value != null) {
    return value;
}
```

Likewise, `if (map.containsKey(key)) { map.remove(key); ... }` becomes a single
`remove` with a null check, `if (!map.containsKey(key)) { map.put(key, value); }`
becomes `putIfAbsent` or `computeIfAbsent`, and `if (!set.contains(element)) {
set.add(element); ... }` and `if (set.contains(element)) { set.remove(element);
... }` use the boolean result of a single `add` or `remove`.

The merged map forms assume the map does not store null values, which a
`containsKey` guard distinguishes from absence. When a map legitimately holds
null values, keep the guard and whitelist the file.

The check only looks at single argument `contains` and `containsKey` calls,
which are the instance methods on a map or collection. Static helpers that take
the container as an extra argument, such as `ArrayUtil.contains(array, value)`
followed by `ArrayUtil.remove(array, value)`, are not flagged: the array
`remove` builds a new array without reporting presence, so the `contains` guard
is what avoids the allocation and keeps the branch's side effects conditional.

The `add` form is only flagged when the receiver's declaration resolves to a
`Set` type. `List.add` and unbounded `Queue.add` always return true, so on
those types the `contains` guard is the deduplication itself and cannot be
folded into the `add`. The `remove` forms apply to lists as well, since
`List.remove(Object)` does report presence.

#### Example 1

Instead of:

```java
protected String method1(Map<String, String> map, String name, String value) {

    if (!map.containsKey(name)) {
        map.put(name, value);

        return value;
    }

    return null;
}
```

We should do:

```java
protected String method1(Map<String, String> map, String name, String value) {

    if (map.putIfAbsent(name, value) == null) {
        return value;
    }

    return null;
}
```

#### Example 2

Instead of:

```java
protected String method2(Map<String, String> map, String name) {
    if (map.containsKey(name)) {
        return map.get(name);
    }

    return "";
}
```

We should do:

```java
protected String method2(Map<String, String> map, String name) {
    String value = map.get(name);

    if (value != null) {
        return value;
    }

    return "";
}
```

#### Example 3

Instead of:

```java
protected String method3(Map<String, String> map, String name) {
    if (map.containsKey(name)) {
        return map.remove(name);
    }

    return "";
}
```

We should do:

```java
protected String method3(Map<String, String> map, String name) {
    String value = map.remove(name);

    if (value != null) {
        return value;
    }

    return "";
}
```

#### Example 4

Instead of:

```java
protected void method4(Set<String> set, String name) {
    if (!set.contains(name)) {
        set.add(name);
    }
}
```

We should do:

```java
protected void method4(Set<String> set, String name) {
    set.add(name);
}
```

#### Example 5

Instead of:

```java
protected void method5(Set<String> set, String name) {
    if (set.contains(name)) {
        set.remove(name);
    }
}
```

We should do:

```java
protected void method5(Set<String> set, String name) {
    set.remove(name);
}
```

#### Example 6

Instead of:

```java
protected void method6(List<String> list, String name) {
    if (list.contains(name)) {
        list.remove(name);
    }
}
```

We should do:

```java
protected void method6(List<String> list, String name) {
    list.remove(name);
}
```