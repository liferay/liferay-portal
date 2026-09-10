/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.model.FaroProjectTable;
import com.liferay.osb.faro.model.impl.FaroProjectImpl;
import com.liferay.osb.faro.model.impl.FaroProjectModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from FaroProject.
 *
 * @author Matthew Kong
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.osb.faro.model.impl.FaroProjectImpl",
		"table.name=OSBFaro_FaroProject"
	},
	service = ArgumentsResolver.class
)
public class FaroProjectModelArgumentsResolver implements ArgumentsResolver {

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

		FaroProjectModelImpl faroProjectModelImpl =
			(FaroProjectModelImpl)baseModel;

		long columnBitmask = faroProjectModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(faroProjectModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					faroProjectModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(faroProjectModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return FaroProjectImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return FaroProjectTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		FaroProjectModelImpl faroProjectModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = faroProjectModelImpl.getColumnOriginalValue(columnName);
			}
			else {
				value = faroProjectModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:564436373