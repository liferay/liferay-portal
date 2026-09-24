/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.push.notifications.model.PushNotificationsDeviceTable;
import com.liferay.push.notifications.model.impl.PushNotificationsDeviceImpl;
import com.liferay.push.notifications.model.impl.PushNotificationsDeviceModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from PushNotificationsDevice.
 *
 * @author Bruno Farache
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.push.notifications.model.impl.PushNotificationsDeviceImpl",
		"table.name=PushNotificationsDevice"
	},
	service = ArgumentsResolver.class
)
public class PushNotificationsDeviceModelArgumentsResolver
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

		PushNotificationsDeviceModelImpl pushNotificationsDeviceModelImpl =
			(PushNotificationsDeviceModelImpl)baseModel;

		long columnBitmask =
			pushNotificationsDeviceModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				pushNotificationsDeviceModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					pushNotificationsDeviceModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				pushNotificationsDeviceModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return PushNotificationsDeviceImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return PushNotificationsDeviceTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		PushNotificationsDeviceModelImpl pushNotificationsDeviceModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = pushNotificationsDeviceModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = pushNotificationsDeviceModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:2052146227