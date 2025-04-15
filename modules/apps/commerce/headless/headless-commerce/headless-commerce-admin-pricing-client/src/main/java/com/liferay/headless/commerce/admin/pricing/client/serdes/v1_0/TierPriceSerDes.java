/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v1_0.TierPrice;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class TierPriceSerDes {

	public static TierPrice toDTO(String json) {
		TierPriceJSONParser tierPriceJSONParser = new TierPriceJSONParser();

		return tierPriceJSONParser.parseToDTO(json);
	}

	public static TierPrice[] toDTOs(String json) {
		TierPriceJSONParser tierPriceJSONParser = new TierPriceJSONParser();

		return tierPriceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TierPrice tierPrice) {
		if (tierPrice == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (tierPrice.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(tierPrice.getCustomFields()));
		}

		if (tierPrice.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(tierPrice.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (tierPrice.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(tierPrice.getId());
		}

		if (tierPrice.getMinimumQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minimumQuantity\": ");

			sb.append(tierPrice.getMinimumQuantity());
		}

		if (tierPrice.getPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(tierPrice.getPrice());
		}

		if (tierPrice.getPriceEntryExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceEntryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(tierPrice.getPriceEntryExternalReferenceCode()));

			sb.append("\"");
		}

		if (tierPrice.getPriceEntryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceEntryId\": ");

			sb.append(tierPrice.getPriceEntryId());
		}

		if (tierPrice.getPromoPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPrice\": ");

			sb.append(tierPrice.getPromoPrice());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TierPriceJSONParser tierPriceJSONParser = new TierPriceJSONParser();

		return tierPriceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TierPrice tierPrice) {
		if (tierPrice == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (tierPrice.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(tierPrice.getCustomFields()));
		}

		if (tierPrice.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(tierPrice.getExternalReferenceCode()));
		}

		if (tierPrice.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(tierPrice.getId()));
		}

		if (tierPrice.getMinimumQuantity() == null) {
			map.put("minimumQuantity", null);
		}
		else {
			map.put(
				"minimumQuantity",
				String.valueOf(tierPrice.getMinimumQuantity()));
		}

		if (tierPrice.getPrice() == null) {
			map.put("price", null);
		}
		else {
			map.put("price", String.valueOf(tierPrice.getPrice()));
		}

		if (tierPrice.getPriceEntryExternalReferenceCode() == null) {
			map.put("priceEntryExternalReferenceCode", null);
		}
		else {
			map.put(
				"priceEntryExternalReferenceCode",
				String.valueOf(tierPrice.getPriceEntryExternalReferenceCode()));
		}

		if (tierPrice.getPriceEntryId() == null) {
			map.put("priceEntryId", null);
		}
		else {
			map.put(
				"priceEntryId", String.valueOf(tierPrice.getPriceEntryId()));
		}

		if (tierPrice.getPromoPrice() == null) {
			map.put("promoPrice", null);
		}
		else {
			map.put("promoPrice", String.valueOf(tierPrice.getPromoPrice()));
		}

		return map;
	}

	public static class TierPriceJSONParser extends BaseJSONParser<TierPrice> {

		@Override
		protected TierPrice createDTO() {
			return new TierPrice();
		}

		@Override
		protected TierPrice[] createDTOArray(int size) {
			return new TierPrice[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "minimumQuantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceEntryExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceEntryId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "promoPrice")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TierPrice tierPrice, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					tierPrice.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					tierPrice.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					tierPrice.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "minimumQuantity")) {
				if (jsonParserFieldValue != null) {
					tierPrice.setMinimumQuantity(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				if (jsonParserFieldValue != null) {
					tierPrice.setPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceEntryExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					tierPrice.setPriceEntryExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceEntryId")) {
				if (jsonParserFieldValue != null) {
					tierPrice.setPriceEntryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "promoPrice")) {
				if (jsonParserFieldValue != null) {
					tierPrice.setPromoPrice(
						new BigDecimal((String)jsonParserFieldValue));
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