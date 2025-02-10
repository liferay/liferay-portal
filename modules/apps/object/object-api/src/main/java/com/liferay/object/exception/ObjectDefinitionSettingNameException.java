/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.exception;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Set;

/**
 * @author Pedro Tavares
 */
public class ObjectDefinitionSettingNameException extends PortalException {

	public static class NotAllowedNames
		extends ObjectDefinitionSettingNameException {

		public NotAllowedNames(
			String objectDefinitionName,
			Set<String> objectDefinitionSettingsNames) {

			super(
				String.format(
					"The settings %s are not allowed for object definition %s",
					StringUtil.merge(
						objectDefinitionSettingsNames,
						StringPool.COMMA_AND_SPACE),
					objectDefinitionName));
		}

	}

	private ObjectDefinitionSettingNameException(String msg) {
		super(msg);
	}

}