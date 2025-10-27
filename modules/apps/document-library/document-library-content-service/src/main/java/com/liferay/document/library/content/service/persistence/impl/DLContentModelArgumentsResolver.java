/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.content.service.persistence.impl;

import com.liferay.document.library.content.model.DLContentTable;
import com.liferay.document.library.content.model.impl.DLContentImpl;
import com.liferay.document.library.content.model.impl.DLContentModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from DLContent.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.document.library.content.model.impl.DLContentImpl",
		"table.name=DLContent"
	},
	service = ArgumentsResolver.class
)
public class DLContentModelArgumentsResolver implements ArgumentsResolver {

	@Override
	public Object[] getArguments(
		FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		if ((columnNames == null) || (columnNames.length == 0)) {
			if (baseModel.isNew()) {
				return new Object[0];
			}

			return null;
		}

		DLContentModelImpl dlContentModelImpl = (DLContentModelImpl)baseModel;

		if (!checkColumn ||
			_hasModifiedColumns(dlContentModelImpl, columnNames) ||
			_hasModifiedColumns(dlContentModelImpl, _ORDER_BY_COLUMNS)) {

			return _getValue(dlContentModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return DLContentImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return DLContentTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		DLContentModelImpl dlContentModelImpl, String[] columnNames,
		boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] = dlContentModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				arguments[i] = dlContentModelImpl.getColumnValue(columnName);
			}
		}

		return arguments;
	}

	private static boolean _hasModifiedColumns(
		DLContentModelImpl dlContentModelImpl, String[] columnNames) {

		if (columnNames.length == 0) {
			return false;
		}

		for (String columnName : columnNames) {
			if (!Objects.equals(
					dlContentModelImpl.getColumnOriginalValue(columnName),
					dlContentModelImpl.getColumnValue(columnName))) {

				return true;
			}
		}

		return false;
	}

	private static final String[] _ORDER_BY_COLUMNS;

	static {
		List<String> orderByColumns = new ArrayList<String>();

		orderByColumns.add("version");

		_ORDER_BY_COLUMNS = orderByColumns.toArray(new String[0]);
	}

}