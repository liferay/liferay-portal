/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.persistence.impl;

import com.liferay.audiences.model.AudiencesEntryGroupRelTable;
import com.liferay.audiences.model.impl.AudiencesEntryGroupRelImpl;
import com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from AudiencesEntryGroupRel.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.audiences.model.impl.AudiencesEntryGroupRelImpl",
		"table.name=AudiencesEntryGroupRel"
	},
	service = ArgumentsResolver.class
)
public class AudiencesEntryGroupRelModelArgumentsResolver
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

		AudiencesEntryGroupRelModelImpl audiencesEntryGroupRelModelImpl =
			(AudiencesEntryGroupRelModelImpl)baseModel;

		long columnBitmask = audiencesEntryGroupRelModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				audiencesEntryGroupRelModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					audiencesEntryGroupRelModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				audiencesEntryGroupRelModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return AudiencesEntryGroupRelImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return AudiencesEntryGroupRelTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		AudiencesEntryGroupRelModelImpl audiencesEntryGroupRelModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = audiencesEntryGroupRelModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = audiencesEntryGroupRelModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:1065431577