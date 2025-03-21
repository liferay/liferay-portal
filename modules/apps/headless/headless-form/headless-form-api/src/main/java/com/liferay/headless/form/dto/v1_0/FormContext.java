/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.dto.v1_0;

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
@GraphQLName(
	description = "https://www.schema.org/FormContext", value = "FormContext"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FormContext")
public class FormContext implements Serializable {

	public static FormContext toDTO(String json) {
		return ObjectMapperUtil.readValue(FormContext.class, json);
	}

	public static FormContext unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FormContext.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "https://www.schema.org/FormFieldValue"
	)
	@Valid
	public FormFieldValue[] getFormFieldValues() {
		if (_formFieldValuesSupplier != null) {
			formFieldValues = _formFieldValuesSupplier.get();

			_formFieldValuesSupplier = null;
		}

		return formFieldValues;
	}

	public void setFormFieldValues(FormFieldValue[] formFieldValues) {
		this.formFieldValues = formFieldValues;

		_formFieldValuesSupplier = null;
	}

	@JsonIgnore
	public void setFormFieldValues(
		UnsafeSupplier<FormFieldValue[], Exception>
			formFieldValuesUnsafeSupplier) {

		_formFieldValuesSupplier = () -> {
			try {
				return formFieldValuesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "https://www.schema.org/FormFieldValue")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FormFieldValue[] formFieldValues;

	@JsonIgnore
	private Supplier<FormFieldValue[]> _formFieldValuesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "https://www.schema.org/FormPageContext"
	)
	@Valid
	public FormPageContext[] getFormPageContexts() {
		if (_formPageContextsSupplier != null) {
			formPageContexts = _formPageContextsSupplier.get();

			_formPageContextsSupplier = null;
		}

		return formPageContexts;
	}

	public void setFormPageContexts(FormPageContext[] formPageContexts) {
		this.formPageContexts = formPageContexts;

		_formPageContextsSupplier = null;
	}

	@JsonIgnore
	public void setFormPageContexts(
		UnsafeSupplier<FormPageContext[], Exception>
			formPageContextsUnsafeSupplier) {

		_formPageContextsSupplier = () -> {
			try {
				return formPageContextsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "https://www.schema.org/FormPageContext")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FormPageContext[] formPageContexts;

	@JsonIgnore
	private Supplier<FormPageContext[]> _formPageContextsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getReadOnly() {
		if (_readOnlySupplier != null) {
			readOnly = _readOnlySupplier.get();

			_readOnlySupplier = null;
		}

		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;

		_readOnlySupplier = null;
	}

	@JsonIgnore
	public void setReadOnly(
		UnsafeSupplier<Boolean, Exception> readOnlyUnsafeSupplier) {

		_readOnlySupplier = () -> {
			try {
				return readOnlyUnsafeSupplier.get();
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
	protected Boolean readOnly;

	@JsonIgnore
	private Supplier<Boolean> _readOnlySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getShowRequiredFieldsWarning() {
		if (_showRequiredFieldsWarningSupplier != null) {
			showRequiredFieldsWarning =
				_showRequiredFieldsWarningSupplier.get();

			_showRequiredFieldsWarningSupplier = null;
		}

		return showRequiredFieldsWarning;
	}

	public void setShowRequiredFieldsWarning(
		Boolean showRequiredFieldsWarning) {

		this.showRequiredFieldsWarning = showRequiredFieldsWarning;

		_showRequiredFieldsWarningSupplier = null;
	}

	@JsonIgnore
	public void setShowRequiredFieldsWarning(
		UnsafeSupplier<Boolean, Exception>
			showRequiredFieldsWarningUnsafeSupplier) {

		_showRequiredFieldsWarningSupplier = () -> {
			try {
				return showRequiredFieldsWarningUnsafeSupplier.get();
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
	protected Boolean showRequiredFieldsWarning;

	@JsonIgnore
	private Supplier<Boolean> _showRequiredFieldsWarningSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getShowSubmitButton() {
		if (_showSubmitButtonSupplier != null) {
			showSubmitButton = _showSubmitButtonSupplier.get();

			_showSubmitButtonSupplier = null;
		}

		return showSubmitButton;
	}

	public void setShowSubmitButton(Boolean showSubmitButton) {
		this.showSubmitButton = showSubmitButton;

		_showSubmitButtonSupplier = null;
	}

	@JsonIgnore
	public void setShowSubmitButton(
		UnsafeSupplier<Boolean, Exception> showSubmitButtonUnsafeSupplier) {

		_showSubmitButtonSupplier = () -> {
			try {
				return showSubmitButtonUnsafeSupplier.get();
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
	protected Boolean showSubmitButton;

	@JsonIgnore
	private Supplier<Boolean> _showSubmitButtonSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormContext)) {
			return false;
		}

		FormContext formContext = (FormContext)object;

		return Objects.equals(toString(), formContext.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FormFieldValue[] formFieldValues = getFormFieldValues();

		if (formFieldValues != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formFieldValues\": ");

			sb.append("[");

			for (int i = 0; i < formFieldValues.length; i++) {
				sb.append(String.valueOf(formFieldValues[i]));

				if ((i + 1) < formFieldValues.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		FormPageContext[] formPageContexts = getFormPageContexts();

		if (formPageContexts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formPageContexts\": ");

			sb.append("[");

			for (int i = 0; i < formPageContexts.length; i++) {
				sb.append(String.valueOf(formPageContexts[i]));

				if ((i + 1) < formPageContexts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean readOnly = getReadOnly();

		if (readOnly != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(readOnly);
		}

		Boolean showRequiredFieldsWarning = getShowRequiredFieldsWarning();

		if (showRequiredFieldsWarning != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showRequiredFieldsWarning\": ");

			sb.append(showRequiredFieldsWarning);
		}

		Boolean showSubmitButton = getShowSubmitButton();

		if (showSubmitButton != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showSubmitButton\": ");

			sb.append(showSubmitButton);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.form.dto.v1_0.FormContext",
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