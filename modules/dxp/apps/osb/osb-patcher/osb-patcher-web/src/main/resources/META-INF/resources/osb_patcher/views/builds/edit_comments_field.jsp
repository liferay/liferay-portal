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

<aui:form action="<%= updatePatcherBuildCommentsFieldURL %>" method="post">
	<aui:input name="patcherBuildId" type="hidden" value="<%= patcherBuild.getPatcherBuildId() %>" />

	<aui:input name="comments" />

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button value="cancel" />
	</aui:button-row>
</aui:form>