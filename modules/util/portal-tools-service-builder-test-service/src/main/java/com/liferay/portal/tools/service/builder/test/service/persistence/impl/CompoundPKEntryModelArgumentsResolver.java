/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The arguments resolver class for retrieving value from CompoundPKEntry.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryImpl",
		"table.name=CompoundPKEntry"
	},
	service = ArgumentsResolver.class
)
public class CompoundPKEntryModelArgumentsResolver
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

		CompoundPKEntryModelImpl compoundPKEntryModelImpl =
			(CompoundPKEntryModelImpl)baseModel;

		long columnBitmask = compoundPKEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(compoundPKEntryModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					compoundPKEntryModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(compoundPKEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CompoundPKEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return CompoundPKEntryTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		CompoundPKEntryModelImpl compoundPKEntryModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = compoundPKEntryModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = compoundPKEntryModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:-1410951859