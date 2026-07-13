/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout page template structure rel element variation service. This utility wraps <code>com.liferay.layout.page.template.service.persistence.impl.LayoutPageTemplateStructureRelElementVariationPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationPersistence
 * @generated
 */
public class LayoutPageTemplateStructureRelElementVariationUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations) {

		getPersistence().cacheResult(
			layoutPageTemplateStructureRelElementVariations);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		getPersistence().cacheResult(
			layoutPageTemplateStructureRelElementVariation);
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
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		getPersistence().clearCache(
			layoutPageTemplateStructureRelElementVariation);
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
	public static Map
		<Serializable, LayoutPageTemplateStructureRelElementVariation>
			fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findWithDynamicQuery(DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findWithDynamicQuery(DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findWithDynamicQuery(
			DynamicQuery dynamicQuery, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutPageTemplateStructureRelElementVariation update(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		return getPersistence().update(
			layoutPageTemplateStructureRelElementVariation);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutPageTemplateStructureRelElementVariation update(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation,
		ServiceContext serviceContext) {

		return getPersistence().update(
			layoutPageTemplateStructureRelElementVariation, serviceContext);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid(
			String uuid, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			findByUuid_First(
				String uuid,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		fetchByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structure rel element variations
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation that was removed
	 */
	public static LayoutPageTemplateStructureRelElementVariation removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid_C(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			findByUuid_C_First(
				String uuid, long companyId,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		fetchByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByPlid(
			long plid, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByPlid(
			plid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			findByPlid_First(
				long plid,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findByPlid_First(plid, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		fetchByPlid_First(
			long plid,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().fetchByPlid_First(plid, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public static void removeByPlid(long plid) {
		getPersistence().removeByPlid(plid);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout page template structure rel element variations
	 */
	public static int countByPlid(long plid) {
		return getPersistence().countByPlid(plid);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findBySegmentsExperienceERC(
			String segmentsExperienceERC, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findBySegmentsExperienceERC(
			segmentsExperienceERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			findBySegmentsExperienceERC_First(
				String segmentsExperienceERC,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findBySegmentsExperienceERC_First(
			segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		fetchBySegmentsExperienceERC_First(
			String segmentsExperienceERC,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().fetchBySegmentsExperienceERC_First(
			segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where segmentsExperienceERC = &#63; from the database.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public static void removeBySegmentsExperienceERC(
		String segmentsExperienceERC) {

		getPersistence().removeBySegmentsExperienceERC(segmentsExperienceERC);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout page template structure rel element variations
	 */
	public static int countBySegmentsExperienceERC(
		String segmentsExperienceERC) {

		return getPersistence().countBySegmentsExperienceERC(
			segmentsExperienceERC);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByP_SEERC(
			long plid, String segmentsExperienceERC, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByP_SEERC(
			plid, segmentsExperienceERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			findByP_SEERC_First(
				long plid, String segmentsExperienceERC,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findByP_SEERC_First(
			plid, segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		fetchByP_SEERC_First(
			long plid, String segmentsExperienceERC,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().fetchByP_SEERC_First(
			plid, segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public static void removeByP_SEERC(
		long plid, String segmentsExperienceERC) {

		getPersistence().removeByP_SEERC(plid, segmentsExperienceERC);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout page template structure rel element variations
	 */
	public static int countByP_SEERC(long plid, String segmentsExperienceERC) {
		return getPersistence().countByP_SEERC(plid, segmentsExperienceERC);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByA_P_SEERC(
			boolean active, long plid, String segmentsExperienceERC, int start,
			int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByA_P_SEERC(
			active, plid, segmentsExperienceERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			findByA_P_SEERC_First(
				boolean active, long plid, String segmentsExperienceERC,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findByA_P_SEERC_First(
			active, plid, segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		fetchByA_P_SEERC_First(
			boolean active, long plid, String segmentsExperienceERC,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().fetchByA_P_SEERC_First(
			active, plid, segmentsExperienceERC, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 */
	public static void removeByA_P_SEERC(
		boolean active, long plid, String segmentsExperienceERC) {

		getPersistence().removeByA_P_SEERC(active, plid, segmentsExperienceERC);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout page template structure rel element variations
	 */
	public static int countByA_P_SEERC(
		boolean active, long plid, String segmentsExperienceERC) {

		return getPersistence().countByA_P_SEERC(
			active, plid, segmentsExperienceERC);
	}

	/**
	 * Returns the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation that was removed
	 */
	public static LayoutPageTemplateStructureRelElementVariation removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new layout page template structure rel element variation with the primary key. Does not add the layout page template structure rel element variation to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key for the new layout page template structure rel element variation
	 * @return the new layout page template structure rel element variation
	 */
	public static LayoutPageTemplateStructureRelElementVariation create(
		long layoutPageTemplateStructureRelElementVariationId) {

		return getPersistence().create(
			layoutPageTemplateStructureRelElementVariationId);
	}

	/**
	 * Removes the layout page template structure rel element variation with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation that was removed
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a layout page template structure rel element variation with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation remove(
			long layoutPageTemplateStructureRelElementVariationId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().remove(
			layoutPageTemplateStructureRelElementVariationId);
	}

	public static LayoutPageTemplateStructureRelElementVariation updateImpl(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		return getPersistence().updateImpl(
			layoutPageTemplateStructureRelElementVariation);
	}

	/**
	 * Returns the layout page template structure rel element variation with the primary key or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a layout page template structure rel element variation with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
			findByPrimaryKey(
				long layoutPageTemplateStructureRelElementVariationId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationException {

		return getPersistence().findByPrimaryKey(
			layoutPageTemplateStructureRelElementVariationId);
	}

	/**
	 * Returns the layout page template structure rel element variation with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation, or <code>null</code> if a layout page template structure rel element variation with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation
		fetchByPrimaryKey(
			long layoutPageTemplateStructureRelElementVariationId) {

		return getPersistence().fetchByPrimaryKey(
			layoutPageTemplateStructureRelElementVariationId);
	}

	/**
	 * Returns the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariation fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid(String uuid) {

		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid(String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid(
			String uuid, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid_C(String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid_C(String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByUuid_C(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByPlid(long plid) {

		return getPersistence().findByPlid(plid);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByPlid(long plid, int start, int end) {

		return getPersistence().findByPlid(plid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByPlid(
			long plid, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().findByPlid(plid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findBySegmentsExperienceERC(String segmentsExperienceERC) {

		return getPersistence().findBySegmentsExperienceERC(
			segmentsExperienceERC);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findBySegmentsExperienceERC(
			String segmentsExperienceERC, int start, int end) {

		return getPersistence().findBySegmentsExperienceERC(
			segmentsExperienceERC, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findBySegmentsExperienceERC(
			String segmentsExperienceERC, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().findBySegmentsExperienceERC(
			segmentsExperienceERC, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByP_SEERC(long plid, String segmentsExperienceERC) {

		return getPersistence().findByP_SEERC(plid, segmentsExperienceERC);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByP_SEERC(
			long plid, String segmentsExperienceERC, int start, int end) {

		return getPersistence().findByP_SEERC(
			plid, segmentsExperienceERC, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByP_SEERC(
			long plid, String segmentsExperienceERC, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().findByP_SEERC(
			plid, segmentsExperienceERC, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByA_P_SEERC(
			boolean active, long plid, String segmentsExperienceERC) {

		return getPersistence().findByA_P_SEERC(
			active, plid, segmentsExperienceERC);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @return the range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByA_P_SEERC(
			boolean active, long plid, String segmentsExperienceERC, int start,
			int end) {

		return getPersistence().findByA_P_SEERC(
			active, plid, segmentsExperienceERC, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where active = &#63; and plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	public static List<LayoutPageTemplateStructureRelElementVariation>
		findByA_P_SEERC(
			boolean active, long plid, String segmentsExperienceERC, int start,
			int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return getPersistence().findByA_P_SEERC(
			active, plid, segmentsExperienceERC, start, end, orderByComparator);
	}

	public static LayoutPageTemplateStructureRelElementVariationPersistence
		getPersistence() {

		return _persistence;
	}

	public static void setPersistence(
		LayoutPageTemplateStructureRelElementVariationPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile
		LayoutPageTemplateStructureRelElementVariationPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1878517135