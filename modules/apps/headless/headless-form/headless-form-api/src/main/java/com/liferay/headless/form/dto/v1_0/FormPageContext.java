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

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "https://www.schema.org/FormPageContext",
	value = "FormPageContext"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FormPageContext")
public class FormPageContext implements Serializable {

	public static FormPageContext toDTO(String json) {
		return ObjectMapperUtil.readValue(FormPageContext.class, json);
	}

	public static FormPageContext unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FormPageContext.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnabled() {
		if (_enabledSupplier != null) {
			enabled = _enabledSupplier.get();

			_enabledSupplier = null;
		}

		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;

		_enabledSupplier = null;
	}

	@JsonIgnore
	public void setEnabled(
		UnsafeSupplier<Boolean, Exception> enabledUnsafeSupplier) {

		_enabledSupplier = () -> {
			try {
				return enabledUnsafeSupplier.get();
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
	protected Boolean enabled;

	@JsonIgnore
	private Supplier<Boolean> _enabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "https://www.schema.org/FormFieldContext"
	)
	@Valid
	public FormFieldContext[] getFormFieldContexts() {
		if (_formFieldContextsSupplier != null) {
			formFieldContexts = _formFieldContextsSupplier.get();

			_formFieldContextsSupplier = null;
		}

		return formFieldContexts;
	}

	public void setFormFieldContexts(FormFieldContext[] formFieldContexts) {
		this.formFieldContexts = formFieldContexts;

		_formFieldContextsSupplier = null;
	}

	@JsonIgnore
	public void setFormFieldContexts(
		UnsafeSupplier<FormFieldContext[], Exception>
			formFieldContextsUnsafeSupplier) {

		_formFieldContextsSupplier = () -> {
			try {
				return formFieldContextsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "https://www.schema.org/FormFieldContext")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FormFieldContext[] formFieldContexts;

	@JsonIgnore
	private Supplier<FormFieldContext[]> _formFieldContextsSupplier;

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

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormPageContext)) {
			return false;
		}

		FormPageContext formPageContext = (FormPageContext)object;

		return Objects.equals(toString(), formPageContext.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean enabled = getEnabled();

		if (enabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(enabled);
		}

		FormFieldContext[] formFieldContexts = getFormFieldContexts();

		if (formFieldContexts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formFieldContexts\": ");

			sb.append("[");

			for (int i = 0; i < formFieldContexts.length; i++) {
				sb.append(String.valueOf(formFieldContexts[i]));

				if ((i + 1) < formFieldContexts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean showRequiredFieldsWarning = getShowRequiredFieldsWarning();

		if (showRequiredFieldsWarning != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showRequiredFieldsWarning\": ");

			sb.append(showRequiredFieldsWarning);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.form.dto.v1_0.FormPageContext",
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