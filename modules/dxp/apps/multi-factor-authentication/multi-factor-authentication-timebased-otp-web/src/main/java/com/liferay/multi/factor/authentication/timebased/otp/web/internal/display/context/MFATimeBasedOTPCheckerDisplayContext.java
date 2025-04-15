/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.web.internal.display.context;

import com.liferay.multi.factor.authentication.timebased.otp.web.internal.constants.MFATimeBasedOTPWebKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Christian Moura
 */
public class MFATimeBasedOTPCheckerDisplayContext {

	public MFATimeBasedOTPCheckerDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_portletDisplay = themeDisplay.getPortletDisplay();
	}

	public Map<String, Object> getContext() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"account",
			() -> {
				User selectedUser = PortalUtil.getSelectedUser(
					_httpServletRequest);

				return selectedUser.getEmailAddress();
			}
		).put(
			"algorithm",
			HtmlUtil.escapeJS(
				GetterUtil.getString(
					_httpServletRequest.getAttribute(
						MFATimeBasedOTPWebKeys.MFA_TIME_BASED_OTP_ALGORITHM)))
		).put(
			"containerId", _portletDisplay.getNamespace() + "qrcode"
		).put(
			"counter",
			GetterUtil.getInteger(
				_httpServletRequest.getAttribute(
					MFATimeBasedOTPWebKeys.MFA_TIME_BASED_OTP_TIME_COUNTER))
		).put(
			"digits",
			GetterUtil.getInteger(
				_httpServletRequest.getAttribute(
					MFATimeBasedOTPWebKeys.MFA_TIME_BASED_OTP_DIGITS))
		).put(
			"issuer",
			StringUtil.replace(
				HtmlUtil.escapeJS(
					GetterUtil.getString(
						_httpServletRequest.getAttribute(
							MFATimeBasedOTPWebKeys.
								MFA_TIME_BASED_OTP_COMPANY_NAME))),
				"\\x20", StringPool.SPACE)
		).put(
			"secret",
			HtmlUtil.escapeJS(
				GetterUtil.getString(
					_httpServletRequest.getAttribute(
						MFATimeBasedOTPWebKeys.
							MFA_TIME_BASED_OTP_SHARED_SECRET)))
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletDisplay _portletDisplay;

}