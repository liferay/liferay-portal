/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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
	description = "Represents each field in a content structure, backed by a content field.",
	value = "ContentStructureField"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContentStructureField")
public class ContentStructureField implements Serializable {

	public static ContentStructureField toDTO(String json) {
		return ObjectMapperUtil.readValue(ContentStructureField.class, json);
	}

	public static ContentStructureField unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ContentStructureField.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form field's type (e.g., date, geolocation, text, etc.)."
	)
	public String getDataType() {
		if (_dataTypeSupplier != null) {
			dataType = _dataTypeSupplier.get();

			_dataTypeSupplier = null;
		}

		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;

		_dataTypeSupplier = null;
	}

	@JsonIgnore
	public void setDataType(
		UnsafeSupplier<String, Exception> dataTypeUnsafeSupplier) {

		_dataTypeSupplier = () -> {
			try {
				return dataTypeUnsafeSupplier.get();
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
		description = "The form field's type (e.g., date, geolocation, text, etc.)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String dataType;

	@JsonIgnore
	private Supplier<String> _dataTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form field's input control type (e.g., text, textarea, select field, etc.)."
	)
	public String getInputControl() {
		if (_inputControlSupplier != null) {
			inputControl = _inputControlSupplier.get();

			_inputControlSupplier = null;
		}

		return inputControl;
	}

	public void setInputControl(String inputControl) {
		this.inputControl = inputControl;

		_inputControlSupplier = null;
	}

	@JsonIgnore
	public void setInputControl(
		UnsafeSupplier<String, Exception> inputControlUnsafeSupplier) {

		_inputControlSupplier = () -> {
			try {
				return inputControlUnsafeSupplier.get();
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
		description = "The form field's input control type (e.g., text, textarea, select field, etc.)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String inputControl;

	@JsonIgnore
	private Supplier<String> _inputControlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form field's label."
	)
	public String getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(String label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The form field's label.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String label;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form field's labels."
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

	@GraphQLField(description = "The form field's labels.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> label_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _label_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the content is accessible in different languages."
	)
	public Boolean getLocalizable() {
		if (_localizableSupplier != null) {
			localizable = _localizableSupplier.get();

			_localizableSupplier = null;
		}

		return localizable;
	}

	public void setLocalizable(Boolean localizable) {
		this.localizable = localizable;

		_localizableSupplier = null;
	}

	@JsonIgnore
	public void setLocalizable(
		UnsafeSupplier<Boolean, Exception> localizableUnsafeSupplier) {

		_localizableSupplier = () -> {
			try {
				return localizableUnsafeSupplier.get();
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
		description = "A flag that indicates whether the content is accessible in different languages."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean localizable;

	@JsonIgnore
	private Supplier<Boolean> _localizableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the form field can have several values."
	)
	public Boolean getMultiple() {
		if (_multipleSupplier != null) {
			multiple = _multipleSupplier.get();

			_multipleSupplier = null;
		}

		return multiple;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;

		_multipleSupplier = null;
	}

	@JsonIgnore
	public void setMultiple(
		UnsafeSupplier<Boolean, Exception> multipleUnsafeSupplier) {

		_multipleSupplier = () -> {
			try {
				return multipleUnsafeSupplier.get();
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
		description = "A flag that indicates whether the form field can have several values."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean multiple;

	@JsonIgnore
	private Supplier<Boolean> _multipleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form field's name."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The form field's name.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The child content structure fields that depend on this form field."
	)
	@Valid
	public ContentStructureField[] getNestedContentStructureFields() {
		if (_nestedContentStructureFieldsSupplier != null) {
			nestedContentStructureFields =
				_nestedContentStructureFieldsSupplier.get();

			_nestedContentStructureFieldsSupplier = null;
		}

		return nestedContentStructureFields;
	}

	public void setNestedContentStructureFields(
		ContentStructureField[] nestedContentStructureFields) {

		this.nestedContentStructureFields = nestedContentStructureFields;

		_nestedContentStructureFieldsSupplier = null;
	}

	@JsonIgnore
	public void setNestedContentStructureFields(
		UnsafeSupplier<ContentStructureField[], Exception>
			nestedContentStructureFieldsUnsafeSupplier) {

		_nestedContentStructureFieldsSupplier = () -> {
			try {
				return nestedContentStructureFieldsUnsafeSupplier.get();
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
		description = "The child content structure fields that depend on this form field."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected ContentStructureField[] nestedContentStructureFields;

	@JsonIgnore
	private Supplier<ContentStructureField[]>
		_nestedContentStructureFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The list of different possible values."
	)
	@Valid
	public Option[] getOptions() {
		if (_optionsSupplier != null) {
			options = _optionsSupplier.get();

			_optionsSupplier = null;
		}

		return options;
	}

	public void setOptions(Option[] options) {
		this.options = options;

		_optionsSupplier = null;
	}

	@JsonIgnore
	public void setOptions(
		UnsafeSupplier<Option[], Exception> optionsUnsafeSupplier) {

		_optionsSupplier = () -> {
			try {
				return optionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The list of different possible values.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Option[] options;

	@JsonIgnore
	private Supplier<Option[]> _optionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The form field's default value."
	)
	public String getPredefinedValue() {
		if (_predefinedValueSupplier != null) {
			predefinedValue = _predefinedValueSupplier.get();

			_predefinedValueSupplier = null;
		}

		return predefinedValue;
	}

	public void setPredefinedValue(String predefinedValue) {
		this.predefinedValue = predefinedValue;

		_predefinedValueSupplier = null;
	}

	@JsonIgnore
	public void setPredefinedValue(
		UnsafeSupplier<String, Exception> predefinedValueUnsafeSupplier) {

		_predefinedValueSupplier = () -> {
			try {
				return predefinedValueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The form field's default value.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String predefinedValue;

	@JsonIgnore
	private Supplier<String> _predefinedValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized form field's default values."
	)
	@Valid
	public Map<String, String> getPredefinedValue_i18n() {
		if (_predefinedValue_i18nSupplier != null) {
			predefinedValue_i18n = _predefinedValue_i18nSupplier.get();

			_predefinedValue_i18nSupplier = null;
		}

		return predefinedValue_i18n;
	}

	public void setPredefinedValue_i18n(
		Map<String, String> predefinedValue_i18n) {

		this.predefinedValue_i18n = predefinedValue_i18n;

		_predefinedValue_i18nSupplier = null;
	}

	@JsonIgnore
	public void setPredefinedValue_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			predefinedValue_i18nUnsafeSupplier) {

		_predefinedValue_i18nSupplier = () -> {
			try {
				return predefinedValue_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized form field's default values.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> predefinedValue_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _predefinedValue_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether this content can be rendered (and answered) several times."
	)
	public Boolean getRepeatable() {
		if (_repeatableSupplier != null) {
			repeatable = _repeatableSupplier.get();

			_repeatableSupplier = null;
		}

		return repeatable;
	}

	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;

		_repeatableSupplier = null;
	}

	@JsonIgnore
	public void setRepeatable(
		UnsafeSupplier<Boolean, Exception> repeatableUnsafeSupplier) {

		_repeatableSupplier = () -> {
			try {
				return repeatableUnsafeSupplier.get();
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
		description = "A flag that indicates whether this content can be rendered (and answered) several times."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean repeatable;

	@JsonIgnore
	private Supplier<Boolean> _repeatableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether this form field is required."
	)
	public Boolean getRequired() {
		if (_requiredSupplier != null) {
			required = _requiredSupplier.get();

			_requiredSupplier = null;
		}

		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;

		_requiredSupplier = null;
	}

	@JsonIgnore
	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		_requiredSupplier = () -> {
			try {
				return requiredUnsafeSupplier.get();
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
		description = "A flag that indicates whether this form field is required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean required;

	@JsonIgnore
	private Supplier<Boolean> _requiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the structure's end target should render the field label."
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

	@GraphQLField(
		description = "A flag that indicates whether the structure's end target should render the field label."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean showLabel;

	@JsonIgnore
	private Supplier<Boolean> _showLabelSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentStructureField)) {
			return false;
		}

		ContentStructureField contentStructureField =
			(ContentStructureField)object;

		return Objects.equals(toString(), contentStructureField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String dataType = getDataType();

		if (dataType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataType\": ");

			sb.append("\"");

			sb.append(_escape(dataType));

			sb.append("\"");
		}

		String inputControl = getInputControl();

		if (inputControl != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inputControl\": ");

			sb.append("\"");

			sb.append(_escape(inputControl));

			sb.append("\"");
		}

		String label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(label));

			sb.append("\"");
		}

		Map<String, String> label_i18n = getLabel_i18n();

		if (label_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(_toJSON(label_i18n));
		}

		Boolean localizable = getLocalizable();

		if (localizable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizable\": ");

			sb.append(localizable);
		}

		Boolean multiple = getMultiple();

		if (multiple != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multiple\": ");

			sb.append(multiple);
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		ContentStructureField[] nestedContentStructureFields =
			getNestedContentStructureFields();

		if (nestedContentStructureFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedContentStructureFields\": ");

			sb.append("[");

			for (int i = 0; i < nestedContentStructureFields.length; i++) {
				sb.append(String.valueOf(nestedContentStructureFields[i]));

				if ((i + 1) < nestedContentStructureFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Option[] options = getOptions();

		if (options != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("[");

			for (int i = 0; i < options.length; i++) {
				sb.append(String.valueOf(options[i]));

				if ((i + 1) < options.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String predefinedValue = getPredefinedValue();

		if (predefinedValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"predefinedValue\": ");

			sb.append("\"");

			sb.append(_escape(predefinedValue));

			sb.append("\"");
		}

		Map<String, String> predefinedValue_i18n = getPredefinedValue_i18n();

		if (predefinedValue_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"predefinedValue_i18n\": ");

			sb.append(_toJSON(predefinedValue_i18n));
		}

		Boolean repeatable = getRepeatable();

		if (repeatable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repeatable\": ");

			sb.append(repeatable);
		}

		Boolean required = getRequired();

		if (required != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(required);
		}

		Boolean showLabel = getShowLabel();

		if (showLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showLabel\": ");

			sb.append(showLabel);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.ContentStructureField",
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