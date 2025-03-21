/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.resource.locator;

import com.liferay.portal.kernel.model.User;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Shuyang Zhou
 */
public interface ResourceLocatorFactory {

	public ResourceLocator create(
		HttpServletRequest httpServletRequest, User user);

}