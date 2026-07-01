/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.impl;

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
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionVersionException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersionTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionVersionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionVersionUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
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
 * The persistence implementation for the kaleo definition version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoDefinitionVersionPersistence.class)
public class KaleoDefinitionVersionPersistenceImpl
	extends BasePersistenceImpl
		<KaleoDefinitionVersion, NoSuchDefinitionVersionException>
	implements KaleoDefinitionVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoDefinitionVersionUtil</code> to access the kaleo definition version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoDefinitionVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoDefinitionVersion, NoSuchDefinitionVersionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo definition versions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo definition versions
	 * @param end the upper bound of the range of kaleo definition versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definition versions
	 */
	@Override
	public List<KaleoDefinitionVersion> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoDefinitionVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo definition version in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition version
	 * @throws NoSuchDefinitionVersionException if a matching kaleo definition version could not be found
	 */
	@Override
	public KaleoDefinitionVersion findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoDefinitionVersion> orderByComparator)
		throws NoSuchDefinitionVersionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo definition version in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition version, or <code>null</code> if a matching kaleo definition version could not be found
	 */
	@Override
	public KaleoDefinitionVersion fetchByCompanyId_First(
		long companyId,
		OrderByComparator<KaleoDefinitionVersion> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo definition versions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo definition versions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo definition versions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoDefinitionVersion, NoSuchDefinitionVersionException>
			_collectionPersistenceFinderByC_N;

	/**
	 * Returns an ordered range of all the kaleo definition versions where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of kaleo definition versions
	 * @param end the upper bound of the range of kaleo definition versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definition versions
	 */
	@Override
	public List<KaleoDefinitionVersion> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<KaleoDefinitionVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo definition version in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition version
	 * @throws NoSuchDefinitionVersionException if a matching kaleo definition version could not be found
	 */
	@Override
	public KaleoDefinitionVersion findByC_N_First(
			long companyId, String name,
			OrderByComparator<KaleoDefinitionVersion> orderByComparator)
		throws NoSuchDefinitionVersionException {

		return _collectionPersistenceFinderByC_N.findFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Returns the first kaleo definition version in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition version, or <code>null</code> if a matching kaleo definition version could not be found
	 */
	@Override
	public KaleoDefinitionVersion fetchByC_N_First(
		long companyId, String name,
		OrderByComparator<KaleoDefinitionVersion> orderByComparator) {

		return _collectionPersistenceFinderByC_N.fetchFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Removes all the kaleo definition versions where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_N(long companyId, String name) {
		_collectionPersistenceFinderByC_N.remove(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the number of kaleo definition versions where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching kaleo definition versions
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _collectionPersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	private UniquePersistenceFinder
		<KaleoDefinitionVersion, NoSuchDefinitionVersionException>
			_uniquePersistenceFinderByC_N_V;

	/**
	 * Returns the kaleo definition version where companyId = &#63; and name = &#63; and version = &#63; or throws a <code>NoSuchDefinitionVersionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param version the version
	 * @return the matching kaleo definition version
	 * @throws NoSuchDefinitionVersionException if a matching kaleo definition version could not be found
	 */
	@Override
	public KaleoDefinitionVersion findByC_N_V(
			long companyId, String name, String version)
		throws NoSuchDefinitionVersionException {

		return _uniquePersistenceFinderByC_N_V.find(
			finderCache, new Object[] {companyId, name, version});
	}

	/**
	 * Returns the kaleo definition version where companyId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo definition version, or <code>null</code> if a matching kaleo definition version could not be found
	 */
	@Override
	public KaleoDefinitionVersion fetchByC_N_V(
		long companyId, String name, String version, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N_V.fetch(
			finderCache, new Object[] {companyId, name, version},
			useFinderCache);
	}

	/**
	 * Removes the kaleo definition version where companyId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param version the version
	 * @return the kaleo definition version that was removed
	 */
	@Override
	public KaleoDefinitionVersion removeByC_N_V(
			long companyId, String name, String version)
		throws NoSuchDefinitionVersionException {

		KaleoDefinitionVersion kaleoDefinitionVersion = findByC_N_V(
			companyId, name, version);

		return remove(kaleoDefinitionVersion);
	}

	/**
	 * Returns the number of kaleo definition versions where companyId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching kaleo definition versions
	 */
	@Override
	public int countByC_N_V(long companyId, String name, String version) {
		return _uniquePersistenceFinderByC_N_V.count(
			finderCache, new Object[] {companyId, name, version});
	}

	public KaleoDefinitionVersionPersistenceImpl() {
		setModelClass(KaleoDefinitionVersion.class);

		setModelImplClass(KaleoDefinitionVersionImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoDefinitionVersionTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo definition version with the primary key. Does not add the kaleo definition version to the database.
	 *
	 * @param kaleoDefinitionVersionId the primary key for the new kaleo definition version
	 * @return the new kaleo definition version
	 */
	@Override
	public KaleoDefinitionVersion create(long kaleoDefinitionVersionId) {
		KaleoDefinitionVersion kaleoDefinitionVersion =
			new KaleoDefinitionVersionImpl();

		kaleoDefinitionVersion.setNew(true);
		kaleoDefinitionVersion.setPrimaryKey(kaleoDefinitionVersionId);

		kaleoDefinitionVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoDefinitionVersion;
	}

	/**
	 * Removes the kaleo definition version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoDefinitionVersionId the primary key of the kaleo definition version
	 * @return the kaleo definition version that was removed
	 * @throws NoSuchDefinitionVersionException if a kaleo definition version with the primary key could not be found
	 */
	@Override
	public KaleoDefinitionVersion remove(long kaleoDefinitionVersionId)
		throws NoSuchDefinitionVersionException {

		return remove((Serializable)kaleoDefinitionVersionId);
	}

	@Override
	protected KaleoDefinitionVersion removeImpl(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoDefinitionVersion)) {
				kaleoDefinitionVersion = (KaleoDefinitionVersion)session.get(
					KaleoDefinitionVersionImpl.class,
					kaleoDefinitionVersion.getPrimaryKeyObj());
			}

			if ((kaleoDefinitionVersion != null) &&
				ctPersistenceHelper.isRemove(kaleoDefinitionVersion)) {

				session.delete(kaleoDefinitionVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoDefinitionVersion != null) {
			clearCache(kaleoDefinitionVersion);
		}

		return kaleoDefinitionVersion;
	}

	@Override
	public KaleoDefinitionVersion updateImpl(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		boolean isNew = kaleoDefinitionVersion.isNew();

		if (!(kaleoDefinitionVersion instanceof
				KaleoDefinitionVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoDefinitionVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoDefinitionVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoDefinitionVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoDefinitionVersion implementation " +
					kaleoDefinitionVersion.getClass());
		}

		KaleoDefinitionVersionModelImpl kaleoDefinitionVersionModelImpl =
			(KaleoDefinitionVersionModelImpl)kaleoDefinitionVersion;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoDefinitionVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoDefinitionVersion.setCreateDate(date);
			}
			else {
				kaleoDefinitionVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoDefinitionVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoDefinitionVersion.setModifiedDate(date);
			}
			else {
				kaleoDefinitionVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoDefinitionVersion)) {
				if (!isNew) {
					session.evict(
						KaleoDefinitionVersionImpl.class,
						kaleoDefinitionVersion.getPrimaryKeyObj());
				}

				session.save(kaleoDefinitionVersion);
			}
			else {
				kaleoDefinitionVersion = (KaleoDefinitionVersion)session.merge(
					kaleoDefinitionVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoDefinitionVersion, false);

		if (isNew) {
			kaleoDefinitionVersion.setNew(false);
		}

		kaleoDefinitionVersion.resetOriginalValues();

		return kaleoDefinitionVersion;
	}

	/**
	 * Returns the kaleo definition version with the primary key or throws a <code>NoSuchDefinitionVersionException</code> if it could not be found.
	 *
	 * @param kaleoDefinitionVersionId the primary key of the kaleo definition version
	 * @return the kaleo definition version
	 * @throws NoSuchDefinitionVersionException if a kaleo definition version with the primary key could not be found
	 */
	@Override
	public KaleoDefinitionVersion findByPrimaryKey(
			long kaleoDefinitionVersionId)
		throws NoSuchDefinitionVersionException {

		return findByPrimaryKey((Serializable)kaleoDefinitionVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo definition version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoDefinitionVersionId the primary key of the kaleo definition version
	 * @return the kaleo definition version, or <code>null</code> if a kaleo definition version with the primary key could not be found
	 */
	@Override
	public KaleoDefinitionVersion fetchByPrimaryKey(
		long kaleoDefinitionVersionId) {

		return fetchByPrimaryKey((Serializable)kaleoDefinitionVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoDefinitionVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEODEFINITIONVERSION;
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
		return KaleoDefinitionVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoDefinitionVersion";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("kaleoDefinitionId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("startKaleoNodeId");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoDefinitionVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "name", "version"});
	}

	/**
	 * Initializes the kaleo definition version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_KALEODEFINITIONVERSION_WHERE,
				_SQL_COUNT_KALEODEFINITIONVERSION_WHERE,
				KaleoDefinitionVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"kaleoDefinitionVersion.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoDefinitionVersion::getCompanyId));

		_collectionPersistenceFinderByC_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false, null),
			_SQL_SELECT_KALEODEFINITIONVERSION_WHERE,
			_SQL_COUNT_KALEODEFINITIONVERSION_WHERE,
			KaleoDefinitionVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"kaleoDefinitionVersion.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, KaleoDefinitionVersion::getCompanyId),
			new FinderColumn<>(
				"kaleoDefinitionVersion.", "name", FinderColumn.Type.STRING,
				"=", true, true, KaleoDefinitionVersion::getName));

		_uniquePersistenceFinderByC_N_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "name", "version"}, 0, 6, false,
				KaleoDefinitionVersion::getCompanyId,
				convertNullFunction(KaleoDefinitionVersion::getName),
				convertNullFunction(KaleoDefinitionVersion::getVersion)),
			_SQL_SELECT_KALEODEFINITIONVERSION_WHERE, "",
			new FinderColumn<>(
				"kaleoDefinitionVersion.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, KaleoDefinitionVersion::getCompanyId),
			new FinderColumn<>(
				"kaleoDefinitionVersion.", "name", FinderColumn.Type.STRING,
				"=", true, true, KaleoDefinitionVersion::getName),
			new FinderColumn<>(
				"kaleoDefinitionVersion.", "version", FinderColumn.Type.STRING,
				"=", true, true, KaleoDefinitionVersion::getVersion));

		KaleoDefinitionVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoDefinitionVersionUtil.setPersistence(null);

		entityCache.removeCache(KaleoDefinitionVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		KaleoDefinitionVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEODEFINITIONVERSION =
		"SELECT kaleoDefinitionVersion FROM KaleoDefinitionVersion kaleoDefinitionVersion";

	private static final String _SQL_SELECT_KALEODEFINITIONVERSION_WHERE =
		"SELECT kaleoDefinitionVersion FROM KaleoDefinitionVersion kaleoDefinitionVersion WHERE ";

	private static final String _SQL_COUNT_KALEODEFINITIONVERSION_WHERE =
		"SELECT COUNT(kaleoDefinitionVersion) FROM KaleoDefinitionVersion kaleoDefinitionVersion WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1539895147