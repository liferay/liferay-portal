/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link FragmentEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLocalService
 * @generated
 */
public class FragmentEntryLocalServiceWrapper
	implements FragmentEntryLocalService,
			   ServiceWrapper<FragmentEntryLocalService> {

	public FragmentEntryLocalServiceWrapper() {
		this(null);
	}

	public FragmentEntryLocalServiceWrapper(
		FragmentEntryLocalService fragmentEntryLocalService) {

		_fragmentEntryLocalService = fragmentEntryLocalService;
	}

	/**
	 * Adds the fragment entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntry the fragment entry
	 * @return the fragment entry that was added
	 */
	@Override
	public FragmentEntry addFragmentEntry(FragmentEntry fragmentEntry) {
		return _fragmentEntryLocalService.addFragmentEntry(fragmentEntry);
	}

	@Override
	public FragmentEntry addFragmentEntry(
			String externalReferenceCode, long userId, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.addFragmentEntry(
			externalReferenceCode, userId, groupId, fragmentCollectionId,
			fragmentEntryKey, name, css, html, js, cacheable, configuration,
			icon, previewFileEntryId, marketplace, readOnly, type, typeOptions,
			status, serviceContext);
	}

	@Override
	public FragmentEntry checkout(
			FragmentEntry publishedFragmentEntry, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.checkout(
			publishedFragmentEntry, version);
	}

	@Override
	public FragmentEntry copyFragmentEntry(
			long userId, long groupId, long sourceFragmentEntryId,
			long fragmentCollectionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.copyFragmentEntry(
			userId, groupId, sourceFragmentEntryId, fragmentCollectionId,
			serviceContext);
	}

	/**
	 * Creates a new fragment entry. Does not add the fragment entry to the database.
	 *
	 * @return the new fragment entry
	 */
	@Override
	public FragmentEntry create() {
		return _fragmentEntryLocalService.create();
	}

	@Override
	public FragmentEntry createFragmentEntry(long fragmentEntryId) {
		return _fragmentEntryLocalService.createFragmentEntry(fragmentEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public FragmentEntry delete(FragmentEntry publishedFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.delete(publishedFragmentEntry);
	}

	@Override
	public FragmentEntry deleteDraft(FragmentEntry draftFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.deleteDraft(draftFragmentEntry);
	}

	/**
	 * Deletes the fragment entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntry the fragment entry
	 * @return the fragment entry that was removed
	 * @throws PortalException
	 */
	@Override
	public FragmentEntry deleteFragmentEntry(FragmentEntry fragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);
	}

	/**
	 * Deletes the fragment entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntryId the primary key of the fragment entry
	 * @return the fragment entry that was removed
	 * @throws PortalException if a fragment entry with the primary key could not be found
	 */
	@Override
	public FragmentEntry deleteFragmentEntry(long fragmentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.deleteFragmentEntry(fragmentEntryId);
	}

	@Override
	public FragmentEntry deleteFragmentEntry(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.deleteFragmentEntry(
			externalReferenceCode, groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.fragment.model.FragmentEntryVersion deleteVersion(
			com.liferay.fragment.model.FragmentEntryVersion
				fragmentEntryVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.deleteVersion(fragmentEntryVersion);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _fragmentEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _fragmentEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _fragmentEntryLocalService.dynamicQuery();
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

		return _fragmentEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
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

		return _fragmentEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
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

		return _fragmentEntryLocalService.dynamicQuery(
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

		return _fragmentEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _fragmentEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public FragmentEntry fetchDraft(FragmentEntry fragmentEntry) {
		return _fragmentEntryLocalService.fetchDraft(fragmentEntry);
	}

	@Override
	public FragmentEntry fetchDraft(long primaryKey) {
		return _fragmentEntryLocalService.fetchDraft(primaryKey);
	}

	@Override
	public FragmentEntry fetchFragmentEntry(long fragmentEntryId) {
		return _fragmentEntryLocalService.fetchFragmentEntry(fragmentEntryId);
	}

	@Override
	public FragmentEntry fetchFragmentEntry(
		long groupId, String fragmentEntryKey) {

		return _fragmentEntryLocalService.fetchFragmentEntry(
			groupId, fragmentEntryKey);
	}

	@Override
	public FragmentEntry fetchFragmentEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _fragmentEntryLocalService.
			fetchFragmentEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public FragmentEntry fetchFragmentEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId, boolean head) {

		return _fragmentEntryLocalService.
			fetchFragmentEntryByExternalReferenceCode(
				externalReferenceCode, groupId, head);
	}

	@Override
	public FragmentEntry fetchFragmentEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _fragmentEntryLocalService.fetchFragmentEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.fragment.model.FragmentEntryVersion fetchLatestVersion(
		FragmentEntry fragmentEntry) {

		return _fragmentEntryLocalService.fetchLatestVersion(fragmentEntry);
	}

	@Override
	public FragmentEntry fetchPublished(FragmentEntry fragmentEntry) {
		return _fragmentEntryLocalService.fetchPublished(fragmentEntry);
	}

	@Override
	public FragmentEntry fetchPublished(long primaryKey) {
		return _fragmentEntryLocalService.fetchPublished(primaryKey);
	}

	@Override
	public String generateFragmentEntryKey(long groupId, String name) {
		return _fragmentEntryLocalService.generateFragmentEntryKey(
			groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _fragmentEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public FragmentEntry getDraft(FragmentEntry fragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.getDraft(fragmentEntry);
	}

	@Override
	public FragmentEntry getDraft(long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.getDraft(primaryKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _fragmentEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	/**
	 * Returns a range of all the fragment entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of fragment entries
	 */
	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
		int start, int end) {

		return _fragmentEntryLocalService.getFragmentEntries(start, end);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
		long fragmentCollectionId) {

		return _fragmentEntryLocalService.getFragmentEntries(
			fragmentCollectionId);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
		long fragmentCollectionId, int start, int end) {

		return _fragmentEntryLocalService.getFragmentEntries(
			fragmentCollectionId, start, end);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int status) {

		return _fragmentEntryLocalService.getFragmentEntries(
			groupId, fragmentCollectionId, status);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
			orderByComparator) {

		return _fragmentEntryLocalService.getFragmentEntries(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
			orderByComparator) {

		return _fragmentEntryLocalService.getFragmentEntries(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
			orderByComparator) {

		return _fragmentEntryLocalService.getFragmentEntries(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
			orderByComparator) {

		return _fragmentEntryLocalService.getFragmentEntries(
			groupId, fragmentCollectionId, name, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _fragmentEntryLocalService.getFragmentEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public java.util.List<FragmentEntry> getFragmentEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntry>
			orderByComparator) {

		return _fragmentEntryLocalService.getFragmentEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of fragment entries.
	 *
	 * @return the number of fragment entries
	 */
	@Override
	public int getFragmentEntriesCount() {
		return _fragmentEntryLocalService.getFragmentEntriesCount();
	}

	@Override
	public int getFragmentEntriesCount(long fragmentCollectionId) {
		return _fragmentEntryLocalService.getFragmentEntriesCount(
			fragmentCollectionId);
	}

	/**
	 * Returns the fragment entry with the primary key.
	 *
	 * @param fragmentEntryId the primary key of the fragment entry
	 * @return the fragment entry
	 * @throws PortalException if a fragment entry with the primary key could not be found
	 */
	@Override
	public FragmentEntry getFragmentEntry(long fragmentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.getFragmentEntry(fragmentEntryId);
	}

	@Override
	public FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.
			getFragmentEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.
			getFragmentEntryByExternalReferenceCode(
				externalReferenceCode, groupId, head);
	}

	@Override
	public FragmentEntry getFragmentEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.getFragmentEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _fragmentEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _fragmentEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public String[] getTempFileNames(
			long userId, long groupId, String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.getTempFileNames(
			userId, groupId, folderName);
	}

	@Override
	public String getUniqueFragmentEntryName(
		long groupId, long fragmentCollectionId, String name) {

		return _fragmentEntryLocalService.getUniqueFragmentEntryName(
			groupId, fragmentCollectionId, name);
	}

	@Override
	public com.liferay.fragment.model.FragmentEntryVersion getVersion(
			FragmentEntry fragmentEntry, int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.getVersion(fragmentEntry, version);
	}

	@Override
	public java.util.List<com.liferay.fragment.model.FragmentEntryVersion>
		getVersions(FragmentEntry fragmentEntry) {

		return _fragmentEntryLocalService.getVersions(fragmentEntry);
	}

	@Override
	public FragmentEntry moveFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.moveFragmentEntry(
			fragmentEntryId, fragmentCollectionId);
	}

	@Override
	public FragmentEntry publishDraft(FragmentEntry draftFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.publishDraft(draftFragmentEntry);
	}

	@Override
	public void registerListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<FragmentEntry, com.liferay.fragment.model.FragmentEntryVersion>
				versionServiceListener) {

		_fragmentEntryLocalService.registerListener(versionServiceListener);
	}

	@Override
	public void unregisterListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<FragmentEntry, com.liferay.fragment.model.FragmentEntryVersion>
				versionServiceListener) {

		_fragmentEntryLocalService.unregisterListener(versionServiceListener);
	}

	@Override
	public FragmentEntry updateDraft(FragmentEntry draftFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.updateDraft(draftFragmentEntry);
	}

	/**
	 * Updates the fragment entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param draftFragmentEntry the fragment entry
	 * @return the fragment entry that was updated
	 * @throws PortalException
	 */
	@Override
	public FragmentEntry updateFragmentEntry(FragmentEntry draftFragmentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.updateFragmentEntry(
			draftFragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, boolean cacheable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntryId, cacheable);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntryId, previewFileEntryId);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long userId, long fragmentEntryId, long fragmentCollectionId,
			String name, String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean readOnly, String typeOptions, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.updateFragmentEntry(
			userId, fragmentEntryId, fragmentCollectionId, name, css, html, js,
			cacheable, configuration, icon, previewFileEntryId, readOnly,
			typeOptions, status);
	}

	@Override
	public FragmentEntry updateFragmentEntry(long fragmentEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntryId, name);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _fragmentEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<FragmentEntry> getCTPersistence() {
		return _fragmentEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<FragmentEntry> getModelClass() {
		return _fragmentEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<FragmentEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _fragmentEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public FragmentEntryLocalService getWrappedService() {
		return _fragmentEntryLocalService;
	}

	@Override
	public void setWrappedService(
		FragmentEntryLocalService fragmentEntryLocalService) {

		_fragmentEntryLocalService = fragmentEntryLocalService;
	}

	private FragmentEntryLocalService _fragmentEntryLocalService;

}