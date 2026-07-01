/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.NestedSetsTreeManager;
import com.liferay.portal.kernel.service.persistence.impl.PersistenceNestedSetsTreeManager;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchNestedSetsTreeEntryException;
import com.liferay.portal.tools.service.builder.test.model.NestedSetsTreeEntry;
import com.liferay.portal.tools.service.builder.test.model.NestedSetsTreeEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.NestedSetsTreeEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.NestedSetsTreeEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.NestedSetsTreeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.NestedSetsTreeEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The persistence implementation for the nested sets tree entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class NestedSetsTreeEntryPersistenceImpl
	extends BasePersistenceImpl
		<NestedSetsTreeEntry, NoSuchNestedSetsTreeEntryException>
	implements NestedSetsTreeEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NestedSetsTreeEntryUtil</code> to access the nested sets tree entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NestedSetsTreeEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationCountAncestors;
	private FinderPath _finderPathWithPaginationCountDescendants;
	private FinderPath _finderPathWithPaginationGetAncestors;
	private FinderPath _finderPathWithPaginationGetDescendants;

	public NestedSetsTreeEntryPersistenceImpl() {
		setModelClass(NestedSetsTreeEntry.class);

		setModelImplClass(NestedSetsTreeEntryImpl.class);
		setModelPKClass(long.class);

		setTable(NestedSetsTreeEntryTable.INSTANCE);
	}

	/**
	 * Creates a new nested sets tree entry with the primary key. Does not add the nested sets tree entry to the database.
	 *
	 * @param nestedSetsTreeEntryId the primary key for the new nested sets tree entry
	 * @return the new nested sets tree entry
	 */
	@Override
	public NestedSetsTreeEntry create(long nestedSetsTreeEntryId) {
		NestedSetsTreeEntry nestedSetsTreeEntry = new NestedSetsTreeEntryImpl();

		nestedSetsTreeEntry.setNew(true);
		nestedSetsTreeEntry.setPrimaryKey(nestedSetsTreeEntryId);

		return nestedSetsTreeEntry;
	}

	/**
	 * Removes the nested sets tree entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the nested sets tree entry
	 * @return the nested sets tree entry that was removed
	 * @throws NoSuchNestedSetsTreeEntryException if a nested sets tree entry with the primary key could not be found
	 */
	@Override
	public NestedSetsTreeEntry remove(long nestedSetsTreeEntryId)
		throws NoSuchNestedSetsTreeEntryException {

		return remove((Serializable)nestedSetsTreeEntryId);
	}

	@Override
	protected NestedSetsTreeEntry removeImpl(
		NestedSetsTreeEntry nestedSetsTreeEntry) {

		Session session = null;

		try {
			session = openSession();

			if (rebuildTreeEnabled) {
				if (session.isDirty()) {
					session.flush();
				}

				nestedSetsTreeManager.delete(nestedSetsTreeEntry);

				clearCache();

				session.clear();
			}

			if (!session.contains(nestedSetsTreeEntry)) {
				nestedSetsTreeEntry = (NestedSetsTreeEntry)session.get(
					NestedSetsTreeEntryImpl.class,
					nestedSetsTreeEntry.getPrimaryKeyObj());
			}

			if (nestedSetsTreeEntry != null) {
				session.delete(nestedSetsTreeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (nestedSetsTreeEntry != null) {
			clearCache(nestedSetsTreeEntry);
		}

		return nestedSetsTreeEntry;
	}

	@Override
	public NestedSetsTreeEntry updateImpl(
		NestedSetsTreeEntry nestedSetsTreeEntry) {

		boolean isNew = nestedSetsTreeEntry.isNew();

		if (!(nestedSetsTreeEntry instanceof NestedSetsTreeEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(nestedSetsTreeEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					nestedSetsTreeEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in nestedSetsTreeEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NestedSetsTreeEntry implementation " +
					nestedSetsTreeEntry.getClass());
		}

		NestedSetsTreeEntryModelImpl nestedSetsTreeEntryModelImpl =
			(NestedSetsTreeEntryModelImpl)nestedSetsTreeEntry;

		Session session = null;

		try {
			session = openSession();

			if (rebuildTreeEnabled) {
				if (session.isDirty()) {
					session.flush();
				}

				if (isNew) {
					nestedSetsTreeManager.insert(
						nestedSetsTreeEntry,
						fetchByPrimaryKey(
							nestedSetsTreeEntry.
								getParentNestedSetsTreeEntryId()));
				}
				else if ((nestedSetsTreeEntryModelImpl.getColumnOriginalValue(
							"parentNestedSetsTreeEntryId") != null) &&
						 !Objects.equals(
							 nestedSetsTreeEntry.
								 getParentNestedSetsTreeEntryId(),
							 nestedSetsTreeEntryModelImpl.
								 getColumnOriginalValue(
									 "parentNestedSetsTreeEntryId"))) {

					nestedSetsTreeManager.move(
						nestedSetsTreeEntry,
						fetchByPrimaryKey(
							nestedSetsTreeEntryModelImpl.getColumnOriginalValue(
								"parentNestedSetsTreeEntryId")),
						fetchByPrimaryKey(
							nestedSetsTreeEntry.
								getParentNestedSetsTreeEntryId()));
				}

				clearCache();

				session.clear();
			}

			if (isNew) {
				session.save(nestedSetsTreeEntry);
			}
			else {
				nestedSetsTreeEntry = (NestedSetsTreeEntry)session.merge(
					nestedSetsTreeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(nestedSetsTreeEntry, false);

		if (isNew) {
			nestedSetsTreeEntry.setNew(false);
		}

		nestedSetsTreeEntry.resetOriginalValues();

		return nestedSetsTreeEntry;
	}

	/**
	 * Returns the nested sets tree entry with the primary key or throws a <code>NoSuchNestedSetsTreeEntryException</code> if it could not be found.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the nested sets tree entry
	 * @return the nested sets tree entry
	 * @throws NoSuchNestedSetsTreeEntryException if a nested sets tree entry with the primary key could not be found
	 */
	@Override
	public NestedSetsTreeEntry findByPrimaryKey(long nestedSetsTreeEntryId)
		throws NoSuchNestedSetsTreeEntryException {

		return findByPrimaryKey((Serializable)nestedSetsTreeEntryId);
	}

	/**
	 * Returns the nested sets tree entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param nestedSetsTreeEntryId the primary key of the nested sets tree entry
	 * @return the nested sets tree entry, or <code>null</code> if a nested sets tree entry with the primary key could not be found
	 */
	@Override
	public NestedSetsTreeEntry fetchByPrimaryKey(long nestedSetsTreeEntryId) {
		return fetchByPrimaryKey((Serializable)nestedSetsTreeEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "nestedSetsTreeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NESTEDSETSTREEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NestedSetsTreeEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public long countAncestors(NestedSetsTreeEntry nestedSetsTreeEntry) {
		Object[] finderArgs = new Object[] {
			nestedSetsTreeEntry.getGroupId(),
			nestedSetsTreeEntry.getLeftNestedSetsTreeEntryId(),
			nestedSetsTreeEntry.getRightNestedSetsTreeEntryId()
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountAncestors, finderArgs, this);

		if (count == null) {
			try {
				count = nestedSetsTreeManager.countAncestors(
					nestedSetsTreeEntry);

				finderCache.putResult(
					_finderPathWithPaginationCountAncestors, finderArgs, count);
			}
			catch (SystemException systemException) {
				throw systemException;
			}
		}

		return count.intValue();
	}

	@Override
	public long countDescendants(NestedSetsTreeEntry nestedSetsTreeEntry) {
		Object[] finderArgs = new Object[] {
			nestedSetsTreeEntry.getGroupId(),
			nestedSetsTreeEntry.getLeftNestedSetsTreeEntryId(),
			nestedSetsTreeEntry.getRightNestedSetsTreeEntryId()
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountDescendants, finderArgs, this);

		if (count == null) {
			try {
				count = nestedSetsTreeManager.countDescendants(
					nestedSetsTreeEntry);

				finderCache.putResult(
					_finderPathWithPaginationCountDescendants, finderArgs,
					count);
			}
			catch (SystemException systemException) {
				throw systemException;
			}
		}

		return count.intValue();
	}

	@Override
	public List<NestedSetsTreeEntry> getAncestors(
		NestedSetsTreeEntry nestedSetsTreeEntry) {

		Object[] finderArgs = new Object[] {
			nestedSetsTreeEntry.getGroupId(),
			nestedSetsTreeEntry.getLeftNestedSetsTreeEntryId(),
			nestedSetsTreeEntry.getRightNestedSetsTreeEntryId()
		};

		List<NestedSetsTreeEntry> list =
			(List<NestedSetsTreeEntry>)finderCache.getResult(
				_finderPathWithPaginationGetAncestors, finderArgs, this);

		if ((list != null) && !list.isEmpty()) {
			for (NestedSetsTreeEntry tempNestedSetsTreeEntry : list) {
				if ((nestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() <
						tempNestedSetsTreeEntry.
							getLeftNestedSetsTreeEntryId()) ||
					(nestedSetsTreeEntry.getRightNestedSetsTreeEntryId() >
						tempNestedSetsTreeEntry.
							getRightNestedSetsTreeEntryId())) {

					list = null;

					break;
				}
			}
		}

		if (list == null) {
			try {
				list = nestedSetsTreeManager.getAncestors(nestedSetsTreeEntry);

				cacheResult(list);

				finderCache.putResult(
					_finderPathWithPaginationGetAncestors, finderArgs, list);
			}
			catch (SystemException systemException) {
				throw systemException;
			}
		}

		return list;
	}

	@Override
	public List<NestedSetsTreeEntry> getDescendants(
		NestedSetsTreeEntry nestedSetsTreeEntry) {

		Object[] finderArgs = new Object[] {
			nestedSetsTreeEntry.getGroupId(),
			nestedSetsTreeEntry.getLeftNestedSetsTreeEntryId(),
			nestedSetsTreeEntry.getRightNestedSetsTreeEntryId()
		};

		List<NestedSetsTreeEntry> list =
			(List<NestedSetsTreeEntry>)finderCache.getResult(
				_finderPathWithPaginationGetDescendants, finderArgs, this);

		if ((list != null) && !list.isEmpty()) {
			for (NestedSetsTreeEntry tempNestedSetsTreeEntry : list) {
				if ((nestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() >
						tempNestedSetsTreeEntry.
							getLeftNestedSetsTreeEntryId()) ||
					(nestedSetsTreeEntry.getRightNestedSetsTreeEntryId() <
						tempNestedSetsTreeEntry.
							getRightNestedSetsTreeEntryId())) {

					list = null;

					break;
				}
			}
		}

		if (list == null) {
			try {
				list = nestedSetsTreeManager.getDescendants(
					nestedSetsTreeEntry);

				cacheResult(list);

				finderCache.putResult(
					_finderPathWithPaginationGetDescendants, finderArgs, list);
			}
			catch (SystemException systemException) {
				throw systemException;
			}
		}

		return list;
	}

	/**
	 * Rebuilds the nested sets tree entries tree for the scope using the modified pre-order tree traversal algorithm.
	 *
	 * <p>
	 * Only call this method if the tree has become stale through operations other than normal CRUD. Under normal circumstances the tree is automatically rebuilt whenver necessary.
	 * </p>
	 *
	 * @param groupId the ID of the scope
	 * @param force whether to force the rebuild even if the tree is not stale
	 */
	@Override
	public void rebuildTree(long groupId, boolean force) {
		if (!rebuildTreeEnabled) {
			return;
		}

		if (force || (countOrphanTreeNodes(groupId) > 0)) {
			Session session = null;

			try {
				session = openSession();

				if (session.isDirty()) {
					session.flush();
				}

				SQLQuery selectSQLQuery = session.createSQLQuery(
					"SELECT nestedSetsTreeEntryId FROM NestedSetsTreeEntry WHERE groupId = ? AND parentNestedSetsTreeEntryId = ? ORDER BY nestedSetsTreeEntryId ASC");

				selectSQLQuery.addScalar(
					"nestedSetsTreeEntryId",
					com.liferay.portal.kernel.dao.orm.Type.LONG);

				SQLQuery updateSQLQuery = session.createSQLQuery(
					"UPDATE NestedSetsTreeEntry SET leftNestedSetsTreeEntryId = ?, rightNestedSetsTreeEntryId = ? WHERE nestedSetsTreeEntryId = ?");

				rebuildTree(
					session, selectSQLQuery, updateSQLQuery, groupId, 0, 0);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}

			clearCache();
		}
	}

	@Override
	public void setRebuildTreeEnabled(boolean rebuildTreeEnabled) {
		this.rebuildTreeEnabled = rebuildTreeEnabled;
	}

	protected long countOrphanTreeNodes(long groupId) {
		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				"SELECT COUNT(*) AS COUNT_VALUE FROM NestedSetsTreeEntry WHERE groupId = ? AND (leftNestedSetsTreeEntryId = 0 OR leftNestedSetsTreeEntryId IS NULL OR rightNestedSetsTreeEntryId = 0 OR rightNestedSetsTreeEntryId IS NULL)");

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (Long)sqlQuery.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected long rebuildTree(
		Session session, SQLQuery selectSQLQuery, SQLQuery updateSQLQuery,
		long groupId, long parentNestedSetsTreeEntryId,
		long leftNestedSetsTreeEntryId) {

		long rightNestedSetsTreeEntryId = leftNestedSetsTreeEntryId + 1;

		QueryPos queryPos = QueryPos.getInstance(selectSQLQuery);

		queryPos.add(groupId);
		queryPos.add(parentNestedSetsTreeEntryId);

		List<Long> nestedSetsTreeEntryIds = selectSQLQuery.list();

		for (long nestedSetsTreeEntryId : nestedSetsTreeEntryIds) {
			rightNestedSetsTreeEntryId = rebuildTree(
				session, selectSQLQuery, updateSQLQuery, groupId,
				nestedSetsTreeEntryId, rightNestedSetsTreeEntryId);
		}

		if (parentNestedSetsTreeEntryId > 0) {
			queryPos = QueryPos.getInstance(updateSQLQuery);

			queryPos.add(leftNestedSetsTreeEntryId);
			queryPos.add(rightNestedSetsTreeEntryId);
			queryPos.add(parentNestedSetsTreeEntryId);

			updateSQLQuery.executeUpdate();
		}

		return rightNestedSetsTreeEntryId + 1;
	}

	/**
	 * Initializes the nested sets tree entry persistence.
	 */
	public void afterPropertiesSet() {
		_finderPathWithPaginationCountAncestors = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countAncestors",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"groupId", "leftNestedSetsTreeEntryId",
				"rightNestedSetsTreeEntryId"
			},
			false);

		_finderPathWithPaginationCountDescendants = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countDescendants",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"groupId", "leftNestedSetsTreeEntryId",
				"rightNestedSetsTreeEntryId"
			},
			false);

		_finderPathWithPaginationGetAncestors = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "getAncestors",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"groupId", "leftNestedSetsTreeEntryId",
				"rightNestedSetsTreeEntryId"
			},
			true);

		_finderPathWithPaginationGetDescendants = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "getDescendants",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"groupId", "leftNestedSetsTreeEntryId",
				"rightNestedSetsTreeEntryId"
			},
			true);

		NestedSetsTreeEntryUtil.setPersistence(this);
	}

	public void destroy() {
		NestedSetsTreeEntryUtil.setPersistence(null);

		entityCache.removeCache(NestedSetsTreeEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	protected NestedSetsTreeManager<NestedSetsTreeEntry> nestedSetsTreeManager =
		new PersistenceNestedSetsTreeManager<NestedSetsTreeEntry>(
			this, "NestedSetsTreeEntry", "NestedSetsTreeEntry",
			NestedSetsTreeEntryImpl.class, "nestedSetsTreeEntryId", "groupId",
			"leftNestedSetsTreeEntryId", "rightNestedSetsTreeEntryId");
	protected boolean rebuildTreeEnabled = true;

	private static final String _SQL_SELECT_NESTEDSETSTREEENTRY =
		"SELECT nestedSetsTreeEntry FROM NestedSetsTreeEntry nestedSetsTreeEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1420491055