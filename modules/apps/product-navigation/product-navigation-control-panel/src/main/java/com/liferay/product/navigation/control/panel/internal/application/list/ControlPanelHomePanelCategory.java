/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.panel.internal.application.list;

import com.liferay.application.list.BasePanelCategory;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL,
		"panel.category.order:Integer=50"
	},
	service = PanelCategory.class
)
public class ControlPanelHomePanelCategory extends BasePanelCategory {

	@Override
	public String getKey() {
		return PanelCategoryKeys.CONTROL_PANEL_HOME;
	}

	@Override
	public String getLabel(Locale locale) {
		return "Home";
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (FeatureFlagManagerUtil.isEnabled(
				permissionChecker.getCompanyId(), "LPD-36105")) {
			return true;
		}

		return false;
	}

	@Reference
	private Language _language;

}