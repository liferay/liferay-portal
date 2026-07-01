/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.reading.time.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import com.liferay.reading.time.exception.NoSuchEntryException;
import com.liferay.reading.time.model.ReadingTimeEntry;
import com.liferay.reading.time.model.ReadingTimeEntryTable;
import com.liferay.reading.time.model.impl.ReadingTimeEntryImpl;
import com.liferay.reading.time.model.impl.ReadingTimeEntryModelImpl;
import com.liferay.reading.time.service.persistence.ReadingTimeEntryPersistence;
import com.liferay.reading.time.service.persistence.ReadingTimeEntryUtil;
import com.liferay.reading.time.service.persistence.impl.constants.ReadingTimePersistenceConstants;

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
 * The persistence implementation for the reading time entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ReadingTimeEntryPersistence.class)
public class ReadingTimeEntryPersistenceImpl
	extends BasePersistenceImpl<ReadingTimeEntry, NoSuchEntryException>
	implements ReadingTimeEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ReadingTimeEntryUtil</code> to access the reading time entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ReadingTimeEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ReadingTimeEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the reading time entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ReadingTimeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of reading time entries
	 * @param end the upper bound of the range of reading time entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching reading time entries
	 */
	@Override
	public List<ReadingTimeEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ReadingTimeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first reading time entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching reading time entry
	 * @throws NoSuchEntryException if a matching reading time entry could not be found
	 */
	@Override
	public ReadingTimeEntry findByUuid_First(
			String uuid, OrderByComparator<ReadingTimeEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first reading time entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching reading time entry, or <code>null</code> if a matching reading time entry could not be found
	 */
	@Override
	public ReadingTimeEntry fetchByUuid_First(
		String uuid, OrderByComparator<ReadingTimeEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the reading time entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of reading time entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching reading time entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<ReadingTimeEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the reading time entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching reading time entry
	 * @throws NoSuchEntryException if a matching reading time entry could not be found
	 */
	@Override
	public ReadingTimeEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the reading time entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching reading time entry, or <code>null</code> if a matching reading time entry could not be found
	 */
	@Override
	public ReadingTimeEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the reading time entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the reading time entry that was removed
	 */
	@Override
	public ReadingTimeEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException {

		ReadingTimeEntry readingTimeEntry = findByUUID_G(uuid, groupId);

		return remove(readingTimeEntry);
	}

	/**
	 * Returns the number of reading time entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching reading time entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<ReadingTimeEntry, NoSuchEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the reading time entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ReadingTimeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of reading time entries
	 * @param end the upper bound of the range of reading time entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching reading time entries
	 */
	@Override
	public List<ReadingTimeEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ReadingTimeEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first reading time entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching reading time entry
	 * @throws NoSuchEntryException if a matching reading time entry could not be found
	 */
	@Override
	public ReadingTimeEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ReadingTimeEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first reading time entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching reading time entry, or <code>null</code> if a matching reading time entry could not be found
	 */
	@Override
	public ReadingTimeEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ReadingTimeEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the reading time entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of reading time entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching reading time entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder<ReadingTimeEntry, NoSuchEntryException>
		_uniquePersistenceFinderByG_C_C;

	/**
	 * Returns the reading time entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching reading time entry
	 * @throws NoSuchEntryException if a matching reading time entry could not be found
	 */
	@Override
	public ReadingTimeEntry findByG_C_C(
			long groupId, long classNameId, long classPK)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByG_C_C.find(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	/**
	 * Returns the reading time entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching reading time entry, or <code>null</code> if a matching reading time entry could not be found
	 */
	@Override
	public ReadingTimeEntry fetchByG_C_C(
		long groupId, long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_C.fetch(
			finderCache, new Object[] {groupId, classNameId, classPK},
			useFinderCache);
	}

	/**
	 * Removes the reading time entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the reading time entry that was removed
	 */
	@Override
	public ReadingTimeEntry removeByG_C_C(
			long groupId, long classNameId, long classPK)
		throws NoSuchEntryException {

		ReadingTimeEntry readingTimeEntry = findByG_C_C(
			groupId, classNameId, classPK);

		return remove(readingTimeEntry);
	}

	/**
	 * Returns the number of reading time entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching reading time entries
	 */
	@Override
	public int countByG_C_C(long groupId, long classNameId, long classPK) {
		return _uniquePersistenceFinderByG_C_C.count(
			finderCache, new Object[] {groupId, classNameId, classPK});
	}

	public ReadingTimeEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ReadingTimeEntry.class);

		setModelImplClass(ReadingTimeEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ReadingTimeEntryTable.INSTANCE);
	}

	/**
	 * Creates a new reading time entry with the primary key. Does not add the reading time entry to the database.
	 *
	 * @param readingTimeEntryId the primary key for the new reading time entry
	 * @return the new reading time entry
	 */
	@Override
	public ReadingTimeEntry create(long readingTimeEntryId) {
		ReadingTimeEntry readingTimeEntry = new ReadingTimeEntryImpl();

		readingTimeEntry.setNew(true);
		readingTimeEntry.setPrimaryKey(readingTimeEntryId);

		String uuid = PortalUUIDUtil.generate();

		readingTimeEntry.setUuid(uuid);

		readingTimeEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return readingTimeEntry;
	}

	/**
	 * Removes the reading time entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param readingTimeEntryId the primary key of the reading time entry
	 * @return the reading time entry that was removed
	 * @throws NoSuchEntryException if a reading time entry with the primary key could not be found
	 */
	@Override
	public ReadingTimeEntry remove(long readingTimeEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)readingTimeEntryId);
	}

	@Override
	protected ReadingTimeEntry removeImpl(ReadingTimeEntry readingTimeEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(readingTimeEntry)) {
				readingTimeEntry = (ReadingTimeEntry)session.get(
					ReadingTimeEntryImpl.class,
					readingTimeEntry.getPrimaryKeyObj());
			}

			if ((readingTimeEntry != null) &&
				ctPersistenceHelper.isRemove(readingTimeEntry)) {

				session.delete(readingTimeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (readingTimeEntry != null) {
			clearCache(readingTimeEntry);
		}

		return readingTimeEntry;
	}

	@Override
	public ReadingTimeEntry updateImpl(ReadingTimeEntry readingTimeEntry) {
		boolean isNew = readingTimeEntry.isNew();

		if (!(readingTimeEntry instanceof ReadingTimeEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(readingTimeEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					readingTimeEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in readingTimeEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ReadingTimeEntry implementation " +
					readingTimeEntry.getClass());
		}

		ReadingTimeEntryModelImpl readingTimeEntryModelImpl =
			(ReadingTimeEntryModelImpl)readingTimeEntry;

		if (Validator.isNull(readingTimeEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			readingTimeEntry.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (readingTimeEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				readingTimeEntry.setCreateDate(date);
			}
			else {
				readingTimeEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!readingTimeEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				readingTimeEntry.setModifiedDate(date);
			}
			else {
				readingTimeEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(readingTimeEntry)) {
				if (!isNew) {
					session.evict(
						ReadingTimeEntryImpl.class,
						readingTimeEntry.getPrimaryKeyObj());
				}

				session.save(readingTimeEntry);
			}
			else {
				readingTimeEntry = (ReadingTimeEntry)session.merge(
					readingTimeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(readingTimeEntry, false);

		if (isNew) {
			readingTimeEntry.setNew(false);
		}

		readingTimeEntry.resetOriginalValues();

		return readingTimeEntry;
	}

	/**
	 * Returns the reading time entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param readingTimeEntryId the primary key of the reading time entry
	 * @return the reading time entry
	 * @throws NoSuchEntryException if a reading time entry with the primary key could not be found
	 */
	@Override
	public ReadingTimeEntry findByPrimaryKey(long readingTimeEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)readingTimeEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the reading time entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param readingTimeEntryId the primary key of the reading time entry
	 * @return the reading time entry, or <code>null</code> if a reading time entry with the primary key could not be found
	 */
	@Override
	public ReadingTimeEntry fetchByPrimaryKey(long readingTimeEntryId) {
		return fetchByPrimaryKey((Serializable)readingTimeEntryId);
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
		return "readingTimeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_READINGTIMEENTRY;
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
		return ReadingTimeEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "ReadingTimeEntry";
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
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("readingTime");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("readingTimeEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "classPK"});
	}

	/**
	 * Initializes the reading time entry persistence.
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
			_SQL_SELECT_READINGTIMEENTRY_WHERE,
			_SQL_COUNT_READINGTIMEENTRY_WHERE,
			ReadingTimeEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"readingTimeEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ReadingTimeEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(ReadingTimeEntry::getUuid),
				ReadingTimeEntry::getGroupId),
			_SQL_SELECT_READINGTIMEENTRY_WHERE, "",
			new FinderColumn<>(
				"readingTimeEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ReadingTimeEntry::getUuid),
			new FinderColumn<>(
				"readingTimeEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, ReadingTimeEntry::getGroupId));

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
				_SQL_SELECT_READINGTIMEENTRY_WHERE,
				_SQL_COUNT_READINGTIMEENTRY_WHERE,
				ReadingTimeEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"readingTimeEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ReadingTimeEntry::getUuid),
				new FinderColumn<>(
					"readingTimeEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ReadingTimeEntry::getCompanyId));

		_uniquePersistenceFinderByG_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "classNameId", "classPK"}, 0, 0, false,
				ReadingTimeEntry::getGroupId, ReadingTimeEntry::getClassNameId,
				ReadingTimeEntry::getClassPK),
			_SQL_SELECT_READINGTIMEENTRY_WHERE, "",
			new FinderColumn<>(
				"readingTimeEntry.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, ReadingTimeEntry::getGroupId),
			new FinderColumn<>(
				"readingTimeEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, ReadingTimeEntry::getClassNameId),
			new FinderColumn<>(
				"readingTimeEntry.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, ReadingTimeEntry::getClassPK));

		ReadingTimeEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ReadingTimeEntryUtil.setPersistence(null);

		entityCache.removeCache(ReadingTimeEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = ReadingTimePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ReadingTimePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ReadingTimePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ReadingTimeEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_READINGTIMEENTRY =
		"SELECT readingTimeEntry FROM ReadingTimeEntry readingTimeEntry";

	private static final String _SQL_SELECT_READINGTIMEENTRY_WHERE =
		"SELECT readingTimeEntry FROM ReadingTimeEntry readingTimeEntry WHERE ";

	private static final String _SQL_COUNT_READINGTIMEENTRY_WHERE =
		"SELECT COUNT(readingTimeEntry) FROM ReadingTimeEntry readingTimeEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ReadingTimeEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ReadingTimeEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:12334744