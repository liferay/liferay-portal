/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.background.task.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.model.BackgroundTaskTable;
import com.liferay.portal.background.task.model.impl.BackgroundTaskImpl;
import com.liferay.portal.background.task.model.impl.BackgroundTaskModelImpl;
import com.liferay.portal.background.task.service.persistence.BackgroundTaskPersistence;
import com.liferay.portal.background.task.service.persistence.BackgroundTaskUtil;
import com.liferay.portal.background.task.service.persistence.impl.constants.BackgroundTaskPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the background task service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = BackgroundTaskPersistence.class)
public class BackgroundTaskPersistenceImpl
	extends BasePersistenceImpl<BackgroundTask, NoSuchBackgroundTaskException>
	implements BackgroundTaskPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BackgroundTaskUtil</code> to access the background task persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BackgroundTaskImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByGroupId_First(
			long groupId, OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByGroupId_First(
		long groupId, OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the background tasks where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of background tasks where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the background tasks where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByCompanyId_First(
			long companyId, OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first background task in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByCompanyId_First(
		long companyId, OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the background tasks where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of background tasks where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByCompleted;

	/**
	 * Returns an ordered range of all the background tasks where completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param completed the completed
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByCompleted(
		boolean completed, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompleted.find(
			finderCache, new Object[] {completed}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where completed = &#63;.
	 *
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByCompleted_First(
			boolean completed,
			OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		return _collectionPersistenceFinderByCompleted.findFirst(
			finderCache, new Object[] {completed}, orderByComparator);
	}

	/**
	 * Returns the first background task in the ordered set where completed = &#63;.
	 *
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByCompleted_First(
		boolean completed,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByCompleted.fetchFirst(
			finderCache, new Object[] {completed}, orderByComparator);
	}

	/**
	 * Removes all the background tasks where completed = &#63; from the database.
	 *
	 * @param completed the completed
	 */
	@Override
	public void removeByCompleted(boolean completed) {
		_collectionPersistenceFinderByCompleted.remove(
			finderCache, new Object[] {completed});
	}

	/**
	 * Returns the number of background tasks where completed = &#63;.
	 *
	 * @param completed the completed
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByCompleted(boolean completed) {
		return _collectionPersistenceFinderByCompleted.count(
			finderCache, new Object[] {completed});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByStatus;

	/**
	 * Returns an ordered range of all the background tasks where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByStatus(
		int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStatus.find(
			finderCache, new Object[] {status}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByStatus_First(
			int status, OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		return _collectionPersistenceFinderByStatus.findFirst(
			finderCache, new Object[] {status}, orderByComparator);
	}

	/**
	 * Returns the first background task in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByStatus_First(
		int status, OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByStatus.fetchFirst(
			finderCache, new Object[] {status}, orderByComparator);
	}

	/**
	 * Removes all the background tasks where status = &#63; from the database.
	 *
	 * @param status the status
	 */
	@Override
	public void removeByStatus(int status) {
		_collectionPersistenceFinderByStatus.remove(
			finderCache, new Object[] {status});
	}

	/**
	 * Returns the number of background tasks where status = &#63;.
	 *
	 * @param status the status
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByStatus(int status) {
		return _collectionPersistenceFinderByStatus.count(
			finderCache, new Object[] {status});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and taskExecutorClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_T(
		long groupId, String taskExecutorClassName, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, new String[] {taskExecutorClassName}
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and taskExecutorClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByG_T_First(
			long groupId, String taskExecutorClassName,
			OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		BackgroundTask backgroundTask = fetchByG_T_First(
			groupId, taskExecutorClassName, orderByComparator);

		if (backgroundTask != null) {
			return backgroundTask;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", taskExecutorClassName=");
		sb.append(taskExecutorClassName);

		sb.append("}");

		throw new NoSuchBackgroundTaskException(sb.toString());
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and taskExecutorClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByG_T_First(
		long groupId, String taskExecutorClassName,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, new String[] {taskExecutorClassName}
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and taskExecutorClassName = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param taskExecutorClassNames the task executor class names
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_T(
		long[] groupIds, String[] taskExecutorClassNames, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(taskExecutorClassNames)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the background tasks where groupId = &#63; and taskExecutorClassName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 */
	@Override
	public void removeByG_T(long groupId, String taskExecutorClassName) {
		_collectionPersistenceFinderByG_T.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, new String[] {taskExecutorClassName}
			});
	}

	/**
	 * Returns the number of background tasks where groupId = &#63; and taskExecutorClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_T(long groupId, String taskExecutorClassName) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, new String[] {taskExecutorClassName}
			});
	}

	/**
	 * Returns the number of background tasks where groupId = any &#63; and taskExecutorClassName = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param taskExecutorClassNames the task executor class names
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_T(long[] groupIds, String[] taskExecutorClassNames) {
		return _collectionPersistenceFinderByG_T.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(taskExecutorClassNames)
			});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByG_S_First(
			long groupId, int status,
			OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Removes all the background tasks where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		_collectionPersistenceFinderByG_S.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of background tasks where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByT_S;

	/**
	 * Returns an ordered range of all the background tasks where taskExecutorClassName = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByT_S(
		String taskExecutorClassName, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_S.find(
			finderCache,
			new Object[] {new String[] {taskExecutorClassName}, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where taskExecutorClassName = &#63; and status = &#63;.
	 *
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByT_S_First(
			String taskExecutorClassName, int status,
			OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		BackgroundTask backgroundTask = fetchByT_S_First(
			taskExecutorClassName, status, orderByComparator);

		if (backgroundTask != null) {
			return backgroundTask;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("taskExecutorClassName=");
		sb.append(taskExecutorClassName);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBackgroundTaskException(sb.toString());
	}

	/**
	 * Returns the first background task in the ordered set where taskExecutorClassName = &#63; and status = &#63;.
	 *
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByT_S_First(
		String taskExecutorClassName, int status,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByT_S.fetchFirst(
			finderCache,
			new Object[] {new String[] {taskExecutorClassName}, status},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the background tasks where taskExecutorClassName = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param taskExecutorClassNames the task executor class names
	 * @param status the status
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByT_S(
		String[] taskExecutorClassNames, int status, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_S.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(taskExecutorClassNames), status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the background tasks where taskExecutorClassName = &#63; and status = &#63; from the database.
	 *
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 */
	@Override
	public void removeByT_S(String taskExecutorClassName, int status) {
		_collectionPersistenceFinderByT_S.remove(
			finderCache,
			new Object[] {new String[] {taskExecutorClassName}, status});
	}

	/**
	 * Returns the number of background tasks where taskExecutorClassName = &#63; and status = &#63;.
	 *
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByT_S(String taskExecutorClassName, int status) {
		return _collectionPersistenceFinderByT_S.count(
			finderCache,
			new Object[] {new String[] {taskExecutorClassName}, status});
	}

	/**
	 * Returns the number of background tasks where taskExecutorClassName = any &#63; and status = &#63;.
	 *
	 * @param taskExecutorClassNames the task executor class names
	 * @param status the status
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByT_S(String[] taskExecutorClassNames, int status) {
		return _collectionPersistenceFinderByT_S.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(taskExecutorClassNames), status
			});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByG_N_T;

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_N_T(
		long groupId, String name, String taskExecutorClassName, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_T.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, name, new String[] {taskExecutorClassName}
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByG_N_T_First(
			long groupId, String name, String taskExecutorClassName,
			OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		BackgroundTask backgroundTask = fetchByG_N_T_First(
			groupId, name, taskExecutorClassName, orderByComparator);

		if (backgroundTask != null) {
			return backgroundTask;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", taskExecutorClassName=");
		sb.append(taskExecutorClassName);

		sb.append("}");

		throw new NoSuchBackgroundTaskException(sb.toString());
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByG_N_T_First(
		long groupId, String name, String taskExecutorClassName,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByG_N_T.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, name, new String[] {taskExecutorClassName}
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param taskExecutorClassNames the task executor class names
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_N_T(
		long[] groupIds, String name, String[] taskExecutorClassNames,
		int start, int end, OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_T.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), name,
				ArrayUtil.sortedUnique(taskExecutorClassNames)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the background tasks where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 */
	@Override
	public void removeByG_N_T(
		long groupId, String name, String taskExecutorClassName) {

		_collectionPersistenceFinderByG_N_T.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, name, new String[] {taskExecutorClassName}
			});
	}

	/**
	 * Returns the number of background tasks where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_N_T(
		long groupId, String name, String taskExecutorClassName) {

		return _collectionPersistenceFinderByG_N_T.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, name, new String[] {taskExecutorClassName}
			});
	}

	/**
	 * Returns the number of background tasks where groupId = any &#63; and name = &#63; and taskExecutorClassName = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param taskExecutorClassNames the task executor class names
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_N_T(
		long[] groupIds, String name, String[] taskExecutorClassNames) {

		return _collectionPersistenceFinderByG_N_T.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), name,
				ArrayUtil.sortedUnique(taskExecutorClassNames)
			});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByG_T_C;

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_T_C(
		long groupId, String taskExecutorClassName, boolean completed,
		int start, int end, OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_C.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, new String[] {taskExecutorClassName},
				completed
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByG_T_C_First(
			long groupId, String taskExecutorClassName, boolean completed,
			OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		BackgroundTask backgroundTask = fetchByG_T_C_First(
			groupId, taskExecutorClassName, completed, orderByComparator);

		if (backgroundTask != null) {
			return backgroundTask;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", taskExecutorClassName=");
		sb.append(taskExecutorClassName);

		sb.append(", completed=");
		sb.append(completed);

		sb.append("}");

		throw new NoSuchBackgroundTaskException(sb.toString());
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByG_T_C_First(
		long groupId, String taskExecutorClassName, boolean completed,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByG_T_C.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, new String[] {taskExecutorClassName},
				completed
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and taskExecutorClassName = &#63; and completed = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param taskExecutorClassNames the task executor class names
	 * @param completed the completed
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_T_C(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed,
		int start, int end, OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_C.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(taskExecutorClassNames), completed
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the background tasks where groupId = &#63; and taskExecutorClassName = &#63; and completed = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 */
	@Override
	public void removeByG_T_C(
		long groupId, String taskExecutorClassName, boolean completed) {

		_collectionPersistenceFinderByG_T_C.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, new String[] {taskExecutorClassName},
				completed
			});
	}

	/**
	 * Returns the number of background tasks where groupId = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_T_C(
		long groupId, String taskExecutorClassName, boolean completed) {

		return _collectionPersistenceFinderByG_T_C.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, new String[] {taskExecutorClassName},
				completed
			});
	}

	/**
	 * Returns the number of background tasks where groupId = any &#63; and taskExecutorClassName = any &#63; and completed = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param taskExecutorClassNames the task executor class names
	 * @param completed the completed
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_T_C(
		long[] groupIds, String[] taskExecutorClassNames, boolean completed) {

		return _collectionPersistenceFinderByG_T_C.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds),
				ArrayUtil.sortedUnique(taskExecutorClassNames), completed
			});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByG_T_S;

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and taskExecutorClassName = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_T_S(
		long groupId, String taskExecutorClassName, int status, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_S.find(
			finderCache,
			new Object[] {
				groupId, new String[] {taskExecutorClassName}, status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and taskExecutorClassName = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByG_T_S_First(
			long groupId, String taskExecutorClassName, int status,
			OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		BackgroundTask backgroundTask = fetchByG_T_S_First(
			groupId, taskExecutorClassName, status, orderByComparator);

		if (backgroundTask != null) {
			return backgroundTask;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", taskExecutorClassName=");
		sb.append(taskExecutorClassName);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBackgroundTaskException(sb.toString());
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and taskExecutorClassName = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByG_T_S_First(
		long groupId, String taskExecutorClassName, int status,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByG_T_S.fetchFirst(
			finderCache,
			new Object[] {
				groupId, new String[] {taskExecutorClassName}, status
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and taskExecutorClassName = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassNames the task executor class names
	 * @param status the status
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_T_S(
		long groupId, String[] taskExecutorClassNames, int status, int start,
		int end, OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_S.find(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(taskExecutorClassNames), status
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the background tasks where groupId = &#63; and taskExecutorClassName = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 */
	@Override
	public void removeByG_T_S(
		long groupId, String taskExecutorClassName, int status) {

		_collectionPersistenceFinderByG_T_S.remove(
			finderCache,
			new Object[] {
				groupId, new String[] {taskExecutorClassName}, status
			});
	}

	/**
	 * Returns the number of background tasks where groupId = &#63; and taskExecutorClassName = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassName the task executor class name
	 * @param status the status
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_T_S(
		long groupId, String taskExecutorClassName, int status) {

		return _collectionPersistenceFinderByG_T_S.count(
			finderCache,
			new Object[] {
				groupId, new String[] {taskExecutorClassName}, status
			});
	}

	/**
	 * Returns the number of background tasks where groupId = &#63; and taskExecutorClassName = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param taskExecutorClassNames the task executor class names
	 * @param status the status
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_T_S(
		long groupId, String[] taskExecutorClassNames, int status) {

		return _collectionPersistenceFinderByG_T_S.count(
			finderCache,
			new Object[] {
				groupId, ArrayUtil.sortedUnique(taskExecutorClassNames), status
			});
	}

	private CollectionPersistenceFinder
		<BackgroundTask, NoSuchBackgroundTaskException>
			_collectionPersistenceFinderByG_N_T_C;

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_N_T_C(
		long groupId, String name, String taskExecutorClassName,
		boolean completed, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_T_C.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, name, taskExecutorClassName, completed
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task
	 * @throws NoSuchBackgroundTaskException if a matching background task could not be found
	 */
	@Override
	public BackgroundTask findByG_N_T_C_First(
			long groupId, String name, String taskExecutorClassName,
			boolean completed,
			OrderByComparator<BackgroundTask> orderByComparator)
		throws NoSuchBackgroundTaskException {

		BackgroundTask backgroundTask = fetchByG_N_T_C_First(
			groupId, name, taskExecutorClassName, completed, orderByComparator);

		if (backgroundTask != null) {
			return backgroundTask;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", taskExecutorClassName=");
		sb.append(taskExecutorClassName);

		sb.append(", completed=");
		sb.append(completed);

		sb.append("}");

		throw new NoSuchBackgroundTaskException(sb.toString());
	}

	/**
	 * Returns the first background task in the ordered set where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching background task, or <code>null</code> if a matching background task could not be found
	 */
	@Override
	public BackgroundTask fetchByG_N_T_C_First(
		long groupId, String name, String taskExecutorClassName,
		boolean completed,
		OrderByComparator<BackgroundTask> orderByComparator) {

		return _collectionPersistenceFinderByG_N_T_C.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, name, taskExecutorClassName, completed
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the background tasks where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63; and completed = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BackgroundTaskModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @param start the lower bound of the range of background tasks
	 * @param end the upper bound of the range of background tasks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching background tasks
	 */
	@Override
	public List<BackgroundTask> findByG_N_T_C(
		long[] groupIds, String name, String taskExecutorClassName,
		boolean completed, int start, int end,
		OrderByComparator<BackgroundTask> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N_T_C.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), name, taskExecutorClassName,
				completed
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the background tasks where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63; and completed = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 */
	@Override
	public void removeByG_N_T_C(
		long groupId, String name, String taskExecutorClassName,
		boolean completed) {

		_collectionPersistenceFinderByG_N_T_C.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, name, taskExecutorClassName, completed
			});
	}

	/**
	 * Returns the number of background tasks where groupId = &#63; and name = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_N_T_C(
		long groupId, String name, String taskExecutorClassName,
		boolean completed) {

		return _collectionPersistenceFinderByG_N_T_C.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, name, taskExecutorClassName, completed
			});
	}

	/**
	 * Returns the number of background tasks where groupId = any &#63; and name = &#63; and taskExecutorClassName = &#63; and completed = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param taskExecutorClassName the task executor class name
	 * @param completed the completed
	 * @return the number of matching background tasks
	 */
	@Override
	public int countByG_N_T_C(
		long[] groupIds, String name, String taskExecutorClassName,
		boolean completed) {

		return _collectionPersistenceFinderByG_N_T_C.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), name, taskExecutorClassName,
				completed
			});
	}

	public BackgroundTaskPersistenceImpl() {
		setModelClass(BackgroundTask.class);

		setModelImplClass(BackgroundTaskImpl.class);
		setModelPKClass(long.class);

		setTable(BackgroundTaskTable.INSTANCE);
	}

	/**
	 * Creates a new background task with the primary key. Does not add the background task to the database.
	 *
	 * @param backgroundTaskId the primary key for the new background task
	 * @return the new background task
	 */
	@Override
	public BackgroundTask create(long backgroundTaskId) {
		BackgroundTask backgroundTask = new BackgroundTaskImpl();

		backgroundTask.setNew(true);
		backgroundTask.setPrimaryKey(backgroundTaskId);

		backgroundTask.setCompanyId(CompanyThreadLocal.getCompanyId());

		return backgroundTask;
	}

	/**
	 * Removes the background task with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param backgroundTaskId the primary key of the background task
	 * @return the background task that was removed
	 * @throws NoSuchBackgroundTaskException if a background task with the primary key could not be found
	 */
	@Override
	public BackgroundTask remove(long backgroundTaskId)
		throws NoSuchBackgroundTaskException {

		return remove((Serializable)backgroundTaskId);
	}

	@Override
	protected BackgroundTask removeImpl(BackgroundTask backgroundTask) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(backgroundTask)) {
				backgroundTask = (BackgroundTask)session.get(
					BackgroundTaskImpl.class,
					backgroundTask.getPrimaryKeyObj());
			}

			if (backgroundTask != null) {
				session.delete(backgroundTask);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (backgroundTask != null) {
			clearCache(backgroundTask);
		}

		return backgroundTask;
	}

	@Override
	public BackgroundTask updateImpl(BackgroundTask backgroundTask) {
		boolean isNew = backgroundTask.isNew();

		if (!(backgroundTask instanceof BackgroundTaskModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(backgroundTask.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					backgroundTask);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in backgroundTask proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BackgroundTask implementation " +
					backgroundTask.getClass());
		}

		BackgroundTaskModelImpl backgroundTaskModelImpl =
			(BackgroundTaskModelImpl)backgroundTask;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (backgroundTask.getCreateDate() == null)) {
			if (serviceContext == null) {
				backgroundTask.setCreateDate(date);
			}
			else {
				backgroundTask.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!backgroundTaskModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				backgroundTask.setModifiedDate(date);
			}
			else {
				backgroundTask.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(backgroundTask);
			}
			else {
				backgroundTask = (BackgroundTask)session.merge(backgroundTask);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(backgroundTask, false);

		if (isNew) {
			backgroundTask.setNew(false);
		}

		backgroundTask.resetOriginalValues();

		return backgroundTask;
	}

	/**
	 * Returns the background task with the primary key or throws a <code>NoSuchBackgroundTaskException</code> if it could not be found.
	 *
	 * @param backgroundTaskId the primary key of the background task
	 * @return the background task
	 * @throws NoSuchBackgroundTaskException if a background task with the primary key could not be found
	 */
	@Override
	public BackgroundTask findByPrimaryKey(long backgroundTaskId)
		throws NoSuchBackgroundTaskException {

		return findByPrimaryKey((Serializable)backgroundTaskId);
	}

	/**
	 * Returns the background task with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param backgroundTaskId the primary key of the background task
	 * @return the background task, or <code>null</code> if a background task with the primary key could not be found
	 */
	@Override
	public BackgroundTask fetchByPrimaryKey(long backgroundTaskId) {
		return fetchByPrimaryKey((Serializable)backgroundTaskId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "backgroundTaskId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BACKGROUNDTASK;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BackgroundTaskModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the background task persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_BACKGROUNDTASK_WHERE,
				_SQL_COUNT_BACKGROUNDTASK_WHERE,
				BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"backgroundTask.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, BackgroundTask::getGroupId));

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
				_SQL_SELECT_BACKGROUNDTASK_WHERE,
				_SQL_COUNT_BACKGROUNDTASK_WHERE,
				BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"backgroundTask.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, BackgroundTask::getCompanyId));

		_collectionPersistenceFinderByCompleted =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompleted",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"completed"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompleted", new String[] {Boolean.class.getName()},
					new String[] {"completed"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompleted", new String[] {Boolean.class.getName()},
					new String[] {"completed"}, false),
				_SQL_SELECT_BACKGROUNDTASK_WHERE,
				_SQL_COUNT_BACKGROUNDTASK_WHERE,
				BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"backgroundTask.", "completed", FinderColumn.Type.BOOLEAN,
					"=", true, true, BackgroundTask::isCompleted));

		_collectionPersistenceFinderByStatus =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByStatus",
					new String[] {
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByStatus",
					new String[] {Integer.class.getName()},
					new String[] {"status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByStatus",
					new String[] {Integer.class.getName()},
					new String[] {"status"}, false),
				_SQL_SELECT_BACKGROUNDTASK_WHERE,
				_SQL_COUNT_BACKGROUNDTASK_WHERE,
				BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"backgroundTask.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, BackgroundTask::getStatus));

		_collectionPersistenceFinderByG_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "taskExecutorClassName"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "taskExecutorClassName"}, 0, 2, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "taskExecutorClassName"}, 0, 2, false,
				null),
			_SQL_SELECT_BACKGROUNDTASK_WHERE, _SQL_COUNT_BACKGROUNDTASK_WHERE,
			BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new ArrayableFinderColumn<>(
				"backgroundTask.", "groupId", FinderColumn.Type.LONG, "=",
				false, true, true, BackgroundTask::getGroupId),
			new ArrayableFinderColumn<>(
				"backgroundTask.", "taskExecutorClassName",
				FinderColumn.Type.STRING, "=", false, true, true,
				BackgroundTask::getTaskExecutorClassName));

		_collectionPersistenceFinderByG_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, false),
			_SQL_SELECT_BACKGROUNDTASK_WHERE, _SQL_COUNT_BACKGROUNDTASK_WHERE,
			BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"backgroundTask.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, BackgroundTask::getGroupId),
			new FinderColumn<>(
				"backgroundTask.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, BackgroundTask::getStatus));

		_collectionPersistenceFinderByT_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_S",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"taskExecutorClassName", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_S",
				new String[] {String.class.getName(), Integer.class.getName()},
				new String[] {"taskExecutorClassName", "status"}, 0, 1, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByT_S",
				new String[] {String.class.getName(), Integer.class.getName()},
				new String[] {"taskExecutorClassName", "status"}, 0, 1, false,
				null),
			_SQL_SELECT_BACKGROUNDTASK_WHERE, _SQL_COUNT_BACKGROUNDTASK_WHERE,
			BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new ArrayableFinderColumn<>(
				"backgroundTask.", "taskExecutorClassName",
				FinderColumn.Type.STRING, "=", false, true, true,
				BackgroundTask::getTaskExecutorClassName),
			new FinderColumn<>(
				"backgroundTask.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, BackgroundTask::getStatus));

		_collectionPersistenceFinderByG_N_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "name", "taskExecutorClassName"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "name", "taskExecutorClassName"}, 0, 6,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_N_T",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "name", "taskExecutorClassName"}, 0, 6,
				false, null),
			_SQL_SELECT_BACKGROUNDTASK_WHERE, _SQL_COUNT_BACKGROUNDTASK_WHERE,
			BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new ArrayableFinderColumn<>(
				"backgroundTask.", "groupId", FinderColumn.Type.LONG, "=",
				false, true, true, BackgroundTask::getGroupId),
			new FinderColumn<>(
				"backgroundTask.", "name", FinderColumn.Type.STRING, "=", true,
				true, BackgroundTask::getName),
			new ArrayableFinderColumn<>(
				"backgroundTask.", "taskExecutorClassName",
				FinderColumn.Type.STRING, "=", false, true, true,
				BackgroundTask::getTaskExecutorClassName));

		_collectionPersistenceFinderByG_T_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "taskExecutorClassName", "completed"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "taskExecutorClassName", "completed"},
				0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "taskExecutorClassName", "completed"},
				0, 2, false, null),
			_SQL_SELECT_BACKGROUNDTASK_WHERE, _SQL_COUNT_BACKGROUNDTASK_WHERE,
			BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new ArrayableFinderColumn<>(
				"backgroundTask.", "groupId", FinderColumn.Type.LONG, "=",
				false, true, true, BackgroundTask::getGroupId),
			new ArrayableFinderColumn<>(
				"backgroundTask.", "taskExecutorClassName",
				FinderColumn.Type.STRING, "=", false, true, true,
				BackgroundTask::getTaskExecutorClassName),
			new FinderColumn<>(
				"backgroundTask.", "completed", FinderColumn.Type.BOOLEAN, "=",
				true, true, BackgroundTask::isCompleted));

		_collectionPersistenceFinderByG_T_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "taskExecutorClassName", "status"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "taskExecutorClassName", "status"}, 0,
				2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_T_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "taskExecutorClassName", "status"}, 0,
				2, false, null),
			_SQL_SELECT_BACKGROUNDTASK_WHERE, _SQL_COUNT_BACKGROUNDTASK_WHERE,
			BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"backgroundTask.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, BackgroundTask::getGroupId),
			new ArrayableFinderColumn<>(
				"backgroundTask.", "taskExecutorClassName",
				FinderColumn.Type.STRING, "=", false, true, true,
				BackgroundTask::getTaskExecutorClassName),
			new FinderColumn<>(
				"backgroundTask.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, BackgroundTask::getStatus));

		_collectionPersistenceFinderByG_N_T_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N_T_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "name", "taskExecutorClassName", "completed"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N_T_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "name", "taskExecutorClassName", "completed"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_N_T_C",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "name", "taskExecutorClassName", "completed"
					},
					0, 6, false, null),
				_SQL_SELECT_BACKGROUNDTASK_WHERE,
				_SQL_COUNT_BACKGROUNDTASK_WHERE,
				BackgroundTaskModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"backgroundTask.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, BackgroundTask::getGroupId),
				new FinderColumn<>(
					"backgroundTask.", "name", FinderColumn.Type.STRING, "=",
					true, true, BackgroundTask::getName),
				new FinderColumn<>(
					"backgroundTask.", "taskExecutorClassName",
					FinderColumn.Type.STRING, "=", true, true,
					BackgroundTask::getTaskExecutorClassName),
				new FinderColumn<>(
					"backgroundTask.", "completed", FinderColumn.Type.BOOLEAN,
					"=", true, true, BackgroundTask::isCompleted));

		BackgroundTaskUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BackgroundTaskUtil.setPersistence(null);

		entityCache.removeCache(BackgroundTaskImpl.class.getName());
	}

	@Override
	@Reference(
		target = BackgroundTaskPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = BackgroundTaskPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = BackgroundTaskPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		BackgroundTaskModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BACKGROUNDTASK =
		"SELECT backgroundTask FROM BackgroundTask backgroundTask";

	private static final String _SQL_SELECT_BACKGROUNDTASK_WHERE =
		"SELECT backgroundTask FROM BackgroundTask backgroundTask WHERE ";

	private static final String _SQL_COUNT_BACKGROUNDTASK_WHERE =
		"SELECT COUNT(backgroundTask) FROM BackgroundTask backgroundTask WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BackgroundTask exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1048191047