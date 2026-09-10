/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

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
public class SitePagesNavigationMenuValueSerDes {

	public static SitePagesNavigationMenuValue toDTO(String json) {
		SitePagesNavigationMenuValueJSONParser
			sitePagesNavigationMenuValueJSONParser =
				new SitePagesNavigationMenuValueJSONParser();

		return sitePagesNavigationMenuValueJSONParser.parseToDTO(json);
	}

	public static SitePagesNavigationMenuValue[] toDTOs(String json) {
		SitePagesNavigationMenuValueJSONParser
			sitePagesNavigationMenuValueJSONParser =
				new SitePagesNavigationMenuValueJSONParser();

		return sitePagesNavigationMenuValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SitePagesNavigationMenuValue sitePagesNavigationMenuValue) {

		if (sitePagesNavigationMenuValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sitePagesNavigationMenuValue.getPageSetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSetType\": ");

			sb.append("\"");
			sb.append(sitePagesNavigationMenuValue.getPageSetType());
			sb.append("\"");
		}

		if (sitePagesNavigationMenuValue.
				getParentSitePageExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSitePageExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					sitePagesNavigationMenuValue.
						getParentSitePageExternalReferenceCode()));

			sb.append("\"");
		}

		if (sitePagesNavigationMenuValue.getNavigationMenuType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuType\": ");

			sb.append("\"");
			sb.append(sitePagesNavigationMenuValue.getNavigationMenuType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SitePagesNavigationMenuValueJSONParser
			sitePagesNavigationMenuValueJSONParser =
				new SitePagesNavigationMenuValueJSONParser();

		return sitePagesNavigationMenuValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SitePagesNavigationMenuValue sitePagesNavigationMenuValue) {

		if (sitePagesNavigationMenuValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sitePagesNavigationMenuValue.getPageSetType() == null) {
			map.put("pageSetType", null);
		}
		else {
			map.put(
				"pageSetType",
				String.valueOf(sitePagesNavigationMenuValue.getPageSetType()));
		}

		if (sitePagesNavigationMenuValue.
				getParentSitePageExternalReferenceCode() == null) {

			map.put("parentSitePageExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentSitePageExternalReferenceCode",
				String.valueOf(
					sitePagesNavigationMenuValue.
						getParentSitePageExternalReferenceCode()));
		}

		if (sitePagesNavigationMenuValue.getNavigationMenuType() == null) {
			map.put("navigationMenuType", null);
		}
		else {
			map.put(
				"navigationMenuType",
				String.valueOf(
					sitePagesNavigationMenuValue.getNavigationMenuType()));
		}

		return map;
	}

	public static class SitePagesNavigationMenuValueJSONParser
		extends BaseJSONParser<SitePagesNavigationMenuValue> {

		@Override
		protected SitePagesNavigationMenuValue createDTO() {
			return new SitePagesNavigationMenuValue();
		}

		@Override
		protected SitePagesNavigationMenuValue[] createDTOArray(int size) {
			return new SitePagesNavigationMenuValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "pageSetType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentSitePageExternalReferenceCode")) {

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
			SitePagesNavigationMenuValue sitePagesNavigationMenuValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "pageSetType")) {
				if (jsonParserFieldValue != null) {
					sitePagesNavigationMenuValue.setPageSetType(
						SitePagesNavigationMenuValue.PageSetType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentSitePageExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					sitePagesNavigationMenuValue.
						setParentSitePageExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuType")) {

				if (jsonParserFieldValue != null) {
					sitePagesNavigationMenuValue.setNavigationMenuType(
						SitePagesNavigationMenuValue.NavigationMenuType.create(
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
// LIFERAY-REST-BUILDER-HASH:-476454191