/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.sso;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Map;

/**
 * @author Michael C. Han
 */
public interface OpenSSO {

	public Map<String, String> getAttributes(
		HttpServletRequest httpServletRequest, String serviceUrl);

	public String getSubjectId(
		HttpServletRequest httpServletRequest, String serviceUrl);

	public boolean isAuthenticated(
			HttpServletRequest httpServletRequest, String serviceUrl)
		throws IOException;

	public boolean isValidServiceUrl(String serviceUrl);

	public boolean isValidUrl(String url);

	public boolean isValidUrls(String[] urls);

}