/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

/**
 * @author Shuyang Zhou
 */
public class PortletRequestModelFactory {

	public PortletRequestModelFactory(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
	}

	public PortletRequestModel getPortletRequestModel() {
		if (_portletRequestModel == null) {
			_portletRequestModel = new PortletRequestModel(
				_portletRequest, _portletResponse);
		}

		return _portletRequestModel;
	}

	private final PortletRequest _portletRequest;
	private PortletRequestModel _portletRequestModel;
	private final PortletResponse _portletResponse;

}