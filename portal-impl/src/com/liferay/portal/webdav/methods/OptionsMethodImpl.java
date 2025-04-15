/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.methods;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.webdav.methods.Method;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class OptionsMethodImpl implements Method {

	@Override
	public int process(WebDAVRequest webDAVRequest) {
		HttpServletResponse httpServletResponse =
			webDAVRequest.getHttpServletResponse();

		WebDAVStorage webDAVStorage = webDAVRequest.getWebDAVStorage();

		if (webDAVStorage.isSupportsClassTwo()) {
			httpServletResponse.addHeader("DAV", "1,2");
		}
		else {
			httpServletResponse.addHeader("DAV", "1");
		}

		httpServletResponse.addHeader(
			"Allow", StringUtil.merge(Method.SUPPORTED_METHOD_NAMES));
		httpServletResponse.addHeader("MS-Author-Via", "DAV");

		return HttpServletResponse.SC_OK;
	}

}