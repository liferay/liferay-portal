/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.impl;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.base.CSDiagramSettingServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CSDiagramSetting"
	},
	service = AopService.class
)
public class CSDiagramSettingServiceImpl
	extends CSDiagramSettingServiceBaseImpl {

	@Override
	public CSDiagramSetting addCSDiagramSetting(
			long cpDefinitionId, long cpAttachmentFileEntryId, String color,
			double radius, String type)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramSettingLocalService.addCSDiagramSetting(
			getUserId(), cpDefinitionId, cpAttachmentFileEntryId, color, radius,
			type);
	}

	@Override
	public CSDiagramSetting fetchCSDiagramSettingByCPDefinitionId(
			long cpDefinitionId)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramSettingLocalService.
			fetchCSDiagramSettingByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public CSDiagramSetting getCSDiagramSetting(long csDiagramSettingId)
		throws PortalException {

		CSDiagramSetting csDiagramSetting =
			csDiagramSettingPersistence.findByPrimaryKey(csDiagramSettingId);

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), csDiagramSetting.getCPDefinitionId(),
			ActionKeys.UPDATE);

		return csDiagramSetting;
	}

	@Override
	public CSDiagramSetting getCSDiagramSettingByCPDefinitionId(
			long cpDefinitionId)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramSettingPersistence.findByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public CSDiagramSetting updateCSDiagramSetting(
			long csDiagramSettingId, long cpAttachmentFileEntryId, String color,
			double radius, String type)
		throws PortalException {

		CSDiagramSetting csDiagramSetting =
			csDiagramSettingPersistence.findByPrimaryKey(csDiagramSettingId);

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), csDiagramSetting.getCPDefinitionId(),
			ActionKeys.UPDATE);

		return csDiagramSettingLocalService.updateCSDiagramSetting(
			csDiagramSetting.getCSDiagramSettingId(), cpAttachmentFileEntryId,
			color, radius, type);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPDefinition)"
	)
	private ModelResourcePermission<CPDefinition>
		_cpDefinitionModelResourcePermission;

}