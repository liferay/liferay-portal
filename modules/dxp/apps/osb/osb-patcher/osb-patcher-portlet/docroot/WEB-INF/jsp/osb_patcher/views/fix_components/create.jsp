<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/WEB-INF/jsp/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="fix-components" />
</liferay-util:include>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/header.jsp" servletContext="<%= application %>">
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