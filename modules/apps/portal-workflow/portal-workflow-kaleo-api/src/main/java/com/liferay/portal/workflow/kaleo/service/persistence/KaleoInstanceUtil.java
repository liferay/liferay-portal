/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the kaleo instance service. This utility wraps <code>com.liferay.portal.workflow.kaleo.service.persistence.impl.KaleoInstancePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstancePersistence
 * @generated
 */
public class KaleoInstanceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<KaleoInstance> kaleoInstances) {
		getPersistence().cacheResult(kaleoInstances);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(KaleoInstance kaleoInstance) {
		getPersistence().cacheResult(kaleoInstance);
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
	public static void clearCache(KaleoInstance kaleoInstance) {
		getPersistence().clearCache(kaleoInstance);
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
	public static Map<Serializable, KaleoInstance> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<KaleoInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<KaleoInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<KaleoInstance> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static KaleoInstance update(KaleoInstance kaleoInstance) {
		return getPersistence().update(kaleoInstance);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static KaleoInstance update(
		KaleoInstance kaleoInstance, ServiceContext serviceContext) {

		return getPersistence().update(kaleoInstance, serviceContext);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public static KaleoInstance findByCompanyId_First(
			long companyId, OrderByComparator<KaleoInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public static KaleoInstance fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of kaleo instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo instances
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public static KaleoInstance findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().findByKaleoDefinitionVersionId_First(
			kaleoDefinitionVersionId, orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public static KaleoInstance fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().fetchByKaleoDefinitionVersionId_First(
			kaleoDefinitionVersionId, orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	public static void removeByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		getPersistence().removeByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	/**
	 * Returns the number of kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo instances
	 */
	public static int countByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		return getPersistence().countByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public static KaleoInstance findByC_U_First(
			long companyId, long userId,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().findByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public static KaleoInstance fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().fetchByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public static void removeByC_U(long companyId, long userId) {
		getPersistence().removeByC_U(companyId, userId);
	}

	/**
	 * Returns the number of kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching kaleo instances
	 */
	public static int countByC_U(long companyId, long userId) {
		return getPersistence().countByC_U(companyId, userId);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKDI_C(
			kaleoDefinitionId, completed, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public static KaleoInstance findByKDI_C_First(
			long kaleoDefinitionId, boolean completed,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().findByKDI_C_First(
			kaleoDefinitionId, completed, orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public static KaleoInstance fetchByKDI_C_First(
		long kaleoDefinitionId, boolean completed,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().fetchByKDI_C_First(
			kaleoDefinitionId, completed, orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 */
	public static void removeByKDI_C(
		long kaleoDefinitionId, boolean completed) {

		getPersistence().removeByKDI_C(kaleoDefinitionId, completed);
	}

	/**
	 * Returns the number of kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @return the number of matching kaleo instances
	 */
	public static int countByKDI_C(long kaleoDefinitionId, boolean completed) {
		return getPersistence().countByKDI_C(kaleoDefinitionId, completed);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKDVI_C(
			kaleoDefinitionVersionId, completed, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public static KaleoInstance findByKDVI_C_First(
			long kaleoDefinitionVersionId, boolean completed,
			OrderByComparator<KaleoInstance> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().findByKDVI_C_First(
			kaleoDefinitionVersionId, completed, orderByComparator);
	}

	/**
	 * Returns the first kaleo instance in the ordered set where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public static KaleoInstance fetchByKDVI_C_First(
		long kaleoDefinitionVersionId, boolean completed,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().fetchByKDVI_C_First(
			kaleoDefinitionVersionId, completed, orderByComparator);
	}

	/**
	 * Removes all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 */
	public static void removeByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed) {

		getPersistence().removeByKDVI_C(kaleoDefinitionVersionId, completed);
	}

	/**
	 * Returns the number of kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @return the number of matching kaleo instances
	 */
	public static int countByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed) {

		return getPersistence().countByKDVI_C(
			kaleoDefinitionVersionId, completed);
	}

	/**
	 * Returns the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; or throws a <code>NoSuchInstanceException</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching kaleo instance
	 * @throws NoSuchInstanceException if a matching kaleo instance could not be found
	 */
	public static KaleoInstance findByKII_C_U(
			long kaleoInstanceId, long companyId, long userId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().findByKII_C_U(
			kaleoInstanceId, companyId, userId);
	}

	/**
	 * Returns the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public static KaleoInstance fetchByKII_C_U(
		long kaleoInstanceId, long companyId, long userId,
		boolean useFinderCache) {

		return getPersistence().fetchByKII_C_U(
			kaleoInstanceId, companyId, userId, useFinderCache);
	}

	/**
	 * Removes the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the kaleo instance that was removed
	 */
	public static KaleoInstance removeByKII_C_U(
			long kaleoInstanceId, long companyId, long userId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().removeByKII_C_U(
			kaleoInstanceId, companyId, userId);
	}

	/**
	 * Returns the number of kaleo instances where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching kaleo instances
	 */
	public static int countByKII_C_U(
		long kaleoInstanceId, long companyId, long userId) {

		return getPersistence().countByKII_C_U(
			kaleoInstanceId, companyId, userId);
	}

	/**
	 * Creates a new kaleo instance with the primary key. Does not add the kaleo instance to the database.
	 *
	 * @param kaleoInstanceId the primary key for the new kaleo instance
	 * @return the new kaleo instance
	 */
	public static KaleoInstance create(long kaleoInstanceId) {
		return getPersistence().create(kaleoInstanceId);
	}

	/**
	 * Removes the kaleo instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance that was removed
	 * @throws NoSuchInstanceException if a kaleo instance with the primary key could not be found
	 */
	public static KaleoInstance remove(long kaleoInstanceId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().remove(kaleoInstanceId);
	}

	public static KaleoInstance updateImpl(KaleoInstance kaleoInstance) {
		return getPersistence().updateImpl(kaleoInstance);
	}

	/**
	 * Returns the kaleo instance with the primary key or throws a <code>NoSuchInstanceException</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance
	 * @throws NoSuchInstanceException if a kaleo instance with the primary key could not be found
	 */
	public static KaleoInstance findByPrimaryKey(long kaleoInstanceId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchInstanceException {

		return getPersistence().findByPrimaryKey(kaleoInstanceId);
	}

	/**
	 * Returns the kaleo instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance, or <code>null</code> if a kaleo instance with the primary key could not be found
	 */
	public static KaleoInstance fetchByPrimaryKey(long kaleoInstanceId) {
		return getPersistence().fetchByPrimaryKey(kaleoInstanceId);
	}

	/**
	 * Returns the kaleo instance where kaleoInstanceId = &#63; and companyId = &#63; and userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching kaleo instance, or <code>null</code> if a matching kaleo instance could not be found
	 */
	public static KaleoInstance fetchByKII_C_U(
		long kaleoInstanceId, long companyId, long userId) {

		return getPersistence().fetchByKII_C_U(
			kaleoInstanceId, companyId, userId);
	}

	/**
	 * Returns all the kaleo instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching kaleo instances
	 */
	public static List<KaleoInstance> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the kaleo instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the matching kaleo instances
	 */
	public static List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId);
	}

	/**
	 * Returns a range of all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().findByKaleoDefinitionVersionId(
			kaleoDefinitionVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching kaleo instances
	 */
	public static List<KaleoInstance> findByC_U(long companyId, long userId) {
		return getPersistence().findByC_U(companyId, userId);
	}

	/**
	 * Returns a range of all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByC_U(
		long companyId, long userId, int start, int end) {

		return getPersistence().findByC_U(companyId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @return the matching kaleo instances
	 */
	public static List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed) {

		return getPersistence().findByKDI_C(kaleoDefinitionId, completed);
	}

	/**
	 * Returns a range of all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed, int start, int end) {

		return getPersistence().findByKDI_C(
			kaleoDefinitionId, completed, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionId the kaleo definition ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKDI_C(
		long kaleoDefinitionId, boolean completed, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().findByKDI_C(
			kaleoDefinitionId, completed, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @return the matching kaleo instances
	 */
	public static List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed) {

		return getPersistence().findByKDVI_C(
			kaleoDefinitionVersionId, completed);
	}

	/**
	 * Returns a range of all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed, int start, int end) {

		return getPersistence().findByKDVI_C(
			kaleoDefinitionVersionId, completed, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo instances where kaleoDefinitionVersionId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo instances
	 */
	public static List<KaleoInstance> findByKDVI_C(
		long kaleoDefinitionVersionId, boolean completed, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator) {

		return getPersistence().findByKDVI_C(
			kaleoDefinitionVersionId, completed, start, end, orderByComparator);
	}

	public static KaleoInstancePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(KaleoInstancePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile KaleoInstancePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2093496150