/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.persistence.impl;

import com.liferay.commerce.notification.model.CommerceNotificationTemplateCommerceAccountGroupRelTable;
import com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelImpl;
import com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from CommerceNotificationTemplateCommerceAccountGroupRel.
 *
 * @author Alessio Antonio Rendina
 * @deprecated
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelImpl",
		"table.name=CNTemplateCAccountGroupRel"
	},
	service = ArgumentsResolver.class
)
public class
	CommerceNotificationTemplateCommerceAccountGroupRelModelArgumentsResolver
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

		CommerceNotificationTemplateCommerceAccountGroupRelModelImpl
			commerceNotificationTemplateCommerceAccountGroupRelModelImpl =
				(CommerceNotificationTemplateCommerceAccountGroupRelModelImpl)
					baseModel;

		long columnBitmask =
			commerceNotificationTemplateCommerceAccountGroupRelModelImpl.
				getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				commerceNotificationTemplateCommerceAccountGroupRelModelImpl,
				finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					commerceNotificationTemplateCommerceAccountGroupRelModelImpl.
						getColumnBitmask(columnName);
			}

			if (finderPath.isBaseModelResult() &&
				(CommerceNotificationTemplateCommerceAccountGroupRelPersistenceImpl.FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
					finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				commerceNotificationTemplateCommerceAccountGroupRelModelImpl,
				finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CommerceNotificationTemplateCommerceAccountGroupRelImpl.class.
			getName();
	}

	@Override
	public String getTableName() {
		return CommerceNotificationTemplateCommerceAccountGroupRelTable.
			INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		CommerceNotificationTemplateCommerceAccountGroupRelModelImpl
			commerceNotificationTemplateCommerceAccountGroupRelModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					commerceNotificationTemplateCommerceAccountGroupRelModelImpl.
						getColumnOriginalValue(columnName);
			}
			else {
				value =
					commerceNotificationTemplateCommerceAccountGroupRelModelImpl.
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
			CommerceNotificationTemplateCommerceAccountGroupRelModelImpl.
				getColumnBitmask("createDate");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1103742257