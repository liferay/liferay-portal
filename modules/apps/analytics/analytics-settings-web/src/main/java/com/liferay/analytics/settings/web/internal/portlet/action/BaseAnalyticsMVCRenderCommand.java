/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.portlet.action;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.web.internal.constants.AnalyticsSettingsWebKeys;
import com.liferay.analytics.settings.web.internal.user.AnalyticsUsersManager;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Francisco Dias
 */
public abstract class BaseAnalyticsMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(getJspPath());

		try {
			_setHttpServletRequestAttributes(
				PortalUtil.getHttpServletRequest(renderRequest));

			requestDispatcher.include(
				PortalUtil.getHttpServletRequest(renderRequest),
				PortalUtil.getHttpServletResponse(renderResponse));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to include JSP " + getJspPath(), exception);
			}

			throw new PortletException(
				"Unable to include JSP " + getJspPath(), exception);
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	protected abstract String getJspPath();

	@Reference
	protected AnalyticsUsersManager analyticsUsersManager;

	@Reference
	protected ConfigurationProvider configurationProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.analytics.settings.web)"
	)
	protected ServletContext servletContext;

	private void _setHttpServletRequestAttributes(
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		httpServletRequest.setAttribute(
			AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION,
			configurationProvider.getCompanyConfiguration(
				AnalyticsConfiguration.class, themeDisplay.getCompanyId()));

		httpServletRequest.setAttribute(
			AnalyticsSettingsWebKeys.ANALYTICS_USERS_MANAGER,
			analyticsUsersManager);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAnalyticsMVCRenderCommand.class);

}