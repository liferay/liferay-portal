/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.PortletMode;
import jakarta.portlet.RenderResponse;

import java.util.Collection;

/**
 * @author Dante Wang
 */
public class MockRenderResponse
	extends MockMimeResponse implements RenderResponse {

	public Collection<? extends PortletMode> getNextPossiblePortletModes() {
		return _nextPossiblePortletModes;
	}

	public String getTitle() {
		return _title;
	}

	@Override
	public void setNextPossiblePortletModes(
		Collection<? extends PortletMode> portletModes) {

		_nextPossiblePortletModes = portletModes;
	}

	@Override
	public void setTitle(String title) {
		_title = title;
	}

	private Collection<? extends PortletMode> _nextPossiblePortletModes;
	private String _title;

}