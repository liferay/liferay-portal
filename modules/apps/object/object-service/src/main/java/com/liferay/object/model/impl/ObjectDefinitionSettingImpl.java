/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectDefinitionSettingConstants;

import java.util.Objects;

/**
 * @author Marco Leo
 */
public class ObjectDefinitionSettingImpl
	extends ObjectDefinitionSettingBaseImpl {

	@Override
	public boolean isReadOnly() {
		if (Objects.equals(
				getName(),
				ObjectDefinitionSettingConstants.
					NAME_ROOT_OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODES) ||
			Objects.equals(
				getName(),
				ObjectDefinitionSettingConstants.
					NAME_ROOT_OBJECT_DEFINITION_IDS)) {

			return true;
		}

		return false;
	}

}