/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the social request service. This utility wraps <code>com.liferay.portlet.social.service.persistence.impl.SocialRequestPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SocialRequestPersistence
 * @generated
 */
public class SocialRequestUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<SocialRequest> socialRequests) {
		getPersistence().cacheResult(socialRequests);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(SocialRequest socialRequest) {
		getPersistence().cacheResult(socialRequest);
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
	public static void clearCache(SocialRequest socialRequest) {
		getPersistence().clearCache(socialRequest);
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
	public static Map<Serializable, SocialRequest> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SocialRequest> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SocialRequest> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SocialRequest> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SocialRequest update(SocialRequest socialRequest) {
		return getPersistence().update(socialRequest);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SocialRequest update(
		SocialRequest socialRequest, ServiceContext serviceContext) {

		return getPersistence().update(socialRequest, serviceContext);
	}

	/**
	 * Returns an ordered range of all the social requests where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByUuid_First(
			String uuid, OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByUuid_First(
		String uuid, OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the social requests where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of social requests where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching social requests
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the social request where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchRequestException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByUUID_G(String uuid, long groupId)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the social request where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the social request where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the social request that was removed
	 */
	public static SocialRequest removeByUUID_G(String uuid, long groupId)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of social requests where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching social requests
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the social requests where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching social requests
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the social requests where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByUserId_First(
			long userId, OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByUserId_First(
		long userId, OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the social requests where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of social requests where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social requests
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns an ordered range of all the social requests where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByReceiverUserId(
		long receiverUserId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByReceiverUserId(
			receiverUserId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByReceiverUserId_First(
			long receiverUserId,
			OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByReceiverUserId_First(
			receiverUserId, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByReceiverUserId_First(
		long receiverUserId,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByReceiverUserId_First(
			receiverUserId, orderByComparator);
	}

	/**
	 * Removes all the social requests where receiverUserId = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 */
	public static void removeByReceiverUserId(long receiverUserId) {
		getPersistence().removeByReceiverUserId(receiverUserId);
	}

	/**
	 * Returns the number of social requests where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social requests
	 */
	public static int countByReceiverUserId(long receiverUserId) {
		return getPersistence().countByReceiverUserId(receiverUserId);
	}

	/**
	 * Returns an ordered range of all the social requests where userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByU_S(
		long userId, int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_S(
			userId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByU_S_First(
			long userId, int status,
			OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByU_S_First(
			userId, status, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByU_S_First(
		long userId, int status,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByU_S_First(
			userId, status, orderByComparator);
	}

	/**
	 * Removes all the social requests where userId = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param status the status
	 */
	public static void removeByU_S(long userId, int status) {
		getPersistence().removeByU_S(userId, status);
	}

	/**
	 * Returns the number of social requests where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching social requests
	 */
	public static int countByU_S(long userId, int status) {
		return getPersistence().countByU_S(userId, status);
	}

	/**
	 * Returns an ordered range of all the social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the social requests where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_C(long classNameId, long classPK) {
		getPersistence().removeByC_C(classNameId, classPK);
	}

	/**
	 * Returns the number of social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social requests
	 */
	public static int countByC_C(long classNameId, long classPK) {
		return getPersistence().countByC_C(classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_S(
			receiverUserId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByR_S_First(
			long receiverUserId, int status,
			OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByR_S_First(
			receiverUserId, status, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByR_S_First(
		long receiverUserId, int status,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByR_S_First(
			receiverUserId, status, orderByComparator);
	}

	/**
	 * Removes all the social requests where receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	public static void removeByR_S(long receiverUserId, int status) {
		getPersistence().removeByR_S(receiverUserId, status);
	}

	/**
	 * Returns the number of social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching social requests
	 */
	public static int countByR_S(long receiverUserId, int status) {
		return getPersistence().countByR_S(receiverUserId, status);
	}

	/**
	 * Returns the social request where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or throws a <code>NoSuchRequestException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByU_C_C_T_R(
			long userId, long classNameId, long classPK, int type,
			long receiverUserId)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByU_C_C_T_R(
			userId, classNameId, classPK, type, receiverUserId);
	}

	/**
	 * Returns the social request where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByU_C_C_T_R(
		long userId, long classNameId, long classPK, int type,
		long receiverUserId, boolean useFinderCache) {

		return getPersistence().fetchByU_C_C_T_R(
			userId, classNameId, classPK, type, receiverUserId, useFinderCache);
	}

	/**
	 * Removes the social request where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the social request that was removed
	 */
	public static SocialRequest removeByU_C_C_T_R(
			long userId, long classNameId, long classPK, int type,
			long receiverUserId)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().removeByU_C_C_T_R(
			userId, classNameId, classPK, type, receiverUserId);
	}

	/**
	 * Returns the number of social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social requests
	 */
	public static int countByU_C_C_T_R(
		long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		return getPersistence().countByU_C_C_T_R(
			userId, classNameId, classPK, type, receiverUserId);
	}

	/**
	 * Returns an ordered range of all the social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status,
		int start, int end, OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_C_C_T_S(
			userId, classNameId, classPK, type, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByU_C_C_T_S_First(
			long userId, long classNameId, long classPK, int type, int status,
			OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByU_C_C_T_S_First(
			userId, classNameId, classPK, type, status, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByU_C_C_T_S_First(
		long userId, long classNameId, long classPK, int type, int status,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByU_C_C_T_S_First(
			userId, classNameId, classPK, type, status, orderByComparator);
	}

	/**
	 * Removes all the social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status) {

		getPersistence().removeByU_C_C_T_S(
			userId, classNameId, classPK, type, status);
	}

	/**
	 * Returns the number of social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @return the number of matching social requests
	 */
	public static int countByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status) {

		return getPersistence().countByU_C_C_T_S(
			userId, classNameId, classPK, type, status);
	}

	/**
	 * Returns an ordered range of all the social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public static SocialRequest findByC_C_T_R_S_First(
			long classNameId, long classPK, int type, long receiverUserId,
			int status, OrderByComparator<SocialRequest> orderByComparator)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByC_C_T_R_S_First(
			classNameId, classPK, type, receiverUserId, status,
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByC_C_T_R_S_First(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().fetchByC_C_T_R_S_First(
			classNameId, classPK, type, receiverUserId, status,
			orderByComparator);
	}

	/**
	 * Removes all the social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	public static void removeByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status) {

		getPersistence().removeByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status);
	}

	/**
	 * Returns the number of social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching social requests
	 */
	public static int countByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status) {

		return getPersistence().countByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status);
	}

	/**
	 * Creates a new social request with the primary key. Does not add the social request to the database.
	 *
	 * @param requestId the primary key for the new social request
	 * @return the new social request
	 */
	public static SocialRequest create(long requestId) {
		return getPersistence().create(requestId);
	}

	/**
	 * Removes the social request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request that was removed
	 * @throws NoSuchRequestException if a social request with the primary key could not be found
	 */
	public static SocialRequest remove(long requestId)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().remove(requestId);
	}

	public static SocialRequest updateImpl(SocialRequest socialRequest) {
		return getPersistence().updateImpl(socialRequest);
	}

	/**
	 * Returns the social request with the primary key or throws a <code>NoSuchRequestException</code> if it could not be found.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request
	 * @throws NoSuchRequestException if a social request with the primary key could not be found
	 */
	public static SocialRequest findByPrimaryKey(long requestId)
		throws com.liferay.social.kernel.exception.NoSuchRequestException {

		return getPersistence().findByPrimaryKey(requestId);
	}

	/**
	 * Returns the social request with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request, or <code>null</code> if a social request with the primary key could not be found
	 */
	public static SocialRequest fetchByPrimaryKey(long requestId) {
		return getPersistence().fetchByPrimaryKey(requestId);
	}

	/**
	 * Returns the social request where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the social request where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public static SocialRequest fetchByU_C_C_T_R(
		long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		return getPersistence().fetchByU_C_C_T_R(
			userId, classNameId, classPK, type, receiverUserId);
	}

	/**
	 * Returns all the social requests where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the social requests where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social requests where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the social requests where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social requests where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByReceiverUserId(
		long receiverUserId) {

		return getPersistence().findByReceiverUserId(receiverUserId);
	}

	/**
	 * Returns a range of all the social requests where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByReceiverUserId(
		long receiverUserId, int start, int end) {

		return getPersistence().findByReceiverUserId(
			receiverUserId, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByReceiverUserId(
		long receiverUserId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByReceiverUserId(
			receiverUserId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social requests where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByU_S(long userId, int status) {
		return getPersistence().findByU_S(userId, status);
	}

	/**
	 * Returns a range of all the social requests where userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByU_S(
		long userId, int status, int start, int end) {

		return getPersistence().findByU_S(userId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByU_S(
		long userId, int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByU_S(
			userId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByC_C(
		long classNameId, long classPK) {

		return getPersistence().findByC_C(classNameId, classPK);
	}

	/**
	 * Returns a range of all the social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_C(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByR_S(
		long receiverUserId, int status) {

		return getPersistence().findByR_S(receiverUserId, status);
	}

	/**
	 * Returns a range of all the social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByR_S(
		long receiverUserId, int status, int start, int end) {

		return getPersistence().findByR_S(receiverUserId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByR_S(
			receiverUserId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status) {

		return getPersistence().findByU_C_C_T_S(
			userId, classNameId, classPK, type, status);
	}

	/**
	 * Returns a range of all the social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status,
		int start, int end) {

		return getPersistence().findByU_C_C_T_S(
			userId, classNameId, classPK, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status,
		int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByU_C_C_T_S(
			userId, classNameId, classPK, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching social requests
	 */
	public static List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status) {

		return getPersistence().findByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status);
	}

	/**
	 * Returns a range of all the social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @return the range of matching social requests
	 */
	public static List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, int start, int end) {

		return getPersistence().findByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social requests
	 */
	public static List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator) {

		return getPersistence().findByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status, start, end,
			orderByComparator);
	}

	public static SocialRequestPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(SocialRequestPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile SocialRequestPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-241126866