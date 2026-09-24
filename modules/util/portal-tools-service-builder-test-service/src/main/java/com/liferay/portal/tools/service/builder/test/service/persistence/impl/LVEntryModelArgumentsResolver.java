/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.tools.service.builder.test.model.LVEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

/**
 * The arguments resolver class for retrieving value from LVEntry.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.tools.service.builder.test.model.impl.LVEntryImpl",
		"table.name=LVEntry"
	},
	service = ArgumentsResolver.class
)
public class LVEntryModelArgumentsResolver implements ArgumentsResolver {

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

		LVEntryModelImpl lvEntryModelImpl = (LVEntryModelImpl)baseModel;

		BiPredicate<LVEntryModelImpl, Boolean> whereBiPredicate =
			_whereBiPredicates.get(finderPath.getFinderName());

		if ((whereBiPredicate != null) &&
			!whereBiPredicate.test(lvEntryModelImpl, original)) {

			return null;
		}

		long columnBitmask = lvEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(lvEntryModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |= lvEntryModelImpl.getColumnBitmask(
					columnName);
			}

			Long whereColumnBitmask = _whereColumnBitmasks.get(
				finderPath.getFinderName());

			if (whereColumnBitmask != null) {
				finderPathColumnBitmask |= whereColumnBitmask;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(lvEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return LVEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return LVEntryTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		LVEntryModelImpl lvEntryModelImpl, String columnName,
		boolean original) {

		if (original) {
			return lvEntryModelImpl.getColumnOriginalValue(columnName);
		}

		return lvEntryModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		LVEntryModelImpl lvEntryModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = lvEntryModelImpl.getColumnOriginalValue(columnName);
			}
			else {
				value = lvEntryModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();
	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map<String, BiPredicate<LVEntryModelImpl, Boolean>>
		_whereBiPredicates = new HashMap<>();

	static {
		long whereColumnBitmask = LVEntryModelImpl.getColumnBitmask(
			"lvEntryId");

		BiPredicate<LVEntryModelImpl, Boolean> whereBiPredicate =
			(lvEntryModelImpl, original) ->
				GetterUtil.getLong(
					_getColumnValue(lvEntryModelImpl, "lvEntryId", original)) >
						0L;

		_whereColumnBitmasks.put("GroupId", whereColumnBitmask);

		_whereBiPredicates.put("GroupId", whereBiPredicate);
		_whereColumnBitmasks.put("GroupId_Head", whereColumnBitmask);

		_whereBiPredicates.put("GroupId_Head", whereBiPredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1358478935