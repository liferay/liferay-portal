/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service;

import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for FriendlyURLEntry. This utility wraps
 * <code>com.liferay.friendly.url.service.impl.FriendlyURLEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see FriendlyURLEntryLocalService
 * @generated
 */
public class FriendlyURLEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.friendly.url.service.impl.FriendlyURLEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the friendly url entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FriendlyURLEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param friendlyURLEntry the friendly url entry
	 * @return the friendly url entry that was added
	 */
	public static FriendlyURLEntry addFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry) {

		return getService().addFriendlyURLEntry(friendlyURLEntry);
	}

	public static FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFriendlyURLEntry(
			groupId, classNameId, parentClassPK, classPK, defaultLanguageId,
			urlTitleMap, serviceContext);
	}

	public static FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK,
			Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFriendlyURLEntry(
			groupId, classNameId, classPK, urlTitleMap, serviceContext);
	}

	public static FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFriendlyURLEntry(
			groupId, classNameId, classPK, defaultLanguageId, urlTitleMap,
			serviceContext);
	}

	public static FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK, String urlTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFriendlyURLEntry(
			groupId, classNameId, classPK, urlTitle, serviceContext);
	}

	/**
	 * Creates a new friendly url entry with the primary key. Does not add the friendly url entry to the database.
	 *
	 * @param friendlyURLEntryId the primary key for the new friendly url entry
	 * @return the new friendly url entry
	 */
	public static FriendlyURLEntry createFriendlyURLEntry(
		long friendlyURLEntryId) {

		return getService().createFriendlyURLEntry(friendlyURLEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyFriendlyURLEntries(
		long companyId, long classNameId) {

		getService().deleteCompanyFriendlyURLEntries(companyId, classNameId);
	}

	/**
	 * Deletes the friendly url entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FriendlyURLEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param friendlyURLEntry the friendly url entry
	 * @return the friendly url entry that was removed
	 */
	public static FriendlyURLEntry deleteFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry) {

		return getService().deleteFriendlyURLEntry(friendlyURLEntry);
	}

	/**
	 * Deletes the friendly url entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FriendlyURLEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param friendlyURLEntryId the primary key of the friendly url entry
	 * @return the friendly url entry that was removed
	 * @throws PortalException if a friendly url entry with the primary key could not be found
	 */
	public static FriendlyURLEntry deleteFriendlyURLEntry(
			long friendlyURLEntryId)
		throws PortalException {

		return getService().deleteFriendlyURLEntry(friendlyURLEntryId);
	}

	public static void deleteFriendlyURLEntry(
		long groupId, long classNameId, long classPK) {

		getService().deleteFriendlyURLEntry(groupId, classNameId, classPK);
	}

	public static void deleteFriendlyURLLocalizationEntry(
			com.liferay.friendly.url.model.FriendlyURLEntryLocalization
				friendlyURLEntryLocalization)
		throws PortalException {

		getService().deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryLocalization);
	}

	public static void deleteFriendlyURLLocalizationEntry(
			long friendlyURLEntryId, String languageId)
		throws PortalException {

		getService().deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryId, languageId);
	}

	public static void deleteGroupFriendlyURLEntries(
		long groupId, long classNameId) {

		getService().deleteGroupFriendlyURLEntries(groupId, classNameId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static FriendlyURLEntry fetchFriendlyURLEntry(
		long friendlyURLEntryId) {

		return getService().fetchFriendlyURLEntry(friendlyURLEntryId);
	}

	public static FriendlyURLEntry fetchFriendlyURLEntry(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		return getService().fetchFriendlyURLEntry(
			groupId, classNameId, parentClassPK, urlTitle);
	}

	public static FriendlyURLEntry fetchFriendlyURLEntry(
		long groupId, long classNameId, String urlTitle) {

		return getService().fetchFriendlyURLEntry(
			groupId, classNameId, urlTitle);
	}

	/**
	 * Returns the friendly url entry matching the UUID and group.
	 *
	 * @param uuid the friendly url entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	public static FriendlyURLEntry fetchFriendlyURLEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchFriendlyURLEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
		fetchFriendlyURLEntryLocalization(
			long groupId, long classNameId, long parentClassPK,
			String urlTitle) {

		return getService().fetchFriendlyURLEntryLocalization(
			groupId, classNameId, parentClassPK, urlTitle);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
		fetchFriendlyURLEntryLocalization(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle) {

		return getService().fetchFriendlyURLEntryLocalization(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
		fetchFriendlyURLEntryLocalization(
			long friendlyURLEntryId, String languageId) {

		return getService().fetchFriendlyURLEntryLocalization(
			friendlyURLEntryId, languageId);
	}

	public static FriendlyURLEntry fetchMainFriendlyURLEntry(
		long classNameId, long classPK) {

		return getService().fetchMainFriendlyURLEntry(classNameId, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	/**
	 * Returns a range of all the friendly url entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.friendly.url.model.impl.FriendlyURLEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @return the range of friendly url entries
	 */
	public static List<FriendlyURLEntry> getFriendlyURLEntries(
		int start, int end) {

		return getService().getFriendlyURLEntries(start, end);
	}

	public static List<FriendlyURLEntry> getFriendlyURLEntries(
		long groupId, long classNameId, long classPK) {

		return getService().getFriendlyURLEntries(
			groupId, classNameId, classPK);
	}

	/**
	 * Returns all the friendly url entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the friendly url entries
	 * @param companyId the primary key of the company
	 * @return the matching friendly url entries, or an empty list if no matches were found
	 */
	public static List<FriendlyURLEntry>
		getFriendlyURLEntriesByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getFriendlyURLEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of friendly url entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the friendly url entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of friendly url entries
	 * @param end the upper bound of the range of friendly url entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching friendly url entries, or an empty list if no matches were found
	 */
	public static List<FriendlyURLEntry>
		getFriendlyURLEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<FriendlyURLEntry> orderByComparator) {

		return getService().getFriendlyURLEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of friendly url entries.
	 *
	 * @return the number of friendly url entries
	 */
	public static int getFriendlyURLEntriesCount() {
		return getService().getFriendlyURLEntriesCount();
	}

	/**
	 * Returns the friendly url entry with the primary key.
	 *
	 * @param friendlyURLEntryId the primary key of the friendly url entry
	 * @return the friendly url entry
	 * @throws PortalException if a friendly url entry with the primary key could not be found
	 */
	public static FriendlyURLEntry getFriendlyURLEntry(long friendlyURLEntryId)
		throws PortalException {

		return getService().getFriendlyURLEntry(friendlyURLEntryId);
	}

	/**
	 * Returns the friendly url entry matching the UUID and group.
	 *
	 * @param uuid the friendly url entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching friendly url entry
	 * @throws PortalException if a matching friendly url entry could not be found
	 */
	public static FriendlyURLEntry getFriendlyURLEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getFriendlyURLEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			getFriendlyURLEntryLocalization(
				long groupId, long classNameId, String urlTitle)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getService().getFriendlyURLEntryLocalization(
			groupId, classNameId, urlTitle);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			getFriendlyURLEntryLocalization(
				long groupId, long classNameId, String languageId,
				String urlTitle)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return getService().getFriendlyURLEntryLocalization(
			groupId, classNameId, languageId, urlTitle);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			getFriendlyURLEntryLocalization(
				long friendlyURLEntryId, String languageId)
		throws PortalException {

		return getService().getFriendlyURLEntryLocalization(
			friendlyURLEntryId, languageId);
	}

	public static List
		<com.liferay.friendly.url.model.FriendlyURLEntryLocalization>
			getFriendlyURLEntryLocalizations(long friendlyURLEntryId) {

		return getService().getFriendlyURLEntryLocalizations(
			friendlyURLEntryId);
	}

	public static List
		<com.liferay.friendly.url.model.FriendlyURLEntryLocalization>
			getFriendlyURLEntryLocalizations(
				long groupId, long classNameId, long classPK, String languageId,
				int start, int end,
				OrderByComparator
					<com.liferay.friendly.url.model.
						FriendlyURLEntryLocalization> orderByComparator) {

		return getService().getFriendlyURLEntryLocalizations(
			groupId, classNameId, classPK, languageId, start, end,
			orderByComparator);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static FriendlyURLEntry getMainFriendlyURLEntry(
			long classNameId, long classPK)
		throws PortalException {

		return getService().getMainFriendlyURLEntry(classNameId, classPK);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static String getUniqueUrlTitle(
		long groupId, long classNameId, long parentClassPK, long classPK,
		String urlTitle, String languageId) {

		return getService().getUniqueUrlTitle(
			groupId, classNameId, parentClassPK, classPK, urlTitle, languageId);
	}

	public static String getUniqueUrlTitle(
		long groupId, long classNameId, long classPK, String urlTitle,
		String languageId) {

		return getService().getUniqueUrlTitle(
			groupId, classNameId, classPK, urlTitle, languageId);
	}

	public static Map<String, String> getUniqueUrlTitleMap(
		long groupId, long classNameId, long parentClassPK, long classPK,
		Map<java.util.Locale, String> titleMap) {

		return getService().getUniqueUrlTitleMap(
			groupId, classNameId, parentClassPK, classPK, titleMap);
	}

	public static void setMainFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry) {

		getService().setMainFriendlyURLEntry(friendlyURLEntry);
	}

	/**
	 * Updates the friendly url entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FriendlyURLEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param friendlyURLEntry the friendly url entry
	 * @return the friendly url entry that was updated
	 */
	public static FriendlyURLEntry updateFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry) {

		return getService().updateFriendlyURLEntry(friendlyURLEntry);
	}

	public static FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long parentClassPK,
			long classPK, String defaultLanguageId,
			Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFriendlyURLEntry(
			friendlyURLEntryId, classNameId, parentClassPK, classPK,
			defaultLanguageId, urlTitleMap, serviceContext);
	}

	public static FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap)
		throws PortalException {

		return getService().updateFriendlyURLEntry(
			friendlyURLEntryId, classNameId, classPK, defaultLanguageId,
			urlTitleMap);
	}

	public static FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long classPK,
			String defaultLanguageId, Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFriendlyURLEntry(
			friendlyURLEntryId, classNameId, classPK, defaultLanguageId,
			urlTitleMap, serviceContext);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			updateFriendlyURLEntryLocalization(
				FriendlyURLEntry friendlyURLEntry, String languageId,
				String urlTitle)
		throws PortalException {

		return getService().updateFriendlyURLEntryLocalization(
			friendlyURLEntry, languageId, urlTitle);
	}

	public static List
		<com.liferay.friendly.url.model.FriendlyURLEntryLocalization>
				updateFriendlyURLEntryLocalizations(
					FriendlyURLEntry friendlyURLEntry,
					Map<String, String> urlTitleMap)
			throws PortalException {

		return getService().updateFriendlyURLEntryLocalizations(
			friendlyURLEntry, urlTitleMap);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
		updateFriendlyURLLocalization(
			com.liferay.friendly.url.model.FriendlyURLEntryLocalization
				friendlyURLEntryLocalization) {

		return getService().updateFriendlyURLLocalization(
			friendlyURLEntryLocalization);
	}

	public static com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			updateFriendlyURLLocalization(
				long friendlyURLLocalizationId, String urlTitle)
		throws PortalException {

		return getService().updateFriendlyURLLocalization(
			friendlyURLLocalizationId, urlTitle);
	}

	public static void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			Map<String, String> urlTitleMap)
		throws PortalException {

		getService().validate(
			groupId, classNameId, parentClassPK, classPK, urlTitleMap);
	}

	public static void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String urlTitle)
		throws PortalException {

		getService().validate(
			groupId, classNameId, parentClassPK, classPK, urlTitle);
	}

	public static void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String languageId, String urlTitle)
		throws PortalException {

		getService().validate(
			groupId, classNameId, parentClassPK, classPK, languageId, urlTitle);
	}

	public static void validate(
			long groupId, long classNameId, long classPK,
			Map<String, String> urlTitleMap)
		throws PortalException {

		getService().validate(groupId, classNameId, classPK, urlTitleMap);
	}

	public static void validate(
			long groupId, long classNameId, long classPK, String urlTitle)
		throws PortalException {

		getService().validate(groupId, classNameId, classPK, urlTitle);
	}

	public static void validate(
			long groupId, long classNameId, long classPK, String languageId,
			String urlTitle)
		throws PortalException {

		getService().validate(
			groupId, classNameId, classPK, languageId, urlTitle);
	}

	public static void validate(long groupId, long classNameId, String urlTitle)
		throws PortalException {

		getService().validate(groupId, classNameId, urlTitle);
	}

	public static FriendlyURLEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FriendlyURLEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			FriendlyURLEntryLocalServiceUtil.class,
			FriendlyURLEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1252061885