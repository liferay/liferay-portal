/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.portlet;

import com.liferay.item.selector.constants.ItemSelectorPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + ItemSelectorPortletKeys.ITEM_SELECTOR,
	service = ControlPanelEntry.class
)
public class ItemSelectorControlPanelEntry extends BaseControlPanelEntry {

	@Override
	protected boolean hasPermissionImplicitlyGranted(
		PermissionChecker permissionChecker, Group group, Portlet portlet) {

		return true;
	}

}