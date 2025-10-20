/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ups;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.ups.constants.UPSServiceCodeConstants;

import java.util.Base64;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Alessio Antonio Rendina
 */
@RequestMapping("/options")
@RestController
public class OptionsRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
			@AuthenticationPrincipal Jwt jwt, @RequestBody String json)
		throws Exception {

		return post(jwt, json, _log);
	}

	protected ResponseEntity<String> post(Jwt jwt, String json, Log log)
		throws Exception {

		log(jwt, log, json);

		JSONArray jsonArray = new JSONArray();

		JSONObject jsonObject = new JSONObject(json);

		double depth = 0;
		double height = 0;
		int quantity = 0;
		double weight = 0;
		double width = 0;

		JSONObject orderJSONObject = jsonObject.getJSONObject("order");

		JSONArray orderItemsJSONArray = orderJSONObject.getJSONArray(
			"orderItems");

		for (int i = 0; i < orderItemsJSONArray.length(); i++) {
			JSONObject orderItemJSONObject = orderItemsJSONArray.getJSONObject(
				i);

			JSONObject skuJSONObject = _get(
				"Bearer " + jwt.getTokenValue(),
				"/o/headless-commerce-admin-catalog/v1.0/skus/" +
					orderItemJSONObject.getString("skuId"));

			depth += skuJSONObject.getDouble("depth");
			height += skuJSONObject.getDouble("height");

			quantity += orderItemJSONObject.getInt("quantity");

			weight += skuJSONObject.getDouble("weight");
			width += skuJSONObject.getDouble("width");
		}

		JSONObject typeSettingsJSONObject = jsonObject.getJSONObject(
			"typeSettings");

		for (String code :
				StringUtil.split(
					typeSettingsJSONObject.getString("ratingCodes"))) {

			jsonArray.put(
				_postRate(
					typeSettingsJSONObject.getString("clientId"),
					typeSettingsJSONObject.getString("clientSecret"), code,
					depth, height, log, quantity,
					orderJSONObject.getJSONObject("shippingAddress"),
					typeSettingsJSONObject, weight, width));
		}

		return new ResponseEntity<>(
			new JSONObject(
			).put(
				"shippingOptions", _toShippingOptionsJSONArray(jsonArray)
			).toString(),
			HttpStatus.OK);
	}

	private JSONObject _get(String authorization, String path) {
		return new JSONObject(
			get(
				authorization,
				UriComponentsBuilder.fromPath(
					path
				).build(
				).toUri()));
	}

	private String _getAccessToken(
		String clientId, String clientSecret, Log log) {

		try {
			Base64.Encoder encoder = Base64.getEncoder();

			String credentials = clientId + ":" + clientSecret;

			JSONObject jsonObject = new JSONObject(
				post(
					BodyInserters.fromFormData(
						"grant_type", "client_credentials"
					).toString(),
					HashMapBuilder.put(
						HttpHeaders.AUTHORIZATION,
						"Basic " +
							encoder.encodeToString(credentials.getBytes())
					).put(
						HttpHeaders.CONTENT_TYPE,
						MediaType.APPLICATION_FORM_URLENCODED_VALUE
					).build(),
					UriComponentsBuilder.fromUriString(
						"https://wwwcie.ups.com/security/v1/oauth/token"
					).build(
					).toUri()));

			return jsonObject.getString("access_token");
		}
		catch (Exception exception) {
			if (log.isDebugEnabled()) {
				log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private JSONObject _postRate(
		String clientId, String clientSecret, String code, double depth,
		double height, Log log, int quantity,
		JSONObject shippingAddressJSONObject, JSONObject typeSettingsJSONObject,
		double weight, double width) {

		String body = new JSONObject(
		).put(
			"RateRequest",
			new JSONObject(
			).put(
				"Shipment",
				new JSONObject(
				).put(
					"NumOfPieces", String.valueOf(quantity)
				).put(
					"Package",
					new JSONObject(
					).put(
						"Dimensions",
						new JSONObject(
						).put(
							"Height", String.valueOf(height)
						).put(
							"Length", String.valueOf(depth)
						).put(
							"UnitOfMeasurement",
							new JSONObject(
							).put(
								"Code",
								typeSettingsJSONObject.getString(
									"dimensionsUnitOfMeasurementCode")
							)
						).put(
							"Width", String.valueOf(width)
						)
					).put(
						"PackageWeight",
						new JSONObject(
						).put(
							"UnitOfMeasurement",
							new JSONObject(
							).put(
								"Code",
								typeSettingsJSONObject.getString(
									"packageWeightUnitOfMeasurementCode")
							)
						).put(
							"Weight", String.valueOf(weight)
						)
					).put(
						"PackagingType",
						new JSONObject(
						).put(
							"Code",
							typeSettingsJSONObject.getString(
								"packagingTypeCode")
						)
					).put(
						"SimpleRate",
						new JSONObject(
						).put(
							"Code",
							typeSettingsJSONObject.getString("simpleRateCode")
						)
					)
				).put(
					"Service",
					new JSONObject(
					).put(
						"Code", code
					)
				).put(
					"Shipper",
					new JSONObject(
					).put(
						"Address",
						new JSONObject(
						).put(
							"AddressLine",
							new JSONArray(
							).put(
								typeSettingsJSONObject.getString(
									"shipperAddressLine1")
							).put(
								typeSettingsJSONObject.getString(
									"shipperAddressLine2")
							).put(
								typeSettingsJSONObject.getString(
									"shipperAddressLine3")
							)
						).put(
							"CountryCode",
							typeSettingsJSONObject.getString(
								"shipperCountryCode")
						).put(
							"PostalCode",
							typeSettingsJSONObject.getString(
								"shipperPostalCode")
						)
					)
				).put(
					"ShipTo",
					new JSONObject(
					).put(
						"Address",
						new JSONObject(
						).put(
							"AddressLine",
							new JSONArray(
							).put(
								shippingAddressJSONObject.getString("street1")
							).put(
								shippingAddressJSONObject.getString("street2")
							).put(
								shippingAddressJSONObject.getString("street3")
							)
						).put(
							"City", shippingAddressJSONObject.getString("city")
						).put(
							"CountryCode",
							shippingAddressJSONObject.getString(
								"countryISOCode")
						).put(
							"PostalCode",
							shippingAddressJSONObject.getString("zip")
						).put(
							"StateProvinceCode",
							shippingAddressJSONObject.getString("regionISOCode")
						)
					)
				)
			)
		).toString();

		try {
			return new JSONObject(
				post(
					"Bearer " + _getAccessToken(clientId, clientSecret, log),
					body,
					UriComponentsBuilder.fromUriString(
						"https://wwwcie.ups.com/api/rating/v2403/Rate"
					).build(
					).toUri()));
		}
		catch (Exception exception) {
			if (log.isDebugEnabled()) {
				log.debug(exception);
			}
		}

		return new JSONObject();
	}

	private JSONArray _toShippingOptionsJSONArray(JSONArray jsonArray) {
		JSONArray shippingOptionsJSONArray = new JSONArray();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (!jsonObject.has("RateResponse")) {
				continue;
			}

			JSONObject rateResponseJSONObject = jsonObject.getJSONObject(
				"RateResponse");

			JSONObject responseJSONObject =
				rateResponseJSONObject.getJSONObject("Response");

			JSONObject responseStatusJSONObject =
				responseJSONObject.getJSONObject("ResponseStatus");

			if (!StringUtil.equalsIgnoreCase(
					responseStatusJSONObject.getString("Code"), "1")) {

				continue;
			}

			JSONObject ratedShipmentJSONObject =
				rateResponseJSONObject.getJSONObject("RatedShipment");

			JSONObject serviceJSONObject =
				ratedShipmentJSONObject.getJSONObject("Service");
			JSONObject totalChargesJSONObject =
				ratedShipmentJSONObject.getJSONObject("TotalCharges");

			shippingOptionsJSONArray.put(
				new JSONObject(
				).put(
					"amount", totalChargesJSONObject.getString("MonetaryValue")
				).put(
					"currencyCode",
					totalChargesJSONObject.getString("CurrencyCode")
				).put(
					"key", serviceJSONObject.getString("Code")
				).put(
					"name",
					UPSServiceCodeConstants.getCodeName(
						serviceJSONObject.getString("Code"))
				).put(
					"priority", i
				));
		}

		return shippingOptionsJSONArray;
	}

	private static final Log _log = LogFactory.getLog(
		OptionsRestController.class);

}