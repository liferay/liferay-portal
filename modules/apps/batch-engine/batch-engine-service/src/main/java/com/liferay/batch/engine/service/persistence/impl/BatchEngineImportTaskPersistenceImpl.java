/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.persistence.impl;

import com.liferay.batch.engine.exception.DuplicateBatchEngineImportTaskExternalReferenceCodeException;
import com.liferay.batch.engine.exception.NoSuchImportTaskException;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.BatchEngineImportTaskTable;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskImpl;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskModelImpl;
import com.liferay.batch.engine.service.persistence.BatchEngineImportTaskPersistence;
import com.liferay.batch.engine.service.persistence.BatchEngineImportTaskUtil;
import com.liferay.batch.engine.service.persistence.impl.constants.BatchEnginePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the batch engine import task service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shuyang Zhou
 * @generated
 */
@Component(service = BatchEngineImportTaskPersistence.class)
public class BatchEngineImportTaskPersistenceImpl
	extends BasePersistenceImpl
		<BatchEngineImportTask, NoSuchImportTaskException>
	implements BatchEngineImportTaskPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BatchEngineImportTaskUtil</code> to access the batch engine import task persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BatchEngineImportTaskImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<BatchEngineImportTask, NoSuchImportTaskException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the batch engine import tasks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first batch engine import task in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByUuid_First(
			String uuid,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first batch engine import task in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByUuid_First(
		String uuid,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the batch engine import tasks where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of batch engine import tasks where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<BatchEngineImportTask, NoSuchImportTaskException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the batch engine import tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch engine import task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first batch engine import task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the batch engine import tasks where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of batch engine import tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<BatchEngineImportTask, NoSuchImportTaskException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the batch engine import tasks where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch engine import task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByCompanyId_First(
			long companyId,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first batch engine import task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByCompanyId_First(
		long companyId,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the batch engine import tasks where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of batch engine import tasks where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<BatchEngineImportTask, NoSuchImportTaskException>
			_collectionPersistenceFinderByExecuteStatus;

	/**
	 * Returns an ordered range of all the batch engine import tasks where executeStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param executeStatus the execute status
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine import tasks
	 */
	@Override
	public List<BatchEngineImportTask> findByExecuteStatus(
		String executeStatus, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByExecuteStatus.find(
			finderCache, new Object[] {executeStatus}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch engine import task in the ordered set where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByExecuteStatus_First(
			String executeStatus,
			OrderByComparator<BatchEngineImportTask> orderByComparator)
		throws NoSuchImportTaskException {

		return _collectionPersistenceFinderByExecuteStatus.findFirst(
			finderCache, new Object[] {executeStatus}, orderByComparator);
	}

	/**
	 * Returns the first batch engine import task in the ordered set where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByExecuteStatus_First(
		String executeStatus,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return _collectionPersistenceFinderByExecuteStatus.fetchFirst(
			finderCache, new Object[] {executeStatus}, orderByComparator);
	}

	/**
	 * Removes all the batch engine import tasks where executeStatus = &#63; from the database.
	 *
	 * @param executeStatus the execute status
	 */
	@Override
	public void removeByExecuteStatus(String executeStatus) {
		_collectionPersistenceFinderByExecuteStatus.remove(
			finderCache, new Object[] {executeStatus});
	}

	/**
	 * Returns the number of batch engine import tasks where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByExecuteStatus(String executeStatus) {
		return _collectionPersistenceFinderByExecuteStatus.count(
			finderCache, new Object[] {executeStatus});
	}

	private UniquePersistenceFinder
		<BatchEngineImportTask, NoSuchImportTaskException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the batch engine import task where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchImportTaskException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching batch engine import task
	 * @throws NoSuchImportTaskException if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchImportTaskException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the batch engine import task where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the batch engine import task where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the batch engine import task that was removed
	 */
	@Override
	public BatchEngineImportTask removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchImportTaskException {

		BatchEngineImportTask batchEngineImportTask = findByERC_C(
			externalReferenceCode, companyId);

		return remove(batchEngineImportTask);
	}

	/**
	 * Returns the number of batch engine import tasks where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching batch engine import tasks
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public BatchEngineImportTaskPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BatchEngineImportTask.class);

		setModelImplClass(BatchEngineImportTaskImpl.class);
		setModelPKClass(long.class);

		setTable(BatchEngineImportTaskTable.INSTANCE);
	}

	/**
	 * Creates a new batch engine import task with the primary key. Does not add the batch engine import task to the database.
	 *
	 * @param batchEngineImportTaskId the primary key for the new batch engine import task
	 * @return the new batch engine import task
	 */
	@Override
	public BatchEngineImportTask create(long batchEngineImportTaskId) {
		BatchEngineImportTask batchEngineImportTask =
			new BatchEngineImportTaskImpl();

		batchEngineImportTask.setNew(true);
		batchEngineImportTask.setPrimaryKey(batchEngineImportTaskId);

		String uuid = PortalUUIDUtil.generate();

		batchEngineImportTask.setUuid(uuid);

		batchEngineImportTask.setCompanyId(CompanyThreadLocal.getCompanyId());

		return batchEngineImportTask;
	}

	/**
	 * Removes the batch engine import task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task that was removed
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask remove(long batchEngineImportTaskId)
		throws NoSuchImportTaskException {

		return remove((Serializable)batchEngineImportTaskId);
	}

	@Override
	protected BatchEngineImportTask removeImpl(
		BatchEngineImportTask batchEngineImportTask) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(batchEngineImportTask)) {
				batchEngineImportTask = (BatchEngineImportTask)session.get(
					BatchEngineImportTaskImpl.class,
					batchEngineImportTask.getPrimaryKeyObj());
			}

			if (batchEngineImportTask != null) {
				session.delete(batchEngineImportTask);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (batchEngineImportTask != null) {
			clearCache(batchEngineImportTask);
		}

		return batchEngineImportTask;
	}

	@Override
	public BatchEngineImportTask updateImpl(
		BatchEngineImportTask batchEngineImportTask) {

		boolean isNew = batchEngineImportTask.isNew();

		if (!(batchEngineImportTask instanceof
				BatchEngineImportTaskModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(batchEngineImportTask.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					batchEngineImportTask);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in batchEngineImportTask proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BatchEngineImportTask implementation " +
					batchEngineImportTask.getClass());
		}

		BatchEngineImportTaskModelImpl batchEngineImportTaskModelImpl =
			(BatchEngineImportTaskModelImpl)batchEngineImportTask;

		if (Validator.isNull(batchEngineImportTask.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			batchEngineImportTask.setUuid(uuid);
		}

		if (Validator.isNull(
				batchEngineImportTask.getExternalReferenceCode())) {

			batchEngineImportTask.setExternalReferenceCode(
				batchEngineImportTask.getUuid());
		}
		else {
			if (!Objects.equals(
					batchEngineImportTaskModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					batchEngineImportTask.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = batchEngineImportTask.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = batchEngineImportTask.getPrimaryKey();
					}

					try {
						batchEngineImportTask.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								BatchEngineImportTask.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								batchEngineImportTask.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			BatchEngineImportTask ercBatchEngineImportTask = fetchByERC_C(
				batchEngineImportTask.getExternalReferenceCode(),
				batchEngineImportTask.getCompanyId());

			if (isNew) {
				if (ercBatchEngineImportTask != null) {
					throw new DuplicateBatchEngineImportTaskExternalReferenceCodeException(
						"Duplicate batch engine import task with external reference code " +
							batchEngineImportTask.getExternalReferenceCode() +
								" and company " +
									batchEngineImportTask.getCompanyId());
				}
			}
			else {
				if ((ercBatchEngineImportTask != null) &&
					(batchEngineImportTask.getBatchEngineImportTaskId() !=
						ercBatchEngineImportTask.
							getBatchEngineImportTaskId())) {

					throw new DuplicateBatchEngineImportTaskExternalReferenceCodeException(
						"Duplicate batch engine import task with external reference code " +
							batchEngineImportTask.getExternalReferenceCode() +
								" and company " +
									batchEngineImportTask.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (batchEngineImportTask.getCreateDate() == null)) {
			if (serviceContext == null) {
				batchEngineImportTask.setCreateDate(date);
			}
			else {
				batchEngineImportTask.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!batchEngineImportTaskModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				batchEngineImportTask.setModifiedDate(date);
			}
			else {
				batchEngineImportTask.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(batchEngineImportTask);
			}
			else {
				batchEngineImportTask = (BatchEngineImportTask)session.merge(
					batchEngineImportTask);
			}

			session.flush();
			session.clear();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(batchEngineImportTask, false);

		if (isNew) {
			batchEngineImportTask.setNew(false);
		}

		batchEngineImportTask.resetOriginalValues();

		return batchEngineImportTask;
	}

	/**
	 * Returns the batch engine import task with the primary key or throws a <code>NoSuchImportTaskException</code> if it could not be found.
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task
	 * @throws NoSuchImportTaskException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask findByPrimaryKey(long batchEngineImportTaskId)
		throws NoSuchImportTaskException {

		return findByPrimaryKey((Serializable)batchEngineImportTaskId);
	}

	/**
	 * Returns the batch engine import task with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task, or <code>null</code> if a batch engine import task with the primary key could not be found
	 */
	@Override
	public BatchEngineImportTask fetchByPrimaryKey(
		long batchEngineImportTaskId) {

		return fetchByPrimaryKey((Serializable)batchEngineImportTaskId);
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
		return "batchEngineImportTaskId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BATCHENGINEIMPORTTASK;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BatchEngineImportTaskModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the batch engine import task persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE,
			_SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE,
			BatchEngineImportTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"batchEngineImportTask.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				BatchEngineImportTask::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE,
				_SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE,
				BatchEngineImportTaskModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"batchEngineImportTask.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					BatchEngineImportTask::getUuid),
				new FinderColumn<>(
					"batchEngineImportTask.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					BatchEngineImportTask::getCompanyId));

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
				_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE,
				_SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE,
				BatchEngineImportTaskModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"batchEngineImportTask.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					BatchEngineImportTask::getCompanyId));

		_collectionPersistenceFinderByExecuteStatus =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByExecuteStatus",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"executeStatus"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByExecuteStatus",
					new String[] {String.class.getName()},
					new String[] {"executeStatus"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByExecuteStatus",
					new String[] {String.class.getName()},
					new String[] {"executeStatus"}, 0, 1, false, null),
				_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE,
				_SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE,
				BatchEngineImportTaskModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"batchEngineImportTask.", "executeStatus",
					FinderColumn.Type.STRING, "=", true, true,
					BatchEngineImportTask::getExecuteStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					BatchEngineImportTask::getExternalReferenceCode),
				BatchEngineImportTask::getCompanyId),
			_SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE, "",
			new FinderColumn<>(
				"batchEngineImportTask.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				BatchEngineImportTask::getExternalReferenceCode),
			new FinderColumn<>(
				"batchEngineImportTask.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, BatchEngineImportTask::getCompanyId));

		BatchEngineImportTaskUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BatchEngineImportTaskUtil.setPersistence(null);

		entityCache.removeCache(BatchEngineImportTaskImpl.class.getName());
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
		BatchEngineImportTaskModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BATCHENGINEIMPORTTASK =
		"SELECT batchEngineImportTask FROM BatchEngineImportTask batchEngineImportTask";

	private static final String _SQL_SELECT_BATCHENGINEIMPORTTASK_WHERE =
		"SELECT batchEngineImportTask FROM BatchEngineImportTask batchEngineImportTask WHERE ";

	private static final String _SQL_COUNT_BATCHENGINEIMPORTTASK_WHERE =
		"SELECT COUNT(batchEngineImportTask) FROM BatchEngineImportTask batchEngineImportTask WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BatchEngineImportTask exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BatchEngineImportTaskPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1151582670