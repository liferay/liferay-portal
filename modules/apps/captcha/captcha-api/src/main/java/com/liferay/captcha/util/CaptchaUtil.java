/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.util;

import com.liferay.captcha.provider.CaptchaProvider;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 * @author Pei-Jung Lan
 */
public class CaptchaUtil {

	public static void check(HttpServletRequest httpServletRequest)
		throws CaptchaException {

		getCaptcha().check(httpServletRequest);
	}

	public static void check(PortletRequest portletRequest)
		throws CaptchaException {

		getCaptcha().check(portletRequest);
	}

	public static void enforceCaptcha(HttpServletRequest httpServletRequest) {
		getCaptcha().enforceCaptcha(httpServletRequest);
	}

	public static void enforceCaptcha(PortletRequest portletRequest) {
		getCaptcha().enforceCaptcha(portletRequest);
	}

	public static Captcha getCaptcha() {
		CaptchaProvider captchaProvider = _captchaProviderSnapshot.get();

		return captchaProvider.getCaptcha();
	}

	public static String getTaglibPath() {
		return getCaptcha().getTaglibPath();
	}

	public static boolean isEnabled(HttpServletRequest httpServletRequest) {
		return getCaptcha().isEnabled(httpServletRequest);
	}

	public static boolean isEnabled(PortletRequest portletRequest) {
		return getCaptcha().isEnabled(portletRequest);
	}

	public static void serveImage(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		getCaptcha().serveImage(httpServletRequest, httpServletResponse);
	}

	private static final Snapshot<CaptchaProvider> _captchaProviderSnapshot =
		new Snapshot<>(CaptchaUtil.class, CaptchaProvider.class);

}