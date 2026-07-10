<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

CommerceShipmentContentDisplayContext commerceShipmentContentDisplayContext = (CommerceShipmentContentDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceShipment commerceShipment = commerceShipmentContentDisplayContext.getCommerceShipment();

CommerceAddress commerceAddress = commerceShipment.fetchCommerceAddress();

String street1 = StringPool.BLANK;
String street2 = StringPool.BLANK;
String street3 = StringPool.BLANK;
String city = StringPool.BLANK;
String zip = StringPool.BLANK;
String phoneNumber = StringPool.BLANK;
String regionCode = StringPool.BLANK;

if (commerceAddress != null) {
	street1 = commerceAddress.getStreet1();
	street2 = commerceAddress.getStreet2();
	street3 = commerceAddress.getStreet3();
	city = commerceAddress.getCity();
	zip = commerceAddress.getZip();
	phoneNumber = commerceAddress.getPhoneNumber();

	Region region = commerceAddress.getRegion();

	if (region != null) {
		regionCode = region.getRegionCode();
	}
}
%>

<portlet:actionURL name="/commerce_shipment/edit_commerce_shipment" var="editCommerceShipmentActionURL">
	<portlet:param name="mvcRenderCommandName" value="/commerce_shipment/edit_commerce_shipment" />
</portlet:actionURL>

<div class="b2b-portlet-content-header">
	<liferay-ui:icon
		cssClass="header-back-to"
		icon="order-arrow-down"
		id="TabsBack"
		label="<%= false %>"
		markupView="lexicon"
		message='<%= LanguageUtil.get(resourceBundle, "back") %>'
		method="get"
		url="<%= layout.getRegularURL(request) %>"
	/>

	<div class="autofit-float autofit-row header-title-bar">
		<div class="autofit-col autofit-col-expand">
			<liferay-ui:header
				backURL="<%= redirect %>"
				localizeTitle="<%= false %>"
				showBackURL="<%= false %>"
				title='<%= LanguageUtil.format(request, "shipment-number-x", commerceShipment.getCommerceShipmentId()) %>'
			/>
		</div>
	</div>
</div>

<div class="autofit-float autofit-row shipment-details-header">
	<div class="autofit-col autofit-col-expand">
		<div class="autofit-section">
			<h3 class="shipment-details-title"><liferay-ui:message key="shipping-date" /></h3>

			<div class="shipment-date shipment-details-subtitle">
				<%= commerceShipmentContentDisplayContext.getCommerceShipmentShippingDate(commerceShipment) %>
			</div>

			<div class="shipment-time">
				<%= commerceShipmentContentDisplayContext.getCommerceShipmentShippingTime(commerceShipment) %>
			</div>
		</div>
	</div>

	<div class="autofit-col autofit-col-expand">
		<div class="autofit-section">
			<h3 class="shipment-details-title"><liferay-ui:message key="customer" /></h3>

			<div class="customer-name shipment-details-subtitle">
				<%= HtmlUtil.escape(commerceShipment.getAccountEntryName()) %>
			</div>

			<div class="customer-id">
				<%= commerceShipment.getCommerceAccountId() %>
			</div>
		</div>
	</div>

	<div class="autofit-col autofit-col-expand">
		<div class="autofit-section">
			<h3 class="shipment-details-title"><liferay-ui:message key="delivery" /></h3>

			<div class="shipment-address shipment-details-subtitle">
				<c:if test="<%= Validator.isNotNull(street2) %>">
					<p><%= HtmlUtil.escape(street1) %></p>
				</c:if>

				<c:if test="<%= Validator.isNotNull(street2) %>">
					<p><%= HtmlUtil.escape(street2) %></p>
				</c:if>

				<c:if test="<%= Validator.isNotNull(street3) %>">
					<p><%= HtmlUtil.escape(street3) %></p>
				</c:if>

				<p><%= city + StringPool.COMMA_AND_SPACE + regionCode + StringPool.SPACE + zip %></p>

				<c:if test="<%= Validator.isNotNull(phoneNumber) %>">
					<p><%= HtmlUtil.escape(phoneNumber) %></p>
				</c:if>
			</div>
		</div>
	</div>

	<div class="autofit-col autofit-col-expand">
		<div class="autofit-section">
			<h3 class="shipment-details-title"><liferay-ui:message key="status" /></h3>

			<div class="shipment-details-subtitle shipment-status">
				<%= commerceShipmentContentDisplayContext.getCommerceShipmentStatusLabel(commerceShipment.getStatus()) %>
			</div>
		</div>
	</div>

	<div class="autofit-col autofit-col-expand">
		<div class="autofit-section">
			<h3 class="shipment-details-title"><liferay-ui:message key="expected-date" /></h3>

			<div class="shipment-date shipment-details-subtitle">
				<%= commerceShipmentContentDisplayContext.getCommerceShipmentExpectedDate(commerceShipment) %>
			</div>

			<div class="shipment-time">
				<%= commerceShipmentContentDisplayContext.getCommerceShipmentExpectedTime(commerceShipment) %>
			</div>
		</div>
	</div>
</div>

<div class="autofit-float autofit-row shipment-details-header">
	<div class="autofit-col autofit-col-expand">
		<div class="autofit-section">
			<h3 class="shipment-details-title"><liferay-ui:message key="carrier" /></h3>

			<c:if test="<%= Validator.isNotNull(commerceShipment.getCarrier()) %>">
				<div class="shipment-carrier shipment-details-subtitle">
					<%= HtmlUtil.escape(commerceShipment.getCarrier()) %>
				</div>
			</c:if>
		</div>
	</div>

	<div class="autofit-col autofit-col-expand">
		<div class="autofit-section">
			<h3 class="shipment-details-title"><liferay-ui:message key="method" /></h3>

			<%
			String shippingMethodName = commerceShipmentContentDisplayContext.getCommerceShipmentShippingMethodName(commerceShipment);
			String shippingOptionName = commerceShipmentContentDisplayContext.getCommerceOrderShippingOptionName(commerceShipment);
			%>

			<c:if test="<%= Validator.isNotNull(shippingMethodName) %>">
				<div class="shipment-details-subtitle shipment-method-name">
					<%= HtmlUtil.escape(shippingMethodName) %>
				</div>
			</c:if>

			<c:if test="<%= Validator.isNotNull(shippingOptionName) %>">
				<div class="shipment-details-subtitle shipment-option-name">
					<%= HtmlUtil.escape(shippingOptionName) %>
				</div>
			</c:if>
		</div>
	</div>

	<div class="autofit-col autofit-col-expand">
		<div class="autofit-section">
			<h3 class="shipment-details-title"><liferay-ui:message key="tracking-number" /></h3>

			<c:if test="<%= Validator.isNotNull(commerceShipment.getTrackingNumber()) %>">
				<div class="shipment-details-subtitle shipment-tracking-number">
					<%= HtmlUtil.escape(commerceShipment.getTrackingNumber()) %>
				</div>
			</c:if>
		</div>
	</div>
</div>

<liferay-portlet:actionURL name="/commerce_open_order_content/edit_commerce_order_item" var="editCommerceOrderItemURL" />

<liferay-ui:search-container
	cssClass="shipment-details-table"
	searchContainer="<%= commerceShipmentContentDisplayContext.getCommerceShipmentItemsSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.commerce.model.CommerceShipmentItem"
		escapedModel="<%= true %>"
		keyProperty="commerceShipmentItemId"
		modelVar="commerceShipmentItem"
	>

		<%
		CommerceOrderItem commerceOrderItem = commerceShipmentItem.fetchCommerceOrderItem();
		%>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="sku"
			value="<%= (commerceOrderItem == null) ? StringPool.BLANK : HtmlUtil.escape(commerceOrderItem.getSku()) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="name"
			value="<%= (commerceOrderItem == null) ? StringPool.BLANK : HtmlUtil.escape(commerceOrderItem.getName(languageId)) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			property="quantity"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
		>

			<%
			String viewCommerceOrderDetailsURL = commerceShipmentContentDisplayContext.getViewCommerceOrderDetailsURL(commerceShipmentItem);
			%>

			<c:if test="<%= Validator.isNotNull(viewCommerceOrderDetailsURL) %>">
				<a href="<%= viewCommerceOrderDetailsURL %>">
					<liferay-ui:message key="view-order-details" />
				</a>
			</c:if>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>