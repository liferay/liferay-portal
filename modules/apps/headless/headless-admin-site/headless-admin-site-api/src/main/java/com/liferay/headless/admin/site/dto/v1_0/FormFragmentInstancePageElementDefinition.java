/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

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
	description = "The page element definition of a form fragment instance.",
	value = "FormFragmentInstancePageElementDefinition"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The page element definition of a form fragment instance."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FormFragmentInstancePageElementDefinition")
public class FormFragmentInstancePageElementDefinition
	extends PageElementDefinition implements Serializable {

	public static FormFragmentInstancePageElementDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(
			FormFragmentInstancePageElementDefinition.class, json);
	}

	public static FormFragmentInstancePageElementDefinition unsafeToDTO(
		String json) {

		return ObjectMapperUtil.unsafeReadValue(
			FormFragmentInstancePageElementDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The mapped field key."
	)
	public String getFieldKey() {
		if (_fieldKeySupplier != null) {
			fieldKey = _fieldKeySupplier.get();

			_fieldKeySupplier = null;
		}

		return fieldKey;
	}

	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;

		_fieldKeySupplier = null;
	}

	@JsonIgnore
	public void setFieldKey(
		UnsafeSupplier<String, Exception> fieldKeyUnsafeSupplier) {

		_fieldKeySupplier = () -> {
			try {
				return fieldKeyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The mapped field key.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fieldKey;

	@JsonIgnore
	private Supplier<String> _fieldKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment instance of the form fragment page element definition."
	)
	@Valid
	public FragmentInstance getFragmentInstance() {
		if (_fragmentInstanceSupplier != null) {
			fragmentInstance = _fragmentInstanceSupplier.get();

			_fragmentInstanceSupplier = null;
		}

		return fragmentInstance;
	}

	public void setFragmentInstance(FragmentInstance fragmentInstance) {
		this.fragmentInstance = fragmentInstance;

		_fragmentInstanceSupplier = null;
	}

	@JsonIgnore
	public void setFragmentInstance(
		UnsafeSupplier<FragmentInstance, Exception>
			fragmentInstanceUnsafeSupplier) {

		_fragmentInstanceSupplier = () -> {
			try {
				return fragmentInstanceUnsafeSupplier.get();
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
		description = "The fragment instance of the form fragment page element definition."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentInstance fragmentInstance;

	@JsonIgnore
	private Supplier<FragmentInstance> _fragmentInstanceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized help texts of the form fragment."
	)
	@Valid
	public Map<String, String> getHelpText_i18n() {
		if (_helpText_i18nSupplier != null) {
			helpText_i18n = _helpText_i18nSupplier.get();

			_helpText_i18nSupplier = null;
		}

		return helpText_i18n;
	}

	public void setHelpText_i18n(Map<String, String> helpText_i18n) {
		this.helpText_i18n = helpText_i18n;

		_helpText_i18nSupplier = null;
	}

	@JsonIgnore
	public void setHelpText_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			helpText_i18nUnsafeSupplier) {

		_helpText_i18nSupplier = () -> {
			try {
				return helpText_i18nUnsafeSupplier.get();
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
		description = "The localized help texts of the form fragment."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> helpText_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _helpText_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized labels of the form fragment."
	)
	@Valid
	public Map<String, String> getLabel_i18n() {
		if (_label_i18nSupplier != null) {
			label_i18n = _label_i18nSupplier.get();

			_label_i18nSupplier = null;
		}

		return label_i18n;
	}

	public void setLabel_i18n(Map<String, String> label_i18n) {
		this.label_i18n = label_i18n;

		_label_i18nSupplier = null;
	}

	@JsonIgnore
	public void setLabel_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			label_i18nUnsafeSupplier) {

		_label_i18nSupplier = () -> {
			try {
				return label_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized labels of the form fragment.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> label_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _label_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to mark as required."
	)
	public Boolean getMarkAsRequired() {
		if (_markAsRequiredSupplier != null) {
			markAsRequired = _markAsRequiredSupplier.get();

			_markAsRequiredSupplier = null;
		}

		return markAsRequired;
	}

	public void setMarkAsRequired(Boolean markAsRequired) {
		this.markAsRequired = markAsRequired;

		_markAsRequiredSupplier = null;
	}

	@JsonIgnore
	public void setMarkAsRequired(
		UnsafeSupplier<Boolean, Exception> markAsRequiredUnsafeSupplier) {

		_markAsRequiredSupplier = () -> {
			try {
				return markAsRequiredUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether to mark as required.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean markAsRequired;

	@JsonIgnore
	private Supplier<Boolean> _markAsRequiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to render as read-only field."
	)
	public Boolean getReadOnlyField() {
		if (_readOnlyFieldSupplier != null) {
			readOnlyField = _readOnlyFieldSupplier.get();

			_readOnlyFieldSupplier = null;
		}

		return readOnlyField;
	}

	public void setReadOnlyField(Boolean readOnlyField) {
		this.readOnlyField = readOnlyField;

		_readOnlyFieldSupplier = null;
	}

	@JsonIgnore
	public void setReadOnlyField(
		UnsafeSupplier<Boolean, Exception> readOnlyFieldUnsafeSupplier) {

		_readOnlyFieldSupplier = () -> {
			try {
				return readOnlyFieldUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether to render as read-only field.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean readOnlyField;

	@JsonIgnore
	private Supplier<Boolean> _readOnlyFieldSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to show the help text."
	)
	public Boolean getShowHelpText() {
		if (_showHelpTextSupplier != null) {
			showHelpText = _showHelpTextSupplier.get();

			_showHelpTextSupplier = null;
		}

		return showHelpText;
	}

	public void setShowHelpText(Boolean showHelpText) {
		this.showHelpText = showHelpText;

		_showHelpTextSupplier = null;
	}

	@JsonIgnore
	public void setShowHelpText(
		UnsafeSupplier<Boolean, Exception> showHelpTextUnsafeSupplier) {

		_showHelpTextSupplier = () -> {
			try {
				return showHelpTextUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether to show the help text.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean showHelpText;

	@JsonIgnore
	private Supplier<Boolean> _showHelpTextSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to show the label."
	)
	public Boolean getShowLabel() {
		if (_showLabelSupplier != null) {
			showLabel = _showLabelSupplier.get();

			_showLabelSupplier = null;
		}

		return showLabel;
	}

	public void setShowLabel(Boolean showLabel) {
		this.showLabel = showLabel;

		_showLabelSupplier = null;
	}

	@JsonIgnore
	public void setShowLabel(
		UnsafeSupplier<Boolean, Exception> showLabelUnsafeSupplier) {

		_showLabelSupplier = () -> {
			try {
				return showLabelUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether to show the label.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean showLabel;

	@JsonIgnore
	private Supplier<Boolean> _showLabelSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormFragmentInstancePageElementDefinition)) {
			return false;
		}

		FormFragmentInstancePageElementDefinition
			formFragmentInstancePageElementDefinition =
				(FormFragmentInstancePageElementDefinition)object;

		return Objects.equals(
			toString(), formFragmentInstancePageElementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String fieldKey = getFieldKey();

		if (fieldKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldKey\": ");

			sb.append("\"");

			sb.append(_escape(fieldKey));

			sb.append("\"");
		}

		FragmentInstance fragmentInstance = getFragmentInstance();

		if (fragmentInstance != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentInstance\": ");

			sb.append(String.valueOf(fragmentInstance));
		}

		Map<String, String> helpText_i18n = getHelpText_i18n();

		if (helpText_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"helpText_i18n\": ");

			sb.append(_toJSON(helpText_i18n));
		}

		Map<String, String> label_i18n = getLabel_i18n();

		if (label_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(_toJSON(label_i18n));
		}

		Boolean markAsRequired = getMarkAsRequired();

		if (markAsRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"markAsRequired\": ");

			sb.append(markAsRequired);
		}

		Boolean readOnlyField = getReadOnlyField();

		if (readOnlyField != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnlyField\": ");

			sb.append(readOnlyField);
		}

		Boolean showHelpText = getShowHelpText();

		if (showHelpText != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showHelpText\": ");

			sb.append(showHelpText);
		}

		Boolean showLabel = getShowLabel();

		if (showLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showLabel\": ");

			sb.append(showLabel);
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(type);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FormFragmentInstancePageElementDefinition",
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
// LIFERAY-REST-BUILDER-HASH:476102532