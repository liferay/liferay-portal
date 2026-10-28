<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/portal/init.jsp" %>

<%
String cmd = ParamUtil.getString(request, Constants.CMD);

Portlet portlet = (Portlet)request.getAttribute(WebKeys.RENDER_PORTLET);

String portletId = portlet.getPortletId();
String rootPortletId = portlet.getRootPortletId();
String instanceId = portlet.getInstanceId();

String portletPrimaryKey = PortletPermissionUtil.getPrimaryKey(plid, portletId);

String columnId = GetterUtil.getString(request.getAttribute(WebKeys.RENDER_PORTLET_COLUMN_ID));
int columnPos = GetterUtil.getInteger(request.getAttribute(WebKeys.RENDER_PORTLET_COLUMN_POS));
int columnCount = GetterUtil.getInteger(request.getAttribute(WebKeys.RENDER_PORTLET_COLUMN_COUNT));

boolean stateMax = layoutTypePortlet.hasStateMaxPortletId(portletId);
boolean stateMin = layoutTypePortlet.hasStateMinPortletId(portletId);

boolean modeAbout = layoutTypePortlet.hasModeAboutPortletId(portletId);
boolean modeConfig = layoutTypePortlet.hasModeConfigPortletId(portletId);
boolean modeEdit = layoutTypePortlet.hasModeEditPortletId(portletId);
boolean modeEditDefaults = layoutTypePortlet.hasModeEditDefaultsPortletId(portletId);
boolean modeEditGuest = layoutTypePortlet.hasModeEditGuestPortletId(portletId);
boolean modeHelp = layoutTypePortlet.hasModeHelpPortletId(portletId);
boolean modePreview = layoutTypePortlet.hasModePreviewPortletId(portletId);
boolean modePrint = layoutTypePortlet.hasModePrintPortletId(portletId);

boolean columnDisabled = layoutTypePortlet.isColumnDisabled(columnId);
boolean customizable = layoutTypePortlet.isCustomizable();

PortletPreferences portletPreferences = PortletPreferencesLocalServiceUtil.getStrictPreferences(PortletPreferencesFactoryUtil.getPortletPreferencesIds(request, portletId));

PortletPreferences setupPortletPreferences = themeDisplay.getStrictLayoutPortletSetup(layout, portletId);

Group group = null;
boolean privateLayout = false;

if (layout instanceof VirtualLayout) {
	VirtualLayout virtualLayout = (VirtualLayout)layout;

	Layout sourceLayout = virtualLayout.getSourceLayout();

	group = sourceLayout.getGroup();
	privateLayout = sourceLayout.isPrivateLayout();
}
else {
	group = layout.getGroup();
	privateLayout = layout.isPrivateLayout();
}

long portletItemId = ParamUtil.getLong(request, "p_p_i_id");

if (portletItemId > 0) {
	PortletPreferencesServiceUtil.restoreArchivedPreferences(themeDisplay.getSiteGroupId(), layout, portlet.getPortletId(), portletItemId, portletPreferences);
}

PortletConfig portletConfig = PortletConfigFactoryUtil.create(portlet, application);

PortletContext portletCtx = portletConfig.getPortletContext();

WindowState windowState = WindowState.NORMAL;

if (themeDisplay.isStateExclusive()) {
	windowState = LiferayWindowState.EXCLUSIVE;
}
else if (themeDisplay.isStatePopUp()) {
	windowState = LiferayWindowState.POP_UP;
}
else if (stateMax) {
	windowState = WindowState.MAXIMIZED;
}
else if (stateMin) {
	windowState = WindowState.MINIMIZED;
}

PortletMode portletMode = PortletMode.VIEW;

if (modeAbout) {
	portletMode = LiferayPortletMode.ABOUT;
}
else if (modeConfig) {
	portletMode = LiferayPortletMode.CONFIG;
}
else if (modeEdit) {
	portletMode = PortletMode.EDIT;
}
else if (modeEditDefaults) {
	portletMode = LiferayPortletMode.EDIT_DEFAULTS;
}
else if (modeEditGuest) {
	portletMode = LiferayPortletMode.EDIT_GUEST;
}
else if (modeHelp) {
	portletMode = PortletMode.HELP;
}
else if (modePreview) {
	portletMode = LiferayPortletMode.PREVIEW;
}
else if (modePrint) {
	portletMode = LiferayPortletMode.PRINT;
}
else {
	String customPortletMode = layoutTypePortlet.getAddedCustomPortletMode();

	if (customPortletMode != null) {
		portletMode = new PortletMode(customPortletMode);
	}
}

InvokerPortlet invokerPortlet = null;

try {
	if (portlet.isReady()) {
		invokerPortlet = PortletInstanceFactoryUtil.create(portlet, application);
	}
}
/*catch (UnavailableException ue) {
	ue.printStackTrace();
}*/
catch (PortletException pe) {
	_log.error(pe);
}
catch (RuntimeException re) {
	_log.error(re);
}

LiferayRenderRequest liferayRenderRequest = RenderRequestFactory.create(request, portlet, invokerPortlet, portletCtx, windowState, portletMode, portletPreferences, plid);

BufferCacheServletResponse bufferCacheServletResponse = new BufferCacheServletResponse(response);

LiferayRenderResponse liferayRenderResponse = RenderResponseFactory.create(bufferCacheServletResponse, liferayRenderRequest);

