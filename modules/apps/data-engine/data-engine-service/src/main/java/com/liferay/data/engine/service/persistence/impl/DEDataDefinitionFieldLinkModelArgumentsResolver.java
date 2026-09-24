/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service.persistence.impl;

import com.liferay.data.engine.model.DEDataDefinitionFieldLinkTable;
import com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkImpl;
import com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from DEDataDefinitionFieldLink.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkImpl",
		"table.name=DEDataDefinitionFieldLink"
	},
	service = ArgumentsResolver.class
)
public class DEDataDefinitionFieldLinkModelArgumentsResolver
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

		DEDataDefinitionFieldLinkModelImpl deDataDefinitionFieldLinkModelImpl =
			(DEDataDefinitionFieldLinkModelImpl)baseModel;

		long columnBitmask =
			deDataDefinitionFieldLinkModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				deDataDefinitionFieldLinkModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					deDataDefinitionFieldLinkModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				deDataDefinitionFieldLinkModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return DEDataDefinitionFieldLinkImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return DEDataDefinitionFieldLinkTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		DEDataDefinitionFieldLinkModelImpl deDataDefinitionFieldLinkModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					deDataDefinitionFieldLinkModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				value = deDataDefinitionFieldLinkModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:-1548949780