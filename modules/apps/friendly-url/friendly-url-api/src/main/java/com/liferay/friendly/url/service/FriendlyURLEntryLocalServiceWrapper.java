/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service;

import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link FriendlyURLEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see FriendlyURLEntryLocalService
 * @generated
 */
public class FriendlyURLEntryLocalServiceWrapper
	implements FriendlyURLEntryLocalService,
			   ServiceWrapper<FriendlyURLEntryLocalService> {

	public FriendlyURLEntryLocalServiceWrapper() {
		this(null);
	}

	public FriendlyURLEntryLocalServiceWrapper(
		FriendlyURLEntryLocalService friendlyURLEntryLocalService) {

		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
	}

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
	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry) {

		return _friendlyURLEntryLocalService.addFriendlyURLEntry(
			friendlyURLEntry);
	}

	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String defaultLanguageId, java.util.Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.addFriendlyURLEntry(
			groupId, classNameId, parentClassPK, classPK, defaultLanguageId,
			urlTitleMap, serviceContext);
	}

	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK,
			java.util.Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.addFriendlyURLEntry(
			groupId, classNameId, classPK, urlTitleMap, serviceContext);
	}

	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK,
			String defaultLanguageId, java.util.Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.addFriendlyURLEntry(
			groupId, classNameId, classPK, defaultLanguageId, urlTitleMap,
			serviceContext);
	}

	@Override
	public FriendlyURLEntry addFriendlyURLEntry(
			long groupId, long classNameId, long classPK, String urlTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.addFriendlyURLEntry(
			groupId, classNameId, classPK, urlTitle, serviceContext);
	}

	/**
	 * Creates a new friendly url entry with the primary key. Does not add the friendly url entry to the database.
	 *
	 * @param friendlyURLEntryId the primary key for the new friendly url entry
	 * @return the new friendly url entry
	 */
	@Override
	public FriendlyURLEntry createFriendlyURLEntry(long friendlyURLEntryId) {
		return _friendlyURLEntryLocalService.createFriendlyURLEntry(
			friendlyURLEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCompanyFriendlyURLEntries(
		long companyId, long classNameId) {

		_friendlyURLEntryLocalService.deleteCompanyFriendlyURLEntries(
			companyId, classNameId);
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
	@Override
	public FriendlyURLEntry deleteFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry) {

		return _friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			friendlyURLEntry);
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
	@Override
	public FriendlyURLEntry deleteFriendlyURLEntry(long friendlyURLEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			friendlyURLEntryId);
	}

	@Override
	public void deleteFriendlyURLEntry(
		long groupId, long classNameId, long classPK) {

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			groupId, classNameId, classPK);
	}

	@Override
	public void deleteFriendlyURLLocalizationEntry(
			com.liferay.friendly.url.model.FriendlyURLEntryLocalization
				friendlyURLEntryLocalization)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryLocalization);
	}

	@Override
	public void deleteFriendlyURLLocalizationEntry(
			long friendlyURLEntryId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryId, languageId);
	}

	@Override
	public void deleteGroupFriendlyURLEntries(long groupId, long classNameId) {
		_friendlyURLEntryLocalService.deleteGroupFriendlyURLEntries(
			groupId, classNameId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _friendlyURLEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _friendlyURLEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _friendlyURLEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _friendlyURLEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _friendlyURLEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _friendlyURLEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _friendlyURLEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _friendlyURLEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public FriendlyURLEntry fetchFriendlyURLEntry(long friendlyURLEntryId) {
		return _friendlyURLEntryLocalService.fetchFriendlyURLEntry(
			friendlyURLEntryId);
	}

	@Override
	public FriendlyURLEntry fetchFriendlyURLEntry(
		long groupId, long classNameId, long parentClassPK, String urlTitle) {

		return _friendlyURLEntryLocalService.fetchFriendlyURLEntry(
			groupId, classNameId, parentClassPK, urlTitle);
	}

	@Override
	public FriendlyURLEntry fetchFriendlyURLEntry(
		long groupId, long classNameId, String urlTitle) {

		return _friendlyURLEntryLocalService.fetchFriendlyURLEntry(
			groupId, classNameId, urlTitle);
	}

	/**
	 * Returns the friendly url entry matching the UUID and group.
	 *
	 * @param uuid the friendly url entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching friendly url entry, or <code>null</code> if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry fetchFriendlyURLEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _friendlyURLEntryLocalService.
			fetchFriendlyURLEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
		fetchFriendlyURLEntryLocalization(
			long groupId, long classNameId, long parentClassPK,
			String urlTitle) {

		return _friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
			groupId, classNameId, parentClassPK, urlTitle);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
		fetchFriendlyURLEntryLocalization(
			long groupId, long classNameId, long parentClassPK,
			String languageId, String urlTitle) {

		return _friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
			groupId, classNameId, parentClassPK, languageId, urlTitle);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
		fetchFriendlyURLEntryLocalization(
			long friendlyURLEntryId, String languageId) {

		return _friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
			friendlyURLEntryId, languageId);
	}

	@Override
	public FriendlyURLEntry fetchMainFriendlyURLEntry(
		long classNameId, long classPK) {

		return _friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
			classNameId, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _friendlyURLEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _friendlyURLEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
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
	@Override
	public java.util.List<FriendlyURLEntry> getFriendlyURLEntries(
		int start, int end) {

		return _friendlyURLEntryLocalService.getFriendlyURLEntries(start, end);
	}

	@Override
	public java.util.List<FriendlyURLEntry> getFriendlyURLEntries(
		long groupId, long classNameId, long classPK) {

		return _friendlyURLEntryLocalService.getFriendlyURLEntries(
			groupId, classNameId, classPK);
	}

	/**
	 * Returns all the friendly url entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the friendly url entries
	 * @param companyId the primary key of the company
	 * @return the matching friendly url entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<FriendlyURLEntry>
		getFriendlyURLEntriesByUuidAndCompanyId(String uuid, long companyId) {

		return _friendlyURLEntryLocalService.
			getFriendlyURLEntriesByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<FriendlyURLEntry>
		getFriendlyURLEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FriendlyURLEntry>
				orderByComparator) {

		return _friendlyURLEntryLocalService.
			getFriendlyURLEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of friendly url entries.
	 *
	 * @return the number of friendly url entries
	 */
	@Override
	public int getFriendlyURLEntriesCount() {
		return _friendlyURLEntryLocalService.getFriendlyURLEntriesCount();
	}

	/**
	 * Returns the friendly url entry with the primary key.
	 *
	 * @param friendlyURLEntryId the primary key of the friendly url entry
	 * @return the friendly url entry
	 * @throws PortalException if a friendly url entry with the primary key could not be found
	 */
	@Override
	public FriendlyURLEntry getFriendlyURLEntry(long friendlyURLEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.getFriendlyURLEntry(
			friendlyURLEntryId);
	}

	/**
	 * Returns the friendly url entry matching the UUID and group.
	 *
	 * @param uuid the friendly url entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching friendly url entry
	 * @throws PortalException if a matching friendly url entry could not be found
	 */
	@Override
	public FriendlyURLEntry getFriendlyURLEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.
			getFriendlyURLEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			getFriendlyURLEntryLocalization(
				long groupId, long classNameId, String urlTitle)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return _friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
			groupId, classNameId, urlTitle);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			getFriendlyURLEntryLocalization(
				long groupId, long classNameId, String languageId,
				String urlTitle)
		throws com.liferay.friendly.url.exception.
			NoSuchFriendlyURLEntryLocalizationException {

		return _friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
			groupId, classNameId, languageId, urlTitle);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			getFriendlyURLEntryLocalization(
				long friendlyURLEntryId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
			friendlyURLEntryId, languageId);
	}

	@Override
	public java.util.List
		<com.liferay.friendly.url.model.FriendlyURLEntryLocalization>
			getFriendlyURLEntryLocalizations(long friendlyURLEntryId) {

		return _friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
			friendlyURLEntryId);
	}

	@Override
	public java.util.List
		<com.liferay.friendly.url.model.FriendlyURLEntryLocalization>
			getFriendlyURLEntryLocalizations(
				long groupId, long classNameId, long classPK, String languageId,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.friendly.url.model.
						FriendlyURLEntryLocalization> orderByComparator) {

		return _friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
			groupId, classNameId, classPK, languageId, start, end,
			orderByComparator);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _friendlyURLEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public FriendlyURLEntry getMainFriendlyURLEntry(
			long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.getMainFriendlyURLEntry(
			classNameId, classPK);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _friendlyURLEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public String getUniqueUrlTitle(
		long groupId, long classNameId, long parentClassPK, long classPK,
		String urlTitle, String languageId) {

		return _friendlyURLEntryLocalService.getUniqueUrlTitle(
			groupId, classNameId, parentClassPK, classPK, urlTitle, languageId);
	}

	@Override
	public String getUniqueUrlTitle(
		long groupId, long classNameId, long classPK, String urlTitle,
		String languageId) {

		return _friendlyURLEntryLocalService.getUniqueUrlTitle(
			groupId, classNameId, classPK, urlTitle, languageId);
	}

	@Override
	public java.util.Map<String, String> getUniqueUrlTitleMap(
		long groupId, long classNameId, long parentClassPK, long classPK,
		java.util.Map<java.util.Locale, String> titleMap) {

		return _friendlyURLEntryLocalService.getUniqueUrlTitleMap(
			groupId, classNameId, parentClassPK, classPK, titleMap);
	}

	@Override
	public void setMainFriendlyURLEntry(FriendlyURLEntry friendlyURLEntry) {
		_friendlyURLEntryLocalService.setMainFriendlyURLEntry(friendlyURLEntry);
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
	@Override
	public FriendlyURLEntry updateFriendlyURLEntry(
		FriendlyURLEntry friendlyURLEntry) {

		return _friendlyURLEntryLocalService.updateFriendlyURLEntry(
			friendlyURLEntry);
	}

	@Override
	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long parentClassPK,
			long classPK, String defaultLanguageId,
			java.util.Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.updateFriendlyURLEntry(
			friendlyURLEntryId, classNameId, parentClassPK, classPK,
			defaultLanguageId, urlTitleMap, serviceContext);
	}

	@Override
	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long classPK,
			String defaultLanguageId, java.util.Map<String, String> urlTitleMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.updateFriendlyURLEntry(
			friendlyURLEntryId, classNameId, classPK, defaultLanguageId,
			urlTitleMap);
	}

	@Override
	public FriendlyURLEntry updateFriendlyURLEntry(
			long friendlyURLEntryId, long classNameId, long classPK,
			String defaultLanguageId, java.util.Map<String, String> urlTitleMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.updateFriendlyURLEntry(
			friendlyURLEntryId, classNameId, classPK, defaultLanguageId,
			urlTitleMap, serviceContext);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			updateFriendlyURLEntryLocalization(
				FriendlyURLEntry friendlyURLEntry, String languageId,
				String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.updateFriendlyURLEntryLocalization(
			friendlyURLEntry, languageId, urlTitle);
	}

	@Override
	public java.util.List
		<com.liferay.friendly.url.model.FriendlyURLEntryLocalization>
				updateFriendlyURLEntryLocalizations(
					FriendlyURLEntry friendlyURLEntry,
					java.util.Map<String, String> urlTitleMap)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.
			updateFriendlyURLEntryLocalizations(friendlyURLEntry, urlTitleMap);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
		updateFriendlyURLLocalization(
			com.liferay.friendly.url.model.FriendlyURLEntryLocalization
				friendlyURLEntryLocalization) {

		return _friendlyURLEntryLocalService.updateFriendlyURLLocalization(
			friendlyURLEntryLocalization);
	}

	@Override
	public com.liferay.friendly.url.model.FriendlyURLEntryLocalization
			updateFriendlyURLLocalization(
				long friendlyURLLocalizationId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _friendlyURLEntryLocalService.updateFriendlyURLLocalization(
			friendlyURLLocalizationId, urlTitle);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			java.util.Map<String, String> urlTitleMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.validate(
			groupId, classNameId, parentClassPK, classPK, urlTitleMap);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.validate(
			groupId, classNameId, parentClassPK, classPK, urlTitle);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long parentClassPK, long classPK,
			String languageId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.validate(
			groupId, classNameId, parentClassPK, classPK, languageId, urlTitle);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long classPK,
			java.util.Map<String, String> urlTitleMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.validate(
			groupId, classNameId, classPK, urlTitleMap);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long classPK, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.validate(
			groupId, classNameId, classPK, urlTitle);
	}

	@Override
	public void validate(
			long groupId, long classNameId, long classPK, String languageId,
			String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.validate(
			groupId, classNameId, classPK, languageId, urlTitle);
	}

	@Override
	public void validate(long groupId, long classNameId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		_friendlyURLEntryLocalService.validate(groupId, classNameId, urlTitle);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _friendlyURLEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<FriendlyURLEntry> getCTPersistence() {
		return _friendlyURLEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<FriendlyURLEntry> getModelClass() {
		return _friendlyURLEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<FriendlyURLEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _friendlyURLEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public FriendlyURLEntryLocalService getWrappedService() {
		return _friendlyURLEntryLocalService;
	}

	@Override
	public void setWrappedService(
		FriendlyURLEntryLocalService friendlyURLEntryLocalService) {

		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
	}

	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:89411744