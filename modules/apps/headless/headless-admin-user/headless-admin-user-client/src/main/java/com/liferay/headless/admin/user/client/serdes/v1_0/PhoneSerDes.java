/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PhoneSerDes {

	public static Phone toDTO(String json) {
		PhoneJSONParser phoneJSONParser = new PhoneJSONParser();

		return phoneJSONParser.parseToDTO(json);
	}

	public static Phone[] toDTOs(String json) {
		PhoneJSONParser phoneJSONParser = new PhoneJSONParser();

		return phoneJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Phone phone) {
		if (phone == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (phone.getExtension() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"extension\": ");

			sb.append("\"");

			sb.append(_escape(phone.getExtension()));

			sb.append("\"");
		}

		if (phone.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(phone.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (phone.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(phone.getId());
		}

		if (phone.getPhoneNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumber\": ");

			sb.append("\"");

			sb.append(_escape(phone.getPhoneNumber()));

			sb.append("\"");
		}

		if (phone.getPhoneType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneType\": ");

			sb.append("\"");

			sb.append(_escape(phone.getPhoneType()));

			sb.append("\"");
		}

		if (phone.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(phone.getPrimary());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PhoneJSONParser phoneJSONParser = new PhoneJSONParser();

		return phoneJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Phone phone) {
		if (phone == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (phone.getExtension() == null) {
			map.put("extension", null);
		}
		else {
			map.put("extension", String.valueOf(phone.getExtension()));
		}

		if (phone.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(phone.getExternalReferenceCode()));
		}

		if (phone.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(phone.getId()));
		}

		if (phone.getPhoneNumber() == null) {
			map.put("phoneNumber", null);
		}
		else {
			map.put("phoneNumber", String.valueOf(phone.getPhoneNumber()));
		}

		if (phone.getPhoneType() == null) {
			map.put("phoneType", null);
		}
		else {
			map.put("phoneType", String.valueOf(phone.getPhoneType()));
		}

		if (phone.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put("primary", String.valueOf(phone.getPrimary()));
		}

		return map;
	}

	public static class PhoneJSONParser extends BaseJSONParser<Phone> {

		@Override
		protected Phone createDTO() {
			return new Phone();
		}

		@Override
		protected Phone[] createDTOArray(int size) {
			return new Phone[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "extension")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "phoneType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Phone phone, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "extension")) {
				if (jsonParserFieldValue != null) {
					phone.setExtension((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					phone.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					phone.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				if (jsonParserFieldValue != null) {
					phone.setPhoneNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "phoneType")) {
				if (jsonParserFieldValue != null) {
					phone.setPhoneType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					phone.setPrimary((Boolean)jsonParserFieldValue);
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