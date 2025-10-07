/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service;

import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for BatchEngineExportTask. This utility wraps
 * <code>com.liferay.batch.engine.service.impl.BatchEngineExportTaskLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Shuyang Zhou
 * @see BatchEngineExportTaskLocalService
 * @generated
 */
public class BatchEngineExportTaskLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.batch.engine.service.impl.BatchEngineExportTaskLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static BatchEngineExportTask addBatchEngineExportTask(
		BatchEngineExportTask batchEngineExportTask) {

		return getService().addBatchEngineExportTask(batchEngineExportTask);
	}

	public static BatchEngineExportTask addBatchEngineExportTask(
		String externalReferenceCode, long companyId, long userId,
		String callbackURL, String className, String contentType,
		String executeStatus, List<String> fieldNames,
		Map<String, Serializable> parameters, String taskItemDelegateName) {

		return getService().addBatchEngineExportTask(
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
	public static BatchEngineExportTask createBatchEngineExportTask(
		long batchEngineExportTaskId) {

		return getService().createBatchEngineExportTask(
			batchEngineExportTaskId);
	}

	public static BatchEngineExportTask createBatchEngineExportTask(
		long batchEngineExportTaskId, String externalReferenceCode,
		long companyId, long userId, String callbackURL, String className,
		String contentType, String executeStatus, List<String> fieldNames,
		Map<String, Serializable> parameters, String taskItemDelegateName) {

		return getService().createBatchEngineExportTask(
			batchEngineExportTaskId, externalReferenceCode, companyId, userId,
			callbackURL, className, contentType, executeStatus, fieldNames,
			parameters, taskItemDelegateName);
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
	 * Deletes the batch engine export task from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BatchEngineExportTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param batchEngineExportTask the batch engine export task
	 * @return the batch engine export task that was removed
	 */
	public static BatchEngineExportTask deleteBatchEngineExportTask(
		BatchEngineExportTask batchEngineExportTask) {

		return getService().deleteBatchEngineExportTask(batchEngineExportTask);
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
	public static BatchEngineExportTask deleteBatchEngineExportTask(
			long batchEngineExportTaskId)
		throws PortalException {

		return getService().deleteBatchEngineExportTask(
			batchEngineExportTaskId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.engine.model.impl.BatchEngineExportTaskModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.engine.model.impl.BatchEngineExportTaskModelImpl</code>.
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

	public static BatchEngineExportTask fetchBatchEngineExportTask(
		long batchEngineExportTaskId) {

		return getService().fetchBatchEngineExportTask(batchEngineExportTaskId);
	}

	public static BatchEngineExportTask
		fetchBatchEngineExportTaskByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchBatchEngineExportTaskByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the batch engine export task with the matching UUID and company.
	 *
	 * @param uuid the batch engine export task's UUID
	 * @param companyId the primary key of the company
	 * @return the matching batch engine export task, or <code>null</code> if a matching batch engine export task could not be found
	 */
	public static BatchEngineExportTask
		fetchBatchEngineExportTaskByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchBatchEngineExportTaskByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the batch engine export task with the primary key.
	 *
	 * @param batchEngineExportTaskId the primary key of the batch engine export task
	 * @return the batch engine export task
	 * @throws PortalException if a batch engine export task with the primary key could not be found
	 */
	public static BatchEngineExportTask getBatchEngineExportTask(
			long batchEngineExportTaskId)
		throws PortalException {

		return getService().getBatchEngineExportTask(batchEngineExportTaskId);
	}

	public static BatchEngineExportTask
			getBatchEngineExportTaskByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getBatchEngineExportTaskByExternalReferenceCode(
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
	public static BatchEngineExportTask
			getBatchEngineExportTaskByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getBatchEngineExportTaskByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<BatchEngineExportTask> getBatchEngineExportTasks(
		int start, int end) {

		return getService().getBatchEngineExportTasks(start, end);
	}

	public static List<BatchEngineExportTask> getBatchEngineExportTasks(
		long companyId, int start, int end) {

		return getService().getBatchEngineExportTasks(companyId, start, end);
	}

	public static List<BatchEngineExportTask> getBatchEngineExportTasks(
		long companyId, int start, int end,
		OrderByComparator<BatchEngineExportTask> orderByComparator) {

		return getService().getBatchEngineExportTasks(
			companyId, start, end, orderByComparator);
	}

	public static List<BatchEngineExportTask> getBatchEngineExportTasks(
		String executeStatus) {

		return getService().getBatchEngineExportTasks(executeStatus);
	}

	/**
	 * Returns the number of batch engine export tasks.
	 *
	 * @return the number of batch engine export tasks
	 */
	public static int getBatchEngineExportTasksCount() {
		return getService().getBatchEngineExportTasksCount();
	}

	public static int getBatchEngineExportTasksCount(long companyId) {
		return getService().getBatchEngineExportTasksCount(companyId);
	}

	public static
		com.liferay.batch.engine.model.BatchEngineExportTaskContentBlobModel
			getContentBlobModel(Serializable primaryKey) {

		return getService().getContentBlobModel(primaryKey);
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

	public static InputStream openContentInputStream(
		long batchEngineExportTaskId) {

		return getService().openContentInputStream(batchEngineExportTaskId);
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
	public static BatchEngineExportTask updateBatchEngineExportTask(
		BatchEngineExportTask batchEngineExportTask) {

		return getService().updateBatchEngineExportTask(batchEngineExportTask);
	}

	public static BatchEngineExportTaskLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<BatchEngineExportTaskLocalService>
		_serviceSnapshot = new Snapshot<>(
			BatchEngineExportTaskLocalServiceUtil.class,
			BatchEngineExportTaskLocalService.class);

}