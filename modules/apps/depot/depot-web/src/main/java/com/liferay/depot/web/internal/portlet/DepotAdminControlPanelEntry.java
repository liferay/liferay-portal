/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet;

import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
	service = ControlPanelEntry.class
)
public class DepotAdminControlPanelEntry extends BaseControlPanelEntry {
}