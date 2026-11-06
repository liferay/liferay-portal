<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceAccountGroupItemSelectorViewDisplayContext commerceAccountGroupItemSelectorViewDisplayContext = (CommerceAccountGroupItemSelectorViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

SearchContainer<AccountGroup> commerceAccountGroupSearchContainer = commerceAccountGroupItemSelectorViewDisplayContext.getSearchContainer();
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new CommerceAccountGroupItemSelectorViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, commerceAccountGroupSearchContainer) %>"
/>

<div class="container-fluid container-fluid-max-xl" id="<portlet:namespace />commerceAccountGroupSelectorWrapper">
	<liferay-ui:search-container
		id="accountGroups"
		searchContainer="<%= commerceAccountGroupSearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.account.model.AccountGroup"
			cssClass="commerce-account-row"
			keyPropertyName="accountGroupId"
			modelVar="accountGroup"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"commerce-account-group-id", accountGroup.getAccountGroupId()
				).put(
					"name", accountGroup.getName()
				).build());
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				property="name"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand"
				name="create-date"
				property="createDate"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
			searchContainer="<%= commerceAccountGroupSearchContainer %>"
		/>
	</liferay-ui:search-container>
</div>