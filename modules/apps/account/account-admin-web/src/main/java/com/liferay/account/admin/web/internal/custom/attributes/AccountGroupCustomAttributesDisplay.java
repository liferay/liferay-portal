/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.custom.attributes;

import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountGroup;
import com.liferay.expando.kernel.model.BaseCustomAttributesDisplay;
import com.liferay.expando.kernel.model.CustomAttributesDisplay;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(
	property = "jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_GROUPS_ADMIN,
	service = CustomAttributesDisplay.class
)
public class AccountGroupCustomAttributesDisplay
	extends BaseCustomAttributesDisplay {

	@Override
	public String getClassName() {
		return AccountGroup.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "tag";
	}

}