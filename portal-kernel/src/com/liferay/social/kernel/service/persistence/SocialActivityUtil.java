/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialActivity;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the social activity service. This utility wraps <code>com.liferay.portlet.social.service.persistence.impl.SocialActivityPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityPersistence
 * @generated
 */
public class SocialActivityUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<SocialActivity> socialActivities) {
		getPersistence().cacheResult(socialActivities);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(SocialActivity socialActivity) {
		getPersistence().cacheResult(socialActivity);
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
	public static void clearCache(SocialActivity socialActivity) {
		getPersistence().clearCache(socialActivity);
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
	public static Map<Serializable, SocialActivity> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SocialActivity> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SocialActivity> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SocialActivity> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SocialActivity update(SocialActivity socialActivity) {
		return getPersistence().update(socialActivity);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SocialActivity update(
		SocialActivity socialActivity, ServiceContext serviceContext) {

		return getPersistence().update(socialActivity, serviceContext);
	}

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByGroupId_First(
			long groupId, OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByGroupId_First(
		long groupId, OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the social activities where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of social activities where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activities
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the social activities where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByUserId_First(
			long userId, OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByUserId_First(
		long userId, OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the social activities where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of social activities where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social activities
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns an ordered range of all the social activities where activitySetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param activitySetId the activity set ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByActivitySetId(
		long activitySetId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByActivitySetId(
			activitySetId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByActivitySetId_First(
			long activitySetId,
			OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByActivitySetId_First(
			activitySetId, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByActivitySetId_First(
		long activitySetId,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByActivitySetId_First(
			activitySetId, orderByComparator);
	}

	/**
	 * Removes all the social activities where activitySetId = &#63; from the database.
	 *
	 * @param activitySetId the activity set ID
	 */
	public static void removeByActivitySetId(long activitySetId) {
		getPersistence().removeByActivitySetId(activitySetId);
	}

	/**
	 * Returns the number of social activities where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @return the number of matching social activities
	 */
	public static int countByActivitySetId(long activitySetId) {
		return getPersistence().countByActivitySetId(activitySetId);
	}

	/**
	 * Returns the social activity where mirrorActivityId = &#63; or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByMirrorActivityId(long mirrorActivityId)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByMirrorActivityId(mirrorActivityId);
	}

	/**
	 * Returns the social activity where mirrorActivityId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByMirrorActivityId(
		long mirrorActivityId, boolean useFinderCache) {

		return getPersistence().fetchByMirrorActivityId(
			mirrorActivityId, useFinderCache);
	}

	/**
	 * Removes the social activity where mirrorActivityId = &#63; from the database.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the social activity that was removed
	 */
	public static SocialActivity removeByMirrorActivityId(long mirrorActivityId)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().removeByMirrorActivityId(mirrorActivityId);
	}

	/**
	 * Returns the number of social activities where mirrorActivityId = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the number of matching social activities
	 */
	public static int countByMirrorActivityId(long mirrorActivityId) {
		return getPersistence().countByMirrorActivityId(mirrorActivityId);
	}

	/**
	 * Returns an ordered range of all the social activities where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByReceiverUserId(
		long receiverUserId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByReceiverUserId(
			receiverUserId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByReceiverUserId_First(
			long receiverUserId,
			OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByReceiverUserId_First(
			receiverUserId, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByReceiverUserId_First(
		long receiverUserId,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByReceiverUserId_First(
			receiverUserId, orderByComparator);
	}

	/**
	 * Removes all the social activities where receiverUserId = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 */
	public static void removeByReceiverUserId(long receiverUserId) {
		getPersistence().removeByReceiverUserId(receiverUserId);
	}

	/**
	 * Returns the number of social activities where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	public static int countByReceiverUserId(long receiverUserId) {
		return getPersistence().countByReceiverUserId(receiverUserId);
	}

	/**
	 * Returns an ordered range of all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CN(
			companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByC_CN_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByC_CN_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the social activities where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public static void removeByC_CN(long companyId, long classNameId) {
		getPersistence().removeByC_CN(companyId, classNameId);
	}

	/**
	 * Returns the number of social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching social activities
	 */
	public static int countByC_CN(long companyId, long classNameId) {
		return getPersistence().countByC_CN(companyId, classNameId);
	}

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the social activities where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_C(long classNameId, long classPK) {
		getPersistence().removeByC_C(classNameId, classPK);
	}

	/**
	 * Returns the number of social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social activities
	 */
	public static int countByC_C(long classNameId, long classPK) {
		return getPersistence().countByC_C(classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end, OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByM_C_C(
			mirrorActivityId, classNameId, classPK, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByM_C_C_First(
			long mirrorActivityId, long classNameId, long classPK,
			OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByM_C_C_First(
			mirrorActivityId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByM_C_C_First(
		long mirrorActivityId, long classNameId, long classPK,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByM_C_C_First(
			mirrorActivityId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByM_C_C(
		long mirrorActivityId, long classNameId, long classPK) {

		getPersistence().removeByM_C_C(mirrorActivityId, classNameId, classPK);
	}

	/**
	 * Returns the number of social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social activities
	 */
	public static int countByM_C_C(
		long mirrorActivityId, long classNameId, long classPK) {

		return getPersistence().countByM_C_C(
			mirrorActivityId, classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_T(
			classNameId, classPK, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByC_C_T_First(
			long classNameId, long classPK, int type,
			OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByC_C_T_First(
			classNameId, classPK, type, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByC_C_T_First(
		long classNameId, long classPK, int type,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByC_C_T_First(
			classNameId, classPK, type, orderByComparator);
	}

	/**
	 * Removes all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	public static void removeByC_C_T(long classNameId, long classPK, int type) {
		getPersistence().removeByC_C_T(classNameId, classPK, type);
	}

	/**
	 * Returns the number of social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching social activities
	 */
	public static int countByC_C_T(long classNameId, long classPK, int type) {
		return getPersistence().countByC_C_T(classNameId, classPK, type);
	}

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByG_U_C_C_T_R_First(
			long groupId, long userId, long classNameId, long classPK, int type,
			long receiverUserId,
			OrderByComparator<SocialActivity> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByG_U_C_C_T_R_First(
			groupId, userId, classNameId, classPK, type, receiverUserId,
			orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByG_U_C_C_T_R_First(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().fetchByG_U_C_C_T_R_First(
			groupId, userId, classNameId, classPK, type, receiverUserId,
			orderByComparator);
	}

	/**
	 * Removes all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 */
	public static void removeByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		getPersistence().removeByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId);
	}

	/**
	 * Returns the number of social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	public static int countByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		return getPersistence().countByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId);
	}

	/**
	 * Returns the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	public static SocialActivity findByG_U_CD_C_C_T_R(
			long groupId, long userId, long createDate, long classNameId,
			long classPK, int type, long receiverUserId)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByG_U_CD_C_C_T_R(
			groupId, userId, createDate, classNameId, classPK, type,
			receiverUserId);
	}

	/**
	 * Returns the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByG_U_CD_C_C_T_R(
		long groupId, long userId, long createDate, long classNameId,
		long classPK, int type, long receiverUserId, boolean useFinderCache) {

		return getPersistence().fetchByG_U_CD_C_C_T_R(
			groupId, userId, createDate, classNameId, classPK, type,
			receiverUserId, useFinderCache);
	}

	/**
	 * Removes the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the social activity that was removed
	 */
	public static SocialActivity removeByG_U_CD_C_C_T_R(
			long groupId, long userId, long createDate, long classNameId,
			long classPK, int type, long receiverUserId)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().removeByG_U_CD_C_C_T_R(
			groupId, userId, createDate, classNameId, classPK, type,
			receiverUserId);
	}

	/**
	 * Returns the number of social activities where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	public static int countByG_U_CD_C_C_T_R(
		long groupId, long userId, long createDate, long classNameId,
		long classPK, int type, long receiverUserId) {

		return getPersistence().countByG_U_CD_C_C_T_R(
			groupId, userId, createDate, classNameId, classPK, type,
			receiverUserId);
	}

	/**
	 * Creates a new social activity with the primary key. Does not add the social activity to the database.
	 *
	 * @param activityId the primary key for the new social activity
	 * @return the new social activity
	 */
	public static SocialActivity create(long activityId) {
		return getPersistence().create(activityId);
	}

	/**
	 * Removes the social activity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity that was removed
	 * @throws NoSuchActivityException if a social activity with the primary key could not be found
	 */
	public static SocialActivity remove(long activityId)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().remove(activityId);
	}

	public static SocialActivity updateImpl(SocialActivity socialActivity) {
		return getPersistence().updateImpl(socialActivity);
	}

	/**
	 * Returns the social activity with the primary key or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity
	 * @throws NoSuchActivityException if a social activity with the primary key could not be found
	 */
	public static SocialActivity findByPrimaryKey(long activityId)
		throws com.liferay.social.kernel.exception.NoSuchActivityException {

		return getPersistence().findByPrimaryKey(activityId);
	}

	/**
	 * Returns the social activity with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity, or <code>null</code> if a social activity with the primary key could not be found
	 */
	public static SocialActivity fetchByPrimaryKey(long activityId) {
		return getPersistence().fetchByPrimaryKey(activityId);
	}

	/**
	 * Returns the social activity where mirrorActivityId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByMirrorActivityId(
		long mirrorActivityId) {

		return getPersistence().fetchByMirrorActivityId(mirrorActivityId);
	}

	/**
	 * Returns the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	public static SocialActivity fetchByG_U_CD_C_C_T_R(
		long groupId, long userId, long createDate, long classNameId,
		long classPK, int type, long receiverUserId) {

		return getPersistence().fetchByG_U_CD_C_C_T_R(
			groupId, userId, createDate, classNameId, classPK, type,
			receiverUserId);
	}

	/**
	 * Returns all the social activities where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the social activities where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social activities where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the social activities where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the social activities where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social activities where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByActivitySetId(long activitySetId) {
		return getPersistence().findByActivitySetId(activitySetId);
	}

	/**
	 * Returns a range of all the social activities where activitySetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param activitySetId the activity set ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByActivitySetId(
		long activitySetId, int start, int end) {

		return getPersistence().findByActivitySetId(activitySetId, start, end);
	}

	/**
	 * Returns an ordered range of all the social activities where activitySetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param activitySetId the activity set ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByActivitySetId(
		long activitySetId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByActivitySetId(
			activitySetId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social activities where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByReceiverUserId(
		long receiverUserId) {

		return getPersistence().findByReceiverUserId(receiverUserId);
	}

	/**
	 * Returns a range of all the social activities where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByReceiverUserId(
		long receiverUserId, int start, int end) {

		return getPersistence().findByReceiverUserId(
			receiverUserId, start, end);
	}

	/**
	 * Returns an ordered range of all the social activities where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByReceiverUserId(
		long receiverUserId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByReceiverUserId(
			receiverUserId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByC_CN(
		long companyId, long classNameId) {

		return getPersistence().findByC_CN(companyId, classNameId);
	}

	/**
	 * Returns a range of all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return getPersistence().findByC_CN(companyId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByC_CN(
			companyId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByC_C(
		long classNameId, long classPK) {

		return getPersistence().findByC_C(classNameId, classPK);
	}

	/**
	 * Returns a range of all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_C(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK) {

		return getPersistence().findByM_C_C(
			mirrorActivityId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end) {

		return getPersistence().findByM_C_C(
			mirrorActivityId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end, OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByM_C_C(
			mirrorActivityId, classNameId, classPK, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type) {

		return getPersistence().findByC_C_T(classNameId, classPK, type);
	}

	/**
	 * Returns a range of all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end) {

		return getPersistence().findByC_C_T(
			classNameId, classPK, type, start, end);
	}

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByC_C_T(
			classNameId, classPK, type, start, end, orderByComparator);
	}

	/**
	 * Returns all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activities
	 */
	public static List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		return getPersistence().findByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId);
	}

	/**
	 * Returns a range of all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @return the range of matching social activities
	 */
	public static List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId, int start, int end) {

		return getPersistence().findByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId, start,
			end);
	}

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activities
	 */
	public static List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator) {

		return getPersistence().findByG_U_C_C_T_R(
			groupId, userId, classNameId, classPK, type, receiverUserId, start,
			end, orderByComparator);
	}

	public static SocialActivityPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(SocialActivityPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile SocialActivityPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-986932828