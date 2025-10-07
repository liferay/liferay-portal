/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link BatchEngineExportTaskLocalService}.
 *
 * @author Shuyang Zhou
 * @see BatchEngineExportTaskLocalService
 * @generated
 */
public class BatchEngineExportTaskLocalServiceWrapper
	implements BatchEngineExportTaskLocalService,
			   ServiceWrapper<BatchEngineExportTaskLocalService> {

	public BatchEngineExportTaskLocalServiceWrapper() {
		this(null);
	}

	public BatchEngineExportTaskLocalServiceWrapper(
		BatchEngineExportTaskLocalService batchEngineExportTaskLocalService) {

		_batchEngineExportTaskLocalService = batchEngineExportTaskLocalService;
	}

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
	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		addBatchEngineExportTask(
			com.liferay.batch.engine.model.BatchEngineExportTask
				batchEngineExportTask) {

		return _batchEngineExportTaskLocalService.addBatchEngineExportTask(
			batchEngineExportTask);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		addBatchEngineExportTask(
			String externalReferenceCode, long companyId, long userId,
			String callbackURL, String className, String contentType,
			String executeStatus, java.util.List<String> fieldNames,
			java.util.Map<String, java.io.Serializable> parameters,
			String taskItemDelegateName) {

		return _batchEngineExportTaskLocalService.addBatchEngineExportTask(
			externalReferenceCode, companyId, userId, callbackURL, className,
			contentType, executeStatus, fieldNames, parameters,
			taskItemDelegateName);
	}

	/**
	 * Creates a new batch engine export task with the primary key. Does not add the batch engine export task to the database.
	 *
	 * @param batchEngineExportTaskId the primary key for the new batch engine export task
	 * @return the new batch engine export task
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		createBatchEngineExportTask(long batchEngineExportTaskId) {

		return _batchEngineExportTaskLocalService.createBatchEngineExportTask(
			batchEngineExportTaskId);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		createBatchEngineExportTask(
			long batchEngineExportTaskId, String externalReferenceCode,
			long companyId, long userId, String callbackURL, String className,
			String contentType, String executeStatus,
			java.util.List<String> fieldNames,
			java.util.Map<String, java.io.Serializable> parameters,
			String taskItemDelegateName) {

		return _batchEngineExportTaskLocalService.createBatchEngineExportTask(
			batchEngineExportTaskId, externalReferenceCode, companyId, userId,
			callbackURL, className, contentType, executeStatus, fieldNames,
			parameters, taskItemDelegateName);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineExportTaskLocalService.createPersistedModel(
			primaryKeyObj);
	}

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
	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		deleteBatchEngineExportTask(
			com.liferay.batch.engine.model.BatchEngineExportTask
				batchEngineExportTask) {

		return _batchEngineExportTaskLocalService.deleteBatchEngineExportTask(
			batchEngineExportTask);
	}

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
	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
			deleteBatchEngineExportTask(long batchEngineExportTaskId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineExportTaskLocalService.deleteBatchEngineExportTask(
			batchEngineExportTaskId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineExportTaskLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _batchEngineExportTaskLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _batchEngineExportTaskLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _batchEngineExportTaskLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _batchEngineExportTaskLocalService.dynamicQuery(dynamicQuery);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _batchEngineExportTaskLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _batchEngineExportTaskLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _batchEngineExportTaskLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _batchEngineExportTaskLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		fetchBatchEngineExportTask(long batchEngineExportTaskId) {

		return _batchEngineExportTaskLocalService.fetchBatchEngineExportTask(
			batchEngineExportTaskId);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		fetchBatchEngineExportTaskByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _batchEngineExportTaskLocalService.
			fetchBatchEngineExportTaskByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the batch engine export task with the matching UUID and company.
	 *
	 * @param uuid the batch engine export task's UUID
	 * @param companyId the primary key of the company
	 * @return the matching batch engine export task, or <code>null</code> if a matching batch engine export task could not be found
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		fetchBatchEngineExportTaskByUuidAndCompanyId(
			String uuid, long companyId) {

		return _batchEngineExportTaskLocalService.
			fetchBatchEngineExportTaskByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _batchEngineExportTaskLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the batch engine export task with the primary key.
	 *
	 * @param batchEngineExportTaskId the primary key of the batch engine export task
	 * @return the batch engine export task
	 * @throws PortalException if a batch engine export task with the primary key could not be found
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
			getBatchEngineExportTask(long batchEngineExportTaskId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineExportTaskLocalService.getBatchEngineExportTask(
			batchEngineExportTaskId);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
			getBatchEngineExportTaskByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineExportTaskLocalService.
			getBatchEngineExportTaskByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the batch engine export task with the matching UUID and company.
	 *
	 * @param uuid the batch engine export task's UUID
	 * @param companyId the primary key of the company
	 * @return the matching batch engine export task
	 * @throws PortalException if a matching batch engine export task could not be found
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
			getBatchEngineExportTaskByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineExportTaskLocalService.
			getBatchEngineExportTaskByUuidAndCompanyId(uuid, companyId);
	}

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
	@Override
	public java.util.List<com.liferay.batch.engine.model.BatchEngineExportTask>
		getBatchEngineExportTasks(int start, int end) {

		return _batchEngineExportTaskLocalService.getBatchEngineExportTasks(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.batch.engine.model.BatchEngineExportTask>
		getBatchEngineExportTasks(long companyId, int start, int end) {

		return _batchEngineExportTaskLocalService.getBatchEngineExportTasks(
			companyId, start, end);
	}

	@Override
	public java.util.List<com.liferay.batch.engine.model.BatchEngineExportTask>
		getBatchEngineExportTasks(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.batch.engine.model.BatchEngineExportTask>
					orderByComparator) {

		return _batchEngineExportTaskLocalService.getBatchEngineExportTasks(
			companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.batch.engine.model.BatchEngineExportTask>
		getBatchEngineExportTasks(String executeStatus) {

		return _batchEngineExportTaskLocalService.getBatchEngineExportTasks(
			executeStatus);
	}

	/**
	 * Returns the number of batch engine export tasks.
	 *
	 * @return the number of batch engine export tasks
	 */
	@Override
	public int getBatchEngineExportTasksCount() {
		return _batchEngineExportTaskLocalService.
			getBatchEngineExportTasksCount();
	}

	@Override
	public int getBatchEngineExportTasksCount(long companyId) {
		return _batchEngineExportTaskLocalService.
			getBatchEngineExportTasksCount(companyId);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTaskContentBlobModel
		getContentBlobModel(java.io.Serializable primaryKey) {

		return _batchEngineExportTaskLocalService.getContentBlobModel(
			primaryKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _batchEngineExportTaskLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _batchEngineExportTaskLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _batchEngineExportTaskLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineExportTaskLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.io.InputStream openContentInputStream(
		long batchEngineExportTaskId) {

		return _batchEngineExportTaskLocalService.openContentInputStream(
			batchEngineExportTaskId);
	}

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
	@Override
	public com.liferay.batch.engine.model.BatchEngineExportTask
		updateBatchEngineExportTask(
			com.liferay.batch.engine.model.BatchEngineExportTask
				batchEngineExportTask) {

		return _batchEngineExportTaskLocalService.updateBatchEngineExportTask(
			batchEngineExportTask);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _batchEngineExportTaskLocalService.getBasePersistence();
	}

	@Override
	public BatchEngineExportTaskLocalService getWrappedService() {
		return _batchEngineExportTaskLocalService;
	}

	@Override
	public void setWrappedService(
		BatchEngineExportTaskLocalService batchEngineExportTaskLocalService) {

		_batchEngineExportTaskLocalService = batchEngineExportTaskLocalService;
	}

	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

}