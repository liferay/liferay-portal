/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
	description = "The form page element definition form's configuration.",
	value = "FormConfig"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FormConfig")
public class FormConfig implements Serializable {

	public static FormConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(FormConfig.class, json);
	}

	public static FormConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FormConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form reference."
	)
	@Valid
	public Object getFormReference() {
		if (_formReferenceSupplier != null) {
			formReference = _formReferenceSupplier.get();

			_formReferenceSupplier = null;
		}

		return formReference;
	}

	public void setFormReference(Object formReference) {
		this.formReference = formReference;

		_formReferenceSupplier = null;
	}

	@JsonIgnore
	public void setFormReference(
		UnsafeSupplier<Object, Exception> formReferenceUnsafeSupplier) {

		_formReferenceSupplier = () -> {
			try {
				return formReferenceUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The form reference.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object formReference;

	@JsonIgnore
	private Supplier<Object> _formReferenceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("formType")
	@Valid
	public FormType getFormType() {
		if (_formTypeSupplier != null) {
			formType = _formTypeSupplier.get();

			_formTypeSupplier = null;
		}

		return formType;
	}

	@JsonIgnore
	public String getFormTypeAsString() {
		FormType formType = getFormType();

		if (formType == null) {
			return null;
		}

		return formType.toString();
	}

	public void setFormType(FormType formType) {
		this.formType = formType;

		_formTypeSupplier = null;
	}

	@JsonIgnore
	public void setFormType(
		UnsafeSupplier<FormType, Exception> formTypeUnsafeSupplier) {

		_formTypeSupplier = () -> {
			try {
				return formTypeUnsafeSupplier.get();
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
	protected FormType formType;

	@JsonIgnore
	private Supplier<FormType> _formTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form page element's number of steps."
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

	@GraphQLField(description = "The form page element's number of steps.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer numberOfSteps;

	@JsonIgnore
	private Supplier<Integer> _numberOfStepsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The definition of the result when a form submission is successful."
	)
	@Valid
	public Object getSuccessFormSubmissionResult() {
		if (_successFormSubmissionResultSupplier != null) {
			successFormSubmissionResult =
				_successFormSubmissionResultSupplier.get();

			_successFormSubmissionResultSupplier = null;
		}

		return successFormSubmissionResult;
	}

	public void setSuccessFormSubmissionResult(
		Object successFormSubmissionResult) {

		this.successFormSubmissionResult = successFormSubmissionResult;

		_successFormSubmissionResultSupplier = null;
	}

	@JsonIgnore
	public void setSuccessFormSubmissionResult(
		UnsafeSupplier<Object, Exception>
			successFormSubmissionResultUnsafeSupplier) {

		_successFormSubmissionResultSupplier = () -> {
			try {
				return successFormSubmissionResultUnsafeSupplier.get();
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
		description = "The definition of the result when a form submission is successful."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object successFormSubmissionResult;

	@JsonIgnore
	private Supplier<Object> _successFormSubmissionResultSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormConfig)) {
			return false;
		}

		FormConfig formConfig = (FormConfig)object;

		return Objects.equals(toString(), formConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object formReference = getFormReference();

		if (formReference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formReference\": ");

			if (formReference instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)formReference));
			}
			else if (formReference instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)formReference));
				sb.append("\"");
			}
			else {
				sb.append(formReference);
			}
		}

		FormType formType = getFormType();

		if (formType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formType\": ");

			sb.append("\"");
			sb.append(formType);
			sb.append("\"");
		}

		Integer numberOfSteps = getNumberOfSteps();

		if (numberOfSteps != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfSteps\": ");

			sb.append(numberOfSteps);
		}

		Object successFormSubmissionResult = getSuccessFormSubmissionResult();

		if (successFormSubmissionResult != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"successFormSubmissionResult\": ");

			if (successFormSubmissionResult instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)successFormSubmissionResult));
			}
			else if (successFormSubmissionResult instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)successFormSubmissionResult));
				sb.append("\"");
			}
			else {
				sb.append(successFormSubmissionResult);
			}
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FormConfig",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("FormType")
	public static enum FormType {

		MULTISTEP("Multistep"), SIMPLE("Simple");

		@JsonCreator
		public static FormType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (FormType formType : values()) {
				if (Objects.equals(formType.getValue(), value)) {
					return formType;
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

		private FormType(String value) {
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}