<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/import/init.jsp" %>

<%
boolean privateLayout = ParamUtil.getBoolean(request, "privateLayout");

String displayStyle = ParamUtil.getString(request, "displayStyle");

if (Validator.isNotNull(displayStyle) && Validator.isNotNull(displayStyle)) {
	portalPreferences.setValue(portletDisplay.getPortletName(), "displayStyle", displayStyle);
	portalPreferences.setValue(portletDisplay.getPortletName(), "displayStyle", displayStyle);
}
else {
	displayStyle = portalPreferences.getValue(portletDisplay.getPortletName(), "displayStyle", "descriptive");
}

String navigation = ParamUtil.getString(request, "navigation", "all");

String orderByCol = ParamUtil.getString(request, "orderByCol");
String orderByType = ParamUtil.getString(request, "orderByType");

if (Validator.isNotNull(orderByCol) && Validator.isNotNull(orderByType)) {
	portalPreferences.setValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-col", orderByCol);
	portalPreferences.setValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-type", orderByType);
}
else {
	orderByCol = portalPreferences.getValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-col", "create-date");
	orderByType = portalPreferences.getValue(PortletKeys.BACKGROUND_TASK, "entries-order-by-type", "desc");
}

String searchContainerId = "importLayoutProcesses";

GroupDisplayContextHelper groupDisplayContextHelper = new GroupDisplayContextHelper(request);
%>

<c:choose>
	<c:when test="<%= (!stagingGroupHelper.isCompanyGroup(groupDisplayContextHelper.getGroup()) && !GroupPermissionUtil.contains(permissionChecker, groupDisplayContextHelper.getGroupId(), ActionKeys.EXPORT_IMPORT_LAYOUTS)) || (stagingGroupHelper.isCompanyGroup(groupDisplayContextHelper.getGroup()) && !PortletPermissionUtil.contains(permissionChecker, layout, portletDisplay.getPortletName(), ActionKeys.ACCESS_IN_CONTROL_PANEL)) %>">
		<div class="alert alert-info">
			<liferay-ui:message key="you-do-not-have-permission-to-access-the-requested-resource" />
		</div>
	</c:when>
	<c:otherwise>
		<liferay-util:include page="/import/navigation.jsp" servletContext="<%= application %>" />

		<liferay-util:include page="/toolbar.jsp" servletContext="<%= application %>">
			<liferay-util:param name="mvcRenderCommandName" value="/export_import/view_import_layouts" />
			<liferay-util:param name="groupId" value="<%= String.valueOf(groupDisplayContextHelper.getGroupId()) %>" />
			<liferay-util:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
			<liferay-util:param name="displayStyle" value="<%= displayStyle %>" />
			<liferay-util:param name="navigation" value="<%= navigation %>" />
			<liferay-util:param name="orderByCol" value="<%= orderByCol %>" />
			<liferay-util:param name="orderByType" value="<%= orderByType %>" />
			<liferay-util:param name="searchContainerId" value="<%= searchContainerId %>" />
		</liferay-util:include>

		<liferay-util:include page="/import/processes_list/view.jsp" servletContext="<%= application %>">
			<liferay-util:param name="groupId" value="<%= String.valueOf(groupDisplayContextHelper.getGroupId()) %>" />
			<liferay-util:param name="displayStyle" value="<%= displayStyle %>" />
			<liferay-util:param name="navigation" value="<%= navigation %>" />
			<liferay-util:param name="orderByCol" value="<%= orderByCol %>" />
			<liferay-util:param name="orderByType" value="<%= orderByType %>" />
			<liferay-util:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
			<liferay-util:param name="searchContainerId" value="<%= searchContainerId %>" />
		</liferay-util:include>

		<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-11309") %>'>
			<liferay-frontend:component
				module="{setupMocks} from exportimport-web"
			/>
		</c:if>
	</c:otherwise>
</c:choose>

<aui:script use="liferay-export-import-export-import">
	<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/export_import/import_layouts" var="importProcessesURL">
		<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.IMPORT %>" />
		<portlet:param name="<%= SearchContainer.DEFAULT_CUR_PARAM %>" value="<%= ParamUtil.getString(request, SearchContainer.DEFAULT_CUR_PARAM) %>" />
		<portlet:param name="<%= SearchContainer.DEFAULT_DELTA_PARAM %>" value="<%= ParamUtil.getString(request, SearchContainer.DEFAULT_DELTA_PARAM) %>" />
		<portlet:param name="groupId" value="<%= String.valueOf(groupDisplayContextHelper.getGroupId()) %>" />
		<portlet:param name="privateLayout" value="<%= String.valueOf(privateLayout) %>" />
		<portlet:param name="displayStyle" value="<%= displayStyle %>" />
		<portlet:param name="navigation" value="<%= navigation %>" />
		<portlet:param name="orderByCol" value="<%= orderByCol %>" />
		<portlet:param name="orderByType" value="<%= orderByType %>" />
		<portlet:param name="searchContainerId" value="<%= searchContainerId %>" />
	</liferay-portlet:resourceURL>

	var exportImport = new Liferay.ExportImport({
		incompleteProcessMessageNode:
			'#<portlet:namespace />incompleteProcessMessage',
		locale: '<%= locale.toLanguageTag() %>',
		namespace: '<portlet:namespace />',
		processesNode: '#importProcessesSearchContainer',
		processesResourceURL:
			'<%= HtmlUtil.escapeJS(importProcessesURL.toString()) %>',
		timeZoneOffset: <%= timeZoneOffset %>,
	});

	var destroyInstance = function (event) {
		if (event.portletId === '<%= portletDisplay.getRootPortletId() %>') {
			exportImport.destroy();

			Liferay.detach('destroyPortlet', destroyInstance);
		}
	};

	Liferay.on('destroyPortlet', destroyInstance);
</aui:script>