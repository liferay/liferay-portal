/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.ActionURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.MutableActionParameters;
import jakarta.portlet.PortalContext;

/**
 * @author Dante Wang
 */
public class MockActionURL extends MockPortletURL implements ActionURL {

	public MockActionURL(PortalContext portalContext, MimeResponse.Copy copy) {
		super(portalContext, URL_TYPE_ACTION);
	}

	@Override
	public MutableActionParameters getActionParameters() {
		if (_mutableActionParameters == null) {
			_mutableActionParameters = new MockMutableActionParameters();
		}

		return _mutableActionParameters;
	}

	public static class MockMutableActionParameters
		extends MockMutablePortletParameters
		implements MutableActionParameters {

		@Override
		public MutableActionParameters clone() {
			return null;
		}

	}

	private MutableActionParameters _mutableActionParameters;

}