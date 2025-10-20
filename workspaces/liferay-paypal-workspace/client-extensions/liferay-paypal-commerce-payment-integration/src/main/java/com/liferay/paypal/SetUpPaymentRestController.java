/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.paypal;

import com.liferay.petra.function.RetryableUnsafeSupplier;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.math.BigDecimal;

import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Brian I. Kim
 */
@RequestMapping("/set-up-payment")
@RestController
public class SetUpPaymentRestController extends BaseRestController {

	@GetMapping("get-google-environment/{clientId}")
	public ResponseEntity<String> getGoogleEnvironment(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable("clientId") String clientId) {

		String mode = "TEST";

		if (StringUtils.equals(clientId.substring(0, 3), "li_")) {
			mode = "PRODUCTION";
		}

		return new ResponseEntity<>(
			new JSONObject(
			).put(
				"mode", mode
			).toString(),
			HttpStatus.OK);
	}

	@GetMapping("get-google-order/{orderId}/{countryCode}")
	public ResponseEntity<String> getGoogleOrder(
		@AuthenticationPrincipal Jwt jwt, @PathVariable("orderId") long orderId,
		@PathVariable("countryCode") String countryCode) {

		JSONObject orderJSONObject = _getOrderJSONObject(jwt, orderId);

		return new ResponseEntity<>(
			new JSONObject(
			).put(
				"countryCode", countryCode
			).put(
				"currencyCode", orderJSONObject.getString("currencyCode")
			).put(
				"displayItems",
				new JSONArray(
				).put(
					new JSONObject(
					).put(
						"label", "Subtotal"
					).put(
						"price",
						BigDecimal.valueOf(
							orderJSONObject.getDouble("subtotalAmount")
						).toString()
					).put(
						"type", "SUBTOTAL"
					)
				).put(
					new JSONObject(
					).put(
						"label", "Tax"
					).put(
						"price",
						BigDecimal.valueOf(
							orderJSONObject.getDouble("taxAmount")
						).toString()
					).put(
						"type", "TAX"
					)
				)
			).put(
				"totalPrice",
				BigDecimal.valueOf(
					orderJSONObject.getDouble("totalAmount")
				).toString()
			).put(
				"totalPriceLabel", "Total"
			).put(
				"totalPriceStatus", "FINAL"
			).toString(),
			HttpStatus.OK);
	}

	@GetMapping("get-paypal-order/{orderId}")
	public ResponseEntity<String> getPayPalOrder(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable("orderId") long orderId) {

		UnsafeSupplier<String, RuntimeException> unsafeSupplier =
			new RetryableUnsafeSupplier<>(
				(exception, maxRetries, retryCount) -> {
				},
				() -> get(
					"Bearer " + jwt.getTokenValue(),
					UriComponentsBuilder.fromPath(
						"/o/c/b9k3paypaltransactions" +
							"/by-external-reference-code/" + orderId
					).build(
					).toUri()));

		String transactionCode = new JSONObject(
			unsafeSupplier.get()
		).getString(
			"transactionCode"
		);

		if (StringUtils.isNotBlank(transactionCode)) {
			delete(
				"Bearer " + jwt.getTokenValue(), StringPool.BLANK,
				UriComponentsBuilder.fromPath(
					"/o/c/b9k3paypaltransactions/by-external-reference-code/" +
						orderId
				).build(
				).toUri());
		}

		return new ResponseEntity<>(
			new JSONObject(
			).put(
				"id", transactionCode
			).toString(),
			HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		log(jwt, _log, json);

		String errorMessages = null;
		String payload = null;
		String paymentStatus = "4";
		String transactionCode = null;

		try {
			JSONObject jsonObject = new JSONObject(json);

			JSONObject commercePaymentEntryJSONObject =
				jsonObject.getJSONObject("commercePaymentEntry");
			JSONObject httpServletRequestParameterMapJSONObject =
				jsonObject.getJSONObject("httpServletRequestParameterMap");
			JSONObject typeSettingsJSONObject = jsonObject.getJSONObject(
				"typeSettings");

			JSONArray fundingSourceJSONArray =
				httpServletRequestParameterMapJSONObject.getJSONArray(
					"fundingSource");
			JSONObject orderJSONObject = _getOrderJSONObject(
				jwt, commercePaymentEntryJSONObject.getLong("classPK"));

			JSONObject ordersResponseJSONObject = new JSONObject(
				post(
					new JSONObject(
					).put(
						"intent", "CAPTURE"
					).put(
						"payment_source",
						_getPaymentSourceJSONObject(
							commercePaymentEntryJSONObject,
							String.valueOf(fundingSourceJSONArray.get(0)),
							orderJSONObject)
					).put(
						"purchase_units",
						_getPurchaseUnitJSONArray(
							commercePaymentEntryJSONObject,
							typeSettingsJSONObject.getString("merchantId"),
							orderJSONObject)
					).toString(),
					HashMapBuilder.put(
						HttpHeaders.AUTHORIZATION,
						"Bearer " + getAuthorization(typeSettingsJSONObject)
					).put(
						"PayPal-Partner-Attribution-Id", "Liferay_SP_PPCP_API"
					).put(
						"PayPal-Request-Id",
						commercePaymentEntryJSONObject.getString(
							"commercePaymentEntryId")
					).put(
						"Prefer", "return=representation"
					).build(),
					UriComponentsBuilder.fromUriString(
						getPayPalURL(typeSettingsJSONObject.getString("mode"))
					).path(
						"/v2/checkout/orders"
					).build(
					).toUri()));

			payload = ordersResponseJSONObject.toString();

			paymentStatus = "18";

			transactionCode = ordersResponseJSONObject.getString("id");

			post(
				"Bearer " + jwt.getTokenValue(),
				new JSONObject(
				).put(
					"externalReferenceCode",
					commercePaymentEntryJSONObject.getString("classPK")
				).put(
					"transactionCode", transactionCode
				).toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/b9k3paypaltransactions"
				).build(
				).toUri());

			post(
				"Bearer " + jwt.getTokenValue(),
				new JSONObject(
				).put(
					"clientId", typeSettingsJSONObject.getString("clientId")
				).put(
					"clientSecret",
					typeSettingsJSONObject.getString("clientSecret")
				).put(
					"externalReferenceCode", transactionCode
				).put(
					"mode", typeSettingsJSONObject.getString("mode")
				).put(
					"paymentEntryId",
					commercePaymentEntryJSONObject.getLong(
						"commercePaymentEntryId")
				).put(
					"webhookId", typeSettingsJSONObject.getString("webhookId")
				).toString(),
				UriComponentsBuilder.fromPath(
					"/o/c/b9k3paypalwebhooks"
				).build(
				).toUri());
		}
		catch (Exception exception) {
			errorMessages = ExceptionUtils.getStackTrace(exception);

			_log.error(errorMessages);
		}

		return new ResponseEntity<>(
			new JSONObject(
			).put(
				"errorMessages", errorMessages
			).put(
				"payload", payload
			).put(
				"paymentStatus", paymentStatus
			).put(
				"transactionCode", transactionCode
			).toString(),
			HttpStatus.OK);
	}

