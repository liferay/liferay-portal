/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link BackgroundTaskLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see BackgroundTaskLocalService
 * @generated
 */
public class BackgroundTaskLocalServiceWrapper
	implements BackgroundTaskLocalService,
			   ServiceWrapper<BackgroundTaskLocalService> {

	public BackgroundTaskLocalServiceWrapper() {
		this(null);
	}

	public BackgroundTaskLocalServiceWrapper(
		BackgroundTaskLocalService backgroundTaskLocalService) {

		_backgroundTaskLocalService = backgroundTaskLocalService;
	}

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
	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		addBackgroundTask(
			com.liferay.portal.background.task.model.BackgroundTask
				backgroundTask) {

		return _backgroundTaskLocalService.addBackgroundTask(backgroundTask);
	}

	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
			addBackgroundTask(
				long userId, long groupId, String name,
				String taskExecutorClassName,
				java.util.Map<String, java.io.Serializable> taskContextMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _backgroundTaskLocalService.addBackgroundTask(
			userId, groupId, name, taskExecutorClassName, taskContextMap,
			serviceContext);
	}

	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
			addBackgroundTask(
				long userId, long groupId, String name,
				String[] servletContextNames, Class<?> taskExecutorClass,
				java.util.Map<String, java.io.Serializable> taskContextMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _backgroundTaskLocalService.addBackgroundTask(
			userId, groupId, name, servletContextNames, taskExecutorClass,
			taskContextMap, serviceContext);
	}

	@Override
	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName,
			java.io.File file)
		throws com.liferay.portal.kernel.exception.PortalException {

		_backgroundTaskLocalService.addBackgroundTaskAttachment(
			userId, backgroundTaskId, fileName, file);
	}

	@Override
	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName,
			java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		_backgroundTaskLocalService.addBackgroundTaskAttachment(
			userId, backgroundTaskId, fileName, inputStream);
	}

	@Override
	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String sourceFileName,
			String title, java.io.File file)
		throws com.liferay.portal.kernel.exception.PortalException {

		_backgroundTaskLocalService.addBackgroundTaskAttachment(
			userId, backgroundTaskId, sourceFileName, title, file);
	}

	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		amendBackgroundTask(
			long backgroundTaskId,
			java.util.Map<String, java.io.Serializable> taskContextMap,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _backgroundTaskLocalService.amendBackgroundTask(
			backgroundTaskId, taskContextMap, status, serviceContext);
	}

	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		amendBackgroundTask(
			long backgroundTaskId,
			java.util.Map<String, java.io.Serializable> taskContextMap,
			String errorStackTrace, int status, String statusMessage,
			com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _backgroundTaskLocalService.amendBackgroundTask(
			backgroundTaskId, taskContextMap, errorStackTrace, status,
			statusMessage, serviceContext);
	}

	@Override
	public void cleanUpBackgroundTask(long backgroundTaskId, int status) {
		_backgroundTaskLocalService.cleanUpBackgroundTask(
			backgroundTaskId, status);
	}

	@Override
	public void cleanUpBackgroundTasks() {
		_backgroundTaskLocalService.cleanUpBackgroundTasks();
	}

	/**
	 * Creates a new background task with the primary key. Does not add the background task to the database.
	 *
	 * @param backgroundTaskId the primary key for the new background task
	 * @return the new background task
	 */
	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		createBackgroundTask(long backgroundTaskId) {

		return _backgroundTaskLocalService.createBackgroundTask(
			backgroundTaskId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _backgroundTaskLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
			deleteBackgroundTask(
				com.liferay.portal.background.task.model.BackgroundTask
					backgroundTask)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _backgroundTaskLocalService.deleteBackgroundTask(backgroundTask);
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
	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
			deleteBackgroundTask(long backgroundTaskId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _backgroundTaskLocalService.deleteBackgroundTask(
			backgroundTaskId);
	}

	@Override
	public void deleteCompanyBackgroundTasks(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_backgroundTaskLocalService.deleteCompanyBackgroundTasks(companyId);
	}

	@Override
	public void deleteGroupBackgroundTasks(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_backgroundTaskLocalService.deleteGroupBackgroundTasks(groupId);
	}

	@Override
	public void deleteGroupBackgroundTasks(
			long groupId, String name, String taskExecutorClassName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_backgroundTaskLocalService.deleteGroupBackgroundTasks(
			groupId, name, taskExecutorClassName);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _backgroundTaskLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _backgroundTaskLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _backgroundTaskLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _backgroundTaskLocalService.dynamicQuery();
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

		return _backgroundTaskLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _backgroundTaskLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _backgroundTaskLocalService.dynamicQuery(
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

		return _backgroundTaskLocalService.dynamicQueryCount(dynamicQuery);
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

		return _backgroundTaskLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		fetchBackgroundTask(long backgroundTaskId) {

		return _backgroundTaskLocalService.fetchBackgroundTask(
			backgroundTaskId);
	}

	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		fetchFirstBackgroundTask(
			long groupId, String taskExecutorClassName, boolean completed,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.background.task.model.BackgroundTask>
					orderByComparator) {

		return _backgroundTaskLocalService.fetchFirstBackgroundTask(
			groupId, taskExecutorClassName, completed, orderByComparator);
	}

	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		fetchFirstBackgroundTask(String taskExecutorClassName, int status) {

		return _backgroundTaskLocalService.fetchFirstBackgroundTask(
			taskExecutorClassName, status);
	}

	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		fetchFirstBackgroundTask(
			String taskExecutorClassName, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.background.task.model.BackgroundTask>
					orderByComparator) {

		return _backgroundTaskLocalService.fetchFirstBackgroundTask(
			taskExecutorClassName, status, orderByComparator);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _backgroundTaskLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the background task with the primary key.
	 *
	 * @param backgroundTaskId the primary key of the background task
	 * @return the background task
	 * @throws PortalException if a background task with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
			getBackgroundTask(long backgroundTaskId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);
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
	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(int start, int end) {

		return _backgroundTaskLocalService.getBackgroundTasks(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(long groupId, int status) {

		return _backgroundTaskLocalService.getBackgroundTasks(groupId, status);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(long groupId, String taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupId, taskExecutorClassName);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long groupId, String taskExecutorClassName, boolean completed,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupId, taskExecutorClassName, completed, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long groupId, String taskExecutorClassName, int status) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupId, taskExecutorClassName, status);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long groupId, String taskExecutorClassName, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupId, taskExecutorClassName, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long groupId, String name, String taskExecutorClassName,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupId, name, taskExecutorClassName, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long groupId, String[] taskExecutorClassNames, int status) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupId, taskExecutorClassNames, status);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long[] groupIds, String name, String taskExecutorClassName,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupIds, name, taskExecutorClassName, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long[] groupIds, String name, String[] taskExecutorClassNames,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupIds, name, taskExecutorClassNames, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long[] groupIds, String[] taskExecutorClassNames) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupIds, taskExecutorClassNames);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long[] groupIds, String[] taskExecutorClassNames,
				boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupIds, taskExecutorClassNames, completed);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long[] groupIds, String[] taskExecutorClassNames,
				boolean completed, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupIds, taskExecutorClassNames, completed, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				long[] groupIds, String[] taskExecutorClassNames, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			groupIds, taskExecutorClassNames, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(String taskExecutorClassName, int status) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			taskExecutorClassName, status);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				String taskExecutorClassName, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			taskExecutorClassName, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(String[] taskExecutorClassNames, int status) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			taskExecutorClassNames, status);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasks(
				String[] taskExecutorClassNames, int status, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.background.task.model.BackgroundTask>
						orderByComparator) {

		return _backgroundTaskLocalService.getBackgroundTasks(
			taskExecutorClassNames, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasksByDuration(
				long[] groupIds, String[] taskExecutorClassNames,
				boolean completed, int start, int end, boolean orderByType) {

		return _backgroundTaskLocalService.getBackgroundTasksByDuration(
			groupIds, taskExecutorClassNames, completed, start, end,
			orderByType);
	}

	@Override
	public java.util.List
		<com.liferay.portal.background.task.model.BackgroundTask>
			getBackgroundTasksByDuration(
				long[] groupIds, String[] taskExecutorClassNames, int start,
				int end, boolean orderByType) {

		return _backgroundTaskLocalService.getBackgroundTasksByDuration(
			groupIds, taskExecutorClassNames, start, end, orderByType);
	}

	/**
	 * Returns the number of background tasks.
	 *
	 * @return the number of background tasks
	 */
	@Override
	public int getBackgroundTasksCount() {
		return _backgroundTaskLocalService.getBackgroundTasksCount();
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, taskExecutorClassName);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName, boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, taskExecutorClassName, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, name, taskExecutorClassName);
	}

	@Override
	public int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName,
		boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupId, name, taskExecutorClassName, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName,
		boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName, completed);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String[] taskExecutorClassName) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, name, taskExecutorClassName);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String[] taskExecutorClassNames) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, taskExecutorClassNames);
	}

	@Override
	public int getBackgroundTasksCount(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed) {

		return _backgroundTaskLocalService.getBackgroundTasksCount(
			groupIds, taskExecutorClassNames, completed);
	}

	@Override
	public String getBackgroundTaskStatusJSON(long backgroundTaskId) {
		return _backgroundTaskLocalService.getBackgroundTaskStatusJSON(
			backgroundTaskId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _backgroundTaskLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _backgroundTaskLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _backgroundTaskLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void resumeBackgroundTask(long backgroundTaskId) {
		_backgroundTaskLocalService.resumeBackgroundTask(backgroundTaskId);
	}

	@Override
	public void triggerBackgroundTask(long backgroundTaskId) {
		_backgroundTaskLocalService.triggerBackgroundTask(backgroundTaskId);
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
	@Override
	public com.liferay.portal.background.task.model.BackgroundTask
		updateBackgroundTask(
			com.liferay.portal.background.task.model.BackgroundTask
				backgroundTask) {

		return _backgroundTaskLocalService.updateBackgroundTask(backgroundTask);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _backgroundTaskLocalService.getBasePersistence();
	}

	@Override
	public BackgroundTaskLocalService getWrappedService() {
		return _backgroundTaskLocalService;
	}

	@Override
	public void setWrappedService(
		BackgroundTaskLocalService backgroundTaskLocalService) {

		_backgroundTaskLocalService = backgroundTaskLocalService;
	}

	private BackgroundTaskLocalService _backgroundTaskLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-506089697