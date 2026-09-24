/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.rel.service.persistence.impl;

import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRelTable;
import com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelImpl;
import com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelModelImpl;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;

/**
 * The arguments resolver class for retrieving value from AssetEntryAssetCategoryRel.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	property = {
		"class.name=com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelImpl",
		"table.name=AssetEntryAssetCategoryRel"
	},
	service = ArgumentsResolver.class
)
public class AssetEntryAssetCategoryRelModelArgumentsResolver
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

		AssetEntryAssetCategoryRelModelImpl
			assetEntryAssetCategoryRelModelImpl =
				(AssetEntryAssetCategoryRelModelImpl)baseModel;

		long columnBitmask =
			assetEntryAssetCategoryRelModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				assetEntryAssetCategoryRelModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					assetEntryAssetCategoryRelModelImpl.getColumnBitmask(
						columnName);
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				assetEntryAssetCategoryRelModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return AssetEntryAssetCategoryRelImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return AssetEntryAssetCategoryRelTable.INSTANCE.getTableName();
	}

	private static Object[] _getValue(
		AssetEntryAssetCategoryRelModelImpl assetEntryAssetCategoryRelModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value =
					assetEntryAssetCategoryRelModelImpl.getColumnOriginalValue(
						columnName);
			}
			else {
				value = assetEntryAssetCategoryRelModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();

}
// LIFERAY-SERVICE-BUILDER-HASH:1073687815