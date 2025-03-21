/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.weblogic.support.internal.include;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * @author Minhchau Dang
 */
public class WebLogicIncludeServletResponse extends HttpServletResponseWrapper {

	public WebLogicIncludeServletResponse(
		HttpServletResponse httpServletResponse) {

		super(httpServletResponse);
	}

	@Override
	public void setBufferSize(int bufferSize) {
	}

}