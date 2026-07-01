/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence.impl;

import com.liferay.dynamic.data.lists.exception.NoSuchRecordSetVersionException;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersionTable;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetVersionImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetVersionModelImpl;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordSetVersionPersistence;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordSetVersionUtil;
import com.liferay.dynamic.data.lists.service.persistence.impl.constants.DDLPersistenceConstants;
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
 * The persistence implementation for the ddl record set version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDLRecordSetVersionPersistence.class)
public class DDLRecordSetVersionPersistenceImpl
	extends BasePersistenceImpl
		<DDLRecordSetVersion, NoSuchRecordSetVersionException>
	implements DDLRecordSetVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDLRecordSetVersionUtil</code> to access the ddl record set version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDLRecordSetVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDLRecordSetVersion, NoSuchRecordSetVersionException>
			_collectionPersistenceFinderByRecordSetId;

	/**
	 * Returns an ordered range of all the ddl record set versions where recordSetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordSetId the record set ID
	 * @param start the lower bound of the range of ddl record set versions
	 * @param end the upper bound of the range of ddl record set versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record set versions
	 */
	@Override
	public List<DDLRecordSetVersion> findByRecordSetId(
		long recordSetId, int start, int end,
		OrderByComparator<DDLRecordSetVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRecordSetId.find(
			finderCache, new Object[] {recordSetId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddl record set version in the ordered set where recordSetId = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set version
	 * @throws NoSuchRecordSetVersionException if a matching ddl record set version could not be found
	 */
	@Override
	public DDLRecordSetVersion findByRecordSetId_First(
			long recordSetId,
			OrderByComparator<DDLRecordSetVersion> orderByComparator)
		throws NoSuchRecordSetVersionException {

		return _collectionPersistenceFinderByRecordSetId.findFirst(
			finderCache, new Object[] {recordSetId}, orderByComparator);
	}

	/**
	 * Returns the first ddl record set version in the ordered set where recordSetId = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set version, or <code>null</code> if a matching ddl record set version could not be found
	 */
	@Override
	public DDLRecordSetVersion fetchByRecordSetId_First(
		long recordSetId,
		OrderByComparator<DDLRecordSetVersion> orderByComparator) {

		return _collectionPersistenceFinderByRecordSetId.fetchFirst(
			finderCache, new Object[] {recordSetId}, orderByComparator);
	}

	/**
	 * Removes all the ddl record set versions where recordSetId = &#63; from the database.
	 *
	 * @param recordSetId the record set ID
	 */
	@Override
	public void removeByRecordSetId(long recordSetId) {
		_collectionPersistenceFinderByRecordSetId.remove(
			finderCache, new Object[] {recordSetId});
	}

	/**
	 * Returns the number of ddl record set versions where recordSetId = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @return the number of matching ddl record set versions
	 */
	@Override
	public int countByRecordSetId(long recordSetId) {
		return _collectionPersistenceFinderByRecordSetId.count(
			finderCache, new Object[] {recordSetId});
	}

	private UniquePersistenceFinder
		<DDLRecordSetVersion, NoSuchRecordSetVersionException>
			_uniquePersistenceFinderByRS_V;

	/**
	 * Returns the ddl record set version where recordSetId = &#63; and version = &#63; or throws a <code>NoSuchRecordSetVersionException</code> if it could not be found.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @return the matching ddl record set version
	 * @throws NoSuchRecordSetVersionException if a matching ddl record set version could not be found
	 */
	@Override
	public DDLRecordSetVersion findByRS_V(long recordSetId, String version)
		throws NoSuchRecordSetVersionException {

		return _uniquePersistenceFinderByRS_V.find(
			finderCache, new Object[] {recordSetId, version});
	}

	/**
	 * Returns the ddl record set version where recordSetId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddl record set version, or <code>null</code> if a matching ddl record set version could not be found
	 */
	@Override
	public DDLRecordSetVersion fetchByRS_V(
		long recordSetId, String version, boolean useFinderCache) {

		return _uniquePersistenceFinderByRS_V.fetch(
			finderCache, new Object[] {recordSetId, version}, useFinderCache);
	}

	/**
	 * Removes the ddl record set version where recordSetId = &#63; and version = &#63; from the database.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @return the ddl record set version that was removed
	 */
	@Override
	public DDLRecordSetVersion removeByRS_V(long recordSetId, String version)
		throws NoSuchRecordSetVersionException {

		DDLRecordSetVersion ddlRecordSetVersion = findByRS_V(
			recordSetId, version);

		return remove(ddlRecordSetVersion);
	}

	/**
	 * Returns the number of ddl record set versions where recordSetId = &#63; and version = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param version the version
	 * @return the number of matching ddl record set versions
	 */
	@Override
	public int countByRS_V(long recordSetId, String version) {
		return _uniquePersistenceFinderByRS_V.count(
			finderCache, new Object[] {recordSetId, version});
	}

	private CollectionPersistenceFinder
		<DDLRecordSetVersion, NoSuchRecordSetVersionException>
			_collectionPersistenceFinderByRS_S;

	/**
	 * Returns an ordered range of all the ddl record set versions where recordSetId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordSetId the record set ID
	 * @param status the status
	 * @param start the lower bound of the range of ddl record set versions
	 * @param end the upper bound of the range of ddl record set versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record set versions
	 */
	@Override
	public List<DDLRecordSetVersion> findByRS_S(
		long recordSetId, int status, int start, int end,
		OrderByComparator<DDLRecordSetVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRS_S.find(
			finderCache, new Object[] {recordSetId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddl record set version in the ordered set where recordSetId = &#63; and status = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set version
	 * @throws NoSuchRecordSetVersionException if a matching ddl record set version could not be found
	 */
	@Override
	public DDLRecordSetVersion findByRS_S_First(
			long recordSetId, int status,
			OrderByComparator<DDLRecordSetVersion> orderByComparator)
		throws NoSuchRecordSetVersionException {

		return _collectionPersistenceFinderByRS_S.findFirst(
			finderCache, new Object[] {recordSetId, status}, orderByComparator);
	}

	/**
	 * Returns the first ddl record set version in the ordered set where recordSetId = &#63; and status = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set version, or <code>null</code> if a matching ddl record set version could not be found
	 */
	@Override
	public DDLRecordSetVersion fetchByRS_S_First(
		long recordSetId, int status,
		OrderByComparator<DDLRecordSetVersion> orderByComparator) {

		return _collectionPersistenceFinderByRS_S.fetchFirst(
			finderCache, new Object[] {recordSetId, status}, orderByComparator);
	}

	/**
	 * Removes all the ddl record set versions where recordSetId = &#63; and status = &#63; from the database.
	 *
	 * @param recordSetId the record set ID
	 * @param status the status
	 */
	@Override
	public void removeByRS_S(long recordSetId, int status) {
		_collectionPersistenceFinderByRS_S.remove(
			finderCache, new Object[] {recordSetId, status});
	}

	/**
	 * Returns the number of ddl record set versions where recordSetId = &#63; and status = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param status the status
	 * @return the number of matching ddl record set versions
	 */
	@Override
	public int countByRS_S(long recordSetId, int status) {
		return _collectionPersistenceFinderByRS_S.count(
			finderCache, new Object[] {recordSetId, status});
	}

	public DDLRecordSetVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDLRecordSetVersion.class);

		setModelImplClass(DDLRecordSetVersionImpl.class);
		setModelPKClass(long.class);

		setTable(DDLRecordSetVersionTable.INSTANCE);
	}

	/**
	 * Creates a new ddl record set version with the primary key. Does not add the ddl record set version to the database.
	 *
	 * @param recordSetVersionId the primary key for the new ddl record set version
	 * @return the new ddl record set version
	 */
	@Override
	public DDLRecordSetVersion create(long recordSetVersionId) {
		DDLRecordSetVersion ddlRecordSetVersion = new DDLRecordSetVersionImpl();

		ddlRecordSetVersion.setNew(true);
		ddlRecordSetVersion.setPrimaryKey(recordSetVersionId);

		ddlRecordSetVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddlRecordSetVersion;
	}

	/**
	 * Removes the ddl record set version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recordSetVersionId the primary key of the ddl record set version
	 * @return the ddl record set version that was removed
	 * @throws NoSuchRecordSetVersionException if a ddl record set version with the primary key could not be found
	 */
	@Override
	public DDLRecordSetVersion remove(long recordSetVersionId)
		throws NoSuchRecordSetVersionException {

		return remove((Serializable)recordSetVersionId);
	}

	@Override
	protected DDLRecordSetVersion removeImpl(
		DDLRecordSetVersion ddlRecordSetVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddlRecordSetVersion)) {
				ddlRecordSetVersion = (DDLRecordSetVersion)session.get(
					DDLRecordSetVersionImpl.class,
					ddlRecordSetVersion.getPrimaryKeyObj());
			}

			if ((ddlRecordSetVersion != null) &&
				ctPersistenceHelper.isRemove(ddlRecordSetVersion)) {

				session.delete(ddlRecordSetVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddlRecordSetVersion != null) {
			clearCache(ddlRecordSetVersion);
		}

		return ddlRecordSetVersion;
	}

	@Override
	public DDLRecordSetVersion updateImpl(
		DDLRecordSetVersion ddlRecordSetVersion) {

		boolean isNew = ddlRecordSetVersion.isNew();

		if (!(ddlRecordSetVersion instanceof DDLRecordSetVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddlRecordSetVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddlRecordSetVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddlRecordSetVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDLRecordSetVersion implementation " +
					ddlRecordSetVersion.getClass());
		}

		DDLRecordSetVersionModelImpl ddlRecordSetVersionModelImpl =
			(DDLRecordSetVersionModelImpl)ddlRecordSetVersion;

		if (isNew && (ddlRecordSetVersion.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				ddlRecordSetVersion.setCreateDate(date);
			}
			else {
				ddlRecordSetVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddlRecordSetVersion)) {
				if (!isNew) {
					session.evict(
						DDLRecordSetVersionImpl.class,
						ddlRecordSetVersion.getPrimaryKeyObj());
				}

				session.save(ddlRecordSetVersion);
			}
			else {
				ddlRecordSetVersion = (DDLRecordSetVersion)session.merge(
					ddlRecordSetVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddlRecordSetVersion, false);

		if (isNew) {
			ddlRecordSetVersion.setNew(false);
		}

		ddlRecordSetVersion.resetOriginalValues();

		return ddlRecordSetVersion;
	}

	/**
	 * Returns the ddl record set version with the primary key or throws a <code>NoSuchRecordSetVersionException</code> if it could not be found.
	 *
	 * @param recordSetVersionId the primary key of the ddl record set version
	 * @return the ddl record set version
	 * @throws NoSuchRecordSetVersionException if a ddl record set version with the primary key could not be found
	 */
	@Override
	public DDLRecordSetVersion findByPrimaryKey(long recordSetVersionId)
		throws NoSuchRecordSetVersionException {

		return findByPrimaryKey((Serializable)recordSetVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddl record set version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recordSetVersionId the primary key of the ddl record set version
	 * @return the ddl record set version, or <code>null</code> if a ddl record set version with the primary key could not be found
	 */
	@Override
	public DDLRecordSetVersion fetchByPrimaryKey(long recordSetVersionId) {
		return fetchByPrimaryKey((Serializable)recordSetVersionId);
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
		return "recordSetVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDLRECORDSETVERSION;
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
		return DDLRecordSetVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDLRecordSetVersion";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("recordSetId");
		ctMergeColumnNames.add("DDMStructureVersionId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("settings_");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("recordSetVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"recordSetId", "version"});
	}

	/**
	 * Initializes the ddl record set version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByRecordSetId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRecordSetId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"recordSetId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByRecordSetId", new String[] {Long.class.getName()},
					new String[] {"recordSetId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRecordSetId", new String[] {Long.class.getName()},
					new String[] {"recordSetId"}, false),
				_SQL_SELECT_DDLRECORDSETVERSION_WHERE,
				_SQL_COUNT_DDLRECORDSETVERSION_WHERE,
				DDLRecordSetVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ddlRecordSetVersion.", "recordSetId",
					FinderColumn.Type.LONG, "=", true, true,
					DDLRecordSetVersion::getRecordSetId));

		_uniquePersistenceFinderByRS_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByRS_V",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"recordSetId", "version"}, 0, 2, false,
				DDLRecordSetVersion::getRecordSetId,
				convertNullFunction(DDLRecordSetVersion::getVersion)),
			_SQL_SELECT_DDLRECORDSETVERSION_WHERE, "",
			new FinderColumn<>(
				"ddlRecordSetVersion.", "recordSetId", FinderColumn.Type.LONG,
				"=", true, true, DDLRecordSetVersion::getRecordSetId),
			new FinderColumn<>(
				"ddlRecordSetVersion.", "version", FinderColumn.Type.STRING,
				"=", true, true, DDLRecordSetVersion::getVersion));

		_collectionPersistenceFinderByRS_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRS_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"recordSetId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRS_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"recordSetId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRS_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"recordSetId", "status"}, false),
			_SQL_SELECT_DDLRECORDSETVERSION_WHERE,
			_SQL_COUNT_DDLRECORDSETVERSION_WHERE,
			DDLRecordSetVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"ddlRecordSetVersion.", "recordSetId", FinderColumn.Type.LONG,
				"=", true, true, DDLRecordSetVersion::getRecordSetId),
			new FinderColumn<>(
				"ddlRecordSetVersion.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, DDLRecordSetVersion::getStatus));

		DDLRecordSetVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDLRecordSetVersionUtil.setPersistence(null);

		entityCache.removeCache(DDLRecordSetVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DDLRecordSetVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDLRECORDSETVERSION =
		"SELECT ddlRecordSetVersion FROM DDLRecordSetVersion ddlRecordSetVersion";

	private static final String _SQL_SELECT_DDLRECORDSETVERSION_WHERE =
		"SELECT ddlRecordSetVersion FROM DDLRecordSetVersion ddlRecordSetVersion WHERE ";

	private static final String _SQL_COUNT_DDLRECORDSETVERSION_WHERE =
		"SELECT COUNT(ddlRecordSetVersion) FROM DDLRecordSetVersion ddlRecordSetVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDLRecordSetVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDLRecordSetVersionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"settings"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1424134741