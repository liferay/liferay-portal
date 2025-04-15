/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.web.internal.display.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.util.JavaConstants;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Leonardo Barros
 */
public class DDLRequestHelper extends BaseRequestHelper {

	public DDLRequestHelper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);

		_renderRequest = (RenderRequest)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);

		_portletPreferences = _renderRequest.getPreferences();
	}

	public PortletPreferences getPortletPreferences() {
		return _portletPreferences;
	}

	public RenderRequest getRenderRequest() {
		return _renderRequest;
	}

	private final PortletPreferences _portletPreferences;
	private final RenderRequest _renderRequest;

}