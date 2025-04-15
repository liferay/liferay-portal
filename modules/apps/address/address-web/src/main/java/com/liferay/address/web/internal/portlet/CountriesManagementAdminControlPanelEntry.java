/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.web.internal.portlet;

import com.liferay.address.web.internal.constants.AddressPortletKeys;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "jakarta.portlet.name=" + AddressPortletKeys.COUNTRIES_MANAGEMENT_ADMIN,
	service = ControlPanelEntry.class
)
public class CountriesManagementAdminControlPanelEntry
	extends BaseControlPanelEntry {
}