/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.util;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.message.boards.settings.MBGroupServiceSettings;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Dante Wang
 */
public class MBRequestUtil {

	public static CaptchaConfiguration getCaptchaConfiguration(
			HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		CaptchaConfiguration captchaConfiguration =
			(CaptchaConfiguration)httpServletRequest.getAttribute(
				_MB_CAPTCHA_CONFIGURATION);

		if (captchaConfiguration != null) {
			return captchaConfiguration;
		}

		captchaConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				CaptchaConfiguration.class,
				PortalUtil.getCompanyId(httpServletRequest));

		httpServletRequest.setAttribute(
			_MB_CAPTCHA_CONFIGURATION, captchaConfiguration);

		return captchaConfiguration;
	}

	public static MBGroupServiceSettings getMBGroupServiceSettings(
			HttpServletRequest httpServletRequest, long groupId)
		throws PortalException {

		MBGroupServiceSettings mbGroupServiceSettings =
			(MBGroupServiceSettings)httpServletRequest.getAttribute(
				_MB_GROUP_SERVICE_SETTINGS);

		if (mbGroupServiceSettings != null) {
			return mbGroupServiceSettings;
		}

		mbGroupServiceSettings = MBGroupServiceSettings.getInstance(groupId);

		httpServletRequest.setAttribute(
			_MB_GROUP_SERVICE_SETTINGS, mbGroupServiceSettings);

		return mbGroupServiceSettings;
	}

	private static final String _MB_CAPTCHA_CONFIGURATION =
		"MB_CAPTCHA_CONFIGURATION";

	private static final String _MB_GROUP_SERVICE_SETTINGS =
		"MB_GROUP_SERVICE_SETTINGS";

}