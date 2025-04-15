/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.icon.minimize.internal;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = PortletConfigurationIcon.class)
public class MinimizePortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "minimize"
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getJspPath() {
		return "/configuration/icon/minimize.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		String key = "minimize";

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (portletDisplay.isStateMin()) {
			key = "restore";
		}

		return _language.get(getLocale(portletRequest), key);
	}

	@Override
	public double getWeight() {
		return 6.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!GetterUtil.getBoolean(
				themeDisplay.getThemeSetting(
					"show-maximize-minimize-application-links"))) {

			return false;
		}

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypePortlet()) {
			return false;
		}

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		LayoutTypeController layoutTypeController =
			layoutTypePortlet.getLayoutTypeController();

		if (layoutTypeController.isFullPageDisplayable()) {
			return false;
		}

		Portlet portlet = (Portlet)portletRequest.getAttribute(
			WebKeys.RENDER_PORTLET);

		if (!portlet.hasWindowState(
				portletRequest.getResponseContentType(),
				WindowState.MINIMIZED)) {

			return false;
		}

		Group group = themeDisplay.getScopeGroup();

		if ((!themeDisplay.isSignedIn() ||
			 (group.hasStagingGroup() && !group.isStagingGroup()) ||
			 !_hasUpdateLayoutPermission(themeDisplay)) &&
			!PropsValues.LAYOUT_GUEST_SHOW_MIN_ICON) {

			return false;
		}

		return true;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private boolean _hasUpdateLayoutPermission(ThemeDisplay themeDisplay) {
		try {
			return LayoutPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
				ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MinimizePortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portlet.configuration.icon.minimize)"
	)
	private ServletContext _servletContext;

}