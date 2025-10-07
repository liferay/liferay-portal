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

<portlet:actionURL name="/commerce_open_order_content/edit_commerce_order" var="editCommerceOrderShippingAddressActionURL" />

<commerce-ui:modal-content
	contentCssClasses="p-0"
	title='<%= LanguageUtil.get(request, "shipping-address") %>'
>
	<aui:form action="<%= editCommerceOrderShippingAddressActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="selectShippingAddress" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="commerceOrderId" type="hidden" value="<%= commerceOrder.getCommerceOrderId() %>" />

		<frontend-data-set:classic-display
			contextParams='<%=
				HashMapBuilder.<String, String>put(
					"commerceOrderId", String.valueOf(commerceOrder.getCommerceOrderId())
				).build()
			%>'
			creationMenu='<%= commerceOrderContentDisplayContext.getCommerceAddressCreationMenu("/commerce_open_order_content/edit_commerce_order_shipping_address") %>'
			dataProviderKey="<%= CommerceOrderFDSNames.SHIPPING_ADDRESSES %>"
			defaultSelectedItems="<%= Collections.singletonList(String.valueOf(commerceOrder.getShippingAddressId())) %>"
			formName="fm"
			id="<%= CommerceOrderFDSNames.SHIPPING_ADDRESSES %>"
			itemsPerPage="<%= 10 %>"
			selectedItemsKey="addressId"
			selectionType="single"
		/>
	</aui:form>
</commerce-ui:modal-content>