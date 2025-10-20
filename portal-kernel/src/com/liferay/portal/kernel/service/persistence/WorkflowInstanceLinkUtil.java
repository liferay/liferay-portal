/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the workflow instance link service. This utility wraps <code>com.liferay.portal.service.persistence.impl.WorkflowInstanceLinkPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowInstanceLinkPersistence
 * @generated
 */
public class WorkflowInstanceLinkUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(WorkflowInstanceLink workflowInstanceLink) {
		getPersistence().clearCache(workflowInstanceLink);
	}

	/**
	 * @see BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, WorkflowInstanceLink> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<WorkflowInstanceLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<WorkflowInstanceLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<WorkflowInstanceLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static WorkflowInstanceLink update(
		WorkflowInstanceLink workflowInstanceLink) {

		return getPersistence().update(workflowInstanceLink);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static WorkflowInstanceLink update(
		WorkflowInstanceLink workflowInstanceLink,
		ServiceContext serviceContext) {

		return getPersistence().update(workflowInstanceLink, serviceContext);
	}

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or throws a <code>NoSuchWorkflowInstanceLinkException</code> if it could not be found.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink findByWorkflowInstanceId(
			long workflowInstanceId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByWorkflowInstanceId(workflowInstanceId);
	}

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink fetchByWorkflowInstanceId(
		long workflowInstanceId) {

		return getPersistence().fetchByWorkflowInstanceId(workflowInstanceId);
	}

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink fetchByWorkflowInstanceId(
		long workflowInstanceId, boolean useFinderCache) {

		return getPersistence().fetchByWorkflowInstanceId(
			workflowInstanceId, useFinderCache);
	}

	/**
	 * Removes the workflow instance link where workflowInstanceId = &#63; from the database.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the workflow instance link that was removed
	 */
	public static WorkflowInstanceLink removeByWorkflowInstanceId(
			long workflowInstanceId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().removeByWorkflowInstanceId(workflowInstanceId);
	}

	/**
	 * Returns the number of workflow instance links where workflowInstanceId = &#63;.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the number of matching workflow instance links
	 */
	public static int countByWorkflowInstanceId(long workflowInstanceId) {
		return getPersistence().countByWorkflowInstanceId(workflowInstanceId);
	}

	/**
	 * Returns all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId) {

		return getPersistence().findByC_C(companyId, classNameId);
	}

	/**
	 * Returns a range of all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end) {

		return getPersistence().findByC_C(companyId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().findByC_C(
			companyId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByC_C_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink findByC_C_Last(
			long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByC_C_Last(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink fetchByC_C_Last(
		long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().fetchByC_C_Last(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the workflow instance links before and after the current workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param workflowInstanceLinkId the primary key of the current workflow instance link
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	public static WorkflowInstanceLink[] findByC_C_PrevAndNext(
			long workflowInstanceLinkId, long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByC_C_PrevAndNext(
			workflowInstanceLinkId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the workflow instance links where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public static void removeByC_C(long companyId, long classNameId) {
		getPersistence().removeByC_C(companyId, classNameId);
	}

	/**
	 * Returns the number of workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow instance links
	 */
	public static int countByC_C(long companyId, long classNameId) {
		return getPersistence().countByC_C(companyId, classNameId);
	}

	/**
	 * Returns all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId) {

		return getPersistence().findByG_C_C(groupId, companyId, classNameId);
	}

	/**
	 * Returns a range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end) {

		return getPersistence().findByG_C_C(
			groupId, companyId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().findByG_C_C(
			groupId, companyId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C(
			groupId, companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink findByG_C_C_First(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByG_C_C_First(
			groupId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink fetchByG_C_C_First(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_First(
			groupId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink findByG_C_C_Last(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByG_C_C_Last(
			groupId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink fetchByG_C_C_Last(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_Last(
			groupId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the workflow instance links before and after the current workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param workflowInstanceLinkId the primary key of the current workflow instance link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	public static WorkflowInstanceLink[] findByG_C_C_PrevAndNext(
			long workflowInstanceLinkId, long groupId, long companyId,
			long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByG_C_C_PrevAndNext(
			workflowInstanceLinkId, groupId, companyId, classNameId,
			orderByComparator);
	}

	/**
	 * Removes all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public static void removeByG_C_C(
		long groupId, long companyId, long classNameId) {

		getPersistence().removeByG_C_C(groupId, companyId, classNameId);
	}

	/**
	 * Returns the number of workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow instance links
	 */
	public static int countByG_C_C(
		long groupId, long companyId, long classNameId) {

		return getPersistence().countByG_C_C(groupId, companyId, classNameId);
	}

	/**
	 * Returns all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		return getPersistence().findByG_C_C_C(
			groupId, companyId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end) {

		return getPersistence().findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	public static List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink findByG_C_C_C_First(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByG_C_C_C_First(
			groupId, companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink fetchByG_C_C_C_First(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_C_First(
			groupId, companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink findByG_C_C_C_Last(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByG_C_C_C_Last(
			groupId, companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public static WorkflowInstanceLink fetchByG_C_C_C_Last(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_C_Last(
			groupId, companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the workflow instance links before and after the current workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param workflowInstanceLinkId the primary key of the current workflow instance link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	public static WorkflowInstanceLink[] findByG_C_C_C_PrevAndNext(
			long workflowInstanceLinkId, long groupId, long companyId,
			long classNameId, long classPK,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByG_C_C_C_PrevAndNext(
			workflowInstanceLinkId, groupId, companyId, classNameId, classPK,
			orderByComparator);
	}

	/**
	 * Removes all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		getPersistence().removeByG_C_C_C(
			groupId, companyId, classNameId, classPK);
	}

	/**
	 * Returns the number of workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching workflow instance links
	 */
	public static int countByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		return getPersistence().countByG_C_C_C(
			groupId, companyId, classNameId, classPK);
	}

	/**
	 * Caches the workflow instance link in the entity cache if it is enabled.
	 *
	 * @param workflowInstanceLink the workflow instance link
	 */
	public static void cacheResult(WorkflowInstanceLink workflowInstanceLink) {
		getPersistence().cacheResult(workflowInstanceLink);
	}

	/**
	 * Caches the workflow instance links in the entity cache if it is enabled.
	 *
	 * @param workflowInstanceLinks the workflow instance links
	 */
	public static void cacheResult(
		List<WorkflowInstanceLink> workflowInstanceLinks) {

		getPersistence().cacheResult(workflowInstanceLinks);
	}

	/**
	 * Creates a new workflow instance link with the primary key. Does not add the workflow instance link to the database.
	 *
	 * @param workflowInstanceLinkId the primary key for the new workflow instance link
	 * @return the new workflow instance link
	 */
	public static WorkflowInstanceLink create(long workflowInstanceLinkId) {
		return getPersistence().create(workflowInstanceLinkId);
	}

	/**
	 * Removes the workflow instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link that was removed
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	public static WorkflowInstanceLink remove(long workflowInstanceLinkId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().remove(workflowInstanceLinkId);
	}

	public static WorkflowInstanceLink updateImpl(
		WorkflowInstanceLink workflowInstanceLink) {

		return getPersistence().updateImpl(workflowInstanceLink);
	}

	/**
	 * Returns the workflow instance link with the primary key or throws a <code>NoSuchWorkflowInstanceLinkException</code> if it could not be found.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	public static WorkflowInstanceLink findByPrimaryKey(
			long workflowInstanceLinkId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowInstanceLinkException {

		return getPersistence().findByPrimaryKey(workflowInstanceLinkId);
	}

	/**
	 * Returns the workflow instance link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link, or <code>null</code> if a workflow instance link with the primary key could not be found
	 */
	public static WorkflowInstanceLink fetchByPrimaryKey(
		long workflowInstanceLinkId) {

		return getPersistence().fetchByPrimaryKey(workflowInstanceLinkId);
	}

	/**
	 * Returns all the workflow instance links.
	 *
	 * @return the workflow instance links
	 */
	public static List<WorkflowInstanceLink> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the workflow instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of workflow instance links
	 */
	public static List<WorkflowInstanceLink> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the workflow instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of workflow instance links
	 */
	public static List<WorkflowInstanceLink> findAll(
		int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the workflow instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of workflow instance links
	 */
	public static List<WorkflowInstanceLink> findAll(
		int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the workflow instance links from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of workflow instance links.
	 *
	 * @return the number of workflow instance links
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static WorkflowInstanceLinkPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		WorkflowInstanceLinkPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile WorkflowInstanceLinkPersistence _persistence;

}