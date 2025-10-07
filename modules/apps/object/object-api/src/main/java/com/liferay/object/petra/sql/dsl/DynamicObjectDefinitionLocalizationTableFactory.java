/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.petra.sql.dsl;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

import java.util.List;

/**
 * @author Feliphe Marinho
 */
public class DynamicObjectDefinitionLocalizationTableFactory {

	public static DynamicObjectDefinitionLocalizationTable create(
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService) {

		List<ObjectField> localizedObjectFields = null;

		if (objectDefinition.isUnmodifiableSystemObject()) {
			localizedObjectFields =
				objectFieldLocalService.getLocalizedObjectFields(
					objectDefinition.getObjectDefinitionId(), false);

			if (localizedObjectFields.isEmpty()) {
				return null;
			}
		}
		else {
			localizedObjectFields =
				objectFieldLocalService.getLocalizedObjectFields(
					objectDefinition.getObjectDefinitionId());
		}

		if (!objectDefinition.isEnableLocalization() ||
			(FeatureFlagManagerUtil.isEnabled(
				objectDefinition.getCompanyId(), "LPD-32050") &&
			 localizedObjectFields.isEmpty())) {

			return null;
		}

		return new DynamicObjectDefinitionLocalizationTable(
			objectDefinition, localizedObjectFields);
	}

}