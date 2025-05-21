<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<aui:model-context bean="${patcherBuild}" model="<%= PatcherBuild.class %>" />

<portlet:actionURL var="updatePatcherBuildCommentsFieldURL">
	<portlet:param name="controller" value="builds" />
	<portlet:param name="action" value="updateCommentsField" />
</portlet:actionURL>

<aui:form action="${updatePatcherBuildCommentsFieldURL}" method="post">
	<aui:input name="id" type="hidden" value="${patcherBuild.patcherBuildId}" />

	<aui:input name="comments" />

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button onClick="Liferay.Patcher.closeWindow();" value="cancel" />
	</aui:button-row>
</aui:form>