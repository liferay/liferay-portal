/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.field.type;

/**
 * @author Víctor Galán
 */
public class AssigneeInfoFieldType implements InfoFieldType {

	public static final AssigneeInfoFieldType INSTANCE =
		new AssigneeInfoFieldType();

	@Override
	public String getName() {
		return "assignee";
	}

	private AssigneeInfoFieldType() {
	}

}