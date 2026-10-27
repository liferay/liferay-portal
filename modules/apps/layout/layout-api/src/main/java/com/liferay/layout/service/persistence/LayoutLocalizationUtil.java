/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.persistence;

import com.liferay.layout.model.LayoutLocalization;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout localization service. This utility wraps <code>com.liferay.layout.service.persistence.impl.LayoutLocalizationPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutLocalizationPersistence
 * @generated
 */
public class LayoutLocalizationUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<LayoutLocalization> layoutLocalizations) {

		getPersistence().cacheResult(layoutLocalizations);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(LayoutLocalization layoutLocalization) {
		getPersistence().cacheResult(layoutLocalization);
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
	public static void clearCache(LayoutLocalization layoutLocalization) {
		getPersistence().clearCache(layoutLocalization);
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
	public static Map<Serializable, LayoutLocalization> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutLocalization> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutLocalization> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutLocalization> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutLocalization> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutLocalization update(
		LayoutLocalization layoutLocalization) {

		return getPersistence().update(layoutLocalization);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutLocalization update(
		LayoutLocalization layoutLocalization, ServiceContext serviceContext) {

		return getPersistence().update(layoutLocalization, serviceContext);
	}

	/**
	 * Returns an ordered range of all the layout localizations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout localizations
	 */
	public static List<LayoutLocalization> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutLocalization> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout localization in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	public static LayoutLocalization findByUuid_First(
			String uuid,
			OrderByComparator<LayoutLocalization> orderByComparator)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first layout localization in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchByUuid_First(
		String uuid, OrderByComparator<LayoutLocalization> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the layout localizations where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of layout localizations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout localizations
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the layout localization where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutLocalizationException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	public static LayoutLocalization findByUUID_G(String uuid, long groupId)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout localization where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the layout localization where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout localization that was removed
	 */
	public static LayoutLocalization removeByUUID_G(String uuid, long groupId)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of layout localizations where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout localizations
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the layout localizations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout localizations
	 */
	public static List<LayoutLocalization> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutLocalization> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout localization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	public static LayoutLocalization findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutLocalization> orderByComparator)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first layout localization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutLocalization> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layout localizations where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of layout localizations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout localizations
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the layout localization where languageId = &#63; and plid = &#63; or throws a <code>NoSuchLayoutLocalizationException</code> if it could not be found.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	public static LayoutLocalization findByL_P(String languageId, long plid)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().findByL_P(languageId, plid);
	}

	/**
	 * Returns the layout localization where languageId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchByL_P(
		String languageId, long plid, boolean useFinderCache) {

		return getPersistence().fetchByL_P(languageId, plid, useFinderCache);
	}

	/**
	 * Removes the layout localization where languageId = &#63; and plid = &#63; from the database.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the layout localization that was removed
	 */
	public static LayoutLocalization removeByL_P(String languageId, long plid)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().removeByL_P(languageId, plid);
	}

	/**
	 * Returns the number of layout localizations where languageId = &#63; and plid = &#63;.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the number of matching layout localizations
	 */
	public static int countByL_P(String languageId, long plid) {
		return getPersistence().countByL_P(languageId, plid);
	}

	/**
	 * Returns the layout localization where groupId = &#63; and languageId = &#63; and plid = &#63; or throws a <code>NoSuchLayoutLocalizationException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	public static LayoutLocalization findByG_L_P(
			long groupId, String languageId, long plid)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().findByG_L_P(groupId, languageId, plid);
	}

	/**
	 * Returns the layout localization where groupId = &#63; and languageId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchByG_L_P(
		long groupId, String languageId, long plid, boolean useFinderCache) {

		return getPersistence().fetchByG_L_P(
			groupId, languageId, plid, useFinderCache);
	}

	/**
	 * Removes the layout localization where groupId = &#63; and languageId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the layout localization that was removed
	 */
	public static LayoutLocalization removeByG_L_P(
			long groupId, String languageId, long plid)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().removeByG_L_P(groupId, languageId, plid);
	}

	/**
	 * Returns the number of layout localizations where groupId = &#63; and languageId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the number of matching layout localizations
	 */
	public static int countByG_L_P(long groupId, String languageId, long plid) {
		return getPersistence().countByG_L_P(groupId, languageId, plid);
	}

	/**
	 * Creates a new layout localization with the primary key. Does not add the layout localization to the database.
	 *
	 * @param layoutLocalizationId the primary key for the new layout localization
	 * @return the new layout localization
	 */
	public static LayoutLocalization create(long layoutLocalizationId) {
		return getPersistence().create(layoutLocalizationId);
	}

	/**
	 * Removes the layout localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization that was removed
	 * @throws NoSuchLayoutLocalizationException if a layout localization with the primary key could not be found
	 */
	public static LayoutLocalization remove(long layoutLocalizationId)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().remove(layoutLocalizationId);
	}

	public static LayoutLocalization updateImpl(
		LayoutLocalization layoutLocalization) {

		return getPersistence().updateImpl(layoutLocalization);
	}

	/**
	 * Returns the layout localization with the primary key or throws a <code>NoSuchLayoutLocalizationException</code> if it could not be found.
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization
	 * @throws NoSuchLayoutLocalizationException if a layout localization with the primary key could not be found
	 */
	public static LayoutLocalization findByPrimaryKey(long layoutLocalizationId)
		throws com.liferay.layout.exception.NoSuchLayoutLocalizationException {

		return getPersistence().findByPrimaryKey(layoutLocalizationId);
	}

	/**
	 * Returns the layout localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization, or <code>null</code> if a layout localization with the primary key could not be found
	 */
	public static LayoutLocalization fetchByPrimaryKey(
		long layoutLocalizationId) {

		return getPersistence().fetchByPrimaryKey(layoutLocalizationId);
	}

	/**
	 * Returns the layout localization where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout localization where languageId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchByL_P(String languageId, long plid) {
		return getPersistence().fetchByL_P(languageId, plid);
	}

	/**
	 * Returns the layout localization where groupId = &#63; and languageId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	public static LayoutLocalization fetchByG_L_P(
		long groupId, String languageId, long plid) {

		return getPersistence().fetchByG_L_P(groupId, languageId, plid);
	}

	/**
	 * Returns all the layout localizations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout localizations
	 */
	public static List<LayoutLocalization> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the layout localizations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @return the range of matching layout localizations
	 */
	public static List<LayoutLocalization> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout localizations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout localizations
	 */
	public static List<LayoutLocalization> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutLocalization> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout localizations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout localizations
	 */
	public static List<LayoutLocalization> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout localizations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @return the range of matching layout localizations
	 */
	public static List<LayoutLocalization> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout localizations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.model.impl.LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout localizations
	 */
	public static List<LayoutLocalization> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutLocalization> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	public static LayoutLocalizationPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		LayoutLocalizationPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile LayoutLocalizationPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1968392085