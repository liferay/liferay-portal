/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.model.impl.UserModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

/**
 * The arguments resolver class for retrieving value from User.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.model.impl.UserImpl", "table.name=User_"
	},
	service = ArgumentsResolver.class
)
public class UserModelArgumentsResolver implements ArgumentsResolver {

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

		UserModelImpl userModelImpl = (UserModelImpl)baseModel;

		BiPredicate<UserModelImpl, Boolean> wherePredicate =
			_wherePredicates.get(finderPath.getFinderName());

		if ((wherePredicate != null) &&
			!wherePredicate.test(userModelImpl, original)) {

			return null;
		}

		long columnBitmask = userModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(userModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |= userModelImpl.getColumnBitmask(
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
			return _getValue(userModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return UserImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return UserTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		UserModelImpl userModelImpl, String columnName, boolean original) {

		if (original) {
			return userModelImpl.getColumnOriginalValue(columnName);
		}

		return userModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		UserModelImpl userModelImpl, FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = userModelImpl.getColumnOriginalValue(columnName);
			}
			else {
				value = userModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();
	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map<String, BiPredicate<UserModelImpl, Boolean>>
		_wherePredicates = new HashMap<>();

	static {
		long whereColumnBitmask = UserModelImpl.getColumnBitmask("type_");
		BiPredicate<UserModelImpl, Boolean> wherePredicate =
			(userModelImpl, original) ->
				GetterUtil.getInteger(
					_getColumnValue(userModelImpl, "type_", original)) == 1;

		_whereColumnBitmasks.put("CompanyId", whereColumnBitmask);
		_wherePredicates.put("CompanyId", wherePredicate);
		_whereColumnBitmasks.put("GtU_C", whereColumnBitmask);
		_wherePredicates.put("GtU_C", wherePredicate);
		_whereColumnBitmasks.put("C_CD", whereColumnBitmask);
		_wherePredicates.put("C_CD", wherePredicate);
		_whereColumnBitmasks.put("C_MD", whereColumnBitmask);
		_wherePredicates.put("C_MD", wherePredicate);
		_whereColumnBitmasks.put("C_S", whereColumnBitmask);
		_wherePredicates.put("C_S", wherePredicate);
		_whereColumnBitmasks.put("C_CD_MD", whereColumnBitmask);
		_wherePredicates.put("C_CD_MD", wherePredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1876571596