/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.background.task.model.BackgroundTask;
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
 * Provides the local service utility for BackgroundTask. This utility wraps
 * <code>com.liferay.portal.background.task.service.impl.BackgroundTaskLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see BackgroundTaskLocalService
 * @generated
 */
public class BackgroundTaskLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.background.task.service.impl.BackgroundTaskLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the background task to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BackgroundTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param backgroundTask the background task
	 * @return the background task that was added
	 */
	public static BackgroundTask addBackgroundTask(
		BackgroundTask backgroundTask) {

		return getService().addBackgroundTask(backgroundTask);
	}

	public static BackgroundTask addBackgroundTask(
			long userId, long groupId, String name,
			String taskExecutorClassName,
			Map<String, Serializable> taskContextMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addBackgroundTask(
			userId, groupId, name, taskExecutorClassName, taskContextMap,
			serviceContext);
	}

	public static BackgroundTask addBackgroundTask(
			long userId, long groupId, String name,
			String[] servletContextNames, Class<?> taskExecutorClass,
			Map<String, Serializable> taskContextMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addBackgroundTask(
			userId, groupId, name, servletContextNames, taskExecutorClass,
			taskContextMap, serviceContext);
	}

	public static void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName,
			java.io.File file)
		throws PortalException {

		getService().addBackgroundTaskAttachment(
			userId, backgroundTaskId, fileName, file);
	}

	public static void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName,
			InputStream inputStream)
		throws PortalException {

		getService().addBackgroundTaskAttachment(
			userId, backgroundTaskId, fileName, inputStream);
	}

	public static void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String sourceFileName,
			String title, java.io.File file)
		throws PortalException {

		getService().addBackgroundTaskAttachment(
			userId, backgroundTaskId, sourceFileName, title, file);
	}

	public static BackgroundTask amendBackgroundTask(
		long backgroundTaskId, Map<String, Serializable> taskContextMap,
		int status,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().amendBackgroundTask(
			backgroundTaskId, taskContextMap, status, serviceContext);
	}

	public static BackgroundTask amendBackgroundTask(
		long backgroundTaskId, Map<String, Serializable> taskContextMap,
		String errorStackTrace, int status, String statusMessage,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().amendBackgroundTask(
			backgroundTaskId, taskContextMap, errorStackTrace, status,
			statusMessage, serviceContext);
	}

	public static void cleanUpBackgroundTask(
		long backgroundTaskId, int status) {

		getService().cleanUpBackgroundTask(backgroundTaskId, status);
	}

	public static void cleanUpBackgroundTasks() {
		getService().cleanUpBackgroundTasks();
	}

	/**
	 * Creates a new background task with the primary key. Does not add the background task to the database.
	 *
	 * @param backgroundTaskId the primary key for the new background task
	 * @return the new background task
	 */
	public static BackgroundTask createBackgroundTask(long backgroundTaskId) {
		return getService().createBackgroundTask(backgroundTaskId);
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
	 * Deletes the background task from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BackgroundTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param backgroundTask the background task
	 * @return the background task that was removed
	 * @throws PortalException
	 */
	public static BackgroundTask deleteBackgroundTask(
			BackgroundTask backgroundTask)
		throws PortalException {

		return getService().deleteBackgroundTask(backgroundTask);
	}

	/**
	 * Deletes the background task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BackgroundTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param backgroundTaskId the primary key of the background task
	 * @return the background task that was removed
	 * @throws PortalException if a background task with the primary key could not be found
	 */
	public static BackgroundTask deleteBackgroundTask(long backgroundTaskId)
		throws PortalException {

		return getService().deleteBackgroundTask(backgroundTaskId);
	}

	public static void deleteCompanyBackgroundTasks(long companyId)
		throws PortalException {

		getService().deleteCompanyBackgroundTasks(companyId);
	}

	public static void deleteGroupBackgroundTasks(long groupId)
		throws PortalException {

		getService().deleteGroupBackgroundTasks(groupId);
	}

	public static void deleteGroupBackgroundTasks(
			long groupId, String name, String taskExecutorClassName)
		throws PortalException {

		getService().deleteGroupBackgroundTasks(
			groupId, name, taskExecutorClassName);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.background.task.model.impl.BackgroundTaskModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.background.task.model.impl.BackgroundTaskModelImpl</code>.
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

	public static BackgroundTask fetchBackgroundTask(long backgroundTaskId) {
		return getService().fetchBackgroundTask(backgroundTaskId);
	}

	public static BackgroundTask fetchFirstBackgroundTask(
		long groupId, String taskExecutorClassName, boolean completed,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().fetchFirstBackgroundTask(
			groupId, taskExecutorClassName, completed, orderByComparator);
	}

	public static BackgroundTask fetchFirstBackgroundTask(
		String taskExecutorClassName, int status) {

		return getService().fetchFirstBackgroundTask(
			taskExecutorClassName, status);
	}

	public static BackgroundTask fetchFirstBackgroundTask(
		String taskExecutorClassName, int status,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().fetchFirstBackgroundTask(
			taskExecutorClassName, status, orderByComparator);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the background task with the primary key.
	 *
	 * @param backgroundTaskId the primary key of the background task
	 * @return the background task
	 * @throws PortalException if a background task with the primary key could not be found
	 */
	public static BackgroundTask getBackgroundTask(long backgroundTaskId)
		throws PortalException {

		return getService().getBackgroundTask(backgroundTaskId);
	}

	/**
	 * Returns a range of all the background tasks.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.background.task.model.impl.BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @return the range of background tasks
	 */
	public static List<BackgroundTask> getBackgroundTasks(int start, int end) {
		return getService().getBackgroundTasks(start, end);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, int status) {

		return getService().getBackgroundTasks(groupId, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName) {

		return getService().getBackgroundTasks(groupId, taskExecutorClassName);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, boolean completed,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			groupId, taskExecutorClassName, completed, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, int status) {

		return getService().getBackgroundTasks(
			groupId, taskExecutorClassName, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			groupId, taskExecutorClassName, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			groupId, name, taskExecutorClassName, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long groupId, String[] taskExecutorClassNames, int status) {

		return getService().getBackgroundTasks(
			groupId, taskExecutorClassNames, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			groupIds, name, taskExecutorClassName, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String name, String[] taskExecutorClassNames,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			groupIds, name, taskExecutorClassNames, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String[] taskExecutorClassNames) {

		return getService().getBackgroundTasks(
			groupIds, taskExecutorClassNames);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed) {

		return getService().getBackgroundTasks(
			groupIds, taskExecutorClassNames, completed);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			groupIds, taskExecutorClassNames, completed, start, end,
			orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String[] taskExecutorClassNames, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			groupIds, taskExecutorClassNames, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		String taskExecutorClassName, int status) {

		return getService().getBackgroundTasks(taskExecutorClassName, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		String taskExecutorClassName, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			taskExecutorClassName, status, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		String[] taskExecutorClassNames, int status) {

		return getService().getBackgroundTasks(taskExecutorClassNames, status);
	}

	public static List<BackgroundTask> getBackgroundTasks(
		String[] taskExecutorClassNames, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return getService().getBackgroundTasks(
			taskExecutorClassNames, status, start, end, orderByComparator);
	}

	public static List<BackgroundTask> getBackgroundTasksByDuration(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed,
		int start, int end, boolean orderByType) {

		return getService().getBackgroundTasksByDuration(
			groupIds, taskExecutorClassNames, completed, start, end,
			orderByType);
	}

	public static List<BackgroundTask> getBackgroundTasksByDuration(
		long[] groupIds, String[] taskExecutorClassNames, int start, int end,
		boolean orderByType) {

		return getService().getBackgroundTasksByDuration(
			groupIds, taskExecutorClassNames, start, end, orderByType);
	}

	/**
	 * Returns the number of background tasks.
	 *
	 * @return the number of background tasks
	 */
	public static int getBackgroundTasksCount() {
		return getService().getBackgroundTasksCount();
	}

	public static int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName) {

		return getService().getBackgroundTasksCount(
			groupId, taskExecutorClassName);
	}

	public static int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName, boolean completed) {

		return getService().getBackgroundTasksCount(
			groupId, taskExecutorClassName, completed);
	}

	public static int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName) {

		return getService().getBackgroundTasksCount(
			groupId, name, taskExecutorClassName);
	}

	public static int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName,
		boolean completed) {

		return getService().getBackgroundTasksCount(
			groupId, name, taskExecutorClassName, completed);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName) {

		return getService().getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName,
		boolean completed) {

		return getService().getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName, completed);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String name, String[] taskExecutorClassName) {

		return getService().getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String[] taskExecutorClassNames) {

		return getService().getBackgroundTasksCount(
			groupIds, taskExecutorClassNames);
	}

	public static int getBackgroundTasksCount(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed) {

		return getService().getBackgroundTasksCount(
			groupIds, taskExecutorClassNames, completed);
	}

	public static String getBackgroundTaskStatusJSON(long backgroundTaskId) {
		return getService().getBackgroundTaskStatusJSON(backgroundTaskId);
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

	public static void resumeBackgroundTask(long backgroundTaskId) {
		getService().resumeBackgroundTask(backgroundTaskId);
	}

	public static void triggerBackgroundTask(long backgroundTaskId) {
		getService().triggerBackgroundTask(backgroundTaskId);
	}

	/**
	 * Updates the background task in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect BackgroundTaskLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param backgroundTask the background task
	 * @return the background task that was updated
	 */
	public static BackgroundTask updateBackgroundTask(
		BackgroundTask backgroundTask) {

		return getService().updateBackgroundTask(backgroundTask);
	}

	public static BackgroundTaskLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<BackgroundTaskLocalService> _serviceSnapshot =
		new Snapshot<>(
			BackgroundTaskLocalServiceUtil.class,
			BackgroundTaskLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:388018999