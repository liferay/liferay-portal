/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.impl;

import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadataTable;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataImpl;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from OAuthClientPRLocalMetadata.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataImpl",
		"table.name=OAuthClientPRLocalMetadata"
	},
	service = ArgumentsResolver.class
)
public class OAuthClientPRLocalMetadataModelArgumentsResolver
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

		OAuthClientPRLocalMetadataModelImpl
			oAuthClientPRLocalMetadataModelImpl =
				(OAuthClientPRLocalMetadataModelImpl)baseModel;

		long columnBitmask =
			oAuthClientPRLocalMetadataModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				oAuthClientPRLocalMetadataModelImpl, columnNames, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					oAuthClientPRLocalMetadataModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				oAuthClientPRLocalMetadataModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return OAuthClientPRLocalMetadataImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return OAuthClientPRLocalMetadataTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		OAuthClientPRLocalMetadataModelImpl oAuthClientPRLocalMetadataModelImpl,
		String[] columnNames, boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] =
					oAuthClientPRLocalMetadataModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				arguments[i] =
					oAuthClientPRLocalMetadataModelImpl.getColumnValue(
						columnName);
			}
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:-2131941674