if (stateMin) {
	liferayRenderResponse.setUseDefaultTemplate(true);
}

liferayRenderRequest.defineObjects(portletConfig, liferayRenderResponse);

String responseContentType = liferayRenderRequest.getResponseContentType();

String currentURL = PortalUtil.getCurrentURL(request);

String portletResource = ParamUtil.getString(request, "portletResource");

if (Validator.isNull(portletResource)) {
	portletResource = ParamUtil.getString(liferayRenderRequest, "portletResource");
}

Portlet portletResourcePortlet = null;

if (Validator.isNotNull(portletResource)) {
	portletResourcePortlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletResource);
}

boolean showCloseIcon = true;
boolean showConfigurationIcon = false;
boolean showEditIcon = false;
boolean showEditDefaultsIcon = false;
boolean showEditGuestIcon = false;
boolean showExportImportIcon = false;
boolean showHelpIcon = false;
boolean showMoveIcon = !stateMax && !themeDisplay.isStateExclusive();
boolean showPortletCssIcon = false;
boolean showPortletIcon = (portletResourcePortlet != null) ? Validator.isNotNull(portletResourcePortlet.getIcon()) : Validator.isNotNull(portlet.getIcon());
boolean showPrintIcon = portlet.hasPortletMode(responseContentType, LiferayPortletMode.PRINT);
boolean showRefreshIcon = portlet.isAjaxable() && (portlet.getRenderWeight() == 0);
boolean showStagingIcon = false;

Layout curLayout = PortletConfigurationLayoutUtil.getLayout(themeDisplay);

if ((!group.hasLocalOrRemoteStagingGroup() || !PropsValues.STAGING_LIVE_GROUP_LOCKING_ENABLED) && PortletPermissionUtil.contains(permissionChecker, themeDisplay.getScopeGroupId(), curLayout, portlet, ActionKeys.CONFIGURATION)) {
	showConfigurationIcon = true;

	boolean supportsConfigurationLAR = portlet.getConfigurationActionInstance() != null;
	boolean supportsDataLAR = !(portlet.getPortletDataHandlerInstance() instanceof DefaultConfigurationPortletDataHandler);

	if (supportsConfigurationLAR || supportsDataLAR || !group.isControlPanel()) {
		showExportImportIcon = true;
	}

	if (PropsValues.PORTLET_CSS_ENABLED) {
		showPortletCssIcon = true;
	}

	Group checkingStagingGroup = group;

	if (checkingStagingGroup.isControlPanel()) {
		checkingStagingGroup = themeDisplay.getSiteGroup();
	}

	if ((checkingStagingGroup.isStaged() || checkingStagingGroup.isStagedRemotely()) && (!checkingStagingGroup.hasLocalOrRemoteStagingGroup() || PropsValues.STAGING_LIVE_GROUP_REMOTE_STAGING_ENABLED) && checkingStagingGroup.isStagedPortlet(portletId)) {
		showStagingIcon = true;
	}
}

if (!columnDisabled && customizable && !portlet.isPreferencesCompanyWide() && portlet.isPreferencesUniquePerLayout() && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.CUSTOMIZE)) {
	showConfigurationIcon = true;

	if (PropsValues.PORTLET_CSS_ENABLED) {
		showPortletCssIcon = true;
	}
}

if (group.isLayoutPrototype()) {
	showExportImportIcon = false;
	showStagingIcon = false;
}

if (portlet.hasPortletMode(responseContentType, PortletMode.EDIT) && PortletPermissionUtil.contains(permissionChecker, layout, portletId, ActionKeys.PREFERENCES)) {
	showEditIcon = true;

	if (portlet.hasPortletMode(responseContentType, LiferayPortletMode.EDIT_DEFAULTS)) {
		showEditDefaultsIcon = true;
	}

	if (portlet.hasPortletMode(responseContentType, LiferayPortletMode.EDIT_GUEST) && !layout.isPrivateLayout()) {
		showEditGuestIcon = true;
	}
}

if (portlet.hasPortletMode(responseContentType, PortletMode.HELP) && PortletPermissionUtil.contains(permissionChecker, layout, portletId, ActionKeys.HELP)) {
	showHelpIcon = true;
}

boolean supportsMimeType = portlet.hasPortletMode(responseContentType, portletMode);

if (responseContentType.equals(ContentTypes.XHTML_MP) && portlet.hasMultipleMimeTypes()) {
	supportsMimeType = GetterUtil.getBoolean(setupPortletPreferences.getValue("portletSetupSupportedClientsMobileDevices_" + portletMode, String.valueOf(supportsMimeType)));
}

// Only authenticated with the correct permissions can update a layout. If
// staging is activated, only staging layouts can be updated.

if ((!themeDisplay.isSignedIn() || (group.hasStagingGroup() && PropsValues.STAGING_LIVE_GROUP_LOCKING_ENABLED) || !LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE)) && !(!columnDisabled && customizable && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.CUSTOMIZE))) {
	showCloseIcon = false;
	showMoveIcon = false;
}

// Portlets cannot be moved if the column is not customizable

if (columnDisabled && customizable) {
	showCloseIcon = false;
	showMoveIcon = false;
}

// Portlets cannot be moved unless they belong to the layout

if (!layoutTypePortlet.hasPortletId(portletId, true)) {
	showCloseIcon = false;
	showMoveIcon = false;
}

