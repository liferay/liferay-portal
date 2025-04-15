/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.client.serdes.v1_0;

import com.liferay.headless.admin.address.client.dto.v1_0.Country;
import com.liferay.headless.admin.address.client.dto.v1_0.Region;
import com.liferay.headless.admin.address.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Drew Brokke
 * @generated
 */
@Generated("")
public class CountrySerDes {

	public static Country toDTO(String json) {
		CountryJSONParser countryJSONParser = new CountryJSONParser();

		return countryJSONParser.parseToDTO(json);
	}

	public static Country[] toDTOs(String json) {
		CountryJSONParser countryJSONParser = new CountryJSONParser();

		return countryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Country country) {
		if (country == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (country.getA2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"a2\": ");

			sb.append("\"");

			sb.append(_escape(country.getA2()));

			sb.append("\"");
		}

		if (country.getA3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"a3\": ");

			sb.append("\"");

			sb.append(_escape(country.getA3()));

			sb.append("\"");
		}

		if (country.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(country.getActive());
		}

		if (country.getBillingAllowed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"billingAllowed\": ");

			sb.append(country.getBillingAllowed());
		}

		if (country.getGroupFilterEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupFilterEnabled\": ");

			sb.append(country.getGroupFilterEnabled());
		}

		if (country.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(country.getId());
		}

		if (country.getIdd() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"idd\": ");

			sb.append(country.getIdd());
		}

		if (country.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(country.getName()));

			sb.append("\"");
		}

		if (country.getNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"number\": ");

			sb.append(country.getNumber());
		}

		if (country.getPosition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(country.getPosition());
		}

		if (country.getRegions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"regions\": ");

			sb.append("[");

			for (int i = 0; i < country.getRegions().length; i++) {
				sb.append(String.valueOf(country.getRegions()[i]));

				if ((i + 1) < country.getRegions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (country.getShippingAllowed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAllowed\": ");

			sb.append(country.getShippingAllowed());
		}

		if (country.getSubjectToVAT() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subjectToVAT\": ");

			sb.append(country.getSubjectToVAT());
		}

		if (country.getTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(country.getTitle_i18n()));
		}

		if (country.getZipRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"zipRequired\": ");

			sb.append(country.getZipRequired());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CountryJSONParser countryJSONParser = new CountryJSONParser();

		return countryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Country country) {
		if (country == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (country.getA2() == null) {
			map.put("a2", null);
		}
		else {
			map.put("a2", String.valueOf(country.getA2()));
		}

		if (country.getA3() == null) {
			map.put("a3", null);
		}
		else {
			map.put("a3", String.valueOf(country.getA3()));
		}

		if (country.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(country.getActive()));
		}

		if (country.getBillingAllowed() == null) {
			map.put("billingAllowed", null);
		}
		else {
			map.put(
				"billingAllowed", String.valueOf(country.getBillingAllowed()));
		}

		if (country.getGroupFilterEnabled() == null) {
			map.put("groupFilterEnabled", null);
		}
		else {
			map.put(
				"groupFilterEnabled",
				String.valueOf(country.getGroupFilterEnabled()));
		}

		if (country.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(country.getId()));
		}

		if (country.getIdd() == null) {
			map.put("idd", null);
		}
		else {
			map.put("idd", String.valueOf(country.getIdd()));
		}

		if (country.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(country.getName()));
		}

		if (country.getNumber() == null) {
			map.put("number", null);
		}
		else {
			map.put("number", String.valueOf(country.getNumber()));
		}

		if (country.getPosition() == null) {
			map.put("position", null);
		}
		else {
			map.put("position", String.valueOf(country.getPosition()));
		}

		if (country.getRegions() == null) {
			map.put("regions", null);
		}
		else {
			map.put("regions", String.valueOf(country.getRegions()));
		}

		if (country.getShippingAllowed() == null) {
			map.put("shippingAllowed", null);
		}
		else {
			map.put(
				"shippingAllowed",
				String.valueOf(country.getShippingAllowed()));
		}

		if (country.getSubjectToVAT() == null) {
			map.put("subjectToVAT", null);
		}
		else {
			map.put("subjectToVAT", String.valueOf(country.getSubjectToVAT()));
		}

		if (country.getTitle_i18n() == null) {
			map.put("title_i18n", null);
		}
		else {
			map.put("title_i18n", String.valueOf(country.getTitle_i18n()));
		}

		if (country.getZipRequired() == null) {
			map.put("zipRequired", null);
		}
		else {
			map.put("zipRequired", String.valueOf(country.getZipRequired()));
		}

		return map;
	}

	public static class CountryJSONParser extends BaseJSONParser<Country> {

		@Override
		protected Country createDTO() {
			return new Country();
		}

		@Override
		protected Country[] createDTOArray(int size) {
			return new Country[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "a2")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "a3")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "billingAllowed")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "groupFilterEnabled")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "idd")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "number")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "regions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAllowed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subjectToVAT")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "zipRequired")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Country country, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "a2")) {
				if (jsonParserFieldValue != null) {
					country.setA2((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "a3")) {
				if (jsonParserFieldValue != null) {
					country.setA3((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					country.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "billingAllowed")) {
				if (jsonParserFieldValue != null) {
					country.setBillingAllowed((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "groupFilterEnabled")) {

				if (jsonParserFieldValue != null) {
					country.setGroupFilterEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					country.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "idd")) {
				if (jsonParserFieldValue != null) {
					country.setIdd(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					country.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "number")) {
				if (jsonParserFieldValue != null) {
					country.setNumber(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				if (jsonParserFieldValue != null) {
					country.setPosition(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "regions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Region[] regionsArray =
						new Region[jsonParserFieldValues.length];

					for (int i = 0; i < regionsArray.length; i++) {
						regionsArray[i] = RegionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					country.setRegions(regionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAllowed")) {
				if (jsonParserFieldValue != null) {
					country.setShippingAllowed((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subjectToVAT")) {
				if (jsonParserFieldValue != null) {
					country.setSubjectToVAT((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				if (jsonParserFieldValue != null) {
					country.setTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "zipRequired")) {
				if (jsonParserFieldValue != null) {
					country.setZipRequired((Boolean)jsonParserFieldValue);
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