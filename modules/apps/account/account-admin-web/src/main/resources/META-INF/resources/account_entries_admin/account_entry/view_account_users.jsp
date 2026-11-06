<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_DISPLAY);

SearchContainer<AccountUserDisplay> accountUserDisplaySearchContainer = AccountUserDisplaySearchContainerFactory.create(accountEntryDisplay.getAccountEntryId(), liferayPortletRequest, liferayPortletResponse);

ViewAccountUsersManagementToolbarDisplayContext viewAccountUsersManagementToolbarDisplayContext = new ViewAccountUsersManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountUserDisplaySearchContainer);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));

renderResponse.setTitle(accountEntryDisplay.getName());
%>

<clay:management-toolbar
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"accountEntryName", accountEntryDisplay.getName()
		).build()
	%>'
	managementToolbarDisplayContext="<%= viewAccountUsersManagementToolbarDisplayContext %>"
	propsTransformer="{AccountUsersManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="accountEntryId" type="hidden" value="<%= accountEntryDisplay.getAccountEntryId() %>" />
		<aui:input name="accountUserIds" type="hidden" />

		<liferay-ui:error exception="<%= UserEmailAddressException.MustHaveValidDomain.class %>" message="one-or-more-of-the-selected-users-have-invalid-email-address-domains" />

		<liferay-ui:search-container
			searchContainer="<%= accountUserDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.account.admin.web.internal.display.AccountUserDisplay"
				keyPropertyName="userId"
				modelVar="accountUser"
			>

				<%
				String rowURL = null;

				AccountUserActionDropdownItemsProvider accountUserActionDropdownItemsProvider = new AccountUserActionDropdownItemsProvider(accountEntryDisplay, accountUser, permissionChecker, renderRequest, renderResponse);

				if (AccountUserPermission.hasEditUserPermission(permissionChecker, portletName, accountEntryDisplay, accountUser.getUser())) {
					rowURL = accountUserActionDropdownItemsProvider.getEditAccountUserURL();
				}
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="name"
					value="<%= HtmlUtil.escape(accountUser.getName()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="email-address"
					property="emailAddress"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="job-title"
					value="<%= HtmlUtil.escape(accountUser.getJobTitle()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="account-roles"
					value="<%= HtmlUtil.escape(accountUser.getAccountRoleNamesString(accountEntryDisplay.getAccountEntryId(), locale)) %>"
				/>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= accountUserActionDropdownItemsProvider.getActionDropdownItems() %>"
						propsTransformer="{AccountUserDropdownDefaultPropsTransformer} from account-admin-web"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>