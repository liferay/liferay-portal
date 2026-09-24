/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from LayoutPageTemplateStructureRelElementVariation.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationImpl",
		"table.name=LPTSRelElementVariation"
	},
	service = ArgumentsResolver.class
)
public class
	LayoutPageTemplateStructureRelElementVariationModelArgumentsResolver
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

		LayoutPageTemplateStructureRelElementVariationModelImpl
			layoutPageTemplateStructureRelElementVariationModelImpl =
				(LayoutPageTemplateStructureRelElementVariationModelImpl)
					baseModel;

		long columnBitmask =
			layoutPageTemplateStructureRelElementVariationModelImpl.
				getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				layoutPageTemplateStructureRelElementVariationModelImpl,
				finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					layoutPageTemplateStructureRelElementVariationModelImpl.
						getColumnBitmask(columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				layoutPageTemplateStructureRelElementVariationModelImpl,
				finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return LayoutPageTemplateStructureRelElementVariationImpl.class.
			getName();
	}

	@Override
	public String getTableName() {
		return LayoutPageTemplateStructureRelElementVariationTable.INSTANCE.
			getTableName();
	}

	private static Object[] _getValue(
		LayoutPageTemplateStructureRelElementVariationModelImpl
			layoutPageTemplateStructureRelElementVariationModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					layoutPageTemplateStructureRelElementVariationModelImpl.
						getColumnOriginalValue(columnName);
			}
			else {
				value =
					layoutPageTemplateStructureRelElementVariationModelImpl.
						getColumnValue(columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:1358746632