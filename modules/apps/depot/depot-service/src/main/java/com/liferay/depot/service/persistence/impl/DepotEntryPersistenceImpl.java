/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence.impl;

import com.liferay.depot.exception.NoSuchEntryException;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryTable;
import com.liferay.depot.model.impl.DepotEntryImpl;
import com.liferay.depot.model.impl.DepotEntryModelImpl;
import com.liferay.depot.service.persistence.DepotEntryPersistence;
import com.liferay.depot.service.persistence.DepotEntryUtil;
import com.liferay.depot.service.persistence.impl.constants.DepotPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

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

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the depot entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DepotEntryPersistence.class)
public class DepotEntryPersistenceImpl
	extends BasePersistenceImpl<DepotEntry, NoSuchEntryException>
	implements DepotEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DepotEntryUtil</code> to access the depot entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DepotEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DepotEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the depot entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entries
	 */
	@Override
	public List<DepotEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DepotEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first depot entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry findByUuid_First(
			String uuid, OrderByComparator<DepotEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first depot entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry fetchByUuid_First(
		String uuid, OrderByComparator<DepotEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the depot entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of depot entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching depot entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<DepotEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the depot entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the depot entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the depot entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the depot entry that was removed
	 */
	@Override
	public DepotEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		DepotEntry depotEntry = findByUUID_G(uuid, groupId);

		return remove(depotEntry);
	}

	/**
	 * Returns the number of depot entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching depot entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<DepotEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the depot entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entries
	 */
	@Override
	public List<DepotEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DepotEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DepotEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first depot entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DepotEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the depot entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of depot entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching depot entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder<DepotEntry, NoSuchEntryException>
		_uniquePersistenceFinderByGroupId;

	/**
	 * Returns the depot entry where groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @return the matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry findByGroupId(long groupId) throws NoSuchEntryException {
		return _uniquePersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the depot entry where groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry fetchByGroupId(long groupId, boolean useFinderCache) {
		return _uniquePersistenceFinderByGroupId.fetch(
			finderCache, new Object[] {groupId}, useFinderCache);
	}

	/**
	 * Removes the depot entry where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @return the depot entry that was removed
	 */
	@Override
	public DepotEntry removeByGroupId(long groupId)
		throws NoSuchEntryException {

		DepotEntry depotEntry = findByGroupId(groupId);

		return remove(depotEntry);
	}

	/**
	 * Returns the number of depot entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching depot entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _uniquePersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder<DepotEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the depot entries where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entries
	 */
	@Override
	public List<DepotEntry> findByC_T(
		long companyId, int type, int start, int end,
		OrderByComparator<DepotEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			finderCache, new Object[] {companyId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry findByC_T_First(
			long companyId, int type,
			OrderByComparator<DepotEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_T.findFirst(
			finderCache, new Object[] {companyId, type}, orderByComparator);
	}

	/**
	 * Returns the first depot entry in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	@Override
	public DepotEntry fetchByC_T_First(
		long companyId, int type,
		OrderByComparator<DepotEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			finderCache, new Object[] {companyId, type}, orderByComparator);
	}

	/**
	 * Removes all the depot entries where companyId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	@Override
	public void removeByC_T(long companyId, int type) {
		_collectionPersistenceFinderByC_T.remove(
			finderCache, new Object[] {companyId, type});
	}

	/**
	 * Returns the number of depot entries where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching depot entries
	 */
	@Override
	public int countByC_T(long companyId, int type) {
		return _collectionPersistenceFinderByC_T.count(
			finderCache, new Object[] {companyId, type});
	}

	public DepotEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DepotEntry.class);

		setModelImplClass(DepotEntryImpl.class);
		setModelPKClass(long.class);

		setTable(DepotEntryTable.INSTANCE);
	}

	/**
	 * Creates a new depot entry with the primary key. Does not add the depot entry to the database.
	 *
	 * @param depotEntryId the primary key for the new depot entry
	 * @return the new depot entry
	 */
	@Override
	public DepotEntry create(long depotEntryId) {
		DepotEntry depotEntry = new DepotEntryImpl();

		depotEntry.setNew(true);
		depotEntry.setPrimaryKey(depotEntryId);

		String uuid = PortalUUIDUtil.generate();

		depotEntry.setUuid(uuid);

		depotEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return depotEntry;
	}

	/**
	 * Removes the depot entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry that was removed
	 * @throws NoSuchEntryException if a depot entry with the primary key could not be found
	 */
	@Override
	public DepotEntry remove(long depotEntryId) throws NoSuchEntryException {
		return remove((Serializable)depotEntryId);
	}

	@Override
	protected DepotEntry removeImpl(DepotEntry depotEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(depotEntry)) {
				depotEntry = (DepotEntry)session.get(
					DepotEntryImpl.class, depotEntry.getPrimaryKeyObj());
			}

			if ((depotEntry != null) &&
				ctPersistenceHelper.isRemove(depotEntry)) {

				session.delete(depotEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (depotEntry != null) {
			clearCache(depotEntry);
		}

		return depotEntry;
	}

	@Override
	public DepotEntry updateImpl(DepotEntry depotEntry) {
		boolean isNew = depotEntry.isNew();

		if (!(depotEntry instanceof DepotEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(depotEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(depotEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in depotEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DepotEntry implementation " +
					depotEntry.getClass());
		}

		DepotEntryModelImpl depotEntryModelImpl =
			(DepotEntryModelImpl)depotEntry;

		if (Validator.isNull(depotEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			depotEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (depotEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				depotEntry.setCreateDate(date);
			}
			else {
				depotEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!depotEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				depotEntry.setModifiedDate(date);
			}
			else {
				depotEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(depotEntry)) {
				if (!isNew) {
					session.evict(
						DepotEntryImpl.class, depotEntry.getPrimaryKeyObj());
				}

				session.save(depotEntry);
			}
			else {
				depotEntry = (DepotEntry)session.merge(depotEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(depotEntry, false);

		if (isNew) {
			depotEntry.setNew(false);
		}

		depotEntry.resetOriginalValues();

		return depotEntry;
	}

	/**
	 * Returns the depot entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry
	 * @throws NoSuchEntryException if a depot entry with the primary key could not be found
	 */
	@Override
	public DepotEntry findByPrimaryKey(long depotEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)depotEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the depot entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry, or <code>null</code> if a depot entry with the primary key could not be found
	 */
	@Override
	public DepotEntry fetchByPrimaryKey(long depotEntryId) {
		return fetchByPrimaryKey((Serializable)depotEntryId);
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
		return "depotEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DEPOTENTRY;
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
		return DepotEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DepotEntry";
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
		ctMergeColumnNames.add("type_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("depotEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId"});
	}

	/**
	 * Initializes the depot entry persistence.
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
			_SQL_SELECT_DEPOTENTRY_WHERE, _SQL_COUNT_DEPOTENTRY_WHERE,
			DepotEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"depotEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DepotEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DepotEntry::getUuid),
				DepotEntry::getGroupId),
			_SQL_SELECT_DEPOTENTRY_WHERE, "",
			new FinderColumn<>(
				"depotEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DepotEntry::getUuid),
			new FinderColumn<>(
				"depotEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DepotEntry::getGroupId));

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
				_SQL_SELECT_DEPOTENTRY_WHERE, _SQL_COUNT_DEPOTENTRY_WHERE,
				DepotEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"depotEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, DepotEntry::getUuid),
				new FinderColumn<>(
					"depotEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DepotEntry::getCompanyId));

		_uniquePersistenceFinderByGroupId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByGroupId",
				new String[] {Long.class.getName()}, new String[] {"groupId"},
				0, 0, false, DepotEntry::getGroupId),
			_SQL_SELECT_DEPOTENTRY_WHERE, "",
			new FinderColumn<>(
				"depotEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DepotEntry::getGroupId));

		_collectionPersistenceFinderByC_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"companyId", "type_"}, false),
			_SQL_SELECT_DEPOTENTRY_WHERE, _SQL_COUNT_DEPOTENTRY_WHERE,
			DepotEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"depotEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, DepotEntry::getCompanyId),
			new FinderColumn<>(
				"depotEntry.", "type", "type_", FinderColumn.Type.INTEGER, "=",
				true, true, DepotEntry::getType));

		DepotEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DepotEntryUtil.setPersistence(null);

		entityCache.removeCache(DepotEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DepotPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		DepotEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DEPOTENTRY =
		"SELECT depotEntry FROM DepotEntry depotEntry";

	private static final String _SQL_SELECT_DEPOTENTRY_WHERE =
		"SELECT depotEntry FROM DepotEntry depotEntry WHERE ";

	private static final String _SQL_COUNT_DEPOTENTRY_WHERE =
		"SELECT COUNT(depotEntry) FROM DepotEntry depotEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1130099054