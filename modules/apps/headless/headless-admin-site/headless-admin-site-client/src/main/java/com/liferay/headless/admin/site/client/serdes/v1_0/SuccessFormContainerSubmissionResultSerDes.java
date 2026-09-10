/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.EmbeddedMessageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.StayInPageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.SuccessFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.dto.v1_0.URLFormContainerSubmissionResult;
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
public class SuccessFormContainerSubmissionResultSerDes {

	public static SuccessFormContainerSubmissionResult toDTO(String json) {
		SuccessFormContainerSubmissionResultJSONParser
			successFormContainerSubmissionResultJSONParser =
				new SuccessFormContainerSubmissionResultJSONParser();

		return successFormContainerSubmissionResultJSONParser.parseToDTO(json);
	}

	public static SuccessFormContainerSubmissionResult[] toDTOs(String json) {
		SuccessFormContainerSubmissionResultJSONParser
			successFormContainerSubmissionResultJSONParser =
				new SuccessFormContainerSubmissionResultJSONParser();

		return successFormContainerSubmissionResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SuccessFormContainerSubmissionResult
			successFormContainerSubmissionResult) {

		if (successFormContainerSubmissionResult == null) {
			return "null";
		}

		SuccessFormContainerSubmissionResult.Type type =
			successFormContainerSubmissionResult.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("DisplayPage")) {
				return DisplayPageFormContainerSubmissionResultSerDes.toJSON(
					(DisplayPageFormContainerSubmissionResult)
						successFormContainerSubmissionResult);
			}

			if (typeString.equals("EmbeddedMessage")) {
				return EmbeddedMessageFormContainerSubmissionResultSerDes.
					toJSON(
						(EmbeddedMessageFormContainerSubmissionResult)
							successFormContainerSubmissionResult);
			}

			if (typeString.equals("SitePage")) {
				return SitePageFormContainerSubmissionResultSerDes.toJSON(
					(SitePageFormContainerSubmissionResult)
						successFormContainerSubmissionResult);
			}

			if (typeString.equals("StayInPage")) {
				return StayInPageFormContainerSubmissionResultSerDes.toJSON(
					(StayInPageFormContainerSubmissionResult)
						successFormContainerSubmissionResult);
			}

			if (typeString.equals("Url")) {
				return URLFormContainerSubmissionResultSerDes.toJSON(
					(URLFormContainerSubmissionResult)
						successFormContainerSubmissionResult);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		SuccessFormContainerSubmissionResultJSONParser
			successFormContainerSubmissionResultJSONParser =
				new SuccessFormContainerSubmissionResultJSONParser();

		return successFormContainerSubmissionResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SuccessFormContainerSubmissionResult
			successFormContainerSubmissionResult) {

		if (successFormContainerSubmissionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (successFormContainerSubmissionResult.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(successFormContainerSubmissionResult.getType()));
		}

		return map;
	}

	public static class SuccessFormContainerSubmissionResultJSONParser
		extends BaseJSONParser<SuccessFormContainerSubmissionResult> {

		@Override
		protected SuccessFormContainerSubmissionResult createDTO() {
			return null;
		}

		@Override
		protected SuccessFormContainerSubmissionResult[] createDTOArray(
			int size) {

			return new SuccessFormContainerSubmissionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public SuccessFormContainerSubmissionResult parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("DisplayPage")) {
					return DisplayPageFormContainerSubmissionResult.toDTO(json);
				}

				if (typeString.equals("EmbeddedMessage")) {
					return EmbeddedMessageFormContainerSubmissionResult.toDTO(
						json);
				}

				if (typeString.equals("SitePage")) {
					return SitePageFormContainerSubmissionResult.toDTO(json);
				}

				if (typeString.equals("StayInPage")) {
					return StayInPageFormContainerSubmissionResult.toDTO(json);
				}

				if (typeString.equals("Url")) {
					return URLFormContainerSubmissionResult.toDTO(json);
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
			SuccessFormContainerSubmissionResult
				successFormContainerSubmissionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					successFormContainerSubmissionResult.setType(
						SuccessFormContainerSubmissionResult.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:809318243