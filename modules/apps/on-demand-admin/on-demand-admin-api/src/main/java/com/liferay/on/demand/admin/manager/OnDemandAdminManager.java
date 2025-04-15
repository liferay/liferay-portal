/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.manager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;

import jakarta.portlet.PortletRequest;

import java.util.Date;

/**
 * @author Pei-Jung Lan
 */
public interface OnDemandAdminManager {

	public void cleanUpOnDemandAdminUsers(Date olderThanDate)
		throws PortalException;

	public String getLoginURL(
			Company company, PortletRequest portletRequest, long userId)
		throws PortalException;

	public boolean isOnDemandAdminUser(User user);

}