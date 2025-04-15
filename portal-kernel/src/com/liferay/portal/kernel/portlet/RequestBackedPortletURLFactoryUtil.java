/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
public class RequestBackedPortletURLFactoryUtil {

	public static RequestBackedPortletURLFactory create(
		HttpServletRequest httpServletRequest) {

		return new HttpServletRequestRequestBackedPortletURLFactory(
			httpServletRequest);
	}

	public static RequestBackedPortletURLFactory create(
		PortletRequest portletRequest) {

		PortletResponse portletResponse =
			(PortletResponse)portletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if (portletResponse == null) {
			return create(PortalUtil.getHttpServletRequest(portletRequest));
		}

		return new LiferayPortletResponseRequestBackedPortletURLFactory(
			PortalUtil.getLiferayPortletRequest(portletRequest),
			PortalUtil.getLiferayPortletResponse(portletResponse));
	}

	private static Layout _getControlPanelLayout(
		Layout controlPanelLayout, Group group) {

		if (controlPanelLayout == null) {
			return null;
		}

		if (group.isControlPanel()) {
			return controlPanelLayout;
		}

		return new VirtualLayout(controlPanelLayout, group);
	}

	private static PortletURL _populateControlPanelPortletURL(
		LiferayPortletURL liferayPortletURL, long refererGroupId,
		long refererPlid) {

		if (refererGroupId > 0) {
			liferayPortletURL.setRefererGroupId(refererGroupId);
		}

		if (refererPlid > 0) {
			liferayPortletURL.setRefererPlid(refererPlid);
		}

		try {
			liferayPortletURL.setWindowState(WindowState.MAXIMIZED);
		}
		catch (WindowStateException windowStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(windowStateException);
			}
		}

		return liferayPortletURL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RequestBackedPortletURLFactoryUtil.class);

	private static class HttpServletRequestRequestBackedPortletURLFactory
		implements RequestBackedPortletURLFactory {

		@Override
		public PortletURL createActionURL(String portletId) {
			String actionPhase = PortletRequest.ACTION_PHASE;

			return createPortletURL(portletId, actionPhase);
		}

		@Override
		public PortletURL createControlPanelActionURL(
			String portletId, Group group, long refererGroupId,
			long refererPlid) {

			return createControlPanelPortletURL(
				portletId, group, refererGroupId, refererPlid,
				PortletRequest.ACTION_PHASE);
		}

		@Override
		public PortletURL createControlPanelPortletURL(
			String portletId, Group group, long refererGroupId,
			long refererPlid, String lifecycle) {

			Layout controlPanelLayout = null;

			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (themeDisplay != null) {
				controlPanelLayout = themeDisplay.getControlPanelLayout();

				if (group == null) {
					group = themeDisplay.getScopeGroup();
				}
			}

			LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
				_httpServletRequest, portletId,
				_getControlPanelLayout(controlPanelLayout, group), lifecycle);

			return _populateControlPanelPortletURL(
				liferayPortletURL, refererGroupId, refererPlid);
		}

		@Override
		public PortletURL createControlPanelRenderURL(
			String portletId, Group group, long refererGroupId,
			long refererPlid) {

			return createControlPanelPortletURL(
				portletId, group, refererGroupId, refererPlid,
				PortletRequest.RENDER_PHASE);
		}

		@Override
		public PortletURL createControlPanelResourceURL(
			String portletId, Group group, long refererGroupId,
			long refererPlid) {

			return createControlPanelPortletURL(
				portletId, group, refererGroupId, refererPlid,
				PortletRequest.RESOURCE_PHASE);
		}

		@Override
		public PortletURL createPortletURL(String portletId, String lifecycle) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return PortletURLFactoryUtil.create(
				_httpServletRequest, portletId, themeDisplay.getPlid(),
				lifecycle);
		}

		@Override
		public PortletURL createRenderURL(String portletId) {
			return createPortletURL(portletId, PortletRequest.RENDER_PHASE);
		}

		@Override
		public PortletURL createResourceURL(String portletId) {
			return createPortletURL(portletId, PortletRequest.RESOURCE_PHASE);
		}

		private HttpServletRequestRequestBackedPortletURLFactory(
			HttpServletRequest httpServletRequest) {

			_httpServletRequest = httpServletRequest;
		}

		private final HttpServletRequest _httpServletRequest;

	}

	private static class LiferayPortletResponseRequestBackedPortletURLFactory
		implements RequestBackedPortletURLFactory {

		@Override
		public PortletURL createActionURL(String portletId) {
			return _liferayPortletResponse.createActionURL(portletId);
		}

		@Override
		public PortletURL createControlPanelActionURL(
			String portletId, Group group, long refererGroupId,
			long refererPlid) {

			return createControlPanelPortletURL(
				portletId, group, refererGroupId, refererPlid,
				PortletRequest.ACTION_PHASE);
		}

		@Override
		public PortletURL createControlPanelPortletURL(
			String portletId, Group group, long refererGroupId,
			long refererPlid, String lifecycle) {

			Layout controlPanelLayout = null;

			ThemeDisplay themeDisplay =
				(ThemeDisplay)_liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (themeDisplay != null) {
				controlPanelLayout = themeDisplay.getControlPanelLayout();

				if (group == null) {
					group = themeDisplay.getScopeGroup();
				}
			}

			LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
				_liferayPortletRequest, portletId,
				_getControlPanelLayout(controlPanelLayout, group), lifecycle);

			return _populateControlPanelPortletURL(
				liferayPortletURL, refererGroupId, refererPlid);
		}

		@Override
		public PortletURL createControlPanelRenderURL(
			String portletId, Group group, long refererGroupId,
			long refererPlid) {

			return createControlPanelPortletURL(
				portletId, group, refererGroupId, refererPlid,
				PortletRequest.RENDER_PHASE);
		}

		@Override
		public PortletURL createControlPanelResourceURL(
			String portletId, Group group, long refererGroupId,
			long refererPlid) {

			return createControlPanelPortletURL(
				portletId, group, refererGroupId, refererPlid,
				PortletRequest.RESOURCE_PHASE);
		}

		@Override
		public PortletURL createPortletURL(String portletId, String lifecycle) {
			return _liferayPortletResponse.createLiferayPortletURL(
				portletId, lifecycle);
		}

		@Override
		public PortletURL createRenderURL(String portletId) {
			return _liferayPortletResponse.createRenderURL(portletId);
		}

		@Override
		public PortletURL createResourceURL(String portletId) {
			return _liferayPortletResponse.createResourceURL(portletId);
		}

		private LiferayPortletResponseRequestBackedPortletURLFactory(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) {

			_liferayPortletRequest = liferayPortletRequest;
			_liferayPortletResponse = liferayPortletResponse;
		}

		private final LiferayPortletRequest _liferayPortletRequest;
		private final LiferayPortletResponse _liferayPortletResponse;

	}

}