/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.Summary;
import com.liferay.headless.commerce.delivery.order.client.json.BaseJSONParser;

import java.math.BigDecimal;

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
public class SummarySerDes {

	public static Summary toDTO(String json) {
		SummaryJSONParser summaryJSONParser = new SummaryJSONParser();

		return summaryJSONParser.parseToDTO(json);
	}

	public static Summary[] toDTOs(String json) {
		SummaryJSONParser summaryJSONParser = new SummaryJSONParser();

		return summaryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Summary summary) {
		if (summary == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (summary.getCurrency() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currency\": ");

			sb.append("\"");

			sb.append(_escape(summary.getCurrency()));

			sb.append("\"");
		}

		if (summary.getItemsQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemsQuantity\": ");

			sb.append(summary.getItemsQuantity());
		}

		if (summary.getShippingDiscountPercentages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentages\": ");

			sb.append("[");

			for (int i = 0; i < summary.getShippingDiscountPercentages().length;
				 i++) {

				sb.append(_toJSON(summary.getShippingDiscountPercentages()[i]));

				if ((i + 1) < summary.getShippingDiscountPercentages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (summary.getShippingDiscountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountValue\": ");

			sb.append(summary.getShippingDiscountValue());
		}

		if (summary.getShippingDiscountValueFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(summary.getShippingDiscountValueFormatted()));

			sb.append("\"");
		}

		if (summary.getShippingValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValue\": ");

