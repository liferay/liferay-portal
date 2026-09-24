/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.persistence.impl;

import com.liferay.batch.engine.model.BatchEngineImportTaskErrorTable;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskErrorImpl;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskErrorModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from BatchEngineImportTaskError.
 *
 * @author Shuyang Zhou
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.batch.engine.model.impl.BatchEngineImportTaskErrorImpl",
		"table.name=BatchEngineImportTaskError"
	},
	service = ArgumentsResolver.class
)
public class BatchEngineImportTaskErrorModelArgumentsResolver
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

		BatchEngineImportTaskErrorModelImpl
			batchEngineImportTaskErrorModelImpl =
				(BatchEngineImportTaskErrorModelImpl)baseModel;

		long columnBitmask =
			batchEngineImportTaskErrorModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				batchEngineImportTaskErrorModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					batchEngineImportTaskErrorModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				batchEngineImportTaskErrorModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return BatchEngineImportTaskErrorImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return BatchEngineImportTaskErrorTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		BatchEngineImportTaskErrorModelImpl batchEngineImportTaskErrorModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					batchEngineImportTaskErrorModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				value = batchEngineImportTaskErrorModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:-853728374