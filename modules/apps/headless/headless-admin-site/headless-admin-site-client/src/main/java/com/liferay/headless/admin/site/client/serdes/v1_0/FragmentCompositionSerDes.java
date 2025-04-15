/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentComposition;
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
public class FragmentCompositionSerDes {

	public static FragmentComposition toDTO(String json) {
		FragmentCompositionJSONParser fragmentCompositionJSONParser =
			new FragmentCompositionJSONParser();

		return fragmentCompositionJSONParser.parseToDTO(json);
	}

	public static FragmentComposition[] toDTOs(String json) {
		FragmentCompositionJSONParser fragmentCompositionJSONParser =
			new FragmentCompositionJSONParser();

		return fragmentCompositionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentComposition fragmentComposition) {
		if (fragmentComposition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragmentComposition.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(fragmentComposition.getCreator());
		}

		if (fragmentComposition.getCreatorExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(fragmentComposition.getCreatorExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentComposition.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					fragmentComposition.getDateCreated()));

			sb.append("\"");
		}

		if (fragmentComposition.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					fragmentComposition.getDateModified()));

			sb.append("\"");
		}

		if (fragmentComposition.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					fragmentComposition.getDatePublished()));

			sb.append("\"");
		}

		if (fragmentComposition.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(fragmentComposition.getDescription()));

			sb.append("\"");
		}

		if (fragmentComposition.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(fragmentComposition.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentComposition.getFragmentSetExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSetExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentComposition.getFragmentSetExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentComposition.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(fragmentComposition.getKey()));

			sb.append("\"");
		}

		if (fragmentComposition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(fragmentComposition.getName()));

			sb.append("\"");
		}

		if (fragmentComposition.getPageElement() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageElement\": ");

			sb.append(String.valueOf(fragmentComposition.getPageElement()));
		}

		if (fragmentComposition.getThumbnail() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append(String.valueOf(fragmentComposition.getThumbnail()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentCompositionJSONParser fragmentCompositionJSONParser =
			new FragmentCompositionJSONParser();

		return fragmentCompositionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentComposition fragmentComposition) {

		if (fragmentComposition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragmentComposition.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator", String.valueOf(fragmentComposition.getCreator()));
		}

		if (fragmentComposition.getCreatorExternalReferenceCode() == null) {
			map.put("creatorExternalReferenceCode", null);
		}
		else {
			map.put(
				"creatorExternalReferenceCode",
				String.valueOf(
					fragmentComposition.getCreatorExternalReferenceCode()));
		}

		if (fragmentComposition.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					fragmentComposition.getDateCreated()));
		}

		if (fragmentComposition.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					fragmentComposition.getDateModified()));
		}

		if (fragmentComposition.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(
					fragmentComposition.getDatePublished()));
		}

		if (fragmentComposition.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(fragmentComposition.getDescription()));
		}

		if (fragmentComposition.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(fragmentComposition.getExternalReferenceCode()));
		}

		if (fragmentComposition.getFragmentSetExternalReferenceCode() == null) {
			map.put("fragmentSetExternalReferenceCode", null);
		}
		else {
			map.put(
				"fragmentSetExternalReferenceCode",
				String.valueOf(
					fragmentComposition.getFragmentSetExternalReferenceCode()));
		}

		if (fragmentComposition.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(fragmentComposition.getKey()));
		}

		if (fragmentComposition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(fragmentComposition.getName()));
		}

		if (fragmentComposition.getPageElement() == null) {
			map.put("pageElement", null);
		}
		else {
			map.put(
				"pageElement",
				String.valueOf(fragmentComposition.getPageElement()));
		}

		if (fragmentComposition.getThumbnail() == null) {
			map.put("thumbnail", null);
		}
		else {
			map.put(
				"thumbnail",
				String.valueOf(fragmentComposition.getThumbnail()));
		}

		return map;
	}

	public static class FragmentCompositionJSONParser
		extends BaseJSONParser<FragmentComposition> {

		@Override
		protected FragmentComposition createDTO() {
			return new FragmentComposition();
		}

		@Override
		protected FragmentComposition[] createDTOArray(int size) {
			return new FragmentComposition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "creatorExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentSetExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageElement")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentComposition fragmentComposition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "creatorExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentComposition.setCreatorExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentComposition.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentSetExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentComposition.setFragmentSetExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageElement")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setPageElement(
						PageElementSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				if (jsonParserFieldValue != null) {
					fragmentComposition.setThumbnail(
						ItemExternalReferenceSerDes.toDTO(
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