/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.product.navigation.control.menu;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.product.navigation.control.menu.BaseInfoMessageProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.InfoMessageProductNavigationControlMenuEntryTypeConstants;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.TOOLS,
		"product.navigation.control.menu.entry.order:Integer=250"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class DeprecatedInfoMessageProductNavigationControlMenuEntry
	extends BaseInfoMessageProductNavigationControlMenuEntry {

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest) {
		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(httpServletRequest), "LPD-105225")) {

			return false;
		}

		return super.isShow(httpServletRequest);
	}

	@Override
	protected String getPortletName() {
		return PortletKeys.MESSAGE_BOARDS_ADMIN;
	}

	@Override
	protected String getType() {
		return InfoMessageProductNavigationControlMenuEntryTypeConstants.
			DEPRECATED;
	}

	@Reference
	private Portal _portal;

}