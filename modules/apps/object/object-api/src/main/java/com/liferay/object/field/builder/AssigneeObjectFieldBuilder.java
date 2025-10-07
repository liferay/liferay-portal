/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.field.builder;

import com.liferay.object.constants.ObjectFieldConstants;

/**
 * @author Carolina Barbosa
 */
public class AssigneeObjectFieldBuilder extends ObjectFieldBuilder {

	public AssigneeObjectFieldBuilder() {
		objectField.setBusinessType(
			ObjectFieldConstants.BUSINESS_TYPE_ASSIGNEE);
		objectField.setDBType(ObjectFieldConstants.DB_TYPE_LONG);
	}

}