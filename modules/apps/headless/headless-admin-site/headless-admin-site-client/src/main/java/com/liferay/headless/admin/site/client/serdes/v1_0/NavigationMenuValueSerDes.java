/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContextualMenuNavigationMenuValue;
import com.liferay.headless.admin.site.client.dto.v1_0.NavigationMenuValue;
import com.liferay.headless.admin.site.client.dto.v1_0.SiteMenuNavigationMenuValue;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePagesNavigationMenuValue;
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
public class NavigationMenuValueSerDes {

	public static NavigationMenuValue toDTO(String json) {
		NavigationMenuValueJSONParser navigationMenuValueJSONParser =
			new NavigationMenuValueJSONParser();

		return navigationMenuValueJSONParser.parseToDTO(json);
	}

	public static NavigationMenuValue[] toDTOs(String json) {
		NavigationMenuValueJSONParser navigationMenuValueJSONParser =
			new NavigationMenuValueJSONParser();

		return navigationMenuValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(NavigationMenuValue navigationMenuValue) {
		if (navigationMenuValue == null) {
			return "null";
		}

		NavigationMenuValue.NavigationMenuType navigationMenuType =
			navigationMenuValue.getNavigationMenuType();

		if (navigationMenuType != null) {
			String navigationMenuTypeString = navigationMenuType.toString();

			if (navigationMenuTypeString.equals("ContextualMenu")) {
				return ContextualMenuNavigationMenuValueSerDes.toJSON(
					(ContextualMenuNavigationMenuValue)navigationMenuValue);
			}

			if (navigationMenuTypeString.equals("SiteMenu")) {
				return SiteMenuNavigationMenuValueSerDes.toJSON(
					(SiteMenuNavigationMenuValue)navigationMenuValue);
			}

			if (navigationMenuTypeString.equals("SitePages")) {
				return SitePagesNavigationMenuValueSerDes.toJSON(
					(SitePagesNavigationMenuValue)navigationMenuValue);
			}

			throw new IllegalArgumentException(
				"Unknown navigationMenuType " + navigationMenuTypeString);
		}
		else {
			throw new IllegalArgumentException(
				"Missing navigationMenuType parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		NavigationMenuValueJSONParser navigationMenuValueJSONParser =
			new NavigationMenuValueJSONParser();

		return navigationMenuValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		NavigationMenuValue navigationMenuValue) {

		if (navigationMenuValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (navigationMenuValue.getNavigationMenuType() == null) {
			map.put("navigationMenuType", null);
		}
		else {
			map.put(
				"navigationMenuType",
				String.valueOf(navigationMenuValue.getNavigationMenuType()));
		}

		return map;
	}

	public static class NavigationMenuValueJSONParser
		extends BaseJSONParser<NavigationMenuValue> {

		@Override
		protected NavigationMenuValue createDTO() {
			return null;
		}

		@Override
		protected NavigationMenuValue[] createDTOArray(int size) {
			return new NavigationMenuValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "navigationMenuType")) {
				return false;
			}

			return false;
		}

		@Override
		public NavigationMenuValue parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object navigationMenuType = jsonMap.get("navigationMenuType");

			if (navigationMenuType != null) {
				String navigationMenuTypeString = navigationMenuType.toString();

				if (navigationMenuTypeString.equals("ContextualMenu")) {
					return ContextualMenuNavigationMenuValue.toDTO(json);
				}

				if (navigationMenuTypeString.equals("SiteMenu")) {
					return SiteMenuNavigationMenuValue.toDTO(json);
				}

				if (navigationMenuTypeString.equals("SitePages")) {
					return SitePagesNavigationMenuValue.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown navigationMenuType " + navigationMenuTypeString);
			}
			else {
				throw new IllegalArgumentException(
					"Missing navigationMenuType parameter");
			}
		}

		@Override
		protected void setField(
			NavigationMenuValue navigationMenuValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "navigationMenuType")) {
				if (jsonParserFieldValue != null) {
					navigationMenuValue.setNavigationMenuType(
						NavigationMenuValue.NavigationMenuType.create(
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
// LIFERAY-REST-BUILDER-HASH:-1639611650