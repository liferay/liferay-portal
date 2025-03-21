/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.ActionResult;
import com.liferay.portal.kernel.portlet.PortletContainer;
import com.liferay.portal.kernel.portlet.PortletContainerException;
import com.liferay.portal.kernel.portlet.RestrictPortletServletRequest;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.Event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class RestrictPortletContainerWrapper implements PortletContainer {

	public static PortletContainer createRestrictPortletContainerWrapper(
		PortletContainer portletContainer) {

		if (PropsValues.PORTLET_CONTAINER_RESTRICT) {
			portletContainer = new RestrictPortletContainerWrapper(
				portletContainer);
		}

		return portletContainer;
	}

	public RestrictPortletContainerWrapper(PortletContainer portletContainer) {
		_portletContainer = portletContainer;
	}

	@Override
	public void preparePortlet(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws PortletContainerException {

		_portletContainer.preparePortlet(httpServletRequest, portlet);
	}

	@Override
	public ActionResult processAction(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		RestrictPortletServletRequest restrictPortletServletRequest =
			new RestrictPortletServletRequest(httpServletRequest);

		try {
			return _portletContainer.processAction(
				httpServletRequest, httpServletResponse, portlet);
		}
		finally {
			restrictPortletServletRequest.mergeSharedAttributes();
		}
	}

	@Override
	public List<Event> processEvent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet,
			Layout layout, Event event)
		throws PortletContainerException {

		RestrictPortletServletRequest restrictPortletServletRequest =
			new RestrictPortletServletRequest(httpServletRequest);

		try {
			return _portletContainer.processEvent(
				httpServletRequest, httpServletResponse, portlet, layout,
				event);
		}
		finally {
			restrictPortletServletRequest.mergeSharedAttributes();
		}
	}

	@Override
	public void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout) {

		_portletContainer.processPublicRenderParameters(
			httpServletRequest, layout);
	}

	@Override
	public void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		_portletContainer.processPublicRenderParameters(
			httpServletRequest, layout, portlet);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		_render(
			httpServletRequest,
			() -> _portletContainer.render(
				httpServletRequest, httpServletResponse, portlet));
	}

	@Override
	public void renderHeaders(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		_render(
			httpServletRequest,
			() -> _portletContainer.renderHeaders(
				httpServletRequest, httpServletResponse, portlet));
	}

	@Override
	public void serveResource(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		RestrictPortletServletRequest restrictPortletServletRequest =
			new RestrictPortletServletRequest(httpServletRequest);

		try {
			_portletContainer.serveResource(
				httpServletRequest, httpServletResponse, portlet);
		}
		catch (Exception exception) {
			throw new PortletContainerException(exception);
		}
		finally {
			restrictPortletServletRequest.mergeSharedAttributes();
		}
	}

	private void _render(
			HttpServletRequest httpServletRequest, Renderable renderable)
		throws PortletContainerException {

		RestrictPortletServletRequest restrictPortletServletRequest = null;

		if (httpServletRequest instanceof RestrictPortletServletRequest) {
			restrictPortletServletRequest =
				(RestrictPortletServletRequest)httpServletRequest;

			Map<String, Object> attributes =
				restrictPortletServletRequest.getAttributes();

			if (attributes.containsKey(WebKeys.RENDER_PORTLET)) {
				restrictPortletServletRequest =
					new RestrictPortletServletRequest(httpServletRequest);
			}
		}
		else {
			restrictPortletServletRequest = new RestrictPortletServletRequest(
				httpServletRequest);
		}

		try {
			renderable.render();
		}
		finally {
			restrictPortletServletRequest.removeAttribute(WebKeys.RENDER_PATH);
			restrictPortletServletRequest.removeAttribute(
				WebKeys.RENDER_PORTLET_COLUMN_COUNT);
			restrictPortletServletRequest.removeAttribute(
				WebKeys.RENDER_PORTLET_COLUMN_ID);
			restrictPortletServletRequest.removeAttribute(
				WebKeys.RENDER_PORTLET_COLUMN_POS);

			restrictPortletServletRequest.mergeSharedAttributes();
		}
	}

	private final PortletContainer _portletContainer;

	@FunctionalInterface
	private interface Renderable {

		public void render() throws PortletContainerException;

	}

}