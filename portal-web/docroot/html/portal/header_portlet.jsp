<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/portal/init.jsp" %>

<%
Portlet portlet = (Portlet)request.getAttribute(WebKeys.RENDER_PORTLET);

InvokerPortlet invokerPortlet = null;

try {
	if (portlet.isReady()) {
		invokerPortlet = PortletInstanceFactoryUtil.create(portlet, application);
	}
}
catch (PortletException pe) {
	_log.error(pe);
}
catch (RuntimeException re) {
	_log.error(re);
}

if ((invokerPortlet == null) || !invokerPortlet.isHeaderPortlet()) {
	return;
}

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

PortletPreferences portletPreferences = PortletPreferencesLocalServiceUtil.getStrictPreferences(PortletPreferencesFactoryUtil.getPortletPreferencesIds(request, portletId));

PortletPreferences setupPortletPreferences = themeDisplay.getStrictLayoutPortletSetup(layout, portletId);

Group group = null;

if (layout instanceof VirtualLayout) {
	VirtualLayout virtualLayout = (VirtualLayout)layout;

	Layout sourceLayout = virtualLayout.getSourceLayout();

	group = sourceLayout.getGroup();
}
else {
	group = themeDisplay.getLayoutGroup(layout);
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

BufferCacheServletResponse bufferCacheServletResponse = new BufferCacheServletResponse(response);

LiferayHeaderRequest liferayHeaderRequest = HeaderRequestFactory.create(request, portlet, invokerPortlet, portletCtx, windowState, portletMode, portletPreferences, plid);

PortletRequest portletRequest = liferayHeaderRequest;

LiferayHeaderResponse liferayHeaderResponse = HeaderResponseFactory.create(liferayHeaderRequest, bufferCacheServletResponse);

liferayHeaderRequest.defineObjects(portletConfig, liferayHeaderResponse);

String responseContentType = liferayHeaderRequest.getResponseContentType();

String portletResource = ParamUtil.getString(request, "portletResource");

if (Validator.isNull(portletResource)) {
	portletResource = ParamUtil.getString(portletRequest, "portletResource");
}

Portlet portletResourcePortlet = null;

if (Validator.isNotNull(portletResource)) {
	portletResourcePortlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletResource);
}

boolean supportsMimeType = portlet.hasPortletMode(responseContentType, portletMode);

if (responseContentType.equals(ContentTypes.XHTML_MP) && portlet.hasMultipleMimeTypes()) {
	supportsMimeType = GetterUtil.getBoolean(setupPortletPreferences.getValue("portletSetupSupportedClientsMobileDevices_" + portletMode, String.valueOf(supportsMimeType)));
}

long portletScopeGroupId = 0;

if (Validator.isNotNull(portletResource)) {
	portletScopeGroupId = PortalUtil.getScopeGroupId(request, portletResourcePortlet.getPortletId());
}
else {
	portletScopeGroupId = PortalUtil.getScopeGroupId(request, portletId);
}

if (portletScopeGroupId != themeDisplay.getScopeGroupId()) {
	themeDisplay.setScopeGroupId(portletScopeGroupId);
}

Group siteGroup = themeDisplay.getSiteGroup();

if (siteGroup.isStagingGroup()) {
	siteGroup = siteGroup.getLiveGroup();
}

if (siteGroup.isStaged() && !siteGroup.isStagedRemotely() && !siteGroup.isStagedPortlet(portletId)) {
	themeDisplay.setSiteGroupId(siteGroup.getGroupId());
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
portletDisplay.setPortletDecorate(false);
portletDisplay.setPortletDisplayName(PortalUtil.getPortletTitle(portletRequest));
portletDisplay.setPortletName(portletConfig.getPortletName());
portletDisplay.setPortletResource(portletResource);
portletDisplay.setResourcePK(portletPrimaryKey);
portletDisplay.setRestoreCurrentView(portlet.isRestoreCurrentView());
portletDisplay.setRootPortletId(rootPortletId);
portletDisplay.setStateExclusive(themeDisplay.isStateExclusive());
portletDisplay.setStateMax(stateMax);
portletDisplay.setStateMin(stateMin);
portletDisplay.setStateNormal(windowState.equals(WindowState.NORMAL));
portletDisplay.setStatePopUp(themeDisplay.isStatePopUp());
portletDisplay.setPortletPreferences(setupPortletPreferences);
portletDisplay.setWebDAVEnabledSupplier(() -> portlet.getWebDAVStorageInstance() != null);
%>

<%@ include file="/html/portal/header_portlet-ext.jsp" %>

<%

// Render portlet header

if (portlet.isActive() && portlet.isReady() && supportsMimeType && (invokerPortlet != null)) {
	try {
		if (!PortalUtil.isSkipPortletContentRendering(group, layoutTypePortlet, portletDisplay, portletDisplay.getPortletName()) && invokerPortlet.isHeaderPortlet()) {
			invokerPortlet.renderHeaders(liferayHeaderRequest, liferayHeaderResponse);

			liferayHeaderResponse.writeToHead();
		}

		liferayHeaderResponse.transferHeaders(bufferCacheServletResponse);
	}
	catch (UnavailableException ue) {
		if (ue.isPermanent()) {
			PortletInstanceFactoryUtil.destroy(portlet);
		}
	}
	catch (Exception e) {
		LogUtil.log(_log, e);
	}
}

liferayHeaderRequest.cleanUp();
%>

<%!
private static final Log _log = LogFactoryUtil.getLog("portal_web.docroot.html.portal.header_portlet_jsp");
%>