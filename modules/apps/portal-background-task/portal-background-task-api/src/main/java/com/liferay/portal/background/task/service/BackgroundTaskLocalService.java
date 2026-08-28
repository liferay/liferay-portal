/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for BackgroundTask. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see BackgroundTaskLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface BackgroundTaskLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.background.task.service.impl.BackgroundTaskLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the background task local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link BackgroundTaskLocalServiceUtil} if injection and service tracking are not available.
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
	@Indexable(type = IndexableType.REINDEX)
	public BackgroundTask addBackgroundTask(BackgroundTask backgroundTask);

	public BackgroundTask addBackgroundTask(
			long userId, long groupId, String name,
			String taskExecutorClassName,
			Map<String, Serializable> taskContextMap,
			ServiceContext serviceContext)
		throws PortalException;

	public BackgroundTask addBackgroundTask(
			long userId, long groupId, String name,
			String[] servletContextNames, Class<?> taskExecutorClass,
			Map<String, Serializable> taskContextMap,
			ServiceContext serviceContext)
		throws PortalException;

	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName, File file)
		throws PortalException;

	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String fileName,
			InputStream inputStream)
		throws PortalException;

	public void addBackgroundTaskAttachment(
			long userId, long backgroundTaskId, String sourceFileName,
			String title, File file)
		throws PortalException;

	public BackgroundTask amendBackgroundTask(
		long backgroundTaskId, Map<String, Serializable> taskContextMap,
		int status, ServiceContext serviceContext);

	public BackgroundTask amendBackgroundTask(
		long backgroundTaskId, Map<String, Serializable> taskContextMap,
		String errorStackTrace, int status, String statusMessage,
		ServiceContext serviceContext);

	@Clusterable(onMaster = true)
	public void cleanUpBackgroundTask(long backgroundTaskId, int status);

	@Clusterable(onMaster = true)
	public void cleanUpBackgroundTasks();

	/**
	 * Creates a new background task with the primary key. Does not add the background task to the database.
	 *
	 * @param backgroundTaskId the primary key for the new background task
	 * @return the new background task
	 */
	@Transactional(enabled = false)
	public BackgroundTask createBackgroundTask(long backgroundTaskId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public BackgroundTask deleteBackgroundTask(BackgroundTask backgroundTask)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public BackgroundTask deleteBackgroundTask(long backgroundTaskId)
		throws PortalException;

	public void deleteCompanyBackgroundTasks(long companyId)
		throws PortalException;

	public void deleteGroupBackgroundTasks(long groupId) throws PortalException;

	public void deleteGroupBackgroundTasks(
			long groupId, String name, String taskExecutorClassName)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.background.task.model.impl.BackgroundTaskModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.background.task.model.impl.BackgroundTaskModelImpl</code>.
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
	public BackgroundTask fetchBackgroundTask(long backgroundTaskId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BackgroundTask fetchFirstBackgroundTask(
		long groupId, String taskExecutorClassName, boolean completed,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BackgroundTask fetchFirstBackgroundTask(
		String taskExecutorClassName, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BackgroundTask fetchFirstBackgroundTask(
		String taskExecutorClassName, int status,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the background task with the primary key.
	 *
	 * @param backgroundTaskId the primary key of the background task
	 * @return the background task
	 * @throws PortalException if a background task with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BackgroundTask getBackgroundTask(long backgroundTaskId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(long groupId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, boolean completed,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String taskExecutorClassName, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long groupId, String[] taskExecutorClassNames, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String name, String[] taskExecutorClassNames,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String[] taskExecutorClassNames);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed,
		int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		long[] groupIds, String[] taskExecutorClassNames, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		String taskExecutorClassName, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		String taskExecutorClassName, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		String[] taskExecutorClassNames, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasks(
		String[] taskExecutorClassNames, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasksByDuration(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed,
		int start, int end, boolean orderByType);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<BackgroundTask> getBackgroundTasksByDuration(
		long[] groupIds, String[] taskExecutorClassNames, int start, int end,
		boolean orderByType);

	/**
	 * Returns the number of background tasks.
	 *
	 * @return the number of background tasks
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long groupId, String taskExecutorClassName, boolean completed);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long groupId, String name, String taskExecutorClassName,
		boolean completed);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String taskExecutorClassName,
		boolean completed);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long[] groupIds, String name, String[] taskExecutorClassName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long[] groupIds, String[] taskExecutorClassNames);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getBackgroundTasksCount(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed);

	@Clusterable(onMaster = true)
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getBackgroundTaskStatusJSON(long backgroundTaskId);

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

	@Clusterable(onMaster = true)
	public void resumeBackgroundTask(long backgroundTaskId);

	@Clusterable(onMaster = true)
	public void triggerBackgroundTask(long backgroundTaskId);

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
	@Indexable(type = IndexableType.REINDEX)
	public BackgroundTask updateBackgroundTask(BackgroundTask backgroundTask);

}
// LIFERAY-SERVICE-BUILDER-HASH:2071560740