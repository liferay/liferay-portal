/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
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
public class ContentPageSpecificationSerDes {

	public static ContentPageSpecification toDTO(String json) {
		ContentPageSpecificationJSONParser contentPageSpecificationJSONParser =
			new ContentPageSpecificationJSONParser();

		return contentPageSpecificationJSONParser.parseToDTO(json);
	}

	public static ContentPageSpecification[] toDTOs(String json) {
		ContentPageSpecificationJSONParser contentPageSpecificationJSONParser =
			new ContentPageSpecificationJSONParser();

		return contentPageSpecificationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ContentPageSpecification contentPageSpecification) {

		if (contentPageSpecification == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentPageSpecification.
				getDraftContentPageSpecificationExternalReferenceCode() !=
					null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"draftContentPageSpecificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					contentPageSpecification.
						getDraftContentPageSpecificationExternalReferenceCode()));

			sb.append("\"");
		}

		if (contentPageSpecification.getPageExperiences() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageExperiences\": ");

			sb.append("[");

			for (int i = 0;
				 i < contentPageSpecification.getPageExperiences().length;
				 i++) {

				sb.append(
					String.valueOf(
						contentPageSpecification.getPageExperiences()[i]));

				if ((i + 1) <
						contentPageSpecification.getPageExperiences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentPageSpecification.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(contentPageSpecification.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (contentPageSpecification.getSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(contentPageSpecification.getSettings()));
		}

		if (contentPageSpecification.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(contentPageSpecification.getStatus());

			sb.append("\"");
		}

		if (contentPageSpecification.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(contentPageSpecification.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentPageSpecificationJSONParser contentPageSpecificationJSONParser =
			new ContentPageSpecificationJSONParser();

		return contentPageSpecificationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContentPageSpecification contentPageSpecification) {

		if (contentPageSpecification == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentPageSpecification.
				getDraftContentPageSpecificationExternalReferenceCode() ==
					null) {

			map.put("draftContentPageSpecificationExternalReferenceCode", null);
		}
		else {
			map.put(
				"draftContentPageSpecificationExternalReferenceCode",
				String.valueOf(
					contentPageSpecification.
						getDraftContentPageSpecificationExternalReferenceCode()));
		}

		if (contentPageSpecification.getPageExperiences() == null) {
			map.put("pageExperiences", null);
		}
		else {
			map.put(
				"pageExperiences",
				String.valueOf(contentPageSpecification.getPageExperiences()));
		}

		if (contentPageSpecification.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					contentPageSpecification.getExternalReferenceCode()));
		}

		if (contentPageSpecification.getSettings() == null) {
			map.put("settings", null);
		}
		else {
			map.put(
				"settings",
				String.valueOf(contentPageSpecification.getSettings()));
		}

		if (contentPageSpecification.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status", String.valueOf(contentPageSpecification.getStatus()));
		}

		if (contentPageSpecification.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(contentPageSpecification.getType()));
		}

		return map;
	}

	public static class ContentPageSpecificationJSONParser
		extends BaseJSONParser<ContentPageSpecification> {

		@Override
		protected ContentPageSpecification createDTO() {
			return new ContentPageSpecification();
		}

		@Override
		protected ContentPageSpecification[] createDTOArray(int size) {
			return new ContentPageSpecification[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName,
					"draftContentPageSpecificationExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageExperiences")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentPageSpecification contentPageSpecification,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName,
					"draftContentPageSpecificationExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					contentPageSpecification.
						setDraftContentPageSpecificationExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageExperiences")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PageExperience[] pageExperiencesArray =
						new PageExperience[jsonParserFieldValues.length];

					for (int i = 0; i < pageExperiencesArray.length; i++) {
						pageExperiencesArray[i] = PageExperienceSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					contentPageSpecification.setPageExperiences(
						pageExperiencesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					contentPageSpecification.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				if (jsonParserFieldValue != null) {
					contentPageSpecification.setSettings(
						SettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					contentPageSpecification.setStatus(
						ContentPageSpecification.Status.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					contentPageSpecification.setType(
						ContentPageSpecification.Type.create(
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