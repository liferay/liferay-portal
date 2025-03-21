/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the maximum size of an asset library's associated mime type.",
	value = "MimeTypeLimit"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "MimeTypeLimit")
public class MimeTypeLimit implements Serializable {

	public static MimeTypeLimit toDTO(String json) {
		return ObjectMapperUtil.readValue(MimeTypeLimit.class, json);
	}

	public static MimeTypeLimit unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(MimeTypeLimit.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getMaximumSize() {
		if (_maximumSizeSupplier != null) {
			maximumSize = _maximumSizeSupplier.get();

			_maximumSizeSupplier = null;
		}

		return maximumSize;
	}

	public void setMaximumSize(Integer maximumSize) {
		this.maximumSize = maximumSize;

		_maximumSizeSupplier = null;
	}

	@JsonIgnore
	public void setMaximumSize(
		UnsafeSupplier<Integer, Exception> maximumSizeUnsafeSupplier) {

		_maximumSizeSupplier = () -> {
			try {
				return maximumSizeUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer maximumSize;

	@JsonIgnore
	private Supplier<Integer> _maximumSizeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMimeType() {
		if (_mimeTypeSupplier != null) {
			mimeType = _mimeTypeSupplier.get();

			_mimeTypeSupplier = null;
		}

		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;

		_mimeTypeSupplier = null;
	}

	@JsonIgnore
	public void setMimeType(
		UnsafeSupplier<String, Exception> mimeTypeUnsafeSupplier) {

		_mimeTypeSupplier = () -> {
			try {
				return mimeTypeUnsafeSupplier.get();
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
	protected String mimeType;

	@JsonIgnore
	private Supplier<String> _mimeTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MimeTypeLimit)) {
			return false;
		}

		MimeTypeLimit mimeTypeLimit = (MimeTypeLimit)object;

		return Objects.equals(toString(), mimeTypeLimit.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Integer maximumSize = getMaximumSize();

		if (maximumSize != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maximumSize\": ");

			sb.append(maximumSize);
		}

		String mimeType = getMimeType();

		if (mimeType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mimeType\": ");

			sb.append("\"");

			sb.append(_escape(mimeType));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.asset.library.dto.v1_0.MimeTypeLimit",
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