<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long accountEntryId = ParamUtil.getLong(request, "accountEntryId");

long accountRoleId = ParamUtil.getLong(request, "accountRoleId");

AccountRole accountRole = AccountRoleLocalServiceUtil.fetchAccountRole(accountRoleId);

Role role = null;

if (accountRole != null) {
	role = accountRole.getRole();
}

SearchContainer<AccountUserDisplay> accountRoleUserDisplaySearchContainer = AccountUserDisplaySearchContainerFactory.create(accountEntryId, role.getRoleId(), liferayPortletRequest, liferayPortletResponse);
%>

<clay:management-toolbar
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"accountEntryName", role.getTitle(locale)
		).build()
	%>'
	managementToolbarDisplayContext="<%= new ViewAccountRoleAssigneesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountRoleUserDisplaySearchContainer) %>"
	propsTransformer="{AccountUsersManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="accountEntryId" type="hidden" value="<%= accountEntryId %>" />
		<aui:input name="accountRoleId" type="hidden" value="<%= accountRoleId %>" />
		<aui:input name="accountUserIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= accountRoleUserDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.account.admin.web.internal.display.AccountUserDisplay"
				keyPropertyName="userId"
				modelVar="accountUser"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="name"
					value="<%= HtmlUtil.escape(accountUser.getName()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="email-address"
					property="emailAddress"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					name="job-title"
					value="<%= HtmlUtil.escape(accountUser.getJobTitle()) %>"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>