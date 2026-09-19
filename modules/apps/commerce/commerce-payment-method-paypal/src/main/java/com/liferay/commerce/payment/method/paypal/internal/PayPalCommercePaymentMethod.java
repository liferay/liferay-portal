/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.paypal.internal;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.paypal.internal.configuration.PayPalGroupServiceConfiguration;
import com.liferay.commerce.payment.method.paypal.internal.constants.PayPalCommercePaymentMethodConstants;
import com.liferay.commerce.payment.request.CommercePaymentRequest;
import com.liferay.commerce.payment.result.CommercePaymentResult;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.AgreementStateDescriptor;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.MerchantPreferences;
import com.paypal.api.payments.Patch;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PaymentDefinition;
import com.paypal.api.payments.Plan;
import com.paypal.api.payments.ShippingAddress;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.AddressPortable;
import com.paypal.orders.AmountBreakdown;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Item;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Name;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersAuthorizeRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.orders.ShippingDetail;
import com.paypal.payments.AuthorizationsCaptureRequest;
import com.paypal.payments.Capture;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Money;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;

import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "commerce.payment.engine.method.key=" + PayPalCommercePaymentMethod.KEY,
	service = CommercePaymentMethod.class
)
public class PayPalCommercePaymentMethod implements CommercePaymentMethod {

	public static final String KEY = "paypal";

	public PayPalCommercePaymentMethod() {
		DecimalFormatSymbols decimalFormatSymbols =
			_payPalDecimalFormat.getDecimalFormatSymbols();

		decimalFormatSymbols.setDecimalSeparator(CharPool.PERIOD);
		decimalFormatSymbols.setGroupingSeparator(CharPool.COMMA);

		_payPalDecimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
	}

	@Override
	public boolean activateRecurringPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		Agreement agreement = new Agreement();

		agreement.setId(commercePaymentRequest.getTransactionId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentRequest.getCommerceOrderId());

		APIContext apiContext = _getAPIContext(commerceOrder.getGroupId());

		AgreementStateDescriptor agreementStateDescriptor =
			new AgreementStateDescriptor();

		agreementStateDescriptor.setNote(
			_getResource(
				commercePaymentRequest.getLocale(), "reactivate-agreement"));

		agreement.reActivate(apiContext, agreementStateDescriptor);

		Agreement updatedAgreement = Agreement.get(
			apiContext, agreement.getId());

