/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.PostalAddress;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PostalAddressSerDes {

	public static PostalAddress toDTO(String json) {
		PostalAddressJSONParser postalAddressJSONParser =
			new PostalAddressJSONParser();

		return postalAddressJSONParser.parseToDTO(json);
	}

	public static PostalAddress[] toDTOs(String json) {
		PostalAddressJSONParser postalAddressJSONParser =
			new PostalAddressJSONParser();

		return postalAddressJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PostalAddress postalAddress) {
		if (postalAddress == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (postalAddress.getAddressCountry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountry\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getAddressCountry()));

			sb.append("\"");
		}

		if (postalAddress.getAddressCountry_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountry_i18n\": ");

			sb.append(_toJSON(postalAddress.getAddressCountry_i18n()));
		}

		if (postalAddress.getAddressLocality() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressLocality\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getAddressLocality()));

			sb.append("\"");
		}

		if (postalAddress.getAddressRegion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressRegion\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getAddressRegion()));

			sb.append("\"");
		}

		if (postalAddress.getAddressSubtype() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressSubtype\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getAddressSubtype()));

			sb.append("\"");
		}

		if (postalAddress.getAddressType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressType\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getAddressType()));

			sb.append("\"");
		}

		if (postalAddress.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (postalAddress.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(postalAddress.getId());
		}

		if (postalAddress.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getName()));

			sb.append("\"");
		}

		if (postalAddress.getPhoneNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumber\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getPhoneNumber()));

			sb.append("\"");
		}

		if (postalAddress.getPostalCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postalCode\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getPostalCode()));

			sb.append("\"");
		}

		if (postalAddress.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(postalAddress.getPrimary());
		}

		if (postalAddress.getStreetAddressLine1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"streetAddressLine1\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getStreetAddressLine1()));

			sb.append("\"");
		}

		if (postalAddress.getStreetAddressLine2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"streetAddressLine2\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getStreetAddressLine2()));

			sb.append("\"");
		}

		if (postalAddress.getStreetAddressLine3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"streetAddressLine3\": ");

			sb.append("\"");

			sb.append(_escape(postalAddress.getStreetAddressLine3()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PostalAddressJSONParser postalAddressJSONParser =
			new PostalAddressJSONParser();

		return postalAddressJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PostalAddress postalAddress) {
		if (postalAddress == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (postalAddress.getAddressCountry() == null) {
			map.put("addressCountry", null);
		}
		else {
			map.put(
				"addressCountry",
				String.valueOf(postalAddress.getAddressCountry()));
		}

		if (postalAddress.getAddressCountry_i18n() == null) {
			map.put("addressCountry_i18n", null);
		}
		else {
			map.put(
				"addressCountry_i18n",
				String.valueOf(postalAddress.getAddressCountry_i18n()));
		}

		if (postalAddress.getAddressLocality() == null) {
			map.put("addressLocality", null);
		}
		else {
			map.put(
				"addressLocality",
				String.valueOf(postalAddress.getAddressLocality()));
		}

		if (postalAddress.getAddressRegion() == null) {
			map.put("addressRegion", null);
		}
		else {
			map.put(
				"addressRegion",
				String.valueOf(postalAddress.getAddressRegion()));
		}

		if (postalAddress.getAddressSubtype() == null) {
			map.put("addressSubtype", null);
		}
		else {
			map.put(
				"addressSubtype",
				String.valueOf(postalAddress.getAddressSubtype()));
		}

		if (postalAddress.getAddressType() == null) {
			map.put("addressType", null);
		}
		else {
			map.put(
				"addressType", String.valueOf(postalAddress.getAddressType()));
		}

		if (postalAddress.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(postalAddress.getExternalReferenceCode()));
		}

		if (postalAddress.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(postalAddress.getId()));
		}

		if (postalAddress.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(postalAddress.getName()));
		}

		if (postalAddress.getPhoneNumber() == null) {
			map.put("phoneNumber", null);
		}
		else {
			map.put(
				"phoneNumber", String.valueOf(postalAddress.getPhoneNumber()));
		}

		if (postalAddress.getPostalCode() == null) {
			map.put("postalCode", null);
		}
		else {
			map.put(
				"postalCode", String.valueOf(postalAddress.getPostalCode()));
		}

		if (postalAddress.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put("primary", String.valueOf(postalAddress.getPrimary()));
		}

		if (postalAddress.getStreetAddressLine1() == null) {
			map.put("streetAddressLine1", null);
		}
		else {
			map.put(
				"streetAddressLine1",
				String.valueOf(postalAddress.getStreetAddressLine1()));
		}

		if (postalAddress.getStreetAddressLine2() == null) {
			map.put("streetAddressLine2", null);
		}
		else {
			map.put(
				"streetAddressLine2",
				String.valueOf(postalAddress.getStreetAddressLine2()));
		}

		if (postalAddress.getStreetAddressLine3() == null) {
			map.put("streetAddressLine3", null);
		}
		else {
			map.put(
				"streetAddressLine3",
				String.valueOf(postalAddress.getStreetAddressLine3()));
		}

		return map;
	}

	public static class PostalAddressJSONParser
		extends BaseJSONParser<PostalAddress> {

		@Override
		protected PostalAddress createDTO() {
			return new PostalAddress();
		}

		@Override
		protected PostalAddress[] createDTOArray(int size) {
			return new PostalAddress[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "addressCountry")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "addressCountry_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "addressLocality")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "addressRegion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "addressSubtype")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "addressType")) {
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
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "postalCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "streetAddressLine1")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "streetAddressLine2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "streetAddressLine3")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PostalAddress postalAddress, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "addressCountry")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setAddressCountry(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "addressCountry_i18n")) {

				if (jsonParserFieldValue != null) {
					postalAddress.setAddressCountry_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addressLocality")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setAddressLocality(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addressRegion")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setAddressRegion(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addressSubtype")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setAddressSubtype(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addressType")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setAddressType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					postalAddress.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setPhoneNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "postalCode")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setPostalCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					postalAddress.setPrimary((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "streetAddressLine1")) {

				if (jsonParserFieldValue != null) {
					postalAddress.setStreetAddressLine1(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "streetAddressLine2")) {

				if (jsonParserFieldValue != null) {
					postalAddress.setStreetAddressLine2(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "streetAddressLine3")) {

				if (jsonParserFieldValue != null) {
					postalAddress.setStreetAddressLine3(
						(String)jsonParserFieldValue);
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