/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.impl;

import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.dynamic.data.lists.service.base.DDLRecordSetVersionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the remote service for accessing, adding, deleting, and updating
 * dynamic data list (DDL) record set version. Its methods include permission
 * checks.
 *
 * @author Leonardo Barros
 */
@Component(
	property = {
		"json.web.service.context.name=ddl",
		"json.web.service.context.path=DDLRecordSetVersion"
	},
	service = AopService.class
)
public class DDLRecordSetVersionServiceImpl
	extends DDLRecordSetVersionServiceBaseImpl {

	@Override
	public DDLRecordSetVersion getLatestRecordSetVersion(long recordSetId)
		throws PortalException {

		_ddlRecordSetModelResourcePermission.check(
			getPermissionChecker(), recordSetId, ActionKeys.VIEW);

		return ddlRecordSetVersionLocalService.getLatestRecordSetVersion(
			recordSetId);
	}

	@Override
	public DDLRecordSetVersion getRecordSetVersion(long recordSetVersionId)
		throws PortalException {

		DDLRecordSetVersion recordSetVersion =
			ddlRecordSetVersionPersistence.findByPrimaryKey(recordSetVersionId);

		_ddlRecordSetModelResourcePermission.check(
			getPermissionChecker(), recordSetVersion.getRecordSetId(),
			ActionKeys.VIEW);

		return recordSetVersion;
	}

	@Override
	public List<DDLRecordSetVersion> getRecordSetVersions(
			long recordSetId, int start, int end,
			OrderByComparator<DDLRecordSetVersion> orderByComparator)
		throws PortalException {

		_ddlRecordSetModelResourcePermission.check(
			getPermissionChecker(), recordSetId, ActionKeys.VIEW);

		return ddlRecordSetVersionPersistence.findByRecordSetId(
			recordSetId, start, end, orderByComparator);
	}

	@Override
	public int getRecordSetVersionsCount(long recordSetId)
		throws PortalException {

		_ddlRecordSetModelResourcePermission.check(
			getPermissionChecker(), recordSetId, ActionKeys.VIEW);

		return ddlRecordSetVersionPersistence.countByRecordSetId(recordSetId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.lists.model.DDLRecordSet)"
	)
	private ModelResourcePermission<DDLRecordSet>
		_ddlRecordSetModelResourcePermission;

}