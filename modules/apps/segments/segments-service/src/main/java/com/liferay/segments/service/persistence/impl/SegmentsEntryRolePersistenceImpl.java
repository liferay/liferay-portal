/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.impl;

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
import com.liferay.segments.exception.NoSuchEntryRoleException;
import com.liferay.segments.model.SegmentsEntryRole;
import com.liferay.segments.model.SegmentsEntryRoleTable;
import com.liferay.segments.model.impl.SegmentsEntryRoleImpl;
import com.liferay.segments.model.impl.SegmentsEntryRoleModelImpl;
import com.liferay.segments.service.persistence.SegmentsEntryRolePersistence;
import com.liferay.segments.service.persistence.SegmentsEntryRoleUtil;
import com.liferay.segments.service.persistence.impl.constants.SegmentsPersistenceConstants;

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
 * The persistence implementation for the segments entry role service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @generated
 */
@Component(service = SegmentsEntryRolePersistence.class)
public class SegmentsEntryRolePersistenceImpl
	extends BasePersistenceImpl<SegmentsEntryRole, NoSuchEntryRoleException>
	implements SegmentsEntryRolePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SegmentsEntryRoleUtil</code> to access the segments entry role persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SegmentsEntryRoleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SegmentsEntryRole, NoSuchEntryRoleException>
			_collectionPersistenceFinderBySegmentsEntryId;

	/**
	 * Returns an ordered range of all the segments entry roles where segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsEntryRoleModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of segments entry roles
	 * @param end the upper bound of the range of segments entry roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments entry roles
	 */
	@Override
	public List<SegmentsEntryRole> findBySegmentsEntryId(
		long segmentsEntryId, int start, int end,
		OrderByComparator<SegmentsEntryRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsEntryId.find(
			finderCache, new Object[] {segmentsEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments entry role in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry role
	 * @throws NoSuchEntryRoleException if a matching segments entry role could not be found
	 */
	@Override
	public SegmentsEntryRole findBySegmentsEntryId_First(
			long segmentsEntryId,
			OrderByComparator<SegmentsEntryRole> orderByComparator)
		throws NoSuchEntryRoleException {

		return _collectionPersistenceFinderBySegmentsEntryId.findFirst(
			finderCache, new Object[] {segmentsEntryId}, orderByComparator);
	}

	/**
	 * Returns the first segments entry role in the ordered set where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry role, or <code>null</code> if a matching segments entry role could not be found
	 */
	@Override
	public SegmentsEntryRole fetchBySegmentsEntryId_First(
		long segmentsEntryId,
		OrderByComparator<SegmentsEntryRole> orderByComparator) {

		return _collectionPersistenceFinderBySegmentsEntryId.fetchFirst(
			finderCache, new Object[] {segmentsEntryId}, orderByComparator);
	}

	/**
	 * Removes all the segments entry roles where segmentsEntryId = &#63; from the database.
	 *
	 * @param segmentsEntryId the segments entry ID
	 */
	@Override
	public void removeBySegmentsEntryId(long segmentsEntryId) {
		_collectionPersistenceFinderBySegmentsEntryId.remove(
			finderCache, new Object[] {segmentsEntryId});
	}

	/**
	 * Returns the number of segments entry roles where segmentsEntryId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @return the number of matching segments entry roles
	 */
	@Override
	public int countBySegmentsEntryId(long segmentsEntryId) {
		return _collectionPersistenceFinderBySegmentsEntryId.count(
			finderCache, new Object[] {segmentsEntryId});
	}

	private CollectionPersistenceFinder
		<SegmentsEntryRole, NoSuchEntryRoleException>
			_collectionPersistenceFinderByRoleId;

	/**
	 * Returns an ordered range of all the segments entry roles where roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsEntryRoleModelImpl</code>.
	 * </p>
	 *
	 * @param roleId the role ID
	 * @param start the lower bound of the range of segments entry roles
	 * @param end the upper bound of the range of segments entry roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments entry roles
	 */
	@Override
	public List<SegmentsEntryRole> findByRoleId(
		long roleId, int start, int end,
		OrderByComparator<SegmentsEntryRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRoleId.find(
			finderCache, new Object[] {roleId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first segments entry role in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry role
	 * @throws NoSuchEntryRoleException if a matching segments entry role could not be found
	 */
	@Override
	public SegmentsEntryRole findByRoleId_First(
			long roleId, OrderByComparator<SegmentsEntryRole> orderByComparator)
		throws NoSuchEntryRoleException {

		return _collectionPersistenceFinderByRoleId.findFirst(
			finderCache, new Object[] {roleId}, orderByComparator);
	}

	/**
	 * Returns the first segments entry role in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments entry role, or <code>null</code> if a matching segments entry role could not be found
	 */
	@Override
	public SegmentsEntryRole fetchByRoleId_First(
		long roleId, OrderByComparator<SegmentsEntryRole> orderByComparator) {

		return _collectionPersistenceFinderByRoleId.fetchFirst(
			finderCache, new Object[] {roleId}, orderByComparator);
	}

	/**
	 * Removes all the segments entry roles where roleId = &#63; from the database.
	 *
	 * @param roleId the role ID
	 */
	@Override
	public void removeByRoleId(long roleId) {
		_collectionPersistenceFinderByRoleId.remove(
			finderCache, new Object[] {roleId});
	}

	/**
	 * Returns the number of segments entry roles where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the number of matching segments entry roles
	 */
	@Override
	public int countByRoleId(long roleId) {
		return _collectionPersistenceFinderByRoleId.count(
			finderCache, new Object[] {roleId});
	}

	private UniquePersistenceFinder<SegmentsEntryRole, NoSuchEntryRoleException>
		_uniquePersistenceFinderByS_R;

	/**
	 * Returns the segments entry role where segmentsEntryId = &#63; and roleId = &#63; or throws a <code>NoSuchEntryRoleException</code> if it could not be found.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param roleId the role ID
	 * @return the matching segments entry role
	 * @throws NoSuchEntryRoleException if a matching segments entry role could not be found
	 */
	@Override
	public SegmentsEntryRole findByS_R(long segmentsEntryId, long roleId)
		throws NoSuchEntryRoleException {

		return _uniquePersistenceFinderByS_R.find(
			finderCache, new Object[] {segmentsEntryId, roleId});
	}

	/**
	 * Returns the segments entry role where segmentsEntryId = &#63; and roleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param roleId the role ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments entry role, or <code>null</code> if a matching segments entry role could not be found
	 */
	@Override
	public SegmentsEntryRole fetchByS_R(
		long segmentsEntryId, long roleId, boolean useFinderCache) {

		return _uniquePersistenceFinderByS_R.fetch(
			finderCache, new Object[] {segmentsEntryId, roleId},
			useFinderCache);
	}

	/**
	 * Removes the segments entry role where segmentsEntryId = &#63; and roleId = &#63; from the database.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param roleId the role ID
	 * @return the segments entry role that was removed
	 */
	@Override
	public SegmentsEntryRole removeByS_R(long segmentsEntryId, long roleId)
		throws NoSuchEntryRoleException {

		SegmentsEntryRole segmentsEntryRole = findByS_R(
			segmentsEntryId, roleId);

		return remove(segmentsEntryRole);
	}

	/**
	 * Returns the number of segments entry roles where segmentsEntryId = &#63; and roleId = &#63;.
	 *
	 * @param segmentsEntryId the segments entry ID
	 * @param roleId the role ID
	 * @return the number of matching segments entry roles
	 */
	@Override
	public int countByS_R(long segmentsEntryId, long roleId) {
		return _uniquePersistenceFinderByS_R.count(
			finderCache, new Object[] {segmentsEntryId, roleId});
	}

	public SegmentsEntryRolePersistenceImpl() {
		setModelClass(SegmentsEntryRole.class);

		setModelImplClass(SegmentsEntryRoleImpl.class);
		setModelPKClass(long.class);

		setTable(SegmentsEntryRoleTable.INSTANCE);
	}

	/**
	 * Creates a new segments entry role with the primary key. Does not add the segments entry role to the database.
	 *
	 * @param segmentsEntryRoleId the primary key for the new segments entry role
	 * @return the new segments entry role
	 */
	@Override
	public SegmentsEntryRole create(long segmentsEntryRoleId) {
		SegmentsEntryRole segmentsEntryRole = new SegmentsEntryRoleImpl();

		segmentsEntryRole.setNew(true);
		segmentsEntryRole.setPrimaryKey(segmentsEntryRoleId);

		segmentsEntryRole.setCompanyId(CompanyThreadLocal.getCompanyId());

		return segmentsEntryRole;
	}

	/**
	 * Removes the segments entry role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsEntryRoleId the primary key of the segments entry role
	 * @return the segments entry role that was removed
	 * @throws NoSuchEntryRoleException if a segments entry role with the primary key could not be found
	 */
	@Override
	public SegmentsEntryRole remove(long segmentsEntryRoleId)
		throws NoSuchEntryRoleException {

		return remove((Serializable)segmentsEntryRoleId);
	}

	@Override
	protected SegmentsEntryRole removeImpl(
		SegmentsEntryRole segmentsEntryRole) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(segmentsEntryRole)) {
				segmentsEntryRole = (SegmentsEntryRole)session.get(
					SegmentsEntryRoleImpl.class,
					segmentsEntryRole.getPrimaryKeyObj());
			}

			if ((segmentsEntryRole != null) &&
				ctPersistenceHelper.isRemove(segmentsEntryRole)) {

				session.delete(segmentsEntryRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (segmentsEntryRole != null) {
			clearCache(segmentsEntryRole);
		}

		return segmentsEntryRole;
	}

	@Override
	public SegmentsEntryRole updateImpl(SegmentsEntryRole segmentsEntryRole) {
		boolean isNew = segmentsEntryRole.isNew();

		if (!(segmentsEntryRole instanceof SegmentsEntryRoleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(segmentsEntryRole.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					segmentsEntryRole);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in segmentsEntryRole proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SegmentsEntryRole implementation " +
					segmentsEntryRole.getClass());
		}

		SegmentsEntryRoleModelImpl segmentsEntryRoleModelImpl =
			(SegmentsEntryRoleModelImpl)segmentsEntryRole;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (segmentsEntryRole.getCreateDate() == null)) {
			if (serviceContext == null) {
				segmentsEntryRole.setCreateDate(date);
			}
			else {
				segmentsEntryRole.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!segmentsEntryRoleModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				segmentsEntryRole.setModifiedDate(date);
			}
			else {
				segmentsEntryRole.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(segmentsEntryRole)) {
				if (!isNew) {
					session.evict(
						SegmentsEntryRoleImpl.class,
						segmentsEntryRole.getPrimaryKeyObj());
				}

				session.save(segmentsEntryRole);
			}
			else {
				segmentsEntryRole = (SegmentsEntryRole)session.merge(
					segmentsEntryRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(segmentsEntryRole, false);

		if (isNew) {
			segmentsEntryRole.setNew(false);
		}

		segmentsEntryRole.resetOriginalValues();

		return segmentsEntryRole;
	}

	/**
	 * Returns the segments entry role with the primary key or throws a <code>NoSuchEntryRoleException</code> if it could not be found.
	 *
	 * @param segmentsEntryRoleId the primary key of the segments entry role
	 * @return the segments entry role
	 * @throws NoSuchEntryRoleException if a segments entry role with the primary key could not be found
	 */
	@Override
	public SegmentsEntryRole findByPrimaryKey(long segmentsEntryRoleId)
		throws NoSuchEntryRoleException {

		return findByPrimaryKey((Serializable)segmentsEntryRoleId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the segments entry role with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsEntryRoleId the primary key of the segments entry role
	 * @return the segments entry role, or <code>null</code> if a segments entry role with the primary key could not be found
	 */
	@Override
	public SegmentsEntryRole fetchByPrimaryKey(long segmentsEntryRoleId) {
		return fetchByPrimaryKey((Serializable)segmentsEntryRoleId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "segmentsEntryRoleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SEGMENTSENTRYROLE;
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
		return SegmentsEntryRoleModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SegmentsEntryRole";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("segmentsEntryId");
		ctMergeColumnNames.add("roleId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("segmentsEntryRoleId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"segmentsEntryId", "roleId"});
	}

	/**
	 * Initializes the segments entry role persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderBySegmentsEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsEntryId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsEntryId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsEntryId"}, false),
				_SQL_SELECT_SEGMENTSENTRYROLE_WHERE,
				_SQL_COUNT_SEGMENTSENTRYROLE_WHERE,
				SegmentsEntryRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"segmentsEntryRole.", "segmentsEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					SegmentsEntryRole::getSegmentsEntryId));

		_collectionPersistenceFinderByRoleId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRoleId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"roleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRoleId",
					new String[] {Long.class.getName()},
					new String[] {"roleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRoleId",
					new String[] {Long.class.getName()},
					new String[] {"roleId"}, false),
				_SQL_SELECT_SEGMENTSENTRYROLE_WHERE,
				_SQL_COUNT_SEGMENTSENTRYROLE_WHERE,
				SegmentsEntryRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"segmentsEntryRole.", "roleId", FinderColumn.Type.LONG, "=",
					true, true, SegmentsEntryRole::getRoleId));

		_uniquePersistenceFinderByS_R = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByS_R",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"segmentsEntryId", "roleId"}, 0, 0, false,
				SegmentsEntryRole::getSegmentsEntryId,
				SegmentsEntryRole::getRoleId),
			_SQL_SELECT_SEGMENTSENTRYROLE_WHERE, "",
			new FinderColumn<>(
				"segmentsEntryRole.", "segmentsEntryId", FinderColumn.Type.LONG,
				"=", true, true, SegmentsEntryRole::getSegmentsEntryId),
			new FinderColumn<>(
				"segmentsEntryRole.", "roleId", FinderColumn.Type.LONG, "=",
				true, true, SegmentsEntryRole::getRoleId));

		SegmentsEntryRoleUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SegmentsEntryRoleUtil.setPersistence(null);

		entityCache.removeCache(SegmentsEntryRoleImpl.class.getName());
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SegmentsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SegmentsEntryRoleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SEGMENTSENTRYROLE =
		"SELECT segmentsEntryRole FROM SegmentsEntryRole segmentsEntryRole";

	private static final String _SQL_SELECT_SEGMENTSENTRYROLE_WHERE =
		"SELECT segmentsEntryRole FROM SegmentsEntryRole segmentsEntryRole WHERE ";

	private static final String _SQL_COUNT_SEGMENTSENTRYROLE_WHERE =
		"SELECT COUNT(segmentsEntryRole) FROM SegmentsEntryRole segmentsEntryRole WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-570740937