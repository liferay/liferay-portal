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
	<liferay-util:param name="tabs1" value="product-versions" />
</liferay-util:include>

<liferay-util:include page="/WEB-INF/jsp/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="create-product-version" />
	<liferay-util:param name="controller" value="product_versions" />
	<liferay-util:param name="action" value="index" />
</liferay-util:include>

<aui:model-context bean="${patcherProductVersion}" model="<%= PatcherProductVersion.class %>" />

<portlet:actionURL var="addPatcherProductVersionURL">
	<portlet:param name="controller" value="product_versions" />
	<portlet:param name="action" value="add" />
</portlet:actionURL>

<aui:form action="${addPatcherProductVersionURL}" method="post">
	<portlet:renderURL var="viewPatcherProductVersionsURL">
		<portlet:param name="controller" value="product_versions" />
		<portlet:param name="action" value="index" />
	</portlet:renderURL>

	<aui:input name="redirect" type="hidden" value="${viewPatcherProductVersionsURL}" />

	<aui:input name="name" />

	<aui:select label="fix-delivery-method" name="fixDeliveryMethod">
		<aui:option label="${PatcherProductVersionConstants.LABEL_FIX_DELIVERY_METHOD_FIX_PACK_20}" value="${PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_FIX_PACK_20}" />
		<aui:option label="${PatcherProductVersionConstants.LABEL_FIX_DELIVERY_METHOD_FIX_PACK_30}" value="${PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30}" />
		<aui:option label="${PatcherProductVersionConstants.LABEL_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE}" value="${PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE}" />
	</aui:select>

	<aui:input label="module-folder-name" name="moduleFolderName" />

	<aui:button-row>
		<aui:button type="submit" value="add" />

		<aui:button href="${viewPatcherProductVersionsURL}" value="cancel" />
	</aui:button-row>
</aui:form>