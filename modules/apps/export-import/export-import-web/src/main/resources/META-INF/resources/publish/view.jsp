<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-staging:defineObjects />

<%
String cmd = ParamUtil.getString(request, Constants.CMD);

if (Validator.isNull(cmd)) {
	cmd = ParamUtil.getString(request, "originalCmd", Constants.PUBLISH_TO_LIVE);
}

String publishConfigurationButtons = ParamUtil.getString(request, "publishConfigurationButtons", "custom");

long exportImportConfigurationId = 0;

ExportImportConfiguration exportImportConfiguration = null;

Map<String, Serializable> exportImportConfigurationSettingsMap = Collections.emptyMap();

Map<String, String[]> parameterMap = Collections.emptyMap();

if (SessionMessages.contains(liferayPortletRequest, portletDisplay.getId() + "exportImportConfigurationId")) {
	exportImportConfigurationId = (Long)SessionMessages.get(liferayPortletRequest, portletDisplay.getId() + "exportImportConfigurationId");

	if (exportImportConfigurationId > 0) {
		exportImportConfiguration = ExportImportConfigurationLocalServiceUtil.getExportImportConfiguration(exportImportConfigurationId);
	}

	exportImportConfigurationSettingsMap = (Map<String, Serializable>)SessionMessages.get(liferayPortletRequest, portletDisplay.getId() + "settingsMap");

	parameterMap = (Map<String, String[]>)exportImportConfigurationSettingsMap.get("parameterMap");
}
else {
	exportImportConfigurationId = ParamUtil.getLong(request, "exportImportConfigurationId");

	if (exportImportConfigurationId > 0) {
		exportImportConfiguration = ExportImportConfigurationLocalServiceUtil.getExportImportConfiguration(exportImportConfigurationId);

		exportImportConfigurationSettingsMap = exportImportConfiguration.getSettingsMap();

		parameterMap = (Map<String, String[]>)exportImportConfigurationSettingsMap.get("parameterMap");
	}
}

long layoutSetBranchId = MapUtil.getLong(parameterMap, "layoutSetBranchId", ParamUtil.getLong(request, "layoutSetBranchId"));
String layoutSetBranchName = MapUtil.getString(parameterMap, "layoutSetBranchName", ParamUtil.getString(request, "layoutSetBranchName"));

long selPlid = ParamUtil.getLong(request, "selPlid", LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

boolean configuredPublish = (exportImportConfiguration == null) ? false : true;

PortletURL customPublishURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/export_import/publish_layouts"
).setCMD(
	cmd
).setTabs1(
	privateLayout ? "private-pages" : "public-pages"
).setParameter(
	"groupId", stagingGroupId
).setParameter(
	"layoutSetBranchId", layoutSetBranchId
).setParameter(
	"privateLayout", privateLayout
).setParameter(
	"publishConfigurationButtons", "custom"
).setParameter(
	"selPlid", selPlid
).buildPortletURL();

boolean localPublishing = true;

if ((liveGroup.isStaged() && liveGroup.isStagedRemotely()) || cmd.equals(Constants.PUBLISH_TO_REMOTE)) {
	localPublishing = false;
}

UnicodeProperties liveGroupTypeSettingsUnicodeProperties = liveGroup.getTypeSettingsProperties();

PortletURL publishTemplatesURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/export_import/publish_layouts"
).setCMD(
	Constants.PUBLISH
).setParameter(
	"groupId", stagingGroupId
).setParameter(
	"layoutSetBranchId", layoutSetBranchId
).setParameter(
	"layoutSetBranchName", layoutSetBranchName
).setParameter(
	"localPublishing", localPublishing
).setParameter(
	"privateLayout", privateLayout
).setParameter(
	"publishConfigurationButtons", "saved"
).buildPortletURL();

PortletURL simplePublishRedirectURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/export_import/publish_layouts"
).setParameter(
	"groupId", groupId
).setParameter(
	"privateLayout", privateLayout
).setParameter(
	"quickPublish", true
).buildPortletURL();
%>

