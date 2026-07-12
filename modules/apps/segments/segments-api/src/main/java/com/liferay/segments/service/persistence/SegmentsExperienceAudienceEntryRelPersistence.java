/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.segments.exception.NoSuchExperienceAudienceEntryRelException;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the segments experience audience entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @see SegmentsExperienceAudienceEntryRelUtil
 * @generated
 */
@ProviderType
public interface SegmentsExperienceAudienceEntryRelPersistence
	extends BasePersistence<SegmentsExperienceAudienceEntryRel>,
			CTPersistence<SegmentsExperienceAudienceEntryRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SegmentsExperienceAudienceEntryRelUtil} to access the segments experience audience entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public java.util.List<SegmentsExperienceAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<SegmentsExperienceAudienceEntryRel> orderByComparator);

	/**
	 * Removes all the segments experience audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of segments experience audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experience audience entry rels
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the segments experience audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel findByUUID_G(
			String uuid, long groupId)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the segments experience audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the segments experience audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experience audience entry rel that was removed
	 */
	public SegmentsExperienceAudienceEntryRel removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the number of segments experience audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experience audience entry rels
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public java.util.List<SegmentsExperienceAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<SegmentsExperienceAudienceEntryRel> orderByComparator);

	/**
	 * Removes all the segments experience audience entry rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experience audience entry rels
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public java.util.List<SegmentsExperienceAudienceEntryRel> findByG_SEERC(
		long groupId, String segmentsExperienceERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel findByG_SEERC_First(
			long groupId, String segmentsExperienceERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel fetchByG_SEERC_First(
		long groupId, String segmentsExperienceERC,
		com.liferay.portal.kernel.util.OrderByComparator
			<SegmentsExperienceAudienceEntryRel> orderByComparator);

	/**
	 * Removes all the segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public void removeByG_SEERC(long groupId, String segmentsExperienceERC);

	/**
	 * Returns the number of segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching segments experience audience entry rels
	 */
	public int countByG_SEERC(long groupId, String segmentsExperienceERC);

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public java.util.List<SegmentsExperienceAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel findByC_AEERC_First(
			long companyId, String audienceEntryERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel fetchByC_AEERC_First(
		long companyId, String audienceEntryERC,
		com.liferay.portal.kernel.util.OrderByComparator
			<SegmentsExperienceAudienceEntryRel> orderByComparator);

	/**
	 * Removes all the segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public void removeByC_AEERC(long companyId, String audienceEntryERC);

	/**
	 * Returns the number of segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching segments experience audience entry rels
	 */
	public int countByC_AEERC(long companyId, String audienceEntryERC);

	/**
	 * Returns the segments experience audience entry rel where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63; or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel findByG_AEERC_SEERC(
			long groupId, String audienceEntryERC, String segmentsExperienceERC)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the segments experience audience entry rel where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public SegmentsExperienceAudienceEntryRel fetchByG_AEERC_SEERC(
		long groupId, String audienceEntryERC, String segmentsExperienceERC,
		boolean useFinderCache);

	/**
	 * Removes the segments experience audience entry rel where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the segments experience audience entry rel that was removed
	 */
	public SegmentsExperienceAudienceEntryRel removeByG_AEERC_SEERC(
			long groupId, String audienceEntryERC, String segmentsExperienceERC)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the number of segments experience audience entry rels where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching segments experience audience entry rels
	 */
	public int countByG_AEERC_SEERC(
		long groupId, String audienceEntryERC, String segmentsExperienceERC);

	/**
	 * Creates a new segments experience audience entry rel with the primary key. Does not add the segments experience audience entry rel to the database.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key for the new segments experience audience entry rel
	 * @return the new segments experience audience entry rel
	 */
	public SegmentsExperienceAudienceEntryRel create(
		long segmentsExperienceAudienceEntryRelId);

	/**
	 * Removes the segments experience audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was removed
	 * @throws NoSuchExperienceAudienceEntryRelException if a segments experience audience entry rel with the primary key could not be found
	 */
	public SegmentsExperienceAudienceEntryRel remove(
			long segmentsExperienceAudienceEntryRelId)
		throws NoSuchExperienceAudienceEntryRelException;

	public SegmentsExperienceAudienceEntryRel updateImpl(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel);

	/**
	 * Returns the segments experience audience entry rel with the primary key or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a segments experience audience entry rel with the primary key could not be found
	 */
	public SegmentsExperienceAudienceEntryRel findByPrimaryKey(
			long segmentsExperienceAudienceEntryRelId)
		throws NoSuchExperienceAudienceEntryRelException;

	/**
	 * Returns the segments experience audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel, or <code>null</code> if a segments experience audience entry rel with the primary key could not be found
	 */
	public SegmentsExperienceAudienceEntryRel fetchByPrimaryKey(
		long segmentsExperienceAudienceEntryRelId);

	/**
	 * Returns the segments experience audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public default SegmentsExperienceAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the segments experience audience entry rel where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public default SegmentsExperienceAudienceEntryRel fetchByG_AEERC_SEERC(
		long groupId, String audienceEntryERC, String segmentsExperienceERC) {

		return fetchByG_AEERC_SEERC(
			groupId, audienceEntryERC, segmentsExperienceERC, true);
	}

	/**
	 * Returns all the segments experience audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByUuid(String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experience audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @return the range of matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByUuid(String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByUuid(
			String uuid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByUuid_C(String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @return the range of matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByUuid_C(String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByUuid_C(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByG_SEERC(long groupId, String segmentsExperienceERC) {

		return findByG_SEERC(
			groupId, segmentsExperienceERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @return the range of matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByG_SEERC(
			long groupId, String segmentsExperienceERC, int start, int end) {

		return findByG_SEERC(
			groupId, segmentsExperienceERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByG_SEERC(
			long groupId, String segmentsExperienceERC, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator) {

		return findByG_SEERC(
			groupId, segmentsExperienceERC, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByC_AEERC(long companyId, String audienceEntryERC) {

		return findByC_AEERC(
			companyId, audienceEntryERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @return the range of matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByC_AEERC(
			long companyId, String audienceEntryERC, int start, int end) {

		return findByC_AEERC(
			companyId, audienceEntryERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public default java.util.List<SegmentsExperienceAudienceEntryRel>
		findByC_AEERC(
			long companyId, String audienceEntryERC, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator) {

		return findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-876106642