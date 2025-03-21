/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Michael Young
 */
public class DefaultLandingPageAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			doRun(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	protected void doRun(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String path = PrefsPropsUtil.getString(
			PortalUtil.getCompanyId(httpServletRequest),
			PropsKeys.DEFAULT_LANDING_PAGE_PATH);

		if (_log.isInfoEnabled()) {
			_log.info(
				PropsKeys.DEFAULT_LANDING_PAGE_PATH + StringPool.EQUAL + path);
		}

		if (Validator.isNull(path)) {
			return;
		}

		HttpSession httpSession = httpServletRequest.getSession();

		if (path.contains("${liferay:screenName}") ||
			path.contains("${liferay:userId}")) {

			User user = (User)httpSession.getAttribute(WebKeys.USER);

			if (user == null) {
				return;
			}

			path = StringUtil.replace(
				path,
				new String[] {"${liferay:screenName}", "${liferay:userId}"},
				new String[] {
					HtmlUtil.escapeURL(user.getScreenName()),
					String.valueOf(user.getUserId())
				});
		}

		LastPath lastPath = new LastPath(
			StringPool.BLANK,
			FriendlyURLNormalizerUtil.normalizeWithEncoding(path));

		httpSession.setAttribute(WebKeys.LAST_PATH, lastPath);

		// The commented code shows how you can programmaticaly set the user's
		// landing page. You can modify this class to utilize a custom algorithm
		// for forwarding a user to his landing page. See the references to this
		// class in portal.properties.

		/*Map<String, String[]> params = new HashMap<>();

		params.put("p_l_id", new String[] {"1806"});

		LastPath lastPath = new LastPath("/c", "/portal/layout", params);

		httpSession.setAttribute(WebKeys.LAST_PATH, lastPath);*/
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultLandingPageAction.class);

}