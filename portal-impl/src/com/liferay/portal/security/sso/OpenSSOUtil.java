/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.sso.OpenSSO;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Map;

/**
 * <p>
 * See https://issues.liferay.com/browse/LEP-5943.
 * </p>
 *
 * @author Prashant Dighe
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
public class OpenSSOUtil {

	public static Map<String, String> getAttributes(
		HttpServletRequest httpServletRequest, String serviceUrl) {

		OpenSSO openSSO = _openSSOSnapshot.get();

		return openSSO.getAttributes(httpServletRequest, serviceUrl);
	}

	public static String getSubjectId(
		HttpServletRequest httpServletRequest, String serviceUrl) {

		OpenSSO openSSO = _openSSOSnapshot.get();

		return openSSO.getSubjectId(httpServletRequest, serviceUrl);
	}

	public static boolean isAuthenticated(
			HttpServletRequest httpServletRequest, String serviceUrl)
		throws IOException {

		OpenSSO openSSO = _openSSOSnapshot.get();

		return openSSO.isAuthenticated(httpServletRequest, serviceUrl);
	}

	public static boolean isValidServiceUrl(String serviceUrl) {
		OpenSSO openSSO = _openSSOSnapshot.get();

		return openSSO.isValidServiceUrl(serviceUrl);
	}

	public static boolean isValidUrl(String url) {
		OpenSSO openSSO = _openSSOSnapshot.get();

		return openSSO.isValidUrl(url);
	}

	public static boolean isValidUrls(String[] urls) {
		OpenSSO openSSO = _openSSOSnapshot.get();

		return openSSO.isValidUrls(urls);
	}

	private OpenSSOUtil() {
	}

	private static final Snapshot<OpenSSO> _openSSOSnapshot = new Snapshot<>(
		OpenSSOUtil.class, OpenSSO.class);

}