// Portlets in the Control Panel cannot be moved

if (layout.isTypeControlPanel()) {
	showCloseIcon = false;
	showMoveIcon = false;
}

// Static portlets cannot be moved

if (portlet.isStatic()) {
	showCloseIcon = false;
	showMoveIcon = false;
}

// Portlets in a layout linked to a layout prototype cannot be moved

if (layout.isLayoutPrototypeLinkActive()) {
	showCloseIcon = false;
	showConfigurationIcon = false;
	showMoveIcon = false;
	showPortletCssIcon = false;
}

// Portlets in a full page cannot be moved

LayoutTypeController layoutTypeController = layoutTypePortlet.getLayoutTypeController();

if (layoutTypeController.isFullPageDisplayable()) {
	showCloseIcon = false;
}

if (Validator.isNotNull(portletResource)) {
	themeDisplay.setScopeGroupId(PortalUtil.getScopeGroupId(request, portletResourcePortlet.getPortletId()));
}
else {
	themeDisplay.setScopeGroupId(PortalUtil.getScopeGroupId(request, portletId));
}

Group siteGroup = themeDisplay.getSiteGroup();

if (siteGroup.isStagingGroup()) {
	siteGroup = siteGroup.getLiveGroup();
}

if (siteGroup.isStaged() && !siteGroup.isStagedRemotely() && !siteGroup.isStagedPortlet(portletId)) {
	themeDisplay.setSiteGroupId(siteGroup.getGroupId());
}

// Portlet decorate

boolean portletDecorate = true;

Boolean portletDecorateObj = (Boolean)request.getAttribute(WebKeys.PORTLET_DECORATE);

if (portletDecorateObj != null) {
	portletDecorate = portletDecorateObj.booleanValue();

	request.removeAttribute(WebKeys.PORTLET_DECORATE);
}

portletDisplay.recycle();

portletDisplay.setActive(portlet.isActive());
portletDisplay.setColumnCount(columnCount);
portletDisplay.setColumnId(columnId);
portletDisplay.setColumnPos(columnPos);
portletDisplay.setId(portletId);
portletDisplay.setInstanceId(instanceId);
portletDisplay.setModeAbout(modeAbout);
portletDisplay.setModeConfig(modeConfig);
portletDisplay.setModeEdit(modeEdit);
portletDisplay.setModeEditDefaults(modeEditDefaults);
portletDisplay.setModeEditGuest(modeEditGuest);
portletDisplay.setModeHelp(modeHelp);
portletDisplay.setModePreview(modePreview);
portletDisplay.setModePrint(modePrint);
portletDisplay.setModeView(portletMode.equals(PortletMode.VIEW));
portletDisplay.setNamespace(PortalUtil.getPortletNamespace(portletId));
portletDisplay.setPortletDecorate(portletDecorate);
portletDisplay.setPortletDisplayName(PortalUtil.getPortletTitle(liferayRenderRequest));
portletDisplay.setPortletName(portletConfig.getPortletName());
portletDisplay.setPortletResource(portletResource);
portletDisplay.setResourcePK(portletPrimaryKey);
portletDisplay.setRestoreCurrentView(portlet.isRestoreCurrentView());
portletDisplay.setRootPortletId(rootPortletId);
portletDisplay.setShowCloseIcon(showCloseIcon);
portletDisplay.setShowConfigurationIcon(showConfigurationIcon);
portletDisplay.setShowEditIcon(showEditIcon);
portletDisplay.setShowEditDefaultsIcon(showEditDefaultsIcon);
portletDisplay.setShowEditGuestIcon(showEditGuestIcon);
portletDisplay.setShowExportImportIcon(showExportImportIcon);
portletDisplay.setShowHelpIcon(showHelpIcon);
portletDisplay.setShowMoveIcon(showMoveIcon);
portletDisplay.setShowPortletCssIcon(showPortletCssIcon);
portletDisplay.setShowPortletIcon(showPortletIcon);
portletDisplay.setShowPrintIcon(showPrintIcon);
portletDisplay.setShowRefreshIcon(showRefreshIcon);
portletDisplay.setShowStagingIcon(showStagingIcon);
portletDisplay.setStateExclusive(themeDisplay.isStateExclusive());
portletDisplay.setStateMax(stateMax);
portletDisplay.setStateMin(stateMin);
portletDisplay.setStateNormal(windowState.equals(WindowState.NORMAL));
portletDisplay.setStatePopUp(themeDisplay.isStatePopUp());
portletDisplay.setPortletPreferences(setupPortletPreferences);
portletDisplay.setWebDAVEnabledSupplier(() -> portlet.getWebDAVStorageInstance() != null);

// Portlet custom CSS class name

String customCSSClassName = PortletConfigurationUtil.getPortletCustomCSSClassName(setupPortletPreferences);

portletDisplay.setCustomCSSClassName(customCSSClassName);

// Portlet icon

String portletIcon = null;

if (portletResourcePortlet != null) {
	portletIcon = portletResourcePortlet.getStaticResourcePath() + portletResourcePortlet.getIcon();
}
else {
	portletIcon = portlet.getStaticResourcePath() + portlet.getIcon();
}

portletDisplay.setURLPortlet(themeDisplay.getCDNHost() + portletIcon);

// URL close

