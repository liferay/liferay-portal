/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRelTable;
import com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelImpl;
import com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from SegmentsExperienceAudienceEntryRel.
 *
 * @author Eduardo Garcia
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelImpl",
		"table.name=SExperienceAudienceEntryRel"
	},
	service = ArgumentsResolver.class
)
public class SegmentsExperienceAudienceEntryRelModelArgumentsResolver
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

		SegmentsExperienceAudienceEntryRelModelImpl
			segmentsExperienceAudienceEntryRelModelImpl =
				(SegmentsExperienceAudienceEntryRelModelImpl)baseModel;

		long columnBitmask =
			segmentsExperienceAudienceEntryRelModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				segmentsExperienceAudienceEntryRelModelImpl, finderPath,
				original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					segmentsExperienceAudienceEntryRelModelImpl.
						getColumnBitmask(columnName);
			}

			if (finderPath.isBaseModelResult() &&
				(SegmentsExperienceAudienceEntryRelPersistenceImpl.
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
						finderPath.getCacheName())) {

				finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				segmentsExperienceAudienceEntryRelModelImpl, finderPath,
				original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return SegmentsExperienceAudienceEntryRelImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return SegmentsExperienceAudienceEntryRelTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		SegmentsExperienceAudienceEntryRelModelImpl
			segmentsExperienceAudienceEntryRelModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					segmentsExperienceAudienceEntryRelModelImpl.
						getColumnOriginalValue(columnName);
			}
			else {
				value =
					segmentsExperienceAudienceEntryRelModelImpl.getColumnValue(
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
			SegmentsExperienceAudienceEntryRelModelImpl.getColumnBitmask(
				"priority");

		_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1771213345