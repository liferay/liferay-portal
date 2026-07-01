/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.service.persistence.impl;

import com.liferay.batch.planner.exception.NoSuchPolicyException;
import com.liferay.batch.planner.model.BatchPlannerPolicy;
import com.liferay.batch.planner.model.BatchPlannerPolicyTable;
import com.liferay.batch.planner.model.impl.BatchPlannerPolicyImpl;
import com.liferay.batch.planner.model.impl.BatchPlannerPolicyModelImpl;
import com.liferay.batch.planner.service.persistence.BatchPlannerPolicyPersistence;
import com.liferay.batch.planner.service.persistence.BatchPlannerPolicyUtil;
import com.liferay.batch.planner.service.persistence.impl.constants.BatchPlannerPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the batch planner policy service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Igor Beslic
 * @generated
 */
@Component(service = BatchPlannerPolicyPersistence.class)
public class BatchPlannerPolicyPersistenceImpl
	extends BasePersistenceImpl<BatchPlannerPolicy, NoSuchPolicyException>
	implements BatchPlannerPolicyPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BatchPlannerPolicyUtil</code> to access the batch planner policy persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BatchPlannerPolicyImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<BatchPlannerPolicy, NoSuchPolicyException>
			_collectionPersistenceFinderByBatchPlannerPlanId;

	/**
	 * Returns an ordered range of all the batch planner policies where batchPlannerPlanId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPolicyModelImpl</code>.
	 * </p>
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param start the lower bound of the range of batch planner policies
	 * @param end the upper bound of the range of batch planner policies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner policies
	 */
	@Override
	public List<BatchPlannerPolicy> findByBatchPlannerPlanId(
		long batchPlannerPlanId, int start, int end,
		OrderByComparator<BatchPlannerPolicy> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByBatchPlannerPlanId.find(
			finderCache, new Object[] {batchPlannerPlanId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch planner policy in the ordered set where batchPlannerPlanId = &#63;.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner policy
	 * @throws NoSuchPolicyException if a matching batch planner policy could not be found
	 */
	@Override
	public BatchPlannerPolicy findByBatchPlannerPlanId_First(
			long batchPlannerPlanId,
			OrderByComparator<BatchPlannerPolicy> orderByComparator)
		throws NoSuchPolicyException {

		return _collectionPersistenceFinderByBatchPlannerPlanId.findFirst(
			finderCache, new Object[] {batchPlannerPlanId}, orderByComparator);
	}

	/**
	 * Returns the first batch planner policy in the ordered set where batchPlannerPlanId = &#63;.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner policy, or <code>null</code> if a matching batch planner policy could not be found
	 */
	@Override
	public BatchPlannerPolicy fetchByBatchPlannerPlanId_First(
		long batchPlannerPlanId,
		OrderByComparator<BatchPlannerPolicy> orderByComparator) {

		return _collectionPersistenceFinderByBatchPlannerPlanId.fetchFirst(
			finderCache, new Object[] {batchPlannerPlanId}, orderByComparator);
	}

	/**
	 * Removes all the batch planner policies where batchPlannerPlanId = &#63; from the database.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 */
	@Override
	public void removeByBatchPlannerPlanId(long batchPlannerPlanId) {
		_collectionPersistenceFinderByBatchPlannerPlanId.remove(
			finderCache, new Object[] {batchPlannerPlanId});
	}

	/**
	 * Returns the number of batch planner policies where batchPlannerPlanId = &#63;.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @return the number of matching batch planner policies
	 */
	@Override
	public int countByBatchPlannerPlanId(long batchPlannerPlanId) {
		return _collectionPersistenceFinderByBatchPlannerPlanId.count(
			finderCache, new Object[] {batchPlannerPlanId});
	}

	private UniquePersistenceFinder<BatchPlannerPolicy, NoSuchPolicyException>
		_uniquePersistenceFinderByBPPI_N;

	/**
	 * Returns the batch planner policy where batchPlannerPlanId = &#63; and name = &#63; or throws a <code>NoSuchPolicyException</code> if it could not be found.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param name the name
	 * @return the matching batch planner policy
	 * @throws NoSuchPolicyException if a matching batch planner policy could not be found
	 */
	@Override
	public BatchPlannerPolicy findByBPPI_N(long batchPlannerPlanId, String name)
		throws NoSuchPolicyException {

		return _uniquePersistenceFinderByBPPI_N.find(
			finderCache, new Object[] {batchPlannerPlanId, name});
	}

	/**
	 * Returns the batch planner policy where batchPlannerPlanId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching batch planner policy, or <code>null</code> if a matching batch planner policy could not be found
	 */
	@Override
	public BatchPlannerPolicy fetchByBPPI_N(
		long batchPlannerPlanId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByBPPI_N.fetch(
			finderCache, new Object[] {batchPlannerPlanId, name},
			useFinderCache);
	}

	/**
	 * Removes the batch planner policy where batchPlannerPlanId = &#63; and name = &#63; from the database.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param name the name
	 * @return the batch planner policy that was removed
	 */
	@Override
	public BatchPlannerPolicy removeByBPPI_N(
			long batchPlannerPlanId, String name)
		throws NoSuchPolicyException {

		BatchPlannerPolicy batchPlannerPolicy = findByBPPI_N(
			batchPlannerPlanId, name);

		return remove(batchPlannerPolicy);
	}

	/**
	 * Returns the number of batch planner policies where batchPlannerPlanId = &#63; and name = &#63;.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param name the name
	 * @return the number of matching batch planner policies
	 */
	@Override
	public int countByBPPI_N(long batchPlannerPlanId, String name) {
		return _uniquePersistenceFinderByBPPI_N.count(
			finderCache, new Object[] {batchPlannerPlanId, name});
	}

	public BatchPlannerPolicyPersistenceImpl() {
		setModelClass(BatchPlannerPolicy.class);

		setModelImplClass(BatchPlannerPolicyImpl.class);
		setModelPKClass(long.class);

		setTable(BatchPlannerPolicyTable.INSTANCE);
	}

	/**
	 * Creates a new batch planner policy with the primary key. Does not add the batch planner policy to the database.
	 *
	 * @param batchPlannerPolicyId the primary key for the new batch planner policy
	 * @return the new batch planner policy
	 */
	@Override
	public BatchPlannerPolicy create(long batchPlannerPolicyId) {
		BatchPlannerPolicy batchPlannerPolicy = new BatchPlannerPolicyImpl();

		batchPlannerPolicy.setNew(true);
		batchPlannerPolicy.setPrimaryKey(batchPlannerPolicyId);

		batchPlannerPolicy.setCompanyId(CompanyThreadLocal.getCompanyId());

		return batchPlannerPolicy;
	}

	/**
	 * Removes the batch planner policy with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param batchPlannerPolicyId the primary key of the batch planner policy
	 * @return the batch planner policy that was removed
	 * @throws NoSuchPolicyException if a batch planner policy with the primary key could not be found
	 */
	@Override
	public BatchPlannerPolicy remove(long batchPlannerPolicyId)
		throws NoSuchPolicyException {

		return remove((Serializable)batchPlannerPolicyId);
	}

	@Override
	protected BatchPlannerPolicy removeImpl(
		BatchPlannerPolicy batchPlannerPolicy) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(batchPlannerPolicy)) {
				batchPlannerPolicy = (BatchPlannerPolicy)session.get(
					BatchPlannerPolicyImpl.class,
					batchPlannerPolicy.getPrimaryKeyObj());
			}

			if (batchPlannerPolicy != null) {
				session.delete(batchPlannerPolicy);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (batchPlannerPolicy != null) {
			clearCache(batchPlannerPolicy);
		}

		return batchPlannerPolicy;
	}

	@Override
	public BatchPlannerPolicy updateImpl(
		BatchPlannerPolicy batchPlannerPolicy) {

		boolean isNew = batchPlannerPolicy.isNew();

		if (!(batchPlannerPolicy instanceof BatchPlannerPolicyModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(batchPlannerPolicy.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					batchPlannerPolicy);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in batchPlannerPolicy proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BatchPlannerPolicy implementation " +
					batchPlannerPolicy.getClass());
		}

		BatchPlannerPolicyModelImpl batchPlannerPolicyModelImpl =
			(BatchPlannerPolicyModelImpl)batchPlannerPolicy;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (batchPlannerPolicy.getCreateDate() == null)) {
			if (serviceContext == null) {
				batchPlannerPolicy.setCreateDate(date);
			}
			else {
				batchPlannerPolicy.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!batchPlannerPolicyModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				batchPlannerPolicy.setModifiedDate(date);
			}
			else {
				batchPlannerPolicy.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(batchPlannerPolicy);
			}
			else {
				batchPlannerPolicy = (BatchPlannerPolicy)session.merge(
					batchPlannerPolicy);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(batchPlannerPolicy, false);

		if (isNew) {
			batchPlannerPolicy.setNew(false);
		}

		batchPlannerPolicy.resetOriginalValues();

		return batchPlannerPolicy;
	}

	/**
	 * Returns the batch planner policy with the primary key or throws a <code>NoSuchPolicyException</code> if it could not be found.
	 *
	 * @param batchPlannerPolicyId the primary key of the batch planner policy
	 * @return the batch planner policy
	 * @throws NoSuchPolicyException if a batch planner policy with the primary key could not be found
	 */
	@Override
	public BatchPlannerPolicy findByPrimaryKey(long batchPlannerPolicyId)
		throws NoSuchPolicyException {

		return findByPrimaryKey((Serializable)batchPlannerPolicyId);
	}

	/**
	 * Returns the batch planner policy with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param batchPlannerPolicyId the primary key of the batch planner policy
	 * @return the batch planner policy, or <code>null</code> if a batch planner policy with the primary key could not be found
	 */
	@Override
	public BatchPlannerPolicy fetchByPrimaryKey(long batchPlannerPolicyId) {
		return fetchByPrimaryKey((Serializable)batchPlannerPolicyId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "batchPlannerPolicyId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BATCHPLANNERPOLICY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BatchPlannerPolicyModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the batch planner policy persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByBatchPlannerPlanId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByBatchPlannerPlanId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"batchPlannerPlanId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByBatchPlannerPlanId",
					new String[] {Long.class.getName()},
					new String[] {"batchPlannerPlanId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByBatchPlannerPlanId",
					new String[] {Long.class.getName()},
					new String[] {"batchPlannerPlanId"}, false),
				_SQL_SELECT_BATCHPLANNERPOLICY_WHERE,
				_SQL_COUNT_BATCHPLANNERPOLICY_WHERE,
				BatchPlannerPolicyModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"batchPlannerPolicy.", "batchPlannerPlanId",
					FinderColumn.Type.LONG, "=", true, true,
					BatchPlannerPolicy::getBatchPlannerPlanId));

		_uniquePersistenceFinderByBPPI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByBPPI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"batchPlannerPlanId", "name"}, 0, 2, false,
				BatchPlannerPolicy::getBatchPlannerPlanId,
				convertNullFunction(BatchPlannerPolicy::getName)),
			_SQL_SELECT_BATCHPLANNERPOLICY_WHERE, "",
			new FinderColumn<>(
				"batchPlannerPolicy.", "batchPlannerPlanId",
				FinderColumn.Type.LONG, "=", true, true,
				BatchPlannerPolicy::getBatchPlannerPlanId),
			new FinderColumn<>(
				"batchPlannerPolicy.", "name", FinderColumn.Type.STRING, "=",
				true, true, BatchPlannerPolicy::getName));

		BatchPlannerPolicyUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BatchPlannerPolicyUtil.setPersistence(null);

		entityCache.removeCache(BatchPlannerPolicyImpl.class.getName());
	}

	@Override
	@Reference(
		target = BatchPlannerPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = BatchPlannerPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = BatchPlannerPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		BatchPlannerPolicyModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BATCHPLANNERPOLICY =
		"SELECT batchPlannerPolicy FROM BatchPlannerPolicy batchPlannerPolicy";

	private static final String _SQL_SELECT_BATCHPLANNERPOLICY_WHERE =
		"SELECT batchPlannerPolicy FROM BatchPlannerPolicy batchPlannerPolicy WHERE ";

	private static final String _SQL_COUNT_BATCHPLANNERPOLICY_WHERE =
		"SELECT COUNT(batchPlannerPolicy) FROM BatchPlannerPolicy batchPlannerPolicy WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BatchPlannerPolicy exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BatchPlannerPolicyPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:494665768