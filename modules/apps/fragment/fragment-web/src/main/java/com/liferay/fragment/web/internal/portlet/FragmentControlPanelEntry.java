/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.staging.StagingGroupHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
	service = ControlPanelEntry.class
)
public class FragmentControlPanelEntry extends BaseControlPanelEntry {

	@Override
	protected boolean hasAccessPermissionDenied(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		if (_stagingGroupHelper.isLocalLiveGroup(group) ||
			_stagingGroupHelper.isRemoteLiveGroup(group)) {

			return false;
		}

		if (group.isLayout()) {
			return true;
		}

		return super.hasAccessPermissionDenied(
			permissionChecker, group, portlet);
	}

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}