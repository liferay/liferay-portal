/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0.MeasurementUnit;
import com.liferay.headless.commerce.admin.site.setting.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class MeasurementUnitSerDes {

	public static MeasurementUnit toDTO(String json) {
		MeasurementUnitJSONParser measurementUnitJSONParser =
			new MeasurementUnitJSONParser();

		return measurementUnitJSONParser.parseToDTO(json);
	}

	public static MeasurementUnit[] toDTOs(String json) {
		MeasurementUnitJSONParser measurementUnitJSONParser =
			new MeasurementUnitJSONParser();

		return measurementUnitJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MeasurementUnit measurementUnit) {
		if (measurementUnit == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (measurementUnit.getCompanyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"companyId\": ");

			sb.append(measurementUnit.getCompanyId());
		}

		if (measurementUnit.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(measurementUnit.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (measurementUnit.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(measurementUnit.getId());
		}

		if (measurementUnit.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(measurementUnit.getKey()));

			sb.append("\"");
		}

		if (measurementUnit.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(measurementUnit.getName()));
		}

		if (measurementUnit.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(measurementUnit.getPrimary());
		}

		if (measurementUnit.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(measurementUnit.getPriority());
		}

		if (measurementUnit.getRate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rate\": ");

			sb.append(measurementUnit.getRate());
		}

		if (measurementUnit.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(measurementUnit.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MeasurementUnitJSONParser measurementUnitJSONParser =
			new MeasurementUnitJSONParser();

		return measurementUnitJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(MeasurementUnit measurementUnit) {
		if (measurementUnit == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (measurementUnit.getCompanyId() == null) {
			map.put("companyId", null);
		}
		else {
			map.put(
				"companyId", String.valueOf(measurementUnit.getCompanyId()));
		}

		if (measurementUnit.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(measurementUnit.getExternalReferenceCode()));
		}

		if (measurementUnit.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(measurementUnit.getId()));
		}

		if (measurementUnit.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(measurementUnit.getKey()));
		}

		if (measurementUnit.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(measurementUnit.getName()));
		}

		if (measurementUnit.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put("primary", String.valueOf(measurementUnit.getPrimary()));
		}

		if (measurementUnit.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(measurementUnit.getPriority()));
		}

		if (measurementUnit.getRate() == null) {
			map.put("rate", null);
		}
		else {
			map.put("rate", String.valueOf(measurementUnit.getRate()));
		}

		if (measurementUnit.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(measurementUnit.getType()));
		}

		return map;
	}

	public static class MeasurementUnitJSONParser
		extends BaseJSONParser<MeasurementUnit> {

		@Override
		protected MeasurementUnit createDTO() {
			return new MeasurementUnit();
		}

		@Override
		protected MeasurementUnit[] createDTOArray(int size) {
			return new MeasurementUnit[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "companyId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MeasurementUnit measurementUnit, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "companyId")) {
				if (jsonParserFieldValue != null) {
					measurementUnit.setCompanyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					measurementUnit.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					measurementUnit.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					measurementUnit.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					measurementUnit.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					measurementUnit.setPrimary((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					measurementUnit.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rate")) {
				if (jsonParserFieldValue != null) {
					measurementUnit.setRate(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					measurementUnit.setType((String)jsonParserFieldValue);
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