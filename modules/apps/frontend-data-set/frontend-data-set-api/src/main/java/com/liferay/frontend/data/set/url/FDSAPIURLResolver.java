/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.url;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Gianmarco Brunialti Masera
 */
public interface FDSAPIURLResolver {

	public String getSchema();

	public String resolve(String baseURL, HttpServletRequest httpServletRequest)
		throws PortalException;

}