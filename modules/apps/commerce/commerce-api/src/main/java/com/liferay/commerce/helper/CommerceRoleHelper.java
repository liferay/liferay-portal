/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.helper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public interface CommerceRoleHelper {

	public void checkCommerceAccountRoles(ServiceContext serviceContext)
		throws PortalException;

	public void checkCommerceUserRoles(ServiceContext serviceContext)
		throws PortalException;

	public boolean hasCommerceUserPermissions(long companyId)
		throws PortalException;

}