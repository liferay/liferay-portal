<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceOrder commerceOrder = commerceOrderContentDisplayContext.getCommerceOrder();

CommerceAddress billingCommerceAddress = commerceOrder.getBillingAddress();
CommerceAddress shippingCommerceAddress = commerceOrder.getShippingAddress();

CommerceOrderPrice commerceOrderPrice = commerceOrderContentDisplayContext.getCommerceOrderPrice();

CommerceMoney shippingValueCommerceMoney = commerceOrderPrice.getShippingValue();
CommerceDiscountValue shippingCommerceDiscountValue = commerceOrderPrice.getShippingDiscountValue();
CommerceMoney subtotalCommerceMoney = commerceOrderPrice.getSubtotal();
CommerceDiscountValue subtotalCommerceDiscountValue = commerceOrderPrice.getSubtotalDiscountValue();
CommerceMoney taxValueCommerceMoney = commerceOrderPrice.getTaxValue();
CommerceDiscountValue totalCommerceDiscountValue = commerceOrderPrice.getTotalDiscountValue();
CommerceMoney totalOrderCommerceMoney = commerceOrderPrice.getTotal();

String priceDisplayType = commerceOrderContentDisplayContext.getCommercePriceDisplayType();

if (priceDisplayType.equals(CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {
	shippingValueCommerceMoney = commerceOrderPrice.getShippingValueWithTaxAmount();
	shippingCommerceDiscountValue = commerceOrderPrice.getShippingDiscountValueWithTaxAmount();
	subtotalCommerceMoney = commerceOrderPrice.getSubtotalWithTaxAmount();
	subtotalCommerceDiscountValue = commerceOrderPrice.getSubtotalDiscountValueWithTaxAmount();
	totalCommerceDiscountValue = commerceOrderPrice.getTotalDiscountValueWithTaxAmount();
	totalOrderCommerceMoney = commerceOrderPrice.getTotalWithTaxAmount();
}

AccountEntry accountEntry = commerceOrderContentDisplayContext.getAccountEntry();

if (commerceOrder != null) {
	accountEntry = commerceOrder.getAccountEntry();
}
%>

<liferay-ui:error exception="<%= CommerceOrderValidatorException.class %>">

	<%
	CommerceOrderValidatorException commerceOrderValidatorException = (CommerceOrderValidatorException)errorException;
	%>

	<c:if test="<%= commerceOrderValidatorException != null %>">

		<%
		for (CommerceOrderValidatorResult commerceOrderValidatorResult : commerceOrderValidatorException.getCommerceOrderValidatorResults()) {
		%>

			<liferay-ui:message key="<%= HtmlUtil.escape(commerceOrderValidatorResult.getLocalizedMessage()) %>" />

		<%
		}
		%>

	</c:if>
</liferay-ui:error>

<div class="commerce-panel">
	<div class="commerce-panel__content">
		<div class="align-items-center row">
			<div class="col-md-3">
				<div class="autofit-col-expand commerce-order-title">
					<%= HtmlUtil.escape(accountEntry.getName()) %>
				</div>
			</div>

			<div class="col-md-3">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="total" /></dt>
					<dd><%= HtmlUtil.escape(totalOrderCommerceMoney.format(locale)) %></dd>
				</dl>
			</div>

			<div class="col-md-3">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="notes" /></dt>
					<dd>

						<%
						request.setAttribute("order_notes.jsp-showLabel", Boolean.TRUE);
						request.setAttribute("order_notes.jsp-taglibLinkCssClass", "link-outline link-outline-borderless link-outline-secondary lfr-icon-item-reverse");
						%>

						<liferay-util:include page="/placed_commerce_orders/order_notes.jsp" servletContext="<%= application %>" />
					</dd>
				</dl>
			</div>
		</div>
	</div>

	<div class="commerce-panel__content">
		<div class="align-items-center row">
			<div class="col-md-3">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="account-id" /></dt>
					<dd><%= accountEntry.getAccountEntryId() %></dd>
				</dl>
			</div>

			<div class="col-md-3">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="order-id" /></dt>
					<dd><%= commerceOrder.getCommerceOrderId() %></dd>
				</dl>
			</div>

			<div class="col-md-3">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="order-type" /></dt>
					<dd><%= commerceOrderContentDisplayContext.getCommerceOrderTypeName(languageId) %></dd>
				</dl>
			</div>

			<div class="col-md-3">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="order-date" /></dt>
					<dd>
						<%= commerceOrderContentDisplayContext.getCommerceOrderDate(commerceOrder) %>

						<c:if test="<%= commerceOrderContentDisplayContext.isShowCommerceOrderCreateTime() %>">
							<%= commerceOrderContentDisplayContext.getCommerceOrderTime(commerceOrder) %>
						</c:if>
					</dd>
				</dl>
			</div>
		</div>
	</div>

	<div class="commerce-panel__content">
		<div class="align-items-center row">
			<div class="col-md-3">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="erc" /></dt>
					<dd><%= HtmlUtil.escape(commerceOrder.getExternalReferenceCode()) %></dd>
				</dl>
			</div>

			<div class="col-md-3">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="order-status" /></dt>
					<dd><%= commerceOrderContentDisplayContext.getCommerceOrderStatusLabel(commerceOrder) %></dd>
				</dl>
			</div>
		</div>
	</div>
</div>

<c:choose>
	<c:when test="<%= commerceOrderContentDisplayContext.isShowPurchaseOrderNumber() %>">
		<div class="row">
			<div class="col-md-6">
				<div class="commerce-panel">
					<div class="commerce-panel__title"><liferay-ui:message key="name" /></div>
					<div class="commerce-panel__content">
						<dl class="commerce-list">
							<%= HtmlUtil.escape(commerceOrder.getName()) %>
						</dl>
					</div>
				</div>
			</div>

			<div class="col-md-6">
				<div class="commerce-panel">
					<div class="commerce-panel__title"><liferay-ui:message key="purchase-order-number" /></div>
					<div class="commerce-panel__content">
						<dl class="commerce-list">
							<%= HtmlUtil.escape(commerceOrder.getPurchaseOrderNumber()) %>
						</dl>
					</div>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="row">
			<div class="col-md-12">
				<div class="commerce-panel">
					<div class="commerce-panel__title"><liferay-ui:message key="name" /></div>
					<div class="commerce-panel__content">
						<dl class="commerce-list">
							<%= HtmlUtil.escape(commerceOrder.getName()) %>
						</dl>
					</div>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<div class="row">
	<div class="col-md-6">
		<div class="commerce-panel">
			<div class="commerce-panel__title"><liferay-ui:message key="billing-address" /></div>
			<div class="commerce-panel__content" data-qa-id="commerceBillingAddress">
				<c:if test="<%= commerceOrderContentDisplayContext.hasViewBillingAddressPermission(permissionChecker, accountEntry) && (billingCommerceAddress != null) %>">
					<p><%= HtmlUtil.escape(billingCommerceAddress.getName()) %></p>

					<c:if test="<%= !Validator.isBlank(billingCommerceAddress.getSubtype()) %>">
						<p><%= HtmlUtil.escape(billingCommerceAddress.getSubtype(locale)) %></p>
					</c:if>

					<p><%= HtmlUtil.escape(billingCommerceAddress.getStreet1()) %></p>

					<c:if test="<%= !Validator.isBlank(billingCommerceAddress.getStreet2()) %>">
						<p><%= HtmlUtil.escape(billingCommerceAddress.getStreet2()) %></p>
					</c:if>

					<c:if test="<%= !Validator.isBlank(billingCommerceAddress.getStreet3()) %>">
						<p><%= HtmlUtil.escape(billingCommerceAddress.getStreet3()) %></p>
					</c:if>

					<p><%= HtmlUtil.escape(billingCommerceAddress.getCity() + StringPool.SPACE + billingCommerceAddress.getZip()) %></p>

					<c:if test="<%= commerceOrderContentDisplayContext.isShowCommerceOrderFullAddress() %>">
						<p>

							<%
							Region region = billingCommerceAddress.getRegion();
							%>

							<c:if test="<%= region != null %>">
								<%= HtmlUtil.escape(region.getTitle() + StringPool.SPACE) %>
							</c:if>

							<%
							Country country = billingCommerceAddress.getCountry();
							%>

							<%= HtmlUtil.escape(country.getName(locale)) %>
						</p>
					</c:if>

					<c:if test="<%= commerceOrderContentDisplayContext.isShowCommerceOrderPhoneNumber() %>">
						<p><%= HtmlUtil.escape(billingCommerceAddress.getPhoneNumber()) %></p>
					</c:if>
				</c:if>
			</div>
		</div>
	</div>

	<div class="col-md-6">
		<div class="commerce-panel">
			<div class="commerce-panel__title"><liferay-ui:message key="shipping-address" /></div>
			<div class="commerce-panel__content" data-qa-id="commerceShippingAddress">
				<c:if test="<%= shippingCommerceAddress != null %>">
					<p><%= HtmlUtil.escape(shippingCommerceAddress.getName()) %></p>

					<c:if test="<%= !Validator.isBlank(shippingCommerceAddress.getSubtype()) %>">
						<p><%= HtmlUtil.escape(shippingCommerceAddress.getSubtype(locale)) %></p>
					</c:if>

					<p><%= HtmlUtil.escape(shippingCommerceAddress.getStreet1()) %></p>

					<c:if test="<%= !Validator.isBlank(shippingCommerceAddress.getStreet2()) %>">
						<p><%= HtmlUtil.escape(shippingCommerceAddress.getStreet2()) %></p>
					</c:if>

					<c:if test="<%= !Validator.isBlank(shippingCommerceAddress.getStreet3()) %>">
						<p><%= HtmlUtil.escape(shippingCommerceAddress.getStreet3()) %></p>
					</c:if>

					<p><%= HtmlUtil.escape(shippingCommerceAddress.getCity() + StringPool.SPACE + shippingCommerceAddress.getZip()) %></p>

					<c:if test="<%= commerceOrderContentDisplayContext.isShowCommerceOrderFullAddress() %>">
						<p>

							<%
							Region region = shippingCommerceAddress.getRegion();
							%>

							<c:if test="<%= region != null %>">
								<%= HtmlUtil.escape(region.getTitle() + StringPool.SPACE) %>
							</c:if>

							<%
							Country country = shippingCommerceAddress.getCountry();
							%>

							<%= HtmlUtil.escape(country.getName(locale)) %>
						</p>
					</c:if>

					<c:if test="<%= commerceOrderContentDisplayContext.isShowCommerceOrderPhoneNumber() %>">
						<p><%= HtmlUtil.escape(shippingCommerceAddress.getPhoneNumber()) %></p>
					</c:if>
				</c:if>
			</div>
		</div>
	</div>

	<div class="col-md-6">
		<div class="commerce-panel">
			<div class="commerce-panel__title"><liferay-ui:message key="delivery-terms" /></div>
			<div class="commerce-panel__content">
				<p>
					<c:if test="<%= commerceOrder.getDeliveryCommerceTermEntryId() != 0 %>">
						<a href="#" id="<%= commerceOrder.getDeliveryCommerceTermEntryId() %>"><%= HtmlUtil.escape(commerceOrder.getDeliveryCommerceTermEntryName()) %></a>

						<liferay-frontend:component
							context='<%=
								HashMapBuilder.<String, Object>put(
									"HTMLElementId", commerceOrder.getDeliveryCommerceTermEntryId()
								).put(
									"modalContent", commerceOrder.getDeliveryCommerceTermEntryDescription()
								).put(
									"modalTitle", commerceOrder.getDeliveryCommerceTermEntryName()
								).build()
							%>'
							module="{attachModalToHTMLElement} from commerce-order-content-web"
						/>
					</c:if>
				</p>
			</div>
		</div>
	</div>

	<div class="col-md-6">
		<div class="commerce-panel">
			<div class="commerce-panel__title"><liferay-ui:message key="payment-terms" /></div>
			<div class="commerce-panel__content">
				<p>
					<c:if test="<%= commerceOrder.getPaymentCommerceTermEntryId() != 0 %>">
						<a href="#" id="<%= commerceOrder.getPaymentCommerceTermEntryId() %>"><%= HtmlUtil.escape(commerceOrder.getPaymentCommerceTermEntryName()) %></a>

						<liferay-frontend:component
							context='<%=
								HashMapBuilder.<String, Object>put(
									"HTMLElementId", commerceOrder.getPaymentCommerceTermEntryId()
								).put(
									"modalContent", commerceOrder.getPaymentCommerceTermEntryDescription()
								).put(
									"modalTitle", commerceOrder.getPaymentCommerceTermEntryName()
								).build()
							%>'
							module="{attachModalToHTMLElement} from commerce-order-content-web"
						/>
					</c:if>
				</p>
			</div>
		</div>
	</div>
</div>

<div class="commerce-cta is-visible">
	<portlet:actionURL name="/commerce_open_order_content/edit_commerce_order" var="editCommerceOrderActionURL">
		<portlet:param name="mvcRenderCommandName" value="/commerce_order_content/view_commerce_order_details" />
	</portlet:actionURL>

	<aui:form action="<%= editCommerceOrderActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="commerceOrderId" type="hidden" value="<%= String.valueOf(commerceOrder.getCommerceOrderId()) %>" />
	</aui:form>

	<c:if test="<%= commerceOrderContentDisplayContext.isShowProcessQuote() %>">
		<aui:button cssClass="btn-lg" onClick='<%= liferayPortletResponse.getNamespace() + "handleCTA(null, 'processQuote', null);" %>' value="process-quote" />
	</c:if>

	<c:if test='<%= FeatureFlagManagerUtil.isEnabled(commerceOrder.getCompanyId(), "LPD-10562") && (commerceOrder.getOrderStatus() == CommerceOrderConstants.ORDER_STATUS_COMPLETED) %>'>
		<aui:button cssClass="btn-lg" onClick='<%= liferayPortletResponse.getNamespace() + "handleCTA(null, 'makeReturn', null);" %>' value="make-a-return" />
	</c:if>

	<aui:button cssClass="btn-lg" onClick='<%= liferayPortletResponse.getNamespace() + "handleCTA(null, 'reorder', null);" %>' value="reorder" />

	<c:if test="<%= commerceOrderContentDisplayContext.isShowRetryPayment() %>">
		<aui:button cssClass="btn-lg" href="<%= commerceOrderContentDisplayContext.getRetryPaymentURL() %>" primary="<%= true %>" value="retry-payment" />
	</c:if>

	<liferay-util:dynamic-include key="com.liferay.commerce.order.content.web#/place_order_detail_cta#" />
</div>

<div class="row">
	<div class="col-md-12">

		<%
		java.util.Map<String, String> contextParams = new java.util.HashMap<>();

		contextParams.put("commerceOrderId", String.valueOf(commerceOrder.getCommerceOrderId()));
		%>

		<frontend-data-set:classic-display
			contextParams="<%= contextParams %>"
			dataProviderKey="<%= CommerceOrderFDSNames.PLACED_ORDER_ITEMS %>"
			id="<%= CommerceOrderFDSNames.PLACED_ORDER_ITEMS %>"
			itemsPerPage="<%= 10 %>"
			nestedItemsKey="orderItemId"
			nestedItemsReferenceKey="orderItems"
			propsTransformer="{PlacedOrderItemClassicFDSPropsTransformer} from commerce-order-content-web"
			style="stacked"
		/>
	</div>
</div>

<div class="row">
	<div class="col-md-9">
	</div>

	<liferay-ui:icon-menu
		direction="right"
		icon="<%= StringPool.BLANK %>"
		markupView="lexicon"
		message="<%= StringPool.BLANK %>"
		showWhenSingleIcon="<%= true %>"
		triggerCssClass="btn btn-lg btn-monospaced btn-primary position-fixed thumb-menu"
	>
		<liferay-ui:icon
			icon="print"
			message="print"
			target="_blank"
			url="<%= commerceOrderContentDisplayContext.getExportCommerceOrderReportURL() %>"
		/>
	</liferay-ui:icon-menu>

	<div class="col-md-3">
		<div class="commerce-panel">
			<div class="commerce-panel__content">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="subtotal" /></dt>
					<dd class="text-right"><%= HtmlUtil.escape(subtotalCommerceMoney.format(locale)) %></dd>

					<c:if test="<%= (subtotalCommerceDiscountValue != null) && (BigDecimal.ZERO.compareTo(subtotalCommerceDiscountValue.getDiscountPercentage()) < 0) %>">
						<dt><liferay-ui:message key="subtotal-discount" /></dt>

						<%
						CommerceMoney subtotalDiscountAmountCommerceMoney = subtotalCommerceDiscountValue.getDiscountAmount();
						%>

						<dd class="text-right"><%= HtmlUtil.escape(subtotalDiscountAmountCommerceMoney.format(locale)) %></dd>
						<dt></dt>
						<dd class="text-right"><%= HtmlUtil.escape(commerceOrderContentDisplayContext.getLocalizedPercentage(subtotalCommerceDiscountValue.getDiscountPercentage(), locale)) %></dd>
					</c:if>

					<dt><liferay-ui:message key="delivery" /></dt>
					<dd class="text-right"><%= HtmlUtil.escape(shippingValueCommerceMoney.format(locale)) %></dd>

					<c:if test="<%= (shippingCommerceDiscountValue != null) && (BigDecimal.ZERO.compareTo(shippingCommerceDiscountValue.getDiscountPercentage()) < 0) %>">
						<dt><liferay-ui:message key="delivery-discount" /></dt>

						<%
						CommerceMoney shippingDiscountAmountCommerceMoney = shippingCommerceDiscountValue.getDiscountAmount();
						%>

						<dd class="text-right"><%= HtmlUtil.escape(shippingDiscountAmountCommerceMoney.format(locale)) %></dd>
						<dt></dt>

						<%
						CommerceDiscountValue shippingDiscountValueWithTaxAmount = commerceOrderPrice.getShippingDiscountValueWithTaxAmount();
						%>

						<dd class="text-right"><%= HtmlUtil.escape(commerceOrderContentDisplayContext.getLocalizedPercentage(shippingDiscountValueWithTaxAmount.getDiscountPercentage(), locale)) %></dd>
					</c:if>

					<c:if test="<%= priceDisplayType.equals(CommercePricingConstants.TAX_EXCLUDED_FROM_PRICE) %>">
						<dt><liferay-ui:message key="tax" /></dt>
						<dd class="text-right"><%= HtmlUtil.escape(taxValueCommerceMoney.format(locale)) %></dd>
					</c:if>

					<c:if test="<%= (totalCommerceDiscountValue != null) && (BigDecimal.ZERO.compareTo(totalCommerceDiscountValue.getDiscountPercentage()) < 0) %>">
						<dt><liferay-ui:message key="total-discount" /></dt>

						<%
						CommerceMoney totalDiscountAmountCommerceMoney = totalCommerceDiscountValue.getDiscountAmount();
						%>

						<dd class="text-right"><%= HtmlUtil.escape(totalDiscountAmountCommerceMoney.format(locale)) %></dd>
						<dt></dt>
						<dd class="text-right"><%= HtmlUtil.escape(commerceOrderContentDisplayContext.getLocalizedPercentage(totalCommerceDiscountValue.getDiscountPercentage(), locale)) %></dd>
					</c:if>
				</dl>
			</div>

			<div class="commerce-panel__content">
				<dl class="commerce-list">
					<dt><liferay-ui:message key="total" /></dt>
					<dd class="text-right"><%= HtmlUtil.escape(totalOrderCommerceMoney.format(locale)) %></dd>
				</dl>
			</div>
		</div>
	</div>
</div>

<aui:script>
	function <portlet:namespace />viewCommerceOrderShipments(uri) {
		Liferay.Util.openModal({
			containerProps: {},
			id: 'viewCommerceOrderShipmentsDialog',
			iframeBodyCssClass: 'dialog',
			title: '',
			url: uri,
		});
	}
</aui:script>

<portlet:renderURL var="viewReturnableOrderItemsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/commerce_order_content/view_returnable_commerce_order_items" />
	<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrder.getCommerceOrderId()) %>" />
</portlet:renderURL>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"namespace", liferayPortletResponse.getNamespace()
		).put(
			"returnableOrderItemsContextParams", commerceOrderContentDisplayContext.getReturnableOrderItemsContextParams()
		).put(
			"viewReturnableOrderItemsURL", viewReturnableOrderItemsURL
		).build()
	%>'
	module="{viewCommerceOrderDetailsCTAs} from commerce-order-content-web"
/>