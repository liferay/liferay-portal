/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Jose Luis Navarro
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Outcome of applying the candidate data mask to the sample text.",
	value = "DataMaskPreviewResult"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Outcome of applying the candidate data mask to the sample text."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DataMaskPreviewResult")
public class DataMaskPreviewResult implements Serializable {

	public static DataMaskPreviewResult toDTO(String json) {
		return ObjectMapperUtil.readValue(DataMaskPreviewResult.class, json);
	}

	public static DataMaskPreviewResult unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DataMaskPreviewResult.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Set when the candidate regex failed to compile (or threw at apply time). A diagnostic message is shown inline in the admin UI. When present, output is the unredacted sampleText."
	)
	public String getError() {
		if (_errorSupplier != null) {
			error = _errorSupplier.get();

			_errorSupplier = null;
		}

		return error;
	}

	public void setError(String error) {
		this.error = error;

		_errorSupplier = null;
	}

	@JsonIgnore
	public void setError(
		UnsafeSupplier<String, Exception> errorUnsafeSupplier) {

		_errorSupplier = () -> {
			try {
				return errorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Set when the candidate regex failed to compile (or threw at apply time). A diagnostic message is shown inline in the admin UI. When present, output is the unredacted sampleText."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String error;

	@JsonIgnore
	private Supplier<String> _errorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The sample text after redaction. Equal to sampleText when no matches were found OR when an error occurred."
	)
	public String getOutput() {
		if (_outputSupplier != null) {
			output = _outputSupplier.get();

			_outputSupplier = null;
		}

		return output;
	}

	public void setOutput(String output) {
		this.output = output;

		_outputSupplier = null;
	}

	@JsonIgnore
	public void setOutput(
		UnsafeSupplier<String, Exception> outputUnsafeSupplier) {

		_outputSupplier = () -> {
			try {
				return outputUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The sample text after redaction. Equal to sampleText when no matches were found OR when an error occurred."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String output;

	@JsonIgnore
	private Supplier<String> _outputSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataMaskPreviewResult)) {
			return false;
		}

		DataMaskPreviewResult dataMaskPreviewResult =
			(DataMaskPreviewResult)object;

		return Objects.equals(toString(), dataMaskPreviewResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String error = getError();

		if (error != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"error\": ");

			sb.append("\"");

			sb.append(_escape(error));

			sb.append("\"");
		}

		String output = getOutput();

		if (output != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"output\": ");

			sb.append("\"");

			sb.append(_escape(output));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.data.mask.dto.v1_0.DataMaskPreviewResult",
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
// LIFERAY-REST-BUILDER-HASH:-836133852