<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
%>

<liferay-ui:header
	title="create-product-version"
/>

<aui:model-context bean="<%= null %>" model="<%= PatcherProductVersion.class %>" />

<portlet:actionURL name="/patcher/add_product_versions" var="addPatcherProductVersionURL" />

<liferay-frontend:edit-form
	action="<%= addPatcherProductVersionURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<liferay-frontend:edit-form-body>
		<aui:input name="name" />

		<aui:select label="fix-delivery-method" name="fixDeliveryMethod">
			<aui:option label="<%= PatcherProductVersionConstants.LABEL_FIX_DELIVERY_METHOD_FIX_PACK_20 %>" value="<%= PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_FIX_PACK_20 %>" />
			<aui:option label="<%= PatcherProductVersionConstants.LABEL_FIX_DELIVERY_METHOD_FIX_PACK_30 %>" value="<%= PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30 %>" />
			<aui:option label="<%= PatcherProductVersionConstants.LABEL_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE %>" value="<%= PatcherProductVersionConstants.TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE %>" />
		</aui:select>

		<aui:input label="module-folder-name" name="moduleFolderName" />
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>