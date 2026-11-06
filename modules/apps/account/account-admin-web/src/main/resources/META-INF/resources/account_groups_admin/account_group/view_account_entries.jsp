<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String backURL = ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL()));

AccountGroupDisplay accountGroupDisplay = (AccountGroupDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_GROUP_DISPLAY);

SearchContainer<AccountEntryDisplay> accountEntryDisplaySearchContainer = AccountEntryDisplaySearchContainerFactory.createWithAccountGroupId(accountGroupDisplay.getAccountGroupId(), liferayPortletRequest, liferayPortletResponse);

ViewAccountGroupAccountEntriesManagementToolbarDisplayContext viewAccountGroupAccountEntriesManagementToolbarDisplayContext = new ViewAccountGroupAccountEntriesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountEntryDisplaySearchContainer);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);

renderResponse.setTitle(accountGroupDisplay.getName());
%>

<portlet:actionURL name="/account_admin/assign_account_group_account_entries" var="assignAccountGroupAccountEntriesURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<portlet:actionURL name="/account_admin/remove_account_group_account_entries" var="removeAccountGroupAccountEntriesURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="accountGroupId" value="<%= String.valueOf(accountGroupDisplay.getAccountGroupId()) %>" />
</portlet:actionURL>

<portlet:renderURL var="selectAccountGroupAccountEntriesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcPath" value="/account_users_admin/select_account_entry.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="accountGroupId" value="<%= String.valueOf(accountGroupDisplay.getAccountGroupId()) %>" />
	<portlet:param name="showCreateButton" value="<%= Boolean.TRUE.toString() %>" />
	<portlet:param name="singleSelect" value="<%= Boolean.FALSE.toString() %>" />
</portlet:renderURL>

<clay:management-toolbar
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"accountGroupName", accountGroupDisplay.getName()
		).put(
			"assignAccountGroupAccountEntriesURL", assignAccountGroupAccountEntriesURL
		).put(
			"removeAccountGroupAccountEntriesURL", removeAccountGroupAccountEntriesURL
		).put(
			"selectAccountGroupAccountEntriesURL", selectAccountGroupAccountEntriesURL
		).build()
	%>'
	managementToolbarDisplayContext="<%= viewAccountGroupAccountEntriesManagementToolbarDisplayContext %>"
	propsTransformer="{AccountGroupAccountEntriesManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="accountEntryIds" type="hidden" />
		<aui:input name="accountGroupId" type="hidden" value="<%= accountGroupDisplay.getAccountGroupId() %>" />

		<liferay-ui:search-container
			searchContainer="<%= accountEntryDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.account.admin.web.internal.display.AccountEntryDisplay"
				keyPropertyName="accountEntryId"
				modelVar="accountEntryDisplay"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-title"
					name="name"
					value="<%= HtmlUtil.escape(accountEntryDisplay.getName()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="organizations"
					value="<%= HtmlUtil.escape(accountEntryDisplay.getOrganizationNames()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="type"
					property="type"
					translate="<%= true %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="status"
				>
					<clay:label
						displayType="<%= accountEntryDisplay.getStatusLabelStyle() %>"
						label="<%= accountEntryDisplay.getStatusLabel() %>"
					/>
				</liferay-ui:search-container-column-text>

				<c:if test="<%= AccountGroupPermission.contains(permissionChecker, accountGroupDisplay.getAccountGroupId(), AccountActionKeys.ASSIGN_ACCOUNTS) %>">
					<liferay-ui:search-container-column-text>
						<portlet:actionURL name="/account_admin/remove_account_group_account_entries" var="removeAccountGroupAccountEntryURL">
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="accountEntryIds" value="<%= String.valueOf(accountEntryDisplay.getAccountEntryId()) %>" />
							<portlet:param name="accountGroupId" value="<%= String.valueOf(accountGroupDisplay.getAccountGroupId()) %>" />
						</portlet:actionURL>

						<liferay-ui:icon-delete
							confirmation="are-you-sure-you-want-to-remove-this-account"
							icon="times-circle"
							message="remove"
							showIcon="<%= true %>"
							url="<%= removeAccountGroupAccountEntryURL %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:if>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>