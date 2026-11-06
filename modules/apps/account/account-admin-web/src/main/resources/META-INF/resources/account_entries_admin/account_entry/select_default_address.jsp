<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = AccountEntryDisplayFactoryUtil.create(ParamUtil.getLong(request, "accountEntryId"), request);

long defaultAddressId = 0;

String type = ParamUtil.getString(request, "type");

if (Objects.equals(type, "billing")) {
	defaultAddressId = accountEntryDisplay.getDefaultBillingAddressId();
}
else if (Objects.equals(type, "shipping")) {
	defaultAddressId = accountEntryDisplay.getDefaultShippingAddressId();
}

SearchContainer<AddressDisplay> accountEntryAddressDisplaySearchContainer = AccountEntryAddressDisplaySearchContainerFactory.create(liferayPortletRequest, liferayPortletResponse);

accountEntryAddressDisplaySearchContainer.setRowChecker(null);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SelectAccountEntryAddressManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, accountEntryAddressDisplaySearchContainer) %>"
	propsTransformer="{SelectAccountDefaultAddressManagementToolbarPropsTransformer} from account-admin-web"
/>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "selectDefaultAddress" %>'
>
	<liferay-ui:search-container
		searchContainer="<%= accountEntryAddressDisplaySearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.account.admin.web.internal.display.AddressDisplay"
			keyPropertyName="addressId"
			modelVar="addressDisplay"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="name"
				value="<%= addressDisplay.getName() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="street"
				value="<%= addressDisplay.getStreet() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="city"
				value="<%= addressDisplay.getCity() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="region"
				value="<%= addressDisplay.getRegionName() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="postal-code"
				value="<%= addressDisplay.getZip() %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-small table-cell-minw-150"
				name="type"
				value="<%= addressDisplay.getType(themeDisplay.getLocale()) %>"
			/>

			<liferay-ui:search-container-column-text>
				<clay:radio
					checked="<%= addressDisplay.getAddressId() == defaultAddressId %>"
					cssClass="selector-button"
					data-entityid="<%= addressDisplay.getAddressId() %>"
					label="<%= addressDisplay.getName() %>"
					name="selectAddress"
					showLabel="<%= false %>"
					value="<%= String.valueOf(addressDisplay.getAddressId()) %>"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>