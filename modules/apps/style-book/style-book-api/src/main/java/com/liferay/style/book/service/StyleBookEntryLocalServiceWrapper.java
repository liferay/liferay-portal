/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.style.book.model.StyleBookEntry;

/**
 * Provides a wrapper for {@link StyleBookEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookEntryLocalService
 * @generated
 */
public class StyleBookEntryLocalServiceWrapper
	implements ServiceWrapper<StyleBookEntryLocalService>,
			   StyleBookEntryLocalService {

	public StyleBookEntryLocalServiceWrapper() {
		this(null);
	}

	public StyleBookEntryLocalServiceWrapper(
		StyleBookEntryLocalService styleBookEntryLocalService) {

		_styleBookEntryLocalService = styleBookEntryLocalService;
	}

	@Override
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long userId, long groupId,
			boolean defaultStyleBookEntry, String frontendTokensValues,
			String name, String styleBookEntryKey, String themeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.addStyleBookEntry(
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
	@Override
	public StyleBookEntry addStyleBookEntry(StyleBookEntry styleBookEntry) {
		return _styleBookEntryLocalService.addStyleBookEntry(styleBookEntry);
	}

	@Override
	public StyleBookEntry checkout(
			StyleBookEntry publishedStyleBookEntry, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.checkout(
			publishedStyleBookEntry, version);
	}

	@Override
	public StyleBookEntry copyStyleBookEntry(
			long userId, long groupId, long sourceStyleBookEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.copyStyleBookEntry(
			userId, groupId, sourceStyleBookEntryId, serviceContext);
	}

	/**
	 * Creates a new style book entry. Does not add the style book entry to the database.
	 *
	 * @return the new style book entry
	 */
	@Override
	public StyleBookEntry create() {
		return _styleBookEntryLocalService.create();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public StyleBookEntry delete(StyleBookEntry publishedStyleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.delete(publishedStyleBookEntry);
	}

	@Override
	public StyleBookEntry deleteDraft(StyleBookEntry draftStyleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.deleteDraft(draftStyleBookEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteStyleBookEntries(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_styleBookEntryLocalService.deleteStyleBookEntries(groupId);
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
	@Override
	public StyleBookEntry deleteStyleBookEntry(long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.deleteStyleBookEntry(
			styleBookEntryId);
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.deleteStyleBookEntry(
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
	@Override
	public StyleBookEntry deleteStyleBookEntry(StyleBookEntry styleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.deleteStyleBookEntry(styleBookEntry);
	}

	@Override
	public com.liferay.style.book.model.StyleBookEntryVersion deleteVersion(
			com.liferay.style.book.model.StyleBookEntryVersion
				styleBookEntryVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.deleteVersion(styleBookEntryVersion);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _styleBookEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _styleBookEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _styleBookEntryLocalService.dynamicQuery();
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

		return _styleBookEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _styleBookEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _styleBookEntryLocalService.dynamicQuery(
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

		return _styleBookEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _styleBookEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public StyleBookEntry fetchDefaultStyleBookEntry(
		long groupId, String themeId) {

		return _styleBookEntryLocalService.fetchDefaultStyleBookEntry(
			groupId, themeId);
	}

	@Override
	public StyleBookEntry fetchDraft(long primaryKey) {
		return _styleBookEntryLocalService.fetchDraft(primaryKey);
	}

	@Override
	public StyleBookEntry fetchDraft(StyleBookEntry styleBookEntry) {
		return _styleBookEntryLocalService.fetchDraft(styleBookEntry);
	}

	@Override
	public com.liferay.style.book.model.StyleBookEntryVersion
		fetchLatestVersion(StyleBookEntry styleBookEntry) {

		return _styleBookEntryLocalService.fetchLatestVersion(styleBookEntry);
	}

	@Override
	public StyleBookEntry fetchPublished(long primaryKey) {
		return _styleBookEntryLocalService.fetchPublished(primaryKey);
	}

	@Override
	public StyleBookEntry fetchPublished(StyleBookEntry styleBookEntry) {
		return _styleBookEntryLocalService.fetchPublished(styleBookEntry);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntry(long styleBookEntryId) {
		return _styleBookEntryLocalService.fetchStyleBookEntry(
			styleBookEntryId);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntry(
		long groupId, String styleBookEntryKey) {

		return _styleBookEntryLocalService.fetchStyleBookEntry(
			groupId, styleBookEntryKey);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _styleBookEntryLocalService.
			fetchStyleBookEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId, boolean head) {

		return _styleBookEntryLocalService.
			fetchStyleBookEntryByExternalReferenceCode(
				externalReferenceCode, groupId, head);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _styleBookEntryLocalService.fetchStyleBookEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public String generateStyleBookEntryKey(long groupId, String name) {
		return _styleBookEntryLocalService.generateStyleBookEntryKey(
			groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _styleBookEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public StyleBookEntry getDraft(long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.getDraft(primaryKey);
	}

	@Override
	public StyleBookEntry getDraft(StyleBookEntry styleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.getDraft(styleBookEntry);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _styleBookEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _styleBookEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _styleBookEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public java.util.List<StyleBookEntry> getStyleBookEntries(
		int start, int end) {

		return _styleBookEntryLocalService.getStyleBookEntries(start, end);
	}

	@Override
	public java.util.List<StyleBookEntry> getStyleBookEntries(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntry>
			orderByComparator) {

		return _styleBookEntryLocalService.getStyleBookEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<StyleBookEntry> getStyleBookEntries(
		long groupId, String themeId) {

		return _styleBookEntryLocalService.getStyleBookEntries(
			groupId, themeId);
	}

	@Override
	public java.util.List<StyleBookEntry> getStyleBookEntries(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookEntry>
			orderByComparator) {

		return _styleBookEntryLocalService.getStyleBookEntries(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public java.util.List<StyleBookEntry> getStyleBookEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _styleBookEntryLocalService.
			getStyleBookEntriesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns the number of style book entries.
	 *
	 * @return the number of style book entries
	 */
	@Override
	public int getStyleBookEntriesCount() {
		return _styleBookEntryLocalService.getStyleBookEntriesCount();
	}

	@Override
	public int getStyleBookEntriesCount(long groupId) {
		return _styleBookEntryLocalService.getStyleBookEntriesCount(groupId);
	}

	@Override
	public int getStyleBookEntriesCount(long groupId, String name) {
		return _styleBookEntryLocalService.getStyleBookEntriesCount(
			groupId, name);
	}

	/**
	 * Returns the style book entry with the primary key.
	 *
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the style book entry
	 * @throws PortalException if a style book entry with the primary key could not be found
	 */
	@Override
	public StyleBookEntry getStyleBookEntry(long styleBookEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.getStyleBookEntry(styleBookEntryId);
	}

	@Override
	public StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.
			getStyleBookEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public StyleBookEntry getStyleBookEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.
			getStyleBookEntryByExternalReferenceCode(
				externalReferenceCode, groupId, head);
	}

	@Override
	public com.liferay.style.book.model.StyleBookEntryVersion getVersion(
			StyleBookEntry styleBookEntry, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.getVersion(styleBookEntry, version);
	}

	@Override
	public java.util.List<com.liferay.style.book.model.StyleBookEntryVersion>
		getVersions(StyleBookEntry styleBookEntry) {

		return _styleBookEntryLocalService.getVersions(styleBookEntry);
	}

	@Override
	public StyleBookEntry publishDraft(StyleBookEntry draftStyleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.publishDraft(draftStyleBookEntry);
	}

	@Override
	public void registerListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<StyleBookEntry, com.liferay.style.book.model.StyleBookEntryVersion>
				versionServiceListener) {

		_styleBookEntryLocalService.registerListener(versionServiceListener);
	}

	@Override
	public void unregisterListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<StyleBookEntry, com.liferay.style.book.model.StyleBookEntryVersion>
				versionServiceListener) {

		_styleBookEntryLocalService.unregisterListener(versionServiceListener);
	}

	@Override
	public StyleBookEntry updateDefaultStyleBookEntry(
			long styleBookEntryId, boolean defaultStyleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntryId, defaultStyleBookEntry);
	}

	@Override
	public StyleBookEntry updateDraft(StyleBookEntry draftStyleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.updateDraft(draftStyleBookEntry);
	}

	@Override
	public StyleBookEntry updateFrontendTokensValues(
			long styleBookEntryId, String frontendTokensValues)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.updateFrontendTokensValues(
			styleBookEntryId, frontendTokensValues);
	}

	@Override
	public StyleBookEntry updateName(long styleBookEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.updateName(styleBookEntryId, name);
	}

	@Override
	public StyleBookEntry updatePreviewFileEntryId(
			long styleBookEntryId, long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.updatePreviewFileEntryId(
			styleBookEntryId, previewFileEntryId);
	}

	@Override
	public StyleBookEntry updateStyleBookEntry(
			long userId, long styleBookEntryId, boolean defaultStylebookEntry,
			String frontendTokensValues, String name, String styleBookEntryKey,
			long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.updateStyleBookEntry(
			userId, styleBookEntryId, defaultStylebookEntry,
			frontendTokensValues, name, styleBookEntryKey, previewFileEntryId);
	}

	@Override
	public StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, String frontendTokensValues, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.updateStyleBookEntry(
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
	@Override
	public StyleBookEntry updateStyleBookEntry(
			StyleBookEntry draftStyleBookEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookEntryLocalService.updateStyleBookEntry(
			draftStyleBookEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _styleBookEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<StyleBookEntry> getCTPersistence() {
		return _styleBookEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<StyleBookEntry> getModelClass() {
		return _styleBookEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<StyleBookEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _styleBookEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public StyleBookEntryLocalService getWrappedService() {
		return _styleBookEntryLocalService;
	}

	@Override
	public void setWrappedService(
		StyleBookEntryLocalService styleBookEntryLocalService) {

		_styleBookEntryLocalService = styleBookEntryLocalService;
	}

	private StyleBookEntryLocalService _styleBookEntryLocalService;

}