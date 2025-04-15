/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.web.internal.portlet;

import com.liferay.portal.kernel.portlet.AdministratorControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.security.audit.web.internal.constants.AuditPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = "jakarta.portlet.name=" + AuditPortletKeys.AUDIT,
	service = ControlPanelEntry.class
)
public class AuditControlPanelEntry extends AdministratorControlPanelEntry {
}