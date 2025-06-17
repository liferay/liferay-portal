/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.model.PatcherFixRelTable;
import com.liferay.osb.patcher.model.impl.PatcherFixRelImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from PatcherFixRel.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.osb.patcher.model.impl.PatcherFixRelImpl",
		"table.name=OSBPatcher_PatcherFixRel"
	},
	service = ArgumentsResolver.class
)
public class PatcherFixRelModelArgumentsResolver implements ArgumentsResolver {

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

		PatcherFixRelModelImpl patcherFixRelModelImpl =
			(PatcherFixRelModelImpl)baseModel;

		long columnBitmask = patcherFixRelModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(patcherFixRelModelImpl, columnNames, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					patcherFixRelModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(patcherFixRelModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return PatcherFixRelImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return PatcherFixRelTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		PatcherFixRelModelImpl patcherFixRelModelImpl, String[] columnNames,
		boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] = patcherFixRelModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				arguments[i] = patcherFixRelModelImpl.getColumnValue(
					columnName);
			}
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}