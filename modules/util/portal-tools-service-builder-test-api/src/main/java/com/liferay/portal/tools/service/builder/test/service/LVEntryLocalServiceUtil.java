/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.LVEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for LVEntry. This utility wraps
 * <code>com.liferay.portal.tools.service.builder.test.service.impl.LVEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LVEntryLocalService
 * @generated
 */
public class LVEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.LVEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean addBigDecimalEntryLVEntries(
		long bigDecimalEntryId, List<LVEntry> lvEntries) {

		return getService().addBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntries);
	}

	public static boolean addBigDecimalEntryLVEntries(
		long bigDecimalEntryId, long[] lvEntryIds) {

		return getService().addBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntryIds);
	}

	public static boolean addBigDecimalEntryLVEntry(
		long bigDecimalEntryId, long lvEntryId) {

		return getService().addBigDecimalEntryLVEntry(
			bigDecimalEntryId, lvEntryId);
	}

	public static boolean addBigDecimalEntryLVEntry(
		long bigDecimalEntryId, LVEntry lvEntry) {

		return getService().addBigDecimalEntryLVEntry(
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
	public static LVEntry addLVEntry(LVEntry lvEntry) {
		return getService().addLVEntry(lvEntry);
	}

	public static
		com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization
				addLVEntryLocalization(
					LVEntry draftLVEntry, String languageId, String title,
					String content)
			throws PortalException {

		return getService().addLVEntryLocalization(
			draftLVEntry, languageId, title, content);
	}

	public static LVEntry checkout(LVEntry publishedLVEntry, int version)
		throws PortalException {

		return getService().checkout(publishedLVEntry, version);
	}

	public static void clearBigDecimalEntryLVEntries(long bigDecimalEntryId) {
		getService().clearBigDecimalEntryLVEntries(bigDecimalEntryId);
	}

	/**
	 * Creates a new lv entry. Does not add the lv entry to the database.
	 *
	 * @return the new lv entry
	 */
	public static LVEntry create() {
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

	public static LVEntry delete(LVEntry publishedLVEntry)
		throws PortalException {

		return getService().delete(publishedLVEntry);
	}

	public static void deleteBigDecimalEntryLVEntries(
		long bigDecimalEntryId, List<LVEntry> lvEntries) {

		getService().deleteBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntries);
	}

	public static void deleteBigDecimalEntryLVEntries(
		long bigDecimalEntryId, long[] lvEntryIds) {

		getService().deleteBigDecimalEntryLVEntries(
			bigDecimalEntryId, lvEntryIds);
	}

	public static void deleteBigDecimalEntryLVEntry(
		long bigDecimalEntryId, long lvEntryId) {

		getService().deleteBigDecimalEntryLVEntry(bigDecimalEntryId, lvEntryId);
	}

	public static void deleteBigDecimalEntryLVEntry(
		long bigDecimalEntryId, LVEntry lvEntry) {

		getService().deleteBigDecimalEntryLVEntry(bigDecimalEntryId, lvEntry);
	}

	public static LVEntry deleteDraft(LVEntry draftLVEntry)
		throws PortalException {

		return getService().deleteDraft(draftLVEntry);
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
	public static LVEntry deleteLVEntry(long lvEntryId) throws PortalException {
		return getService().deleteLVEntry(lvEntryId);
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
	public static LVEntry deleteLVEntry(LVEntry lvEntry) {
		return getService().deleteLVEntry(lvEntry);
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
		com.liferay.portal.tools.service.builder.test.model.LVEntryVersion
				deleteVersion(
					com.liferay.portal.tools.service.builder.test.model.
						LVEntryVersion lvEntryVersion)
			throws PortalException {

		return getService().deleteVersion(lvEntryVersion);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LVEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LVEntryModelImpl</code>.
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

	public static LVEntry fetchDraft(long primaryKey) {
		return getService().fetchDraft(primaryKey);
	}

	public static LVEntry fetchDraft(LVEntry lvEntry) {
		return getService().fetchDraft(lvEntry);
	}

	public static
		com.liferay.portal.tools.service.builder.test.model.LVEntryVersion
			fetchLatestVersion(LVEntry lvEntry) {

		return getService().fetchLatestVersion(lvEntry);
	}

	public static LVEntry fetchLVEntry(long lvEntryId) {
		return getService().fetchLVEntry(lvEntryId);
	}

	public static
		com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization
			fetchLVEntryLocalization(long lvEntryId, String languageId) {

		return getService().fetchLVEntryLocalization(lvEntryId, languageId);
	}

	public static com.liferay.portal.tools.service.builder.test.model.
		LVEntryLocalizationVersion fetchLVEntryLocalizationVersion(
			long lvEntryId, String languageId, int version) {

		return getService().fetchLVEntryLocalizationVersion(
			lvEntryId, languageId, version);
	}

	public static LVEntry fetchPublished(long primaryKey) {
		return getService().fetchPublished(primaryKey);
	}

	public static LVEntry fetchPublished(LVEntry lvEntry) {
		return getService().fetchPublished(lvEntry);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<LVEntry> getBigDecimalEntryLVEntries(
		long bigDecimalEntryId) {

		return getService().getBigDecimalEntryLVEntries(bigDecimalEntryId);
	}

	public static List<LVEntry> getBigDecimalEntryLVEntries(
		long bigDecimalEntryId, int start, int end) {

		return getService().getBigDecimalEntryLVEntries(
			bigDecimalEntryId, start, end);
	}

	public static List<LVEntry> getBigDecimalEntryLVEntries(
		long bigDecimalEntryId, int start, int end,
		OrderByComparator<LVEntry> orderByComparator) {

		return getService().getBigDecimalEntryLVEntries(
			bigDecimalEntryId, start, end, orderByComparator);
	}

	public static int getBigDecimalEntryLVEntriesCount(long bigDecimalEntryId) {
		return getService().getBigDecimalEntryLVEntriesCount(bigDecimalEntryId);
	}

	/**
	 * Returns the bigDecimalEntryIds of the big decimal entries associated with the lv entry.
	 *
	 * @param lvEntryId the lvEntryId of the lv entry
	 * @return long[] the bigDecimalEntryIds of big decimal entries associated with the lv entry
	 */
	public static long[] getBigDecimalEntryPrimaryKeys(long lvEntryId) {
		return getService().getBigDecimalEntryPrimaryKeys(lvEntryId);
	}

	public static LVEntry getDraft(long primaryKey) throws PortalException {
		return getService().getDraft(primaryKey);
	}

	public static LVEntry getDraft(LVEntry lvEntry) throws PortalException {
		return getService().getDraft(lvEntry);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	public static List<LVEntry> getLVEntries(int start, int end) {
		return getService().getLVEntries(start, end);
	}

	/**
	 * Returns the number of lv entries.
	 *
	 * @return the number of lv entries
	 */
	public static int getLVEntriesCount() {
		return getService().getLVEntriesCount();
	}

	/**
	 * Returns the lv entry with the primary key.
	 *
	 * @param lvEntryId the primary key of the lv entry
	 * @return the lv entry
	 * @throws PortalException if a lv entry with the primary key could not be found
	 */
	public static LVEntry getLVEntry(long lvEntryId) throws PortalException {
		return getService().getLVEntry(lvEntryId);
	}

	public static
		com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization
				getLVEntryLocalization(long lvEntryId, String languageId)
			throws PortalException {

		return getService().getLVEntryLocalization(lvEntryId, languageId);
	}

	public static List
		<com.liferay.portal.tools.service.builder.test.model.
			LVEntryLocalization> getLVEntryLocalizations(long lvEntryId) {

		return getService().getLVEntryLocalizations(lvEntryId);
	}

	public static List
		<com.liferay.portal.tools.service.builder.test.model.
			LVEntryLocalizationVersion> getLVEntryLocalizationVersions(
				long lvEntryId) {

		return getService().getLVEntryLocalizationVersions(lvEntryId);
	}

	public static List
		<com.liferay.portal.tools.service.builder.test.model.
			LVEntryLocalizationVersion> getLVEntryLocalizationVersions(
					long lvEntryId, String languageId)
				throws PortalException {

		return getService().getLVEntryLocalizationVersions(
			lvEntryId, languageId);
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
		com.liferay.portal.tools.service.builder.test.model.LVEntryVersion
				getVersion(LVEntry lvEntry, int version)
			throws PortalException {

		return getService().getVersion(lvEntry, version);
	}

	public static List
		<com.liferay.portal.tools.service.builder.test.model.LVEntryVersion>
			getVersions(LVEntry lvEntry) {

		return getService().getVersions(lvEntry);
	}

	public static boolean hasBigDecimalEntryLVEntries(long bigDecimalEntryId) {
		return getService().hasBigDecimalEntryLVEntries(bigDecimalEntryId);
	}

	public static boolean hasBigDecimalEntryLVEntry(
		long bigDecimalEntryId, long lvEntryId) {

		return getService().hasBigDecimalEntryLVEntry(
			bigDecimalEntryId, lvEntryId);
	}

	public static LVEntry publishDraft(LVEntry draftLVEntry)
		throws PortalException {

		return getService().publishDraft(draftLVEntry);
	}

	public static void registerListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<LVEntry,
			 com.liferay.portal.tools.service.builder.test.model.LVEntryVersion>
				versionServiceListener) {

		getService().registerListener(versionServiceListener);
	}

	public static void setBigDecimalEntryLVEntries(
		long bigDecimalEntryId, long[] lvEntryIds) {

		getService().setBigDecimalEntryLVEntries(bigDecimalEntryId, lvEntryIds);
	}

	public static void unregisterListener(
		com.liferay.portal.kernel.service.version.VersionServiceListener
			<LVEntry,
			 com.liferay.portal.tools.service.builder.test.model.LVEntryVersion>
				versionServiceListener) {

		getService().unregisterListener(versionServiceListener);
	}

	public static LVEntry updateDraft(LVEntry draftLVEntry)
		throws PortalException {

		return getService().updateDraft(draftLVEntry);
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
	public static LVEntry updateLVEntry(LVEntry draftLVEntry)
		throws PortalException {

		return getService().updateLVEntry(draftLVEntry);
	}

	public static
		com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization
				updateLVEntryLocalization(
					LVEntry draftLVEntry, String languageId, String title,
					String content)
			throws PortalException {

		return getService().updateLVEntryLocalization(
			draftLVEntry, languageId, title, content);
	}

	public static List
		<com.liferay.portal.tools.service.builder.test.model.
			LVEntryLocalization> updateLVEntryLocalizations(
					LVEntry draftLVEntry, Map<String, String> titleMap,
					Map<String, String> contentMap)
				throws PortalException {

		return getService().updateLVEntryLocalizations(
			draftLVEntry, titleMap, contentMap);
	}

	public static LVEntryLocalService getService() {
		return _service;
	}

	public static void setService(LVEntryLocalService service) {
		_service = service;
	}

	private static volatile LVEntryLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:32172927