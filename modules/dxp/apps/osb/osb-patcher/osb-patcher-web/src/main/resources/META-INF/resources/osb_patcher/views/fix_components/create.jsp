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
	<liferay-util:param name="title" value="add-fix-component" />
	<liferay-util:param name="controller" value="fix_components" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherFixComponent}" model="<%= PatcherFixComponent.class %>" />

<portlet:actionURL var="addPatcherFixComponentURL">
	<portlet:param name="controller" value="fix_components" />
	<portlet:param name="action" value="add" />
</portlet:actionURL>

<aui:form action="${addPatcherFixComponentURL}" method="post">
	<portlet:renderURL var="viewPatcherFixComponentsURL">
		<portlet:param name="controller" value="fix_components" />
		<portlet:param name="action" value="index" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="${viewPatcherFixComponentsURL}" />

	<aui:input label="name" name="patcherFixComponentName" type="text" />

	<aui:button-row>
		<aui:button type="submit" value="add" />

		<aui:button href="${viewPatcherFixComponentsURL}" value="cancel" />
	</aui:button-row>
</aui:form>