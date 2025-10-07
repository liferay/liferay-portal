<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long patcherProductVersionId = ParamUtil.getLong(request, "patcherProductVersionId");

PatcherProductVersion patcherProductVersion = PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(patcherProductVersionId);
%>

<liferay-ui:header
	title='<%= LanguageUtil.format(request, "edit-x", patcherProductVersion.getName()) %>'
/>

<aui:model-context bean="<%= patcherProductVersion %>" model="<%= PatcherProductVersion.class %>" />

<portlet:actionURL name="/patcher/update_product_versions" var="updatePatcherProductVersionURL" />

<liferay-frontend:edit-form
	action="<%= updatePatcherProductVersionURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="patcherProductVersionId" type="hidden" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

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