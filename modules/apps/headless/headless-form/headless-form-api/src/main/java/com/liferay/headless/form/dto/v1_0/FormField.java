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
	description = "https://www.schema.org/FormField", value = "FormField"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FormField")
public class FormField implements Serializable {

	public static FormField toDTO(String json) {
		return ObjectMapperUtil.readValue(FormField.class, json);
	}

	public static FormField unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FormField.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAutocomplete() {
		if (_autocompleteSupplier != null) {
			autocomplete = _autocompleteSupplier.get();

			_autocompleteSupplier = null;
		}

		return autocomplete;
	}

	public void setAutocomplete(Boolean autocomplete) {
		this.autocomplete = autocomplete;

		_autocompleteSupplier = null;
	}

	@JsonIgnore
	public void setAutocomplete(
		UnsafeSupplier<Boolean, Exception> autocompleteUnsafeSupplier) {

		_autocompleteSupplier = () -> {
			try {
				return autocompleteUnsafeSupplier.get();
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
	protected Boolean autocomplete;

	@JsonIgnore
	private Supplier<Boolean> _autocompleteSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDataSourceType() {
		if (_dataSourceTypeSupplier != null) {
			dataSourceType = _dataSourceTypeSupplier.get();

			_dataSourceTypeSupplier = null;
		}

		return dataSourceType;
	}

	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;

		_dataSourceTypeSupplier = null;
	}

	@JsonIgnore
	public void setDataSourceType(
		UnsafeSupplier<String, Exception> dataSourceTypeUnsafeSupplier) {

		_dataSourceTypeSupplier = () -> {
			try {
				return dataSourceTypeUnsafeSupplier.get();
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
	protected String dataSourceType;

	@JsonIgnore
	private Supplier<String> _dataSourceTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String dataType;

	@JsonIgnore
	private Supplier<String> _dataTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDisplayStyle() {
		if (_displayStyleSupplier != null) {
			displayStyle = _displayStyleSupplier.get();

			_displayStyleSupplier = null;
		}

		return displayStyle;
	}

	public void setDisplayStyle(String displayStyle) {
		this.displayStyle = displayStyle;

		_displayStyleSupplier = null;
	}

	@JsonIgnore
	public void setDisplayStyle(
		UnsafeSupplier<String, Exception> displayStyleUnsafeSupplier) {

		_displayStyleSupplier = () -> {
			try {
				return displayStyleUnsafeSupplier.get();
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
	protected String displayStyle;

	@JsonIgnore
	private Supplier<String> _displayStyleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public FormFieldOption[] getFormFieldOptions() {
		if (_formFieldOptionsSupplier != null) {
			formFieldOptions = _formFieldOptionsSupplier.get();

			_formFieldOptionsSupplier = null;
		}

		return formFieldOptions;
	}

	public void setFormFieldOptions(FormFieldOption[] formFieldOptions) {
		this.formFieldOptions = formFieldOptions;

		_formFieldOptionsSupplier = null;
	}

	@JsonIgnore
	public void setFormFieldOptions(
		UnsafeSupplier<FormFieldOption[], Exception>
			formFieldOptionsUnsafeSupplier) {

		_formFieldOptionsSupplier = () -> {
			try {
				return formFieldOptionsUnsafeSupplier.get();
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
	protected FormFieldOption[] formFieldOptions;

	@JsonIgnore
	private Supplier<FormFieldOption[]> _formFieldOptionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Grid getGrid() {
		if (_gridSupplier != null) {
			grid = _gridSupplier.get();

			_gridSupplier = null;
		}

		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;

		_gridSupplier = null;
	}

	@JsonIgnore
	public void setGrid(UnsafeSupplier<Grid, Exception> gridUnsafeSupplier) {
		_gridSupplier = () -> {
			try {
				return gridUnsafeSupplier.get();
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
	protected Grid grid;

	@JsonIgnore
	private Supplier<Grid> _gridSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getHasFormRules() {
		if (_hasFormRulesSupplier != null) {
			hasFormRules = _hasFormRulesSupplier.get();

			_hasFormRulesSupplier = null;
		}

		return hasFormRules;
	}

	public void setHasFormRules(Boolean hasFormRules) {
		this.hasFormRules = hasFormRules;

		_hasFormRulesSupplier = null;
	}

	@JsonIgnore
	public void setHasFormRules(
		UnsafeSupplier<Boolean, Exception> hasFormRulesUnsafeSupplier) {

		_hasFormRulesSupplier = () -> {
			try {
				return hasFormRulesUnsafeSupplier.get();
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
	protected Boolean hasFormRules;

	@JsonIgnore
	private Supplier<Boolean> _hasFormRulesSupplier;

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
	public Boolean getImmutable() {
		if (_immutableSupplier != null) {
			immutable = _immutableSupplier.get();

			_immutableSupplier = null;
		}

		return immutable;
	}

	public void setImmutable(Boolean immutable) {
		this.immutable = immutable;

		_immutableSupplier = null;
	}

	@JsonIgnore
	public void setImmutable(
		UnsafeSupplier<Boolean, Exception> immutableUnsafeSupplier) {

		_immutableSupplier = () -> {
			try {
				return immutableUnsafeSupplier.get();
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
	protected Boolean immutable;

	@JsonIgnore
	private Supplier<Boolean> _immutableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getInline() {
		if (_inlineSupplier != null) {
			inline = _inlineSupplier.get();

			_inlineSupplier = null;
		}

		return inline;
	}

	public void setInline(Boolean inline) {
		this.inline = inline;

		_inlineSupplier = null;
	}

	@JsonIgnore
	public void setInline(
		UnsafeSupplier<Boolean, Exception> inlineUnsafeSupplier) {

		_inlineSupplier = () -> {
			try {
				return inlineUnsafeSupplier.get();
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
	protected Boolean inline;

	@JsonIgnore
	private Supplier<Boolean> _inlineSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String inputControl;

	@JsonIgnore
	private Supplier<String> _inputControlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String label;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> label_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _label_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean localizable;

	@JsonIgnore
	private Supplier<Boolean> _localizableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean multiple;

	@JsonIgnore
	private Supplier<Boolean> _multipleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPlaceholder() {
		if (_placeholderSupplier != null) {
			placeholder = _placeholderSupplier.get();

			_placeholderSupplier = null;
		}

		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;

		_placeholderSupplier = null;
	}

	@JsonIgnore
	public void setPlaceholder(
		UnsafeSupplier<String, Exception> placeholderUnsafeSupplier) {

		_placeholderSupplier = () -> {
			try {
				return placeholderUnsafeSupplier.get();
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
	protected String placeholder;

	@JsonIgnore
	private Supplier<String> _placeholderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String predefinedValue;

	@JsonIgnore
	private Supplier<String> _predefinedValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> predefinedValue_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _predefinedValue_i18nSupplier;

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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean repeatable;

	@JsonIgnore
	private Supplier<Boolean> _repeatableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean required;

	@JsonIgnore
	private Supplier<Boolean> _requiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getShowAsSwitcher() {
		if (_showAsSwitcherSupplier != null) {
			showAsSwitcher = _showAsSwitcherSupplier.get();

			_showAsSwitcherSupplier = null;
		}

		return showAsSwitcher;
	}

	public void setShowAsSwitcher(Boolean showAsSwitcher) {
		this.showAsSwitcher = showAsSwitcher;

		_showAsSwitcherSupplier = null;
	}

	@JsonIgnore
	public void setShowAsSwitcher(
		UnsafeSupplier<Boolean, Exception> showAsSwitcherUnsafeSupplier) {

		_showAsSwitcherSupplier = () -> {
			try {
				return showAsSwitcherUnsafeSupplier.get();
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
	protected Boolean showAsSwitcher;

	@JsonIgnore
	private Supplier<Boolean> _showAsSwitcherSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean showLabel;

	@JsonIgnore
	private Supplier<Boolean> _showLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getStyle() {
		if (_styleSupplier != null) {
			style = _styleSupplier.get();

			_styleSupplier = null;
		}

		return style;
	}

	public void setStyle(String style) {
		this.style = style;

		_styleSupplier = null;
	}

	@JsonIgnore
	public void setStyle(
		UnsafeSupplier<String, Exception> styleUnsafeSupplier) {

		_styleSupplier = () -> {
			try {
				return styleUnsafeSupplier.get();
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
	protected String style;

	@JsonIgnore
	private Supplier<String> _styleSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTooltip() {
		if (_tooltipSupplier != null) {
			tooltip = _tooltipSupplier.get();

			_tooltipSupplier = null;
		}

		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;

		_tooltipSupplier = null;
	}

	@JsonIgnore
	public void setTooltip(
		UnsafeSupplier<String, Exception> tooltipUnsafeSupplier) {

		_tooltipSupplier = () -> {
			try {
				return tooltipUnsafeSupplier.get();
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
	protected String tooltip;

	@JsonIgnore
	private Supplier<String> _tooltipSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "https://www.schema.org/FormFieldValidation"
	)
	@Valid
	public Validation getValidation() {
		if (_validationSupplier != null) {
			validation = _validationSupplier.get();

			_validationSupplier = null;
		}

		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;

		_validationSupplier = null;
	}

	@JsonIgnore
	public void setValidation(
		UnsafeSupplier<Validation, Exception> validationUnsafeSupplier) {

		_validationSupplier = () -> {
			try {
				return validationUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "https://www.schema.org/FormFieldValidation")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Validation validation;

	@JsonIgnore
	private Supplier<Validation> _validationSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormField)) {
			return false;
		}

		FormField formField = (FormField)object;

		return Objects.equals(toString(), formField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean autocomplete = getAutocomplete();

		if (autocomplete != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"autocomplete\": ");

			sb.append(autocomplete);
		}

		String dataSourceType = getDataSourceType();

		if (dataSourceType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataSourceType\": ");

			sb.append("\"");

			sb.append(_escape(dataSourceType));

			sb.append("\"");
		}

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

		String displayStyle = getDisplayStyle();

		if (displayStyle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayStyle\": ");

			sb.append("\"");

			sb.append(_escape(displayStyle));

			sb.append("\"");
		}

		FormFieldOption[] formFieldOptions = getFormFieldOptions();

		if (formFieldOptions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formFieldOptions\": ");

			sb.append("[");

			for (int i = 0; i < formFieldOptions.length; i++) {
				sb.append(String.valueOf(formFieldOptions[i]));

				if ((i + 1) < formFieldOptions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Grid grid = getGrid();

		if (grid != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"grid\": ");

			sb.append(String.valueOf(grid));
		}

		Boolean hasFormRules = getHasFormRules();

		if (hasFormRules != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hasFormRules\": ");

			sb.append(hasFormRules);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		Boolean immutable = getImmutable();

		if (immutable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"immutable\": ");

			sb.append(immutable);
		}

		Boolean inline = getInline();

		if (inline != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inline\": ");

			sb.append(inline);
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

		String placeholder = getPlaceholder();

		if (placeholder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placeholder\": ");

			sb.append("\"");

			sb.append(_escape(placeholder));

			sb.append("\"");
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

		Boolean readOnly = getReadOnly();

		if (readOnly != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(readOnly);
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

		Boolean showAsSwitcher = getShowAsSwitcher();

		if (showAsSwitcher != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showAsSwitcher\": ");

			sb.append(showAsSwitcher);
		}

		Boolean showLabel = getShowLabel();

		if (showLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showLabel\": ");

			sb.append(showLabel);
		}

		String style = getStyle();

		if (style != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"style\": ");

			sb.append("\"");

			sb.append(_escape(style));

			sb.append("\"");
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

		String tooltip = getTooltip();

		if (tooltip != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tooltip\": ");

			sb.append("\"");

			sb.append(_escape(tooltip));

			sb.append("\"");
		}

		Validation validation = getValidation();

		if (validation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"validation\": ");

			sb.append(String.valueOf(validation));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.form.dto.v1_0.FormField",
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