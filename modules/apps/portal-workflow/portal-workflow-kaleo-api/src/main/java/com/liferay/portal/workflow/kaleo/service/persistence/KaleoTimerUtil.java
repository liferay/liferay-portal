/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the kaleo timer service. This utility wraps <code>com.liferay.portal.workflow.kaleo.service.persistence.impl.KaleoTimerPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTimerPersistence
 * @generated
 */
public class KaleoTimerUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<KaleoTimer> kaleoTimers) {
		getPersistence().cacheResult(kaleoTimers);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(KaleoTimer kaleoTimer) {
		getPersistence().cacheResult(kaleoTimer);
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
	public static void clearCache(KaleoTimer kaleoTimer) {
		getPersistence().clearCache(kaleoTimer);
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
	public static Map<Serializable, KaleoTimer> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<KaleoTimer> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<KaleoTimer> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<KaleoTimer> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<KaleoTimer> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static KaleoTimer update(KaleoTimer kaleoTimer) {
		return getPersistence().update(kaleoTimer);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static KaleoTimer update(
		KaleoTimer kaleoTimer, ServiceContext serviceContext) {

		return getPersistence().update(kaleoTimer, serviceContext);
	}

	/**
	 * Returns an ordered range of all the kaleo timers where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param start the lower bound of the range of kaleo timers
	 * @param end the upper bound of the range of kaleo timers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo timers
	 */
	public static List<KaleoTimer> findByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK, int start, int end,
		OrderByComparator<KaleoTimer> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKCN_KCPK(
			kaleoClassName, kaleoClassPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo timer in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer
	 * @throws NoSuchTimerException if a matching kaleo timer could not be found
	 */
	public static KaleoTimer findByKCN_KCPK_First(
			String kaleoClassName, long kaleoClassPK,
			OrderByComparator<KaleoTimer> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTimerException {

		return getPersistence().findByKCN_KCPK_First(
			kaleoClassName, kaleoClassPK, orderByComparator);
	}

	/**
	 * Returns the first kaleo timer in the ordered set where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer, or <code>null</code> if a matching kaleo timer could not be found
	 */
	public static KaleoTimer fetchByKCN_KCPK_First(
		String kaleoClassName, long kaleoClassPK,
		OrderByComparator<KaleoTimer> orderByComparator) {

		return getPersistence().fetchByKCN_KCPK_First(
			kaleoClassName, kaleoClassPK, orderByComparator);
	}

	/**
	 * Removes all the kaleo timers where kaleoClassName = &#63; and kaleoClassPK = &#63; from the database.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 */
	public static void removeByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK) {

		getPersistence().removeByKCN_KCPK(kaleoClassName, kaleoClassPK);
	}

	/**
	 * Returns the number of kaleo timers where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @return the number of matching kaleo timers
	 */
	public static int countByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK) {

		return getPersistence().countByKCN_KCPK(kaleoClassName, kaleoClassPK);
	}

	/**
	 * Returns an ordered range of all the kaleo timers where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo timers
	 * @param end the upper bound of the range of kaleo timers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo timers
	 */
	public static List<KaleoTimer> findByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId, int start,
		int end, OrderByComparator<KaleoTimer> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKCN_KDVI(
			kaleoClassName, kaleoDefinitionVersionId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo timer in the ordered set where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer
	 * @throws NoSuchTimerException if a matching kaleo timer could not be found
	 */
	public static KaleoTimer findByKCN_KDVI_First(
			String kaleoClassName, long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTimer> orderByComparator)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTimerException {

		return getPersistence().findByKCN_KDVI_First(
			kaleoClassName, kaleoDefinitionVersionId, orderByComparator);
	}

	/**
	 * Returns the first kaleo timer in the ordered set where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer, or <code>null</code> if a matching kaleo timer could not be found
	 */
	public static KaleoTimer fetchByKCN_KDVI_First(
		String kaleoClassName, long kaleoDefinitionVersionId,
		OrderByComparator<KaleoTimer> orderByComparator) {

		return getPersistence().fetchByKCN_KDVI_First(
			kaleoClassName, kaleoDefinitionVersionId, orderByComparator);
	}

	/**
	 * Removes all the kaleo timers where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	public static void removeByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		getPersistence().removeByKCN_KDVI(
			kaleoClassName, kaleoDefinitionVersionId);
	}

	/**
	 * Returns the number of kaleo timers where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo timers
	 */
	public static int countByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		return getPersistence().countByKCN_KDVI(
			kaleoClassName, kaleoDefinitionVersionId);
	}

	/**
	 * Creates a new kaleo timer with the primary key. Does not add the kaleo timer to the database.
	 *
	 * @param kaleoTimerId the primary key for the new kaleo timer
	 * @return the new kaleo timer
	 */
	public static KaleoTimer create(long kaleoTimerId) {
		return getPersistence().create(kaleoTimerId);
	}

	/**
	 * Removes the kaleo timer with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTimerId the primary key of the kaleo timer
	 * @return the kaleo timer that was removed
	 * @throws NoSuchTimerException if a kaleo timer with the primary key could not be found
	 */
	public static KaleoTimer remove(long kaleoTimerId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTimerException {

		return getPersistence().remove(kaleoTimerId);
	}

	public static KaleoTimer updateImpl(KaleoTimer kaleoTimer) {
		return getPersistence().updateImpl(kaleoTimer);
	}

	/**
	 * Returns the kaleo timer with the primary key or throws a <code>NoSuchTimerException</code> if it could not be found.
	 *
	 * @param kaleoTimerId the primary key of the kaleo timer
	 * @return the kaleo timer
	 * @throws NoSuchTimerException if a kaleo timer with the primary key could not be found
	 */
	public static KaleoTimer findByPrimaryKey(long kaleoTimerId)
		throws com.liferay.portal.workflow.kaleo.exception.
			NoSuchTimerException {

		return getPersistence().findByPrimaryKey(kaleoTimerId);
	}

	/**
	 * Returns the kaleo timer with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTimerId the primary key of the kaleo timer
	 * @return the kaleo timer, or <code>null</code> if a kaleo timer with the primary key could not be found
	 */
	public static KaleoTimer fetchByPrimaryKey(long kaleoTimerId) {
		return getPersistence().fetchByPrimaryKey(kaleoTimerId);
	}

	/**
	 * Returns all the kaleo timers where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @return the matching kaleo timers
	 */
	public static List<KaleoTimer> findByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK) {

		return getPersistence().findByKCN_KCPK(kaleoClassName, kaleoClassPK);
	}

	/**
	 * Returns a range of all the kaleo timers where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param start the lower bound of the range of kaleo timers
	 * @param end the upper bound of the range of kaleo timers (not inclusive)
	 * @return the range of matching kaleo timers
	 */
	public static List<KaleoTimer> findByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK, int start, int end) {

		return getPersistence().findByKCN_KCPK(
			kaleoClassName, kaleoClassPK, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo timers where kaleoClassName = &#63; and kaleoClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoClassPK the kaleo class pk
	 * @param start the lower bound of the range of kaleo timers
	 * @param end the upper bound of the range of kaleo timers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo timers
	 */
	public static List<KaleoTimer> findByKCN_KCPK(
		String kaleoClassName, long kaleoClassPK, int start, int end,
		OrderByComparator<KaleoTimer> orderByComparator) {

		return getPersistence().findByKCN_KCPK(
			kaleoClassName, kaleoClassPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the kaleo timers where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the matching kaleo timers
	 */
	public static List<KaleoTimer> findByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		return getPersistence().findByKCN_KDVI(
			kaleoClassName, kaleoDefinitionVersionId);
	}

	/**
	 * Returns a range of all the kaleo timers where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo timers
	 * @param end the upper bound of the range of kaleo timers (not inclusive)
	 * @return the range of matching kaleo timers
	 */
	public static List<KaleoTimer> findByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId, int start,
		int end) {

		return getPersistence().findByKCN_KDVI(
			kaleoClassName, kaleoDefinitionVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the kaleo timers where kaleoClassName = &#63; and kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoClassName the kaleo class name
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo timers
	 * @param end the upper bound of the range of kaleo timers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo timers
	 */
	public static List<KaleoTimer> findByKCN_KDVI(
		String kaleoClassName, long kaleoDefinitionVersionId, int start,
		int end, OrderByComparator<KaleoTimer> orderByComparator) {

		return getPersistence().findByKCN_KDVI(
			kaleoClassName, kaleoDefinitionVersionId, start, end,
			orderByComparator);
	}

	public static KaleoTimerPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(KaleoTimerPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile KaleoTimerPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-936387034