	private JSONObject _getAmountJSONObject(
		String currencyCode, JSONObject orderJSONObject) {

		return new JSONObject(
		).put(
			"breakdown",
			new JSONObject(
			).put(
				"item_total",
				new JSONObject(
				).put(
					"currency_code", currencyCode
				).put(
					"value",
					BigDecimal.valueOf(
						orderJSONObject.getDouble("subtotalAmount")
					).longValue()
				)
			).put(
				"shipping",
				new JSONObject(
				).put(
					"currency_code", currencyCode
				).put(
					"value",
					BigDecimal.valueOf(
						orderJSONObject.getDouble("shippingAmountValue")
					).longValue()
				)
			).put(
				"tax_total",
				new JSONObject(
				).put(
					"currency_code", currencyCode
				).put(
					"value",
					BigDecimal.valueOf(
						orderJSONObject.getDouble("taxAmount")
					).longValue()
				)
			)
		).put(
			"currency_code", currencyCode
		).put(
			"value",
			BigDecimal.valueOf(
				orderJSONObject.getDouble("totalAmount")
			).longValue()
		);
	}

	private JSONObject _getExperienceContextJSONObject(
		String callbackURL, String cancelURL, String fundingSource,
		boolean shippable) {

		JSONObject experienceContextJSONObject = new JSONObject();

		if (!Objects.equals(fundingSource, "apple_pay") &&
			!Objects.equals(fundingSource, "google_pay")) {

			if (shippable) {
				experienceContextJSONObject.put(
					"shipping_preference", "SET_PROVIDED_ADDRESS");
			}
			else {
				experienceContextJSONObject.put(
					"shipping_preference", "NO_SHIPPING");
			}
		}

		if (ArrayUtils.contains(_FUNDING_SOURCES, fundingSource)) {
			experienceContextJSONObject.put(
				"cancel_url", cancelURL
			).put(
				"return_url", callbackURL
			);
		}
		else if (Objects.equals(fundingSource, "paypal")) {
			experienceContextJSONObject.put(
				"cancel_url", cancelURL
			).put(
				"return_url", callbackURL
			).put(
				"user_action", "PAY_NOW"
			);
		}

		return experienceContextJSONObject;
	}

	private JSONArray _getItemsJSONArray(
		String currencyCode, String languageId, JSONObject orderJSONObject) {

		JSONArray itemsJSONArray = new JSONArray();

		JSONArray orderItemsJSONArray = orderJSONObject.getJSONArray(
			"orderItems");

		for (int i = 0; i < orderItemsJSONArray.length(); i++) {
			JSONObject orderItemJSONObject = orderItemsJSONArray.getJSONObject(
				i);

			BigDecimal quantity = BigDecimal.valueOf(
				orderItemJSONObject.getLong("quantity"));

			itemsJSONArray.put(
				new JSONObject(
				).put(
					"name",
					orderItemJSONObject.getJSONObject(
						"name"
					).getString(
						languageId
					)
				).put(
					"quantity",
					quantity.stripTrailingZeros(
					).toPlainString()
				).put(
					"sku", orderItemJSONObject.getString("sku")
				).put(
					"unit_amount",
					new JSONObject(
					).put(
						"currency_code", currencyCode
					).put(
						"value",
						BigDecimal.valueOf(
							orderItemJSONObject.getDouble("finalPrice")
						).divide(
							quantity
						).longValue()
					)
				));
		}

		return itemsJSONArray;
	}

