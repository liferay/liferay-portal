/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.model.MBSuspiciousActivityTable;
import com.liferay.message.boards.model.impl.MBSuspiciousActivityImpl;
import com.liferay.message.boards.model.impl.MBSuspiciousActivityModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from MBSuspiciousActivity.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.message.boards.model.impl.MBSuspiciousActivityImpl",
		"table.name=MBSuspiciousActivity"
	},
	service = ArgumentsResolver.class
)
public class MBSuspiciousActivityModelArgumentsResolver
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

		MBSuspiciousActivityModelImpl mbSuspiciousActivityModelImpl =
			(MBSuspiciousActivityModelImpl)baseModel;

		long columnBitmask = mbSuspiciousActivityModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				mbSuspiciousActivityModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					mbSuspiciousActivityModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				mbSuspiciousActivityModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return MBSuspiciousActivityImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return MBSuspiciousActivityTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		MBSuspiciousActivityModelImpl mbSuspiciousActivityModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = mbSuspiciousActivityModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = mbSuspiciousActivityModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:702011172