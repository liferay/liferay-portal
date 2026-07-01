/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.persistence.impl;

import com.liferay.batch.engine.exception.NoSuchImportTaskErrorException;
import com.liferay.batch.engine.model.BatchEngineImportTaskError;
import com.liferay.batch.engine.model.BatchEngineImportTaskErrorTable;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskErrorImpl;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskErrorModelImpl;
import com.liferay.batch.engine.service.persistence.BatchEngineImportTaskErrorPersistence;
import com.liferay.batch.engine.service.persistence.BatchEngineImportTaskErrorUtil;
import com.liferay.batch.engine.service.persistence.impl.constants.BatchEnginePersistenceConstants;
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
 * The persistence implementation for the batch engine import task error service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shuyang Zhou
 * @generated
 */
@Component(service = BatchEngineImportTaskErrorPersistence.class)
public class BatchEngineImportTaskErrorPersistenceImpl
	extends BasePersistenceImpl
		<BatchEngineImportTaskError, NoSuchImportTaskErrorException>
	implements BatchEngineImportTaskErrorPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BatchEngineImportTaskErrorUtil</code> to access the batch engine import task error persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BatchEngineImportTaskErrorImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<BatchEngineImportTaskError, NoSuchImportTaskErrorException>
			_collectionPersistenceFinderByBatchEngineImportTaskId;

	/**
	 * Returns an ordered range of all the batch engine import task errors where batchEngineImportTaskId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskErrorModelImpl</code>.
	 * </p>
	 *
	 * @param batchEngineImportTaskId the batch engine import task ID
	 * @param start the lower bound of the range of batch engine import task errors
	 * @param end the upper bound of the range of batch engine import task errors (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import task errors
	 */
	@Override
	public List<BatchEngineImportTaskError> findByBatchEngineImportTaskId(
		long batchEngineImportTaskId, int start, int end,
		OrderByComparator<BatchEngineImportTaskError> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByBatchEngineImportTaskId.find(
			finderCache, new Object[] {batchEngineImportTaskId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch engine import task error in the ordered set where batchEngineImportTaskId = &#63;.
	 *
	 * @param batchEngineImportTaskId the batch engine import task ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task error
	 * @throws NoSuchImportTaskErrorException if a matching batch engine import task error could not be found
	 */
	@Override
	public BatchEngineImportTaskError findByBatchEngineImportTaskId_First(
			long batchEngineImportTaskId,
			OrderByComparator<BatchEngineImportTaskError> orderByComparator)
		throws NoSuchImportTaskErrorException {

		return _collectionPersistenceFinderByBatchEngineImportTaskId.findFirst(
			finderCache, new Object[] {batchEngineImportTaskId},
			orderByComparator);
	}

	/**
	 * Returns the first batch engine import task error in the ordered set where batchEngineImportTaskId = &#63;.
	 *
	 * @param batchEngineImportTaskId the batch engine import task ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task error, or <code>null</code> if a matching batch engine import task error could not be found
	 */
	@Override
	public BatchEngineImportTaskError fetchByBatchEngineImportTaskId_First(
		long batchEngineImportTaskId,
		OrderByComparator<BatchEngineImportTaskError> orderByComparator) {

		return _collectionPersistenceFinderByBatchEngineImportTaskId.fetchFirst(
			finderCache, new Object[] {batchEngineImportTaskId},
			orderByComparator);
	}

	/**
	 * Removes all the batch engine import task errors where batchEngineImportTaskId = &#63; from the database.
	 *
	 * @param batchEngineImportTaskId the batch engine import task ID
	 */
	@Override
	public void removeByBatchEngineImportTaskId(long batchEngineImportTaskId) {
		_collectionPersistenceFinderByBatchEngineImportTaskId.remove(
			finderCache, new Object[] {batchEngineImportTaskId});
	}

	/**
	 * Returns the number of batch engine import task errors where batchEngineImportTaskId = &#63;.
	 *
	 * @param batchEngineImportTaskId the batch engine import task ID
	 * @return the number of matching batch engine import task errors
	 */
	@Override
	public int countByBatchEngineImportTaskId(long batchEngineImportTaskId) {
		return _collectionPersistenceFinderByBatchEngineImportTaskId.count(
			finderCache, new Object[] {batchEngineImportTaskId});
	}

	public BatchEngineImportTaskErrorPersistenceImpl() {
		setModelClass(BatchEngineImportTaskError.class);

		setModelImplClass(BatchEngineImportTaskErrorImpl.class);
		setModelPKClass(long.class);

		setTable(BatchEngineImportTaskErrorTable.INSTANCE);
	}

	/**
	 * Creates a new batch engine import task error with the primary key. Does not add the batch engine import task error to the database.
	 *
	 * @param batchEngineImportTaskErrorId the primary key for the new batch engine import task error
	 * @return the new batch engine import task error
	 */
	@Override
	public BatchEngineImportTaskError create(
		long batchEngineImportTaskErrorId) {

		BatchEngineImportTaskError batchEngineImportTaskError =
			new BatchEngineImportTaskErrorImpl();

		batchEngineImportTaskError.setNew(true);
		batchEngineImportTaskError.setPrimaryKey(batchEngineImportTaskErrorId);

		batchEngineImportTaskError.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return batchEngineImportTaskError;
	}

	/**
	 * Removes the batch engine import task error with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param batchEngineImportTaskErrorId the primary key of the batch engine import task error
	 * @return the batch engine import task error that was removed
	 * @throws NoSuchImportTaskErrorException if a batch engine import task error with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTaskError remove(long batchEngineImportTaskErrorId)
		throws NoSuchImportTaskErrorException {

		return remove((Serializable)batchEngineImportTaskErrorId);
	}

	@Override
	protected BatchEngineImportTaskError removeImpl(
		BatchEngineImportTaskError batchEngineImportTaskError) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(batchEngineImportTaskError)) {
				batchEngineImportTaskError =
					(BatchEngineImportTaskError)session.get(
						BatchEngineImportTaskErrorImpl.class,
						batchEngineImportTaskError.getPrimaryKeyObj());
			}

			if (batchEngineImportTaskError != null) {
				session.delete(batchEngineImportTaskError);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (batchEngineImportTaskError != null) {
			clearCache(batchEngineImportTaskError);
		}

		return batchEngineImportTaskError;
	}

	@Override
	public BatchEngineImportTaskError updateImpl(
		BatchEngineImportTaskError batchEngineImportTaskError) {

		boolean isNew = batchEngineImportTaskError.isNew();

		if (!(batchEngineImportTaskError instanceof
				BatchEngineImportTaskErrorModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(batchEngineImportTaskError.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					batchEngineImportTaskError);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in batchEngineImportTaskError proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BatchEngineImportTaskError implementation " +
					batchEngineImportTaskError.getClass());
		}

		BatchEngineImportTaskErrorModelImpl
			batchEngineImportTaskErrorModelImpl =
				(BatchEngineImportTaskErrorModelImpl)batchEngineImportTaskError;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (batchEngineImportTaskError.getCreateDate() == null)) {
			if (serviceContext == null) {
				batchEngineImportTaskError.setCreateDate(date);
			}
			else {
				batchEngineImportTaskError.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!batchEngineImportTaskErrorModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				batchEngineImportTaskError.setModifiedDate(date);
			}
			else {
				batchEngineImportTaskError.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(batchEngineImportTaskError);
			}
			else {
				batchEngineImportTaskError =
					(BatchEngineImportTaskError)session.merge(
						batchEngineImportTaskError);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(batchEngineImportTaskError, false);

		if (isNew) {
			batchEngineImportTaskError.setNew(false);
		}

		batchEngineImportTaskError.resetOriginalValues();

		return batchEngineImportTaskError;
	}

	/**
	 * Returns the batch engine import task error with the primary key or throws a <code>NoSuchImportTaskErrorException</code> if it could not be found.
	 *
	 * @param batchEngineImportTaskErrorId the primary key of the batch engine import task error
	 * @return the batch engine import task error
	 * @throws NoSuchImportTaskErrorException if a batch engine import task error with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTaskError findByPrimaryKey(
			long batchEngineImportTaskErrorId)
		throws NoSuchImportTaskErrorException {

		return findByPrimaryKey((Serializable)batchEngineImportTaskErrorId);
	}

	/**
	 * Returns the batch engine import task error with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param batchEngineImportTaskErrorId the primary key of the batch engine import task error
	 * @return the batch engine import task error, or <code>null</code> if a batch engine import task error with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTaskError fetchByPrimaryKey(
		long batchEngineImportTaskErrorId) {

		return fetchByPrimaryKey((Serializable)batchEngineImportTaskErrorId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "batchEngineImportTaskErrorId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BATCHENGINEIMPORTTASKERROR;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BatchEngineImportTaskErrorModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the batch engine import task error persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByBatchEngineImportTaskId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByBatchEngineImportTaskId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"batchEngineImportTaskId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByBatchEngineImportTaskId",
					new String[] {Long.class.getName()},
					new String[] {"batchEngineImportTaskId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByBatchEngineImportTaskId",
					new String[] {Long.class.getName()},
					new String[] {"batchEngineImportTaskId"}, false),
				_SQL_SELECT_BATCHENGINEIMPORTTASKERROR_WHERE,
				_SQL_COUNT_BATCHENGINEIMPORTTASKERROR_WHERE,
				BatchEngineImportTaskErrorModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"batchEngineImportTaskError.", "batchEngineImportTaskId",
					FinderColumn.Type.LONG, "=", true, true,
					BatchEngineImportTaskError::getBatchEngineImportTaskId));

		BatchEngineImportTaskErrorUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BatchEngineImportTaskErrorUtil.setPersistence(null);

		entityCache.removeCache(BatchEngineImportTaskErrorImpl.class.getName());
	}

	@Override
	@Reference(
		target = BatchEnginePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = BatchEnginePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = BatchEnginePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		BatchEngineImportTaskErrorModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BATCHENGINEIMPORTTASKERROR =
		"SELECT batchEngineImportTaskError FROM BatchEngineImportTaskError batchEngineImportTaskError";

	private static final String _SQL_SELECT_BATCHENGINEIMPORTTASKERROR_WHERE =
		"SELECT batchEngineImportTaskError FROM BatchEngineImportTaskError batchEngineImportTaskError WHERE ";

	private static final String _SQL_COUNT_BATCHENGINEIMPORTTASKERROR_WHERE =
		"SELECT COUNT(batchEngineImportTaskError) FROM BatchEngineImportTaskError batchEngineImportTaskError WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-820433925