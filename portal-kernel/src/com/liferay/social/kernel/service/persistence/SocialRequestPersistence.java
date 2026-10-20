/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.exception.NoSuchRequestException;
import com.liferay.social.kernel.model.SocialRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the social request service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SocialRequestUtil
 * @generated
 */
@ProviderType
public interface SocialRequestPersistence
	extends BasePersistence<SocialRequest>, CTPersistence<SocialRequest> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SocialRequestUtil} to access the social request persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

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
	public java.util.List<SocialRequest> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social request in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public SocialRequest findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

	/**
	 * Returns the first social request in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public SocialRequest fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of social requests where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching social requests
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the social request where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchRequestException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public SocialRequest findByUUID_G(String uuid, long groupId)
		throws NoSuchRequestException;

	/**
	 * Returns the social request where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public SocialRequest fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the social request where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the social request that was removed
	 */
	public SocialRequest removeByUUID_G(String uuid, long groupId)
		throws NoSuchRequestException;

	/**
	 * Returns the number of social requests where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching social requests
	 */
	public int countByUUID_G(String uuid, long groupId);

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
	public java.util.List<SocialRequest> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social request in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public SocialRequest findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

	/**
	 * Returns the first social request in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public SocialRequest fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching social requests
	 */
	public int countByUuid_C(String uuid, long companyId);

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
	public java.util.List<SocialRequest> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social request in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public SocialRequest findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

	/**
	 * Returns the first social request in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public SocialRequest fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of social requests where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social requests
	 */
	public int countByUserId(long userId);

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
	public java.util.List<SocialRequest> findByReceiverUserId(
		long receiverUserId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public SocialRequest findByReceiverUserId_First(
			long receiverUserId,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public SocialRequest fetchByReceiverUserId_First(
		long receiverUserId,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where receiverUserId = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 */
	public void removeByReceiverUserId(long receiverUserId);

	/**
	 * Returns the number of social requests where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social requests
	 */
	public int countByReceiverUserId(long receiverUserId);

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
	public java.util.List<SocialRequest> findByU_S(
		long userId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public SocialRequest findByU_S_First(
			long userId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public SocialRequest fetchByU_S_First(
		long userId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where userId = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param status the status
	 */
	public void removeByU_S(long userId, int status);

	/**
	 * Returns the number of social requests where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching social requests
	 */
	public int countByU_S(long userId, int status);

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
	public java.util.List<SocialRequest> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public SocialRequest findByC_C_First(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public SocialRequest fetchByC_C_First(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByC_C(long classNameId, long classPK);

	/**
	 * Returns the number of social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social requests
	 */
	public int countByC_C(long classNameId, long classPK);

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
	public java.util.List<SocialRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	public SocialRequest findByR_S_First(
			long receiverUserId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public SocialRequest fetchByR_S_First(
		long receiverUserId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	public void removeByR_S(long receiverUserId, int status);

	/**
	 * Returns the number of social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching social requests
	 */
	public int countByR_S(long receiverUserId, int status);

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
	public SocialRequest findByU_C_C_T_R(
			long userId, long classNameId, long classPK, int type,
			long receiverUserId)
		throws NoSuchRequestException;

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
	public SocialRequest fetchByU_C_C_T_R(
		long userId, long classNameId, long classPK, int type,
		long receiverUserId, boolean useFinderCache);

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
	public SocialRequest removeByU_C_C_T_R(
			long userId, long classNameId, long classPK, int type,
			long receiverUserId)
		throws NoSuchRequestException;

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
	public int countByU_C_C_T_R(
		long userId, long classNameId, long classPK, int type,
		long receiverUserId);

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
	public java.util.List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

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
	public SocialRequest findByU_C_C_T_S_First(
			long userId, long classNameId, long classPK, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

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
	public SocialRequest fetchByU_C_C_T_S_First(
		long userId, long classNameId, long classPK, int type, int status,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 */
	public void removeByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status);

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
	public int countByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status);

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
	public java.util.List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator,
		boolean useFinderCache);

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
	public SocialRequest findByC_C_T_R_S_First(
			long classNameId, long classPK, int type, long receiverUserId,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
				orderByComparator)
		throws NoSuchRequestException;

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
	public SocialRequest fetchByC_C_T_R_S_First(
		long classNameId, long classPK, int type, long receiverUserId,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator);

	/**
	 * Removes all the social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	public void removeByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status);

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
	public int countByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status);

	/**
	 * Creates a new social request with the primary key. Does not add the social request to the database.
	 *
	 * @param requestId the primary key for the new social request
	 * @return the new social request
	 */
	public SocialRequest create(long requestId);

	/**
	 * Removes the social request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request that was removed
	 * @throws NoSuchRequestException if a social request with the primary key could not be found
	 */
	public SocialRequest remove(long requestId) throws NoSuchRequestException;

	public SocialRequest updateImpl(SocialRequest socialRequest);

	/**
	 * Returns the social request with the primary key or throws a <code>NoSuchRequestException</code> if it could not be found.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request
	 * @throws NoSuchRequestException if a social request with the primary key could not be found
	 */
	public SocialRequest findByPrimaryKey(long requestId)
		throws NoSuchRequestException;

	/**
	 * Returns the social request with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request, or <code>null</code> if a social request with the primary key could not be found
	 */
	public SocialRequest fetchByPrimaryKey(long requestId);

	/**
	 * Returns the social request where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	public default SocialRequest fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
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
	public default SocialRequest fetchByU_C_C_T_R(
		long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		return fetchByU_C_C_T_R(
			userId, classNameId, classPK, type, receiverUserId, true);
	}

	/**
	 * Returns all the social requests where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching social requests
	 */
	public default java.util.List<SocialRequest> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
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
	public default java.util.List<SocialRequest> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching social requests
	 */
	public default java.util.List<SocialRequest> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
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
	public default java.util.List<SocialRequest> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social requests where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching social requests
	 */
	public default java.util.List<SocialRequest> findByUserId(long userId) {
		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
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
	public default java.util.List<SocialRequest> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social requests where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the matching social requests
	 */
	public default java.util.List<SocialRequest> findByReceiverUserId(
		long receiverUserId) {

		return findByReceiverUserId(
			receiverUserId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByReceiverUserId(
		long receiverUserId, int start, int end) {

		return findByReceiverUserId(receiverUserId, start, end, null, true);
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
	public default java.util.List<SocialRequest> findByReceiverUserId(
		long receiverUserId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByReceiverUserId(
			receiverUserId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social requests where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching social requests
	 */
	public default java.util.List<SocialRequest> findByU_S(
		long userId, int status) {

		return findByU_S(
			userId, status, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByU_S(
		long userId, int status, int start, int end) {

		return findByU_S(userId, status, start, end, null, true);
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
	public default java.util.List<SocialRequest> findByU_S(
		long userId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByU_S(userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching social requests
	 */
	public default java.util.List<SocialRequest> findByC_C(
		long classNameId, long classPK) {

		return findByC_C(
			classNameId, classPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return findByC_C(classNameId, classPK, start, end, null, true);
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
	public default java.util.List<SocialRequest> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByC_C(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the matching social requests
	 */
	public default java.util.List<SocialRequest> findByR_S(
		long receiverUserId, int status) {

		return findByR_S(
			receiverUserId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByR_S(
		long receiverUserId, int status, int start, int end) {

		return findByR_S(receiverUserId, status, start, end, null, true);
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
	public default java.util.List<SocialRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByR_S(
			receiverUserId, status, start, end, orderByComparator, true);
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
	public default java.util.List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status) {

		return findByU_C_C_T_S(
			userId, classNameId, classPK, type, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status,
		int start, int end) {

		return findByU_C_C_T_S(
			userId, classNameId, classPK, type, status, start, end, null, true);
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
	public default java.util.List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByU_C_C_T_S(
			userId, classNameId, classPK, type, status, start, end,
			orderByComparator, true);
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
	public default java.util.List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status) {

		return findByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, int start, int end) {

		return findByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status, start, end,
			null, true);
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
	public default java.util.List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SocialRequest>
			orderByComparator) {

		return findByC_C_T_R_S(
			classNameId, classPK, type, receiverUserId, status, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2042221864