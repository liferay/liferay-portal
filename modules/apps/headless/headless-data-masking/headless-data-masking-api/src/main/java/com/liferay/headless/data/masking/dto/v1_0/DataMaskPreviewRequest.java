/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.dto.v1_0;

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

import jakarta.validation.constraints.NotEmpty;

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
	description = "A candidate data mask plus a sample text to apply it to.",
	value = "DataMaskPreviewRequest"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A candidate data mask plus a sample text to apply it to.",
	requiredProperties = {"detectionRegex", "replacementValue", "sampleText"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DataMaskPreviewRequest")
public class DataMaskPreviewRequest implements Serializable {

	public static DataMaskPreviewRequest toDTO(String json) {
		return ObjectMapperUtil.readValue(DataMaskPreviewRequest.class, json);
	}

	public static DataMaskPreviewRequest unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DataMaskPreviewRequest.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The regex that identifies the substring(s) to be masked. Compiled by java.util.regex.Pattern on the server, so semantics match what the production redaction pipeline will see at runtime &mdash; not what a client side JavaScript regex engine would produce."
	)
	public String getDetectionRegex() {
		if (_detectionRegexSupplier != null) {
			detectionRegex = _detectionRegexSupplier.get();

			_detectionRegexSupplier = null;
		}

		return detectionRegex;
	}

	public void setDetectionRegex(String detectionRegex) {
		this.detectionRegex = detectionRegex;

		_detectionRegexSupplier = null;
	}

	@JsonIgnore
	public void setDetectionRegex(
		UnsafeSupplier<String, Exception> detectionRegexUnsafeSupplier) {

		_detectionRegexSupplier = () -> {
			try {
				return detectionRegexUnsafeSupplier.get();
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
		description = "The regex that identifies the substring(s) to be masked. Compiled by java.util.regex.Pattern on the server, so semantics match what the production redaction pipeline will see at runtime &mdash; not what a client side JavaScript regex engine would produce."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String detectionRegex;

	@JsonIgnore
	private Supplier<String> _detectionRegexSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Optional. When set, each detection match is further processed by this regex against the matched substring; useful for partial redaction styles like \"keep the last 4 digits\" or \"/24 CIDR truncation\". When unset, the entire detection match is replaced by replacementValue."
	)
	public String getReplacementRegex() {
		if (_replacementRegexSupplier != null) {
			replacementRegex = _replacementRegexSupplier.get();

			_replacementRegexSupplier = null;
		}

		return replacementRegex;
	}

	public void setReplacementRegex(String replacementRegex) {
		this.replacementRegex = replacementRegex;

		_replacementRegexSupplier = null;
	}

	@JsonIgnore
	public void setReplacementRegex(
		UnsafeSupplier<String, Exception> replacementRegexUnsafeSupplier) {

		_replacementRegexSupplier = () -> {
			try {
				return replacementRegexUnsafeSupplier.get();
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
		description = "Optional. When set, each detection match is further processed by this regex against the matched substring; useful for partial redaction styles like \"keep the last 4 digits\" or \"/24 CIDR truncation\". When unset, the entire detection match is replaced by replacementValue."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String replacementRegex;

	@JsonIgnore
	private Supplier<String> _replacementRegexSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The literal string (or replacement template with $1, $2&hellip; back references when replacementRegex is set) that substitutes a match."
	)
	public String getReplacementValue() {
		if (_replacementValueSupplier != null) {
			replacementValue = _replacementValueSupplier.get();

			_replacementValueSupplier = null;
		}

		return replacementValue;
	}

	public void setReplacementValue(String replacementValue) {
		this.replacementValue = replacementValue;

		_replacementValueSupplier = null;
	}

	@JsonIgnore
	public void setReplacementValue(
		UnsafeSupplier<String, Exception> replacementValueUnsafeSupplier) {

		_replacementValueSupplier = () -> {
			try {
				return replacementValueUnsafeSupplier.get();
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
		description = "The literal string (or replacement template with $1, $2&hellip; back references when replacementRegex is set) that substitutes a match."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String replacementValue;

	@JsonIgnore
	private Supplier<String> _replacementValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The input string the candidate mask is applied to. It is returned in DataMaskPreviewResult.output after redaction."
	)
	public String getSampleText() {
		if (_sampleTextSupplier != null) {
			sampleText = _sampleTextSupplier.get();

			_sampleTextSupplier = null;
		}

		return sampleText;
	}

	public void setSampleText(String sampleText) {
		this.sampleText = sampleText;

		_sampleTextSupplier = null;
	}

	@JsonIgnore
	public void setSampleText(
		UnsafeSupplier<String, Exception> sampleTextUnsafeSupplier) {

		_sampleTextSupplier = () -> {
			try {
				return sampleTextUnsafeSupplier.get();
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
		description = "The input string the candidate mask is applied to. It is returned in DataMaskPreviewResult.output after redaction."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String sampleText;

	@JsonIgnore
	private Supplier<String> _sampleTextSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataMaskPreviewRequest)) {
			return false;
		}

		DataMaskPreviewRequest dataMaskPreviewRequest =
			(DataMaskPreviewRequest)object;

		return Objects.equals(toString(), dataMaskPreviewRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String detectionRegex = getDetectionRegex();

		if (detectionRegex != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"detectionRegex\": ");

			sb.append("\"");

			sb.append(_escape(detectionRegex));

			sb.append("\"");
		}

		String replacementRegex = getReplacementRegex();

		if (replacementRegex != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementRegex\": ");

			sb.append("\"");

			sb.append(_escape(replacementRegex));

			sb.append("\"");
		}

		String replacementValue = getReplacementValue();

		if (replacementValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementValue\": ");

			sb.append("\"");

			sb.append(_escape(replacementValue));

			sb.append("\"");
		}

		String sampleText = getSampleText();

		if (sampleText != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleText\": ");

			sb.append("\"");

			sb.append(_escape(sampleText));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.data.masking.dto.v1_0.DataMaskPreviewRequest",
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
// LIFERAY-REST-BUILDER-HASH:-1675791869