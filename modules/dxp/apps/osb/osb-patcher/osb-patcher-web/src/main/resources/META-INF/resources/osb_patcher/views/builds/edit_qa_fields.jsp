<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
long patcherBuildId = ParamUtil.getLong(request, "patcherBuildId");

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);
%>

<aui:model-context bean="<%= patcherBuild %>" model="<%= PatcherBuild.class %>" />

<portlet:actionURL name="/patcher/update_qa_fields_builds" var="updatePatcherBuildQAFieldsURL" />

<liferay-frontend:edit-form
	action="<%= updatePatcherBuildQAFieldsURL %>"
	cssClass="pt-0"
	fluid="<%= true %>"
>
	<aui:input name="patcherBuildId" type="hidden" value="<%= patcherBuild.getPatcherBuildId() %>" />

	<liferay-frontend:edit-form-body>
		<aui:select name="qaStatus" showEmptyOption="<%= false %>">
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_NEEDED %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_STARTED %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_PASSED %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_STARTED %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_FAILED_MANUALLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_PASSED_MANUALLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_PENDING %>" value="<%= WorkflowConstants.STATUS_PENDING %>" />
			<aui:option label="<%= WorkflowConstants.LABEL_BUILD_QA_PENDING_SMOKE_ONLY %>" value="<%= WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY %>" />
		</aui:select>

		<aui:input name="qaComments" type="textarea" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>