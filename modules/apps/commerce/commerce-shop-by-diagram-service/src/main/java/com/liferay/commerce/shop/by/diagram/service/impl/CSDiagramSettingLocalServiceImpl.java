/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.impl;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting;
import com.liferay.commerce.shop.by.diagram.service.base.CSDiagramSettingLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.shop.by.diagram.model.CSDiagramSetting",
	service = AopService.class
)
public class CSDiagramSettingLocalServiceImpl
	extends CSDiagramSettingLocalServiceBaseImpl {

	@Override
	public CSDiagramSetting addCSDiagramSetting(
			long userId, long cpDefinitionId, long cpAttachmentFileEntryId,
			String color, double radius, String type)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long csDiagramSettingId = counterLocalService.increment();

		CSDiagramSetting csDiagramSetting = csDiagramSettingPersistence.create(
			csDiagramSettingId);

		csDiagramSetting.setCompanyId(user.getCompanyId());
		csDiagramSetting.setUserId(user.getUserId());
		csDiagramSetting.setUserName(user.getFullName());
		csDiagramSetting.setCPAttachmentFileEntryId(cpAttachmentFileEntryId);
		csDiagramSetting.setCPDefinitionId(cpDefinitionId);
		csDiagramSetting.setColor(color);
		csDiagramSetting.setRadius(radius);
		csDiagramSetting.setType(type);

		return csDiagramSettingPersistence.update(csDiagramSetting);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CSDiagramSetting deleteCSDiagramSetting(
		CSDiagramSetting csDiagramSetting) {

		return csDiagramSettingPersistence.remove(csDiagramSetting);
	}

	@Override
	public CSDiagramSetting deleteCSDiagramSetting(long csDiagramSettingId)
		throws PortalException {

		CSDiagramSetting csDiagramSetting =
			csDiagramSettingPersistence.findByPrimaryKey(csDiagramSettingId);

		return csDiagramSettingLocalService.deleteCSDiagramSetting(
			csDiagramSetting);
	}

	@Override
	public CSDiagramSetting deleteCSDiagramSettingByCPDefinitionId(
		long cpDefinitionId) {

		CSDiagramSetting csDiagramSetting =
			csDiagramSettingPersistence.fetchByCPDefinitionId(cpDefinitionId);

		if (csDiagramSetting == null) {
			return null;
		}

		return csDiagramSettingLocalService.deleteCSDiagramSetting(
			csDiagramSetting);
	}

	@Override
	public CSDiagramSetting fetchCSDiagramSettingByCPDefinitionId(
		long cpDefinitionId) {

		return csDiagramSettingPersistence.fetchByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public CSDiagramSetting getCSDiagramSettingByCPDefinitionId(
			long cpDefinitionId)
		throws PortalException {

		return csDiagramSettingPersistence.findByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public CSDiagramSetting updateCSDiagramSetting(
			long csDiagramSettingId, long cpAttachmentFileEntryId, String color,
			double radius, String type)
		throws PortalException {

		CSDiagramSetting csDiagramSetting =
			csDiagramSettingPersistence.findByPrimaryKey(csDiagramSettingId);

		csDiagramSetting.setCPAttachmentFileEntryId(cpAttachmentFileEntryId);
		csDiagramSetting.setColor(color);
		csDiagramSetting.setRadius(radius);
		csDiagramSetting.setType(type);

		return csDiagramSettingPersistence.update(csDiagramSetting);
	}

	@Reference
	private UserLocalService _userLocalService;

}