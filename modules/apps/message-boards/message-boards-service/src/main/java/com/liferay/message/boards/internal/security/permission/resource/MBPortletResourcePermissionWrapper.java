/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.security.permission.resource;

import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.portal.kernel.security.permission.resource.BasePortletResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.StagedPortletPermissionLogic;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = "resource.name=" + MBConstants.RESOURCE_NAME,
	service = PortletResourcePermission.class
)
public class MBPortletResourcePermissionWrapper
	extends BasePortletResourcePermissionWrapper {

	@Override
	protected PortletResourcePermission doGetPortletResourcePermission() {
		return PortletResourcePermissionFactory.create(
			MBConstants.RESOURCE_NAME,
			new MBPortletResourcePermissionLogic(_mbBanLocalService),
			new StagedPortletPermissionLogic(
				_stagingPermission, MBPortletKeys.MESSAGE_BOARDS_ADMIN));
	}

	@Reference
	private MBBanLocalService _mbBanLocalService;

	@Reference
	private StagingPermission _stagingPermission;

}