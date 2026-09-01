/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adyen;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.util.HMACValidator;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Crescenzo Rega
 */
@RequestMapping("/notifications")
@RestController
public class NotificationsRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@RequestHeader Map<String, String> headers, @RequestBody String json) {

		try {
			NotificationRequest notificationRequest =
				NotificationRequest.fromJson(json);

			List<NotificationRequestItem> notificationRequestItems =
				notificationRequest.getNotificationItems();

			if (notificationRequestItems.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.ACCEPTED);
			}

			NotificationRequestItem notificationRequestItem =
				notificationRequestItems.get(0);

			String externalReferenceCode = _getExternalReferenceCode(
				notificationRequestItem);

			JSONObject n1a0AdyenWebhookJSONObject = new JSONObject(
				get(
					_liferayOAuth2AccessTokenManager.getAuthorization(
						"liferay-adyen-commerce-payment-integration-oauth-" +
							"application-headless-server"),
					UriComponentsBuilder.fromPath(
						"/o/c/n1a0adyenwebhooks/by-external-reference-code/" +
							externalReferenceCode
					).build(
					).toUri()));

			if (!_hasAuthentication(
					headers.get("authorization"), n1a0AdyenWebhookJSONObject)) {

				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}

			HMACValidator hmacValidator = new HMACValidator();

			if (!hmacValidator.validateHMAC(
					notificationRequestItem,
					n1a0AdyenWebhookJSONObject.getString("hmacSignature"))) {

				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			String paymentId = null;
			String errorMessages = null;
			String paymentStatus = "4";

			if (StringUtils.equalsAny(
					notificationRequestItem.getEventCode(), "AUTHORISATION",
					"CAPTURE")) {

				if (notificationRequestItem.isSuccess()) {
					paymentId = notificationRequestItem.getMerchantReference();
					paymentStatus = "0";
				}
				else {
					errorMessages = notificationRequestItem.getReason();
				}
			}
			else if (StringUtils.equals(
						notificationRequestItem.getEventCode(),
						"CANCELLATION")) {

				if (notificationRequestItem.isSuccess()) {
					paymentStatus = "8";
				}
				else {
					errorMessages = notificationRequestItem.getReason();
				}
			}
			else if (StringUtils.equals(
						notificationRequestItem.getEventCode(), "REFUND")) {

				if (notificationRequestItem.isSuccess()) {
					paymentId = _getPaymentId(notificationRequestItem);
					paymentStatus = "17";
				}
				else {
					errorMessages = notificationRequestItem.getReason();
				}
			}

			if (StringUtils.isNotBlank(paymentId)) {
				_updatePayment(errorMessages, json, paymentId, paymentStatus);

				delete(
					_liferayOAuth2AccessTokenManager.getAuthorization(
						"liferay-adyen-commerce-payment-integration-oauth-" +
							"application-headless-server"),
					"",
					UriComponentsBuilder.fromPath(
						"/o/c/n1a0adyenwebhooks/by-external-reference-code/" +
							externalReferenceCode
					).build(
					).toUri());
			}
		}
		catch (Exception exception) {
			_log.error(ExceptionUtils.getStackTrace(exception));

			return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
		}

		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	private String _getExternalReferenceCode(
		NotificationRequestItem notificationRequestItem) {

		if (StringUtils.equalsAny(
				notificationRequestItem.getEventCode(), "AUTHORISATION",
				"CAPTURE")) {

			Map<String, String> additionalData =
				notificationRequestItem.getAdditionalData();

			return additionalData.get("paymentLinkId");
		}

		if (StringUtils.equals(
				notificationRequestItem.getEventCode(), "REFUND")) {

			return notificationRequestItem.getOriginalReference();
		}

		return null;
	}

	private String _getPaymentId(
		NotificationRequestItem notificationRequestItem) {

		JSONObject paymentsJSONObject = new JSONObject(
			get(
				_liferayOAuth2AccessTokenManager.getAuthorization(
					"liferay-adyen-commerce-payment-integration-oauth-" +
						"application-headless-server"),
				UriComponentsBuilder.fromPath(
					"/o/headless-commerce-admin-payment/v1.0/payments"
				).queryParam(
					"filter",
					"relatedItemId eq " +
						notificationRequestItem.getMerchantReference()
				).build(
				).toUri()));

		JSONArray itemsJSONArray = paymentsJSONObject.getJSONArray("items");

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

			String payload = itemJSONObject.getString("payload");

			if (StringUtils.contains(
					payload, notificationRequestItem.getOriginalReference())) {

				return String.valueOf(itemJSONObject.getInt("id"));
			}
		}

		return null;
	}

	private boolean _hasAuthentication(
		String authorization, JSONObject n1a0AdyenWebhookJSONObject) {

		if (StringUtils.isBlank(authorization) &&
			!StringUtils.contains(authorization, "Basic")) {

			return false;
		}

		Base64.Decoder decoder = Base64.getDecoder();

		String[] authorizationParts = new String(
			decoder.decode(
				authorization.substring(
					"Basic".length()
				).trim()),
			StandardCharsets.UTF_8
		).split(
			":", 2
		);

		String webhookPassword = authorizationParts[1];
		String webhookUserName = authorizationParts[0];

		String n1a0AdyenWebhookPassword = n1a0AdyenWebhookJSONObject.getString(
			"webhookPassword");

		if (MessageDigest.isEqual(
				webhookPassword.getBytes(StandardCharsets.UTF_8),
				n1a0AdyenWebhookPassword.getBytes(StandardCharsets.UTF_8)) &&
			webhookUserName.equals(
				n1a0AdyenWebhookJSONObject.getString("webhookUsername"))) {

			return true;
		}

		return false;
	}

	private void _updatePayment(
		String errorMessages, String json, String paymentId,
		String paymentStatus) {

		patch(
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"liferay-adyen-commerce-payment-integration-oauth-" +
					"application-headless-server"),
			new JSONObject(
			).put(
				"errorMessages", errorMessages
			).put(
				"payload",
				json.replaceAll(
					"\\n", ""
				).replaceAll(
					"\\s", ""
				)
			).put(
				"paymentStatus", paymentStatus
			).toString(),
			UriComponentsBuilder.fromPath(
				"/o/headless-commerce-admin-payment/v1.0/payments/" + paymentId
			).build(
			).toUri());
	}

	private static final Log _log = LogFactory.getLog(
		NotificationsRestController.class);

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}