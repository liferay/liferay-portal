/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.taglib.servlet.taglib.util;

import com.liferay.flags.configuration.FlagsGroupServiceConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class FlagsTagUtil {

	public static String getCaptchaURI(HttpServletRequest httpServletRequest) {
		LiferayPortletURL captchaResourceURL = PortletURLFactoryUtil.create(
			httpServletRequest, PortletKeys.FLAGS,
			PortletRequest.RESOURCE_PHASE);

		captchaResourceURL.setCopyCurrentRenderParameters(false);
		captchaResourceURL.setResourceID("/flags/get_captcha");

		return captchaResourceURL.toString();
	}

	public static String getCurrentURL(HttpServletRequest httpServletRequest) {
		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if ((portletRequest == null) || (portletResponse == null)) {
			return PortalUtil.getCurrentURL(httpServletRequest);
		}

		PortletURL currentURLObj = PortletURLUtil.getCurrent(
			PortalUtil.getLiferayPortletRequest(portletRequest),
			PortalUtil.getLiferayPortletResponse(portletResponse));

		return currentURLObj.toString();
	}

	public static Map<String, String> getReasons(
			long companyId, HttpServletRequest httpServletRequest)
		throws PortalException {

		Map<String, String> reasons = new HashMap<>();

		FlagsGroupServiceConfiguration flagsGroupServiceConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				FlagsGroupServiceConfiguration.class, companyId);

		for (String reason : flagsGroupServiceConfiguration.reasons()) {
			reasons.put(reason, LanguageUtil.get(httpServletRequest, reason));
		}

		return reasons;
	}

	public static String getURI(HttpServletRequest httpServletRequest) {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, PortletKeys.FLAGS,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/flags/edit_entry"
		).buildString();
	}

	public static boolean isFlagsEnabled(ThemeDisplay themeDisplay)
		throws PortalException {

		FlagsGroupServiceConfiguration flagsGroupServiceConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				FlagsGroupServiceConfiguration.class,
				themeDisplay.getCompanyId());

		if (flagsGroupServiceConfiguration.guestUsersEnabled() ||
			themeDisplay.isSignedIn()) {

			return true;
		}

		return false;
	}

}