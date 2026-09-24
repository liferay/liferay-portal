/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.service.persistence.impl;

import com.liferay.client.extension.model.ClientExtensionEntryRelTable;
import com.liferay.client.extension.model.impl.ClientExtensionEntryRelImpl;
import com.liferay.client.extension.model.impl.ClientExtensionEntryRelModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from ClientExtensionEntryRel.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.client.extension.model.impl.ClientExtensionEntryRelImpl",
		"table.name=ClientExtensionEntryRel"
	},
	service = ArgumentsResolver.class
)
public class ClientExtensionEntryRelModelArgumentsResolver
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

		ClientExtensionEntryRelModelImpl clientExtensionEntryRelModelImpl =
			(ClientExtensionEntryRelModelImpl)baseModel;

		long columnBitmask =
			clientExtensionEntryRelModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				clientExtensionEntryRelModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					clientExtensionEntryRelModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				clientExtensionEntryRelModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return ClientExtensionEntryRelImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return ClientExtensionEntryRelTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		ClientExtensionEntryRelModelImpl clientExtensionEntryRelModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = clientExtensionEntryRelModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = clientExtensionEntryRelModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:1527366672