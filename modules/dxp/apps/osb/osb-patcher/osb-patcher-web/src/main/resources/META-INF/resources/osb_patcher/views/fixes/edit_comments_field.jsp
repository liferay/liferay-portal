<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<aui:model-context bean="${patcherFix}" model="<%= PatcherFix.class %>" />

<portlet:actionURL var="updatePatcherFixCommentsFieldURL">
	<portlet:param name="controller" value="fixes" />
	<portlet:param name="action" value="updateCommentsField" />
</portlet:actionURL>

<aui:form action="${updatePatcherFixCommentsFieldURL}" method="post">
	<aui:input name="id" type="hidden" value="${patcherFix.patcherFixId}" />

	<aui:input name="comments" />

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button onClick="Liferay.Patcher.closeWindow();" value="cancel" />
	</aui:button-row>
</aui:form>