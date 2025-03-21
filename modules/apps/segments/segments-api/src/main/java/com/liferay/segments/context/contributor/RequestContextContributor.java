/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.context.contributor;

import com.liferay.segments.context.Context;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Provides an interface for extending the {@link Context} with information from
 * the request.
 *
 * @author Eduardo García
 */
public interface RequestContextContributor {

	/**
	 * Contributes additional information to the context.
	 *
	 * @param context the context that segments users
	 * @param httpServletRequest the current request
	 */
	public void contribute(
		Context context, HttpServletRequest httpServletRequest);

}