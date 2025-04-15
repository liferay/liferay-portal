/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.segments.constants.SegmentsPortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"panel.app.order:Integer=300",
		"panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_MEMBERS
	},
	service = PanelApp.class
)
public class SegmentsPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return SegmentsPortletKeys.SEGMENTS;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (group.isLayoutSetPrototype() || group.isUser()) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS + ")"
	)
	private Portlet _portlet;

}