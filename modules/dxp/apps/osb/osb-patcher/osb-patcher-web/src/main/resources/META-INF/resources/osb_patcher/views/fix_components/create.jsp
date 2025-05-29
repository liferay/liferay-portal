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
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_fix_components" />
</liferay-util:include>

<aui:model-context bean="<%= null %>" model="<%= PatcherFixComponent.class %>" />

<portlet:actionURL name="/patcher/add_fix_components" var="addPatcherFixComponentURL" />

<aui:form action="<%= addPatcherFixComponentURL %>" method="post">
	<portlet:renderURL var="viewPatcherFixComponentsURL">
		<portlet:param name="mvcRenderCommandName" value="/patcher/index_fix_components" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="<%= viewPatcherFixComponentsURL %>" />

	<aui:input name="name" type="text" />

	<aui:button-row>
		<aui:button type="submit" value="add" />

		<aui:button href="<%= viewPatcherFixComponentsURL %>" value="cancel" />
	</aui:button-row>
</aui:form>