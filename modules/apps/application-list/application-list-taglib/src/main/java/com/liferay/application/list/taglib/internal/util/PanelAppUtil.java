/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.internal.util;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class PanelAppUtil {

	public static String getLabel(
		HttpServletRequest httpServletRequest, PanelApp panelApp) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String label = HtmlUtil.escape(
			panelApp.getLabel(themeDisplay.getLocale()));

		if (Validator.isNull(label)) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				themeDisplay.getCompanyId(), panelApp.getPortletId());

			label = HtmlUtil.escape(
				PortalUtil.getPortletTitle(
					portlet, ServletContextUtil.getServletContext(),
					themeDisplay.getLocale()));
		}

		return label;
	}

	public static String getURL(
		HttpServletRequest httpServletRequest, PanelApp panelApp) {

		try {
			return String.valueOf(panelApp.getPortletURL(httpServletRequest));
		}
		catch (PortalException portalException) {
			_log.error("Unable to get portlet URL", portalException);
		}

		return null;
	}

	public static boolean isActive(
		HttpServletRequest httpServletRequest, PanelApp panelApp) {

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String parameterName =
			PortalUtil.getPortletNamespace(themeDisplay.getPpid()) +
				"portletResource";

		String portletResource = ParamUtil.getString(
			originalHttpServletRequest, parameterName);

		boolean active = Objects.equals(
			portletResource, panelApp.getPortletId());

		if (Validator.isNull(portletResource)) {
			active = Objects.equals(
				ParamUtil.getString(
					httpServletRequest, "selPpid", themeDisplay.getPpid()),
				panelApp.getPortletId());
		}

		return active;
	}

	private static final Log _log = LogFactoryUtil.getLog(PanelAppUtil.class);

}