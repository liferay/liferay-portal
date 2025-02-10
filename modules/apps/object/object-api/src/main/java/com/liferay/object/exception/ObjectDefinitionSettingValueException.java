/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Pedro Tavares
 */
public class ObjectDefinitionSettingValueException extends PortalException {

	public static class InvalidValue
		extends ObjectDefinitionSettingValueException {

		public InvalidValue(
			String objectDefinitionName, String objectDefinitionSettingName,
			String objectDefinitionSettingValue) {

			super(
				String.format(
					"The value %s of setting \"%s\" is invalid for object " +
						"definition \"%s\"",
					objectDefinitionSettingValue, objectDefinitionSettingName,
					objectDefinitionName));
		}

	}

	private ObjectDefinitionSettingValueException(String msg) {
		super(msg);
	}

}