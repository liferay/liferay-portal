/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.embedded.test;

import com.liferay.portal.model.impl.PortletImpl;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Manuel de la Peña
 */
public class TestNonembeddedPortlet extends PortletImpl implements Portlet {

	@Override
	public void destroy() {
	}

	@Override
	public String getPortletId() {
		Class<?> clazz = getClass();

		return clazz.getCanonicalName();
	}

	@Override
	public void init(PortletConfig portletConfig) {
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public boolean isUndeployedPortlet() {
		return true;
	}

	@Override
	public void processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {
	}

	@Override
	public void render(
		RenderRequest renderRequest, RenderResponse renderResponse) {
	}

}