<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/asset/init.jsp" %>

<%
CommerceOrder commerceOrder = (CommerceOrder)request.getAttribute(CommerceOrderConstants.COMMERCE_ORDER);
%>

<div class="container-fluid container-fluid-max-xl">
	<div class="h4"><liferay-ui:message key="order-details" /></div>

	<liferay-ui:search-container
		id="commerceOrderItems"
	>
		<liferay-ui:search-container-results
			results="<%= commerceOrder.getCommerceOrderItems() %>"
			resultsVar="commerceOrderItems"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.commerce.model.CommerceOrderItem"
			escapedModel="<%= true %>"
			keyPropertyName="commerceOrderItemId"
			modelVar="commerceOrderItem"
		>
			<liferay-ui:search-container-column-text
				cssClass="font-weight-bold important table-cell-expand"
				property="sku"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
				value="<%= HtmlUtil.escape(commerceOrderItem.getName(locale)) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				property="quantity"
			/>

			<%
			CommerceMoney finalPriceCommerceMoney = commerceOrderItem.getFinalPriceMoney();
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="price"
				value="<%= HtmlUtil.escape(finalPriceCommerceMoney.format(locale)) %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</div>