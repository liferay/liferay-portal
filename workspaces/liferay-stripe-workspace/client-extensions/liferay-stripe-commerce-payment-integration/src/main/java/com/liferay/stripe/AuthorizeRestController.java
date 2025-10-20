/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.stripe;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.petra.string.StringBundler;

import com.stripe.Stripe;
import com.stripe.model.TaxRate;
import com.stripe.model.checkout.Session;
import com.stripe.param.TaxRateCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Crescenzo Rega
 */
@RequestMapping("/authorize")
@RestController
public class AuthorizeRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		log(jwt, _log, json);

		String errorMessages = null;
		String paymentStatus = "4";
		String redirectURL = null;
		String transactionCode = null;

		try {
			JSONObject jsonObject = new JSONObject(json);

			JSONObject typeSettingsJSONObject = jsonObject.getJSONObject(
				"typeSettings");

			Stripe.apiKey = typeSettingsJSONObject.getString("apiKey");

			JSONObject commercePaymentEntryJSONObject =
				jsonObject.getJSONObject("commercePaymentEntry");

			Session session = _createSession(
				commercePaymentEntryJSONObject, jwt);

			if (Objects.equals(session.getStatus(), "open")) {
				paymentStatus = "2";
				redirectURL = session.getUrl();
				transactionCode = session.getId();
			}
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
				"paymentStatus", paymentStatus
			).put(
				"redirectURL", redirectURL
			).put(
				"transactionCode", transactionCode
			).toString(),
			HttpStatus.OK);
	}

	private Session _createSession(JSONObject jsonObject, Jwt jwt)
		throws Exception {

		SessionCreateParams sessionCreateParams = null;

		if (Objects.equals(
				jsonObject.getString("className"),
				"com.liferay.commerce.model.CommerceOrder")) {

			JSONObject orderJSONObject = new JSONObject(
				Objects.requireNonNull(
					get(
						"Bearer " + jwt.getTokenValue(),
						UriComponentsBuilder.fromPath(
							"/o/headless-commerce-admin-order/v1.0/orders/" +
								jsonObject.getLong("classPK")
						).queryParam(
							"nestedFields", "orderItems"
						).build(
						).toUri())));

			SessionCreateParams.Builder builder = SessionCreateParams.builder(
			).addAllLineItem(
				_getLineItems(
					orderJSONObject.getString("currencyCode"),
					jsonObject.getString("languageId"),
					orderJSONObject.getJSONArray("orderItems"))
			).addPaymentMethodType(
				SessionCreateParams.PaymentMethodType.CARD
			).setCancelUrl(
				jsonObject.getString("cancelURL")
			).setCurrency(
				orderJSONObject.getString("currencyCode")
			).setMode(
				SessionCreateParams.Mode.PAYMENT
			).setSuccessUrl(
				jsonObject.getString("callbackURL")
			);

			if (orderJSONObject.getBoolean("shippable")) {
				builder.addShippingOption(
					_getShippingOption(
						orderJSONObject.getString("currencyCode"),
						orderJSONObject.getLong("shippingAmountValue"),
						orderJSONObject.getString("shippingOption")));
			}

			sessionCreateParams = builder.build();
		}
		else {
			sessionCreateParams = SessionCreateParams.builder(
			).addAllLineItem(
				_getLineItems(jsonObject)
			).addPaymentMethodType(
				SessionCreateParams.PaymentMethodType.CARD
			).setCancelUrl(
				jsonObject.getString("cancelURL")
			).setCurrency(
				jsonObject.getString("currencyCode")
			).setMode(
				SessionCreateParams.Mode.PAYMENT
			).setSuccessUrl(
				jsonObject.getString("callbackURL")
			).build();
		}

		return Session.create(sessionCreateParams);
	}

	private TaxRate _createTaxRate(String name, BigDecimal taxPercentage)
		throws Exception {

		return TaxRate.create(
			TaxRateCreateParams.builder(
			).setDisplayName(
				name
			).setPercentage(
				taxPercentage
			).setInclusive(
				true
			).build());
	}

	private List<SessionCreateParams.LineItem> _getLineItems(
		JSONObject jsonObject) {

		return Collections.singletonList(
			SessionCreateParams.LineItem.builder(
			).setPriceData(
				SessionCreateParams.LineItem.PriceData.builder(
				).setCurrency(
					jsonObject.getString("currencyCode")
				).setProductData(
					SessionCreateParams.LineItem.PriceData.ProductData.builder(
					).setName(
						StringBundler.concat(
							jsonObject.getString("classNameLabel"), " ",
							jsonObject.getString("classPK"))
					).build()
				).setUnitAmount(
					BigDecimal.valueOf(
						jsonObject.getDouble("amount")
					).multiply(
						BigDecimal.valueOf(100)
					).longValue()
				).build()
			).setQuantity(
				BigDecimal.ONE.longValue()
			).build());
	}

	private List<SessionCreateParams.LineItem> _getLineItems(
			String currencyCode, String languageId,
			JSONArray orderItemsJSONArray)
		throws Exception {

		List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

		for (int i = 0; i < orderItemsJSONArray.length(); i++) {
			JSONObject orderItemJSONObject = orderItemsJSONArray.getJSONObject(
				i);

			BigDecimal finalPrice = BigDecimal.valueOf(
				orderItemJSONObject.getDouble("finalPrice"));
			BigDecimal finalPriceWithTaxAmount = BigDecimal.valueOf(
				orderItemJSONObject.getDouble("finalPriceWithTaxAmount"));

			BigDecimal taxPercentage = finalPriceWithTaxAmount.subtract(
				finalPrice
			).divide(
				finalPrice, 4, RoundingMode.HALF_DOWN
			).multiply(
				BigDecimal.valueOf(100)
			);

			String name = orderItemJSONObject.getJSONObject(
				"name"
			).getString(
				languageId
			);

			TaxRate taxRate = _createTaxRate(name, taxPercentage);

			long quantity = orderItemJSONObject.getLong("quantity");

			SessionCreateParams.LineItem lineItem =
				SessionCreateParams.LineItem.builder(
				).addTaxRate(
					taxRate.getId()
				).setPriceData(
					SessionCreateParams.LineItem.PriceData.builder(
					).setCurrency(
						currencyCode
					).setProductData(
						SessionCreateParams.LineItem.PriceData.ProductData.
							builder(
							).setName(
								name
							).build()
					).setUnitAmount(
						finalPriceWithTaxAmount.divide(
							BigDecimal.valueOf(quantity)
						).multiply(
							BigDecimal.valueOf(100)
						).longValue()
					).build()
				).setQuantity(
					quantity
				).build();

			lineItems.add(lineItem);
		}

		return lineItems;
	}

	private SessionCreateParams.ShippingOption _getShippingOption(
		String currencyCode, Long shippingAmountValue, String shippingOption) {

		return SessionCreateParams.ShippingOption.builder(
		).setShippingRateData(
			SessionCreateParams.ShippingOption.ShippingRateData.builder(
			).setDisplayName(
				shippingOption
			).setFixedAmount(
				SessionCreateParams.ShippingOption.ShippingRateData.FixedAmount.
					builder(
					).setAmount(
						BigDecimal.valueOf(
							shippingAmountValue
						).multiply(
							BigDecimal.valueOf(100)
						).longValue()
					).setCurrency(
						currencyCode
					).build()
			).setType(
				SessionCreateParams.ShippingOption.ShippingRateData.Type.
					FIXED_AMOUNT
			).build()
		).build();
	}

	private static final Log _log = LogFactory.getLog(
		AuthorizeRestController.class);

}