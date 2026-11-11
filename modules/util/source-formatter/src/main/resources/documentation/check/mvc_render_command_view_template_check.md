## MVCRenderCommandViewTemplateCheck

`MVCPortlet.render` falls back to the `view-template` init parameter of the portlet when no MVC render command answers `/`. An MVC render command that is registered for `/` and only returns a constant path therefore does nothing that the init parameter does not already do.

The check reports each `"mvc.command.name=/"` registration in a Java class that implements `MVCRenderCommand` when the body of its `render(RenderRequest, RenderResponse)` method is nothing but `return "<path>";`. The command may be registered for other names as well. A command for `/` that does any other work, such as setting a request attribute, and a command that only returns a constant path but does not map `/` are not reported.

### Example

Incorrect:

```java
@Component(
    property = {
        "jakarta.portlet.name=" + FooPortletKeys.FOO,
        "mvc.command.name=/"
    },
    service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

    @Override
    public String render(
        RenderRequest renderRequest, RenderResponse renderResponse) {

        return "/view.jsp";
    }

}
```

Correct, after deleting the command and adding the init parameter to the portlet:

```java
@Component(
    property = {
        "jakarta.portlet.name=" + FooPortletKeys.FOO,
        "jakarta.portlet.init-param.view-template=/view.jsp"
    },
    service = Portlet.class
)
public class FooPortlet extends MVCPortlet {
}
```

### Allowed Files

A command that has to stay can be allowed in the nearest `source-formatter.properties`. Explain the reason in the commit message.

```
source.check.MVCRenderCommandViewTemplateCheck.allowedFileNames=\
    modules/apps/foo/foo-web/src/main/java/com/liferay/foo/web/internal/portlet/action/ViewMVCRenderCommand.java
```