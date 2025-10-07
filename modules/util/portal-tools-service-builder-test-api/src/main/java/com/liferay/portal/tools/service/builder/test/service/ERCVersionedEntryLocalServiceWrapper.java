/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ERCVersionedEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ERCVersionedEntryLocalService
 * @generated
 */
public class ERCVersionedEntryLocalServiceWrapper
	implements ERCVersionedEntryLocalService,
			   ServiceWrapper<ERCVersionedEntryLocalService> {

	public ERCVersionedEntryLocalServiceWrapper() {
		this(null);
	}

	public ERCVersionedEntryLocalServiceWrapper(
		ERCVersionedEntryLocalService ercVersionedEntryLocalService) {

		_ercVersionedEntryLocalService = ercVersionedEntryLocalService;
	}

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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		addERCVersionedEntry(
			com.liferay.portal.tools.service.builder.test.model.
				ERCVersionedEntry ercVersionedEntry) {

		return _ercVersionedEntryLocalService.addERCVersionedEntry(
			ercVersionedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			checkout(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry publishedERCVersionedEntry,
				int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.checkout(
			publishedERCVersionedEntry, version);
	}

	/**
	 * Creates a new erc versioned entry. Does not add the erc versioned entry to the database.
	 *
	 * @return the new erc versioned entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		create() {

		return _ercVersionedEntryLocalService.create();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			delete(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry publishedERCVersionedEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.delete(
			publishedERCVersionedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			deleteDraft(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry draftERCVersionedEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.deleteDraft(
			draftERCVersionedEntry);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		deleteERCVersionedEntry(
			com.liferay.portal.tools.service.builder.test.model.
				ERCVersionedEntry ercVersionedEntry) {

		return _ercVersionedEntryLocalService.deleteERCVersionedEntry(
			ercVersionedEntry);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			deleteERCVersionedEntry(long ercVersionedEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.deleteERCVersionedEntry(
			ercVersionedEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			ERCVersionedEntryVersion deleteVersion(
					com.liferay.portal.tools.service.builder.test.model.
						ERCVersionedEntryVersion ercVersionedEntryVersion)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.deleteVersion(
			ercVersionedEntryVersion);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ercVersionedEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ercVersionedEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ercVersionedEntryLocalService.dynamicQuery();
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

		return _ercVersionedEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _ercVersionedEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _ercVersionedEntryLocalService.dynamicQuery(
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

		return _ercVersionedEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ercVersionedEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		fetchDraft(
			com.liferay.portal.tools.service.builder.test.model.
				ERCVersionedEntry ercVersionedEntry) {

		return _ercVersionedEntryLocalService.fetchDraft(ercVersionedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		fetchDraft(long primaryKey) {

		return _ercVersionedEntryLocalService.fetchDraft(primaryKey);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		fetchERCVersionedEntry(long ercVersionedEntryId) {

		return _ercVersionedEntryLocalService.fetchERCVersionedEntry(
			ercVersionedEntryId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		fetchERCVersionedEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _ercVersionedEntryLocalService.
			fetchERCVersionedEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		fetchERCVersionedEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId, boolean head) {

		return _ercVersionedEntryLocalService.
			fetchERCVersionedEntryByExternalReferenceCode(
				externalReferenceCode, groupId, head);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			ERCVersionedEntryVersion fetchLatestVersion(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry ercVersionedEntry) {

		return _ercVersionedEntryLocalService.fetchLatestVersion(
			ercVersionedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		fetchPublished(
			com.liferay.portal.tools.service.builder.test.model.
				ERCVersionedEntry ercVersionedEntry) {

		return _ercVersionedEntryLocalService.fetchPublished(ercVersionedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
		fetchPublished(long primaryKey) {

		return _ercVersionedEntryLocalService.fetchPublished(primaryKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ercVersionedEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			getDraft(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry ercVersionedEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.getDraft(ercVersionedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			getDraft(long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.getDraft(primaryKey);
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
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry>
			getERCVersionedEntries(int start, int end) {

		return _ercVersionedEntryLocalService.getERCVersionedEntries(
			start, end);
	}

	/**
	 * Returns the number of erc versioned entries.
	 *
	 * @return the number of erc versioned entries
	 */
	@Override
	public int getERCVersionedEntriesCount() {
		return _ercVersionedEntryLocalService.getERCVersionedEntriesCount();
	}

	/**
	 * Returns the erc versioned entry with the primary key.
	 *
	 * @param ercVersionedEntryId the primary key of the erc versioned entry
	 * @return the erc versioned entry
	 * @throws PortalException if a erc versioned entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			getERCVersionedEntry(long ercVersionedEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.getERCVersionedEntry(
			ercVersionedEntryId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			getERCVersionedEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.
			getERCVersionedEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			getERCVersionedEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId, boolean head)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.
			getERCVersionedEntryByExternalReferenceCode(
				externalReferenceCode, groupId, head);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ercVersionedEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ercVersionedEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.
			ERCVersionedEntryVersion getVersion(
					com.liferay.portal.tools.service.builder.test.model.
						ERCVersionedEntry ercVersionedEntry,
					int version)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.getVersion(
			ercVersionedEntry, version);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			ERCVersionedEntryVersion> getVersions(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry ercVersionedEntry) {

		return _ercVersionedEntryLocalService.getVersions(ercVersionedEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			publishDraft(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry draftERCVersionedEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.publishDraft(
			draftERCVersionedEntry);
	}

	@Override
	public void registerListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<com.liferay.portal.tools.service.builder.test.model.
				ERCVersionedEntry,
			 com.liferay.portal.tools.service.builder.test.model.
				 ERCVersionedEntryVersion> versionServiceListener) {

		_ercVersionedEntryLocalService.registerListener(versionServiceListener);
	}

	@Override
	public void unregisterListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<com.liferay.portal.tools.service.builder.test.model.
				ERCVersionedEntry,
			 com.liferay.portal.tools.service.builder.test.model.
				 ERCVersionedEntryVersion> versionServiceListener) {

		_ercVersionedEntryLocalService.unregisterListener(
			versionServiceListener);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			updateDraft(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry draftERCVersionedEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.updateDraft(
			draftERCVersionedEntry);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry
			updateERCVersionedEntry(
				com.liferay.portal.tools.service.builder.test.model.
					ERCVersionedEntry draftERCVersionedEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ercVersionedEntryLocalService.updateERCVersionedEntry(
			draftERCVersionedEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ercVersionedEntryLocalService.getBasePersistence();
	}

	@Override
	public ERCVersionedEntryLocalService getWrappedService() {
		return _ercVersionedEntryLocalService;
	}

	@Override
	public void setWrappedService(
		ERCVersionedEntryLocalService ercVersionedEntryLocalService) {

		_ercVersionedEntryLocalService = ercVersionedEntryLocalService;
	}

	private ERCVersionedEntryLocalService _ercVersionedEntryLocalService;

}