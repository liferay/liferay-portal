<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="fix-components" />
</liferay-util:include>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="${patcherFixComponent.name}" />
	<liferay-util:param name="controller" value="fix_components" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherFixComponent}" model="<%= PatcherFixComponent.class %>" />

<portlet:actionURL var="updatePatcherFixComponentURL">
	<portlet:param name="controller" value="fix_components" />
	<portlet:param name="action" value="update" />
</portlet:actionURL>

<aui:form action="${updatePatcherFixComponentURL}" method="post">
	<portlet:renderURL var="viewPatcherFixComponentsURL">
		<portlet:param name="controller" value="fix_components" />
		<portlet:param name="action" value="index" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="${viewPatcherFixComponentsURL}" />
	<aui:input name="id" type="hidden" value="${patcherFixComponent.patcherFixComponentId}" />

	<aui:input label="name" name="patcherFixComponentName" type="text" value="${patcherFixComponent.name}" />

	<aui:button-row>
		<aui:button type="submit" value="update" />

		<aui:button href="${viewPatcherFixComponentsURL}" value="cancel" />
	</aui:button-row>
</aui:form>