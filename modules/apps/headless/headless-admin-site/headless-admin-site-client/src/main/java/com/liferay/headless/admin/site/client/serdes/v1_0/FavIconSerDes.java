/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIconClientExtension;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIconItemExternalReference;
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
public class FavIconSerDes {

	public static FavIcon toDTO(String json) {
		FavIconJSONParser favIconJSONParser = new FavIconJSONParser();

		return favIconJSONParser.parseToDTO(json);
	}

	public static FavIcon[] toDTOs(String json) {
		FavIconJSONParser favIconJSONParser = new FavIconJSONParser();

		return favIconJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FavIcon favIcon) {
		if (favIcon == null) {
			return "null";
		}

		FavIcon.FavIconType favIconType = favIcon.getFavIconType();

		if (favIconType != null) {
			String favIconTypeString = favIconType.toString();

			if (favIconTypeString.equals("ClientExtension")) {
				return FavIconClientExtensionSerDes.toJSON(
					(FavIconClientExtension)favIcon);
			}

			if (favIconTypeString.equals("ItemExternalReference")) {
				return FavIconItemExternalReferenceSerDes.toJSON(
					(FavIconItemExternalReference)favIcon);
			}

			throw new IllegalArgumentException(
				"Unknown favIconType " + favIconTypeString);
		}
		else {
			throw new IllegalArgumentException("Missing favIconType parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		FavIconJSONParser favIconJSONParser = new FavIconJSONParser();

		return favIconJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FavIcon favIcon) {
		if (favIcon == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (favIcon.getFavIconType() == null) {
			map.put("favIconType", null);
		}
		else {
			map.put("favIconType", String.valueOf(favIcon.getFavIconType()));
		}

		return map;
	}

	public static class FavIconJSONParser extends BaseJSONParser<FavIcon> {

		@Override
		protected FavIcon createDTO() {
			return null;
		}

		@Override
		protected FavIcon[] createDTOArray(int size) {
			return new FavIcon[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "favIconType")) {
				return false;
			}

			return false;
		}

		@Override
		public FavIcon parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object favIconType = jsonMap.get("favIconType");

			if (favIconType != null) {
				String favIconTypeString = favIconType.toString();

				if (favIconTypeString.equals("ClientExtension")) {
					return FavIconClientExtension.toDTO(json);
				}

				if (favIconTypeString.equals("ItemExternalReference")) {
					return FavIconItemExternalReference.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown favIconType " + favIconTypeString);
			}
			else {
				throw new IllegalArgumentException(
					"Missing favIconType parameter");
			}
		}

		@Override
		protected void setField(
			FavIcon favIcon, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "favIconType")) {
				if (jsonParserFieldValue != null) {
					favIcon.setFavIconType(
						FavIcon.FavIconType.create(
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