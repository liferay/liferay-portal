/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet.filter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(
	property = {
		"before-filter=URL Rewrite Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Faro Filter", "url-pattern=/o/proxy/download/*",
		"url-pattern=/web/guest/*", "url-pattern=/workspace/*"
	},
	service = Filter.class
)
public class FaroFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		String uri = httpServletRequest.getRequestURI();

		Matcher matcher = _pattern.matcher(uri);

		if (matcher.find()) {
			return false;
		}

		String ppid = httpServletRequest.getParameter("p_p_id");

		if (StringUtil.equals(ppid, PortletKeys.FAST_LOGIN) ||
			StringUtil.equals(ppid, PortletKeys.LOGIN)) {

			return false;
		}

		HttpSession httpSession = httpServletRequest.getSession();

		String samlSpSessionKey = (String)httpSession.getAttribute(
			_SAML_SP_SESSION_KEY);

		if (Validator.isNotNull(samlSpSessionKey)) {
			return false;
		}

		try {
			User user = _portal.getUser(httpServletRequest);

			if ((user != null) && !user.isDefaultUser()) {
				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return true;
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		String uri = httpServletRequest.getRequestURI();

		if (uri.startsWith("/web/guest")) {
			httpServletResponse.sendRedirect(
				_portal.getPortalURL(httpServletRequest) +
					"/c/portal/login?redirect=/");
		}
		else {
			String encodeURL = URLCodec.encodeURL(
				_portal.getCurrentURL(httpServletRequest));

			httpServletResponse.sendRedirect(
				_portal.getPortalURL(httpServletRequest) +
					"/c/portal/login?redirect=" + encodeURL);
		}
	}

	private static final String _SAML_SP_SESSION_KEY = "SAML_SP_SESSION_KEY";

	private static final Log _log = LogFactoryUtil.getLog(FaroFilter.class);

	private static final Pattern _pattern = Pattern.compile(
		"/workspace/(.*)/endpoints/(.*)");

	@Reference
	private Portal _portal;

}