StringBundler sb = new StringBundler(18);

sb.append(themeDisplay.getPathMain());
sb.append("/portal/update_layout?p_auth=");
sb.append(AuthTokenUtil.getToken(request));
sb.append("&p_l_id=");
sb.append(plid);
sb.append("&p_p_id=");
sb.append(portletDisplay.getId());
sb.append("&p_v_l_s_g_id=");
sb.append(themeDisplay.getSiteGroupId());

String doAsUserId = themeDisplay.getDoAsUserId();

if (doAsUserId.isEmpty()) {
	doAsUserId = null;
}
else {
	doAsUserId = URLCodec.encodeURL(doAsUserId);

	sb.append("&doAsUserId=");
	sb.append(doAsUserId);
}

sb.append("&");
sb.append(Constants.CMD);
sb.append("=");
sb.append(Constants.DELETE);
sb.append("&referer=");

StringBundler innerSB = new StringBundler(5);

innerSB.append(themeDisplay.getPathMain());
innerSB.append("/portal/layout?p_l_id=");
innerSB.append(plid);

if (doAsUserId != null) {
	innerSB.append("&doAsUserId=");
	innerSB.append(doAsUserId);
}

sb.append(URLCodec.encodeURL(innerSB.toString()));

sb.append("&refresh=1");

String urlClose = sb.toString();

if (themeDisplay.isAddSessionIdToURL()) {
	urlClose = PortalUtil.getURLWithSessionId(urlClose, themeDisplay.getSessionId());
}

portletDisplay.setURLClose(urlClose);

// URL configuration

PortletURL urlConfiguration = PortletProviderUtil.getPortletURL(request, PortletConfigurationApplicationType.PortletConfiguration.CLASS_NAME, PortletProvider.Action.VIEW);

if (urlConfiguration != null) {
	urlConfiguration.setWindowState(LiferayWindowState.POP_UP);

	if (portlet.getConfigurationActionInstance() != null) {
		urlConfiguration.setParameter("mvcPath", "/edit_configuration.jsp");

		String settingsScope = (String)request.getAttribute(WebKeys.SETTINGS_SCOPE);

		settingsScope = ParamUtil.get(request, "settingsScope", settingsScope);

		if (Validator.isNotNull(settingsScope)) {
			urlConfiguration.setParameter("settingsScope", settingsScope);
		}
	}
	else {
		urlConfiguration.setParameter("mvcPath", "/edit_sharing.jsp");
	}

	urlConfiguration.setParameter("redirect", currentURL);
	urlConfiguration.setParameter("returnToFullPageURL", currentURL);
	urlConfiguration.setParameter("portletConfiguration", Boolean.TRUE.toString());
	urlConfiguration.setParameter("portletResource", portletDisplay.getId());
	urlConfiguration.setParameter("resourcePrimKey", portletPrimaryKey);

	portletDisplay.setURLConfiguration(urlConfiguration.toString());

	StringBundler urlConfigurationJSSB = new StringBundler(PropsValues.PORTLET_CONFIG_SHOW_PORTLET_ID ? 14 : 12);

	urlConfigurationJSSB.append("Liferay.Portlet.openModal({");
	urlConfigurationJSSB.append("iframeBodyCssClass: '");
	urlConfigurationJSSB.append("', namespace: '");
	urlConfigurationJSSB.append(portletDisplay.getNamespace());
	urlConfigurationJSSB.append("', portletSelector: '#p_p_id_");
	urlConfigurationJSSB.append(portletDisplay.getId());
	urlConfigurationJSSB.append("_', portletId: '");
	urlConfigurationJSSB.append(portletDisplay.getId());

	if (PropsValues.PORTLET_CONFIG_SHOW_PORTLET_ID) {
		urlConfigurationJSSB.append("', subTitle: '");
		urlConfigurationJSSB.append(portletDisplay.getId());
	}

	urlConfigurationJSSB.append("', title: '");
	urlConfigurationJSSB.append(UnicodeLanguageUtil.get(request, "configuration"));
	urlConfigurationJSSB.append("', url: '");
	urlConfigurationJSSB.append(HtmlUtil.escapeJS(portletDisplay.getURLConfiguration()));
	urlConfigurationJSSB.append("'}); return false;");

	portletDisplay.setURLConfigurationJS(urlConfigurationJSSB.toString());
}

// URL edit

LiferayPortletURL urlEdit = PortletURLFactoryUtil.create(request, portlet, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModeEdit()) {
	urlEdit.setWindowState(WindowState.NORMAL);
	urlEdit.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isMaximizeEdit() || portletDisplay.isStateMax()) {
		urlEdit.setWindowState(WindowState.MAXIMIZED);
	}
	else {
		urlEdit.setWindowState(WindowState.NORMAL);
	}

	urlEdit.setPortletMode(PortletMode.EDIT);
}

urlEdit.setEscapeXml(false);

portletDisplay.setURLEdit(urlEdit.toString());

// URL edit defaults

LiferayPortletURL urlEditDefaults = PortletURLFactoryUtil.create(request, portlet, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModeEditDefaults()) {
	urlEditDefaults.setWindowState(WindowState.NORMAL);
	urlEditDefaults.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isMaximizeEdit()) {
		urlEditDefaults.setWindowState(WindowState.MAXIMIZED);
	}
	else {
		urlEditDefaults.setWindowState(WindowState.NORMAL);
	}

	urlEditDefaults.setPortletMode(LiferayPortletMode.EDIT_DEFAULTS);
}

