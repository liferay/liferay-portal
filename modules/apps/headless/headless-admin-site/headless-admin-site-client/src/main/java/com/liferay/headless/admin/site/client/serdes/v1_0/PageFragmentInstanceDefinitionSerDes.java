/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentField;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.PageFragmentInstanceDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class PageFragmentInstanceDefinitionSerDes {

	public static PageFragmentInstanceDefinition toDTO(String json) {
		PageFragmentInstanceDefinitionJSONParser
			pageFragmentInstanceDefinitionJSONParser =
				new PageFragmentInstanceDefinitionJSONParser();

		return pageFragmentInstanceDefinitionJSONParser.parseToDTO(json);
	}

	public static PageFragmentInstanceDefinition[] toDTOs(String json) {
		PageFragmentInstanceDefinitionJSONParser
			pageFragmentInstanceDefinitionJSONParser =
				new PageFragmentInstanceDefinitionJSONParser();

		return pageFragmentInstanceDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageFragmentInstanceDefinition pageFragmentInstanceDefinition) {

		if (pageFragmentInstanceDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (pageFragmentInstanceDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageFragmentInstanceDefinition.getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(pageFragmentInstanceDefinition.getCssClasses()[i]));

				if ((i + 1) <
						pageFragmentInstanceDefinition.getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFragmentInstanceDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(pageFragmentInstanceDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (pageFragmentInstanceDefinition.getCustomCSSViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 pageFragmentInstanceDefinition.
						 getCustomCSSViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageFragmentInstanceDefinition.getCustomCSSViewports()
							[i]));

				if ((i + 1) < pageFragmentInstanceDefinition.
						getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFragmentInstanceDefinition.getDatePropagated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePropagated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					pageFragmentInstanceDefinition.getDatePropagated()));

			sb.append("\"");
		}

		if (pageFragmentInstanceDefinition.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					pageFragmentInstanceDefinition.getDatePublished()));

			sb.append("\"");
		}

		if (pageFragmentInstanceDefinition.
				getDraftPageElementExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"draftPageElementExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					pageFragmentInstanceDefinition.
						getDraftPageElementExternalReferenceCode()));

			sb.append("\"");
		}

		if (pageFragmentInstanceDefinition.getFragmentConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentConfig\": ");

			sb.append(
				_toJSON(pageFragmentInstanceDefinition.getFragmentConfig()));
		}

		if (pageFragmentInstanceDefinition.getFragmentFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageFragmentInstanceDefinition.getFragmentFields().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageFragmentInstanceDefinition.getFragmentFields()[i]));

				if ((i + 1) <
						pageFragmentInstanceDefinition.
							getFragmentFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFragmentInstanceDefinition.getFragmentReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentReference\": ");

			if (pageFragmentInstanceDefinition.getFragmentReference() instanceof
					String) {

				sb.append("\"");
				sb.append(
					(String)
						pageFragmentInstanceDefinition.getFragmentReference());
				sb.append("\"");
			}
			else {
				sb.append(
					pageFragmentInstanceDefinition.getFragmentReference());
			}
		}

		if (pageFragmentInstanceDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(
					pageFragmentInstanceDefinition.getFragmentStyle()));
		}

		if (pageFragmentInstanceDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 pageFragmentInstanceDefinition.
						 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageFragmentInstanceDefinition.getFragmentViewports()
							[i]));

				if ((i + 1) < pageFragmentInstanceDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFragmentInstanceDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(pageFragmentInstanceDefinition.getIndexed());
		}

		if (pageFragmentInstanceDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageFragmentInstanceDefinition.getName()));

			sb.append("\"");
		}

		if (pageFragmentInstanceDefinition.getNamespace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"namespace\": ");

			sb.append("\"");

			sb.append(_escape(pageFragmentInstanceDefinition.getNamespace()));

			sb.append("\"");
		}

		if (pageFragmentInstanceDefinition.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(pageFragmentInstanceDefinition.getUuid()));

			sb.append("\"");
		}

		if (pageFragmentInstanceDefinition.getWidgetInstances() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstances\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageFragmentInstanceDefinition.getWidgetInstances().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageFragmentInstanceDefinition.getWidgetInstances()
							[i]));

				if ((i + 1) < pageFragmentInstanceDefinition.
						getWidgetInstances().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageFragmentInstanceDefinitionJSONParser
			pageFragmentInstanceDefinitionJSONParser =
				new PageFragmentInstanceDefinitionJSONParser();

		return pageFragmentInstanceDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageFragmentInstanceDefinition pageFragmentInstanceDefinition) {

		if (pageFragmentInstanceDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (pageFragmentInstanceDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(pageFragmentInstanceDefinition.getCssClasses()));
		}

		if (pageFragmentInstanceDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(pageFragmentInstanceDefinition.getCustomCSS()));
		}

		if (pageFragmentInstanceDefinition.getCustomCSSViewports() == null) {
			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(
					pageFragmentInstanceDefinition.getCustomCSSViewports()));
		}

		if (pageFragmentInstanceDefinition.getDatePropagated() == null) {
			map.put("datePropagated", null);
		}
		else {
			map.put(
				"datePropagated",
				liferayToJSONDateFormat.format(
					pageFragmentInstanceDefinition.getDatePropagated()));
		}

		if (pageFragmentInstanceDefinition.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(
					pageFragmentInstanceDefinition.getDatePublished()));
		}

		if (pageFragmentInstanceDefinition.
				getDraftPageElementExternalReferenceCode() == null) {

			map.put("draftPageElementExternalReferenceCode", null);
		}
		else {
			map.put(
				"draftPageElementExternalReferenceCode",
				String.valueOf(
					pageFragmentInstanceDefinition.
						getDraftPageElementExternalReferenceCode()));
		}

		if (pageFragmentInstanceDefinition.getFragmentConfig() == null) {
			map.put("fragmentConfig", null);
		}
		else {
			map.put(
				"fragmentConfig",
				String.valueOf(
					pageFragmentInstanceDefinition.getFragmentConfig()));
		}

		if (pageFragmentInstanceDefinition.getFragmentFields() == null) {
			map.put("fragmentFields", null);
		}
		else {
			map.put(
				"fragmentFields",
				String.valueOf(
					pageFragmentInstanceDefinition.getFragmentFields()));
		}

		if (pageFragmentInstanceDefinition.getFragmentReference() == null) {
			map.put("fragmentReference", null);
		}
		else {
			map.put(
				"fragmentReference",
				String.valueOf(
					pageFragmentInstanceDefinition.getFragmentReference()));
		}

		if (pageFragmentInstanceDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(
					pageFragmentInstanceDefinition.getFragmentStyle()));
		}

		if (pageFragmentInstanceDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					pageFragmentInstanceDefinition.getFragmentViewports()));
		}

		if (pageFragmentInstanceDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(pageFragmentInstanceDefinition.getIndexed()));
		}

		if (pageFragmentInstanceDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(pageFragmentInstanceDefinition.getName()));
		}

		if (pageFragmentInstanceDefinition.getNamespace() == null) {
			map.put("namespace", null);
		}
		else {
			map.put(
				"namespace",
				String.valueOf(pageFragmentInstanceDefinition.getNamespace()));
		}

		if (pageFragmentInstanceDefinition.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put(
				"uuid",
				String.valueOf(pageFragmentInstanceDefinition.getUuid()));
		}

		if (pageFragmentInstanceDefinition.getWidgetInstances() == null) {
			map.put("widgetInstances", null);
		}
		else {
			map.put(
				"widgetInstances",
				String.valueOf(
					pageFragmentInstanceDefinition.getWidgetInstances()));
		}

		return map;
	}

	public static class PageFragmentInstanceDefinitionJSONParser
		extends BaseJSONParser<PageFragmentInstanceDefinition> {

		@Override
		protected PageFragmentInstanceDefinition createDTO() {
			return new PageFragmentInstanceDefinition();
		}

		@Override
		protected PageFragmentInstanceDefinition[] createDTOArray(int size) {
			return new PageFragmentInstanceDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCSSViewports")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "datePropagated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"draftPageElementExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentConfig")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentReference")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "namespace")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstances")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageFragmentInstanceDefinition pageFragmentInstanceDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setCustomCSS(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCSSViewports")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					CustomCSSViewport[] customCSSViewportsArray =
						new CustomCSSViewport[jsonParserFieldValues.length];

					for (int i = 0; i < customCSSViewportsArray.length; i++) {
						customCSSViewportsArray[i] =
							CustomCSSViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageFragmentInstanceDefinition.setCustomCSSViewports(
						customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePropagated")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setDatePropagated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"draftPageElementExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.
						setDraftPageElementExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentConfig")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setFragmentConfig(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FragmentField[] fragmentFieldsArray =
						new FragmentField[jsonParserFieldValues.length];

					for (int i = 0; i < fragmentFieldsArray.length; i++) {
						fragmentFieldsArray[i] = FragmentFieldSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					pageFragmentInstanceDefinition.setFragmentFields(
						fragmentFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentReference")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setFragmentReference(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setFragmentStyle(
						FragmentStyleSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FragmentViewport[] fragmentViewportsArray =
						new FragmentViewport[jsonParserFieldValues.length];

					for (int i = 0; i < fragmentViewportsArray.length; i++) {
						fragmentViewportsArray[i] =
							FragmentViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageFragmentInstanceDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "namespace")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setNamespace(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					pageFragmentInstanceDefinition.setUuid(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstances")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WidgetInstance[] widgetInstancesArray =
						new WidgetInstance[jsonParserFieldValues.length];

					for (int i = 0; i < widgetInstancesArray.length; i++) {
						widgetInstancesArray[i] = WidgetInstanceSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					pageFragmentInstanceDefinition.setWidgetInstances(
						widgetInstancesArray);
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