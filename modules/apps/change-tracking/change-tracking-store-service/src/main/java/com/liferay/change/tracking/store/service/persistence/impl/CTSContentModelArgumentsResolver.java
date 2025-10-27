/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.service.persistence.impl;

import com.liferay.change.tracking.store.model.CTSContentTable;
import com.liferay.change.tracking.store.model.impl.CTSContentImpl;
import com.liferay.change.tracking.store.model.impl.CTSContentModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from CTSContent.
 *
 * @author Shuyang Zhou
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.change.tracking.store.model.impl.CTSContentImpl",
		"table.name=CTSContent"
	},
	service = ArgumentsResolver.class
)
public class CTSContentModelArgumentsResolver implements ArgumentsResolver {

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

		CTSContentModelImpl ctsContentModelImpl =
			(CTSContentModelImpl)baseModel;

		if (!checkColumn ||
			_hasModifiedColumns(ctsContentModelImpl, columnNames) ||
			_hasModifiedColumns(ctsContentModelImpl, _ORDER_BY_COLUMNS)) {

			return _getValue(ctsContentModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CTSContentImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return CTSContentTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		CTSContentModelImpl ctsContentModelImpl, String[] columnNames,
		boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] = ctsContentModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				arguments[i] = ctsContentModelImpl.getColumnValue(columnName);
			}
		}

		return arguments;
	}

	private static boolean _hasModifiedColumns(
		CTSContentModelImpl ctsContentModelImpl, String[] columnNames) {

		if (columnNames.length == 0) {
			return false;
		}

		for (String columnName : columnNames) {
			if (!Objects.equals(
					ctsContentModelImpl.getColumnOriginalValue(columnName),
					ctsContentModelImpl.getColumnValue(columnName))) {

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