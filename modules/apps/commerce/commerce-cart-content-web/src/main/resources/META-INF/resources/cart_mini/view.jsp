<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceCartContentMiniDisplayContext commerceCartContentMiniDisplayContext = (CommerceCartContentMiniDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

Map<String, Object> contextObjects = HashMapBuilder.<String, Object>put(
	"commerceCartContentMiniDisplayContext", commerceCartContentMiniDisplayContext
).build();

CommerceMoney subtotalCommerceMoney = null;
CommerceDiscountValue subtotalCommerceDiscountValue = null;
CommerceMoney taxValueCommerceMoney = null;
CommerceDiscountValue totalCommerceDiscountValue = null;
CommerceMoney totalOrderCommerceMoney = null;

String priceDisplayType = commerceCartContentMiniDisplayContext.getCommercePriceDisplayType();

CommerceOrderPrice commerceOrderPrice = commerceCartContentMiniDisplayContext.getCommerceOrderPrice();

if (commerceOrderPrice != null) {
	subtotalCommerceMoney = commerceOrderPrice.getSubtotal();
	subtotalCommerceDiscountValue = commerceOrderPrice.getSubtotalDiscountValue();
	taxValueCommerceMoney = commerceOrderPrice.getTaxValue();

	totalCommerceDiscountValue = commerceOrderPrice.getTotalDiscountValue();
	totalOrderCommerceMoney = commerceOrderPrice.getTotal();

	if (priceDisplayType.equals(CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {
		subtotalCommerceMoney = commerceOrderPrice.getSubtotalWithTaxAmount();
		subtotalCommerceDiscountValue = commerceOrderPrice.getSubtotalDiscountValueWithTaxAmount();
		totalCommerceDiscountValue = commerceOrderPrice.getTotalDiscountValueWithTaxAmount();
		totalOrderCommerceMoney = commerceOrderPrice.getTotalWithTaxAmount();
	}
}

SearchContainer<CommerceOrderItem> commerceOrderItemSearchContainer = commerceCartContentMiniDisplayContext.getSearchContainer();

PortletURL portletURL = PortletURLBuilder.create(
	commerceCartContentMiniDisplayContext.getPortletURL()
).setParameter(
	"searchContainerId", "commerceOrderItems"
).buildPortletURL();

request.setAttribute("view.jsp-portletURL", portletURL);
%>

<liferay-ddm:template-renderer
	className="<%= CommerceCartContentMiniPortlet.class.getName() %>"
	contextObjects="<%= contextObjects %>"
	displayStyle="<%= commerceCartContentMiniDisplayContext.getDisplayStyle() %>"
	displayStyleGroupId="<%= commerceCartContentMiniDisplayContext.getDisplayStyleGroupId() %>"
	entries="<%= commerceOrderItemSearchContainer.getResults() %>"
>
	<ul class="commerce-order-items-header">
		<li class="autofit-row">
			<div class="autofit-col autofit-col-expand">
				<div class="commerce-title h4">
					<liferay-ui:message arguments="<%= commerceCartContentMiniDisplayContext.getCommerceOrderItemsQuantity() %>" key="items-x" translateArguments="<%= false %>" />
				</div>
			</div>

			<div class="autofit-col">
				<liferay-commerce:order-transitions
					commerceOrderId="<%= commerceCartContentMiniDisplayContext.getCommerceOrderId() %>"
					cssClass="btn commerce-btn"
				/>
			</div>

			<c:if test="<%= commerceCartContentMiniDisplayContext.hasPermission(ActionKeys.VIEW) %>">
				<div class="autofit-col">
					<div><a class="btn commerce-btn" href="<%= commerceCartContentMiniDisplayContext.getCommerceCartPortletURL() %>"><liferay-ui:message key="edit-cart" /></a></div>
				</div>
			</c:if>
		</li>
	</ul>

	<div class="commerce-order-items-body" id="<portlet:namespace />entriesContainer">
		<liferay-ui:search-container
			cssClass="list-group-flush"
			id="commerceOrderItems"
			iteratorURL="<%= portletURL %>"
			searchContainer="<%= commerceOrderItemSearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.commerce.model.CommerceOrderItem"
				keyProperty="commerceOrderItemId"
				modelVar="commerceOrderItem"
			>

				<%
				CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

				String cpInstanceCDNURL = commerceCartContentMiniDisplayContext.getCPInstanceCDNURL(commerceOrderItem);
				%>

				<liferay-ui:search-container-column-text
					cssClass="col-1 thumbnail-section"
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
										fileVersion="<%= commerceCartContentMiniDisplayContext.getCPInstanceImageFileVersion(commerceOrderItem) %>"
									/>
								</c:otherwise>
							</c:choose>
						</span>
					</span>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="autofit-col-expand"
				>
					<div class="description-section">
						<div class="list-group-title">
							<a href="<%= commerceCartContentMiniDisplayContext.getCPDefinitionURL(cpDefinition.getCPDefinitionId(), themeDisplay) %>">
								<%= HtmlUtil.escape(cpDefinition.getName(languageId)) %>
							</a>
						</div>

						<%
						StringJoiner stringJoiner = new StringJoiner(StringPool.COMMA);

						for (KeyValuePair keyValuePair : commerceCartContentMiniDisplayContext.getKeyValuePairs(commerceOrderItem.getCPDefinitionId(), commerceOrderItem.getJson(), locale)) {
							stringJoiner.add(keyValuePair.getValue());
						}
						%>

						<div class="list-group-subtitle"><liferay-ui:message arguments="<%= HtmlUtil.escape(commerceOrderItem.getSku()) %>" key="sku-x" translateArguments="<%= false %>" /></div>
						<div class="list-group-subtitle"><%= HtmlUtil.escape(stringJoiner.toString()) %></div>

						<%
						CPInstance cpInstance = commerceOrderItem.fetchCPInstance();
						%>

						<c:if test="<%= (cpInstance != null) && Validator.isNotNull(cpInstance.getCPSubscriptionInfo()) %>">
							<div class="list-group-subtitle">
								<commerce-ui:product-subscription-info
									CPInstanceId="<%= commerceOrderItem.getCPInstanceId() %>"
									showDuration="<%= false %>"
								/>
							</div>
						</c:if>
					</div>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text>
					<div class="quantity-section">
						<span class="commerce-quantity"><%= commerceOrderItem.getQuantity() %></span><span class="inline-item-after">x</span>
					</div>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					name="price"
				>
					<c:if test="<%= commerceCartContentMiniDisplayContext.hasViewPricePermission() %>">

						<%
						CommerceMoney unitPriceCommerceMoney = commerceCartContentMiniDisplayContext.getUnitPriceCommerceMoney(commerceOrderItem);
						CommerceMoney unitPromoPriceCommerceMoney = commerceCartContentMiniDisplayContext.getUnitPromoPriceCommerceMoney(commerceOrderItem);
						%>

						<c:choose>
							<c:when test="<%= commerceCartContentMiniDisplayContext.isUnitPromoPriceActive(commerceOrderItem) %>">
								<c:choose>
									<c:when test="<%= unitPromoPriceCommerceMoney.isPriceOnApplication() %>">
										<liferay-ui:message key="<%= CommercePriceConstants.PRICE_VALUE_PRICE_ON_APPLICATION %>" />
									</c:when>
									<c:otherwise>
										<%= HtmlUtil.escape(unitPromoPriceCommerceMoney.format(locale)) %>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="<%= unitPriceCommerceMoney.isPriceOnApplication() %>">
										<liferay-ui:message key="<%= CommercePriceConstants.PRICE_VALUE_PRICE_ON_APPLICATION %>" />
									</c:when>
									<c:otherwise>
										<%= HtmlUtil.escape(unitPriceCommerceMoney.format(locale)) %>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:if>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="descriptive"
				markupView="lexicon"
				paginate="<%= false %>"
				searchContainer="<%= commerceOrderItemSearchContainer %>"
			/>
		</liferay-ui:search-container>

		<c:if test="<%= commerceCartContentMiniDisplayContext.getCommerceOrderItemsQuantity() > commerceOrderItemSearchContainer.getDelta() %>">
			<ul class="commerce-order-items-header">
				<li class="autofit-row">
					<c:if test="<%= commerceCartContentMiniDisplayContext.hasPermission(ActionKeys.VIEW) %>">
						<a class="btn btn-link commerce-link" href="<%= commerceCartContentMiniDisplayContext.getCommerceCartPortletURL() %>"><liferay-ui:message key="view-more" /></a>
					</c:if>
				</li>
			</ul>
		</c:if>
	</div>

	<ul class="commerce-order-items-footer">
		<li class="autofit-row commerce-tax">
			<c:if test="<%= subtotalCommerceMoney != null %>">
				<div class="autofit-col autofit-col-expand">
					<div class="commerce-description"><liferay-ui:message key="subtotal" /></div>
				</div>

				<div class="autofit-col">
					<div class="commerce-value"><%= HtmlUtil.escape(subtotalCommerceMoney.format(locale)) %></div>
				</div>
			</c:if>

			<c:if test="<%= subtotalCommerceDiscountValue != null %>">

				<%
				CommerceMoney subtotalDiscountAmountCommerceMoney = subtotalCommerceDiscountValue.getDiscountAmount();
				%>

				<div class="autofit-col autofit-col-expand">
					<div class="commerce-description"><liferay-ui:message key="subtotal-discount" /></div>
				</div>

				<div class="commerce-value">
					<%= HtmlUtil.escape(subtotalDiscountAmountCommerceMoney.format(locale)) %>
				</div>

				<div class="commerce-value pl-1">
					(<%= HtmlUtil.escape(commerceCartContentMiniDisplayContext.getLocalizedPercentage(subtotalCommerceDiscountValue.getDiscountPercentage(), locale)) %>)
				</div>
			</c:if>
		</li>
		<li class="autofit-row commerce-tax">
			<c:if test="<%= (taxValueCommerceMoney != null) && priceDisplayType.equals(CommercePricingConstants.TAX_EXCLUDED_FROM_PRICE) %>">
				<div class="autofit-col autofit-col-expand">
					<div class="commerce-description"><liferay-ui:message key="tax" /></div>
				</div>

				<div class="autofit-col">
					<div class="commerce-value"><%= HtmlUtil.escape(taxValueCommerceMoney.format(locale)) %></div>
				</div>
			</c:if>
		</li>
		<li class="autofit-row commerce-total">
			<c:if test="<%= totalCommerceDiscountValue != null %>">

				<%
				CommerceMoney totalDiscountAmountCommerceMoney = totalCommerceDiscountValue.getDiscountAmount();
				%>

				<div class="autofit-col autofit-col-expand">
					<div class="commerce-description"><liferay-ui:message key="total-discount" /></div>
				</div>

				<div class="commerce-value">
					<%= HtmlUtil.escape(totalDiscountAmountCommerceMoney.format(locale)) %>
				</div>

				<div class="commerce-value pl-1">
					(<%= HtmlUtil.escape(commerceCartContentMiniDisplayContext.getLocalizedPercentage(totalCommerceDiscountValue.getDiscountPercentage(), locale)) %>)
				</div>
			</c:if>

			<c:if test="<%= totalOrderCommerceMoney != null %>">
				<div class="autofit-col autofit-col-expand">
					<div class="commerce-description"><liferay-ui:message key="total" /></div>
				</div>

				<div class="autofit-col">
					<div class="commerce-value"><%= HtmlUtil.escape(totalOrderCommerceMoney.format(locale)) %></div>
				</div>
			</c:if>
		</li>
	</ul>

	<%@ include file="/common/transition.jspf" %>
</liferay-ddm:template-renderer>

<liferay-frontend:component
	module="{cartMiniView} from commerce-cart-content-web"
/>