	private JSONObject _getOrderJSONObject(Jwt jwt, long orderId) {
		return new JSONObject(
			get(
				"Bearer " + jwt.getTokenValue(),
				UriComponentsBuilder.fromPath(
					"/o/headless-commerce-admin-order/v1.0/orders/" + orderId
				).queryParam(
					"nestedFields", "billingAddress,orderItems,shippingAddress"
				).build(
				).toUri()));
	}

	private JSONObject _getPaymentSourceJSONObject(
		JSONObject commercePaymentEntryJSONObject, String fundingSource,
		JSONObject orderJSONObject) {

		return new JSONObject(
		).put(
			fundingSource,
			_getPayPalPaymentSourceJSONObject(
				commercePaymentEntryJSONObject, fundingSource, orderJSONObject)
		);
	}

	private JSONObject _getPayPalPaymentSourceJSONObject(
		JSONObject commercePaymentEntryJSONObject, String fundingSource,
		JSONObject orderJSONObject) {

		JSONObject billingAddressJSONObject = orderJSONObject.getJSONObject(
			"billingAddress");

		String countryCode = billingAddressJSONObject.getString(
			"countryISOCode");
		String name = billingAddressJSONObject.getString("name");

		boolean shippable = orderJSONObject.getBoolean("shippable");

		if (shippable) {
			JSONObject shippingAddressJSONObject =
				orderJSONObject.getJSONObject("shippingAddress");

			countryCode = shippingAddressJSONObject.getString("countryISOCode");
			name = shippingAddressJSONObject.getString("name");
		}

		JSONObject paymentSourceJSONObject = new JSONObject();

		if (ArrayUtils.contains(_FUNDING_SOURCES, fundingSource)) {
			paymentSourceJSONObject.put(
				"country_code", countryCode
			).put(
				"name", name
			);
		}

		return paymentSourceJSONObject.put(
			"experience_context",
			_getExperienceContextJSONObject(
				commercePaymentEntryJSONObject.getString("callbackURL"),
				commercePaymentEntryJSONObject.getString("cancelURL"),
				fundingSource, shippable));
	}

	private JSONArray _getPurchaseUnitJSONArray(
		JSONObject commercePaymentEntryJSONObject, String merchantId,
		JSONObject orderJSONObject) {

		JSONObject purchaseUnitJSONObject = new JSONObject();

		if (Objects.equals(
				commercePaymentEntryJSONObject.getString("className"),
				"com.liferay.commerce.model.CommerceOrder")) {

			purchaseUnitJSONObject.put(
				"amount",
				_getAmountJSONObject(
					commercePaymentEntryJSONObject.getString("currencyCode"),
					orderJSONObject)
			).put(
				"items",
				_getItemsJSONArray(
					commercePaymentEntryJSONObject.getString("currencyCode"),
					commercePaymentEntryJSONObject.getString("languageId"),
					orderJSONObject)
			);

			if (orderJSONObject.getBoolean("shippable")) {
				purchaseUnitJSONObject.put(
					"shipping",
					_getShippingJSONObject(
						orderJSONObject.getJSONObject("shippingAddress")));
			}
		}

		long commercePaymentEntryId = commercePaymentEntryJSONObject.getLong(
			"commercePaymentEntryId");

		return new JSONArray(
		).put(
			purchaseUnitJSONObject.put(
				"description", "Payment: " + commercePaymentEntryId
			).put(
				"payee",
				new JSONObject(
				).put(
					"merchant_id", merchantId
				)
			).put(
				"reference_id", commercePaymentEntryId
			)
		);
	}

	private JSONObject _getShippingJSONObject(
		JSONObject shippingAddressJSONObject) {

		return new JSONObject(
		).put(
			"address",
			new JSONObject(
			).put(
				"address_line_1", shippingAddressJSONObject.getString("street1")
			).put(
				"address_line_2", shippingAddressJSONObject.getString("street2")
			).put(
				"admin_area_1",
				shippingAddressJSONObject.getString("regionISOCode")
			).put(
				"admin_area_2", shippingAddressJSONObject.getString("city")
			).put(
				"country_code",
				shippingAddressJSONObject.getString("countryISOCode")
			).put(
				"postal_code", shippingAddressJSONObject.getString("zip")
			)
		).put(
			"name",
			new JSONObject(
			).put(
				"full_name", shippingAddressJSONObject.getString("name")
			)
		);
	}

	private static final String[] _FUNDING_SOURCES = {
		"bancontact", "eps", "giropay", "ideal", "mybank", "p24", "trustly"
	};

	private static final Log _log = LogFactory.getLog(
		SetUpPaymentRestController.class);

}