/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.persistence.impl;

import com.liferay.layout.model.LayoutClassedModelUsageTable;
import com.liferay.layout.model.impl.LayoutClassedModelUsageImpl;
import com.liferay.layout.model.impl.LayoutClassedModelUsageModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from LayoutClassedModelUsage.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.layout.model.impl.LayoutClassedModelUsageImpl",
		"table.name=LayoutClassedModelUsage"
	},
	service = ArgumentsResolver.class
)
public class LayoutClassedModelUsageModelArgumentsResolver
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

		LayoutClassedModelUsageModelImpl layoutClassedModelUsageModelImpl =
			(LayoutClassedModelUsageModelImpl)baseModel;

		BiPredicate<LayoutClassedModelUsageModelImpl, Boolean>
			whereBiPredicate = _whereBiPredicates.get(
				finderPath.getFinderName());

		if ((whereBiPredicate != null) &&
			!whereBiPredicate.test(
				layoutClassedModelUsageModelImpl, original)) {

			return null;
		}

		long columnBitmask =
			layoutClassedModelUsageModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				layoutClassedModelUsageModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					layoutClassedModelUsageModelImpl.getColumnBitmask(
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
				layoutClassedModelUsageModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return LayoutClassedModelUsageImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return LayoutClassedModelUsageTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		LayoutClassedModelUsageModelImpl layoutClassedModelUsageModelImpl,
		String columnName, boolean original) {

		if (original) {
			return layoutClassedModelUsageModelImpl.getColumnOriginalValue(
				columnName);
		}

		return layoutClassedModelUsageModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		LayoutClassedModelUsageModelImpl layoutClassedModelUsageModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = layoutClassedModelUsageModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = layoutClassedModelUsageModelImpl.getColumnValue(
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
		<String, BiPredicate<LayoutClassedModelUsageModelImpl, Boolean>>
			_whereBiPredicates = new HashMap<>();

	static {
		long whereColumnBitmask =
			LayoutClassedModelUsageModelImpl.getColumnBitmask("containerKey");

		BiPredicate<LayoutClassedModelUsageModelImpl, Boolean>
			whereBiPredicate =
				(layoutClassedModelUsageModelImpl, original) ->
					Validator.isNotNull(
						GetterUtil.getString(
							_getColumnValue(
								layoutClassedModelUsageModelImpl,
								"containerKey", original)));

		_whereColumnBitmasks.put("Plid", whereColumnBitmask);

		_whereBiPredicates.put("Plid", whereBiPredicate);
		_whereColumnBitmasks.put("C_CN", whereColumnBitmask);

		_whereBiPredicates.put("C_CN", whereBiPredicate);
		_whereColumnBitmasks.put("CN_CPK", whereColumnBitmask);

		_whereBiPredicates.put("CN_CPK", whereBiPredicate);
		_whereColumnBitmasks.put("C_CERC_CN", whereColumnBitmask);

		_whereBiPredicates.put("C_CERC_CN", whereBiPredicate);
		_whereColumnBitmasks.put("C_CN_CT", whereColumnBitmask);

		_whereBiPredicates.put("C_CN_CT", whereBiPredicate);
		_whereColumnBitmasks.put("CN_CPK_T", whereColumnBitmask);

		_whereBiPredicates.put("CN_CPK_T", whereBiPredicate);
		_whereColumnBitmasks.put("CK_CT_P", whereColumnBitmask);

		_whereBiPredicates.put("CK_CT_P", whereBiPredicate);
		_whereColumnBitmasks.put("C_CERC_CN_T", whereColumnBitmask);

		_whereBiPredicates.put("C_CERC_CN_T", whereBiPredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-384729305