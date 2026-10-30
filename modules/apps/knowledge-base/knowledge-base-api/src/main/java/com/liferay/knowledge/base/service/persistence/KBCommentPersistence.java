/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence;

import com.liferay.knowledge.base.exception.NoSuchCommentException;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the kb comment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KBCommentUtil
 * @generated
 */
@ProviderType
public interface KBCommentPersistence
	extends BasePersistence<KBComment>, CTPersistence<KBComment> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link KBCommentUtil} to access the kb comment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public java.util.List<KBComment> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public KBComment findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<KBComment>
				orderByComparator)
		throws NoSuchCommentException;

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public KBComment fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator);

	/**
	 * Removes all the kb comments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of kb comments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kb comments
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the kb comment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCommentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public KBComment findByUUID_G(String uuid, long groupId)
		throws NoSuchCommentException;

	/**
	 * Returns the kb comment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public KBComment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the kb comment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kb comment that was removed
	 */
	public KBComment removeByUUID_G(String uuid, long groupId)
		throws NoSuchCommentException;

	/**
	 * Returns the number of kb comments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kb comments
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public java.util.List<KBComment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public KBComment findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<KBComment>
				orderByComparator)
		throws NoSuchCommentException;

	/**
	 * Returns the first kb comment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public KBComment fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator);

	/**
	 * Removes all the kb comments where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kb comments
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public java.util.List<KBComment> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public KBComment findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<KBComment>
				orderByComparator)
		throws NoSuchCommentException;

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public KBComment fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator);

	/**
	 * Removes all the kb comments where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of kb comments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching kb comments
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public java.util.List<KBComment> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public KBComment findByG_S_First(
			long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBComment>
				orderByComparator)
		throws NoSuchCommentException;

	/**
	 * Returns the first kb comment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public KBComment fetchByG_S_First(
		long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator);

	/**
	 * Removes all the kb comments where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public void removeByG_S(long groupId, int status);

	/**
	 * Returns the number of kb comments where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb comments
	 */
	public int countByG_S(long groupId, int status);

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public java.util.List<KBComment> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public KBComment findByC_C_First(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<KBComment>
				orderByComparator)
		throws NoSuchCommentException;

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public KBComment fetchByC_C_First(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator);

	/**
	 * Removes all the kb comments where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByC_C(long classNameId, long classPK);

	/**
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching kb comments
	 */
	public int countByC_C(long classNameId, long classPK);

	/**
	 * Returns an ordered range of all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public java.util.List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb comment in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public KBComment findByU_C_C_First(
			long userId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<KBComment>
				orderByComparator)
		throws NoSuchCommentException;

	/**
	 * Returns the first kb comment in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public KBComment fetchByU_C_C_First(
		long userId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator);

	/**
	 * Removes all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByU_C_C(long userId, long classNameId, long classPK);

	/**
	 * Returns the number of kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching kb comments
	 */
	public int countByU_C_C(long userId, long classNameId, long classPK);

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public java.util.List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment
	 * @throws NoSuchCommentException if a matching kb comment could not be found
	 */
	public KBComment findByC_C_S_First(
			long classNameId, long classPK, int status,
			com.liferay.portal.kernel.util.OrderByComparator<KBComment>
				orderByComparator)
		throws NoSuchCommentException;

	/**
	 * Returns the first kb comment in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public KBComment fetchByC_C_S_First(
		long classNameId, long classPK, int status,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator);

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb comments
	 */
	public java.util.List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	public void removeByC_C_S(long classNameId, long classPK, int status);

	/**
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching kb comments
	 */
	public int countByC_C_S(long classNameId, long classPK, int status);

	/**
	 * Returns the number of kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @return the number of matching kb comments
	 */
	public int countByC_C_S(long classNameId, long classPK, int[] statuses);

	/**
	 * Creates a new kb comment with the primary key. Does not add the kb comment to the database.
	 *
	 * @param kbCommentId the primary key for the new kb comment
	 * @return the new kb comment
	 */
	public KBComment create(long kbCommentId);

	/**
	 * Removes the kb comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment that was removed
	 * @throws NoSuchCommentException if a kb comment with the primary key could not be found
	 */
	public KBComment remove(long kbCommentId) throws NoSuchCommentException;

	public KBComment updateImpl(KBComment kbComment);

	/**
	 * Returns the kb comment with the primary key or throws a <code>NoSuchCommentException</code> if it could not be found.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment
	 * @throws NoSuchCommentException if a kb comment with the primary key could not be found
	 */
	public KBComment findByPrimaryKey(long kbCommentId)
		throws NoSuchCommentException;

	/**
	 * Returns the kb comment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kbCommentId the primary key of the kb comment
	 * @return the kb comment, or <code>null</code> if a kb comment with the primary key could not be found
	 */
	public KBComment fetchByPrimaryKey(long kbCommentId);

	/**
	 * Returns the kb comment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb comment, or <code>null</code> if a matching kb comment could not be found
	 */
	public default KBComment fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns all the kb comments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching kb comments
	 */
	public default java.util.List<KBComment> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kb comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public default java.util.List<KBComment> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public default java.util.List<KBComment> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching kb comments
	 */
	public default java.util.List<KBComment> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public default java.util.List<KBComment> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kb comments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public default java.util.List<KBComment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kb comments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching kb comments
	 */
	public default java.util.List<KBComment> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kb comments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public default java.util.List<KBComment> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public default java.util.List<KBComment> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb comments
	 */
	public default java.util.List<KBComment> findByG_S(
		long groupId, int status) {

		return findByG_S(
			groupId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public default java.util.List<KBComment> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kb comments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public default java.util.List<KBComment> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C(
		long classNameId, long classPK) {

		return findByC_C(
			classNameId, classPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return findByC_C(classNameId, classPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return findByC_C(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching kb comments
	 */
	public default java.util.List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK) {

		return findByU_C_C(
			userId, classNameId, classPK,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public default java.util.List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end) {

		return findByU_C_C(
			userId, classNameId, classPK, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kb comments where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public default java.util.List<KBComment> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return findByU_C_C(
			userId, classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status) {

		return findByC_C_S(
			classNameId, classPK, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end) {

		return findByC_C_S(
			classNameId, classPK, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return findByC_C_S(
			classNameId, classPK, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @return the matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses) {

		return findByC_C_S(
			classNameId, classPK, statuses,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @return the range of matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses, int start, int end) {

		return findByC_C_S(
			classNameId, classPK, statuses, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the kb comments where classNameId = &#63; and classPK = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBCommentModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb comments
	 * @param end the upper bound of the range of kb comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb comments
	 */
	public default java.util.List<KBComment> findByC_C_S(
		long classNameId, long classPK, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBComment>
			orderByComparator) {

		return findByC_C_S(
			classNameId, classPK, statuses, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1880896415