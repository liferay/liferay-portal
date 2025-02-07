/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.base.ObjectDefinitionSettingLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import java.util.List;

import org.osgi.service.component.annotations.Component;

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
	public List<ObjectDefinitionSetting> getObjectDefinitionSettings(
		long objectDefinitionId) {

		return objectDefinitionSettingPersistence.findByObjectDefinitionId(
			objectDefinitionId);
	}

}