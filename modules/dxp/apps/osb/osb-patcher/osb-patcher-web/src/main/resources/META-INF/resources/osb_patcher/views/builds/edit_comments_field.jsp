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

<portlet:actionURL name="/patcher/update_comments_field_builds" var="updatePatcherBuildCommentsFieldURL" />

<liferay-frontend:edit-form
	action="<%= updatePatcherBuildCommentsFieldURL %>"
	cssClass="pt-0"
	fluid="<%= true %>"
>
	<aui:input name="patcherBuildId" type="hidden" value="<%= patcherBuild.getPatcherBuildId() %>" />

	<liferay-frontend:edit-form-body>
		<aui:input name="comments" type="textarea" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>