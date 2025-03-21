/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ModelHintsConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Julio Camarero
 */
public class InputLocalizedTag extends IncludeTag {

	public List<String> getActiveLanguageIds() {
		return _activeLanguageIds;
	}

	public Set<Locale> getAvailableLocales() {
		return _availableLocales;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public String getDisplayWidth() {
		return _displayWidth;
	}

	public String getEditorName() {
		return _editorName;
	}

	public String getFieldPrefix() {
		return _fieldPrefix;
	}

	public String getFieldPrefixSeparator() {
		return _fieldPrefixSeparator;
	}

	public String getFormName() {
		return _formName;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public String getId() {
		return _id;
	}

	public String getInputAddon() {
		return _inputAddon;
	}

	public String getLanguageId() {
		return _languageId;
	}

	public String getLanguagesDropdownDirection() {
		return _languagesDropdownDirection;
	}

	public String getMaxLength() {
		return _maxLength;
	}

	public String getName() {
		return _name;
	}

	public String getPlaceholder() {
		return _placeholder;
	}

	public String getSelectedLanguageId() {
		return _selectedLanguageId;
	}

	public String getToolbarSet() {
		return _toolbarSet;
	}

	public String getType() {
		return _type;
	}

	public String getXml() {
		return _xml;
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

	public boolean isLanguagesDropdownVisible() {
		return _languagesDropdownVisible;
	}

	public void setActiveLanguageIds(List<String> activeLanguageIds) {
		_activeLanguageIds = activeLanguageIds;
	}

	public void setAdminMode(boolean adminMode) {
		_adminMode = adminMode;
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setAutoSize(boolean autoSize) {
		_autoSize = autoSize;
	}

	public void setAvailableLocales(Set<Locale> availableLocales) {
		_availableLocales = availableLocales;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setDisplayWidth(String displayWidth) {
		_displayWidth = displayWidth;
	}

	public void setEditorName(String editorName) {
		_editorName = editorName;
	}

	public void setFieldPrefix(String fieldPrefix) {
		_fieldPrefix = fieldPrefix;
	}

	public void setFieldPrefixSeparator(String fieldPrefixSeparator) {
		_fieldPrefixSeparator = fieldPrefixSeparator;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	public void setInputAddon(String inputAddon) {
		_inputAddon = inputAddon;
	}

	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	public void setLanguagesDropdownDirection(
		String languagesDropdownDirection) {

		_languagesDropdownDirection = languagesDropdownDirection;
	}

	public void setLanguagesDropdownVisible(boolean languagesDropdownVisible) {
		_languagesDropdownVisible = languagesDropdownVisible;
	}

	public void setMaxLength(String maxLength) {
		_maxLength = maxLength;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPlaceholder(String placeholder) {
		_placeholder = placeholder;
	}

	public void setSelectedLanguageId(String selectedLanguageId) {
		_selectedLanguageId = selectedLanguageId;
	}

	public void setToolbarSet(String toolbarSet) {
		_toolbarSet = toolbarSet;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setXml(String xml) {
		_xml = xml;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_activeLanguageIds = new ArrayList<>();
		_adminMode = false;
		_autoFocus = false;
		_autoSize = false;
		_availableLocales = null;
		_cssClass = null;
		_defaultLanguageId = null;
		_disabled = false;
		_displayWidth = ModelHintsConstants.TEXT_DISPLAY_WIDTH;
		_editorName = _EDITOR_WYSIWYG_DEFAULT;
		_fieldPrefix = null;
		_fieldPrefixSeparator = null;
		_formName = null;
		_helpMessage = null;
		_id = null;
		_ignoreRequestValue = false;
		_inputAddon = null;
		_languageId = null;
		_languagesDropdownDirection = null;
		_languagesDropdownVisible = true;
		_maxLength = null;
		_name = null;
		_placeholder = null;
		_selectedLanguageId = null;
		_toolbarSet = "simple";
		_type = "input";
		_xml = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		Set<Locale> availableLocales = _availableLocales;

		if (availableLocales == null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			availableLocales = LanguageUtil.getAvailableLocales(
				themeDisplay.getSiteGroupId());
		}

		String formName = _formName;

		if (Validator.isNull(formName)) {
			formName = "fm";
		}

		String id = _id;

		if (Validator.isNull(id)) {
			id = _name;
		}

		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:activeLanguageIds", _activeLanguageIds);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:adminMode", String.valueOf(_adminMode));
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:autoFocus", String.valueOf(_autoFocus));
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:autoSize", String.valueOf(_autoSize));
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:availableLocales", availableLocales);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:cssClass", _cssClass);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:defaultLanguageId", _defaultLanguageId);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:disabled", String.valueOf(_disabled));
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:displayWidth", _displayWidth);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:dynamicAttributes",
			getDynamicAttributes());
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:editorName", _editorName);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:fieldPrefix", _fieldPrefix);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:fieldPrefixSeparator",
			_fieldPrefixSeparator);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:formName", formName);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:helpMessage", _helpMessage);
		httpServletRequest.setAttribute("liferay-ui:input-localized:id", id);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:ignoreRequestValue",
			String.valueOf(_ignoreRequestValue));
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:inputAddon", _inputAddon);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:languageId", _languageId);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:languagesDropdownDirection",
			_languagesDropdownDirection);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:languagesDropdownVisible",
			String.valueOf(_languagesDropdownVisible));
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:maxLength", _maxLength);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:name", _name);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:placeholder", _placeholder);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:selectedLanguageId",
			_selectedLanguageId);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:toolbarSet", _toolbarSet);
		httpServletRequest.setAttribute(
			"liferay-ui:input-localized:type", _type);
		httpServletRequest.setAttribute("liferay-ui:input-localized:xml", _xml);
	}

	private static final String _EDITOR_WYSIWYG_DEFAULT = PropsUtil.get(
		PropsKeys.EDITOR_WYSIWYG_DEFAULT);

	private static final String _PAGE =
		"/html/taglib/ui/input_localized/page.jsp";

	private List<String> _activeLanguageIds = new ArrayList<>();
	private boolean _adminMode;
	private boolean _autoFocus;
	private boolean _autoSize;
	private Set<Locale> _availableLocales;
	private String _cssClass;
	private String _defaultLanguageId;
	private boolean _disabled;
	private String _displayWidth = ModelHintsConstants.TEXT_DISPLAY_WIDTH;
	private String _editorName = _EDITOR_WYSIWYG_DEFAULT;
	private String _fieldPrefix;
	private String _fieldPrefixSeparator;
	private String _formName;
	private String _helpMessage;
	private String _id;
	private boolean _ignoreRequestValue;
	private String _inputAddon;
	private String _languageId;
	private String _languagesDropdownDirection;
	private boolean _languagesDropdownVisible = true;
	private String _maxLength;
	private String _name;
	private String _placeholder;
	private String _selectedLanguageId;
	private String _toolbarSet = "simple";
	private String _type = "input";
	private String _xml;

}