/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.dynamic.data.mapping.service.base.DDMTemplateVersionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"json.web.service.context.name=ddm",
		"json.web.service.context.path=DDMTemplateVersion"
	},
	service = AopService.class
)
public class DDMTemplateVersionServiceImpl
	extends DDMTemplateVersionServiceBaseImpl {

	@Override
	public DDMTemplateVersion getLatestTemplateVersion(long templateId)
		throws PortalException {

		_ddmTemplateModelResourcePermission.check(
			getPermissionChecker(), templateId, ActionKeys.VIEW);

		return ddmTemplateVersionLocalService.getLatestTemplateVersion(
			templateId);
	}

	@Override
	public DDMTemplateVersion getTemplateVersion(long templateVersionId)
		throws PortalException {

		DDMTemplateVersion templateVersion =
			ddmTemplateVersionPersistence.findByPrimaryKey(templateVersionId);

		_ddmTemplateModelResourcePermission.check(
			getPermissionChecker(), templateVersion.getTemplateId(),
			ActionKeys.VIEW);

		return templateVersion;
	}

	@Override
	public List<DDMTemplateVersion> getTemplateVersions(
			long templateId, int start, int end,
			OrderByComparator<DDMTemplateVersion> orderByComparator)
		throws PortalException {

		_ddmTemplateModelResourcePermission.check(
			getPermissionChecker(), templateId, ActionKeys.VIEW);

		return ddmTemplateVersionPersistence.findByTemplateId(
			templateId, start, end, orderByComparator);
	}

	@Override
	public int getTemplateVersionsCount(long templateId)
		throws PortalException {

		_ddmTemplateModelResourcePermission.check(
			getPermissionChecker(), templateId, ActionKeys.VIEW);

		return ddmTemplateVersionPersistence.countByTemplateId(templateId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMTemplate)"
	)
	private ModelResourcePermission<DDMTemplate>
		_ddmTemplateModelResourcePermission;

}