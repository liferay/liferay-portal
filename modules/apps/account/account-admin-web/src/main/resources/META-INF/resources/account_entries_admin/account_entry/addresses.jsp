<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_DISPLAY);

SearchContainer<AddressDisplay> accountEntryAddressDisplaySearchContainer = AccountEntryAddressDisplaySearchContainerFactory.create(liferayPortletRequest, liferayPortletResponse);

ViewAccountEntryAddressesManagementToolbarDisplayContext viewAccountEntryAddressesManagementToolbarDisplayContext = new ViewAccountEntryAddressesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountEntryAddressDisplaySearchContainer);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));

renderResponse.setTitle(accountEntryDisplay.getName());
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= viewAccountEntryAddressesManagementToolbarDisplayContext %>"
	propsTransformer="{AccountEntryAddressesManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid>
	<aui:form method="post" name="fm">
		<aui:input name="accountEntryAddressIds" type="hidden" />

		<liferay-ui:search-container
			searchContainer="<%= accountEntryAddressDisplaySearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.account.admin.web.internal.display.AddressDisplay"
				keyProperty="addressId"
				modelVar="addressDisplay"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(viewAccountEntryAddressesManagementToolbarDisplayContext.getAvailableActions(accountEntryDisplay))
					).build());
				%>

				<portlet:renderURL var="rowURL">
					<portlet:param name="mvcRenderCommandName" value="/account_admin/edit_account_entry_address" />
					<portlet:param name="backURL" value="<%= currentURL %>" />
					<portlet:param name="accountEntryAddressId" value="<%= String.valueOf(addressDisplay.getAddressId()) %>" />
					<portlet:param name="accountEntryId" value="<%= String.valueOf(accountEntryDisplay.getAccountEntryId()) %>" />
				</portlet:renderURL>

				<%
				if (!AccountEntryPermission.contains(permissionChecker, accountEntryDisplay.getAccountEntryId(), AccountActionKeys.MANAGE_ADDRESSES)) {
					rowURL = null;
				}
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="name"
					value="<%= addressDisplay.getName() %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="street"
					value="<%= addressDisplay.getStreet() %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="city"
					value="<%= addressDisplay.getCity() %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="region"
					value="<%= addressDisplay.getRegionName() %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="postal-code"
					value="<%= addressDisplay.getZip() %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="type"
					value="<%= addressDisplay.getType(themeDisplay.getLocale()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-small table-cell-minw-150"
					href="<%= rowURL %>"
					name="subtype"
					value="<%= addressDisplay.getSubtype() %>"
				/>

				<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-35914") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-small table-cell-minw-150"
						name="status"
					>
						<clay:label
							displayType="<%= WorkflowConstants.getStatusStyle(addressDisplay.getStatus()) %>"
							label="<%= WorkflowConstants.getStatusLabel(addressDisplay.getStatus()) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:if>

				<liferay-ui:search-container-column-jsp
					path="/account_entries_admin/account_entry_address_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>