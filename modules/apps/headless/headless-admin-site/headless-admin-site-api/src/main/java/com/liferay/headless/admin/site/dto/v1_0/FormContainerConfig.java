/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The form container page element definition form's container configuration.",
	value = "FormContainerConfig"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The form container page element definition form's container configuration."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FormContainerConfig")
public class FormContainerConfig implements Serializable {

	public static FormContainerConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(FormContainerConfig.class, json);
	}

	public static FormContainerConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			FormContainerConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form container reference."
	)
	@Valid
	public FormContainerReference getFormContainerReference() {
		if (_formContainerReferenceSupplier != null) {
			formContainerReference = _formContainerReferenceSupplier.get();

			_formContainerReferenceSupplier = null;
		}

		return formContainerReference;
	}

	public void setFormContainerReference(
		FormContainerReference formContainerReference) {

		this.formContainerReference = formContainerReference;

		_formContainerReferenceSupplier = null;
	}

	@JsonIgnore
	public void setFormContainerReference(
		UnsafeSupplier<FormContainerReference, Exception>
			formContainerReferenceUnsafeSupplier) {

		_formContainerReferenceSupplier = () -> {
			try {
				return formContainerReferenceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The form container reference.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FormContainerReference formContainerReference;

	@JsonIgnore
	private Supplier<FormContainerReference> _formContainerReferenceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("formContainerType")
	@Valid
	public FormContainerType getFormContainerType() {
		if (_formContainerTypeSupplier != null) {
			formContainerType = _formContainerTypeSupplier.get();

			_formContainerTypeSupplier = null;
		}

		return formContainerType;
	}

	@JsonIgnore
	public String getFormContainerTypeAsString() {
		FormContainerType formContainerType = getFormContainerType();

		if (formContainerType == null) {
			return null;
		}

		return formContainerType.toString();
	}

	public void setFormContainerType(FormContainerType formContainerType) {
		this.formContainerType = formContainerType;

		_formContainerTypeSupplier = null;
	}

	@JsonIgnore
	public void setFormContainerType(
		UnsafeSupplier<FormContainerType, Exception>
			formContainerTypeUnsafeSupplier) {

		_formContainerTypeSupplier = () -> {
			try {
				return formContainerTypeUnsafeSupplier.get();
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
	protected FormContainerType formContainerType;

	@JsonIgnore
	private Supplier<FormContainerType> _formContainerTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public LocalizationConfig getLocalizationConfig() {
		if (_localizationConfigSupplier != null) {
			localizationConfig = _localizationConfigSupplier.get();

			_localizationConfigSupplier = null;
		}

		return localizationConfig;
	}

	public void setLocalizationConfig(LocalizationConfig localizationConfig) {
		this.localizationConfig = localizationConfig;

		_localizationConfigSupplier = null;
	}

	@JsonIgnore
	public void setLocalizationConfig(
		UnsafeSupplier<LocalizationConfig, Exception>
			localizationConfigUnsafeSupplier) {

		_localizationConfigSupplier = () -> {
			try {
				return localizationConfigUnsafeSupplier.get();
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
	protected LocalizationConfig localizationConfig;

	@JsonIgnore
	private Supplier<LocalizationConfig> _localizationConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form container page element's number of steps."
	)
	public Integer getNumberOfSteps() {
		if (_numberOfStepsSupplier != null) {
			numberOfSteps = _numberOfStepsSupplier.get();

			_numberOfStepsSupplier = null;
		}

		return numberOfSteps;
	}

	public void setNumberOfSteps(Integer numberOfSteps) {
		this.numberOfSteps = numberOfSteps;

		_numberOfStepsSupplier = null;
	}

	@JsonIgnore
	public void setNumberOfSteps(
		UnsafeSupplier<Integer, Exception> numberOfStepsUnsafeSupplier) {

		_numberOfStepsSupplier = () -> {
			try {
				return numberOfStepsUnsafeSupplier.get();
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
		description = "The form container page element's number of steps."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfSteps;

	@JsonIgnore
	private Supplier<Integer> _numberOfStepsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The definition of the result when a form container submission is successful."
	)
	@Valid
	public SuccessFormContainerSubmissionResult
		getSuccessFormContainerSubmissionResult() {

		if (_successFormContainerSubmissionResultSupplier != null) {
			successFormContainerSubmissionResult =
				_successFormContainerSubmissionResultSupplier.get();

			_successFormContainerSubmissionResultSupplier = null;
		}

		return successFormContainerSubmissionResult;
	}

	public void setSuccessFormContainerSubmissionResult(
		SuccessFormContainerSubmissionResult
			successFormContainerSubmissionResult) {

		this.successFormContainerSubmissionResult =
			successFormContainerSubmissionResult;

		_successFormContainerSubmissionResultSupplier = null;
	}

	@JsonIgnore
	public void setSuccessFormContainerSubmissionResult(
		UnsafeSupplier<SuccessFormContainerSubmissionResult, Exception>
			successFormContainerSubmissionResultUnsafeSupplier) {

		_successFormContainerSubmissionResultSupplier = () -> {
			try {
				return successFormContainerSubmissionResultUnsafeSupplier.get();
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
		description = "The definition of the result when a form container submission is successful."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SuccessFormContainerSubmissionResult
		successFormContainerSubmissionResult;

	@JsonIgnore
	private Supplier<SuccessFormContainerSubmissionResult>
		_successFormContainerSubmissionResultSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormContainerConfig)) {
			return false;
		}

		FormContainerConfig formContainerConfig = (FormContainerConfig)object;

		return Objects.equals(toString(), formContainerConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FormContainerReference formContainerReference =
			getFormContainerReference();

		if (formContainerReference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formContainerReference\": ");

			sb.append(String.valueOf(formContainerReference));
		}

		FormContainerType formContainerType = getFormContainerType();

		if (formContainerType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formContainerType\": ");

			sb.append("\"");
			sb.append(formContainerType);
			sb.append("\"");
		}

		LocalizationConfig localizationConfig = getLocalizationConfig();

		if (localizationConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizationConfig\": ");

			sb.append(String.valueOf(localizationConfig));
		}

		Integer numberOfSteps = getNumberOfSteps();

		if (numberOfSteps != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfSteps\": ");

			sb.append(numberOfSteps);
		}

		SuccessFormContainerSubmissionResult
			successFormContainerSubmissionResult =
				getSuccessFormContainerSubmissionResult();

		if (successFormContainerSubmissionResult != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"successFormContainerSubmissionResult\": ");

			sb.append(String.valueOf(successFormContainerSubmissionResult));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FormContainerConfig",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("FormContainerType")
	public static enum FormContainerType {

		MULTISTEP("Multistep"), SIMPLE("Simple");

		@JsonCreator
		public static FormContainerType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (FormContainerType formContainerType : values()) {
				if (Objects.equals(formContainerType.getValue(), value)) {
					return formContainerType;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private FormContainerType(String value) {
			_value = value;
		}

		private final String _value;

	}

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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-437597493