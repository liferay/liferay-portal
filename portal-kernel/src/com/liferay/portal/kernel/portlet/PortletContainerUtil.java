/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.servlet.TempAttributesServletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.QName;

import jakarta.portlet.Event;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Shuyang Zhou
 * @author Raymond Augé
 * @author Neil Griffin
 */
public class PortletContainerUtil {

	public static List<LayoutTypePortlet> getLayoutTypePortlets(Layout layout)
		throws PortletContainerException {

		if (_PORTLET_EVENT_DISTRIBUTION_LAYOUT_SET) {
			List<Layout> layouts = null;

			try {
				layouts = LayoutLocalServiceUtil.getLayouts(
					layout.getGroupId(), layout.isPrivateLayout(),
					LayoutConstants.TYPE_PORTLET);
			}
			catch (PortalException portalException) {
				throw new PortletContainerException(portalException);
			}

			List<LayoutTypePortlet> layoutTypePortlets = new ArrayList<>(
				layouts.size());

			for (Layout curLayout : layouts) {
				LayoutTypePortlet layoutTypePortlet =
					(LayoutTypePortlet)curLayout.getLayoutType();

				layoutTypePortlets.add(layoutTypePortlet);
			}

			return layoutTypePortlets;
		}

		if (layout.isTypePortlet()) {
			List<LayoutTypePortlet> layoutTypePortlets = new ArrayList<>(1);

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			layoutTypePortlets.add(layoutTypePortlet);

			return layoutTypePortlets;
		}

		return Collections.emptyList();
	}

	public static PortletContainer getPortletContainer() {
		return _portletContainer;
	}

	public static void preparePortlet(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws PortletContainerException {

		_portletContainer.preparePortlet(httpServletRequest, portlet);
	}

	public static void processAction(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		PortletContainer portletContainer = _portletContainer;

		ActionResult actionResult = portletContainer.processAction(
			httpServletRequest, httpServletResponse, portlet);

		String location = actionResult.getLocation();

		if (_PORTLET_EVENT_DISTRIBUTION_LAYOUT_SET ||
			portlet.isActionURLRedirect() || Validator.isNull(location)) {

			List<Event> events = actionResult.getEvents();

			if (!events.isEmpty()) {
				_processEvents(httpServletRequest, httpServletResponse, events);
			}
		}

		if (Validator.isNull(location) || httpServletResponse.isCommitted()) {
			return;
		}

		PortletApp portletApp = portlet.getPortletApp();

		if ((portletApp.getSpecMajorVersion() >= 3) &&
			portlet.isActionURLRedirect()) {

			Layout layout = (Layout)httpServletRequest.getAttribute(
				WebKeys.LAYOUT);

			LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
				httpServletRequest, portlet, layout,
				PortletRequest.RENDER_PHASE, MimeResponse.Copy.ALL);

			try {
				URL locationURL = new URL(location);

				URL renderURL = new URL(liferayPortletURL.toString());

				String protocol = locationURL.getProtocol();
				String host = locationURL.getHost();
				int port = locationURL.getPort();

				if (protocol.equals(renderURL.getProtocol()) &&
					host.equals(renderURL.getHost()) &&
					(port == renderURL.getPort()) &&
					_hasSamePortletIdParameter(
						locationURL.getQuery(), renderURL.getQuery())) {

					location = liferayPortletURL.toString();
				}
			}
			catch (MalformedURLException malformedURLException) {
				throw new PortletContainerException(malformedURLException);
			}
		}

		try {
			httpServletResponse.sendRedirect(location);
		}
		catch (IOException ioException) {
			throw new PortletContainerException(ioException);
		}
	}

