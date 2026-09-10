/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ActionFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.BackgroundImageFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.DateFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.HTMLFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ImageFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.LinkFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentEditableElementValue;
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
public class FragmentEditableElementValueSerDes {

	public static FragmentEditableElementValue toDTO(String json) {
		FragmentEditableElementValueJSONParser
			fragmentEditableElementValueJSONParser =
				new FragmentEditableElementValueJSONParser();

		return fragmentEditableElementValueJSONParser.parseToDTO(json);
	}

	public static FragmentEditableElementValue[] toDTOs(String json) {
		FragmentEditableElementValueJSONParser
			fragmentEditableElementValueJSONParser =
				new FragmentEditableElementValueJSONParser();

		return fragmentEditableElementValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FragmentEditableElementValue fragmentEditableElementValue) {

		if (fragmentEditableElementValue == null) {
			return "null";
		}

		FragmentEditableElementValue.Type type =
			fragmentEditableElementValue.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("Action")) {
				return ActionFragmentEditableElementValueSerDes.toJSON(
					(ActionFragmentEditableElementValue)
						fragmentEditableElementValue);
			}

			if (typeString.equals("BackgroundImage")) {
				return BackgroundImageFragmentEditableElementValueSerDes.toJSON(
					(BackgroundImageFragmentEditableElementValue)
						fragmentEditableElementValue);
			}

			if (typeString.equals("Date")) {
				return DateFragmentEditableElementValueSerDes.toJSON(
					(DateFragmentEditableElementValue)
						fragmentEditableElementValue);
			}

			if (typeString.equals("HTML")) {
				return HTMLFragmentEditableElementValueSerDes.toJSON(
					(HTMLFragmentEditableElementValue)
						fragmentEditableElementValue);
			}

			if (typeString.equals("Image")) {
				return ImageFragmentEditableElementValueSerDes.toJSON(
					(ImageFragmentEditableElementValue)
						fragmentEditableElementValue);
			}

			if (typeString.equals("Link")) {
				return LinkFragmentEditableElementValueSerDes.toJSON(
					(LinkFragmentEditableElementValue)
						fragmentEditableElementValue);
			}

			if (typeString.equals("RichText")) {
				return HTMLFragmentEditableElementValueSerDes.toJSON(
					(HTMLFragmentEditableElementValue)
						fragmentEditableElementValue);
			}

			if (typeString.equals("Text")) {
				return TextFragmentEditableElementValueSerDes.toJSON(
					(TextFragmentEditableElementValue)
						fragmentEditableElementValue);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		FragmentEditableElementValueJSONParser
			fragmentEditableElementValueJSONParser =
				new FragmentEditableElementValueJSONParser();

		return fragmentEditableElementValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentEditableElementValue fragmentEditableElementValue) {

		if (fragmentEditableElementValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentEditableElementValue.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(fragmentEditableElementValue.getType()));
		}

		return map;
	}

	public static class FragmentEditableElementValueJSONParser
		extends BaseJSONParser<FragmentEditableElementValue> {

		@Override
		protected FragmentEditableElementValue createDTO() {
			return null;
		}

		@Override
		protected FragmentEditableElementValue[] createDTOArray(int size) {
			return new FragmentEditableElementValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public FragmentEditableElementValue parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("Action")) {
					return ActionFragmentEditableElementValue.toDTO(json);
				}

				if (typeString.equals("BackgroundImage")) {
					return BackgroundImageFragmentEditableElementValue.toDTO(
						json);
				}

				if (typeString.equals("Date")) {
					return DateFragmentEditableElementValue.toDTO(json);
				}

				if (typeString.equals("HTML")) {
					return HTMLFragmentEditableElementValue.toDTO(json);
				}

				if (typeString.equals("Image")) {
					return ImageFragmentEditableElementValue.toDTO(json);
				}

				if (typeString.equals("Link")) {
					return LinkFragmentEditableElementValue.toDTO(json);
				}

				if (typeString.equals("RichText")) {
					return HTMLFragmentEditableElementValue.toDTO(json);
				}

				if (typeString.equals("Text")) {
					return TextFragmentEditableElementValue.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			FragmentEditableElementValue fragmentEditableElementValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					fragmentEditableElementValue.setType(
						FragmentEditableElementValue.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-1665129600