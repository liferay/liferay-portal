/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("Range")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Range")
public class Range implements Serializable {

	public static Range toDTO(String json) {
		return ObjectMapperUtil.readValue(Range.class, json);
	}

	public static Range unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Range.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFormat() {
		if (_formatSupplier != null) {
			format = _formatSupplier.get();

			_formatSupplier = null;
		}

		return format;
	}

	public void setFormat(String format) {
		this.format = format;

		_formatSupplier = null;
	}

	@JsonIgnore
	public void setFormat(
		UnsafeSupplier<String, Exception> formatUnsafeSupplier) {

		_formatSupplier = () -> {
			try {
				return formatUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String format;

	@JsonIgnore
	private Supplier<String> _formatSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getGt() {
		if (_gtSupplier != null) {
			gt = _gtSupplier.get();

			_gtSupplier = null;
		}

		return gt;
	}

	public void setGt(Object gt) {
		this.gt = gt;

		_gtSupplier = null;
	}

	@JsonIgnore
	public void setGt(UnsafeSupplier<Object, Exception> gtUnsafeSupplier) {
		_gtSupplier = () -> {
			try {
				return gtUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object gt;

	@JsonIgnore
	private Supplier<Object> _gtSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getGte() {
		if (_gteSupplier != null) {
			gte = _gteSupplier.get();

			_gteSupplier = null;
		}

		return gte;
	}

	public void setGte(Object gte) {
		this.gte = gte;

		_gteSupplier = null;
	}

	@JsonIgnore
	public void setGte(UnsafeSupplier<Object, Exception> gteUnsafeSupplier) {
		_gteSupplier = () -> {
			try {
				return gteUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object gte;

	@JsonIgnore
	private Supplier<Object> _gteSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getLt() {
		if (_ltSupplier != null) {
			lt = _ltSupplier.get();

			_ltSupplier = null;
		}

		return lt;
	}

	public void setLt(Object lt) {
		this.lt = lt;

		_ltSupplier = null;
	}

	@JsonIgnore
	public void setLt(UnsafeSupplier<Object, Exception> ltUnsafeSupplier) {
		_ltSupplier = () -> {
			try {
				return ltUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object lt;

	@JsonIgnore
	private Supplier<Object> _ltSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getLte() {
		if (_lteSupplier != null) {
			lte = _lteSupplier.get();

			_lteSupplier = null;
		}

		return lte;
	}

	public void setLte(Object lte) {
		this.lte = lte;

		_lteSupplier = null;
	}

	@JsonIgnore
	public void setLte(UnsafeSupplier<Object, Exception> lteUnsafeSupplier) {
		_lteSupplier = () -> {
			try {
				return lteUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object lte;

	@JsonIgnore
	private Supplier<Object> _lteSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getParameterName() {
		if (_parameterNameSupplier != null) {
			parameterName = _parameterNameSupplier.get();

			_parameterNameSupplier = null;
		}

		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;

		_parameterNameSupplier = null;
	}

	@JsonIgnore
	public void setParameterName(
		UnsafeSupplier<String, Exception> parameterNameUnsafeSupplier) {

		_parameterNameSupplier = () -> {
			try {
				return parameterNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String parameterName;

	@JsonIgnore
	private Supplier<String> _parameterNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Range)) {
			return false;
		}

		Range range = (Range)object;

		return Objects.equals(toString(), range.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String format = getFormat();

		if (format != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"format\": ");

			sb.append("\"");

			sb.append(_escape(format));

			sb.append("\"");
		}

		Object gt = getGt();

		if (gt != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gt\": ");

			if (gt instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)gt));
			}
			else if (gt instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)gt));
				sb.append("\"");
			}
			else {
				sb.append(gt);
			}
		}

		Object gte = getGte();

		if (gte != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gte\": ");

			if (gte instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)gte));
			}
			else if (gte instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)gte));
				sb.append("\"");
			}
			else {
				sb.append(gte);
			}
		}

		Object lt = getLt();

		if (lt != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lt\": ");

			if (lt instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)lt));
			}
			else if (lt instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)lt));
				sb.append("\"");
			}
			else {
				sb.append(lt);
			}
		}

		Object lte = getLte();

		if (lte != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lte\": ");

			if (lte instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)lte));
			}
			else if (lte instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)lte));
				sb.append("\"");
			}
			else {
				sb.append(lte);
			}
		}

		String parameterName = getParameterName();

		if (parameterName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterName\": ");

			sb.append("\"");

			sb.append(_escape(parameterName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.Range",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
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
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}