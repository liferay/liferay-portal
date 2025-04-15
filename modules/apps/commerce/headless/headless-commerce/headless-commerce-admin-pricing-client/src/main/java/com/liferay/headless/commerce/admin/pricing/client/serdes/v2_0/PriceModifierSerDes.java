/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifier;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifierCategory;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifierProduct;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifierProductGroup;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class PriceModifierSerDes {

	public static PriceModifier toDTO(String json) {
		PriceModifierJSONParser priceModifierJSONParser =
			new PriceModifierJSONParser();

		return priceModifierJSONParser.parseToDTO(json);
	}

	public static PriceModifier[] toDTOs(String json) {
		PriceModifierJSONParser priceModifierJSONParser =
			new PriceModifierJSONParser();

		return priceModifierJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PriceModifier priceModifier) {
		if (priceModifier == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (priceModifier.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(priceModifier.getActions()));
		}

		if (priceModifier.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(priceModifier.getActive());
		}

		if (priceModifier.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(priceModifier.getDisplayDate()));

			sb.append("\"");
		}

		if (priceModifier.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					priceModifier.getExpirationDate()));

			sb.append("\"");
		}

		if (priceModifier.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceModifier.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceModifier.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(priceModifier.getId());
		}

		if (priceModifier.getModifierAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifierAmount\": ");

			sb.append(priceModifier.getModifierAmount());
		}

		if (priceModifier.getModifierType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifierType\": ");

			sb.append("\"");

			sb.append(_escape(priceModifier.getModifierType()));

			sb.append("\"");
		}

		if (priceModifier.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(priceModifier.getNeverExpire());
		}

		if (priceModifier.getPriceListExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(priceModifier.getPriceListExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceModifier.getPriceListId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceListId\": ");

			sb.append(priceModifier.getPriceListId());
		}

		if (priceModifier.getPriceModifierCategories() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierCategories\": ");

			sb.append("[");

			for (int i = 0;
				 i < priceModifier.getPriceModifierCategories().length; i++) {

				sb.append(
					String.valueOf(
						priceModifier.getPriceModifierCategories()[i]));

				if ((i + 1) <
						priceModifier.getPriceModifierCategories().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceModifier.getPriceModifierProductGroups() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierProductGroups\": ");

			sb.append("[");

			for (int i = 0;
				 i < priceModifier.getPriceModifierProductGroups().length;
				 i++) {

				sb.append(
					String.valueOf(
						priceModifier.getPriceModifierProductGroups()[i]));

				if ((i + 1) <
						priceModifier.getPriceModifierProductGroups().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceModifier.getPriceModifierProducts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierProducts\": ");

			sb.append("[");

			for (int i = 0; i < priceModifier.getPriceModifierProducts().length;
				 i++) {

				sb.append(
					String.valueOf(
						priceModifier.getPriceModifierProducts()[i]));

				if ((i + 1) < priceModifier.getPriceModifierProducts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (priceModifier.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priceModifier.getPriority());
		}

		if (priceModifier.getTarget() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(_escape(priceModifier.getTarget()));

			sb.append("\"");
		}

		if (priceModifier.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(priceModifier.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PriceModifierJSONParser priceModifierJSONParser =
			new PriceModifierJSONParser();

		return priceModifierJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PriceModifier priceModifier) {
		if (priceModifier == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (priceModifier.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(priceModifier.getActions()));
		}

		if (priceModifier.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(priceModifier.getActive()));
		}

		if (priceModifier.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(priceModifier.getDisplayDate()));
		}

		if (priceModifier.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(
					priceModifier.getExpirationDate()));
		}

		if (priceModifier.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(priceModifier.getExternalReferenceCode()));
		}

		if (priceModifier.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(priceModifier.getId()));
		}

		if (priceModifier.getModifierAmount() == null) {
			map.put("modifierAmount", null);
		}
		else {
			map.put(
				"modifierAmount",
				String.valueOf(priceModifier.getModifierAmount()));
		}

		if (priceModifier.getModifierType() == null) {
			map.put("modifierType", null);
		}
		else {
			map.put(
				"modifierType",
				String.valueOf(priceModifier.getModifierType()));
		}

		if (priceModifier.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put(
				"neverExpire", String.valueOf(priceModifier.getNeverExpire()));
		}

		if (priceModifier.getPriceListExternalReferenceCode() == null) {
			map.put("priceListExternalReferenceCode", null);
		}
		else {
			map.put(
				"priceListExternalReferenceCode",
				String.valueOf(
					priceModifier.getPriceListExternalReferenceCode()));
		}

		if (priceModifier.getPriceListId() == null) {
			map.put("priceListId", null);
		}
		else {
			map.put(
				"priceListId", String.valueOf(priceModifier.getPriceListId()));
		}

		if (priceModifier.getPriceModifierCategories() == null) {
			map.put("priceModifierCategories", null);
		}
		else {
			map.put(
				"priceModifierCategories",
				String.valueOf(priceModifier.getPriceModifierCategories()));
		}

		if (priceModifier.getPriceModifierProductGroups() == null) {
			map.put("priceModifierProductGroups", null);
		}
		else {
			map.put(
				"priceModifierProductGroups",
				String.valueOf(priceModifier.getPriceModifierProductGroups()));
		}

		if (priceModifier.getPriceModifierProducts() == null) {
			map.put("priceModifierProducts", null);
		}
		else {
			map.put(
				"priceModifierProducts",
				String.valueOf(priceModifier.getPriceModifierProducts()));
		}

		if (priceModifier.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(priceModifier.getPriority()));
		}

		if (priceModifier.getTarget() == null) {
			map.put("target", null);
		}
		else {
			map.put("target", String.valueOf(priceModifier.getTarget()));
		}

		if (priceModifier.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(priceModifier.getTitle()));
		}

		return map;
	}

	public static class PriceModifierJSONParser
		extends BaseJSONParser<PriceModifier> {

		@Override
		protected PriceModifier createDTO() {
			return new PriceModifier();
		}

		@Override
		protected PriceModifier[] createDTOArray(int size) {
			return new PriceModifier[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifierAmount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifierType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierCategories")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierProductGroups")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierProducts")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "target")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PriceModifier priceModifier, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceModifier.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifierAmount")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setModifierAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifierType")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setModifierType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setNeverExpire((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceListExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceModifier.setPriceListExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceListId")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setPriceListId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierCategories")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceModifierCategory[] priceModifierCategoriesArray =
						new PriceModifierCategory[jsonParserFieldValues.length];

					for (int i = 0; i < priceModifierCategoriesArray.length;
						 i++) {

						priceModifierCategoriesArray[i] =
							PriceModifierCategorySerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					priceModifier.setPriceModifierCategories(
						priceModifierCategoriesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierProductGroups")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceModifierProductGroup[]
						priceModifierProductGroupsArray =
							new PriceModifierProductGroup
								[jsonParserFieldValues.length];

					for (int i = 0; i < priceModifierProductGroupsArray.length;
						 i++) {

						priceModifierProductGroupsArray[i] =
							PriceModifierProductGroupSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					priceModifier.setPriceModifierProductGroups(
						priceModifierProductGroupsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierProducts")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PriceModifierProduct[] priceModifierProductsArray =
						new PriceModifierProduct[jsonParserFieldValues.length];

					for (int i = 0; i < priceModifierProductsArray.length;
						 i++) {

						priceModifierProductsArray[i] =
							PriceModifierProductSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					priceModifier.setPriceModifierProducts(
						priceModifierProductsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "target")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setTarget((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					priceModifier.setTitle((String)jsonParserFieldValue);
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