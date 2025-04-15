/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.custom.field;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class CustomValue {

	public static CustomValue toDTO(String json) {
		CustomValueJSONParser customValueJSONParser =
			new CustomValueJSONParser();

		return customValueJSONParser.parseToDTO(json);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CustomValue)) {
			return false;
		}

		CustomValue customValue = (CustomValue)object;

		return Objects.equals(toString(), customValue.toString());
	}

	public Object getData() {
		return data;
	}

	public Map<String, String> getData_i18n() {
		return data_i18n;
	}

	public Geo getGeo() {
		return geo;
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setData(UnsafeSupplier<Object, Exception> dataUnsafeSupplier) {
		try {
			data = dataUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setData_i18n(Map<String, String> data_i18n) {
		this.data_i18n = data_i18n;
	}

	public void setData_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			data_i18nUnsafeSupplier) {

		try {
			data_i18n = data_i18nUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setGeo(Geo geo) {
		this.geo = geo;
	}

	public void setGeo(UnsafeSupplier<Geo, Exception> geoUnsafeSupplier) {
		try {
			geo = geoUnsafeSupplier.get();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public String toString() {
		return CustomValueJSONParser.toJSON(this);
	}

	protected Object data;
	protected Map<String, String> data_i18n;
	protected Geo geo;

	private static class CustomValueJSONParser
		extends BaseJSONParser<CustomValue> {

		public static String toJSON(CustomValue customValue) {
			if (customValue == null) {
				return "null";
			}

			StringBuilder sb = new StringBuilder();

			sb.append("{");

			if (customValue.getData() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"data\": ");

				if (customValue.getData() instanceof String) {
					sb.append("\"");
					sb.append(_escape((String)customValue.getData()));
					sb.append("\"");
				}
				else {
					sb.append(customValue.getData());
				}
			}

			if (customValue.getData_i18n() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"data_i18n\": ");

				sb.append(_toJSON(customValue.getData_i18n()));
			}

			if (customValue.getGeo() != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append("\"geo\": ");

				sb.append(String.valueOf(customValue.getGeo()));
			}

			sb.append("}");

			return sb.toString();
		}

		@Override
		protected CustomValue createDTO() {
			return new CustomValue();
		}

		@Override
		protected CustomValue[] createDTOArray(int size) {
			return new CustomValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "data")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "data_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "geo")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CustomValue customValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "data")) {
				if (jsonParserFieldValue != null) {
					customValue.setData((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "data_i18n")) {
				if (jsonParserFieldValue != null) {
					customValue.setData_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "geo")) {
				if (jsonParserFieldValue != null) {
					customValue.setGeo(Geo.toDTO((String)jsonParserFieldValue));
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

			Iterator<Map.Entry<String, ?>> iterator = set.iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, ?> entry = iterator.next();

				sb.append("\"");
				sb.append(entry.getKey());
				sb.append("\": ");
				sb.append(_toJSON(entry.getValue()));

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

}