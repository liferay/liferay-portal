/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletSession;
import com.liferay.portal.kernel.portlet.PortletFilterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.filter.FilterChain;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletServlet extends HttpServlet {

	public static final String PORTLET_APP =
		"com.liferay.portal.kernel.model.PortletApp";

	public static final String PORTLET_SERVLET_CONFIG =
		"com.liferay.portal.kernel.servlet.PortletServletConfig";

	public static final String PORTLET_SERVLET_FILTER_CHAIN =
		"com.liferay.portal.kernel.servlet.PortletServletFilterChain";

	public static final String PORTLET_SERVLET_REQUEST =
		"com.liferay.portal.kernel.servlet.PortletServletRequest";

	public static final String PORTLET_SERVLET_RESPONSE =
		"com.liferay.portal.kernel.servlet.PortletServletResponse";

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		if (httpServletRequest.getAttribute(WebKeys.EXTEND_SESSION) != null) {
			httpServletRequest.removeAttribute(WebKeys.EXTEND_SESSION);

			HttpSession httpSession = httpServletRequest.getSession(false);

			if (httpSession != null) {
				httpSession.setAttribute(WebKeys.EXTEND_SESSION, Boolean.TRUE);

				httpSession.removeAttribute(WebKeys.EXTEND_SESSION);
			}

			return;
		}

		String portletId = (String)httpServletRequest.getAttribute(
			WebKeys.PORTLET_ID);

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		String lifecycle = (String)httpServletRequest.getAttribute(
			PortletRequest.LIFECYCLE_PHASE);

		FilterChain filterChain = (FilterChain)httpServletRequest.getAttribute(
			PORTLET_SERVLET_FILTER_CHAIN);

		LiferayPortletSession portletSession =
			(LiferayPortletSession)portletRequest.getPortletSession();

		portletRequest.setAttribute(PORTLET_SERVLET_CONFIG, getServletConfig());
		portletRequest.setAttribute(
			PORTLET_SERVLET_REQUEST, httpServletRequest);
		portletRequest.setAttribute(
			PORTLET_SERVLET_RESPONSE, httpServletResponse);
		portletRequest.setAttribute(WebKeys.PORTLET_ID, portletId);

		// LPS-66826

		portletSession.setHttpSession(
			_getSharedHttpSession(httpServletRequest, portletRequest));

		try {
			PortletFilterUtil.doFilter(
				portletRequest, portletResponse, lifecycle, filterChain);
		}
		catch (PortletException portletException) {
			_log.error(
				StringBundler.concat(
					"Unable to process portlet ", portletId, ": ",
					portletException.getMessage()),
				portletException);

			throw new ServletException(portletException);
		}
	}

	private HttpSession _getSharedHttpSession(
		HttpServletRequest httpServletRequest, PortletRequest portletRequest) {

		LiferayPortletRequest liferayPortletRequest =
			PortalUtil.getLiferayPortletRequest(portletRequest);

		Portlet portlet = liferayPortletRequest.getPortlet();

		HttpServletRequest originalHttpServletRequest =
			liferayPortletRequest.getOriginalHttpServletRequest();

		HttpSession portalHttpSession = originalHttpServletRequest.getSession();

		if (!portlet.isPrivateSessionAttributes()) {
			return portalHttpSession;
		}

		HttpSession portletHttpSession = httpServletRequest.getSession();

		return new SharedSession(portalHttpSession, portletHttpSession);
	}

	private static final Log _log = LogFactoryUtil.getLog(PortletServlet.class);

}