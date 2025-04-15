/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletConfigurationLayoutUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationUtil;
import com.liferay.portlet.portletconfiguration.util.ConfigurationActionRequest;
import com.liferay.portlet.portletconfiguration.util.ConfigurationRenderRequest;
import com.liferay.portlet.portletconfiguration.util.ConfigurationResourceRequest;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.ResourceRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Daniel Kocsis
 */
public class ActionUtil {

	public static void deleteBackgroundTask(ActionRequest actionRequest)
		throws PortalException {

		long backgroundTaskId = ParamUtil.getLong(
			actionRequest, BackgroundTaskConstants.BACKGROUND_TASK_ID);

		BackgroundTaskManagerUtil.deleteBackgroundTask(backgroundTaskId);
	}

	public static Group getGroup(HttpServletRequest httpServletRequest)
		throws Exception {

		Group group = null;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

		if (groupId > 0) {
			group = GroupLocalServiceUtil.getGroup(groupId);
		}
		else if (!cmd.equals(Constants.ADD)) {
			group = themeDisplay.getSiteGroup();
		}

		httpServletRequest.setAttribute(WebKeys.GROUP, group);

		return group;
	}

	public static Group getGroup(PortletRequest portletRequest)
		throws Exception {

		return getGroup(PortalUtil.getHttpServletRequest(portletRequest));
	}

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

	public static ActionRequest getWrappedActionRequest(
			ActionRequest actionRequest, PortletPreferences portletPreferences)
		throws PortalException {

		portletPreferences = _getPortletPreferences(
			PortalUtil.getHttpServletRequest(actionRequest),
			actionRequest.getPreferences(), portletPreferences);

		return new ConfigurationActionRequest(
			actionRequest, portletPreferences);
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
			JavaConstants.JAVAX_PORTLET_REQUEST, renderRequest);

		return renderRequest;
	}

	public static ResourceRequest getWrappedResourceRequest(
			ResourceRequest resourceRequest,
			PortletPreferences portletPreferences)
		throws PortalException {

		portletPreferences = _getPortletPreferences(
			PortalUtil.getHttpServletRequest(resourceRequest),
			resourceRequest.getPreferences(), portletPreferences);

		return new ConfigurationResourceRequest(
			resourceRequest, portletPreferences);
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