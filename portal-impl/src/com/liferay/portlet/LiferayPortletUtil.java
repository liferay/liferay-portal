/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portlet.internal.PortletRequestImpl;
import com.liferay.portlet.internal.PortletResponseImpl;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.filter.PortletRequestWrapper;
import jakarta.portlet.filter.PortletResponseWrapper;

/**
 * @author Neil Griffin
 */
public class LiferayPortletUtil {

	public static LiferayPortletRequest getLiferayPortletRequest(
		PortletRequest portletRequest) {

		if (portletRequest == null) {
			return null;
		}

		while (!(portletRequest instanceof PortletRequestImpl)) {
			if (portletRequest instanceof PortletRequestWrapper) {
				PortletRequestWrapper portletRequestWrapper =
					(PortletRequestWrapper)portletRequest;

				portletRequest = portletRequestWrapper.getRequest();
			}
			else {
				throw new RuntimeException(
					"Unable to unwrap the portlet request from " +
						portletRequest.getClass());
			}
		}

		return (PortletRequestImpl)portletRequest;
	}

	public static LiferayPortletResponse getLiferayPortletResponse(
		PortletResponse portletResponse) {

		if (portletResponse == null) {
			return null;
		}

		while (!(portletResponse instanceof PortletResponseImpl)) {
			if (portletResponse instanceof PortletResponseWrapper) {
				PortletResponseWrapper portletResponseWrapper =
					(PortletResponseWrapper)portletResponse;

				portletResponse = portletResponseWrapper.getResponse();
			}
			else {
				throw new RuntimeException(
					"Unable to unwrap the portlet response from " +
						portletResponse.getClass());
			}
		}

		return (PortletResponseImpl)portletResponse;
	}

}