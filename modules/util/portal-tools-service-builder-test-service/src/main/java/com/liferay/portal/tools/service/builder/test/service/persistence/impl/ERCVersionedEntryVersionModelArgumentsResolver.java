/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntryVersionTable;
import com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryVersionImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryVersionModelImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The arguments resolver class for retrieving value from ERCVersionedEntryVersion.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryVersionImpl",
		"table.name=ERCVersionedEntryVersion"
	},
	service = ArgumentsResolver.class
)
public class ERCVersionedEntryVersionModelArgumentsResolver
	implements ArgumentsResolver {

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

		ERCVersionedEntryVersionModelImpl ercVersionedEntryVersionModelImpl =
			(ERCVersionedEntryVersionModelImpl)baseModel;

		if (!checkColumn ||
			_hasModifiedColumns(
				ercVersionedEntryVersionModelImpl, columnNames) ||
			_hasModifiedColumns(
				ercVersionedEntryVersionModelImpl, _ORDER_BY_COLUMNS)) {

			return _getValue(
				ercVersionedEntryVersionModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return ERCVersionedEntryVersionImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return ERCVersionedEntryVersionTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		ERCVersionedEntryVersionModelImpl ercVersionedEntryVersionModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					ercVersionedEntryVersionModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				value = ercVersionedEntryVersionModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static boolean _hasModifiedColumns(
		ERCVersionedEntryVersionModelImpl ercVersionedEntryVersionModelImpl,
		String[] columnNames) {

		if (columnNames.length == 0) {
			return false;
		}

		for (String columnName : columnNames) {
			if (!Objects.equals(
					ercVersionedEntryVersionModelImpl.getColumnOriginalValue(
						columnName),
					ercVersionedEntryVersionModelImpl.getColumnValue(
						columnName))) {

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
// LIFERAY-SERVICE-BUILDER-HASH:-808016544