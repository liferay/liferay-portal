/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.persistence.impl;

import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryLocalizationException;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalizationTable;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationImpl;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryLocalizationModelImpl;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryLocalizationPersistence;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryLocalizationUtil;
import com.liferay.friendly.url.service.persistence.impl.constants.FURLPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the friendly url entry localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = FriendlyURLEntryLocalizationPersistence.class)
public class FriendlyURLEntryLocalizationPersistenceImpl
	extends BasePersistenceImpl
		<FriendlyURLEntryLocalization,
		 NoSuchFriendlyURLEntryLocalizationException>
	implements FriendlyURLEntryLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FriendlyURLEntryLocalizationUtil</code> to access the friendly url entry localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FriendlyURLEntryLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FriendlyURLEntryLocalization,
		 NoSuchFriendlyURLEntryLocalizationException>
			_collectionPersistenceFinderByFriendlyURLEntryId;

	/**
	 * Returns an ordered range of all the friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param start the lower bound of the range of friendly url entry localizations
	 * @param end the upper bound of the range of friendly url entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching friendly url entry localizations
	 */
	@Override
	public List<FriendlyURLEntryLocalization> findByFriendlyURLEntryId(
		long friendlyURLEntryId, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFriendlyURLEntryId.find(
			finderCache, new Object[] {friendlyURLEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByFriendlyURLEntryId_First(
			long friendlyURLEntryId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return _collectionPersistenceFinderByFriendlyURLEntryId.findFirst(
			finderCache, new Object[] {friendlyURLEntryId}, orderByComparator);
	}

	/**
	 * Returns the first friendly url entry localization in the ordered set where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_First(
		long friendlyURLEntryId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByFriendlyURLEntryId.fetchFirst(
			finderCache, new Object[] {friendlyURLEntryId}, orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where friendlyURLEntryId = &#63; from the database.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 */
	@Override
	public void removeByFriendlyURLEntryId(long friendlyURLEntryId) {
		_collectionPersistenceFinderByFriendlyURLEntryId.remove(
			finderCache, new Object[] {friendlyURLEntryId});
	}

	/**
	 * Returns the number of friendly url entry localizations where friendlyURLEntryId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByFriendlyURLEntryId(long friendlyURLEntryId) {
		return _collectionPersistenceFinderByFriendlyURLEntryId.count(
			finderCache, new Object[] {friendlyURLEntryId});
	}

	private UniquePersistenceFinder
		<FriendlyURLEntryLocalization,
		 NoSuchFriendlyURLEntryLocalizationException>
			_uniquePersistenceFinderByFriendlyURLEntryId_LanguageId;

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the matching friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return _uniquePersistenceFinderByFriendlyURLEntryId_LanguageId.find(
			finderCache, new Object[] {friendlyURLEntryId, languageId});
	}

	/**
	 * Returns the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching friendly url entry localization, or <code>null</code> if a matching friendly url entry localization could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByFriendlyURLEntryId_LanguageId(
		long friendlyURLEntryId, String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByFriendlyURLEntryId_LanguageId.fetch(
			finderCache, new Object[] {friendlyURLEntryId, languageId},
			useFinderCache);
	}

	/**
	 * Removes the friendly url entry localization where friendlyURLEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the friendly url entry localization that was removed
	 */
	@Override
	public FriendlyURLEntryLocalization removeByFriendlyURLEntryId_LanguageId(
			long friendlyURLEntryId, String languageId)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByFriendlyURLEntryId_LanguageId(friendlyURLEntryId, languageId);

		return remove(friendlyURLEntryLocalization);
	}

	/**
	 * Returns the number of friendly url entry localizations where friendlyURLEntryId = &#63; and languageId = &#63;.
	 *
	 * @param friendlyURLEntryId the friendly url entry ID
	 * @param languageId the language ID
	 * @return the number of matching friendly url entry localizations
	 */
	@Override
	public int countByFriendlyURLEntryId_LanguageId(
		long friendlyURLEntryId, String languageId) {

		return _uniquePersistenceFinderByFriendlyURLEntryId_LanguageId.count(
			finderCache, new Object[] {friendlyURLEntryId, languageId});
	}

	private CollectionPersistenceFinder
		<FriendlyURLEntryLocalization,
		 NoSuchFriendlyURLEntryLocalizationException>
			_collectionPersistenceFinderByG_C_P_U;

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
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
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_P_U.find(
			finderCache,
			new Object[] {groupId, classNameId, parentClassPK, urlTitle}, start,
			end, orderByComparator, useFinderCache);
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
	@Override
	public FriendlyURLEntryLocalization findByG_C_P_U_First(
			long groupId, long classNameId, long parentClassPK, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return _collectionPersistenceFinderByG_C_P_U.findFirst(
			finderCache,
			new Object[] {groupId, classNameId, parentClassPK, urlTitle},
			orderByComparator);
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
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_P_U_First(
		long groupId, long classNameId, long parentClassPK, String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByG_C_P_U.fetchFirst(
			finderCache,
			new Object[] {groupId, classNameId, parentClassPK, urlTitle},
			orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param parentClassPK the parent class pk
	 * @param urlTitle the url title
	 */
	@Override
	public void removeByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		_collectionPersistenceFinderByG_C_P_U.remove(
			finderCache,
			new Object[] {groupId, classNameId, parentClassPK, urlTitle});
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
	@Override
	public int countByG_C_P_U(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		return _collectionPersistenceFinderByG_C_P_U.count(
			finderCache,
			new Object[] {groupId, classNameId, parentClassPK, urlTitle});
	}

	private CollectionPersistenceFinder
		<FriendlyURLEntryLocalization,
		 NoSuchFriendlyURLEntryLocalizationException>
			_collectionPersistenceFinderByC_C_U_C;

	/**
	 * Returns an ordered range of all the friendly url entry localizations where companyId = &#63; and classNameId = &#63; and urlTitle = &#63; and ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
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
	@Override
	public List<FriendlyURLEntryLocalization> findByC_C_U_C(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_U_C.find(
			finderCache,
			new Object[] {companyId, classNameId, urlTitle, ctCollectionId},
			start, end, orderByComparator, useFinderCache);
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
	@Override
	public FriendlyURLEntryLocalization findByC_C_U_C_First(
			long companyId, long classNameId, String urlTitle,
			long ctCollectionId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return _collectionPersistenceFinderByC_C_U_C.findFirst(
			finderCache,
			new Object[] {companyId, classNameId, urlTitle, ctCollectionId},
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
	@Override
	public FriendlyURLEntryLocalization fetchByC_C_U_C_First(
		long companyId, long classNameId, String urlTitle, long ctCollectionId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByC_C_U_C.fetchFirst(
			finderCache,
			new Object[] {companyId, classNameId, urlTitle, ctCollectionId},
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
	@Override
	public void removeByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		_collectionPersistenceFinderByC_C_U_C.remove(
			finderCache,
			new Object[] {companyId, classNameId, urlTitle, ctCollectionId});
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
	@Override
	public int countByC_C_U_C(
		long companyId, long classNameId, String urlTitle,
		long ctCollectionId) {

		return _collectionPersistenceFinderByC_C_U_C.count(
			finderCache,
			new Object[] {companyId, classNameId, urlTitle, ctCollectionId});
	}

	private CollectionPersistenceFinder
		<FriendlyURLEntryLocalization,
		 NoSuchFriendlyURLEntryLocalizationException>
			_collectionPersistenceFinderByG_C_C_L;

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
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
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId,
		int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_L.find(
			finderCache,
			new Object[] {groupId, classNameId, classPK, languageId}, start,
			end, orderByComparator, useFinderCache);
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
	@Override
	public FriendlyURLEntryLocalization findByG_C_C_L_First(
			long groupId, long classNameId, long classPK, String languageId,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return _collectionPersistenceFinderByG_C_C_L.findFirst(
			finderCache,
			new Object[] {groupId, classNameId, classPK, languageId},
			orderByComparator);
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
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_C_L_First(
		long groupId, long classNameId, long classPK, String languageId,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_L.fetchFirst(
			finderCache,
			new Object[] {groupId, classNameId, classPK, languageId},
			orderByComparator);
	}

	/**
	 * Removes all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and classPK = &#63; and languageId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param languageId the language ID
	 */
	@Override
	public void removeByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		_collectionPersistenceFinderByG_C_C_L.remove(
			finderCache,
			new Object[] {groupId, classNameId, classPK, languageId});
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
	@Override
	public int countByG_C_C_L(
		long groupId, long classNameId, long classPK, String languageId) {

		return _collectionPersistenceFinderByG_C_C_L.count(
			finderCache,
			new Object[] {groupId, classNameId, classPK, languageId});
	}

	private UniquePersistenceFinder
		<FriendlyURLEntryLocalization,
		 NoSuchFriendlyURLEntryLocalizationException>
			_uniquePersistenceFinderByG_C_P_L_U;

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
	@Override
	public FriendlyURLEntryLocalization findByG_C_P_L_U(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return _uniquePersistenceFinderByG_C_P_L_U.find(
			finderCache,
			new Object[] {
				groupId, classNameId, parentClassPK, languageId, urlTitle
			});
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
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_P_L_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_P_L_U.fetch(
			finderCache,
			new Object[] {
				groupId, classNameId, parentClassPK, languageId, urlTitle
			},
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
	@Override
	public FriendlyURLEntryLocalization removeByG_C_P_L_U(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle)
		throws NoSuchFriendlyURLEntryLocalizationException {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			findByG_C_P_L_U(
				groupId, classNameId, parentClassPK, languageId, urlTitle);

		return remove(friendlyURLEntryLocalization);
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
	@Override
	public int countByG_C_P_L_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return _uniquePersistenceFinderByG_C_P_L_U.count(
			finderCache,
			new Object[] {
				groupId, classNameId, parentClassPK, languageId, urlTitle
			});
	}

	private CollectionPersistenceFinder
		<FriendlyURLEntryLocalization,
		 NoSuchFriendlyURLEntryLocalizationException>
			_collectionPersistenceFinderByG_C_P_NotL_U;

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
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return findByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
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
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end) {

		return findByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
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
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return findByG_C_P_NotL_U(
			groupId, classNameId, parentClassPK, languageId, urlTitle, start,
			end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the friendly url entry localizations where groupId = &#63; and classNameId = &#63; and parentClassPK = &#63; and languageId &ne; &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FriendlyURLEntryLocalizationModelImpl</code>.
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
	@Override
	public List<FriendlyURLEntryLocalization> findByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle, int start, int end,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_P_NotL_U.find(
			finderCache,
			new Object[] {
				groupId, classNameId, parentClassPK, languageId, urlTitle
			},
			start, end, orderByComparator, useFinderCache);
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
	@Override
	public FriendlyURLEntryLocalization findByG_C_P_NotL_U_First(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle,
			OrderByComparator<FriendlyURLEntryLocalization> orderByComparator)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return _collectionPersistenceFinderByG_C_P_NotL_U.findFirst(
			finderCache,
			new Object[] {
				groupId, classNameId, parentClassPK, languageId, urlTitle
			},
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
	@Override
	public FriendlyURLEntryLocalization fetchByG_C_P_NotL_U_First(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle,
		OrderByComparator<FriendlyURLEntryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByG_C_P_NotL_U.fetchFirst(
			finderCache,
			new Object[] {
				groupId, classNameId, parentClassPK, languageId, urlTitle
			},
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
	@Override
	public void removeByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		_collectionPersistenceFinderByG_C_P_NotL_U.remove(
			finderCache,
			new Object[] {
				groupId, classNameId, parentClassPK, languageId, urlTitle
			});
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
	@Override
	public int countByG_C_P_NotL_U(
		long groupId, long classNameId, long parentClassPK, String languageId,
		String urlTitle) {

		return _collectionPersistenceFinderByG_C_P_NotL_U.count(
			finderCache,
			new Object[] {
				groupId, classNameId, parentClassPK, languageId, urlTitle
			});
	}

	public FriendlyURLEntryLocalizationPersistenceImpl() {
		setModelClass(FriendlyURLEntryLocalization.class);

		setModelImplClass(FriendlyURLEntryLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(FriendlyURLEntryLocalizationTable.INSTANCE);
	}

	/**
	 * Creates a new friendly url entry localization with the primary key. Does not add the friendly url entry localization to the database.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key for the new friendly url entry localization
	 * @return the new friendly url entry localization
	 */
	@Override
	public FriendlyURLEntryLocalization create(
		long friendlyURLEntryLocalizationId) {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			new FriendlyURLEntryLocalizationImpl();

		friendlyURLEntryLocalization.setNew(true);
		friendlyURLEntryLocalization.setPrimaryKey(
			friendlyURLEntryLocalizationId);

		friendlyURLEntryLocalization.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return friendlyURLEntryLocalization;
	}

	/**
	 * Removes the friendly url entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization that was removed
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization remove(
			long friendlyURLEntryLocalizationId)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return remove((Serializable)friendlyURLEntryLocalizationId);
	}

	@Override
	protected FriendlyURLEntryLocalization removeImpl(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(friendlyURLEntryLocalization)) {
				friendlyURLEntryLocalization =
					(FriendlyURLEntryLocalization)session.get(
						FriendlyURLEntryLocalizationImpl.class,
						friendlyURLEntryLocalization.getPrimaryKeyObj());
			}

			if ((friendlyURLEntryLocalization != null) &&
				ctPersistenceHelper.isRemove(friendlyURLEntryLocalization)) {

				session.delete(friendlyURLEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (friendlyURLEntryLocalization != null) {
			clearCache(friendlyURLEntryLocalization);
		}

		return friendlyURLEntryLocalization;
	}

	@Override
	public FriendlyURLEntryLocalization updateImpl(
		FriendlyURLEntryLocalization friendlyURLEntryLocalization) {

		boolean isNew = friendlyURLEntryLocalization.isNew();

		if (!(friendlyURLEntryLocalization instanceof
				FriendlyURLEntryLocalizationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					friendlyURLEntryLocalization.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					friendlyURLEntryLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in friendlyURLEntryLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FriendlyURLEntryLocalization implementation " +
					friendlyURLEntryLocalization.getClass());
		}

		FriendlyURLEntryLocalizationModelImpl
			friendlyURLEntryLocalizationModelImpl =
				(FriendlyURLEntryLocalizationModelImpl)
					friendlyURLEntryLocalization;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(friendlyURLEntryLocalization)) {
				if (!isNew) {
					session.evict(
						FriendlyURLEntryLocalizationImpl.class,
						friendlyURLEntryLocalization.getPrimaryKeyObj());
				}

				session.save(friendlyURLEntryLocalization);
			}
			else {
				friendlyURLEntryLocalization =
					(FriendlyURLEntryLocalization)session.merge(
						friendlyURLEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(friendlyURLEntryLocalization, false);

		if (isNew) {
			friendlyURLEntryLocalization.setNew(false);
		}

		friendlyURLEntryLocalization.resetOriginalValues();

		return friendlyURLEntryLocalization;
	}

	/**
	 * Returns the friendly url entry localization with the primary key or throws a <code>NoSuchFriendlyURLEntryLocalizationException</code> if it could not be found.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization
	 * @throws NoSuchFriendlyURLEntryLocalizationException if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization findByPrimaryKey(
			long friendlyURLEntryLocalizationId)
		throws NoSuchFriendlyURLEntryLocalizationException {

		return findByPrimaryKey((Serializable)friendlyURLEntryLocalizationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the friendly url entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param friendlyURLEntryLocalizationId the primary key of the friendly url entry localization
	 * @return the friendly url entry localization, or <code>null</code> if a friendly url entry localization with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntryLocalization fetchByPrimaryKey(
		long friendlyURLEntryLocalizationId) {

		return fetchByPrimaryKey((Serializable)friendlyURLEntryLocalizationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "friendlyURLEntryLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return FriendlyURLEntryLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "FriendlyURLEntryLocalization";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("friendlyURLEntryId");
		ctMergeColumnNames.add("languageId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("classNameId");
		ctMergeColumnNames.add("parentClassPK");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("urlTitle");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("friendlyURLEntryLocalizationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"friendlyURLEntryId", "languageId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "classNameId", "parentClassPK", "languageId",
				"urlTitle"
			});
	}

	/**
	 * Initializes the friendly url entry localization persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByFriendlyURLEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFriendlyURLEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"friendlyURLEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFriendlyURLEntryId",
					new String[] {Long.class.getName()},
					new String[] {"friendlyURLEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFriendlyURLEntryId",
					new String[] {Long.class.getName()},
					new String[] {"friendlyURLEntryId"}, false),
				_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "friendlyURLEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getFriendlyURLEntryId));

		_uniquePersistenceFinderByFriendlyURLEntryId_LanguageId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY,
					"fetchByFriendlyURLEntryId_LanguageId",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"friendlyURLEntryId", "languageId"}, 0, 2,
					false, FriendlyURLEntryLocalization::getFriendlyURLEntryId,
					convertNullFunction(
						FriendlyURLEntryLocalization::getLanguageId)),
				_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE, "",
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "friendlyURLEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getFriendlyURLEntryId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					FriendlyURLEntryLocalization::getLanguageId));

		_collectionPersistenceFinderByG_C_P_U =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_P_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "parentClassPK", "urlTitle"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_P_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "parentClassPK", "urlTitle"
					},
					0, 8, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_P_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "parentClassPK", "urlTitle"
					},
					0, 8, false, null),
				_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getGroupId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getClassNameId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "parentClassPK",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getParentClassPK),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "urlTitle",
					FinderColumn.Type.STRING, "=", true, true,
					FriendlyURLEntryLocalization::getUrlTitle));

		_collectionPersistenceFinderByC_C_U_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_U_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "urlTitle", "ctCollectionId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_U_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "urlTitle", "ctCollectionId"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_U_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Long.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "urlTitle", "ctCollectionId"
					},
					0, 4, false, null),
				_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getCompanyId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getClassNameId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "urlTitle",
					FinderColumn.Type.STRING, "=", true, true,
					FriendlyURLEntryLocalization::getUrlTitle),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "ctCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getCtCollectionId));

		_collectionPersistenceFinderByG_C_C_L =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "languageId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "languageId"
					},
					0, 8, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "languageId"
					},
					0, 8, false, null),
				_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getGroupId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getClassNameId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getClassPK),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					FriendlyURLEntryLocalization::getLanguageId));

		_uniquePersistenceFinderByG_C_P_L_U = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_P_L_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"groupId", "classNameId", "parentClassPK", "languageId",
					"urlTitle"
				},
				0, 24, false, FriendlyURLEntryLocalization::getGroupId,
				FriendlyURLEntryLocalization::getClassNameId,
				FriendlyURLEntryLocalization::getParentClassPK,
				convertNullFunction(
					FriendlyURLEntryLocalization::getLanguageId),
				convertNullFunction(FriendlyURLEntryLocalization::getUrlTitle)),
			_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE, "",
			new FinderColumn<>(
				"friendlyURLEntryLocalization.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				FriendlyURLEntryLocalization::getGroupId),
			new FinderColumn<>(
				"friendlyURLEntryLocalization.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				FriendlyURLEntryLocalization::getClassNameId),
			new FinderColumn<>(
				"friendlyURLEntryLocalization.", "parentClassPK",
				FinderColumn.Type.LONG, "=", true, true,
				FriendlyURLEntryLocalization::getParentClassPK),
			new FinderColumn<>(
				"friendlyURLEntryLocalization.", "languageId",
				FinderColumn.Type.STRING, "=", true, true,
				FriendlyURLEntryLocalization::getLanguageId),
			new FinderColumn<>(
				"friendlyURLEntryLocalization.", "urlTitle",
				FinderColumn.Type.STRING, "=", true, true,
				FriendlyURLEntryLocalization::getUrlTitle));

		_collectionPersistenceFinderByG_C_P_NotL_U =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_C_P_NotL_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "parentClassPK", "languageId",
						"urlTitle"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_C_P_NotL_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "parentClassPK", "languageId",
						"urlTitle"
					},
					false),
				_SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				_SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE,
				FriendlyURLEntryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getGroupId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getClassNameId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "parentClassPK",
					FinderColumn.Type.LONG, "=", true, true,
					FriendlyURLEntryLocalization::getParentClassPK),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "languageId",
					FinderColumn.Type.STRING, "!=", true, true,
					FriendlyURLEntryLocalization::getLanguageId),
				new FinderColumn<>(
					"friendlyURLEntryLocalization.", "urlTitle",
					FinderColumn.Type.STRING, "=", true, true,
					FriendlyURLEntryLocalization::getUrlTitle));

		FriendlyURLEntryLocalizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FriendlyURLEntryLocalizationUtil.setPersistence(null);

		entityCache.removeCache(
			FriendlyURLEntryLocalizationImpl.class.getName());
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = FURLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		FriendlyURLEntryLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION =
		"SELECT friendlyURLEntryLocalization FROM FriendlyURLEntryLocalization friendlyURLEntryLocalization";

	private static final String _SQL_SELECT_FRIENDLYURLENTRYLOCALIZATION_WHERE =
		"SELECT friendlyURLEntryLocalization FROM FriendlyURLEntryLocalization friendlyURLEntryLocalization WHERE ";

	private static final String _SQL_COUNT_FRIENDLYURLENTRYLOCALIZATION_WHERE =
		"SELECT COUNT(friendlyURLEntryLocalization) FROM FriendlyURLEntryLocalization friendlyURLEntryLocalization WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FriendlyURLEntryLocalization exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FriendlyURLEntryLocalizationPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-665798561