		return Objects.equals(
			PayPalCommercePaymentMethodConstants.ACTIVE,
			updatedAgreement.getState());
	}

	@Override
	public CommercePaymentResult authorizePayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		boolean success = false;
		int status = CommerceOrderPaymentConstants.STATUS_FAILED;
		String transactionId = StringPool.BLANK;

		try {
			String url = null;

			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					commercePaymentRequest.getCommerceOrderId());

			OrderRequest orderRequest = new OrderRequest();

			orderRequest.checkoutPaymentIntent(
				PayPalCommercePaymentMethodConstants.INTENT_AUTHORIZE);
			orderRequest.purchaseUnits(
				_buildRequestBody(
					commerceOrder, commercePaymentRequest.getLocale()));

			OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest();

			ordersCreateRequest.prefer("return=representation");
			ordersCreateRequest.requestBody(orderRequest);

			PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(
				commerceOrder);

			HttpResponse<Order> orderCreateHttpResponse =
				payPalHttpClient.execute(ordersCreateRequest);

			if (orderCreateHttpResponse.statusCode() == 201) {
				Order createOrder = orderCreateHttpResponse.result();

				OrdersAuthorizeRequest ordersAuthorizeRequest =
					new OrdersAuthorizeRequest(createOrder.id());

				ordersAuthorizeRequest.requestBody(new OrderRequest());

				HttpResponse<Order> authorizeHttpResponse =
					payPalHttpClient.execute(ordersAuthorizeRequest);

				if (authorizeHttpResponse.statusCode() == 201) {
					Order authorizeOrder = authorizeHttpResponse.result();

					for (LinkDescription linkDescription :
							authorizeOrder.links()) {

						if (Objects.equals(
								PayPalCommercePaymentMethodConstants.
									APPROVE_URL,
								linkDescription.rel())) {

							url = linkDescription.href();
						}
					}

					success = true;
					status = CommerceOrderPaymentConstants.STATUS_AUTHORIZED;
					transactionId = authorizeOrder.id();
				}
			}

			return new CommercePaymentResult(
				transactionId, commercePaymentRequest.getCommerceOrderId(),
				status, true, url, null, Collections.emptyList(), success);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			HttpException httpException = (HttpException)ioException;

			JSONObject jsonObject = new JSONObject(httpException.getMessage());

			List<String> errorMessages = _getErrorMessages(
				jsonObject, StringPool.BLANK);

			return new CommercePaymentResult(
				commercePaymentRequest.getTransactionId(),
				commercePaymentRequest.getCommerceOrderId(), status, true, null,
				null, errorMessages, success);
		}
	}

	@Override
	public CommercePaymentResult cancelPayment(
		CommercePaymentRequest commercePaymentRequest) {

		return new CommercePaymentResult(
			commercePaymentRequest.getTransactionId(),
			commercePaymentRequest.getCommerceOrderId(),
			CommerceOrderPaymentConstants.STATUS_CANCELLED, false, null, null,
			Collections.emptyList(), true);
	}

	@Override
	public boolean cancelRecurringPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		Agreement agreement = new Agreement();

		agreement.setId(commercePaymentRequest.getTransactionId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentRequest.getCommerceOrderId());

		APIContext apiContext = _getAPIContext(commerceOrder.getGroupId());

		AgreementStateDescriptor agreementStateDescriptor =
			new AgreementStateDescriptor();

		agreementStateDescriptor.setNote(
			_getResource(
				commercePaymentRequest.getLocale(), "cancel-agreement"));

		agreement.cancel(apiContext, agreementStateDescriptor);

		Agreement updatedAgreement = Agreement.get(
			apiContext, agreement.getId());

		return Objects.equals(
			PayPalCommercePaymentMethodConstants.CANCELLED,
			updatedAgreement.getState());
	}

	@Override
	public CommercePaymentResult capturePayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		boolean success = false;
		int status = CommerceOrderPaymentConstants.STATUS_FAILED;
		String transactionId = commercePaymentRequest.getTransactionId();

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentRequest.getCommerceOrderId());

		AuthorizationsCaptureRequest authorizationsCaptureRequest =
			new AuthorizationsCaptureRequest(
				commercePaymentRequest.getTransactionId());

		authorizationsCaptureRequest.requestBody(new OrderRequest());

		PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(commerceOrder);

		HttpResponse<Capture> captureHttpResponse = payPalHttpClient.execute(
			authorizationsCaptureRequest);

		if (captureHttpResponse.statusCode() == 201) {
			Capture capture = captureHttpResponse.result();

			success = true;
			status = CommerceOrderPaymentConstants.STATUS_COMPLETED;
			transactionId = capture.id();
		}

		return new CommercePaymentResult(
			transactionId, commercePaymentRequest.getCommerceOrderId(), status,
			false, null, transactionId, Collections.emptyList(), success);
	}

	@Override
	public CommercePaymentResult completePayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		try {
			boolean success = false;

			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					commercePaymentRequest.getCommerceOrderId());

			OrdersCaptureRequest ordersCaptureRequest =
				new OrdersCaptureRequest(
					commercePaymentRequest.getTransactionId());

			ordersCaptureRequest.requestBody(new OrderRequest());

			PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(
				commerceOrder);

			HttpResponse<Order> orderCaptureHttpResponse =
				payPalHttpClient.execute(ordersCaptureRequest);

			if (orderCaptureHttpResponse.statusCode() == 201) {
				success = true;

				Order captureOrder = orderCaptureHttpResponse.result();

				return new CommercePaymentResult(
					captureOrder.id(),
					commercePaymentRequest.getCommerceOrderId(),
					CommerceOrderPaymentConstants.STATUS_COMPLETED, false, null,
					null, Collections.emptyList(), success);
			}

			return new CommercePaymentResult(
				commercePaymentRequest.getTransactionId(),
				commercePaymentRequest.getCommerceOrderId(),
				CommerceOrderPaymentConstants.STATUS_AUTHORIZED, true, null,
				null, Collections.emptyList(), success);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			HttpException httpException = (HttpException)ioException;

			JSONObject jsonObject = new JSONObject(httpException.getMessage());

			List<String> errorMessages = _getErrorMessages(
				jsonObject, StringPool.BLANK);

			return new CommercePaymentResult(
				commercePaymentRequest.getTransactionId(),
				commercePaymentRequest.getCommerceOrderId(),
				CommerceOrderPaymentConstants.STATUS_AUTHORIZED, true, null,
				null, errorMessages, false);
		}
	}

	@Override
	public CommercePaymentResult completeRecurringPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		try {
			boolean success = true;

			Agreement agreement = new Agreement();

			PayPalCommercePaymentRequest payPalCommercePaymentRequest =
				(PayPalCommercePaymentRequest)commercePaymentRequest;

			agreement.setToken(payPalCommercePaymentRequest.getTransactionId());

			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					commercePaymentRequest.getCommerceOrderId());

			Agreement activeAgreement = agreement.execute(
				_getAPIContext(commerceOrder.getGroupId()),
				agreement.getToken());

			if (PayPalCommercePaymentMethodConstants.PAYMENT_STATE_FAILED.
					equals(activeAgreement.getState())) {

				success = false;
			}

			List<String> messages = Arrays.asList(
				activeAgreement.getDescription());

			return new CommercePaymentResult(
				activeAgreement.getId(),
				commercePaymentRequest.getCommerceOrderId(),
				CommerceOrderPaymentConstants.STATUS_COMPLETED, false, null,
				null, messages, success);
		}
		catch (PayPalRESTException payPalRESTException) {
			_log.error(payPalRESTException);

			return new CommercePaymentResult(
				commercePaymentRequest.getTransactionId(),
				commercePaymentRequest.getCommerceOrderId(),
				CommerceOrderPaymentConstants.STATUS_AUTHORIZED, true, null,
				null,
				Collections.singletonList(payPalRESTException.getMessage()),
				false);
		}
	}

	@Override
	public String getDescription(Locale locale) {
		return _getResource(locale, "paypal-description");
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return "PayPal Subscriptions";
	}

	/**
	 * @deprecated As of Mueller (7.2.x), this method will be removed
	 */
	@Deprecated
	@Override
	public int getOrderStatusUpdateMaxIntervalMinutes() {
		return 2880;
	}

	@Override
	public int getPaymentType() {
		return CommercePaymentMethodConstants.TYPE_ONLINE_REDIRECT;
	}

	@Override
	public String getServletPath() {
		return PayPalCommercePaymentMethodConstants.PAYMENT_METHOD_SERVLET_PATH;
	}

	@Override
	public boolean getSubscriptionValidity(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentRequest.getCommerceOrderId());

		try {
			Agreement agreement = Agreement.get(
				_getAPIContext(commerceOrder.getGroupId()),
				commercePaymentRequest.getTransactionId());

			String agreementState = agreement.getState();

			if (Objects.equals(
					PayPalCommercePaymentMethodConstants.ACTIVE,
					agreementState)) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Override
	public boolean isAuthorizeEnabled() {
		return false;
	}

	@Override
	public boolean isCancelEnabled() {
		return false;
	}

	@Override
	public boolean isCaptureEnabled() {
		return false;
	}

	@Override
	public boolean isCompleteEnabled() {
		return false;
	}

	@Override
	public boolean isCompleteRecurringEnabled() {
		return true;
	}

	@Override
	public boolean isPartialRefundEnabled() {
		return false;
	}

	@Override
	public boolean isProcessPaymentEnabled() {
		return false;
	}

	@Override
	public boolean isProcessRecurringEnabled() {
		return true;
	}

	@Override
	public boolean isRefundEnabled() {
		return false;
	}

	@Override
	public boolean isVoidEnabled() {
		return false;
	}

	@Override
	public CommercePaymentResult partiallyRefundPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		boolean success = false;
		int status = CommerceOrderPaymentConstants.STATUS_FAILED;
		String refundId = StringPool.BLANK;

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentRequest.getCommerceOrderId());

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		CapturesRefundRequest capturesRefundRequest = new CapturesRefundRequest(
			commercePaymentRequest.getTransactionId());

		capturesRefundRequest.prefer("return=representation");

		capturesRefundRequest.requestBody(
			_buildRefundRequestBody(
				String.valueOf(commercePaymentRequest.getAmount()),
				commerceCurrency.getCode()));

		PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(commerceOrder);

		HttpResponse<Refund> refundHttpResponse = payPalHttpClient.execute(
			capturesRefundRequest);

		if (refundHttpResponse.statusCode() == 201) {
			Refund refund = refundHttpResponse.result();

			success = true;
			status = CommerceOrderConstants.ORDER_STATUS_PARTIALLY_REFUNDED;

			refundId = refund.id();
		}

		return new CommercePaymentResult(
			commercePaymentRequest.getTransactionId(),
			commercePaymentRequest.getCommerceOrderId(), status, false, null,
			refundId, Collections.emptyList(), success);
	}

	@Override
	public CommercePaymentResult processPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		boolean success = false;
		int status = CommerceOrderPaymentConstants.STATUS_FAILED;
		String transactionId = StringPool.BLANK;

		try {
			String url = null;

			CommerceOrder commerceOrder =
				_commerceOrderLocalService.getCommerceOrder(
					commercePaymentRequest.getCommerceOrderId());

			OrderRequest orderRequest = new OrderRequest();

			orderRequest.checkoutPaymentIntent(
				PayPalCommercePaymentMethodConstants.INTENT_CAPTURE);
			orderRequest.purchaseUnits(
				_buildRequestBody(
					commerceOrder, commercePaymentRequest.getLocale()));

			ApplicationContext applicationContext = new ApplicationContext();

			applicationContext.cancelUrl(commercePaymentRequest.getCancelUrl());
			applicationContext.returnUrl(commercePaymentRequest.getReturnUrl());

			orderRequest.applicationContext(applicationContext);

			OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest();

			ordersCreateRequest.prefer("return=representation");
			ordersCreateRequest.requestBody(orderRequest);

			PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(
				commerceOrder);

			HttpResponse<Order> orderCreateHttpResponse =
				payPalHttpClient.execute(ordersCreateRequest);

			if (orderCreateHttpResponse.statusCode() == 201) {
				Order createOrder = orderCreateHttpResponse.result();

				for (LinkDescription linkDescription : createOrder.links()) {
					if (Objects.equals(
							PayPalCommercePaymentMethodConstants.APPROVE_URL,
							linkDescription.rel())) {

						url = linkDescription.href();
					}
				}

				if (Validator.isNull(url)) {
					throw new PortalException(
						"Unable to get PayPal payment URL");
				}

				success = true;
				status = CommerceOrderPaymentConstants.STATUS_AUTHORIZED;

				transactionId = createOrder.id();
			}

			return new CommercePaymentResult(
				transactionId, commercePaymentRequest.getCommerceOrderId(),
				status, true, url, transactionId, Collections.emptyList(),
				success);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			HttpException httpException = (HttpException)ioException;

			JSONObject jsonObject = new JSONObject(httpException.getMessage());

			List<String> errorMessages = _getErrorMessages(
				jsonObject, StringPool.BLANK);

			return new CommercePaymentResult(
				commercePaymentRequest.getTransactionId(),
				commercePaymentRequest.getCommerceOrderId(), status, true, null,
				null, errorMessages, success);
		}
	}

	@Override
	public CommercePaymentResult processRecurringPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentRequest.getCommerceOrderId());

		boolean success = false;
		int status = CommerceOrderPaymentConstants.STATUS_FAILED;

		try {
			APIContext apiContext = _getAPIContext(commerceOrder.getGroupId());

			Plan plan = _getPlan(
				commercePaymentRequest, commerceOrder, apiContext,
				commercePaymentRequest.getLocale());

			if (plan == null) {
				return null;
			}

			String url = null;

			Agreement agreement = _getAgreement(
				commerceOrder, apiContext, plan,
				commercePaymentRequest.getLocale());

			for (Links links : agreement.getLinks()) {
				if (Objects.equals(
						PayPalCommercePaymentMethodConstants.APPROVAL_URL,
						links.getRel())) {

					url = links.getHref();

					break;
				}
			}

			String token = agreement.getToken();

			if (PayPalCommercePaymentMethodConstants.
					AUTHORIZATION_STATE_CREATED.equalsIgnoreCase(
						plan.getState()) &&
				Validator.isNotNull(token)) {

				success = true;
				status = CommerceOrderPaymentConstants.STATUS_AUTHORIZED;
			}

			List<String> messages = Arrays.asList(plan.getState());

			return new CommercePaymentResult(
				token, commercePaymentRequest.getCommerceOrderId(), status,
				true, url, null, messages, success);
		}
		catch (PayPalRESTException payPalRESTException) {
			_log.error(payPalRESTException);

			return new CommercePaymentResult(
				commercePaymentRequest.getTransactionId(),
				commercePaymentRequest.getCommerceOrderId(), status, true, null,
				null,
				Collections.singletonList(payPalRESTException.getMessage()),
				success);
		}
	}

	@Override
	public CommercePaymentResult refundPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		boolean success = false;
		int status = CommerceOrderPaymentConstants.STATUS_FAILED;
		String refundId = StringPool.BLANK;

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentRequest.getCommerceOrderId());

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		CapturesRefundRequest capturesRefundRequest = new CapturesRefundRequest(
			commercePaymentRequest.getTransactionId());

		capturesRefundRequest.prefer("return=representation");

		BigDecimal orderTotal = commerceOrder.getTotal();

		capturesRefundRequest.requestBody(
			_buildRefundRequestBody(
				orderTotal.toString(), commerceCurrency.getCode()));

		PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(commerceOrder);

		HttpResponse<Refund> refundHttpResponse = payPalHttpClient.execute(
			capturesRefundRequest);

		if (refundHttpResponse.statusCode() == 201) {
			Refund refund = refundHttpResponse.result();

			success = true;
			status = CommerceOrderPaymentConstants.STATUS_REFUNDED;

			refundId = refund.id();
		}

		return new CommercePaymentResult(
			commercePaymentRequest.getTransactionId(),
			commercePaymentRequest.getCommerceOrderId(), status, false, null,
			refundId, Collections.emptyList(), success);
	}

	@Override
	public boolean suspendRecurringPayment(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		Agreement agreement = new Agreement();

		agreement.setId(commercePaymentRequest.getTransactionId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentRequest.getCommerceOrderId());

		APIContext apiContext = _getAPIContext(commerceOrder.getGroupId());

		AgreementStateDescriptor agreementStateDescriptor =
			new AgreementStateDescriptor();

		agreementStateDescriptor.setNote(
			_getResource(
				commercePaymentRequest.getLocale(), "suspend-agreement"));

		agreement.suspend(apiContext, agreementStateDescriptor);

		Agreement updatedAgreement = Agreement.get(
			apiContext, agreement.getId());

		return Objects.equals(
			PayPalCommercePaymentMethodConstants.SUSPENDED,
			updatedAgreement.getState());
	}

	@Override
	public CommercePaymentResult voidTransaction(
			CommercePaymentRequest commercePaymentRequest)
		throws Exception {

		return null;
	}

	private List<PurchaseUnitRequest> _buildFullRequestBody(
			CommerceOrder commerceOrder, Locale locale)
		throws PortalException {

		List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown();

		amountWithBreakdown.currencyCode(commerceCurrency.getCode());
		amountWithBreakdown.value(
			_getAmountValue(commerceOrder.getTotal(), commerceCurrency));

		AmountBreakdown amountBreakdown = new AmountBreakdown();

		com.paypal.orders.Money shippingMoney = new com.paypal.orders.Money();

		shippingMoney.currencyCode(commerceCurrency.getCode());
		shippingMoney.value(
			_getAmountValue(
				commerceOrder.getShippingAmount(), commerceCurrency));

		amountBreakdown.shipping(shippingMoney);

		com.paypal.orders.Money itemTotalMoney = new com.paypal.orders.Money();

		itemTotalMoney.currencyCode(commerceCurrency.getCode());
		itemTotalMoney.value(
			_getAmountValue(commerceOrder.getSubtotal(), commerceCurrency));

		amountBreakdown.itemTotal(itemTotalMoney);

		com.paypal.orders.Money taxTotalMoney = new com.paypal.orders.Money();

		taxTotalMoney.currencyCode(commerceCurrency.getCode());
		taxTotalMoney.value(
			_getAmountValue(commerceOrder.getTaxAmount(), commerceCurrency));

		amountBreakdown.taxTotal(taxTotalMoney);

		amountWithBreakdown.amountBreakdown(amountBreakdown);

		PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest();

		purchaseUnitRequest.amountWithBreakdown(amountWithBreakdown);

		CommerceAddress shippingCommerceAddress =
			commerceOrder.getShippingAddress();

		if (shippingCommerceAddress != null) {
			ShippingDetail shippingDetail = new ShippingDetail();

			Name name = new Name();

			name.fullName(shippingCommerceAddress.getName());

			shippingDetail.name(name);

			AddressPortable addressPortable = new AddressPortable();

			addressPortable.addressLine1(shippingCommerceAddress.getStreet1());
			addressPortable.addressLine2(shippingCommerceAddress.getStreet2());
			addressPortable.postalCode(shippingCommerceAddress.getZip());

			Country country = shippingCommerceAddress.getCountry();

			addressPortable.countryCode(country.getA2());

			Region region = shippingCommerceAddress.getRegion();

			addressPortable.adminArea1(region.getRegionCode());

			addressPortable.adminArea2(shippingCommerceAddress.getCity());

			shippingDetail.addressPortable(addressPortable);

			purchaseUnitRequest.shippingDetail(shippingDetail);
		}

		purchaseUnitRequest.items(
			TransformUtil.transform(
				commerceOrder.getCommerceOrderItems(),
				commerceOrderItem -> {
					Item item = new Item();

					item.name(commerceOrderItem.getName(locale));
					item.quantity(
						String.valueOf(commerceOrderItem.getQuantity()));
					item.sku(commerceOrderItem.getSku());

					com.paypal.orders.Money unitAmountMoney =
						new com.paypal.orders.Money();

					unitAmountMoney.currencyCode(commerceCurrency.getCode());

					BigDecimal finalPrice = commerceOrderItem.getFinalPrice();

					BigDecimal unitAmount = finalPrice.divide(
						commerceOrderItem.getQuantity());

					unitAmountMoney.value(
						_getAmountValue(unitAmount, commerceCurrency));

					item.unitAmount(unitAmountMoney);

					return item;
				}));

		purchaseUnitRequests.add(purchaseUnitRequest);

		return purchaseUnitRequests;
	}

	private List<PurchaseUnitRequest> _buildMinimalRequestBody(
			CommerceOrder commerceOrder)
		throws PortalException {

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		return TransformUtil.transform(
			commerceOrder.getCommerceOrderItems(),
			commerceOrderItem -> {
				AmountWithBreakdown amountWithBreakdown =
					new AmountWithBreakdown();

				amountWithBreakdown.currencyCode(commerceCurrency.getCode());

				amountWithBreakdown.value(
					_getAmountValue(
						commerceOrderItem.getFinalPrice(), commerceCurrency));

				PurchaseUnitRequest purchaseUnitRequest =
					new PurchaseUnitRequest();

				purchaseUnitRequest.amountWithBreakdown(amountWithBreakdown);
				purchaseUnitRequest.referenceId(
					String.valueOf(commerceOrderItem.getCommerceOrderItemId()));

				return purchaseUnitRequest;
			});
	}

	private RefundRequest _buildRefundRequestBody(
		String amount, String currencyCode) {

		Money amountMoney = new Money();

		amountMoney.currencyCode(currencyCode);
		amountMoney.value(amount);

		RefundRequest refundRequest = new RefundRequest();

		refundRequest.amount(amountMoney);

		return refundRequest;
	}

	private List<PurchaseUnitRequest> _buildRequestBody(
			CommerceOrder commerceOrder, Locale locale)
		throws PortalException {

		PayPalGroupServiceConfiguration payPalGroupServiceConfiguration =
			_getPayPalGroupServiceConfiguration(commerceOrder.getGroupId());

		String requestDetails =
			payPalGroupServiceConfiguration.requestDetails();

		if (requestDetails.equals(
				PayPalCommercePaymentMethodConstants.REQUEST_DETAILS_FULL)) {

			return _buildFullRequestBody(commerceOrder, locale);
		}

		return _buildMinimalRequestBody(commerceOrder);
	}

	private APIContext _getAPIContext(long groupId) throws PortalException {
		PayPalGroupServiceConfiguration payPalGroupServiceConfiguration =
			_getPayPalGroupServiceConfiguration(groupId);

		return new APIContext(
			payPalGroupServiceConfiguration.clientId(),
			payPalGroupServiceConfiguration.clientSecret(),
			payPalGroupServiceConfiguration.mode());
	}

	private Agreement _getAgreement(
			CommerceOrder commerceOrder, APIContext apiContext, Plan plan,
			Locale locale)
		throws Exception {

		// Create new agreement

		Agreement agreement = new Agreement();

		agreement.setName(_getResource(locale, "base-agreement"));
		agreement.setDescription(
			_getResource(locale, "base-agreement-description"));

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(_DATE_FORMAT);

		Calendar calendar = Calendar.getInstance(_timeZone);

		calendar.add(Calendar.DAY_OF_MONTH, 1);

		String date = simpleDateFormat.format(calendar.getTime());

		agreement.setStartDate(date);

		// Set plan ID

		Plan agreementPlan = new Plan();

		agreementPlan.setId(plan.getId());

		agreement.setPlan(agreementPlan);

		// Add payer details

		Payer payer = new Payer();

		payer.setPaymentMethod(KEY);

		agreement.setPayer(payer);

		CommerceAddress commerceAddress = commerceOrder.getShippingAddress();

		if (commerceAddress == null) {
			AccountEntry accountEntry = commerceOrder.getAccountEntry();

			commerceAddress = _commerceAddressLocalService.fetchCommerceAddress(
				accountEntry.getDefaultShippingAddressId());
		}

		if (commerceAddress == null) {
			commerceAddress = commerceOrder.getBillingAddress();
		}

		ShippingAddress shippingAddress = _getShippingAddress(commerceAddress);

		shippingAddress.setRecipientName(null);

		agreement.setShippingAddress(shippingAddress);

		return agreement.create(apiContext);
	}

	private String _getAmountValue(
		BigDecimal amount, CommerceCurrency commerceCurrency) {

		BigDecimal scaledAmount = amount.setScale(
			2, RoundingMode.valueOf(commerceCurrency.getRoundingMode()));

		return scaledAmount.toPlainString();
	}

	private List<String> _getErrorMessages(
		JSONObject jsonObject, String prefix) {

		List<String> errorMessages = new ArrayList<>();

		Iterator<?> keysIterator = jsonObject.keys();
		StringBuilder stringBuilder = new StringBuilder();

		while (keysIterator.hasNext()) {
			String key = (String)keysIterator.next();

			stringBuilder.append(
				String.format("%s%s: ", prefix, StringUtils.capitalize(key)));

			if (jsonObject.get(key) instanceof JSONObject) {
				stringBuilder.append(
					_getErrorMessages(
						jsonObject.getJSONObject(key), prefix + "\t"));
			}
			else if (jsonObject.get(key) instanceof JSONArray) {
				int counter = 1;

				for (Object object : jsonObject.getJSONArray(key)) {
					stringBuilder.append(
						String.format("\n%s\t%d:\n", prefix, counter++));
					stringBuilder.append(
						_getErrorMessages((JSONObject)object, prefix + "\t\t"));
				}
			}
			else {
				stringBuilder.append(
					String.format("%s\n", jsonObject.getString(key)));
			}
		}

		errorMessages.add(stringBuilder.toString());

		return errorMessages;
	}

	private PayPalGroupServiceConfiguration _getPayPalGroupServiceConfiguration(
			long groupId)
		throws PortalException {

		return _configurationProvider.getConfiguration(
			PayPalGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId,
				PayPalCommercePaymentMethodConstants.
					COMMERCE_PAYMENT_ENGINE_SERVICE_NAME));
	}

	private PayPalHttpClient _getPayPalHttpClient(CommerceOrder commerceOrder)
		throws PortalException {

		PayPalGroupServiceConfiguration payPalGroupServiceConfiguration =
			_getPayPalGroupServiceConfiguration(commerceOrder.getGroupId());

		PayPalEnvironment payPalEnvironment = new PayPalEnvironment.Sandbox(
			payPalGroupServiceConfiguration.clientId(),
			payPalGroupServiceConfiguration.clientSecret());

		String mode = payPalGroupServiceConfiguration.mode();

		if (mode.equals(PayPalCommercePaymentMethodConstants.MODE_LIVE)) {
			payPalEnvironment = new PayPalEnvironment.Live(
				payPalGroupServiceConfiguration.clientId(),
				payPalGroupServiceConfiguration.clientSecret());
		}

		return new PayPalHttpClient(payPalEnvironment);
	}

	private Plan _getPlan(
			CommercePaymentRequest commercePaymentRequest,
			CommerceOrder commerceOrder, APIContext apiContext, Locale locale)
		throws PayPalRESTException, PortalException {

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		List<PaymentDefinition> paymentDefinitions = new ArrayList<>(
			commerceOrderItems.size());

		CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

		String subscriptionType = commerceOrderItem.getSubscriptionType();

		if (subscriptionType.equals(CPConstants.MONTHLY_SUBSCRIPTION_TYPE)) {
			subscriptionType = PayPalCommercePaymentMethodConstants.MONTH;
		}
		else if (subscriptionType.equals(CPConstants.DAILY_SUBSCRIPTION_TYPE)) {
			subscriptionType = PayPalCommercePaymentMethodConstants.DAY;
		}
		else if (subscriptionType.equals(
					CPConstants.WEEKLY_SUBSCRIPTION_TYPE)) {

			subscriptionType = PayPalCommercePaymentMethodConstants.WEEK;
		}
		else if (subscriptionType.equals(
					CPConstants.YEARLY_SUBSCRIPTION_TYPE)) {

			subscriptionType = PayPalCommercePaymentMethodConstants.YEAR;
		}

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		Currency amountCurrency = new Currency(
			commerceCurrency.getCode(),
			_payPalDecimalFormat.format(commerceOrderItem.getFinalPrice()));

		PaymentDefinition paymentDefinition = new PaymentDefinition(
			_getResource(locale, "payment-definition"),
			PayPalCommercePaymentMethodConstants.PAYMENT_DEFINITION_REGULAR,
			String.valueOf(commerceOrderItem.getSubscriptionLength()),
			subscriptionType,
			String.valueOf(commerceOrderItem.getMaxSubscriptionCycles()),
			amountCurrency);

		paymentDefinitions.add(paymentDefinition);

		String name = _getResource(locale, "payment-plan");
		String description = _getResource(locale, "payment-plan-description");

		String type = PayPalCommercePaymentMethodConstants.PLAN_FIXED;

		if (commerceOrderItem.getMaxSubscriptionCycles() == 0) {
			type = PayPalCommercePaymentMethodConstants.PLAN_INFINITE;
		}

		Plan plan = new Plan(name, description, type);

		plan.setPaymentDefinitions(paymentDefinitions);

		MerchantPreferences merchantPreferences = new MerchantPreferences();

		merchantPreferences.setAutoBillAmount(
			PayPalCommercePaymentMethodConstants.AUTO_BILLING_AMOUNT_ENABLED);
		merchantPreferences.setCancelUrl(commercePaymentRequest.getCancelUrl());
		merchantPreferences.setInitialFailAmountAction(
			PayPalCommercePaymentMethodConstants.INITIAL_FAIL_AMOUNT_ACTION);
		merchantPreferences.setReturnUrl(commercePaymentRequest.getReturnUrl());

		BigDecimal shippingAmount = commerceOrder.getShippingAmount();

		merchantPreferences.setSetupFee(
			new Currency(
				commerceCurrency.getCode(),
				_payPalDecimalFormat.format(
					shippingAmount.add(commerceOrder.getTaxAmount()))));

		PayPalGroupServiceConfiguration payPalGroupServiceConfiguration =
			_getPayPalGroupServiceConfiguration(commerceOrder.getGroupId());

		String attemptsMaxCount =
			payPalGroupServiceConfiguration.paymentAttemptsMaxCount();

		try {
			Integer.parseInt(attemptsMaxCount);
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(numberFormatException);
			}

			attemptsMaxCount = "0";
		}

		merchantPreferences.setMaxFailAttempts(attemptsMaxCount);

		plan.setMerchantPreferences(merchantPreferences);

		plan = plan.create(apiContext);

		return _updatePlan(apiContext, plan);
	}

	private String _getResource(Locale locale, String key) {
		if (locale == null) {
			locale = LocaleUtil.getSiteDefault();
		}

		return _language.get(_getResourceBundle(locale), key);
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private ShippingAddress _getShippingAddress(CommerceAddress commerceAddress)
		throws Exception {

		ShippingAddress shippingAddress = new ShippingAddress();

		if (commerceAddress != null) {
			shippingAddress.setCity(commerceAddress.getCity());

			Country country = commerceAddress.getCountry();

			if (country != null) {
				shippingAddress.setCountryCode(country.getA2());
				shippingAddress.setLine1(commerceAddress.getStreet1());
				shippingAddress.setLine2(commerceAddress.getStreet2());
				shippingAddress.setPostalCode(commerceAddress.getZip());
				shippingAddress.setRecipientName(commerceAddress.getName());
			}

			Region region = commerceAddress.getRegion();

			if (region != null) {
				shippingAddress.setState(region.getRegionCode());
			}
		}

		return shippingAddress;
	}

	private Plan _updatePlan(APIContext apiContext, Plan plan)
		throws PayPalRESTException {

		Patch patch = new Patch();

		patch.setOp(PayPalCommercePaymentMethodConstants.OPERATION_REPLACE);
		patch.setPath(StringPool.FORWARD_SLASH);
		patch.setValue(
			Collections.singletonMap(
				PayPalCommercePaymentMethodConstants.STATE,
				PayPalCommercePaymentMethodConstants.ACTIVE));

		plan.update(apiContext, Collections.singletonList(patch));

		return plan;
	}

	private static final String _DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss'Z'";

	private static final Log _log = LogFactoryUtil.getLog(
		PayPalCommercePaymentMethod.class);

	private static final DecimalFormat _payPalDecimalFormat = new DecimalFormat(
		"#,###.##");
	private static final TimeZone _timeZone = TimeZone.getTimeZone("UTC");

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

}