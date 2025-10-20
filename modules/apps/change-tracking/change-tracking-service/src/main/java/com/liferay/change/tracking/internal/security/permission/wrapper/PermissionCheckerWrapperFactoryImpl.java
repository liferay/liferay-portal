/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.security.permission.wrapper;

import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapperFactory;
import com.liferay.portal.kernel.service.ClassNameLocalService;

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

		User user = permissionChecker.getUser();

		if ((user != null) && user.isOnDemandUser()) {
			return new CTOnDemandUserPermissionCheckerWrapper(
				permissionChecker, _classNameLocalService,
				_ctCollectionLocalService, _ctEntryLocalService);
		}

		return permissionChecker;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

}