	public static void processEvent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet,
			Layout layout, Event event)
		throws PortletContainerException {

		PortletContainer portletContainer = _portletContainer;

		List<Event> events = portletContainer.processEvent(
			httpServletRequest, httpServletResponse, portlet, layout, event);

		if (!events.isEmpty()) {
			_processEvents(httpServletRequest, httpServletResponse, events);
		}
	}

	public static void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout) {

		_portletContainer.processPublicRenderParameters(
			httpServletRequest, layout);
	}

	public static void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		_portletContainer.processPublicRenderParameters(
			httpServletRequest, layout, portlet);
	}

	public static void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		_portletContainer.render(
			httpServletRequest, httpServletResponse, portlet);
	}

	public static void renderHeaders(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		_portletContainer.renderHeaders(
			httpServletRequest, httpServletResponse, portlet);
	}

	public static void serveResource(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		_portletContainer.serveResource(
			httpServletRequest, httpServletResponse, portlet);
	}

	public static HttpServletRequest setupOptionalRenderParameters(
		HttpServletRequest httpServletRequest, String renderPath,
		String columnId, Integer columnPos, Integer columnCount) {

		return setupOptionalRenderParameters(
			httpServletRequest, renderPath, columnId, columnPos, columnCount,
			null, null);
	}

	public static HttpServletRequest setupOptionalRenderParameters(
		HttpServletRequest httpServletRequest, String renderPath,
		String columnId, Integer columnPos, Integer columnCount,
		Boolean boundary, Boolean decorate) {

		if (_PORTLET_CONTAINER_RESTRICT) {
			RestrictPortletServletRequest restrictPortletServletRequest =
				new RestrictPortletServletRequest(httpServletRequest);

			if (renderPath != null) {
				restrictPortletServletRequest.setAttribute(
					WebKeys.RENDER_PATH, renderPath);
			}

			if (columnId != null) {
				restrictPortletServletRequest.setAttribute(
					WebKeys.RENDER_PORTLET_COLUMN_ID, columnId);
			}

			if (columnPos != null) {
				restrictPortletServletRequest.setAttribute(
					WebKeys.RENDER_PORTLET_COLUMN_POS, columnPos);
			}

			if (columnCount != null) {
				restrictPortletServletRequest.setAttribute(
					WebKeys.RENDER_PORTLET_COLUMN_COUNT, columnCount);
			}

			if (boundary != null) {
				restrictPortletServletRequest.setAttribute(
					WebKeys.RENDER_PORTLET_BOUNDARY, boundary);
			}

			if (decorate != null) {
				restrictPortletServletRequest.setAttribute(
					WebKeys.PORTLET_DECORATE, decorate);
			}

			return restrictPortletServletRequest;
		}

		TempAttributesServletRequest tempAttributesServletRequest =
			new TempAttributesServletRequest(httpServletRequest);

		if (renderPath != null) {
			tempAttributesServletRequest.setTempAttribute(
				WebKeys.RENDER_PATH, renderPath);
		}

		if (columnId != null) {
			tempAttributesServletRequest.setTempAttribute(
				WebKeys.RENDER_PORTLET_COLUMN_ID, columnId);
		}

		if (columnPos != null) {
			tempAttributesServletRequest.setTempAttribute(
				WebKeys.RENDER_PORTLET_COLUMN_POS, columnPos);
		}

		if (columnCount != null) {
			tempAttributesServletRequest.setTempAttribute(
				WebKeys.RENDER_PORTLET_COLUMN_COUNT, columnCount);
		}

		return tempAttributesServletRequest;
	}

	public void setPortletContainer(PortletContainer portletContainer) {
		_portletContainer = portletContainer;
	}

	private static boolean _hasSamePortletIdParameter(
		String queryString1, String queryString2) {

		if ((queryString1 == null) || (queryString2 == null)) {
			return false;
		}

		int x1 = queryString1.indexOf("p_p_id=");

		if (x1 < 0) {
			return false;
		}

		int x2 = queryString2.indexOf("p_p_id=");

		if (x2 < 0) {
			return false;
		}

		x1 += 7;
		x2 += 7;

		int y1 = queryString1.indexOf(CharPool.AMPERSAND, x1);

		if (y1 < 0) {
			y1 = queryString1.length();
		}

		int length = y1 - x1;

		int y2 = length + x2;

		if ((y2 > queryString2.length()) ||
			((y2 != queryString2.length()) &&
			 (queryString2.charAt(y2) != CharPool.AMPERSAND))) {

			return false;
		}

		return queryString1.regionMatches(x1, queryString2, x2, length);
	}

	private static void _processEvents(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, List<Event> events)
		throws PortletContainerException {

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		List<LayoutTypePortlet> layoutTypePortlets = getLayoutTypePortlets(
			layout);

		for (LayoutTypePortlet layoutTypePortlet : layoutTypePortlets) {
			List<Portlet> portlets = null;

			try {
				portlets = layoutTypePortlet.getAllPortlets();
			}
			catch (Exception exception) {
				throw new PortletContainerException(exception);
			}

			for (Portlet portlet : portlets) {
				for (Event event : events) {
					javax.xml.namespace.QName qName = event.getQName();

					QName processingQName = portlet.getProcessingEvent(
						qName.getNamespaceURI(), qName.getLocalPart());

					if (processingQName == null) {
						continue;
					}

					processEvent(
						httpServletRequest, httpServletResponse, portlet,
						layoutTypePortlet.getLayout(), event);
				}
			}
		}
	}

	private static final boolean _PORTLET_CONTAINER_RESTRICT =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.PORTLET_CONTAINER_RESTRICT));

	private static final boolean _PORTLET_EVENT_DISTRIBUTION_LAYOUT_SET =
		!StringUtil.equalsIgnoreCase(
			PropsUtil.get(PropsKeys.PORTLET_EVENT_DISTRIBUTION), "layout");

	private static PortletContainer _portletContainer;

}