/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.persistence.impl;

import com.liferay.exportimport.report.model.ExportImportReportEntryTable;
import com.liferay.exportimport.report.model.impl.ExportImportReportEntryImpl;
import com.liferay.exportimport.report.model.impl.ExportImportReportEntryModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from ExportImportReportEntry.
 *
 * @author Carlos Correa
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.exportimport.report.model.impl.ExportImportReportEntryImpl",
		"table.name=ExportImportReportEntry"
	},
	service = ArgumentsResolver.class
)
public class ExportImportReportEntryModelArgumentsResolver
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

		ExportImportReportEntryModelImpl exportImportReportEntryModelImpl =
			(ExportImportReportEntryModelImpl)baseModel;

		long columnBitmask =
			exportImportReportEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				exportImportReportEntryModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					exportImportReportEntryModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				exportImportReportEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return ExportImportReportEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return ExportImportReportEntryTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		ExportImportReportEntryModelImpl exportImportReportEntryModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = exportImportReportEntryModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = exportImportReportEntryModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:1358881901