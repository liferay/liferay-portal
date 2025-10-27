/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.persistence.impl;

import com.liferay.batch.engine.model.BatchEngineImportTaskTable;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskImpl;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from BatchEngineImportTask.
 *
 * @author Shuyang Zhou
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.batch.engine.model.impl.BatchEngineImportTaskImpl",
		"table.name=BatchEngineImportTask"
	},
	service = ArgumentsResolver.class
)
public class BatchEngineImportTaskModelArgumentsResolver
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

		BatchEngineImportTaskModelImpl batchEngineImportTaskModelImpl =
			(BatchEngineImportTaskModelImpl)baseModel;

		if (!checkColumn ||
			_hasModifiedColumns(batchEngineImportTaskModelImpl, columnNames)) {

			return _getValue(
				batchEngineImportTaskModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return BatchEngineImportTaskImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return BatchEngineImportTaskTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		BatchEngineImportTaskModelImpl batchEngineImportTaskModelImpl,
		String[] columnNames, boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] =
					batchEngineImportTaskModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				arguments[i] = batchEngineImportTaskModelImpl.getColumnValue(
					columnName);
			}
		}

		return arguments;
	}

	private static boolean _hasModifiedColumns(
		BatchEngineImportTaskModelImpl batchEngineImportTaskModelImpl,
		String[] columnNames) {

		if (columnNames.length == 0) {
			return false;
		}

		for (String columnName : columnNames) {
			if (!Objects.equals(
					batchEngineImportTaskModelImpl.getColumnOriginalValue(
						columnName),
					batchEngineImportTaskModelImpl.getColumnValue(
						columnName))) {

				return true;
			}
		}

		return false;
	}

}