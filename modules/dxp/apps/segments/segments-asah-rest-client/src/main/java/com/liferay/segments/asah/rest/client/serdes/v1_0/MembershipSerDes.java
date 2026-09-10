/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.client.serdes.v1_0;

import com.liferay.segments.asah.rest.client.dto.v1_0.Membership;
import com.liferay.segments.asah.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class MembershipSerDes {

	public static Membership toDTO(String json) {
		MembershipJSONParser membershipJSONParser = new MembershipJSONParser();

		return membershipJSONParser.parseToDTO(json);
	}

	public static Membership[] toDTOs(String json) {
		MembershipJSONParser membershipJSONParser = new MembershipJSONParser();

		return membershipJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Membership membership) {
		if (membership == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (membership.getIndividualPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualPK\": ");

			sb.append("\"");

			sb.append(_escape(membership.getIndividualPK()));

			sb.append("\"");
		}

		if (membership.getRemoved() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"removed\": ");

			sb.append(membership.getRemoved());
		}

		if (membership.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(membership.getUserId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MembershipJSONParser membershipJSONParser = new MembershipJSONParser();

		return membershipJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Membership membership) {
		if (membership == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (membership.getIndividualPK() == null) {
			map.put("individualPK", null);
		}
		else {
			map.put(
				"individualPK", String.valueOf(membership.getIndividualPK()));
		}

		if (membership.getRemoved() == null) {
			map.put("removed", null);
		}
		else {
			map.put("removed", String.valueOf(membership.getRemoved()));
		}

		if (membership.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put("userId", String.valueOf(membership.getUserId()));
		}

		return map;
	}

	public static class MembershipJSONParser
		extends BaseJSONParser<Membership> {

		@Override
		protected Membership createDTO() {
			return new Membership();
		}

		@Override
		protected Membership[] createDTOArray(int size) {
			return new Membership[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "individualPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "removed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Membership membership, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "individualPK")) {
				if (jsonParserFieldValue != null) {
					membership.setIndividualPK((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "removed")) {
				if (jsonParserFieldValue != null) {
					membership.setRemoved((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					membership.setUserId(
						Long.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:2036807926