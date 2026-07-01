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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchLazyBlobEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.LazyBlobEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.LazyBlobEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.LazyBlobEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.LazyBlobEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LazyBlobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LazyBlobEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the lazy blob entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LazyBlobEntryPersistence.class)
public class LazyBlobEntryPersistenceImpl
	extends BasePersistenceImpl<LazyBlobEntry, NoSuchLazyBlobEntryException>
	implements LazyBlobEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LazyBlobEntryUtil</code> to access the lazy blob entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LazyBlobEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LazyBlobEntry, NoSuchLazyBlobEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the lazy blob entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LazyBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of lazy blob entries
	 * @param end the upper bound of the range of lazy blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lazy blob entries
	 */
	@Override
	public List<LazyBlobEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LazyBlobEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first lazy blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry findByUuid_First(
			String uuid, OrderByComparator<LazyBlobEntry> orderByComparator)
		throws NoSuchLazyBlobEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first lazy blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lazy blob entry, or <code>null</code> if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry fetchByUuid_First(
		String uuid, OrderByComparator<LazyBlobEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the lazy blob entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of lazy blob entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching lazy blob entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<LazyBlobEntry, NoSuchLazyBlobEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the lazy blob entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLazyBlobEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchLazyBlobEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the lazy blob entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lazy blob entry, or <code>null</code> if a matching lazy blob entry could not be found
	 */
	@Override
	public LazyBlobEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the lazy blob entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the lazy blob entry that was removed
	 */
	@Override
	public LazyBlobEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchLazyBlobEntryException {

		LazyBlobEntry lazyBlobEntry = findByUUID_G(uuid, groupId);

		return remove(lazyBlobEntry);
	}

	/**
	 * Returns the number of lazy blob entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching lazy blob entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	public LazyBlobEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LazyBlobEntry.class);

		setModelImplClass(LazyBlobEntryImpl.class);
		setModelPKClass(long.class);

		setTable(LazyBlobEntryTable.INSTANCE);
	}

	/**
	 * Creates a new lazy blob entry with the primary key. Does not add the lazy blob entry to the database.
	 *
	 * @param lazyBlobEntryId the primary key for the new lazy blob entry
	 * @return the new lazy blob entry
	 */
	@Override
	public LazyBlobEntry create(long lazyBlobEntryId) {
		LazyBlobEntry lazyBlobEntry = new LazyBlobEntryImpl();

		lazyBlobEntry.setNew(true);
		lazyBlobEntry.setPrimaryKey(lazyBlobEntryId);

		String uuid = PortalUUIDUtil.generate();

		lazyBlobEntry.setUuid(uuid);

		return lazyBlobEntry;
	}

	/**
	 * Removes the lazy blob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lazyBlobEntryId the primary key of the lazy blob entry
	 * @return the lazy blob entry that was removed
	 * @throws NoSuchLazyBlobEntryException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry remove(long lazyBlobEntryId)
		throws NoSuchLazyBlobEntryException {

		return remove((Serializable)lazyBlobEntryId);
	}

	@Override
	protected LazyBlobEntry removeImpl(LazyBlobEntry lazyBlobEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(lazyBlobEntry)) {
				lazyBlobEntry = (LazyBlobEntry)session.get(
					LazyBlobEntryImpl.class, lazyBlobEntry.getPrimaryKeyObj());
			}

			if (lazyBlobEntry != null) {
				session.delete(lazyBlobEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (lazyBlobEntry != null) {
			clearCache(lazyBlobEntry);
		}

		return lazyBlobEntry;
	}

	@Override
	public LazyBlobEntry updateImpl(LazyBlobEntry lazyBlobEntry) {
		boolean isNew = lazyBlobEntry.isNew();

		if (!(lazyBlobEntry instanceof LazyBlobEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(lazyBlobEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					lazyBlobEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in lazyBlobEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LazyBlobEntry implementation " +
					lazyBlobEntry.getClass());
		}

		LazyBlobEntryModelImpl lazyBlobEntryModelImpl =
			(LazyBlobEntryModelImpl)lazyBlobEntry;

		if (Validator.isNull(lazyBlobEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			lazyBlobEntry.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(lazyBlobEntry);
			}
			else {
				lazyBlobEntry = (LazyBlobEntry)session.merge(lazyBlobEntry);
			}

			session.flush();
			session.clear();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(lazyBlobEntry, false);

		if (isNew) {
			lazyBlobEntry.setNew(false);
		}

		lazyBlobEntry.resetOriginalValues();

		return lazyBlobEntry;
	}

	/**
	 * Returns the lazy blob entry with the primary key or throws a <code>NoSuchLazyBlobEntryException</code> if it could not be found.
	 *
	 * @param lazyBlobEntryId the primary key of the lazy blob entry
	 * @return the lazy blob entry
	 * @throws NoSuchLazyBlobEntryException if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry findByPrimaryKey(long lazyBlobEntryId)
		throws NoSuchLazyBlobEntryException {

		return findByPrimaryKey((Serializable)lazyBlobEntryId);
	}

	/**
	 * Returns the lazy blob entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param lazyBlobEntryId the primary key of the lazy blob entry
	 * @return the lazy blob entry, or <code>null</code> if a lazy blob entry with the primary key could not be found
	 */
	@Override
	public LazyBlobEntry fetchByPrimaryKey(long lazyBlobEntryId) {
		return fetchByPrimaryKey((Serializable)lazyBlobEntryId);
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
		return "lazyBlobEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAZYBLOBENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LazyBlobEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the lazy blob entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_LAZYBLOBENTRY_WHERE, _SQL_COUNT_LAZYBLOBENTRY_WHERE,
			LazyBlobEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"lazyBlobEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LazyBlobEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LazyBlobEntry::getUuid),
				LazyBlobEntry::getGroupId),
			_SQL_SELECT_LAZYBLOBENTRY_WHERE, "",
			new FinderColumn<>(
				"lazyBlobEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LazyBlobEntry::getUuid),
			new FinderColumn<>(
				"lazyBlobEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, LazyBlobEntry::getGroupId));

		LazyBlobEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LazyBlobEntryUtil.setPersistence(null);

		entityCache.removeCache(LazyBlobEntryImpl.class.getName());
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
		LazyBlobEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAZYBLOBENTRY =
		"SELECT lazyBlobEntry FROM LazyBlobEntry lazyBlobEntry";

	private static final String _SQL_SELECT_LAZYBLOBENTRY_WHERE =
		"SELECT lazyBlobEntry FROM LazyBlobEntry lazyBlobEntry WHERE ";

	private static final String _SQL_COUNT_LAZYBLOBENTRY_WHERE =
		"SELECT COUNT(lazyBlobEntry) FROM LazyBlobEntry lazyBlobEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1700059967