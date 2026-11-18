/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.util;

import com.liferay.multi.factor.authentication.web.internal.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.web.internal.constants.MFAWebKeys;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.Key;

import java.util.Map;

/**
 * @author Pedro Victor Silvestre
 */
public class MFABrowserVerificationUtil {

	public static String getVerificationRedirectURL(
			HttpServletRequest httpServletRequest,
			Map<String, String[]> parameterMap, String redirect, long userId,
			WindowState windowState)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long plid = (themeDisplay == null) ? 0 : themeDisplay.getPlid();

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			httpServletRequest, MFAPortletKeys.MFA_VERIFY, plid,
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"mvcRenderCommandName", "/mfa_verify/view");
		liferayPortletURL.setParameter("redirect", redirect);
		liferayPortletURL.setParameter(
			"saveLastPath", Boolean.FALSE.toString());

		Key key = EncryptorUtil.generateKey();

		String encryptedState = EncryptorUtil.encrypt(
			key,
			JSONFactoryUtil.looseSerializeDeep(
				HashMapBuilder.<String, Object>put(
					"requestParameters",
					() -> HashMapBuilder.putAll(
						parameterMap
					).remove(
						"redirect"
					).build()
				).build()));

		liferayPortletURL.setParameter("state", encryptedState);

		liferayPortletURL.setWindowState(windowState);

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute(MFAWebKeys.MFA_USER_ID, userId);
		httpSession.setAttribute(
			MFAWebKeys.MFA_WEB_DIGEST, DigesterUtil.digest(encryptedState));
		httpSession.setAttribute(
			MFAWebKeys.MFA_WEB_KEY, EncryptorUtil.serializeKey(key));

		return liferayPortletURL.toString();
	}

}