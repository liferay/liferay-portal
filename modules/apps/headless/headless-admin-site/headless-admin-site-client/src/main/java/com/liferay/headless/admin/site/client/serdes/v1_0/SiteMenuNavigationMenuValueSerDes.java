/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.SiteMenuNavigationMenuValue;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class SiteMenuNavigationMenuValueSerDes {

	public static SiteMenuNavigationMenuValue toDTO(String json) {
		SiteMenuNavigationMenuValueJSONParser
			siteMenuNavigationMenuValueJSONParser =
				new SiteMenuNavigationMenuValueJSONParser();

		return siteMenuNavigationMenuValueJSONParser.parseToDTO(json);
	}

	public static SiteMenuNavigationMenuValue[] toDTOs(String json) {
		SiteMenuNavigationMenuValueJSONParser
			siteMenuNavigationMenuValueJSONParser =
				new SiteMenuNavigationMenuValueJSONParser();

		return siteMenuNavigationMenuValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SiteMenuNavigationMenuValue siteMenuNavigationMenuValue) {

		if (siteMenuNavigationMenuValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (siteMenuNavigationMenuValue.
				getNavigationMenuItemExternalReference() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuItemExternalReference\": ");

			sb.append(
				String.valueOf(
					siteMenuNavigationMenuValue.
						getNavigationMenuItemExternalReference()));
		}

		if (siteMenuNavigationMenuValue.
				getParentMenuItemExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentMenuItemExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					siteMenuNavigationMenuValue.
						getParentMenuItemExternalReferenceCode()));

			sb.append("\"");
		}

		if (siteMenuNavigationMenuValue.getNavigationMenuType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuType\": ");

			sb.append("\"");
			sb.append(siteMenuNavigationMenuValue.getNavigationMenuType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteMenuNavigationMenuValueJSONParser
			siteMenuNavigationMenuValueJSONParser =
				new SiteMenuNavigationMenuValueJSONParser();

		return siteMenuNavigationMenuValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SiteMenuNavigationMenuValue siteMenuNavigationMenuValue) {

		if (siteMenuNavigationMenuValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (siteMenuNavigationMenuValue.
				getNavigationMenuItemExternalReference() == null) {

			map.put("navigationMenuItemExternalReference", null);
		}
		else {
			map.put(
				"navigationMenuItemExternalReference",
				String.valueOf(
					siteMenuNavigationMenuValue.
						getNavigationMenuItemExternalReference()));
		}

		if (siteMenuNavigationMenuValue.
				getParentMenuItemExternalReferenceCode() == null) {

			map.put("parentMenuItemExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentMenuItemExternalReferenceCode",
				String.valueOf(
					siteMenuNavigationMenuValue.
						getParentMenuItemExternalReferenceCode()));
		}

		if (siteMenuNavigationMenuValue.getNavigationMenuType() == null) {
			map.put("navigationMenuType", null);
		}
		else {
			map.put(
				"navigationMenuType",
				String.valueOf(
					siteMenuNavigationMenuValue.getNavigationMenuType()));
		}

		return map;
	}

	public static class SiteMenuNavigationMenuValueJSONParser
		extends BaseJSONParser<SiteMenuNavigationMenuValue> {

		@Override
		protected SiteMenuNavigationMenuValue createDTO() {
			return new SiteMenuNavigationMenuValue();
		}

		@Override
		protected SiteMenuNavigationMenuValue[] createDTOArray(int size) {
			return new SiteMenuNavigationMenuValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName,
					"navigationMenuItemExternalReference")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentMenuItemExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuType")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SiteMenuNavigationMenuValue siteMenuNavigationMenuValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName,
					"navigationMenuItemExternalReference")) {

				if (jsonParserFieldValue != null) {
					siteMenuNavigationMenuValue.
						setNavigationMenuItemExternalReference(
							ItemExternalReferenceSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentMenuItemExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					siteMenuNavigationMenuValue.
						setParentMenuItemExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuType")) {

				if (jsonParserFieldValue != null) {
					siteMenuNavigationMenuValue.setNavigationMenuType(
						SiteMenuNavigationMenuValue.NavigationMenuType.create(
							(String)jsonParserFieldValue));
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:1781489108