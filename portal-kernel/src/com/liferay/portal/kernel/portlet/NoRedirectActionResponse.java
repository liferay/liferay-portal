/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.ActionResponse;
import jakarta.portlet.filter.ActionResponseWrapper;

/**
 * @author Brian Wing Shun Chan
 */
public class NoRedirectActionResponse extends ActionResponseWrapper {

	public NoRedirectActionResponse(ActionResponse actionResponse) {
		super(actionResponse);
	}

	public String getRedirectLocation() {
		return _redirectLocation;
	}

	@Override
	public void sendRedirect(String location) {

		// Disable send redirect

		_redirectLocation = location;
	}

	private String _redirectLocation;

}