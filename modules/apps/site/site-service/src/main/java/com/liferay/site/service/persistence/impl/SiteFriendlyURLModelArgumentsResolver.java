/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.site.model.SiteFriendlyURLTable;
import com.liferay.site.model.impl.SiteFriendlyURLImpl;
import com.liferay.site.model.impl.SiteFriendlyURLModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from SiteFriendlyURL.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.site.model.impl.SiteFriendlyURLImpl",
		"table.name=SiteFriendlyURL"
	},
	service = ArgumentsResolver.class
)
public class SiteFriendlyURLModelArgumentsResolver
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

		SiteFriendlyURLModelImpl siteFriendlyURLModelImpl =
			(SiteFriendlyURLModelImpl)baseModel;

		long columnBitmask = siteFriendlyURLModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(siteFriendlyURLModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					siteFriendlyURLModelImpl.getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(siteFriendlyURLModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return SiteFriendlyURLImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return SiteFriendlyURLTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		SiteFriendlyURLModelImpl siteFriendlyURLModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = siteFriendlyURLModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = siteFriendlyURLModelImpl.getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:1658190275