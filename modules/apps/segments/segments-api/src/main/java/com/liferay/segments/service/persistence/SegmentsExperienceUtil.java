/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.model.SegmentsExperience;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the segments experience service. This utility wraps <code>com.liferay.segments.service.persistence.impl.SegmentsExperiencePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Garcia
 * @see SegmentsExperiencePersistence
 * @generated
 */
public class SegmentsExperienceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(SegmentsExperience segmentsExperience) {
		getPersistence().clearCache(segmentsExperience);
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
	public static Map<Serializable, SegmentsExperience> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SegmentsExperience> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SegmentsExperience> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SegmentsExperience> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SegmentsExperience update(
		SegmentsExperience segmentsExperience) {

		return getPersistence().update(segmentsExperience);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SegmentsExperience update(
		SegmentsExperience segmentsExperience, ServiceContext serviceContext) {

		return getPersistence().update(segmentsExperience, serviceContext);
	}

	/**
	 * Returns all the segments experiences where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByUuid_First(
			String uuid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByUuid_First(
		String uuid, OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByUuid_Last(
			String uuid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByUuid_Last(
		String uuid, OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where uuid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByUuid_PrevAndNext(
			long segmentsExperienceId, String uuid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByUuid_PrevAndNext(
			segmentsExperienceId, uuid, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of segments experiences where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching segments experiences
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByUUID_G(String uuid, long groupId)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the segments experience where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the segments experience where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the segments experience that was removed
	 */
	public static SegmentsExperience removeByUUID_G(String uuid, long groupId)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of segments experiences where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByUuid_C_PrevAndNext(
			long segmentsExperienceId, String uuid, long companyId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByUuid_C_PrevAndNext(
			segmentsExperienceId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of segments experiences where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching segments experiences
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByGroupId_First(
			long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByGroupId_First(
		long groupId, OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByGroupId_Last(
			long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByGroupId_Last(
		long groupId, OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByGroupId_PrevAndNext(
			long segmentsExperienceId, long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByGroupId_PrevAndNext(
			segmentsExperienceId, groupId, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] filterFindByGroupId_PrevAndNext(
			long segmentsExperienceId, long groupId,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByGroupId_PrevAndNext(
			segmentsExperienceId, groupId, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P(long groupId, long plid) {
		return getPersistence().findByG_P(groupId, plid);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end) {

		return getPersistence().findByG_P(groupId, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, plid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, plid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_P_First(
			long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_First(
			groupId, plid, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_First(
		long groupId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_P_First(
			groupId, plid, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_P_Last(
			long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_Last(
			groupId, plid, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_Last(
		long groupId, long plid,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_P_Last(
			groupId, plid, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByG_P_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_PrevAndNext(
			segmentsExperienceId, groupId, plid, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid) {

		return getPersistence().filterFindByG_P(groupId, plid);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid, int start, int end) {

		return getPersistence().filterFindByG_P(groupId, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P(
		long groupId, long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_P(
			groupId, plid, start, end, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] filterFindByG_P_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByG_P_PrevAndNext(
			segmentsExperienceId, groupId, plid, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 */
	public static void removeByG_P(long groupId, long plid) {
		getPersistence().removeByG_P(groupId, plid);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	public static int countByG_P(long groupId, long plid) {
		return getPersistence().countByG_P(groupId, plid);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByG_P(long groupId, long plid) {
		return getPersistence().filterCountByG_P(groupId, plid);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_A(
		long groupId, boolean active) {

		return getPersistence().findByG_A(groupId, active);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_A(
		long groupId, boolean active, int start, int end) {

		return getPersistence().findByG_A(groupId, active, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_A(
			groupId, active, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_A(
			groupId, active, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_A_First(
			long groupId, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_A_First(
			groupId, active, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_A_First(
		long groupId, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_A_First(
			groupId, active, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_A_Last(
			long groupId, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_A_Last(
			groupId, active, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_A_Last(
		long groupId, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_A_Last(
			groupId, active, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByG_A_PrevAndNext(
			long segmentsExperienceId, long groupId, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_A_PrevAndNext(
			segmentsExperienceId, groupId, active, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_A(
		long groupId, boolean active) {

		return getPersistence().filterFindByG_A(groupId, active);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_A(
		long groupId, boolean active, int start, int end) {

		return getPersistence().filterFindByG_A(groupId, active, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_A(
			groupId, active, start, end, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] filterFindByG_A_PrevAndNext(
			long segmentsExperienceId, long groupId, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByG_A_PrevAndNext(
			segmentsExperienceId, groupId, active, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_A(
		long[] groupIds, boolean active) {

		return getPersistence().filterFindByG_A(groupIds, active);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_A(
		long[] groupIds, boolean active, int start, int end) {

		return getPersistence().filterFindByG_A(groupIds, active, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_A(
		long[] groupIds, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_A(
			groupIds, active, start, end, orderByComparator);
	}

	/**
	 * Returns all the segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active) {

		return getPersistence().findByG_A(groupIds, active);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active, int start, int end) {

		return getPersistence().findByG_A(groupIds, active, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_A(
			groupIds, active, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and active = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_A(
		long[] groupIds, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_A(
			groupIds, active, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	public static void removeByG_A(long groupId, boolean active) {
		getPersistence().removeByG_A(groupId, active);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	public static int countByG_A(long groupId, boolean active) {
		return getPersistence().countByG_A(groupId, active);
	}

	/**
	 * Returns the number of segments experiences where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	public static int countByG_A(long[] groupIds, boolean active) {
		return getPersistence().countByG_A(groupIds, active);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByG_A(long groupId, boolean active) {
		return getPersistence().filterCountByG_A(groupId, active);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = any &#63; and active = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByG_A(long[] groupIds, boolean active) {
		return getPersistence().filterCountByG_A(groupIds, active);
	}

	/**
	 * Returns all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC) {

		return getPersistence().findBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC);
	}

	/**
	 * Returns a range of all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC, int start,
		int end) {

		return getPersistence().findBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC, int start,
		int end, OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC, int start,
		int end, OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findBySEERC_SESERC_First(
			String segmentsEntryERC, String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findBySEERC_SESERC_First(
			segmentsEntryERC, segmentsEntryScopeERC, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchBySEERC_SESERC_First(
		String segmentsEntryERC, String segmentsEntryScopeERC,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchBySEERC_SESERC_First(
			segmentsEntryERC, segmentsEntryScopeERC, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findBySEERC_SESERC_Last(
			String segmentsEntryERC, String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findBySEERC_SESERC_Last(
			segmentsEntryERC, segmentsEntryScopeERC, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchBySEERC_SESERC_Last(
		String segmentsEntryERC, String segmentsEntryScopeERC,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchBySEERC_SESERC_Last(
			segmentsEntryERC, segmentsEntryScopeERC, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findBySEERC_SESERC_PrevAndNext(
			long segmentsExperienceId, String segmentsEntryERC,
			String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findBySEERC_SESERC_PrevAndNext(
			segmentsExperienceId, segmentsEntryERC, segmentsEntryScopeERC,
			orderByComparator);
	}

	/**
	 * Removes all the segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; from the database.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 */
	public static void removeBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC) {

		getPersistence().removeBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC);
	}

	/**
	 * Returns the number of segments experiences where segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences
	 */
	public static int countBySEERC_SESERC(
		String segmentsEntryERC, String segmentsEntryScopeERC) {

		return getPersistence().countBySEERC_SESERC(
			segmentsEntryERC, segmentsEntryScopeERC);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		return getPersistence().findByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end) {

		return getPersistence().findByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, start, end,
			orderByComparator, useFinderCache);
	}

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
	public static SegmentsExperience findByG_SEERC_SESERC_First(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_First(
			groupId, segmentsEntryERC, segmentsEntryScopeERC,
			orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_SEERC_SESERC_First(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_SEERC_SESERC_First(
			groupId, segmentsEntryERC, segmentsEntryScopeERC,
			orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_SEERC_SESERC_Last(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_Last(
			groupId, segmentsEntryERC, segmentsEntryScopeERC,
			orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_SEERC_SESERC_Last(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_SEERC_SESERC_Last(
			groupId, segmentsEntryERC, segmentsEntryScopeERC,
			orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByG_SEERC_SESERC_PrevAndNext(
			long segmentsExperienceId, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_PrevAndNext(
			segmentsExperienceId, groupId, segmentsEntryERC,
			segmentsEntryScopeERC, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		return getPersistence().filterFindByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end) {

		return getPersistence().filterFindByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, start, end,
			orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] filterFindByG_SEERC_SESERC_PrevAndNext(
			long segmentsExperienceId, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByG_SEERC_SESERC_PrevAndNext(
			segmentsExperienceId, groupId, segmentsEntryERC,
			segmentsEntryScopeERC, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 */
	public static void removeByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		getPersistence().removeByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences
	 */
	public static int countByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		return getPersistence().countByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByG_SEERC_SESERC(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC) {

		return getPersistence().filterCountByG_SEERC_SESERC(
			groupId, segmentsEntryERC, segmentsEntryScopeERC);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_SEK_P(
			long groupId, String segmentsExperienceKey, long plid)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEK_P(
			groupId, segmentsExperienceKey, plid);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid) {

		return getPersistence().fetchByG_SEK_P(
			groupId, segmentsExperienceKey, plid);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid,
		boolean useFinderCache) {

		return getPersistence().fetchByG_SEK_P(
			groupId, segmentsExperienceKey, plid, useFinderCache);
	}

	/**
	 * Removes the segments experience where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the segments experience that was removed
	 */
	public static SegmentsExperience removeByG_SEK_P(
			long groupId, String segmentsExperienceKey, long plid)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().removeByG_SEK_P(
			groupId, segmentsExperienceKey, plid);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsExperienceKey = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceKey the segments experience key
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	public static int countByG_SEK_P(
		long groupId, String segmentsExperienceKey, long plid) {

		return getPersistence().countByG_SEK_P(
			groupId, segmentsExperienceKey, plid);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_P_P(
			long groupId, long plid, int priority)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_P(groupId, plid, priority);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_P(
		long groupId, long plid, int priority) {

		return getPersistence().fetchByG_P_P(groupId, plid, priority);
	}

	/**
	 * Returns the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_P(
		long groupId, long plid, int priority, boolean useFinderCache) {

		return getPersistence().fetchByG_P_P(
			groupId, plid, priority, useFinderCache);
	}

	/**
	 * Removes the segments experience where groupId = &#63; and plid = &#63; and priority = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the segments experience that was removed
	 */
	public static SegmentsExperience removeByG_P_P(
			long groupId, long plid, int priority)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().removeByG_P_P(groupId, plid, priority);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	public static int countByG_P_P(long groupId, long plid, int priority) {
		return getPersistence().countByG_P_P(groupId, plid, priority);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority) {

		return getPersistence().findByG_P_GtP(groupId, plid, priority);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end) {

		return getPersistence().findByG_P_GtP(
			groupId, plid, priority, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_P_GtP(
			groupId, plid, priority, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_GtP(
			groupId, plid, priority, start, end, orderByComparator,
			useFinderCache);
	}

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
	public static SegmentsExperience findByG_P_GtP_First(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_GtP_First(
			groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_GtP_First(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_P_GtP_First(
			groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_P_GtP_Last(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_GtP_Last(
			groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_GtP_Last(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_P_GtP_Last(
			groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByG_P_GtP_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_GtP_PrevAndNext(
			segmentsExperienceId, groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority) {

		return getPersistence().filterFindByG_P_GtP(groupId, plid, priority);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority, int start, int end) {

		return getPersistence().filterFindByG_P_GtP(
			groupId, plid, priority, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> filterFindByG_P_GtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_P_GtP(
			groupId, plid, priority, start, end, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] filterFindByG_P_GtP_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByG_P_GtP_PrevAndNext(
			segmentsExperienceId, groupId, plid, priority, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 */
	public static void removeByG_P_GtP(long groupId, long plid, int priority) {
		getPersistence().removeByG_P_GtP(groupId, plid, priority);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	public static int countByG_P_GtP(long groupId, long plid, int priority) {
		return getPersistence().countByG_P_GtP(groupId, plid, priority);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &gt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByG_P_GtP(
		long groupId, long plid, int priority) {

		return getPersistence().filterCountByG_P_GtP(groupId, plid, priority);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority) {

		return getPersistence().findByG_P_LtP(groupId, plid, priority);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end) {

		return getPersistence().findByG_P_LtP(
			groupId, plid, priority, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_P_LtP(
			groupId, plid, priority, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_LtP(
			groupId, plid, priority, start, end, orderByComparator,
			useFinderCache);
	}

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
	public static SegmentsExperience findByG_P_LtP_First(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_LtP_First(
			groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_LtP_First(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_P_LtP_First(
			groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_P_LtP_Last(
			long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_LtP_Last(
			groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_LtP_Last(
		long groupId, long plid, int priority,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_P_LtP_Last(
			groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByG_P_LtP_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_LtP_PrevAndNext(
			segmentsExperienceId, groupId, plid, priority, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority) {

		return getPersistence().filterFindByG_P_LtP(groupId, plid, priority);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority, int start, int end) {

		return getPersistence().filterFindByG_P_LtP(
			groupId, plid, priority, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> filterFindByG_P_LtP(
		long groupId, long plid, int priority, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_P_LtP(
			groupId, plid, priority, start, end, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] filterFindByG_P_LtP_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, int priority,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByG_P_LtP_PrevAndNext(
			segmentsExperienceId, groupId, plid, priority, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 */
	public static void removeByG_P_LtP(long groupId, long plid, int priority) {
		getPersistence().removeByG_P_LtP(groupId, plid, priority);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences
	 */
	public static int countByG_P_LtP(long groupId, long plid, int priority) {
		return getPersistence().countByG_P_LtP(groupId, plid, priority);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and priority &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param priority the priority
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByG_P_LtP(
		long groupId, long plid, int priority) {

		return getPersistence().filterCountByG_P_LtP(groupId, plid, priority);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active) {

		return getPersistence().findByG_P_A(groupId, plid, active);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end) {

		return getPersistence().findByG_P_A(groupId, plid, active, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_P_A(
			groupId, plid, active, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_A(
			groupId, plid, active, start, end, orderByComparator,
			useFinderCache);
	}

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
	public static SegmentsExperience findByG_P_A_First(
			long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_A_First(
			groupId, plid, active, orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_A_First(
		long groupId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_P_A_First(
			groupId, plid, active, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_P_A_Last(
			long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_A_Last(
			groupId, plid, active, orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_P_A_Last(
		long groupId, long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_P_A_Last(
			groupId, plid, active, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByG_P_A_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_P_A_PrevAndNext(
			segmentsExperienceId, groupId, plid, active, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active) {

		return getPersistence().filterFindByG_P_A(groupId, plid, active);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active, int start, int end) {

		return getPersistence().filterFindByG_P_A(
			groupId, plid, active, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> filterFindByG_P_A(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_P_A(
			groupId, plid, active, start, end, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] filterFindByG_P_A_PrevAndNext(
			long segmentsExperienceId, long groupId, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByG_P_A_PrevAndNext(
			segmentsExperienceId, groupId, plid, active, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and plid = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 */
	public static void removeByG_P_A(long groupId, long plid, boolean active) {
		getPersistence().removeByG_P_A(groupId, plid, active);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences
	 */
	public static int countByG_P_A(long groupId, long plid, boolean active) {
		return getPersistence().countByG_P_A(groupId, plid, active);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param active the active
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByG_P_A(
		long groupId, long plid, boolean active) {

		return getPersistence().filterCountByG_P_A(groupId, plid, active);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid) {

		return getPersistence().findByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, int start, int end) {

		return getPersistence().findByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_SEERC_SESERC_P_First(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_P_First(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid,
			orderByComparator);
	}

	/**
	 * Returns the first segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_SEERC_SESERC_P_First(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_SEERC_SESERC_P_First(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid,
			orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_SEERC_SESERC_P_Last(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_P_Last(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid,
			orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_SEERC_SESERC_P_Last(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_SEERC_SESERC_P_Last(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid,
			orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByG_SEERC_SESERC_P_PrevAndNext(
			long segmentsExperienceId, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_P_PrevAndNext(
			segmentsExperienceId, groupId, segmentsEntryERC,
			segmentsEntryScopeERC, plid, orderByComparator);
	}

	/**
	 * Returns all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @return the matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid) {

		return getPersistence().filterFindByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, int start, int end) {

		return getPersistence().filterFindByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching segments experiences that the user has permission to view
	 */
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, start, end,
			orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] filterFindByG_SEERC_SESERC_P_PrevAndNext(
			long segmentsExperienceId, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC, long plid,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByG_SEERC_SESERC_P_PrevAndNext(
			segmentsExperienceId, groupId, segmentsEntryERC,
			segmentsEntryScopeERC, plid, orderByComparator);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 */
	public static void removeByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid) {

		getPersistence().removeByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid);
	}

	/**
	 * Returns the number of segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @return the number of matching segments experiences
	 */
	public static int countByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid) {

		return getPersistence().countByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid);
	}

	/**
	 * Returns the number of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @return the number of matching segments experiences that the user has permission to view
	 */
	public static int filterCountByG_SEERC_SESERC_P(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid) {

		return getPersistence().filterCountByG_SEERC_SESERC_P(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid);
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return getPersistence().findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end) {

		return getPersistence().findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			start, end, orderByComparator, useFinderCache);
	}

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
	public static SegmentsExperience findByG_SEERC_SESERC_P_A_First(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_P_A_First(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			orderByComparator);
	}

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
	public static SegmentsExperience fetchByG_SEERC_SESERC_P_A_First(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_SEERC_SESERC_P_A_First(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByG_SEERC_SESERC_P_A_Last(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
			long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_P_A_Last(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			orderByComparator);
	}

	/**
	 * Returns the last segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByG_SEERC_SESERC_P_A_Last(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().fetchByG_SEERC_SESERC_P_A_Last(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[] findByG_SEERC_SESERC_P_A_PrevAndNext(
			long segmentsExperienceId, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC, long plid, boolean active,
			OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByG_SEERC_SESERC_P_A_PrevAndNext(
			segmentsExperienceId, groupId, segmentsEntryERC,
			segmentsEntryScopeERC, plid, active, orderByComparator);
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
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return getPersistence().filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end) {

		return getPersistence().filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permissions to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active,
			start, end, orderByComparator);
	}

	/**
	 * Returns the segments experiences before and after the current segments experience in the ordered set of segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * @param segmentsExperienceId the primary key of the current segments experience
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience[]
			filterFindByG_SEERC_SESERC_P_A_PrevAndNext(
				long segmentsExperienceId, long groupId,
				String segmentsEntryERC, String segmentsEntryScopeERC,
				long plid, boolean active,
				OrderByComparator<SegmentsExperience> orderByComparator)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().filterFindByG_SEERC_SESERC_P_A_PrevAndNext(
			segmentsExperienceId, groupId, segmentsEntryERC,
			segmentsEntryScopeERC, plid, active, orderByComparator);
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
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return getPersistence().filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active);
	}

	/**
	 * Returns a range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end) {

		return getPersistence().filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences that the user has permission to view where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> filterFindByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().filterFindByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			start, end, orderByComparator);
	}

	/**
	 * Returns all the segments experiences where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERCs the segments entry ercs
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 * @return the matching segments experiences
	 */
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return getPersistence().findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active);
	}

	/**
	 * Returns a range of all the segments experiences where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end) {

		return getPersistence().findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = any &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
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
	public static List<SegmentsExperience> findByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active,
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the segments experiences where groupId = &#63; and segmentsEntryERC = &#63; and segmentsEntryScopeERC = &#63; and plid = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsEntryERC the segments entry erc
	 * @param segmentsEntryScopeERC the segments entry scope erc
	 * @param plid the plid
	 * @param active the active
	 */
	public static void removeByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		getPersistence().removeByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active);
	}

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
	public static int countByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return getPersistence().countByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active);
	}

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
	public static int countByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return getPersistence().countByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active);
	}

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
	public static int filterCountByG_SEERC_SESERC_P_A(
		long groupId, String segmentsEntryERC, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return getPersistence().filterCountByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERC, segmentsEntryScopeERC, plid, active);
	}

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
	public static int filterCountByG_SEERC_SESERC_P_A(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active) {

		return getPersistence().filterCountByG_SEERC_SESERC_P_A(
			groupId, segmentsEntryERCs, segmentsEntryScopeERC, plid, active);
	}

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching segments experience
	 * @throws NoSuchExperienceException if a matching segments experience could not be found
	 */
	public static SegmentsExperience findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the segments experience where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	public static SegmentsExperience fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the segments experience where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the segments experience that was removed
	 */
	public static SegmentsExperience removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of segments experiences where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching segments experiences
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Caches the segments experience in the entity cache if it is enabled.
	 *
	 * @param segmentsExperience the segments experience
	 */
	public static void cacheResult(SegmentsExperience segmentsExperience) {
		getPersistence().cacheResult(segmentsExperience);
	}

	/**
	 * Caches the segments experiences in the entity cache if it is enabled.
	 *
	 * @param segmentsExperiences the segments experiences
	 */
	public static void cacheResult(
		List<SegmentsExperience> segmentsExperiences) {

		getPersistence().cacheResult(segmentsExperiences);
	}

	/**
	 * Creates a new segments experience with the primary key. Does not add the segments experience to the database.
	 *
	 * @param segmentsExperienceId the primary key for the new segments experience
	 * @return the new segments experience
	 */
	public static SegmentsExperience create(long segmentsExperienceId) {
		return getPersistence().create(segmentsExperienceId);
	}

	/**
	 * Removes the segments experience with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience that was removed
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience remove(long segmentsExperienceId)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().remove(segmentsExperienceId);
	}

	public static SegmentsExperience updateImpl(
		SegmentsExperience segmentsExperience) {

		return getPersistence().updateImpl(segmentsExperience);
	}

	/**
	 * Returns the segments experience with the primary key or throws a <code>NoSuchExperienceException</code> if it could not be found.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience
	 * @throws NoSuchExperienceException if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience findByPrimaryKey(long segmentsExperienceId)
		throws com.liferay.segments.exception.NoSuchExperienceException {

		return getPersistence().findByPrimaryKey(segmentsExperienceId);
	}

	/**
	 * Returns the segments experience with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience, or <code>null</code> if a segments experience with the primary key could not be found
	 */
	public static SegmentsExperience fetchByPrimaryKey(
		long segmentsExperienceId) {

		return getPersistence().fetchByPrimaryKey(segmentsExperienceId);
	}

	/**
	 * Returns all the segments experiences.
	 *
	 * @return the segments experiences
	 */
	public static List<SegmentsExperience> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the segments experiences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of segments experiences
	 */
	public static List<SegmentsExperience> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the segments experiences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of segments experiences
	 */
	public static List<SegmentsExperience> findAll(
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the segments experiences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of segments experiences
	 */
	public static List<SegmentsExperience> findAll(
		int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the segments experiences from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of segments experiences.
	 *
	 * @return the number of segments experiences
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static SegmentsExperiencePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		SegmentsExperiencePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile SegmentsExperiencePersistence _persistence;

}