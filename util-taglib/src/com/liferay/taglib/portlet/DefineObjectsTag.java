/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Brian Wing Shun Chan
 */
public class DefineObjectsTag extends TagSupport {

	@Override
	public int doStartTag() {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		String lifecycle = (String)httpServletRequest.getAttribute(
			PortletRequest.LIFECYCLE_PHASE);

		PortletConfig portletConfig =
			(PortletConfig)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_CONFIG);

		if (portletConfig != null) {
			pageContext.setAttribute("portletConfig", portletConfig);
			pageContext.setAttribute(
				"portletName", portletConfig.getPortletName());
		}

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		if (portletRequest != null) {
			pageContext.setAttribute(
				"liferayPortletRequest",
				PortalUtil.getLiferayPortletRequest(portletRequest));

			if (lifecycle != null) {
				String portletRequestAttrName = null;

				if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
					portletRequestAttrName = "actionRequest";
				}
				else if (lifecycle.equals(PortletRequest.EVENT_PHASE)) {
					portletRequestAttrName = "eventRequest";
				}
				else if (lifecycle.equals(PortletRequest.HEADER_PHASE)) {
					portletRequestAttrName = "headerRequest";
				}
				else if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
					portletRequestAttrName = "renderRequest";
				}
				else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
					portletRequestAttrName = "resourceRequest";
				}

				pageContext.setAttribute(
					portletRequestAttrName, portletRequest);
			}

			PortletPreferences portletPreferences =
				portletRequest.getPreferences();

			pageContext.setAttribute("portletPreferences", portletPreferences);
			pageContext.setAttribute(
				"portletPreferencesValues",
				_mapProxyProviderFunction.apply(
					new PortletPreferencesValuesInvocationHandler(
						portletPreferences)));

			PortletSession portletSession = portletRequest.getPortletSession();

			pageContext.setAttribute("portletSession", portletSession);

			try {
				pageContext.setAttribute(
					"portletSessionScope", portletSession.getAttributeMap());
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalStateException);
				}
			}
		}

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if (portletResponse == null) {
			return SKIP_BODY;
		}

		pageContext.setAttribute(
			"liferayPortletResponse",
			PortalUtil.getLiferayPortletResponse(portletResponse));

		if (lifecycle != null) {
			String portletResponseAttrName = null;

			if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
				portletResponseAttrName = "actionResponse";
			}
			else if (lifecycle.equals(PortletRequest.EVENT_PHASE)) {
				portletResponseAttrName = "eventResponse";
			}
			else if (lifecycle.equals(PortletRequest.HEADER_PHASE)) {
				portletResponseAttrName = "headerResponse";
			}
			else if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
				portletResponseAttrName = "renderResponse";
			}
			else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
				portletResponseAttrName = "resourceResponse";
			}

			pageContext.setAttribute(portletResponseAttrName, portletResponse);
		}

		return SKIP_BODY;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefineObjectsTag.class);

	private static final Function<InvocationHandler, Map<?, ?>>
		_mapProxyProviderFunction = ProxyUtil.getProxyProviderFunction(
			Map.class);

	private static class PortletPreferencesValuesInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws ReflectiveOperationException {

			if (_map == null) {
				_map = _portletPreferences.getMap();
			}

			return method.invoke(_map, args);
		}

		private PortletPreferencesValuesInvocationHandler(
			PortletPreferences portletPreferences) {

			_portletPreferences = portletPreferences;
		}

		private Map<String, String[]> _map;
		private final PortletPreferences _portletPreferences;

	}

}