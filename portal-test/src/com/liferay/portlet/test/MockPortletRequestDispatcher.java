/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.test;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

/**
 * @author David Arques
 * @see com.liferay.portlet.internal.PortletRequestDispatcherImpl
 */
public class MockPortletRequestDispatcher implements PortletRequestDispatcher {

	@Override
	public void forward(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortletException {
	}

	@Override
	public void include(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortletException {
	}

	@Override
	public void include(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
	}

}