/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUserTable;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from OpenIdConnectUser.
 *
 * @author Arthur Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserImpl",
		"table.name=OpenIdConnectUser"
	},
	service = ArgumentsResolver.class
)
public class OpenIdConnectUserModelArgumentsResolver
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

		OpenIdConnectUserModelImpl openIdConnectUserModelImpl =
			(OpenIdConnectUserModelImpl)baseModel;

		long columnBitmask = openIdConnectUserModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(openIdConnectUserModelImpl, columnNames, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					openIdConnectUserModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(openIdConnectUserModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return OpenIdConnectUserImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return OpenIdConnectUserTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		OpenIdConnectUserModelImpl openIdConnectUserModelImpl,
		String[] columnNames, boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] =
					openIdConnectUserModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				arguments[i] = openIdConnectUserModelImpl.getColumnValue(
					columnName);
			}
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}