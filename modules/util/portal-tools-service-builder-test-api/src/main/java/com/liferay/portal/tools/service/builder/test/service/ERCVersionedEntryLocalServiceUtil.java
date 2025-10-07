/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ERCVersionedEntry. This utility wraps
 * <code>com.liferay.portal.tools.service.builder.test.service.impl.ERCVersionedEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ERCVersionedEntryLocalService
 * @generated
 */
public class ERCVersionedEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.ERCVersionedEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the erc versioned entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ERCVersionedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ercVersionedEntry the erc versioned entry
	 * @return the erc versioned entry that was added
	 */
	public static ERCVersionedEntry addERCVersionedEntry(
		ERCVersionedEntry ercVersionedEntry) {

		return getService().addERCVersionedEntry(ercVersionedEntry);
	}

	public static ERCVersionedEntry checkout(
			ERCVersionedEntry publishedERCVersionedEntry, int version)
		throws PortalException {

		return getService().checkout(publishedERCVersionedEntry, version);
	}

	/**
	 * Creates a new erc versioned entry. Does not add the erc versioned entry to the database.
	 *
	 * @return the new erc versioned entry
	 */
	public static ERCVersionedEntry create() {
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

	public static ERCVersionedEntry delete(
			ERCVersionedEntry publishedERCVersionedEntry)
		throws PortalException {

		return getService().delete(publishedERCVersionedEntry);
	}

	public static ERCVersionedEntry deleteDraft(
			ERCVersionedEntry draftERCVersionedEntry)
		throws PortalException {

		return getService().deleteDraft(draftERCVersionedEntry);
	}

	/**
	 * Deletes the erc versioned entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ERCVersionedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ercVersionedEntry the erc versioned entry
	 * @return the erc versioned entry that was removed
	 */
	public static ERCVersionedEntry deleteERCVersionedEntry(
		ERCVersionedEntry ercVersionedEntry) {

		return getService().deleteERCVersionedEntry(ercVersionedEntry);
	}

	/**
	 * Deletes the erc versioned entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ERCVersionedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry that was removed
	 * @throws PortalException if a erc versioned entry with the primary key could not be found
	 */
	public static ERCVersionedEntry deleteERCVersionedEntry(
			long ercVersionedEntryId)
		throws PortalException {

		return getService().deleteERCVersionedEntry(ercVersionedEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static
		com.liferay.portal.tools.service.builder.test.model.
			ERCVersionedEntryVersion deleteVersion(
					com.liferay.portal.tools.service.builder.test.model.
						ERCVersionedEntryVersion ercVersionedEntryVersion)
				throws PortalException {

		return getService().deleteVersion(ercVersionedEntryVersion);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryModelImpl</code>.
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

	public static ERCVersionedEntry fetchDraft(
		ERCVersionedEntry ercVersionedEntry) {

		return getService().fetchDraft(ercVersionedEntry);
	}

	public static ERCVersionedEntry fetchDraft(long primaryKey) {
		return getService().fetchDraft(primaryKey);
	}

	public static ERCVersionedEntry fetchERCVersionedEntry(
		long ercVersionedEntryId) {

		return getService().fetchERCVersionedEntry(ercVersionedEntryId);
	}

	public static ERCVersionedEntry
		fetchERCVersionedEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchERCVersionedEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static ERCVersionedEntry
		fetchERCVersionedEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head) {

		return getService().fetchERCVersionedEntryByExternalReferenceCode(
			externalReferenceCode, groupId, head);
	}

	public static
		com.liferay.portal.tools.service.builder.test.model.
			ERCVersionedEntryVersion fetchLatestVersion(
				ERCVersionedEntry ercVersionedEntry) {

		return getService().fetchLatestVersion(ercVersionedEntry);
	}

	public static ERCVersionedEntry fetchPublished(
		ERCVersionedEntry ercVersionedEntry) {

		return getService().fetchPublished(ercVersionedEntry);
	}

	public static ERCVersionedEntry fetchPublished(long primaryKey) {
		return getService().fetchPublished(primaryKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static ERCVersionedEntry getDraft(
			ERCVersionedEntry ercVersionedEntry)
		throws PortalException {

		return getService().getDraft(ercVersionedEntry);
	}

	public static ERCVersionedEntry getDraft(long primaryKey)
		throws PortalException {

		return getService().getDraft(primaryKey);
	}

	/**
	 * Returns a range of all the erc versioned entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.ERCVersionedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of erc versioned entries
	 * @param end the upper bound of the range of erc versioned entries (not inclusive)
	 * @return the range of erc versioned entries
	 */
	public static List<ERCVersionedEntry> getERCVersionedEntries(
		int start, int end) {

		return getService().getERCVersionedEntries(start, end);
	}

	/**
	 * Returns the number of erc versioned entries.
	 *
	 * @return the number of erc versioned entries
	 */
	public static int getERCVersionedEntriesCount() {
		return getService().getERCVersionedEntriesCount();
	}

	/**
	 * Returns the erc versioned entry with the primary key.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry
	 * @throws PortalException if a erc versioned entry with the primary key could not be found
	 */
	public static ERCVersionedEntry getERCVersionedEntry(
			long ercVersionedEntryId)
		throws PortalException {

		return getService().getERCVersionedEntry(ercVersionedEntryId);
	}

	public static ERCVersionedEntry getERCVersionedEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getERCVersionedEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static ERCVersionedEntry getERCVersionedEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head)
		throws PortalException {

		return getService().getERCVersionedEntryByExternalReferenceCode(
			externalReferenceCode, groupId, head);
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

	public static
		com.liferay.portal.tools.service.builder.test.model.
			ERCVersionedEntryVersion getVersion(
					ERCVersionedEntry ercVersionedEntry, int version)
				throws PortalException {

		return getService().getVersion(ercVersionedEntry, version);
	}

	public static List
		<com.liferay.portal.tools.service.builder.test.model.
			ERCVersionedEntryVersion> getVersions(
				ERCVersionedEntry ercVersionedEntry) {

		return getService().getVersions(ercVersionedEntry);
	}

	public static ERCVersionedEntry publishDraft(
			ERCVersionedEntry draftERCVersionedEntry)
		throws PortalException {

		return getService().publishDraft(draftERCVersionedEntry);
	}

	public static void registerListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<ERCVersionedEntry,
			 com.liferay.portal.tools.service.builder.test.model.
				 ERCVersionedEntryVersion> versionServiceListener) {

		getService().registerListener(versionServiceListener);
	}

	public static void unregisterListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<ERCVersionedEntry,
			 com.liferay.portal.tools.service.builder.test.model.
				 ERCVersionedEntryVersion> versionServiceListener) {

		getService().unregisterListener(versionServiceListener);
	}

	public static ERCVersionedEntry updateDraft(
			ERCVersionedEntry draftERCVersionedEntry)
		throws PortalException {

		return getService().updateDraft(draftERCVersionedEntry);
	}

	/**
	 * Updates the erc versioned entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ERCVersionedEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param draftERCVersionedEntry the erc versioned entry
	 * @return the erc versioned entry that was updated
	 */
	public static ERCVersionedEntry updateERCVersionedEntry(
			ERCVersionedEntry draftERCVersionedEntry)
		throws PortalException {

		return getService().updateERCVersionedEntry(draftERCVersionedEntry);
	}

	public static ERCVersionedEntryLocalService getService() {
		return _service;
	}

	public static void setService(ERCVersionedEntryLocalService service) {
		_service = service;
	}

	private static volatile ERCVersionedEntryLocalService _service;

}