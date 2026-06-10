<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CheckoutDisplayContext checkoutDisplayContext = (CheckoutDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceContext commerceContext = (CommerceContext)request.getAttribute(CommerceWebKeys.COMMERCE_CONTEXT);

AccountEntry accountEntry = commerceContext.getAccountEntry();

OrderSummaryCheckoutStepDisplayContext orderSummaryCheckoutStepDisplayContext = (OrderSummaryCheckoutStepDisplayContext)request.getAttribute(CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_DISPLAY_CONTEXT);

CommerceOrder commerceOrder = orderSummaryCheckoutStepDisplayContext.getCommerceOrder();

CommerceOrderPrice commerceOrderPrice = orderSummaryCheckoutStepDisplayContext.getCommerceOrderPrice();

CommerceDiscountValue shippingDiscountValue = commerceOrderPrice.getShippingDiscountValue();
CommerceMoney shippingValueCommerceMoney = commerceOrderPrice.getShippingValue();
CommerceMoney subtotalCommerceMoney = commerceOrderPrice.getSubtotal();
CommerceDiscountValue subtotalCommerceDiscountValue = commerceOrderPrice.getSubtotalDiscountValue();
CommerceMoney taxValueCommerceMoney = commerceOrderPrice.getTaxValue();
CommerceDiscountValue totalCommerceDiscountValue = commerceOrderPrice.getTotalDiscountValue();
CommerceMoney totalOrderCommerceMoney = commerceOrderPrice.getTotal();

String priceDisplayType = orderSummaryCheckoutStepDisplayContext.getCommercePriceDisplayType();

if (priceDisplayType.equals(CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {
	shippingDiscountValue = commerceOrderPrice.getShippingDiscountValueWithTaxAmount();
	shippingValueCommerceMoney = commerceOrderPrice.getShippingValueWithTaxAmount();
	subtotalCommerceMoney = commerceOrderPrice.getSubtotalWithTaxAmount();
	subtotalCommerceDiscountValue = commerceOrderPrice.getSubtotalDiscountValueWithTaxAmount();
	totalCommerceDiscountValue = commerceOrderPrice.getTotalDiscountValueWithTaxAmount();
	totalOrderCommerceMoney = commerceOrderPrice.getTotalWithTaxAmount();
}
%>

<div class="commerce-order-summary">
	<liferay-ui:error exception="<%= AccountEntryValidatorException.class %>" message="account-validation-failed" />
	<liferay-ui:error exception="<%= CommerceDiscountLimitationTimesException.class %>" message="the-inserted-coupon-code-has-reached-its-usage-limit" />
	<liferay-ui:error exception="<%= CommerceOrderBillingAddressException.class %>" message="please-select-a-valid-billing-address" />
	<liferay-ui:error exception="<%= CommerceOrderGuestCheckoutException.class %>" message="you-must-sign-in-to-complete-this-order" />
	<liferay-ui:error exception="<%= CommerceOrderPaymentMethodException.class %>" message="please-select-a-valid-payment-method" />
	<liferay-ui:error exception="<%= CommerceOrderShippingAddressException.class %>" message="please-select-a-valid-shipping-address" />
	<liferay-ui:error exception="<%= CommerceOrderShippingMethodException.class %>" message="please-select-a-valid-shipping-method" />
	<liferay-ui:error exception="<%= NoSuchDiscountException.class %>" message="the-inserted-coupon-is-no-longer-valid" />

	<%
	AccountEntryValidatorResult accountEntryValidatorResult = orderSummaryCheckoutStepDisplayContext.getAccountEntryValidatorResult();
	%>

	<c:choose>
		<c:when test="<%= accountEntryValidatorResult != null %>">
			<div class="alert alert-danger">
				<liferay-ui:message key="<%= accountEntryValidatorResult.getResultMessage() %>" />
			</div>

			<aui:button-row>
				<portlet:renderURL var="previousStepURL">
					<portlet:param name="commerceOrderUuid" value="<%= String.valueOf(checkoutDisplayContext.getCommerceOrderUuid()) %>" />
					<portlet:param name="checkoutStepName" value="<%= checkoutDisplayContext.getPreviousCheckoutStepName() %>" />
				</portlet:renderURL>

				<aui:button cssClass="pull-left" href="<%= previousStepURL %>" value="previous" />

				<c:if test="<%= Validator.isNotNull(accountEntryValidatorResult.getActionURL()) %>">
					<aui:button cssClass="pull-right" href="<%= HtmlUtil.escapeHREF(accountEntryValidatorResult.getActionURL()) %>" primary="<%= true %>" value='<%= Validator.isNotNull(accountEntryValidatorResult.getActionLabel()) ? HtmlUtil.escape(LanguageUtil.get(request, accountEntryValidatorResult.getActionLabel())) : LanguageUtil.get(request, "resolve") %>' />
				</c:if>
			</aui:button-row>
		</c:when>
		<c:otherwise>
			<clay:row>
				<clay:col
					cssClass="commerce-checkout-summary"
					size="8"
				>
					<div>
						<liferay-frontend:screen-navigation
							context="<%= commerceOrder %>"
							key="<%= CommerceCheckoutScreenNavigationConstants.SCREEN_NAVIGATION_KEY_COMMERCE_CHECKOUT_ORDER_SUMMARY %>"
							menubarCssClass="menubar menubar-transparent menubar-vertical-expand-lg"
							navCssClass="col-lg-3"
							portletURL="<%= currentURLObj %>"
						/>
					</div>

					<ul class="commerce-checkout-summary-footer">
						<li class="autofit-row commerce-subtotal">
							<div class="autofit-col autofit-col-expand">
								<div class="commerce-description"><liferay-ui:message key="subtotal" /></div>
							</div>

							<div class="autofit-col">
								<div class="commerce-value"><%= HtmlUtil.escape(subtotalCommerceMoney.format(locale)) %></div>
							</div>
						</li>

						<c:if test="<%= subtotalCommerceDiscountValue != null %>">

							<%
							CommerceMoney subtotalDiscountAmountCommerceMoney = subtotalCommerceDiscountValue.getDiscountAmount();
							%>

							<li class="autofit-row commerce-subtotal-discount">
								<div class="autofit-col autofit-col-expand">
									<div class="commerce-description"><liferay-ui:message key="subtotal-discount" /></div>
								</div>

								<div class="commerce-value">
									<%= HtmlUtil.escape(subtotalDiscountAmountCommerceMoney.format(locale)) %>
								</div>
							</li>
							<li class="autofit-row commerce-subtotal-discount">
								<div class="autofit-col autofit-col-expand"></div>

								<div class="commerce-value">
									<%= HtmlUtil.escape(orderSummaryCheckoutStepDisplayContext.getLocalizedPercentage(subtotalCommerceDiscountValue.getDiscountPercentage(), locale)) %>
								</div>
							</li>
						</c:if>

						<li class="autofit-row commerce-delivery">
							<div class="autofit-col autofit-col-expand">
								<div class="commerce-description"><liferay-ui:message key="delivery" /></div>
							</div>

							<div class="autofit-col">
								<div class="commerce-value"><%= HtmlUtil.escape(shippingValueCommerceMoney.format(locale)) %></div>
							</div>
						</li>

						<c:if test="<%= shippingDiscountValue != null %>">

							<%
							CommerceMoney shippingDiscountAmountCommerceMoney = shippingDiscountValue.getDiscountAmount();
							%>

							<li class="autofit-row commerce-delivery-discount">
								<div class="autofit-col autofit-col-expand">
									<div class="commerce-description"><liferay-ui:message key="delivery-discount" /></div>
								</div>

								<div class="commerce-value">
									<%= HtmlUtil.escape(shippingDiscountAmountCommerceMoney.format(locale)) %>
								</div>
							</li>
							<li class="autofit-row commerce-delivery-discount">
								<div class="autofit-col autofit-col-expand"></div>

								<div class="commerce-value">
									<%= HtmlUtil.escape(orderSummaryCheckoutStepDisplayContext.getLocalizedPercentage(shippingDiscountValue.getDiscountPercentage(), locale)) %>
								</div>
							</li>
						</c:if>

						<c:if test="<%= priceDisplayType.equals(CommercePricingConstants.TAX_EXCLUDED_FROM_PRICE) %>">
							<li class="autofit-row commerce-tax">
								<div class="autofit-col autofit-col-expand">
									<div class="commerce-description"><liferay-ui:message key="tax" /></div>
								</div>

								<div class="autofit-col">
									<div class="commerce-value"><%= HtmlUtil.escape(taxValueCommerceMoney.format(locale)) %></div>
								</div>
							</li>
						</c:if>

						<c:if test="<%= totalCommerceDiscountValue != null %>">

							<%
							CommerceMoney totalDiscountAmountCommerceAmount = totalCommerceDiscountValue.getDiscountAmount();
							%>

							<li class="autofit-row commerce-total-discount">
								<div class="autofit-col autofit-col-expand">
									<div class="commerce-description"><liferay-ui:message key="total-discount" /></div>
								</div>

								<div class="autofit-col commerce-value">
									<%= HtmlUtil.escape(totalDiscountAmountCommerceAmount.format(locale)) %>
								</div>
							</li>
							<li class="autofit-row commerce-total-discount">
								<div class="autofit-col autofit-col-expand"></div>

								<div class="autofit-col commerce-value">
									<%= HtmlUtil.escape(orderSummaryCheckoutStepDisplayContext.getLocalizedPercentage(totalCommerceDiscountValue.getDiscountPercentage(), locale)) %>
								</div>
							</li>
						</c:if>

						<li class="autofit-row commerce-total">
							<div class="autofit-col autofit-col-expand">
								<div class="commerce-description"><liferay-ui:message key="total" /></div>
							</div>

							<div class="autofit-col">
								<div class="commerce-value"><%= HtmlUtil.escape(totalOrderCommerceMoney.format(locale)) %></div>
							</div>
						</li>
					</ul>
				</clay:col>

				<clay:col
					cssClass="commerce-checkout-info"
					size="4"
				>

					<%
					CommerceAddress shippingAddress = commerceOrder.getShippingAddress();
					%>

					<c:if test="<%= shippingAddress != null %>">
						<address class="shipping-address" data-qa-id="commerceShippingAddress">
							<div class="h5">
								<liferay-ui:message key="shipping-address-and-date" />
							</div>

							<%
							request.setAttribute("address.jsp-commerceAddress", shippingAddress);
							%>

							<%= HtmlUtil.escape(shippingAddress.getName()) %> <br />

							<c:if test="<%= Validator.isNotNull(shippingAddress.getSubtype()) %>">
								<%= HtmlUtil.escape(shippingAddress.getSubtype(locale)) %> <br />
							</c:if>

							<%= HtmlUtil.escape(shippingAddress.getStreet1()) %> <br />

							<c:if test="<%= Validator.isNotNull(shippingAddress.getStreet2()) %>">
								<%= HtmlUtil.escape(shippingAddress.getStreet2()) %> <br />
							</c:if>

							<c:if test="<%= Validator.isNotNull(shippingAddress.getStreet3()) %>">
								<%= HtmlUtil.escape(shippingAddress.getStreet3()) %> <br />
							</c:if>

							<%= HtmlUtil.escape(shippingAddress.getCity()) %> <br />

							<c:if test="<%= Validator.isNotNull(shippingAddress.getZip()) && checkoutDisplayContext.isOrderSummaryShowFullAddressEnabled() %>">
								<%= HtmlUtil.escape(shippingAddress.getZip()) %> <br />
							</c:if>

							<%
							Region region = shippingAddress.getRegion();
							%>

							<c:if test="<%= (region != null) && checkoutDisplayContext.isOrderSummaryShowFullAddressEnabled() %>">
								<%= HtmlUtil.escape(region.getTitle()) %> <br />
							</c:if>

							<%
							Country country = shippingAddress.getCountry();
							%>

							<c:if test="<%= country != null %>">
								<%= HtmlUtil.escape(country.getTitle(locale)) %><br />
							</c:if>

							<c:if test="<%= Validator.isNotNull(shippingAddress.getPhoneNumber()) && checkoutDisplayContext.isOrderSummaryShowPhoneNumberEnabled() %>">
								<%= HtmlUtil.escape(shippingAddress.getPhoneNumber()) %> <br />
							</c:if>

							<c:if test="<%= orderSummaryCheckoutStepDisplayContext.isCheckoutRequestedDeliveryDateEnabled() %>">

								<%
								int requestedDeliveryDay = 0;
								int requestedDeliveryMonth = -1;
								int requestedDeliveryYear = 0;

								Date requestedDeliveryDate = commerceOrder.getRequestedDeliveryDate();

								if (requestedDeliveryDate != null) {
									Calendar calendar = CalendarFactoryUtil.getCalendar(requestedDeliveryDate.getTime());

									requestedDeliveryDay = calendar.get(Calendar.DAY_OF_MONTH);
									requestedDeliveryMonth = calendar.get(Calendar.MONTH);
									requestedDeliveryYear = calendar.get(Calendar.YEAR);
								}
								%>

								<div class="form-group input-date-wrapper">
									<label for="requestedDeliveryDate"><liferay-ui:message key="requested-delivery-date" /></label>

									<liferay-ui:input-date
										dayParam="requestedDeliveryDateDay"
										dayValue="<%= requestedDeliveryDay %>"
										disabled="<%= false %>"
										firstEnabledDate="<%= new Date() %>"
										monthParam="requestedDeliveryDateMonth"
										monthValue="<%= requestedDeliveryMonth %>"
										name="requestedDeliveryDate"
										nullable="<%= true %>"
										showDisableCheckbox="<%= false %>"
										yearParam="requestedDeliveryDateYear"
										yearValue="<%= requestedDeliveryYear %>"
									/>
								</div>
							</c:if>
						</address>
					</c:if>

					<%
					CommerceAddress commerceBillingAddress = commerceOrder.getBillingAddress();
					%>

					<c:if test="<%= (commerceBillingAddress != null) && orderSummaryCheckoutStepDisplayContext.hasViewBillingAddressPermission(permissionChecker, accountEntry) %>">
						<address class="billing-address" data-qa-id="commerceBillingAddress">
							<div class="h5">
								<liferay-ui:message key="billing-address" />
							</div>

							<%
							request.setAttribute("address.jsp-commerceAddress", commerceBillingAddress);
							%>

							<%= HtmlUtil.escape(commerceBillingAddress.getName()) %> <br />

							<c:if test="<%= Validator.isNotNull(commerceBillingAddress.getSubtype()) %>">
								<%= HtmlUtil.escape(commerceBillingAddress.getSubtype(locale)) %> <br />
							</c:if>

							<%= HtmlUtil.escape(commerceBillingAddress.getStreet1()) %> <br />

							<c:if test="<%= Validator.isNotNull(commerceBillingAddress.getStreet2()) %>">
								<%= HtmlUtil.escape(commerceBillingAddress.getStreet2()) %> <br />
							</c:if>

							<c:if test="<%= Validator.isNotNull(commerceBillingAddress.getStreet3()) %>">
								<%= HtmlUtil.escape(commerceBillingAddress.getStreet3()) %> <br />
							</c:if>

							<%= HtmlUtil.escape(commerceBillingAddress.getCity()) %> <br />

							<c:if test="<%= Validator.isNotNull(commerceBillingAddress.getZip()) && checkoutDisplayContext.isOrderSummaryShowFullAddressEnabled() %>">
								<%= HtmlUtil.escape(commerceBillingAddress.getZip()) %> <br />
							</c:if>

							<%
							Region region = commerceBillingAddress.getRegion();
							%>

							<c:if test="<%= (region != null) && checkoutDisplayContext.isOrderSummaryShowFullAddressEnabled() %>">
								<%= HtmlUtil.escape(region.getTitle()) %> <br />
							</c:if>

							<%
							Country country = commerceBillingAddress.getCountry();
							%>

							<c:if test="<%= country != null %>">
								<%= HtmlUtil.escape(country.getTitle(locale)) %><br />
							</c:if>

							<c:if test="<%= Validator.isNotNull(commerceBillingAddress.getPhoneNumber()) && checkoutDisplayContext.isOrderSummaryShowPhoneNumberEnabled() %>">
								<%= HtmlUtil.escape(commerceBillingAddress.getPhoneNumber()) %> <br />
							</c:if>
						</address>
					</c:if>

					<%
					String commerceShippingOptionName = StringPool.BLANK;

					if (commerceOrder.getShippingOptionName() != null) {
						commerceShippingOptionName = orderSummaryCheckoutStepDisplayContext.getShippingOptionName(locale);
					}
					%>

					<c:if test="<%= Validator.isNotNull(commerceShippingOptionName) %>">
						<div class="panel-body shipping-method">
							<div class="h5">
								<liferay-ui:message key="method" />
							</div>

							<div class="shipping-description">
								<%= HtmlUtil.escape(commerceShippingOptionName) %>
							</div>

							<div class="shipping-cost">
								<%= HtmlUtil.escape(shippingValueCommerceMoney.format(locale)) %>
							</div>
						</div>
					</c:if>

					<%
					String commercePaymentMethodName = StringPool.BLANK;

					if (commerceOrder.getCommercePaymentMethodKey() != null) {
						commercePaymentMethodName = orderSummaryCheckoutStepDisplayContext.getPaymentMethodName(commerceOrder.getCommercePaymentMethodKey(), locale);
					}
					%>

					<c:if test="<%= Validator.isNotNull(commercePaymentMethodName) %>">
						<div class="panel-body payment-method">
							<div class="h5">
								<liferay-ui:message key="payment" />
							</div>

							<div class="shipping-description">
								<%= HtmlUtil.escape(commercePaymentMethodName) %>
							</div>
						</div>
					</c:if>

					<%
					String deliveryTermEntryName = orderSummaryCheckoutStepDisplayContext.getDeliveryTermEntryName(locale);
					%>

					<c:if test="<%= Validator.isNotNull(deliveryTermEntryName) %>">
						<div class="delivery-term panel-body">
							<div class="h5">
								<liferay-ui:message key="delivery-terms" />
							</div>

							<div class="shipping-description">
								<a href="#" id="<%= commerceOrder.getDeliveryCommerceTermEntryId() %>"><%= HtmlUtil.escape(deliveryTermEntryName) %></a>

								<liferay-frontend:component
									context='<%=
										HashMapBuilder.<String, Object>put(
											"HTMLElementId", commerceOrder.getDeliveryCommerceTermEntryId()
										).put(
											"modalContent", commerceOrder.getDeliveryCommerceTermEntryDescription()
										).put(
											"modalTitle", deliveryTermEntryName
										).build()
									%>'
									module="{attachModalToHTMLElement} from commerce-checkout-web"
								/>
							</div>
						</div>
					</c:if>

					<%
					String paymentTermEntryName = orderSummaryCheckoutStepDisplayContext.getPaymentTermEntryName(locale);
					%>

					<c:if test="<%= Validator.isNotNull(paymentTermEntryName) %>">
						<div class="panel-body payment-term">
							<div class="h5">
								<liferay-ui:message key="payment-terms" />
							</div>

							<div class="shipping-description">
								<a href="#" id="<%= commerceOrder.getPaymentCommerceTermEntryId() %>"><%= HtmlUtil.escape(paymentTermEntryName) %></a>

								<liferay-frontend:component
									context='<%=
										HashMapBuilder.<String, Object>put(
											"HTMLElementId", commerceOrder.getPaymentCommerceTermEntryId()
										).put(
											"modalContent", commerceOrder.getPaymentCommerceTermEntryDescription()
										).put(
											"modalTitle", paymentTermEntryName
										).build()
									%>'
									module="{attachModalToHTMLElement} from commerce-checkout-web"
								/>
							</div>
						</div>
					</c:if>
				</clay:col>
			</clay:row>
		</c:otherwise>
	</c:choose>
</div>