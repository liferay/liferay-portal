## EmptyServicePropertiesCheck

Do not pass an empty `Hashtable` or `HashMapDictionary` as the service properties of `registerService`. OSGi documents `null` as "no properties", so the empty dictionary only allocates and suggests properties that are not there.

Pass `null` instead.

### Example

Incorrect:

```java
_serviceRegistration = bundleContext.registerService(
    LogContext.class, _auditLogContext, new HashMapDictionary<>());
```

Correct:

```java
_serviceRegistration = bundleContext.registerService(
    LogContext.class, _auditLogContext, null);
```

The check flags a qualified `registerService` call, such as `bundleContext.registerService`, whose last argument is a new `Hashtable` or `HashMapDictionary` built with no arguments, or a local variable that is declared with one and used nowhere else.

An unqualified `registerService` call is a helper method of the class rather than an OSGi registration, and the helper may require a dictionary that is not null, so it is not flagged. A variable that is filled, read, or passed anywhere else is not flagged either.