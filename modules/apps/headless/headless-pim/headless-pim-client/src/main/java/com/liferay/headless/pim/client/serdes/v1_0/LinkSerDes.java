/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.client.serdes.v1_0;

import com.liferay.headless.pim.client.dto.v1_0.Link;
import com.liferay.headless.pim.client.dto.v1_0.LinkReference;
import com.liferay.headless.pim.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Stefano Motta
 * @generated
 */
@Generated("")
public class LinkSerDes {

	public static Link toDTO(String json) {
		LinkJSONParser linkJSONParser = new LinkJSONParser();

		return linkJSONParser.parseToDTO(json);
	}

	public static Link[] toDTOs(String json) {
		LinkJSONParser linkJSONParser = new LinkJSONParser();

		return linkJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Link link) {
		if (link == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (link.getSourceLinkReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sourceLinkReference\": ");

			sb.append(String.valueOf(link.getSourceLinkReference()));
		}

		if (link.getTargetLinkReferences() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"targetLinkReferences\": ");

			sb.append("[");

			for (int i = 0; i < link.getTargetLinkReferences().length; i++) {
				sb.append(String.valueOf(link.getTargetLinkReferences()[i]));

				if ((i + 1) < link.getTargetLinkReferences().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (link.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(link.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LinkJSONParser linkJSONParser = new LinkJSONParser();

		return linkJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Link link) {
		if (link == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (link.getSourceLinkReference() == null) {
			map.put("sourceLinkReference", null);
		}
		else {
			map.put(
				"sourceLinkReference",
				String.valueOf(link.getSourceLinkReference()));
		}

		if (link.getTargetLinkReferences() == null) {
			map.put("targetLinkReferences", null);
		}
		else {
			map.put(
				"targetLinkReferences",
				String.valueOf(link.getTargetLinkReferences()));
		}

		if (link.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(link.getType()));
		}

		return map;
	}

	public static class LinkJSONParser extends BaseJSONParser<Link> {

		@Override
		protected Link createDTO() {
			return new Link();
		}

		@Override
		protected Link[] createDTOArray(int size) {
			return new Link[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "sourceLinkReference")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "targetLinkReferences")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Link link, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "sourceLinkReference")) {
				if (jsonParserFieldValue != null) {
					link.setSourceLinkReference(
						LinkReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "targetLinkReferences")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					LinkReference[] targetLinkReferencesArray =
						new LinkReference[jsonParserFieldValues.length];

					for (int i = 0; i < targetLinkReferencesArray.length; i++) {
						targetLinkReferencesArray[i] =
							LinkReferenceSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					link.setTargetLinkReferences(targetLinkReferencesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					link.setType((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-2126993653