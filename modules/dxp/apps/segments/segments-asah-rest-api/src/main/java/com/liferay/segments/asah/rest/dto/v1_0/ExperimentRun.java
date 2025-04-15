/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.dto.v1_0;

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

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("ExperimentRun")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"confidenceLevel", "experimentVariants"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ExperimentRun")
public class ExperimentRun implements Serializable {

	public static ExperimentRun toDTO(String json) {
		return ObjectMapperUtil.readValue(ExperimentRun.class, json);
	}

	public static ExperimentRun unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ExperimentRun.class, json);
	}

	@DecimalMax("99")
	@DecimalMin("80")
	@io.swagger.v3.oas.annotations.media.Schema
	public Double getConfidenceLevel() {
		if (_confidenceLevelSupplier != null) {
			confidenceLevel = _confidenceLevelSupplier.get();

			_confidenceLevelSupplier = null;
		}

		return confidenceLevel;
	}

	public void setConfidenceLevel(Double confidenceLevel) {
		this.confidenceLevel = confidenceLevel;

		_confidenceLevelSupplier = null;
	}

	@JsonIgnore
	public void setConfidenceLevel(
		UnsafeSupplier<Double, Exception> confidenceLevelUnsafeSupplier) {

		_confidenceLevelSupplier = () -> {
			try {
				return confidenceLevelUnsafeSupplier.get();
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
	@NotNull
	protected Double confidenceLevel;

	@JsonIgnore
	private Supplier<Double> _confidenceLevelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ExperimentVariant[] getExperimentVariants() {
		if (_experimentVariantsSupplier != null) {
			experimentVariants = _experimentVariantsSupplier.get();

			_experimentVariantsSupplier = null;
		}

		return experimentVariants;
	}

	public void setExperimentVariants(ExperimentVariant[] experimentVariants) {
		this.experimentVariants = experimentVariants;

		_experimentVariantsSupplier = null;
	}

	@JsonIgnore
	public void setExperimentVariants(
		UnsafeSupplier<ExperimentVariant[], Exception>
			experimentVariantsUnsafeSupplier) {

		_experimentVariantsSupplier = () -> {
			try {
				return experimentVariantsUnsafeSupplier.get();
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
	@NotNull
	protected ExperimentVariant[] experimentVariants;

	@JsonIgnore
	private Supplier<ExperimentVariant[]> _experimentVariantsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(String status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<String, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
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
	protected String status;

	@JsonIgnore
	private Supplier<String> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExperimentRun)) {
			return false;
		}

		ExperimentRun experimentRun = (ExperimentRun)object;

		return Objects.equals(toString(), experimentRun.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Double confidenceLevel = getConfidenceLevel();

		if (confidenceLevel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"confidenceLevel\": ");

			sb.append(confidenceLevel);
		}

		ExperimentVariant[] experimentVariants = getExperimentVariants();

		if (experimentVariants != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"experimentVariants\": ");

			sb.append("[");

			for (int i = 0; i < experimentVariants.length; i++) {
				sb.append(String.valueOf(experimentVariants[i]));

				if ((i + 1) < experimentVariants.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(status));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.segments.asah.rest.dto.v1_0.ExperimentRun",
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