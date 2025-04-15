/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.account.admin.web.internal.display.AccountGroupDisplay;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountGroupPermission;
import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class AccountGroupDetailsScreenNavigationEntry
	extends AccountGroupDetailsScreenNavigationCategory
	implements ScreenNavigationEntry<AccountGroupDisplay> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(
		User user, AccountGroupDisplay accountGroupDisplay) {

		if (accountGroupDisplay.getAccountGroupId() ==
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) {

			return PortalPermissionUtil.contains(
				PermissionCheckerFactoryUtil.create(user),
				AccountActionKeys.ADD_ACCOUNT_GROUP);
		}

		return AccountGroupPermission.contains(
			PermissionCheckerFactoryUtil.create(user),
			accountGroupDisplay.getAccountGroupId(), ActionKeys.UPDATE);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse,
			"/account_groups_admin/account_group/details.jsp");
	}

	@Reference
	protected JSPRenderer jspRenderer;

}