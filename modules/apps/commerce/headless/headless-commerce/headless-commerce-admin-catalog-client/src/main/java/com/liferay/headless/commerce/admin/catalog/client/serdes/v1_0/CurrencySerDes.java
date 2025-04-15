/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Currency;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

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
public class CurrencySerDes {

	public static Currency toDTO(String json) {
		CurrencyJSONParser currencyJSONParser = new CurrencyJSONParser();

		return currencyJSONParser.parseToDTO(json);
	}

	public static Currency[] toDTOs(String json) {
		CurrencyJSONParser currencyJSONParser = new CurrencyJSONParser();

		return currencyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Currency currency) {
		if (currency == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (currency.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(currency.getActive());
		}

		if (currency.getCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"code\": ");

			sb.append("\"");

			sb.append(_escape(currency.getCode()));

			sb.append("\"");
		}

		if (currency.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(currency.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (currency.getFormatPattern() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formatPattern\": ");

			sb.append(_toJSON(currency.getFormatPattern()));
		}

		if (currency.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(currency.getId());
		}

		if (currency.getMaxFractionDigits() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxFractionDigits\": ");

			sb.append(currency.getMaxFractionDigits());
		}

		if (currency.getMinFractionDigits() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minFractionDigits\": ");

			sb.append(currency.getMinFractionDigits());
		}

		if (currency.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(currency.getName()));
		}

		if (currency.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(currency.getPrimary());
		}

		if (currency.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(currency.getPriority());
		}

		if (currency.getRate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rate\": ");

			sb.append(currency.getRate());
		}

		if (currency.getRoundingMode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roundingMode\": ");

			sb.append("\"");

			sb.append(currency.getRoundingMode());

			sb.append("\"");
		}

		if (currency.getSymbol() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"symbol\": ");

			sb.append("\"");

			sb.append(_escape(currency.getSymbol()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CurrencyJSONParser currencyJSONParser = new CurrencyJSONParser();

		return currencyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Currency currency) {
		if (currency == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (currency.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(currency.getActive()));
		}

		if (currency.getCode() == null) {
			map.put("code", null);
		}
		else {
			map.put("code", String.valueOf(currency.getCode()));
		}

		if (currency.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(currency.getExternalReferenceCode()));
		}

		if (currency.getFormatPattern() == null) {
			map.put("formatPattern", null);
		}
		else {
			map.put(
				"formatPattern", String.valueOf(currency.getFormatPattern()));
		}

		if (currency.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(currency.getId()));
		}

		if (currency.getMaxFractionDigits() == null) {
			map.put("maxFractionDigits", null);
		}
		else {
			map.put(
				"maxFractionDigits",
				String.valueOf(currency.getMaxFractionDigits()));
		}

		if (currency.getMinFractionDigits() == null) {
			map.put("minFractionDigits", null);
		}
		else {
			map.put(
				"minFractionDigits",
				String.valueOf(currency.getMinFractionDigits()));
		}

		if (currency.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(currency.getName()));
		}

		if (currency.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put("primary", String.valueOf(currency.getPrimary()));
		}

		if (currency.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(currency.getPriority()));
		}

		if (currency.getRate() == null) {
			map.put("rate", null);
		}
		else {
			map.put("rate", String.valueOf(currency.getRate()));
		}

		if (currency.getRoundingMode() == null) {
			map.put("roundingMode", null);
		}
		else {
			map.put("roundingMode", String.valueOf(currency.getRoundingMode()));
		}

		if (currency.getSymbol() == null) {
			map.put("symbol", null);
		}
		else {
			map.put("symbol", String.valueOf(currency.getSymbol()));
		}

		return map;
	}

	public static class CurrencyJSONParser extends BaseJSONParser<Currency> {

		@Override
		protected Currency createDTO() {
			return new Currency();
		}

		@Override
		protected Currency[] createDTOArray(int size) {
			return new Currency[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "code")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formatPattern")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "maxFractionDigits")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "minFractionDigits")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roundingMode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "symbol")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Currency currency, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					currency.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "code")) {
				if (jsonParserFieldValue != null) {
					currency.setCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					currency.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formatPattern")) {
				if (jsonParserFieldValue != null) {
					currency.setFormatPattern(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					currency.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxFractionDigits")) {
				if (jsonParserFieldValue != null) {
					currency.setMaxFractionDigits(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "minFractionDigits")) {
				if (jsonParserFieldValue != null) {
					currency.setMinFractionDigits(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					currency.setName((Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					currency.setPrimary((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					currency.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rate")) {
				if (jsonParserFieldValue != null) {
					currency.setRate(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roundingMode")) {
				if (jsonParserFieldValue != null) {
					currency.setRoundingMode(
						Currency.RoundingMode.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "symbol")) {
				if (jsonParserFieldValue != null) {
					currency.setSymbol((String)jsonParserFieldValue);
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