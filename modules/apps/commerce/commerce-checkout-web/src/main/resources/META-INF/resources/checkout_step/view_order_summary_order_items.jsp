<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
OrderSummaryCheckoutStepDisplayContext orderSummaryCheckoutStepDisplayContext = (OrderSummaryCheckoutStepDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceOrder commerceOrder = orderSummaryCheckoutStepDisplayContext.getCommerceOrder();

String priceDisplayType = orderSummaryCheckoutStepDisplayContext.getCommercePriceDisplayType();

Map<Long, List<CommerceOrderValidatorResult>> commerceOrderValidatorResultsMap = orderSummaryCheckoutStepDisplayContext.getCommerceOrderValidatorResultsMap();
%>

<div class="commerce-checkout-summary-body" id="<portlet:namespace />entriesContainer">
	<liferay-ui:search-container
		cssClass="list-group-flush"
		id="commerceOrderItems"
	>
		<liferay-ui:search-container-results
			results="<%= commerceOrder.getCommerceOrderItems() %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.commerce.model.CommerceOrderItem"
			keyProperty="CommerceOrderItemId"
			modelVar="commerceOrderItem"
		>

			<%
			String cpInstanceCDNURL = orderSummaryCheckoutStepDisplayContext.getCPInstanceCDNURL(commerceOrderItem);
			%>

			<liferay-ui:search-container-column-text
				cssClass="thumbnail-section"
				name="image"
			>
				<span class="sticker sticker-xl">
					<span class="sticker-overlay">
						<c:choose>
							<c:when test="<%= Validator.isNotNull(cpInstanceCDNURL) %>">
								<img alt="thumbnail" class="sticker-img" src="<%= cpInstanceCDNURL %>" />
							</c:when>
							<c:otherwise>
								<liferay-adaptive-media:img
									alt="thumbnail"
									class="sticker-img"
									fileVersion="<%= orderSummaryCheckoutStepDisplayContext.getCPInstanceImageFileVersion(commerceOrderItem) %>"
								/>
							</c:otherwise>
						</c:choose>
					</span>
				</span>
			</liferay-ui:search-container-column-text>

			<%
			CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();
			%>

			<liferay-ui:search-container-column-text
				cssClass="autofit-col-expand"
				name="product"
			>
				<div class="description-section">
					<div class="list-group-title">
						<%= HtmlUtil.escape(cpDefinition.getName(themeDisplay.getLanguageId())) %>
					</div>

					<%
					StringJoiner stringJoiner = new StringJoiner(StringPool.COMMA);

					for (KeyValuePair keyValuePair : orderSummaryCheckoutStepDisplayContext.getKeyValuePairs(commerceOrderItem.getCPDefinitionId(), commerceOrderItem.getJson(), locale)) {
						String keyValuePairValue = keyValuePair.getValue();

						if (keyValuePairValue.contains("fileEntryId")) {
							stringJoiner.add(orderSummaryCheckoutStepDisplayContext.getJSONOptionValue(keyValuePairValue, "title"));
						}
						else {
							stringJoiner.add(keyValuePair.getValue());
						}
					}
					%>

					<div class="list-group-subtitle"><%= HtmlUtil.escape(stringJoiner.toString()) %></div>

					<c:if test="<%= !commerceOrderValidatorResultsMap.isEmpty() %>">

						<%
						List<CommerceOrderValidatorResult> commerceOrderValidatorResults = commerceOrderValidatorResultsMap.get(commerceOrderItem.getCommerceOrderItemId());

						for (CommerceOrderValidatorResult commerceOrderValidatorResult : commerceOrderValidatorResults) {
						%>

							<div class="alert-danger commerce-alert-danger">
								<liferay-ui:message key="<%= HtmlUtil.escape(commerceOrderValidatorResult.getLocalizedMessage()) %>" />
							</div>

						<%
						}
						%>

					</c:if>
				</div>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="quantity"
			>
				<div class="quantity-section">
					<span class="commerce-quantity"><%= orderSummaryCheckoutStepDisplayContext.getCommerceOrderItemFormattedQuantity(commerceOrderItem) %></span><span class="inline-item-after">x</span>
				</div>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="uom"
			>
				<div class="value-section">
					<span class="commerce-value">
						<%= HtmlUtil.escape(commerceOrderItem.getUnitOfMeasureKey()) %>
					</span>
				</div>
			</liferay-ui:search-container-column-text>

			<%
			CommerceProductPrice commerceProductPrice = orderSummaryCheckoutStepDisplayContext.getCommerceProductPrice(commerceOrderItem);
			CPInstance cpInstance = commerceOrderItem.fetchCPInstance();
			%>

			<liferay-ui:search-container-column-text
				name="price"
			>
				<c:if test="<%= commerceProductPrice != null %>">

					<%
					CommerceMoney unitPriceCommerceMoney = commerceProductPrice.getUnitPrice();
					CommerceMoney unitPromoPriceCommerceMoney = commerceProductPrice.getUnitPromoPrice();

					if (priceDisplayType.equals(CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {
						unitPriceCommerceMoney = commerceProductPrice.getUnitPriceWithTaxAmount();
						unitPromoPriceCommerceMoney = commerceProductPrice.getUnitPromoPriceWithTaxAmount();
					}
					%>

					<div class="value-section">
						<span class="price">
							<c:choose>
								<c:when test="<%= !unitPromoPriceCommerceMoney.isEmpty() && BigDecimalUtil.gt(unitPromoPriceCommerceMoney.getPrice(), BigDecimal.ZERO) && BigDecimalUtil.lt(unitPromoPriceCommerceMoney.getPrice(), unitPriceCommerceMoney.getPrice()) %>">
									<span class="price-value price-value-promo">
										<%= HtmlUtil.escape(unitPromoPriceCommerceMoney.format(locale)) %>
									</span>
									<span class="price-value price-value-inactive">
										<%= HtmlUtil.escape(unitPriceCommerceMoney.format(locale)) %>
									</span>
								</c:when>
								<c:otherwise>
									<span class="price-value {$additionalPriceClasses}">
										<%= HtmlUtil.escape(unitPriceCommerceMoney.format(locale)) %>
									</span>
								</c:otherwise>
							</c:choose>
						</span>

						<c:if test="<%= (cpInstance != null) && Validator.isNotNull(cpInstance.getCPSubscriptionInfo()) %>">
							<span class="commerce-subscription-info">
								<commerce-ui:product-subscription-info
									CPInstanceId="<%= commerceOrderItem.getCPInstanceId() %>"
									showDuration="<%= false %>"
								/>
							</span>
						</c:if>
					</div>
				</c:if>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="discount"
			>
				<c:if test="<%= commerceProductPrice != null %>">

					<%
					CommerceDiscountValue discountValue = commerceProductPrice.getDiscountValue();

					if (priceDisplayType.equals(CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {
						discountValue = commerceProductPrice.getDiscountValueWithTaxAmount();
					}

					CommerceMoney discountAmountCommerceMoney = null;

					if (discountValue != null) {
						discountAmountCommerceMoney = discountValue.getDiscountAmount();
					}
					%>

					<div class="value-section">
						<span class="commerce-value">
							<%= (discountAmountCommerceMoney == null) ? StringPool.BLANK : HtmlUtil.escape(discountAmountCommerceMoney.format(locale)) %>
						</span>
					</div>
				</c:if>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="total"
			>
				<c:if test="<%= commerceProductPrice != null %>">

					<%
					CommerceMoney finalPriceCommerceMoney = commerceProductPrice.getFinalPrice();

					if (priceDisplayType.equals(CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {
						finalPriceCommerceMoney = commerceProductPrice.getFinalPriceWithTaxAmount();
					}
					%>

					<div class="value-section">
						<span class="commerce-value">
							<%= HtmlUtil.escape(finalPriceCommerceMoney.format(locale)) %>
						</span>
					</div>
				</c:if>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="list"
			markupView="lexicon"
			paginate="<%= false %>"
		/>
	</liferay-ui:search-container>
</div>