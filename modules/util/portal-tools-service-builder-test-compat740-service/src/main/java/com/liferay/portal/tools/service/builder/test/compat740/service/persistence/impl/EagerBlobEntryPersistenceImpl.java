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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchEagerBlobEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.EagerBlobEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.EagerBlobEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.EagerBlobEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.EagerBlobEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.EagerBlobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.EagerBlobEntryUtil;
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
 * The persistence implementation for the eager blob entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = EagerBlobEntryPersistence.class)
public class EagerBlobEntryPersistenceImpl
	extends BasePersistenceImpl<EagerBlobEntry, NoSuchEagerBlobEntryException>
	implements EagerBlobEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>EagerBlobEntryUtil</code> to access the eager blob entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		EagerBlobEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<EagerBlobEntry, NoSuchEagerBlobEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the eager blob entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>EagerBlobEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of eager blob entries
	 * @param end the upper bound of the range of eager blob entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching eager blob entries
	 */
	@Override
	public List<EagerBlobEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<EagerBlobEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			dummyFinderCache, new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first eager blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry findByUuid_First(
			String uuid, OrderByComparator<EagerBlobEntry> orderByComparator)
		throws NoSuchEagerBlobEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			dummyFinderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first eager blob entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching eager blob entry, or <code>null</code> if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry fetchByUuid_First(
		String uuid, OrderByComparator<EagerBlobEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			dummyFinderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the eager blob entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			dummyFinderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of eager blob entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching eager blob entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			dummyFinderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<EagerBlobEntry, NoSuchEagerBlobEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the eager blob entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEagerBlobEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEagerBlobEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			dummyFinderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the eager blob entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching eager blob entry, or <code>null</code> if a matching eager blob entry could not be found
	 */
	@Override
	public EagerBlobEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			dummyFinderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the eager blob entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the eager blob entry that was removed
	 */
	@Override
	public EagerBlobEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEagerBlobEntryException {

		EagerBlobEntry eagerBlobEntry = findByUUID_G(uuid, groupId);

		return remove(eagerBlobEntry);
	}

	/**
	 * Returns the number of eager blob entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching eager blob entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			dummyFinderCache, new Object[] {uuid, groupId});
	}

	public EagerBlobEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("blob", "blob_");

		setDBColumnNames(dbColumnNames);

		setModelClass(EagerBlobEntry.class);

		setModelImplClass(EagerBlobEntryImpl.class);
		setModelPKClass(long.class);

		setTable(EagerBlobEntryTable.INSTANCE);
	}

	/**
	 * Creates a new eager blob entry with the primary key. Does not add the eager blob entry to the database.
	 *
	 * @param eagerBlobEntryId the primary key for the new eager blob entry
	 * @return the new eager blob entry
	 */
	@Override
	public EagerBlobEntry create(long eagerBlobEntryId) {
		EagerBlobEntry eagerBlobEntry = new EagerBlobEntryImpl();

		eagerBlobEntry.setNew(true);
		eagerBlobEntry.setPrimaryKey(eagerBlobEntryId);

		String uuid = PortalUUIDUtil.generate();

		eagerBlobEntry.setUuid(uuid);

		return eagerBlobEntry;
	}

	/**
	 * Removes the eager blob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param eagerBlobEntryId the primary key of the eager blob entry
	 * @return the eager blob entry that was removed
	 * @throws NoSuchEagerBlobEntryException if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry remove(long eagerBlobEntryId)
		throws NoSuchEagerBlobEntryException {

		return remove((Serializable)eagerBlobEntryId);
	}

	@Override
	protected EagerBlobEntry removeImpl(EagerBlobEntry eagerBlobEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(eagerBlobEntry)) {
				eagerBlobEntry = (EagerBlobEntry)session.get(
					EagerBlobEntryImpl.class,
					eagerBlobEntry.getPrimaryKeyObj());
			}

			if (eagerBlobEntry != null) {
				session.delete(eagerBlobEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (eagerBlobEntry != null) {
			clearCache(eagerBlobEntry);
		}

		return eagerBlobEntry;
	}

	@Override
	public EagerBlobEntry updateImpl(EagerBlobEntry eagerBlobEntry) {
		boolean isNew = eagerBlobEntry.isNew();

		if (!(eagerBlobEntry instanceof EagerBlobEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(eagerBlobEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					eagerBlobEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in eagerBlobEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom EagerBlobEntry implementation " +
					eagerBlobEntry.getClass());
		}

		EagerBlobEntryModelImpl eagerBlobEntryModelImpl =
			(EagerBlobEntryModelImpl)eagerBlobEntry;

		if (Validator.isNull(eagerBlobEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			eagerBlobEntry.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(eagerBlobEntry);
			}
			else {
				eagerBlobEntry = (EagerBlobEntry)session.merge(eagerBlobEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(eagerBlobEntry, false);

		if (isNew) {
			eagerBlobEntry.setNew(false);
		}

		eagerBlobEntry.resetOriginalValues();

		return eagerBlobEntry;
	}

	/**
	 * Returns the eager blob entry with the primary key or throws a <code>NoSuchEagerBlobEntryException</code> if it could not be found.
	 *
	 * @param eagerBlobEntryId the primary key of the eager blob entry
	 * @return the eager blob entry
	 * @throws NoSuchEagerBlobEntryException if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry findByPrimaryKey(long eagerBlobEntryId)
		throws NoSuchEagerBlobEntryException {

		return findByPrimaryKey((Serializable)eagerBlobEntryId);
	}

	/**
	 * Returns the eager blob entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param eagerBlobEntryId the primary key of the eager blob entry
	 * @return the eager blob entry, or <code>null</code> if a eager blob entry with the primary key could not be found
	 */
	@Override
	public EagerBlobEntry fetchByPrimaryKey(long eagerBlobEntryId) {
		return fetchByPrimaryKey((Serializable)eagerBlobEntryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "eagerBlobEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EAGERBLOBENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return EagerBlobEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the eager blob entry persistence.
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
			_SQL_SELECT_EAGERBLOBENTRY_WHERE, _SQL_COUNT_EAGERBLOBENTRY_WHERE,
			EagerBlobEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"eagerBlobEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, EagerBlobEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(EagerBlobEntry::getUuid),
				EagerBlobEntry::getGroupId),
			_SQL_SELECT_EAGERBLOBENTRY_WHERE, "",
			new FinderColumn<>(
				"eagerBlobEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, EagerBlobEntry::getUuid),
			new FinderColumn<>(
				"eagerBlobEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, EagerBlobEntry::getGroupId));

		EagerBlobEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		EagerBlobEntryUtil.setPersistence(null);

		dummyEntityCache.removeCache(EagerBlobEntryImpl.class.getName());
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

	private static final String _ENTITY_ALIAS_PREFIX =
		EagerBlobEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_EAGERBLOBENTRY =
		"SELECT eagerBlobEntry FROM EagerBlobEntry eagerBlobEntry";

	private static final String _SQL_SELECT_EAGERBLOBENTRY_WHERE =
		"SELECT eagerBlobEntry FROM EagerBlobEntry eagerBlobEntry WHERE ";

	private static final String _SQL_COUNT_EAGERBLOBENTRY_WHERE =
		"SELECT COUNT(eagerBlobEntry) FROM EagerBlobEntry eagerBlobEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No EagerBlobEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		EagerBlobEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "blob"});

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1547389084