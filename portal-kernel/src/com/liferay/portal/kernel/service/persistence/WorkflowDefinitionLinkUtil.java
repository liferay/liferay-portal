/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the workflow definition link service. This utility wraps <code>com.liferay.portal.service.persistence.impl.WorkflowDefinitionLinkPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowDefinitionLinkPersistence
 * @generated
 */
public class WorkflowDefinitionLinkUtil {

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
	public static void clearCache(
		WorkflowDefinitionLink workflowDefinitionLink) {

		getPersistence().clearCache(workflowDefinitionLink);
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
	public static Map<Serializable, WorkflowDefinitionLink> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<WorkflowDefinitionLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<WorkflowDefinitionLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<WorkflowDefinitionLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static WorkflowDefinitionLink update(
		WorkflowDefinitionLink workflowDefinitionLink) {

		return getPersistence().update(workflowDefinitionLink);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static WorkflowDefinitionLink update(
		WorkflowDefinitionLink workflowDefinitionLink,
		ServiceContext serviceContext) {

		return getPersistence().update(workflowDefinitionLink, serviceContext);
	}

	/**
	 * Returns all the workflow definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching workflow definition links
	 */
	public static List<WorkflowDefinitionLink> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

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
	public static List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

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
	public static List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

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
	public static List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink findByUuid_First(
			String uuid,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByUuid_First(
		String uuid,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink findByUuid_Last(
			String uuid,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByUuid_Last(
		String uuid,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public static WorkflowDefinitionLink[] findByUuid_PrevAndNext(
			long workflowDefinitionLinkId, String uuid,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByUuid_PrevAndNext(
			workflowDefinitionLinkId, uuid, orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching workflow definition links
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink findByUUID_G(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the workflow definition link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the workflow definition link that was removed
	 */
	public static WorkflowDefinitionLink removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching workflow definition links
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching workflow definition links
	 */
	public static List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

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
	public static List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

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
	public static List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

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
	public static List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

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
	public static WorkflowDefinitionLink[] findByUuid_C_PrevAndNext(
			long workflowDefinitionLinkId, String uuid, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByUuid_C_PrevAndNext(
			workflowDefinitionLinkId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching workflow definition links
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the workflow definition links where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching workflow definition links
	 */
	public static List<WorkflowDefinitionLink> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

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
	public static List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

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
	public static List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

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
	public static List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink findByCompanyId_First(
			long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByCompanyId_First(
		long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink findByCompanyId_Last(
			long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public static WorkflowDefinitionLink[] findByCompanyId_PrevAndNext(
			long workflowDefinitionLinkId, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByCompanyId_PrevAndNext(
			workflowDefinitionLinkId, companyId, orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of workflow definition links where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching workflow definition links
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow definition links
	 */
	public static List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId) {

		return getPersistence().findByG_C_C(groupId, companyId, classNameId);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end) {

		return getPersistence().findByG_C_C(
			groupId, companyId, classNameId, start, end);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findByG_C_C(
			groupId, companyId, classNameId, start, end, orderByComparator);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C(
			groupId, companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

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
	public static WorkflowDefinitionLink findByG_C_C_First(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_C_First(
			groupId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByG_C_C_First(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_First(
			groupId, companyId, classNameId, orderByComparator);
	}

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
	public static WorkflowDefinitionLink findByG_C_C_Last(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_C_Last(
			groupId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByG_C_C_Last(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_Last(
			groupId, companyId, classNameId, orderByComparator);
	}

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
	public static WorkflowDefinitionLink[] findByG_C_C_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classNameId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_C_PrevAndNext(
			workflowDefinitionLinkId, groupId, companyId, classNameId,
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; from the database.
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
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow definition links
	 */
	public static int countByG_C_C(
		long groupId, long companyId, long classNameId) {

		return getPersistence().countByG_C_C(groupId, companyId, classNameId);
	}

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @return the matching workflow definition links
	 */
	public static List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK) {

		return getPersistence().findByG_C_CPK(groupId, companyId, classPK);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end) {

		return getPersistence().findByG_C_CPK(
			groupId, companyId, classPK, start, end);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findByG_C_CPK(
			groupId, companyId, classPK, start, end, orderByComparator);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_CPK(
			groupId, companyId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

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
	public static WorkflowDefinitionLink findByG_C_CPK_First(
			long groupId, long companyId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_CPK_First(
			groupId, companyId, classPK, orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByG_C_CPK_First(
		long groupId, long companyId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByG_C_CPK_First(
			groupId, companyId, classPK, orderByComparator);
	}

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
	public static WorkflowDefinitionLink findByG_C_CPK_Last(
			long groupId, long companyId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_CPK_Last(
			groupId, companyId, classPK, orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByG_C_CPK_Last(
		long groupId, long companyId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByG_C_CPK_Last(
			groupId, companyId, classPK, orderByComparator);
	}

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
	public static WorkflowDefinitionLink[] findByG_C_CPK_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_CPK_PrevAndNext(
			workflowDefinitionLinkId, groupId, companyId, classPK,
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 */
	public static void removeByG_C_CPK(
		long groupId, long companyId, long classPK) {

		getPersistence().removeByG_C_CPK(groupId, companyId, classPK);
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @return the number of matching workflow definition links
	 */
	public static int countByG_C_CPK(
		long groupId, long companyId, long classPK) {

		return getPersistence().countByG_C_CPK(groupId, companyId, classPK);
	}

	/**
	 * Returns all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @return the matching workflow definition links
	 */
	public static List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		return getPersistence().findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion);
	}

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
	public static List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end) {

		return getPersistence().findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion, start,
			end);
	}

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
	public static List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion, start,
			end, orderByComparator);
	}

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
	public static List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion, start,
			end, orderByComparator, useFinderCache);
	}

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
	public static WorkflowDefinitionLink findByC_W_W_First(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByC_W_W_First(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByC_W_W_First(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByC_W_W_First(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			orderByComparator);
	}

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
	public static WorkflowDefinitionLink findByC_W_W_Last(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByC_W_W_Last(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			orderByComparator);
	}

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByC_W_W_Last(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByC_W_W_Last(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			orderByComparator);
	}

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
	public static WorkflowDefinitionLink[] findByC_W_W_PrevAndNext(
			long workflowDefinitionLinkId, long companyId,
			String workflowDefinitionName, int workflowDefinitionVersion,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByC_W_W_PrevAndNext(
			workflowDefinitionLinkId, companyId, workflowDefinitionName,
			workflowDefinitionVersion, orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 */
	public static void removeByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		getPersistence().removeByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion);
	}

	/**
	 * Returns the number of workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @return the number of matching workflow definition links
	 */
	public static int countByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		return getPersistence().countByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion);
	}

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching workflow definition links
	 */
	public static List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		return getPersistence().findByG_C_C_C(
			groupId, companyId, classNameId, classPK);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end) {

		return getPersistence().findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end,
			orderByComparator);
	}

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
	public static List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end,
			orderByComparator, useFinderCache);
	}

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
	public static WorkflowDefinitionLink findByG_C_C_C_First(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_C_C_First(
			groupId, companyId, classNameId, classPK, orderByComparator);
	}

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
	public static WorkflowDefinitionLink fetchByG_C_C_C_First(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_C_First(
			groupId, companyId, classNameId, classPK, orderByComparator);
	}

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
	public static WorkflowDefinitionLink findByG_C_C_C_Last(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_C_C_Last(
			groupId, companyId, classNameId, classPK, orderByComparator);
	}

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
	public static WorkflowDefinitionLink fetchByG_C_C_C_Last(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().fetchByG_C_C_C_Last(
			groupId, companyId, classNameId, classPK, orderByComparator);
	}

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
	public static WorkflowDefinitionLink[] findByG_C_C_C_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classNameId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_C_C_PrevAndNext(
			workflowDefinitionLinkId, groupId, companyId, classNameId, classPK,
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching workflow definition links
	 */
	public static int countByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		return getPersistence().countByG_C_C_C(
			groupId, companyId, classNameId, classPK);
	}

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
	public static WorkflowDefinitionLink findByG_C_C_W(
			long groupId, long companyId, long classNameId,
			String workflowDefinitionName)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName);
	}

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName) {

		return getPersistence().fetchByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName);
	}

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
	public static WorkflowDefinitionLink fetchByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName, boolean useFinderCache) {

		return getPersistence().fetchByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName,
			useFinderCache);
	}

	/**
	 * Removes the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the workflow definition link that was removed
	 */
	public static WorkflowDefinitionLink removeByG_C_C_W(
			long groupId, long companyId, long classNameId,
			String workflowDefinitionName)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().removeByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName);
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the number of matching workflow definition links
	 */
	public static int countByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName) {

		return getPersistence().countByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName);
	}

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
	public static WorkflowDefinitionLink findByG_C_C_C_T(
			long groupId, long companyId, long classNameId, long classPK,
			long typePK)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK);
	}

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
	public static WorkflowDefinitionLink fetchByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK) {

		return getPersistence().fetchByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK);
	}

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
	public static WorkflowDefinitionLink fetchByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK, boolean useFinderCache) {

		return getPersistence().fetchByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK, useFinderCache);
	}

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
	public static WorkflowDefinitionLink removeByG_C_C_C_T(
			long groupId, long companyId, long classNameId, long classPK,
			long typePK)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().removeByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK);
	}

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
	public static int countByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK) {

		return getPersistence().countByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK);
	}

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	public static WorkflowDefinitionLink fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the workflow definition link that was removed
	 */
	public static WorkflowDefinitionLink removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of workflow definition links where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching workflow definition links
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Caches the workflow definition link in the entity cache if it is enabled.
	 *
	 * @param workflowDefinitionLink the workflow definition link
	 */
	public static void cacheResult(
		WorkflowDefinitionLink workflowDefinitionLink) {

		getPersistence().cacheResult(workflowDefinitionLink);
	}

	/**
	 * Caches the workflow definition links in the entity cache if it is enabled.
	 *
	 * @param workflowDefinitionLinks the workflow definition links
	 */
	public static void cacheResult(
		List<WorkflowDefinitionLink> workflowDefinitionLinks) {

		getPersistence().cacheResult(workflowDefinitionLinks);
	}

	/**
	 * Creates a new workflow definition link with the primary key. Does not add the workflow definition link to the database.
	 *
	 * @param workflowDefinitionLinkId the primary key for the new workflow definition link
	 * @return the new workflow definition link
	 */
	public static WorkflowDefinitionLink create(long workflowDefinitionLinkId) {
		return getPersistence().create(workflowDefinitionLinkId);
	}

	/**
	 * Removes the workflow definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link that was removed
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public static WorkflowDefinitionLink remove(long workflowDefinitionLinkId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().remove(workflowDefinitionLinkId);
	}

	public static WorkflowDefinitionLink updateImpl(
		WorkflowDefinitionLink workflowDefinitionLink) {

		return getPersistence().updateImpl(workflowDefinitionLink);
	}

	/**
	 * Returns the workflow definition link with the primary key or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	public static WorkflowDefinitionLink findByPrimaryKey(
			long workflowDefinitionLinkId)
		throws com.liferay.portal.kernel.exception.
			NoSuchWorkflowDefinitionLinkException {

		return getPersistence().findByPrimaryKey(workflowDefinitionLinkId);
	}

	/**
	 * Returns the workflow definition link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link, or <code>null</code> if a workflow definition link with the primary key could not be found
	 */
	public static WorkflowDefinitionLink fetchByPrimaryKey(
		long workflowDefinitionLinkId) {

		return getPersistence().fetchByPrimaryKey(workflowDefinitionLinkId);
	}

	/**
	 * Returns all the workflow definition links.
	 *
	 * @return the workflow definition links
	 */
	public static List<WorkflowDefinitionLink> findAll() {
		return getPersistence().findAll();
	}

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
	public static List<WorkflowDefinitionLink> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

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
	public static List<WorkflowDefinitionLink> findAll(
		int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

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
	public static List<WorkflowDefinitionLink> findAll(
		int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the workflow definition links from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of workflow definition links.
	 *
	 * @return the number of workflow definition links
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static WorkflowDefinitionLinkPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		WorkflowDefinitionLinkPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile WorkflowDefinitionLinkPersistence _persistence;

}