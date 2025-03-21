/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.search;

import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;

import jakarta.portlet.PortletResponse;

/**
 * @author Jorge Ferrer
 * @author Connor McKay
 */
public class ResourceActionRowChecker extends RowChecker {

	public ResourceActionRowChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public boolean isChecked(Object object) {
		try {
			return doIsChecked(object);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	protected boolean doIsChecked(Object object) throws Exception {
		Object[] objArray = (Object[])object;

		Role role = (Role)objArray[0];
		String actionId = (String)objArray[1];
		String resourceName = (String)objArray[2];
		Integer scope = (Integer)objArray[4];

		return ResourcePermissionLocalServiceUtil.hasScopeResourcePermission(
			role.getCompanyId(), resourceName, scope, role.getRoleId(),
			actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceActionRowChecker.class);

}