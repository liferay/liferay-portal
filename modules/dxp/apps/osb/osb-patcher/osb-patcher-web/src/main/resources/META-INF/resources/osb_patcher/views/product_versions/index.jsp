<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherProductVersionsDisplayContext patcherProductVersionsDisplayContext = new PatcherProductVersionsDisplayContext(request, renderRequest, renderResponse);
%>

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="product-versions" />
</liferay-util:include>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PatcherProductVersionsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, patcherProductVersionsDisplayContext.getSearchContainer()) %>"
/>

<liferay-ui:search-container
	searchContainer="<%= patcherProductVersionsDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherProductVersion"
		escapedModel="<%= true %>"
		keyProperty="patcherProductVersionId"
		modelVar="patcherProductVersion"
	>
		<liferay-ui:search-container-column-text
			property="name"
		/>

		<liferay-ui:search-container-column-text
			name="fix-delivery-method"
			value="<%= PatcherProductVersionConstants.getTypeLabel(patcherProductVersion.getFixDeliveryMethod()) %>"
		/>

		<liferay-ui:search-container-column-text
			name="module-folder-name"
			property="moduleFolderName"
		/>

		<liferay-ui:search-container-column-text
			align="right"
		>
			<liferay-ui:icon-menu>
				<c:if test="<%= PatcherPermission.contains(themeDisplay, patcherProductVersion, PatcherActionKeys.EDIT, patcherProductVersion.getUserId()) %>">
					<portlet:renderURL var="editPatcherProductVersionURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/edit_product_versions" />
						<portlet:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProductVersion.getPatcherProductVersionId()) %>" />
					</portlet:renderURL>

					<liferay-ui:icon
						image="edit"
						method="get"
						url="<%= editPatcherProductVersionURL %>"
					/>
				</c:if>
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>