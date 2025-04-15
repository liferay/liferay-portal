/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class RoleSearchTerms extends RoleDisplayTerms {

	public RoleSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		description = DAOParamUtil.getString(portletRequest, DESCRIPTION);
		name = DAOParamUtil.getString(portletRequest, NAME);
		type = ParamUtil.getInteger(portletRequest, TYPE);
	}

	public Integer[] getTypesObj() {
		if ((type == RoleConstants.TYPE_DEPOT) ||
			(type == RoleConstants.TYPE_ORGANIZATION) ||
			(type == RoleConstants.TYPE_REGULAR) ||
			(type == RoleConstants.TYPE_SITE)) {

			return new Integer[] {type};
		}

		return new Integer[0];
	}

}