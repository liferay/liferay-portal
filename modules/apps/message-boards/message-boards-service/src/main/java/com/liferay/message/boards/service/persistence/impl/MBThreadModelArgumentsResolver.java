/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.model.MBThreadTable;
import com.liferay.message.boards.model.impl.MBThreadImpl;
import com.liferay.message.boards.model.impl.MBThreadModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from MBThread.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.message.boards.model.impl.MBThreadImpl",
		"table.name=MBThread"
	},
	service = ArgumentsResolver.class
)
public class MBThreadModelArgumentsResolver implements ArgumentsResolver {

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

		MBThreadModelImpl mbThreadModelImpl = (MBThreadModelImpl)baseModel;

		BiPredicate<MBThreadModelImpl, Boolean> wherePredicate =
			_wherePredicates.get(finderPath.getFinderName());

		if ((wherePredicate != null) &&
			!wherePredicate.test(mbThreadModelImpl, original)) {

			return null;
		}

		long columnBitmask = mbThreadModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(mbThreadModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |= mbThreadModelImpl.getColumnBitmask(
					columnName);
			}

			Long whereColumnBitmask = _whereColumnBitmasks.get(
				finderPath.getFinderName());

			if (whereColumnBitmask != null) {
				finderPathColumnBitmask |= whereColumnBitmask;
			}

			if (finderPath.isBaseModelResult() &&
				(MBThreadPersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(mbThreadModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return MBThreadImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return MBThreadTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		MBThreadModelImpl mbThreadModelImpl, String columnName,
		boolean original) {

		if (original) {
			return mbThreadModelImpl.getColumnOriginalValue(columnName);
		}

		return mbThreadModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		MBThreadModelImpl mbThreadModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = mbThreadModelImpl.getColumnOriginalValue(columnName);
			}
			else {
				value = mbThreadModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

	private static final long _ORDER_BY_COLUMNS_BITMASK;

	static {
		long orderByColumnsBitmask = 0;

		orderByColumnsBitmask |= MBThreadModelImpl.getColumnBitmask("priority");
		orderByColumnsBitmask |= MBThreadModelImpl.getColumnBitmask(
			"lastPostDate");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map<String, BiPredicate<MBThreadModelImpl, Boolean>>
		_wherePredicates = new HashMap<>();

	static {
		long whereColumnBitmask = MBThreadModelImpl.getColumnBitmask(
			"categoryId");
		BiPredicate<MBThreadModelImpl, Boolean> wherePredicate =
			(mbThreadModelImpl, original) ->
				GetterUtil.getLong(
					_getColumnValue(
						mbThreadModelImpl, "categoryId", original)) != -1L;

		_whereColumnBitmasks.put("GroupId", whereColumnBitmask);
		_wherePredicates.put("GroupId", wherePredicate);
		_whereColumnBitmasks.put("G_S", whereColumnBitmask);
		_wherePredicates.put("G_S", wherePredicate);
		_whereColumnBitmasks.put("L_P", whereColumnBitmask);
		_wherePredicates.put("L_P", wherePredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:976897253