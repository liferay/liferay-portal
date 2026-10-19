/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetEntry. This utility wraps
 * <code>com.liferay.portlet.asset.service.impl.AssetEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetEntryLocalService
 * @generated
 */
public class AssetEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntry the asset entry
	 * @return the asset entry that was added
	 */
	public static AssetEntry addAssetEntry(AssetEntry assetEntry) {
		return getService().addAssetEntry(assetEntry);
	}

	public static boolean addAssetTagAssetEntries(
		long tagId, List<AssetEntry> assetEntries) {

		return getService().addAssetTagAssetEntries(tagId, assetEntries);
	}

	public static boolean addAssetTagAssetEntries(long tagId, long[] entryIds) {
		return getService().addAssetTagAssetEntries(tagId, entryIds);
	}

	public static boolean addAssetTagAssetEntry(
		long tagId, AssetEntry assetEntry) {

		return getService().addAssetTagAssetEntry(tagId, assetEntry);
	}

	public static boolean addAssetTagAssetEntry(long tagId, long entryId) {
		return getService().addAssetTagAssetEntry(tagId, entryId);
	}

	public static void clearAssetTagAssetEntries(long tagId) {
		getService().clearAssetTagAssetEntries(tagId);
	}

	/**
	 * Creates a new asset entry with the primary key. Does not add the asset entry to the database.
	 *
	 * @param entryId the primary key for the new asset entry
	 * @return the new asset entry
	 */
	public static AssetEntry createAssetEntry(long entryId) {
		return getService().createAssetEntry(entryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the asset entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntry the asset entry
	 * @return the asset entry that was removed
	 */
	public static AssetEntry deleteAssetEntry(AssetEntry assetEntry) {
		return getService().deleteAssetEntry(assetEntry);
	}

	/**
	 * Deletes the asset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry that was removed
	 * @throws PortalException if a asset entry with the primary key could not be found
	 */
	public static AssetEntry deleteAssetEntry(long entryId)
		throws PortalException {

		return getService().deleteAssetEntry(entryId);
	}

	public static void deleteAssetTagAssetEntries(
		long tagId, List<AssetEntry> assetEntries) {

		getService().deleteAssetTagAssetEntries(tagId, assetEntries);
	}

	public static void deleteAssetTagAssetEntries(long tagId, long[] entryIds) {
		getService().deleteAssetTagAssetEntries(tagId, entryIds);
	}

	public static void deleteAssetTagAssetEntry(
		long tagId, AssetEntry assetEntry) {

		getService().deleteAssetTagAssetEntry(tagId, assetEntry);
	}

	public static void deleteAssetTagAssetEntry(long tagId, long entryId) {
		getService().deleteAssetTagAssetEntry(tagId, entryId);
	}

	public static void deleteEntries(long companyId, String className)
		throws PortalException {

		getService().deleteEntries(companyId, className);
	}

	public static AssetEntry deleteEntry(AssetEntry entry)
		throws PortalException {

		return getService().deleteEntry(entry);
	}

	public static AssetEntry deleteEntry(long entryId) throws PortalException {
		return getService().deleteEntry(entryId);
	}

	public static AssetEntry deleteEntry(String className, long classPK)
		throws PortalException {

		return getService().deleteEntry(className, classPK);
	}

	public static void deleteGroupEntries(long groupId) throws PortalException {
		getService().deleteGroupEntries(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
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

	public static AssetEntry fetchAssetEntry(long entryId) {
		return getService().fetchAssetEntry(entryId);
	}

	public static AssetEntry fetchEntry(long entryId) {
		return getService().fetchEntry(entryId);
	}

	public static AssetEntry fetchEntry(long classNameId, long classPK) {
		return getService().fetchEntry(classNameId, classPK);
	}

	public static AssetEntry fetchEntry(long groupId, String classUuid) {
		return getService().fetchEntry(groupId, classUuid);
	}

	public static AssetEntry fetchEntry(String className, long classPK) {
		return getService().fetchEntry(className, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the asset entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @return the range of asset entries
	 */
	public static List<AssetEntry> getAssetEntries(int start, int end) {
		return getService().getAssetEntries(start, end);
	}

	/**
	 * Returns the number of asset entries.
	 *
	 * @return the number of asset entries
	 */
	public static int getAssetEntriesCount() {
		return getService().getAssetEntriesCount();
	}

	/**
	 * Returns the asset entry with the primary key.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry
	 * @throws PortalException if a asset entry with the primary key could not be found
	 */
	public static AssetEntry getAssetEntry(long entryId)
		throws PortalException {

		return getService().getAssetEntry(entryId);
	}

	public static List<AssetEntry> getAssetTagAssetEntries(long tagId) {
		return getService().getAssetTagAssetEntries(tagId);
	}

	public static List<AssetEntry> getAssetTagAssetEntries(
		long tagId, int start, int end) {

		return getService().getAssetTagAssetEntries(tagId, start, end);
	}

	public static List<AssetEntry> getAssetTagAssetEntries(
		long tagId, int start, int end,
		OrderByComparator<AssetEntry> orderByComparator) {

		return getService().getAssetTagAssetEntries(
			tagId, start, end, orderByComparator);
	}

	public static int getAssetTagAssetEntriesCount(long tagId) {
		return getService().getAssetTagAssetEntriesCount(tagId);
	}

	/**
	 * Returns the tagIds of the asset tags associated with the asset entry.
	 *
	 * @param entryId the entryId of the asset entry
	 * @return long[] the tagIds of asset tags associated with the asset entry
	 */
	public static long[] getAssetTagPrimaryKeys(long entryId) {
		return getService().getAssetTagPrimaryKeys(entryId);
	}

	public static List<AssetEntry> getCompanyEntries(
		long companyId, int start, int end) {

		return getService().getCompanyEntries(companyId, start, end);
	}

	public static int getCompanyEntriesCount(long companyId) {
		return getService().getCompanyEntriesCount(companyId);
	}

	public static List<AssetEntry> getEntries(
		com.liferay.asset.kernel.service.persistence.AssetEntryQuery
			entryQuery) {

		return getService().getEntries(entryQuery);
	}

	public static int getEntriesCount(
		com.liferay.asset.kernel.service.persistence.AssetEntryQuery
			entryQuery) {

		return getService().getEntriesCount(entryQuery);
	}

	public static AssetEntry getEntry(long entryId) throws PortalException {
		return getService().getEntry(entryId);
	}

	public static AssetEntry getEntry(long groupId, String classUuid)
		throws PortalException {

		return getService().getEntry(groupId, classUuid);
	}

	public static AssetEntry getEntry(String className, long classPK)
		throws PortalException {

		return getService().getEntry(className, classPK);
	}

	public static double getEntryPriority(long classNameId, long classPK) {
		return getService().getEntryPriority(classNameId, classPK);
	}

	public static double getEntryPriority(String className, long classPK) {
		return getService().getEntryPriority(className, classPK);
	}

	public static List<AssetEntry> getGroupEntries(long groupId) {
		return getService().getGroupEntries(groupId);
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

	public static boolean hasAssetTagAssetEntries(long tagId) {
		return getService().hasAssetTagAssetEntries(tagId);
	}

	public static boolean hasAssetTagAssetEntry(long tagId, long entryId) {
		return getService().hasAssetTagAssetEntry(tagId, entryId);
	}

	public static void incrementViewCounter(long userId, AssetEntry assetEntry)
		throws PortalException {

		getService().incrementViewCounter(userId, assetEntry);
	}

	public static AssetEntry incrementViewCounter(
			long companyId, long userId, String className, long classPK)
		throws PortalException {

		return getService().incrementViewCounter(
			companyId, userId, className, classPK);
	}

	public static void incrementViewCounter(
		long companyId, long userId, String className, long classPK,
		int increment) {

		getService().incrementViewCounter(
			companyId, userId, className, classPK, increment);
	}

	public static void reindex(List<AssetEntry> entries)
		throws PortalException {

		getService().reindex(entries);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, long[] classNameIds,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end,
		com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
			companyId, groupIds, userId, classNameIds, classTypeId, keywords,
			showNonindexable, statuses, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, long[] classNameIds,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end,
		com.liferay.portal.kernel.search.Sort[] sorts) {

		return getService().search(
			companyId, groupIds, userId, classNameIds, classTypeId, keywords,
			showNonindexable, statuses, start, end, sorts);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable, int status,
		int start, int end) {

		return getService().search(
			companyId, groupIds, userId, className, classTypeId, keywords,
			showNonindexable, status, start, end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end) {

		return getService().search(
			companyId, groupIds, userId, className, classTypeId, keywords,
			showNonindexable, statuses, start, end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end,
		com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
			companyId, groupIds, userId, className, classTypeId, keywords,
			showNonindexable, statuses, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses, int start, int end,
		com.liferay.portal.kernel.search.Sort[] sorts) {

		return getService().search(
			companyId, groupIds, userId, className, classTypeId, keywords,
			showNonindexable, statuses, start, end, sorts);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, int status, int start, int end) {

		return getService().search(
			companyId, groupIds, userId, className, classTypeId, keywords,
			status, start, end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int status, boolean andSearch, int start, int end) {

		return getService().search(
			companyId, groupIds, userId, className, classTypeId, userName,
			title, description, assetCategoryIds, assetTagNames,
			showNonindexable, status, andSearch, start, end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch, int start, int end) {

		return getService().search(
			companyId, groupIds, userId, className, classTypeId, userName,
			title, description, assetCategoryIds, assetTagNames,
			showNonindexable, statuses, andSearch, start, end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, int status,
		boolean andSearch, int start, int end) {

		return getService().search(
			companyId, groupIds, userId, className, classTypeId, userName,
			title, description, assetCategoryIds, assetTagNames, status,
			andSearch, start, end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		String keywords, int status, int start, int end) {

		return getService().search(
			companyId, groupIds, userId, className, keywords, status, start,
			end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long[] groupIds, long userId, String className,
		String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, int status,
		boolean andSearch, int start, int end) {

		return getService().search(
			companyId, groupIds, userId, className, userName, title,
			description, assetCategoryIds, assetTagNames, status, andSearch,
			start, end);
	}

	public static long searchCount(
		long companyId, long[] groupIds, long userId, long[] classNameIds,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses) {

		return getService().searchCount(
			companyId, groupIds, userId, classNameIds, classTypeId, keywords,
			showNonindexable, statuses);
	}

	public static long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, boolean showNonindexable,
		int[] statuses) {

		return getService().searchCount(
			companyId, groupIds, userId, className, classTypeId, keywords,
			showNonindexable, statuses);
	}

	public static long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String keywords, String assetCategoryIds,
		String assetTagNames, boolean showInvisible, boolean showNonindexable,
		int[] statuses, boolean andSearch) {

		return getService().searchCount(
			companyId, groupIds, userId, className, classTypeId, keywords,
			assetCategoryIds, assetTagNames, showInvisible, showNonindexable,
			statuses, andSearch);
	}

	public static long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showInvisible,
		boolean showNonindexable, int[] statuses, boolean andSearch) {

		return getService().searchCount(
			companyId, groupIds, userId, className, classTypeId, userName,
			title, description, assetCategoryIds, assetTagNames, showInvisible,
			showNonindexable, statuses, andSearch);
	}

	public static long searchCount(
		long companyId, long[] groupIds, long userId, String className,
		long classTypeId, String userName, String title, String description,
		String assetCategoryIds, String assetTagNames, boolean showNonindexable,
		int[] statuses, boolean andSearch) {

		return getService().searchCount(
			companyId, groupIds, userId, className, classTypeId, userName,
			title, description, assetCategoryIds, assetTagNames,
			showNonindexable, statuses, andSearch);
	}

	public static void setAssetTagAssetEntries(long tagId, long[] entryIds) {
		getService().setAssetTagAssetEntries(tagId, entryIds);
	}

	/**
	 * Updates the asset entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntry the asset entry
	 * @return the asset entry that was updated
	 */
	public static AssetEntry updateAssetEntry(AssetEntry assetEntry) {
		return getService().updateAssetEntry(assetEntry);
	}

	public static AssetEntry updateEntry(
			long userId, long groupId, java.util.Date createDate,
			java.util.Date modifiedDate, String className, long classPK,
			String classUuid, long classTypeId, long[] categoryIds,
			String[] tagNames, boolean listable, boolean visible,
			java.util.Date startDate, java.util.Date endDate,
			java.util.Date publishDate, java.util.Date expirationDate,
			String mimeType, String title, String description, String summary,
			String url, String layoutUuid, int height, int width,
			Double priority)
		throws PortalException {

		return getService().updateEntry(
			userId, groupId, createDate, modifiedDate, className, classPK,
			classUuid, classTypeId, categoryIds, tagNames, listable, visible,
			startDate, endDate, publishDate, expirationDate, mimeType, title,
			description, summary, url, layoutUuid, height, width, priority);
	}

	public static AssetEntry updateEntry(
			long userId, long groupId, java.util.Date createDate,
			java.util.Date modifiedDate, String className, long classPK,
			String classUuid, long classTypeId, long[] categoryIds,
			String[] tagNames, boolean listable, boolean visible,
			java.util.Date startDate, java.util.Date endDate,
			java.util.Date publishDate, java.util.Date expirationDate,
			String mimeType, String title, String description, String summary,
			String url, String layoutUuid, int height, int width,
			Double priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateEntry(
			userId, groupId, createDate, modifiedDate, className, classPK,
			classUuid, classTypeId, categoryIds, tagNames, listable, visible,
			startDate, endDate, publishDate, expirationDate, mimeType, title,
			description, summary, url, layoutUuid, height, width, priority,
			serviceContext);
	}

	public static AssetEntry updateEntry(
			long userId, long groupId, String className, long classPK,
			long[] categoryIds, String[] tagNames)
		throws PortalException {

		return getService().updateEntry(
			userId, groupId, className, classPK, categoryIds, tagNames);
	}

	public static AssetEntry updateEntry(
			String className, long classPK, java.util.Date publishDate,
			java.util.Date expirationDate, boolean listable, boolean visible)
		throws PortalException {

		return getService().updateEntry(
			className, classPK, publishDate, expirationDate, listable, visible);
	}

	public static AssetEntry updateVisible(AssetEntry entry, boolean visible)
		throws PortalException {

		return getService().updateVisible(entry, visible);
	}

	public static AssetEntry updateVisible(
			String className, long classPK, boolean visible)
		throws PortalException {

		return getService().updateVisible(className, classPK, visible);
	}

	public static void validate(
			long groupId, String className, long classPK, long classTypePK,
			long[] categoryIds, String[] tagNames)
		throws PortalException {

		getService().validate(
			groupId, className, classPK, classTypePK, categoryIds, tagNames);
	}

	public static void validate(
			long groupId, String className, long classTypePK,
			long[] categoryIds, String[] tagNames)
		throws PortalException {

		getService().validate(
			groupId, className, classTypePK, categoryIds, tagNames);
	}

	public static AssetEntryLocalService getService() {
		return _service;
	}

	public static void setService(AssetEntryLocalService service) {
		_service = service;
	}

	private static volatile AssetEntryLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-159373524