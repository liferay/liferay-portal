/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;

/**
 * @author Shuyang Zhou
 */
public abstract class BasePersistenceFinder
	<T extends BaseModel<T>, E extends NoSuchModelException> {

	public String buildNoSuchKeyMessage(Object[] values) {
		StringBundler sb = new StringBundler((finderColumns.length * 3) + 1);

		sb.append(basePersistenceImpl.getNoSuchEntityWithKeyPrefix());

		for (int i = 0; i < finderColumns.length; i++) {
			sb.append(finderColumns[i].getKeyFragment());

			Object value = values[i];

			if (finderColumns[i] instanceof ArrayableFinderColumn) {
				value = finderColumns[i].toFinderArg(value);
			}

			sb.append(value);
			sb.append(", ");
		}

		sb.setStringAt("}", sb.index() - 1);

		return sb.toString();
	}

	@SafeVarargs
	protected BasePersistenceFinder(
		BasePersistenceImpl<T, E> basePersistenceImpl, String sqlSelectWhere,
		String where, String dbWhere, FinderColumn<T>... finderColumns) {

		if (finderColumns.length == 0) {
			throw new IllegalArgumentException("Missing finder columns");
		}

		this.basePersistenceImpl = basePersistenceImpl;
		this.sqlSelectWhere = sqlSelectWhere;
		this.where = where;
		this.dbWhere = dbWhere;
		this.finderColumns = finderColumns;
	}

	protected void bindQueryParams(QueryPos queryPos, Object[] values) {
		for (int i = 0; i < finderColumns.length; i++) {
			finderColumns[i].bindValue(queryPos, values[i]);
		}
	}

	protected Object[] buildFinderArgs(Object[] values) {
		Object[] finderArgs = new Object[finderColumns.length];

		for (int i = 0; i < finderColumns.length; i++) {
			finderArgs[i] = finderColumns[i].toFinderArg(values[i]);
		}

		return finderArgs;
	}

	protected String buildSQLWhere(
		String sqlWhere, Object[] values, boolean sqlQuery) {

		StringBundler sb = new StringBundler((finderColumns.length * 2) + 2);

		sb.append(sqlWhere);

		for (int i = 0; i < finderColumns.length; i++) {
			String fragment = finderColumns[i].getSqlFragment(
				values[i], sqlQuery);

			if (fragment.isEmpty()) {
				continue;
			}

			sb.append(fragment);
			sb.append(" AND ");
		}

		String whereClause = sqlQuery ? dbWhere : where;

		if (!whereClause.isEmpty()) {
			sb.append(whereClause);
		}
		else if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	protected boolean matchesAll(T entity, Object[] values) {
		for (int i = 0; i < finderColumns.length; i++) {
			if (!finderColumns[i].matches(entity, values[i])) {
				return false;
			}
		}

		return true;
	}

	protected void normalizeValues(Object[] values) {
		for (int i = 0; i < finderColumns.length; i++) {
			values[i] = finderColumns[i].normalizeValue(values[i]);
		}
	}

	@SuppressWarnings("unchecked")
	protected SafeCloseable setCTCollectionIdWithSafeCloseable() {
		CTPersistenceHelper ctPersistenceHelper =
			basePersistenceImpl.getCTPersistenceHelper();

		if (ctPersistenceHelper == null) {
			return _NO_OP_SAFE_CLOSEABLE;
		}

		return ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
			(Class)basePersistenceImpl.getModelClass());
	}

	protected final BasePersistenceImpl<T, E> basePersistenceImpl;
	protected final String dbWhere;
	protected final FinderColumn<T>[] finderColumns;
	protected final String sqlSelectWhere;
	protected final String where;

	private static final SafeCloseable _NO_OP_SAFE_CLOSEABLE = () -> {
	};

}