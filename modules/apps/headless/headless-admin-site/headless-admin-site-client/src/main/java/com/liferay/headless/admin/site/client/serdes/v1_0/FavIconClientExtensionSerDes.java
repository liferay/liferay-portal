/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FavIconClientExtension;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class FavIconClientExtensionSerDes {

	public static FavIconClientExtension toDTO(String json) {
		FavIconClientExtensionJSONParser favIconClientExtensionJSONParser =
			new FavIconClientExtensionJSONParser();

		return favIconClientExtensionJSONParser.parseToDTO(json);
	}

	public static FavIconClientExtension[] toDTOs(String json) {
		FavIconClientExtensionJSONParser favIconClientExtensionJSONParser =
			new FavIconClientExtensionJSONParser();

		return favIconClientExtensionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FavIconClientExtension favIconClientExtension) {
		if (favIconClientExtension == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (favIconClientExtension.getClientExtensionConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clientExtensionConfig\": ");

			sb.append(
				_toJSON(favIconClientExtension.getClientExtensionConfig()));
		}

		if (favIconClientExtension.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(favIconClientExtension.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (favIconClientExtension.getFavIconType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"favIconType\": ");

			sb.append("\"");
			sb.append(favIconClientExtension.getFavIconType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FavIconClientExtensionJSONParser favIconClientExtensionJSONParser =
			new FavIconClientExtensionJSONParser();

		return favIconClientExtensionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FavIconClientExtension favIconClientExtension) {

		if (favIconClientExtension == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (favIconClientExtension.getClientExtensionConfig() == null) {
			map.put("clientExtensionConfig", null);
		}
		else {
			map.put(
				"clientExtensionConfig",
				String.valueOf(
					favIconClientExtension.getClientExtensionConfig()));
		}

		if (favIconClientExtension.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					favIconClientExtension.getExternalReferenceCode()));
		}

		if (favIconClientExtension.getFavIconType() == null) {
			map.put("favIconType", null);
		}
		else {
			map.put(
				"favIconType",
				String.valueOf(favIconClientExtension.getFavIconType()));
		}

		return map;
	}

	public static class FavIconClientExtensionJSONParser
		extends BaseJSONParser<FavIconClientExtension> {

		@Override
		protected FavIconClientExtension createDTO() {
			return new FavIconClientExtension();
		}

		@Override
		protected FavIconClientExtension[] createDTOArray(int size) {
			return new FavIconClientExtension[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "clientExtensionConfig")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "favIconType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FavIconClientExtension favIconClientExtension,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "clientExtensionConfig")) {
				if (jsonParserFieldValue != null) {
					favIconClientExtension.setClientExtensionConfig(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					favIconClientExtension.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "favIconType")) {
				if (jsonParserFieldValue != null) {
					favIconClientExtension.setFavIconType(
						FavIconClientExtension.FavIconType.create(
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