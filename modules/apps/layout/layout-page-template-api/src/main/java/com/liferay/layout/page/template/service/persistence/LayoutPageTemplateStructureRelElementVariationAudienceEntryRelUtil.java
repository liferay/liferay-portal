/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout page template structure rel element variation audience entry rel service. This utility wraps <code>com.liferay.layout.page.template.service.persistence.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence
 * @generated
 */
public class
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels) {

		getPersistence().cacheResult(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRels);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		getPersistence().cacheResult(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
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
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		getPersistence().clearCache(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
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
		<Serializable,
		 LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findWithDynamicQuery(DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findWithDynamicQuery(
				DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findWithDynamicQuery(
				DynamicQuery dynamicQuery, int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		update(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		return getPersistence().update(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		update(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			ServiceContext serviceContext) {

		return getPersistence().update(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			serviceContext);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid(
				String uuid, int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator,
				boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUuid_First(
				String uuid,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUuid_First(
			String uuid,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUUID_G(String uuid, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUUID_G(String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			removeByUUID_G(String uuid, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid_C(
				String uuid, long companyId, int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator,
				boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUuid_C_First(
				String uuid, long companyId,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByLayoutPageTemplateStructureRelElementVariationERC(
				String layoutPageTemplateStructureRelElementVariationERC,
				int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator,
				boolean useFinderCache) {

		return getPersistence().
			findByLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC, start, end,
				orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByLayoutPageTemplateStructureRelElementVariationERC_First(
				String layoutPageTemplateStructureRelElementVariationERC,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().
			findByLayoutPageTemplateStructureRelElementVariationERC_First(
				layoutPageTemplateStructureRelElementVariationERC,
				orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByLayoutPageTemplateStructureRelElementVariationERC_First(
			String layoutPageTemplateStructureRelElementVariationERC,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator) {

		return getPersistence().
			fetchByLayoutPageTemplateStructureRelElementVariationERC_First(
				layoutPageTemplateStructureRelElementVariationERC,
				orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63; from the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 */
	public static void
		removeByLayoutPageTemplateStructureRelElementVariationERC(
			String layoutPageTemplateStructureRelElementVariationERC) {

		getPersistence().
			removeByLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public static int countByLayoutPageTemplateStructureRelElementVariationERC(
		String layoutPageTemplateStructureRelElementVariationERC) {

		return getPersistence().
			countByLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByC_AEERC(
				long companyId, String audienceEntryERC, int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator,
				boolean useFinderCache) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByC_AEERC_First(
				long companyId, String audienceEntryERC,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().findByC_AEERC_First(
			companyId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByC_AEERC_First(
			long companyId, String audienceEntryERC,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator) {

		return getPersistence().fetchByC_AEERC_First(
			companyId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public static void removeByC_AEERC(
		long companyId, String audienceEntryERC) {

		getPersistence().removeByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public static int countByC_AEERC(long companyId, String audienceEntryERC) {
		return getPersistence().countByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByERC_G(String externalReferenceCode, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByERC_G(
			String externalReferenceCode, long groupId,
			boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			removeByERC_G(String externalReferenceCode, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new layout page template structure rel element variation audience entry rel with the primary key. Does not add the layout page template structure rel element variation audience entry rel to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key for the new layout page template structure rel element variation audience entry rel
	 * @return the new layout page template structure rel element variation audience entry rel
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		create(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId) {

		return getPersistence().create(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	/**
	 * Removes the layout page template structure rel element variation audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			remove(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().remove(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		updateImpl(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		return getPersistence().updateImpl(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel with the primary key or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByPrimaryKey(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return getPersistence().findByPrimaryKey(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel, or <code>null</code> if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByPrimaryKey(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId) {

		return getPersistence().fetchByPrimaryKey(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUUID_G(String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	public static LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByERC_G(String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid(String uuid) {

		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid(String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid(
				String uuid, int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid_C(String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid_C(String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByUuid_C(
				String uuid, long companyId, int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @return the matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByLayoutPageTemplateStructureRelElementVariationERC(
				String layoutPageTemplateStructureRelElementVariationERC) {

		return getPersistence().
			findByLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByLayoutPageTemplateStructureRelElementVariationERC(
				String layoutPageTemplateStructureRelElementVariationERC,
				int start, int end) {

		return getPersistence().
			findByLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByLayoutPageTemplateStructureRelElementVariationERC(
				String layoutPageTemplateStructureRelElementVariationERC,
				int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return getPersistence().
			findByLayoutPageTemplateStructureRelElementVariationERC(
				layoutPageTemplateStructureRelElementVariationERC, start, end,
				orderByComparator);
	}

	/**
	 * Returns all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByC_AEERC(long companyId, String audienceEntryERC) {

		return getPersistence().findByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns a range of all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @return the range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByC_AEERC(
				long companyId, String audienceEntryERC, int start, int end) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	public static List
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
			findByC_AEERC(
				long companyId, String audienceEntryERC, int start, int end,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator);
	}

	public static
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence
			getPersistence() {

		return _persistence;
	}

	public static void setPersistence(
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence
			persistence) {

		_persistence = persistence;
	}

	private static volatile
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence
			_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-540601816