urlEditDefaults.setEscapeXml(false);

portletDisplay.setURLEditDefaults(urlEditDefaults.toString());

// URL edit guest

LiferayPortletURL urlEditGuest = PortletURLFactoryUtil.create(request, portlet, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModeEditGuest()) {
	urlEditGuest.setWindowState(WindowState.NORMAL);
	urlEditGuest.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isMaximizeEdit()) {
		urlEditGuest.setWindowState(WindowState.MAXIMIZED);
	}
	else {
		urlEditGuest.setWindowState(WindowState.NORMAL);
	}

	urlEditGuest.setPortletMode(LiferayPortletMode.EDIT_GUEST);
}

urlEditGuest.setEscapeXml(false);

portletDisplay.setURLEditGuest(urlEditGuest.toString());

// URL export / import

Portlet exportImportPortlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), PortletKeys.EXPORT_IMPORT);

LiferayPortletURL urlExportImport = PortletURLFactoryUtil.create(request, exportImportPortlet, PortletRequest.RENDER_PHASE);

urlExportImport.setWindowState(LiferayWindowState.POP_UP);

urlExportImport.setParameter("mvcRenderCommandName", "/export_import/export_import");
urlExportImport.setParameter("redirect", currentURL);
urlExportImport.setParameter("returnToFullPageURL", currentURL);
urlExportImport.setParameter("portletResource", portletDisplay.getId());

urlExportImport.setEscapeXml(false);

portletDisplay.setURLExportImport(urlExportImport.toString() + "&" + PortalUtil.getPortletNamespace(PortletKeys.EXPORT_IMPORT));

// URL help

LiferayPortletURL urlHelp = PortletURLFactoryUtil.create(request, portlet, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModeHelp()) {
	urlHelp.setWindowState(WindowState.NORMAL);
	urlHelp.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isMaximizeHelp()) {
		urlHelp.setWindowState(WindowState.MAXIMIZED);
	}
	else {
		urlHelp.setWindowState(WindowState.NORMAL);
	}

	urlHelp.setPortletMode(PortletMode.HELP);
}

urlHelp.setEscapeXml(false);

portletDisplay.setURLHelp(urlHelp.toString());

// URL max

String lifecycle = PortletRequest.RENDER_PHASE;

if (!portletDisplay.isRestoreCurrentView()) {
	lifecycle = PortletRequest.ACTION_PHASE;
}

LiferayPortletURL urlMax = PortletURLFactoryUtil.create(request, portlet, lifecycle);

if (portletDisplay.isStateMax()) {
	urlMax.setWindowState(WindowState.NORMAL);
}
else {
	urlMax.setWindowState(WindowState.MAXIMIZED);
}

if (urlMax instanceof LiferayPortletURL) {
	LiferayPortletURL liferayPortletURL = (LiferayPortletURL)urlMax;

	liferayPortletURL.setWindowStateRestoreCurrentView(true);
}
else {
	try {
		BeanPropertiesUtil.setProperty(urlMax, "windowStateRestoreCurrentView", true);
	}
	catch (Exception e) {
		_log.error(ClassUtil.getClassName(urlMax) + " must implement the method setWindowStateRestoreCurrentView(boolean)", e);
	}
}

urlMax.setEscapeXml(false);

if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
	Map<String, String[]> renderParameters = RenderParametersPool.get(request, plid, portletDisplay.getId());

	if (renderParameters != null) {
		String portletNamespace = portletDisplay.getNamespace();
		Set<String> publicRenderParameterNames = SetUtil.fromEnumeration(portletConfig.getPublicRenderParameterNames());

		for (Map.Entry<String, String[]> entry : renderParameters.entrySet()) {
			String key = entry.getKey();

			if (key.startsWith(portletNamespace) || publicRenderParameterNames.contains(key)) {
				if (key.startsWith(portletNamespace)) {
					key = key.substring(portletNamespace.length());
				}

				PortletApp portletApp = portlet.getPortletApp();

				if (portletApp.getSpecMajorVersion() >= 3) {
					MutableRenderParameters mutableRenderParameters = urlMax.getRenderParameters();

					mutableRenderParameters.setValues(key, entry.getValue());
				}
				else {
					urlMax.setParameter(key, entry.getValue());
				}
			}
		}
	}
}

portletDisplay.setURLMax(urlMax.toString());

// URL min

sb = new StringBundler(16);

sb.append(themeDisplay.getPathMain());
sb.append("/portal/update_layout?p_l_id=");
sb.append(plid);
sb.append("&p_p_id=");
sb.append(portletDisplay.getId());
sb.append("&p_p_restore=");
sb.append(portletDisplay.isStateMin());
sb.append("&p_v_l_s_g_id=");
sb.append(themeDisplay.getSiteGroupId());

if (doAsUserId != null) {
	sb.append("&doAsUserId=");
	sb.append(URLCodec.encodeURL(doAsUserId));
}

sb.append("&");
sb.append(Constants.CMD);
sb.append("=minimize&referer=");

innerSB = new StringBundler(7);

innerSB.append(themeDisplay.getPathMain());
innerSB.append("/portal/layout?p_auth=");
innerSB.append(AuthTokenUtil.getToken(request));
innerSB.append("&p_l_id=");
innerSB.append(plid);

