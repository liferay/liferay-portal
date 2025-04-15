/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.css.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletDecorator;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.PortletSetupUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class PortletConfigurationCSSPortletDisplayContext {

	public PortletConfigurationCSSPortletDisplayContext(
			RenderRequest renderRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletResource = ParamUtil.getString(
			renderRequest, "portletResource");

		PortletPreferences portletPreferences =
			themeDisplay.getStrictLayoutPortletSetup(
				themeDisplay.getLayout(), portletResource);

		JSONObject portletSetupJSONObject = PortletSetupUtil.cssToJSONObject(
			portletPreferences);

		_renderRequest = renderRequest;

		_portletResource = portletResource;
		_portletPreferences = portletPreferences;
		_portletSetupJSONObject = portletSetupJSONObject;
	}

	public String getBackgroundColor() {
		JSONObject bgDataJSONObject = _portletSetupJSONObject.getJSONObject(
			"bgData");

		if (bgDataJSONObject == null) {
			return StringPool.BLANK;
		}

		return bgDataJSONObject.getString("backgroundColor");
	}

	public String getBorderProperty(String position, String property) {
		JSONObject borderDataJSONObject = _portletSetupJSONObject.getJSONObject(
			"borderData");

		if (borderDataJSONObject == null) {
			return StringPool.BLANK;
		}

		JSONObject borderPropertyJSONObject =
			borderDataJSONObject.getJSONObject(property);

		return borderPropertyJSONObject.getString(position);
	}

	public String getBorderWidthProperty(String position, String property) {
		JSONObject borderDataJSONObject = _portletSetupJSONObject.getJSONObject(
			"borderData");

		if (borderDataJSONObject == null) {
			return StringPool.BLANK;
		}

		JSONObject borderWidthJSONObject = borderDataJSONObject.getJSONObject(
			"borderWidth");

		JSONObject borderWidthPositionJSONObject =
			borderWidthJSONObject.getJSONObject(position);

		return borderWidthPositionJSONObject.getString(property);
	}

	public String getCustomCSS() {
		JSONObject advancedDataJSONObject =
			_portletSetupJSONObject.getJSONObject("advancedData");

		if (advancedDataJSONObject == null) {
			return StringPool.BLANK;
		}

		return advancedDataJSONObject.getString("customCSS");
	}

	public String getCustomCSSClassName() {
		JSONObject advancedDataJSONObject =
			_portletSetupJSONObject.getJSONObject("advancedData");

		if (advancedDataJSONObject == null) {
			return StringPool.BLANK;
		}

		return advancedDataJSONObject.getString("customCSSClassName");
	}

	public String getCustomTitleXML() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(_renderRequest);

		HttpSession httpSession = httpServletRequest.getSession();

		ServletContext servletContext = httpSession.getServletContext();

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			_portletResource);

		Map<Locale, String> customTitleMap = new HashMap<>();

		for (Locale curLocale :
				LanguageUtil.getAvailableLocales(
					themeDisplay.getSiteGroupId())) {

			String languageId = LocaleUtil.toLanguageId(curLocale);

			String portletSetupTitle = _portletPreferences.getValue(
				"portletSetupTitle_" + languageId,
				PortalUtil.getPortletTitle(portlet, servletContext, curLocale));

			customTitleMap.put(curLocale, portletSetupTitle);
		}

		return LocalizationUtil.updateLocalization(
			customTitleMap, StringPool.BLANK, "customTitle",
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
	}

	public DecimalFormat getDecimalFormat() {
		if (_decimalFormat != null) {
			return _decimalFormat;
		}

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();

		decimalFormatSymbols.setDecimalSeparator('.');

		_decimalFormat = new DecimalFormat("#.##em", decimalFormatSymbols);

		return _decimalFormat;
	}

	public String getMarginProperty(String position, String property) {
		JSONObject spacingDataJSONObject =
			_portletSetupJSONObject.getJSONObject("spacingData");

		if (spacingDataJSONObject == null) {
			return StringPool.BLANK;
		}

		JSONObject marginJSONObject = spacingDataJSONObject.getJSONObject(
			"margin");

		JSONObject marginPositionJSONObject = marginJSONObject.getJSONObject(
			position);

		return marginPositionJSONObject.getString(property);
	}

	public String getPaddingProperty(String position, String property) {
		JSONObject spacingDataJSONObject =
			_portletSetupJSONObject.getJSONObject("spacingData");

		if (spacingDataJSONObject == null) {
			return StringPool.BLANK;
		}

		JSONObject paddingJSONObject = spacingDataJSONObject.getJSONObject(
			"padding");

		JSONObject paddingPositionJSONObject = paddingJSONObject.getJSONObject(
			position);

		return paddingPositionJSONObject.getString(property);
	}

	public String getPortletDecoratorId() {
		if (_portletDecoratorId != null) {
			return _portletDecoratorId;
		}

		_portletDecoratorId = _portletPreferences.getValue(
			"portletSetupPortletDecoratorId", _getDefaultDecoratorId());

		return _portletDecoratorId;
	}

	public String getPortletResource() {
		return _portletResource;
	}

	public String getTextDataProperty(String property) {
		JSONObject textDataJSONObject = _portletSetupJSONObject.getJSONObject(
			"textData");

		if (textDataJSONObject == null) {
			return StringPool.BLANK;
		}

		return textDataJSONObject.getString(property);
	}

	public boolean hasAccess() throws PortalException {
		if (Validator.isNull(getPortletResource())) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
			getPortletResource(), ActionKeys.CONFIGURATION);
	}

	public boolean isBorderSameForAll(String property) {
		JSONObject borderDataJSONObject = _portletSetupJSONObject.getJSONObject(
			"borderData");

		if (borderDataJSONObject == null) {
			return false;
		}

		JSONObject borderPropertyJSONObject =
			borderDataJSONObject.getJSONObject(property);

		return borderPropertyJSONObject.getBoolean("sameForAll");
	}

	public boolean isSpacingSameForAll(String property) {
		JSONObject spacingDataJSONObject =
			_portletSetupJSONObject.getJSONObject("spacingData");

		if (spacingDataJSONObject == null) {
			return false;
		}

		JSONObject spacingPropertyJSONObject =
			spacingDataJSONObject.getJSONObject(property);

		return spacingPropertyJSONObject.getBoolean("sameForAll");
	}

	public boolean isUseCustomTitle() {
		if (_useCustomTitle != null) {
			return _useCustomTitle;
		}

		_useCustomTitle = GetterUtil.getBoolean(
			_portletPreferences.getValue(
				"portletSetupUseCustomTitle", StringPool.BLANK));

		return _useCustomTitle;
	}

	private String _getDefaultDecoratorId() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Theme theme = themeDisplay.getTheme();

		List<PortletDecorator> filteredPortletDecorators = ListUtil.filter(
			theme.getPortletDecorators(),
			PortletDecorator::isDefaultPortletDecorator);

		if (ListUtil.isEmpty(filteredPortletDecorators)) {
			return StringPool.BLANK;
		}

		PortletDecorator defaultPortletDecorator =
			filteredPortletDecorators.get(0);

		return defaultPortletDecorator.getPortletDecoratorId();
	}

	private DecimalFormat _decimalFormat;
	private String _portletDecoratorId;
	private final PortletPreferences _portletPreferences;
	private final String _portletResource;
	private final JSONObject _portletSetupJSONObject;
	private final RenderRequest _renderRequest;
	private Boolean _useCustomTitle;

}