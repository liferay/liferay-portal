/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.model.PatcherFixComponentTable;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from PatcherFixComponent.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.osb.patcher.model.impl.PatcherFixComponentImpl",
		"table.name=OSBPatcher_PatcherFixComponent"
	},
	service = ArgumentsResolver.class
)
public class PatcherFixComponentModelArgumentsResolver
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

		PatcherFixComponentModelImpl patcherFixComponentModelImpl =
			(PatcherFixComponentModelImpl)baseModel;

		long columnBitmask = patcherFixComponentModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				patcherFixComponentModelImpl, columnNames, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					patcherFixComponentModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				patcherFixComponentModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return PatcherFixComponentImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return PatcherFixComponentTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		PatcherFixComponentModelImpl patcherFixComponentModelImpl,
		String[] columnNames, boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] =
					patcherFixComponentModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				arguments[i] = patcherFixComponentModelImpl.getColumnValue(
					columnName);
			}
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}