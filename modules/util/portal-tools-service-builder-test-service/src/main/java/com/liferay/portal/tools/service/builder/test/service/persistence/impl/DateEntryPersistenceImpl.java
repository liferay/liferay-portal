/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDateEntryException;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;
import com.liferay.portal.tools.service.builder.test.model.DateEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.DateEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
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

	private CollectionPersistenceFinder<DateEntry, NoSuchDateEntryException>
		_collectionPersistenceFinderBySnapshotDate;

	/**
	 * Returns an ordered range of all the date entries where snapshotDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param snapshotDate the snapshot date
	 * @param start the lower bound of the range of date entries
	 * @param end the upper bound of the range of date entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching date entries
	 */
	@Override
	public List<DateEntry> findBySnapshotDate(
		Date snapshotDate, int start, int end,
		OrderByComparator<DateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySnapshotDate.find(
			finderCache, new Object[] {snapshotDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first date entry in the ordered set where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching date entry
	 * @throws NoSuchDateEntryException if a matching date entry could not be found
	 */
	@Override
	public DateEntry findBySnapshotDate_First(
			Date snapshotDate, OrderByComparator<DateEntry> orderByComparator)
		throws NoSuchDateEntryException {

		return _collectionPersistenceFinderBySnapshotDate.findFirst(
			finderCache, new Object[] {snapshotDate}, orderByComparator);
	}

	/**
	 * Returns the first date entry in the ordered set where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching date entry, or <code>null</code> if a matching date entry could not be found
	 */
	@Override
	public DateEntry fetchBySnapshotDate_First(
		Date snapshotDate, OrderByComparator<DateEntry> orderByComparator) {

		return _collectionPersistenceFinderBySnapshotDate.fetchFirst(
			finderCache, new Object[] {snapshotDate}, orderByComparator);
	}

	/**
	 * Removes all the date entries where snapshotDate = &#63; from the database.
	 *
	 * @param snapshotDate the snapshot date
	 */
	@Override
	public void removeBySnapshotDate(Date snapshotDate) {
		_collectionPersistenceFinderBySnapshotDate.remove(
			finderCache, new Object[] {snapshotDate});
	}

	/**
	 * Returns the number of date entries where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @return the number of matching date entries
	 */
	@Override
	public int countBySnapshotDate(Date snapshotDate) {
		return _collectionPersistenceFinderBySnapshotDate.count(
			finderCache, new Object[] {snapshotDate});
	}

	private UniquePersistenceFinder<DateEntry, NoSuchDateEntryException>
		_uniquePersistenceFinderByC_S;

	/**
	 * Returns the date entry where companyId = &#63; and snapshotDate = &#63; or throws a <code>NoSuchDateEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the matching date entry
	 * @throws NoSuchDateEntryException if a matching date entry could not be found
	 */
	@Override
	public DateEntry findByC_S(long companyId, Date snapshotDate)
		throws NoSuchDateEntryException {

		return _uniquePersistenceFinderByC_S.find(
			finderCache, new Object[] {companyId, snapshotDate});
	}

	/**
	 * Returns the date entry where companyId = &#63; and snapshotDate = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching date entry, or <code>null</code> if a matching date entry could not be found
	 */
	@Override
	public DateEntry fetchByC_S(
		long companyId, Date snapshotDate, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_S.fetch(
			finderCache, new Object[] {companyId, snapshotDate},
			useFinderCache);
	}

	/**
	 * Removes the date entry where companyId = &#63; and snapshotDate = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the date entry that was removed
	 */
	@Override
	public DateEntry removeByC_S(long companyId, Date snapshotDate)
		throws NoSuchDateEntryException {

		DateEntry dateEntry = findByC_S(companyId, snapshotDate);

		return remove(dateEntry);
	}

	/**
	 * Returns the number of date entries where companyId = &#63; and snapshotDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the number of matching date entries
	 */
	@Override
	public int countByC_S(long companyId, Date snapshotDate) {
		return _uniquePersistenceFinderByC_S.count(
			finderCache, new Object[] {companyId, snapshotDate});
	}

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

		dateEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

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

		if (!(dateEntry instanceof DateEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dateEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(dateEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dateEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DateEntry implementation " +
					dateEntry.getClass());
		}

		DateEntryModelImpl dateEntryModelImpl = (DateEntryModelImpl)dateEntry;

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
		_collectionPersistenceFinderBySnapshotDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySnapshotDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"snapshotDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySnapshotDate", new String[] {Date.class.getName()},
					new String[] {"snapshotDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySnapshotDate", new String[] {Date.class.getName()},
					new String[] {"snapshotDate"}, false),
				_SQL_SELECT_DATEENTRY_WHERE, _SQL_COUNT_DATEENTRY_WHERE,
				DateEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"dateEntry.", "snapshotDate", FinderColumn.Type.DATE, "=",
					true, true, DateEntry::getSnapshotDate));

		_uniquePersistenceFinderByC_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_S",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"companyId", "snapshotDate"}, 0, 0, false,
				DateEntry::getCompanyId,
				convertDateFunction(DateEntry::getSnapshotDate)),
			_SQL_SELECT_DATEENTRY_WHERE, "",
			new FinderColumn<>(
				"dateEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, DateEntry::getCompanyId),
			new FinderColumn<>(
				"dateEntry.", "snapshotDate", FinderColumn.Type.DATE, "=", true,
				true, DateEntry::getSnapshotDate));

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

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		DateEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DATEENTRY =
		"SELECT dateEntry FROM DateEntry dateEntry";

	private static final String _SQL_SELECT_DATEENTRY_WHERE =
		"SELECT dateEntry FROM DateEntry dateEntry WHERE ";

	private static final String _SQL_COUNT_DATEENTRY_WHERE =
		"SELECT COUNT(dateEntry) FROM DateEntry dateEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:614687324