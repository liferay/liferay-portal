/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.base.DDMFormInstanceRecordServiceBaseImpl;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
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
		"json.web.service.context.path=DDMFormInstanceRecord"
	},
	service = AopService.class
)
public class DDMFormInstanceRecordServiceImpl
	extends DDMFormInstanceRecordServiceBaseImpl {

	@Override
	public DDMFormInstanceRecord addFormInstanceRecord(
			long groupId, long ddmFormInstanceId, DDMFormValues ddmFormValues,
			ServiceContext serviceContext)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId,
			DDMActionKeys.ADD_FORM_INSTANCE_RECORD);

		return ddmFormInstanceRecordLocalService.addFormInstanceRecord(
			getGuestOrUserId(), groupId, ddmFormInstanceId, ddmFormValues,
			serviceContext);
	}

	@Override
	public void deleteFormInstanceRecord(long ddmFormInstanceRecordId)
		throws PortalException {

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceRecordId, ActionKeys.DELETE);

		ddmFormInstanceRecordLocalService.deleteFormInstanceRecord(
			ddmFormInstanceRecordId);
	}

	@Override
	public DDMFormInstanceRecord getFormInstanceRecord(
			long ddmFormInstanceRecordId)
		throws PortalException {

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceRecordId, ActionKeys.VIEW);

		return ddmFormInstanceRecordPersistence.findByPrimaryKey(
			ddmFormInstanceRecordId);
	}

	@Override
	public List<DDMFormInstanceRecord> getFormInstanceRecords(
			long ddmFormInstanceId)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstanceRecordPersistence.findByFormInstanceId(
			ddmFormInstanceId);
	}

	@Override
	public List<DDMFormInstanceRecord> getFormInstanceRecords(
			long ddmFormInstanceId, int status, int start, int end,
			OrderByComparator<DDMFormInstanceRecord> orderByComparator)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstanceRecordLocalService.getFormInstanceRecords(
			ddmFormInstanceId, status, start, end, orderByComparator);
	}

	@Override
	public int getFormInstanceRecordsCount(long ddmFormInstanceId)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstanceRecordPersistence.countByFormInstanceId(
			ddmFormInstanceId);
	}

	@Override
	public void revertFormInstanceRecord(
			long ddmFormInstanceRecordId, String version,
			ServiceContext serviceContext)
		throws PortalException {

		DDMFormInstanceRecord ddmFormInstanceRecord =
			ddmFormInstanceRecordPersistence.findByPrimaryKey(
				ddmFormInstanceRecordId);

		_ddmFormInstanceRecordModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceRecord, ActionKeys.UPDATE);

		ddmFormInstanceRecordLocalService.revertFormInstanceRecord(
			getGuestOrUserId(), ddmFormInstanceRecordId, version,
			serviceContext);
	}

	@Override
	public BaseModelSearchResult<DDMFormInstanceRecord>
			searchFormInstanceRecords(
				long ddmFormInstanceId, String[] notEmptyFields, int status,
				int start, int end, Sort sort)
		throws PortalException {

		_ddmFormInstanceModelResourcePermission.check(
			getPermissionChecker(), ddmFormInstanceId, ActionKeys.VIEW);

		return ddmFormInstanceRecordLocalService.searchFormInstanceRecords(
			ddmFormInstanceId, notEmptyFields, status, start, end, sort);
	}

	@Override
	public DDMFormInstanceRecord updateFormInstanceRecord(
			long ddmFormInstanceRecordId, boolean majorVersion,
			DDMFormValues ddmFormValues, ServiceContext serviceContext)
		throws PortalException {

		if (!_ddmFormInstanceRecordModelResourcePermission.contains(
				getPermissionChecker(), ddmFormInstanceRecordId,
				DDMActionKeys.ADD_FORM_INSTANCE_RECORD)) {

			_ddmFormInstanceRecordModelResourcePermission.check(
				getPermissionChecker(), ddmFormInstanceRecordId,
				ActionKeys.UPDATE);
		}

		return ddmFormInstanceRecordLocalService.updateFormInstanceRecord(
			getUserId(), ddmFormInstanceRecordId, majorVersion, ddmFormValues,
			serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstance)"
	)
	private ModelResourcePermission<DDMFormInstance>
		_ddmFormInstanceModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord)"
	)
	private ModelResourcePermission<DDMFormInstanceRecord>
		_ddmFormInstanceRecordModelResourcePermission;

}