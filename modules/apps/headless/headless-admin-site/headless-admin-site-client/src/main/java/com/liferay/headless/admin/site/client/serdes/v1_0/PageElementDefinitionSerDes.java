/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentCompositionInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentDropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ModulePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstancePageElementDefinition;
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
public class PageElementDefinitionSerDes {

	public static PageElementDefinition toDTO(String json) {
		PageElementDefinitionJSONParser pageElementDefinitionJSONParser =
			new PageElementDefinitionJSONParser();

		return pageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static PageElementDefinition[] toDTOs(String json) {
		PageElementDefinitionJSONParser pageElementDefinitionJSONParser =
			new PageElementDefinitionJSONParser();

		return pageElementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageElementDefinition pageElementDefinition) {
		if (pageElementDefinition == null) {
			return "null";
		}

		PageElementDefinition.Type type = pageElementDefinition.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("Collection")) {
				return CollectionPageElementDefinitionSerDes.toJSON(
					(CollectionPageElementDefinition)pageElementDefinition);
			}

			if (typeString.equals("CollectionItem")) {
				return CollectionItemPageElementDefinitionSerDes.toJSON(
					(CollectionItemPageElementDefinition)pageElementDefinition);
			}

			if (typeString.equals("Container")) {
				return ContainerPageElementDefinitionSerDes.toJSON(
					(ContainerPageElementDefinition)pageElementDefinition);
			}

			if (typeString.equals("DropZone")) {
				return DropZonePageElementDefinitionSerDes.toJSON(
					(DropZonePageElementDefinition)pageElementDefinition);
			}

			if (typeString.equals("Form")) {
				return FormPageElementDefinitionSerDes.toJSON(
					(FormPageElementDefinition)pageElementDefinition);
			}

			if (typeString.equals("FormStep")) {
				return FormStepPageElementDefinitionSerDes.toJSON(
					(FormStepPageElementDefinition)pageElementDefinition);
			}

			if (typeString.equals("FormStepContainer")) {
				return FormStepContainerPageElementDefinitionSerDes.toJSON(
					(FormStepContainerPageElementDefinition)
						pageElementDefinition);
			}

			if (typeString.equals("Fragment")) {
				return FragmentInstancePageElementDefinitionSerDes.toJSON(
					(FragmentInstancePageElementDefinition)
						pageElementDefinition);
			}

			if (typeString.equals("FragmentComposition")) {
				return FragmentCompositionInstancePageElementDefinitionSerDes.
					toJSON(
						(FragmentCompositionInstancePageElementDefinition)
							pageElementDefinition);
			}

			if (typeString.equals("FragmentDropZone")) {
				return FragmentDropZonePageElementDefinitionSerDes.toJSON(
					(FragmentDropZonePageElementDefinition)
						pageElementDefinition);
			}

			if (typeString.equals("Grid")) {
				return GridPageElementDefinitionSerDes.toJSON(
					(GridPageElementDefinition)pageElementDefinition);
			}

			if (typeString.equals("Module")) {
				return ModulePageElementDefinitionSerDes.toJSON(
					(ModulePageElementDefinition)pageElementDefinition);
			}

			if (typeString.equals("Widget")) {
				return WidgetInstancePageElementDefinitionSerDes.toJSON(
					(WidgetInstancePageElementDefinition)pageElementDefinition);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		PageElementDefinitionJSONParser pageElementDefinitionJSONParser =
			new PageElementDefinitionJSONParser();

		return pageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageElementDefinition pageElementDefinition) {

		if (pageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(pageElementDefinition.getType()));
		}

		return map;
	}

	public static class PageElementDefinitionJSONParser
		extends BaseJSONParser<PageElementDefinition> {

		@Override
		protected PageElementDefinition createDTO() {
			return null;
		}

		@Override
		protected PageElementDefinition[] createDTOArray(int size) {
			return new PageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public PageElementDefinition parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("Collection")) {
					return CollectionPageElementDefinition.toDTO(json);
				}

				if (typeString.equals("CollectionItem")) {
					return CollectionItemPageElementDefinition.toDTO(json);
				}

				if (typeString.equals("Container")) {
					return ContainerPageElementDefinition.toDTO(json);
				}

				if (typeString.equals("DropZone")) {
					return DropZonePageElementDefinition.toDTO(json);
				}

				if (typeString.equals("Form")) {
					return FormPageElementDefinition.toDTO(json);
				}

				if (typeString.equals("FormStep")) {
					return FormStepPageElementDefinition.toDTO(json);
				}

				if (typeString.equals("FormStepContainer")) {
					return FormStepContainerPageElementDefinition.toDTO(json);
				}

				if (typeString.equals("Fragment")) {
					return FragmentInstancePageElementDefinition.toDTO(json);
				}

				if (typeString.equals("FragmentComposition")) {
					return FragmentCompositionInstancePageElementDefinition.
						toDTO(json);
				}

				if (typeString.equals("FragmentDropZone")) {
					return FragmentDropZonePageElementDefinition.toDTO(json);
				}

				if (typeString.equals("Grid")) {
					return GridPageElementDefinition.toDTO(json);
				}

				if (typeString.equals("Module")) {
					return ModulePageElementDefinition.toDTO(json);
				}

				if (typeString.equals("Widget")) {
					return WidgetInstancePageElementDefinition.toDTO(json);
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
			PageElementDefinition pageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					pageElementDefinition.setType(
						PageElementDefinition.Type.create(
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