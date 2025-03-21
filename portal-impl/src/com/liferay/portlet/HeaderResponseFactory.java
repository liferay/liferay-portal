/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.LiferayHeaderResponse;
import com.liferay.portlet.internal.HeaderRequestImpl;
import com.liferay.portlet.internal.HeaderResponseImpl;

import jakarta.portlet.HeaderRequest;
import jakarta.portlet.filter.HeaderRequestWrapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Neil Griffin
 */
public class HeaderResponseFactory {

	public static LiferayHeaderResponse create(
		HeaderRequest headerRequest, HttpServletResponse httpServletResponse) {

		while (headerRequest instanceof HeaderRequestWrapper) {
			HeaderRequestWrapper headerRequestWrapper =
				(HeaderRequestWrapper)headerRequest;

			headerRequest = headerRequestWrapper.getRequest();
		}

		HeaderResponseImpl headerResponseImpl = new HeaderResponseImpl();

		headerResponseImpl.init(
			(HeaderRequestImpl)headerRequest, httpServletResponse);

		return headerResponseImpl;
	}

}