/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchWorkflowDefinitionLinkException;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the workflow definition link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowDefinitionLinkUtil
 * @generated
 */
@ProviderType
public interface WorkflowDefinitionLinkPersistence
	extends BasePersistence<WorkflowDefinitionLink>,
			CTPersistence<WorkflowDefinitionLink> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link WorkflowDefinitionLinkUtil} to access the workflow definition link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the workflow definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByUuid(String uuid);

	/**
	 * Returns a range of all the workflow definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink[] findByUuid_PrevAndNext(
			long workflowDefinitionLinkId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Removes all the workflow definition links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of workflow definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching workflow definition links
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByUUID_G(String uuid, long groupId)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the workflow definition link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the workflow definition link that was removed
	 */
	public WorkflowDefinitionLink removeByUUID_G(String uuid, long groupId)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the number of workflow definition links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching workflow definition links
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink[] findByUuid_C_PrevAndNext(
			long workflowDefinitionLinkId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Removes all the workflow definition links where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching workflow definition links
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the workflow definition links where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByCompanyId(
		long companyId);

	/**
	 * Returns a range of all the workflow definition links where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink[] findByCompanyId_PrevAndNext(
			long workflowDefinitionLinkId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Removes all the workflow definition links where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of workflow definition links where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching workflow definition links
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId);

	/**
	 * Returns a range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByG_C_C_First(
			long groupId, long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_C_First(
		long groupId, long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByG_C_C_Last(
			long groupId, long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_C_Last(
		long groupId, long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink[] findByG_C_C_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public void removeByG_C_C(long groupId, long companyId, long classNameId);

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow definition links
	 */
	public int countByG_C_C(long groupId, long companyId, long classNameId);

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @return the matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK);

	/**
	 * Returns a range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end);

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByG_C_CPK_First(
			long groupId, long companyId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_CPK_First(
		long groupId, long companyId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByG_C_CPK_Last(
			long groupId, long companyId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_CPK_Last(
		long groupId, long companyId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink[] findByG_C_CPK_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 */
	public void removeByG_C_CPK(long groupId, long companyId, long classPK);

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @return the number of matching workflow definition links
	 */
	public int countByG_C_CPK(long groupId, long companyId, long classPK);

	/**
	 * Returns all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @return the matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion);

	/**
	 * Returns a range of all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end);

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByC_W_W_First(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByC_W_W_First(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByC_W_W_Last(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByC_W_W_Last(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink[] findByC_W_W_PrevAndNext(
			long workflowDefinitionLinkId, long companyId,
			String workflowDefinitionName, int workflowDefinitionVersion,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Removes all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 */
	public void removeByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion);

	/**
	 * Returns the number of workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @return the number of matching workflow definition links
	 */
	public int countByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion);

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK);

	/**
	 * Returns a range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end);

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByG_C_C_C_First(
			long groupId, long companyId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_C_C_First(
		long groupId, long companyId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByG_C_C_C_Last(
			long groupId, long companyId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_C_C_Last(
		long groupId, long companyId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink[] findByG_C_C_C_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK);

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching workflow definition links
	 */
	public int countByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK);

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByG_C_C_W(
			long groupId, long companyId, long classNameId,
			String workflowDefinitionName)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName);

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName, boolean useFinderCache);

	/**
	 * Removes the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the workflow definition link that was removed
	 */
	public WorkflowDefinitionLink removeByG_C_C_W(
			long groupId, long companyId, long classNameId,
			String workflowDefinitionName)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the number of matching workflow definition links
	 */
	public int countByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName);

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByG_C_C_C_T(
			long groupId, long companyId, long classNameId, long classPK,
			long typePK)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK);

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK, boolean useFinderCache);

	/**
	 * Removes the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the workflow definition link that was removed
	 */
	public WorkflowDefinitionLink removeByG_C_C_C_T(
			long groupId, long companyId, long classNameId, long classPK,
			long typePK)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the number of matching workflow definition links
	 */
	public int countByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK);

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByERC_G(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public WorkflowDefinitionLink fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the workflow definition link that was removed
	 */
	public WorkflowDefinitionLink removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the number of workflow definition links where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching workflow definition links
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Caches the workflow definition link in the entity cache if it is enabled.
	 *
	 * @param workflowDefinitionLink the workflow definition link
	 */
	public void cacheResult(WorkflowDefinitionLink workflowDefinitionLink);

	/**
	 * Caches the workflow definition links in the entity cache if it is enabled.
	 *
	 * @param workflowDefinitionLinks the workflow definition links
	 */
	public void cacheResult(
		java.util.List<WorkflowDefinitionLink> workflowDefinitionLinks);

	/**
	 * Creates a new workflow definition link with the primary key. Does not add the workflow definition link to the database.
	 *
	 * @param workflowDefinitionLinkId the primary key for the new workflow definition link
	 * @return the new workflow definition link
	 */
	public WorkflowDefinitionLink create(long workflowDefinitionLinkId);

	/**
	 * Removes the workflow definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link that was removed
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink remove(long workflowDefinitionLinkId)
		throws NoSuchWorkflowDefinitionLinkException;

	public WorkflowDefinitionLink updateImpl(
		WorkflowDefinitionLink workflowDefinitionLink);

	/**
	 * Returns the workflow definition link with the primary key or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink findByPrimaryKey(
			long workflowDefinitionLinkId)
		throws NoSuchWorkflowDefinitionLinkException;

	/**
	 * Returns the workflow definition link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link, or <code>null</code> if a workflow definition link with the primary key could not be found
	 */
	public WorkflowDefinitionLink fetchByPrimaryKey(
		long workflowDefinitionLinkId);

	/**
	 * Returns all the workflow definition links.
	 *
	 * @return the workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findAll();

	/**
	 * Returns a range of all the workflow definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the workflow definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the workflow definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of workflow definition links
	 */
	public java.util.List<WorkflowDefinitionLink> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WorkflowDefinitionLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the workflow definition links from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of workflow definition links.
	 *
	 * @return the number of workflow definition links
	 */
	public int countAll();

}