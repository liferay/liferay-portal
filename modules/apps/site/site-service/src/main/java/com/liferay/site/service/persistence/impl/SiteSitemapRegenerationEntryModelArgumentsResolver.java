/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.site.model.SiteSitemapRegenerationEntryTable;
import com.liferay.site.model.impl.SiteSitemapRegenerationEntryImpl;
import com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from SiteSitemapRegenerationEntry.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.site.model.impl.SiteSitemapRegenerationEntryImpl",
		"table.name=SiteSitemapRegenerationEntry"
	},
	service = ArgumentsResolver.class
)
public class SiteSitemapRegenerationEntryModelArgumentsResolver
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

		SiteSitemapRegenerationEntryModelImpl
			siteSitemapRegenerationEntryModelImpl =
				(SiteSitemapRegenerationEntryModelImpl)baseModel;

		long columnBitmask =
			siteSitemapRegenerationEntryModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				siteSitemapRegenerationEntryModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					siteSitemapRegenerationEntryModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				siteSitemapRegenerationEntryModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return SiteSitemapRegenerationEntryImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return SiteSitemapRegenerationEntryTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		SiteSitemapRegenerationEntryModelImpl
			siteSitemapRegenerationEntryModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					siteSitemapRegenerationEntryModelImpl.
						getColumnOriginalValue(columnName);
			}
			else {
				value = siteSitemapRegenerationEntryModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:1256570693