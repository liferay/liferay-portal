<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long accountGroupId = ParamUtil.getLong(request, "accountGroupId");

boolean filterManageableAccountEntries = true;

LinkedHashMap<String, Object> params = new LinkedHashMap<>();

if (accountGroupId == 0) {
	params.put("allowNewUserMembership", Boolean.TRUE);
}
else if (AccountGroupPermission.contains(permissionChecker, accountGroupId, AccountActionKeys.ASSIGN_ACCOUNTS)) {
	filterManageableAccountEntries = false;
}

SearchContainer<AccountEntryDisplay> accountEntryDisplaySearchContainer = AccountEntryDisplaySearchContainerFactory.createWithParams(liferayPortletRequest, liferayPortletResponse, params, filterManageableAccountEntries);

if (accountGroupId > 0) {
	accountEntryDisplaySearchContainer.setRowChecker(new AccountGroupAccountEntryRowChecker(accountGroupId, liferayPortletResponse));
}
else if (ParamUtil.getLong(request, "userId") > 0) {
	accountEntryDisplaySearchContainer.setRowChecker(new UserAccountEntryRowChecker(liferayPortletResponse, ParamUtil.getLong(request, "userId")));
}

SelectAccountEntryManagementToolbarDisplayContext selectAccountEntryManagementToolbarDisplayContext = new SelectAccountEntryManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountEntryDisplaySearchContainer);

if (selectAccountEntryManagementToolbarDisplayContext.isSingleSelect()) {
	accountEntryDisplaySearchContainer.setRowChecker(null);
}
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= selectAccountEntryManagementToolbarDisplayContext %>"
/>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "selectAccountEntry" %>'
>
	<liferay-ui:search-container
		searchContainer="<%= accountEntryDisplaySearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.account.admin.web.internal.display.AccountEntryDisplay"
			keyProperty="accountEntryId"
			modelVar="accountEntryDisplay"
		>

			<%
			Map<String, Object> data = HashMapBuilder.<String, Object>put(
				"accountentryid", accountEntryDisplay.getAccountEntryId()
			).put(
				"entityid", accountEntryDisplay.getAccountEntryId()
			).put(
				"entityname", accountEntryDisplay.getName()
			).build();

			row.setData(data);

			String cssClass = "table-cell-expand";
			%>

			<liferay-ui:search-container-column-text
				cssClass='<%= cssClass + " table-title" %>'
				name="name"
				value="<%= HtmlUtil.escape(accountEntryDisplay.getName()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="<%= cssClass %>"
				name="type"
				translate="<%= true %>"
				value="<%= HtmlUtil.escape(accountEntryDisplay.getType()) %>"
			/>

			<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest"
					name="status"
				>
					<clay:label
						displayType="<%= accountEntryDisplay.getStatusLabelStyle() %>"
						label="<%= accountEntryDisplay.getStatusLabel() %>"
					/>
				</liferay-ui:search-container-column-text>
			</c:if>

			<c:if test="<%= selectAccountEntryManagementToolbarDisplayContext.isSingleSelect() %>">
				<liferay-ui:search-container-column-text>
					<aui:button cssClass="choose-account selector-button" data="<%= data %>" value="choose" />
				</liferay-ui:search-container-column-text>
			</c:if>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>