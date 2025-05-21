<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<aui:model-context bean="${patcherBuild}" model="<%= PatcherBuild.class %>" />

<portlet:actionURL var="updatePatcherBuildQAFieldsURL">
	<portlet:param name="controller" value="builds" />
	<portlet:param name="action" value="updateQAFields" />
</portlet:actionURL>

<aui:form action="${updatePatcherBuildQAFieldsURL}" method="post">
	<aui:input name="id" type="hidden" value="${patcherBuild.patcherBuildId}" />

	<aui:select name="qaStatus" showEmptyOption="${false}">
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_NEEDED}" value="${WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_STARTED}" value="${WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_PASSED}" value="${WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_STARTED}" value="${WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_FAILED_MANUALLY}" value="${WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_PASSED_MANUALLY}" value="${WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY}" />
		<aui:option label="${WorkflowConstants.LABEL_PENDING}" value="${WorkflowConstants.STATUS_PENDING}" />
		<aui:option label="${WorkflowConstants.LABEL_BUILD_QA_PENDING_SMOKE_ONLY}" value="${WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY}" />
	</aui:select>

	<aui:input name="qaComments" />

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button onClick="Liferay.Patcher.closeWindow();" value="cancel" />
	</aui:button-row>
</aui:form>