if (doAsUserId != null) {
	innerSB.append("&doAsUserId=");
	innerSB.append(doAsUserId);
}

sb.append(URLCodec.encodeURL(innerSB.toString()));

sb.append("&refresh=1");

portletDisplay.setURLMin(sb.toString());

// URL print

LiferayPortletURL urlPrint = PortletURLFactoryUtil.create(request, portlet, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModePrint()) {
	urlPrint.setWindowState(WindowState.NORMAL);
	urlPrint.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isPopUpPrint()) {
		urlPrint.setWindowState(LiferayWindowState.POP_UP);
	}
	else {
		urlPrint.setWindowState(WindowState.NORMAL);
	}

	urlPrint.setPortletMode(LiferayPortletMode.PRINT);
}

urlPrint.setEscapeXml(false);

portletDisplay.setURLPrint(urlPrint.toString());

// URL refresh

String urlRefresh = "javascript:void(0);";

portletDisplay.setURLRefresh(urlRefresh);

// URL staging

LiferayPortletURL urlStaging = PortletURLFactoryUtil.create(request, exportImportPortlet, PortletRequest.RENDER_PHASE);

urlStaging.setWindowState(LiferayWindowState.POP_UP);

urlStaging.setParameter("mvcRenderCommandName", "/export_import/publish_portlet");
urlStaging.setParameter("cmd", Constants.PUBLISH_TO_LIVE);
urlStaging.setParameter("redirect", currentURL);
urlStaging.setParameter("returnToFullPageURL", currentURL);
urlStaging.setParameter("portletResource", portletDisplay.getId());

urlStaging.setEscapeXml(false);

portletDisplay.setURLStaging(urlStaging.toString() + "&" + PortalUtil.getPortletNamespace(PortletKeys.EXPORT_IMPORT));

// URL back

String urlBack = null;

if (portletDisplay.isModeEdit()) {
	urlBack = urlEdit.toString();
}
else if (portletDisplay.isModeEditDefaults()) {
	urlBack = urlEditDefaults.toString();
}
else if (portletDisplay.isModeEditGuest()) {
	urlBack = urlEditGuest.toString();
}
else if (portletDisplay.isModeHelp()) {
	urlBack = urlHelp.toString();
}
else if (portletDisplay.isModePrint()) {
	urlBack = urlPrint.toString();
}
else if (portletDisplay.isStateMax()) {
	urlBack = ParamUtil.getString(liferayRenderRequest, "returnToFullPageURL");
	urlBack = PortalUtil.escapeRedirect(urlBack);

	if (Validator.isNull(urlBack)) {
		urlBack = urlMax.toString();
	}
}

if (urlBack != null) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(urlBack);
}

if (themeDisplay.isWidget()) {
	portletDisplay.setShowBackIcon(false);
}

if (group.isControlPanel()) {
	portletDisplay.setShowBackIcon(false);
	portletDisplay.setShowConfigurationIcon(false);
	portletDisplay.setShowMoveIcon(false);
	portletDisplay.setShowPortletCssIcon(false);

	if (!portlet.isPreferencesUniquePerLayout() && (portlet.getConfigurationActionInstance() != null) && PortletPermissionUtil.contains(permissionChecker, themeDisplay.getScopeGroupId(), curLayout, portlet, ActionKeys.CONFIGURATION)) {
		portletDisplay.setShowConfigurationIcon(true);
	}
}
%>

<%@ include file="/html/portal/render_portlet-ext.jsp" %>

<%

// Render portlet

boolean portletException = false;
Boolean portletVisibility = null;

if (portlet.isActive() && portlet.isInclude() && portlet.isReady() && supportsMimeType && (invokerPortlet != null)) {
	try {
		if (!PortalUtil.isSkipPortletContentRendering(group, layoutTypePortlet, portletDisplay, portletDisplay.getPortletName())) {
			invokerPortlet.render(liferayRenderRequest, liferayRenderResponse);
		}

		portletVisibility = (Boolean)liferayRenderRequest.getAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY);

		if (portletVisibility != null) {
			request.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, portletVisibility);
		}

		liferayRenderResponse.transferHeaders(bufferCacheServletResponse);
	}
	catch (UnavailableException ue) {
		portletException = true;

		if (ue.isPermanent()) {
			PortletInstanceFactoryUtil.destroy(portlet);
		}
	}
	catch (Exception e) {
		portletException = true;

		LogUtil.log(_log, e);
	}
}

String portalProductMenuApplicationTypePortletId = PortletProviderUtil.getPortletId(PortalProductMenuApplicationType.ProductMenu.CLASS_NAME, PortletProvider.Action.VIEW);

if ((layout.isTypePanel() || layout.isTypeControlPanel()) && !Objects.equals(portletDisplay.getId(), portalProductMenuApplicationTypePortletId) && !portlet.isStatic()) {
	PortalUtil.setPageTitle(portletDisplay.getTitle(), request);
}

Boolean renderPortletBoundary = GetterUtil.getBoolean(request.getAttribute(WebKeys.RENDER_PORTLET_BOUNDARY), true) && !themeDisplay.isStateExclusive() && portlet.isInclude();
%>

