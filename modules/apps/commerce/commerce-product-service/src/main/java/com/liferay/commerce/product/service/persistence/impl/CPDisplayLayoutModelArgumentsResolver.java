/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.model.CPDisplayLayoutTable;
import com.liferay.commerce.product.model.impl.CPDisplayLayoutImpl;
import com.liferay.commerce.product.model.impl.CPDisplayLayoutModelImpl;
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
 * The arguments resolver class for retrieving value from CPDisplayLayout.
 *
 * @author Marco Leo
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.commerce.product.model.impl.CPDisplayLayoutImpl",
		"table.name=CPDisplayLayout"
	},
	service = ArgumentsResolver.class
)
public class CPDisplayLayoutModelArgumentsResolver
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

		CPDisplayLayoutModelImpl cpDisplayLayoutModelImpl =
			(CPDisplayLayoutModelImpl)baseModel;

		BiPredicate<CPDisplayLayoutModelImpl, Boolean> whereBiPredicate =
			_whereBiPredicates.get(finderPath.getFinderName());

		if ((whereBiPredicate != null) &&
			!whereBiPredicate.test(cpDisplayLayoutModelImpl, original)) {

			return null;
		}

		long columnBitmask = cpDisplayLayoutModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(cpDisplayLayoutModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					cpDisplayLayoutModelImpl.getColumnBitmask(columnName);
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
			return _getValue(cpDisplayLayoutModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CPDisplayLayoutImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return CPDisplayLayoutTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		CPDisplayLayoutModelImpl cpDisplayLayoutModelImpl, String columnName,
		boolean original) {

		if (original) {
			return cpDisplayLayoutModelImpl.getColumnOriginalValue(columnName);
		}

		return cpDisplayLayoutModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		CPDisplayLayoutModelImpl cpDisplayLayoutModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = cpDisplayLayoutModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = cpDisplayLayoutModelImpl.getColumnValue(columnName);
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
		<String, BiPredicate<CPDisplayLayoutModelImpl, Boolean>>
			_whereBiPredicates = new HashMap<>();

	static {
		long whereColumnBitmask = CPDisplayLayoutModelImpl.getColumnBitmask(
			"layoutPageTemplateEntryUuid");

		BiPredicate<CPDisplayLayoutModelImpl, Boolean> whereBiPredicate =
			(cpDisplayLayoutModelImpl, original) -> Validator.isNotNull(
				GetterUtil.getString(
					_getColumnValue(
						cpDisplayLayoutModelImpl, "layoutPageTemplateEntryUuid",
						original)));

		_whereColumnBitmasks.put("C_C_LPTEU", whereColumnBitmask);

		_whereBiPredicates.put("C_C_LPTEU", whereBiPredicate);
		whereColumnBitmask = CPDisplayLayoutModelImpl.getColumnBitmask(
			"layoutUuid");

		whereBiPredicate =
			(cpDisplayLayoutModelImpl, original) -> Validator.isNotNull(
				GetterUtil.getString(
					_getColumnValue(
						cpDisplayLayoutModelImpl, "layoutUuid", original)));

		_whereColumnBitmasks.put("C_C_L", whereColumnBitmask);

		_whereBiPredicates.put("C_C_L", whereBiPredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:65558241