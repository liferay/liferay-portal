/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.portlet.AdministratorControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christopher Kian
 */
@Component(
	property = "jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
	service = ControlPanelEntry.class
)
public class InstanceSettingsControlPanelEntry
	extends AdministratorControlPanelEntry {
}