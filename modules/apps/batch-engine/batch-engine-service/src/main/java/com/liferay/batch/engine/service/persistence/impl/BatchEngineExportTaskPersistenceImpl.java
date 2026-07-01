/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.persistence.impl;

import com.liferay.batch.engine.exception.DuplicateBatchEngineExportTaskExternalReferenceCodeException;
import com.liferay.batch.engine.exception.NoSuchExportTaskException;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineExportTaskTable;
import com.liferay.batch.engine.model.impl.BatchEngineExportTaskImpl;
import com.liferay.batch.engine.model.impl.BatchEngineExportTaskModelImpl;
import com.liferay.batch.engine.service.persistence.BatchEngineExportTaskPersistence;
import com.liferay.batch.engine.service.persistence.BatchEngineExportTaskUtil;
import com.liferay.batch.engine.service.persistence.impl.constants.BatchEnginePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
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
 * The persistence implementation for the batch engine export task service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shuyang Zhou
 * @generated
 */
@Component(service = BatchEngineExportTaskPersistence.class)
public class BatchEngineExportTaskPersistenceImpl
	extends BasePersistenceImpl
		<BatchEngineExportTask, NoSuchExportTaskException>
	implements BatchEngineExportTaskPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BatchEngineExportTaskUtil</code> to access the batch engine export task persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BatchEngineExportTaskImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<BatchEngineExportTask, NoSuchExportTaskException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the batch engine export tasks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineExportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of batch engine export tasks
	 * @param end the upper bound of the range of batch engine export tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine export tasks
	 */
	@Override
	public List<BatchEngineExportTask> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BatchEngineExportTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first batch engine export task in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine export task
	 * @throws NoSuchExportTaskException if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask findByUuid_First(
			String uuid,
			OrderByComparator<BatchEngineExportTask> orderByComparator)
		throws NoSuchExportTaskException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first batch engine export task in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine export task, or <code>null</code> if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask fetchByUuid_First(
		String uuid,
		OrderByComparator<BatchEngineExportTask> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the batch engine export tasks where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of batch engine export tasks where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching batch engine export tasks
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<BatchEngineExportTask, NoSuchExportTaskException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the batch engine export tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineExportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine export tasks
	 * @param end the upper bound of the range of batch engine export tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine export tasks
	 */
	@Override
	public List<BatchEngineExportTask> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BatchEngineExportTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch engine export task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine export task
	 * @throws NoSuchExportTaskException if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<BatchEngineExportTask> orderByComparator)
		throws NoSuchExportTaskException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first batch engine export task in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine export task, or <code>null</code> if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<BatchEngineExportTask> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the batch engine export tasks where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of batch engine export tasks where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching batch engine export tasks
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<BatchEngineExportTask, NoSuchExportTaskException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the batch engine export tasks where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineExportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch engine export tasks
	 * @param end the upper bound of the range of batch engine export tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine export tasks
	 */
	@Override
	public List<BatchEngineExportTask> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineExportTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch engine export task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine export task
	 * @throws NoSuchExportTaskException if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask findByCompanyId_First(
			long companyId,
			OrderByComparator<BatchEngineExportTask> orderByComparator)
		throws NoSuchExportTaskException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first batch engine export task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine export task, or <code>null</code> if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask fetchByCompanyId_First(
		long companyId,
		OrderByComparator<BatchEngineExportTask> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the batch engine export tasks where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of batch engine export tasks where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch engine export tasks
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<BatchEngineExportTask, NoSuchExportTaskException>
			_collectionPersistenceFinderByExecuteStatus;

	/**
	 * Returns an ordered range of all the batch engine export tasks where executeStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchEngineExportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param executeStatus the execute status
	 * @param start the lower bound of the range of batch engine export tasks
	 * @param end the upper bound of the range of batch engine export tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch engine export tasks
	 */
	@Override
	public List<BatchEngineExportTask> findByExecuteStatus(
		String executeStatus, int start, int end,
		OrderByComparator<BatchEngineExportTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByExecuteStatus.find(
			finderCache, new Object[] {executeStatus}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch engine export task in the ordered set where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine export task
	 * @throws NoSuchExportTaskException if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask findByExecuteStatus_First(
			String executeStatus,
			OrderByComparator<BatchEngineExportTask> orderByComparator)
		throws NoSuchExportTaskException {

		return _collectionPersistenceFinderByExecuteStatus.findFirst(
			finderCache, new Object[] {executeStatus}, orderByComparator);
	}

	/**
	 * Returns the first batch engine export task in the ordered set where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch engine export task, or <code>null</code> if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask fetchByExecuteStatus_First(
		String executeStatus,
		OrderByComparator<BatchEngineExportTask> orderByComparator) {

		return _collectionPersistenceFinderByExecuteStatus.fetchFirst(
			finderCache, new Object[] {executeStatus}, orderByComparator);
	}

	/**
	 * Removes all the batch engine export tasks where executeStatus = &#63; from the database.
	 *
	 * @param executeStatus the execute status
	 */
	@Override
	public void removeByExecuteStatus(String executeStatus) {
		_collectionPersistenceFinderByExecuteStatus.remove(
			finderCache, new Object[] {executeStatus});
	}

	/**
	 * Returns the number of batch engine export tasks where executeStatus = &#63;.
	 *
	 * @param executeStatus the execute status
	 * @return the number of matching batch engine export tasks
	 */
	@Override
	public int countByExecuteStatus(String executeStatus) {
		return _collectionPersistenceFinderByExecuteStatus.count(
			finderCache, new Object[] {executeStatus});
	}

	private UniquePersistenceFinder
		<BatchEngineExportTask, NoSuchExportTaskException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the batch engine export task where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchExportTaskException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching batch engine export task
	 * @throws NoSuchExportTaskException if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchExportTaskException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the batch engine export task where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching batch engine export task, or <code>null</code> if a matching batch engine export task could not be found
	 */
	@Override
	public BatchEngineExportTask fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the batch engine export task where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the batch engine export task that was removed
	 */
	@Override
	public BatchEngineExportTask removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchExportTaskException {

		BatchEngineExportTask batchEngineExportTask = findByERC_C(
			externalReferenceCode, companyId);

		return remove(batchEngineExportTask);
	}

	/**
	 * Returns the number of batch engine export tasks where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching batch engine export tasks
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public BatchEngineExportTaskPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BatchEngineExportTask.class);

		setModelImplClass(BatchEngineExportTaskImpl.class);
		setModelPKClass(long.class);

		setTable(BatchEngineExportTaskTable.INSTANCE);
	}

	/**
	 * Creates a new batch engine export task with the primary key. Does not add the batch engine export task to the database.
	 *
	 * @param batchEngineExportTaskId the primary key for the new batch engine export task
	 * @return the new batch engine export task
	 */
	@Override
	public BatchEngineExportTask create(long batchEngineExportTaskId) {
		BatchEngineExportTask batchEngineExportTask =
			new BatchEngineExportTaskImpl();

		batchEngineExportTask.setNew(true);
		batchEngineExportTask.setPrimaryKey(batchEngineExportTaskId);

		String uuid = PortalUUIDUtil.generate();

		batchEngineExportTask.setUuid(uuid);

		batchEngineExportTask.setCompanyId(CompanyThreadLocal.getCompanyId());

		return batchEngineExportTask;
	}

	/**
	 * Removes the batch engine export task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param batchEngineExportTaskId the primary key of the batch engine export task
	 * @return the batch engine export task that was removed
	 * @throws NoSuchExportTaskException if a batch engine export task with the primary key could not be found
	 */
	@Override
	public BatchEngineExportTask remove(long batchEngineExportTaskId)
		throws NoSuchExportTaskException {

		return remove((Serializable)batchEngineExportTaskId);
	}

	@Override
	protected BatchEngineExportTask removeImpl(
		BatchEngineExportTask batchEngineExportTask) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(batchEngineExportTask)) {
				batchEngineExportTask = (BatchEngineExportTask)session.get(
					BatchEngineExportTaskImpl.class,
					batchEngineExportTask.getPrimaryKeyObj());
			}

			if (batchEngineExportTask != null) {
				session.delete(batchEngineExportTask);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (batchEngineExportTask != null) {
			clearCache(batchEngineExportTask);
		}

		return batchEngineExportTask;
	}

	@Override
	public BatchEngineExportTask updateImpl(
		BatchEngineExportTask batchEngineExportTask) {

		boolean isNew = batchEngineExportTask.isNew();

		if (!(batchEngineExportTask instanceof
				BatchEngineExportTaskModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(batchEngineExportTask.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					batchEngineExportTask);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in batchEngineExportTask proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BatchEngineExportTask implementation " +
					batchEngineExportTask.getClass());
		}

		BatchEngineExportTaskModelImpl batchEngineExportTaskModelImpl =
			(BatchEngineExportTaskModelImpl)batchEngineExportTask;

		if (Validator.isNull(batchEngineExportTask.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			batchEngineExportTask.setUuid(uuid);
		}

		if (Validator.isNull(
				batchEngineExportTask.getExternalReferenceCode())) {

			batchEngineExportTask.setExternalReferenceCode(
				batchEngineExportTask.getUuid());
		}
		else {
			if (!Objects.equals(
					batchEngineExportTaskModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					batchEngineExportTask.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = batchEngineExportTask.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = batchEngineExportTask.getPrimaryKey();
					}

					try {
						batchEngineExportTask.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								BatchEngineExportTask.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								batchEngineExportTask.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			BatchEngineExportTask ercBatchEngineExportTask = fetchByERC_C(
				batchEngineExportTask.getExternalReferenceCode(),
				batchEngineExportTask.getCompanyId());

			if (isNew) {
				if (ercBatchEngineExportTask != null) {
					throw new DuplicateBatchEngineExportTaskExternalReferenceCodeException(
						"Duplicate batch engine export task with external reference code " +
							batchEngineExportTask.getExternalReferenceCode() +
								" and company " +
									batchEngineExportTask.getCompanyId());
				}
			}
			else {
				if ((ercBatchEngineExportTask != null) &&
					(batchEngineExportTask.getBatchEngineExportTaskId() !=
						ercBatchEngineExportTask.
							getBatchEngineExportTaskId())) {

					throw new DuplicateBatchEngineExportTaskExternalReferenceCodeException(
						"Duplicate batch engine export task with external reference code " +
							batchEngineExportTask.getExternalReferenceCode() +
								" and company " +
									batchEngineExportTask.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (batchEngineExportTask.getCreateDate() == null)) {
			if (serviceContext == null) {
				batchEngineExportTask.setCreateDate(date);
			}
			else {
				batchEngineExportTask.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!batchEngineExportTaskModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				batchEngineExportTask.setModifiedDate(date);
			}
			else {
				batchEngineExportTask.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(batchEngineExportTask);
			}
			else {
				batchEngineExportTask = (BatchEngineExportTask)session.merge(
					batchEngineExportTask);
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

		cacheUniqueFindersResult(batchEngineExportTask, false);

		if (isNew) {
			batchEngineExportTask.setNew(false);
		}

		batchEngineExportTask.resetOriginalValues();

		return batchEngineExportTask;
	}

	/**
	 * Returns the batch engine export task with the primary key or throws a <code>NoSuchExportTaskException</code> if it could not be found.
	 *
	 * @param batchEngineExportTaskId the primary key of the batch engine export task
	 * @return the batch engine export task
	 * @throws NoSuchExportTaskException if a batch engine export task with the primary key could not be found
	 */
	@Override
	public BatchEngineExportTask findByPrimaryKey(long batchEngineExportTaskId)
		throws NoSuchExportTaskException {

		return findByPrimaryKey((Serializable)batchEngineExportTaskId);
	}

	/**
	 * Returns the batch engine export task with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param batchEngineExportTaskId the primary key of the batch engine export task
	 * @return the batch engine export task, or <code>null</code> if a batch engine export task with the primary key could not be found
	 */
	@Override
	public BatchEngineExportTask fetchByPrimaryKey(
		long batchEngineExportTaskId) {

		return fetchByPrimaryKey((Serializable)batchEngineExportTaskId);
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
		return "batchEngineExportTaskId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BATCHENGINEEXPORTTASK;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BatchEngineExportTaskModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the batch engine export task persistence.
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
			_SQL_SELECT_BATCHENGINEEXPORTTASK_WHERE,
			_SQL_COUNT_BATCHENGINEEXPORTTASK_WHERE,
			BatchEngineExportTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"batchEngineExportTask.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				BatchEngineExportTask::getUuid));

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
				_SQL_SELECT_BATCHENGINEEXPORTTASK_WHERE,
				_SQL_COUNT_BATCHENGINEEXPORTTASK_WHERE,
				BatchEngineExportTaskModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"batchEngineExportTask.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					BatchEngineExportTask::getUuid),
				new FinderColumn<>(
					"batchEngineExportTask.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					BatchEngineExportTask::getCompanyId));

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
				_SQL_SELECT_BATCHENGINEEXPORTTASK_WHERE,
				_SQL_COUNT_BATCHENGINEEXPORTTASK_WHERE,
				BatchEngineExportTaskModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"batchEngineExportTask.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					BatchEngineExportTask::getCompanyId));

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
				_SQL_SELECT_BATCHENGINEEXPORTTASK_WHERE,
				_SQL_COUNT_BATCHENGINEEXPORTTASK_WHERE,
				BatchEngineExportTaskModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"batchEngineExportTask.", "executeStatus",
					FinderColumn.Type.STRING, "=", true, true,
					BatchEngineExportTask::getExecuteStatus));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					BatchEngineExportTask::getExternalReferenceCode),
				BatchEngineExportTask::getCompanyId),
			_SQL_SELECT_BATCHENGINEEXPORTTASK_WHERE, "",
			new FinderColumn<>(
				"batchEngineExportTask.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				BatchEngineExportTask::getExternalReferenceCode),
			new FinderColumn<>(
				"batchEngineExportTask.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, BatchEngineExportTask::getCompanyId));

		BatchEngineExportTaskUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BatchEngineExportTaskUtil.setPersistence(null);

		entityCache.removeCache(BatchEngineExportTaskImpl.class.getName());
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
		BatchEngineExportTaskModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BATCHENGINEEXPORTTASK =
		"SELECT batchEngineExportTask FROM BatchEngineExportTask batchEngineExportTask";

	private static final String _SQL_SELECT_BATCHENGINEEXPORTTASK_WHERE =
		"SELECT batchEngineExportTask FROM BatchEngineExportTask batchEngineExportTask WHERE ";

	private static final String _SQL_COUNT_BATCHENGINEEXPORTTASK_WHERE =
		"SELECT COUNT(batchEngineExportTask) FROM BatchEngineExportTask batchEngineExportTask WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1741718025