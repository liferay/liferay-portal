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
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchVersionedEntryVersionException;
import com.liferay.portal.tools.service.builder.test.compat740.model.VersionedEntryVersion;
import com.liferay.portal.tools.service.builder.test.compat740.model.VersionedEntryVersionTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.VersionedEntryVersionImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.VersionedEntryVersionModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.VersionedEntryVersionPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.VersionedEntryVersionUtil;
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
 * The persistence implementation for the versioned entry version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = VersionedEntryVersionPersistence.class)
public class VersionedEntryVersionPersistenceImpl
	extends BasePersistenceImpl
		<VersionedEntryVersion, NoSuchVersionedEntryVersionException>
	implements VersionedEntryVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>VersionedEntryVersionUtil</code> to access the versioned entry version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		VersionedEntryVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<VersionedEntryVersion, NoSuchVersionedEntryVersionException>
			_collectionPersistenceFinderByVersionedEntryId;

	/**
	 * Returns an ordered range of all the versioned entry versions where versionedEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByVersionedEntryId(
		long versionedEntryId, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByVersionedEntryId.find(
			finderCache, new Object[] {versionedEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first versioned entry version in the ordered set where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByVersionedEntryId_First(
			long versionedEntryId,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		return _collectionPersistenceFinderByVersionedEntryId.findFirst(
			finderCache, new Object[] {versionedEntryId}, orderByComparator);
	}

	/**
	 * Returns the first versioned entry version in the ordered set where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByVersionedEntryId_First(
		long versionedEntryId,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByVersionedEntryId.fetchFirst(
			finderCache, new Object[] {versionedEntryId}, orderByComparator);
	}

	/**
	 * Removes all the versioned entry versions where versionedEntryId = &#63; from the database.
	 *
	 * @param versionedEntryId the versioned entry ID
	 */
	@Override
	public void removeByVersionedEntryId(long versionedEntryId) {
		_collectionPersistenceFinderByVersionedEntryId.remove(
			finderCache, new Object[] {versionedEntryId});
	}

	/**
	 * Returns the number of versioned entry versions where versionedEntryId = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @return the number of matching versioned entry versions
	 */
	@Override
	public int countByVersionedEntryId(long versionedEntryId) {
		return _collectionPersistenceFinderByVersionedEntryId.count(
			finderCache, new Object[] {versionedEntryId});
	}

	private UniquePersistenceFinder
		<VersionedEntryVersion, NoSuchVersionedEntryVersionException>
			_uniquePersistenceFinderByVersionedEntryId_Version;

	/**
	 * Returns the versioned entry version where versionedEntryId = &#63; and version = &#63; or throws a <code>NoSuchVersionedEntryVersionException</code> if it could not be found.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @return the matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByVersionedEntryId_Version(
			long versionedEntryId, int version)
		throws NoSuchVersionedEntryVersionException {

		return _uniquePersistenceFinderByVersionedEntryId_Version.find(
			finderCache, new Object[] {versionedEntryId, version});
	}

	/**
	 * Returns the versioned entry version where versionedEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByVersionedEntryId_Version(
		long versionedEntryId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByVersionedEntryId_Version.fetch(
			finderCache, new Object[] {versionedEntryId, version},
			useFinderCache);
	}

	/**
	 * Removes the versioned entry version where versionedEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @return the versioned entry version that was removed
	 */
	@Override
	public VersionedEntryVersion removeByVersionedEntryId_Version(
			long versionedEntryId, int version)
		throws NoSuchVersionedEntryVersionException {

		VersionedEntryVersion versionedEntryVersion =
			findByVersionedEntryId_Version(versionedEntryId, version);

		return remove(versionedEntryVersion);
	}

	/**
	 * Returns the number of versioned entry versions where versionedEntryId = &#63; and version = &#63;.
	 *
	 * @param versionedEntryId the versioned entry ID
	 * @param version the version
	 * @return the number of matching versioned entry versions
	 */
	@Override
	public int countByVersionedEntryId_Version(
		long versionedEntryId, int version) {

		return _uniquePersistenceFinderByVersionedEntryId_Version.count(
			finderCache, new Object[] {versionedEntryId, version});
	}

	private CollectionPersistenceFinder
		<VersionedEntryVersion, NoSuchVersionedEntryVersionException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the versioned entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first versioned entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByGroupId_First(
			long groupId,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first versioned entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByGroupId_First(
		long groupId,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the versioned entry versions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of versioned entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching versioned entry versions
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<VersionedEntryVersion, NoSuchVersionedEntryVersionException>
			_collectionPersistenceFinderByGroupId_Version;

	/**
	 * Returns an ordered range of all the versioned entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>VersionedEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of versioned entry versions
	 * @param end the upper bound of the range of versioned entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching versioned entry versions
	 */
	@Override
	public List<VersionedEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		OrderByComparator<VersionedEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId_Version.find(
			finderCache, new Object[] {groupId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first versioned entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion findByGroupId_Version_First(
			long groupId, int version,
			OrderByComparator<VersionedEntryVersion> orderByComparator)
		throws NoSuchVersionedEntryVersionException {

		return _collectionPersistenceFinderByGroupId_Version.findFirst(
			finderCache, new Object[] {groupId, version}, orderByComparator);
	}

	/**
	 * Returns the first versioned entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching versioned entry version, or <code>null</code> if a matching versioned entry version could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByGroupId_Version_First(
		long groupId, int version,
		OrderByComparator<VersionedEntryVersion> orderByComparator) {

		return _collectionPersistenceFinderByGroupId_Version.fetchFirst(
			finderCache, new Object[] {groupId, version}, orderByComparator);
	}

	/**
	 * Removes all the versioned entry versions where groupId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 */
	@Override
	public void removeByGroupId_Version(long groupId, int version) {
		_collectionPersistenceFinderByGroupId_Version.remove(
			finderCache, new Object[] {groupId, version});
	}

	/**
	 * Returns the number of versioned entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching versioned entry versions
	 */
	@Override
	public int countByGroupId_Version(long groupId, int version) {
		return _collectionPersistenceFinderByGroupId_Version.count(
			finderCache, new Object[] {groupId, version});
	}

	public VersionedEntryVersionPersistenceImpl() {
		setModelClass(VersionedEntryVersion.class);

		setModelImplClass(VersionedEntryVersionImpl.class);
		setModelPKClass(long.class);

		setTable(VersionedEntryVersionTable.INSTANCE);
	}

	/**
	 * Creates a new versioned entry version with the primary key. Does not add the versioned entry version to the database.
	 *
	 * @param versionedEntryVersionId the primary key for the new versioned entry version
	 * @return the new versioned entry version
	 */
	@Override
	public VersionedEntryVersion create(long versionedEntryVersionId) {
		VersionedEntryVersion versionedEntryVersion =
			new VersionedEntryVersionImpl();

		versionedEntryVersion.setNew(true);
		versionedEntryVersion.setPrimaryKey(versionedEntryVersionId);

		return versionedEntryVersion;
	}

	/**
	 * Removes the versioned entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param versionedEntryVersionId the primary key of the versioned entry version
	 * @return the versioned entry version that was removed
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion remove(long versionedEntryVersionId)
		throws NoSuchVersionedEntryVersionException {

		return remove((Serializable)versionedEntryVersionId);
	}

	@Override
	protected VersionedEntryVersion removeImpl(
		VersionedEntryVersion versionedEntryVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(versionedEntryVersion)) {
				versionedEntryVersion = (VersionedEntryVersion)session.get(
					VersionedEntryVersionImpl.class,
					versionedEntryVersion.getPrimaryKeyObj());
			}

			if (versionedEntryVersion != null) {
				session.delete(versionedEntryVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (versionedEntryVersion != null) {
			clearCache(versionedEntryVersion);
		}

		return versionedEntryVersion;
	}

	@Override
	public VersionedEntryVersion updateImpl(
		VersionedEntryVersion versionedEntryVersion) {

		boolean isNew = versionedEntryVersion.isNew();

		if (!(versionedEntryVersion instanceof
				VersionedEntryVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(versionedEntryVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					versionedEntryVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in versionedEntryVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom VersionedEntryVersion implementation " +
					versionedEntryVersion.getClass());
		}

		VersionedEntryVersionModelImpl versionedEntryVersionModelImpl =
			(VersionedEntryVersionModelImpl)versionedEntryVersion;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(versionedEntryVersion);
			}
			else {
				throw new IllegalArgumentException(
					"VersionedEntryVersion is read only, create a new version instead");
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(versionedEntryVersion, false);

		if (isNew) {
			versionedEntryVersion.setNew(false);
		}

		versionedEntryVersion.resetOriginalValues();

		return versionedEntryVersion;
	}

	/**
	 * Returns the versioned entry version with the primary key or throws a <code>NoSuchVersionedEntryVersionException</code> if it could not be found.
	 *
	 * @param versionedEntryVersionId the primary key of the versioned entry version
	 * @return the versioned entry version
	 * @throws NoSuchVersionedEntryVersionException if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion findByPrimaryKey(long versionedEntryVersionId)
		throws NoSuchVersionedEntryVersionException {

		return findByPrimaryKey((Serializable)versionedEntryVersionId);
	}

	/**
	 * Returns the versioned entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param versionedEntryVersionId the primary key of the versioned entry version
	 * @return the versioned entry version, or <code>null</code> if a versioned entry version with the primary key could not be found
	 */
	@Override
	public VersionedEntryVersion fetchByPrimaryKey(
		long versionedEntryVersionId) {

		return fetchByPrimaryKey((Serializable)versionedEntryVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "versionedEntryVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_VERSIONEDENTRYVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return VersionedEntryVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the versioned entry version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByVersionedEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByVersionedEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"versionedEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByVersionedEntryId",
					new String[] {Long.class.getName()},
					new String[] {"versionedEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByVersionedEntryId",
					new String[] {Long.class.getName()},
					new String[] {"versionedEntryId"}, false),
				_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE,
				_SQL_COUNT_VERSIONEDENTRYVERSION_WHERE,
				VersionedEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"versionedEntryVersion.", "versionedEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					VersionedEntryVersion::getVersionedEntryId));

		_uniquePersistenceFinderByVersionedEntryId_Version =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByVersionedEntryId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"versionedEntryId", "version"}, 0, 0, false,
					VersionedEntryVersion::getVersionedEntryId,
					VersionedEntryVersion::getVersion),
				_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE, "",
				new FinderColumn<>(
					"versionedEntryVersion.", "versionedEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					VersionedEntryVersion::getVersionedEntryId),
				new FinderColumn<>(
					"versionedEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					VersionedEntryVersion::getVersion));

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
				_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE,
				_SQL_COUNT_VERSIONEDENTRYVERSION_WHERE,
				VersionedEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"versionedEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, VersionedEntryVersion::getGroupId));

		_collectionPersistenceFinderByGroupId_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByGroupId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByGroupId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByGroupId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "version"}, false),
				_SQL_SELECT_VERSIONEDENTRYVERSION_WHERE,
				_SQL_COUNT_VERSIONEDENTRYVERSION_WHERE,
				VersionedEntryVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"versionedEntryVersion.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, VersionedEntryVersion::getGroupId),
				new FinderColumn<>(
					"versionedEntryVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					VersionedEntryVersion::getVersion));

		VersionedEntryVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		VersionedEntryVersionUtil.setPersistence(null);

		entityCache.removeCache(VersionedEntryVersionImpl.class.getName());
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
		VersionedEntryVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_VERSIONEDENTRYVERSION =
		"SELECT versionedEntryVersion FROM VersionedEntryVersion versionedEntryVersion";

	private static final String _SQL_SELECT_VERSIONEDENTRYVERSION_WHERE =
		"SELECT versionedEntryVersion FROM VersionedEntryVersion versionedEntryVersion WHERE ";

	private static final String _SQL_COUNT_VERSIONEDENTRYVERSION_WHERE =
		"SELECT COUNT(versionedEntryVersion) FROM VersionedEntryVersion versionedEntryVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No VersionedEntryVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		VersionedEntryVersionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1146170334