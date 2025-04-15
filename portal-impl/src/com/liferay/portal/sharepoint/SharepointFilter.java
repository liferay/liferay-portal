/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.sharepoint;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.servlet.filters.secure.BaseAuthFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Bruno Farache
 * @author Alexander Chow
 */
public class SharepointFilter extends BaseAuthFilter {

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);

		setUsePermissionChecker(true);
	}

	protected boolean isSharepointRequest(String uri) {
		if (uri == null) {
			return false;
		}

		if (uri.endsWith("*.asmx")) {
			return true;
		}

		for (String prefix : _PREFIXES) {
			if (uri.startsWith(prefix)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isWebDAVRequest(String uri) {
		if (uri.startsWith("/webdav")) {
			return true;
		}

		return false;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		String method = httpServletRequest.getMethod();

		String userAgent = GetterUtil.getString(
			httpServletRequest.getHeader(HttpHeaders.USER_AGENT));

		if ((userAgent.startsWith(
				"Microsoft Data Access Internet Publishing") ||
			 userAgent.startsWith("Microsoft Office Protocol Discovery")) &&
			method.equals(HttpMethods.OPTIONS)) {

			setOptionsHeaders(httpServletRequest, httpServletResponse);

			return;
		}

		if (!isSharepointRequest(httpServletRequest.getRequestURI())) {
			processFilter(
				SharepointFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		if (method.equals(HttpMethods.GET) || method.equals(HttpMethods.HEAD)) {
			setGetHeaders(httpServletResponse);
		}
		else if (method.equals(HttpMethods.POST)) {
			setPostHeaders(httpServletResponse);
		}

		super.processFilter(
			httpServletRequest, httpServletResponse, filterChain);
	}

	protected void setGetHeaders(HttpServletResponse httpServletResponse) {
		httpServletResponse.setContentType("text/html");
		httpServletResponse.setHeader(
			"Public-Extension", "http://schemas.microsoft.com/repl-2");
		httpServletResponse.setHeader(
			"MicrosoftSharePointTeamServices", SharepointUtil.VERSION);
		httpServletResponse.setHeader("Cache-Control", "no-cache");
	}

	protected void setOptionsHeaders(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (isWebDAVRequest(httpServletRequest.getRequestURI())) {
			httpServletResponse.setHeader("MS-Author-Via", "DAV,MS-FP/4.0");
		}
		else {
			httpServletResponse.setHeader("MS-Author-Via", "MS-FP/4.0,DAV");
		}

		httpServletResponse.setHeader("MicrosoftOfficeWebServer", "5.0_Collab");
		httpServletResponse.setHeader(
			"MicrosoftSharePointTeamServices", SharepointUtil.VERSION);
		httpServletResponse.setHeader("DAV", "1,2");
		httpServletResponse.setHeader("Accept-Ranges", "none");
		httpServletResponse.setHeader("Cache-Control", "no-cache");
		httpServletResponse.setHeader(
			"Allow",
			"COPY, DELETE, GET, GETLIB, HEAD, LOCK, MKCOL, MOVE, OPTIONS, " +
				"POST, PROPFIND, PROPPATCH, PUT, UNLOCK");
	}

	protected void setPostHeaders(HttpServletResponse httpServletResponse) {
		httpServletResponse.setContentType("application/x-vermeer-rpc");
		httpServletResponse.setHeader(
			"MicrosoftSharePointTeamServices", SharepointUtil.VERSION);
		httpServletResponse.setHeader("Cache-Control", "no-cache");
		httpServletResponse.setHeader("Connection", "close");
	}

	private static final String[] _PREFIXES = {
		"/_vti_inf.html", "/_vti_bin", "/sharepoint", "/history", "/resources"
	};

}