/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentField;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
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
public class FragmentInstancePageElementDefinitionSerDes {

	public static FragmentInstancePageElementDefinition toDTO(String json) {
		FragmentInstancePageElementDefinitionJSONParser
			fragmentInstancePageElementDefinitionJSONParser =
				new FragmentInstancePageElementDefinitionJSONParser();

		return fragmentInstancePageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static FragmentInstancePageElementDefinition[] toDTOs(String json) {
		FragmentInstancePageElementDefinitionJSONParser
			fragmentInstancePageElementDefinitionJSONParser =
				new FragmentInstancePageElementDefinitionJSONParser();

		return fragmentInstancePageElementDefinitionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		FragmentInstancePageElementDefinition
			fragmentInstancePageElementDefinition) {

		if (fragmentInstancePageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragmentInstancePageElementDefinition.getConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configuration\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentInstancePageElementDefinition.getConfiguration()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getCss() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"css\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstancePageElementDefinition.getCss()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 fragmentInstancePageElementDefinition.
						 getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(
						fragmentInstancePageElementDefinition.getCssClasses()
							[i]));

				if ((i + 1) < fragmentInstancePageElementDefinition.
						getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentInstancePageElementDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(
				_escape(fragmentInstancePageElementDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getCustomCSSViewports() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < fragmentInstancePageElementDefinition.
					 getCustomCSSViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						fragmentInstancePageElementDefinition.
							getCustomCSSViewports()[i]));

				if ((i + 1) < fragmentInstancePageElementDefinition.
						getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentInstancePageElementDefinition.getDatePropagated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePropagated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					fragmentInstancePageElementDefinition.getDatePropagated()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.
				getDraftFragmentInstanceExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"draftFragmentInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentInstancePageElementDefinition.
						getDraftFragmentInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getFragmentConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentConfig\": ");

			sb.append(
				_toJSON(
					fragmentInstancePageElementDefinition.getFragmentConfig()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < fragmentInstancePageElementDefinition.
					 getFragmentFields().length;
				 i++) {

				sb.append(
					String.valueOf(
						fragmentInstancePageElementDefinition.
							getFragmentFields()[i]));

				if ((i + 1) < fragmentInstancePageElementDefinition.
						getFragmentFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentInstancePageElementDefinition.
				getFragmentInstanceExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentInstancePageElementDefinition.
						getFragmentInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getFragmentReference() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentReference\": ");

			sb.append(
				String.valueOf(
					fragmentInstancePageElementDefinition.
						getFragmentReference()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(
					fragmentInstancePageElementDefinition.getFragmentStyle()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentType\": ");

			sb.append("\"");

			sb.append(fragmentInstancePageElementDefinition.getFragmentType());

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getFragmentViewports() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < fragmentInstancePageElementDefinition.
					 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						fragmentInstancePageElementDefinition.
							getFragmentViewports()[i]));

				if ((i + 1) < fragmentInstancePageElementDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentInstancePageElementDefinition.getHtml() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"html\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstancePageElementDefinition.getHtml()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(fragmentInstancePageElementDefinition.getIndexed());
		}

		if (fragmentInstancePageElementDefinition.getJs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"js\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstancePageElementDefinition.getJs()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstancePageElementDefinition.getName()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getNamespace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"namespace\": ");

			sb.append("\"");

			sb.append(
				_escape(fragmentInstancePageElementDefinition.getNamespace()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstancePageElementDefinition.getUuid()));

			sb.append("\"");
		}

		if (fragmentInstancePageElementDefinition.getWidgetInstances() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstances\": ");

			sb.append("[");

			for (int i = 0;
				 i < fragmentInstancePageElementDefinition.
					 getWidgetInstances().length;
				 i++) {

				sb.append(
					String.valueOf(
						fragmentInstancePageElementDefinition.
							getWidgetInstances()[i]));

				if ((i + 1) < fragmentInstancePageElementDefinition.
						getWidgetInstances().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentInstancePageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(fragmentInstancePageElementDefinition.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentInstancePageElementDefinitionJSONParser
			fragmentInstancePageElementDefinitionJSONParser =
				new FragmentInstancePageElementDefinitionJSONParser();

		return fragmentInstancePageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentInstancePageElementDefinition
			fragmentInstancePageElementDefinition) {

		if (fragmentInstancePageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragmentInstancePageElementDefinition.getConfiguration() == null) {
			map.put("configuration", null);
		}
		else {
			map.put(
				"configuration",
				String.valueOf(
					fragmentInstancePageElementDefinition.getConfiguration()));
		}

		if (fragmentInstancePageElementDefinition.getCss() == null) {
			map.put("css", null);
		}
		else {
			map.put(
				"css",
				String.valueOf(fragmentInstancePageElementDefinition.getCss()));
		}

		if (fragmentInstancePageElementDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(
					fragmentInstancePageElementDefinition.getCssClasses()));
		}

		if (fragmentInstancePageElementDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(
					fragmentInstancePageElementDefinition.getCustomCSS()));
		}

		if (fragmentInstancePageElementDefinition.getCustomCSSViewports() ==
				null) {

			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(
					fragmentInstancePageElementDefinition.
						getCustomCSSViewports()));
		}

		if (fragmentInstancePageElementDefinition.getDatePropagated() == null) {
			map.put("datePropagated", null);
		}
		else {
			map.put(
				"datePropagated",
				liferayToJSONDateFormat.format(
					fragmentInstancePageElementDefinition.getDatePropagated()));
		}

		if (fragmentInstancePageElementDefinition.
				getDraftFragmentInstanceExternalReferenceCode() == null) {

			map.put("draftFragmentInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"draftFragmentInstanceExternalReferenceCode",
				String.valueOf(
					fragmentInstancePageElementDefinition.
						getDraftFragmentInstanceExternalReferenceCode()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentConfig() == null) {
			map.put("fragmentConfig", null);
		}
		else {
			map.put(
				"fragmentConfig",
				String.valueOf(
					fragmentInstancePageElementDefinition.getFragmentConfig()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentFields() == null) {
			map.put("fragmentFields", null);
		}
		else {
			map.put(
				"fragmentFields",
				String.valueOf(
					fragmentInstancePageElementDefinition.getFragmentFields()));
		}

		if (fragmentInstancePageElementDefinition.
				getFragmentInstanceExternalReferenceCode() == null) {

			map.put("fragmentInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"fragmentInstanceExternalReferenceCode",
				String.valueOf(
					fragmentInstancePageElementDefinition.
						getFragmentInstanceExternalReferenceCode()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentReference() ==
				null) {

			map.put("fragmentReference", null);
		}
		else {
			map.put(
				"fragmentReference",
				String.valueOf(
					fragmentInstancePageElementDefinition.
						getFragmentReference()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(
					fragmentInstancePageElementDefinition.getFragmentStyle()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentType() == null) {
			map.put("fragmentType", null);
		}
		else {
			map.put(
				"fragmentType",
				String.valueOf(
					fragmentInstancePageElementDefinition.getFragmentType()));
		}

		if (fragmentInstancePageElementDefinition.getFragmentViewports() ==
				null) {

			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					fragmentInstancePageElementDefinition.
						getFragmentViewports()));
		}

		if (fragmentInstancePageElementDefinition.getHtml() == null) {
			map.put("html", null);
		}
		else {
			map.put(
				"html",
				String.valueOf(
					fragmentInstancePageElementDefinition.getHtml()));
		}

		if (fragmentInstancePageElementDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(
					fragmentInstancePageElementDefinition.getIndexed()));
		}

		if (fragmentInstancePageElementDefinition.getJs() == null) {
			map.put("js", null);
		}
		else {
			map.put(
				"js",
				String.valueOf(fragmentInstancePageElementDefinition.getJs()));
		}

		if (fragmentInstancePageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(
					fragmentInstancePageElementDefinition.getName()));
		}

		if (fragmentInstancePageElementDefinition.getNamespace() == null) {
			map.put("namespace", null);
		}
		else {
			map.put(
				"namespace",
				String.valueOf(
					fragmentInstancePageElementDefinition.getNamespace()));
		}

		if (fragmentInstancePageElementDefinition.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put(
				"uuid",
				String.valueOf(
					fragmentInstancePageElementDefinition.getUuid()));
		}

		if (fragmentInstancePageElementDefinition.getWidgetInstances() ==
				null) {

			map.put("widgetInstances", null);
		}
		else {
			map.put(
				"widgetInstances",
				String.valueOf(
					fragmentInstancePageElementDefinition.
						getWidgetInstances()));
		}

		if (fragmentInstancePageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					fragmentInstancePageElementDefinition.getType()));
		}

		return map;
	}

	public static class FragmentInstancePageElementDefinitionJSONParser
		extends BaseJSONParser<FragmentInstancePageElementDefinition> {

		@Override
		protected FragmentInstancePageElementDefinition createDTO() {
			return new FragmentInstancePageElementDefinition();
		}

		@Override
		protected FragmentInstancePageElementDefinition[] createDTOArray(
			int size) {

			return new FragmentInstancePageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "configuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "css")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
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
			else if (Objects.equals(
						jsonParserFieldName,
						"draftFragmentInstanceExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentConfig")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentFields")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentInstanceExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentReference")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "html")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "js")) {
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
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentInstancePageElementDefinition
				fragmentInstancePageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "configuration")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setConfiguration(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "css")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setCss(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setCustomCSS(
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

					fragmentInstancePageElementDefinition.setCustomCSSViewports(
						customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePropagated")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setDatePropagated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"draftFragmentInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.
						setDraftFragmentInstanceExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentConfig")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setFragmentConfig(
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

					fragmentInstancePageElementDefinition.setFragmentFields(
						fragmentFieldsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.
						setFragmentInstanceExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentReference")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setFragmentReference(
						FragmentReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setFragmentStyle(
						FragmentStyleSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentType")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setFragmentType(
						FragmentInstancePageElementDefinition.FragmentType.
							create((String)jsonParserFieldValue));
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

					fragmentInstancePageElementDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "html")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setHtml(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "js")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setJs(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "namespace")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setNamespace(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setUuid(
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

					fragmentInstancePageElementDefinition.setWidgetInstances(
						widgetInstancesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					fragmentInstancePageElementDefinition.setType(
						FragmentInstancePageElementDefinition.Type.create(
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