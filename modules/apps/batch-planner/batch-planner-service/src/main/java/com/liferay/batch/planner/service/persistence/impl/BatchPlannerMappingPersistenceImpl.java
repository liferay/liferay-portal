/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.service.persistence.impl;

import com.liferay.batch.planner.exception.NoSuchMappingException;
import com.liferay.batch.planner.model.BatchPlannerMapping;
import com.liferay.batch.planner.model.BatchPlannerMappingTable;
import com.liferay.batch.planner.model.impl.BatchPlannerMappingImpl;
import com.liferay.batch.planner.model.impl.BatchPlannerMappingModelImpl;
import com.liferay.batch.planner.service.persistence.BatchPlannerMappingPersistence;
import com.liferay.batch.planner.service.persistence.BatchPlannerMappingUtil;
import com.liferay.batch.planner.service.persistence.impl.constants.BatchPlannerPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the batch planner mapping service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Igor Beslic
 * @generated
 */
@Component(service = BatchPlannerMappingPersistence.class)
public class BatchPlannerMappingPersistenceImpl
	extends BasePersistenceImpl<BatchPlannerMapping, NoSuchMappingException>
	implements BatchPlannerMappingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BatchPlannerMappingUtil</code> to access the batch planner mapping persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BatchPlannerMappingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<BatchPlannerMapping, NoSuchMappingException>
			_collectionPersistenceFinderByBatchPlannerPlanId;

	/**
	 * Returns an ordered range of all the batch planner mappings where batchPlannerPlanId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerMappingModelImpl</code>.
	 * </p>
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param start the lower bound of the range of batch planner mappings
	 * @param end the upper bound of the range of batch planner mappings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner mappings
	 */
	@Override
	public List<BatchPlannerMapping> findByBatchPlannerPlanId(
		long batchPlannerPlanId, int start, int end,
		OrderByComparator<BatchPlannerMapping> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByBatchPlannerPlanId.find(
			finderCache, new Object[] {batchPlannerPlanId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch planner mapping in the ordered set where batchPlannerPlanId = &#63;.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner mapping
	 * @throws NoSuchMappingException if a matching batch planner mapping could not be found
	 */
	@Override
	public BatchPlannerMapping findByBatchPlannerPlanId_First(
			long batchPlannerPlanId,
			OrderByComparator<BatchPlannerMapping> orderByComparator)
		throws NoSuchMappingException {

		return _collectionPersistenceFinderByBatchPlannerPlanId.findFirst(
			finderCache, new Object[] {batchPlannerPlanId}, orderByComparator);
	}

	/**
	 * Returns the first batch planner mapping in the ordered set where batchPlannerPlanId = &#63;.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner mapping, or <code>null</code> if a matching batch planner mapping could not be found
	 */
	@Override
	public BatchPlannerMapping fetchByBatchPlannerPlanId_First(
		long batchPlannerPlanId,
		OrderByComparator<BatchPlannerMapping> orderByComparator) {

		return _collectionPersistenceFinderByBatchPlannerPlanId.fetchFirst(
			finderCache, new Object[] {batchPlannerPlanId}, orderByComparator);
	}

	/**
	 * Removes all the batch planner mappings where batchPlannerPlanId = &#63; from the database.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 */
	@Override
	public void removeByBatchPlannerPlanId(long batchPlannerPlanId) {
		_collectionPersistenceFinderByBatchPlannerPlanId.remove(
			finderCache, new Object[] {batchPlannerPlanId});
	}

	/**
	 * Returns the number of batch planner mappings where batchPlannerPlanId = &#63;.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @return the number of matching batch planner mappings
	 */
	@Override
	public int countByBatchPlannerPlanId(long batchPlannerPlanId) {
		return _collectionPersistenceFinderByBatchPlannerPlanId.count(
			finderCache, new Object[] {batchPlannerPlanId});
	}

	private UniquePersistenceFinder<BatchPlannerMapping, NoSuchMappingException>
		_uniquePersistenceFinderByBPPI_EFN_IFN;

	/**
	 * Returns the batch planner mapping where batchPlannerPlanId = &#63; and externalFieldName = &#63; and internalFieldName = &#63; or throws a <code>NoSuchMappingException</code> if it could not be found.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param externalFieldName the external field name
	 * @param internalFieldName the internal field name
	 * @return the matching batch planner mapping
	 * @throws NoSuchMappingException if a matching batch planner mapping could not be found
	 */
	@Override
	public BatchPlannerMapping findByBPPI_EFN_IFN(
			long batchPlannerPlanId, String externalFieldName,
			String internalFieldName)
		throws NoSuchMappingException {

		return _uniquePersistenceFinderByBPPI_EFN_IFN.find(
			finderCache,
			new Object[] {
				batchPlannerPlanId, externalFieldName, internalFieldName
			});
	}

	/**
	 * Returns the batch planner mapping where batchPlannerPlanId = &#63; and externalFieldName = &#63; and internalFieldName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param externalFieldName the external field name
	 * @param internalFieldName the internal field name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching batch planner mapping, or <code>null</code> if a matching batch planner mapping could not be found
	 */
	@Override
	public BatchPlannerMapping fetchByBPPI_EFN_IFN(
		long batchPlannerPlanId, String externalFieldName,
		String internalFieldName, boolean useFinderCache) {

		return _uniquePersistenceFinderByBPPI_EFN_IFN.fetch(
			finderCache,
			new Object[] {
				batchPlannerPlanId, externalFieldName, internalFieldName
			},
			useFinderCache);
	}

	/**
	 * Removes the batch planner mapping where batchPlannerPlanId = &#63; and externalFieldName = &#63; and internalFieldName = &#63; from the database.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param externalFieldName the external field name
	 * @param internalFieldName the internal field name
	 * @return the batch planner mapping that was removed
	 */
	@Override
	public BatchPlannerMapping removeByBPPI_EFN_IFN(
			long batchPlannerPlanId, String externalFieldName,
			String internalFieldName)
		throws NoSuchMappingException {

		BatchPlannerMapping batchPlannerMapping = findByBPPI_EFN_IFN(
			batchPlannerPlanId, externalFieldName, internalFieldName);

		return remove(batchPlannerMapping);
	}

	/**
	 * Returns the number of batch planner mappings where batchPlannerPlanId = &#63; and externalFieldName = &#63; and internalFieldName = &#63;.
	 *
	 * @param batchPlannerPlanId the batch planner plan ID
	 * @param externalFieldName the external field name
	 * @param internalFieldName the internal field name
	 * @return the number of matching batch planner mappings
	 */
	@Override
	public int countByBPPI_EFN_IFN(
		long batchPlannerPlanId, String externalFieldName,
		String internalFieldName) {

		return _uniquePersistenceFinderByBPPI_EFN_IFN.count(
			finderCache,
			new Object[] {
				batchPlannerPlanId, externalFieldName, internalFieldName
			});
	}

	public BatchPlannerMappingPersistenceImpl() {
		setModelClass(BatchPlannerMapping.class);

		setModelImplClass(BatchPlannerMappingImpl.class);
		setModelPKClass(long.class);

		setTable(BatchPlannerMappingTable.INSTANCE);
	}

	/**
	 * Creates a new batch planner mapping with the primary key. Does not add the batch planner mapping to the database.
	 *
	 * @param batchPlannerMappingId the primary key for the new batch planner mapping
	 * @return the new batch planner mapping
	 */
	@Override
	public BatchPlannerMapping create(long batchPlannerMappingId) {
		BatchPlannerMapping batchPlannerMapping = new BatchPlannerMappingImpl();

		batchPlannerMapping.setNew(true);
		batchPlannerMapping.setPrimaryKey(batchPlannerMappingId);

		batchPlannerMapping.setCompanyId(CompanyThreadLocal.getCompanyId());

		return batchPlannerMapping;
	}

	/**
	 * Removes the batch planner mapping with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param batchPlannerMappingId the primary key of the batch planner mapping
	 * @return the batch planner mapping that was removed
	 * @throws NoSuchMappingException if a batch planner mapping with the primary key could not be found
	 */
	@Override
	public BatchPlannerMapping remove(long batchPlannerMappingId)
		throws NoSuchMappingException {

		return remove((Serializable)batchPlannerMappingId);
	}

	@Override
	protected BatchPlannerMapping removeImpl(
		BatchPlannerMapping batchPlannerMapping) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(batchPlannerMapping)) {
				batchPlannerMapping = (BatchPlannerMapping)session.get(
					BatchPlannerMappingImpl.class,
					batchPlannerMapping.getPrimaryKeyObj());
			}

			if (batchPlannerMapping != null) {
				session.delete(batchPlannerMapping);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (batchPlannerMapping != null) {
			clearCache(batchPlannerMapping);
		}

		return batchPlannerMapping;
	}

	@Override
	public BatchPlannerMapping updateImpl(
		BatchPlannerMapping batchPlannerMapping) {

		boolean isNew = batchPlannerMapping.isNew();

		if (!(batchPlannerMapping instanceof BatchPlannerMappingModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(batchPlannerMapping.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					batchPlannerMapping);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in batchPlannerMapping proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BatchPlannerMapping implementation " +
					batchPlannerMapping.getClass());
		}

		BatchPlannerMappingModelImpl batchPlannerMappingModelImpl =
			(BatchPlannerMappingModelImpl)batchPlannerMapping;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (batchPlannerMapping.getCreateDate() == null)) {
			if (serviceContext == null) {
				batchPlannerMapping.setCreateDate(date);
			}
			else {
				batchPlannerMapping.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!batchPlannerMappingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				batchPlannerMapping.setModifiedDate(date);
			}
			else {
				batchPlannerMapping.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(batchPlannerMapping);
			}
			else {
				batchPlannerMapping = (BatchPlannerMapping)session.merge(
					batchPlannerMapping);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(batchPlannerMapping, false);

		if (isNew) {
			batchPlannerMapping.setNew(false);
		}

		batchPlannerMapping.resetOriginalValues();

		return batchPlannerMapping;
	}

	/**
	 * Returns the batch planner mapping with the primary key or throws a <code>NoSuchMappingException</code> if it could not be found.
	 *
	 * @param batchPlannerMappingId the primary key of the batch planner mapping
	 * @return the batch planner mapping
	 * @throws NoSuchMappingException if a batch planner mapping with the primary key could not be found
	 */
	@Override
	public BatchPlannerMapping findByPrimaryKey(long batchPlannerMappingId)
		throws NoSuchMappingException {

		return findByPrimaryKey((Serializable)batchPlannerMappingId);
	}

	/**
	 * Returns the batch planner mapping with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param batchPlannerMappingId the primary key of the batch planner mapping
	 * @return the batch planner mapping, or <code>null</code> if a batch planner mapping with the primary key could not be found
	 */
	@Override
	public BatchPlannerMapping fetchByPrimaryKey(long batchPlannerMappingId) {
		return fetchByPrimaryKey((Serializable)batchPlannerMappingId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "batchPlannerMappingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BATCHPLANNERMAPPING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BatchPlannerMappingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the batch planner mapping persistence.
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
				_SQL_SELECT_BATCHPLANNERMAPPING_WHERE,
				_SQL_COUNT_BATCHPLANNERMAPPING_WHERE,
				BatchPlannerMappingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"batchPlannerMapping.", "batchPlannerPlanId",
					FinderColumn.Type.LONG, "=", true, true,
					BatchPlannerMapping::getBatchPlannerPlanId));

		_uniquePersistenceFinderByBPPI_EFN_IFN = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByBPPI_EFN_IFN",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"batchPlannerPlanId", "externalFieldName",
					"internalFieldName"
				},
				0, 6, false, BatchPlannerMapping::getBatchPlannerPlanId,
				convertNullFunction(BatchPlannerMapping::getExternalFieldName),
				convertNullFunction(BatchPlannerMapping::getInternalFieldName)),
			_SQL_SELECT_BATCHPLANNERMAPPING_WHERE, "",
			new FinderColumn<>(
				"batchPlannerMapping.", "batchPlannerPlanId",
				FinderColumn.Type.LONG, "=", true, true,
				BatchPlannerMapping::getBatchPlannerPlanId),
			new FinderColumn<>(
				"batchPlannerMapping.", "externalFieldName",
				FinderColumn.Type.STRING, "=", true, true,
				BatchPlannerMapping::getExternalFieldName),
			new FinderColumn<>(
				"batchPlannerMapping.", "internalFieldName",
				FinderColumn.Type.STRING, "=", true, true,
				BatchPlannerMapping::getInternalFieldName));

		BatchPlannerMappingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BatchPlannerMappingUtil.setPersistence(null);

		entityCache.removeCache(BatchPlannerMappingImpl.class.getName());
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
		BatchPlannerMappingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BATCHPLANNERMAPPING =
		"SELECT batchPlannerMapping FROM BatchPlannerMapping batchPlannerMapping";

	private static final String _SQL_SELECT_BATCHPLANNERMAPPING_WHERE =
		"SELECT batchPlannerMapping FROM BatchPlannerMapping batchPlannerMapping WHERE ";

	private static final String _SQL_COUNT_BATCHPLANNERMAPPING_WHERE =
		"SELECT COUNT(batchPlannerMapping) FROM BatchPlannerMapping batchPlannerMapping WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-173039725