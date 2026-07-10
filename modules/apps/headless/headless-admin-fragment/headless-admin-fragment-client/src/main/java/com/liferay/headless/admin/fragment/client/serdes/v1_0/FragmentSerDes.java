/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.BasicFragment;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FormFragment;
import com.liferay.headless.admin.fragment.client.dto.v1_0.Fragment;
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
public class FragmentSerDes {

	public static Fragment toDTO(String json) {
		FragmentJSONParser fragmentJSONParser = new FragmentJSONParser();

		return fragmentJSONParser.parseToDTO(json);
	}

	public static Fragment[] toDTOs(String json) {
		FragmentJSONParser fragmentJSONParser = new FragmentJSONParser();

		return fragmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Fragment fragment) {
		if (fragment == null) {
			return "null";
		}

		Fragment.Type type = fragment.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("BasicFragment")) {
				return BasicFragmentSerDes.toJSON((BasicFragment)fragment);
			}

			if (typeString.equals("FormFragment")) {
				return FormFragmentSerDes.toJSON((FormFragment)fragment);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		FragmentJSONParser fragmentJSONParser = new FragmentJSONParser();

		return fragmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Fragment fragment) {
		if (fragment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragment.getCacheable() == null) {
			map.put("cacheable", null);
		}
		else {
			map.put("cacheable", String.valueOf(fragment.getCacheable()));
		}

		if (fragment.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(fragment.getCreator()));
		}

		if (fragment.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(fragment.getDateCreated()));
		}

		if (fragment.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(fragment.getDateModified()));
		}

		if (fragment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(fragment.getExternalReferenceCode()));
		}

		if (fragment.getFragmentSet() == null) {
			map.put("fragmentSet", null);
		}
		else {
			map.put("fragmentSet", String.valueOf(fragment.getFragmentSet()));
		}

		if (fragment.getFragmentSetExternalReferenceCode() == null) {
			map.put("fragmentSetExternalReferenceCode", null);
		}
		else {
			map.put(
				"fragmentSetExternalReferenceCode",
				String.valueOf(fragment.getFragmentSetExternalReferenceCode()));
		}

		if (fragment.getFragmentVersions() == null) {
			map.put("fragmentVersions", null);
		}
		else {
			map.put(
				"fragmentVersions",
				String.valueOf(fragment.getFragmentVersions()));
		}

		if (fragment.getIcon() == null) {
			map.put("icon", null);
		}
		else {
			map.put("icon", String.valueOf(fragment.getIcon()));
		}

		if (fragment.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(fragment.getKey()));
		}

		if (fragment.getMarketplace() == null) {
			map.put("marketplace", null);
		}
		else {
			map.put("marketplace", String.valueOf(fragment.getMarketplace()));
		}

		if (fragment.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(fragment.getName()));
		}

		if (fragment.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put("readOnly", String.valueOf(fragment.getReadOnly()));
		}

		if (fragment.getThumbnailURLReference() == null) {
			map.put("thumbnailURLReference", null);
		}
		else {
			map.put(
				"thumbnailURLReference",
				String.valueOf(fragment.getThumbnailURLReference()));
		}

		if (fragment.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(fragment.getType()));
		}

		return map;
	}

	public static class FragmentJSONParser extends BaseJSONParser<Fragment> {

		@Override
		protected Fragment createDTO() {
			return null;
		}

		@Override
		protected Fragment[] createDTOArray(int size) {
			return new Fragment[size];
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
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentSetExternalReferenceCode")) {

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
		public Fragment parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("BasicFragment")) {
					return BasicFragment.toDTO(json);
				}

				if (typeString.equals("FormFragment")) {
					return FormFragment.toDTO(json);
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
			Fragment fragment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cacheable")) {
				if (jsonParserFieldValue != null) {
					fragment.setCacheable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					fragment.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					fragment.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					fragment.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentSet")) {
				if (jsonParserFieldValue != null) {
					fragment.setFragmentSet(
						FragmentSetSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentSetExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragment.setFragmentSetExternalReferenceCode(
						(String)jsonParserFieldValue);
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

					fragment.setFragmentVersions(fragmentVersionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "icon")) {
				if (jsonParserFieldValue != null) {
					fragment.setIcon((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					fragment.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marketplace")) {
				if (jsonParserFieldValue != null) {
					fragment.setMarketplace((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					fragment.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					fragment.setReadOnly((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "thumbnailURLReference")) {

				if (jsonParserFieldValue != null) {
					fragment.setThumbnailURLReference(
						ThumbnailURLReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					fragment.setType(
						Fragment.Type.create((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-1654088476