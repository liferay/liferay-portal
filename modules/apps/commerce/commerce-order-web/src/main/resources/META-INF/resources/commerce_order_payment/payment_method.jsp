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

long commerceOrderId = commerceOrder.getCommerceOrderId();
%>

<portlet:actionURL name="/commerce_order/edit_commerce_order" var="editCommerceOrderPaymentMethodActionURL" />

<aui:form action="<%= editCommerceOrderPaymentMethodActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="paymentMethod" />
	<aui:input name="commerceOrderId" type="hidden" value="<%= commerceOrderId %>" />

	<liferay-ui:error exception="<%= CommerceOrderPaymentMethodException.class %>" message="please-select-a-valid-payment-method" />

	<frontend-data-set:classic-display
		contextParams='<%=
			HashMapBuilder.<String, String>put(
				"commerceOrderId", String.valueOf(commerceOrderId)
			).build()
		%>'
		dataProviderKey="<%= CommerceOrderFDSNames.PAYMENT_METHODS %>"
		defaultSelectedItems="<%= Collections.singletonList(String.valueOf(commerceOrder.getCommercePaymentMethodKey())) %>"
		formName="fm"
		id="<%= CommerceOrderFDSNames.PAYMENT_METHODS %>"
		itemsPerPage="<%= 10 %>"
		selectedItemsKey="paymentMethodKey"
		selectionType="single"
	/>
</aui:form>