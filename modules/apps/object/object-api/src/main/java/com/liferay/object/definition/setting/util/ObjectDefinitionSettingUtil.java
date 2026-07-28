/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.setting.util;

import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pedro Tavares
 */
public class ObjectDefinitionSettingUtil {

	public static String getValue(
		String name, List<ObjectDefinitionSetting> objectDefinitionSettings) {

		for (ObjectDefinitionSetting objectDefinitionSetting :
				objectDefinitionSettings) {

			if (Objects.equals(objectDefinitionSetting.getName(), name)) {
				return objectDefinitionSetting.getValue();
			}
		}

		return null;
	}

	public static boolean isSitemapable(
		ObjectDefinition objectDefinition,
		Map<Long, ObjectDefinitionSetting> objectDefinitionSettingsMap) {

		if (!objectDefinition.isSystem()) {
			return true;
		}

		if (objectDefinition.isUnmodifiableSystemObject()) {
			return false;
		}

		ObjectDefinitionSetting objectDefinitionSetting =
			objectDefinitionSettingsMap.get(
				objectDefinition.getObjectDefinitionId());

		if ((objectDefinitionSetting == null) ||
			!Objects.equals(
				objectDefinitionSetting.getName(),
				ObjectDefinitionSettingConstants.NAME_SITEMAPABLE)) {

			return false;
		}

		return GetterUtil.getBoolean(objectDefinitionSetting.getValue());
	}

}