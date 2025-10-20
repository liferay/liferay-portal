/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.internal.security.permission.wrapper;

import com.liferay.on.demand.admin.manager.OnDemandAdminManager;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapperFactory;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "service.ranking:Integer=100",
	service = PermissionCheckerWrapperFactory.class
)
public class PermissionCheckerWrapperFactoryImpl
	implements PermissionCheckerWrapperFactory {

	@Override
	public PermissionChecker wrapPermissionChecker(
		PermissionChecker permissionChecker) {

		return new OnDemandAdminPermissionCheckerWrapper(
			permissionChecker, _onDemandAdminManager, _userLocalService);
	}

	@Reference
	private OnDemandAdminManager _onDemandAdminManager;

	@Reference
	private UserLocalService _userLocalService;

}