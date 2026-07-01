/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchVersionedEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.VersionedEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.VersionedEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.VersionedEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.VersionedEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.VersionedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.VersionedEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the versioned entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = VersionedEntryPersistence.class)
public class VersionedEntryPersistenceImpl
	extends BasePersistenceImpl<VersionedEntry, NoSuchVersionedEntryException>
	implements VersionedEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>VersionedEntryUtil</code> to access the versioned entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		VersionedEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<VersionedEntry, NoSuchVersionedEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the versioned entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<VersionedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first versioned entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry
	 * @throws NoSuchVersionedEntryException if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry findByGroupId_First(
			long groupId, OrderByComparator<VersionedEntry> orderByComparator)
		throws NoSuchVersionedEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first versioned entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByGroupId_First(
		long groupId, OrderByComparator<VersionedEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the versioned entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of versioned entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching versioned entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<VersionedEntry, NoSuchVersionedEntryException>
			_collectionPersistenceFinderByGroupId_Head;

	/**
	 * Returns an ordered range of all the versioned entries where groupId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param start the lower bound of the range of versioned entries
	 * @param end the upper bound of the range of versioned entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entries
	 */
	@Override
	public List<VersionedEntry> findByGroupId_Head(
		long groupId, boolean head, int start, int end,
		OrderByComparator<VersionedEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId_Head.find(
			finderCache, new Object[] {groupId, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first versioned entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry
	 * @throws NoSuchVersionedEntryException if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry findByGroupId_Head_First(
			long groupId, boolean head,
			OrderByComparator<VersionedEntry> orderByComparator)
		throws NoSuchVersionedEntryException {

		return _collectionPersistenceFinderByGroupId_Head.findFirst(
			finderCache, new Object[] {groupId, head}, orderByComparator);
	}

	/**
	 * Returns the first versioned entry in the ordered set where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByGroupId_Head_First(
		long groupId, boolean head,
		OrderByComparator<VersionedEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId_Head.fetchFirst(
			finderCache, new Object[] {groupId, head}, orderByComparator);
	}

	/**
	 * Removes all the versioned entries where groupId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 */
	@Override
	public void removeByGroupId_Head(long groupId, boolean head) {
		_collectionPersistenceFinderByGroupId_Head.remove(
			finderCache, new Object[] {groupId, head});
	}

	/**
	 * Returns the number of versioned entries where groupId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching versioned entries
	 */
	@Override
	public int countByGroupId_Head(long groupId, boolean head) {
		return _collectionPersistenceFinderByGroupId_Head.count(
			finderCache, new Object[] {groupId, head});
	}

	private UniquePersistenceFinder
		<VersionedEntry, NoSuchVersionedEntryException>
			_uniquePersistenceFinderByHeadId;

	/**
	 * Returns the versioned entry where headId = &#63; or throws a <code>NoSuchVersionedEntryException</code> if it could not be found.
	 *
	 * @param headId the head ID
	 * @return the matching versioned entry
	 * @throws NoSuchVersionedEntryException if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry findByHeadId(long headId)
		throws NoSuchVersionedEntryException {

		return _uniquePersistenceFinderByHeadId.find(
			finderCache, new Object[] {headId});
	}

	/**
	 * Returns the versioned entry where headId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headId the head ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching versioned entry, or <code>null</code> if a matching versioned entry could not be found
	 */
	@Override
	public VersionedEntry fetchByHeadId(long headId, boolean useFinderCache) {
		return _uniquePersistenceFinderByHeadId.fetch(
			finderCache, new Object[] {headId}, useFinderCache);
	}

	/**
	 * Removes the versioned entry where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 * @return the versioned entry that was removed
	 */
	@Override
	public VersionedEntry removeByHeadId(long headId)
		throws NoSuchVersionedEntryException {

		VersionedEntry versionedEntry = findByHeadId(headId);

		return remove(versionedEntry);
	}

	/**
	 * Returns the number of versioned entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching versioned entries
	 */
	@Override
	public int countByHeadId(long headId) {
		return _uniquePersistenceFinderByHeadId.count(
			finderCache, new Object[] {headId});
	}

	public VersionedEntryPersistenceImpl() {
		setModelClass(VersionedEntry.class);

		setModelImplClass(VersionedEntryImpl.class);
		setModelPKClass(long.class);

		setTable(VersionedEntryTable.INSTANCE);
	}

	/**
	 * Creates a new versioned entry with the primary key. Does not add the versioned entry to the database.
	 *
	 * @param versionedEntryId the primary key for the new versioned entry
	 * @return the new versioned entry
	 */
	@Override
	public VersionedEntry create(long versionedEntryId) {
		VersionedEntry versionedEntry = new VersionedEntryImpl();

		versionedEntry.setNew(true);
		versionedEntry.setPrimaryKey(versionedEntryId);

		return versionedEntry;
	}

	/**
	 * Removes the versioned entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param versionedEntryId the primary key of the versioned entry
	 * @return the versioned entry that was removed
	 * @throws NoSuchVersionedEntryException if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry remove(long versionedEntryId)
		throws NoSuchVersionedEntryException {

		return remove((Serializable)versionedEntryId);
	}

	@Override
	protected VersionedEntry removeImpl(VersionedEntry versionedEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(versionedEntry)) {
				versionedEntry = (VersionedEntry)session.get(
					VersionedEntryImpl.class,
					versionedEntry.getPrimaryKeyObj());
			}

			if (versionedEntry != null) {
				session.delete(versionedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (versionedEntry != null) {
			clearCache(versionedEntry);
		}

		return versionedEntry;
	}

	@Override
	public VersionedEntry updateImpl(VersionedEntry versionedEntry) {
		boolean isNew = versionedEntry.isNew();

		if (!(versionedEntry instanceof VersionedEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(versionedEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					versionedEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in versionedEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom VersionedEntry implementation " +
					versionedEntry.getClass());
		}

		VersionedEntryModelImpl versionedEntryModelImpl =
			(VersionedEntryModelImpl)versionedEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(versionedEntry);
			}
			else {
				versionedEntry = (VersionedEntry)session.merge(versionedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(versionedEntry, false);

		if (isNew) {
			versionedEntry.setNew(false);
		}

		versionedEntry.resetOriginalValues();

		return versionedEntry;
	}

	/**
	 * Returns the versioned entry with the primary key or throws a <code>NoSuchVersionedEntryException</code> if it could not be found.
	 *
	 * @param versionedEntryId the primary key of the versioned entry
	 * @return the versioned entry
	 * @throws NoSuchVersionedEntryException if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry findByPrimaryKey(long versionedEntryId)
		throws NoSuchVersionedEntryException {

		return findByPrimaryKey((Serializable)versionedEntryId);
	}

	/**
	 * Returns the versioned entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param versionedEntryId the primary key of the versioned entry
	 * @return the versioned entry, or <code>null</code> if a versioned entry with the primary key could not be found
	 */
	@Override
	public VersionedEntry fetchByPrimaryKey(long versionedEntryId) {
		return fetchByPrimaryKey((Serializable)versionedEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "versionedEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_VERSIONEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return VersionedEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the versioned entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_VERSIONEDENTRY_WHERE,
				_SQL_COUNT_VERSIONEDENTRY_WHERE,
				VersionedEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"versionedEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, VersionedEntry::getGroupId));

		_collectionPersistenceFinderByGroupId_Head =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByGroupId_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByGroupId_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "head"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByGroupId_Head",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "head"}, false),
				_SQL_SELECT_VERSIONEDENTRY_WHERE,
				_SQL_COUNT_VERSIONEDENTRY_WHERE,
				VersionedEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"versionedEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, VersionedEntry::getGroupId),
				new FinderColumn<>(
					"versionedEntry.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, VersionedEntry::isHead));

		_uniquePersistenceFinderByHeadId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByHeadId",
				new String[] {Long.class.getName()}, new String[] {"headId"}, 0,
				0, false, VersionedEntry::getHeadId),
			_SQL_SELECT_VERSIONEDENTRY_WHERE, "",
			new FinderColumn<>(
				"versionedEntry.", "headId", FinderColumn.Type.LONG, "=", true,
				true, VersionedEntry::getHeadId));

		VersionedEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		VersionedEntryUtil.setPersistence(null);

		entityCache.removeCache(VersionedEntryImpl.class.getName());
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

	private static final String _ENTITY_ALIAS_PREFIX =
		VersionedEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_VERSIONEDENTRY =
		"SELECT versionedEntry FROM VersionedEntry versionedEntry";

	private static final String _SQL_SELECT_VERSIONEDENTRY_WHERE =
		"SELECT versionedEntry FROM VersionedEntry versionedEntry WHERE ";

	private static final String _SQL_COUNT_VERSIONEDENTRY_WHERE =
		"SELECT COUNT(versionedEntry) FROM VersionedEntry versionedEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No VersionedEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		VersionedEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-708848786