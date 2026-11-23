/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.util;

import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Carlos Correa
 */
public class LaunchUtil {

	public static Predicate getEqualsPredicate(
			String fieldName, long objectDefinitionId, Object value)
		throws PortalException {

		ObjectField objectField = ObjectFieldLocalServiceUtil.getObjectField(
			objectDefinitionId, fieldName);

		Table<?> table = ObjectFieldLocalServiceUtil.getTable(
			objectDefinitionId, objectField.getName());

		Column<?, Object> column = (Column<?, Object>)table.getColumn(
			objectField.getDBColumnName());

		return column.eq(value);
	}

}