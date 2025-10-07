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

<clay:navigation-bar
	navigationItems='<%= patcherDisplayContext.getNavigationItems("product-versions") %>'
/>

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
			<clay:dropdown-actions
				aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
				dropdownItems="<%= patcherProductVersionsDisplayContext.getDropdownItems(patcherProductVersion) %>"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>