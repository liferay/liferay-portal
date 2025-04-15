/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

import java.text.Format;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class InputFieldTag extends IncludeTag {

	public List<String> getActiveLanguageIds() {
		return _activeLanguageIds;
	}

	public String getAutoComplete() {
		return _autoComplete;
	}

	public Object getBean() {
		return _bean;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getDateTogglerCheckboxLabel() {
		return _dateTogglerCheckboxLabel;
	}

	public String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public Object getDefaultValue() {
		return _defaultValue;
	}

	public String getField() {
		return _field;
	}

	public String getFieldParam() {
		return _fieldParam;
	}

	public Format getFormat() {
		return _format;
	}

	public String getFormName() {
		return _formName;
	}

	public String getId() {
		return _id;
	}

	public String getLanguageId() {
		return _languageId;
	}

	public String getLanguagesDropdownDirection() {
		return _languagesDropdownDirection;
	}

	public Class<?> getModel() {
		return _model;
	}

	public String getPlaceholder() {
		return _placeholder;
	}

	public boolean isAdminMode() {
		return _adminMode;
	}

	public boolean isAutoFocus() {
		return _autoFocus;
	}

	public boolean isAutoSize() {
		return _autoSize;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isIgnoreRequestValue() {
		return _ignoreRequestValue;
	}

	public void setActiveLanguageIds(List<String> activeLanguageIds) {
		_activeLanguageIds = activeLanguageIds;
	}

	public void setAdminMode(boolean adminMode) {
		_adminMode = adminMode;
	}

	public void setAutoComplete(String autoComplete) {
		_autoComplete = autoComplete;
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setAutoSize(boolean autoSize) {
		_autoSize = autoSize;
	}

	public void setBean(Object bean) {
		_bean = bean;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDateTogglerCheckboxLabel(String dateTogglerCheckboxLabel) {
		_dateTogglerCheckboxLabel = dateTogglerCheckboxLabel;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setDefaultValue(Object defaultValue) {
		_defaultValue = defaultValue;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setField(String field) {
		_field = field;
	}

	public void setFieldParam(String fieldParam) {
		_fieldParam = fieldParam;
	}

	public void setFormat(Format format) {
		_format = format;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	public void setLanguagesDropdownDirection(
		String languagesDropdownDirection) {

		_languagesDropdownDirection = languagesDropdownDirection;
	}

	public void setModel(Class<?> model) {
		_model = model;
	}

	public void setPlaceholder(String placeholder) {
		_placeholder = placeholder;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_activeLanguageIds = new ArrayList<>();
		_adminMode = false;
		_autoComplete = null;
		_autoFocus = false;
		_autoSize = false;
		_bean = null;
		_cssClass = null;
		_dateTogglerCheckboxLabel = null;
		_defaultLanguageId = null;
		_defaultValue = null;
		_disabled = false;
		_field = null;
		_fieldParam = null;
		_format = null;
		_formName = "fm";
		_id = null;
		_ignoreRequestValue = false;
		_languageId = null;
		_languagesDropdownDirection = null;
		_model = null;
		_placeholder = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		String fieldParam = _fieldParam;

		if (Validator.isNull(fieldParam)) {
			fieldParam = _field;
		}

		String id = _id;

		if (Validator.isNull(id)) {
			id = fieldParam;
		}

		httpServletRequest.setAttribute(
			"liferay-ui:input-field:activeLanguageIds", _activeLanguageIds);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:adminMode", _adminMode);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:autoComplete", _autoComplete);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:autoFocus", String.valueOf(_autoFocus));
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:autoSize", String.valueOf(_autoSize));
		httpServletRequest.setAttribute("liferay-ui:input-field:bean", _bean);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:dateTogglerCheckboxLabel",
			_dateTogglerCheckboxLabel);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:defaultLanguageId", _defaultLanguageId);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:defaultValue", _defaultValue);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:disabled", String.valueOf(_disabled));
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:dynamicAttributes", getDynamicAttributes());
		httpServletRequest.setAttribute("liferay-ui:input-field:field", _field);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:fieldParam", fieldParam);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:format", _format);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:formName", _formName);
		httpServletRequest.setAttribute("liferay-ui:input-field:id", id);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:ignoreRequestValue",
			String.valueOf(_ignoreRequestValue));
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:languageId", _languageId);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:languagesDropdownDirection",
			_languagesDropdownDirection);
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:model", _model.getName());
		httpServletRequest.setAttribute(
			"liferay-ui:input-field:placeholder", _placeholder);
	}

	private static final String _PAGE = "/html/taglib/ui/input_field/page.jsp";

	private List<String> _activeLanguageIds = new ArrayList<>();
	private boolean _adminMode;
	private String _autoComplete;
	private boolean _autoFocus;
	private boolean _autoSize;
	private Object _bean;
	private String _cssClass;
	private String _dateTogglerCheckboxLabel;
	private String _defaultLanguageId;
	private Object _defaultValue;
	private boolean _disabled;
	private String _field;
	private String _fieldParam;
	private Format _format;
	private String _formName = "fm";
	private String _id;
	private boolean _ignoreRequestValue;
	private String _languageId;
	private String _languagesDropdownDirection;
	private Class<?> _model;
	private String _placeholder;

}