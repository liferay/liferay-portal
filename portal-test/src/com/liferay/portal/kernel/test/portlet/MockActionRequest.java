/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.ActionParameters;
import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;

/**
 * @author Dante Wang
 */
public class MockActionRequest
	extends MockClientDataRequest implements ActionRequest {

	public MockActionRequest() {
	}

	public MockActionRequest(
		PortalContext portalContext, PortletContext portletContext) {

		super(portalContext, portletContext);
	}

	public MockActionRequest(PortletContext portletContext) {
		super(portletContext);
	}

	public MockActionRequest(PortletMode portletMode) {
		setPortletMode(portletMode);
	}

	public MockActionRequest(String actionName) {
		setParameter("jakarta.portlet.action", actionName);
	}

	@Override
	public ActionParameters getActionParameters() {
		if (_actionParameters == null) {
			_actionParameters = new MockActionParameters();
		}

		return _actionParameters;
	}

	@Override
	protected String getLifecyclePhase() {
		return "ACTION_PHASE";
	}

	private ActionParameters _actionParameters;

}