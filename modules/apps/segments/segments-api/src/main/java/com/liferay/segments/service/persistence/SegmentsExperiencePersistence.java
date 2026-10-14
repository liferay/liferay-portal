/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.segments.exception.NoSuchExperienceException;
import com.liferay.segments.model.SegmentsExperience;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the segments experience service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @see SegmentsExperienceUtil
 * @generated
 */
@ProviderType
public interface SegmentsExperiencePersistence
	extends BasePersistence<SegmentsExperience>,
			CTPersistence<SegmentsExperience> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SegmentsExperienceUtil} to access the segments experience persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Removes all the segments experiences where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of segments experiences where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experiences
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByUUID_G(String uuid, long groupId)
		throws NoSuchExperienceException;

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the segments experience where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experience that was removed
	 */
	public SegmentsExperience removeByUUID_G(String uuid, long groupId)
		throws NoSuchExperienceException;

	/**
	 * Returns the number of segments experiences where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Removes all the segments experiences where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experiences
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_P_First(
			long groupId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_P_First(
		long groupId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 */
	public void removeByG_P(long groupId, long plid);

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	public int countByG_P(long groupId, long plid);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_P(long groupId, long plid);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_A(
		long groupId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_A_First(
			long groupId, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_A_First(
		long groupId, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_A(
		long groupId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_A(
		long[] groupIds, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and active = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the segments experiences where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	public void removeByG_A(long groupId, boolean active);

	/**
	 * Returns the number of segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	public int countByG_A(long groupId, boolean active);

	/**
	 * Returns the number of segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	public int countByG_A(long[] groupIds, boolean active);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_A(long groupId, boolean active);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_A(long[] groupIds, boolean active);

	/**
	 * Returns an ordered range of all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findBySEERC_SESERC_First(
			String segmentsEntryERC, String segmentsEntryScopeERC,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchBySEERC_SESERC_First(
		String segmentsEntryERC, String segmentsEntryScopeERC,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Removes all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; from the database.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 */
	public void removeBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC);

	/**
	 * Returns the number of segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences
	 */
	public int countBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_SEERC_SESERC_First(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_SEERC_SESERC_First(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 */
	public void removeByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC);

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences
	 */
	public int countByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC);

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_SEK_P(
			long groupId, String segmentsExperienceKey, long plid)
		throws NoSuchExperienceException;

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid,
		boolean useFinderCache);

	/**
	 * Removes the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the segments experience that was removed
	 */
	public SegmentsExperience removeByG_SEK_P(
			long groupId, String segmentsExperienceKey, long plid)
		throws NoSuchExperienceException;

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	public int countByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid);

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_P_P(long groupId, long plid, int priority)
		throws NoSuchExperienceException;

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_P_P(
		long groupId, long plid, int priority, boolean useFinderCache);

	/**
	 * Removes the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the segments experience that was removed
	 */
	public SegmentsExperience removeByG_P_P(
			long groupId, long plid, int priority)
		throws NoSuchExperienceException;

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	public int countByG_P_P(long groupId, long plid, int priority);

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority);

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_P_GtP_First(
			long groupId, long plid, int priority,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_P_GtP_First(
		long groupId, long plid, int priority,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority);

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority, int start, int end);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 */
	public void removeByG_P_GtP(long groupId, long plid, int priority);

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	public int countByG_P_GtP(long groupId, long plid, int priority);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_P_GtP(long groupId, long plid, int priority);

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority);

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_P_LtP_First(
			long groupId, long plid, int priority,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_P_LtP_First(
		long groupId, long plid, int priority,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority);

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority, int start, int end);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 */
	public void removeByG_P_LtP(long groupId, long plid, int priority);

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	public int countByG_P_LtP(long groupId, long plid, int priority);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_P_LtP(long groupId, long plid, int priority);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_P_A_First(
			long groupId, long plid, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_P_A_First(
		long groupId, long plid, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 */
	public void removeByG_P_A(long groupId, long plid, boolean active);

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	public int countByG_P_A(long groupId, long plid, boolean active);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_P_A(long groupId, long plid, boolean active);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByG_SEERC_SESERC_P_A_First(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
				orderByComparator)
		throws NoSuchExperienceException;

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByG_SEERC_SESERC_P_A_First(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public java.util.List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator);

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public java.util.List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 */
	public void removeByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active);

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	public int countByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active);

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	public int countByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active);

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public int filterCountByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active);

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public SegmentsExperience findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchExperienceException;

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public SegmentsExperience fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the segments experience where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the segments experience that was removed
	 */
	public SegmentsExperience removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchExperienceException;

	/**
	 * Returns the number of segments experiences where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new segments experience with the primary key. Does not add the segments experience to the database.
	 *
	 * @param segmentsExperienceId the primary key for the new segments experience
	 * @return the new segments experience
	 */
	public SegmentsExperience create(long segmentsExperienceId);

	/**
	 * Removes the segments experience with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience that was removed
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public SegmentsExperience remove(long segmentsExperienceId)
		throws NoSuchExperienceException;

	public SegmentsExperience updateImpl(SegmentsExperience segmentsExperience);

	/**
	 * Returns the segments experience with the primary key or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public SegmentsExperience findByPrimaryKey(long segmentsExperienceId)
		throws NoSuchExperienceException;

	/**
	 * Returns the segments experience with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience, or <code>null</code> if a segments experience with the primary key could not be found
	 */
	public SegmentsExperience fetchByPrimaryKey(long segmentsExperienceId);

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public default SegmentsExperience fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public default SegmentsExperience fetchByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid) {

		return fetchByG_SEK_P(groupId, segmentsExperienceKey, plid, true);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public default SegmentsExperience fetchByG_P_P(
		long groupId, long plid, int priority) {

		return fetchByG_P_P(groupId, plid, priority, true);
	}

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public default SegmentsExperience fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the segments experiences where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_P(
		long groupId, long plid) {

		return findByG_P(
			groupId, plid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end) {

		return findByG_P(groupId, plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByG_P(groupId, plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid) {

		return filterFindByG_P(
			groupId, plid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid, int start, int end) {

		return filterFindByG_P(groupId, plid, start, end, null);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_A(
		long groupId, boolean active) {

		return findByG_A(
			groupId, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_A(
		long groupId, boolean active, int start, int end) {

		return findByG_A(groupId, active, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_A(
		long groupId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByG_A(groupId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active) {

		return findByG_A(
			groupIds, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active, int start, int end) {

		return findByG_A(groupIds, active, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByG_A(groupIds, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience> filterFindByG_A(
		long groupId, boolean active) {

		return filterFindByG_A(
			groupId, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience> filterFindByG_A(
		long groupId, boolean active, int start, int end) {

		return filterFindByG_A(groupId, active, start, end, null);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience> filterFindByG_A(
		long[] groupIds, boolean active) {

		return filterFindByG_A(
			groupIds, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience> filterFindByG_A(
		long[] groupIds, boolean active, int start, int end) {

		return filterFindByG_A(groupIds, active, start, end, null);
	}

	/**
	 * Returns all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC) {

		return findBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC, int start,
		int end) {

		return findBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		return findByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end) {

		return findByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience>
		filterFindByG_SEERC_SESERC(
			long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC) {

		return filterFindByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience>
		filterFindByG_SEERC_SESERC(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			int start, int end) {

		return filterFindByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, start, end, null);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active) {

		return findByG_P_A(
			groupId, plid, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end) {

		return findByG_P_A(groupId, plid, active, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByG_P_A(
			groupId, plid, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active) {

		return filterFindByG_P_A(
			groupId, plid, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active, int start, int end) {

		return filterFindByG_P_A(groupId, plid, active, start, end, null);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end) {

		return findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end) {

		return findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public default java.util.List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<SegmentsExperience>
			orderByComparator) {

		return findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience>
		filterFindByG_SEERC_SESERC_P_A(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, boolean active) {

		return filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience>
		filterFindByG_SEERC_SESERC_P_A(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, boolean active, int start, int end) {

		return filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			start, end, null);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience>
		filterFindByG_SEERC_SESERC_P_A(
			long groupId, String[] segmentsEntryERCs,
			String segmentsEntryScopeERC, long plid, boolean active) {

		return filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public default java.util.List<SegmentsExperience>
		filterFindByG_SEERC_SESERC_P_A(
			long groupId, String[] segmentsEntryERCs,
			String segmentsEntryScopeERC, long plid, boolean active, int start,
			int end) {

		return filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1892110534