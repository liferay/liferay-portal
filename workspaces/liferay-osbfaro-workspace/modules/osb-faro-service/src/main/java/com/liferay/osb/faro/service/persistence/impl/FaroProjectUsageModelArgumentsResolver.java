/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.model.FaroProjectUsageTable;
import com.liferay.osb.faro.model.impl.FaroProjectUsageImpl;
import com.liferay.osb.faro.model.impl.FaroProjectUsageModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from FaroProjectUsage.
 *
 * @author Matthew Kong
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.osb.faro.model.impl.FaroProjectUsageImpl",
		"table.name=OSBFaro_FaroProjectUsage"
	},
	service = ArgumentsResolver.class
)
public class FaroProjectUsageModelArgumentsResolver
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

		FaroProjectUsageModelImpl faroProjectUsageModelImpl =
			(FaroProjectUsageModelImpl)baseModel;

		long columnBitmask = faroProjectUsageModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(faroProjectUsageModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					faroProjectUsageModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(faroProjectUsageModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return FaroProjectUsageImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return FaroProjectUsageTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		FaroProjectUsageModelImpl faroProjectUsageModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = faroProjectUsageModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = faroProjectUsageModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:562459504