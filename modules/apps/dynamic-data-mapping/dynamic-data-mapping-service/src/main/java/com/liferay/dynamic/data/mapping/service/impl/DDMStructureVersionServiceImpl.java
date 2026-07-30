/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.base.DDMStructureVersionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pablo Carvalho
 */
@Component(
	property = {
		"json.web.service.context.name=ddm",
		"json.web.service.context.path=DDMStructureVersion"
	},
	service = AopService.class
)
public class DDMStructureVersionServiceImpl
	extends DDMStructureVersionServiceBaseImpl {

	@Override
	public DDMStructureVersion getLatestStructureVersion(long structureId)
		throws PortalException {

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structureId, ActionKeys.VIEW);

		return ddmStructureVersionLocalService.getLatestStructureVersion(
			structureId);
	}

	@Override
	public DDMStructureVersion getStructureVersion(long structureVersionId)
		throws PortalException {

		DDMStructureVersion structureVersion =
			ddmStructureVersionPersistence.findByPrimaryKey(structureVersionId);

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structureVersion.getStructureId(),
			ActionKeys.VIEW);

		return structureVersion;
	}

	@Override
	public List<DDMStructureVersion> getStructureVersions(
			long structureId, int start, int end,
			OrderByComparator<DDMStructureVersion> orderByComparator)
		throws PortalException {

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structureId, ActionKeys.VIEW);

		return ddmStructureVersionPersistence.findByStructureId(
			structureId, start, end, orderByComparator);
	}

	@Override
	public int getStructureVersionsCount(long structureId)
		throws PortalException {

		_ddmStructureModelResourcePermission.check(
			getPermissionChecker(), structureId, ActionKeys.VIEW);

		return ddmStructureVersionPersistence.countByStructureId(structureId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMStructure)"
	)
	private ModelResourcePermission<DDMStructure>
		_ddmStructureModelResourcePermission;

}