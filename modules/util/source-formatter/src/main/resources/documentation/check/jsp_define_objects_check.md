## JSPDefineObjectsCheck

We can use `liferay-frontend:defineObjects`, `liferay-theme:defineObjects` and
`portlet:defineObjects` tags to prevent unnecessary code duplication.

Commonly used logic in JSP is defined and can be used when we use those tags.

#### <liferay-frontend:defineObjects />

```
String currentURL = currentURLObj.toString();

PortletURL currentURLObj = PortletURLUtil.getCurrent(liferayPortletRequest, liferayPortletResponse);

ResourceBundle resourceBundle = ResourceBundleUtil.getBundle("content.Language", locale, getClass());

WindowState windowState = liferayPortletRequest.getWindowState();
```

#### <liferay-theme:defineObjects />

```
Account account = themeDisplay.getAccount();

ColorScheme colorScheme = themeDisplay.getColorScheme();

Company company = themeDisplay.getCompany();

Layout layout = themeDisplay.getLayout();

List<Layout> layouts = themeDisplay.getLayouts();

LayoutTypePortlet layoutTypePortlet = themeDisplay.getLayoutTypePortlet();

Locale locale = themeDisplay.getLocale();

PermissionChecker permissionChecker = themeDisplay.getPermissionChecker();

long plid = themeDisplay.getPlid();

PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

User realUser = themeDisplay.getRealUser();

long scopeGroupId = themeDisplay.getScopeGroupId();

Theme theme = themeDisplay.getTheme();

ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);

TimeZone timeZone = themeDisplay.getTimeZone();

User user = themeDisplay.getUser();

long portletGroupId = themeDisplay.getScopeGroupId();
```

#### <portlet:defineObjects />

```
PortletConfig portletConfig = (PortletConfig)request.getAttribute(JavaConstants.JAKARTA_PORTLET_CONFIG);

String portletName = portletConfig.getPortletName();

LiferayPortletRequest liferayPortletRequest = PortalUtil.getLiferayPortletRequest(portletRequest);

PortletRequest actionRequest = (PortletRequest)request.getAttribute(JavaConstants.JAKARTA_PORTLET_REQUEST);

PortletRequest eventRequest = (PortletRequest)request.getAttribute(JavaConstants.JAKARTA_PORTLET_REQUEST);

PortletRequest renderRequest = (PortletRequest)request.getAttribute(JavaConstants.JAKARTA_PORTLET_REQUEST);

PortletRequest resourceRequest = (PortletRequest)request.getAttribute(JavaConstants.JAKARTA_PORTLET_REQUEST);

PortletPreferences portletPreferences = portletRequest.getPreferences();

Map<String, String[]> portletPreferencesValues = portletPreferences.getMap();

PortletSession portletSession = portletRequest.getPortletSession();

Map<String, Object> portletSessionScope = portletSession.getAttributeMap();

LiferayPortletResponse liferayPortletResponse = PortalUtil.getLiferayPortletResponse(portletResponse);

PortletResponse actionResponse = (PortletResponse)request.getAttribute(JavaConstants.JAKARTA_PORTLET_RESPONSE);

PortletResponse eventResponse = (PortletResponse)request.getAttribute(JavaConstants.JAKARTA_PORTLET_RESPONSE);

PortletResponse renderResponse = (PortletResponse)request.getAttribute(JavaConstants.JAKARTA_PORTLET_RESPONSE);

PortletResponse resourceResponse = (PortletResponse)request.getAttribute(JavaConstants.JAKARTA_PORTLET_RESPONSE);
```