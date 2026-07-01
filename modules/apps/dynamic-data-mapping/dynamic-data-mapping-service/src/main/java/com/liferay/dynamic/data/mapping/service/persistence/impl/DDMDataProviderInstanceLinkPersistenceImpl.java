/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchDataProviderInstanceLinkException;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLinkTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceLinkImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceLinkModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceLinkUtil;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
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
 * The persistence implementation for the ddm data provider instance link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMDataProviderInstanceLinkPersistence.class)
public class DDMDataProviderInstanceLinkPersistenceImpl
	extends BasePersistenceImpl
		<DDMDataProviderInstanceLink, NoSuchDataProviderInstanceLinkException>
	implements DDMDataProviderInstanceLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMDataProviderInstanceLinkUtil</code> to access the ddm data provider instance link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMDataProviderInstanceLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMDataProviderInstanceLink, NoSuchDataProviderInstanceLinkException>
			_collectionPersistenceFinderByDataProviderInstanceId;

	/**
	 * Returns an ordered range of all the ddm data provider instance links where dataProviderInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 * @param start the lower bound of the range of ddm data provider instance links
	 * @param end the upper bound of the range of ddm data provider instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm data provider instance links
	 */
	@Override
	public List<DDMDataProviderInstanceLink> findByDataProviderInstanceId(
		long dataProviderInstanceId, int start, int end,
		OrderByComparator<DDMDataProviderInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDataProviderInstanceId.find(
			finderCache, new Object[] {dataProviderInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm data provider instance link in the ordered set where dataProviderInstanceId = &#63;.
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance link
	 * @throws NoSuchDataProviderInstanceLinkException if a matching ddm data provider instance link could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink findByDataProviderInstanceId_First(
			long dataProviderInstanceId,
			OrderByComparator<DDMDataProviderInstanceLink> orderByComparator)
		throws NoSuchDataProviderInstanceLinkException {

		return _collectionPersistenceFinderByDataProviderInstanceId.findFirst(
			finderCache, new Object[] {dataProviderInstanceId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm data provider instance link in the ordered set where dataProviderInstanceId = &#63;.
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance link, or <code>null</code> if a matching ddm data provider instance link could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink fetchByDataProviderInstanceId_First(
		long dataProviderInstanceId,
		OrderByComparator<DDMDataProviderInstanceLink> orderByComparator) {

		return _collectionPersistenceFinderByDataProviderInstanceId.fetchFirst(
			finderCache, new Object[] {dataProviderInstanceId},
			orderByComparator);
	}

	/**
	 * Removes all the ddm data provider instance links where dataProviderInstanceId = &#63; from the database.
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 */
	@Override
	public void removeByDataProviderInstanceId(long dataProviderInstanceId) {
		_collectionPersistenceFinderByDataProviderInstanceId.remove(
			finderCache, new Object[] {dataProviderInstanceId});
	}

	/**
	 * Returns the number of ddm data provider instance links where dataProviderInstanceId = &#63;.
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 * @return the number of matching ddm data provider instance links
	 */
	@Override
	public int countByDataProviderInstanceId(long dataProviderInstanceId) {
		return _collectionPersistenceFinderByDataProviderInstanceId.count(
			finderCache, new Object[] {dataProviderInstanceId});
	}

	private CollectionPersistenceFinder
		<DDMDataProviderInstanceLink, NoSuchDataProviderInstanceLinkException>
			_collectionPersistenceFinderByStructureId;

	/**
	 * Returns an ordered range of all the ddm data provider instance links where structureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param structureId the structure ID
	 * @param start the lower bound of the range of ddm data provider instance links
	 * @param end the upper bound of the range of ddm data provider instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm data provider instance links
	 */
	@Override
	public List<DDMDataProviderInstanceLink> findByStructureId(
		long structureId, int start, int end,
		OrderByComparator<DDMDataProviderInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStructureId.find(
			finderCache, new Object[] {structureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm data provider instance link in the ordered set where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance link
	 * @throws NoSuchDataProviderInstanceLinkException if a matching ddm data provider instance link could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink findByStructureId_First(
			long structureId,
			OrderByComparator<DDMDataProviderInstanceLink> orderByComparator)
		throws NoSuchDataProviderInstanceLinkException {

		return _collectionPersistenceFinderByStructureId.findFirst(
			finderCache, new Object[] {structureId}, orderByComparator);
	}

	/**
	 * Returns the first ddm data provider instance link in the ordered set where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance link, or <code>null</code> if a matching ddm data provider instance link could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink fetchByStructureId_First(
		long structureId,
		OrderByComparator<DDMDataProviderInstanceLink> orderByComparator) {

		return _collectionPersistenceFinderByStructureId.fetchFirst(
			finderCache, new Object[] {structureId}, orderByComparator);
	}

	/**
	 * Removes all the ddm data provider instance links where structureId = &#63; from the database.
	 *
	 * @param structureId the structure ID
	 */
	@Override
	public void removeByStructureId(long structureId) {
		_collectionPersistenceFinderByStructureId.remove(
			finderCache, new Object[] {structureId});
	}

	/**
	 * Returns the number of ddm data provider instance links where structureId = &#63;.
	 *
	 * @param structureId the structure ID
	 * @return the number of matching ddm data provider instance links
	 */
	@Override
	public int countByStructureId(long structureId) {
		return _collectionPersistenceFinderByStructureId.count(
			finderCache, new Object[] {structureId});
	}

	private UniquePersistenceFinder
		<DDMDataProviderInstanceLink, NoSuchDataProviderInstanceLinkException>
			_uniquePersistenceFinderByD_S;

	/**
	 * Returns the ddm data provider instance link where dataProviderInstanceId = &#63; and structureId = &#63; or throws a <code>NoSuchDataProviderInstanceLinkException</code> if it could not be found.
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 * @param structureId the structure ID
	 * @return the matching ddm data provider instance link
	 * @throws NoSuchDataProviderInstanceLinkException if a matching ddm data provider instance link could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink findByD_S(
			long dataProviderInstanceId, long structureId)
		throws NoSuchDataProviderInstanceLinkException {

		return _uniquePersistenceFinderByD_S.find(
			finderCache, new Object[] {dataProviderInstanceId, structureId});
	}

	/**
	 * Returns the ddm data provider instance link where dataProviderInstanceId = &#63; and structureId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 * @param structureId the structure ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm data provider instance link, or <code>null</code> if a matching ddm data provider instance link could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink fetchByD_S(
		long dataProviderInstanceId, long structureId, boolean useFinderCache) {

		return _uniquePersistenceFinderByD_S.fetch(
			finderCache, new Object[] {dataProviderInstanceId, structureId},
			useFinderCache);
	}

	/**
	 * Removes the ddm data provider instance link where dataProviderInstanceId = &#63; and structureId = &#63; from the database.
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 * @param structureId the structure ID
	 * @return the ddm data provider instance link that was removed
	 */
	@Override
	public DDMDataProviderInstanceLink removeByD_S(
			long dataProviderInstanceId, long structureId)
		throws NoSuchDataProviderInstanceLinkException {

		DDMDataProviderInstanceLink ddmDataProviderInstanceLink = findByD_S(
			dataProviderInstanceId, structureId);

		return remove(ddmDataProviderInstanceLink);
	}

	/**
	 * Returns the number of ddm data provider instance links where dataProviderInstanceId = &#63; and structureId = &#63;.
	 *
	 * @param dataProviderInstanceId the data provider instance ID
	 * @param structureId the structure ID
	 * @return the number of matching ddm data provider instance links
	 */
	@Override
	public int countByD_S(long dataProviderInstanceId, long structureId) {
		return _uniquePersistenceFinderByD_S.count(
			finderCache, new Object[] {dataProviderInstanceId, structureId});
	}

	public DDMDataProviderInstanceLinkPersistenceImpl() {
		setModelClass(DDMDataProviderInstanceLink.class);

		setModelImplClass(DDMDataProviderInstanceLinkImpl.class);
		setModelPKClass(long.class);

		setTable(DDMDataProviderInstanceLinkTable.INSTANCE);
	}

	/**
	 * Creates a new ddm data provider instance link with the primary key. Does not add the ddm data provider instance link to the database.
	 *
	 * @param dataProviderInstanceLinkId the primary key for the new ddm data provider instance link
	 * @return the new ddm data provider instance link
	 */
	@Override
	public DDMDataProviderInstanceLink create(long dataProviderInstanceLinkId) {
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink =
			new DDMDataProviderInstanceLinkImpl();

		ddmDataProviderInstanceLink.setNew(true);
		ddmDataProviderInstanceLink.setPrimaryKey(dataProviderInstanceLinkId);

		ddmDataProviderInstanceLink.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return ddmDataProviderInstanceLink;
	}

	/**
	 * Removes the ddm data provider instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dataProviderInstanceLinkId the primary key of the ddm data provider instance link
	 * @return the ddm data provider instance link that was removed
	 * @throws NoSuchDataProviderInstanceLinkException if a ddm data provider instance link with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink remove(long dataProviderInstanceLinkId)
		throws NoSuchDataProviderInstanceLinkException {

		return remove((Serializable)dataProviderInstanceLinkId);
	}

	@Override
	protected DDMDataProviderInstanceLink removeImpl(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmDataProviderInstanceLink)) {
				ddmDataProviderInstanceLink =
					(DDMDataProviderInstanceLink)session.get(
						DDMDataProviderInstanceLinkImpl.class,
						ddmDataProviderInstanceLink.getPrimaryKeyObj());
			}

			if ((ddmDataProviderInstanceLink != null) &&
				ctPersistenceHelper.isRemove(ddmDataProviderInstanceLink)) {

				session.delete(ddmDataProviderInstanceLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmDataProviderInstanceLink != null) {
			clearCache(ddmDataProviderInstanceLink);
		}

		return ddmDataProviderInstanceLink;
	}

	@Override
	public DDMDataProviderInstanceLink updateImpl(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		boolean isNew = ddmDataProviderInstanceLink.isNew();

		if (!(ddmDataProviderInstanceLink instanceof
				DDMDataProviderInstanceLinkModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					ddmDataProviderInstanceLink.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmDataProviderInstanceLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmDataProviderInstanceLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMDataProviderInstanceLink implementation " +
					ddmDataProviderInstanceLink.getClass());
		}

		DDMDataProviderInstanceLinkModelImpl
			ddmDataProviderInstanceLinkModelImpl =
				(DDMDataProviderInstanceLinkModelImpl)
					ddmDataProviderInstanceLink;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmDataProviderInstanceLink)) {
				if (!isNew) {
					session.evict(
						DDMDataProviderInstanceLinkImpl.class,
						ddmDataProviderInstanceLink.getPrimaryKeyObj());
				}

				session.save(ddmDataProviderInstanceLink);
			}
			else {
				ddmDataProviderInstanceLink =
					(DDMDataProviderInstanceLink)session.merge(
						ddmDataProviderInstanceLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmDataProviderInstanceLink, false);

		if (isNew) {
			ddmDataProviderInstanceLink.setNew(false);
		}

		ddmDataProviderInstanceLink.resetOriginalValues();

		return ddmDataProviderInstanceLink;
	}

	/**
	 * Returns the ddm data provider instance link with the primary key or throws a <code>NoSuchDataProviderInstanceLinkException</code> if it could not be found.
	 *
	 * @param dataProviderInstanceLinkId the primary key of the ddm data provider instance link
	 * @return the ddm data provider instance link
	 * @throws NoSuchDataProviderInstanceLinkException if a ddm data provider instance link with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink findByPrimaryKey(
			long dataProviderInstanceLinkId)
		throws NoSuchDataProviderInstanceLinkException {

		return findByPrimaryKey((Serializable)dataProviderInstanceLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm data provider instance link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dataProviderInstanceLinkId the primary key of the ddm data provider instance link
	 * @return the ddm data provider instance link, or <code>null</code> if a ddm data provider instance link with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstanceLink fetchByPrimaryKey(
		long dataProviderInstanceLinkId) {

		return fetchByPrimaryKey((Serializable)dataProviderInstanceLinkId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dataProviderInstanceLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMDATAPROVIDERINSTANCELINK;
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
		return DDMDataProviderInstanceLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMDataProviderInstanceLink";
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
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("dataProviderInstanceId");
		ctMergeColumnNames.add("structureId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("dataProviderInstanceLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"dataProviderInstanceId", "structureId"});
	}

	/**
	 * Initializes the ddm data provider instance link persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByDataProviderInstanceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByDataProviderInstanceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"dataProviderInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByDataProviderInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"dataProviderInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByDataProviderInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"dataProviderInstanceId"}, false),
				_SQL_SELECT_DDMDATAPROVIDERINSTANCELINK_WHERE,
				_SQL_COUNT_DDMDATAPROVIDERINSTANCELINK_WHERE,
				DDMDataProviderInstanceLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ddmDataProviderInstanceLink.", "dataProviderInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMDataProviderInstanceLink::getDataProviderInstanceId));

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
				_SQL_SELECT_DDMDATAPROVIDERINSTANCELINK_WHERE,
				_SQL_COUNT_DDMDATAPROVIDERINSTANCELINK_WHERE,
				DDMDataProviderInstanceLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ddmDataProviderInstanceLink.", "structureId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMDataProviderInstanceLink::getStructureId));

		_uniquePersistenceFinderByD_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByD_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"dataProviderInstanceId", "structureId"}, 0, 0,
				false, DDMDataProviderInstanceLink::getDataProviderInstanceId,
				DDMDataProviderInstanceLink::getStructureId),
			_SQL_SELECT_DDMDATAPROVIDERINSTANCELINK_WHERE, "",
			new FinderColumn<>(
				"ddmDataProviderInstanceLink.", "dataProviderInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMDataProviderInstanceLink::getDataProviderInstanceId),
			new FinderColumn<>(
				"ddmDataProviderInstanceLink.", "structureId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMDataProviderInstanceLink::getStructureId));

		DDMDataProviderInstanceLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMDataProviderInstanceLinkUtil.setPersistence(null);

		entityCache.removeCache(
			DDMDataProviderInstanceLinkImpl.class.getName());
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
		DDMDataProviderInstanceLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMDATAPROVIDERINSTANCELINK =
		"SELECT ddmDataProviderInstanceLink FROM DDMDataProviderInstanceLink ddmDataProviderInstanceLink";

	private static final String _SQL_SELECT_DDMDATAPROVIDERINSTANCELINK_WHERE =
		"SELECT ddmDataProviderInstanceLink FROM DDMDataProviderInstanceLink ddmDataProviderInstanceLink WHERE ";

	private static final String _SQL_COUNT_DDMDATAPROVIDERINSTANCELINK_WHERE =
		"SELECT COUNT(ddmDataProviderInstanceLink) FROM DDMDataProviderInstanceLink ddmDataProviderInstanceLink WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMDataProviderInstanceLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMDataProviderInstanceLinkPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:328583612