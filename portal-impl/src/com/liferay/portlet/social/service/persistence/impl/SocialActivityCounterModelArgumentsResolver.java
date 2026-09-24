/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portlet.social.model.impl.SocialActivityCounterImpl;
import com.liferay.portlet.social.model.impl.SocialActivityCounterModelImpl;
import com.liferay.social.kernel.model.SocialActivityCounterTable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

/**
 * The arguments resolver class for retrieving value from SocialActivityCounter.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"class.name=com.liferay.portlet.social.model.impl.SocialActivityCounterImpl",
		"table.name=SocialActivityCounter"
	},
	service = ArgumentsResolver.class
)
public class SocialActivityCounterModelArgumentsResolver
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

		SocialActivityCounterModelImpl socialActivityCounterModelImpl =
			(SocialActivityCounterModelImpl)baseModel;

		BiPredicate<SocialActivityCounterModelImpl, Boolean> whereBiPredicate =
			_whereBiPredicates.get(finderPath.getFinderName());

		if ((whereBiPredicate != null) &&
			!whereBiPredicate.test(socialActivityCounterModelImpl, original)) {

			return null;
		}

		long columnBitmask = socialActivityCounterModelImpl.getColumnBitmask();

		if (!checkColumn || (columnBitmask == 0)) {
			return _getValue(
				socialActivityCounterModelImpl, finderPath, original);
		}

		Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
			finderPath);

		if (finderPathColumnBitmask == null) {
			finderPathColumnBitmask = 0L;

			for (String columnName : columnNames) {
				finderPathColumnBitmask |=
					socialActivityCounterModelImpl.getColumnBitmask(columnName);
			}

			Long whereColumnBitmask = _whereColumnBitmasks.get(
				finderPath.getFinderName());

			if (whereColumnBitmask != null) {
				finderPathColumnBitmask |= whereColumnBitmask;
			}

			_finderPathColumnBitmasksCache.put(
				finderPath, finderPathColumnBitmask);
		}

		if ((columnBitmask & finderPathColumnBitmask) != 0) {
			return _getValue(
				socialActivityCounterModelImpl, finderPath, original);
		}

		return null;
	}

	@Override
	public String getClassName() {
		return SocialActivityCounterImpl.class.getName();
	}

	@Override
	public String getTableName() {
		return SocialActivityCounterTable.INSTANCE.getTableName();
	}

	private static Object _getColumnValue(
		SocialActivityCounterModelImpl socialActivityCounterModelImpl,
		String columnName, boolean original) {

		if (original) {
			return socialActivityCounterModelImpl.getColumnOriginalValue(
				columnName);
		}

		return socialActivityCounterModelImpl.getColumnValue(columnName);
	}

	private static Object[] _getValue(
		SocialActivityCounterModelImpl socialActivityCounterModelImpl,
		FinderPath finderPath, boolean original) {

		String[] columnNames = finderPath.getColumnNames();

		Object[] arguments = new Object[columnNames.length];

		for (int i = 0; i < arguments.length; i++) {
			String columnName = columnNames[i];

			Object value;

			if (original) {
				value = socialActivityCounterModelImpl.getColumnOriginalValue(
					columnName);
			}
			else {
				value = socialActivityCounterModelImpl.getColumnValue(
					columnName);
			}

			arguments[i] = finderPath.normalizeArgument(i, value);
		}

		return arguments;
	}

	private static final Map<FinderPath, Long> _finderPathColumnBitmasksCache =
		new ConcurrentHashMap<>();
	private static final Map<String, Long> _whereColumnBitmasks =
		new HashMap<>();
	private static final Map
		<String, BiPredicate<SocialActivityCounterModelImpl, Boolean>>
			_whereBiPredicates = new HashMap<>();

	static {
		long whereColumnBitmask =
			SocialActivityCounterModelImpl.getColumnBitmask("endPeriod");

		BiPredicate<SocialActivityCounterModelImpl, Boolean> whereBiPredicate =
			(socialActivityCounterModelImpl, original) ->
				GetterUtil.getInteger(
					_getColumnValue(
						socialActivityCounterModelImpl, "endPeriod",
						original)) == -1;

		_whereColumnBitmasks.put("G_C_C_O", whereColumnBitmask);

		_whereBiPredicates.put("G_C_C_O", whereBiPredicate);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1277532371