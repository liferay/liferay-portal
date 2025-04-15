/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.Comment;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class CommentSerDes {

	public static Comment toDTO(String json) {
		CommentJSONParser commentJSONParser = new CommentJSONParser();

		return commentJSONParser.parseToDTO(json);
	}

	public static Comment[] toDTOs(String json) {
		CommentJSONParser commentJSONParser = new CommentJSONParser();

		return commentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Comment comment) {
		if (comment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (comment.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(comment.getActions()));
		}

		if (comment.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(comment.getCreator()));
		}

		if (comment.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(comment.getDateCreated()));

			sb.append("\"");
		}

		if (comment.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(comment.getDateModified()));

			sb.append("\"");
		}

		if (comment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(comment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (comment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(comment.getId());
		}

		if (comment.getNumberOfComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfComments\": ");

			sb.append(comment.getNumberOfComments());
		}

		if (comment.getParentCommentId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentCommentId\": ");

			sb.append(comment.getParentCommentId());
		}

		if (comment.getText() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"text\": ");

			sb.append("\"");

			sb.append(_escape(comment.getText()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CommentJSONParser commentJSONParser = new CommentJSONParser();

		return commentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Comment comment) {
		if (comment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (comment.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(comment.getActions()));
		}

		if (comment.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(comment.getCreator()));
		}

		if (comment.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(comment.getDateCreated()));
		}

		if (comment.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(comment.getDateModified()));
		}

		if (comment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(comment.getExternalReferenceCode()));
		}

		if (comment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(comment.getId()));
		}

		if (comment.getNumberOfComments() == null) {
			map.put("numberOfComments", null);
		}
		else {
			map.put(
				"numberOfComments",
				String.valueOf(comment.getNumberOfComments()));
		}

		if (comment.getParentCommentId() == null) {
			map.put("parentCommentId", null);
		}
		else {
			map.put(
				"parentCommentId",
				String.valueOf(comment.getParentCommentId()));
		}

		if (comment.getText() == null) {
			map.put("text", null);
		}
		else {
			map.put("text", String.valueOf(comment.getText()));
		}

		return map;
	}

	public static class CommentJSONParser extends BaseJSONParser<Comment> {

		@Override
		protected Comment createDTO() {
			return new Comment();
		}

		@Override
		protected Comment[] createDTOArray(int size) {
			return new Comment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
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
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfComments")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentCommentId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Comment comment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					comment.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					comment.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					comment.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					comment.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					comment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					comment.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfComments")) {
				if (jsonParserFieldValue != null) {
					comment.setNumberOfComments(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentCommentId")) {
				if (jsonParserFieldValue != null) {
					comment.setParentCommentId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				if (jsonParserFieldValue != null) {
					comment.setText((String)jsonParserFieldValue);
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