/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CategoryFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.CheckboxFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ColorPaletteFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ColorPickerFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.LengthFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.NavigationMenuFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.SelectFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.TargetCollectionDisplayFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.URLFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.VideoFragmentConfigurationFieldValue;
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
public class FragmentConfigurationFieldValueSerDes {

	public static FragmentConfigurationFieldValue toDTO(String json) {
		FragmentConfigurationFieldValueJSONParser
			fragmentConfigurationFieldValueJSONParser =
				new FragmentConfigurationFieldValueJSONParser();

		return fragmentConfigurationFieldValueJSONParser.parseToDTO(json);
	}

	public static FragmentConfigurationFieldValue[] toDTOs(String json) {
		FragmentConfigurationFieldValueJSONParser
			fragmentConfigurationFieldValueJSONParser =
				new FragmentConfigurationFieldValueJSONParser();

		return fragmentConfigurationFieldValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FragmentConfigurationFieldValue fragmentConfigurationFieldValue) {

		if (fragmentConfigurationFieldValue == null) {
			return "null";
		}

		FragmentConfigurationFieldValue.Type type =
			fragmentConfigurationFieldValue.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("Category")) {
				return CategoryFragmentConfigurationFieldValueSerDes.toJSON(
					(CategoryFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("Checkbox")) {
				return CheckboxFragmentConfigurationFieldValueSerDes.toJSON(
					(CheckboxFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("Collection")) {
				return CollectionFragmentConfigurationFieldValueSerDes.toJSON(
					(CollectionFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("ColorPalette")) {
				return ColorPaletteFragmentConfigurationFieldValueSerDes.toJSON(
					(ColorPaletteFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("ColorPicker")) {
				return ColorPickerFragmentConfigurationFieldValueSerDes.toJSON(
					(ColorPickerFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("Item")) {
				return ItemFragmentConfigurationFieldValueSerDes.toJSON(
					(ItemFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("Length")) {
				return LengthFragmentConfigurationFieldValueSerDes.toJSON(
					(LengthFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("NavigationMenu")) {
				return NavigationMenuFragmentConfigurationFieldValueSerDes.
					toJSON(
						(NavigationMenuFragmentConfigurationFieldValue)
							fragmentConfigurationFieldValue);
			}

			if (typeString.equals("Select")) {
				return SelectFragmentConfigurationFieldValueSerDes.toJSON(
					(SelectFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("TargetCollectionDisplay")) {
				return TargetCollectionDisplayFragmentConfigurationFieldValueSerDes.
					toJSON(
						(TargetCollectionDisplayFragmentConfigurationFieldValue)
							fragmentConfigurationFieldValue);
			}

			if (typeString.equals("Text")) {
				return TextFragmentConfigurationFieldValueSerDes.toJSON(
					(TextFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("URL")) {
				return URLFragmentConfigurationFieldValueSerDes.toJSON(
					(URLFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			if (typeString.equals("Video")) {
				return VideoFragmentConfigurationFieldValueSerDes.toJSON(
					(VideoFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		FragmentConfigurationFieldValueJSONParser
			fragmentConfigurationFieldValueJSONParser =
				new FragmentConfigurationFieldValueJSONParser();

		return fragmentConfigurationFieldValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentConfigurationFieldValue fragmentConfigurationFieldValue) {

		if (fragmentConfigurationFieldValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentConfigurationFieldValue.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(fragmentConfigurationFieldValue.getType()));
		}

		return map;
	}

	public static class FragmentConfigurationFieldValueJSONParser
		extends BaseJSONParser<FragmentConfigurationFieldValue> {

		@Override
		protected FragmentConfigurationFieldValue createDTO() {
			return null;
		}

		@Override
		protected FragmentConfigurationFieldValue[] createDTOArray(int size) {
			return new FragmentConfigurationFieldValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public FragmentConfigurationFieldValue parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("Category")) {
					return CategoryFragmentConfigurationFieldValue.toDTO(json);
				}

				if (typeString.equals("Checkbox")) {
					return CheckboxFragmentConfigurationFieldValue.toDTO(json);
				}

				if (typeString.equals("Collection")) {
					return CollectionFragmentConfigurationFieldValue.toDTO(
						json);
				}

				if (typeString.equals("ColorPalette")) {
					return ColorPaletteFragmentConfigurationFieldValue.toDTO(
						json);
				}

				if (typeString.equals("ColorPicker")) {
					return ColorPickerFragmentConfigurationFieldValue.toDTO(
						json);
				}

				if (typeString.equals("Item")) {
					return ItemFragmentConfigurationFieldValue.toDTO(json);
				}

				if (typeString.equals("Length")) {
					return LengthFragmentConfigurationFieldValue.toDTO(json);
				}

				if (typeString.equals("NavigationMenu")) {
					return NavigationMenuFragmentConfigurationFieldValue.toDTO(
						json);
				}

				if (typeString.equals("Select")) {
					return SelectFragmentConfigurationFieldValue.toDTO(json);
				}

				if (typeString.equals("TargetCollectionDisplay")) {
					return TargetCollectionDisplayFragmentConfigurationFieldValue.
						toDTO(json);
				}

				if (typeString.equals("Text")) {
					return TextFragmentConfigurationFieldValue.toDTO(json);
				}

				if (typeString.equals("URL")) {
					return URLFragmentConfigurationFieldValue.toDTO(json);
				}

				if (typeString.equals("Video")) {
					return VideoFragmentConfigurationFieldValue.toDTO(json);
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
			FragmentConfigurationFieldValue fragmentConfigurationFieldValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					fragmentConfigurationFieldValue.setType(
						FragmentConfigurationFieldValue.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-2012017422