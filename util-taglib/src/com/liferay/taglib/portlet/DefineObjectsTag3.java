/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portlet;

import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.AUIUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.StateAwareResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class DefineObjectsTag3 extends DefineObjectsTag {

	@Override
	public int doStartTag() {
		super.doStartTag();

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		String lifecycle = (String)httpServletRequest.getAttribute(
			PortletRequest.LIFECYCLE_PHASE);

		PortletConfig portletConfig =
			(PortletConfig)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_CONFIG);

		if (portletConfig != null) {
			pageContext.setAttribute(
				"portletContext", portletConfig.getPortletContext());
		}

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		if (portletRequest != null) {
			if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
				ActionRequest actionRequest = (ActionRequest)portletRequest;

				pageContext.setAttribute(
					"actionParams", actionRequest.getActionParameters());
			}

			if (lifecycle.equals(PortletRequest.ACTION_PHASE) ||
				lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

				pageContext.setAttribute("clientDataRequest", portletRequest);
			}

			pageContext.setAttribute(
				"contextPath", portletRequest.getContextPath());
			pageContext.setAttribute("cookies", portletRequest.getCookies());
			pageContext.setAttribute("locale", portletRequest.getLocale());

			List<Locale> locales = Collections.list(
				portletRequest.getLocales());

			pageContext.setAttribute("locales", locales.toArray(new Locale[0]));

			pageContext.setAttribute(
				"portletMode", portletRequest.getPortletMode());
			pageContext.setAttribute("portletRequest", portletRequest);
			pageContext.setAttribute(
				"renderParams", portletRequest.getRenderParameters());

			if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
				ResourceRequest resourceRequest =
					(ResourceRequest)portletRequest;

				pageContext.setAttribute(
					"resourceParams", resourceRequest.getResourceParameters());
			}

			pageContext.setAttribute("windowId", portletRequest.getWindowID());
			pageContext.setAttribute(
				"windowState", portletRequest.getWindowState());
		}

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (portletResponse == null) {
			return SKIP_BODY;
		}

		String namespace = AUIUtil.getNamespace(
			portletRequest, portletResponse);

		if (Validator.isNull(namespace)) {
			namespace = AUIUtil.getNamespace(httpServletRequest);
		}

		pageContext.setAttribute("namespace", namespace);
		pageContext.setAttribute("portletResponse", portletResponse);

		if (lifecycle.equals(PortletRequest.ACTION_PHASE) ||
			lifecycle.equals(PortletRequest.EVENT_PHASE)) {

			StateAwareResponse stateAwareResponse =
				(StateAwareResponse)portletResponse;

			pageContext.setAttribute(
				"mutableRenderParams",
				stateAwareResponse.getRenderParameters());
			pageContext.setAttribute("stateAwareResponse", stateAwareResponse);
		}

		return SKIP_BODY;
	}

}