/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FragmentEntry. This utility wraps
 * <code>com.liferay.fragment.service.impl.FragmentEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLocalService
 * @generated
 */
public class FragmentEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static FragmentEntry addFragmentEntry(FragmentEntry fragmentEntry) {
		return getService().addFragmentEntry(fragmentEntry);
	}

	public static FragmentEntry addFragmentEntry(
			String externalReferenceCode, long userId, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentEntry(
			externalReferenceCode, userId, groupId, fragmentCollectionId,
			fragmentEntryKey, name, css, html, js, cacheable, configuration,
			icon, previewFileEntryId, marketplace, readOnly, type, typeOptions,
			status, serviceContext);
	}

	public static FragmentEntry checkout(
			FragmentEntry publishedFragmentEntry, int version)
		throws PortalException {

		return getService().checkout(publishedFragmentEntry, version);
	}

	public static FragmentEntry copyFragmentEntry(
			long userId, long groupId, long sourceFragmentEntryId,
			long fragmentCollectionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().copyFragmentEntry(
			userId, groupId, sourceFragmentEntryId, fragmentCollectionId,
			serviceContext);
	}

	/**
	 * Creates a new fragment entry. Does not add the fragment entry to the database.
	 *
	 * @return the new fragment entry
	 */
	public static FragmentEntry create() {
		return getService().create();
	}

	public static FragmentEntry createFragmentEntry(long fragmentEntryId) {
		return getService().createFragmentEntry(fragmentEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static FragmentEntry delete(FragmentEntry publishedFragmentEntry)
		throws PortalException {

		return getService().delete(publishedFragmentEntry);
	}

	public static FragmentEntry deleteDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		return getService().deleteDraft(draftFragmentEntry);
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
	public static FragmentEntry deleteFragmentEntry(FragmentEntry fragmentEntry)
		throws PortalException {

		return getService().deleteFragmentEntry(fragmentEntry);
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
	public static FragmentEntry deleteFragmentEntry(long fragmentEntryId)
		throws PortalException {

		return getService().deleteFragmentEntry(fragmentEntryId);
	}

	public static FragmentEntry deleteFragmentEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteFragmentEntry(externalReferenceCode, groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.fragment.model.FragmentEntryVersion deleteVersion(
			com.liferay.fragment.model.FragmentEntryVersion
				fragmentEntryVersion)
		throws PortalException {

		return getService().deleteVersion(fragmentEntryVersion);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
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

	public static FragmentEntry fetchDraft(FragmentEntry fragmentEntry) {
		return getService().fetchDraft(fragmentEntry);
	}

	public static FragmentEntry fetchDraft(long primaryKey) {
		return getService().fetchDraft(primaryKey);
	}

	public static FragmentEntry fetchFragmentEntry(long fragmentEntryId) {
		return getService().fetchFragmentEntry(fragmentEntryId);
	}

	public static FragmentEntry fetchFragmentEntry(
		long groupId, String fragmentEntryKey) {

		return getService().fetchFragmentEntry(groupId, fragmentEntryKey);
	}

	public static FragmentEntry fetchFragmentEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchFragmentEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static FragmentEntry fetchFragmentEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId, boolean head) {

		return getService().fetchFragmentEntryByExternalReferenceCode(
			externalReferenceCode, groupId, head);
	}

	public static FragmentEntry fetchFragmentEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchFragmentEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.fragment.model.FragmentEntryVersion
		fetchLatestVersion(FragmentEntry fragmentEntry) {

		return getService().fetchLatestVersion(fragmentEntry);
	}

	public static FragmentEntry fetchPublished(FragmentEntry fragmentEntry) {
		return getService().fetchPublished(fragmentEntry);
	}

	public static FragmentEntry fetchPublished(long primaryKey) {
		return getService().fetchPublished(primaryKey);
	}

	public static String generateFragmentEntryKey(long groupId, String name) {
		return getService().generateFragmentEntryKey(groupId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static FragmentEntry getDraft(FragmentEntry fragmentEntry)
		throws PortalException {

		return getService().getDraft(fragmentEntry);
	}

	public static FragmentEntry getDraft(long primaryKey)
		throws PortalException {

		return getService().getDraft(primaryKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
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
	public static List<FragmentEntry> getFragmentEntries(int start, int end) {
		return getService().getFragmentEntries(start, end);
	}

	public static List<FragmentEntry> getFragmentEntries(
		long fragmentCollectionId) {

		return getService().getFragmentEntries(fragmentCollectionId);
	}

	public static List<FragmentEntry> getFragmentEntries(
		long fragmentCollectionId, int start, int end) {

		return getService().getFragmentEntries(
			fragmentCollectionId, start, end);
	}

	public static List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int status) {

		return getService().getFragmentEntries(
			groupId, fragmentCollectionId, status);
	}

	public static List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getService().getFragmentEntries(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getService().getFragmentEntries(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getService().getFragmentEntries(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentEntry> orderByComparator) {

		return getService().getFragmentEntries(
			groupId, fragmentCollectionId, name, start, end, orderByComparator);
	}

	public static List<FragmentEntry> getFragmentEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getFragmentEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	public static List<FragmentEntry> getFragmentEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getService().getFragmentEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of fragment entries.
	 *
	 * @return the number of fragment entries
	 */
	public static int getFragmentEntriesCount() {
		return getService().getFragmentEntriesCount();
	}

	public static int getFragmentEntriesCount(long fragmentCollectionId) {
		return getService().getFragmentEntriesCount(fragmentCollectionId);
	}

	/**
	 * Returns the fragment entry with the primary key.
	 *
	 * @param fragmentEntryId the primary key of the fragment entry
	 * @return the fragment entry
	 * @throws PortalException if a fragment entry with the primary key could not be found
	 */
	public static FragmentEntry getFragmentEntry(long fragmentEntryId)
		throws PortalException {

		return getService().getFragmentEntry(fragmentEntryId);
	}

	public static FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFragmentEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head)
		throws PortalException {

		return getService().getFragmentEntryByExternalReferenceCode(
			externalReferenceCode, groupId, head);
	}

	public static FragmentEntry getFragmentEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getFragmentEntryByUuidAndGroupId(uuid, groupId);
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

	public static String[] getTempFileNames(
			long userId, long groupId, String folderName)
		throws PortalException {

		return getService().getTempFileNames(userId, groupId, folderName);
	}

	public static String getUniqueFragmentEntryName(
		long groupId, long fragmentCollectionId, String name) {

		return getService().getUniqueFragmentEntryName(
			groupId, fragmentCollectionId, name);
	}

	public static com.liferay.fragment.model.FragmentEntryVersion getVersion(
			FragmentEntry fragmentEntry, int version)
		throws PortalException {

		return getService().getVersion(fragmentEntry, version);
	}

	public static List<com.liferay.fragment.model.FragmentEntryVersion>
		getVersions(FragmentEntry fragmentEntry) {

		return getService().getVersions(fragmentEntry);
	}

	public static FragmentEntry moveFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId)
		throws PortalException {

		return getService().moveFragmentEntry(
			fragmentEntryId, fragmentCollectionId);
	}

	public static FragmentEntry publishDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		return getService().publishDraft(draftFragmentEntry);
	}

	public static void registerListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<FragmentEntry, com.liferay.fragment.model.FragmentEntryVersion>
				versionServiceListener) {

		getService().registerListener(versionServiceListener);
	}

	public static void unregisterListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<FragmentEntry, com.liferay.fragment.model.FragmentEntryVersion>
				versionServiceListener) {

		getService().unregisterListener(versionServiceListener);
	}

	public static FragmentEntry updateDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		return getService().updateDraft(draftFragmentEntry);
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
	public static FragmentEntry updateFragmentEntry(
			FragmentEntry draftFragmentEntry)
		throws PortalException {

		return getService().updateFragmentEntry(draftFragmentEntry);
	}

	public static FragmentEntry updateFragmentEntry(
			long fragmentEntryId, boolean cacheable)
		throws PortalException {

		return getService().updateFragmentEntry(fragmentEntryId, cacheable);
	}

	public static FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long previewFileEntryId)
		throws PortalException {

		return getService().updateFragmentEntry(
			fragmentEntryId, previewFileEntryId);
	}

	public static FragmentEntry updateFragmentEntry(
			long userId, long fragmentEntryId, long fragmentCollectionId,
			String name, String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean readOnly, String typeOptions, int status)
		throws PortalException {

		return getService().updateFragmentEntry(
			userId, fragmentEntryId, fragmentCollectionId, name, css, html, js,
			cacheable, configuration, icon, previewFileEntryId, readOnly,
			typeOptions, status);
	}

	public static FragmentEntry updateFragmentEntry(
			long fragmentEntryId, String name)
		throws PortalException {

		return getService().updateFragmentEntry(fragmentEntryId, name);
	}

	public static FragmentEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FragmentEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			FragmentEntryLocalServiceUtil.class,
			FragmentEntryLocalService.class);

}