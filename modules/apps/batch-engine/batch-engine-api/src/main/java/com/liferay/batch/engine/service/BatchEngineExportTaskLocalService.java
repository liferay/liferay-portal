/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service;

import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.model.BatchEngineExportTaskContentBlobModel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for BatchEngineExportTask. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Shuyang Zhou
 * @see BatchEngineExportTaskLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface BatchEngineExportTaskLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.batch.engine.service.impl.BatchEngineExportTaskLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the batch engine export task local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link BatchEngineExportTaskLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the batch engine export task to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineExportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineExportTask the batch engine export task
	 * @return the batch engine export task that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public BatchEngineExportTask addBatchEngineExportTask(
		BatchEngineExportTask batchEngineExportTask);

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public BatchEngineExportTask addBatchEngineExportTask(
		String externalReferenceCode, long companyId, long userId,
		String callbackURL, String className, String contentType,
		String executeStatus, List<String> fieldNames,
		Map<String, Serializable> parameters, String taskItemDelegateName);

	/**
	 * Creates a new batch engine export task with the primary key. Does not add the batch engine export task to the database.
	 *
	 * @param batchEngineExportTaskId the primary key for the new batch engine export task
	 * @return the new batch engine export task
	 */
	@Transactional(enabled = false)
	public BatchEngineExportTask createBatchEngineExportTask(
		long batchEngineExportTaskId);

	public BatchEngineExportTask createBatchEngineExportTask(
		long batchEngineExportTaskId, String externalReferenceCode,
		long companyId, long userId, String callbackURL, String className,
		String contentType, String executeStatus, List<String> fieldNames,
		Map<String, Serializable> parameters, String taskItemDelegateName);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the batch engine export task from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineExportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineExportTask the batch engine export task
	 * @return the batch engine export task that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public BatchEngineExportTask deleteBatchEngineExportTask(
		BatchEngineExportTask batchEngineExportTask);

	/**
	 * Deletes the batch engine export task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineExportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineExportTaskId the primary key of the batch engine export task
	 * @return the batch engine export task that was removed
	 * @throws PortalException if a batch engine export task with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public BatchEngineExportTask deleteBatchEngineExportTask(
			long batchEngineExportTaskId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.engine.model.impl.BatchEngineExportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.engine.model.impl.BatchEngineExportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BatchEngineExportTask fetchBatchEngineExportTask(
		long batchEngineExportTaskId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BatchEngineExportTask
		fetchBatchEngineExportTaskByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	/**
	 * Returns the batch engine export task with the matching UUID and company.
	 *
	 * @param uuid the batch engine export task's UUID
	 * @param companyId the primary key of the company
	 * @return the matching batch engine export task, or <code>null</code> if a matching batch engine export task could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BatchEngineExportTask fetchBatchEngineExportTaskByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the batch engine export task with the primary key.
	 *
	 * @param batchEngineExportTaskId the primary key of the batch engine export task
	 * @return the batch engine export task
	 * @throws PortalException if a batch engine export task with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BatchEngineExportTask getBatchEngineExportTask(
			long batchEngineExportTaskId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BatchEngineExportTask
			getBatchEngineExportTaskByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the batch engine export task with the matching UUID and company.
	 *
	 * @param uuid the batch engine export task's UUID
	 * @param companyId the primary key of the company
	 * @return the matching batch engine export task
	 * @throws PortalException if a matching batch engine export task could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BatchEngineExportTask getBatchEngineExportTaskByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the batch engine export tasks.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.engine.model.impl.BatchEngineExportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of batch engine export tasks
	 * @param end the upper bound of the range of batch engine export tasks (not inclusive)
	 * @return the range of batch engine export tasks
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
		long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineExportTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BatchEngineExportTask> getBatchEngineExportTasks(
		String executeStatus);

	/**
	 * Returns the number of batch engine export tasks.
	 *
	 * @return the number of batch engine export tasks
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBatchEngineExportTasksCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBatchEngineExportTasksCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BatchEngineExportTaskContentBlobModel getContentBlobModel(
		Serializable primaryKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(readOnly = true)
	public InputStream openContentInputStream(long batchEngineExportTaskId);

	/**
	 * Updates the batch engine export task in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineExportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineExportTask the batch engine export task
	 * @return the batch engine export task that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public BatchEngineExportTask updateBatchEngineExportTask(
		BatchEngineExportTask batchEngineExportTask);

}