/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.taglib.servlet.taglib;

import com.liferay.frontend.editor.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin Tan
 */
public class InputLocalizedTag extends IncludeTag {

	public List<String> getActiveLanguageIds() {
		return _activeLanguageIds;
	}

	public Set<Locale> getAvailableLocales() {
		return _availableLocales;
	}

	public String getComponentId() {
		return _componentId;
	}

	public String getConfigKey() {
		return _configKey;
	}

	public String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public String getEditorName() {
		return _editorName;
	}

	public String getName() {
		return _name;
	}

	public String getOnBlurMethod() {
		return _onBlurMethod;
	}

	public String getOnChangeMethod() {
		return _onChangeMethod;
	}

	public String getOnFocusMethod() {
		return _onFocusMethod;
	}

	public String getSelectedLanguageId() {
		return _selectedLanguageId;
	}

	public String getXml() {
		return _xml;
	}

	public boolean isAdminMode() {
		return _adminMode;
	}

	public boolean isAutofillFromDefault() {
		return _autofillFromDefault;
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

	public void setAutofillFromDefault(boolean autofillFromDefault) {
		_autofillFromDefault = autofillFromDefault;
	}

	public void setAvailableLocales(Set<Locale> availableLocales) {
		_availableLocales = availableLocales;
	}

	public void setComponentId(String componentId) {
		_componentId = componentId;
	}

	public void setConfigKey(String configKey) {
		_configKey = configKey;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setEditorName(String editorName) {
		_editorName = editorName;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	public void setLanguagesDropdownVisible(boolean languagesDropdownVisible) {
		_languagesDropdownVisible = languagesDropdownVisible;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOnBlurMethod(String onBlurMethod) {
		_onBlurMethod = onBlurMethod;
	}

	public void setOnChangeMethod(String onChangeMethod) {
		_onChangeMethod = onChangeMethod;
	}

	public void setOnFocusMethod(String onFocusMethod) {
		_onFocusMethod = onFocusMethod;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setSelectedLanguageId(String selectedLanguageId) {
		_selectedLanguageId = selectedLanguageId;
	}

	public void setXml(String xml) {
		_xml = xml;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_activeLanguageIds = new ArrayList<>();
		_adminMode = false;
		_autofillFromDefault = false;
		_availableLocales = null;
		_componentId = null;
		_configKey = null;
		_defaultLanguageId = null;
		_editorName = "ckeditor5_classic";
		_ignoreRequestValue = false;
		_languagesDropdownVisible = true;
		_name = null;
		_onBlurMethod = null;
		_onChangeMethod = null;
		_onFocusMethod = null;
		_selectedLanguageId = null;
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
			HttpServletRequest parentHttpServletRequest = getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)parentHttpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			availableLocales = LanguageUtil.getAvailableLocales(
				themeDisplay.getSiteGroupId());
		}

		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:activeLanguageIds",
			_activeLanguageIds);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:adminMode", _adminMode);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:autofillFromDefault",
			_autofillFromDefault);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:availableLocales",
			_getAvailableLocalesJSONArray(availableLocales));
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:componentId", _componentId);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:editorConfigurationData",
			_getCKEditor5ClassicEditorConfigurationData(httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:languagesDropdownVisible",
			_languagesDropdownVisible);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:name", _name);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:onBlurMethod", _onBlurMethod);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:onChangeMethod", _onChangeMethod);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:onFocusMethod", _onFocusMethod);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:selectedLanguageId",
			_selectedLanguageId);
		httpServletRequest.setAttribute(
			"liferay-editor:input-localized:translations",
			_getTranslationsJSONObject(availableLocales, httpServletRequest));
	}

	private JSONArray _getAvailableLocalesJSONArray(
		Set<Locale> availableLocales) {

		JSONArray availableLocalesJSONArray = JSONFactoryUtil.createJSONArray();

		HttpServletRequest parentHttpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)parentHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (Locale locale : availableLocales) {
			String w3cLanguageId = LocaleUtil.toW3cLanguageId(locale);

			JSONObject localeJSONObject = JSONUtil.put(
				"displayName", locale.getDisplayName(themeDisplay.getLocale())
			).put(
				"id", LocaleUtil.toLanguageId(locale)
			).put(
				"label", w3cLanguageId
			).put(
				"symbol", StringUtil.toLowerCase(w3cLanguageId)
			);

			if (availableLocales.contains(locale)) {
				availableLocalesJSONArray.put(localeJSONObject);
			}
		}

		return availableLocalesJSONArray;
	}

	private Map<String, Object> _getCKEditor5ClassicEditorConfigurationData(
		HttpServletRequest httpServletRequest) {

		String portletId = GetterUtil.getString(
			(String)httpServletRequest.getAttribute(WebKeys.PORTLET_ID));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				PortletIdCodec.decodePortletName(portletId), _getConfigKey(),
				_editorName, new HashMap<String, Object>(), themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(
					themeDisplay.getRequest()));

		return editorConfiguration.getData();
	}

	private String _getConfigKey() {
		String configKey = _configKey;

		if (Validator.isNull(configKey)) {
			configKey = _name + "Editor";
		}

		return configKey;
	}

	private JSONObject _getTranslationsJSONObject(
		Set<Locale> availableLocales, HttpServletRequest httpServletRequest) {

		Map<String, String> translationsMap = new HashMap<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String namespace = portletDisplay.getNamespace();

		for (Locale availableLocale : availableLocales) {
			String languageId = LocaleUtil.toLanguageId(availableLocale);

			String languageValue = LocalizationUtil.getLocalization(
				_xml, languageId, false);

			if (!_ignoreRequestValue) {
				String parameterName = StringBundler.concat(
					namespace, _name, StringPool.UNDERLINE, languageId);

				languageValue = ParamUtil.getString(
					httpServletRequest, parameterName, languageValue);
			}

			if (Validator.isNotNull(languageValue) ||
				languageId.equals(_defaultLanguageId)) {

				translationsMap.put(
					languageId, GetterUtil.getString(languageValue));
			}
		}

		return JSONFactoryUtil.createJSONObject(translationsMap);
	}

	private static final String _PAGE = "/input_localized/page.jsp";

	private List<String> _activeLanguageIds = new ArrayList<>();
	private boolean _adminMode;
	private boolean _autofillFromDefault;
	private Set<Locale> _availableLocales;
	private String _componentId;
	private String _configKey;
	private String _defaultLanguageId;
	private String _editorName = "ckeditor5_classic";
	private boolean _ignoreRequestValue;
	private boolean _languagesDropdownVisible = true;
	private String _name;
	private String _onBlurMethod;
	private String _onChangeMethod;
	private String _onFocusMethod;
	private String _selectedLanguageId;
	private String _xml;

}