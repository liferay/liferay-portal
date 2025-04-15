/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.staging.StagingGroupHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"panel.app.order:Integer=300",
		"panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_DESIGN
	},
	service = PanelApp.class
)
public class FragmentPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return FragmentPortletKeys.FRAGMENT;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (_stagingGroupHelper.isLocalLiveGroup(group) ||
			_stagingGroupHelper.isRemoteLiveGroup(group)) {

			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT + ")"
	)
	private Portlet _portlet;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}