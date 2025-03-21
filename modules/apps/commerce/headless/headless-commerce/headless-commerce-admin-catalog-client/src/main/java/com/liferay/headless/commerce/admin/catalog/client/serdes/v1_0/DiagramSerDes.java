/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Diagram;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class DiagramSerDes {

	public static Diagram toDTO(String json) {
		DiagramJSONParser diagramJSONParser = new DiagramJSONParser();

		return diagramJSONParser.parseToDTO(json);
	}

	public static Diagram[] toDTOs(String json) {
		DiagramJSONParser diagramJSONParser = new DiagramJSONParser();

		return diagramJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Diagram diagram) {
		if (diagram == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (diagram.getAttachmentBase64() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachmentBase64\": ");

			sb.append(String.valueOf(diagram.getAttachmentBase64()));
		}

		if (diagram.getColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"color\": ");

			sb.append("\"");

			sb.append(_escape(diagram.getColor()));

			sb.append("\"");
		}

		if (diagram.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(diagram.getId());
		}

		if (diagram.getImageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageId\": ");

			sb.append(diagram.getImageId());
		}

		if (diagram.getImageURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageURL\": ");

			sb.append("\"");

			sb.append(_escape(diagram.getImageURL()));

			sb.append("\"");
		}

		if (diagram.getProductExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(diagram.getProductExternalReferenceCode()));

			sb.append("\"");
		}

		if (diagram.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(diagram.getProductId());
		}

		if (diagram.getRadius() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"radius\": ");

			sb.append(diagram.getRadius());
		}

		if (diagram.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(diagram.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiagramJSONParser diagramJSONParser = new DiagramJSONParser();

		return diagramJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Diagram diagram) {
		if (diagram == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (diagram.getAttachmentBase64() == null) {
			map.put("attachmentBase64", null);
		}
		else {
			map.put(
				"attachmentBase64",
				String.valueOf(diagram.getAttachmentBase64()));
		}

		if (diagram.getColor() == null) {
			map.put("color", null);
		}
		else {
			map.put("color", String.valueOf(diagram.getColor()));
		}

		if (diagram.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(diagram.getId()));
		}

		if (diagram.getImageId() == null) {
			map.put("imageId", null);
		}
		else {
			map.put("imageId", String.valueOf(diagram.getImageId()));
		}

		if (diagram.getImageURL() == null) {
			map.put("imageURL", null);
		}
		else {
			map.put("imageURL", String.valueOf(diagram.getImageURL()));
		}

		if (diagram.getProductExternalReferenceCode() == null) {
			map.put("productExternalReferenceCode", null);
		}
		else {
			map.put(
				"productExternalReferenceCode",
				String.valueOf(diagram.getProductExternalReferenceCode()));
		}

		if (diagram.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put("productId", String.valueOf(diagram.getProductId()));
		}

		if (diagram.getRadius() == null) {
			map.put("radius", null);
		}
		else {
			map.put("radius", String.valueOf(diagram.getRadius()));
		}

		if (diagram.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(diagram.getType()));
		}

		return map;
	}

	public static class DiagramJSONParser extends BaseJSONParser<Diagram> {

		@Override
		protected Diagram createDTO() {
			return new Diagram();
		}

		@Override
		protected Diagram[] createDTOArray(int size) {
			return new Diagram[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attachmentBase64")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "color")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "imageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "imageURL")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "radius")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Diagram diagram, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attachmentBase64")) {
				if (jsonParserFieldValue != null) {
					diagram.setAttachmentBase64(
						AttachmentBase64SerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "color")) {
				if (jsonParserFieldValue != null) {
					diagram.setColor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					diagram.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "imageId")) {
				if (jsonParserFieldValue != null) {
					diagram.setImageId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "imageURL")) {
				if (jsonParserFieldValue != null) {
					diagram.setImageURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					diagram.setProductExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					diagram.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "radius")) {
				if (jsonParserFieldValue != null) {
					diagram.setRadius(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					diagram.setType((String)jsonParserFieldValue);
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