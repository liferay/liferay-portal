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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFilterPrimaryEntryException;
import com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry;
import com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterPrimaryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterPrimaryEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the filter primary entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class FilterPrimaryEntryPersistenceImpl
	extends BasePersistenceImpl
		<FilterPrimaryEntry, NoSuchFilterPrimaryEntryException>
	implements FilterPrimaryEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FilterPrimaryEntryUtil</code> to access the filter primary entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FilterPrimaryEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FilterPrimaryEntry, NoSuchFilterPrimaryEntryException>
			_collectionPersistenceFinderByResourcePrimKey;

	/**
	 * Returns an ordered range of all the filter primary entries where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter primary entries
	 */
	@Override
	public List<FilterPrimaryEntry> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByResourcePrimKey.find(
			finderCache, new Object[] {resourcePrimKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first filter primary entry in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter primary entry
	 * @throws NoSuchFilterPrimaryEntryException if a matching filter primary entry could not be found
	 */
	@Override
	public FilterPrimaryEntry findByResourcePrimKey_First(
			long resourcePrimKey,
			OrderByComparator<FilterPrimaryEntry> orderByComparator)
		throws NoSuchFilterPrimaryEntryException {

		return _collectionPersistenceFinderByResourcePrimKey.findFirst(
			finderCache, new Object[] {resourcePrimKey}, orderByComparator);
	}

	/**
	 * Returns the first filter primary entry in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter primary entry, or <code>null</code> if a matching filter primary entry could not be found
	 */
	@Override
	public FilterPrimaryEntry fetchByResourcePrimKey_First(
		long resourcePrimKey,
		OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return _collectionPersistenceFinderByResourcePrimKey.fetchFirst(
			finderCache, new Object[] {resourcePrimKey}, orderByComparator);
	}

	/**
	 * Removes all the filter primary entries where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	@Override
	public void removeByResourcePrimKey(long resourcePrimKey) {
		_collectionPersistenceFinderByResourcePrimKey.remove(
			finderCache, new Object[] {resourcePrimKey});
	}

	/**
	 * Returns the number of filter primary entries where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching filter primary entries
	 */
	@Override
	public int countByResourcePrimKey(long resourcePrimKey) {
		return _collectionPersistenceFinderByResourcePrimKey.count(
			finderCache, new Object[] {resourcePrimKey});
	}

	private FilterCollectionPersistenceFinder
		<FilterPrimaryEntry, NoSuchFilterPrimaryEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the filter primary entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter primary entries
	 */
	@Override
	public List<FilterPrimaryEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first filter primary entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter primary entry
	 * @throws NoSuchFilterPrimaryEntryException if a matching filter primary entry could not be found
	 */
	@Override
	public FilterPrimaryEntry findByGroupId_First(
			long groupId,
			OrderByComparator<FilterPrimaryEntry> orderByComparator)
		throws NoSuchFilterPrimaryEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first filter primary entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter primary entry, or <code>null</code> if a matching filter primary entry could not be found
	 */
	@Override
	public FilterPrimaryEntry fetchByGroupId_First(
		long groupId, OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the filter primary entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter primary entries that the user has permission to view
	 */
	@Override
	public List<FilterPrimaryEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the filter primary entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of filter primary entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching filter primary entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of filter primary entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching filter primary entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	public FilterPrimaryEntryPersistenceImpl() {
		setModelClass(FilterPrimaryEntry.class);

		setModelImplClass(FilterPrimaryEntryImpl.class);
		setModelPKClass(long.class);

		setTable(FilterPrimaryEntryTable.INSTANCE);
	}

	/**
	 * Creates a new filter primary entry with the primary key. Does not add the filter primary entry to the database.
	 *
	 * @param filterPrimaryEntryId the primary key for the new filter primary entry
	 * @return the new filter primary entry
	 */
	@Override
	public FilterPrimaryEntry create(long filterPrimaryEntryId) {
		FilterPrimaryEntry filterPrimaryEntry = new FilterPrimaryEntryImpl();

		filterPrimaryEntry.setNew(true);
		filterPrimaryEntry.setPrimaryKey(filterPrimaryEntryId);

		filterPrimaryEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return filterPrimaryEntry;
	}

	/**
	 * Removes the filter primary entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry that was removed
	 * @throws NoSuchFilterPrimaryEntryException if a filter primary entry with the primary key could not be found
	 */
	@Override
	public FilterPrimaryEntry remove(long filterPrimaryEntryId)
		throws NoSuchFilterPrimaryEntryException {

		return remove((Serializable)filterPrimaryEntryId);
	}

	@Override
	protected FilterPrimaryEntry removeImpl(
		FilterPrimaryEntry filterPrimaryEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(filterPrimaryEntry)) {
				filterPrimaryEntry = (FilterPrimaryEntry)session.get(
					FilterPrimaryEntryImpl.class,
					filterPrimaryEntry.getPrimaryKeyObj());
			}

			if (filterPrimaryEntry != null) {
				session.delete(filterPrimaryEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (filterPrimaryEntry != null) {
			clearCache(filterPrimaryEntry);
		}

		return filterPrimaryEntry;
	}

	@Override
	public FilterPrimaryEntry updateImpl(
		FilterPrimaryEntry filterPrimaryEntry) {

		boolean isNew = filterPrimaryEntry.isNew();

		if (!(filterPrimaryEntry instanceof FilterPrimaryEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(filterPrimaryEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					filterPrimaryEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in filterPrimaryEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FilterPrimaryEntry implementation " +
					filterPrimaryEntry.getClass());
		}

		FilterPrimaryEntryModelImpl filterPrimaryEntryModelImpl =
			(FilterPrimaryEntryModelImpl)filterPrimaryEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(filterPrimaryEntry);
			}
			else {
				filterPrimaryEntry = (FilterPrimaryEntry)session.merge(
					filterPrimaryEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(filterPrimaryEntry, false);

		if (isNew) {
			filterPrimaryEntry.setNew(false);
		}

		filterPrimaryEntry.resetOriginalValues();

		return filterPrimaryEntry;
	}

	/**
	 * Returns the filter primary entry with the primary key or throws a <code>NoSuchFilterPrimaryEntryException</code> if it could not be found.
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry
	 * @throws NoSuchFilterPrimaryEntryException if a filter primary entry with the primary key could not be found
	 */
	@Override
	public FilterPrimaryEntry findByPrimaryKey(long filterPrimaryEntryId)
		throws NoSuchFilterPrimaryEntryException {

		return findByPrimaryKey((Serializable)filterPrimaryEntryId);
	}

	/**
	 * Returns the filter primary entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry, or <code>null</code> if a filter primary entry with the primary key could not be found
	 */
	@Override
	public FilterPrimaryEntry fetchByPrimaryKey(long filterPrimaryEntryId) {
		return fetchByPrimaryKey((Serializable)filterPrimaryEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "filterPrimaryEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FILTERPRIMARYENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FilterPrimaryEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the filter primary entry persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByResourcePrimKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByResourcePrimKey",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"resourcePrimKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByResourcePrimKey",
					new String[] {Long.class.getName()},
					new String[] {"resourcePrimKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByResourcePrimKey",
					new String[] {Long.class.getName()},
					new String[] {"resourcePrimKey"}, false),
				_SQL_SELECT_FILTERPRIMARYENTRY_WHERE,
				_SQL_COUNT_FILTERPRIMARYENTRY_WHERE,
				FilterPrimaryEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"filterPrimaryEntry.", "resourcePrimKey",
					FinderColumn.Type.LONG, "=", true, true,
					FilterPrimaryEntry::getResourcePrimKey));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_FILTERPRIMARYENTRY_WHERE,
				_SQL_COUNT_FILTERPRIMARYENTRY_WHERE,
				FilterPrimaryEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"filterPrimaryEntry.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, FilterPrimaryEntry::getGroupId));

		FilterPrimaryEntryUtil.setPersistence(this);
	}

	public void destroy() {
		FilterPrimaryEntryUtil.setPersistence(null);

		entityCache.removeCache(FilterPrimaryEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		FilterPrimaryEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FILTERPRIMARYENTRY =
		"SELECT filterPrimaryEntry FROM FilterPrimaryEntry filterPrimaryEntry";

	private static final String _SQL_SELECT_FILTERPRIMARYENTRY_WHERE =
		"SELECT filterPrimaryEntry FROM FilterPrimaryEntry filterPrimaryEntry WHERE ";

	private static final String _SQL_COUNT_FILTERPRIMARYENTRY_WHERE =
		"SELECT COUNT(filterPrimaryEntry) FROM FilterPrimaryEntry filterPrimaryEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-148141614