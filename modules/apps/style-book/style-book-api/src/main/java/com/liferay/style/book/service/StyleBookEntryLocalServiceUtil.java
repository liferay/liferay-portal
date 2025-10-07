/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.style.book.model.StyleBookEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for StyleBookEntry. This utility wraps
 * <code>com.liferay.style.book.service.impl.StyleBookEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryLocalService
 * @generated
 */
public class StyleBookEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.style.book.service.impl.StyleBookEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long userId, long groupId,
			boolean defaultStyleBookEntry, String frontendTokensValues,
			String name, String styleBookEntryKey, String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addStyleBookEntry(
			externalReferenceCode, userId, groupId, defaultStyleBookEntry,
			frontendTokensValues, name, styleBookEntryKey, themeId,
			serviceContext);
	}

	/**
	 * Adds the style book entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookEntry the style book entry
	 * @return the style book entry that was added
	 */
	public static StyleBookEntry addStyleBookEntry(
		StyleBookEntry styleBookEntry) {

		return getService().addStyleBookEntry(styleBookEntry);
	}

	public static StyleBookEntry checkout(
			StyleBookEntry publishedStyleBookEntry, int version)
		throws PortalException {

		return getService().checkout(publishedStyleBookEntry, version);
	}

	public static StyleBookEntry copyStyleBookEntry(
			long userId, long groupId, long sourceStyleBookEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyStyleBookEntry(
			userId, groupId, sourceStyleBookEntryId, serviceContext);
	}

	/**
	 * Creates a new style book entry. Does not add the style book entry to the database.
	 *
	 * @return the new style book entry
	 */
	public static StyleBookEntry create() {
		return getService().create();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static StyleBookEntry delete(StyleBookEntry publishedStyleBookEntry)
		throws PortalException {

		return getService().delete(publishedStyleBookEntry);
	}

	public static StyleBookEntry deleteDraft(StyleBookEntry draftStyleBookEntry)
		throws PortalException {

		return getService().deleteDraft(draftStyleBookEntry);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteStyleBookEntries(long groupId)
		throws PortalException {

		getService().deleteStyleBookEntries(groupId);
	}

	/**
	 * Deletes the style book entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the style book entry that was removed
	 * @throws PortalException if a style book entry with the primary key could not be found
	 */
	public static StyleBookEntry deleteStyleBookEntry(long styleBookEntryId)
		throws PortalException {

		return getService().deleteStyleBookEntry(styleBookEntryId);
	}

	public static StyleBookEntry deleteStyleBookEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteStyleBookEntry(
			externalReferenceCode, groupId);
	}

	/**
	 * Deletes the style book entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookEntry the style book entry
	 * @return the style book entry that was removed
	 * @throws PortalException
	 */
	public static StyleBookEntry deleteStyleBookEntry(
			StyleBookEntry styleBookEntry)
		throws PortalException {

		return getService().deleteStyleBookEntry(styleBookEntry);
	}

	public static com.liferay.style.book.model.StyleBookEntryVersion
			deleteVersion(
				com.liferay.style.book.model.StyleBookEntryVersion
					styleBookEntryVersion)
		throws PortalException {

		return getService().deleteVersion(styleBookEntryVersion);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryModelImpl</code>.
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

	public static StyleBookEntry fetchDefaultStyleBookEntry(
		long groupId, String themeId) {

		return getService().fetchDefaultStyleBookEntry(groupId, themeId);
	}

	public static StyleBookEntry fetchDraft(long primaryKey) {
		return getService().fetchDraft(primaryKey);
	}

	public static StyleBookEntry fetchDraft(StyleBookEntry styleBookEntry) {
		return getService().fetchDraft(styleBookEntry);
	}

	public static com.liferay.style.book.model.StyleBookEntryVersion
		fetchLatestVersion(StyleBookEntry styleBookEntry) {

		return getService().fetchLatestVersion(styleBookEntry);
	}

	public static StyleBookEntry fetchPublished(long primaryKey) {
		return getService().fetchPublished(primaryKey);
	}

	public static StyleBookEntry fetchPublished(StyleBookEntry styleBookEntry) {
		return getService().fetchPublished(styleBookEntry);
	}

	public static StyleBookEntry fetchStyleBookEntry(long styleBookEntryId) {
		return getService().fetchStyleBookEntry(styleBookEntryId);
	}

	public static StyleBookEntry fetchStyleBookEntry(
		long groupId, String styleBookEntryKey) {

		return getService().fetchStyleBookEntry(groupId, styleBookEntryKey);
	}

	public static StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchStyleBookEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId, boolean head) {

		return getService().fetchStyleBookEntryByExternalReferenceCode(
			externalReferenceCode, groupId, head);
	}

	public static StyleBookEntry fetchStyleBookEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchStyleBookEntryByUuidAndGroupId(uuid, groupId);
	}

	public static String generateStyleBookEntryKey(long groupId, String name) {
		return getService().generateStyleBookEntryKey(groupId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static StyleBookEntry getDraft(long primaryKey)
		throws PortalException {

		return getService().getDraft(primaryKey);
	}

	public static StyleBookEntry getDraft(StyleBookEntry styleBookEntry)
		throws PortalException {

		return getService().getDraft(styleBookEntry);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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

	/**
	 * Returns a range of all the style book entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of style book entries
	 * @param end the upper bound of the range of style book entries (not inclusive)
	 * @return the range of style book entries
	 */
	public static List<StyleBookEntry> getStyleBookEntries(int start, int end) {
		return getService().getStyleBookEntries(start, end);
	}

	public static List<StyleBookEntry> getStyleBookEntries(
		long groupId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return getService().getStyleBookEntries(
			groupId, start, end, orderByComparator);
	}

	public static List<StyleBookEntry> getStyleBookEntries(
		long groupId, String themeId) {

		return getService().getStyleBookEntries(groupId, themeId);
	}

	public static List<StyleBookEntry> getStyleBookEntries(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return getService().getStyleBookEntries(
			groupId, name, start, end, orderByComparator);
	}

	public static List<StyleBookEntry> getStyleBookEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getStyleBookEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns the number of style book entries.
	 *
	 * @return the number of style book entries
	 */
	public static int getStyleBookEntriesCount() {
		return getService().getStyleBookEntriesCount();
	}

	public static int getStyleBookEntriesCount(long groupId) {
		return getService().getStyleBookEntriesCount(groupId);
	}

	public static int getStyleBookEntriesCount(long groupId, String name) {
		return getService().getStyleBookEntriesCount(groupId, name);
	}

	/**
	 * Returns the style book entry with the primary key.
	 *
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the style book entry
	 * @throws PortalException if a style book entry with the primary key could not be found
	 */
	public static StyleBookEntry getStyleBookEntry(long styleBookEntryId)
		throws PortalException {

		return getService().getStyleBookEntry(styleBookEntryId);
	}

	public static StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getStyleBookEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head)
		throws PortalException {

		return getService().getStyleBookEntryByExternalReferenceCode(
			externalReferenceCode, groupId, head);
	}

	public static com.liferay.style.book.model.StyleBookEntryVersion getVersion(
			StyleBookEntry styleBookEntry, int version)
		throws PortalException {

		return getService().getVersion(styleBookEntry, version);
	}

	public static List<com.liferay.style.book.model.StyleBookEntryVersion>
		getVersions(StyleBookEntry styleBookEntry) {

		return getService().getVersions(styleBookEntry);
	}

	public static StyleBookEntry publishDraft(
			StyleBookEntry draftStyleBookEntry)
		throws PortalException {

		return getService().publishDraft(draftStyleBookEntry);
	}

	public static void registerListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<StyleBookEntry, com.liferay.style.book.model.StyleBookEntryVersion>
				versionServiceListener) {

		getService().registerListener(versionServiceListener);
	}

	public static void unregisterListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<StyleBookEntry, com.liferay.style.book.model.StyleBookEntryVersion>
				versionServiceListener) {

		getService().unregisterListener(versionServiceListener);
	}

	public static StyleBookEntry updateDefaultStyleBookEntry(
			long styleBookEntryId, boolean defaultStyleBookEntry)
		throws PortalException {

		return getService().updateDefaultStyleBookEntry(
			styleBookEntryId, defaultStyleBookEntry);
	}

	public static StyleBookEntry updateDraft(StyleBookEntry draftStyleBookEntry)
		throws PortalException {

		return getService().updateDraft(draftStyleBookEntry);
	}

	public static StyleBookEntry updateFrontendTokensValues(
			long styleBookEntryId, String frontendTokensValues)
		throws PortalException {

		return getService().updateFrontendTokensValues(
			styleBookEntryId, frontendTokensValues);
	}

	public static StyleBookEntry updateName(long styleBookEntryId, String name)
		throws PortalException {

		return getService().updateName(styleBookEntryId, name);
	}

	public static StyleBookEntry updatePreviewFileEntryId(
			long styleBookEntryId, long previewFileEntryId)
		throws PortalException {

		return getService().updatePreviewFileEntryId(
			styleBookEntryId, previewFileEntryId);
	}

	public static StyleBookEntry updateStyleBookEntry(
			long userId, long styleBookEntryId, boolean defaultStylebookEntry,
			String frontendTokensValues, String name, String styleBookEntryKey,
			long previewFileEntryId)
		throws PortalException {

		return getService().updateStyleBookEntry(
			userId, styleBookEntryId, defaultStylebookEntry,
			frontendTokensValues, name, styleBookEntryKey, previewFileEntryId);
	}

	public static StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, String frontendTokensValues, String name)
		throws PortalException {

		return getService().updateStyleBookEntry(
			styleBookEntryId, frontendTokensValues, name);
	}

	/**
	 * Updates the style book entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param draftStyleBookEntry the style book entry
	 * @return the style book entry that was updated
	 */
	public static StyleBookEntry updateStyleBookEntry(
			StyleBookEntry draftStyleBookEntry)
		throws PortalException {

		return getService().updateStyleBookEntry(draftStyleBookEntry);
	}

	public static StyleBookEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<StyleBookEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			StyleBookEntryLocalServiceUtil.class,
			StyleBookEntryLocalService.class);

}