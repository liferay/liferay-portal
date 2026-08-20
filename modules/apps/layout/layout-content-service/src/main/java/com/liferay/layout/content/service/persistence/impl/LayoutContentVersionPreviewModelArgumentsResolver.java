/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.persistence.impl;

import com.liferay.layout.content.model.LayoutContentVersionPreviewTable;
import com.liferay.layout.content.model.impl.LayoutContentVersionPreviewImpl;
import com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from LayoutContentVersionPreview.
 *
 * @author Lourdes Fernández Besada
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.layout.content.model.impl.LayoutContentVersionPreviewImpl",
		"table.name=LayoutContentVersionPreview"
	},
	service = ArgumentsResolver.class
)
public class LayoutContentVersionPreviewModelArgumentsResolver
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

		LayoutContentVersionPreviewModelImpl
			layoutContentVersionPreviewModelImpl =
				(LayoutContentVersionPreviewModelImpl)baseModel;

		long columnBitmask =
			layoutContentVersionPreviewModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				layoutContentVersionPreviewModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					layoutContentVersionPreviewModelImpl.getColumnBitmask(
						columnName);
			}

			if (finderPath.isBaseModelResult() &&
				(LayoutContentVersionPreviewPersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				layoutContentVersionPreviewModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return LayoutContentVersionPreviewImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return LayoutContentVersionPreviewTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		LayoutContentVersionPreviewModelImpl
			layoutContentVersionPreviewModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					layoutContentVersionPreviewModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				value = layoutContentVersionPreviewModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

	private static final long _ORDER_BY_COLUMNS_BITMASK;

	static {
		long orderByColumnsBitmask = 0;

		orderByColumnsBitmask |=
			LayoutContentVersionPreviewModelImpl.getColumnBitmask("createDate");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1255284489