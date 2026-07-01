/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceVersionException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersionTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceVersionImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceVersionModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceVersionPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceVersionUtil;
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
 * The persistence implementation for the ddm form instance version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMFormInstanceVersionPersistence.class)
public class DDMFormInstanceVersionPersistenceImpl
	extends BasePersistenceImpl
		<DDMFormInstanceVersion, NoSuchFormInstanceVersionException>
	implements DDMFormInstanceVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMFormInstanceVersionUtil</code> to access the ddm form instance version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMFormInstanceVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMFormInstanceVersion, NoSuchFormInstanceVersionException>
			_collectionPersistenceFinderByFormInstanceId;

	/**
	 * Returns an ordered range of all the ddm form instance versions where formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance versions
	 * @param end the upper bound of the range of ddm form instance versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance versions
	 */
	@Override
	public List<DDMFormInstanceVersion> findByFormInstanceId(
		long formInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFormInstanceId.find(
			finderCache, new Object[] {formInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance version in the ordered set where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance version
	 * @throws NoSuchFormInstanceVersionException if a matching ddm form instance version could not be found
	 */
	@Override
	public DDMFormInstanceVersion findByFormInstanceId_First(
			long formInstanceId,
			OrderByComparator<DDMFormInstanceVersion> orderByComparator)
		throws NoSuchFormInstanceVersionException {

		return _collectionPersistenceFinderByFormInstanceId.findFirst(
			finderCache, new Object[] {formInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance version in the ordered set where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance version, or <code>null</code> if a matching ddm form instance version could not be found
	 */
	@Override
	public DDMFormInstanceVersion fetchByFormInstanceId_First(
		long formInstanceId,
		OrderByComparator<DDMFormInstanceVersion> orderByComparator) {

		return _collectionPersistenceFinderByFormInstanceId.fetchFirst(
			finderCache, new Object[] {formInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance versions where formInstanceId = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 */
	@Override
	public void removeByFormInstanceId(long formInstanceId) {
		_collectionPersistenceFinderByFormInstanceId.remove(
			finderCache, new Object[] {formInstanceId});
	}

	/**
	 * Returns the number of ddm form instance versions where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance versions
	 */
	@Override
	public int countByFormInstanceId(long formInstanceId) {
		return _collectionPersistenceFinderByFormInstanceId.count(
			finderCache, new Object[] {formInstanceId});
	}

	private UniquePersistenceFinder
		<DDMFormInstanceVersion, NoSuchFormInstanceVersionException>
			_uniquePersistenceFinderByF_V;

	/**
	 * Returns the ddm form instance version where formInstanceId = &#63; and version = &#63; or throws a <code>NoSuchFormInstanceVersionException</code> if it could not be found.
	 *
	 * @param formInstanceId the form instance ID
	 * @param version the version
	 * @return the matching ddm form instance version
	 * @throws NoSuchFormInstanceVersionException if a matching ddm form instance version could not be found
	 */
	@Override
	public DDMFormInstanceVersion findByF_V(long formInstanceId, String version)
		throws NoSuchFormInstanceVersionException {

		return _uniquePersistenceFinderByF_V.find(
			finderCache, new Object[] {formInstanceId, version});
	}

	/**
	 * Returns the ddm form instance version where formInstanceId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param formInstanceId the form instance ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance version, or <code>null</code> if a matching ddm form instance version could not be found
	 */
	@Override
	public DDMFormInstanceVersion fetchByF_V(
		long formInstanceId, String version, boolean useFinderCache) {

		return _uniquePersistenceFinderByF_V.fetch(
			finderCache, new Object[] {formInstanceId, version},
			useFinderCache);
	}

	/**
	 * Removes the ddm form instance version where formInstanceId = &#63; and version = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 * @param version the version
	 * @return the ddm form instance version that was removed
	 */
	@Override
	public DDMFormInstanceVersion removeByF_V(
			long formInstanceId, String version)
		throws NoSuchFormInstanceVersionException {

		DDMFormInstanceVersion ddmFormInstanceVersion = findByF_V(
			formInstanceId, version);

		return remove(ddmFormInstanceVersion);
	}

	/**
	 * Returns the number of ddm form instance versions where formInstanceId = &#63; and version = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param version the version
	 * @return the number of matching ddm form instance versions
	 */
	@Override
	public int countByF_V(long formInstanceId, String version) {
		return _uniquePersistenceFinderByF_V.count(
			finderCache, new Object[] {formInstanceId, version});
	}

	private CollectionPersistenceFinder
		<DDMFormInstanceVersion, NoSuchFormInstanceVersionException>
			_collectionPersistenceFinderByF_S;

	/**
	 * Returns an ordered range of all the ddm form instance versions where formInstanceId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance versions
	 * @param end the upper bound of the range of ddm form instance versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance versions
	 */
	@Override
	public List<DDMFormInstanceVersion> findByF_S(
		long formInstanceId, int status, int start, int end,
		OrderByComparator<DDMFormInstanceVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByF_S.find(
			finderCache, new Object[] {formInstanceId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance version in the ordered set where formInstanceId = &#63; and status = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance version
	 * @throws NoSuchFormInstanceVersionException if a matching ddm form instance version could not be found
	 */
	@Override
	public DDMFormInstanceVersion findByF_S_First(
			long formInstanceId, int status,
			OrderByComparator<DDMFormInstanceVersion> orderByComparator)
		throws NoSuchFormInstanceVersionException {

		return _collectionPersistenceFinderByF_S.findFirst(
			finderCache, new Object[] {formInstanceId, status},
			orderByComparator);
	}

	/**
	 * Returns the first ddm form instance version in the ordered set where formInstanceId = &#63; and status = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance version, or <code>null</code> if a matching ddm form instance version could not be found
	 */
	@Override
	public DDMFormInstanceVersion fetchByF_S_First(
		long formInstanceId, int status,
		OrderByComparator<DDMFormInstanceVersion> orderByComparator) {

		return _collectionPersistenceFinderByF_S.fetchFirst(
			finderCache, new Object[] {formInstanceId, status},
			orderByComparator);
	}

	/**
	 * Removes all the ddm form instance versions where formInstanceId = &#63; and status = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 * @param status the status
	 */
	@Override
	public void removeByF_S(long formInstanceId, int status) {
		_collectionPersistenceFinderByF_S.remove(
			finderCache, new Object[] {formInstanceId, status});
	}

	/**
	 * Returns the number of ddm form instance versions where formInstanceId = &#63; and status = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param status the status
	 * @return the number of matching ddm form instance versions
	 */
	@Override
	public int countByF_S(long formInstanceId, int status) {
		return _collectionPersistenceFinderByF_S.count(
			finderCache, new Object[] {formInstanceId, status});
	}

	public DDMFormInstanceVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMFormInstanceVersion.class);

		setModelImplClass(DDMFormInstanceVersionImpl.class);
		setModelPKClass(long.class);

		setTable(DDMFormInstanceVersionTable.INSTANCE);
	}

	/**
	 * Creates a new ddm form instance version with the primary key. Does not add the ddm form instance version to the database.
	 *
	 * @param formInstanceVersionId the primary key for the new ddm form instance version
	 * @return the new ddm form instance version
	 */
	@Override
	public DDMFormInstanceVersion create(long formInstanceVersionId) {
		DDMFormInstanceVersion ddmFormInstanceVersion =
			new DDMFormInstanceVersionImpl();

		ddmFormInstanceVersion.setNew(true);
		ddmFormInstanceVersion.setPrimaryKey(formInstanceVersionId);

		ddmFormInstanceVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmFormInstanceVersion;
	}

	/**
	 * Removes the ddm form instance version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceVersionId the primary key of the ddm form instance version
	 * @return the ddm form instance version that was removed
	 * @throws NoSuchFormInstanceVersionException if a ddm form instance version with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceVersion remove(long formInstanceVersionId)
		throws NoSuchFormInstanceVersionException {

		return remove((Serializable)formInstanceVersionId);
	}

	@Override
	protected DDMFormInstanceVersion removeImpl(
		DDMFormInstanceVersion ddmFormInstanceVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmFormInstanceVersion)) {
				ddmFormInstanceVersion = (DDMFormInstanceVersion)session.get(
					DDMFormInstanceVersionImpl.class,
					ddmFormInstanceVersion.getPrimaryKeyObj());
			}

			if ((ddmFormInstanceVersion != null) &&
				ctPersistenceHelper.isRemove(ddmFormInstanceVersion)) {

				session.delete(ddmFormInstanceVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmFormInstanceVersion != null) {
			clearCache(ddmFormInstanceVersion);
		}

		return ddmFormInstanceVersion;
	}

	@Override
	public DDMFormInstanceVersion updateImpl(
		DDMFormInstanceVersion ddmFormInstanceVersion) {

		boolean isNew = ddmFormInstanceVersion.isNew();

		if (!(ddmFormInstanceVersion instanceof
				DDMFormInstanceVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmFormInstanceVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmFormInstanceVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmFormInstanceVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMFormInstanceVersion implementation " +
					ddmFormInstanceVersion.getClass());
		}

		DDMFormInstanceVersionModelImpl ddmFormInstanceVersionModelImpl =
			(DDMFormInstanceVersionModelImpl)ddmFormInstanceVersion;

		if (isNew && (ddmFormInstanceVersion.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				ddmFormInstanceVersion.setCreateDate(date);
			}
			else {
				ddmFormInstanceVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmFormInstanceVersion)) {
				if (!isNew) {
					session.evict(
						DDMFormInstanceVersionImpl.class,
						ddmFormInstanceVersion.getPrimaryKeyObj());
				}

				session.save(ddmFormInstanceVersion);
			}
			else {
				ddmFormInstanceVersion = (DDMFormInstanceVersion)session.merge(
					ddmFormInstanceVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmFormInstanceVersion, false);

		if (isNew) {
			ddmFormInstanceVersion.setNew(false);
		}

		ddmFormInstanceVersion.resetOriginalValues();

		return ddmFormInstanceVersion;
	}

	/**
	 * Returns the ddm form instance version with the primary key or throws a <code>NoSuchFormInstanceVersionException</code> if it could not be found.
	 *
	 * @param formInstanceVersionId the primary key of the ddm form instance version
	 * @return the ddm form instance version
	 * @throws NoSuchFormInstanceVersionException if a ddm form instance version with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceVersion findByPrimaryKey(long formInstanceVersionId)
		throws NoSuchFormInstanceVersionException {

		return findByPrimaryKey((Serializable)formInstanceVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm form instance version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceVersionId the primary key of the ddm form instance version
	 * @return the ddm form instance version, or <code>null</code> if a ddm form instance version with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceVersion fetchByPrimaryKey(
		long formInstanceVersionId) {

		return fetchByPrimaryKey((Serializable)formInstanceVersionId);
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
		return "formInstanceVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMFORMINSTANCEVERSION;
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
		return DDMFormInstanceVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMFormInstanceVersion";
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
		ctMergeColumnNames.add("formInstanceId");
		ctMergeColumnNames.add("structureVersionId");
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
			Collections.singleton("formInstanceVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"formInstanceId", "version"});
	}

	/**
	 * Initializes the ddm form instance version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByFormInstanceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFormInstanceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"formInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFormInstanceId", new String[] {Long.class.getName()},
					new String[] {"formInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFormInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"formInstanceId"}, false),
				_SQL_SELECT_DDMFORMINSTANCEVERSION_WHERE,
				_SQL_COUNT_DDMFORMINSTANCEVERSION_WHERE,
				DDMFormInstanceVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ddmFormInstanceVersion.", "formInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMFormInstanceVersion::getFormInstanceId));

		_uniquePersistenceFinderByF_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByF_V",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"formInstanceId", "version"}, 0, 2, false,
				DDMFormInstanceVersion::getFormInstanceId,
				convertNullFunction(DDMFormInstanceVersion::getVersion)),
			_SQL_SELECT_DDMFORMINSTANCEVERSION_WHERE, "",
			new FinderColumn<>(
				"ddmFormInstanceVersion.", "formInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMFormInstanceVersion::getFormInstanceId),
			new FinderColumn<>(
				"ddmFormInstanceVersion.", "version", FinderColumn.Type.STRING,
				"=", true, true, DDMFormInstanceVersion::getVersion));

		_collectionPersistenceFinderByF_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByF_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"formInstanceId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByF_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"formInstanceId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByF_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"formInstanceId", "status"}, false),
			_SQL_SELECT_DDMFORMINSTANCEVERSION_WHERE,
			_SQL_COUNT_DDMFORMINSTANCEVERSION_WHERE,
			DDMFormInstanceVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"ddmFormInstanceVersion.", "formInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMFormInstanceVersion::getFormInstanceId),
			new FinderColumn<>(
				"ddmFormInstanceVersion.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, DDMFormInstanceVersion::getStatus));

		DDMFormInstanceVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMFormInstanceVersionUtil.setPersistence(null);

		entityCache.removeCache(DDMFormInstanceVersionImpl.class.getName());
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
		DDMFormInstanceVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMFORMINSTANCEVERSION =
		"SELECT ddmFormInstanceVersion FROM DDMFormInstanceVersion ddmFormInstanceVersion";

	private static final String _SQL_SELECT_DDMFORMINSTANCEVERSION_WHERE =
		"SELECT ddmFormInstanceVersion FROM DDMFormInstanceVersion ddmFormInstanceVersion WHERE ";

	private static final String _SQL_COUNT_DDMFORMINSTANCEVERSION_WHERE =
		"SELECT COUNT(ddmFormInstanceVersion) FROM DDMFormInstanceVersion ddmFormInstanceVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMFormInstanceVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceVersionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"settings"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-936038170