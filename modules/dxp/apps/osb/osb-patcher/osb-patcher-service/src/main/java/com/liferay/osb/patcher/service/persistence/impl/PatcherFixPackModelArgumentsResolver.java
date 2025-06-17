/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.model.PatcherFixPackTable;
import com.liferay.osb.patcher.model.impl.PatcherFixPackImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from PatcherFixPack.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.osb.patcher.model.impl.PatcherFixPackImpl",
		"table.name=OSBPatcher_PatcherFixPack"
	},
	service = ArgumentsResolver.class
)
public class PatcherFixPackModelArgumentsResolver implements ArgumentsResolver {

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

		PatcherFixPackModelImpl patcherFixPackModelImpl =
			(PatcherFixPackModelImpl)baseModel;

		long columnBitmask = patcherFixPackModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(patcherFixPackModelImpl, columnNames, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					patcherFixPackModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(patcherFixPackModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return PatcherFixPackImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return PatcherFixPackTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		PatcherFixPackModelImpl patcherFixPackModelImpl, String[] columnNames,
		boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] = patcherFixPackModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				arguments[i] = patcherFixPackModelImpl.getColumnValue(
					columnName);
			}
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}