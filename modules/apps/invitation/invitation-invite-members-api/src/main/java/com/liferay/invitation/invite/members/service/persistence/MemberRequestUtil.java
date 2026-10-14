/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service.persistence;

import com.liferay.invitation.invite.members.model.MemberRequest;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the member request service. This utility wraps <code>com.liferay.invitation.invite.members.service.persistence.impl.MemberRequestPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MemberRequestPersistence
 * @generated
 */
public class MemberRequestUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<MemberRequest> memberRequests) {
		getPersistence().cacheResult(memberRequests);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(MemberRequest memberRequest) {
		getPersistence().cacheResult(memberRequest);
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
	public static void clearCache(MemberRequest memberRequest) {
		getPersistence().clearCache(memberRequest);
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
	public static Map<Serializable, MemberRequest> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<MemberRequest> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<MemberRequest> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<MemberRequest> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<MemberRequest> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static MemberRequest update(MemberRequest memberRequest) {
		return getPersistence().update(memberRequest);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static MemberRequest update(
		MemberRequest memberRequest, ServiceContext serviceContext) {

		return getPersistence().update(memberRequest, serviceContext);
	}

	/**
	 * Returns the member request where key = &#63; or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param key the key
	 * @return the matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	public static MemberRequest findByKey(String key)
		throws com.liferay.invitation.invite.members.exception.
			NoSuchMemberRequestException {

		return getPersistence().findByKey(key);
	}

	/**
	 * Returns the member request where key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public static MemberRequest fetchByKey(String key, boolean useFinderCache) {
		return getPersistence().fetchByKey(key, useFinderCache);
	}

	/**
	 * Removes the member request where key = &#63; from the database.
	 *
	 * @param key the key
	 * @return the member request that was removed
	 */
	public static MemberRequest removeByKey(String key)
		throws com.liferay.invitation.invite.members.exception.
			NoSuchMemberRequestException {

		return getPersistence().removeByKey(key);
	}

	/**
	 * Returns the number of member requests where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching member requests
	 */
	public static int countByKey(String key) {
		return getPersistence().countByKey(key);
	}

	/**
	 * Returns an ordered range of all the member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.invitation.invite.members.model.impl.MemberRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of member requests
	 * @param end the upper bound of the range of member requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching member requests
	 */
	public static List<MemberRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		OrderByComparator<MemberRequest> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_S(
			receiverUserId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first member request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	public static MemberRequest findByR_S_First(
			long receiverUserId, int status,
			OrderByComparator<MemberRequest> orderByComparator)
		throws com.liferay.invitation.invite.members.exception.
			NoSuchMemberRequestException {

		return getPersistence().findByR_S_First(
			receiverUserId, status, orderByComparator);
	}

	/**
	 * Returns the first member request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public static MemberRequest fetchByR_S_First(
		long receiverUserId, int status,
		OrderByComparator<MemberRequest> orderByComparator) {

		return getPersistence().fetchByR_S_First(
			receiverUserId, status, orderByComparator);
	}

	/**
	 * Removes all the member requests where receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	public static void removeByR_S(long receiverUserId, int status) {
		getPersistence().removeByR_S(receiverUserId, status);
	}

	/**
	 * Returns the number of member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching member requests
	 */
	public static int countByR_S(long receiverUserId, int status) {
		return getPersistence().countByR_S(receiverUserId, status);
	}

	/**
	 * Returns the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	public static MemberRequest findByG_R_S(
			long groupId, long receiverUserId, int status)
		throws com.liferay.invitation.invite.members.exception.
			NoSuchMemberRequestException {

		return getPersistence().findByG_R_S(groupId, receiverUserId, status);
	}

	/**
	 * Returns the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public static MemberRequest fetchByG_R_S(
		long groupId, long receiverUserId, int status, boolean useFinderCache) {

		return getPersistence().fetchByG_R_S(
			groupId, receiverUserId, status, useFinderCache);
	}

	/**
	 * Removes the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the member request that was removed
	 */
	public static MemberRequest removeByG_R_S(
			long groupId, long receiverUserId, int status)
		throws com.liferay.invitation.invite.members.exception.
			NoSuchMemberRequestException {

		return getPersistence().removeByG_R_S(groupId, receiverUserId, status);
	}

	/**
	 * Returns the number of member requests where groupId = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching member requests
	 */
	public static int countByG_R_S(
		long groupId, long receiverUserId, int status) {

		return getPersistence().countByG_R_S(groupId, receiverUserId, status);
	}

	/**
	 * Creates a new member request with the primary key. Does not add the member request to the database.
	 *
	 * @param memberRequestId the primary key for the new member request
	 * @return the new member request
	 */
	public static MemberRequest create(long memberRequestId) {
		return getPersistence().create(memberRequestId);
	}

	/**
	 * Removes the member request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request that was removed
	 * @throws NoSuchMemberRequestException if a member request with the primary key could not be found
	 */
	public static MemberRequest remove(long memberRequestId)
		throws com.liferay.invitation.invite.members.exception.
			NoSuchMemberRequestException {

		return getPersistence().remove(memberRequestId);
	}

	public static MemberRequest updateImpl(MemberRequest memberRequest) {
		return getPersistence().updateImpl(memberRequest);
	}

	/**
	 * Returns the member request with the primary key or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request
	 * @throws NoSuchMemberRequestException if a member request with the primary key could not be found
	 */
	public static MemberRequest findByPrimaryKey(long memberRequestId)
		throws com.liferay.invitation.invite.members.exception.
			NoSuchMemberRequestException {

		return getPersistence().findByPrimaryKey(memberRequestId);
	}

	/**
	 * Returns the member request with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request, or <code>null</code> if a member request with the primary key could not be found
	 */
	public static MemberRequest fetchByPrimaryKey(long memberRequestId) {
		return getPersistence().fetchByPrimaryKey(memberRequestId);
	}

	/**
	 * Returns the member request where key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public static MemberRequest fetchByKey(String key) {
		return getPersistence().fetchByKey(key);
	}

	/**
	 * Returns the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public static MemberRequest fetchByG_R_S(
		long groupId, long receiverUserId, int status) {

		return getPersistence().fetchByG_R_S(groupId, receiverUserId, status);
	}

	/**
	 * Returns all the member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching member requests
	 */
	public static List<MemberRequest> findByR_S(
		long receiverUserId, int status) {

		return getPersistence().findByR_S(receiverUserId, status);
	}

	/**
	 * Returns a range of all the member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.invitation.invite.members.model.impl.MemberRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of member requests
	 * @param end the upper bound of the range of member requests (not inclusive)
	 * @return the range of matching member requests
	 */
	public static List<MemberRequest> findByR_S(
		long receiverUserId, int status, int start, int end) {

		return getPersistence().findByR_S(receiverUserId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.invitation.invite.members.model.impl.MemberRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of member requests
	 * @param end the upper bound of the range of member requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching member requests
	 */
	public static List<MemberRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		OrderByComparator<MemberRequest> orderByComparator) {

		return getPersistence().findByR_S(
			receiverUserId, status, start, end, orderByComparator);
	}

	public static MemberRequestPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(MemberRequestPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile MemberRequestPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1835848114