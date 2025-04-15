/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet;

import jakarta.servlet.ServletConfig;

import java.util.Dictionary;

/**
 * @author Dante Wang
 */
public interface HttpServletEndpoint {

	public Dictionary<String, Object> getProperties();

	public ServletConfig getServletConfig();

}