<c:if test="<%= renderPortletBoundary %>">

	<%
	if ((themeDisplay.isStatePopUp() || themeDisplay.isWidget()) && !portlet.isStatic()) {
		PortalUtil.setPageTitle(portletDisplay.getTitle(), request);
	}

	String cssClasses = StringPool.BLANK;

	if (portletDisplay.isStateMin()) {
		cssClasses += " portlet-minimized";
	}

	if (!portletDisplay.isShowMoveIcon()) {
		if (columnDisabled && customizable) {
			cssClasses += " portlet-static";
		}
		else if (portlet.isStaticStart()) {
			cssClasses += " portlet-static portlet-static-start";
		}
		else if (portlet.isStaticEnd()) {
			cssClasses += " portlet-static portlet-static-end";
		}
	}

	// Portlet decorator

	String portletDecoratorId = setupPortletPreferences.getValue("portletSetupPortletDecoratorId", StringPool.BLANK);

	PortletDecorator portletDecorator = ThemeLocalServiceUtil.getPortletDecorator(company.getCompanyId(), theme.getThemeId(), portletDecoratorId);

	if ((portletDecorator != null) && portletDisplay.isPortletDecorate()) {
		portletDisplay.setPortletDecoratorId(portletDecorator.getPortletDecoratorId());

		cssClasses += StringPool.SPACE + portletDecorator.getCssClass();
	}

	cssClasses = "portlet-boundary portlet-boundary" + HtmlUtil.escapeAttribute(PortalUtil.getPortletNamespace(rootPortletId)) + StringPool.SPACE + cssClasses + StringPool.SPACE + portlet.getCssClassWrapper() + StringPool.SPACE + HtmlUtil.escapeAttribute(customCSSClassName);

	if (portletResourcePortlet != null) {
		cssClasses += StringPool.SPACE + portletResourcePortlet.getCssClassWrapper();
	}

	if ((portletVisibility != null) && !layout.isTypeControlPanel()) {
		cssClasses += " lfr-configurator-visibility";
	}
	%>

	<div class="<%= cssClasses %>" id="p_p_id<%= HtmlUtil.escapeAttribute((PortalUtil.getLiferayPortletResponse(liferayRenderResponse)).getNamespace()) %>">
		<span id="p_<%= HtmlUtil.escapeAttribute(portletId) %>"></span>
</c:if>

<c:if test="<%= supportsMimeType && (portlet.isActive() || portlet.isShowPortletInactive()) && portlet.isInclude() %>">

	<%
	boolean useDefaultTemplate = liferayRenderResponse.getUseDefaultTemplate();

	boolean addNotAjaxablePortlet = !portlet.isAjaxable() && cmd.equals("add");

	liferayRenderRequest.setAttribute(WebKeys.PORTLET_CONTENT, bufferCacheServletResponse.getString());

	if (!portlet.isReady()) {
		request.setAttribute(WebKeys.PORTLET_CONTENT_JSP, "/portal/portlet_not_ready.jsp");
	}

	if (portletException) {
		request.setAttribute(WebKeys.PORTLET_CONTENT_JSP, "/portal/portlet_error.jsp");
	}

	if (addNotAjaxablePortlet) {
		request.setAttribute(WebKeys.PORTLET_CONTENT_JSP, "/portal/portlet_not_ajaxable.jsp");
	}
	%>

	<c:choose>
		<c:when test="<%= useDefaultTemplate || portletException || addNotAjaxablePortlet %>">
			<liferay-util:include page='<%= StrutsUtil.TEXT_HTML_DIR + "/common/themes/portlet.jsp" %>' />
		</c:when>
		<c:otherwise>
			<%= liferayRenderRequest.getAttribute(WebKeys.PORTLET_CONTENT) %>
		</c:otherwise>
	</c:choose>
</c:if>

<%
boolean canEditTitle = showConfigurationIcon;

if (layout.isSystem() && !layout.isTypeControlPanel() && Objects.equals(layout.getFriendlyURL(), PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL)) {
	canEditTitle = false;
}

String staticVar = "yes";

if (portletDisplay.isShowMoveIcon()) {
	staticVar = "no";
}
else {
	if (portlet.isStaticStart()) {
		staticVar = "start";
	}

	if (portlet.isStaticEnd()) {
		staticVar = "end";
	}
}
%>

<aui:script position='<%= themeDisplay.isIsolated() ? "inline" : "auto" %>'>
	<c:if test="<%= !layoutTypePortlet.hasStateMax() && !themeDisplay.isStatePopUp() %>">
		Liferay.Portlet.register('<%= HtmlUtil.escapeJS(portletDisplay.getId()) %>');
	</c:if>

	Liferay.Portlet.onLoad({
		canEditTitle: <%= canEditTitle %>,
		columnPos: <%= columnPos %>,
		isAjaxable: <%= portlet.isAjaxable() %>,
		isStatic: '<%= staticVar %>',
		namespacedId:
			'p_p_id<%= HtmlUtil.escapeJS((PortalUtil.getLiferayPortletResponse(liferayRenderResponse)).getNamespace()) %>',
		portletId: '<%= HtmlUtil.escapeJS(portletDisplay.getId()) %>',
		refreshURL:
			'<%= HtmlUtil.escapeJS(PortletURLUtil.getRefreshURL(request, themeDisplay, false)) %>',
		refreshURLData:
			<%= JSONFactoryUtil.looseSerializeDeep(PortletURLUtil.getRefreshURLParameters(request)) %>,
	});
