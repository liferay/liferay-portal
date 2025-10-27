/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service.persistence.impl;

import com.liferay.analytics.message.storage.model.AnalyticsMessageTable;
import com.liferay.analytics.message.storage.model.impl.AnalyticsMessageImpl;
import com.liferay.analytics.message.storage.model.impl.AnalyticsMessageModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from AnalyticsMessage.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.analytics.message.storage.model.impl.AnalyticsMessageImpl",
		"table.name=AnalyticsMessage"
	},
	service = ArgumentsResolver.class
)
public class AnalyticsMessageModelArgumentsResolver
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

		AnalyticsMessageModelImpl analyticsMessageModelImpl =
			(AnalyticsMessageModelImpl)baseModel;

		if (!checkColumn ||
			_hasModifiedColumns(analyticsMessageModelImpl, columnNames) ||
			_hasModifiedColumns(analyticsMessageModelImpl, _ORDER_BY_COLUMNS)) {

			return _getValue(analyticsMessageModelImpl, columnNames, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return AnalyticsMessageImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return AnalyticsMessageTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		AnalyticsMessageModelImpl analyticsMessageModelImpl,
		String[] columnNames, boolean original) {

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			if (original) {
				arguments[i] = analyticsMessageModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				arguments[i] = analyticsMessageModelImpl.getColumnValue(
					columnName);
			}
		}

		return arguments;
	}

	private static boolean _hasModifiedColumns(
		AnalyticsMessageModelImpl analyticsMessageModelImpl,
		String[] columnNames) {

		if (columnNames.length == 0) {
			return false;
		}

		for (String columnName : columnNames) {
			if (!Objects.equals(
					analyticsMessageModelImpl.getColumnOriginalValue(
						columnName),
					analyticsMessageModelImpl.getColumnValue(columnName))) {

				return true;
			}
		}

		return false;
	}

	private static final String[] _ORDER_BY_COLUMNS;

	static {
		List<String> orderByColumns = new ArrayList<String>();

		_ORDER_BY_COLUMNS = orderByColumns.toArray(new String[0]);
	}

}