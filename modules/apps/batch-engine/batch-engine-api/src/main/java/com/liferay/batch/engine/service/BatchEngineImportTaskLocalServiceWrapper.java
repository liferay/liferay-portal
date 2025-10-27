/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link BatchEngineImportTaskLocalService}.
 *
 * @author Shuyang Zhou
 * @see BatchEngineImportTaskLocalService
 * @generated
 */
public class BatchEngineImportTaskLocalServiceWrapper
	implements BatchEngineImportTaskLocalService,
			   ServiceWrapper<BatchEngineImportTaskLocalService> {

	public BatchEngineImportTaskLocalServiceWrapper() {
		this(null);
	}

	public BatchEngineImportTaskLocalServiceWrapper(
		BatchEngineImportTaskLocalService batchEngineImportTaskLocalService) {

		_batchEngineImportTaskLocalService = batchEngineImportTaskLocalService;
	}

	/**
	 * Adds the batch engine import task to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineImportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineImportTask the batch engine import task
	 * @return the batch engine import task that was added
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
		addBatchEngineImportTask(
			com.liferay.batch.engine.model.BatchEngineImportTask
				batchEngineImportTask) {

		return _batchEngineImportTaskLocalService.addBatchEngineImportTask(
			batchEngineImportTask);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
			addBatchEngineImportTask(
				String externalReferenceCode, long companyId, long userId,
				long batchSize, String callbackURL, String className,
				byte[] content, String contentType, String executeStatus,
				java.util.Map<String, String> fieldNameMappingMap,
				int importStrategy, String operation,
				java.util.Map<String, java.io.Serializable> parameters,
				String taskItemDelegateName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.addBatchEngineImportTask(
			externalReferenceCode, companyId, userId, batchSize, callbackURL,
			className, content, contentType, executeStatus, fieldNameMappingMap,
			importStrategy, operation, parameters, taskItemDelegateName);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
			addBatchEngineImportTask(
				String externalReferenceCode, long companyId, long userId,
				long batchSize, String callbackURL, String className,
				byte[] content, String contentType, String executeStatus,
				java.util.Map<String, String> fieldNameMappingMap,
				int importStrategy, String operation,
				java.util.Map<String, java.io.Serializable> parameters,
				String taskItemDelegateName,
				com.liferay.batch.engine.BatchEngineTaskItemDelegate<?>
					batchEngineTaskItemDelegate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.addBatchEngineImportTask(
			externalReferenceCode, companyId, userId, batchSize, callbackURL,
			className, content, contentType, executeStatus, fieldNameMappingMap,
			importStrategy, operation, parameters, taskItemDelegateName,
			batchEngineTaskItemDelegate);
	}

	/**
	 * Creates a new batch engine import task with the primary key. Does not add the batch engine import task to the database.
	 *
	 * @param batchEngineImportTaskId the primary key for the new batch engine import task
	 * @return the new batch engine import task
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
		createBatchEngineImportTask(long batchEngineImportTaskId) {

		return _batchEngineImportTaskLocalService.createBatchEngineImportTask(
			batchEngineImportTaskId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the batch engine import task from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineImportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineImportTask the batch engine import task
	 * @return the batch engine import task that was removed
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
		deleteBatchEngineImportTask(
			com.liferay.batch.engine.model.BatchEngineImportTask
				batchEngineImportTask) {

		return _batchEngineImportTaskLocalService.deleteBatchEngineImportTask(
			batchEngineImportTask);
	}

	/**
	 * Deletes the batch engine import task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineImportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task that was removed
	 * @throws PortalException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
			deleteBatchEngineImportTask(long batchEngineImportTaskId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.deleteBatchEngineImportTask(
			batchEngineImportTaskId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _batchEngineImportTaskLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _batchEngineImportTaskLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _batchEngineImportTaskLocalService.dynamicQuery();
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

		return _batchEngineImportTaskLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.engine.model.impl.BatchEngineImportTaskModelImpl</code>.
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

		return _batchEngineImportTaskLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.engine.model.impl.BatchEngineImportTaskModelImpl</code>.
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

		return _batchEngineImportTaskLocalService.dynamicQuery(
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

		return _batchEngineImportTaskLocalService.dynamicQueryCount(
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

		return _batchEngineImportTaskLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
		fetchBatchEngineImportTask(long batchEngineImportTaskId) {

		return _batchEngineImportTaskLocalService.fetchBatchEngineImportTask(
			batchEngineImportTaskId);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
		fetchBatchEngineImportTaskByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _batchEngineImportTaskLocalService.
			fetchBatchEngineImportTaskByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the batch engine import task with the matching UUID and company.
	 *
	 * @param uuid the batch engine import task's UUID
	 * @param companyId the primary key of the company
	 * @return the matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
		fetchBatchEngineImportTaskByUuidAndCompanyId(
			String uuid, long companyId) {

		return _batchEngineImportTaskLocalService.
			fetchBatchEngineImportTaskByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _batchEngineImportTaskLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the batch engine import task with the primary key.
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task
	 * @throws PortalException if a batch engine import task with the primary key could not be found
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
			getBatchEngineImportTask(long batchEngineImportTaskId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.getBatchEngineImportTask(
			batchEngineImportTaskId);
	}

	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
			getBatchEngineImportTaskByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.
			getBatchEngineImportTaskByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the batch engine import task with the matching UUID and company.
	 *
	 * @param uuid the batch engine import task's UUID
	 * @param companyId the primary key of the company
	 * @return the matching batch engine import task
	 * @throws PortalException if a matching batch engine import task could not be found
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
			getBatchEngineImportTaskByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.
			getBatchEngineImportTaskByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the batch engine import tasks.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.engine.model.impl.BatchEngineImportTaskModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of batch engine import tasks
	 * @param end the upper bound of the range of batch engine import tasks (not inclusive)
	 * @return the range of batch engine import tasks
	 */
	@Override
	public java.util.List<com.liferay.batch.engine.model.BatchEngineImportTask>
		getBatchEngineImportTasks(int start, int end) {

		return _batchEngineImportTaskLocalService.getBatchEngineImportTasks(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.batch.engine.model.BatchEngineImportTask>
		getBatchEngineImportTasks(long companyId, int start, int end) {

		return _batchEngineImportTaskLocalService.getBatchEngineImportTasks(
			companyId, start, end);
	}

	@Override
	public java.util.List<com.liferay.batch.engine.model.BatchEngineImportTask>
		getBatchEngineImportTasks(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.batch.engine.model.BatchEngineImportTask>
					orderByComparator) {

		return _batchEngineImportTaskLocalService.getBatchEngineImportTasks(
			companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.batch.engine.model.BatchEngineImportTask>
		getBatchEngineImportTasks(String executeStatus) {

		return _batchEngineImportTaskLocalService.getBatchEngineImportTasks(
			executeStatus);
	}

	/**
	 * Returns the number of batch engine import tasks.
	 *
	 * @return the number of batch engine import tasks
	 */
	@Override
	public int getBatchEngineImportTasksCount() {
		return _batchEngineImportTaskLocalService.
			getBatchEngineImportTasksCount();
	}

	@Override
	public int getBatchEngineImportTasksCount(long companyId) {
		return _batchEngineImportTaskLocalService.
			getBatchEngineImportTasksCount(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _batchEngineImportTaskLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _batchEngineImportTaskLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _batchEngineImportTaskLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _batchEngineImportTaskLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the batch engine import task in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineImportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineImportTask the batch engine import task
	 * @return the batch engine import task that was updated
	 */
	@Override
	public com.liferay.batch.engine.model.BatchEngineImportTask
		updateBatchEngineImportTask(
			com.liferay.batch.engine.model.BatchEngineImportTask
				batchEngineImportTask) {

		return _batchEngineImportTaskLocalService.updateBatchEngineImportTask(
			batchEngineImportTask);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _batchEngineImportTaskLocalService.getBasePersistence();
	}

	@Override
	public BatchEngineImportTaskLocalService getWrappedService() {
		return _batchEngineImportTaskLocalService;
	}

	@Override
	public void setWrappedService(
		BatchEngineImportTaskLocalService batchEngineImportTaskLocalService) {

		_batchEngineImportTaskLocalService = batchEngineImportTaskLocalService;
	}

	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

}