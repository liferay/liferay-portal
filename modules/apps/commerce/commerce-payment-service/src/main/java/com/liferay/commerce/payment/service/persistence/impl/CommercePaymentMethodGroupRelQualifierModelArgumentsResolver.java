/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.impl;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifierTable;
import com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierImpl;
import com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from CommercePaymentMethodGroupRelQualifier.
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierImpl",
		"table.name=CPMethodGroupRelQualifier"
	},
	service = ArgumentsResolver.class
)
public class CommercePaymentMethodGroupRelQualifierModelArgumentsResolver
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

		CommercePaymentMethodGroupRelQualifierModelImpl
			commercePaymentMethodGroupRelQualifierModelImpl =
				(CommercePaymentMethodGroupRelQualifierModelImpl)baseModel;

		long columnBitmask =
			commercePaymentMethodGroupRelQualifierModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				commercePaymentMethodGroupRelQualifierModelImpl, finderPath,
				original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					commercePaymentMethodGroupRelQualifierModelImpl.
						getColumnBitmask(columnName);
			}

			if (finderPath.isBaseModelResult() &&
				(CommercePaymentMethodGroupRelQualifierPersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				commercePaymentMethodGroupRelQualifierModelImpl, finderPath,
				original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CommercePaymentMethodGroupRelQualifierImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
			getTableName();
	}

	private static Object[] _getValue(
		CommercePaymentMethodGroupRelQualifierModelImpl
			commercePaymentMethodGroupRelQualifierModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					commercePaymentMethodGroupRelQualifierModelImpl.
						getColumnOriginalValue(columnName);
			}
			else {
				value =
					commercePaymentMethodGroupRelQualifierModelImpl.
						getColumnValue(columnName);
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
			CommercePaymentMethodGroupRelQualifierModelImpl.getColumnBitmask(
				"createDate");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:856795834