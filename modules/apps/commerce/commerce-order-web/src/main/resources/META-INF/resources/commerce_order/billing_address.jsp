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

<portlet:actionURL name="/commerce_order/edit_commerce_order" var="editCommerceOrderBillingAddressActionURL" />

<div class="container-fluid container-fluid-max-xl p-4">
	<aui:form action="<%= editCommerceOrderBillingAddressActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="selectBillingAddress" />
		<aui:input name="commerceOrderId" type="hidden" value="<%= commerceOrder.getCommerceOrderId() %>" />

		<frontend-data-set:classic-display
			contextParams='<%=
				HashMapBuilder.<String, String>put(
					"commerceOrderId", String.valueOf(commerceOrder.getCommerceOrderId())
				).build()
			%>'
			creationMenu='<%= commerceOrderEditDisplayContext.getCommerceAddressCreationMenu("/commerce_order/edit_commerce_order_billing_address") %>'
			dataProviderKey="<%= CommerceOrderFDSNames.BILLING_ADDRESSES %>"
			defaultSelectedItems="<%= Collections.singletonList(Math.toIntExact(commerceOrder.getBillingAddressId())) %>"
			formName="fm"
			id="<%= CommerceOrderFDSNames.BILLING_ADDRESSES %>"
			itemsPerPage="<%= 10 %>"
			selectedItemsKey="addressId"
			selectionType="single"
		/>
	</aui:form>
</div>