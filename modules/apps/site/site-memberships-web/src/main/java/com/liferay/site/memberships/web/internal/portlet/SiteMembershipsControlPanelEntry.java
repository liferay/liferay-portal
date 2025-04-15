/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.portlet;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.site.memberships.constants.SiteMembershipsPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Raymond Augé
 * @author Jorge Ferrer
 */
@Component(
	property = "jakarta.portlet.name=" + SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN,
	service = ControlPanelEntry.class
)
public class SiteMembershipsControlPanelEntry extends BaseControlPanelEntry {

	@Override
	protected boolean hasAccessPermissionDenied(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		if (group.isCompany() || group.isLayoutSetPrototype() ||
			!group.isManualMembership() || group.isUser()) {

			return true;
		}

		return super.hasAccessPermissionDenied(
			permissionChecker, group, portlet);
	}

	@Override
	protected boolean hasPermissionImplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		if (GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.ASSIGN_MEMBERS)) {

			return true;
		}

		return super.hasPermissionImplicitlyGranted(
			permissionChecker, group, portlet);
	}

}