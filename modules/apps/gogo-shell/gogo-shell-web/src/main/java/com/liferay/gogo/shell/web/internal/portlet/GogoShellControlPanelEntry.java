/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gogo.shell.web.internal.portlet;

import com.liferay.gogo.shell.web.internal.constants.GogoShellPortletKeys;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.OmniadminControlPanelEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "jakarta.portlet.name=" + GogoShellPortletKeys.GOGO_SHELL,
	service = ControlPanelEntry.class
)
public class GogoShellControlPanelEntry extends OmniadminControlPanelEntry {
}