## JavaMetaAnnotationsCheck

When using `aQute.bnd.annotation.metatype.Meta` annotations, we should use `-`
as delimiter for values of the `description` and `name` of the attribute. For
values of the `id` of the attribute, we use `.` as the delimiter.

The reason for this is that for `description` and `name` we retrieve translated
values from `language.properties`.

A `@Meta.AD` in a configuration that generates UI also requires an explicit
`name`. Without one, bnd derives the name from the method name, which is not a
language key, so the configuration fails localization at runtime.

### Example

```java
@Meta.OCD(
    id = "com.liferay.document.library.repository.cmis.configuration.CMISRepositoryConfiguration",
    localization = "content/Language",
    name = "cmis-repository-configuration-name"
)

@Meta.AD(
    deflt = "1", description = "delete-depth-description",
    name = "delete-depth-name", required = false
)
```