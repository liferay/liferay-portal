/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.PortalContext;
import jakarta.portlet.ResourceURL;

/**
 * @author Dante Wang
 */
public class MockResourceURL extends MockPortletURL implements ResourceURL {

	public MockResourceURL(PortalContext portalContext, String urlType) {
		super(portalContext, urlType);
	}

	@Override
	public String getCacheability() {
		return _cacheability;
	}

	@Override
	public String getResourceID() {
		return _resourceID;
	}

	@Override
	public MutableResourceParameters getResourceParameters() {
		if (_mutableResourceParameters == null) {
			_mutableResourceParameters = new MockMutableResourceParameters();
		}

		return _mutableResourceParameters;
	}

	@Override
	public void setCacheability(String cacheability) {
		_cacheability = cacheability;
	}

	@Override
	public void setResourceID(String resourceID) {
		_resourceID = resourceID;
	}

	public static class MockMutableResourceParameters
		extends MockMutablePortletParameters
		implements MutableResourceParameters {

		@Override
		public MutableResourceParameters clone() {
			return null;
		}

	}

	private String _cacheability;
	private MutableResourceParameters _mutableResourceParameters;
	private String _resourceID;

}