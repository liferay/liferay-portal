/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.exception.NoSuchFaroProjectException;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroProjectTable;
import com.liferay.osb.faro.model.impl.FaroProjectImpl;
import com.liferay.osb.faro.model.impl.FaroProjectModelImpl;
import com.liferay.osb.faro.service.persistence.FaroProjectPersistence;
import com.liferay.osb.faro.service.persistence.FaroProjectUtil;
import com.liferay.osb.faro.service.persistence.impl.constants.OSBFaroPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the faro project service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @generated
 */
@Component(service = FaroProjectPersistence.class)
public class FaroProjectPersistenceImpl
	extends BasePersistenceImpl<FaroProject, NoSuchFaroProjectException>
	implements FaroProjectPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FaroProjectUtil</code> to access the faro project persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FaroProjectImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<FaroProject, NoSuchFaroProjectException>
		_uniquePersistenceFinderByGroupId;

	/**
	 * Returns the faro project where groupId = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	@Override
	public FaroProject findByGroupId(long groupId)
		throws NoSuchFaroProjectException {

		return _uniquePersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the faro project where groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	@Override
	public FaroProject fetchByGroupId(long groupId, boolean useFinderCache) {
		return _uniquePersistenceFinderByGroupId.fetch(
			finderCache, new Object[] {groupId}, useFinderCache);
	}

	/**
	 * Removes the faro project where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @return the faro project that was removed
	 */
	@Override
	public FaroProject removeByGroupId(long groupId)
		throws NoSuchFaroProjectException {

		FaroProject faroProject = findByGroupId(groupId);

		return remove(faroProject);
	}

	/**
	 * Returns the number of faro projects where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro projects
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _uniquePersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder<FaroProject, NoSuchFaroProjectException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the faro projects where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro projects
	 */
	@Override
	public List<FaroProject> findByUserId(
		long userId, int start, int end,
		OrderByComparator<FaroProject> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first faro project in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	@Override
	public FaroProject findByUserId_First(
			long userId, OrderByComparator<FaroProject> orderByComparator)
		throws NoSuchFaroProjectException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first faro project in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	@Override
	public FaroProject fetchByUserId_First(
		long userId, OrderByComparator<FaroProject> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the faro projects where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of faro projects where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching faro projects
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private UniquePersistenceFinder<FaroProject, NoSuchFaroProjectException>
		_uniquePersistenceFinderByCorpProjectUuid;

	/**
	 * Returns the faro project where corpProjectUuid = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	@Override
	public FaroProject findByCorpProjectUuid(String corpProjectUuid)
		throws NoSuchFaroProjectException {

		return _uniquePersistenceFinderByCorpProjectUuid.find(
			finderCache, new Object[] {corpProjectUuid});
	}

	/**
	 * Returns the faro project where corpProjectUuid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	@Override
	public FaroProject fetchByCorpProjectUuid(
		String corpProjectUuid, boolean useFinderCache) {

		return _uniquePersistenceFinderByCorpProjectUuid.fetch(
			finderCache, new Object[] {corpProjectUuid}, useFinderCache);
	}

	/**
	 * Removes the faro project where corpProjectUuid = &#63; from the database.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the faro project that was removed
	 */
	@Override
	public FaroProject removeByCorpProjectUuid(String corpProjectUuid)
		throws NoSuchFaroProjectException {

		FaroProject faroProject = findByCorpProjectUuid(corpProjectUuid);

		return remove(faroProject);
	}

	/**
	 * Returns the number of faro projects where corpProjectUuid = &#63;.
	 *
	 * @param corpProjectUuid the corp project uuid
	 * @return the number of matching faro projects
	 */
	@Override
	public int countByCorpProjectUuid(String corpProjectUuid) {
		return _uniquePersistenceFinderByCorpProjectUuid.count(
			finderCache, new Object[] {corpProjectUuid});
	}

	private CollectionPersistenceFinder<FaroProject, NoSuchFaroProjectException>
		_collectionPersistenceFinderByServerLocation;

	/**
	 * Returns an ordered range of all the faro projects where serverLocation = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param serverLocation the server location
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro projects
	 */
	@Override
	public List<FaroProject> findByServerLocation(
		String serverLocation, int start, int end,
		OrderByComparator<FaroProject> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByServerLocation.find(
			finderCache, new Object[] {serverLocation}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro project in the ordered set where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	@Override
	public FaroProject findByServerLocation_First(
			String serverLocation,
			OrderByComparator<FaroProject> orderByComparator)
		throws NoSuchFaroProjectException {

		return _collectionPersistenceFinderByServerLocation.findFirst(
			finderCache, new Object[] {serverLocation}, orderByComparator);
	}

	/**
	 * Returns the first faro project in the ordered set where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	@Override
	public FaroProject fetchByServerLocation_First(
		String serverLocation,
		OrderByComparator<FaroProject> orderByComparator) {

		return _collectionPersistenceFinderByServerLocation.fetchFirst(
			finderCache, new Object[] {serverLocation}, orderByComparator);
	}

	/**
	 * Removes all the faro projects where serverLocation = &#63; from the database.
	 *
	 * @param serverLocation the server location
	 */
	@Override
	public void removeByServerLocation(String serverLocation) {
		_collectionPersistenceFinderByServerLocation.remove(
			finderCache, new Object[] {serverLocation});
	}

	/**
	 * Returns the number of faro projects where serverLocation = &#63;.
	 *
	 * @param serverLocation the server location
	 * @return the number of matching faro projects
	 */
	@Override
	public int countByServerLocation(String serverLocation) {
		return _collectionPersistenceFinderByServerLocation.count(
			finderCache, new Object[] {serverLocation});
	}

	private UniquePersistenceFinder<FaroProject, NoSuchFaroProjectException>
		_uniquePersistenceFinderByWeDeployKey;

	/**
	 * Returns the faro project where weDeployKey = &#63; or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the matching faro project
	 * @throws NoSuchFaroProjectException if a matching faro project could not be found
	 */
	@Override
	public FaroProject findByWeDeployKey(String weDeployKey)
		throws NoSuchFaroProjectException {

		return _uniquePersistenceFinderByWeDeployKey.find(
			finderCache, new Object[] {weDeployKey});
	}

	/**
	 * Returns the faro project where weDeployKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param weDeployKey the we deploy key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project, or <code>null</code> if a matching faro project could not be found
	 */
	@Override
	public FaroProject fetchByWeDeployKey(
		String weDeployKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByWeDeployKey.fetch(
			finderCache, new Object[] {weDeployKey}, useFinderCache);
	}

	/**
	 * Removes the faro project where weDeployKey = &#63; from the database.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the faro project that was removed
	 */
	@Override
	public FaroProject removeByWeDeployKey(String weDeployKey)
		throws NoSuchFaroProjectException {

		FaroProject faroProject = findByWeDeployKey(weDeployKey);

		return remove(faroProject);
	}

	/**
	 * Returns the number of faro projects where weDeployKey = &#63;.
	 *
	 * @param weDeployKey the we deploy key
	 * @return the number of matching faro projects
	 */
	@Override
	public int countByWeDeployKey(String weDeployKey) {
		return _uniquePersistenceFinderByWeDeployKey.count(
			finderCache, new Object[] {weDeployKey});
	}

	public FaroProjectPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("state", "state_");

		setDBColumnNames(dbColumnNames);

		setModelClass(FaroProject.class);

		setModelImplClass(FaroProjectImpl.class);
		setModelPKClass(long.class);

		setTable(FaroProjectTable.INSTANCE);
	}

	/**
	 * Creates a new faro project with the primary key. Does not add the faro project to the database.
	 *
	 * @param faroProjectId the primary key for the new faro project
	 * @return the new faro project
	 */
	@Override
	public FaroProject create(long faroProjectId) {
		FaroProject faroProject = new FaroProjectImpl();

		faroProject.setNew(true);
		faroProject.setPrimaryKey(faroProjectId);

		faroProject.setCompanyId(CompanyThreadLocal.getCompanyId());

		return faroProject;
	}

	/**
	 * Removes the faro project with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project that was removed
	 * @throws NoSuchFaroProjectException if a faro project with the primary key could not be found
	 */
	@Override
	public FaroProject remove(long faroProjectId)
		throws NoSuchFaroProjectException {

		return remove((Serializable)faroProjectId);
	}

	@Override
	protected FaroProject removeImpl(FaroProject faroProject) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(faroProject)) {
				faroProject = (FaroProject)session.get(
					FaroProjectImpl.class, faroProject.getPrimaryKeyObj());
			}

			if (faroProject != null) {
				session.delete(faroProject);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (faroProject != null) {
			clearCache(faroProject);
		}

		return faroProject;
	}

	@Override
	public FaroProject updateImpl(FaroProject faroProject) {
		boolean isNew = faroProject.isNew();

		if (!(faroProject instanceof FaroProjectModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(faroProject.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(faroProject);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in faroProject proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FaroProject implementation " +
					faroProject.getClass());
		}

		FaroProjectModelImpl faroProjectModelImpl =
			(FaroProjectModelImpl)faroProject;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(faroProject);
			}
			else {
				faroProject = (FaroProject)session.merge(faroProject);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(faroProject, false);

		if (isNew) {
			faroProject.setNew(false);
		}

		faroProject.resetOriginalValues();

		return faroProject;
	}

	/**
	 * Returns the faro project with the primary key or throws a <code>NoSuchFaroProjectException</code> if it could not be found.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project
	 * @throws NoSuchFaroProjectException if a faro project with the primary key could not be found
	 */
	@Override
	public FaroProject findByPrimaryKey(long faroProjectId)
		throws NoSuchFaroProjectException {

		return findByPrimaryKey((Serializable)faroProjectId);
	}

	/**
	 * Returns the faro project with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project, or <code>null</code> if a faro project with the primary key could not be found
	 */
	@Override
	public FaroProject fetchByPrimaryKey(long faroProjectId) {
		return fetchByPrimaryKey((Serializable)faroProjectId);
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
		return "faroProjectId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FAROPROJECT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FaroProjectModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the faro project persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByGroupId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByGroupId",
				new String[] {Long.class.getName()}, new String[] {"groupId"},
				0, 0, false, FaroProject::getGroupId),
			_SQL_SELECT_FAROPROJECT_WHERE, "",
			new FinderColumn<>(
				"faroProject.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, FaroProject::getGroupId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_FAROPROJECT_WHERE, _SQL_COUNT_FAROPROJECT_WHERE,
				FaroProjectModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"faroProject.", "userId", FinderColumn.Type.LONG, "=", true,
					true, FaroProject::getUserId));

		_uniquePersistenceFinderByCorpProjectUuid =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByCorpProjectUuid",
					new String[] {String.class.getName()},
					new String[] {"corpProjectUuid"}, 0, 1, false,
					convertNullFunction(FaroProject::getCorpProjectUuid)),
				_SQL_SELECT_FAROPROJECT_WHERE, "",
				new FinderColumn<>(
					"faroProject.", "corpProjectUuid", FinderColumn.Type.STRING,
					"=", true, true, FaroProject::getCorpProjectUuid));

		_collectionPersistenceFinderByServerLocation =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByServerLocation",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"serverLocation"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByServerLocation",
					new String[] {String.class.getName()},
					new String[] {"serverLocation"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByServerLocation",
					new String[] {String.class.getName()},
					new String[] {"serverLocation"}, 0, 1, false, null),
				_SQL_SELECT_FAROPROJECT_WHERE, _SQL_COUNT_FAROPROJECT_WHERE,
				FaroProjectModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"faroProject.", "serverLocation", FinderColumn.Type.STRING,
					"=", true, true, FaroProject::getServerLocation));

		_uniquePersistenceFinderByWeDeployKey = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByWeDeployKey",
				new String[] {String.class.getName()},
				new String[] {"weDeployKey"}, 0, 1, false,
				convertNullFunction(FaroProject::getWeDeployKey)),
			_SQL_SELECT_FAROPROJECT_WHERE, "",
			new FinderColumn<>(
				"faroProject.", "weDeployKey", FinderColumn.Type.STRING, "=",
				true, true, FaroProject::getWeDeployKey));

		FaroProjectUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FaroProjectUtil.setPersistence(null);

		entityCache.removeCache(FaroProjectImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		FaroProjectModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FAROPROJECT =
		"SELECT faroProject FROM FaroProject faroProject";

	private static final String _SQL_SELECT_FAROPROJECT_WHERE =
		"SELECT faroProject FROM FaroProject faroProject WHERE ";

	private static final String _SQL_COUNT_FAROPROJECT_WHERE =
		"SELECT COUNT(faroProject) FROM FaroProject faroProject WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"state"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-963518515