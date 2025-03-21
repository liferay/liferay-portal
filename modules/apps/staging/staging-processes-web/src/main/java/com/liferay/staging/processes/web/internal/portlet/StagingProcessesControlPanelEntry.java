/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Julio Camarero
 */
@Component(
	property = "jakarta.portlet.name=" + StagingProcessesPortletKeys.STAGING_PROCESSES,
	service = ControlPanelEntry.class
)
public class StagingProcessesControlPanelEntry extends BaseControlPanelEntry {

	@Override
	protected boolean hasAccessPermissionDenied(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		if (!PropsValues.STAGING_LIVE_GROUP_REMOTE_STAGING_ENABLED &&
			group.hasLocalOrRemoteStagingGroup()) {

			return true;
		}

		if (group.isLayoutPrototype() || group.isLayoutSetPrototype() ||
			group.isUser() || group.isUserGroup()) {

			return true;
		}

		if (!group.isStaged() && !group.hasLocalOrRemoteStagingGroup() &&
			(!GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.MANAGE_STAGING) ||
			 !GroupPermissionUtil.contains(
				 permissionChecker, group, ActionKeys.VIEW_STAGING))) {

			return true;
		}

		if (!GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.VIEW_STAGING)) {

			return true;
		}

		return super.hasAccessPermissionDenied(
			permissionChecker, group, portlet);
	}

	@Override
	protected boolean hasAccessPermissionExplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws PortalException {

		if (GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.VIEW_STAGING)) {

			return true;
		}

		return super.hasAccessPermissionExplicitlyGranted(
			permissionChecker, group, portlet);
	}

}