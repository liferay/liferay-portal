/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.service.base.DDMFormInstanceVersionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Leonardo Barros
 */
@Component(
	property = {
		"json.web.service.context.name=ddm",
		"json.web.service.context.path=DDMFormInstanceVersion"
	},
	service = AopService.class
)
public class DDMFormInstanceVersionServiceImpl
	extends DDMFormInstanceVersionServiceBaseImpl {

	@Override
	public DDMFormInstanceVersion getFormInstanceVersion(
			long ddmFormInstanceVersionId)
		throws PortalException {

		DDMFormInstanceVersion ddmFormInstanceVersion =
			ddmFormInstanceVersionPersistence.findByPrimaryKey(
				ddmFormInstanceVersionId);

		_ddmFormInstanceVersionPermissionModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceVersion.getFormInstanceId(),
			ActionKeys.VIEW);

		return ddmFormInstanceVersion;
	}

	@Override
	public List<DDMFormInstanceVersion> getFormInstanceVersions(
			long ddmFormInstanceId, int start, int end,
			OrderByComparator<DDMFormInstanceVersion> orderByComparator)
		throws PortalException {

		_ddmFormInstanceVersionPermissionModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstanceVersionPersistence.findByFormInstanceId(
			ddmFormInstanceId, start, end, orderByComparator);
	}

	@Override
	public int getFormInstanceVersionsCount(long ddmFormInstanceId)
		throws PortalException {

		_ddmFormInstanceVersionPermissionModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstanceVersionPersistence.countByFormInstanceId(
			ddmFormInstanceId);
	}

	@Override
	public DDMFormInstanceVersion getLatestFormInstanceVersion(
			long ddmFormInstanceId)
		throws PortalException {

		_ddmFormInstanceVersionPermissionModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstanceVersionLocalService.getLatestFormInstanceVersion(
			ddmFormInstanceId);
	}

	@Override
	public DDMFormInstanceVersion getLatestFormInstanceVersion(
			long ddmFormInstanceId, int status)
		throws PortalException {

		_ddmFormInstanceVersionPermissionModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstanceVersionLocalService.getLatestFormInstanceVersion(
			ddmFormInstanceId, status);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstance)"
	)
	private ModelResourcePermission<DDMFormInstance>
		_ddmFormInstanceVersionPermissionModelResourcePermission;

}