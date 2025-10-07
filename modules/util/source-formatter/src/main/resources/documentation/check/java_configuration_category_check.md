## JavaConfigurationCategoryCheck

The `category` in `@ExtendedObjectClassDefinition` should always match with the
`categoryKey` of a corresponding class that implements `ConfigurationCategory`

### Example

```java
@ExtendedObjectClassDefinition(category = "audit")
public interface PersistentAuditMessageProcessorConfiguration {
}
```

Corresponding class with key = `audit`:

```java
package com.liferay.configuration.admin.web.internal.category;

import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

@Component
public class AuditConfigurationCategory implements ConfigurationCategory {

    @Override
    public String getCategoryIcon() {
        return _CATEGORY_ICON;
    }

    @Override
    public String getCategoryKey() {
        return _CATEGORY_KEY;
    }

    @Override
    public String getCategorySection() {
        return _CATEGORY_SECTION;
    }

    private static final String _CATEGORY_ICON = "view";

    private static final String _CATEGORY_KEY = "audit";

    private static final String _CATEGORY_SECTION = "security";

}
```