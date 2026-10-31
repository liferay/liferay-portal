/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.base.DDMFormInstanceRecordVersionServiceBaseImpl;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceRecordVersionIdComparator;
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
		"json.web.service.context.path=DDMFormInstanceRecordVersion"
	},
	service = AopService.class
)
public class DDMFormInstanceRecordVersionServiceImpl
	extends DDMFormInstanceRecordVersionServiceBaseImpl {

	@Override
	public DDMFormInstanceRecordVersion fetchLatestFormInstanceRecordVersion(
			long userId, long formInstanceId, String formInstanceVersion,
			int status)
		throws PortalException {

		DDMFormInstanceRecordVersion latestFormInstanceRecordVersion =
			ddmFormInstanceRecordVersionPersistence.fetchByU_F_F_S_First(
				userId, formInstanceId, formInstanceVersion, status,
				DDMFormInstanceRecordVersionIdComparator.getInstance(false));

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(),
			latestFormInstanceRecordVersion.getFormInstanceRecordId(),
			ActionKeys.VIEW);

		return latestFormInstanceRecordVersion;
	}

	@Override
	public DDMFormInstanceRecordVersion getFormInstanceRecordVersion(
			long ddmFormInstanceRecordVersionId)
		throws PortalException {

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			ddmFormInstanceRecordVersionPersistence.findByPrimaryKey(
				ddmFormInstanceRecordVersionId);

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(),
			ddmFormInstanceRecordVersion.getFormInstanceRecordId(),
			ActionKeys.VIEW);

		return ddmFormInstanceRecordVersion;
	}

	@Override
	public DDMFormInstanceRecordVersion getFormInstanceRecordVersion(
			long ddmFormInstanceRecordId, String version)
		throws PortalException {

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceRecordId, ActionKeys.VIEW);

		return ddmFormInstanceRecordVersionPersistence.findByF_V(
			ddmFormInstanceRecordId, version);
	}

	@Override
	public List<DDMFormInstanceRecordVersion> getFormInstanceRecordVersions(
			long ddmFormInstanceRecordId)
		throws PortalException {

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceRecordId, ActionKeys.VIEW);

		return ddmFormInstanceRecordVersionPersistence.
			findByFormInstanceRecordId(ddmFormInstanceRecordId);
	}

	@Override
	public List<DDMFormInstanceRecordVersion> getFormInstanceRecordVersions(
			long ddmFormInstanceRecordId, int start, int end,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws PortalException {

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceRecordId, ActionKeys.VIEW);

		return ddmFormInstanceRecordVersionPersistence.
			findByFormInstanceRecordId(
				ddmFormInstanceRecordId, start, end, orderByComparator);
	}

	@Override
	public int getFormInstanceRecordVersionsCount(long ddmFormInstanceRecordId)
		throws PortalException {

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceRecordId, ActionKeys.VIEW);

		return ddmFormInstanceRecordVersionPersistence.
			countByFormInstanceRecordId(ddmFormInstanceRecordId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord)"
	)
	private ModelResourcePermission<DDMFormInstanceRecord>
		_ddmFormInstanceRecordModelResourcePermission;

}