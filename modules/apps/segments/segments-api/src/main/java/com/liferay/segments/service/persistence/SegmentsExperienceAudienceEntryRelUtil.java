/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the segments experience audience entry rel service. This utility wraps <code>com.liferay.segments.service.persistence.impl.SegmentsExperienceAudienceEntryRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @see SegmentsExperienceAudienceEntryRelPersistence
 * @generated
 */
public class SegmentsExperienceAudienceEntryRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels) {

		getPersistence().cacheResult(segmentsExperienceAudienceEntryRels);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel) {

		getPersistence().cacheResult(segmentsExperienceAudienceEntryRel);
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
	public static void clearCache(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel) {

		getPersistence().clearCache(segmentsExperienceAudienceEntryRel);
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
	public static Map<Serializable, SegmentsExperienceAudienceEntryRel>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SegmentsExperienceAudienceEntryRel update(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel) {

		return getPersistence().update(segmentsExperienceAudienceEntryRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SegmentsExperienceAudienceEntryRel update(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel,
		ServiceContext serviceContext) {

		return getPersistence().update(
			segmentsExperienceAudienceEntryRel, serviceContext);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel findByUuid_First(
			String uuid,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByUuid_First(
		String uuid,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the segments experience audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of segments experience audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experience audience entry rels
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the segments experience audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the segments experience audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the segments experience audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experience audience entry rel that was removed
	 */
	public static SegmentsExperienceAudienceEntryRel removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of segments experience audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experience audience entry rels
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the segments experience audience entry rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experience audience entry rels
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findByG_SEERC(
		long groupId, String segmentsExperienceERC, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_SEERC(
			groupId, segmentsExperienceERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel findByG_SEERC_First(
			long groupId, String segmentsExperienceERC,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().findByG_SEERC_First(
			groupId, segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByG_SEERC_First(
		long groupId, String segmentsExperienceERC,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().fetchByG_SEERC_First(
			groupId, segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Removes all the segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public static void removeByG_SEERC(
		long groupId, String segmentsExperienceERC) {

		getPersistence().removeByG_SEERC(groupId, segmentsExperienceERC);
	}

	/**
	 * Returns the number of segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching segments experience audience entry rels
	 */
	public static int countByG_SEERC(
		long groupId, String segmentsExperienceERC) {

		return getPersistence().countByG_SEERC(groupId, segmentsExperienceERC);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel findByC_AEERC_First(
			long companyId, String audienceEntryERC,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().findByC_AEERC_First(
			companyId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Returns the first segments experience audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByC_AEERC_First(
		long companyId, String audienceEntryERC,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().fetchByC_AEERC_First(
			companyId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Removes all the segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public static void removeByC_AEERC(
		long companyId, String audienceEntryERC) {

		getPersistence().removeByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns the number of segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching segments experience audience entry rels
	 */
	public static int countByC_AEERC(long companyId, String audienceEntryERC) {
		return getPersistence().countByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns the segments experience audience entry rel where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63; or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel findByG_AEERC_SEERC(
			long groupId, String audienceEntryERC, String segmentsExperienceERC)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().findByG_AEERC_SEERC(
			groupId, audienceEntryERC, segmentsExperienceERC);
	}

	/**
	 * Returns the segments experience audience entry rel where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByG_AEERC_SEERC(
		long groupId, String audienceEntryERC, String segmentsExperienceERC,
		boolean useFinderCache) {

		return getPersistence().fetchByG_AEERC_SEERC(
			groupId, audienceEntryERC, segmentsExperienceERC, useFinderCache);
	}

	/**
	 * Removes the segments experience audience entry rel where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the segments experience audience entry rel that was removed
	 */
	public static SegmentsExperienceAudienceEntryRel removeByG_AEERC_SEERC(
			long groupId, String audienceEntryERC, String segmentsExperienceERC)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().removeByG_AEERC_SEERC(
			groupId, audienceEntryERC, segmentsExperienceERC);
	}

	/**
	 * Returns the number of segments experience audience entry rels where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching segments experience audience entry rels
	 */
	public static int countByG_AEERC_SEERC(
		long groupId, String audienceEntryERC, String segmentsExperienceERC) {

		return getPersistence().countByG_AEERC_SEERC(
			groupId, audienceEntryERC, segmentsExperienceERC);
	}

	/**
	 * Creates a new segments experience audience entry rel with the primary key. Does not add the segments experience audience entry rel to the database.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key for the new segments experience audience entry rel
	 * @return the new segments experience audience entry rel
	 */
	public static SegmentsExperienceAudienceEntryRel create(
		long segmentsExperienceAudienceEntryRelId) {

		return getPersistence().create(segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Removes the segments experience audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was removed
	 * @throws NoSuchExperienceAudienceEntryRelException if a segments experience audience entry rel with the primary key could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel remove(
			long segmentsExperienceAudienceEntryRelId)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().remove(segmentsExperienceAudienceEntryRelId);
	}

	public static SegmentsExperienceAudienceEntryRel updateImpl(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel) {

		return getPersistence().updateImpl(segmentsExperienceAudienceEntryRel);
	}

	/**
	 * Returns the segments experience audience entry rel with the primary key or throws a <code>NoSuchExperienceAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel
	 * @throws NoSuchExperienceAudienceEntryRelException if a segments experience audience entry rel with the primary key could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel findByPrimaryKey(
			long segmentsExperienceAudienceEntryRelId)
		throws com.liferay.segments.exception.
			NoSuchExperienceAudienceEntryRelException {

		return getPersistence().findByPrimaryKey(
			segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Returns the segments experience audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel, or <code>null</code> if a segments experience audience entry rel with the primary key could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByPrimaryKey(
		long segmentsExperienceAudienceEntryRelId) {

		return getPersistence().fetchByPrimaryKey(
			segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Returns the segments experience audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the segments experience audience entry rel where groupId = &#63; and audienceEntryERC = &#63; and segmentsExperienceERC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel fetchByG_AEERC_SEERC(
		long groupId, String audienceEntryERC, String segmentsExperienceERC) {

		return getPersistence().fetchByG_AEERC_SEERC(
			groupId, audienceEntryERC, segmentsExperienceERC);
	}

	/**
	 * Returns all the segments experience audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findByUuid(
		String uuid) {

		return getPersistence().findByUuid(uuid);
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
	public static List<SegmentsExperienceAudienceEntryRel> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
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
	public static List<SegmentsExperienceAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the segments experience audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
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
	public static List<SegmentsExperienceAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
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
	public static List<SegmentsExperienceAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the segments experience audience entry rels where groupId = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findByG_SEERC(
		long groupId, String segmentsExperienceERC) {

		return getPersistence().findByG_SEERC(groupId, segmentsExperienceERC);
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
	public static List<SegmentsExperienceAudienceEntryRel> findByG_SEERC(
		long groupId, String segmentsExperienceERC, int start, int end) {

		return getPersistence().findByG_SEERC(
			groupId, segmentsExperienceERC, start, end);
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
	public static List<SegmentsExperienceAudienceEntryRel> findByG_SEERC(
		long groupId, String segmentsExperienceERC, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().findByG_SEERC(
			groupId, segmentsExperienceERC, start, end, orderByComparator);
	}

	/**
	 * Returns all the segments experience audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC) {

		return getPersistence().findByC_AEERC(companyId, audienceEntryERC);
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
	public static List<SegmentsExperienceAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end);
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
	public static List<SegmentsExperienceAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		OrderByComparator<SegmentsExperienceAudienceEntryRel>
			orderByComparator) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator);
	}

	public static SegmentsExperienceAudienceEntryRelPersistence
		getPersistence() {

		return _persistence;
	}

	public static void setPersistence(
		SegmentsExperienceAudienceEntryRelPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile SegmentsExperienceAudienceEntryRelPersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2005557337