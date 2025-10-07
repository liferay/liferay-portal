<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceOrder commerceOrder = commerceOrderContentDisplayContext.getCommerceOrder();
%>

<portlet:actionURL name="/commerce_open_order_content/edit_commerce_order" var="editCommerceOrderBillingAddressActionURL" />

<commerce-ui:modal-content
	contentCssClasses="p-0"
	title='<%= LanguageUtil.get(request, "billing-address") %>'
>
	<aui:form action="<%= editCommerceOrderBillingAddressActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="selectBillingAddress" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="commerceOrderId" type="hidden" value="<%= commerceOrder.getCommerceOrderId() %>" />

		<frontend-data-set:classic-display
			contextParams='<%=
				HashMapBuilder.<String, String>put(
					"commerceOrderId", String.valueOf(commerceOrder.getCommerceOrderId())
				).build()
			%>'
			creationMenu='<%= commerceOrderContentDisplayContext.getCommerceAddressCreationMenu("/commerce_open_order_content/edit_commerce_order_billing_address") %>'
			dataProviderKey="<%= CommerceOrderFDSNames.BILLING_ADDRESSES %>"
			defaultSelectedItems="<%= Collections.singletonList(String.valueOf(commerceOrder.getBillingAddressId())) %>"
			formName="fm"
			id="<%= CommerceOrderFDSNames.BILLING_ADDRESSES %>"
			itemsPerPage="<%= 10 %>"
			selectedItemsKey="addressId"
			selectionType="single"
		/>
	</aui:form>
</commerce-ui:modal-content>