			sb.append(summary.getShippingValue());
		}

		if (summary.getShippingValueFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(summary.getShippingValueFormatted()));

			sb.append("\"");
		}

		if (summary.getShippingValueWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValueWithTaxAmount\": ");

			sb.append(summary.getShippingValueWithTaxAmount());
		}

		if (summary.getShippingValueWithTaxAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValueWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(
				_escape(summary.getShippingValueWithTaxAmountFormatted()));

			sb.append("\"");
		}

		if (summary.getSubtotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotal\": ");

			sb.append(summary.getSubtotal());
		}

		if (summary.getSubtotalDiscountPercentages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentages\": ");

			sb.append("[");

			for (int i = 0; i < summary.getSubtotalDiscountPercentages().length;
				 i++) {

				sb.append(_toJSON(summary.getSubtotalDiscountPercentages()[i]));

				if ((i + 1) < summary.getSubtotalDiscountPercentages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (summary.getSubtotalDiscountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountValue\": ");

			sb.append(summary.getSubtotalDiscountValue());
		}

		if (summary.getSubtotalDiscountValueFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(summary.getSubtotalDiscountValueFormatted()));

			sb.append("\"");
		}

		if (summary.getSubtotalFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalFormatted\": ");

			sb.append("\"");

			sb.append(_escape(summary.getSubtotalFormatted()));

			sb.append("\"");
		}

		if (summary.getTaxValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxValue\": ");

			sb.append(summary.getTaxValue());
		}

		if (summary.getTaxValueFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(summary.getTaxValueFormatted()));

			sb.append("\"");
		}

		if (summary.getTotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(summary.getTotal());
		}

		if (summary.getTotalDiscountPercentages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentages\": ");

			sb.append("[");

			for (int i = 0; i < summary.getTotalDiscountPercentages().length;
				 i++) {

				sb.append(_toJSON(summary.getTotalDiscountPercentages()[i]));

				if ((i + 1) < summary.getTotalDiscountPercentages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (summary.getTotalDiscountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountValue\": ");

			sb.append(summary.getTotalDiscountValue());
		}

		if (summary.getTotalDiscountValueFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(summary.getTotalDiscountValueFormatted()));

			sb.append("\"");
		}

		if (summary.getTotalFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalFormatted\": ");

			sb.append("\"");

			sb.append(_escape(summary.getTotalFormatted()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SummaryJSONParser summaryJSONParser = new SummaryJSONParser();

		return summaryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Summary summary) {
		if (summary == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (summary.getCurrency() == null) {
			map.put("currency", null);
		}
		else {
			map.put("currency", String.valueOf(summary.getCurrency()));
		}

		if (summary.getItemsQuantity() == null) {
			map.put("itemsQuantity", null);
		}
		else {
			map.put(
				"itemsQuantity", String.valueOf(summary.getItemsQuantity()));
		}

		if (summary.getShippingDiscountPercentages() == null) {
			map.put("shippingDiscountPercentages", null);
		}
		else {
			map.put(
				"shippingDiscountPercentages",
				String.valueOf(summary.getShippingDiscountPercentages()));
		}

		if (summary.getShippingDiscountValue() == null) {
			map.put("shippingDiscountValue", null);
		}
		else {
			map.put(
				"shippingDiscountValue",
				String.valueOf(summary.getShippingDiscountValue()));
		}

		if (summary.getShippingDiscountValueFormatted() == null) {
			map.put("shippingDiscountValueFormatted", null);
		}
		else {
			map.put(
				"shippingDiscountValueFormatted",
				String.valueOf(summary.getShippingDiscountValueFormatted()));
		}

		if (summary.getShippingValue() == null) {
			map.put("shippingValue", null);
		}
		else {
			map.put(
				"shippingValue", String.valueOf(summary.getShippingValue()));
		}

		if (summary.getShippingValueFormatted() == null) {
			map.put("shippingValueFormatted", null);
		}
		else {
			map.put(
				"shippingValueFormatted",
				String.valueOf(summary.getShippingValueFormatted()));
		}

		if (summary.getShippingValueWithTaxAmount() == null) {
			map.put("shippingValueWithTaxAmount", null);
		}
		else {
			map.put(
				"shippingValueWithTaxAmount",
				String.valueOf(summary.getShippingValueWithTaxAmount()));
		}

		if (summary.getShippingValueWithTaxAmountFormatted() == null) {
			map.put("shippingValueWithTaxAmountFormatted", null);
		}
		else {
			map.put(
				"shippingValueWithTaxAmountFormatted",
				String.valueOf(
					summary.getShippingValueWithTaxAmountFormatted()));
		}

		if (summary.getSubtotal() == null) {
			map.put("subtotal", null);
		}
		else {
			map.put("subtotal", String.valueOf(summary.getSubtotal()));
		}

		if (summary.getSubtotalDiscountPercentages() == null) {
			map.put("subtotalDiscountPercentages", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentages",
				String.valueOf(summary.getSubtotalDiscountPercentages()));
		}

		if (summary.getSubtotalDiscountValue() == null) {
			map.put("subtotalDiscountValue", null);
		}
		else {
			map.put(
				"subtotalDiscountValue",
				String.valueOf(summary.getSubtotalDiscountValue()));
		}

		if (summary.getSubtotalDiscountValueFormatted() == null) {
			map.put("subtotalDiscountValueFormatted", null);
		}
		else {
			map.put(
				"subtotalDiscountValueFormatted",
				String.valueOf(summary.getSubtotalDiscountValueFormatted()));
		}

		if (summary.getSubtotalFormatted() == null) {
			map.put("subtotalFormatted", null);
		}
		else {
			map.put(
				"subtotalFormatted",
				String.valueOf(summary.getSubtotalFormatted()));
		}

		if (summary.getTaxValue() == null) {
			map.put("taxValue", null);
		}
		else {
			map.put("taxValue", String.valueOf(summary.getTaxValue()));
		}

		if (summary.getTaxValueFormatted() == null) {
			map.put("taxValueFormatted", null);
		}
		else {
			map.put(
				"taxValueFormatted",
				String.valueOf(summary.getTaxValueFormatted()));
		}

		if (summary.getTotal() == null) {
			map.put("total", null);
		}
		else {
			map.put("total", String.valueOf(summary.getTotal()));
		}

		if (summary.getTotalDiscountPercentages() == null) {
			map.put("totalDiscountPercentages", null);
		}
		else {
			map.put(
				"totalDiscountPercentages",
				String.valueOf(summary.getTotalDiscountPercentages()));
		}

		if (summary.getTotalDiscountValue() == null) {
			map.put("totalDiscountValue", null);
		}
		else {
			map.put(
				"totalDiscountValue",
				String.valueOf(summary.getTotalDiscountValue()));
		}

		if (summary.getTotalDiscountValueFormatted() == null) {
			map.put("totalDiscountValueFormatted", null);
		}
		else {
			map.put(
				"totalDiscountValueFormatted",
				String.valueOf(summary.getTotalDiscountValueFormatted()));
		}

		if (summary.getTotalFormatted() == null) {
			map.put("totalFormatted", null);
		}
		else {
			map.put(
				"totalFormatted", String.valueOf(summary.getTotalFormatted()));
		}

		return map;
	}

	public static class SummaryJSONParser extends BaseJSONParser<Summary> {

		@Override
		protected Summary createDTO() {
			return new Summary();
		}

		@Override
		protected Summary[] createDTOArray(int size) {
			return new Summary[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "currency")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "itemsQuantity")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountPercentages")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountValue")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountValueFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingValue")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingValueFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingValueWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingValueWithTaxAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subtotal")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalDiscountPercentages")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalDiscountValue")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountValueFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subtotalFormatted")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxValueFormatted")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentages")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountValue")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountValueFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalFormatted")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Summary summary, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "currency")) {
				if (jsonParserFieldValue != null) {
					summary.setCurrency((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "itemsQuantity")) {
				if (jsonParserFieldValue != null) {
					summary.setItemsQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountPercentages")) {

				if (jsonParserFieldValue != null) {
					summary.setShippingDiscountPercentages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountValue")) {

				if (jsonParserFieldValue != null) {
					summary.setShippingDiscountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountValueFormatted")) {

				if (jsonParserFieldValue != null) {
					summary.setShippingDiscountValueFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingValue")) {
				if (jsonParserFieldValue != null) {
					summary.setShippingValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingValueFormatted")) {

				if (jsonParserFieldValue != null) {
					summary.setShippingValueFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingValueWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					summary.setShippingValueWithTaxAmount(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingValueWithTaxAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					summary.setShippingValueWithTaxAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtotal")) {
				if (jsonParserFieldValue != null) {
					summary.setSubtotal(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalDiscountPercentages")) {

				if (jsonParserFieldValue != null) {
					summary.setSubtotalDiscountPercentages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalDiscountValue")) {

				if (jsonParserFieldValue != null) {
					summary.setSubtotalDiscountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountValueFormatted")) {

				if (jsonParserFieldValue != null) {
					summary.setSubtotalDiscountValueFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtotalFormatted")) {
				if (jsonParserFieldValue != null) {
					summary.setSubtotalFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxValue")) {
				if (jsonParserFieldValue != null) {
					summary.setTaxValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxValueFormatted")) {
				if (jsonParserFieldValue != null) {
					summary.setTaxValueFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				if (jsonParserFieldValue != null) {
					summary.setTotal(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentages")) {

				if (jsonParserFieldValue != null) {
					summary.setTotalDiscountPercentages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountValue")) {

				if (jsonParserFieldValue != null) {
					summary.setTotalDiscountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountValueFormatted")) {

				if (jsonParserFieldValue != null) {
					summary.setTotalDiscountValueFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalFormatted")) {
				if (jsonParserFieldValue != null) {
					summary.setTotalFormatted((String)jsonParserFieldValue);
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