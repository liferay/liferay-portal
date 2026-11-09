<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-util:dynamic-include key="com.liferay.dynamic.data.lists.web#/view_record.jsp#pre" />

<%
String redirect = ParamUtil.getString(request, "redirect");

DDLRecord record = (DDLRecord)request.getAttribute(DDLWebKeys.DYNAMIC_DATA_LISTS_RECORD);

long recordSetId = BeanParamUtil.getLong(record, request, "recordSetId");

DDLRecordSet recordSet = DDLRecordSetServiceUtil.getRecordSet(recordSetId);

boolean editable = ParamUtil.getBoolean(request, "editable", true);

long formDDMTemplateId = ParamUtil.getLong(request, "formDDMTemplateId");

DDMStructure ddmStructure = recordSet.getDDMStructure(formDDMTemplateId);

String version = ParamUtil.getString(request, "version", DDLRecordConstants.VERSION_DEFAULT);

DDLRecordVersion recordVersion = record.getRecordVersion(version);

DDLRecordVersion latestRecordVersion = record.getLatestRecordVersion();

if (ddlDisplayContext.isAdminPortlet()) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	renderResponse.setTitle(LanguageUtil.format(request, "view-x", ddmStructure.getName(locale), false));
}
else {
	portletDisplay.setShowBackIcon(false);
}
%>

<%@ include file="/deprecated_warning.jspf" %>

<clay:container-fluid>
	<c:if test="<%= recordVersion != null %>">
		<aui:model-context bean="<%= recordVersion %>" model="<%= DDLRecordVersion.class %>" />

		<div class="panel text-center">
			<aui:workflow-status markupView="lexicon" model="<%= DDLRecord.class %>" showHelpMessage="<%= false %>" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= recordVersion.getStatus() %>" version="<%= recordVersion.getVersion() %>" />
		</div>
	</c:if>

	<aui:fieldset>

		<%
		DDMFormValues ddmFormValues = null;

		if (recordVersion != null) {
			ddmFormValues = ddlDisplayContext.getDDMFormValues(recordVersion.getDDMStorageId());
		}

		long classNameId = PortalUtil.getClassNameId(DDMStructure.class);
		long classPK = ddmStructure.getPrimaryKey();

		if (formDDMTemplateId > 0) {
			classNameId = PortalUtil.getClassNameId(DDMTemplate.class);
			classPK = formDDMTemplateId;
		}
		%>

		<liferay-ddm:html
			classNameId="<%= classNameId %>"
			classPK="<%= classPK %>"
			ddmFormValues="<%= ddmFormValues %>"
			groupId="<%= record.getGroupId() %>"
			readOnly="<%= true %>"
			requestedLocale="<%= locale %>"
		/>

		<aui:button-row>
			<c:if test="<%= editable && DDLRecordSetPermission.contains(permissionChecker, record.getRecordSet(), ActionKeys.UPDATE) && version.equals(latestRecordVersion.getVersion()) %>">
				<portlet:renderURL var="editRecordURL" windowState="<%= WindowState.MAXIMIZED.toString() %>">
					<portlet:param name="mvcPath" value="/edit_record.jsp" />
					<portlet:param name="redirect" value="<%= redirect %>" />
					<portlet:param name="recordId" value="<%= String.valueOf(record.getRecordId()) %>" />
					<portlet:param name="formDDMTemplateId" value="<%= String.valueOf(formDDMTemplateId) %>" />
				</portlet:renderURL>

				<aui:button href="<%= editRecordURL %>" name="edit" primary="<%= true %>" value="edit" />
			</c:if>

			<aui:button href="<%= redirect %>" name="cancelButton" type="cancel" />
		</aui:button-row>
	</aui:fieldset>
</clay:container-fluid>

<%
PortalUtil.addPortletBreadcrumbEntry(
	request, recordSet.getName(locale),
	PortletURLBuilder.createRenderURL(
		renderResponse
	).setMVCPath(
		"/view_record_set.jsp"
	).setParameter(
		"recordSetId", recordSetId
	).buildString());

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.format(request, "view-x", ddmStructure.getName(locale), false), currentURL);
%>

<liferay-util:dynamic-include key="com.liferay.dynamic.data.lists.web#/view_record.jsp#post" />