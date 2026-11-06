<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SearchContainer<AccountRoleDisplay> accountRoleDisplaySearchContainer = AccountRoleDisplaySearchContainerFactory.create(ParamUtil.getLong(request, "accountEntryId"), liferayPortletRequest, liferayPortletResponse);

accountRoleDisplaySearchContainer.setRowChecker(new AccountEntryUserAccountRoleRowChecker(ParamUtil.getLong(liferayPortletRequest, "accountEntryId"), liferayPortletResponse, ParamUtil.getLong(liferayPortletRequest, "accountUserIds")));
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new ViewAccountUserRolesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountRoleDisplaySearchContainer) %>"
/>

<clay:container-fluid>
	<liferay-ui:search-container
		searchContainer="<%= accountRoleDisplaySearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.account.admin.web.internal.display.AccountRoleDisplay"
			keyPropertyName="accountRoleId"
			modelVar="accountRole"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="name"
				value="<%= HtmlUtil.escape(accountRole.getName(locale)) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="description"
				value="<%= HtmlUtil.escape(accountRole.getDescription(locale)) %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>