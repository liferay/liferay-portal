/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchColumnNameEntryException;
import com.liferay.portal.tools.service.builder.test.model.ColumnNameEntry;
import com.liferay.portal.tools.service.builder.test.model.ColumnNameEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.ColumnNameEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.ColumnNameEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.ColumnNameEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ColumnNameEntryUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the column name entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ColumnNameEntryPersistenceImpl
	extends BasePersistenceImpl<ColumnNameEntry, NoSuchColumnNameEntryException>
	implements ColumnNameEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ColumnNameEntryUtil</code> to access the column name entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ColumnNameEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public ColumnNameEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("columnNameEntryId", "cNameEntryId");

		setDBColumnNames(dbColumnNames);

		setModelClass(ColumnNameEntry.class);

		setModelImplClass(ColumnNameEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ColumnNameEntryTable.INSTANCE);
	}

	/**
	 * Creates a new column name entry with the primary key. Does not add the column name entry to the database.
	 *
	 * @param columnNameEntryId the primary key for the new column name entry
	 * @return the new column name entry
	 */
	@Override
	public ColumnNameEntry create(long columnNameEntryId) {
		ColumnNameEntry columnNameEntry = new ColumnNameEntryImpl();

		columnNameEntry.setNew(true);
		columnNameEntry.setPrimaryKey(columnNameEntryId);

		return columnNameEntry;
	}

	/**
	 * Removes the column name entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry that was removed
	 * @throws NoSuchColumnNameEntryException if a column name entry with the primary key could not be found
	 */
	@Override
	public ColumnNameEntry remove(long columnNameEntryId)
		throws NoSuchColumnNameEntryException {

		return remove((Serializable)columnNameEntryId);
	}

	@Override
	protected ColumnNameEntry removeImpl(ColumnNameEntry columnNameEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(columnNameEntry)) {
				columnNameEntry = (ColumnNameEntry)session.get(
					ColumnNameEntryImpl.class,
					columnNameEntry.getPrimaryKeyObj());
			}

			if (columnNameEntry != null) {
				session.delete(columnNameEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (columnNameEntry != null) {
			clearCache(columnNameEntry);
		}

		return columnNameEntry;
	}

	@Override
	public ColumnNameEntry updateImpl(ColumnNameEntry columnNameEntry) {
		boolean isNew = columnNameEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(columnNameEntry);
			}
			else {
				columnNameEntry = (ColumnNameEntry)session.merge(
					columnNameEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(columnNameEntry, false);

		if (isNew) {
			columnNameEntry.setNew(false);
		}

		columnNameEntry.resetOriginalValues();

		return columnNameEntry;
	}

	/**
	 * Returns the column name entry with the primary key or throws a <code>NoSuchColumnNameEntryException</code> if it could not be found.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry
	 * @throws NoSuchColumnNameEntryException if a column name entry with the primary key could not be found
	 */
	@Override
	public ColumnNameEntry findByPrimaryKey(long columnNameEntryId)
		throws NoSuchColumnNameEntryException {

		return findByPrimaryKey((Serializable)columnNameEntryId);
	}

	/**
	 * Returns the column name entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry, or <code>null</code> if a column name entry with the primary key could not be found
	 */
	@Override
	public ColumnNameEntry fetchByPrimaryKey(long columnNameEntryId) {
		return fetchByPrimaryKey((Serializable)columnNameEntryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "cNameEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COLUMNNAMEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ColumnNameEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the column name entry persistence.
	 */
	public void afterPropertiesSet() {
		ColumnNameEntryUtil.setPersistence(this);
	}

	public void destroy() {
		ColumnNameEntryUtil.setPersistence(null);

		entityCache.removeCache(ColumnNameEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_COLUMNNAMEENTRY =
		"SELECT columnNameEntry FROM ColumnNameEntry columnNameEntry";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"columnNameEntryId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:37391615