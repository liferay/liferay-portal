## JavaBasePortalFilterPortalPropertiesCheck

Ensures consistency between filter inheritance and `portal.properties`
configuration:

- Classes extending `BasePortalFilter` must have their fully qualified class
  name listed in the `portal.properties` Servlet Filters section, since
  `BasePortalFilter` reads `PropsUtil.get(getClass().getName())` to determine
  if the filter is enabled.
- Classes extending `BaseFilter` (but not `BasePortalFilter`) must not be
  listed in the `portal.properties` Servlet Filters section, since they do
  not use that mechanism.

### Example

Incorrect (extends `BasePortalFilter` but missing from portal.properties):

```java
import com.liferay.portal.servlet.filters.BasePortalFilter;

// com.liferay.portal.servlet.filters.MyFilter is NOT listed in portal.properties
public class MyFilter extends BasePortalFilter {
}
```

Correct (extends `BasePortalFilter` and listed in portal.properties):

```java
import com.liferay.portal.servlet.filters.BasePortalFilter;

// com.liferay.portal.servlet.filters.MyFilter IS listed in portal.properties
// Servlet Filters section
public class MyFilter extends BasePortalFilter {
}
```