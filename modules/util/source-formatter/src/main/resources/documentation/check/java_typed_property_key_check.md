## JavaTypedPropertyKeyCheck

The typed property syntax `"<key>:<Type>=<value>"`, for example `"service.ranking:Integer=100"`, is only parsed in the property strings of a Declarative Services `@Component` (and `@OSGiBeanProperties`). A `Dictionary`, a `HashMapDictionaryBuilder` or a `MapUtil.singletonDictionary` takes the key literally, so a key such as `"service.ranking:Integer"` becomes a property with that exact name and the ranking never takes effect.

The check reports every string literal that is exactly a key followed by `:` and one of `Boolean`, `Byte`, `Character`, `Double`, `Float`, `Integer`, `Long`, `Short` or `String`. It also reports a typed key inside a filter string, such as `"(service.ranking:Integer=1)"`, because a filter attribute never parses the typed syntax either and can only match a property literally named `service.ranking:Integer`. A file that imports `aQute.bnd.` packages is skipped because bnd parses the typed syntax itself.

### Example

Incorrect:

```java
_serviceRegistration = bundleContext.registerService(
	Foo.class, foo,
	HashMapDictionaryBuilder.<String, Object>put(
		"service.ranking:Integer", 100
	).build());
```

Correct:

```java
_serviceRegistration = bundleContext.registerService(
	Foo.class, foo,
	HashMapDictionaryBuilder.<String, Object>put(
		"service.ranking", 100
	).build());
```