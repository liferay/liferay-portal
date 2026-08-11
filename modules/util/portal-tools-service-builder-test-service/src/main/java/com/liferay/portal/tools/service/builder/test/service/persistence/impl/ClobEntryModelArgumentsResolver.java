/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.tools.service.builder.test.model.ClobEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.ClobEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.ClobEntryModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The arguments resolver class for retrieving value from ClobEntry.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.tools.service.builder.test.model.impl.ClobEntryImpl",
		"table.name=ClobEntry"
	},
	service = ArgumentsResolver.class
)
public class ClobEntryModelArgumentsResolver implements ArgumentsResolver {

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

		ClobEntryModelImpl clobEntryModelImpl = (ClobEntryModelImpl)baseModel;

		long columnBitmask = clobEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(clobEntryModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |= clobEntryModelImpl.getColumnBitmask(
					columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(clobEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return ClobEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return ClobEntryTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		ClobEntryModelImpl clobEntryModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = clobEntryModelImpl.getColumnOriginalValue(columnName);
			}
			else {
				value = clobEntryModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:-45075531