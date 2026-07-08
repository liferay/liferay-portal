/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.persistence;

import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the friendly url entry localization service. This utility wraps <code>com.liferay.friendly.url.service.persistence.impl.FriendlyURLEntryLocalizationPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FriendlyURLEntryLocalizationPersistence
 * @generated
 */
public class FriendlyURLEntryLocalizationUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations) {

		getPersistence().cacheResult(friendlyURLEntryLocalizations);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		getPersistence().cacheResult(friendlyURLEntryLocalization);
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
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		getPersistence().clearCache(friendlyURLEntryLocalization);
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
	public static Map<Serializable, FriendlyURLEntryLocalization>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FriendlyURLEntryLocalization> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FriendlyURLEntryLocalization> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FriendlyURLEntryLocalization> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FriendlyURLEntryLocalization update(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		return getPersistence().update(friendlyURLEntryLocalization);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FriendlyURLEntryLocalization update(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization,
		ServiceContext serviceContext) {

		return getPersistence().update(
			friendlyURLEntryLocalization, serviceContext);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFriendlyURLEntryId(
			friendlyURLEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization findByFriendlyURLEntryId_First(
			long friendlyURLEntryId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByFriendlyURLEntryId_First(
			friendlyURLEntryId, orderByComparator);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_First(
		long friendlyURLEntryId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().fetchByFriendlyURLEntryId_First(
			friendlyURLEntryId, orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where friendlyURLEntryId = &#63; from the database.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 */
	public static void removeByFriendlyURLEntryId(long friendlyURLEntryId) {
		getPersistence().removeByFriendlyURLEntryId(friendlyURLEntryId);
	}

	/**
	 * Returns the number of friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @return the number of matching friendly url entry localizations
	 */
	public static int countByFriendlyURLEntryId(long friendlyURLEntryId) {
		return getPersistence().countByFriendlyURLEntryId(friendlyURLEntryId);
	}

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization
			findByFriendlyURLEntryId_LanguageId(
				long friendlyURLEntryId, String languageId)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByFriendlyURLEntryId_LanguageId(
			friendlyURLEntryId, languageId);
	}

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization
		fetchByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId,
			boolean useFinderCache) {

		return getPersistence().fetchByFriendlyURLEntryId_LanguageId(
			friendlyURLEntryId, languageId, useFinderCache);
	}

	/**
	 * Removes the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the friendly url entry localization that was removed
	 */
	public static FriendlyURLEntryLocalization
			removeByFriendlyURLEntryId_LanguageId(
				long friendlyURLEntryId, String languageId)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().removeByFriendlyURLEntryId_LanguageId(
			friendlyURLEntryId, languageId);
	}

	/**
	 * Returns the number of friendly url entry localizations where friendlyURLEntryId = &#63; and languageId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the number of matching friendly url entry localizations
	 */
	public static int countByFriendlyURLEntryId_LanguageId(
		long friendlyURLEntryId, String languageId) {

		return getPersistence().countByFriendlyURLEntryId_LanguageId(
			friendlyURLEntryId, languageId);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_U(
			groupId, classNameId, urlTitle, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization findByG_C_U_First(
			long groupId, long classNameId, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByG_C_U_First(
			groupId, classNameId, urlTitle, orderByComparator);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByG_C_U_First(
		long groupId, long classNameId, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().fetchByG_C_U_First(
			groupId, classNameId, urlTitle, orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 */
	public static void removeByG_C_U(
		long groupId, long classNameId, String urlTitle) {

		getPersistence().removeByG_C_U(groupId, classNameId, urlTitle);
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	public static int countByG_C_U(
		long groupId, long classNameId, String urlTitle) {

		return getPersistence().countByG_C_U(groupId, classNameId, urlTitle);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization findByG_C_P_U_First(
			long groupId, long classNameId, long parentClassPK, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByG_C_P_U_First(
			groupId, classNameId, parentClassPK, urlTitle, orderByComparator);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByG_C_P_U_First(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().fetchByG_C_P_U_First(
			groupId, classNameId, parentClassPK, urlTitle, orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 */
	public static void removeByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		getPersistence().removeByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle);
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	public static int countByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		return getPersistence().countByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization findByC_C_U_C_First(
			long companyId, long classNameId, String urlTitle,
			long ctCollectionId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByC_C_U_C_First(
			companyId, classNameId, urlTitle, ctCollectionId,
			orderByComparator);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByC_C_U_C_First(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().fetchByC_C_U_C_First(
			companyId, classNameId, urlTitle, ctCollectionId,
			orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 */
	public static void removeByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		getPersistence().removeByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId);
	}

	/**
	 * Returns the number of friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching friendly url entry localizations
	 */
	public static int countByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		return getPersistence().countByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_L(
			groupId, classNameId, classPK, languageId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization findByG_C_C_L_First(
			long groupId, long classNameId, long classPK, String languageId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByG_C_C_L_First(
			groupId, classNameId, classPK, languageId, orderByComparator);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByG_C_C_L_First(
		long groupId, long classNameId, long classPK, String languageId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().fetchByG_C_C_L_First(
			groupId, classNameId, classPK, languageId, orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 */
	public static void removeByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		getPersistence().removeByG_C_C_L(
			groupId, classNameId, classPK, languageId);
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @return the number of matching friendly url entry localizations
	 */
	public static int countByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		return getPersistence().countByG_C_C_L(
			groupId, classNameId, classPK, languageId);
	}

	/**
	 * Returns the friendly url entry localization where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId = &#63; and urlTitle = &#63; or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization findByG_C_P_L_U(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByG_C_P_L_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	/**
	 * Returns the friendly url entry localization where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByG_C_P_L_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, boolean useFinderCache) {

		return getPersistence().fetchByG_C_P_L_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle,
			useFinderCache);
	}

	/**
	 * Removes the friendly url entry localization where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the friendly url entry localization that was removed
	 */
	public static FriendlyURLEntryLocalization removeByG_C_P_L_U(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().removeByG_C_P_L_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	public static int countByG_C_P_L_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return getPersistence().countByG_C_P_L_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	/**
	 * Returns all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return getPersistence().findByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end) {

		return getPersistence().findByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle, start,
			end);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().findByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization findByG_C_P_NotL_U_First(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByG_C_P_NotL_U_First(
			groupId, classNameId, parentClassPK, languageId, urlTitle,
			orderByComparator);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByG_C_P_NotL_U_First(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().fetchByG_C_P_NotL_U_First(
			groupId, classNameId, parentClassPK, languageId, urlTitle,
			orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 */
	public static void removeByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		getPersistence().removeByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	public static int countByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return getPersistence().countByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	/**
	 * Creates a new friendly url entry localization with the primary key. Does not add the friendly url entry localization to the database.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key for the new friendly url entry localization
	 * @return the new friendly url entry localization
	 */
	public static FriendlyURLEntryLocalization create(
		long friendlyURLEntryLocalizationId) {

		return getPersistence().create(friendlyURLEntryLocalizationId);
	}

	/**
	 * Removes the friendly url entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization that was removed
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	public static FriendlyURLEntryLocalization remove(
			long friendlyURLEntryLocalizationId)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().remove(friendlyURLEntryLocalizationId);
	}

	public static FriendlyURLEntryLocalization updateImpl(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		return getPersistence().updateImpl(friendlyURLEntryLocalization);
	}

	/**
	 * Returns the friendly url entry localization with the primary key or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	public static FriendlyURLEntryLocalization findByPrimaryKey(
			long friendlyURLEntryLocalizationId)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getPersistence().findByPrimaryKey(
			friendlyURLEntryLocalizationId);
	}

	/**
	 * Returns the friendly url entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization, or <code>null</code> if a friendly url entry localization with the primary key could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByPrimaryKey(
		long friendlyURLEntryLocalizationId) {

		return getPersistence().fetchByPrimaryKey(
			friendlyURLEntryLocalizationId);
	}

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization
		fetchByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId) {

		return getPersistence().fetchByFriendlyURLEntryId_LanguageId(
			friendlyURLEntryId, languageId);
	}

	/**
	 * Returns the friendly url entry localization where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public static FriendlyURLEntryLocalization fetchByG_C_P_L_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return getPersistence().fetchByG_C_P_L_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	/**
	 * Returns all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @return the matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId) {

		return getPersistence().findByFriendlyURLEntryId(friendlyURLEntryId);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId, int start, int end) {

		return getPersistence().findByFriendlyURLEntryId(
			friendlyURLEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().findByFriendlyURLEntryId(
			friendlyURLEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle) {

		return getPersistence().findByG_C_U(groupId, classNameId, urlTitle);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end) {

		return getPersistence().findByG_C_U(
			groupId, classNameId, urlTitle, start, end);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().findByG_C_U(
			groupId, classNameId, urlTitle, start, end, orderByComparator);
	}

	/**
	 * Returns all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		return getPersistence().findByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		int start, int end) {

		return getPersistence().findByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle, start, end);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().findByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @return the matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		return getPersistence().findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end) {

		return getPersistence().findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @return the matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		return getPersistence().findByG_C_C_L(
			groupId, classNameId, classPK, languageId);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @return the range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end) {

		return getPersistence().findByG_C_C_L(
			groupId, classNameId, classPK, languageId, start, end);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching friendly url entry localizations
	 */
	public static List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return getPersistence().findByG_C_C_L(
			groupId, classNameId, classPK, languageId, start, end,
			orderByComparator);
	}

	public static FriendlyURLEntryLocalizationPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		FriendlyURLEntryLocalizationPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile FriendlyURLEntryLocalizationPersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1827048853