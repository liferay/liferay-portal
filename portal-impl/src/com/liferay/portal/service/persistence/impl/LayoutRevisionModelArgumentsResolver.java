/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.LayoutRevisionTable;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.model.impl.LayoutRevisionImpl;
import com.liferay.portal.model.impl.LayoutRevisionModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

/**
 * The arguments resolver class for retrieving value from LayoutRevision.
 *
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.model.impl.LayoutRevisionImpl",
		"table.name=LayoutRevision"
	},
	service = ArgumentsResolver.class
)
public class LayoutRevisionModelArgumentsResolver implements ArgumentsResolver {

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

		LayoutRevisionModelImpl layoutRevisionModelImpl =
			(LayoutRevisionModelImpl)baseModel;

		BiPredicate<LayoutRevisionModelImpl, Boolean> whereBiPredicate =
			_whereBiPredicates.get(finderPath.getFinderName());

		if ((whereBiPredicate != null) &&
			!whereBiPredicate.test(layoutRevisionModelImpl, original)) {

			return null;
		}

		long columnBitmask = layoutRevisionModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(layoutRevisionModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					layoutRevisionModelImpl.getColumnBitmask(columnName);
			}

			Long whereColumnBitmask = _whereColumnBitmasks.get(
				finderPath.getFinderName());

			if (whereColumnBitmask != null) {
				finderPathColumnBitmask |= whereColumnBitmask;
			}

			if (finderPath.isBaseModelResult() &&
				(LayoutRevisionPersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(layoutRevisionModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return LayoutRevisionImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return LayoutRevisionTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		LayoutRevisionModelImpl layoutRevisionModelImpl, String columnName,
		boolean original) {

		if (original) {
			return layoutRevisionModelImpl.getColumnOriginalValue(columnName);
		}

		return layoutRevisionModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		LayoutRevisionModelImpl layoutRevisionModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = layoutRevisionModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = layoutRevisionModelImpl.getColumnValue(columnName);
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

		orderByColumnsBitmask |= LayoutRevisionModelImpl.getColumnBitmask(
			"modifiedDate");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map
		<String, BiPredicate<LayoutRevisionModelImpl, Boolean>>
			_whereBiPredicates = new HashMap<>();

	static {
		long whereColumnBitmask = LayoutRevisionModelImpl.getColumnBitmask(
			"status");

		BiPredicate<LayoutRevisionModelImpl, Boolean> whereBiPredicate =
			(layoutRevisionModelImpl, original) ->
				GetterUtil.getInteger(
					_getColumnValue(
						layoutRevisionModelImpl, "status", original)) != 5;

		_whereColumnBitmasks.put("L_L_P", whereColumnBitmask);

		_whereBiPredicates.put("L_L_P", whereBiPredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-253272838