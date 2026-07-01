/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchUndefinedDefaultOrderEntryException;
import com.liferay.portal.tools.service.builder.test.model.UndefinedDefaultOrderEntry;
import com.liferay.portal.tools.service.builder.test.model.UndefinedDefaultOrderEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.UndefinedDefaultOrderEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.UndefinedDefaultOrderEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.UndefinedDefaultOrderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.UndefinedDefaultOrderEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the undefined default order entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UndefinedDefaultOrderEntryPersistenceImpl
	extends BasePersistenceImpl
		<UndefinedDefaultOrderEntry, NoSuchUndefinedDefaultOrderEntryException>
	implements UndefinedDefaultOrderEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UndefinedDefaultOrderEntryUtil</code> to access the undefined default order entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UndefinedDefaultOrderEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<UndefinedDefaultOrderEntry, NoSuchUndefinedDefaultOrderEntryException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the undefined default order entry where name = &#63; or throws a <code>NoSuchUndefinedDefaultOrderEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching undefined default order entry
	 * @throws NoSuchUndefinedDefaultOrderEntryException if a matching undefined default order entry could not be found
	 */
	@Override
	public UndefinedDefaultOrderEntry findByName(String name)
		throws NoSuchUndefinedDefaultOrderEntryException {

		return _uniquePersistenceFinderByName.find(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the undefined default order entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching undefined default order entry, or <code>null</code> if a matching undefined default order entry could not be found
	 */
	@Override
	public UndefinedDefaultOrderEntry fetchByName(
		String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByName.fetch(
			finderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the undefined default order entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the undefined default order entry that was removed
	 */
	@Override
	public UndefinedDefaultOrderEntry removeByName(String name)
		throws NoSuchUndefinedDefaultOrderEntryException {

		UndefinedDefaultOrderEntry undefinedDefaultOrderEntry = findByName(
			name);

		return remove(undefinedDefaultOrderEntry);
	}

	/**
	 * Returns the number of undefined default order entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching undefined default order entries
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			finderCache, new Object[] {name});
	}

	private CollectionPersistenceFinder
		<UndefinedDefaultOrderEntry, NoSuchUndefinedDefaultOrderEntryException>
			_collectionPersistenceFinderByName_Collection;

	/**
	 * Returns an ordered range of all the undefined default order entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UndefinedDefaultOrderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of undefined default order entries
	 * @param end the upper bound of the range of undefined default order entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching undefined default order entries
	 */
	@Override
	public List<UndefinedDefaultOrderEntry> findByName_Collection(
		String name, int start, int end,
		OrderByComparator<UndefinedDefaultOrderEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByName_Collection.find(
			finderCache, new Object[] {name}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first undefined default order entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching undefined default order entry
	 * @throws NoSuchUndefinedDefaultOrderEntryException if a matching undefined default order entry could not be found
	 */
	@Override
	public UndefinedDefaultOrderEntry findByName_Collection_First(
			String name,
			OrderByComparator<UndefinedDefaultOrderEntry> orderByComparator)
		throws NoSuchUndefinedDefaultOrderEntryException {

		return _collectionPersistenceFinderByName_Collection.findFirst(
			finderCache, new Object[] {name}, orderByComparator);
	}

	/**
	 * Returns the first undefined default order entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching undefined default order entry, or <code>null</code> if a matching undefined default order entry could not be found
	 */
	@Override
	public UndefinedDefaultOrderEntry fetchByName_Collection_First(
		String name,
		OrderByComparator<UndefinedDefaultOrderEntry> orderByComparator) {

		return _collectionPersistenceFinderByName_Collection.fetchFirst(
			finderCache, new Object[] {name}, orderByComparator);
	}

	/**
	 * Removes all the undefined default order entries where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName_Collection(String name) {
		_collectionPersistenceFinderByName_Collection.remove(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the number of undefined default order entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching undefined default order entries
	 */
	@Override
	public int countByName_Collection(String name) {
		return _collectionPersistenceFinderByName_Collection.count(
			finderCache, new Object[] {name});
	}

	public UndefinedDefaultOrderEntryPersistenceImpl() {
		setModelClass(UndefinedDefaultOrderEntry.class);

		setModelImplClass(UndefinedDefaultOrderEntryImpl.class);
		setModelPKClass(long.class);

		setTable(UndefinedDefaultOrderEntryTable.INSTANCE);
	}

	/**
	 * Creates a new undefined default order entry with the primary key. Does not add the undefined default order entry to the database.
	 *
	 * @param undefinedDefaultOrderEntryId the primary key for the new undefined default order entry
	 * @return the new undefined default order entry
	 */
	@Override
	public UndefinedDefaultOrderEntry create(
		long undefinedDefaultOrderEntryId) {

		UndefinedDefaultOrderEntry undefinedDefaultOrderEntry =
			new UndefinedDefaultOrderEntryImpl();

		undefinedDefaultOrderEntry.setNew(true);
		undefinedDefaultOrderEntry.setPrimaryKey(undefinedDefaultOrderEntryId);

		return undefinedDefaultOrderEntry;
	}

	/**
	 * Removes the undefined default order entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param undefinedDefaultOrderEntryId the primary key of the undefined default order entry
	 * @return the undefined default order entry that was removed
	 * @throws NoSuchUndefinedDefaultOrderEntryException if a undefined default order entry with the primary key could not be found
	 */
	@Override
	public UndefinedDefaultOrderEntry remove(long undefinedDefaultOrderEntryId)
		throws NoSuchUndefinedDefaultOrderEntryException {

		return remove((Serializable)undefinedDefaultOrderEntryId);
	}

	@Override
	protected UndefinedDefaultOrderEntry removeImpl(
		UndefinedDefaultOrderEntry undefinedDefaultOrderEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(undefinedDefaultOrderEntry)) {
				undefinedDefaultOrderEntry =
					(UndefinedDefaultOrderEntry)session.get(
						UndefinedDefaultOrderEntryImpl.class,
						undefinedDefaultOrderEntry.getPrimaryKeyObj());
			}

			if (undefinedDefaultOrderEntry != null) {
				session.delete(undefinedDefaultOrderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (undefinedDefaultOrderEntry != null) {
			clearCache(undefinedDefaultOrderEntry);
		}

		return undefinedDefaultOrderEntry;
	}

	@Override
	public UndefinedDefaultOrderEntry updateImpl(
		UndefinedDefaultOrderEntry undefinedDefaultOrderEntry) {

		boolean isNew = undefinedDefaultOrderEntry.isNew();

		if (!(undefinedDefaultOrderEntry instanceof
				UndefinedDefaultOrderEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(undefinedDefaultOrderEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					undefinedDefaultOrderEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in undefinedDefaultOrderEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UndefinedDefaultOrderEntry implementation " +
					undefinedDefaultOrderEntry.getClass());
		}

		UndefinedDefaultOrderEntryModelImpl
			undefinedDefaultOrderEntryModelImpl =
				(UndefinedDefaultOrderEntryModelImpl)undefinedDefaultOrderEntry;

		if (!undefinedDefaultOrderEntryModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				undefinedDefaultOrderEntry.setModifiedDate(date);
			}
			else {
				undefinedDefaultOrderEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(undefinedDefaultOrderEntry);
			}
			else {
				undefinedDefaultOrderEntry =
					(UndefinedDefaultOrderEntry)session.merge(
						undefinedDefaultOrderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(undefinedDefaultOrderEntry, false);

		if (isNew) {
			undefinedDefaultOrderEntry.setNew(false);
		}

		undefinedDefaultOrderEntry.resetOriginalValues();

		return undefinedDefaultOrderEntry;
	}

	/**
	 * Returns the undefined default order entry with the primary key or throws a <code>NoSuchUndefinedDefaultOrderEntryException</code> if it could not be found.
	 *
	 * @param undefinedDefaultOrderEntryId the primary key of the undefined default order entry
	 * @return the undefined default order entry
	 * @throws NoSuchUndefinedDefaultOrderEntryException if a undefined default order entry with the primary key could not be found
	 */
	@Override
	public UndefinedDefaultOrderEntry findByPrimaryKey(
			long undefinedDefaultOrderEntryId)
		throws NoSuchUndefinedDefaultOrderEntryException {

		return findByPrimaryKey((Serializable)undefinedDefaultOrderEntryId);
	}

	/**
	 * Returns the undefined default order entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param undefinedDefaultOrderEntryId the primary key of the undefined default order entry
	 * @return the undefined default order entry, or <code>null</code> if a undefined default order entry with the primary key could not be found
	 */
	@Override
	public UndefinedDefaultOrderEntry fetchByPrimaryKey(
		long undefinedDefaultOrderEntryId) {

		return fetchByPrimaryKey((Serializable)undefinedDefaultOrderEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "undefinedDefaultOrderEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_UNDEFINEDDEFAULTORDERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UndefinedDefaultOrderEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the undefined default order entry persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false,
				convertNullFunction(UndefinedDefaultOrderEntry::getName)),
			_SQL_SELECT_UNDEFINEDDEFAULTORDERENTRY_WHERE, "",
			new FinderColumn<>(
				"undefinedDefaultOrderEntry.", "name", FinderColumn.Type.STRING,
				"=", true, true, UndefinedDefaultOrderEntry::getName));

		_collectionPersistenceFinderByName_Collection =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByName_Collection",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByName_Collection",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByName_Collection",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, false, null),
				_SQL_SELECT_UNDEFINEDDEFAULTORDERENTRY_WHERE,
				_SQL_COUNT_UNDEFINEDDEFAULTORDERENTRY_WHERE,
				UndefinedDefaultOrderEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"undefinedDefaultOrderEntry.", "name",
					FinderColumn.Type.STRING, "=", true, true,
					UndefinedDefaultOrderEntry::getName));

		UndefinedDefaultOrderEntryUtil.setPersistence(this);
	}

	public void destroy() {
		UndefinedDefaultOrderEntryUtil.setPersistence(null);

		entityCache.removeCache(UndefinedDefaultOrderEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		UndefinedDefaultOrderEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_UNDEFINEDDEFAULTORDERENTRY =
		"SELECT undefinedDefaultOrderEntry FROM UndefinedDefaultOrderEntry undefinedDefaultOrderEntry";

	private static final String _SQL_SELECT_UNDEFINEDDEFAULTORDERENTRY_WHERE =
		"SELECT undefinedDefaultOrderEntry FROM UndefinedDefaultOrderEntry undefinedDefaultOrderEntry WHERE ";

	private static final String _SQL_COUNT_UNDEFINEDDEFAULTORDERENTRY_WHERE =
		"SELECT COUNT(undefinedDefaultOrderEntry) FROM UndefinedDefaultOrderEntry undefinedDefaultOrderEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1892088585