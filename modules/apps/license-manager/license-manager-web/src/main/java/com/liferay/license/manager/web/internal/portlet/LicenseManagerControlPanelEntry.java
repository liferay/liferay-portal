/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.license.manager.web.internal.portlet;

import com.liferay.license.manager.web.internal.constants.LicenseManagerPortletKeys;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.OmniadminControlPanelEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Sanz
 */
@Component(
	property = "jakarta.portlet.name=" + LicenseManagerPortletKeys.LICENSE_MANAGER,
	service = ControlPanelEntry.class
)
public class LicenseManagerControlPanelEntry
	extends OmniadminControlPanelEntry {
}