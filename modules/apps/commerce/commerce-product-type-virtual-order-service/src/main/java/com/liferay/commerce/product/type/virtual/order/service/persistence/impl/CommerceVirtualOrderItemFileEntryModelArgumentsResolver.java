/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service.persistence.impl;

import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntryTable;
import com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryImpl;
import com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from CommerceVirtualOrderItemFileEntry.
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryImpl",
		"table.name=CVirtualOrderItemFileEntry"
	},
	service = ArgumentsResolver.class
)
public class CommerceVirtualOrderItemFileEntryModelArgumentsResolver
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

		CommerceVirtualOrderItemFileEntryModelImpl
			commerceVirtualOrderItemFileEntryModelImpl =
				(CommerceVirtualOrderItemFileEntryModelImpl)baseModel;

		long columnBitmask =
			commerceVirtualOrderItemFileEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				commerceVirtualOrderItemFileEntryModelImpl, finderPath,
				original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					commerceVirtualOrderItemFileEntryModelImpl.getColumnBitmask(
						columnName);
			}

			if (finderPath.isBaseModelResult() &&
				(CommerceVirtualOrderItemFileEntryPersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				commerceVirtualOrderItemFileEntryModelImpl, finderPath,
				original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CommerceVirtualOrderItemFileEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return CommerceVirtualOrderItemFileEntryTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		CommerceVirtualOrderItemFileEntryModelImpl
			commerceVirtualOrderItemFileEntryModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					commerceVirtualOrderItemFileEntryModelImpl.
						getColumnOriginalValue(columnName);
			}
			else {
				value =
					commerceVirtualOrderItemFileEntryModelImpl.getColumnValue(
						columnName);
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

		orderByColumnsBitmask |=
			CommerceVirtualOrderItemFileEntryModelImpl.getColumnBitmask(
				"createDate");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1730692594