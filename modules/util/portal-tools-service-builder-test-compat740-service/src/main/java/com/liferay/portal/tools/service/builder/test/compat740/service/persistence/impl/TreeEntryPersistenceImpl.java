/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.NestedSetsTreeManager;
import com.liferay.portal.kernel.service.persistence.impl.PersistenceNestedSetsTreeManager;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchTreeEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.TreeEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.TreeEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.TreeEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.TreeEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.TreeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.TreeEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the tree entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = TreeEntryPersistence.class)
public class TreeEntryPersistenceImpl
	extends BasePersistenceImpl<TreeEntry, NoSuchTreeEntryException>
	implements TreeEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TreeEntryUtil</code> to access the tree entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TreeEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationCountAncestors;
	private FinderPath _finderPathWithPaginationCountDescendants;
	private FinderPath _finderPathWithPaginationGetAncestors;
	private FinderPath _finderPathWithPaginationGetDescendants;

	public TreeEntryPersistenceImpl() {
		setModelClass(TreeEntry.class);

		setModelImplClass(TreeEntryImpl.class);
		setModelPKClass(long.class);

		setTable(TreeEntryTable.INSTANCE);
	}

	/**
	 * Creates a new tree entry with the primary key. Does not add the tree entry to the database.
	 *
	 * @param treeEntryId the primary key for the new tree entry
	 * @return the new tree entry
	 */
	@Override
	public TreeEntry create(long treeEntryId) {
		TreeEntry treeEntry = new TreeEntryImpl();

		treeEntry.setNew(true);
		treeEntry.setPrimaryKey(treeEntryId);

		return treeEntry;
	}

	/**
	 * Removes the tree entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param treeEntryId the primary key of the tree entry
	 * @return the tree entry that was removed
	 * @throws NoSuchTreeEntryException if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry remove(long treeEntryId) throws NoSuchTreeEntryException {
		return remove((Serializable)treeEntryId);
	}

	@Override
	protected TreeEntry removeImpl(TreeEntry treeEntry) {
		Session session = null;

		try {
			session = openSession();

			if (rebuildTreeEnabled) {
				if (session.isDirty()) {
					session.flush();
				}

				nestedSetsTreeManager.delete(treeEntry);

				clearCache();

				session.clear();
			}

			if (!session.contains(treeEntry)) {
				treeEntry = (TreeEntry)session.get(
					TreeEntryImpl.class, treeEntry.getPrimaryKeyObj());
			}

			if (treeEntry != null) {
				session.delete(treeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (treeEntry != null) {
			clearCache(treeEntry);
		}

		return treeEntry;
	}

	@Override
	public TreeEntry updateImpl(TreeEntry treeEntry) {
		boolean isNew = treeEntry.isNew();

		if (!(treeEntry instanceof TreeEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(treeEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(treeEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in treeEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TreeEntry implementation " +
					treeEntry.getClass());
		}

		TreeEntryModelImpl treeEntryModelImpl = (TreeEntryModelImpl)treeEntry;

		Session session = null;

		try {
			session = openSession();

			if (rebuildTreeEnabled) {
				if (session.isDirty()) {
					session.flush();
				}

				if (isNew) {
					nestedSetsTreeManager.insert(
						treeEntry,
						fetchByPrimaryKey(treeEntry.getParentTreeEntryId()));
				}
				else if ((treeEntryModelImpl.getColumnOriginalValue(
							"parentTreeEntryId") != null) &&
						 !Objects.equals(
							 treeEntry.getParentTreeEntryId(),
							 treeEntryModelImpl.getColumnOriginalValue(
								 "parentTreeEntryId"))) {

					nestedSetsTreeManager.move(
						treeEntry,
						fetchByPrimaryKey(
							treeEntryModelImpl.getColumnOriginalValue(
								"parentTreeEntryId")),
						fetchByPrimaryKey(treeEntry.getParentTreeEntryId()));
				}

				clearCache();

				session.clear();
			}

			if (isNew) {
				session.save(treeEntry);
			}
			else {
				treeEntry = (TreeEntry)session.merge(treeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(treeEntry, false);

		if (isNew) {
			treeEntry.setNew(false);
		}

		treeEntry.resetOriginalValues();

		return treeEntry;
	}

	/**
	 * Returns the tree entry with the primary key or throws a <code>NoSuchTreeEntryException</code> if it could not be found.
	 *
	 * @param treeEntryId the primary key of the tree entry
	 * @return the tree entry
	 * @throws NoSuchTreeEntryException if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry findByPrimaryKey(long treeEntryId)
		throws NoSuchTreeEntryException {

		return findByPrimaryKey((Serializable)treeEntryId);
	}

	/**
	 * Returns the tree entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param treeEntryId the primary key of the tree entry
	 * @return the tree entry, or <code>null</code> if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry fetchByPrimaryKey(long treeEntryId) {
		return fetchByPrimaryKey((Serializable)treeEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "treeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TREEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return TreeEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public long countAncestors(TreeEntry treeEntry) {
		Object[] finderArgs = new Object[] {
			treeEntry.getGroupId(), treeEntry.getLeftTreeEntryId(),
			treeEntry.getRightTreeEntryId()
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountAncestors, finderArgs, this);

		if (count == null) {
			try {
				count = nestedSetsTreeManager.countAncestors(treeEntry);

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
	public long countDescendants(TreeEntry treeEntry) {
		Object[] finderArgs = new Object[] {
			treeEntry.getGroupId(), treeEntry.getLeftTreeEntryId(),
			treeEntry.getRightTreeEntryId()
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountDescendants, finderArgs, this);

		if (count == null) {
			try {
				count = nestedSetsTreeManager.countDescendants(treeEntry);

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
	public List<TreeEntry> getAncestors(TreeEntry treeEntry) {
		Object[] finderArgs = new Object[] {
			treeEntry.getGroupId(), treeEntry.getLeftTreeEntryId(),
			treeEntry.getRightTreeEntryId()
		};

		List<TreeEntry> list = (List<TreeEntry>)finderCache.getResult(
			_finderPathWithPaginationGetAncestors, finderArgs, this);

		if ((list != null) && !list.isEmpty()) {
			for (TreeEntry tempTreeEntry : list) {
				if ((treeEntry.getLeftTreeEntryId() <
						tempTreeEntry.getLeftTreeEntryId()) ||
					(treeEntry.getRightTreeEntryId() >
						tempTreeEntry.getRightTreeEntryId())) {

					list = null;

					break;
				}
			}
		}

		if (list == null) {
			try {
				list = nestedSetsTreeManager.getAncestors(treeEntry);

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
	public List<TreeEntry> getDescendants(TreeEntry treeEntry) {
		Object[] finderArgs = new Object[] {
			treeEntry.getGroupId(), treeEntry.getLeftTreeEntryId(),
			treeEntry.getRightTreeEntryId()
		};

		List<TreeEntry> list = (List<TreeEntry>)finderCache.getResult(
			_finderPathWithPaginationGetDescendants, finderArgs, this);

		if ((list != null) && !list.isEmpty()) {
			for (TreeEntry tempTreeEntry : list) {
				if ((treeEntry.getLeftTreeEntryId() >
						tempTreeEntry.getLeftTreeEntryId()) ||
					(treeEntry.getRightTreeEntryId() <
						tempTreeEntry.getRightTreeEntryId())) {

					list = null;

					break;
				}
			}
		}

		if (list == null) {
			try {
				list = nestedSetsTreeManager.getDescendants(treeEntry);

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
	 * Rebuilds the tree entries tree for the scope using the modified pre-order tree traversal algorithm.
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
					"SELECT treeEntryId FROM TreeEntry WHERE groupId = ? AND parentTreeEntryId = ? ORDER BY treeEntryId ASC");

				selectSQLQuery.addScalar(
					"treeEntryId", com.liferay.portal.kernel.dao.orm.Type.LONG);

				SQLQuery updateSQLQuery = session.createSQLQuery(
					"UPDATE TreeEntry SET leftTreeEntryId = ?, rightTreeEntryId = ? WHERE treeEntryId = ?");

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
				"SELECT COUNT(*) AS COUNT_VALUE FROM TreeEntry WHERE groupId = ? AND (leftTreeEntryId = 0 OR leftTreeEntryId IS NULL OR rightTreeEntryId = 0 OR rightTreeEntryId IS NULL)");

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
		long groupId, long parentTreeEntryId, long leftTreeEntryId) {

		long rightTreeEntryId = leftTreeEntryId + 1;

		QueryPos queryPos = QueryPos.getInstance(selectSQLQuery);

		queryPos.add(groupId);
		queryPos.add(parentTreeEntryId);

		List<Long> treeEntryIds = selectSQLQuery.list();

		for (long treeEntryId : treeEntryIds) {
			rightTreeEntryId = rebuildTree(
				session, selectSQLQuery, updateSQLQuery, groupId, treeEntryId,
				rightTreeEntryId);
		}

		if (parentTreeEntryId > 0) {
			queryPos = QueryPos.getInstance(updateSQLQuery);

			queryPos.add(leftTreeEntryId);
			queryPos.add(rightTreeEntryId);
			queryPos.add(parentTreeEntryId);

			updateSQLQuery.executeUpdate();
		}

		return rightTreeEntryId + 1;
	}

	/**
	 * Initializes the tree entry persistence.
	 */
	@Activate
	public void activate() {
		_finderPathWithPaginationCountAncestors = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countAncestors",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "leftTreeEntryId", "rightTreeEntryId"},
			false);

		_finderPathWithPaginationCountDescendants = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countDescendants",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "leftTreeEntryId", "rightTreeEntryId"},
			false);

		_finderPathWithPaginationGetAncestors = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "getAncestors",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "leftTreeEntryId", "rightTreeEntryId"},
			true);

		_finderPathWithPaginationGetDescendants = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "getDescendants",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "leftTreeEntryId", "rightTreeEntryId"},
			true);

		TreeEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		TreeEntryUtil.setPersistence(null);

		entityCache.removeCache(TreeEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	protected NestedSetsTreeManager<TreeEntry> nestedSetsTreeManager =
		new PersistenceNestedSetsTreeManager<TreeEntry>(
			this, "TreeEntry", "TreeEntry", TreeEntryImpl.class, "treeEntryId",
			"groupId", "leftTreeEntryId", "rightTreeEntryId");
	protected boolean rebuildTreeEnabled = true;

	private static final String _SQL_SELECT_TREEENTRY =
		"SELECT treeEntry FROM TreeEntry treeEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:960876292