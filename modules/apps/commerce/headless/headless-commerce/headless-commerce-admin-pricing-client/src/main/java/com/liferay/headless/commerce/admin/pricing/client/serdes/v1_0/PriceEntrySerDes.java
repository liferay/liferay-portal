/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v1_0.PriceEntry;
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
public class PriceEntrySerDes {

	public static PriceEntry toDTO(String json) {
		PriceEntryJSONParser priceEntryJSONParser = new PriceEntryJSONParser();

		return priceEntryJSONParser.parseToDTO(json);
	}

	public static PriceEntry[] toDTOs(String json) {
		PriceEntryJSONParser priceEntryJSONParser = new PriceEntryJSONParser();

		return priceEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PriceEntry priceEntry) {
		if (priceEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (priceEntry.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(priceEntry.getCustomFields()));
		}

		if (priceEntry.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceEntry.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceEntry.getHasTierPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hasTierPrice\": ");

			sb.append(priceEntry.getHasTierPrice());
		}

		if (priceEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(priceEntry.getId());
		}

		if (priceEntry.getPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(priceEntry.getPrice());
		}

		if (priceEntry.getPriceListExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceEntry.getPriceListExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceEntry.getPriceListId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListId\": ");

			sb.append(priceEntry.getPriceListId());
		}

		if (priceEntry.getPromoPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPrice\": ");

			sb.append(priceEntry.getPromoPrice());
		}

		if (priceEntry.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(priceEntry.getSku()));

			sb.append("\"");
		}

		if (priceEntry.getSkuExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceEntry.getSkuExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceEntry.getSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(priceEntry.getSkuId());
		}

		if (priceEntry.getTierPrices() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tierPrices\": ");

			sb.append("[");

			for (int i = 0; i < priceEntry.getTierPrices().length; i++) {
				sb.append(String.valueOf(priceEntry.getTierPrices()[i]));

				if ((i + 1) < priceEntry.getTierPrices().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PriceEntryJSONParser priceEntryJSONParser = new PriceEntryJSONParser();

		return priceEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PriceEntry priceEntry) {
		if (priceEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (priceEntry.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(priceEntry.getCustomFields()));
		}

		if (priceEntry.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(priceEntry.getExternalReferenceCode()));
		}

		if (priceEntry.getHasTierPrice() == null) {
			map.put("hasTierPrice", null);
		}
		else {
			map.put(
				"hasTierPrice", String.valueOf(priceEntry.getHasTierPrice()));
		}

		if (priceEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(priceEntry.getId()));
		}

		if (priceEntry.getPrice() == null) {
			map.put("price", null);
		}
		else {
			map.put("price", String.valueOf(priceEntry.getPrice()));
		}

		if (priceEntry.getPriceListExternalReferenceCode() == null) {
			map.put("priceListExternalReferenceCode", null);
		}
		else {
			map.put(
				"priceListExternalReferenceCode",
				String.valueOf(priceEntry.getPriceListExternalReferenceCode()));
		}

		if (priceEntry.getPriceListId() == null) {
			map.put("priceListId", null);
		}
		else {
			map.put("priceListId", String.valueOf(priceEntry.getPriceListId()));
		}

		if (priceEntry.getPromoPrice() == null) {
			map.put("promoPrice", null);
		}
		else {
			map.put("promoPrice", String.valueOf(priceEntry.getPromoPrice()));
		}

		if (priceEntry.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(priceEntry.getSku()));
		}

		if (priceEntry.getSkuExternalReferenceCode() == null) {
			map.put("skuExternalReferenceCode", null);
		}
		else {
			map.put(
				"skuExternalReferenceCode",
				String.valueOf(priceEntry.getSkuExternalReferenceCode()));
		}

		if (priceEntry.getSkuId() == null) {
			map.put("skuId", null);
		}
		else {
			map.put("skuId", String.valueOf(priceEntry.getSkuId()));
		}

		if (priceEntry.getTierPrices() == null) {
			map.put("tierPrices", null);
		}
		else {
			map.put("tierPrices", String.valueOf(priceEntry.getTierPrices()));
		}

		return map;
	}

	public static class PriceEntryJSONParser
		extends BaseJSONParser<PriceEntry> {

		@Override
		protected PriceEntry createDTO() {
			return new PriceEntry();
		}

		@Override
		protected PriceEntry[] createDTOArray(int size) {
			return new PriceEntry[size];
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
			else if (Objects.equals(jsonParserFieldName, "hasTierPrice")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceListExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceListId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "promoPrice")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "skuExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tierPrices")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PriceEntry priceEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					priceEntry.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceEntry.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hasTierPrice")) {
				if (jsonParserFieldValue != null) {
					priceEntry.setHasTierPrice((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					priceEntry.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				if (jsonParserFieldValue != null) {
					priceEntry.setPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceListExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceEntry.setPriceListExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceListId")) {
				if (jsonParserFieldValue != null) {
					priceEntry.setPriceListId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "promoPrice")) {
				if (jsonParserFieldValue != null) {
					priceEntry.setPromoPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					priceEntry.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "skuExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceEntry.setSkuExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				if (jsonParserFieldValue != null) {
					priceEntry.setSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tierPrices")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TierPrice[] tierPricesArray =
						new TierPrice[jsonParserFieldValues.length];

					for (int i = 0; i < tierPricesArray.length; i++) {
						tierPricesArray[i] = TierPriceSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					priceEntry.setTierPrices(tierPricesArray);
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