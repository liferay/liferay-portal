<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SearchContainer<AccountGroupDisplay> accountGroupDisplaySearchContainer = AccountGroupDisplaySearchContainerFactory.create(liferayPortletRequest, liferayPortletResponse);

ViewAccountGroupsManagementToolbarDisplayContext viewAccountGroupsManagementToolbarDisplayContext = new ViewAccountGroupsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountGroupDisplaySearchContainer);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= viewAccountGroupsManagementToolbarDisplayContext %>"
	propsTransformer="{AccountGroupsManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="accountGroupIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= accountGroupDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.account.admin.web.internal.display.AccountGroupDisplay"
				keyProperty="accountGroupId"
				modelVar="accountGroupDisplay"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(viewAccountGroupsManagementToolbarDisplayContext.getAvailableActions(accountGroupDisplay))
					).build());
				%>

				<portlet:renderURL var="rowURL">
					<portlet:param name="mvcRenderCommandName" value="/account_admin/edit_account_group" />
					<portlet:param name="backURL" value="<%= currentURL %>" />
					<portlet:param name="accountGroupId" value="<%= String.valueOf(accountGroupDisplay.getAccountGroupId()) %>" />
					<portlet:param name="screenNavigationCategoryKey" value="<%= AccountScreenNavigationEntryConstants.CATEGORY_KEY_ACCOUNTS %>" />
				</portlet:renderURL>

				<%
				if (!AccountGroupPermission.contains(permissionChecker, accountGroupDisplay.getAccountGroupId(), AccountActionKeys.VIEW_ACCOUNTS)) {
					rowURL = null;
				}
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-title"
					href="<%= rowURL %>"
					name="name"
					value="<%= HtmlUtil.escape(accountGroupDisplay.getName()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					href="<%= rowURL %>"
					name="description"
					value="<%= HtmlUtil.escape(accountGroupDisplay.getDescription()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					href="<%= rowURL %>"
					name="accounts"
					value="<%= String.valueOf(accountGroupDisplay.getAccountEntriesCount()) %>"
				/>

				<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="status"
					>
						<clay:label
							displayType="<%= WorkflowConstants.getStatusStyle(accountGroupDisplay.getStatus()) %>"
							label="<%= WorkflowConstants.getStatusLabel(accountGroupDisplay.getStatus()) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:if>

				<liferay-ui:search-container-column-jsp
					path="/account_groups_admin/account_group_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>