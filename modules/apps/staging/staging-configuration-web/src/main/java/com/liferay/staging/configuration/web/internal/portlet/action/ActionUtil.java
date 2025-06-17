/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.configuration.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletConfigurationLayoutUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationUtil;
import com.liferay.portlet.portletconfiguration.util.ConfigurationRenderRequest;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Levente Hudák
 */
public class ActionUtil {

	public static PortletPreferences getLayoutPortletSetup(
		PortletRequest portletRequest, Portlet portlet) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return PortletPreferencesFactoryUtil.getLayoutPortletSetup(
			themeDisplay.getLayout(), portlet.getPortletId());
	}

	public static Portlet getPortlet(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		String portletId = ParamUtil.getString(
			portletRequest, "portletResource");

		if (!PortletPermissionUtil.contains(
				permissionChecker, themeDisplay.getScopeGroupId(),
				PortletConfigurationLayoutUtil.getLayout(themeDisplay),
				portletId, ActionKeys.CONFIGURATION)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Portlet.class.getName(), portletId,
				ActionKeys.CONFIGURATION);
		}

		return PortletLocalServiceUtil.getPortletById(
			themeDisplay.getCompanyId(), portletId);
	}

	public static String getTitle(Portlet portlet, RenderRequest renderRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);

		PortletPreferences portletPreferences = getLayoutPortletSetup(
			renderRequest, portlet);

		String title = PortletConfigurationUtil.getPortletTitle(
			portlet.getPortletId(),
			_getPortletSetup(
				httpServletRequest, renderRequest.getPreferences(),
				portletPreferences),
			themeDisplay.getLanguageId());

		if (Validator.isNull(title)) {
			ServletContext servletContext =
				(ServletContext)renderRequest.getAttribute(WebKeys.CTX);

			title = PortalUtil.getPortletTitle(
				portlet, servletContext, themeDisplay.getLocale());
		}

		return title;
	}

	public static RenderRequest getWrappedRenderRequest(
			RenderRequest renderRequest, PortletPreferences portletPreferences)
		throws PortalException {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);

		portletPreferences = _getPortletPreferences(
			httpServletRequest, renderRequest.getPreferences(),
			portletPreferences);

		renderRequest = new ConfigurationRenderRequest(
			renderRequest, portletPreferences);

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST, renderRequest);

		return renderRequest;
	}

	private static PortletPreferences _getPortletPreferences(
			HttpServletRequest httpServletRequest,
			PortletPreferences portletConfigPortletPreferences,
			PortletPreferences portletPreferences)
		throws PortalException {

		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		if (Validator.isNull(portletResource)) {
			return portletConfigPortletPreferences;
		}

		if (portletPreferences != null) {
			return portletPreferences;
		}

		return PortletPreferencesFactoryUtil.getPortletPreferences(
			httpServletRequest, portletResource);
	}

	private static PortletPreferences _getPortletSetup(
			HttpServletRequest httpServletRequest,
			PortletPreferences portletConfigPortletPreferences,
			PortletPreferences portletPreferences)
		throws Exception {

		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		if (Validator.isNull(portletResource)) {
			return portletConfigPortletPreferences;
		}

		if (portletPreferences != null) {
			return portletPreferences;
		}

		return PortletPreferencesFactoryUtil.getPortletSetup(
			httpServletRequest, portletResource);
	}

}