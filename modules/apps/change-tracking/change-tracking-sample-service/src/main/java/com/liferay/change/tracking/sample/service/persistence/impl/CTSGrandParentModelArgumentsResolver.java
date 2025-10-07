/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.impl;

import com.liferay.change.tracking.sample.model.CTSGrandParentTable;
import com.liferay.change.tracking.sample.model.impl.CTSGrandParentImpl;
import com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from CTSGrandParent.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.change.tracking.sample.model.impl.CTSGrandParentImpl",
		"table.name=CTSGrandParent"
	},
	service = ArgumentsResolver.class
)
public class CTSGrandParentModelArgumentsResolver implements ArgumentsResolver {

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

		CTSGrandParentModelImpl ctsGrandParentModelImpl =
			(CTSGrandParentModelImpl)baseModel;

		long columnBitmask = ctsGrandParentModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(ctsGrandParentModelImpl, columnNames, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					ctsGrandParentModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(ctsGrandParentModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CTSGrandParentImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return CTSGrandParentTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		CTSGrandParentModelImpl ctsGrandParentModelImpl, String[] columnNames,
		boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] = ctsGrandParentModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				arguments[i] = ctsGrandParentModelImpl.getColumnValue(
					columnName);
			}
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}