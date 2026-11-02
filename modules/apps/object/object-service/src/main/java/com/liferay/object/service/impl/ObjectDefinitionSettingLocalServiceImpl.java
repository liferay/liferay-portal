/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectDefinitionSettingTable;
import com.liferay.object.service.base.ObjectDefinitionSettingLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

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
	public void deleteObjectDefinitionSettingByObjectDefinitionId(
			long objectDefinitionId)
		throws PortalException {

		objectDefinitionSettingPersistence.removeByObjectDefinitionId(
			objectDefinitionId);
	}

	@Override
	public ObjectDefinitionSetting fetchObjectDefinitionSetting(
		long objectDefinitionId, String name) {

		return objectDefinitionSettingPersistence.fetchByODI_N(
			objectDefinitionId, name);
	}

	@Override
	public ObjectDefinitionSetting fetchObjectDefinitionSetting(
		long companyId, String name, String value) {

		return objectDefinitionSettingPersistence.fetchByC_N_V_First(
			companyId, name, value, null);
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

	@Override
	public Map<Long, long[]> loadGetRootObjectDefinitionIdsMap(long companyId) {
		List<Object[]> results = objectDefinitionSettingPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				ObjectDefinitionSettingTable.INSTANCE.objectDefinitionId,
				ObjectDefinitionSettingTable.INSTANCE.value
			).from(
				ObjectDefinitionSettingTable.INSTANCE
			).where(
				ObjectDefinitionSettingTable.INSTANCE.companyId.eq(
					companyId
				).and(
					ObjectDefinitionSettingTable.INSTANCE.name.eq(
						ObjectDefinitionSettingConstants.
							NAME_ROOT_OBJECT_DEFINITION_IDS)
				)
			),
			false);

		Map<Long, long[]> rootObjectDefinitionIdsMap = new HashMap<>();

		for (Object[] result : results) {
			long[] rootObjectDefinitionIds = StringUtil.split(
				(String)result[1], 0L);

			if (ArrayUtil.isEmpty(rootObjectDefinitionIds)) {
				continue;
			}

			rootObjectDefinitionIdsMap.put(
				(Long)result[0], rootObjectDefinitionIds);
		}

		return rootObjectDefinitionIdsMap;
	}

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private UserLocalService _userLocalService;

}