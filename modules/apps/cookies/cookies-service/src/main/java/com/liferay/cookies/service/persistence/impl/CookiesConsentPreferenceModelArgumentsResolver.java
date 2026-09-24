/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.service.persistence.impl;

import com.liferay.cookies.model.CookiesConsentPreferenceTable;
import com.liferay.cookies.model.impl.CookiesConsentPreferenceImpl;
import com.liferay.cookies.model.impl.CookiesConsentPreferenceModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from CookiesConsentPreference.
 *
 * @author Christopher Kian
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.cookies.model.impl.CookiesConsentPreferenceImpl",
		"table.name=CookiesConsentPreference"
	},
	service = ArgumentsResolver.class
)
public class CookiesConsentPreferenceModelArgumentsResolver
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

		CookiesConsentPreferenceModelImpl cookiesConsentPreferenceModelImpl =
			(CookiesConsentPreferenceModelImpl)baseModel;

		long columnBitmask =
			cookiesConsentPreferenceModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				cookiesConsentPreferenceModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					cookiesConsentPreferenceModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				cookiesConsentPreferenceModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return CookiesConsentPreferenceImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return CookiesConsentPreferenceTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		CookiesConsentPreferenceModelImpl cookiesConsentPreferenceModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					cookiesConsentPreferenceModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				value = cookiesConsentPreferenceModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:-2019677400