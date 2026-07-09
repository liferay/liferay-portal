/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.style.book.model.StyleBookEntryVersion;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the style book entry version service. This utility wraps <code>com.liferay.style.book.service.persistence.impl.StyleBookEntryVersionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryVersionPersistence
 * @generated
 */
public class StyleBookEntryVersionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<StyleBookEntryVersion> styleBookEntryVersions) {

		getPersistence().cacheResult(styleBookEntryVersions);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		StyleBookEntryVersion styleBookEntryVersion) {

		getPersistence().cacheResult(styleBookEntryVersion);
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
	public static void clearCache(StyleBookEntryVersion styleBookEntryVersion) {
		getPersistence().clearCache(styleBookEntryVersion);
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
	public static Map<Serializable, StyleBookEntryVersion> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<StyleBookEntryVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<StyleBookEntryVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<StyleBookEntryVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static StyleBookEntryVersion update(
		StyleBookEntryVersion styleBookEntryVersion) {

		return getPersistence().update(styleBookEntryVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static StyleBookEntryVersion update(
		StyleBookEntryVersion styleBookEntryVersion,
		ServiceContext serviceContext) {

		return getPersistence().update(styleBookEntryVersion, serviceContext);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByStyleBookEntryId(
			styleBookEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByStyleBookEntryId_First(
			long styleBookEntryId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByStyleBookEntryId_First(
			styleBookEntryId, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByStyleBookEntryId_First(
		long styleBookEntryId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByStyleBookEntryId_First(
			styleBookEntryId, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where styleBookEntryId = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 */
	public static void removeByStyleBookEntryId(long styleBookEntryId) {
		getPersistence().removeByStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Returns the number of style book entry versions where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the number of matching style book entry versions
	 */
	public static int countByStyleBookEntryId(long styleBookEntryId) {
		return getPersistence().countByStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Returns the style book entry version where styleBookEntryId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByStyleBookEntryId_Version(
			long styleBookEntryId, int version)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByStyleBookEntryId_Version(
			styleBookEntryId, version);
	}

	/**
	 * Returns the style book entry version where styleBookEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByStyleBookEntryId_Version(
		long styleBookEntryId, int version, boolean useFinderCache) {

		return getPersistence().fetchByStyleBookEntryId_Version(
			styleBookEntryId, version, useFinderCache);
	}

	/**
	 * Removes the style book entry version where styleBookEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	public static StyleBookEntryVersion removeByStyleBookEntryId_Version(
			long styleBookEntryId, int version)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().removeByStyleBookEntryId_Version(
			styleBookEntryId, version);
	}

	/**
	 * Returns the number of style book entry versions where styleBookEntryId = &#63; and version = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByStyleBookEntryId_Version(
		long styleBookEntryId, int version) {

		return getPersistence().countByStyleBookEntryId_Version(
			styleBookEntryId, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByUuid_First(
			String uuid,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByUuid_First(
		String uuid,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of style book entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching style book entry versions
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_Version(
			uuid, version, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByUuid_Version_First(
			String uuid, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByUuid_Version_First(
			uuid, version, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByUuid_Version_First(
		String uuid, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByUuid_Version_First(
			uuid, version, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 */
	public static void removeByUuid_Version(String uuid, int version) {
		getPersistence().removeByUuid_Version(uuid, version);
	}

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByUuid_Version(String uuid, int version) {
		return getPersistence().countByUuid_Version(uuid, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUUID_G(
			uuid, groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByUUID_G_First(
			String uuid, long groupId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByUUID_G_First(
			uuid, groupId, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByUUID_G_First(
		String uuid, long groupId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByUUID_G_First(
			uuid, groupId, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 */
	public static void removeByUUID_G(String uuid, long groupId) {
		getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching style book entry versions
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByUUID_G_Version(
			String uuid, long groupId, int version)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByUUID_G_Version(uuid, groupId, version);
	}

	/**
	 * Returns the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G_Version(
			uuid, groupId, version, useFinderCache);
	}

	/**
	 * Removes the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	public static StyleBookEntryVersion removeByUUID_G_Version(
			String uuid, long groupId, int version)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().removeByUUID_G_Version(uuid, groupId, version);
	}

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByUUID_G_Version(
		String uuid, long groupId, int version) {

		return getPersistence().countByUUID_G_Version(uuid, groupId, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching style book entry versions
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C_Version(
			uuid, companyId, version, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByUuid_C_Version_First(
			String uuid, long companyId, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByUuid_C_Version_First(
			uuid, companyId, version, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByUuid_C_Version_First(
		String uuid, long companyId, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByUuid_C_Version_First(
			uuid, companyId, version, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 */
	public static void removeByUuid_C_Version(
		String uuid, long companyId, int version) {

		getPersistence().removeByUuid_C_Version(uuid, companyId, version);
	}

	/**
	 * Returns the number of style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByUuid_C_Version(
		String uuid, long companyId, int version) {

		return getPersistence().countByUuid_C_Version(uuid, companyId, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByGroupId_First(
			long groupId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByGroupId_First(
		long groupId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching style book entry versions
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId_Version(
			groupId, version, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByGroupId_Version_First(
			long groupId, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByGroupId_Version_First(
			groupId, version, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByGroupId_Version_First(
		long groupId, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByGroupId_Version_First(
			groupId, version, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 */
	public static void removeByGroupId_Version(long groupId, int version) {
		getPersistence().removeByGroupId_Version(groupId, version);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByGroupId_Version(long groupId, int version) {
		return getPersistence().countByGroupId_Version(groupId, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_D(
			groupId, defaultStyleBookEntry, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_D_First(
			long groupId, boolean defaultStyleBookEntry,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_D_First(
			groupId, defaultStyleBookEntry, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_D_First(
		long groupId, boolean defaultStyleBookEntry,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_D_First(
			groupId, defaultStyleBookEntry, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 */
	public static void removeByG_D(
		long groupId, boolean defaultStyleBookEntry) {

		getPersistence().removeByG_D(groupId, defaultStyleBookEntry);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_D(long groupId, boolean defaultStyleBookEntry) {
		return getPersistence().countByG_D(groupId, defaultStyleBookEntry);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_D_Version(
			groupId, defaultStyleBookEntry, version, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_D_Version_First(
			long groupId, boolean defaultStyleBookEntry, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_D_Version_First(
			groupId, defaultStyleBookEntry, version, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_D_Version_First(
		long groupId, boolean defaultStyleBookEntry, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_D_Version_First(
			groupId, defaultStyleBookEntry, version, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 */
	public static void removeByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version) {

		getPersistence().removeByG_D_Version(
			groupId, defaultStyleBookEntry, version);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version) {

		return getPersistence().countByG_D_Version(
			groupId, defaultStyleBookEntry, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_N(
			groupId, name, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_N_First(
			long groupId, String name,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_N_First(
			groupId, name, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_N_First(
		long groupId, String name,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_N_First(
			groupId, name, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	public static void removeByG_N(long groupId, String name) {
		getPersistence().removeByG_N(groupId, name);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_N(long groupId, String name) {
		return getPersistence().countByG_N(groupId, name);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_N_Version(
			groupId, name, version, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_N_Version_First(
			long groupId, String name, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_N_Version_First(
			groupId, name, version, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_N_Version_First(
		long groupId, String name, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_N_Version_First(
			groupId, name, version, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 */
	public static void removeByG_N_Version(
		long groupId, String name, int version) {

		getPersistence().removeByG_N_Version(groupId, name, version);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_N_Version(
		long groupId, String name, int version) {

		return getPersistence().countByG_N_Version(groupId, name, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeN(
			groupId, name, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_LikeN_First(
			long groupId, String name,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_LikeN_First(
			groupId, name, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_LikeN_First(
		long groupId, String name,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_LikeN_First(
			groupId, name, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	public static void removeByG_LikeN(long groupId, String name) {
		getPersistence().removeByG_LikeN(groupId, name);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_LikeN(long groupId, String name) {
		return getPersistence().countByG_LikeN(groupId, name);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeN_Version(
			groupId, name, version, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_LikeN_Version_First(
			long groupId, String name, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_LikeN_Version_First(
			groupId, name, version, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_LikeN_Version_First(
		long groupId, String name, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_LikeN_Version_First(
			groupId, name, version, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 */
	public static void removeByG_LikeN_Version(
		long groupId, String name, int version) {

		getPersistence().removeByG_LikeN_Version(groupId, name, version);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_LikeN_Version(
		long groupId, String name, int version) {

		return getPersistence().countByG_LikeN_Version(groupId, name, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_SBEK(
			groupId, styleBookEntryKey, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_SBEK_First(
			long groupId, String styleBookEntryKey,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_SBEK_First(
			groupId, styleBookEntryKey, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_SBEK_First(
		long groupId, String styleBookEntryKey,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_SBEK_First(
			groupId, styleBookEntryKey, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 */
	public static void removeByG_SBEK(long groupId, String styleBookEntryKey) {
		getPersistence().removeByG_SBEK(groupId, styleBookEntryKey);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_SBEK(long groupId, String styleBookEntryKey) {
		return getPersistence().countByG_SBEK(groupId, styleBookEntryKey);
	}

	/**
	 * Returns the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_SBEK_Version(
			long groupId, String styleBookEntryKey, int version)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_SBEK_Version(
			groupId, styleBookEntryKey, version);
	}

	/**
	 * Returns the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_SBEK_Version(
		long groupId, String styleBookEntryKey, int version,
		boolean useFinderCache) {

		return getPersistence().fetchByG_SBEK_Version(
			groupId, styleBookEntryKey, version, useFinderCache);
	}

	/**
	 * Removes the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the style book entry version that was removed
	 */
	public static StyleBookEntryVersion removeByG_SBEK_Version(
			long groupId, String styleBookEntryKey, int version)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().removeByG_SBEK_Version(
			groupId, styleBookEntryKey, version);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_SBEK_Version(
		long groupId, String styleBookEntryKey, int version) {

		return getPersistence().countByG_SBEK_Version(
			groupId, styleBookEntryKey, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T(
			groupId, themeId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_T_First(
			long groupId, String themeId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_T_First(
			groupId, themeId, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_T_First(
		long groupId, String themeId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_T_First(
			groupId, themeId, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 */
	public static void removeByG_T(long groupId, String themeId) {
		getPersistence().removeByG_T(groupId, themeId);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_T(long groupId, String themeId) {
		return getPersistence().countByG_T(groupId, themeId);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T_Version(
			groupId, themeId, version, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_T_Version_First(
			long groupId, String themeId, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_T_Version_First(
			groupId, themeId, version, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_T_Version_First(
		long groupId, String themeId, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_T_Version_First(
			groupId, themeId, version, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 */
	public static void removeByG_T_Version(
		long groupId, String themeId, int version) {

		getPersistence().removeByG_T_Version(groupId, themeId, version);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_T_Version(
		long groupId, String themeId, int version) {

		return getPersistence().countByG_T_Version(groupId, themeId, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_D_T(
			groupId, defaultStyleBookEntry, themeId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_D_T_First(
			long groupId, boolean defaultStyleBookEntry, String themeId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_D_T_First(
			groupId, defaultStyleBookEntry, themeId, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_D_T_First(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_D_T_First(
			groupId, defaultStyleBookEntry, themeId, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 */
	public static void removeByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId) {

		getPersistence().removeByG_D_T(groupId, defaultStyleBookEntry, themeId);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId) {

		return getPersistence().countByG_D_T(
			groupId, defaultStyleBookEntry, themeId);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_D_T_Version_First(
			long groupId, boolean defaultStyleBookEntry, String themeId,
			int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_D_T_Version_First(
			groupId, defaultStyleBookEntry, themeId, version,
			orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_D_T_Version_First(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_D_T_Version_First(
			groupId, defaultStyleBookEntry, themeId, version,
			orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 */
	public static void removeByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version) {

		getPersistence().removeByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version) {

		return getPersistence().countByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeN_T(
			groupId, name, themeId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_LikeN_T_First(
			long groupId, String name, String themeId,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_LikeN_T_First(
			groupId, name, themeId, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_LikeN_T_First(
		long groupId, String name, String themeId,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_LikeN_T_First(
			groupId, name, themeId, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 */
	public static void removeByG_LikeN_T(
		long groupId, String name, String themeId) {

		getPersistence().removeByG_LikeN_T(groupId, name, themeId);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_LikeN_T(
		long groupId, String name, String themeId) {

		return getPersistence().countByG_LikeN_T(groupId, name, themeId);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeN_T_Version(
			groupId, name, themeId, version, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version
	 * @throws NoSuchEntryVersionException if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion findByG_LikeN_T_Version_First(
			long groupId, String name, String themeId, int version,
			OrderByComparator<StyleBookEntryVersion> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByG_LikeN_T_Version_First(
			groupId, name, themeId, version, orderByComparator);
	}

	/**
	 * Returns the first style book entry version in the ordered set where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_LikeN_T_Version_First(
		long groupId, String name, String themeId, int version,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().fetchByG_LikeN_T_Version_First(
			groupId, name, themeId, version, orderByComparator);
	}

	/**
	 * Removes all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 */
	public static void removeByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version) {

		getPersistence().removeByG_LikeN_T_Version(
			groupId, name, themeId, version);
	}

	/**
	 * Returns the number of style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the number of matching style book entry versions
	 */
	public static int countByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version) {

		return getPersistence().countByG_LikeN_T_Version(
			groupId, name, themeId, version);
	}

	/**
	 * Creates a new style book entry version with the primary key. Does not add the style book entry version to the database.
	 *
	 * @param styleBookEntryVersionId the primary key for the new style book entry version
	 * @return the new style book entry version
	 */
	public static StyleBookEntryVersion create(long styleBookEntryVersionId) {
		return getPersistence().create(styleBookEntryVersionId);
	}

	/**
	 * Removes the style book entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version that was removed
	 * @throws NoSuchEntryVersionException if a style book entry version with the primary key could not be found
	 */
	public static StyleBookEntryVersion remove(long styleBookEntryVersionId)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().remove(styleBookEntryVersionId);
	}

	public static StyleBookEntryVersion updateImpl(
		StyleBookEntryVersion styleBookEntryVersion) {

		return getPersistence().updateImpl(styleBookEntryVersion);
	}

	/**
	 * Returns the style book entry version with the primary key or throws a <code>NoSuchEntryVersionException</code> if it could not be found.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version
	 * @throws NoSuchEntryVersionException if a style book entry version with the primary key could not be found
	 */
	public static StyleBookEntryVersion findByPrimaryKey(
			long styleBookEntryVersionId)
		throws com.liferay.style.book.exception.NoSuchEntryVersionException {

		return getPersistence().findByPrimaryKey(styleBookEntryVersionId);
	}

	/**
	 * Returns the style book entry version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param styleBookEntryVersionId the primary key of the style book entry version
	 * @return the style book entry version, or <code>null</code> if a style book entry version with the primary key could not be found
	 */
	public static StyleBookEntryVersion fetchByPrimaryKey(
		long styleBookEntryVersionId) {

		return getPersistence().fetchByPrimaryKey(styleBookEntryVersionId);
	}

	/**
	 * Returns the style book entry version where styleBookEntryId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param version the version
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByStyleBookEntryId_Version(
		long styleBookEntryId, int version) {

		return getPersistence().fetchByStyleBookEntryId_Version(
			styleBookEntryId, version);
	}

	/**
	 * Returns the style book entry version where uuid = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByUUID_G_Version(
		String uuid, long groupId, int version) {

		return getPersistence().fetchByUUID_G_Version(uuid, groupId, version);
	}

	/**
	 * Returns the style book entry version where groupId = &#63; and styleBookEntryKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param version the version
	 * @return the matching style book entry version, or <code>null</code> if a matching style book entry version could not be found
	 */
	public static StyleBookEntryVersion fetchByG_SBEK_Version(
		long groupId, String styleBookEntryKey, int version) {

		return getPersistence().fetchByG_SBEK_Version(
			groupId, styleBookEntryKey, version);
	}

	/**
	 * Returns all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId) {

		return getPersistence().findByStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Returns a range of all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end) {

		return getPersistence().findByStyleBookEntryId(
			styleBookEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByStyleBookEntryId(
			styleBookEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version) {

		return getPersistence().findByUuid_Version(uuid, version);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end) {

		return getPersistence().findByUuid_Version(uuid, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_Version(
		String uuid, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByUuid_Version(
			uuid, version, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId) {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end) {

		return getPersistence().findByUUID_G(uuid, groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByUUID_G(
			uuid, groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version) {

		return getPersistence().findByUuid_C_Version(uuid, companyId, version);
	}

	/**
	 * Returns a range of all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end) {

		return getPersistence().findByUuid_C_Version(
			uuid, companyId, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where uuid = &#63; and companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByUuid_C_Version(
		String uuid, long companyId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByUuid_C_Version(
			uuid, companyId, version, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version) {

		return getPersistence().findByGroupId_Version(groupId, version);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end) {

		return getPersistence().findByGroupId_Version(
			groupId, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByGroupId_Version(
		long groupId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByGroupId_Version(
			groupId, version, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry) {

		return getPersistence().findByG_D(groupId, defaultStyleBookEntry);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry, int start, int end) {

		return getPersistence().findByG_D(
			groupId, defaultStyleBookEntry, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D(
		long groupId, boolean defaultStyleBookEntry, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_D(
			groupId, defaultStyleBookEntry, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version) {

		return getPersistence().findByG_D_Version(
			groupId, defaultStyleBookEntry, version);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version, int start,
		int end) {

		return getPersistence().findByG_D_Version(
			groupId, defaultStyleBookEntry, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_Version(
		long groupId, boolean defaultStyleBookEntry, int version, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_D_Version(
			groupId, defaultStyleBookEntry, version, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_N(
		long groupId, String name) {

		return getPersistence().findByG_N(groupId, name);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_N(
		long groupId, String name, int start, int end) {

		return getPersistence().findByG_N(groupId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_N(
			groupId, name, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version) {

		return getPersistence().findByG_N_Version(groupId, name, version);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version, int start, int end) {

		return getPersistence().findByG_N_Version(
			groupId, name, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_N_Version(
		long groupId, String name, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_N_Version(
			groupId, name, version, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name) {

		return getPersistence().findByG_LikeN(groupId, name);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name, int start, int end) {

		return getPersistence().findByG_LikeN(groupId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_LikeN(
			groupId, name, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version) {

		return getPersistence().findByG_LikeN_Version(groupId, name, version);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version, int start, int end) {

		return getPersistence().findByG_LikeN_Version(
			groupId, name, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_Version(
		long groupId, String name, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_LikeN_Version(
			groupId, name, version, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey) {

		return getPersistence().findByG_SBEK(groupId, styleBookEntryKey);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey, int start, int end) {

		return getPersistence().findByG_SBEK(
			groupId, styleBookEntryKey, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and styleBookEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param styleBookEntryKey the style book entry key
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_SBEK(
		long groupId, String styleBookEntryKey, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_SBEK(
			groupId, styleBookEntryKey, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId) {

		return getPersistence().findByG_T(groupId, themeId);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId, int start, int end) {

		return getPersistence().findByG_T(groupId, themeId, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_T(
		long groupId, String themeId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_T(
			groupId, themeId, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version) {

		return getPersistence().findByG_T_Version(groupId, themeId, version);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version, int start, int end) {

		return getPersistence().findByG_T_Version(
			groupId, themeId, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_T_Version(
		long groupId, String themeId, int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_T_Version(
			groupId, themeId, version, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId) {

		return getPersistence().findByG_D_T(
			groupId, defaultStyleBookEntry, themeId);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId, int start,
		int end) {

		return getPersistence().findByG_D_T(
			groupId, defaultStyleBookEntry, themeId, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_T(
		long groupId, boolean defaultStyleBookEntry, String themeId, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_D_T(
			groupId, defaultStyleBookEntry, themeId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version) {

		return getPersistence().findByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version, int start, int end) {

		return getPersistence().findByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and defaultStyleBookEntry = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param defaultStyleBookEntry the default style book entry
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_D_T_Version(
		long groupId, boolean defaultStyleBookEntry, String themeId,
		int version, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_D_T_Version(
			groupId, defaultStyleBookEntry, themeId, version, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId) {

		return getPersistence().findByG_LikeN_T(groupId, name, themeId);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end) {

		return getPersistence().findByG_LikeN_T(
			groupId, name, themeId, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_T(
		long groupId, String name, String themeId, int start, int end,
		OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_LikeN_T(
			groupId, name, themeId, start, end, orderByComparator);
	}

	/**
	 * Returns all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @return the matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version) {

		return getPersistence().findByG_LikeN_T_Version(
			groupId, name, themeId, version);
	}

	/**
	 * Returns a range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @return the range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version, int start,
		int end) {

		return getPersistence().findByG_LikeN_T_Version(
			groupId, name, themeId, version, start, end);
	}

	/**
	 * Returns an ordered range of all the style book entry versions where groupId = &#63; and name = &#63; and themeId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param themeId the theme ID
	 * @param version the version
	 * @param start the lower bound of the range of style book entry versions
	 * @param end the upper bound of the range of style book entry versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book entry versions
	 */
	public static List<StyleBookEntryVersion> findByG_LikeN_T_Version(
		long groupId, String name, String themeId, int version, int start,
		int end, OrderByComparator<StyleBookEntryVersion> orderByComparator) {

		return getPersistence().findByG_LikeN_T_Version(
			groupId, name, themeId, version, start, end, orderByComparator);
	}

	public static StyleBookEntryVersionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		StyleBookEntryVersionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile StyleBookEntryVersionPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1926020917