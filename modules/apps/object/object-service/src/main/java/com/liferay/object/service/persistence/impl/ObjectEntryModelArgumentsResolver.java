/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.impl.ObjectEntryImpl;
import com.liferay.object.model.impl.ObjectEntryModelImpl;
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
 * The arguments resolver class for retrieving value from ObjectEntry.
 *
 * @author Marco Leo
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.object.model.impl.ObjectEntryImpl",
		"table.name=ObjectEntry"
	},
	service = ArgumentsResolver.class
)
public class ObjectEntryModelArgumentsResolver implements ArgumentsResolver {

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

		ObjectEntryModelImpl objectEntryModelImpl =
			(ObjectEntryModelImpl)baseModel;

		BiPredicate<ObjectEntryModelImpl, Boolean> whereBiPredicate =
			_whereBiPredicates.get(finderPath.getFinderName());

		if ((whereBiPredicate != null) &&
			!whereBiPredicate.test(objectEntryModelImpl, original)) {

			return null;
		}

		long columnBitmask = objectEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(objectEntryModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					objectEntryModelImpl.getColumnBitmask(columnName);
			}

			Long whereColumnBitmask = _whereColumnBitmasks.get(
				finderPath.getFinderName());

			if (whereColumnBitmask != null) {
				finderPathColumnBitmask |= whereColumnBitmask;
			}

			if (finderPath.isBaseModelResult() &&
				(ObjectEntryPersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(objectEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return ObjectEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return ObjectEntryTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		ObjectEntryModelImpl objectEntryModelImpl, String columnName,
		boolean original) {

		if (original) {
			return objectEntryModelImpl.getColumnOriginalValue(columnName);
		}

		return objectEntryModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		ObjectEntryModelImpl objectEntryModelImpl, FinderPath finderPath,
		boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = objectEntryModelImpl.getColumnOriginalValue(columnName);
			}
			else {
				value = objectEntryModelImpl.getColumnValue(columnName);
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

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map<String, BiPredicate<ObjectEntryModelImpl, Boolean>>
		_whereBiPredicates = new HashMap<>();

	static {
		long whereColumnBitmask =
			ObjectEntryModelImpl.getColumnBitmask("objectEntryId") |
			ObjectEntryModelImpl.getColumnBitmask("headObjectEntryId");

		BiPredicate<ObjectEntryModelImpl, Boolean> whereBiPredicate =
			(objectEntryModelImpl, original) ->
				GetterUtil.getLong(
					_getColumnValue(
						objectEntryModelImpl, "objectEntryId", original)) !=
							GetterUtil.getLong(
								_getColumnValue(
									objectEntryModelImpl, "headObjectEntryId",
									original));

		_whereColumnBitmasks.put("HeadObjectEntryId", whereColumnBitmask);

		_whereBiPredicates.put("HeadObjectEntryId", whereBiPredicate);
		whereColumnBitmask =
			ObjectEntryModelImpl.getColumnBitmask("objectEntryId") |
			ObjectEntryModelImpl.getColumnBitmask("headObjectEntryId");

		whereBiPredicate = (objectEntryModelImpl, original) ->
			GetterUtil.getLong(
				_getColumnValue(
					objectEntryModelImpl, "objectEntryId", original)) ==
						GetterUtil.getLong(
							_getColumnValue(
								objectEntryModelImpl, "headObjectEntryId",
								original));

		_whereColumnBitmasks.put("ObjectDefinitionId", whereColumnBitmask);

		_whereBiPredicates.put("ObjectDefinitionId", whereBiPredicate);
		_whereColumnBitmasks.put("G_ODI", whereColumnBitmask);

		_whereBiPredicates.put("G_ODI", whereBiPredicate);
		_whereColumnBitmasks.put("G_OEFI", whereColumnBitmask);

		_whereBiPredicates.put("G_OEFI", whereBiPredicate);
		_whereColumnBitmasks.put("U_ODI", whereColumnBitmask);

		_whereBiPredicates.put("U_ODI", whereBiPredicate);
		_whereColumnBitmasks.put("ODI_NotS", whereColumnBitmask);

		_whereBiPredicates.put("ODI_NotS", whereBiPredicate);
		_whereColumnBitmasks.put("ROEI_NotS", whereColumnBitmask);

		_whereBiPredicates.put("ROEI_NotS", whereBiPredicate);
		_whereColumnBitmasks.put("G_C_OEFI", whereColumnBitmask);

		_whereBiPredicates.put("G_C_OEFI", whereBiPredicate);
		_whereColumnBitmasks.put("G_ODI_S", whereColumnBitmask);

		_whereBiPredicates.put("G_ODI_S", whereBiPredicate);
		_whereColumnBitmasks.put("G_ODI_NotS", whereColumnBitmask);

		_whereBiPredicates.put("G_ODI_NotS", whereBiPredicate);
		_whereColumnBitmasks.put("U_GtCD_ODI", whereColumnBitmask);

		_whereBiPredicates.put("U_GtCD_ODI", whereBiPredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1896703948