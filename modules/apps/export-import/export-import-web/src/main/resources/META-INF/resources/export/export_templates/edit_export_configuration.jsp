<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/export/init.jsp" %>

<%
if (!FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-96546")) {
%>

	<liferay-util:include page="/export/view_export_layouts.jsp" servletContext="<%= application %>" />

<%
	return;
}
%>

<liferay-staging:defineObjects />

<%
String cmd = ParamUtil.getString(request, Constants.CMD);

if (Validator.isNull(cmd)) {
	cmd = Constants.ADD;
}

if (liveGroup == null) {
	liveGroup = group;
	liveGroupId = groupId;
}

long exportImportConfigurationId = 0;

ExportImportConfiguration exportImportConfiguration = null;
Map<String, Serializable> exportImportConfigurationSettingsMap = Collections.emptyMap();

if (SessionMessages.contains(liferayPortletRequest, portletDisplay.getId() + "exportImportConfigurationId")) {
	exportImportConfigurationId = (Long)SessionMessages.get(liferayPortletRequest, portletDisplay.getId() + "exportImportConfigurationId");

	if (exportImportConfigurationId > 0) {
		exportImportConfiguration = ExportImportConfigurationLocalServiceUtil.getExportImportConfiguration(exportImportConfigurationId);
	}

	exportImportConfigurationSettingsMap = (Map<String, Serializable>)SessionMessages.get(liferayPortletRequest, portletDisplay.getId() + "settingsMap");
}
else {
	exportImportConfigurationId = ParamUtil.getLong(request, "exportImportConfigurationId");

	if (exportImportConfigurationId > 0) {
		exportImportConfiguration = ExportImportConfigurationLocalServiceUtil.getExportImportConfiguration(exportImportConfigurationId);

		exportImportConfigurationSettingsMap = exportImportConfiguration.getSettingsMap();
	}
}

if (MapUtil.isNotEmpty(exportImportConfigurationSettingsMap)) {
	privateLayout = GetterUtil.getBoolean(exportImportConfigurationSettingsMap.get("privateLayout"), privateLayout);
}

String treeId = "layoutsExportTree" + liveGroupId + privateLayout;

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/export_import/view_export_configurations"
).setParameter(
	"groupId", groupId
).setParameter(
	"liveGroupId", liveGroupId
).setParameter(
	"privateLayout", privateLayout
).buildPortletURL();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(portletURL.toString());

renderResponse.setTitle((exportImportConfiguration == null) ? LanguageUtil.get(request, "new-export-template") : exportImportConfiguration.getName());
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<portlet:actionURL name="/export_import/edit_export_configuration" var="updateExportConfigurationURL">
		<portlet:param name="mvcRenderCommandName" value="/export_import/edit_export_configuration" />
	</portlet:actionURL>

	<aui:form action='<%= updateExportConfigurationURL + "&etag=0&strip=0" %>' cssClass="lfr-export-dialog" method="post" name="fm1">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= cmd %>" />
		<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
		<aui:input name="exportImportConfigurationId" type="hidden" value="<%= exportImportConfigurationId %>" />
		<aui:input name="groupId" type="hidden" value="<%= String.valueOf(groupId) %>" />
		<aui:input name="liveGroupId" type="hidden" value="<%= String.valueOf(liveGroupId) %>" />
		<aui:input name="privateLayout" type="hidden" value="<%= String.valueOf(privateLayout) %>" />
		<aui:input name="treeId" type="hidden" value="<%= treeId %>" />
		<aui:input name="<%= PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL %>" type="hidden" value="<%= true %>" />
		<aui:input name="<%= PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL %>" type="hidden" value="<%= true %>" />
		<aui:input name="<%= PortletDataHandlerKeys.PORTLET_SETUP_ALL %>" type="hidden" value="<%= true %>" />
		<aui:input name="<%= PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL %>" type="hidden" value="<%= true %>" />

		<liferay-ui:error exception="<%= LARFileNameException.class %>" message="please-enter-a-file-with-a-valid-file-name" />

		<div class="export-dialog-tree">
			<div class="sheet">
				<div class="panel-group panel-group-flush">
					<liferay-staging:configuration-header
						exportImportConfiguration="<%= exportImportConfiguration %>"
					/>

					<liferay-staging:deletions
						cmd="<%= Constants.EXPORT %>"
						exportImportConfigurationId="<%= exportImportConfigurationId %>"
					/>

					<c:if test="<%= !group.isLayoutPrototype() && !group.isCompany() && GroupCapabilityUtil.isSupportsPages(group) %>">
						<liferay-staging:select-pages
							action="<%= Constants.EXPORT %>"
							exportImportConfigurationId="<%= exportImportConfigurationId %>"
							groupId="<%= liveGroupId %>"
							privateLayout="<%= privateLayout %>"
							treeId="<%= treeId %>"
						/>
					</c:if>

					<liferay-staging:content
						cmd="<%= cmd %>"
						exportImportConfigurationId="<%= exportImportConfigurationId %>"
						showAllPortlets="<%= true %>"
						type="<%= Constants.EXPORT %>"
					/>

					<liferay-staging:permissions
						action="<%= Constants.EXPORT %>"
						descriptionCSSClass="permissions-description"
						exportImportConfigurationId="<%= exportImportConfigurationId %>"
						global="<%= group.isCompany() %>"
						labelCSSClass="permissions-label"
					/>
				</div>
			</div>

			<div class="sheet-footer">
				<aui:button type="submit" value="save" />

				<aui:button href="<%= portletURL.toString() %>" type="cancel" />
			</div>
		</div>
	</aui:form>
