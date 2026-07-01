/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchRenameFinderColumnEntryException;
import com.liferay.portal.tools.service.builder.test.model.RenameFinderColumnEntry;
import com.liferay.portal.tools.service.builder.test.model.RenameFinderColumnEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.RenameFinderColumnEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.RenameFinderColumnEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.RenameFinderColumnEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.RenameFinderColumnEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

/**
 * The persistence implementation for the rename finder column entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RenameFinderColumnEntryPersistenceImpl
	extends BasePersistenceImpl
		<RenameFinderColumnEntry, NoSuchRenameFinderColumnEntryException>
	implements RenameFinderColumnEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RenameFinderColumnEntryUtil</code> to access the rename finder column entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RenameFinderColumnEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<RenameFinderColumnEntry, NoSuchRenameFinderColumnEntryException>
			_uniquePersistenceFinderByColumnToRename;

	/**
	 * Returns the rename finder column entry where columnToRename = &#63; or throws a <code>NoSuchRenameFinderColumnEntryException</code> if it could not be found.
	 *
	 * @param columnToRename the column to rename
	 * @return the matching rename finder column entry
	 * @throws NoSuchRenameFinderColumnEntryException if a matching rename finder column entry could not be found
	 */
	@Override
	public RenameFinderColumnEntry findByColumnToRename(String columnToRename)
		throws NoSuchRenameFinderColumnEntryException {

		return _uniquePersistenceFinderByColumnToRename.find(
			finderCache, new Object[] {columnToRename});
	}

	/**
	 * Returns the rename finder column entry where columnToRename = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param columnToRename the column to rename
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching rename finder column entry, or <code>null</code> if a matching rename finder column entry could not be found
	 */
	@Override
	public RenameFinderColumnEntry fetchByColumnToRename(
		String columnToRename, boolean useFinderCache) {

		return _uniquePersistenceFinderByColumnToRename.fetch(
			finderCache, new Object[] {columnToRename}, useFinderCache);
	}

	/**
	 * Removes the rename finder column entry where columnToRename = &#63; from the database.
	 *
	 * @param columnToRename the column to rename
	 * @return the rename finder column entry that was removed
	 */
	@Override
	public RenameFinderColumnEntry removeByColumnToRename(String columnToRename)
		throws NoSuchRenameFinderColumnEntryException {

		RenameFinderColumnEntry renameFinderColumnEntry = findByColumnToRename(
			columnToRename);

		return remove(renameFinderColumnEntry);
	}

	/**
	 * Returns the number of rename finder column entries where columnToRename = &#63;.
	 *
	 * @param columnToRename the column to rename
	 * @return the number of matching rename finder column entries
	 */
	@Override
	public int countByColumnToRename(String columnToRename) {
		return _uniquePersistenceFinderByColumnToRename.count(
			finderCache, new Object[] {columnToRename});
	}

	public RenameFinderColumnEntryPersistenceImpl() {
		setModelClass(RenameFinderColumnEntry.class);

		setModelImplClass(RenameFinderColumnEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RenameFinderColumnEntryTable.INSTANCE);
	}

	/**
	 * Creates a new rename finder column entry with the primary key. Does not add the rename finder column entry to the database.
	 *
	 * @param renameFinderColumnEntryId the primary key for the new rename finder column entry
	 * @return the new rename finder column entry
	 */
	@Override
	public RenameFinderColumnEntry create(long renameFinderColumnEntryId) {
		RenameFinderColumnEntry renameFinderColumnEntry =
			new RenameFinderColumnEntryImpl();

		renameFinderColumnEntry.setNew(true);
		renameFinderColumnEntry.setPrimaryKey(renameFinderColumnEntryId);

		return renameFinderColumnEntry;
	}

	/**
	 * Removes the rename finder column entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param renameFinderColumnEntryId the primary key of the rename finder column entry
	 * @return the rename finder column entry that was removed
	 * @throws NoSuchRenameFinderColumnEntryException if a rename finder column entry with the primary key could not be found
	 */
	@Override
	public RenameFinderColumnEntry remove(long renameFinderColumnEntryId)
		throws NoSuchRenameFinderColumnEntryException {

		return remove((Serializable)renameFinderColumnEntryId);
	}

	@Override
	protected RenameFinderColumnEntry removeImpl(
		RenameFinderColumnEntry renameFinderColumnEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(renameFinderColumnEntry)) {
				renameFinderColumnEntry = (RenameFinderColumnEntry)session.get(
					RenameFinderColumnEntryImpl.class,
					renameFinderColumnEntry.getPrimaryKeyObj());
			}

			if (renameFinderColumnEntry != null) {
				session.delete(renameFinderColumnEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (renameFinderColumnEntry != null) {
			clearCache(renameFinderColumnEntry);
		}

		return renameFinderColumnEntry;
	}

	@Override
	public RenameFinderColumnEntry updateImpl(
		RenameFinderColumnEntry renameFinderColumnEntry) {

		boolean isNew = renameFinderColumnEntry.isNew();

		if (!(renameFinderColumnEntry instanceof
				RenameFinderColumnEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(renameFinderColumnEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					renameFinderColumnEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in renameFinderColumnEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RenameFinderColumnEntry implementation " +
					renameFinderColumnEntry.getClass());
		}

		RenameFinderColumnEntryModelImpl renameFinderColumnEntryModelImpl =
			(RenameFinderColumnEntryModelImpl)renameFinderColumnEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(renameFinderColumnEntry);
			}
			else {
				renameFinderColumnEntry =
					(RenameFinderColumnEntry)session.merge(
						renameFinderColumnEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(renameFinderColumnEntry, false);

		if (isNew) {
			renameFinderColumnEntry.setNew(false);
		}

		renameFinderColumnEntry.resetOriginalValues();

		return renameFinderColumnEntry;
	}

	/**
	 * Returns the rename finder column entry with the primary key or throws a <code>NoSuchRenameFinderColumnEntryException</code> if it could not be found.
	 *
	 * @param renameFinderColumnEntryId the primary key of the rename finder column entry
	 * @return the rename finder column entry
	 * @throws NoSuchRenameFinderColumnEntryException if a rename finder column entry with the primary key could not be found
	 */
	@Override
	public RenameFinderColumnEntry findByPrimaryKey(
			long renameFinderColumnEntryId)
		throws NoSuchRenameFinderColumnEntryException {

		return findByPrimaryKey((Serializable)renameFinderColumnEntryId);
	}

	/**
	 * Returns the rename finder column entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param renameFinderColumnEntryId the primary key of the rename finder column entry
	 * @return the rename finder column entry, or <code>null</code> if a rename finder column entry with the primary key could not be found
	 */
	@Override
	public RenameFinderColumnEntry fetchByPrimaryKey(
		long renameFinderColumnEntryId) {

		return fetchByPrimaryKey((Serializable)renameFinderColumnEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "renameFinderColumnEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RENAMEFINDERCOLUMNENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RenameFinderColumnEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the rename finder column entry persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByColumnToRename =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByColumnToRename",
					new String[] {String.class.getName()},
					new String[] {"columnToRename"}, 0, 1, false,
					convertNullFunction(
						RenameFinderColumnEntry::getColumnToRename)),
				_SQL_SELECT_RENAMEFINDERCOLUMNENTRY_WHERE, "",
				new FinderColumn<>(
					"renameFinderColumnEntry.", "columnToRename",
					FinderColumn.Type.STRING, "=", true, true,
					RenameFinderColumnEntry::getColumnToRename));

		RenameFinderColumnEntryUtil.setPersistence(this);
	}

	public void destroy() {
		RenameFinderColumnEntryUtil.setPersistence(null);

		entityCache.removeCache(RenameFinderColumnEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_RENAMEFINDERCOLUMNENTRY =
		"SELECT renameFinderColumnEntry FROM RenameFinderColumnEntry renameFinderColumnEntry";

	private static final String _SQL_SELECT_RENAMEFINDERCOLUMNENTRY_WHERE =
		"SELECT renameFinderColumnEntry FROM RenameFinderColumnEntry renameFinderColumnEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No RenameFinderColumnEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RenameFinderColumnEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-599173820