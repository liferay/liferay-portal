/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service.persistence;

import com.liferay.invitation.invite.members.exception.NoSuchMemberRequestException;
import com.liferay.invitation.invite.members.model.MemberRequest;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the member request service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MemberRequestUtil
 * @generated
 */
@ProviderType
public interface MemberRequestPersistence
	extends BasePersistence<MemberRequest> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link MemberRequestUtil} to access the member request persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the member request where key = &#63; or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param key the key
	 * @return the matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	public MemberRequest findByKey(String key)
		throws NoSuchMemberRequestException;

	/**
	 * Returns the member request where key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public MemberRequest fetchByKey(String key, boolean useFinderCache);

	/**
	 * Removes the member request where key = &#63; from the database.
	 *
	 * @param key the key
	 * @return the member request that was removed
	 */
	public MemberRequest removeByKey(String key)
		throws NoSuchMemberRequestException;

	/**
	 * Returns the number of member requests where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching member requests
	 */
	public int countByKey(String key);

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
	public java.util.List<MemberRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MemberRequest>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first member request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	public MemberRequest findByR_S_First(
			long receiverUserId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<MemberRequest>
				orderByComparator)
		throws NoSuchMemberRequestException;

	/**
	 * Returns the first member request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public MemberRequest fetchByR_S_First(
		long receiverUserId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<MemberRequest>
			orderByComparator);

	/**
	 * Removes all the member requests where receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	public void removeByR_S(long receiverUserId, int status);

	/**
	 * Returns the number of member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching member requests
	 */
	public int countByR_S(long receiverUserId, int status);

	/**
	 * Returns the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching member request
	 * @throws NoSuchMemberRequestException if a matching member request could not be found
	 */
	public MemberRequest findByG_R_S(
			long groupId, long receiverUserId, int status)
		throws NoSuchMemberRequestException;

	/**
	 * Returns the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public MemberRequest fetchByG_R_S(
		long groupId, long receiverUserId, int status, boolean useFinderCache);

	/**
	 * Removes the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the member request that was removed
	 */
	public MemberRequest removeByG_R_S(
			long groupId, long receiverUserId, int status)
		throws NoSuchMemberRequestException;

	/**
	 * Returns the number of member requests where groupId = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching member requests
	 */
	public int countByG_R_S(long groupId, long receiverUserId, int status);

	/**
	 * Creates a new member request with the primary key. Does not add the member request to the database.
	 *
	 * @param memberRequestId the primary key for the new member request
	 * @return the new member request
	 */
	public MemberRequest create(long memberRequestId);

	/**
	 * Removes the member request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request that was removed
	 * @throws NoSuchMemberRequestException if a member request with the primary key could not be found
	 */
	public MemberRequest remove(long memberRequestId)
		throws NoSuchMemberRequestException;

	public MemberRequest updateImpl(MemberRequest memberRequest);

	/**
	 * Returns the member request with the primary key or throws a <code>NoSuchMemberRequestException</code> if it could not be found.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request
	 * @throws NoSuchMemberRequestException if a member request with the primary key could not be found
	 */
	public MemberRequest findByPrimaryKey(long memberRequestId)
		throws NoSuchMemberRequestException;

	/**
	 * Returns the member request with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param memberRequestId the primary key of the member request
	 * @return the member request, or <code>null</code> if a member request with the primary key could not be found
	 */
	public MemberRequest fetchByPrimaryKey(long memberRequestId);

	/**
	 * Returns the member request where key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public default MemberRequest fetchByKey(String key) {
		return fetchByKey(key, true);
	}

	/**
	 * Returns the member request where groupId = &#63; and receiverUserId = &#63; and status = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching member request, or <code>null</code> if a matching member request could not be found
	 */
	public default MemberRequest fetchByG_R_S(
		long groupId, long receiverUserId, int status) {

		return fetchByG_R_S(groupId, receiverUserId, status, true);
	}

	/**
	 * Returns all the member requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching member requests
	 */
	public default java.util.List<MemberRequest> findByR_S(
		long receiverUserId, int status) {

		return findByR_S(
			receiverUserId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<MemberRequest> findByR_S(
		long receiverUserId, int status, int start, int end) {

		return findByR_S(receiverUserId, status, start, end, null, true);
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
	public default java.util.List<MemberRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MemberRequest>
			orderByComparator) {

		return findByR_S(
			receiverUserId, status, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1920388818