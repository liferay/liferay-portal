/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.layoutconfiguration.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletContainerException;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.portlet.render.PortletRenderParts;
import com.liferay.portal.kernel.portlet.render.PortletRenderUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
public class PortletRenderer {

	public PortletRenderer(
		Portlet portlet, String columnId, Integer columnCount,
		Integer columnPos) {

		_portlet = portlet;
		_columnId = columnId;
		_columnCount = columnCount;
		_columnPos = columnPos;
	}

	public Portlet getPortlet() {
		return _portlet;
	}

	public StringBundler render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			Map<String, Object> headerRequestAttributes)
		throws PortletContainerException {

		httpServletRequest = PortletContainerUtil.setupOptionalRenderParameters(
			httpServletRequest, null, _columnId, _columnPos, _columnCount);

		_copyHeaderRequestAttributes(
			headerRequestAttributes, httpServletRequest);

		return _render(httpServletRequest, httpServletResponse);
	}

	public StringBundler renderAjax(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortletContainerException {

		httpServletRequest = PortletContainerUtil.setupOptionalRenderParameters(
			httpServletRequest, _RENDER_PATH, _columnId, _columnPos,
			_columnCount);

		return _render(httpServletRequest, httpServletResponse);
	}

	public Map<String, Object> renderHeaders(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			List<String> attributePrefixes)
		throws PortletContainerException {

		httpServletRequest = PortletContainerUtil.setupOptionalRenderParameters(
			httpServletRequest, null, _columnId, _columnPos, _columnCount);

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		PortletContainerUtil.renderHeaders(
			httpServletRequest, bufferCacheServletResponse, _portlet);

		Map<String, Object> headerRequestAttributes = new HashMap<>();

		Enumeration<String> enumeration =
			httpServletRequest.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String attributeName = enumeration.nextElement();

			if (attributeName.contains(
					"jakarta.portlet.faces.renderResponseOutput")) {

				headerRequestAttributes.put(
					attributeName,
					httpServletRequest.getAttribute(attributeName));
			}
			else if (attributePrefixes != null) {
				for (String attributePrefix : attributePrefixes) {
					if (attributeName.contains(attributePrefix)) {
						headerRequestAttributes.put(
							attributeName,
							httpServletRequest.getAttribute(attributeName));

						break;
					}
				}
			}
		}

		return headerRequestAttributes;
	}

	private void _copyHeaderRequestAttributes(
		Map<String, Object> headerRequestAttributes,
		HttpServletRequest httpServletRequest) {

		if (headerRequestAttributes != null) {
			for (Map.Entry<String, Object> entry :
					headerRequestAttributes.entrySet()) {

				httpServletRequest.setAttribute(
					entry.getKey(), entry.getValue());
			}
		}
	}

	private StringBundler _render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortletContainerException {

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		try {
			PortletRenderParts portletRenderParts = null;

			if (_columnId == null) {
				httpServletRequest.setAttribute(
					WebKeys.RENDER_PORTLET_RESOURCE, Boolean.TRUE);

				portletRenderParts = PortletRenderUtil.getPortletRenderParts(
					httpServletRequest, StringPool.BLANK, _portlet);

				PortletRenderUtil.writeHeaderPaths(
					bufferCacheServletResponse, portletRenderParts);
			}

			PortletContainerUtil.render(
				httpServletRequest, bufferCacheServletResponse, _portlet);

			if (portletRenderParts != null) {
				PortletRenderUtil.writeFooterPaths(
					bufferCacheServletResponse, portletRenderParts);
			}

			return bufferCacheServletResponse.getStringBundler();
		}
		catch (IOException ioException) {
			throw new PortletContainerException(ioException);
		}
		finally {
			httpServletRequest.removeAttribute(WebKeys.RENDER_PORTLET_RESOURCE);
		}
	}

	private static final String _RENDER_PATH =
		"/html/portal/load_render_portlet.jsp";

	private final Integer _columnCount;
	private final String _columnId;
	private final Integer _columnPos;
	private final Portlet _portlet;

}