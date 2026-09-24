/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.model.CommerceOrderTable;
import com.liferay.commerce.model.impl.CommerceOrderImpl;
import com.liferay.commerce.model.impl.CommerceOrderModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from CommerceOrder.
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.commerce.model.impl.CommerceOrderImpl",
		"table.name=CommerceOrder"
	},
	service = ArgumentsResolver.class
)
public class CommerceOrderModelArgumentsResolver implements ArgumentsResolver {

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

		CommerceOrderModelImpl commerceOrderModelImpl =
			(CommerceOrderModelImpl)baseModel;

		if (!checkColumn ||
			_hasModifiedColumns(commerceOrderModelImpl, columnNames) ||
			_hasModifiedColumns(commerceOrderModelImpl, _ORDER_BY_COLUMNS)) {

			return _getValue(commerceOrderModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CommerceOrderImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return CommerceOrderTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		CommerceOrderModelImpl commerceOrderModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = commerceOrderModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = commerceOrderModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static boolean _hasModifiedColumns(
		CommerceOrderModelImpl commerceOrderModelImpl, String[] columnNames) {

		if (columnNames.length == 0) {
			return false;
		}

		for (String columnName : columnNames) {
			if (!Objects.equals(
					commerceOrderModelImpl.getColumnOriginalValue(columnName),
					commerceOrderModelImpl.getColumnValue(columnName))) {

				return true;
			}
		}

		return false;
	}

	private static final String[] _ORDER_BY_COLUMNS;

	static {
		List<String> orderByColumns = new ArrayList<String>();

		orderByColumns.add("createDate");

		_ORDER_BY_COLUMNS = orderByColumns.toArray(new String[0]);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-670516246