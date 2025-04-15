/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.health.status;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public interface CommerceHealthStatus {

	public void fixIssue(HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getDescription(Locale locale);

	public String getKey();

	public String getName(Locale locale);

	public int getType();

	public boolean isActive();

	public boolean isFixed(long companyId, long commerceChannelId)
		throws PortalException;

}