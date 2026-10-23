/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the portlet preference value service. This utility wraps <code>com.liferay.portal.service.persistence.impl.PortletPreferenceValuePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PortletPreferenceValuePersistence
 * @generated
 */
public class PortletPreferenceValueUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<PortletPreferenceValue> portletPreferenceValues) {

		getPersistence().cacheResult(portletPreferenceValues);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		PortletPreferenceValue portletPreferenceValue) {

		getPersistence().cacheResult(portletPreferenceValue);
	}

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
		PortletPreferenceValue portletPreferenceValue) {

		getPersistence().clearCache(portletPreferenceValue);
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
	public static Map<Serializable, PortletPreferenceValue> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PortletPreferenceValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PortletPreferenceValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PortletPreferenceValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PortletPreferenceValue update(
		PortletPreferenceValue portletPreferenceValue) {

		return getPersistence().update(portletPreferenceValue);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PortletPreferenceValue update(
		PortletPreferenceValue portletPreferenceValue,
		ServiceContext serviceContext) {

		return getPersistence().update(portletPreferenceValue, serviceContext);
	}

	/**
	 * Returns an ordered range of all the portlet preference values where portletPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByPortletPreferencesId(
		long portletPreferencesId, int start, int end,
		OrderByComparator<PortletPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPortletPreferencesId(
			portletPreferencesId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue findByPortletPreferencesId_First(
			long portletPreferencesId,
			OrderByComparator<PortletPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPortletPreferenceValueException {

		return getPersistence().findByPortletPreferencesId_First(
			portletPreferencesId, orderByComparator);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue fetchByPortletPreferencesId_First(
		long portletPreferencesId,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return getPersistence().fetchByPortletPreferencesId_First(
			portletPreferencesId, orderByComparator);
	}

	/**
	 * Removes all the portlet preference values where portletPreferencesId = &#63; from the database.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 */
	public static void removeByPortletPreferencesId(long portletPreferencesId) {
		getPersistence().removeByPortletPreferencesId(portletPreferencesId);
	}

	/**
	 * Returns the number of portlet preference values where portletPreferencesId = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @return the number of matching portlet preference values
	 */
	public static int countByPortletPreferencesId(long portletPreferencesId) {
		return getPersistence().countByPortletPreferencesId(
			portletPreferencesId);
	}

	/**
	 * Returns an ordered range of all the portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByC_N_SV(
		long companyId, String name, String smallValue, int start, int end,
		OrderByComparator<PortletPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_N_SV(
			companyId, name, smallValue, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue findByC_N_SV_First(
			long companyId, String name, String smallValue,
			OrderByComparator<PortletPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPortletPreferenceValueException {

		return getPersistence().findByC_N_SV_First(
			companyId, name, smallValue, orderByComparator);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue fetchByC_N_SV_First(
		long companyId, String name, String smallValue,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return getPersistence().fetchByC_N_SV_First(
			companyId, name, smallValue, orderByComparator);
	}

	/**
	 * Removes all the portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 */
	public static void removeByC_N_SV(
		long companyId, String name, String smallValue) {

		getPersistence().removeByC_N_SV(companyId, name, smallValue);
	}

	/**
	 * Returns the number of portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @return the number of matching portlet preference values
	 */
	public static int countByC_N_SV(
		long companyId, String name, String smallValue) {

		return getPersistence().countByC_N_SV(companyId, name, smallValue);
	}

	/**
	 * Returns the portlet preference value where portletPreferencesId = &#63; and index = &#63; and name = &#63; or throws a <code>NoSuchPortletPreferenceValueException</code> if it could not be found.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @return the matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue findByP_I_N(
			long portletPreferencesId, int index, String name)
		throws com.liferay.portal.kernel.exception.
			NoSuchPortletPreferenceValueException {

		return getPersistence().findByP_I_N(portletPreferencesId, index, name);
	}

	/**
	 * Returns the portlet preference value where portletPreferencesId = &#63; and index = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue fetchByP_I_N(
		long portletPreferencesId, int index, String name,
		boolean useFinderCache) {

		return getPersistence().fetchByP_I_N(
			portletPreferencesId, index, name, useFinderCache);
	}

	/**
	 * Removes the portlet preference value where portletPreferencesId = &#63; and index = &#63; and name = &#63; from the database.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @return the portlet preference value that was removed
	 */
	public static PortletPreferenceValue removeByP_I_N(
			long portletPreferencesId, int index, String name)
		throws com.liferay.portal.kernel.exception.
			NoSuchPortletPreferenceValueException {

		return getPersistence().removeByP_I_N(
			portletPreferencesId, index, name);
	}

	/**
	 * Returns the number of portlet preference values where portletPreferencesId = &#63; and index = &#63; and name = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @return the number of matching portlet preference values
	 */
	public static int countByP_I_N(
		long portletPreferencesId, int index, String name) {

		return getPersistence().countByP_I_N(portletPreferencesId, index, name);
	}

	/**
	 * Returns an ordered range of all the portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByP_N_SV(
		long portletPreferencesId, String name, String smallValue, int start,
		int end, OrderByComparator<PortletPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_N_SV(
			portletPreferencesId, name, smallValue, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue findByP_N_SV_First(
			long portletPreferencesId, String name, String smallValue,
			OrderByComparator<PortletPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPortletPreferenceValueException {

		return getPersistence().findByP_N_SV_First(
			portletPreferencesId, name, smallValue, orderByComparator);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue fetchByP_N_SV_First(
		long portletPreferencesId, String name, String smallValue,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return getPersistence().fetchByP_N_SV_First(
			portletPreferencesId, name, smallValue, orderByComparator);
	}

	/**
	 * Removes all the portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63; from the database.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 */
	public static void removeByP_N_SV(
		long portletPreferencesId, String name, String smallValue) {

		getPersistence().removeByP_N_SV(portletPreferencesId, name, smallValue);
	}

	/**
	 * Returns the number of portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @return the number of matching portlet preference values
	 */
	public static int countByP_N_SV(
		long portletPreferencesId, String name, String smallValue) {

		return getPersistence().countByP_N_SV(
			portletPreferencesId, name, smallValue);
	}

	/**
	 * Creates a new portlet preference value with the primary key. Does not add the portlet preference value to the database.
	 *
	 * @param portletPreferenceValueId the primary key for the new portlet preference value
	 * @return the new portlet preference value
	 */
	public static PortletPreferenceValue create(long portletPreferenceValueId) {
		return getPersistence().create(portletPreferenceValueId);
	}

	/**
	 * Removes the portlet preference value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value that was removed
	 * @throws NoSuchPortletPreferenceValueException if a portlet preference value with the primary key could not be found
	 */
	public static PortletPreferenceValue remove(long portletPreferenceValueId)
		throws com.liferay.portal.kernel.exception.
			NoSuchPortletPreferenceValueException {

		return getPersistence().remove(portletPreferenceValueId);
	}

	public static PortletPreferenceValue updateImpl(
		PortletPreferenceValue portletPreferenceValue) {

		return getPersistence().updateImpl(portletPreferenceValue);
	}

	/**
	 * Returns the portlet preference value with the primary key or throws a <code>NoSuchPortletPreferenceValueException</code> if it could not be found.
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a portlet preference value with the primary key could not be found
	 */
	public static PortletPreferenceValue findByPrimaryKey(
			long portletPreferenceValueId)
		throws com.liferay.portal.kernel.exception.
			NoSuchPortletPreferenceValueException {

		return getPersistence().findByPrimaryKey(portletPreferenceValueId);
	}

	/**
	 * Returns the portlet preference value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value, or <code>null</code> if a portlet preference value with the primary key could not be found
	 */
	public static PortletPreferenceValue fetchByPrimaryKey(
		long portletPreferenceValueId) {

		return getPersistence().fetchByPrimaryKey(portletPreferenceValueId);
	}

	/**
	 * Returns the portlet preference value where portletPreferencesId = &#63; and index = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @return the matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	public static PortletPreferenceValue fetchByP_I_N(
		long portletPreferencesId, int index, String name) {

		return getPersistence().fetchByP_I_N(portletPreferencesId, index, name);
	}

	/**
	 * Returns all the portlet preference values where portletPreferencesId = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @return the matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByPortletPreferencesId(
		long portletPreferencesId) {

		return getPersistence().findByPortletPreferencesId(
			portletPreferencesId);
	}

	/**
	 * Returns a range of all the portlet preference values where portletPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @return the range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByPortletPreferencesId(
		long portletPreferencesId, int start, int end) {

		return getPersistence().findByPortletPreferencesId(
			portletPreferencesId, start, end);
	}

	/**
	 * Returns an ordered range of all the portlet preference values where portletPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByPortletPreferencesId(
		long portletPreferencesId, int start, int end,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return getPersistence().findByPortletPreferencesId(
			portletPreferencesId, start, end, orderByComparator);
	}

	/**
	 * Returns all the portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @return the matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByC_N_SV(
		long companyId, String name, String smallValue) {

		return getPersistence().findByC_N_SV(companyId, name, smallValue);
	}

	/**
	 * Returns a range of all the portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @return the range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByC_N_SV(
		long companyId, String name, String smallValue, int start, int end) {

		return getPersistence().findByC_N_SV(
			companyId, name, smallValue, start, end);
	}

	/**
	 * Returns an ordered range of all the portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByC_N_SV(
		long companyId, String name, String smallValue, int start, int end,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return getPersistence().findByC_N_SV(
			companyId, name, smallValue, start, end, orderByComparator);
	}

	/**
	 * Returns all the portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @return the matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByP_N_SV(
		long portletPreferencesId, String name, String smallValue) {

		return getPersistence().findByP_N_SV(
			portletPreferencesId, name, smallValue);
	}

	/**
	 * Returns a range of all the portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @return the range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByP_N_SV(
		long portletPreferencesId, String name, String smallValue, int start,
		int end) {

		return getPersistence().findByP_N_SV(
			portletPreferencesId, name, smallValue, start, end);
	}

	/**
	 * Returns an ordered range of all the portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portlet preference values
	 */
	public static List<PortletPreferenceValue> findByP_N_SV(
		long portletPreferencesId, String name, String smallValue, int start,
		int end, OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return getPersistence().findByP_N_SV(
			portletPreferencesId, name, smallValue, start, end,
			orderByComparator);
	}

	public static PortletPreferenceValuePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		PortletPreferenceValuePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile PortletPreferenceValuePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-872349236