/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.paypal.internal;

import com.google.gson.Gson;

import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.constants.CommercePaymentIntegrationConstants;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.method.paypal.internal.configuration.PayPalGroupServiceConfiguration;
import com.liferay.commerce.payment.method.paypal.internal.constants.PayPalCommercePaymentMethodConstants;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpRequest;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.AddressPortable;
import com.paypal.orders.AmountBreakdown;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Authorization;
import com.paypal.orders.AuthorizeRequest;
import com.paypal.orders.Item;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Money;
import com.paypal.orders.Name;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersAuthorizeRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.Payee;
import com.paypal.orders.PaymentCollection;
import com.paypal.orders.PurchaseUnit;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.orders.ShippingDetail;
import com.paypal.payments.AuthorizationsCaptureRequest;
import com.paypal.payments.AuthorizationsVoidRequest;
import com.paypal.payments.Capture;
import com.paypal.payments.CaptureRequest;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Crescenzo Rega
 */
@Component(service = CommercePaymentIntegration.class)
public class PayPalCommercePaymentIntegration
	implements CommercePaymentIntegration {

	public static final String KEY = "paypal-integration";

	@Override
	public CommercePaymentEntry authorize(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_FAILED);
		commercePaymentEntry.setRedirectURL(null);

		try {
			PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(
				commercePaymentEntry);

			OrdersAuthorizeRequest ordersAuthorizeRequest =
				new OrdersAuthorizeRequest(
					commercePaymentEntry.getTransactionCode()
				).prefer(
					"return=representation"
				).requestBody(
					new AuthorizeRequest()
				);

			ordersAuthorizeRequest.header(
				PayPalCommercePaymentMethodConstants.
					PAYPAL_PARTNER_ATTRIBUTION_ID,
				"Liferay_SP_PPCP_API");

			_debug(ordersAuthorizeRequest);

			HttpResponse<Order> httpResponse = payPalHttpClient.execute(
				ordersAuthorizeRequest);

			Authorization authorization = _getAuthorization(httpResponse);

			if (authorization != null) {
				commercePaymentEntry.setPaymentStatus(
					CommercePaymentEntryConstants.STATUS_AUTHORIZED);
				commercePaymentEntry.setTransactionCode(authorization.id());
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);

			commercePaymentEntry.setErrorMessages(
				_getErrorMessages(
					new JSONObject(ioException), StringPool.BLANK));
		}

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry cancel(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		try {
			PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(
				commercePaymentEntry);

			AuthorizationsVoidRequest authorizationsVoidRequest =
				new AuthorizationsVoidRequest(
					commercePaymentEntry.getTransactionCode());

			_debug(authorizationsVoidRequest);

			HttpResponse<Void> httpResponse = payPalHttpClient.execute(
				authorizationsVoidRequest);

			if (httpResponse.statusCode() == 204) {
				commercePaymentEntry.setPaymentStatus(
					CommercePaymentEntryConstants.STATUS_CANCELLED);
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);

			commercePaymentEntry.setErrorMessages(
				_getErrorMessages(ioException, StringPool.BLANK));
		}

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry capture(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_FAILED);

		try {
			PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(
				commercePaymentEntry);

			AuthorizationsCaptureRequest authorizationsCaptureRequest =
				new AuthorizationsCaptureRequest(
					commercePaymentEntry.getTransactionCode()
				).payPalRequestId(
					String.valueOf(
						commercePaymentEntry.getCommercePaymentEntryId())
				).requestBody(
					new CaptureRequest()
				);

			authorizationsCaptureRequest.header(
				PayPalCommercePaymentMethodConstants.
					PAYPAL_PARTNER_ATTRIBUTION_ID,
				"Liferay_SP_PPCP_API");

			_debug(authorizationsCaptureRequest);

			HttpResponse<Capture> httpResponse = payPalHttpClient.execute(
				authorizationsCaptureRequest);

			if (httpResponse.statusCode() == 201) {
				Capture capture = httpResponse.result();

				commercePaymentEntry.setPaymentStatus(
					CommercePaymentEntryConstants.STATUS_COMPLETED);
				commercePaymentEntry.setTransactionCode(capture.id());
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);

			commercePaymentEntry.setErrorMessages(
				_getErrorMessages(ioException, StringPool.BLANK));
		}

		return commercePaymentEntry;
	}

	@Override
	public String getDescription(Locale locale) {
		return _getResource(locale, "paypal-description");
	}

	@Override
	public String getKey() {
		return "paypal-integration";
	}

	@Override
	public String getName(Locale locale) {
		return "PayPal";
	}

	@Override
	public int getPaymentIntegrationType() {
		return CommercePaymentIntegrationConstants.
			TYPE_INTERNAL_ONLINE_REDIRECT;
	}

	@Override
	public CommercePaymentEntry refund(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_FAILED);

		try {
			PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(
				commercePaymentEntry);

			CapturesRefundRequest capturesRefundRequest =
				new CapturesRefundRequest(
					commercePaymentEntry.getTransactionCode()
				).prefer(
					"return=representation"
				).requestBody(
					new RefundRequest(
					).amount(
						new com.paypal.payments.Money(
						).currencyCode(
							commercePaymentEntry.getCurrencyCode()
						).value(
							_toScaledString(
								_commerceCurrencyLocalService.
									getCommerceCurrency(
										commercePaymentEntry.getCompanyId(),
										commercePaymentEntry.getCurrencyCode()),
								commercePaymentEntry.getAmount())
						)
					)
				);

			_debug(capturesRefundRequest);

			HttpResponse<Refund> httpResponse = payPalHttpClient.execute(
				capturesRefundRequest);

			if (httpResponse.statusCode() == 201) {
				commercePaymentEntry.setPaymentStatus(
					CommerceOrderPaymentConstants.STATUS_REFUNDED);

				Refund refund = httpResponse.result();

				commercePaymentEntry.setTransactionCode(refund.id());
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);

			commercePaymentEntry.setErrorMessages(
				_getErrorMessages(ioException, StringPool.BLANK));
		}

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry setUpPayment(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		commercePaymentEntry.setPaymentStatus(
			CommercePaymentEntryConstants.STATUS_FAILED);

		try {
			PayPalHttpClient payPalHttpClient = _getPayPalHttpClient(
				commercePaymentEntry);

			OrderRequest orderRequest = new OrderRequest(
			).applicationContext(
				new ApplicationContext(
				).cancelUrl(
					_getApplicationContextURL(
						commercePaymentEntry, httpServletRequest,
						"&cancel=true", commercePaymentEntry.getCancelURL())
				).returnUrl(
					_getApplicationContextURL(
						commercePaymentEntry, httpServletRequest,
						"&orderType=normal",
						commercePaymentEntry.getCallbackURL())
				).shippingPreference(
					_getShippingPreference(commercePaymentEntry.getClassPK())
				).userAction(
					PayPalCommercePaymentMethodConstants.USER_ACTION_PAY_NOW
				)
			).checkoutPaymentIntent(
				PayPalCommercePaymentMethodConstants.INTENT_AUTHORIZE
			);

			if (StringUtils.equals(
					commercePaymentEntry.getClassName(),
					CommerceOrder.class.getName())) {

				orderRequest.purchaseUnits(
					Collections.singletonList(
						_getCommerceOrderPurchaseUnitRequest(
							commercePaymentEntry)));
			}
			else {
				orderRequest.purchaseUnits(
					Collections.singletonList(
						_getDefaultPurchaseUnitRequest(commercePaymentEntry)));
			}

			OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest(
			).prefer(
				"return=representation"
			).requestBody(
				orderRequest
			).payPalPartnerAttributionId(
				"Liferay_SP_PPCP_API"
			);

			_debug(ordersCreateRequest);

			HttpResponse<Order> httpResponse = payPalHttpClient.execute(
				ordersCreateRequest);

			if (httpResponse.statusCode() == 201) {
				Order order = httpResponse.result();

				for (LinkDescription linkDescription : order.links()) {
					if (Objects.equals(
							PayPalCommercePaymentMethodConstants.APPROVE_URL,
							linkDescription.rel())) {

						commercePaymentEntry.setPaymentStatus(
							CommercePaymentEntryConstants.STATUS_CREATED);
						commercePaymentEntry.setRedirectURL(
							linkDescription.href());

						break;
					}
				}

				commercePaymentEntry.setTransactionCode(order.id());
			}
		}
		catch (IOException ioException) {
			_log.error(ioException);

			commercePaymentEntry.setErrorMessages(
				_getErrorMessages(ioException, StringPool.BLANK));
		}

		return commercePaymentEntry;
	}

	private void _debug(HttpRequest httpRequest) {
		if (!_log.isDebugEnabled()) {
			return;
		}

		Class<?> clazz = httpRequest.getClass();

		_log.debug(clazz.getName());

		Gson gson = new Gson();

		_log.debug("Headers: " + gson.toJson(httpRequest.headers()));
		_log.debug("Request body: " + gson.toJson(httpRequest.requestBody()));
	}

	private String _getApplicationContextURL(
		CommercePaymentEntry commercePaymentEntry,
		HttpServletRequest httpServletRequest, String queryString,
		String redirect) {

		StringBundler sb = new StringBundler(13);

		sb.append(_portal.getPortalURL(httpServletRequest));
		sb.append(_portal.getPathModule());
		sb.append(CharPool.SLASH);
		sb.append(CommercePaymentMethodConstants.SERVLET_PATH);
		sb.append(CharPool.QUESTION);

		if (Validator.isNotNull(redirect)) {
			sb.append("redirect=");
			sb.append(URLCodec.encodeURL(redirect));
			sb.append(CharPool.AMPERSAND);
		}

		sb.append("entryId=");
		sb.append(commercePaymentEntry.getCommercePaymentEntryId());
		sb.append("&entryKey=");
		sb.append(KEY);
		sb.append(queryString);

		return sb.toString();
	}

	private Authorization _getAuthorization(HttpResponse<Order> httpResponse) {
		if (httpResponse.statusCode() != 201) {
			return null;
		}

		Order order = httpResponse.result();

		List<PurchaseUnit> purchaseUnits = order.purchaseUnits();

		if (ListUtil.isEmpty(purchaseUnits)) {
			return null;
		}

		PurchaseUnit purchaseUnit = purchaseUnits.get(0);

		if (purchaseUnit == null) {
			return null;
		}

		PaymentCollection paymentCollection = purchaseUnit.payments();

		if (paymentCollection == null) {
			return null;
		}

		List<Authorization> authorizations = paymentCollection.authorizations();

		if (ListUtil.isEmpty(authorizations)) {
			return null;
		}

		return authorizations.get(0);
	}

	private PurchaseUnitRequest _getCommerceOrderPurchaseUnitRequest(
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commercePaymentEntry.getClassPK());

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		Locale locale = LocaleUtil.fromLanguageId(
			commercePaymentEntry.getLanguageId());
		PayPalGroupServiceConfiguration payPalGroupServiceConfiguration =
			_getPayPalGroupServiceConfiguration(commerceOrder.getGroupId());

		PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest(
		).amountWithBreakdown(
			new AmountWithBreakdown(
			).amountBreakdown(
				new AmountBreakdown(
				).itemTotal(
					_toMoney(commerceCurrency, commerceOrder.getSubtotal())
				).shipping(
					_toMoney(
						commerceCurrency, commerceOrder.getShippingAmount())
				).taxTotal(
					_toMoney(commerceCurrency, commerceOrder.getTaxAmount())
				)
			).currencyCode(
				commerceCurrency.getCode()
			).value(
				_toScaledString(commerceCurrency, commerceOrder.getTotal())
			)
		).description(
			"Payment: " + commercePaymentEntry.getCommercePaymentEntryId()
		).items(
			TransformUtil.transform(
				commerceOrder.getCommerceOrderItems(),
				commerceOrderItem -> {
					BigDecimal finalPrice = commerceOrderItem.getFinalPrice();
					BigDecimal quantity = commerceOrderItem.getQuantity();

					BigDecimal unitAmount = finalPrice.divide(quantity);

					return new Item(
					).name(
						commerceOrderItem.getName(locale)
					).quantity(
						quantity.stripTrailingZeros(
						).toPlainString()
					).sku(
						commerceOrderItem.getSku()
					).unitAmount(
						_toMoney(commerceCurrency, unitAmount)
					);
				})
		).payee(
			new Payee(
			).merchantId(
				payPalGroupServiceConfiguration.merchantId()
			)
		).referenceId(
			String.valueOf(commercePaymentEntry.getCommercePaymentEntryId())
		);

		if (commerceOrder.isShippable()) {
			purchaseUnitRequest.shippingDetail(
				_toShippingDetail(commerceOrder.getShippingAddress()));
		}

		return purchaseUnitRequest;
	}

	private PurchaseUnitRequest _getDefaultPurchaseUnitRequest(
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				commercePaymentEntry.getCompanyId(),
				commercePaymentEntry.getCurrencyCode());
		PayPalGroupServiceConfiguration payPalGroupServiceConfiguration =
			_getPayPalGroupServiceConfiguration(commercePaymentEntry);

		return new PurchaseUnitRequest(
		).amountWithBreakdown(
			new AmountWithBreakdown(
			).currencyCode(
				commerceCurrency.getCode()
			).value(
				_toScaledString(
					commerceCurrency, commercePaymentEntry.getAmount())
			)
		).description(
			"Payment: " + commercePaymentEntry.getCommercePaymentEntryId()
		).payee(
			new Payee(
			).merchantId(
				payPalGroupServiceConfiguration.merchantId()
			)
		).referenceId(
			String.valueOf(commercePaymentEntry.getCommercePaymentEntryId())
		);
	}

	private String _getErrorMessages(IOException ioException, String prefix) {
		HttpException httpException = (HttpException)ioException;

		JSONObject jsonObject = new JSONObject(httpException.getMessage());

		return _getErrorMessages(jsonObject, prefix);
	}

	private String _getErrorMessages(JSONObject jsonObject, String prefix) {
		StringBuilder stringBuilder = new StringBuilder();

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

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

		return stringBuilder.toString();
	}

	private PayPalGroupServiceConfiguration _getPayPalGroupServiceConfiguration(
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(
				commercePaymentEntry.getCommerceChannelId());

		return _getPayPalGroupServiceConfiguration(
			commerceChannel.getGroupId());
	}

	private PayPalGroupServiceConfiguration _getPayPalGroupServiceConfiguration(
			long groupId)
		throws PortalException {

		return _configurationProvider.getConfiguration(
			PayPalGroupServiceConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId,
				PayPalCommercePaymentMethodConstants.
					COMMERCE_PAYMENT_INTEGRATION_SERVICE_NAME));
	}

	private PayPalHttpClient _getPayPalHttpClient(
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		PayPalGroupServiceConfiguration payPalGroupServiceConfiguration =
			_getPayPalGroupServiceConfiguration(commercePaymentEntry);

		if (Objects.equals(
				payPalGroupServiceConfiguration.mode(),
				PayPalCommercePaymentMethodConstants.MODE_LIVE)) {

			return new PayPalHttpClient(
				new PayPalEnvironment.Live(
					payPalGroupServiceConfiguration.clientId(),
					payPalGroupServiceConfiguration.clientSecret()));
		}

		return new PayPalHttpClient(
			new PayPalEnvironment.Sandbox(
				payPalGroupServiceConfiguration.clientId(),
				payPalGroupServiceConfiguration.clientSecret()));
	}

	private String _getRegionCode(Region region) {
		if (region == null) {
			return null;
		}

		return region.getRegionCode();
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

	private String _getShippingPreference(long commerceOrderId) {
		CommerceOrder commerceOrder =
			_commerceOrderLocalService.fetchCommerceOrder(commerceOrderId);

		if ((commerceOrder != null) && commerceOrder.isShippable()) {
			return PayPalCommercePaymentMethodConstants.
				SHIPPING_PREFERENCE_PROVIDED;
		}

		return PayPalCommercePaymentMethodConstants.
			SHIPPING_PREFERENCE_NO_SHIPPING;
	}

	private Money _toMoney(
		CommerceCurrency commerceCurrency, BigDecimal value) {

		return new Money(
		).currencyCode(
			commerceCurrency.getCode()
		).value(
			_toScaledString(commerceCurrency, value)
		);
	}

	private String _toScaledString(
		CommerceCurrency commerceCurrency, BigDecimal value) {

		BigDecimal scaledValue = value.setScale(
			commerceCurrency.getMaxFractionDigits(),
			RoundingMode.valueOf(commerceCurrency.getRoundingMode()));

		return scaledValue.toPlainString();
	}

	private ShippingDetail _toShippingDetail(
			CommerceAddress shippingCommerceAddress)
		throws PortalException {

		if (shippingCommerceAddress == null) {
			return null;
		}

		Country country = shippingCommerceAddress.getCountry();

		if (country == null) {
			return null;
		}

		return new ShippingDetail(
		).addressPortable(
			new AddressPortable(
			).addressLine1(
				shippingCommerceAddress.getStreet1()
			).addressLine2(
				shippingCommerceAddress.getStreet2()
			).adminArea1(
				_getRegionCode(shippingCommerceAddress.getRegion())
			).adminArea2(
				shippingCommerceAddress.getCity()
			).countryCode(
				country.getA2()
			).postalCode(
				shippingCommerceAddress.getZip()
			)
		).name(
			new Name(
			).fullName(
				shippingCommerceAddress.getName()
			)
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PayPalCommercePaymentIntegration.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}