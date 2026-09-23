/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.service.builder.test.compat740.model.WhereClauseEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.WhereClauseEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.WhereClauseEntryModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from WhereClauseEntry.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.portal.tools.service.builder.test.compat740.model.impl.WhereClauseEntryImpl",
		"table.name=WhereClauseEntry"
	},
	service = ArgumentsResolver.class
)
public class WhereClauseEntryModelArgumentsResolver
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

		WhereClauseEntryModelImpl whereClauseEntryModelImpl =
			(WhereClauseEntryModelImpl)baseModel;

		BiPredicate<WhereClauseEntryModelImpl, Boolean> wherePredicate =
			_wherePredicates.get(finderPath.getFinderName());

		if ((wherePredicate != null) &&
			!wherePredicate.test(whereClauseEntryModelImpl, original)) {

			return null;
		}

		long columnBitmask = whereClauseEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(whereClauseEntryModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					whereClauseEntryModelImpl.getColumnBitmask(columnName);
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
			return _getValue(whereClauseEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return WhereClauseEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return WhereClauseEntryTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		WhereClauseEntryModelImpl whereClauseEntryModelImpl, String columnName,
		boolean original) {

		if (original) {
			return whereClauseEntryModelImpl.getColumnOriginalValue(columnName);
		}

		return whereClauseEntryModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		WhereClauseEntryModelImpl whereClauseEntryModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = whereClauseEntryModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = whereClauseEntryModelImpl.getColumnValue(columnName);
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
		<String, BiPredicate<WhereClauseEntryModelImpl, Boolean>>
			_wherePredicates = new HashMap<>();

	static {
		long whereColumnBitmask = WhereClauseEntryModelImpl.getColumnBitmask(
			"nickname");
		BiPredicate<WhereClauseEntryModelImpl, Boolean> wherePredicate =
			(whereClauseEntryModelImpl, original) -> Validator.isNotNull(
				GetterUtil.getString(
					_getColumnValue(
						whereClauseEntryModelImpl, "nickname", original)));

		_whereColumnBitmasks.put("Name_Nickname", whereColumnBitmask);
		_wherePredicates.put("Name_Nickname", wherePredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:470184641