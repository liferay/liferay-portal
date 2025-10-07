## ModelSetCallWithCompanyIdCheck

Do not use variable `companyId` as the parameter when calling model set method,
unless set method explicitly references companyId, like `setCompanId`.

### Examples

Incorrect:

```java
ddmFieldAttributeModel.setLargeAttributeValue(
	"Value: a very large value\ncompanyId: " + companyId);
```

Correct:

```java
ddmFieldAttributeModel.setCompanyId(companyId);
ddmFieldAttributeModel.setLargeAttributeValue("Value: a very large value");
```