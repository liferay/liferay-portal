/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchWorkflowInstanceLinkException;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the workflow instance link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowInstanceLinkUtil
 * @generated
 */
@ProviderType
public interface WorkflowInstanceLinkPersistence
	extends BasePersistence<WorkflowInstanceLink>,
			CTPersistence<WorkflowInstanceLink> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link WorkflowInstanceLinkUtil} to access the workflow instance link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or throws a <code>NoSuchWorkflowInstanceLinkException</code> if it could not be found.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink findByWorkflowInstanceId(
			long workflowInstanceId)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink fetchByWorkflowInstanceId(
		long workflowInstanceId);

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink fetchByWorkflowInstanceId(
		long workflowInstanceId, boolean useFinderCache);

	/**
	 * Removes the workflow instance link where workflowInstanceId = &#63; from the database.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the workflow instance link that was removed
	 */
	public WorkflowInstanceLink removeByWorkflowInstanceId(
			long workflowInstanceId)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Returns the number of workflow instance links where workflowInstanceId = &#63;.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the number of matching workflow instance links
	 */
	public int countByWorkflowInstanceId(long workflowInstanceId);

	/**
	 * Returns all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow instance links
	 */
	public java.util.List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId);

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
	public java.util.List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end);

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
	public java.util.List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public java.util.List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink findByC_C_First(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Returns the first workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink fetchByC_C_First(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

	/**
	 * Returns the last workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink findByC_C_Last(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Returns the last workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink fetchByC_C_Last(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public WorkflowInstanceLink[] findByC_C_PrevAndNext(
			long workflowInstanceLinkId, long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Removes all the workflow instance links where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public void removeByC_C(long companyId, long classNameId);

	/**
	 * Returns the number of workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow instance links
	 */
	public int countByC_C(long companyId, long classNameId);

	/**
	 * Returns all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow instance links
	 */
	public java.util.List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId);

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
	public java.util.List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end);

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
	public java.util.List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public java.util.List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator,
		boolean useFinderCache);

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
	public WorkflowInstanceLink findByG_C_C_First(
			long groupId, long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink fetchByG_C_C_First(
		long groupId, long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public WorkflowInstanceLink findByG_C_C_Last(
			long groupId, long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	public WorkflowInstanceLink fetchByG_C_C_Last(
		long groupId, long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public WorkflowInstanceLink[] findByG_C_C_PrevAndNext(
			long workflowInstanceLinkId, long groupId, long companyId,
			long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Removes all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public void removeByG_C_C(long groupId, long companyId, long classNameId);

	/**
	 * Returns the number of workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow instance links
	 */
	public int countByG_C_C(long groupId, long companyId, long classNameId);

	/**
	 * Returns all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching workflow instance links
	 */
	public java.util.List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK);

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
	public java.util.List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end);

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
	public java.util.List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public java.util.List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator,
		boolean useFinderCache);

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
	public WorkflowInstanceLink findByG_C_C_C_First(
			long groupId, long companyId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

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
	public WorkflowInstanceLink fetchByG_C_C_C_First(
		long groupId, long companyId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public WorkflowInstanceLink findByG_C_C_C_Last(
			long groupId, long companyId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

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
	public WorkflowInstanceLink fetchByG_C_C_C_Last(
		long groupId, long companyId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public WorkflowInstanceLink[] findByG_C_C_C_PrevAndNext(
			long workflowInstanceLinkId, long groupId, long companyId,
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Removes all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK);

	/**
	 * Returns the number of workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching workflow instance links
	 */
	public int countByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK);

	/**
	 * Caches the workflow instance link in the entity cache if it is enabled.
	 *
	 * @param workflowInstanceLink the workflow instance link
	 */
	public void cacheResult(WorkflowInstanceLink workflowInstanceLink);

	/**
	 * Caches the workflow instance links in the entity cache if it is enabled.
	 *
	 * @param workflowInstanceLinks the workflow instance links
	 */
	public void cacheResult(
		java.util.List<WorkflowInstanceLink> workflowInstanceLinks);

	/**
	 * Creates a new workflow instance link with the primary key. Does not add the workflow instance link to the database.
	 *
	 * @param workflowInstanceLinkId the primary key for the new workflow instance link
	 * @return the new workflow instance link
	 */
	public WorkflowInstanceLink create(long workflowInstanceLinkId);

	/**
	 * Removes the workflow instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link that was removed
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	public WorkflowInstanceLink remove(long workflowInstanceLinkId)
		throws NoSuchWorkflowInstanceLinkException;

	public WorkflowInstanceLink updateImpl(
		WorkflowInstanceLink workflowInstanceLink);

	/**
	 * Returns the workflow instance link with the primary key or throws a <code>NoSuchWorkflowInstanceLinkException</code> if it could not be found.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	public WorkflowInstanceLink findByPrimaryKey(long workflowInstanceLinkId)
		throws NoSuchWorkflowInstanceLinkException;

	/**
	 * Returns the workflow instance link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link, or <code>null</code> if a workflow instance link with the primary key could not be found
	 */
	public WorkflowInstanceLink fetchByPrimaryKey(long workflowInstanceLinkId);

	/**
	 * Returns all the workflow instance links.
	 *
	 * @return the workflow instance links
	 */
	public java.util.List<WorkflowInstanceLink> findAll();

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
	public java.util.List<WorkflowInstanceLink> findAll(int start, int end);

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
	public java.util.List<WorkflowInstanceLink> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator);

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
	public java.util.List<WorkflowInstanceLink> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowInstanceLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the workflow instance links from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of workflow instance links.
	 *
	 * @return the number of workflow instance links
	 */
	public int countAll();

}