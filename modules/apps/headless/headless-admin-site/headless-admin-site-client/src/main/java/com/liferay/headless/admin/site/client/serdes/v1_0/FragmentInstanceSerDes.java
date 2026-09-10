/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElement;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class FragmentInstanceSerDes {

	public static FragmentInstance toDTO(String json) {
		FragmentInstanceJSONParser fragmentInstanceJSONParser =
			new FragmentInstanceJSONParser();

		return fragmentInstanceJSONParser.parseToDTO(json);
	}

	public static FragmentInstance[] toDTOs(String json) {
		FragmentInstanceJSONParser fragmentInstanceJSONParser =
			new FragmentInstanceJSONParser();

		return fragmentInstanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentInstance fragmentInstance) {
		if (fragmentInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragmentInstance.getBackgroundImageValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundImageValue\": ");

			sb.append(
				String.valueOf(fragmentInstance.getBackgroundImageValue()));
		}

		if (fragmentInstance.getConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configuration\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstance.getConfiguration()));

			sb.append("\"");
		}

		if (fragmentInstance.getCss() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"css\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstance.getCss()));

			sb.append("\"");
		}

		if (fragmentInstance.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0; i < fragmentInstance.getCssClasses().length; i++) {
				sb.append(_toJSON(fragmentInstance.getCssClasses()[i]));

				if ((i + 1) < fragmentInstance.getCssClasses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentInstance.getDatePropagated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePropagated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					fragmentInstance.getDatePropagated()));

			sb.append("\"");
		}

		if (fragmentInstance.getDraftFragmentInstanceExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"draftFragmentInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentInstance.
						getDraftFragmentInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentInstance.getFragmentConfigurationFieldValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentConfigurationFieldValues\": ");

			sb.append(
				_toJSON(
					fragmentInstance.getFragmentConfigurationFieldValues()));
		}

		if (fragmentInstance.getFragmentEditableElements() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentEditableElements\": ");

			sb.append("[");

			for (int i = 0;
				 i < fragmentInstance.getFragmentEditableElements().length;
				 i++) {

				sb.append(
					String.valueOf(
						fragmentInstance.getFragmentEditableElements()[i]));

				if ((i + 1) <
						fragmentInstance.getFragmentEditableElements().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentInstance.getFragmentInstanceExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentInstance.
						getFragmentInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentInstance.getFragmentReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentReference\": ");

			sb.append(String.valueOf(fragmentInstance.getFragmentReference()));
		}

		if (fragmentInstance.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0; i < fragmentInstance.getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(fragmentInstance.getFragmentViewports()[i]));

				if ((i + 1) < fragmentInstance.getFragmentViewports().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentInstance.getHtml() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"html\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstance.getHtml()));

			sb.append("\"");
		}

		if (fragmentInstance.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(fragmentInstance.getIndexed());
		}

		if (fragmentInstance.getJs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"js\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstance.getJs()));

			sb.append("\"");
		}

		if (fragmentInstance.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstance.getName()));

			sb.append("\"");
		}

		if (fragmentInstance.getNamespace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"namespace\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstance.getNamespace()));

			sb.append("\"");
		}

		if (fragmentInstance.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(fragmentInstance.getUuid()));

			sb.append("\"");
		}

		if (fragmentInstance.getWidgetInstances() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstances\": ");

			sb.append("[");

			for (int i = 0; i < fragmentInstance.getWidgetInstances().length;
				 i++) {

				sb.append(
					String.valueOf(fragmentInstance.getWidgetInstances()[i]));

				if ((i + 1) < fragmentInstance.getWidgetInstances().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentInstanceJSONParser fragmentInstanceJSONParser =
			new FragmentInstanceJSONParser();

		return fragmentInstanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FragmentInstance fragmentInstance) {
		if (fragmentInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragmentInstance.getBackgroundImageValue() == null) {
			map.put("backgroundImageValue", null);
		}
		else {
			map.put(
				"backgroundImageValue",
				String.valueOf(fragmentInstance.getBackgroundImageValue()));
		}

		if (fragmentInstance.getConfiguration() == null) {
			map.put("configuration", null);
		}
		else {
			map.put(
				"configuration",
				String.valueOf(fragmentInstance.getConfiguration()));
		}

		if (fragmentInstance.getCss() == null) {
			map.put("css", null);
		}
		else {
			map.put("css", String.valueOf(fragmentInstance.getCss()));
		}

		if (fragmentInstance.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses", String.valueOf(fragmentInstance.getCssClasses()));
		}

		if (fragmentInstance.getDatePropagated() == null) {
			map.put("datePropagated", null);
		}
		else {
			map.put(
				"datePropagated",
				liferayToJSONDateFormat.format(
					fragmentInstance.getDatePropagated()));
		}

		if (fragmentInstance.getDraftFragmentInstanceExternalReferenceCode() ==
				null) {

			map.put("draftFragmentInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"draftFragmentInstanceExternalReferenceCode",
				String.valueOf(
					fragmentInstance.
						getDraftFragmentInstanceExternalReferenceCode()));
		}

		if (fragmentInstance.getFragmentConfigurationFieldValues() == null) {
			map.put("fragmentConfigurationFieldValues", null);
		}
		else {
			map.put(
				"fragmentConfigurationFieldValues",
				String.valueOf(
					fragmentInstance.getFragmentConfigurationFieldValues()));
		}

		if (fragmentInstance.getFragmentEditableElements() == null) {
			map.put("fragmentEditableElements", null);
		}
		else {
			map.put(
				"fragmentEditableElements",
				String.valueOf(fragmentInstance.getFragmentEditableElements()));
		}

		if (fragmentInstance.getFragmentInstanceExternalReferenceCode() ==
				null) {

			map.put("fragmentInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"fragmentInstanceExternalReferenceCode",
				String.valueOf(
					fragmentInstance.
						getFragmentInstanceExternalReferenceCode()));
		}

		if (fragmentInstance.getFragmentReference() == null) {
			map.put("fragmentReference", null);
		}
		else {
			map.put(
				"fragmentReference",
				String.valueOf(fragmentInstance.getFragmentReference()));
		}

		if (fragmentInstance.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(fragmentInstance.getFragmentViewports()));
		}

		if (fragmentInstance.getHtml() == null) {
			map.put("html", null);
		}
		else {
			map.put("html", String.valueOf(fragmentInstance.getHtml()));
		}

		if (fragmentInstance.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put("indexed", String.valueOf(fragmentInstance.getIndexed()));
		}

		if (fragmentInstance.getJs() == null) {
			map.put("js", null);
		}
		else {
			map.put("js", String.valueOf(fragmentInstance.getJs()));
		}

		if (fragmentInstance.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(fragmentInstance.getName()));
		}

		if (fragmentInstance.getNamespace() == null) {
			map.put("namespace", null);
		}
		else {
			map.put(
				"namespace", String.valueOf(fragmentInstance.getNamespace()));
		}

		if (fragmentInstance.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(fragmentInstance.getUuid()));
		}

		if (fragmentInstance.getWidgetInstances() == null) {
			map.put("widgetInstances", null);
		}
		else {
			map.put(
				"widgetInstances",
				String.valueOf(fragmentInstance.getWidgetInstances()));
		}

		return map;
	}

	public static class FragmentInstanceJSONParser
		extends BaseJSONParser<FragmentInstance> {

		@Override
		protected FragmentInstance createDTO() {
			return new FragmentInstance();
		}

		@Override
		protected FragmentInstance[] createDTOArray(int size) {
			return new FragmentInstance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "backgroundImageValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "configuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "css")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
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
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentConfigurationFieldValues")) {

				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentEditableElements")) {

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

			return false;
		}

		@Override
		protected void setField(
			FragmentInstance fragmentInstance, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "backgroundImageValue")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setBackgroundImageValue(
						BackgroundImageValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "configuration")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setConfiguration(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "css")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setCss((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePropagated")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setDatePropagated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"draftFragmentInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentInstance.
						setDraftFragmentInstanceExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentConfigurationFieldValues")) {

				if (jsonParserFieldValue != null) {
					fragmentInstance.setFragmentConfigurationFieldValues(
						(Map<String, FragmentConfigurationFieldValue>)
							jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentEditableElements")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FragmentEditableElement[] fragmentEditableElementsArray =
						new FragmentEditableElement
							[jsonParserFieldValues.length];

					for (int i = 0; i < fragmentEditableElementsArray.length;
						 i++) {

						fragmentEditableElementsArray[i] =
							FragmentEditableElementSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					fragmentInstance.setFragmentEditableElements(
						fragmentEditableElementsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentInstance.setFragmentInstanceExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentReference")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setFragmentReference(
						FragmentReferenceSerDes.toDTO(
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

					fragmentInstance.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "html")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setHtml((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setIndexed((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "js")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setJs((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "namespace")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setNamespace((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					fragmentInstance.setUuid((String)jsonParserFieldValue);
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

					fragmentInstance.setWidgetInstances(widgetInstancesArray);
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
// LIFERAY-REST-BUILDER-HASH:1692542858