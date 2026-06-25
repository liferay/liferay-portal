## InstanceInitializerCheck

Use set call with inlined if-statement when possible:

```java
private ObjectDefinition _toObjectDefinition(
    com.liferay.object.model.ObjectDefinition objectDefinition) {

    return new ObjectDefinition() {
        {
            setEnableLocalization(
                () -> {
                    if (!FeatureFlagManagerUtil.isEnabled("LPS-172017")) {
                        return null;
                    }

                    return objectDefinition.getEnableLocalization();
                });

            setModifiable(
                () -> {
                    if (!FeatureFlagManagerUtil.isEnabled("LPS-172017")) {
                        return null;
                    }

                    return objectDefinition.getModifiable();
                });
        }
    };
}
```

Instead of

```java
private ObjectDefinition _toObjectDefinition(
    com.liferay.object.model.ObjectDefinition objectDefinition) {

    return new ObjectDefinition() {
        {
            if (FeatureFlagManagerUtil.isEnabled("LPS-172017")) {
                enableLocalization =
                    objectDefinition.getEnableLocalization();
                modifiable = objectDefinition.getModifiable();
            }
        }
    };
}
```

___

Inline if-statement inside set call when possible:

```java
private ObjectDefinition _toObjectDefinition(
    com.liferay.object.model.ObjectDefinition objectDefinition) {

    return new ObjectDefinition() {
        {
            setRootObjectDefinitionExternalReferenceCode(
                () -> {
                    if (!FeatureFlagManagerUtil.isEnabled("LPS-172017") ||
                        !objectDefinition.isSystem()) {

                        return null;
                    }

                    return objectDefinition.getExternalReferenceCode();
                });
        }
    };
}
```

Instead of

```java
private ObjectDefinition _toObjectDefinition(
    com.liferay.object.model.ObjectDefinition objectDefinition) {

    return new ObjectDefinition() {
        {
            if (FeatureFlagManagerUtil.isEnabled("LPS-172017")) {
                setRootObjectDefinitionExternalReferenceCode(
                    () -> {
                        if (!objectDefinition.isSystem()) {
                            return null;
                        }

                        return objectDefinition.getExternalReferenceCode();
                    });
            }
        }
    };
}
```