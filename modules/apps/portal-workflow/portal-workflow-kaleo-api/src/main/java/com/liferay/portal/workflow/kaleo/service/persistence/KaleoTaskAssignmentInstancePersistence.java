/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.exception.NoSuchTaskAssignmentInstanceException;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the kaleo task assignment instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskAssignmentInstanceUtil
 * @generated
 */
@ProviderType
public interface KaleoTaskAssignmentInstancePersistence
	extends BasePersistence<KaleoTaskAssignmentInstance>,
			CTPersistence<KaleoTaskAssignmentInstance> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link KaleoTaskAssignmentInstanceUtil} to access the kaleo task assignment instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public java.util.List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException;

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator);

	/**
	 * Removes all the kaleo task assignment instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of kaleo task assignment instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo task assignment instances
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoDefinitionVersionId(
			long kaleoDefinitionVersionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException;

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator);

	/**
	 * Removes all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	public void removeByKaleoDefinitionVersionId(long kaleoDefinitionVersionId);

	/**
	 * Returns the number of kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo task assignment instances
	 */
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId);

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public java.util.List<KaleoTaskAssignmentInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance findByKaleoInstanceId_First(
			long kaleoInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException;

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance fetchByKaleoInstanceId_First(
		long kaleoInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator);

	/**
	 * Removes all the kaleo task assignment instances where kaleoInstanceId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 */
	public void removeByKaleoInstanceId(long kaleoInstanceId);

	/**
	 * Returns the number of kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the number of matching kaleo task assignment instances
	 */
	public int countByKaleoInstanceId(long kaleoInstanceId);

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoTaskInstanceTokenId(
			long kaleoTaskInstanceTokenId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance findByKaleoTaskInstanceTokenId_First(
			long kaleoTaskInstanceTokenId,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException;

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance fetchByKaleoTaskInstanceTokenId_First(
		long kaleoTaskInstanceTokenId,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator);

	/**
	 * Removes all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63; from the database.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 */
	public void removeByKaleoTaskInstanceTokenId(long kaleoTaskInstanceTokenId);

	/**
	 * Returns the number of kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the number of matching kaleo task assignment instances
	 */
	public int countByKaleoTaskInstanceTokenId(long kaleoTaskInstanceTokenId);

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param assigneeClassName the assignee class name
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public java.util.List<KaleoTaskAssignmentInstance> findByAssigneeClassName(
		String assigneeClassName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance findByAssigneeClassName_First(
			String assigneeClassName,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException;

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance fetchByAssigneeClassName_First(
		String assigneeClassName,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator);

	/**
	 * Removes all the kaleo task assignment instances where assigneeClassName = &#63; from the database.
	 *
	 * @param assigneeClassName the assignee class name
	 */
	public void removeByAssigneeClassName(String assigneeClassName);

	/**
	 * Returns the number of kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @return the number of matching kaleo task assignment instances
	 */
	public int countByAssigneeClassName(String assigneeClassName);

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public java.util.List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance findByG_ACPK_First(
			long groupId, long assigneeClassPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException;

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance fetchByG_ACPK_First(
		long groupId, long assigneeClassPK,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator);

	/**
	 * Removes all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 */
	public void removeByG_ACPK(long groupId, long assigneeClassPK);

	/**
	 * Returns the number of kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @return the number of matching kaleo task assignment instances
	 */
	public int countByG_ACPK(long groupId, long assigneeClassPK);

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public java.util.List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance findByACN_ACPK_First(
			String assigneeClassName, long assigneeClassPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator)
		throws NoSuchTaskAssignmentInstanceException;

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public KaleoTaskAssignmentInstance fetchByACN_ACPK_First(
		String assigneeClassName, long assigneeClassPK,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator);

	/**
	 * Removes all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63; from the database.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 */
	public void removeByACN_ACPK(
		String assigneeClassName, long assigneeClassPK);

	/**
	 * Returns the number of kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @return the number of matching kaleo task assignment instances
	 */
	public int countByACN_ACPK(String assigneeClassName, long assigneeClassPK);

	/**
	 * Creates a new kaleo task assignment instance with the primary key. Does not add the kaleo task assignment instance to the database.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key for the new kaleo task assignment instance
	 * @return the new kaleo task assignment instance
	 */
	public KaleoTaskAssignmentInstance create(
		long kaleoTaskAssignmentInstanceId);

	/**
	 * Removes the kaleo task assignment instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was removed
	 * @throws NoSuchTaskAssignmentInstanceException if a kaleo task assignment instance with the primary key could not be found
	 */
	public KaleoTaskAssignmentInstance remove(
			long kaleoTaskAssignmentInstanceId)
		throws NoSuchTaskAssignmentInstanceException;

	public KaleoTaskAssignmentInstance updateImpl(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance);

	/**
	 * Returns the kaleo task assignment instance with the primary key or throws a <code>NoSuchTaskAssignmentInstanceException</code> if it could not be found.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a kaleo task assignment instance with the primary key could not be found
	 */
	public KaleoTaskAssignmentInstance findByPrimaryKey(
			long kaleoTaskAssignmentInstanceId)
		throws NoSuchTaskAssignmentInstanceException;

	/**
	 * Returns the kaleo task assignment instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance, or <code>null</code> if a kaleo task assignment instance with the primary key could not be found
	 */
	public KaleoTaskAssignmentInstance fetchByPrimaryKey(
		long kaleoTaskAssignmentInstanceId);

	/**
	 * Returns all the kaleo task assignment instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId) {

		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {

		return findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoDefinitionVersionId(
			long kaleoDefinitionVersionId, int start, int end) {

		return findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoDefinitionVersionId(
			long kaleoDefinitionVersionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator) {

		return findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoInstanceId(long kaleoInstanceId) {

		return findByKaleoInstanceId(
			kaleoInstanceId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoInstanceId(long kaleoInstanceId, int start, int end) {

		return findByKaleoInstanceId(kaleoInstanceId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoInstanceId(
			long kaleoInstanceId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator) {

		return findByKaleoInstanceId(
			kaleoInstanceId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoTaskInstanceTokenId(long kaleoTaskInstanceTokenId) {

		return findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoTaskInstanceTokenId(
			long kaleoTaskInstanceTokenId, int start, int end) {

		return findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByKaleoTaskInstanceTokenId(
			long kaleoTaskInstanceTokenId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator) {

		return findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @return the matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByAssigneeClassName(String assigneeClassName) {

		return findByAssigneeClassName(
			assigneeClassName,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param assigneeClassName the assignee class name
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByAssigneeClassName(String assigneeClassName, int start, int end) {

		return findByAssigneeClassName(
			assigneeClassName, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param assigneeClassName the assignee class name
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance>
		findByAssigneeClassName(
			String assigneeClassName, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoTaskAssignmentInstance> orderByComparator) {

		return findByAssigneeClassName(
			assigneeClassName, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @return the matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK) {

		return findByG_ACPK(
			groupId, assigneeClassPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK, int start, int end) {

		return findByG_ACPK(groupId, assigneeClassPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator) {

		return findByG_ACPK(
			groupId, assigneeClassPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @return the matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK) {

		return findByACN_ACPK(
			assigneeClassName, assigneeClassPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK, int start, int end) {

		return findByACN_ACPK(
			assigneeClassName, assigneeClassPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public default java.util.List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator) {

		return findByACN_ACPK(
			assigneeClassName, assigneeClassPK, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1051210863