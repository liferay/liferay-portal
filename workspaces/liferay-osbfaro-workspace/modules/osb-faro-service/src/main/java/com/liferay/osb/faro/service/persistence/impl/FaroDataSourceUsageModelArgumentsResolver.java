/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.model.FaroDataSourceUsageTable;
import com.liferay.osb.faro.model.impl.FaroDataSourceUsageImpl;
import com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from FaroDataSourceUsage.
 *
 * @author Matthew Kong
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.osb.faro.model.impl.FaroDataSourceUsageImpl",
		"table.name=OSBFaro_FaroDataSourceUsage"
	},
	service = ArgumentsResolver.class
)
public class FaroDataSourceUsageModelArgumentsResolver
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

		FaroDataSourceUsageModelImpl faroDataSourceUsageModelImpl =
			(FaroDataSourceUsageModelImpl)baseModel;

		long columnBitmask = faroDataSourceUsageModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				faroDataSourceUsageModelImpl, columnNames, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					faroDataSourceUsageModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				faroDataSourceUsageModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return FaroDataSourceUsageImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return FaroDataSourceUsageTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		FaroDataSourceUsageModelImpl faroDataSourceUsageModelImpl,
		String[] columnNames, boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] =
					faroDataSourceUsageModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				arguments[i] = faroDataSourceUsageModelImpl.getColumnValue(
					columnName);
			}
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:632881248