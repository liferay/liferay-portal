/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link LVEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LVEntryLocalService
 * @generated
 */
public class LVEntryLocalServiceWrapper
	implements LVEntryLocalService, ServiceWrapper<LVEntryLocalService> {

	public LVEntryLocalServiceWrapper() {
		this(null);
	}

	public LVEntryLocalServiceWrapper(LVEntryLocalService lvEntryLocalService) {
		_lvEntryLocalService = lvEntryLocalService;
	}

	@Override
	public boolean addBigDecimalEntryLVEntries(
		long bigDecimalEntryId,
		java.util.List
			<com.liferay.portal.tools.service.builder.test.model.LVEntry>
				lvEntries) {

		return _lvEntryLocalService.addBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntries);
	}

	@Override
	public boolean addBigDecimalEntryLVEntries(
		long bigDecimalEntryId, long[] lvEntryIds) {

		return _lvEntryLocalService.addBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntryIds);
	}

	@Override
	public boolean addBigDecimalEntryLVEntry(
		long bigDecimalEntryId, long lvEntryId) {

		return _lvEntryLocalService.addBigDecimalEntryLVEntry(
			bigDecimalEntryId, lvEntryId);
	}

	@Override
	public boolean addBigDecimalEntryLVEntry(
		long bigDecimalEntryId,
		com.liferay.portal.tools.service.builder.test.model.LVEntry lvEntry) {

		return _lvEntryLocalService.addBigDecimalEntryLVEntry(
			bigDecimalEntryId, lvEntry);
	}

	/**
	 * Adds the lv entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LVEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lvEntry the lv entry
	 * @return the lv entry that was added
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
		addLVEntry(
			com.liferay.portal.tools.service.builder.test.model.LVEntry
				lvEntry) {

		return _lvEntryLocalService.addLVEntry(lvEntry);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization
				addLVEntryLocalization(
					com.liferay.portal.tools.service.builder.test.model.LVEntry
						draftLVEntry,
					String languageId, String title, String content)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.addLVEntryLocalization(
			draftLVEntry, languageId, title, content);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry checkout(
			com.liferay.portal.tools.service.builder.test.model.LVEntry
				publishedLVEntry,
			int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.checkout(publishedLVEntry, version);
	}

	@Override
	public void clearBigDecimalEntryLVEntries(long bigDecimalEntryId) {
		_lvEntryLocalService.clearBigDecimalEntryLVEntries(bigDecimalEntryId);
	}

	/**
	 * Creates a new lv entry. Does not add the lv entry to the database.
	 *
	 * @return the new lv entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
		create() {

		return _lvEntryLocalService.create();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry delete(
			com.liferay.portal.tools.service.builder.test.model.LVEntry
				publishedLVEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.delete(publishedLVEntry);
	}

	@Override
	public void deleteBigDecimalEntryLVEntries(
		long bigDecimalEntryId,
		java.util.List
			<com.liferay.portal.tools.service.builder.test.model.LVEntry>
				lvEntries) {

		_lvEntryLocalService.deleteBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntries);
	}

	@Override
	public void deleteBigDecimalEntryLVEntries(
		long bigDecimalEntryId, long[] lvEntryIds) {

		_lvEntryLocalService.deleteBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntryIds);
	}

	@Override
	public void deleteBigDecimalEntryLVEntry(
		long bigDecimalEntryId, long lvEntryId) {

		_lvEntryLocalService.deleteBigDecimalEntryLVEntry(
			bigDecimalEntryId, lvEntryId);
	}

	@Override
	public void deleteBigDecimalEntryLVEntry(
		long bigDecimalEntryId,
		com.liferay.portal.tools.service.builder.test.model.LVEntry lvEntry) {

		_lvEntryLocalService.deleteBigDecimalEntryLVEntry(
			bigDecimalEntryId, lvEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
			deleteDraft(
				com.liferay.portal.tools.service.builder.test.model.LVEntry
					draftLVEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.deleteDraft(draftLVEntry);
	}

	/**
	 * Deletes the lv entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LVEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lvEntryId the primary key of the lv entry
	 * @return the lv entry that was removed
	 * @throws PortalException if a lv entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
			deleteLVEntry(long lvEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.deleteLVEntry(lvEntryId);
	}

	/**
	 * Deletes the lv entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LVEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lvEntry the lv entry
	 * @return the lv entry that was removed
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
		deleteLVEntry(
			com.liferay.portal.tools.service.builder.test.model.LVEntry
				lvEntry) {

		return _lvEntryLocalService.deleteLVEntry(lvEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntryVersion
			deleteVersion(
				com.liferay.portal.tools.service.builder.test.model.
					LVEntryVersion lvEntryVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.deleteVersion(lvEntryVersion);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _lvEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _lvEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _lvEntryLocalService.dynamicQuery();
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

		return _lvEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LVEntryModelImpl</code>.
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

		return _lvEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LVEntryModelImpl</code>.
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

		return _lvEntryLocalService.dynamicQuery(
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

		return _lvEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _lvEntryLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
		fetchDraft(long primaryKey) {

		return _lvEntryLocalService.fetchDraft(primaryKey);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
		fetchDraft(
			com.liferay.portal.tools.service.builder.test.model.LVEntry
				lvEntry) {

		return _lvEntryLocalService.fetchDraft(lvEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntryVersion
		fetchLatestVersion(
			com.liferay.portal.tools.service.builder.test.model.LVEntry
				lvEntry) {

		return _lvEntryLocalService.fetchLatestVersion(lvEntry);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
		fetchLVEntry(long lvEntryId) {

		return _lvEntryLocalService.fetchLVEntry(lvEntryId);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization
			fetchLVEntryLocalization(long lvEntryId, String languageId) {

		return _lvEntryLocalService.fetchLVEntryLocalization(
			lvEntryId, languageId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		LVEntryLocalizationVersion fetchLVEntryLocalizationVersion(
			long lvEntryId, String languageId, int version) {

		return _lvEntryLocalService.fetchLVEntryLocalizationVersion(
			lvEntryId, languageId, version);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
		fetchPublished(long primaryKey) {

		return _lvEntryLocalService.fetchPublished(primaryKey);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
		fetchPublished(
			com.liferay.portal.tools.service.builder.test.model.LVEntry
				lvEntry) {

		return _lvEntryLocalService.fetchPublished(lvEntry);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _lvEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.LVEntry>
			getBigDecimalEntryLVEntries(long bigDecimalEntryId) {

		return _lvEntryLocalService.getBigDecimalEntryLVEntries(
			bigDecimalEntryId);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.LVEntry>
			getBigDecimalEntryLVEntries(
				long bigDecimalEntryId, int start, int end) {

		return _lvEntryLocalService.getBigDecimalEntryLVEntries(
			bigDecimalEntryId, start, end);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.LVEntry>
			getBigDecimalEntryLVEntries(
				long bigDecimalEntryId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.tools.service.builder.test.model.
						LVEntry> orderByComparator) {

		return _lvEntryLocalService.getBigDecimalEntryLVEntries(
			bigDecimalEntryId, start, end, orderByComparator);
	}

	@Override
	public int getBigDecimalEntryLVEntriesCount(long bigDecimalEntryId) {
		return _lvEntryLocalService.getBigDecimalEntryLVEntriesCount(
			bigDecimalEntryId);
	}

	/**
	 * Returns the bigDecimalEntryIds of the big decimal entries associated with the lv entry.
	 *
	 * @param lvEntryId the lvEntryId of the lv entry
	 * @return long[] the bigDecimalEntryIds of big decimal entries associated with the lv entry
	 */
	@Override
	public long[] getBigDecimalEntryPrimaryKeys(long lvEntryId) {
		return _lvEntryLocalService.getBigDecimalEntryPrimaryKeys(lvEntryId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry getDraft(
			long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.getDraft(primaryKey);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry getDraft(
			com.liferay.portal.tools.service.builder.test.model.LVEntry lvEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.getDraft(lvEntry);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _lvEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the lv entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LVEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of lv entries
	 * @param end the upper bound of the range of lv entries (not inclusive)
	 * @return the range of lv entries
	 */
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.LVEntry>
			getLVEntries(int start, int end) {

		return _lvEntryLocalService.getLVEntries(start, end);
	}

	/**
	 * Returns the number of lv entries.
	 *
	 * @return the number of lv entries
	 */
	@Override
	public int getLVEntriesCount() {
		return _lvEntryLocalService.getLVEntriesCount();
	}

	/**
	 * Returns the lv entry with the primary key.
	 *
	 * @param lvEntryId the primary key of the lv entry
	 * @return the lv entry
	 * @throws PortalException if a lv entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
			getLVEntry(long lvEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.getLVEntry(lvEntryId);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization
				getLVEntryLocalization(long lvEntryId, String languageId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.getLVEntryLocalization(
			lvEntryId, languageId);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			LVEntryLocalization> getLVEntryLocalizations(long lvEntryId) {

		return _lvEntryLocalService.getLVEntryLocalizations(lvEntryId);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			LVEntryLocalizationVersion> getLVEntryLocalizationVersions(
				long lvEntryId) {

		return _lvEntryLocalService.getLVEntryLocalizationVersions(lvEntryId);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			LVEntryLocalizationVersion> getLVEntryLocalizationVersions(
					long lvEntryId, String languageId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.getLVEntryLocalizationVersions(
			lvEntryId, languageId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _lvEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntryVersion
			getVersion(
				com.liferay.portal.tools.service.builder.test.model.LVEntry
					lvEntry,
				int version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.getVersion(lvEntry, version);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.LVEntryVersion>
			getVersions(
				com.liferay.portal.tools.service.builder.test.model.LVEntry
					lvEntry) {

		return _lvEntryLocalService.getVersions(lvEntry);
	}

	@Override
	public boolean hasBigDecimalEntryLVEntries(long bigDecimalEntryId) {
		return _lvEntryLocalService.hasBigDecimalEntryLVEntries(
			bigDecimalEntryId);
	}

	@Override
	public boolean hasBigDecimalEntryLVEntry(
		long bigDecimalEntryId, long lvEntryId) {

		return _lvEntryLocalService.hasBigDecimalEntryLVEntry(
			bigDecimalEntryId, lvEntryId);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
			publishDraft(
				com.liferay.portal.tools.service.builder.test.model.LVEntry
					draftLVEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.publishDraft(draftLVEntry);
	}

	@Override
	public void registerListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<com.liferay.portal.tools.service.builder.test.model.LVEntry,
			 com.liferay.portal.tools.service.builder.test.model.LVEntryVersion>
				versionServiceListener) {

		_lvEntryLocalService.registerListener(versionServiceListener);
	}

	@Override
	public void setBigDecimalEntryLVEntries(
		long bigDecimalEntryId, long[] lvEntryIds) {

		_lvEntryLocalService.setBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntryIds);
	}

	@Override
	public void unregisterListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<com.liferay.portal.tools.service.builder.test.model.LVEntry,
			 com.liferay.portal.tools.service.builder.test.model.LVEntryVersion>
				versionServiceListener) {

		_lvEntryLocalService.unregisterListener(versionServiceListener);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
			updateDraft(
				com.liferay.portal.tools.service.builder.test.model.LVEntry
					draftLVEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.updateDraft(draftLVEntry);
	}

	/**
	 * Updates the lv entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LVEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param draftLVEntry the lv entry
	 * @return the lv entry that was updated
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.LVEntry
			updateLVEntry(
				com.liferay.portal.tools.service.builder.test.model.LVEntry
					draftLVEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.updateLVEntry(draftLVEntry);
	}

	@Override
	public
		com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization
				updateLVEntryLocalization(
					com.liferay.portal.tools.service.builder.test.model.LVEntry
						draftLVEntry,
					String languageId, String title, String content)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.updateLVEntryLocalization(
			draftLVEntry, languageId, title, content);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			LVEntryLocalization> updateLVEntryLocalizations(
					com.liferay.portal.tools.service.builder.test.model.LVEntry
						draftLVEntry,
					java.util.Map<String, String> titleMap,
					java.util.Map<String, String> contentMap)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _lvEntryLocalService.updateLVEntryLocalizations(
			draftLVEntry, titleMap, contentMap);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _lvEntryLocalService.getBasePersistence();
	}

	@Override
	public LVEntryLocalService getWrappedService() {
		return _lvEntryLocalService;
	}

	@Override
	public void setWrappedService(LVEntryLocalService lvEntryLocalService) {
		_lvEntryLocalService = lvEntryLocalService;
	}

	private LVEntryLocalService _lvEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1361996429