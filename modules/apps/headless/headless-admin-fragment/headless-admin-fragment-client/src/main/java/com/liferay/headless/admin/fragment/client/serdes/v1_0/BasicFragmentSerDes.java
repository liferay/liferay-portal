/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.BasicFragment;
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
public class BasicFragmentSerDes {

	public static BasicFragment toDTO(String json) {
		BasicFragmentJSONParser basicFragmentJSONParser =
			new BasicFragmentJSONParser();

		return basicFragmentJSONParser.parseToDTO(json);
	}

	public static BasicFragment[] toDTOs(String json) {
		BasicFragmentJSONParser basicFragmentJSONParser =
			new BasicFragmentJSONParser();

		return basicFragmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BasicFragment basicFragment) {
		if (basicFragment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (basicFragment.getCacheable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cacheable\": ");

			sb.append(basicFragment.getCacheable());
		}

		if (basicFragment.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(basicFragment.getCreator());
		}

		if (basicFragment.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(basicFragment.getDateCreated()));

			sb.append("\"");
		}

		if (basicFragment.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					basicFragment.getDateModified()));

			sb.append("\"");
		}

		if (basicFragment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(basicFragment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (basicFragment.getFragmentSet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSet\": ");

			sb.append(String.valueOf(basicFragment.getFragmentSet()));
		}

		if (basicFragment.getFragmentVersions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentVersions\": ");

			sb.append("[");

			for (int i = 0; i < basicFragment.getFragmentVersions().length;
				 i++) {

				sb.append(
					String.valueOf(basicFragment.getFragmentVersions()[i]));

				if ((i + 1) < basicFragment.getFragmentVersions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (basicFragment.getIcon() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"icon\": ");

			sb.append("\"");

			sb.append(_escape(basicFragment.getIcon()));

			sb.append("\"");
		}

		if (basicFragment.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(basicFragment.getKey()));

			sb.append("\"");
		}

		if (basicFragment.getMarketplace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marketplace\": ");

			sb.append(basicFragment.getMarketplace());
		}

		if (basicFragment.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(basicFragment.getName()));

			sb.append("\"");
		}

		if (basicFragment.getReadOnly() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(basicFragment.getReadOnly());
		}

		if (basicFragment.getThumbnailURLReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnailURLReference\": ");

			sb.append(basicFragment.getThumbnailURLReference());
		}

		if (basicFragment.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(basicFragment.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BasicFragmentJSONParser basicFragmentJSONParser =
			new BasicFragmentJSONParser();

		return basicFragmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BasicFragment basicFragment) {
		if (basicFragment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (basicFragment.getCacheable() == null) {
			map.put("cacheable", null);
		}
		else {
			map.put("cacheable", String.valueOf(basicFragment.getCacheable()));
		}

		if (basicFragment.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(basicFragment.getCreator()));
		}

		if (basicFragment.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(basicFragment.getDateCreated()));
		}

		if (basicFragment.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					basicFragment.getDateModified()));
		}

		if (basicFragment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(basicFragment.getExternalReferenceCode()));
		}

		if (basicFragment.getFragmentSet() == null) {
			map.put("fragmentSet", null);
		}
		else {
			map.put(
				"fragmentSet", String.valueOf(basicFragment.getFragmentSet()));
		}

		if (basicFragment.getFragmentVersions() == null) {
			map.put("fragmentVersions", null);
		}
		else {
			map.put(
				"fragmentVersions",
				String.valueOf(basicFragment.getFragmentVersions()));
		}

		if (basicFragment.getIcon() == null) {
			map.put("icon", null);
		}
		else {
			map.put("icon", String.valueOf(basicFragment.getIcon()));
		}

		if (basicFragment.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(basicFragment.getKey()));
		}

		if (basicFragment.getMarketplace() == null) {
			map.put("marketplace", null);
		}
		else {
			map.put(
				"marketplace", String.valueOf(basicFragment.getMarketplace()));
		}

		if (basicFragment.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(basicFragment.getName()));
		}

		if (basicFragment.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put("readOnly", String.valueOf(basicFragment.getReadOnly()));
		}

		if (basicFragment.getThumbnailURLReference() == null) {
			map.put("thumbnailURLReference", null);
		}
		else {
			map.put(
				"thumbnailURLReference",
				String.valueOf(basicFragment.getThumbnailURLReference()));
		}

		if (basicFragment.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(basicFragment.getType()));
		}

		return map;
	}

	public static class BasicFragmentJSONParser
		extends BaseJSONParser<BasicFragment> {

		@Override
		protected BasicFragment createDTO() {
			return new BasicFragment();
		}

		@Override
		protected BasicFragment[] createDTOArray(int size) {
			return new BasicFragment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "cacheable")) {
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
			BasicFragment basicFragment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cacheable")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setCacheable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					basicFragment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentSet")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setFragmentSet(
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

					basicFragment.setFragmentVersions(fragmentVersionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "icon")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setIcon((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marketplace")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setMarketplace((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setReadOnly((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "thumbnailURLReference")) {

				if (jsonParserFieldValue != null) {
					basicFragment.setThumbnailURLReference(
						ThumbnailURLReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					basicFragment.setType(
						BasicFragment.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:951801515