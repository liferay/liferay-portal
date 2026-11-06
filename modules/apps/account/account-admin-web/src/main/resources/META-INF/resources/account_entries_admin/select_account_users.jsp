<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SelectAccountUsersDisplayContext selectAccountUsersDisplayContext = new SelectAccountUsersDisplayContext(liferayPortletRequest, liferayPortletResponse);

SearchContainer<AccountUserDisplay> userSearchContainer = AssignableAccountUserDisplaySearchContainerFactory.create(selectAccountUsersDisplayContext.getAccountEntryId(), liferayPortletRequest, liferayPortletResponse, selectAccountUsersDisplayContext.getRowChecker());

SelectAccountUsersManagementToolbarDisplayContext selectAccountUsersManagementToolbarDisplayContext = new SelectAccountUsersManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, userSearchContainer, selectAccountUsersDisplayContext);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= selectAccountUsersManagementToolbarDisplayContext %>"
	propsTransformer="{SelectAccountUsersManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "selectAccountUser" %>'
>
	<c:if test='<%= Objects.equals(selectAccountUsersManagementToolbarDisplayContext.getNavigation(), "valid-domain-users") %>'>
		<clay:alert
			message="showing-users-with-valid-domains-only"
		/>
	</c:if>

	<liferay-ui:search-container
		searchContainer="<%= userSearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.account.admin.web.internal.display.AccountUserDisplay"
			keyPropertyName="userId"
			modelVar="accountUserDisplay"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="name"
				value="<%= HtmlUtil.escape(accountUserDisplay.getName()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="email-address"
				property="emailAddress"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="job-title"
				value="<%= HtmlUtil.escape(accountUserDisplay.getJobTitle()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="account-roles"
				value="<%= HtmlUtil.escape(accountUserDisplay.getAccountRoleNamesString(selectAccountUsersDisplayContext.getAccountEntryId(), locale)) %>"
			/>

			<c:if test="<%= selectAccountUsersDisplayContext.isSingleSelect() %>">
				<liferay-ui:search-container-column-text>
					<aui:button
						cssClass="choose-user selector-button"
						data='<%=
							HashMapBuilder.<String, Object>put(
								"emailaddress", accountUserDisplay.getEmailAddress()
							).put(
								"entityid", accountUserDisplay.getUserId()
							).put(
								"entityname", accountUserDisplay.getName()
							).put(
								"jobtitle", accountUserDisplay.getJobTitle()
							).build()
						%>'
						value="choose"
					/>
				</liferay-ui:search-container-column-text>
			</c:if>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>