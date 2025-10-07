<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_DISPLAY);

SearchContainer<AccountGroupDisplay> accountGroupDisplaySearchContainer = AccountEntryAccountGroupSearchContainerFactory.create(liferayPortletRequest, liferayPortletResponse);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));

renderResponse.setTitle(accountEntryDisplay.getName());
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new AccountEntryAccountGroupManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountGroupDisplaySearchContainer) %>"
/>

<clay:container-fluid>
	<liferay-ui:search-container
		headerNames="name,description"
		id="accountEntryAccountGroupsSearchContainer"
		searchContainer="<%= accountGroupDisplaySearchContainer %>"
		total="<%= accountGroupDisplaySearchContainer.getTotal() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.account.admin.web.internal.display.AccountGroupDisplay"
			keyProperty="accountGroupId"
			modelVar="accountGroupDisplay"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-title"
				name="name"
				value="<%= HtmlUtil.escape(accountGroupDisplay.getName()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="description"
				value="<%= HtmlUtil.escape(accountGroupDisplay.getDescription()) %>"
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
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>