## JavaBasePortalFilterIsFilterEnabledCheck

Classes extending `com.liferay.portal.servlet.filters.BasePortalFilter` must not
override `isFilterEnabled()`. `BasePortalFilter` reads
`PropsUtil.get(getClass().getName())` to determine if the filter is enabled, and
subclasses should not bypass this mechanism. If a filter needs custom
`isFilterEnabled()` logic, it should extend `BaseFilter` instead.

### Example

Incorrect:

```java
import com.liferay.portal.servlet.filters.BasePortalFilter;

public class MyFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled() {
		return true;
	}

}
```

Correct (remove the override to use portal.properties configuration):

```java
import com.liferay.portal.servlet.filters.BasePortalFilter;

public class MyFilter extends BasePortalFilter {
}
```

Correct (extend BaseFilter if custom isFilterEnabled logic is needed):

```java
import com.liferay.portal.kernel.servlet.BaseFilter;

public class MyFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled() {
		return true;
	}

}
```