<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceOrderEditDisplayContext commerceOrderEditDisplayContext = (CommerceOrderEditDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceOrder commerceOrder = commerceOrderEditDisplayContext.getCommerceOrder();
%>

<portlet:actionURL name="/commerce_order/edit_commerce_order" var="editCommerceOrderShippingAddressActionURL" />

<div class="container-fluid container-fluid-max-xl p-4">
	<aui:form action="<%= editCommerceOrderShippingAddressActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="selectShippingAddress" />
		<aui:input name="commerceOrderId" type="hidden" value="<%= commerceOrder.getCommerceOrderId() %>" />

		<frontend-data-set:classic-display
			contextParams='<%=
				HashMapBuilder.<String, String>put(
					"commerceOrderId", String.valueOf(commerceOrder.getCommerceOrderId())
				).build()
			%>'
			creationMenu='<%= commerceOrderEditDisplayContext.getCommerceAddressCreationMenu("/commerce_order/edit_commerce_order_shipping_address") %>'
			dataProviderKey="<%= CommerceOrderFDSNames.SHIPPING_ADDRESSES %>"
			defaultSelectedItems="<%= Collections.singletonList(Math.toIntExact(commerceOrder.getShippingAddressId())) %>"
			formName="fm"
			id="<%= CommerceOrderFDSNames.SHIPPING_ADDRESSES %>"
			itemsPerPage="<%= 10 %>"
			selectedItemsKey="addressId"
			selectionType="single"
		/>
	</aui:form>
</div>