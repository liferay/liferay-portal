/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetTag. This utility wraps
 * <code>com.liferay.portlet.asset.service.impl.AssetTagLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetTagLocalService
 * @generated
 */
public class AssetTagLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetTagLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean addAssetEntryAssetTag(
		long entryId, AssetTag assetTag) {

		return getService().addAssetEntryAssetTag(entryId, assetTag);
	}

	public static boolean addAssetEntryAssetTag(long entryId, long tagId) {
		return getService().addAssetEntryAssetTag(entryId, tagId);
	}

	public static boolean addAssetEntryAssetTags(
		long entryId, List<AssetTag> assetTags) {

		return getService().addAssetEntryAssetTags(entryId, assetTags);
	}

	public static boolean addAssetEntryAssetTags(long entryId, long[] tagIds) {
		return getService().addAssetEntryAssetTags(entryId, tagIds);
	}

	/**
	 * Adds the asset tag to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTag the asset tag
	 * @return the asset tag that was added
	 */
	public static AssetTag addAssetTag(AssetTag assetTag) {
		return getService().addAssetTag(assetTag);
	}

	/**
	 * Adds an asset tag.
	 *
	 * @param externalReferenceCode
	 * @param userId                the primary key of the user adding the asset tag
	 * @param groupId               the primary key of the group in which the asset tag is to
	 be added
	 * @param name                  the asset tag's name
	 * @param serviceContext        the service context to be applied
	 * @return the asset tag that was added
	 */
	public static AssetTag addTag(
			String externalReferenceCode, long userId, long groupId,
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTag(
			externalReferenceCode, userId, groupId, name, serviceContext);
	}

	/**
	 * Returns the asset tags matching the group and names, creating new asset
	 * tags matching the names if the group doesn't already have them.
	 *
	 * <p>
	 * For each name, if an asset tag with the name doesn't already exist in the
	 * group, this method creates a new asset tag with the name in the group.
	 * </p>
	 *
	 * @param userId the primary key of the user checking the asset tags
	 * @param group the group in which to check the asset tags
	 * @param names the asset tag names
	 * @return the asset tags matching the group and names and new asset tags
	 matching the names that don't already exist in the group
	 */
	public static List<AssetTag> checkTags(
			long userId, com.liferay.portal.kernel.model.Group group,
			String[] names)
		throws PortalException {

		return getService().checkTags(userId, group, names);
	}

	/**
	 * Returns the asset tags matching the group and names, creating new asset
	 * tags matching the names if the group doesn't already have them.
	 *
	 * @param userId the primary key of the user checking the asset tags
	 * @param groupId the primary key of the group in which check the asset
	 tags
	 * @param names the asset tag names
	 * @return the asset tags matching the group and names and new asset tags
	 matching the names that don't already exist in the group
	 */
	public static List<AssetTag> checkTags(
			long userId, long groupId, String[] names)
		throws PortalException {

		return getService().checkTags(userId, groupId, names);
	}

	public static void clearAssetEntryAssetTags(long entryId) {
		getService().clearAssetEntryAssetTags(entryId);
	}

	/**
	 * Creates a new asset tag with the primary key. Does not add the asset tag to the database.
	 *
	 * @param tagId the primary key for the new asset tag
	 * @return the new asset tag
	 */
	public static AssetTag createAssetTag(long tagId) {
		return getService().createAssetTag(tagId);
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
	 * Decrements the number of assets to which the asset tag has been applied.
	 *
	 * @param tagId the primary key of the asset tag
	 * @param classNameId the class name ID of the entity to which the asset
	 tag had been applied
	 * @return the asset tag
	 */
	public static AssetTag decrementAssetCount(long tagId, long classNameId)
		throws PortalException {

		return getService().decrementAssetCount(tagId, classNameId);
	}

	public static void deleteAssetEntryAssetTag(
		long entryId, AssetTag assetTag) {

		getService().deleteAssetEntryAssetTag(entryId, assetTag);
	}

	public static void deleteAssetEntryAssetTag(long entryId, long tagId) {
		getService().deleteAssetEntryAssetTag(entryId, tagId);
	}

	public static void deleteAssetEntryAssetTags(
		long entryId, List<AssetTag> assetTags) {

		getService().deleteAssetEntryAssetTags(entryId, assetTags);
	}

	public static void deleteAssetEntryAssetTags(long entryId, long[] tagIds) {
		getService().deleteAssetEntryAssetTags(entryId, tagIds);
	}

	/**
	 * Deletes the asset tag from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTag the asset tag
	 * @return the asset tag that was removed
	 */
	public static AssetTag deleteAssetTag(AssetTag assetTag) {
		return getService().deleteAssetTag(assetTag);
	}

	/**
	 * Deletes the asset tag with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag that was removed
	 * @throws PortalException if a asset tag with the primary key could not be found
	 */
	public static AssetTag deleteAssetTag(long tagId) throws PortalException {
		return getService().deleteAssetTag(tagId);
	}

	/**
	 * Deletes all asset tags in the group.
	 *
	 * @param groupId the primary key of the group in which to delete all asset
	 tags
	 */
	public static void deleteGroupTags(long groupId) throws PortalException {
		getService().deleteGroupTags(groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the asset tag.
	 *
	 * @param tag the asset tag to be deleted
	 */
	public static void deleteTag(AssetTag tag) throws PortalException {
		getService().deleteTag(tag);
	}

	/**
	 * Deletes the asset tag.
	 *
	 * @param tagId the primary key of the asset tag
	 */
	public static void deleteTag(long tagId) throws PortalException {
		getService().deleteTag(tagId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagModelImpl</code>.
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

	public static AssetTag fetchAssetTag(long tagId) {
		return getService().fetchAssetTag(tagId);
	}

	public static AssetTag fetchAssetTagByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchAssetTagByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset tag matching the UUID and group.
	 *
	 * @param uuid the asset tag's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset tag, or <code>null</code> if a matching asset tag could not be found
	 */
	public static AssetTag fetchAssetTagByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchAssetTagByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the asset tag with the name in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param name the asset tag's name
	 * @return the asset tag with the name in the group or <code>null</code> if
	 it could not be found
	 */
	public static AssetTag fetchTag(long groupId, String name) {
		return getService().fetchTag(groupId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<AssetTag> getAssetEntryAssetTags(long entryId) {
		return getService().getAssetEntryAssetTags(entryId);
	}

	public static List<AssetTag> getAssetEntryAssetTags(
		long entryId, int start, int end) {

		return getService().getAssetEntryAssetTags(entryId, start, end);
	}

	public static List<AssetTag> getAssetEntryAssetTags(
		long entryId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return getService().getAssetEntryAssetTags(
			entryId, start, end, orderByComparator);
	}

	public static int getAssetEntryAssetTagsCount(long entryId) {
		return getService().getAssetEntryAssetTagsCount(entryId);
	}

	/**
	 * Returns the entryIds of the asset entries associated with the asset tag.
	 *
	 * @param tagId the tagId of the asset tag
	 * @return long[] the entryIds of asset entries associated with the asset tag
	 */
	public static long[] getAssetEntryPrimaryKeys(long tagId) {
		return getService().getAssetEntryPrimaryKeys(tagId);
	}

	/**
	 * Returns the asset tag with the primary key.
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag
	 * @throws PortalException if a asset tag with the primary key could not be found
	 */
	public static AssetTag getAssetTag(long tagId) throws PortalException {
		return getService().getAssetTag(tagId);
	}

	public static AssetTag getAssetTagByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getAssetTagByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset tag matching the UUID and group.
	 *
	 * @param uuid the asset tag's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset tag
	 * @throws PortalException if a matching asset tag could not be found
	 */
	public static AssetTag getAssetTagByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getAssetTagByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the asset tags.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the range of asset tags
	 */
	public static List<AssetTag> getAssetTags(int start, int end) {
		return getService().getAssetTags(start, end);
	}

	/**
	 * Returns all the asset tags matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset tags
	 * @param companyId the primary key of the company
	 * @return the matching asset tags, or an empty list if no matches were found
	 */
	public static List<AssetTag> getAssetTagsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getAssetTagsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of asset tags matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset tags
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset tags, or an empty list if no matches were found
	 */
	public static List<AssetTag> getAssetTagsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return getService().getAssetTagsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset tags.
	 *
	 * @return the number of asset tags
	 */
	public static int getAssetTagsCount() {
		return getService().getAssetTagsCount();
	}

	/**
	 * Returns the number of asset tags in the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of asset tags in the company
	 */
	public static int getCompanyTagsCount(long companyId) {
		return getService().getCompanyTagsCount(companyId);
	}

	/**
	 * Returns the asset tags of the asset entry.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset tags of the asset entry
	 */
	public static List<AssetTag> getEntryTags(long entryId) {
		return getService().getEntryTags(entryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	/**
	 * Returns the asset tags in the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @return the asset tags in the groups
	 */
	public static List<AssetTag> getGroupsTags(long[] groupIds) {
		return getService().getGroupsTags(groupIds);
	}

	/**
	 * Returns the asset tags in the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the asset tags in the group
	 */
	public static List<AssetTag> getGroupTags(long groupId) {
		return getService().getGroupTags(groupId);
	}

	/**
	 * Returns a range of all the asset tags in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the range of matching asset tags
	 */
	public static List<AssetTag> getGroupTags(
		long groupId, int start, int end) {

		return getService().getGroupTags(groupId, start, end);
	}

	/**
	 * Returns a range of all the asset tags in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @param orderByComparator the comparator to order the asset tags
	 (optionally <code>null</code>)
	 * @return the range of matching asset tags
	 */
	public static List<AssetTag> getGroupTags(
		long groupId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return getService().getGroupTags(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset tags in the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the number of asset tags in the group
	 */
	public static int getGroupTagsCount(long groupId) {
		return getService().getGroupTagsCount(groupId);
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
	 * Returns the asset tag with the primary key.
	 *
	 * @param tagId the primary key of the asset tag
	 * @return the asset tag with the primary key
	 */
	public static AssetTag getTag(long tagId) throws PortalException {
		return getService().getTag(tagId);
	}

	/**
	 * Returns the asset tag with the name in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param name the name of the asset tag
	 * @return the asset tag with the name in the group
	 */
	public static AssetTag getTag(long groupId, String name)
		throws PortalException {

		return getService().getTag(groupId, name);
	}

	/**
	 * Returns the primary keys of the asset tags with the names in the group.
	 *
	 * @param groupId the primary key of the group
	 * @param names the names of the asset tags
	 * @return the primary keys of the asset tags with the names in the group
	 */
	public static long[] getTagIds(long groupId, String[] names) {
		return getService().getTagIds(groupId, names);
	}

	/**
	 * Returns the primary keys of the asset tags with the name in the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param name the name of the asset tags
	 * @return the primary keys of the asset tags with the name in the groups
	 */
	public static long[] getTagIds(long[] groupIds, String name) {
		return getService().getTagIds(groupIds, name);
	}

	/**
	 * Returns the primary keys of the asset tags with the names in the groups.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param names the names of the asset tags
	 * @return the primary keys of the asset tags with the names in the groups
	 */
	public static long[] getTagIds(long[] groupIds, String[] names) {
		return getService().getTagIds(groupIds, names);
	}

	/**
	 * Returns the primary keys of the asset tags with the names.
	 *
	 * @param name the name of the asset tags
	 * @return the primary keys of the asset tags with the names
	 */
	public static long[] getTagIds(String name) {
		return getService().getTagIds(name);
	}

	/**
	 * Returns the names of all the asset tags.
	 *
	 * @return the names of all the asset tags
	 */
	public static String[] getTagNames() {
		return getService().getTagNames();
	}

	/**
	 * Returns the names of the asset tags of the entity.
	 *
	 * @param classNameId the class name ID of the entity
	 * @param classPK the primary key of the entity
	 * @return the names of the asset tags of the entity
	 */
	public static String[] getTagNames(long classNameId, long classPK) {
		return getService().getTagNames(classNameId, classPK);
	}

	/**
	 * Returns the names of the asset tags of the entity
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the names of the asset tags of the entity
	 */
	public static String[] getTagNames(String className, long classPK) {
		return getService().getTagNames(className, classPK);
	}

	/**
	 * Returns all the asset tags.
	 *
	 * @return the asset tags
	 */
	public static List<AssetTag> getTags() {
		return getService().getTags();
	}

	/**
	 * Returns the asset tags of the entity.
	 *
	 * @param classNameId the class name ID of the entity
	 * @param classPK the primary key of the entity
	 * @return the asset tags of the entity
	 */
	public static List<AssetTag> getTags(long classNameId, long classPK) {
		return getService().getTags(classNameId, classPK);
	}

	public static List<AssetTag> getTags(
		long groupId, long classNameId, String name) {

		return getService().getTags(groupId, classNameId, name);
	}

	public static List<AssetTag> getTags(
		long groupId, long classNameId, String name, int start, int end) {

		return getService().getTags(groupId, classNameId, name, start, end);
	}

	/**
	 * Returns the asset tags of the entity.
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the asset tags of the entity
	 */
	public static List<AssetTag> getTags(String className, long classPK) {
		return getService().getTags(className, classPK);
	}

	public static int getTagsSize(long groupId, long classNameId, String name) {
		return getService().getTagsSize(groupId, classNameId, name);
	}

	public static boolean hasAssetEntryAssetTag(long entryId, long tagId) {
		return getService().hasAssetEntryAssetTag(entryId, tagId);
	}

	public static boolean hasAssetEntryAssetTags(long entryId) {
		return getService().hasAssetEntryAssetTags(entryId);
	}

	/**
	 * Returns <code>true</code> if the group contains an asset tag with the
	 * name.
	 *
	 * @param groupId the primary key of the group
	 * @param name the name of the asset tag
	 * @return <code>true</code> if the group contains an asset tag with the
	 name; <code>false</code> otherwise.
	 */
	public static boolean hasTag(long groupId, String name) {
		return getService().hasTag(groupId, name);
	}

	/**
	 * Increments the number of assets to which the asset tag has been applied.
	 *
	 * @param tagId the primary key of the asset tag
	 * @param classNameId the class name ID of the entity to which the asset
	 tag is being applied
	 * @return the asset tag
	 */
	public static AssetTag incrementAssetCount(long tagId, long classNameId)
		throws PortalException {

		return getService().incrementAssetCount(tagId, classNameId);
	}

	/**
	 * Replaces all occurrences of the first asset tag with the second asset tag
	 * and deletes the first asset tag.
	 *
	 * @param fromTagId the primary key of the asset tag to be replaced
	 * @param toTagId the primary key of the asset tag to apply to the asset
	 entries of the other asset tag
	 */
	public static void mergeTags(long fromTagId, long toTagId)
		throws PortalException {

		getService().mergeTags(fromTagId, toTagId);
	}

	/**
	 * Returns the asset tags in the group whose names match the pattern.
	 *
	 * @param groupId the primary key of the group
	 * @param name the pattern to match
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the asset tags in the group whose names match the pattern
	 */
	public static List<AssetTag> search(
		long groupId, String name, int start, int end) {

		return getService().search(groupId, name, start, end);
	}

	/**
	 * Returns the asset tags in the groups whose names match the pattern.
	 *
	 * @param groupIds the primary keys of the groups
	 * @param name the pattern to match
	 * @param start the lower bound of the range of asset tags
	 * @param end the upper bound of the range of asset tags (not inclusive)
	 * @return the asset tags in the groups whose names match the pattern
	 */
	public static List<AssetTag> search(
		long[] groupIds, String name, int start, int end) {

		return getService().search(groupIds, name, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AssetTag> searchTags(
				long[] groupIds, String name, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchTags(groupIds, name, start, end, sort);
	}

	public static void setAssetEntryAssetTags(long entryId, long[] tagIds) {
		getService().setAssetEntryAssetTags(entryId, tagIds);
	}

	public static void subscribeTag(long userId, long groupId, long tagId)
		throws PortalException {

		getService().subscribeTag(userId, groupId, tagId);
	}

	public static void unsubscribeTag(long userId, long tagId)
		throws PortalException {

		getService().unsubscribeTag(userId, tagId);
	}

	/**
	 * Updates the asset tag in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTag the asset tag
	 * @return the asset tag that was updated
	 */
	public static AssetTag updateAssetTag(AssetTag assetTag) {
		return getService().updateAssetTag(assetTag);
	}

	public static AssetTag updateTag(
			String externalReferenceCode, long userId, long tagId, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateTag(
			externalReferenceCode, userId, tagId, name, serviceContext);
	}

	public static AssetTagLocalService getService() {
		return _service;
	}

	public static void setService(AssetTagLocalService service) {
		_service = service;
	}

	private static volatile AssetTagLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1617078009