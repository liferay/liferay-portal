<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

KaleoTaskFormPair kaleoTaskFormPair = (KaleoTaskFormPair)row.getObject();
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message="<%= StringPool.BLANK %>"
	showWhenSingleIcon="<%= true %>"
>

	<%
	KaleoProcess kaleoProcess = (KaleoProcess)request.getAttribute(KaleoFormsWebKeys.KALEO_PROCESS);

	long kaleoProcessId = BeanParamUtil.getLong(kaleoProcess, request, "kaleoProcessId");

	long ddmStructureId = KaleoFormsUtil.getKaleoProcessDDMStructureId(kaleoProcessId, portletSession);

	String workflowDefinition = ParamUtil.getString(request, "workflowDefinition");

	String initialStateName = KaleoFormsUtil.getInitialStateName(company.getCompanyId(), workflowDefinition);

	String mode = initialStateName.equals(kaleoTaskFormPair.getWorkflowTaskName()) ? DDMTemplateConstants.TEMPLATE_MODE_CREATE : DDMTemplateConstants.TEMPLATE_MODE_EDIT;

	String workflowTaskName = HtmlUtil.escapeJS(kaleoTaskFormPair.getWorkflowTaskName());
	%>

	<liferay-ui:icon
		message="assign-form"
		onClick='<%= "javascript:" + liferayPortletResponse.getNamespace() + "selectFormTemplate(" + ddmStructureId + ", '" + mode + "', '" + HtmlUtil.escapeJS(workflowDefinition) + "', '" + workflowTaskName + "');" %>'
		url="javascript:void(0);"
	/>

	<%
	DDMTemplate ddmTemplate = null;

	long ddmTemplateId = kaleoTaskFormPair.getDDMTemplateId();

	if (ddmTemplateId > 0) {
		ddmTemplate = DDMTemplateLocalServiceUtil.fetchTemplate(ddmTemplateId);
	}
	%>

	<c:if test="<%= ddmTemplate != null %>">

		<%
		long classNameId = ParamUtil.getLong(request, "classNameId");
		%>

		<liferay-ui:icon
			message="unassign-form"
			onClick='<%= "javascript:" + liferayPortletResponse.getNamespace() + "unassignForm({ddmStructureId: '" + ddmStructureId + "', workflowDefinition: '" + HtmlUtil.escapeJS(workflowDefinition) + "', workflowTaskName: '" + workflowTaskName + "', node: this});" %>'
			url="javascript:void(0);"
		/>

		<liferay-portlet:renderURL portletName="<%= PortletProviderUtil.getPortletId(DDMTemplate.class.getName(), PortletProvider.Action.EDIT) %>" var="editFormTemplateURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcPath" value="/edit_template.jsp" />
			<portlet:param name="navigationStartsOn" value="<%= DDMNavigationHelper.EDIT_TEMPLATE %>" />
			<portlet:param name="closeRedirect" value='<%= (String)row.getParameter("backURL") %>' />
			<portlet:param name="showBackURL" value="<%= Boolean.FALSE.toString() %>" />
			<portlet:param name="portletResourceNamespace" value="<%= liferayPortletResponse.getNamespace() %>" />
			<portlet:param name="refererPortletName" value="<%= KaleoFormsPortletKeys.KALEO_FORMS_ADMIN %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(scopeGroupId) %>" />
			<portlet:param name="classNameId" value="<%= String.valueOf(classNameId) %>" />
			<portlet:param name="templateId" value="<%= String.valueOf(ddmTemplate.getTemplateId()) %>" />
			<portlet:param name="showCacheableInput" value="<%= Boolean.TRUE.toString() %>" />
			<portlet:param name="structureAvailableFields" value='<%= liferayPortletResponse.getNamespace() + "getAvailableFields" %>' />
		</liferay-portlet:renderURL>

		<liferay-ui:icon
			message="edit-form"
			onClick='<%= "javascript:" + liferayPortletResponse.getNamespace() + "editFormTemplate('" + editFormTemplateURL + "');" %>'
			url="javascript:void(0);"
		/>
	</c:if>
</liferay-ui:icon-menu>