</clay:container-fluid>

<aui:script use="liferay-export-import-export-import">
	var exportImport = new Liferay.ExportImport({
		archivedSetupsNode:
			'#<%= PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL %>',
		commentsNode: '#<%= PortletDataHandlerKeys.COMMENTS %>',
		deletionsNode: '#<%= PortletDataHandlerKeys.DELETIONS %>',
		exportLAR: true,
		form: document.<portlet:namespace />fm1,
		incompleteProcessMessageNode:
			'#<portlet:namespace />incompleteProcessMessage',
		locale: '<%= locale.toLanguageTag() %>',
		namespace: '<portlet:namespace />',
		pageTreeId: '<%= treeId %>',
		rangeAllNode: '#rangeAll',
		rangeDateRangeNode: '#rangeDateRange',
		rangeLastNode: '#rangeLast',
		ratingsNode: '#<%= PortletDataHandlerKeys.RATINGS %>',
		setupNode: '#<%= PortletDataHandlerKeys.PORTLET_SETUP_ALL %>',
		timeZoneOffset: <%= timeZoneOffset %>,
		userPreferencesNode:
			'#<%= PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL %>',
	});

	Liferay.component('<portlet:namespace />ExportImportComponent', exportImport);

	var form = A.one('#<portlet:namespace />fm1');

	form.on('submit', (event) => {
		event.halt();

		var exportImport = Liferay.component(
			'<portlet:namespace />ExportImportComponent'
		);

		var dateChecker = exportImport.getDateRangeChecker();

		if (dateChecker.validRange) {
			submitForm(form, form.attr('action'), false);
		}
		else {
			exportImport.showNotification(dateChecker);
		}
	});
</aui:script>

<aui:script>
	Liferay.Util.toggleRadio(
		'<portlet:namespace />chooseApplications',
		'<portlet:namespace />selectApplications',
		['<portlet:namespace />showChangeGlobalConfiguration']
	);
	Liferay.Util.toggleRadio(
		'<portlet:namespace />allApplications',
		'<portlet:namespace />showChangeGlobalConfiguration',
		['<portlet:namespace />selectApplications']
	);

	Liferay.Util.toggleRadio('<portlet:namespace />rangeAll', '', [
		'<portlet:namespace />startEndDate',
		'<portlet:namespace />rangeLastInputs',
	]);
	Liferay.Util.toggleRadio(
		'<portlet:namespace />rangeDateRange',
		'<portlet:namespace />startEndDate',
		'<portlet:namespace />rangeLastInputs'
	);
	Liferay.Util.toggleRadio(
		'<portlet:namespace />rangeLast',
		'<portlet:namespace />rangeLastInputs',
		['<portlet:namespace />startEndDate']
	);
</aui:script>