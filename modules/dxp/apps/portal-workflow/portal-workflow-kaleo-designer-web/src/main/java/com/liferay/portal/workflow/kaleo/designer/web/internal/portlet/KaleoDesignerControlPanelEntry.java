/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.designer.web.internal.portlet;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.AdministratorControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.workflow.WorkflowControlPanelEntry;
import com.liferay.portal.workflow.kaleo.designer.web.constants.KaleoDesignerPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Inácio Nery
 */
@Component(
	property = "jakarta.portlet.name=" + KaleoDesignerPortletKeys.KALEO_DESIGNER,
	service = ControlPanelEntry.class
)
public class KaleoDesignerControlPanelEntry
	extends AdministratorControlPanelEntry {

	@Override
	public boolean hasAccessPermission(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		if (!super.hasAccessPermission(permissionChecker, group, portlet)) {
			return false;
		}

		return _workflowControlPanelEntry.hasAccessPermission(
			permissionChecker, group, portlet);
	}

	private final WorkflowControlPanelEntry _workflowControlPanelEntry =
		new WorkflowControlPanelEntry();

}