/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.CategoryTreeNodeSelectorField;
import com.liferay.headless.admin.fragment.client.dto.v1_0.CheckboxField;
import com.liferay.headless.admin.fragment.client.dto.v1_0.ColorPaletteField;
import com.liferay.headless.admin.fragment.client.dto.v1_0.ColorPickerField;
import com.liferay.headless.admin.fragment.client.dto.v1_0.Field;
import com.liferay.headless.admin.fragment.client.dto.v1_0.ItemSelectorField;
import com.liferay.headless.admin.fragment.client.dto.v1_0.LengthField;
import com.liferay.headless.admin.fragment.client.dto.v1_0.SelectField;
import com.liferay.headless.admin.fragment.client.dto.v1_0.TextField;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

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
public class FieldSerDes {

	public static Field toDTO(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToDTO(json);
	}

	public static Field[] toDTOs(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Field field) {
		if (field == null) {
			return "null";
		}

		Field.Type type = field.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("categoryTreeNodeSelector")) {
				return CategoryTreeNodeSelectorFieldSerDes.toJSON(
					(CategoryTreeNodeSelectorField)field);
			}

			if (typeString.equals("checkbox")) {
				return CheckboxFieldSerDes.toJSON((CheckboxField)field);
			}

			if (typeString.equals("colorPalette")) {
				return ColorPaletteFieldSerDes.toJSON((ColorPaletteField)field);
			}

			if (typeString.equals("colorPicker")) {
				return ColorPickerFieldSerDes.toJSON((ColorPickerField)field);
			}

			if (typeString.equals("itemSelector")) {
				return ItemSelectorFieldSerDes.toJSON((ItemSelectorField)field);
			}

			if (typeString.equals("length")) {
				return LengthFieldSerDes.toJSON((LengthField)field);
			}

			if (typeString.equals("select")) {
				return SelectFieldSerDes.toJSON((SelectField)field);
			}

			if (typeString.equals("text")) {
				return TextFieldSerDes.toJSON((TextField)field);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		FieldJSONParser fieldJSONParser = new FieldJSONParser();

		return fieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Field field) {
		if (field == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (field.getDataType() == null) {
			map.put("dataType", null);
		}
		else {
			map.put("dataType", String.valueOf(field.getDataType()));
		}

		if (field.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(field.getDescription()));
		}

		if (field.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(field.getLabel()));
		}

		if (field.getLocalizable() == null) {
			map.put("localizable", null);
		}
		else {
			map.put("localizable", String.valueOf(field.getLocalizable()));
		}

		if (field.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(field.getName()));
		}

		if (field.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(field.getType()));
		}

		return map;
	}

	public static class FieldJSONParser extends BaseJSONParser<Field> {

		@Override
		protected Field createDTO() {
			return null;
		}

		@Override
		protected Field[] createDTOArray(int size) {
			return new Field[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dataType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public Field parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("categoryTreeNodeSelector")) {
					return CategoryTreeNodeSelectorField.toDTO(json);
				}

				if (typeString.equals("checkbox")) {
					return CheckboxField.toDTO(json);
				}

				if (typeString.equals("colorPalette")) {
					return ColorPaletteField.toDTO(json);
				}

				if (typeString.equals("colorPicker")) {
					return ColorPickerField.toDTO(json);
				}

				if (typeString.equals("itemSelector")) {
					return ItemSelectorField.toDTO(json);
				}

				if (typeString.equals("length")) {
					return LengthField.toDTO(json);
				}

				if (typeString.equals("select")) {
					return SelectField.toDTO(json);
				}

				if (typeString.equals("text")) {
					return TextField.toDTO(json);
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
			Field field, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dataType")) {
				if (jsonParserFieldValue != null) {
					field.setDataType(
						Field.DataType.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					field.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					field.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "localizable")) {
				if (jsonParserFieldValue != null) {
					field.setLocalizable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					field.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					field.setType(
						Field.Type.create((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-475784324