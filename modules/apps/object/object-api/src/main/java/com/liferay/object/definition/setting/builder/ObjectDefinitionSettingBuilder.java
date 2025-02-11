/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.setting.builder;

import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionSettingLocalServiceUtil;

/**
 * @author Pedro Tavares
 */
public class ObjectDefinitionSettingBuilder {

	public ObjectDefinitionSetting build() {
		return _objectDefinitionSetting;
	}

	public ObjectDefinitionSettingBuilder name(String name) {
		_objectDefinitionSetting.setName(name);

		return this;
	}

	public ObjectDefinitionSettingBuilder value(String value) {
		_objectDefinitionSetting.setValue(value);

		return this;
	}

	private final ObjectDefinitionSetting _objectDefinitionSetting =
		ObjectDefinitionSettingLocalServiceUtil.createObjectDefinitionSetting(
			0L);

}