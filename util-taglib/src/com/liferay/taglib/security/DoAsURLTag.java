/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.security;

import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @author Brian Wing Shun Chan
 */
public class DoAsURLTag extends TagSupport {

	public static String doTag(
			long doAsUserId, HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		String doAsURL = company.getHomeURL();

		if (Validator.isNull(doAsURL)) {
			doAsURL = _PORTAL_IMPERSONATION_DEFAULT_URL;
		}

		doAsURL = themeDisplay.getPathContext() + doAsURL;

		if (doAsUserId <= 0) {
			User guestUser = company.getGuestUser();

			doAsUserId = guestUser.getUserId();
		}

		String encDoAsUserId = EncryptorUtil.encrypt(
			company.getKeyObj(), String.valueOf(doAsUserId));

		return HttpComponentsUtil.addParameter(
			doAsURL, "doAsUserId", encDoAsUserId);
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			String doAsURL = doTag(
				_doAsUserId, (HttpServletRequest)pageContext.getRequest());

			if (Validator.isNotNull(_var)) {
				pageContext.setAttribute(_var, doAsURL);
			}
			else {
				JspWriter jspWriter = pageContext.getOut();

				jspWriter.write(doAsURL);
			}
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}

		return EVAL_PAGE;
	}

	public void setDoAsUserId(long doAsUserId) {
		_doAsUserId = doAsUserId;
	}

	public void setVar(String var) {
		_var = var;
	}

	private static final String _PORTAL_IMPERSONATION_DEFAULT_URL =
		PropsUtil.get(PropsKeys.PORTAL_IMPERSONATION_DEFAULT_URL);

	private long _doAsUserId;
	private String _var;

}