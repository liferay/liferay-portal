/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.impl;

import com.liferay.message.boards.model.MBMessageTable;
import com.liferay.message.boards.model.impl.MBMessageImpl;
import com.liferay.message.boards.model.impl.MBMessageModelImpl;
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
 * The arguments resolver class for retrieving value from MBMessage.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.message.boards.model.impl.MBMessageImpl",
		"table.name=MBMessage"
	},
	service = ArgumentsResolver.class
)
public class MBMessageModelArgumentsResolver implements ArgumentsResolver {

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

		MBMessageModelImpl mbMessageModelImpl = (MBMessageModelImpl)baseModel;

		BiPredicate<MBMessageModelImpl, Boolean> wherePredicate =
			_wherePredicates.get(finderPath.getFinderName());

		if ((wherePredicate != null) &&
			!wherePredicate.test(mbMessageModelImpl, original)) {

			return null;
		}

		long columnBitmask = mbMessageModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(mbMessageModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |= mbMessageModelImpl.getColumnBitmask(
					columnName);
			}

			Long whereColumnBitmask = _whereColumnBitmasks.get(
				finderPath.getFinderName());

			if (whereColumnBitmask != null) {
				finderPathColumnBitmask |= whereColumnBitmask;
			}

			if (finderPath.isBaseModelResult() &&
				(MBMessagePersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(mbMessageModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return MBMessageImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return MBMessageTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		MBMessageModelImpl mbMessageModelImpl, String columnName,
		boolean original) {

		if (original) {
			return mbMessageModelImpl.getColumnOriginalValue(columnName);
		}

		return mbMessageModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		MBMessageModelImpl mbMessageModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = mbMessageModelImpl.getColumnOriginalValue(columnName);
			}
			else {
				value = mbMessageModelImpl.getColumnValue(columnName);
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

		orderByColumnsBitmask |= MBMessageModelImpl.getColumnBitmask(
			"createDate");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map<String, BiPredicate<MBMessageModelImpl, Boolean>>
		_wherePredicates = new HashMap<>();

	static {
		long whereColumnBitmask = MBMessageModelImpl.getColumnBitmask(
			"categoryId");
		BiPredicate<MBMessageModelImpl, Boolean> wherePredicate =
			(mbMessageModelImpl, original) ->
				GetterUtil.getLong(
					_getColumnValue(
						mbMessageModelImpl, "categoryId", original)) != -1L;

		_whereColumnBitmasks.put("GroupId", whereColumnBitmask);
		_wherePredicates.put("GroupId", wherePredicate);
		_whereColumnBitmasks.put("CompanyId", whereColumnBitmask);
		_wherePredicates.put("CompanyId", wherePredicate);
		_whereColumnBitmasks.put("UserId", whereColumnBitmask);
		_wherePredicates.put("UserId", wherePredicate);
		_whereColumnBitmasks.put("G_S", whereColumnBitmask);
		_wherePredicates.put("G_S", wherePredicate);
		_whereColumnBitmasks.put("C_S", whereColumnBitmask);
		_wherePredicates.put("C_S", wherePredicate);

		whereColumnBitmask = MBMessageModelImpl.getColumnBitmask(
			"parentMessageId");
		wherePredicate = (mbMessageModelImpl, original) ->
			GetterUtil.getLong(
				_getColumnValue(
					mbMessageModelImpl, "parentMessageId", original)) != 0L;

		_whereColumnBitmasks.put("ThreadIdReplies", whereColumnBitmask);
		_wherePredicates.put("ThreadIdReplies", wherePredicate);
		_whereColumnBitmasks.put("TR_S", whereColumnBitmask);
		_wherePredicates.put("TR_S", wherePredicate);

		whereColumnBitmask =
			MBMessageModelImpl.getColumnBitmask("categoryId") |
			MBMessageModelImpl.getColumnBitmask("anonymous");
		wherePredicate = (mbMessageModelImpl, original) ->
			(GetterUtil.getLong(
				_getColumnValue(mbMessageModelImpl, "categoryId", original)) !=
					-1L) &&
			!GetterUtil.getBoolean(
				_getColumnValue(mbMessageModelImpl, "anonymous", original));

		_whereColumnBitmasks.put("G_U", whereColumnBitmask);
		_wherePredicates.put("G_U", wherePredicate);
		_whereColumnBitmasks.put("G_U_S", whereColumnBitmask);
		_wherePredicates.put("G_U_S", wherePredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1370769055