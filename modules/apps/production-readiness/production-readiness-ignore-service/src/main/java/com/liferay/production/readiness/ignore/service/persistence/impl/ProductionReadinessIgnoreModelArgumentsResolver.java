/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnoreTable;
import com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreImpl;
import com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from ProductionReadinessIgnore.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.production.readiness.ignore.model.impl.ProductionReadinessIgnoreImpl",
		"table.name=PR_ProductionReadinessIgnore"
	},
	service = ArgumentsResolver.class
)
public class ProductionReadinessIgnoreModelArgumentsResolver
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

		ProductionReadinessIgnoreModelImpl productionReadinessIgnoreModelImpl =
			(ProductionReadinessIgnoreModelImpl)baseModel;

		long columnBitmask =
			productionReadinessIgnoreModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				productionReadinessIgnoreModelImpl, columnNames, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					productionReadinessIgnoreModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				productionReadinessIgnoreModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return ProductionReadinessIgnoreImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return ProductionReadinessIgnoreTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		ProductionReadinessIgnoreModelImpl productionReadinessIgnoreModelImpl,
		String[] columnNames, boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] =
					productionReadinessIgnoreModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				arguments[i] =
					productionReadinessIgnoreModelImpl.getColumnValue(
						columnName);
			}
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:-1230234339