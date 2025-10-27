/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service;

import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for BatchEngineImportTask. This utility wraps
 * <code>com.liferay.batch.engine.service.impl.BatchEngineImportTaskLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Shuyang Zhou
 * @see BatchEngineImportTaskLocalService
 * @generated
 */
public class BatchEngineImportTaskLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.batch.engine.service.impl.BatchEngineImportTaskLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static BatchEngineImportTask addBatchEngineImportTask(
		BatchEngineImportTask batchEngineImportTask) {

		return getService().addBatchEngineImportTask(batchEngineImportTask);
	}

	public static BatchEngineImportTask addBatchEngineImportTask(
			String externalReferenceCode, long companyId, long userId,
			long batchSize, String callbackURL, String className,
			byte[] content, String contentType, String executeStatus,
			Map<String, String> fieldNameMappingMap, int importStrategy,
			String operation, Map<String, Serializable> parameters,
			String taskItemDelegateName)
		throws PortalException {

		return getService().addBatchEngineImportTask(
			externalReferenceCode, companyId, userId, batchSize, callbackURL,
			className, content, contentType, executeStatus, fieldNameMappingMap,
			importStrategy, operation, parameters, taskItemDelegateName);
	}

	public static BatchEngineImportTask addBatchEngineImportTask(
			String externalReferenceCode, long companyId, long userId,
			long batchSize, String callbackURL, String className,
			byte[] content, String contentType, String executeStatus,
			Map<String, String> fieldNameMappingMap, int importStrategy,
			String operation, Map<String, Serializable> parameters,
			String taskItemDelegateName,
			com.liferay.batch.engine.BatchEngineTaskItemDelegate<?>
				batchEngineTaskItemDelegate)
		throws PortalException {

		return getService().addBatchEngineImportTask(
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
	public static BatchEngineImportTask createBatchEngineImportTask(
		long batchEngineImportTaskId) {

		return getService().createBatchEngineImportTask(
			batchEngineImportTaskId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static BatchEngineImportTask deleteBatchEngineImportTask(
		BatchEngineImportTask batchEngineImportTask) {

		return getService().deleteBatchEngineImportTask(batchEngineImportTask);
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
	public static BatchEngineImportTask deleteBatchEngineImportTask(
			long batchEngineImportTaskId)
		throws PortalException {

		return getService().deleteBatchEngineImportTask(
			batchEngineImportTaskId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static BatchEngineImportTask fetchBatchEngineImportTask(
		long batchEngineImportTaskId) {

		return getService().fetchBatchEngineImportTask(batchEngineImportTaskId);
	}

	public static BatchEngineImportTask
		fetchBatchEngineImportTaskByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchBatchEngineImportTaskByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the batch engine import task with the matching UUID and company.
	 *
	 * @param uuid the batch engine import task's UUID
	 * @param companyId the primary key of the company
	 * @return the matching batch engine import task, or <code>null</code> if a matching batch engine import task could not be found
	 */
	public static BatchEngineImportTask
		fetchBatchEngineImportTaskByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchBatchEngineImportTaskByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the batch engine import task with the primary key.
	 *
	 * @param batchEngineImportTaskId the primary key of the batch engine import task
	 * @return the batch engine import task
	 * @throws PortalException if a batch engine import task with the primary key could not be found
	 */
	public static BatchEngineImportTask getBatchEngineImportTask(
			long batchEngineImportTaskId)
		throws PortalException {

		return getService().getBatchEngineImportTask(batchEngineImportTaskId);
	}

	public static BatchEngineImportTask
			getBatchEngineImportTaskByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getBatchEngineImportTaskByExternalReferenceCode(
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
	public static BatchEngineImportTask
			getBatchEngineImportTaskByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getBatchEngineImportTaskByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<BatchEngineImportTask> getBatchEngineImportTasks(
		int start, int end) {

		return getService().getBatchEngineImportTasks(start, end);
	}

	public static List<BatchEngineImportTask> getBatchEngineImportTasks(
		long companyId, int start, int end) {

		return getService().getBatchEngineImportTasks(companyId, start, end);
	}

	public static List<BatchEngineImportTask> getBatchEngineImportTasks(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineImportTask> orderByComparator) {

		return getService().getBatchEngineImportTasks(
			companyId, start, end, orderByComparator);
	}

	public static List<BatchEngineImportTask> getBatchEngineImportTasks(
		String executeStatus) {

		return getService().getBatchEngineImportTasks(executeStatus);
	}

	/**
	 * Returns the number of batch engine import tasks.
	 *
	 * @return the number of batch engine import tasks
	 */
	public static int getBatchEngineImportTasksCount() {
		return getService().getBatchEngineImportTasksCount();
	}

	public static int getBatchEngineImportTasksCount(long companyId) {
		return getService().getBatchEngineImportTasksCount(companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	public static BatchEngineImportTask updateBatchEngineImportTask(
		BatchEngineImportTask batchEngineImportTask) {

		return getService().updateBatchEngineImportTask(batchEngineImportTask);
	}

	public static BatchEngineImportTaskLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<BatchEngineImportTaskLocalService>
		_serviceSnapshot = new Snapshot<>(
			BatchEngineImportTaskLocalServiceUtil.class,
			BatchEngineImportTaskLocalService.class);

}