<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="product-versions" />
</liferay-util:include>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
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