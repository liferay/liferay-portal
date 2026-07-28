/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDateEntryException;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;
import com.liferay.portal.tools.service.builder.test.model.DateEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DateEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * The persistence implementation for the date entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DateEntryPersistenceImpl
	extends BasePersistenceImpl<DateEntry, NoSuchDateEntryException>
	implements DateEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DateEntryUtil</code> to access the date entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DateEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public DateEntryPersistenceImpl() {
		setModelClass(DateEntry.class);

		setModelImplClass(DateEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DateEntryTable.INSTANCE);
	}

	/**
	 * Creates a new date entry with the primary key. Does not add the date entry to the database.
	 *
	 * @param dateEntryId the primary key for the new date entry
	 * @return the new date entry
	 */
	@Override
	public DateEntry create(long dateEntryId) {
		DateEntry dateEntry = new DateEntryImpl();

		dateEntry.setNew(true);
		dateEntry.setPrimaryKey(dateEntryId);

		return dateEntry;
	}

	/**
	 * Removes the date entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry that was removed
	 * @throws NoSuchDateEntryException if a date entry with the primary key could not be found
	 */
	@Override
	public DateEntry remove(long dateEntryId) throws NoSuchDateEntryException {
		return remove((Serializable)dateEntryId);
	}

	@Override
	protected DateEntry removeImpl(DateEntry dateEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dateEntry)) {
				dateEntry = (DateEntry)session.get(
					DateEntryImpl.class, dateEntry.getPrimaryKeyObj());
			}

			if (dateEntry != null) {
				session.delete(dateEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dateEntry != null) {
			clearCache(dateEntry);
		}

		return dateEntry;
	}

	@Override
	public DateEntry updateImpl(DateEntry dateEntry) {
		boolean isNew = dateEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dateEntry);
			}
			else {
				dateEntry = (DateEntry)session.merge(dateEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dateEntry, false);

		if (isNew) {
			dateEntry.setNew(false);
		}

		dateEntry.resetOriginalValues();

		return dateEntry;
	}

	/**
	 * Returns the date entry with the primary key or throws a <code>NoSuchDateEntryException</code> if it could not be found.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry
	 * @throws NoSuchDateEntryException if a date entry with the primary key could not be found
	 */
	@Override
	public DateEntry findByPrimaryKey(long dateEntryId)
		throws NoSuchDateEntryException {

		return findByPrimaryKey((Serializable)dateEntryId);
	}

	/**
	 * Returns the date entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry, or <code>null</code> if a date entry with the primary key could not be found
	 */
	@Override
	public DateEntry fetchByPrimaryKey(long dateEntryId) {
		return fetchByPrimaryKey((Serializable)dateEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dateEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DATEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DateEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the date entry persistence.
	 */
	public void afterPropertiesSet() {
		DateEntryUtil.setPersistence(this);
	}

	public void destroy() {
		DateEntryUtil.setPersistence(null);

		entityCache.removeCache(DateEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DATEENTRY =
		"SELECT dateEntry FROM DateEntry dateEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-965989843