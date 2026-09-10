/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.model.FaroNotification;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the faro notification service. This utility wraps <code>com.liferay.osb.faro.service.persistence.impl.FaroNotificationPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroNotificationPersistence
 * @generated
 */
public class FaroNotificationUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<FaroNotification> faroNotifications) {
		getPersistence().cacheResult(faroNotifications);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(FaroNotification faroNotification) {
		getPersistence().cacheResult(faroNotification);
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
	public static void clearCache(FaroNotification faroNotification) {
		getPersistence().clearCache(faroNotification);
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
	public static Map<Serializable, FaroNotification> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FaroNotification> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FaroNotification> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FaroNotification> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FaroNotification update(FaroNotification faroNotification) {
		return getPersistence().update(faroNotification);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FaroNotification update(
		FaroNotification faroNotification, ServiceContext serviceContext) {

		return getPersistence().update(faroNotification, serviceContext);
	}

	/**
	 * Returns all the faro notifications where createTime &lt; &#63;.
	 *
	 * @param createTime the create time
	 * @return the matching faro notifications
	 */
	public static List<FaroNotification> findByLtCreateTime(long createTime) {
		return getPersistence().findByLtCreateTime(createTime);
	}

	/**
	 * Returns a range of all the faro notifications where createTime &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param createTime the create time
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @return the range of matching faro notifications
	 */
	public static List<FaroNotification> findByLtCreateTime(
		long createTime, int start, int end) {

		return getPersistence().findByLtCreateTime(createTime, start, end);
	}

	/**
	 * Returns an ordered range of all the faro notifications where createTime &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param createTime the create time
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByLtCreateTime(
		long createTime, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().findByLtCreateTime(
			createTime, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro notifications where createTime &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param createTime the create time
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByLtCreateTime(
		long createTime, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtCreateTime(
			createTime, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro notification in the ordered set where createTime &lt; &#63;.
	 *
	 * @param createTime the create time
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro notification
	 * @throws NoSuchFaroNotificationException if a matching faro notification could not be found
	 */
	public static FaroNotification findByLtCreateTime_First(
			long createTime,
			OrderByComparator<FaroNotification> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroNotificationException {

		return getPersistence().findByLtCreateTime_First(
			createTime, orderByComparator);
	}

	/**
	 * Returns the first faro notification in the ordered set where createTime &lt; &#63;.
	 *
	 * @param createTime the create time
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro notification, or <code>null</code> if a matching faro notification could not be found
	 */
	public static FaroNotification fetchByLtCreateTime_First(
		long createTime,
		OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().fetchByLtCreateTime_First(
			createTime, orderByComparator);
	}

	/**
	 * Removes all the faro notifications where createTime &lt; &#63; from the database.
	 *
	 * @param createTime the create time
	 */
	public static void removeByLtCreateTime(long createTime) {
		getPersistence().removeByLtCreateTime(createTime);
	}

	/**
	 * Returns the number of faro notifications where createTime &lt; &#63;.
	 *
	 * @param createTime the create time
	 * @return the number of matching faro notifications
	 */
	public static int countByLtCreateTime(long createTime) {
		return getPersistence().countByLtCreateTime(createTime);
	}

	/**
	 * Returns all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @return the matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T(
		long groupId, long createTime, long ownerId, String type) {

		return getPersistence().findByG_GtC_O_T(
			groupId, createTime, ownerId, type);
	}

	/**
	 * Returns a range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @return the range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T(
		long groupId, long createTime, long ownerId, String type, int start,
		int end) {

		return getPersistence().findByG_GtC_O_T(
			groupId, createTime, ownerId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T(
		long groupId, long createTime, long ownerId, String type, int start,
		int end, OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().findByG_GtC_O_T(
			groupId, createTime, ownerId, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T(
		long groupId, long createTime, long ownerId, String type, int start,
		int end, OrderByComparator<FaroNotification> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_GtC_O_T(
			groupId, createTime, ownerId, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first faro notification in the ordered set where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro notification
	 * @throws NoSuchFaroNotificationException if a matching faro notification could not be found
	 */
	public static FaroNotification findByG_GtC_O_T_First(
			long groupId, long createTime, long ownerId, String type,
			OrderByComparator<FaroNotification> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroNotificationException {

		return getPersistence().findByG_GtC_O_T_First(
			groupId, createTime, ownerId, type, orderByComparator);
	}

	/**
	 * Returns the first faro notification in the ordered set where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro notification, or <code>null</code> if a matching faro notification could not be found
	 */
	public static FaroNotification fetchByG_GtC_O_T_First(
		long groupId, long createTime, long ownerId, String type,
		OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().fetchByG_GtC_O_T_First(
			groupId, createTime, ownerId, type, orderByComparator);
	}

	/**
	 * Returns all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @return the matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T(
		long groupId, long createTime, long[] ownerIds, String type) {

		return getPersistence().findByG_GtC_O_T(
			groupId, createTime, ownerIds, type);
	}

	/**
	 * Returns a range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @return the range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T(
		long groupId, long createTime, long[] ownerIds, String type, int start,
		int end) {

		return getPersistence().findByG_GtC_O_T(
			groupId, createTime, ownerIds, type, start, end);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T(
		long groupId, long createTime, long[] ownerIds, String type, int start,
		int end, OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().findByG_GtC_O_T(
			groupId, createTime, ownerIds, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T(
		long groupId, long createTime, long[] ownerIds, String type, int start,
		int end, OrderByComparator<FaroNotification> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_GtC_O_T(
			groupId, createTime, ownerIds, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 */
	public static void removeByG_GtC_O_T(
		long groupId, long createTime, long ownerId, String type) {

		getPersistence().removeByG_GtC_O_T(groupId, createTime, ownerId, type);
	}

	/**
	 * Returns the number of faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @return the number of matching faro notifications
	 */
	public static int countByG_GtC_O_T(
		long groupId, long createTime, long ownerId, String type) {

		return getPersistence().countByG_GtC_O_T(
			groupId, createTime, ownerId, type);
	}

	/**
	 * Returns the number of faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @return the number of matching faro notifications
	 */
	public static int countByG_GtC_O_T(
		long groupId, long createTime, long[] ownerIds, String type) {

		return getPersistence().countByG_GtC_O_T(
			groupId, createTime, ownerIds, type);
	}

	/**
	 * Returns all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param subtype the subtype
	 * @return the matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T_S(
		long groupId, long createTime, long ownerId, String type,
		String subtype) {

		return getPersistence().findByG_GtC_O_T_S(
			groupId, createTime, ownerId, type, subtype);
	}

	/**
	 * Returns a range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @return the range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T_S(
		long groupId, long createTime, long ownerId, String type,
		String subtype, int start, int end) {

		return getPersistence().findByG_GtC_O_T_S(
			groupId, createTime, ownerId, type, subtype, start, end);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T_S(
		long groupId, long createTime, long ownerId, String type,
		String subtype, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().findByG_GtC_O_T_S(
			groupId, createTime, ownerId, type, subtype, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T_S(
		long groupId, long createTime, long ownerId, String type,
		String subtype, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_GtC_O_T_S(
			groupId, createTime, ownerId, type, subtype, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro notification in the ordered set where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param subtype the subtype
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro notification
	 * @throws NoSuchFaroNotificationException if a matching faro notification could not be found
	 */
	public static FaroNotification findByG_GtC_O_T_S_First(
			long groupId, long createTime, long ownerId, String type,
			String subtype,
			OrderByComparator<FaroNotification> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroNotificationException {

		return getPersistence().findByG_GtC_O_T_S_First(
			groupId, createTime, ownerId, type, subtype, orderByComparator);
	}

	/**
	 * Returns the first faro notification in the ordered set where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param subtype the subtype
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro notification, or <code>null</code> if a matching faro notification could not be found
	 */
	public static FaroNotification fetchByG_GtC_O_T_S_First(
		long groupId, long createTime, long ownerId, String type,
		String subtype, OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().fetchByG_GtC_O_T_S_First(
			groupId, createTime, ownerId, type, subtype, orderByComparator);
	}

	/**
	 * Returns all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @param subtype the subtype
	 * @return the matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T_S(
		long groupId, long createTime, long[] ownerIds, String type,
		String subtype) {

		return getPersistence().findByG_GtC_O_T_S(
			groupId, createTime, ownerIds, type, subtype);
	}

	/**
	 * Returns a range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @return the range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T_S(
		long groupId, long createTime, long[] ownerIds, String type,
		String subtype, int start, int end) {

		return getPersistence().findByG_GtC_O_T_S(
			groupId, createTime, ownerIds, type, subtype, start, end);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T_S(
		long groupId, long createTime, long[] ownerIds, String type,
		String subtype, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().findByG_GtC_O_T_S(
			groupId, createTime, ownerIds, type, subtype, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_T_S(
		long groupId, long createTime, long[] ownerIds, String type,
		String subtype, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_GtC_O_T_S(
			groupId, createTime, ownerIds, type, subtype, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param subtype the subtype
	 */
	public static void removeByG_GtC_O_T_S(
		long groupId, long createTime, long ownerId, String type,
		String subtype) {

		getPersistence().removeByG_GtC_O_T_S(
			groupId, createTime, ownerId, type, subtype);
	}

	/**
	 * Returns the number of faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param type the type
	 * @param subtype the subtype
	 * @return the number of matching faro notifications
	 */
	public static int countByG_GtC_O_T_S(
		long groupId, long createTime, long ownerId, String type,
		String subtype) {

		return getPersistence().countByG_GtC_O_T_S(
			groupId, createTime, ownerId, type, subtype);
	}

	/**
	 * Returns the number of faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param type the type
	 * @param subtype the subtype
	 * @return the number of matching faro notifications
	 */
	public static int countByG_GtC_O_T_S(
		long groupId, long createTime, long[] ownerIds, String type,
		String subtype) {

		return getPersistence().countByG_GtC_O_T_S(
			groupId, createTime, ownerIds, type, subtype);
	}

	/**
	 * Returns all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @return the matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_R_T_S(
		long groupId, long createTime, long ownerId, boolean read, String type,
		String subtype) {

		return getPersistence().findByG_GtC_O_R_T_S(
			groupId, createTime, ownerId, read, type, subtype);
	}

	/**
	 * Returns a range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @return the range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_R_T_S(
		long groupId, long createTime, long ownerId, boolean read, String type,
		String subtype, int start, int end) {

		return getPersistence().findByG_GtC_O_R_T_S(
			groupId, createTime, ownerId, read, type, subtype, start, end);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_R_T_S(
		long groupId, long createTime, long ownerId, boolean read, String type,
		String subtype, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().findByG_GtC_O_R_T_S(
			groupId, createTime, ownerId, read, type, subtype, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_R_T_S(
		long groupId, long createTime, long ownerId, boolean read, String type,
		String subtype, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_GtC_O_R_T_S(
			groupId, createTime, ownerId, read, type, subtype, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro notification in the ordered set where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro notification
	 * @throws NoSuchFaroNotificationException if a matching faro notification could not be found
	 */
	public static FaroNotification findByG_GtC_O_R_T_S_First(
			long groupId, long createTime, long ownerId, boolean read,
			String type, String subtype,
			OrderByComparator<FaroNotification> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroNotificationException {

		return getPersistence().findByG_GtC_O_R_T_S_First(
			groupId, createTime, ownerId, read, type, subtype,
			orderByComparator);
	}

	/**
	 * Returns the first faro notification in the ordered set where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro notification, or <code>null</code> if a matching faro notification could not be found
	 */
	public static FaroNotification fetchByG_GtC_O_R_T_S_First(
		long groupId, long createTime, long ownerId, boolean read, String type,
		String subtype, OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().fetchByG_GtC_O_R_T_S_First(
			groupId, createTime, ownerId, read, type, subtype,
			orderByComparator);
	}

	/**
	 * Returns all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @return the matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_R_T_S(
		long groupId, long createTime, long[] ownerIds, boolean read,
		String type, String subtype) {

		return getPersistence().findByG_GtC_O_R_T_S(
			groupId, createTime, ownerIds, read, type, subtype);
	}

	/**
	 * Returns a range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @return the range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_R_T_S(
		long groupId, long createTime, long[] ownerIds, boolean read,
		String type, String subtype, int start, int end) {

		return getPersistence().findByG_GtC_O_R_T_S(
			groupId, createTime, ownerIds, read, type, subtype, start, end);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_R_T_S(
		long groupId, long createTime, long[] ownerIds, boolean read,
		String type, String subtype, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator) {

		return getPersistence().findByG_GtC_O_R_T_S(
			groupId, createTime, ownerIds, read, type, subtype, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroNotificationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of faro notifications
	 * @param end the upper bound of the range of faro notifications (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro notifications
	 */
	public static List<FaroNotification> findByG_GtC_O_R_T_S(
		long groupId, long createTime, long[] ownerIds, boolean read,
		String type, String subtype, int start, int end,
		OrderByComparator<FaroNotification> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_GtC_O_R_T_S(
			groupId, createTime, ownerIds, read, type, subtype, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 */
	public static void removeByG_GtC_O_R_T_S(
		long groupId, long createTime, long ownerId, boolean read, String type,
		String subtype) {

		getPersistence().removeByG_GtC_O_R_T_S(
			groupId, createTime, ownerId, read, type, subtype);
	}

	/**
	 * Returns the number of faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerId the owner ID
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @return the number of matching faro notifications
	 */
	public static int countByG_GtC_O_R_T_S(
		long groupId, long createTime, long ownerId, boolean read, String type,
		String subtype) {

		return getPersistence().countByG_GtC_O_R_T_S(
			groupId, createTime, ownerId, read, type, subtype);
	}

	/**
	 * Returns the number of faro notifications where groupId = &#63; and createTime &gt; &#63; and ownerId = any &#63; and read = &#63; and type = &#63; and subtype = &#63;.
	 *
	 * @param groupId the group ID
	 * @param createTime the create time
	 * @param ownerIds the owner IDs
	 * @param read the read
	 * @param type the type
	 * @param subtype the subtype
	 * @return the number of matching faro notifications
	 */
	public static int countByG_GtC_O_R_T_S(
		long groupId, long createTime, long[] ownerIds, boolean read,
		String type, String subtype) {

		return getPersistence().countByG_GtC_O_R_T_S(
			groupId, createTime, ownerIds, read, type, subtype);
	}

	/**
	 * Creates a new faro notification with the primary key. Does not add the faro notification to the database.
	 *
	 * @param faroNotificationId the primary key for the new faro notification
	 * @return the new faro notification
	 */
	public static FaroNotification create(long faroNotificationId) {
		return getPersistence().create(faroNotificationId);
	}

	/**
	 * Removes the faro notification with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroNotificationId the primary key of the faro notification
	 * @return the faro notification that was removed
	 * @throws NoSuchFaroNotificationException if a faro notification with the primary key could not be found
	 */
	public static FaroNotification remove(long faroNotificationId)
		throws com.liferay.osb.faro.exception.NoSuchFaroNotificationException {

		return getPersistence().remove(faroNotificationId);
	}

	public static FaroNotification updateImpl(
		FaroNotification faroNotification) {

		return getPersistence().updateImpl(faroNotification);
	}

	/**
	 * Returns the faro notification with the primary key or throws a <code>NoSuchFaroNotificationException</code> if it could not be found.
	 *
	 * @param faroNotificationId the primary key of the faro notification
	 * @return the faro notification
	 * @throws NoSuchFaroNotificationException if a faro notification with the primary key could not be found
	 */
	public static FaroNotification findByPrimaryKey(long faroNotificationId)
		throws com.liferay.osb.faro.exception.NoSuchFaroNotificationException {

		return getPersistence().findByPrimaryKey(faroNotificationId);
	}

	/**
	 * Returns the faro notification with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroNotificationId the primary key of the faro notification
	 * @return the faro notification, or <code>null</code> if a faro notification with the primary key could not be found
	 */
	public static FaroNotification fetchByPrimaryKey(long faroNotificationId) {
		return getPersistence().fetchByPrimaryKey(faroNotificationId);
	}

	public static FaroNotificationPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(FaroNotificationPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile FaroNotificationPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:711548143