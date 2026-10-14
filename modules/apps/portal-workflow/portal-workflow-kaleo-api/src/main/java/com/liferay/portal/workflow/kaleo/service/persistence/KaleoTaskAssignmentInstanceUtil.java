/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the kaleo task assignment instance service. This utility wraps <code>com.liferay.portal.workflow.kaleo.service.persistence.impl.KaleoTaskAssignmentInstancePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskAssignmentInstancePersistence
 * @generated
 */
public class KaleoTaskAssignmentInstanceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances) {

		getPersistence().cacheResult(kaleoTaskAssignmentInstances);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		getPersistence().cacheResult(kaleoTaskAssignmentInstance);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		getPersistence().clearCache(kaleoTaskAssignmentInstance);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, KaleoTaskAssignmentInstance>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<KaleoTaskAssignmentInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<KaleoTaskAssignmentInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<KaleoTaskAssignmentInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static KaleoTaskAssignmentInstance update(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		return getPersistence().update(kaleoTaskAssignmentInstance);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static KaleoTaskAssignmentInstance update(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance,
		ServiceContext serviceContext) {

		return getPersistence().update(
			kaleoTaskAssignmentInstance, serviceContext);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance fetchByCompanyId_First(
		long companyId,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of kaleo task assignment instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo task assignment instances
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance>
		findByKaleoDefinitionVersionId(
			long kaleoDefinitionVersionId, int start, int end,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance
			findByKaleoDefinitionVersionId_First(
				long kaleoDefinitionVersionId,
				OrderByComparator<KaleoTaskAssignmentInstance>
					orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().findByKaleoDefinitionVersionId_First(
			kaleoDefinitionVersionId, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance
		fetchByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().fetchByKaleoDefinitionVersionId_First(
			kaleoDefinitionVersionId, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	public static void removeByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		getPersistence().removeByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	/**
	 * Returns the number of kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo task assignment instances
	 */
	public static int countByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		return getPersistence().countByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKaleoInstanceId(
			kaleoInstanceId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance findByKaleoInstanceId_First(
			long kaleoInstanceId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().findByKaleoInstanceId_First(
			kaleoInstanceId, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance fetchByKaleoInstanceId_First(
		long kaleoInstanceId,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().fetchByKaleoInstanceId_First(
			kaleoInstanceId, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where kaleoInstanceId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 */
	public static void removeByKaleoInstanceId(long kaleoInstanceId) {
		getPersistence().removeByKaleoInstanceId(kaleoInstanceId);
	}

	/**
	 * Returns the number of kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the number of matching kaleo task assignment instances
	 */
	public static int countByKaleoInstanceId(long kaleoInstanceId) {
		return getPersistence().countByKaleoInstanceId(kaleoInstanceId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance>
		findByKaleoTaskInstanceTokenId(
			long kaleoTaskInstanceTokenId, int start, int end,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance
			findByKaleoTaskInstanceTokenId_First(
				long kaleoTaskInstanceTokenId,
				OrderByComparator<KaleoTaskAssignmentInstance>
					orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().findByKaleoTaskInstanceTokenId_First(
			kaleoTaskInstanceTokenId, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance
		fetchByKaleoTaskInstanceTokenId_First(
			long kaleoTaskInstanceTokenId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().fetchByKaleoTaskInstanceTokenId_First(
			kaleoTaskInstanceTokenId, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63; from the database.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 */
	public static void removeByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId) {

		getPersistence().removeByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId);
	}

	/**
	 * Returns the number of kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the number of matching kaleo task assignment instances
	 */
	public static int countByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId) {

		return getPersistence().countByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByAssigneeClassName(
		String assigneeClassName, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAssigneeClassName(
			assigneeClassName, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance findByAssigneeClassName_First(
			String assigneeClassName,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().findByAssigneeClassName_First(
			assigneeClassName, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance fetchByAssigneeClassName_First(
		String assigneeClassName,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().fetchByAssigneeClassName_First(
			assigneeClassName, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where assigneeClassName = &#63; from the database.
	 *
	 * @param assigneeClassName the assignee class name
	 */
	public static void removeByAssigneeClassName(String assigneeClassName) {
		getPersistence().removeByAssigneeClassName(assigneeClassName);
	}

	/**
	 * Returns the number of kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @return the number of matching kaleo task assignment instances
	 */
	public static int countByAssigneeClassName(String assigneeClassName) {
		return getPersistence().countByAssigneeClassName(assigneeClassName);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_ACPK(
			groupId, assigneeClassPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance findByG_ACPK_First(
			long groupId, long assigneeClassPK,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().findByG_ACPK_First(
			groupId, assigneeClassPK, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance fetchByG_ACPK_First(
		long groupId, long assigneeClassPK,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().fetchByG_ACPK_First(
			groupId, assigneeClassPK, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 */
	public static void removeByG_ACPK(long groupId, long assigneeClassPK) {
		getPersistence().removeByG_ACPK(groupId, assigneeClassPK);
	}

	/**
	 * Returns the number of kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @return the number of matching kaleo task assignment instances
	 */
	public static int countByG_ACPK(long groupId, long assigneeClassPK) {
		return getPersistence().countByG_ACPK(groupId, assigneeClassPK);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByACN_ACPK(
			assigneeClassName, assigneeClassPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance findByACN_ACPK_First(
			String assigneeClassName, long assigneeClassPK,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().findByACN_ACPK_First(
			assigneeClassName, assigneeClassPK, orderByComparator);
	}

	/**
	 * Returns the first kaleo task assignment instance in the ordered set where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task assignment instance, or <code>null</code> if a matching kaleo task assignment instance could not be found
	 */
	public static KaleoTaskAssignmentInstance fetchByACN_ACPK_First(
		String assigneeClassName, long assigneeClassPK,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().fetchByACN_ACPK_First(
			assigneeClassName, assigneeClassPK, orderByComparator);
	}

	/**
	 * Removes all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63; from the database.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 */
	public static void removeByACN_ACPK(
		String assigneeClassName, long assigneeClassPK) {

		getPersistence().removeByACN_ACPK(assigneeClassName, assigneeClassPK);
	}

	/**
	 * Returns the number of kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @return the number of matching kaleo task assignment instances
	 */
	public static int countByACN_ACPK(
		String assigneeClassName, long assigneeClassPK) {

		return getPersistence().countByACN_ACPK(
			assigneeClassName, assigneeClassPK);
	}

	/**
	 * Creates a new kaleo task assignment instance with the primary key. Does not add the kaleo task assignment instance to the database.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key for the new kaleo task assignment instance
	 * @return the new kaleo task assignment instance
	 */
	public static KaleoTaskAssignmentInstance create(
		long kaleoTaskAssignmentInstanceId) {

		return getPersistence().create(kaleoTaskAssignmentInstanceId);
	}

	/**
	 * Removes the kaleo task assignment instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was removed
	 * @throws NoSuchTaskAssignmentInstanceException if a kaleo task assignment instance with the primary key could not be found
	 */
	public static KaleoTaskAssignmentInstance remove(
			long kaleoTaskAssignmentInstanceId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().remove(kaleoTaskAssignmentInstanceId);
	}

	public static KaleoTaskAssignmentInstance updateImpl(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		return getPersistence().updateImpl(kaleoTaskAssignmentInstance);
	}

	/**
	 * Returns the kaleo task assignment instance with the primary key or throws a <code>NoSuchTaskAssignmentInstanceException</code> if it could not be found.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance
	 * @throws NoSuchTaskAssignmentInstanceException if a kaleo task assignment instance with the primary key could not be found
	 */
	public static KaleoTaskAssignmentInstance findByPrimaryKey(
			long kaleoTaskAssignmentInstanceId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskAssignmentInstanceException {

		return getPersistence().findByPrimaryKey(kaleoTaskAssignmentInstanceId);
	}

	/**
	 * Returns the kaleo task assignment instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance, or <code>null</code> if a kaleo task assignment instance with the primary key could not be found
	 */
	public static KaleoTaskAssignmentInstance fetchByPrimaryKey(
		long kaleoTaskAssignmentInstanceId) {

		return getPersistence().fetchByPrimaryKey(
			kaleoTaskAssignmentInstanceId);
	}

	/**
	 * Returns all the kaleo task assignment instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId) {

		return getPersistence().findByCompanyId(companyId);
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
	public static List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
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
	public static List<KaleoTaskAssignmentInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task assignment instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance>
		findByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
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
	public static List<KaleoTaskAssignmentInstance>
		findByKaleoDefinitionVersionId(
			long kaleoDefinitionVersionId, int start, int end) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end);
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
	public static List<KaleoTaskAssignmentInstance>
		findByKaleoDefinitionVersionId(
			long kaleoDefinitionVersionId, int start, int end,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task assignment instances where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByKaleoInstanceId(
		long kaleoInstanceId) {

		return getPersistence().findByKaleoInstanceId(kaleoInstanceId);
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
	public static List<KaleoTaskAssignmentInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end) {

		return getPersistence().findByKaleoInstanceId(
			kaleoInstanceId, start, end);
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
	public static List<KaleoTaskAssignmentInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().findByKaleoInstanceId(
			kaleoInstanceId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task assignment instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance>
		findByKaleoTaskInstanceTokenId(long kaleoTaskInstanceTokenId) {

		return getPersistence().findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId);
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
	public static List<KaleoTaskAssignmentInstance>
		findByKaleoTaskInstanceTokenId(
			long kaleoTaskInstanceTokenId, int start, int end) {

		return getPersistence().findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId, start, end);
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
	public static List<KaleoTaskAssignmentInstance>
		findByKaleoTaskInstanceTokenId(
			long kaleoTaskInstanceTokenId, int start, int end,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task assignment instances where assigneeClassName = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @return the matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByAssigneeClassName(
		String assigneeClassName) {

		return getPersistence().findByAssigneeClassName(assigneeClassName);
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
	public static List<KaleoTaskAssignmentInstance> findByAssigneeClassName(
		String assigneeClassName, int start, int end) {

		return getPersistence().findByAssigneeClassName(
			assigneeClassName, start, end);
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
	public static List<KaleoTaskAssignmentInstance> findByAssigneeClassName(
		String assigneeClassName, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().findByAssigneeClassName(
			assigneeClassName, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task assignment instances where groupId = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assigneeClassPK the assignee class pk
	 * @return the matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK) {

		return getPersistence().findByG_ACPK(groupId, assigneeClassPK);
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
	public static List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK, int start, int end) {

		return getPersistence().findByG_ACPK(
			groupId, assigneeClassPK, start, end);
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
	public static List<KaleoTaskAssignmentInstance> findByG_ACPK(
		long groupId, long assigneeClassPK, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().findByG_ACPK(
			groupId, assigneeClassPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task assignment instances where assigneeClassName = &#63; and assigneeClassPK = &#63;.
	 *
	 * @param assigneeClassName the assignee class name
	 * @param assigneeClassPK the assignee class pk
	 * @return the matching kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK) {

		return getPersistence().findByACN_ACPK(
			assigneeClassName, assigneeClassPK);
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
	public static List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK, int start, int end) {

		return getPersistence().findByACN_ACPK(
			assigneeClassName, assigneeClassPK, start, end);
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
	public static List<KaleoTaskAssignmentInstance> findByACN_ACPK(
		String assigneeClassName, long assigneeClassPK, int start, int end,
		OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getPersistence().findByACN_ACPK(
			assigneeClassName, assigneeClassPK, start, end, orderByComparator);
	}

	public static KaleoTaskAssignmentInstancePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		KaleoTaskAssignmentInstancePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile KaleoTaskAssignmentInstancePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1965124120