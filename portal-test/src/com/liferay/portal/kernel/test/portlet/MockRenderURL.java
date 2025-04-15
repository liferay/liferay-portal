/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortalContext;
import jakarta.portlet.RenderURL;

/**
 * @author Dante Wang
 */
public class MockRenderURL extends MockPortletURL implements RenderURL {

	public MockRenderURL(PortalContext portalContext, MimeResponse.Copy copy) {
		super(portalContext, URL_TYPE_RENDER);
	}

	@Override
	public String getFragmentIdentifier() {
		return _fragmentIdentifier;
	}

	@Override
	public void setFragmentIdentifier(String fragmentIdentifier) {
		_fragmentIdentifier = fragmentIdentifier;
	}

	private String _fragmentIdentifier;

}