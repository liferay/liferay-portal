<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
KaleoProcess kaleoProcess = (KaleoProcess)request.getAttribute(KaleoFormsWebKeys.KALEO_PROCESS);

long kaleoProcessId = BeanParamUtil.getLong(kaleoProcess, request, "kaleoProcessId");

long ddmStructureId = KaleoFormsUtil.getKaleoProcessDDMStructureId(kaleoProcess, portletSession);

String workflowDefinition = KaleoFormsUtil.getWorkflowDefinition(kaleoProcess, portletSession);
%>

<h3 class="kaleo-process-header"><liferay-ui:message key="forms" /></h3>

<p class="kaleo-process-message"><liferay-ui:message key="please-select-or-create-one-form-for-each-workflow-task.-each-form-is-a-subset-of-the-field-set-defined-in-step-2" /></p>

<aui:field-wrapper>

	<%
	KaleoTaskFormPairs kaleoTaskFormPairs = KaleoFormsUtil.getKaleoTaskFormPairs(company.getCompanyId(), kaleoProcessId, ddmStructureId, workflowDefinition, portletSession);
	%>

	<aui:input name="kaleoTaskFormPairsData" type="hidden" value="<%= kaleoTaskFormPairs.toString() %>" />
</aui:field-wrapper>

<portlet:renderURL var="backURL">
	<portlet:param name="mvcPath" value="/admin/edit_kaleo_process.jsp" />
	<portlet:param name="historyKey" value="forms" />
	<portlet:param name="kaleoProcessId" value="<%= String.valueOf(kaleoProcessId) %>" />
</portlet:renderURL>

<div id="<portlet:namespace />resultsContainer">
	<liferay-util:include page="/admin/process/task_template_search_container.jsp" servletContext="<%= application %>">
		<liferay-util:param name="backURL" value="<%= backURL %>" />
		<liferay-util:param name="kaleoProcessId" value="<%= String.valueOf(kaleoProcessId) %>" />
		<liferay-util:param name="workflowDefinition" value="<%= workflowDefinition %>" />
	</liferay-util:include>
</div>