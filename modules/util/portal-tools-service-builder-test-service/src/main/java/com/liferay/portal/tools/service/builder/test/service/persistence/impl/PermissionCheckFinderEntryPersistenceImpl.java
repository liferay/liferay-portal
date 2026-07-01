/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchPermissionCheckFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.PermissionCheckFinderEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.PermissionCheckFinderEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.PermissionCheckFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.PermissionCheckFinderEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the permission check finder entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PermissionCheckFinderEntryPersistenceImpl
	extends BasePersistenceImpl
		<PermissionCheckFinderEntry, NoSuchPermissionCheckFinderEntryException>
	implements PermissionCheckFinderEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PermissionCheckFinderEntryUtil</code> to access the permission check finder entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PermissionCheckFinderEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<PermissionCheckFinderEntry, NoSuchPermissionCheckFinderEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a matching permission check finder entry could not be found
	 */
	@Override
	public PermissionCheckFinderEntry findByGroupId_First(
			long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns the first permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching permission check finder entry, or <code>null</code> if a matching permission check finder entry could not be found
	 */
	@Override
	public PermissionCheckFinderEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries that the user has permission to view
	 */
	@Override
	public List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries that the user has permission to view
	 */
	@Override
	public List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupIds}, start, end, orderByComparator,
			groupIds);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching permission check finder entries
	 */
	@Override
	public List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the permission check finder entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of permission check finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching permission check finder entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of permission check finder entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching permission check finder entries
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	/**
	 * Returns the number of permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching permission check finder entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {new long[] {groupId}}, groupId);
	}

	/**
	 * Returns the number of permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching permission check finder entries that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupIds}, groupIds);
	}

	public PermissionCheckFinderEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("integer", "integer_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PermissionCheckFinderEntry.class);

		setModelImplClass(PermissionCheckFinderEntryImpl.class);
		setModelPKClass(long.class);

		setTable(PermissionCheckFinderEntryTable.INSTANCE);
	}

	/**
	 * Creates a new permission check finder entry with the primary key. Does not add the permission check finder entry to the database.
	 *
	 * @param permissionCheckFinderEntryId the primary key for the new permission check finder entry
	 * @return the new permission check finder entry
	 */
	@Override
	public PermissionCheckFinderEntry create(
		long permissionCheckFinderEntryId) {

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			new PermissionCheckFinderEntryImpl();

		permissionCheckFinderEntry.setNew(true);
		permissionCheckFinderEntry.setPrimaryKey(permissionCheckFinderEntryId);

		permissionCheckFinderEntry.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return permissionCheckFinderEntry;
	}

	/**
	 * Removes the permission check finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry that was removed
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry remove(long permissionCheckFinderEntryId)
		throws NoSuchPermissionCheckFinderEntryException {

		return remove((Serializable)permissionCheckFinderEntryId);
	}

	@Override
	protected PermissionCheckFinderEntry removeImpl(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(permissionCheckFinderEntry)) {
				permissionCheckFinderEntry =
					(PermissionCheckFinderEntry)session.get(
						PermissionCheckFinderEntryImpl.class,
						permissionCheckFinderEntry.getPrimaryKeyObj());
			}

			if (permissionCheckFinderEntry != null) {
				session.delete(permissionCheckFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (permissionCheckFinderEntry != null) {
			clearCache(permissionCheckFinderEntry);
		}

		return permissionCheckFinderEntry;
	}

	@Override
	public PermissionCheckFinderEntry updateImpl(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		boolean isNew = permissionCheckFinderEntry.isNew();

		if (!(permissionCheckFinderEntry instanceof
				PermissionCheckFinderEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(permissionCheckFinderEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					permissionCheckFinderEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in permissionCheckFinderEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PermissionCheckFinderEntry implementation " +
					permissionCheckFinderEntry.getClass());
		}

		PermissionCheckFinderEntryModelImpl
			permissionCheckFinderEntryModelImpl =
				(PermissionCheckFinderEntryModelImpl)permissionCheckFinderEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(permissionCheckFinderEntry);
			}
			else {
				permissionCheckFinderEntry =
					(PermissionCheckFinderEntry)session.merge(
						permissionCheckFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(permissionCheckFinderEntry, false);

		if (isNew) {
			permissionCheckFinderEntry.setNew(false);
		}

		permissionCheckFinderEntry.resetOriginalValues();

		return permissionCheckFinderEntry;
	}

	/**
	 * Returns the permission check finder entry with the primary key or throws a <code>NoSuchPermissionCheckFinderEntryException</code> if it could not be found.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry findByPrimaryKey(
			long permissionCheckFinderEntryId)
		throws NoSuchPermissionCheckFinderEntryException {

		return findByPrimaryKey((Serializable)permissionCheckFinderEntryId);
	}

	/**
	 * Returns the permission check finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry, or <code>null</code> if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public PermissionCheckFinderEntry fetchByPrimaryKey(
		long permissionCheckFinderEntryId) {

		return fetchByPrimaryKey((Serializable)permissionCheckFinderEntryId);
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
		return "permissionCheckFinderEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PERMISSIONCHECKFINDERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PermissionCheckFinderEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the permission check finder entry persistence.
	 */
	public void afterPropertiesSet() {
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
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_PERMISSIONCHECKFINDERENTRY_WHERE,
				_SQL_COUNT_PERMISSIONCHECKFINDERENTRY_WHERE,
				PermissionCheckFinderEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new ArrayableFinderColumn<>(
					"permissionCheckFinderEntry.", "groupId",
					FinderColumn.Type.LONG, "=", false, true, true,
					PermissionCheckFinderEntry::getGroupId));

		PermissionCheckFinderEntryUtil.setPersistence(this);
	}

	public void destroy() {
		PermissionCheckFinderEntryUtil.setPersistence(null);

		entityCache.removeCache(PermissionCheckFinderEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		PermissionCheckFinderEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PERMISSIONCHECKFINDERENTRY =
		"SELECT permissionCheckFinderEntry FROM PermissionCheckFinderEntry permissionCheckFinderEntry";

	private static final String _SQL_SELECT_PERMISSIONCHECKFINDERENTRY_WHERE =
		"SELECT permissionCheckFinderEntry FROM PermissionCheckFinderEntry permissionCheckFinderEntry WHERE ";

	private static final String _SQL_COUNT_PERMISSIONCHECKFINDERENTRY_WHERE =
		"SELECT COUNT(permissionCheckFinderEntry) FROM PermissionCheckFinderEntry permissionCheckFinderEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"integer", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1297583294