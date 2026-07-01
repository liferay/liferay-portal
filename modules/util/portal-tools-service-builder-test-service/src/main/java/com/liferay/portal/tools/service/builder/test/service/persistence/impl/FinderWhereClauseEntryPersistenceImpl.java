/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFinderWhereClauseEntryException;
import com.liferay.portal.tools.service.builder.test.model.FinderWhereClauseEntry;
import com.liferay.portal.tools.service.builder.test.model.FinderWhereClauseEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderWhereClauseEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.FinderWhereClauseEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the finder where clause entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class FinderWhereClauseEntryPersistenceImpl
	extends BasePersistenceImpl
		<FinderWhereClauseEntry, NoSuchFinderWhereClauseEntryException>
	implements FinderWhereClauseEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FinderWhereClauseEntryUtil</code> to access the finder where clause entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FinderWhereClauseEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FinderWhereClauseEntry, NoSuchFinderWhereClauseEntryException>
			_collectionPersistenceFinderByName_Nickname;

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	@Override
	public List<FinderWhereClauseEntry> findByName_Nickname(
		String name, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByName_Nickname.find(
			finderCache, new Object[] {name}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	@Override
	public FinderWhereClauseEntry findByName_Nickname_First(
			String name,
			OrderByComparator<FinderWhereClauseEntry> orderByComparator)
		throws NoSuchFinderWhereClauseEntryException {

		return _collectionPersistenceFinderByName_Nickname.findFirst(
			finderCache, new Object[] {name}, orderByComparator);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	@Override
	public FinderWhereClauseEntry fetchByName_Nickname_First(
		String name,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return _collectionPersistenceFinderByName_Nickname.fetchFirst(
			finderCache, new Object[] {name}, orderByComparator);
	}

	/**
	 * Removes all the finder where clause entries where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName_Nickname(String name) {
		_collectionPersistenceFinderByName_Nickname.remove(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the number of finder where clause entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching finder where clause entries
	 */
	@Override
	public int countByName_Nickname(String name) {
		return _collectionPersistenceFinderByName_Nickname.count(
			finderCache, new Object[] {name});
	}

	public FinderWhereClauseEntryPersistenceImpl() {
		setModelClass(FinderWhereClauseEntry.class);

		setModelImplClass(FinderWhereClauseEntryImpl.class);
		setModelPKClass(long.class);

		setTable(FinderWhereClauseEntryTable.INSTANCE);
	}

	/**
	 * Creates a new finder where clause entry with the primary key. Does not add the finder where clause entry to the database.
	 *
	 * @param finderWhereClauseEntryId the primary key for the new finder where clause entry
	 * @return the new finder where clause entry
	 */
	@Override
	public FinderWhereClauseEntry create(long finderWhereClauseEntryId) {
		FinderWhereClauseEntry finderWhereClauseEntry =
			new FinderWhereClauseEntryImpl();

		finderWhereClauseEntry.setNew(true);
		finderWhereClauseEntry.setPrimaryKey(finderWhereClauseEntryId);

		return finderWhereClauseEntry;
	}

	/**
	 * Removes the finder where clause entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry that was removed
	 * @throws NoSuchFinderWhereClauseEntryException if a finder where clause entry with the primary key could not be found
	 */
	@Override
	public FinderWhereClauseEntry remove(long finderWhereClauseEntryId)
		throws NoSuchFinderWhereClauseEntryException {

		return remove((Serializable)finderWhereClauseEntryId);
	}

	@Override
	protected FinderWhereClauseEntry removeImpl(
		FinderWhereClauseEntry finderWhereClauseEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(finderWhereClauseEntry)) {
				finderWhereClauseEntry = (FinderWhereClauseEntry)session.get(
					FinderWhereClauseEntryImpl.class,
					finderWhereClauseEntry.getPrimaryKeyObj());
			}

			if (finderWhereClauseEntry != null) {
				session.delete(finderWhereClauseEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (finderWhereClauseEntry != null) {
			clearCache(finderWhereClauseEntry);
		}

		return finderWhereClauseEntry;
	}

	@Override
	public FinderWhereClauseEntry updateImpl(
		FinderWhereClauseEntry finderWhereClauseEntry) {

		boolean isNew = finderWhereClauseEntry.isNew();

		if (!(finderWhereClauseEntry instanceof
				FinderWhereClauseEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(finderWhereClauseEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					finderWhereClauseEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in finderWhereClauseEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FinderWhereClauseEntry implementation " +
					finderWhereClauseEntry.getClass());
		}

		FinderWhereClauseEntryModelImpl finderWhereClauseEntryModelImpl =
			(FinderWhereClauseEntryModelImpl)finderWhereClauseEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(finderWhereClauseEntry);
			}
			else {
				finderWhereClauseEntry = (FinderWhereClauseEntry)session.merge(
					finderWhereClauseEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(finderWhereClauseEntry, false);

		if (isNew) {
			finderWhereClauseEntry.setNew(false);
		}

		finderWhereClauseEntry.resetOriginalValues();

		return finderWhereClauseEntry;
	}

	/**
	 * Returns the finder where clause entry with the primary key or throws a <code>NoSuchFinderWhereClauseEntryException</code> if it could not be found.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a finder where clause entry with the primary key could not be found
	 */
	@Override
	public FinderWhereClauseEntry findByPrimaryKey(
			long finderWhereClauseEntryId)
		throws NoSuchFinderWhereClauseEntryException {

		return findByPrimaryKey((Serializable)finderWhereClauseEntryId);
	}

	/**
	 * Returns the finder where clause entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry, or <code>null</code> if a finder where clause entry with the primary key could not be found
	 */
	@Override
	public FinderWhereClauseEntry fetchByPrimaryKey(
		long finderWhereClauseEntryId) {

		return fetchByPrimaryKey((Serializable)finderWhereClauseEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "finderWhereClauseEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FINDERWHERECLAUSEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FinderWhereClauseEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the finder where clause entry persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByName_Nickname =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByName_Nickname",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByName_Nickname",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByName_Nickname",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, false, null),
				_SQL_SELECT_FINDERWHERECLAUSEENTRY_WHERE,
				_SQL_COUNT_FINDERWHERECLAUSEENTRY_WHERE,
				FinderWhereClauseEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				"finderWhereClauseEntry.nickname IS NOT NULL",
				"finderWhereClauseEntry.nickname IS NOT NULL", null,
				new FinderColumn<>(
					"finderWhereClauseEntry.", "name", FinderColumn.Type.STRING,
					"=", true, true, FinderWhereClauseEntry::getName));

		FinderWhereClauseEntryUtil.setPersistence(this);
	}

	public void destroy() {
		FinderWhereClauseEntryUtil.setPersistence(null);

		entityCache.removeCache(FinderWhereClauseEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		FinderWhereClauseEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FINDERWHERECLAUSEENTRY =
		"SELECT finderWhereClauseEntry FROM FinderWhereClauseEntry finderWhereClauseEntry";

	private static final String _SQL_SELECT_FINDERWHERECLAUSEENTRY_WHERE =
		"SELECT finderWhereClauseEntry FROM FinderWhereClauseEntry finderWhereClauseEntry WHERE ";

	private static final String _SQL_COUNT_FINDERWHERECLAUSEENTRY_WHERE =
		"SELECT COUNT(finderWhereClauseEntry) FROM FinderWhereClauseEntry finderWhereClauseEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1452006307