/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryModelImpl;

import java.util.Objects;

/**
 * The arguments resolver class for retrieving value from ERCVersionedEntry.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryImpl",
		"table.name=ERCVersionedEntry"
	},
	service = ArgumentsResolver.class
)
public class ERCVersionedEntryModelArgumentsResolver
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

		ERCVersionedEntryModelImpl ercVersionedEntryModelImpl =
			(ERCVersionedEntryModelImpl)baseModel;

		if (!checkColumn ||
			_hasModifiedColumns(ercVersionedEntryModelImpl, columnNames)) {

			return _getValue(ercVersionedEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return ERCVersionedEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return ERCVersionedEntryTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		ERCVersionedEntryModelImpl ercVersionedEntryModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = ercVersionedEntryModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = ercVersionedEntryModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static boolean _hasModifiedColumns(
		ERCVersionedEntryModelImpl ercVersionedEntryModelImpl,
		String[] columnNames) {

		if (columnNames.length == 0) {
			return false;
		}

		for (String columnName : columnNames) {
			if (!Objects.equals(
					ercVersionedEntryModelImpl.getColumnOriginalValue(
						columnName),
					ercVersionedEntryModelImpl.getColumnValue(columnName))) {

				return true;
			}
		}

		return false;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1792999623