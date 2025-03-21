/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.application.list;

import com.liferay.account.constants.AccountPanelCategoryKeys;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Albert Lee
 */
@Component(
	property = {
		"panel.app.order:Integer=200",
		"panel.category.key=" + AccountPanelCategoryKeys.CONTROL_PANEL_ACCOUNT_ENTRIES_ADMIN
	},
	service = PanelApp.class
)
public class AccountUsersAdminPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return AccountPortletKeys.ACCOUNT_USERS_ADMIN;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_USERS_ADMIN + ")"
	)
	private Portlet _portlet;

}