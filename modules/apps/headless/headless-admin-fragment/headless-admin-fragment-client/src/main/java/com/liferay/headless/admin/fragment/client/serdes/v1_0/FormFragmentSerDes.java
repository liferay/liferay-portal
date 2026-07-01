/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.constant.v1_0.FieldType;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FormFragment;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FragmentVersion;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

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
public class FormFragmentSerDes {

	public static FormFragment toDTO(String json) {
		FormFragmentJSONParser formFragmentJSONParser =
			new FormFragmentJSONParser();

		return formFragmentJSONParser.parseToDTO(json);
	}

	public static FormFragment[] toDTOs(String json) {
		FormFragmentJSONParser formFragmentJSONParser =
			new FormFragmentJSONParser();

		return formFragmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormFragment formFragment) {
		if (formFragment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (formFragment.getFieldTypes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldTypes\": ");

			sb.append("[");

			for (int i = 0; i < formFragment.getFieldTypes().length; i++) {
				sb.append("\"");

				sb.append(formFragment.getFieldTypes()[i]);

				sb.append("\"");

				if ((i + 1) < formFragment.getFieldTypes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formFragment.getCacheable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cacheable\": ");

			sb.append(formFragment.getCacheable());
		}

		if (formFragment.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(formFragment.getCreator());
		}

		if (formFragment.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(formFragment.getDateCreated()));

			sb.append("\"");
		}

		if (formFragment.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(formFragment.getDateModified()));

			sb.append("\"");
		}

		if (formFragment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(formFragment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (formFragment.getFragmentSet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSet\": ");

			sb.append(String.valueOf(formFragment.getFragmentSet()));
		}

		if (formFragment.getFragmentVersions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentVersions\": ");

			sb.append("[");

			for (int i = 0; i < formFragment.getFragmentVersions().length;
				 i++) {

				sb.append(
					String.valueOf(formFragment.getFragmentVersions()[i]));

				if ((i + 1) < formFragment.getFragmentVersions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formFragment.getIcon() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"icon\": ");

			sb.append("\"");

			sb.append(_escape(formFragment.getIcon()));

			sb.append("\"");
		}

		if (formFragment.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(formFragment.getKey()));

			sb.append("\"");
		}

		if (formFragment.getMarketplace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marketplace\": ");

			sb.append(formFragment.getMarketplace());
		}

		if (formFragment.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(formFragment.getName()));

			sb.append("\"");
		}

		if (formFragment.getReadOnly() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(formFragment.getReadOnly());
		}

		if (formFragment.getThumbnailURLReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnailURLReference\": ");

			sb.append(formFragment.getThumbnailURLReference());
		}

		if (formFragment.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(formFragment.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormFragmentJSONParser formFragmentJSONParser =
			new FormFragmentJSONParser();

		return formFragmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FormFragment formFragment) {
		if (formFragment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (formFragment.getFieldTypes() == null) {
			map.put("fieldTypes", null);
		}
		else {
			map.put("fieldTypes", String.valueOf(formFragment.getFieldTypes()));
		}

		if (formFragment.getCacheable() == null) {
			map.put("cacheable", null);
		}
		else {
			map.put("cacheable", String.valueOf(formFragment.getCacheable()));
		}

		if (formFragment.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(formFragment.getCreator()));
		}

		if (formFragment.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(formFragment.getDateCreated()));
		}

		if (formFragment.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(formFragment.getDateModified()));
		}

		if (formFragment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(formFragment.getExternalReferenceCode()));
		}

		if (formFragment.getFragmentSet() == null) {
			map.put("fragmentSet", null);
		}
		else {
			map.put(
				"fragmentSet", String.valueOf(formFragment.getFragmentSet()));
		}

		if (formFragment.getFragmentVersions() == null) {
			map.put("fragmentVersions", null);
		}
		else {
			map.put(
				"fragmentVersions",
				String.valueOf(formFragment.getFragmentVersions()));
		}

		if (formFragment.getIcon() == null) {
			map.put("icon", null);
		}
		else {
			map.put("icon", String.valueOf(formFragment.getIcon()));
		}

		if (formFragment.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(formFragment.getKey()));
		}

		if (formFragment.getMarketplace() == null) {
			map.put("marketplace", null);
		}
		else {
			map.put(
				"marketplace", String.valueOf(formFragment.getMarketplace()));
		}

		if (formFragment.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(formFragment.getName()));
		}

		if (formFragment.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put("readOnly", String.valueOf(formFragment.getReadOnly()));
		}

		if (formFragment.getThumbnailURLReference() == null) {
			map.put("thumbnailURLReference", null);
		}
		else {
			map.put(
				"thumbnailURLReference",
				String.valueOf(formFragment.getThumbnailURLReference()));
		}

		if (formFragment.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(formFragment.getType()));
		}

		return map;
	}

	public static class FormFragmentJSONParser
		extends BaseJSONParser<FormFragment> {

		@Override
		protected FormFragment createDTO() {
			return new FormFragment();
		}

		@Override
		protected FormFragment[] createDTOArray(int size) {
			return new FormFragment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fieldTypes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cacheable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentSet")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentVersions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "icon")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "marketplace")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "thumbnailURLReference")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormFragment formFragment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fieldTypes")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FieldType[] fieldTypesArray =
						new FieldType[jsonParserFieldValues.length];

					for (int i = 0; i < fieldTypesArray.length; i++) {
						fieldTypesArray[i] = FieldType.create(
							(String)jsonParserFieldValues[i]);
					}

					formFragment.setFieldTypes(fieldTypesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cacheable")) {
				if (jsonParserFieldValue != null) {
					formFragment.setCacheable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					formFragment.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					formFragment.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					formFragment.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					formFragment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentSet")) {
				if (jsonParserFieldValue != null) {
					formFragment.setFragmentSet(
						FragmentSetSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentVersions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FragmentVersion[] fragmentVersionsArray =
						new FragmentVersion[jsonParserFieldValues.length];

					for (int i = 0; i < fragmentVersionsArray.length; i++) {
						fragmentVersionsArray[i] = FragmentVersionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					formFragment.setFragmentVersions(fragmentVersionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "icon")) {
				if (jsonParserFieldValue != null) {
					formFragment.setIcon((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					formFragment.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marketplace")) {
				if (jsonParserFieldValue != null) {
					formFragment.setMarketplace((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					formFragment.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					formFragment.setReadOnly((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "thumbnailURLReference")) {

				if (jsonParserFieldValue != null) {
					formFragment.setThumbnailURLReference(
						ThumbnailURLReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					formFragment.setType(
						FormFragment.Type.create((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1730319614