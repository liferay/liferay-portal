/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchRepositoryEntryException;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.model.RepositoryEntryTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.RepositoryEntryPersistence;
import com.liferay.portal.kernel.service.persistence.RepositoryEntryUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.RepositoryEntryImpl;
import com.liferay.portal.model.impl.RepositoryEntryModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the repository entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RepositoryEntryPersistenceImpl
	extends BasePersistenceImpl<RepositoryEntry, NoSuchRepositoryEntryException>
	implements RepositoryEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RepositoryEntryUtil</code> to access the repository entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RepositoryEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<RepositoryEntry, NoSuchRepositoryEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the repository entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of repository entries
	 * @param end the upper bound of the range of repository entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repository entries
	 */
	@Override
	public List<RepositoryEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<RepositoryEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository entry
	 * @throws NoSuchRepositoryEntryException if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry findByUuid_First(
			String uuid, OrderByComparator<RepositoryEntry> orderByComparator)
		throws NoSuchRepositoryEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first repository entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository entry, or <code>null</code> if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry fetchByUuid_First(
		String uuid, OrderByComparator<RepositoryEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the repository entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of repository entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching repository entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<RepositoryEntry, NoSuchRepositoryEntryException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the repository entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchRepositoryEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching repository entry
	 * @throws NoSuchRepositoryEntryException if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchRepositoryEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the repository entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching repository entry, or <code>null</code> if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the repository entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the repository entry that was removed
	 */
	@Override
	public RepositoryEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchRepositoryEntryException {

		RepositoryEntry repositoryEntry = findByUUID_G(uuid, groupId);

		return remove(repositoryEntry);
	}

	/**
	 * Returns the number of repository entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching repository entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<RepositoryEntry, NoSuchRepositoryEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the repository entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of repository entries
	 * @param end the upper bound of the range of repository entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repository entries
	 */
	@Override
	public List<RepositoryEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<RepositoryEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository entry
	 * @throws NoSuchRepositoryEntryException if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<RepositoryEntry> orderByComparator)
		throws NoSuchRepositoryEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first repository entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository entry, or <code>null</code> if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<RepositoryEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the repository entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of repository entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching repository entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<RepositoryEntry, NoSuchRepositoryEntryException>
			_collectionPersistenceFinderByRepositoryId;

	/**
	 * Returns an ordered range of all the repository entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RepositoryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of repository entries
	 * @param end the upper bound of the range of repository entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching repository entries
	 */
	@Override
	public List<RepositoryEntry> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<RepositoryEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRepositoryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first repository entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository entry
	 * @throws NoSuchRepositoryEntryException if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry findByRepositoryId_First(
			long repositoryId,
			OrderByComparator<RepositoryEntry> orderByComparator)
		throws NoSuchRepositoryEntryException {

		return _collectionPersistenceFinderByRepositoryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			orderByComparator);
	}

	/**
	 * Returns the first repository entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching repository entry, or <code>null</code> if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry fetchByRepositoryId_First(
		long repositoryId,
		OrderByComparator<RepositoryEntry> orderByComparator) {

		return _collectionPersistenceFinderByRepositoryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId},
			orderByComparator);
	}

	/**
	 * Removes all the repository entries where repositoryId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 */
	@Override
	public void removeByRepositoryId(long repositoryId) {
		_collectionPersistenceFinderByRepositoryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId});
	}

	/**
	 * Returns the number of repository entries where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the number of matching repository entries
	 */
	@Override
	public int countByRepositoryId(long repositoryId) {
		return _collectionPersistenceFinderByRepositoryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {repositoryId});
	}

	private UniquePersistenceFinder
		<RepositoryEntry, NoSuchRepositoryEntryException>
			_uniquePersistenceFinderByR_M;

	/**
	 * Returns the repository entry where repositoryId = &#63; and mappedId = &#63; or throws a <code>NoSuchRepositoryEntryException</code> if it could not be found.
	 *
	 * @param repositoryId the repository ID
	 * @param mappedId the mapped ID
	 * @return the matching repository entry
	 * @throws NoSuchRepositoryEntryException if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry findByR_M(long repositoryId, String mappedId)
		throws NoSuchRepositoryEntryException {

		return _uniquePersistenceFinderByR_M.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, mappedId});
	}

	/**
	 * Returns the repository entry where repositoryId = &#63; and mappedId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param repositoryId the repository ID
	 * @param mappedId the mapped ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching repository entry, or <code>null</code> if a matching repository entry could not be found
	 */
	@Override
	public RepositoryEntry fetchByR_M(
		long repositoryId, String mappedId, boolean useFinderCache) {

		return _uniquePersistenceFinderByR_M.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, mappedId}, useFinderCache);
	}

	/**
	 * Removes the repository entry where repositoryId = &#63; and mappedId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param mappedId the mapped ID
	 * @return the repository entry that was removed
	 */
	@Override
	public RepositoryEntry removeByR_M(long repositoryId, String mappedId)
		throws NoSuchRepositoryEntryException {

		RepositoryEntry repositoryEntry = findByR_M(repositoryId, mappedId);

		return remove(repositoryEntry);
	}

	/**
	 * Returns the number of repository entries where repositoryId = &#63; and mappedId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param mappedId the mapped ID
	 * @return the number of matching repository entries
	 */
	@Override
	public int countByR_M(long repositoryId, String mappedId) {
		return _uniquePersistenceFinderByR_M.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {repositoryId, mappedId});
	}

	public RepositoryEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(RepositoryEntry.class);

		setModelImplClass(RepositoryEntryImpl.class);
		setModelPKClass(long.class);

		setTable(RepositoryEntryTable.INSTANCE);
	}

	/**
	 * Creates a new repository entry with the primary key. Does not add the repository entry to the database.
	 *
	 * @param repositoryEntryId the primary key for the new repository entry
	 * @return the new repository entry
	 */
	@Override
	public RepositoryEntry create(long repositoryEntryId) {
		RepositoryEntry repositoryEntry = new RepositoryEntryImpl();

		repositoryEntry.setNew(true);
		repositoryEntry.setPrimaryKey(repositoryEntryId);

		String uuid = PortalUUIDUtil.generate();

		repositoryEntry.setUuid(uuid);

		repositoryEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return repositoryEntry;
	}

	/**
	 * Removes the repository entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param repositoryEntryId the primary key of the repository entry
	 * @return the repository entry that was removed
	 * @throws NoSuchRepositoryEntryException if a repository entry with the primary key could not be found
	 */
	@Override
	public RepositoryEntry remove(long repositoryEntryId)
		throws NoSuchRepositoryEntryException {

		return remove((Serializable)repositoryEntryId);
	}

	@Override
	protected RepositoryEntry removeImpl(RepositoryEntry repositoryEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(repositoryEntry)) {
				repositoryEntry = (RepositoryEntry)session.get(
					RepositoryEntryImpl.class,
					repositoryEntry.getPrimaryKeyObj());
			}

			if ((repositoryEntry != null) &&
				CTPersistenceHelperUtil.isRemove(repositoryEntry)) {

				session.delete(repositoryEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (repositoryEntry != null) {
			clearCache(repositoryEntry);
		}

		return repositoryEntry;
	}

	@Override
	public RepositoryEntry updateImpl(RepositoryEntry repositoryEntry) {
		boolean isNew = repositoryEntry.isNew();

		if (!(repositoryEntry instanceof RepositoryEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(repositoryEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					repositoryEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in repositoryEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RepositoryEntry implementation " +
					repositoryEntry.getClass());
		}

		RepositoryEntryModelImpl repositoryEntryModelImpl =
			(RepositoryEntryModelImpl)repositoryEntry;

		if (Validator.isNull(repositoryEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			repositoryEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (repositoryEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				repositoryEntry.setCreateDate(date);
			}
			else {
				repositoryEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!repositoryEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				repositoryEntry.setModifiedDate(date);
			}
			else {
				repositoryEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(repositoryEntry)) {
				if (!isNew) {
					session.evict(
						RepositoryEntryImpl.class,
						repositoryEntry.getPrimaryKeyObj());
				}

				session.save(repositoryEntry);
			}
			else {
				repositoryEntry = (RepositoryEntry)session.merge(
					repositoryEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(repositoryEntry, false);

		if (isNew) {
			repositoryEntry.setNew(false);
		}

		repositoryEntry.resetOriginalValues();

		return repositoryEntry;
	}

	/**
	 * Returns the repository entry with the primary key or throws a <code>NoSuchRepositoryEntryException</code> if it could not be found.
	 *
	 * @param repositoryEntryId the primary key of the repository entry
	 * @return the repository entry
	 * @throws NoSuchRepositoryEntryException if a repository entry with the primary key could not be found
	 */
	@Override
	public RepositoryEntry findByPrimaryKey(long repositoryEntryId)
		throws NoSuchRepositoryEntryException {

		return findByPrimaryKey((Serializable)repositoryEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the repository entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param repositoryEntryId the primary key of the repository entry
	 * @return the repository entry, or <code>null</code> if a repository entry with the primary key could not be found
	 */
	@Override
	public RepositoryEntry fetchByPrimaryKey(long repositoryEntryId) {
		return fetchByPrimaryKey((Serializable)repositoryEntryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "repositoryEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REPOSITORYENTRY;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return RepositoryEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "RepositoryEntry";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("mappedId");
		ctMergeColumnNames.add("manualCheckInRequired");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("repositoryEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"repositoryId", "mappedId"});
	}

	/**
	 * Initializes the repository entry persistence.
	 */
	public void afterPropertiesSet() {
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
			_SQL_SELECT_REPOSITORYENTRY_WHERE, _SQL_COUNT_REPOSITORYENTRY_WHERE,
			RepositoryEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"repositoryEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, RepositoryEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(RepositoryEntry::getUuid),
				RepositoryEntry::getGroupId),
			_SQL_SELECT_REPOSITORYENTRY_WHERE, "",
			new FinderColumn<>(
				"repositoryEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, RepositoryEntry::getUuid),
			new FinderColumn<>(
				"repositoryEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, RepositoryEntry::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_REPOSITORYENTRY_WHERE,
				_SQL_COUNT_REPOSITORYENTRY_WHERE,
				RepositoryEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"repositoryEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					RepositoryEntry::getUuid),
				new FinderColumn<>(
					"repositoryEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, RepositoryEntry::getCompanyId));

		_collectionPersistenceFinderByRepositoryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByRepositoryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"repositoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByRepositoryId", new String[] {Long.class.getName()},
					new String[] {"repositoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRepositoryId", new String[] {Long.class.getName()},
					new String[] {"repositoryId"}, false),
				_SQL_SELECT_REPOSITORYENTRY_WHERE,
				_SQL_COUNT_REPOSITORYENTRY_WHERE,
				RepositoryEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"repositoryEntry.", "repositoryId", FinderColumn.Type.LONG,
					"=", true, true, RepositoryEntry::getRepositoryId));

		_uniquePersistenceFinderByR_M = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByR_M",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"repositoryId", "mappedId"}, 0, 2, false,
				RepositoryEntry::getRepositoryId,
				convertNullFunction(RepositoryEntry::getMappedId)),
			_SQL_SELECT_REPOSITORYENTRY_WHERE, "",
			new FinderColumn<>(
				"repositoryEntry.", "repositoryId", FinderColumn.Type.LONG, "=",
				true, true, RepositoryEntry::getRepositoryId),
			new FinderColumn<>(
				"repositoryEntry.", "mappedId", FinderColumn.Type.STRING, "=",
				true, true, RepositoryEntry::getMappedId));

		RepositoryEntryUtil.setPersistence(this);
	}

	public void destroy() {
		RepositoryEntryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RepositoryEntryImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		RepositoryEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_REPOSITORYENTRY =
		"SELECT repositoryEntry FROM RepositoryEntry repositoryEntry";

	private static final String _SQL_SELECT_REPOSITORYENTRY_WHERE =
		"SELECT repositoryEntry FROM RepositoryEntry repositoryEntry WHERE ";

	private static final String _SQL_COUNT_REPOSITORYENTRY_WHERE =
		"SELECT COUNT(repositoryEntry) FROM RepositoryEntry repositoryEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:465666749