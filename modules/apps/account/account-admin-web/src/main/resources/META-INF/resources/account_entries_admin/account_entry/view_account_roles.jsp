<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_DISPLAY);

SearchContainer<AccountRoleDisplay> accountRoleDisplaySearchContainer = AccountRoleDisplaySearchContainerFactory.create(accountEntryDisplay.getAccountEntryId(), liferayPortletRequest, liferayPortletResponse);

ViewAccountRolesManagementToolbarDisplayContext viewAccountRolesManagementToolbarDisplayContext = new ViewAccountRolesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountRoleDisplaySearchContainer);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));

renderResponse.setTitle(accountEntryDisplay.getName());
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= viewAccountRolesManagementToolbarDisplayContext %>"
	propsTransformer="{AccountRolesManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="accountRoleIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= accountRoleDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.account.admin.web.internal.display.AccountRoleDisplay"
				keyProperty="accountRoleId"
				modelVar="accountRoleDisplay"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(viewAccountRolesManagementToolbarDisplayContext.getAvailableActions(accountRoleDisplay))
					).build());
				%>

				<portlet:renderURL var="rowURL">
					<portlet:param name="mvcPath" value="/account_entries_admin/edit_account_role.jsp" />
					<portlet:param name="backURL" value="<%= currentURL %>" />
					<portlet:param name="accountEntryId" value="<%= String.valueOf(accountEntryDisplay.getAccountEntryId()) %>" />
					<portlet:param name="accountRoleId" value="<%= String.valueOf(accountRoleDisplay.getAccountRoleId()) %>" />
				</portlet:renderURL>

				<%
				if (!accountRoleDisplay.isShowRowURL(permissionChecker)) {
					rowURL = null;
				}
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="name"
					value="<%= HtmlUtil.escape(accountRoleDisplay.getName(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="description"
					value="<%= HtmlUtil.escape(accountRoleDisplay.getDescription(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="type"
					value="<%= accountRoleDisplay.getTypeLabel(locale) %>"
				/>

				<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-small table-cell-minw-150"
						name="status"
					>

						<%
						Role role = accountRoleDisplay.getRole();
						%>

						<clay:label
							displayType="<%= WorkflowConstants.getStatusStyle(role.getStatus()) %>"
							label="<%= WorkflowConstants.getStatusLabel(role.getStatus()) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:if>

				<liferay-ui:search-container-column-jsp
					path="/account_entries_admin/account_role_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>