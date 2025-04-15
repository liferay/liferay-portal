/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.CartItem;
import com.liferay.headless.commerce.delivery.cart.client.json.BaseJSONParser;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class CartItemSerDes {

	public static CartItem toDTO(String json) {
		CartItemJSONParser cartItemJSONParser = new CartItemJSONParser();

		return cartItemJSONParser.parseToDTO(json);
	}

	public static CartItem[] toDTOs(String json) {
		CartItemJSONParser cartItemJSONParser = new CartItemJSONParser();

		return cartItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CartItem cartItem) {
		if (cartItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (cartItem.getAdaptiveMediaImageHTMLTag() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"adaptiveMediaImageHTMLTag\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getAdaptiveMediaImageHTMLTag()));

			sb.append("\"");
		}

		if (cartItem.getCartItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cartItems\": ");

			sb.append("[");

			for (int i = 0; i < cartItem.getCartItems().length; i++) {
				sb.append(String.valueOf(cartItem.getCartItems()[i]));

				if ((i + 1) < cartItem.getCartItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (cartItem.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(cartItem.getCustomFields()));
		}

		if (cartItem.getDeliveryGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryGroup\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getDeliveryGroup()));

			sb.append("\"");
		}

		if (cartItem.getDeliveryGroupName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryGroupName\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getDeliveryGroupName()));

			sb.append("\"");
		}

		if (cartItem.getErrorMessages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessages\": ");

			sb.append("[");

			for (int i = 0; i < cartItem.getErrorMessages().length; i++) {
				sb.append(_toJSON(cartItem.getErrorMessages()[i]));

				if ((i + 1) < cartItem.getErrorMessages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (cartItem.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (cartItem.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(cartItem.getId());
		}

		if (cartItem.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getName()));

			sb.append("\"");
		}

		if (cartItem.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getOptions()));

			sb.append("\"");
		}

		if (cartItem.getParentCartItemId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentCartItemId\": ");

			sb.append(cartItem.getParentCartItemId());
		}

		if (cartItem.getPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(String.valueOf(cartItem.getPrice()));
		}

		if (cartItem.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(cartItem.getProductId());
		}

		if (cartItem.getProductURLs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productURLs\": ");

			sb.append(_toJSON(cartItem.getProductURLs()));
		}

		if (cartItem.getQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(cartItem.getQuantity());
		}

		if (cartItem.getReplacedSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSku\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getReplacedSku()));

			sb.append("\"");
		}

		if (cartItem.getReplacedSkuExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSkuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getReplacedSkuExternalReferenceCode()));

			sb.append("\"");
		}

		if (cartItem.getReplacedSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSkuId\": ");

			sb.append(cartItem.getReplacedSkuId());
		}

		if (cartItem.getRequestedDeliveryDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestedDeliveryDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					cartItem.getRequestedDeliveryDate()));

			sb.append("\"");
		}

		if (cartItem.getSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(cartItem.getSettings()));
		}

		if (cartItem.getShippingAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddress\": ");

			sb.append(String.valueOf(cartItem.getShippingAddress()));
		}

		if (cartItem.getShippingAddressExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(cartItem.getShippingAddressExternalReferenceCode()));

			sb.append("\"");
		}

		if (cartItem.getShippingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressId\": ");

			sb.append(cartItem.getShippingAddressId());
		}

		if (cartItem.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getSku()));

			sb.append("\"");
		}

		if (cartItem.getSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(cartItem.getSkuId());
		}

		if (cartItem.getSkuUnitOfMeasure() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuUnitOfMeasure\": ");

			sb.append(String.valueOf(cartItem.getSkuUnitOfMeasure()));
		}

		if (cartItem.getSubscription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscription\": ");

			sb.append(cartItem.getSubscription());
		}

		if (cartItem.getThumbnail() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getThumbnail()));

			sb.append("\"");
		}

		if (cartItem.getUnitOfMeasure() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasure\": ");

			sb.append("\"");

			sb.append(_escape(cartItem.getUnitOfMeasure()));

			sb.append("\"");
		}

		if (cartItem.getValid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valid\": ");

			sb.append(cartItem.getValid());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CartItemJSONParser cartItemJSONParser = new CartItemJSONParser();

		return cartItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CartItem cartItem) {
		if (cartItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (cartItem.getAdaptiveMediaImageHTMLTag() == null) {
			map.put("adaptiveMediaImageHTMLTag", null);
		}
		else {
			map.put(
				"adaptiveMediaImageHTMLTag",
				String.valueOf(cartItem.getAdaptiveMediaImageHTMLTag()));
		}

		if (cartItem.getCartItems() == null) {
			map.put("cartItems", null);
		}
		else {
			map.put("cartItems", String.valueOf(cartItem.getCartItems()));
		}

		if (cartItem.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(cartItem.getCustomFields()));
		}

		if (cartItem.getDeliveryGroup() == null) {
			map.put("deliveryGroup", null);
		}
		else {
			map.put(
				"deliveryGroup", String.valueOf(cartItem.getDeliveryGroup()));
		}

		if (cartItem.getDeliveryGroupName() == null) {
			map.put("deliveryGroupName", null);
		}
		else {
			map.put(
				"deliveryGroupName",
				String.valueOf(cartItem.getDeliveryGroupName()));
		}

		if (cartItem.getErrorMessages() == null) {
			map.put("errorMessages", null);
		}
		else {
			map.put(
				"errorMessages", String.valueOf(cartItem.getErrorMessages()));
		}

		if (cartItem.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(cartItem.getExternalReferenceCode()));
		}

		if (cartItem.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(cartItem.getId()));
		}

		if (cartItem.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(cartItem.getName()));
		}

		if (cartItem.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(cartItem.getOptions()));
		}

		if (cartItem.getParentCartItemId() == null) {
			map.put("parentCartItemId", null);
		}
		else {
			map.put(
				"parentCartItemId",
				String.valueOf(cartItem.getParentCartItemId()));
		}

		if (cartItem.getPrice() == null) {
			map.put("price", null);
		}
		else {
			map.put("price", String.valueOf(cartItem.getPrice()));
		}

		if (cartItem.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put("productId", String.valueOf(cartItem.getProductId()));
		}

		if (cartItem.getProductURLs() == null) {
			map.put("productURLs", null);
		}
		else {
			map.put("productURLs", String.valueOf(cartItem.getProductURLs()));
		}

		if (cartItem.getQuantity() == null) {
			map.put("quantity", null);
		}
		else {
			map.put("quantity", String.valueOf(cartItem.getQuantity()));
		}

		if (cartItem.getReplacedSku() == null) {
			map.put("replacedSku", null);
		}
		else {
			map.put("replacedSku", String.valueOf(cartItem.getReplacedSku()));
		}

		if (cartItem.getReplacedSkuExternalReferenceCode() == null) {
			map.put("replacedSkuExternalReferenceCode", null);
		}
		else {
			map.put(
				"replacedSkuExternalReferenceCode",
				String.valueOf(cartItem.getReplacedSkuExternalReferenceCode()));
		}

		if (cartItem.getReplacedSkuId() == null) {
			map.put("replacedSkuId", null);
		}
		else {
			map.put(
				"replacedSkuId", String.valueOf(cartItem.getReplacedSkuId()));
		}

		if (cartItem.getRequestedDeliveryDate() == null) {
			map.put("requestedDeliveryDate", null);
		}
		else {
			map.put(
				"requestedDeliveryDate",
				liferayToJSONDateFormat.format(
					cartItem.getRequestedDeliveryDate()));
		}

		if (cartItem.getSettings() == null) {
			map.put("settings", null);
		}
		else {
			map.put("settings", String.valueOf(cartItem.getSettings()));
		}

		if (cartItem.getShippingAddress() == null) {
			map.put("shippingAddress", null);
		}
		else {
			map.put(
				"shippingAddress",
				String.valueOf(cartItem.getShippingAddress()));
		}

		if (cartItem.getShippingAddressExternalReferenceCode() == null) {
			map.put("shippingAddressExternalReferenceCode", null);
		}
		else {
			map.put(
				"shippingAddressExternalReferenceCode",
				String.valueOf(
					cartItem.getShippingAddressExternalReferenceCode()));
		}

		if (cartItem.getShippingAddressId() == null) {
			map.put("shippingAddressId", null);
		}
		else {
			map.put(
				"shippingAddressId",
				String.valueOf(cartItem.getShippingAddressId()));
		}

		if (cartItem.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(cartItem.getSku()));
		}

		if (cartItem.getSkuId() == null) {
			map.put("skuId", null);
		}
		else {
			map.put("skuId", String.valueOf(cartItem.getSkuId()));
		}

		if (cartItem.getSkuUnitOfMeasure() == null) {
			map.put("skuUnitOfMeasure", null);
		}
		else {
			map.put(
				"skuUnitOfMeasure",
				String.valueOf(cartItem.getSkuUnitOfMeasure()));
		}

		if (cartItem.getSubscription() == null) {
			map.put("subscription", null);
		}
		else {
			map.put("subscription", String.valueOf(cartItem.getSubscription()));
		}

		if (cartItem.getThumbnail() == null) {
			map.put("thumbnail", null);
		}
		else {
			map.put("thumbnail", String.valueOf(cartItem.getThumbnail()));
		}

		if (cartItem.getUnitOfMeasure() == null) {
			map.put("unitOfMeasure", null);
		}
		else {
			map.put(
				"unitOfMeasure", String.valueOf(cartItem.getUnitOfMeasure()));
		}

		if (cartItem.getValid() == null) {
			map.put("valid", null);
		}
		else {
			map.put("valid", String.valueOf(cartItem.getValid()));
		}

		return map;
	}

	public static class CartItemJSONParser extends BaseJSONParser<CartItem> {

		@Override
		protected CartItem createDTO() {
			return new CartItem();
		}

		@Override
		protected CartItem[] createDTOArray(int size) {
			return new CartItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "adaptiveMediaImageHTMLTag")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cartItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroup")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroupName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessages")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentCartItemId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productURLs")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSku")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"replacedSkuExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSkuId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddress")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingAddressExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddressId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuUnitOfMeasure")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subscription")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasure")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "valid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CartItem cartItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "adaptiveMediaImageHTMLTag")) {

				if (jsonParserFieldValue != null) {
					cartItem.setAdaptiveMediaImageHTMLTag(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cartItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					CartItem[] cartItemsArray =
						new CartItem[jsonParserFieldValues.length];

					for (int i = 0; i < cartItemsArray.length; i++) {
						cartItemsArray[i] = CartItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					cartItem.setCartItems(cartItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					cartItem.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroup")) {
				if (jsonParserFieldValue != null) {
					cartItem.setDeliveryGroup((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroupName")) {
				if (jsonParserFieldValue != null) {
					cartItem.setDeliveryGroupName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessages")) {
				if (jsonParserFieldValue != null) {
					cartItem.setErrorMessages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					cartItem.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					cartItem.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					cartItem.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					cartItem.setOptions((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentCartItemId")) {
				if (jsonParserFieldValue != null) {
					cartItem.setParentCartItemId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				if (jsonParserFieldValue != null) {
					cartItem.setPrice(
						PriceSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					cartItem.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productURLs")) {
				if (jsonParserFieldValue != null) {
					cartItem.setProductURLs(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				if (jsonParserFieldValue != null) {
					cartItem.setQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSku")) {
				if (jsonParserFieldValue != null) {
					cartItem.setReplacedSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"replacedSkuExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					cartItem.setReplacedSkuExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSkuId")) {
				if (jsonParserFieldValue != null) {
					cartItem.setReplacedSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				if (jsonParserFieldValue != null) {
					cartItem.setRequestedDeliveryDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				if (jsonParserFieldValue != null) {
					cartItem.setSettings(
						SettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddress")) {
				if (jsonParserFieldValue != null) {
					cartItem.setShippingAddress(
						AddressSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingAddressExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					cartItem.setShippingAddressExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddressId")) {
				if (jsonParserFieldValue != null) {
					cartItem.setShippingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					cartItem.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				if (jsonParserFieldValue != null) {
					cartItem.setSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuUnitOfMeasure")) {
				if (jsonParserFieldValue != null) {
					cartItem.setSkuUnitOfMeasure(
						SkuUnitOfMeasureSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscription")) {
				if (jsonParserFieldValue != null) {
					cartItem.setSubscription((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				if (jsonParserFieldValue != null) {
					cartItem.setThumbnail((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasure")) {
				if (jsonParserFieldValue != null) {
					cartItem.setUnitOfMeasure((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "valid")) {
				if (jsonParserFieldValue != null) {
					cartItem.setValid((Boolean)jsonParserFieldValue);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}