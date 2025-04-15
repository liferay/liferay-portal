/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.configuration.icon;

import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Eudaldo Alonso
 */
public abstract class BasePortletConfigurationIcon
	implements PortletConfigurationIcon {

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getAlt() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getAriaRole() {
		return null;
	}

	@Override
	public String getCssClass() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public Map<String, Object> getData() {
		return null;
	}

	@Override
	public String getIconCssClass() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getId() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getImage() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getImageHover() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getLang() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getLinkCssClass() {
		return null;
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return null;
	}

	@Override
	public String getMethod() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getOnClick(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public ResourceBundle getResourceBundle(Locale locale) {
		return PortalUtil.getResourceBundle(locale);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getSrc() {
		return null;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public String getSrcHover() {
		return null;
	}

	@Override
	public String getTarget() {
		return null;
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return null;
	}

	@Override
	public double getWeight() {
		return 0;
	}

	/**
	 * @throws IOException
	 */
	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		return false;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isLabel() {
		return false;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isToolTip() {
		return false;
	}

	@Override
	public boolean isUseDialog() {
		return false;
	}

	protected Locale getLocale(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.getLocale();
	}

	protected String getNamespace(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getNamespace();
	}

}