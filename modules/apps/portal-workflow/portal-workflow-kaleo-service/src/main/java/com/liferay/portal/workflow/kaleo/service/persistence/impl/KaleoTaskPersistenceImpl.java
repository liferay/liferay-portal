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
import com.liferay.portal.workflow.kaleo.exception.NoSuchTaskException;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskUtil;
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
 * The persistence implementation for the kaleo task service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoTaskPersistence.class)
public class KaleoTaskPersistenceImpl
	extends BasePersistenceImpl<KaleoTask, NoSuchTaskException>
	implements KaleoTaskPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoTaskUtil</code> to access the kaleo task persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoTaskImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KaleoTask, NoSuchTaskException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo tasks where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo tasks
	 * @param end the upper bound of the range of kaleo tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo tasks
	 */
	@Override
	public List<KaleoTask> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task
	 * @throws NoSuchTaskException if a matching kaleo task could not be found
	 */
	@Override
	public KaleoTask findByCompanyId_First(
			long companyId, OrderByComparator<KaleoTask> orderByComparator)
		throws NoSuchTaskException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task, or <code>null</code> if a matching kaleo task could not be found
	 */
	@Override
	public KaleoTask fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoTask> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo tasks where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo tasks where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo tasks
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<KaleoTask, NoSuchTaskException>
		_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo tasks where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo tasks
	 * @param end the upper bound of the range of kaleo tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo tasks
	 */
	@Override
	public List<KaleoTask> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task
	 * @throws NoSuchTaskException if a matching kaleo task could not be found
	 */
	@Override
	public KaleoTask findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTask> orderByComparator)
		throws NoSuchTaskException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task, or <code>null</code> if a matching kaleo task could not be found
	 */
	@Override
	public KaleoTask fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoTask> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo tasks where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	@Override
	public void removeByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		_collectionPersistenceFinderByKaleoDefinitionVersionId.remove(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	/**
	 * Returns the number of kaleo tasks where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo tasks
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private UniquePersistenceFinder<KaleoTask, NoSuchTaskException>
		_uniquePersistenceFinderByKaleoNodeId;

	/**
	 * Returns the kaleo task where kaleoNodeId = &#63; or throws a <code>NoSuchTaskException</code> if it could not be found.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the matching kaleo task
	 * @throws NoSuchTaskException if a matching kaleo task could not be found
	 */
	@Override
	public KaleoTask findByKaleoNodeId(long kaleoNodeId)
		throws NoSuchTaskException {

		return _uniquePersistenceFinderByKaleoNodeId.find(
			finderCache, new Object[] {kaleoNodeId});
	}

	/**
	 * Returns the kaleo task where kaleoNodeId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo task, or <code>null</code> if a matching kaleo task could not be found
	 */
	@Override
	public KaleoTask fetchByKaleoNodeId(
		long kaleoNodeId, boolean useFinderCache) {

		return _uniquePersistenceFinderByKaleoNodeId.fetch(
			finderCache, new Object[] {kaleoNodeId}, useFinderCache);
	}

	/**
	 * Removes the kaleo task where kaleoNodeId = &#63; from the database.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the kaleo task that was removed
	 */
	@Override
	public KaleoTask removeByKaleoNodeId(long kaleoNodeId)
		throws NoSuchTaskException {

		KaleoTask kaleoTask = findByKaleoNodeId(kaleoNodeId);

		return remove(kaleoTask);
	}

	/**
	 * Returns the number of kaleo tasks where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the number of matching kaleo tasks
	 */
	@Override
	public int countByKaleoNodeId(long kaleoNodeId) {
		return _uniquePersistenceFinderByKaleoNodeId.count(
			finderCache, new Object[] {kaleoNodeId});
	}

	public KaleoTaskPersistenceImpl() {
		setModelClass(KaleoTask.class);

		setModelImplClass(KaleoTaskImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoTaskTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo task with the primary key. Does not add the kaleo task to the database.
	 *
	 * @param kaleoTaskId the primary key for the new kaleo task
	 * @return the new kaleo task
	 */
	@Override
	public KaleoTask create(long kaleoTaskId) {
		KaleoTask kaleoTask = new KaleoTaskImpl();

		kaleoTask.setNew(true);
		kaleoTask.setPrimaryKey(kaleoTaskId);

		kaleoTask.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoTask;
	}

	/**
	 * Removes the kaleo task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTaskId the primary key of the kaleo task
	 * @return the kaleo task that was removed
	 * @throws NoSuchTaskException if a kaleo task with the primary key could not be found
	 */
	@Override
	public KaleoTask remove(long kaleoTaskId) throws NoSuchTaskException {
		return remove((Serializable)kaleoTaskId);
	}

	@Override
	protected KaleoTask removeImpl(KaleoTask kaleoTask) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoTask)) {
				kaleoTask = (KaleoTask)session.get(
					KaleoTaskImpl.class, kaleoTask.getPrimaryKeyObj());
			}

			if ((kaleoTask != null) &&
				ctPersistenceHelper.isRemove(kaleoTask)) {

				session.delete(kaleoTask);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoTask != null) {
			clearCache(kaleoTask);
		}

		return kaleoTask;
	}

	@Override
	public KaleoTask updateImpl(KaleoTask kaleoTask) {
		boolean isNew = kaleoTask.isNew();

		if (!(kaleoTask instanceof KaleoTaskModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoTask.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(kaleoTask);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoTask proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoTask implementation " +
					kaleoTask.getClass());
		}

		KaleoTaskModelImpl kaleoTaskModelImpl = (KaleoTaskModelImpl)kaleoTask;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoTask.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoTask.setCreateDate(date);
			}
			else {
				kaleoTask.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoTaskModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoTask.setModifiedDate(date);
			}
			else {
				kaleoTask.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoTask)) {
				if (!isNew) {
					session.evict(
						KaleoTaskImpl.class, kaleoTask.getPrimaryKeyObj());
				}

				session.save(kaleoTask);
			}
			else {
				kaleoTask = (KaleoTask)session.merge(kaleoTask);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoTask, false);

		if (isNew) {
			kaleoTask.setNew(false);
		}

		kaleoTask.resetOriginalValues();

		return kaleoTask;
	}

	/**
	 * Returns the kaleo task with the primary key or throws a <code>NoSuchTaskException</code> if it could not be found.
	 *
	 * @param kaleoTaskId the primary key of the kaleo task
	 * @return the kaleo task
	 * @throws NoSuchTaskException if a kaleo task with the primary key could not be found
	 */
	@Override
	public KaleoTask findByPrimaryKey(long kaleoTaskId)
		throws NoSuchTaskException {

		return findByPrimaryKey((Serializable)kaleoTaskId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo task with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTaskId the primary key of the kaleo task
	 * @return the kaleo task, or <code>null</code> if a kaleo task with the primary key could not be found
	 */
	@Override
	public KaleoTask fetchByPrimaryKey(long kaleoTaskId) {
		return fetchByPrimaryKey((Serializable)kaleoTaskId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoTaskId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOTASK;
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
		return KaleoTaskModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoTask";
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
		ctMergeColumnNames.add("kaleoDefinitionVersionId");
		ctMergeColumnNames.add("kaleoNodeId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("kaleoTaskId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo task persistence.
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
				_SQL_SELECT_KALEOTASK_WHERE, _SQL_COUNT_KALEOTASK_WHERE,
				KaleoTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTask.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoTask::getCompanyId));

		_collectionPersistenceFinderByKaleoDefinitionVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, false),
				_SQL_SELECT_KALEOTASK_WHERE, _SQL_COUNT_KALEOTASK_WHERE,
				KaleoTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTask.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTask::getKaleoDefinitionVersionId));

		_uniquePersistenceFinderByKaleoNodeId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKaleoNodeId",
				new String[] {Long.class.getName()},
				new String[] {"kaleoNodeId"}, 0, 0, false,
				KaleoTask::getKaleoNodeId),
			_SQL_SELECT_KALEOTASK_WHERE, "",
			new FinderColumn<>(
				"kaleoTask.", "kaleoNodeId", FinderColumn.Type.LONG, "=", true,
				true, KaleoTask::getKaleoNodeId));

		KaleoTaskUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoTaskUtil.setPersistence(null);

		entityCache.removeCache(KaleoTaskImpl.class.getName());
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
		KaleoTaskModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOTASK =
		"SELECT kaleoTask FROM KaleoTask kaleoTask";

	private static final String _SQL_SELECT_KALEOTASK_WHERE =
		"SELECT kaleoTask FROM KaleoTask kaleoTask WHERE ";

	private static final String _SQL_COUNT_KALEOTASK_WHERE =
		"SELECT COUNT(kaleoTask) FROM KaleoTask kaleoTask WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoTask exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTaskPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-813545228