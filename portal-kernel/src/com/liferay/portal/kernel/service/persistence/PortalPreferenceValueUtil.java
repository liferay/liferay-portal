/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the portal preference value service. This utility wraps <code>com.liferay.portal.service.persistence.impl.PortalPreferenceValuePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PortalPreferenceValuePersistence
 * @generated
 */
public class PortalPreferenceValueUtil {

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
	public static void clearCache(PortalPreferenceValue portalPreferenceValue) {
		getPersistence().clearCache(portalPreferenceValue);
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
	public static Map<Serializable, PortalPreferenceValue> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PortalPreferenceValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PortalPreferenceValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PortalPreferenceValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PortalPreferenceValue update(
		PortalPreferenceValue portalPreferenceValue) {

		return getPersistence().update(portalPreferenceValue);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PortalPreferenceValue update(
		PortalPreferenceValue portalPreferenceValue,
		ServiceContext serviceContext) {

		return getPersistence().update(portalPreferenceValue, serviceContext);
	}

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @return the matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId) {

		return getPersistence().findByPortalPreferencesId(portalPreferencesId);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end) {

		return getPersistence().findByPortalPreferencesId(
			portalPreferencesId, start, end);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().findByPortalPreferencesId(
			portalPreferencesId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByPortalPreferencesId(
		long portalPreferencesId, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPortalPreferencesId(
			portalPreferencesId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByPortalPreferencesId_First(
			long portalPreferencesId,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByPortalPreferencesId_First(
			portalPreferencesId, orderByComparator);
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByPortalPreferencesId_First(
		long portalPreferencesId,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().fetchByPortalPreferencesId_First(
			portalPreferencesId, orderByComparator);
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByPortalPreferencesId_Last(
			long portalPreferencesId,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByPortalPreferencesId_Last(
			portalPreferencesId, orderByComparator);
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByPortalPreferencesId_Last(
		long portalPreferencesId,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().fetchByPortalPreferencesId_Last(
			portalPreferencesId, orderByComparator);
	}

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public static PortalPreferenceValue[] findByPortalPreferencesId_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByPortalPreferencesId_PrevAndNext(
			portalPreferenceValueId, portalPreferencesId, orderByComparator);
	}

	/**
	 * Returns all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @return the matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds) {

		return getPersistence().findByPortalPreferencesId(portalPreferencesIds);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end) {

		return getPersistence().findByPortalPreferencesId(
			portalPreferencesIds, start, end);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().findByPortalPreferencesId(
			portalPreferencesIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByPortalPreferencesId(
		long[] portalPreferencesIds, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPortalPreferencesId(
			portalPreferencesIds, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 */
	public static void removeByPortalPreferencesId(long portalPreferencesId) {
		getPersistence().removeByPortalPreferencesId(portalPreferencesId);
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @return the number of matching portal preference values
	 */
	public static int countByPortalPreferencesId(long portalPreferencesId) {
		return getPersistence().countByPortalPreferencesId(portalPreferencesId);
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = any &#63;.
	 *
	 * @param portalPreferencesIds the portal preferences IDs
	 * @return the number of matching portal preference values
	 */
	public static int countByPortalPreferencesId(long[] portalPreferencesIds) {
		return getPersistence().countByPortalPreferencesId(
			portalPreferencesIds);
	}

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @return the matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace) {

		return getPersistence().findByP_N(portalPreferencesId, namespace);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end) {

		return getPersistence().findByP_N(
			portalPreferencesId, namespace, start, end);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().findByP_N(
			portalPreferencesId, namespace, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_N(
		long portalPreferencesId, String namespace, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_N(
			portalPreferencesId, namespace, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByP_N_First(
			long portalPreferencesId, String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_N_First(
			portalPreferencesId, namespace, orderByComparator);
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByP_N_First(
		long portalPreferencesId, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().fetchByP_N_First(
			portalPreferencesId, namespace, orderByComparator);
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByP_N_Last(
			long portalPreferencesId, String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_N_Last(
			portalPreferencesId, namespace, orderByComparator);
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByP_N_Last(
		long portalPreferencesId, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().fetchByP_N_Last(
			portalPreferencesId, namespace, orderByComparator);
	}

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public static PortalPreferenceValue[] findByP_N_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId,
			String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_N_PrevAndNext(
			portalPreferenceValueId, portalPreferencesId, namespace,
			orderByComparator);
	}

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 */
	public static void removeByP_N(long portalPreferencesId, String namespace) {
		getPersistence().removeByP_N(portalPreferencesId, namespace);
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	public static int countByP_N(long portalPreferencesId, String namespace) {
		return getPersistence().countByP_N(portalPreferencesId, namespace);
	}

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace) {

		return getPersistence().findByP_K_N(
			portalPreferencesId, key, namespace);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end) {

		return getPersistence().findByP_K_N(
			portalPreferencesId, key, namespace, start, end);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end, OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().findByP_K_N(
			portalPreferencesId, key, namespace, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_K_N(
		long portalPreferencesId, String key, String namespace, int start,
		int end, OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_K_N(
			portalPreferencesId, key, namespace, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByP_K_N_First(
			long portalPreferencesId, String key, String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_K_N_First(
			portalPreferencesId, key, namespace, orderByComparator);
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByP_K_N_First(
		long portalPreferencesId, String key, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().fetchByP_K_N_First(
			portalPreferencesId, key, namespace, orderByComparator);
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByP_K_N_Last(
			long portalPreferencesId, String key, String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_K_N_Last(
			portalPreferencesId, key, namespace, orderByComparator);
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByP_K_N_Last(
		long portalPreferencesId, String key, String namespace,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().fetchByP_K_N_Last(
			portalPreferencesId, key, namespace, orderByComparator);
	}

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public static PortalPreferenceValue[] findByP_K_N_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId, String key,
			String namespace,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_K_N_PrevAndNext(
			portalPreferenceValueId, portalPreferencesId, key, namespace,
			orderByComparator);
	}

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 */
	public static void removeByP_K_N(
		long portalPreferencesId, String key, String namespace) {

		getPersistence().removeByP_K_N(portalPreferencesId, key, namespace);
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	public static int countByP_K_N(
		long portalPreferencesId, String key, String namespace) {

		return getPersistence().countByP_K_N(
			portalPreferencesId, key, namespace);
	}

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or throws a <code>NoSuchPreferenceValueException</code> if it could not be found.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByP_I_K_N(
			long portalPreferencesId, int index, String key, String namespace)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_I_K_N(
			portalPreferencesId, index, key, namespace);
	}

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace) {

		return getPersistence().fetchByP_I_K_N(
			portalPreferencesId, index, key, namespace);
	}

	/**
	 * Returns the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace,
		boolean useFinderCache) {

		return getPersistence().fetchByP_I_K_N(
			portalPreferencesId, index, key, namespace, useFinderCache);
	}

	/**
	 * Removes the portal preference value where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the portal preference value that was removed
	 */
	public static PortalPreferenceValue removeByP_I_K_N(
			long portalPreferencesId, int index, String key, String namespace)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().removeByP_I_K_N(
			portalPreferencesId, index, key, namespace);
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and index = &#63; and key = &#63; and namespace = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param index the index
	 * @param key the key
	 * @param namespace the namespace
	 * @return the number of matching portal preference values
	 */
	public static int countByP_I_K_N(
		long portalPreferencesId, int index, String key, String namespace) {

		return getPersistence().countByP_I_K_N(
			portalPreferencesId, index, key, namespace);
	}

	/**
	 * Returns all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @return the matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue) {

		return getPersistence().findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue);
	}

	/**
	 * Returns a range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end) {

		return getPersistence().findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue, start, end);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preference values
	 */
	public static List<PortalPreferenceValue> findByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue, int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByP_K_N_SV_First(
			long portalPreferencesId, String key, String namespace,
			String smallValue,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_K_N_SV_First(
			portalPreferencesId, key, namespace, smallValue, orderByComparator);
	}

	/**
	 * Returns the first portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByP_K_N_SV_First(
		long portalPreferencesId, String key, String namespace,
		String smallValue,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().fetchByP_K_N_SV_First(
			portalPreferencesId, key, namespace, smallValue, orderByComparator);
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value
	 * @throws NoSuchPreferenceValueException if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue findByP_K_N_SV_Last(
			long portalPreferencesId, String key, String namespace,
			String smallValue,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_K_N_SV_Last(
			portalPreferencesId, key, namespace, smallValue, orderByComparator);
	}

	/**
	 * Returns the last portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portal preference value, or <code>null</code> if a matching portal preference value could not be found
	 */
	public static PortalPreferenceValue fetchByP_K_N_SV_Last(
		long portalPreferencesId, String key, String namespace,
		String smallValue,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().fetchByP_K_N_SV_Last(
			portalPreferencesId, key, namespace, smallValue, orderByComparator);
	}

	/**
	 * Returns the portal preference values before and after the current portal preference value in the ordered set where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferenceValueId the primary key of the current portal preference value
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public static PortalPreferenceValue[] findByP_K_N_SV_PrevAndNext(
			long portalPreferenceValueId, long portalPreferencesId, String key,
			String namespace, String smallValue,
			OrderByComparator<PortalPreferenceValue> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByP_K_N_SV_PrevAndNext(
			portalPreferenceValueId, portalPreferencesId, key, namespace,
			smallValue, orderByComparator);
	}

	/**
	 * Removes all the portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63; from the database.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 */
	public static void removeByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue) {

		getPersistence().removeByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue);
	}

	/**
	 * Returns the number of portal preference values where portalPreferencesId = &#63; and key = &#63; and namespace = &#63; and smallValue = &#63;.
	 *
	 * @param portalPreferencesId the portal preferences ID
	 * @param key the key
	 * @param namespace the namespace
	 * @param smallValue the small value
	 * @return the number of matching portal preference values
	 */
	public static int countByP_K_N_SV(
		long portalPreferencesId, String key, String namespace,
		String smallValue) {

		return getPersistence().countByP_K_N_SV(
			portalPreferencesId, key, namespace, smallValue);
	}

	/**
	 * Caches the portal preference value in the entity cache if it is enabled.
	 *
	 * @param portalPreferenceValue the portal preference value
	 */
	public static void cacheResult(
		PortalPreferenceValue portalPreferenceValue) {

		getPersistence().cacheResult(portalPreferenceValue);
	}

	/**
	 * Caches the portal preference values in the entity cache if it is enabled.
	 *
	 * @param portalPreferenceValues the portal preference values
	 */
	public static void cacheResult(
		List<PortalPreferenceValue> portalPreferenceValues) {

		getPersistence().cacheResult(portalPreferenceValues);
	}

	/**
	 * Creates a new portal preference value with the primary key. Does not add the portal preference value to the database.
	 *
	 * @param portalPreferenceValueId the primary key for the new portal preference value
	 * @return the new portal preference value
	 */
	public static PortalPreferenceValue create(long portalPreferenceValueId) {
		return getPersistence().create(portalPreferenceValueId);
	}

	/**
	 * Removes the portal preference value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value that was removed
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public static PortalPreferenceValue remove(long portalPreferenceValueId)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().remove(portalPreferenceValueId);
	}

	public static PortalPreferenceValue updateImpl(
		PortalPreferenceValue portalPreferenceValue) {

		return getPersistence().updateImpl(portalPreferenceValue);
	}

	/**
	 * Returns the portal preference value with the primary key or throws a <code>NoSuchPreferenceValueException</code> if it could not be found.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value
	 * @throws NoSuchPreferenceValueException if a portal preference value with the primary key could not be found
	 */
	public static PortalPreferenceValue findByPrimaryKey(
			long portalPreferenceValueId)
		throws com.liferay.portal.kernel.exception.
			NoSuchPreferenceValueException {

		return getPersistence().findByPrimaryKey(portalPreferenceValueId);
	}

	/**
	 * Returns the portal preference value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portalPreferenceValueId the primary key of the portal preference value
	 * @return the portal preference value, or <code>null</code> if a portal preference value with the primary key could not be found
	 */
	public static PortalPreferenceValue fetchByPrimaryKey(
		long portalPreferenceValueId) {

		return getPersistence().fetchByPrimaryKey(portalPreferenceValueId);
	}

	/**
	 * Returns all the portal preference values.
	 *
	 * @return the portal preference values
	 */
	public static List<PortalPreferenceValue> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @return the range of portal preference values
	 */
	public static List<PortalPreferenceValue> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of portal preference values
	 */
	public static List<PortalPreferenceValue> findAll(
		int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the portal preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preference values
	 * @param end the upper bound of the range of portal preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of portal preference values
	 */
	public static List<PortalPreferenceValue> findAll(
		int start, int end,
		OrderByComparator<PortalPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the portal preference values from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of portal preference values.
	 *
	 * @return the number of portal preference values
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static PortalPreferenceValuePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		PortalPreferenceValuePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile PortalPreferenceValuePersistence _persistence;

}