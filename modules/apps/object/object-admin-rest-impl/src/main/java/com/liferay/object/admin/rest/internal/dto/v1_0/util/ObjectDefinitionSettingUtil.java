/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.util;

import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.petra.function.transform.TransformUtil;

import java.util.List;

/**
 * @author Pedro Tavares
 */
public class ObjectDefinitionSettingUtil {

	public static List<com.liferay.object.model.ObjectDefinitionSetting>
		toObjectDefinitionSettings(
			ObjectDefinitionSetting[] objectDefinitionSettings,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService) {

		return TransformUtil.transformToList(
			objectDefinitionSettings,
			objectDefinitionSetting -> {
				com.liferay.object.model.ObjectDefinitionSetting
					serviceBuilderObjectDefinitionSetting =
						objectDefinitionSettingLocalService.
							createObjectDefinitionSetting(0L);

				serviceBuilderObjectDefinitionSetting.setName(
					objectDefinitionSetting.getName());
				serviceBuilderObjectDefinitionSetting.setValue(
					String.valueOf(objectDefinitionSetting.getValue()));

				return serviceBuilderObjectDefinitionSetting;
			});
	}

}