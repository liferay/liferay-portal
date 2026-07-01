/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchStructureVersionException;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersionTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureVersionPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureVersionUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
 * The persistence implementation for the ddm structure version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMStructureVersionPersistence.class)
public class DDMStructureVersionPersistenceImpl
	extends BasePersistenceImpl
		<DDMStructureVersion, NoSuchStructureVersionException>
	implements DDMStructureVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMStructureVersionUtil</code> to access the ddm structure version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMStructureVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMStructureVersion, NoSuchStructureVersionException>
			_collectionPersistenceFinderByStructureId;

	/**
	 * Returns an ordered range of all the ddm structure versions where structureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureVersionModelImpl</code>.
	 * </p>
	 *
	 * @param structureId the structure ID
	 * @param start the lower bound of the range of ddm structure versions
	 * @param end the upper bound of the range of ddm structure versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure versions
	 */
	@Override
	public List<DDMStructureVersion> findByStructureId(
		long structureId, int start, int end,
		OrderByComparator<DDMStructureVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureId.find(
			finderCache, new Object[] {structureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure version in the ordered set where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure version
	 * @throws NoSuchStructureVersionException if a matching ddm structure version could not be found
	 */
	@Override
	public DDMStructureVersion findByStructureId_First(
			long structureId,
			OrderByComparator<DDMStructureVersion> orderByComparator)
		throws NoSuchStructureVersionException {

		return _collectionPersistenceFinderByStructureId.findFirst(
			finderCache, new Object[] {structureId}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure version in the ordered set where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure version, or <code>null</code> if a matching ddm structure version could not be found
	 */
	@Override
	public DDMStructureVersion fetchByStructureId_First(
		long structureId,
		OrderByComparator<DDMStructureVersion> orderByComparator) {

		return _collectionPersistenceFinderByStructureId.fetchFirst(
			finderCache, new Object[] {structureId}, orderByComparator);
	}

	/**
	 * Removes all the ddm structure versions where structureId = &#63; from the database.
	 *
	 * @param structureId the structure ID
	 */
	@Override
	public void removeByStructureId(long structureId) {
		_collectionPersistenceFinderByStructureId.remove(
			finderCache, new Object[] {structureId});
	}

	/**
	 * Returns the number of ddm structure versions where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @return the number of matching ddm structure versions
	 */
	@Override
	public int countByStructureId(long structureId) {
		return _collectionPersistenceFinderByStructureId.count(
			finderCache, new Object[] {structureId});
	}

	private UniquePersistenceFinder
		<DDMStructureVersion, NoSuchStructureVersionException>
			_uniquePersistenceFinderByS_V;

	/**
	 * Returns the ddm structure version where structureId = &#63; and version = &#63; or throws a <code>NoSuchStructureVersionException</code> if it could not be found.
	 *
	 * @param structureId the structure ID
	 * @param version the version
	 * @return the matching ddm structure version
	 * @throws NoSuchStructureVersionException if a matching ddm structure version could not be found
	 */
	@Override
	public DDMStructureVersion findByS_V(long structureId, String version)
		throws NoSuchStructureVersionException {

		return _uniquePersistenceFinderByS_V.find(
			finderCache, new Object[] {structureId, version});
	}

	/**
	 * Returns the ddm structure version where structureId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param structureId the structure ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure version, or <code>null</code> if a matching ddm structure version could not be found
	 */
	@Override
	public DDMStructureVersion fetchByS_V(
		long structureId, String version, boolean useFinderCache) {

		return _uniquePersistenceFinderByS_V.fetch(
			finderCache, new Object[] {structureId, version}, useFinderCache);
	}

	/**
	 * Removes the ddm structure version where structureId = &#63; and version = &#63; from the database.
	 *
	 * @param structureId the structure ID
	 * @param version the version
	 * @return the ddm structure version that was removed
	 */
	@Override
	public DDMStructureVersion removeByS_V(long structureId, String version)
		throws NoSuchStructureVersionException {

		DDMStructureVersion ddmStructureVersion = findByS_V(
			structureId, version);

		return remove(ddmStructureVersion);
	}

	/**
	 * Returns the number of ddm structure versions where structureId = &#63; and version = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param version the version
	 * @return the number of matching ddm structure versions
	 */
	@Override
	public int countByS_V(long structureId, String version) {
		return _uniquePersistenceFinderByS_V.count(
			finderCache, new Object[] {structureId, version});
	}

	private CollectionPersistenceFinder
		<DDMStructureVersion, NoSuchStructureVersionException>
			_collectionPersistenceFinderByS_S;

	/**
	 * Returns an ordered range of all the ddm structure versions where structureId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureVersionModelImpl</code>.
	 * </p>
	 *
	 * @param structureId the structure ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm structure versions
	 * @param end the upper bound of the range of ddm structure versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structure versions
	 */
	@Override
	public List<DDMStructureVersion> findByS_S(
		long structureId, int status, int start, int end,
		OrderByComparator<DDMStructureVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_S.find(
			finderCache, new Object[] {structureId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure version in the ordered set where structureId = &#63; and status = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure version
	 * @throws NoSuchStructureVersionException if a matching ddm structure version could not be found
	 */
	@Override
	public DDMStructureVersion findByS_S_First(
			long structureId, int status,
			OrderByComparator<DDMStructureVersion> orderByComparator)
		throws NoSuchStructureVersionException {

		return _collectionPersistenceFinderByS_S.findFirst(
			finderCache, new Object[] {structureId, status}, orderByComparator);
	}

	/**
	 * Returns the first ddm structure version in the ordered set where structureId = &#63; and status = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure version, or <code>null</code> if a matching ddm structure version could not be found
	 */
	@Override
	public DDMStructureVersion fetchByS_S_First(
		long structureId, int status,
		OrderByComparator<DDMStructureVersion> orderByComparator) {

		return _collectionPersistenceFinderByS_S.fetchFirst(
			finderCache, new Object[] {structureId, status}, orderByComparator);
	}

	/**
	 * Removes all the ddm structure versions where structureId = &#63; and status = &#63; from the database.
	 *
	 * @param structureId the structure ID
	 * @param status the status
	 */
	@Override
	public void removeByS_S(long structureId, int status) {
		_collectionPersistenceFinderByS_S.remove(
			finderCache, new Object[] {structureId, status});
	}

	/**
	 * Returns the number of ddm structure versions where structureId = &#63; and status = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param status the status
	 * @return the number of matching ddm structure versions
	 */
	@Override
	public int countByS_S(long structureId, int status) {
		return _collectionPersistenceFinderByS_S.count(
			finderCache, new Object[] {structureId, status});
	}

	public DDMStructureVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMStructureVersion.class);

		setModelImplClass(DDMStructureVersionImpl.class);
		setModelPKClass(long.class);

		setTable(DDMStructureVersionTable.INSTANCE);
	}

	/**
	 * Creates a new ddm structure version with the primary key. Does not add the ddm structure version to the database.
	 *
	 * @param structureVersionId the primary key for the new ddm structure version
	 * @return the new ddm structure version
	 */
	@Override
	public DDMStructureVersion create(long structureVersionId) {
		DDMStructureVersion ddmStructureVersion = new DDMStructureVersionImpl();

		ddmStructureVersion.setNew(true);
		ddmStructureVersion.setPrimaryKey(structureVersionId);

		ddmStructureVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmStructureVersion;
	}

	/**
	 * Removes the ddm structure version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param structureVersionId the primary key of the ddm structure version
	 * @return the ddm structure version that was removed
	 * @throws NoSuchStructureVersionException if a ddm structure version with the primary key could not be found
	 */
	@Override
	public DDMStructureVersion remove(long structureVersionId)
		throws NoSuchStructureVersionException {

		return remove((Serializable)structureVersionId);
	}

	@Override
	protected DDMStructureVersion removeImpl(
		DDMStructureVersion ddmStructureVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmStructureVersion)) {
				ddmStructureVersion = (DDMStructureVersion)session.get(
					DDMStructureVersionImpl.class,
					ddmStructureVersion.getPrimaryKeyObj());
			}

			if ((ddmStructureVersion != null) &&
				ctPersistenceHelper.isRemove(ddmStructureVersion)) {

				session.delete(ddmStructureVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmStructureVersion != null) {
			clearCache(ddmStructureVersion);
		}

		return ddmStructureVersion;
	}

	@Override
	public DDMStructureVersion updateImpl(
		DDMStructureVersion ddmStructureVersion) {

		boolean isNew = ddmStructureVersion.isNew();

		if (!(ddmStructureVersion instanceof DDMStructureVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmStructureVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmStructureVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmStructureVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMStructureVersion implementation " +
					ddmStructureVersion.getClass());
		}

		DDMStructureVersionModelImpl ddmStructureVersionModelImpl =
			(DDMStructureVersionModelImpl)ddmStructureVersion;

		if (isNew && (ddmStructureVersion.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				ddmStructureVersion.setCreateDate(date);
			}
			else {
				ddmStructureVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmStructureVersion)) {
				if (!isNew) {
					session.evict(
						DDMStructureVersionImpl.class,
						ddmStructureVersion.getPrimaryKeyObj());
				}

				session.save(ddmStructureVersion);
			}
			else {
				ddmStructureVersion = (DDMStructureVersion)session.merge(
					ddmStructureVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmStructureVersion, false);

		if (isNew) {
			ddmStructureVersion.setNew(false);
		}

		ddmStructureVersion.resetOriginalValues();

		return ddmStructureVersion;
	}

	/**
	 * Returns the ddm structure version with the primary key or throws a <code>NoSuchStructureVersionException</code> if it could not be found.
	 *
	 * @param structureVersionId the primary key of the ddm structure version
	 * @return the ddm structure version
	 * @throws NoSuchStructureVersionException if a ddm structure version with the primary key could not be found
	 */
	@Override
	public DDMStructureVersion findByPrimaryKey(long structureVersionId)
		throws NoSuchStructureVersionException {

		return findByPrimaryKey((Serializable)structureVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm structure version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param structureVersionId the primary key of the ddm structure version
	 * @return the ddm structure version, or <code>null</code> if a ddm structure version with the primary key could not be found
	 */
	@Override
	public DDMStructureVersion fetchByPrimaryKey(long structureVersionId) {
		return fetchByPrimaryKey((Serializable)structureVersionId);
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
		return "structureVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMSTRUCTUREVERSION;
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
		return DDMStructureVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMStructureVersion";
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
		ctMergeColumnNames.add("structureId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("parentStructureId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("definition");
		ctMergeColumnNames.add("storageType");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("structureVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"structureId", "version"});
	}

	/**
	 * Initializes the ddm structure version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByStructureId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByStructureId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"structureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStructureId", new String[] {Long.class.getName()},
					new String[] {"structureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStructureId", new String[] {Long.class.getName()},
					new String[] {"structureId"}, false),
				_SQL_SELECT_DDMSTRUCTUREVERSION_WHERE,
				_SQL_COUNT_DDMSTRUCTUREVERSION_WHERE,
				DDMStructureVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ddmStructureVersion.", "structureId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMStructureVersion::getStructureId));

		_uniquePersistenceFinderByS_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByS_V",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"structureId", "version"}, 0, 2, false,
				DDMStructureVersion::getStructureId,
				convertNullFunction(DDMStructureVersion::getVersion)),
			_SQL_SELECT_DDMSTRUCTUREVERSION_WHERE, "",
			new FinderColumn<>(
				"ddmStructureVersion.", "structureId", FinderColumn.Type.LONG,
				"=", true, true, DDMStructureVersion::getStructureId),
			new FinderColumn<>(
				"ddmStructureVersion.", "version", FinderColumn.Type.STRING,
				"=", true, true, DDMStructureVersion::getVersion));

		_collectionPersistenceFinderByS_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"structureId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"structureId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"structureId", "status"}, false),
			_SQL_SELECT_DDMSTRUCTUREVERSION_WHERE,
			_SQL_COUNT_DDMSTRUCTUREVERSION_WHERE,
			DDMStructureVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"ddmStructureVersion.", "structureId", FinderColumn.Type.LONG,
				"=", true, true, DDMStructureVersion::getStructureId),
			new FinderColumn<>(
				"ddmStructureVersion.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, DDMStructureVersion::getStatus));

		DDMStructureVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMStructureVersionUtil.setPersistence(null);

		entityCache.removeCache(DDMStructureVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DDMStructureVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMSTRUCTUREVERSION =
		"SELECT ddmStructureVersion FROM DDMStructureVersion ddmStructureVersion";

	private static final String _SQL_SELECT_DDMSTRUCTUREVERSION_WHERE =
		"SELECT ddmStructureVersion FROM DDMStructureVersion ddmStructureVersion WHERE ";

	private static final String _SQL_COUNT_DDMSTRUCTUREVERSION_WHERE =
		"SELECT COUNT(ddmStructureVersion) FROM DDMStructureVersion ddmStructureVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMStructureVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMStructureVersionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-717000608