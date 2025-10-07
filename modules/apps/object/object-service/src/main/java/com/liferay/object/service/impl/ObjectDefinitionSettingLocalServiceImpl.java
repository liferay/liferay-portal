/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.base.ObjectDefinitionSettingLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectDefinitionSetting",
	service = AopService.class
)
public class ObjectDefinitionSettingLocalServiceImpl
	extends ObjectDefinitionSettingLocalServiceBaseImpl {

	@Override
	public ObjectDefinitionSetting addObjectDefinitionSetting(
			long userId, long objectDefinitionId, String name, String value)
		throws PortalException {

		_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		ObjectDefinitionSetting objectDefinitionSetting =
			objectDefinitionSettingPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectDefinitionSetting.setCompanyId(user.getCompanyId());
		objectDefinitionSetting.setUserId(user.getUserId());
		objectDefinitionSetting.setUserName(user.getFullName());

		objectDefinitionSetting.setObjectDefinitionId(objectDefinitionId);
		objectDefinitionSetting.setName(name);
		objectDefinitionSetting.setValue(value);

		return objectDefinitionSettingPersistence.update(
			objectDefinitionSetting);
	}

	@Override
	public ObjectDefinitionSetting fetchObjectDefinitionSetting(
		long objectDefinitionId, String name) {

		return objectDefinitionSettingPersistence.fetchByODI_N(
			objectDefinitionId, name);
	}

	@Override
	public ObjectDefinitionSetting getObjectDefinitionSetting(
			long objectDefinitionId, String name)
		throws PortalException {

		return objectDefinitionSettingPersistence.findByODI_N(
			objectDefinitionId, name);
	}

	@Override
	public List<ObjectDefinitionSetting> getObjectDefinitionSettings(
		long objectDefinitionId) {

		return objectDefinitionSettingPersistence.findByObjectDefinitionId(
			objectDefinitionId);
	}

	@Override
	public Map<Long, ObjectDefinitionSetting> getObjectDefinitionSettingsMap(
		long companyId, String name) {

		Map<Long, ObjectDefinitionSetting> objectDefinitionSettingsMap =
			new HashMap<>();

		for (ObjectDefinitionSetting objectDefinitionSetting :
				objectDefinitionSettingPersistence.findByC_N(companyId, name)) {

			objectDefinitionSettingsMap.put(
				objectDefinitionSetting.getObjectDefinitionId(),
				objectDefinitionSetting);
		}

		return objectDefinitionSettingsMap;
	}

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private UserLocalService _userLocalService;

}