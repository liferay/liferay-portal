/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.HoursAvailable;
import com.liferay.headless.admin.user.client.dto.v1_0.Service;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class ServiceSerDes {

	public static Service toDTO(String json) {
		ServiceJSONParser serviceJSONParser = new ServiceJSONParser();

		return serviceJSONParser.parseToDTO(json);
	}

	public static Service[] toDTOs(String json) {
		ServiceJSONParser serviceJSONParser = new ServiceJSONParser();

		return serviceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Service service) {
		if (service == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (service.getHoursAvailable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hoursAvailable\": ");

			sb.append("[");

			for (int i = 0; i < service.getHoursAvailable().length; i++) {
				sb.append(String.valueOf(service.getHoursAvailable()[i]));

				if ((i + 1) < service.getHoursAvailable().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (service.getServiceType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"serviceType\": ");

			sb.append("\"");

			sb.append(_escape(service.getServiceType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ServiceJSONParser serviceJSONParser = new ServiceJSONParser();

		return serviceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Service service) {
		if (service == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (service.getHoursAvailable() == null) {
			map.put("hoursAvailable", null);
		}
		else {
			map.put(
				"hoursAvailable", String.valueOf(service.getHoursAvailable()));
		}

		if (service.getServiceType() == null) {
			map.put("serviceType", null);
		}
		else {
			map.put("serviceType", String.valueOf(service.getServiceType()));
		}

		return map;
	}

	public static class ServiceJSONParser extends BaseJSONParser<Service> {

		@Override
		protected Service createDTO() {
			return new Service();
		}

		@Override
		protected Service[] createDTOArray(int size) {
			return new Service[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "hoursAvailable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "serviceType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Service service, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "hoursAvailable")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					HoursAvailable[] hoursAvailableArray =
						new HoursAvailable[jsonParserFieldValues.length];

					for (int i = 0; i < hoursAvailableArray.length; i++) {
						hoursAvailableArray[i] = HoursAvailableSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					service.setHoursAvailable(hoursAvailableArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "serviceType")) {
				if (jsonParserFieldValue != null) {
					service.setServiceType((String)jsonParserFieldValue);
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