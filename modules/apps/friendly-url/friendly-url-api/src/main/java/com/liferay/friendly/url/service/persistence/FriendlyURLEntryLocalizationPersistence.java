/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.persistence;

import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryLocalizationException;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the friendly url entry localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FriendlyURLEntryLocalizationUtil
 * @generated
 */
@ProviderType
public interface FriendlyURLEntryLocalizationPersistence
	extends BasePersistence<FriendlyURLEntryLocalization>,
			CTPersistence<FriendlyURLEntryLocalization> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FriendlyURLEntryLocalizationUtil} to access the friendly url entry localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

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
	public java.util.List<FriendlyURLEntryLocalization>
		findByFriendlyURLEntryId(
			long friendlyURLEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<FriendlyURLEntryLocalization> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public FriendlyURLEntryLocalization findByFriendlyURLEntryId_First(
			long friendlyURLEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException;

	/**
	 * Returns the first friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_First(
		long friendlyURLEntryId,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator);

	/**
	 * Removes all the friendly url entry localizations where friendlyURLEntryId = &#63; from the database.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 */
	public void removeByFriendlyURLEntryId(long friendlyURLEntryId);

	/**
	 * Returns the number of friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @return the number of matching friendly url entry localizations
	 */
	public int countByFriendlyURLEntryId(long friendlyURLEntryId);

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	public FriendlyURLEntryLocalization findByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId)
		throws NoSuchFriendlyURLEntryLocalizationException;

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_LanguageId(
		long friendlyURLEntryId, String languageId, boolean useFinderCache);

	/**
	 * Removes the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the friendly url entry localization that was removed
	 */
	public FriendlyURLEntryLocalization removeByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId)
		throws NoSuchFriendlyURLEntryLocalizationException;

	/**
	 * Returns the number of friendly url entry localizations where friendlyURLEntryId = &#63; and languageId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the number of matching friendly url entry localizations
	 */
	public int countByFriendlyURLEntryId_LanguageId(
		long friendlyURLEntryId, String languageId);

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
	public java.util.List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache);

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
	public FriendlyURLEntryLocalization findByG_C_U_First(
			long groupId, long classNameId, String urlTitle,
			com.liferay.portal.kernel.util.OrderByComparator
				<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException;

	/**
	 * Returns the first friendly url entry localization in the ordered set where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public FriendlyURLEntryLocalization fetchByG_C_U_First(
		long groupId, long classNameId, String urlTitle,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator);

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 */
	public void removeByG_C_U(long groupId, long classNameId, String urlTitle);

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	public int countByG_C_U(long groupId, long classNameId, String urlTitle);

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
	public java.util.List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache);

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
	public FriendlyURLEntryLocalization findByG_C_P_U_First(
			long groupId, long classNameId, long parentClassPK, String urlTitle,
			com.liferay.portal.kernel.util.OrderByComparator
				<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException;

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
	public FriendlyURLEntryLocalization fetchByG_C_P_U_First(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator);

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 */
	public void removeByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle);

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 * @return the number of matching friendly url entry localizations
	 */
	public int countByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle);

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
	public java.util.List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache);

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
	public FriendlyURLEntryLocalization findByC_C_U_C_First(
			long companyId, long classNameId, String urlTitle,
			long ctCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException;

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
	public FriendlyURLEntryLocalization fetchByC_C_U_C_First(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator);

	/**
	 * Removes all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 */
	public void removeByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId);

	/**
	 * Returns the number of friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching friendly url entry localizations
	 */
	public int countByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId);

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
	public java.util.List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache);

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
	public FriendlyURLEntryLocalization findByG_C_C_L_First(
			long groupId, long classNameId, long classPK, String languageId,
			com.liferay.portal.kernel.util.OrderByComparator
				<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException;

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
	public FriendlyURLEntryLocalization fetchByG_C_C_L_First(
		long groupId, long classNameId, long classPK, String languageId,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator);

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 */
	public void removeByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId);

	/**
	 * Returns the number of friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 * @return the number of matching friendly url entry localizations
	 */
	public int countByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId);

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
	public FriendlyURLEntryLocalization findByG_C_P_L_U(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException;

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
	public FriendlyURLEntryLocalization fetchByG_C_P_L_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, boolean useFinderCache);

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
	public FriendlyURLEntryLocalization removeByG_C_P_L_U(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException;

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
	public int countByG_C_P_L_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle);

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
	public java.util.List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle);

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
	public java.util.List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end);

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
	public java.util.List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator);

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
	public java.util.List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache);

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
	public FriendlyURLEntryLocalization findByG_C_P_NotL_U_First(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle,
			com.liferay.portal.kernel.util.OrderByComparator
				<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException;

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
	public FriendlyURLEntryLocalization fetchByG_C_P_NotL_U_First(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator);

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param languageId the language ID
	 * @param urlTitle the url title
	 */
	public void removeByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle);

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
	public int countByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle);

	/**
	 * Creates a new friendly url entry localization with the primary key. Does not add the friendly url entry localization to the database.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key for the new friendly url entry localization
	 * @return the new friendly url entry localization
	 */
	public FriendlyURLEntryLocalization create(
		long friendlyURLEntryLocalizationId);

	/**
	 * Removes the friendly url entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization that was removed
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	public FriendlyURLEntryLocalization remove(
			long friendlyURLEntryLocalizationId)
		throws NoSuchFriendlyURLEntryLocalizationException;

	public FriendlyURLEntryLocalization updateImpl(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization);

	/**
	 * Returns the friendly url entry localization with the primary key or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	public FriendlyURLEntryLocalization findByPrimaryKey(
			long friendlyURLEntryLocalizationId)
		throws NoSuchFriendlyURLEntryLocalizationException;

	/**
	 * Returns the friendly url entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization, or <code>null</code> if a friendly url entry localization with the primary key could not be found
	 */
	public FriendlyURLEntryLocalization fetchByPrimaryKey(
		long friendlyURLEntryLocalizationId);

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	public default FriendlyURLEntryLocalization
		fetchByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId) {

		return fetchByFriendlyURLEntryId_LanguageId(
			friendlyURLEntryId, languageId, true);
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
	public default FriendlyURLEntryLocalization fetchByG_C_P_L_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return fetchByG_C_P_L_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle, true);
	}

	/**
	 * Returns all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @return the matching friendly url entry localizations
	 */
	public default java.util.List<FriendlyURLEntryLocalization>
		findByFriendlyURLEntryId(long friendlyURLEntryId) {

		return findByFriendlyURLEntryId(
			friendlyURLEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<FriendlyURLEntryLocalization>
		findByFriendlyURLEntryId(long friendlyURLEntryId, int start, int end) {

		return findByFriendlyURLEntryId(
			friendlyURLEntryId, start, end, null, true);
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
	public default java.util.List<FriendlyURLEntryLocalization>
		findByFriendlyURLEntryId(
			long friendlyURLEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<FriendlyURLEntryLocalization> orderByComparator) {

		return findByFriendlyURLEntryId(
			friendlyURLEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param urlTitle the url title
	 * @return the matching friendly url entry localizations
	 */
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle) {

		return findByG_C_U(
			groupId, classNameId, urlTitle,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end) {

		return findByG_C_U(
			groupId, classNameId, urlTitle, start, end, null, true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_U(
		long groupId, long classNameId, String urlTitle, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator) {

		return findByG_C_U(
			groupId, classNameId, urlTitle, start, end, orderByComparator,
			true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		return findByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		int start, int end) {

		return findByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle, start, end, null,
			true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator) {

		return findByG_C_P_U(
			groupId, classNameId, parentClassPK, urlTitle, start, end,
			orderByComparator, true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		return findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end) {

		return findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, start, end, null,
			true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator) {

		return findByC_C_U_C(
			companyId, classNameId, urlTitle, ctCollectionId, start, end,
			orderByComparator, true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		return findByG_C_C_L(
			groupId, classNameId, classPK, languageId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end) {

		return findByG_C_C_L(
			groupId, classNameId, classPK, languageId, start, end, null, true);
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
	public default java.util.List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<FriendlyURLEntryLocalization> orderByComparator) {

		return findByG_C_C_L(
			groupId, classNameId, classPK, languageId, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-979045191