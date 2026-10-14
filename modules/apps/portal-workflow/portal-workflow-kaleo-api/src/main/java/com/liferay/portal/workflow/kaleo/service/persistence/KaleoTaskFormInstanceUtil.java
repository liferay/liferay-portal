/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskFormInstance;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the kaleo task form instance service. This utility wraps <code>com.liferay.portal.workflow.kaleo.service.persistence.impl.KaleoTaskFormInstancePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskFormInstancePersistence
 * @generated
 */
public class KaleoTaskFormInstanceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<KaleoTaskFormInstance> kaleoTaskFormInstances) {

		getPersistence().cacheResult(kaleoTaskFormInstances);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		getPersistence().cacheResult(kaleoTaskFormInstance);
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
	public static void clearCache(KaleoTaskFormInstance kaleoTaskFormInstance) {
		getPersistence().clearCache(kaleoTaskFormInstance);
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
	public static Map<Serializable, KaleoTaskFormInstance> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<KaleoTaskFormInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<KaleoTaskFormInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<KaleoTaskFormInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static KaleoTaskFormInstance update(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		return getPersistence().update(kaleoTaskFormInstance);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static KaleoTaskFormInstance update(
		KaleoTaskFormInstance kaleoTaskFormInstance,
		ServiceContext serviceContext) {

		return getPersistence().update(kaleoTaskFormInstance, serviceContext);
	}

	/**
	 * Returns an ordered range of all the kaleo task form instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	public static KaleoTaskFormInstance findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskFormInstanceException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	public static KaleoTaskFormInstance fetchByCompanyId_First(
		long companyId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of kaleo task form instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo task form instances
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	public static KaleoTaskFormInstance findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskFormInstanceException {

		return getPersistence().findByKaleoDefinitionVersionId_First(
			kaleoDefinitionVersionId, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	public static KaleoTaskFormInstance fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().fetchByKaleoDefinitionVersionId_First(
			kaleoDefinitionVersionId, orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	public static void removeByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		getPersistence().removeByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	/**
	 * Returns the number of kaleo task form instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo task form instances
	 */
	public static int countByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		return getPersistence().countByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKaleoInstanceId(
			kaleoInstanceId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	public static KaleoTaskFormInstance findByKaleoInstanceId_First(
			long kaleoInstanceId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskFormInstanceException {

		return getPersistence().findByKaleoInstanceId_First(
			kaleoInstanceId, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	public static KaleoTaskFormInstance fetchByKaleoInstanceId_First(
		long kaleoInstanceId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().fetchByKaleoInstanceId_First(
			kaleoInstanceId, orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where kaleoInstanceId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 */
	public static void removeByKaleoInstanceId(long kaleoInstanceId) {
		getPersistence().removeByKaleoInstanceId(kaleoInstanceId);
	}

	/**
	 * Returns the number of kaleo task form instances where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the number of matching kaleo task form instances
	 */
	public static int countByKaleoInstanceId(long kaleoInstanceId) {
		return getPersistence().countByKaleoInstanceId(kaleoInstanceId);
	}

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a matching kaleo task form instance could not be found
	 */
	public static KaleoTaskFormInstance findByKaleoTaskInstanceTokenId_First(
			long kaleoTaskInstanceTokenId,
			OrderByComparator<KaleoTaskFormInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskFormInstanceException {

		return getPersistence().findByKaleoTaskInstanceTokenId_First(
			kaleoTaskInstanceTokenId, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form instance in the ordered set where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form instance, or <code>null</code> if a matching kaleo task form instance could not be found
	 */
	public static KaleoTaskFormInstance fetchByKaleoTaskInstanceTokenId_First(
		long kaleoTaskInstanceTokenId,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().fetchByKaleoTaskInstanceTokenId_First(
			kaleoTaskInstanceTokenId, orderByComparator);
	}

	/**
	 * Removes all the kaleo task form instances where kaleoTaskInstanceTokenId = &#63; from the database.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 */
	public static void removeByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId) {

		getPersistence().removeByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId);
	}

	/**
	 * Returns the number of kaleo task form instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the number of matching kaleo task form instances
	 */
	public static int countByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId) {

		return getPersistence().countByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId);
	}

	/**
	 * Creates a new kaleo task form instance with the primary key. Does not add the kaleo task form instance to the database.
	 *
	 * @param kaleoTaskFormInstanceId the primary key for the new kaleo task form instance
	 * @return the new kaleo task form instance
	 */
	public static KaleoTaskFormInstance create(long kaleoTaskFormInstanceId) {
		return getPersistence().create(kaleoTaskFormInstanceId);
	}

	/**
	 * Removes the kaleo task form instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance that was removed
	 * @throws NoSuchTaskFormInstanceException if a kaleo task form instance with the primary key could not be found
	 */
	public static KaleoTaskFormInstance remove(long kaleoTaskFormInstanceId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskFormInstanceException {

		return getPersistence().remove(kaleoTaskFormInstanceId);
	}

	public static KaleoTaskFormInstance updateImpl(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		return getPersistence().updateImpl(kaleoTaskFormInstance);
	}

	/**
	 * Returns the kaleo task form instance with the primary key or throws a <code>NoSuchTaskFormInstanceException</code> if it could not be found.
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance
	 * @throws NoSuchTaskFormInstanceException if a kaleo task form instance with the primary key could not be found
	 */
	public static KaleoTaskFormInstance findByPrimaryKey(
			long kaleoTaskFormInstanceId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTaskFormInstanceException {

		return getPersistence().findByPrimaryKey(kaleoTaskFormInstanceId);
	}

	/**
	 * Returns the kaleo task form instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance, or <code>null</code> if a kaleo task form instance with the primary key could not be found
	 */
	public static KaleoTaskFormInstance fetchByPrimaryKey(
		long kaleoTaskFormInstanceId) {

		return getPersistence().fetchByPrimaryKey(kaleoTaskFormInstanceId);
	}

	/**
	 * Returns all the kaleo task form instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the kaleo task form instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @return the range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo task form instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task form instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	/**
	 * Returns a range of all the kaleo task form instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @return the range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task form instances where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoInstanceId(
		long kaleoInstanceId) {

		return getPersistence().findByKaleoInstanceId(kaleoInstanceId);
	}

	/**
	 * Returns a range of all the kaleo task form instances where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @return the range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end) {

		return getPersistence().findByKaleoInstanceId(
			kaleoInstanceId, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().findByKaleoInstanceId(
			kaleoInstanceId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo task form instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @return the matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId) {

		return getPersistence().findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId);
	}

	/**
	 * Returns a range of all the kaleo task form instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @return the range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId, int start, int end) {

		return getPersistence().findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo task form instances where kaleoTaskInstanceTokenId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskInstanceTokenId the kaleo task instance token ID
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> findByKaleoTaskInstanceTokenId(
		long kaleoTaskInstanceTokenId, int start, int end,
		OrderByComparator<KaleoTaskFormInstance> orderByComparator) {

		return getPersistence().findByKaleoTaskInstanceTokenId(
			kaleoTaskInstanceTokenId, start, end, orderByComparator);
	}

	public static KaleoTaskFormInstancePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		KaleoTaskFormInstancePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile KaleoTaskFormInstancePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-910309915