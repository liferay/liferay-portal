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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.service.builder.test.model.FinderWhereClauseEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

/**
 * The arguments resolver class for retrieving value from FinderWhereClauseEntry.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryImpl",
		"table.name=FinderWhereClauseEntry"
	},
	service = ArgumentsResolver.class
)
public class FinderWhereClauseEntryModelArgumentsResolver
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

		FinderWhereClauseEntryModelImpl finderWhereClauseEntryModelImpl =
			(FinderWhereClauseEntryModelImpl)baseModel;

		BiPredicate<FinderWhereClauseEntryModelImpl, Boolean> whereBiPredicate =
			_whereBiPredicates.get(finderPath.getFinderName());

		if ((whereBiPredicate != null) &&
			!whereBiPredicate.test(finderWhereClauseEntryModelImpl, original)) {

			return null;
		}

		long columnBitmask = finderWhereClauseEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				finderWhereClauseEntryModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					finderWhereClauseEntryModelImpl.getColumnBitmask(
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
			return _getValue(
				finderWhereClauseEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return FinderWhereClauseEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return FinderWhereClauseEntryTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		FinderWhereClauseEntryModelImpl finderWhereClauseEntryModelImpl,
		String columnName, boolean original) {

		if (original) {
			return finderWhereClauseEntryModelImpl.getColumnOriginalValue(
				columnName);
		}

		return finderWhereClauseEntryModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		FinderWhereClauseEntryModelImpl finderWhereClauseEntryModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = finderWhereClauseEntryModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = finderWhereClauseEntryModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();
	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map
		<String, BiPredicate<FinderWhereClauseEntryModelImpl, Boolean>>
			_whereBiPredicates = new HashMap<>();

	static {
		long whereColumnBitmask =
			FinderWhereClauseEntryModelImpl.getColumnBitmask(
				"finderWhereClauseEntryId") |
			FinderWhereClauseEntryModelImpl.getColumnBitmask("headId");

		BiPredicate<FinderWhereClauseEntryModelImpl, Boolean> whereBiPredicate =
			(finderWhereClauseEntryModelImpl, original) ->
				GetterUtil.getLong(
					_getColumnValue(
						finderWhereClauseEntryModelImpl,
						"finderWhereClauseEntryId", original)) ==
							GetterUtil.getLong(
								_getColumnValue(
									finderWhereClauseEntryModelImpl, "headId",
									original));

		_whereColumnBitmasks.put("HeadId", whereColumnBitmask);

		_whereBiPredicates.put("HeadId", whereBiPredicate);
		whereColumnBitmask = FinderWhereClauseEntryModelImpl.getColumnBitmask(
			"nickname");

		whereBiPredicate =
			(finderWhereClauseEntryModelImpl, original) -> Validator.isNotNull(
				GetterUtil.getString(
					_getColumnValue(
						finderWhereClauseEntryModelImpl, "nickname",
						original)));

		_whereColumnBitmasks.put("Name_Nickname", whereColumnBitmask);

		_whereBiPredicates.put("Name_Nickname", whereBiPredicate);
		whereColumnBitmask =
			FinderWhereClauseEntryModelImpl.getColumnBitmask(
				"finderWhereClauseEntryId") |
			FinderWhereClauseEntryModelImpl.getColumnBitmask("headId");

		whereBiPredicate = (finderWhereClauseEntryModelImpl, original) ->
			GetterUtil.getLong(
				_getColumnValue(
					finderWhereClauseEntryModelImpl, "finderWhereClauseEntryId",
					original)) != GetterUtil.getLong(
						_getColumnValue(
							finderWhereClauseEntryModelImpl, "headId",
							original));

		_whereColumnBitmasks.put("Status", whereColumnBitmask);

		_whereBiPredicates.put("Status", whereBiPredicate);
		whereColumnBitmask =
			FinderWhereClauseEntryModelImpl.getColumnBitmask("nickname") |
			FinderWhereClauseEntryModelImpl.getColumnBitmask("status");

		whereBiPredicate = (finderWhereClauseEntryModelImpl, original) ->
			Validator.isNotNull(
				GetterUtil.getString(
					_getColumnValue(
						finderWhereClauseEntryModelImpl, "nickname",
						original))) &&
			(GetterUtil.getInteger(
				_getColumnValue(
					finderWhereClauseEntryModelImpl, "status", original)) != 0);

		_whereColumnBitmasks.put("Name_Status", whereColumnBitmask);

		_whereBiPredicates.put("Name_Status", whereBiPredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1437731124