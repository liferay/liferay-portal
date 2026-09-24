/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence.impl;

import com.liferay.depot.model.DepotAppCustomizationTable;
import com.liferay.depot.model.impl.DepotAppCustomizationImpl;
import com.liferay.depot.model.impl.DepotAppCustomizationModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from DepotAppCustomization.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.depot.model.impl.DepotAppCustomizationImpl",
		"table.name=DepotAppCustomization"
	},
	service = ArgumentsResolver.class
)
public class DepotAppCustomizationModelArgumentsResolver
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

		DepotAppCustomizationModelImpl depotAppCustomizationModelImpl =
			(DepotAppCustomizationModelImpl)baseModel;

		long columnBitmask = depotAppCustomizationModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				depotAppCustomizationModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					depotAppCustomizationModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				depotAppCustomizationModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return DepotAppCustomizationImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return DepotAppCustomizationTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		DepotAppCustomizationModelImpl depotAppCustomizationModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = depotAppCustomizationModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = depotAppCustomizationModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:1611202234