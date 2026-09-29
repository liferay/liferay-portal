/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.builder;

import com.liferay.object.constants.ObjectFieldConstants;

/**
 * @author Kevin Tan
 */
public class LocationObjectFieldBuilder extends ObjectFieldBuilder {

	public LocationObjectFieldBuilder() {
		objectField.setBusinessType(
			ObjectFieldConstants.BUSINESS_TYPE_LOCATION);
		objectField.setDBType(ObjectFieldConstants.DB_TYPE_CLOB);
	}

}