/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

/**
 * The arguments resolver class for retrieving value from Layout.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portal.model.impl.LayoutImpl",
		"table.name=Layout"
	},
	service = ArgumentsResolver.class
)
public class LayoutModelArgumentsResolver implements ArgumentsResolver {

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

		LayoutModelImpl layoutModelImpl = (LayoutModelImpl)baseModel;

		BiPredicate<LayoutModelImpl, Boolean> whereBiPredicate =
			_whereBiPredicates.get(finderPath.getFinderName());

		if ((whereBiPredicate != null) &&
			!whereBiPredicate.test(layoutModelImpl, original)) {

			return null;
		}

		long columnBitmask = layoutModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(layoutModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |= layoutModelImpl.getColumnBitmask(
					columnName);
			}

			Long whereColumnBitmask = _whereColumnBitmasks.get(
				finderPath.getFinderName());

			if (whereColumnBitmask != null) {
				finderPathColumnBitmask |= whereColumnBitmask;
			}

			if (finderPath.isBaseModelResult() &&
				(LayoutPersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(layoutModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return LayoutImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return LayoutTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		LayoutModelImpl layoutModelImpl, String columnName, boolean original) {

		if (original) {
			return layoutModelImpl.getColumnOriginalValue(columnName);
		}

		return layoutModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		LayoutModelImpl layoutModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = layoutModelImpl.getColumnOriginalValue(columnName);
			}
			else {
				value = layoutModelImpl.getColumnValue(columnName);
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

		orderByColumnsBitmask |= LayoutModelImpl.getColumnBitmask(
			"parentLayoutId");
		orderByColumnsBitmask |= LayoutModelImpl.getColumnBitmask("priority");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map<String, BiPredicate<LayoutModelImpl, Boolean>>
		_whereBiPredicates = new HashMap<>();

	static {
		long whereColumnBitmask = LayoutModelImpl.getColumnBitmask("system_");

		BiPredicate<LayoutModelImpl, Boolean> whereBiPredicate =
			(layoutModelImpl, original) -> !GetterUtil.getBoolean(
				_getColumnValue(layoutModelImpl, "system_", original));

		_whereColumnBitmasks.put("GroupId", whereColumnBitmask);

		_whereBiPredicates.put("GroupId", whereBiPredicate);
		_whereColumnBitmasks.put("CompanyId", whereColumnBitmask);

		_whereBiPredicates.put("CompanyId", whereBiPredicate);
		_whereColumnBitmasks.put("ParentPlid", whereColumnBitmask);

		_whereBiPredicates.put("ParentPlid", whereBiPredicate);
		_whereColumnBitmasks.put(
			"LayoutSetPrototypeLayoutERC", whereColumnBitmask);

		_whereBiPredicates.put("LayoutSetPrototypeLayoutERC", whereBiPredicate);
		_whereColumnBitmasks.put("G_T", whereColumnBitmask);

		_whereBiPredicates.put("G_T", whereBiPredicate);
		_whereColumnBitmasks.put("PLPTEERC_PLPTESERC", whereColumnBitmask);

		_whereBiPredicates.put("PLPTEERC_PLPTESERC", whereBiPredicate);
		_whereColumnBitmasks.put("G_P_P", whereColumnBitmask);

		_whereBiPredicates.put("G_P_P", whereBiPredicate);
		_whereColumnBitmasks.put("G_P_T", whereColumnBitmask);

		_whereBiPredicates.put("G_P_T", whereBiPredicate);
		_whereColumnBitmasks.put("G_P_ST", whereColumnBitmask);

		_whereBiPredicates.put("G_P_ST", whereBiPredicate);
		_whereColumnBitmasks.put("G_P_P_H", whereColumnBitmask);

		_whereBiPredicates.put("G_P_P_H", whereBiPredicate);
		_whereColumnBitmasks.put("G_P_P_LteP", whereColumnBitmask);

		_whereBiPredicates.put("G_P_P_LteP", whereBiPredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1983334692