</aui:script>

<c:if test="<%= renderPortletBoundary %>">
	</div>
</c:if>

<c:if test="<%= themeDisplay.isStatePopUp() %>">

	<%
	String refreshPortletId = null;
	%>

	<c:if test="<%= (refreshPortletId = (String)SessionMessages.get(liferayRenderRequest, portletId + SessionMessages.KEY_SUFFIX_REFRESH_PORTLET)) != null %>">

		<%
		if (Validator.isNull(refreshPortletId) && (portletResourcePortlet != null)) {
			refreshPortletId = portletResourcePortlet.getPortletId();
		}

		Map<String, String> refreshPortletData = (Map<String, String>)SessionMessages.get(liferayRenderRequest, portletId + SessionMessages.KEY_SUFFIX_REFRESH_PORTLET_DATA);
		%>

		<aui:script position="inline" use="aui-base">
			if (window.parent) {
				var data = {
					portletAjaxable:
						<%= !(((portletResourcePortlet != null) && !portletResourcePortlet.isAjaxable()) || SessionMessages.contains(liferayRenderRequest, portletId + SessionMessages.KEY_SUFFIX_PORTLET_NOT_AJAXABLE)) %>,
				};

				<c:if test="<%= (refreshPortletData != null) && !refreshPortletData.isEmpty() %>">

					<%
					for (Map.Entry<String, String> entry : refreshPortletData.entrySet()) {
					%>

						data['<%= entry.getKey() %>'] = <%= entry.getValue() %>;

					<%
					}
					%>

				</c:if>

				Liferay.Util.getOpener().Liferay.Portlet.refresh(
					'#p_p_id_<%= HtmlUtil.escapeJS(refreshPortletId) %>_',
					data
				);
			}
		</aui:script>
	</c:if>

	<%
	String closeRedirect = null;
	%>

	<c:if test="<%= (closeRedirect = (String)SessionMessages.get(liferayRenderRequest, portletId + SessionMessages.KEY_SUFFIX_CLOSE_REDIRECT)) != null %>">
		<aui:script use="aui-base">
			var dialog = Liferay.Util.getWindow();

			if (dialog) {
				var hideDialogSignature = '<portlet:namespace />hideRefreshDialog|*';

				dialog.detach(hideDialogSignature);

				dialog.on(
					'<portlet:namespace />hideRefreshDialog|visibleChange',
					function (event) {
						if (!event.newVal && event.src !== 'hideLink') {
							var refreshWindow =
								dialog._refreshWindow || Liferay.Util.getTop();

							var topA = refreshWindow.AUI();

							topA.use('aui-loading-mask-deprecated', function (A) {
								new A.LoadingMask({
									target: A.getBody(),
								}).show();
							});

							refreshWindow.location.href =
								'<%= HtmlUtil.escapeJS(closeRedirect) %>';
						}
						else {
							dialog.detach(hideDialogSignature);
						}
					}
				);
			}
		</aui:script>
	</c:if>

	<%
	String closeRefreshPortletId = null;
	%>

	<c:if test="<%= (closeRefreshPortletId = (String)SessionMessages.get(liferayRenderRequest, portletId + SessionMessages.KEY_SUFFIX_CLOSE_REFRESH_PORTLET)) != null %>">

		<%
		Map<String, String> refreshPortletData = (Map<String, String>)SessionMessages.get(liferayRenderRequest, portletId + SessionMessages.KEY_SUFFIX_REFRESH_PORTLET_DATA);
		%>

		<aui:script use="aui-base">
			var dialog = Liferay.Util.getWindow();

			var hideDialogSignature = '<portlet:namespace />hideRefreshDialog|*';

			dialog.detach(hideDialogSignature);

			dialog.on(
				'<portlet:namespace />hideRefreshDialog|visibleChange',
				function (event) {
					if (!event.newVal && event.src !== 'hideLink') {
						var refreshWindow = dialog._refreshWindow || Liferay.Util.getTop();

						if (window.parent) {
							var data = {
								portletAjaxable:
									<%= !(((portletResourcePortlet != null) && !portletResourcePortlet.isAjaxable()) || SessionMessages.contains(liferayRenderRequest, portletId + SessionMessages.KEY_SUFFIX_PORTLET_NOT_AJAXABLE)) %>,
							};

							<c:if test="<%= (refreshPortletData != null) && !refreshPortletData.isEmpty() %>">

								<%
								for (Map.Entry<String, String> entry : refreshPortletData.entrySet()) {
								%>

									data['<%= entry.getKey() %>'] = <%= entry.getValue() %>;

								<%
								}
								%>

							</c:if>

							refreshWindow.Liferay.Portlet.refresh(
								'#p_p_id_<%= closeRefreshPortletId %>_',
								data
							);
						}
					}
					else {
						dialog.detach(hideDialogSignature);
					}
				}
			);
		</aui:script>
	</c:if>
</c:if>

<%
if (showPortletCssIcon) {
	themeDisplay.setIncludePortletCssJs(true);
}

SessionMessages.clear(liferayRenderRequest);
SessionErrors.clear(liferayRenderRequest);

liferayRenderRequest.cleanUp();
%>

<%!
private static final Log _log = LogFactoryUtil.getLog("portal_web.docroot.html.portal.render_portlet_jsp");
%>