<c:if test='<%= !publishConfigurationButtons.equals("template") %>'>
	<clay:container-fluid
		cssClass="p-0 publish-navbar"
	>
		<clay:content-row
			verticalAlign="center"
		>
			<clay:content-col
				expand="<%= true %>"
			>
				<clay:navigation-bar
					navigationItems='<%=
						new JSPNavigationItemList(pageContext) {
							{
								add(
									navigationItem -> {
										navigationItem.setActive(publishConfigurationButtons.equals("custom"));
										navigationItem.setHref(customPublishURL.toString());
										navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "custom"));
									});

								if (FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-101272")) {
									add(
										navigationItem -> {
											navigationItem.setActive(publishConfigurationButtons.equals("saved"));
											navigationItem.setHref(publishTemplatesURL.toString());
											navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "publish-templates"));
										});
								}
							}
						}
					%>'
				/>
			</clay:content-col>

			<clay:content-col>
				<clay:link
					displayType="link"
					href='<%=
						PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCRenderCommandName(
							"/export_import/publish_layouts_simple"
						).setCMD(
							"localPublishing ? Constants.PUBLISH_TO_LIVE : Constants.PUBLISH_TO_REMOTE"
						).setRedirect(
							simplePublishRedirectURL
						).setParameter(
							"lastImportUserName", user.getFullName()
						).setParameter(
							"lastImportUserUuid", user.getUserUuid()
						).setParameter(
							"layoutSetBranchId", layoutSetBranchId
						).setParameter(
							"layoutSetBranchName", layoutSetBranchName
						).setParameter(
							"localPublishing", localPublishing
						).setParameter(
							"privateLayout", privateLayout
						).setParameter(
							"quickPublish", true
						).setParameter(
							"remoteAddress", liveGroupTypeSettingsUnicodeProperties.getProperty("remoteAddress")
						).setParameter(
							"remoteGroupId", liveGroupTypeSettingsUnicodeProperties.getProperty("remoteGroupId")
						).setParameter(
							"remotePathContext", liveGroupTypeSettingsUnicodeProperties.getProperty("remotePathContext")
						).setParameter(
							"remotePort", liveGroupTypeSettingsUnicodeProperties.getProperty("remotePort")
						).setParameter(
							"secureConnection", liveGroupTypeSettingsUnicodeProperties.getProperty("secureConnection")
						).setParameter(
							"sourceGroupId", stagingGroupId
						).setParameter(
							"targetGroupId", liveGroupId
						).buildString()
					%>'
					label="switch-to-simple-publish-process"
					small="<%= true %>"
					type="button"
				/>
			</clay:content-col>
		</clay:content-row>
	</clay:container-fluid>
</c:if>

<c:choose>
	<c:when test='<%= publishConfigurationButtons.equals("saved") && FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-101272") %>'>
		<liferay-util:include page="/publish/publish_templates/view.jsp" servletContext="<%= application %>">
			<liferay-util:param name="groupId" value="<%= String.valueOf(stagingGroupId) %>" />
			<liferay-util:param name="layoutSetBranchId" value="<%= String.valueOf(layoutSetBranchId) %>" />
			<liferay-util:param name="layoutSetBranchName" value="<%= layoutSetBranchName %>" />
			<liferay-util:param name="localPublishing" value="<%= String.valueOf(localPublishing) %>" />
			<liferay-util:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
		</liferay-util:include>
	</c:when>
	<c:when test="<%= configuredPublish %>">
		<liferay-util:include page="/publish/edit_publish_configuration.jsp" servletContext="<%= application %>">
			<liferay-util:param name="<%= Constants.CMD %>" value="<%= cmd %>" />
			<liferay-util:param name="exportImportConfigurationId" value="<%= String.valueOf(exportImportConfigurationId) %>" />
		</liferay-util:include>
	</c:when>
	<c:otherwise>
		<liferay-util:include page="/publish/edit_publish_configuration.jsp" servletContext="<%= application %>">
			<liferay-util:param name="<%= Constants.CMD %>" value="<%= cmd %>" />
			<liferay-util:param name="tabs1" value='<%= privateLayout ? "private-pages" : "public-pages" %>' />
			<liferay-util:param name="groupId" value="<%= String.valueOf(stagingGroupId) %>" />
			<liferay-util:param name="layoutSetBranchId" value="<%= String.valueOf(layoutSetBranchId) %>" />
			<liferay-util:param name="localPublishing" value="<%= String.valueOf(localPublishing) %>" />
			<liferay-util:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
			<liferay-util:param name="selPlid" value="<%= String.valueOf(selPlid) %>" />
		</liferay-util:include>
	</c:otherwise>
</c:choose>