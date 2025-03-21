/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.methods;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.webdav.methods.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Alexander Chow
 */
public class UnlockMethodImpl implements Method {

	@Override
	public int process(WebDAVRequest webDAVRequest) throws WebDAVException {
		WebDAVStorage storage = webDAVRequest.getWebDAVStorage();

		if (!storage.isSupportsClassTwo()) {
			return HttpServletResponse.SC_METHOD_NOT_ALLOWED;
		}

		if (storage.unlockResource(
				webDAVRequest,
				getToken(webDAVRequest.getHttpServletRequest()))) {

			return HttpServletResponse.SC_NO_CONTENT;
		}

		return HttpServletResponse.SC_PRECONDITION_FAILED;
	}

	protected String getToken(HttpServletRequest httpServletRequest) {
		String token = StringPool.BLANK;

		String value = GetterUtil.getString(
			httpServletRequest.getHeader("Lock-Token"));

		if (_log.isDebugEnabled()) {
			_log.debug("\"Lock-Token\" header is " + value);
		}

		if (value.startsWith("<") && value.endsWith(">")) {
			value = value.substring(1, value.length() - 1);
		}

		int index = value.indexOf(WebDAVUtil.TOKEN_PREFIX);

		if (index >= 0) {
			index += WebDAVUtil.TOKEN_PREFIX.length();

			if (index < value.length()) {
				token = GetterUtil.getString(value.substring(index));
			}
		}

		return token;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UnlockMethodImpl.class);

}