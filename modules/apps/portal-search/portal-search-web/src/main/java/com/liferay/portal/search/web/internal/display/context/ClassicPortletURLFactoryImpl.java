/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.display.context;

import com.liferay.portal.kernel.portlet.PortletURLUtil;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

/**
 * @author André de Oliveira
 */
public class ClassicPortletURLFactoryImpl implements PortletURLFactory {

	public ClassicPortletURLFactoryImpl(
		PortletRequest portletRequest, MimeResponse mimeResponse) {

		_portletRequest = portletRequest;
		_mimeResponse = mimeResponse;
	}

	@Override
	public PortletURL getPortletURL() throws PortletException {
		PortletURL portletURL = PortletURLUtil.getCurrent(
			_portletRequest, _mimeResponse);

		return PortletURLUtil.clone(portletURL, _mimeResponse);
	}

	private final MimeResponse _mimeResponse;
	private final PortletRequest _portletRequest;

}