/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
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
public class PageExperienceSerDes {

	public static PageExperience toDTO(String json) {
		PageExperienceJSONParser pageExperienceJSONParser =
			new PageExperienceJSONParser();

		return pageExperienceJSONParser.parseToDTO(json);
	}

	public static PageExperience[] toDTOs(String json) {
		PageExperienceJSONParser pageExperienceJSONParser =
			new PageExperienceJSONParser();

		return pageExperienceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageExperience pageExperience) {
		if (pageExperience == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageExperience.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(pageExperience.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (pageExperience.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(pageExperience.getKey()));

			sb.append("\"");
		}

		if (pageExperience.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(pageExperience.getName_i18n()));
		}

		if (pageExperience.getPageElements() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageElements\": ");

			sb.append("[");

			for (int i = 0; i < pageExperience.getPageElements().length; i++) {
				sb.append(String.valueOf(pageExperience.getPageElements()[i]));

				if ((i + 1) < pageExperience.getPageElements().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageExperience.getPageSpecificationExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					pageExperience.
						getPageSpecificationExternalReferenceCode()));

			sb.append("\"");
		}

		if (pageExperience.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(pageExperience.getPriority());
		}

		if (pageExperience.getSegmentItemExternalReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"segmentItemExternalReference\": ");

			sb.append(
				String.valueOf(
					pageExperience.getSegmentItemExternalReference()));
		}

		if (pageExperience.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(pageExperience.getUuid()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageExperienceJSONParser pageExperienceJSONParser =
			new PageExperienceJSONParser();

		return pageExperienceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PageExperience pageExperience) {
		if (pageExperience == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageExperience.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(pageExperience.getExternalReferenceCode()));
		}

		if (pageExperience.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(pageExperience.getKey()));
		}

		if (pageExperience.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(pageExperience.getName_i18n()));
		}

		if (pageExperience.getPageElements() == null) {
			map.put("pageElements", null);
		}
		else {
			map.put(
				"pageElements",
				String.valueOf(pageExperience.getPageElements()));
		}

		if (pageExperience.getPageSpecificationExternalReferenceCode() ==
				null) {

			map.put("pageSpecificationExternalReferenceCode", null);
		}
		else {
			map.put(
				"pageSpecificationExternalReferenceCode",
				String.valueOf(
					pageExperience.
						getPageSpecificationExternalReferenceCode()));
		}

		if (pageExperience.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(pageExperience.getPriority()));
		}

		if (pageExperience.getSegmentItemExternalReference() == null) {
			map.put("segmentItemExternalReference", null);
		}
		else {
			map.put(
				"segmentItemExternalReference",
				String.valueOf(
					pageExperience.getSegmentItemExternalReference()));
		}

		if (pageExperience.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(pageExperience.getUuid()));
		}

		return map;
	}

	public static class PageExperienceJSONParser
		extends BaseJSONParser<PageExperience> {

		@Override
		protected PageExperience createDTO() {
			return new PageExperience();
		}

		@Override
		protected PageExperience[] createDTOArray(int size) {
			return new PageExperience[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "pageElements")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"pageSpecificationExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "segmentItemExternalReference")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageExperience pageExperience, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					pageExperience.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					pageExperience.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					pageExperience.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageElements")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PageElement[] pageElementsArray =
						new PageElement[jsonParserFieldValues.length];

					for (int i = 0; i < pageElementsArray.length; i++) {
						pageElementsArray[i] = PageElementSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					pageExperience.setPageElements(pageElementsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"pageSpecificationExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					pageExperience.setPageSpecificationExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					pageExperience.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "segmentItemExternalReference")) {

				if (jsonParserFieldValue != null) {
					pageExperience.setSegmentItemExternalReference(
						ItemExternalReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					pageExperience.setUuid((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:962248645