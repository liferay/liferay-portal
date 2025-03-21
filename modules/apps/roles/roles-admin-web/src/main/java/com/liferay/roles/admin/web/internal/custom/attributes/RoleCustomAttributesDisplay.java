/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.custom.attributes;

import com.liferay.expando.kernel.model.BaseCustomAttributesDisplay;
import com.liferay.expando.kernel.model.CustomAttributesDisplay;
import com.liferay.portal.kernel.model.Role;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Aniceto Perez
 */
@Component(
	property = "jakarta.portlet.name=" + RolesAdminPortletKeys.ROLES_ADMIN,
	service = CustomAttributesDisplay.class
)
public class RoleCustomAttributesDisplay extends BaseCustomAttributesDisplay {

	@Override
	public String getClassName() {
		return Role.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "password-policies";
	}

}