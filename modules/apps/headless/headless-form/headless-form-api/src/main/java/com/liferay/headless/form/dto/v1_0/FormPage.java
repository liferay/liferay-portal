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
	description = "https://www.schema.org/FormPage", value = "FormPage"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FormPage")
public class FormPage implements Serializable {

	public static FormPage toDTO(String json) {
		return ObjectMapperUtil.readValue(FormPage.class, json);
	}

	public static FormPage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FormPage.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public FormField[] getFormFields() {
		if (_formFieldsSupplier != null) {
			formFields = _formFieldsSupplier.get();

			_formFieldsSupplier = null;
		}

		return formFields;
	}

	public void setFormFields(FormField[] formFields) {
		this.formFields = formFields;

		_formFieldsSupplier = null;
	}

	@JsonIgnore
	public void setFormFields(
		UnsafeSupplier<FormField[], Exception> formFieldsUnsafeSupplier) {

		_formFieldsSupplier = () -> {
			try {
				return formFieldsUnsafeSupplier.get();
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
	protected FormField[] formFields;

	@JsonIgnore
	private Supplier<FormField[]> _formFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getHeadline() {
		if (_headlineSupplier != null) {
			headline = _headlineSupplier.get();

			_headlineSupplier = null;
		}

		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;

		_headlineSupplier = null;
	}

	@JsonIgnore
	public void setHeadline(
		UnsafeSupplier<String, Exception> headlineUnsafeSupplier) {

		_headlineSupplier = () -> {
			try {
				return headlineUnsafeSupplier.get();
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
	protected String headline;

	@JsonIgnore
	private Supplier<String> _headlineSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getHeadline_i18n() {
		if (_headline_i18nSupplier != null) {
			headline_i18n = _headline_i18nSupplier.get();

			_headline_i18nSupplier = null;
		}

		return headline_i18n;
	}

	public void setHeadline_i18n(Map<String, String> headline_i18n) {
		this.headline_i18n = headline_i18n;

		_headline_i18nSupplier = null;
	}

	@JsonIgnore
	public void setHeadline_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			headline_i18nUnsafeSupplier) {

		_headline_i18nSupplier = () -> {
			try {
				return headline_i18nUnsafeSupplier.get();
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
	protected Map<String, String> headline_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _headline_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getText() {
		if (_textSupplier != null) {
			text = _textSupplier.get();

			_textSupplier = null;
		}

		return text;
	}

	public void setText(String text) {
		this.text = text;

		_textSupplier = null;
	}

	@JsonIgnore
	public void setText(UnsafeSupplier<String, Exception> textUnsafeSupplier) {
		_textSupplier = () -> {
			try {
				return textUnsafeSupplier.get();
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
	protected String text;

	@JsonIgnore
	private Supplier<String> _textSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, String> getText_i18n() {
		if (_text_i18nSupplier != null) {
			text_i18n = _text_i18nSupplier.get();

			_text_i18nSupplier = null;
		}

		return text_i18n;
	}

	public void setText_i18n(Map<String, String> text_i18n) {
		this.text_i18n = text_i18n;

		_text_i18nSupplier = null;
	}

	@JsonIgnore
	public void setText_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			text_i18nUnsafeSupplier) {

		_text_i18nSupplier = () -> {
			try {
				return text_i18nUnsafeSupplier.get();
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
	protected Map<String, String> text_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _text_i18nSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormPage)) {
			return false;
		}

		FormPage formPage = (FormPage)object;

		return Objects.equals(toString(), formPage.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FormField[] formFields = getFormFields();

		if (formFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formFields\": ");

			sb.append("[");

			for (int i = 0; i < formFields.length; i++) {
				sb.append(String.valueOf(formFields[i]));

				if ((i + 1) < formFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String headline = getHeadline();

		if (headline != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"headline\": ");

			sb.append("\"");

			sb.append(_escape(headline));

			sb.append("\"");
		}

		Map<String, String> headline_i18n = getHeadline_i18n();

		if (headline_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"headline_i18n\": ");

			sb.append(_toJSON(headline_i18n));
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String text = getText();

		if (text != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"text\": ");

			sb.append("\"");

			sb.append(_escape(text));

			sb.append("\"");
		}

		Map<String, String> text_i18n = getText_i18n();

		if (text_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"text_i18n\": ");

			sb.append(_toJSON(text_i18n));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.form.dto.v1_0.FormPage",
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