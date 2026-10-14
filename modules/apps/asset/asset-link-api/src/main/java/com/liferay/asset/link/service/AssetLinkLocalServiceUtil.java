/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.link.service;

import com.liferay.asset.link.model.AssetLink;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetLink. This utility wraps
 * <code>com.liferay.asset.link.service.impl.AssetLinkLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetLinkLocalService
 * @generated
 */
public class AssetLinkLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.link.service.impl.AssetLinkLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was added
	 */
	public static AssetLink addAssetLink(AssetLink assetLink) {
		return getService().addAssetLink(assetLink);
	}

	/**
	 * Adds a new asset link.
	 *
	 * @param userId the primary key of the link's creator
	 * @param entryId1 the primary key of the first asset entry
	 * @param entryId2 the primary key of the second asset entry
	 * @param type the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @param weight the weight of the relationship, allowing precedence
	 ordering of links
	 * @return the asset link
	 */
	public static AssetLink addLink(
			long userId, long entryId1, long entryId2, int type, int weight)
		throws PortalException {

		return getService().addLink(userId, entryId1, entryId2, type, weight);
	}

	/**
	 * Creates a new asset link with the primary key. Does not add the asset link to the database.
	 *
	 * @param linkId the primary key for the new asset link
	 * @return the new asset link
	 */
	public static AssetLink createAssetLink(long linkId) {
		return getService().createAssetLink(linkId);
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
	 * Deletes the asset link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was removed
	 */
	public static AssetLink deleteAssetLink(AssetLink assetLink) {
		return getService().deleteAssetLink(assetLink);
	}

	/**
	 * Deletes the asset link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link that was removed
	 * @throws PortalException if a asset link with the primary key could not be found
	 */
	public static AssetLink deleteAssetLink(long linkId)
		throws PortalException {

		return getService().deleteAssetLink(linkId);
	}

	public static void deleteGroupLinks(long groupId) {
		getService().deleteGroupLinks(groupId);
	}

	/**
	 * Deletes the asset link.
	 *
	 * @param link the asset link
	 */
	public static void deleteLink(AssetLink link) {
		getService().deleteLink(link);
	}

	/**
	 * Deletes the asset link.
	 *
	 * @param linkId the primary key of the asset link
	 */
	public static void deleteLink(long linkId) throws PortalException {
		getService().deleteLink(linkId);
	}

	/**
	 * Deletes all links associated with the asset entry.
	 *
	 * @param entryId the primary key of the asset entry
	 */
	public static void deleteLinks(long entryId) {
		getService().deleteLinks(entryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
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

	public static AssetLink fetchAssetLink(long linkId) {
		return getService().fetchAssetLink(linkId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the asset link with the primary key.
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link
	 * @throws PortalException if a asset link with the primary key could not be found
	 */
	public static AssetLink getAssetLink(long linkId) throws PortalException {
		return getService().getAssetLink(linkId);
	}

	/**
	 * Returns a range of all the asset links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset links
	 * @param end the upper bound of the range of asset links (not inclusive)
	 * @return the range of asset links
	 */
	public static List<AssetLink> getAssetLinks(int start, int end) {
		return getService().getAssetLinks(start, end);
	}

	/**
	 * Returns the number of asset links.
	 *
	 * @return the number of asset links
	 */
	public static int getAssetLinksCount() {
		return getService().getAssetLinksCount();
	}

	/**
	 * Returns all the asset links whose first entry ID is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset links whose first entry ID is the given entry ID
	 */
	public static List<AssetLink> getDirectLinks(long entryId) {
		return getService().getDirectLinks(entryId);
	}

	public static List<AssetLink> getDirectLinks(
		long entryId, boolean excludeInvisibleLinks) {

		return getService().getDirectLinks(entryId, excludeInvisibleLinks);
	}

	/**
	 * Returns all the asset links of the given link type whose first entry ID
	 * is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose first entry ID is
	 the given entry ID
	 */
	public static List<AssetLink> getDirectLinks(long entryId, int typeId) {
		return getService().getDirectLinks(entryId, typeId);
	}

	public static List<AssetLink> getDirectLinks(
		long entryId, int typeId, boolean excludeInvisibleLinks) {

		return getService().getDirectLinks(
			entryId, typeId, excludeInvisibleLinks);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns all the asset links whose first or second entry ID is the given
	 * entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset links whose first or second entry ID is the given entry
	 ID
	 */
	public static List<AssetLink> getLinks(long entryId) {
		return getService().getLinks(entryId);
	}

	public static List<AssetLink> getLinks(
		long groupId, java.util.Date startDate, java.util.Date endDate,
		int start, int end) {

		return getService().getLinks(groupId, startDate, endDate, start, end);
	}

	/**
	 * Returns all the asset links of the given link type whose first or second
	 * entry ID is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose first or second
	 entry ID is the given entry ID
	 */
	public static List<AssetLink> getLinks(long entryId, int typeId) {
		return getService().getLinks(entryId, typeId);
	}

	/**
	 * Returns all the asset links of an AssetEntry.
	 *
	 * @param classNameId AssetEntry's classNameId
	 * @param classPK AssetEntry's classPK
	 * @return the asset links of the given entry params
	 */
	public static List<AssetLink> getLinks(long classNameId, long classPK) {
		return getService().getLinks(classNameId, classPK);
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
	 * Returns all the asset links of the given link type whose second entry ID
	 * is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose second entry ID is
	 the given entry ID
	 */
	public static List<AssetLink> getReverseLinks(long entryId, int typeId) {
		return getService().getReverseLinks(entryId, typeId);
	}

	/**
	 * Updates the asset link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was updated
	 */
	public static AssetLink updateAssetLink(AssetLink assetLink) {
		return getService().updateAssetLink(assetLink);
	}

	public static AssetLink updateLink(
			long userId, long entryId1, long entryId2, int typeId, int weight)
		throws PortalException {

		return getService().updateLink(
			userId, entryId1, entryId2, typeId, weight);
	}

	/**
	 * Updates all links of the asset entry, replacing them with links
	 * associating the asset entry with the asset entries of the given link
	 * entry IDs.
	 *
	 * <p>
	 * If no link exists with a given link entry ID, a new link is created
	 * associating the current asset entry with the asset entry of that link
	 * entry ID. An existing link is deleted if either of its entry IDs is not
	 * contained in the given link entry IDs.
	 * </p>
	 *
	 * @param userId the primary key of the user updating the links
	 * @param entryId the primary key of the asset entry to be managed
	 * @param linkEntryIds the primary keys of the asset entries to be linked
	 with the asset entry to be managed
	 * @param typeId the type of the asset links to be created. Acceptable
	 values include {@link AssetLinkConstants#TYPE_RELATED} which is a
	 bidirectional relationship and {@link
	 AssetLinkConstants#TYPE_CHILD} which is a unidirectional
	 relationship. For more information see {@link AssetLinkConstants}
	 */
	public static void updateLinks(
			long userId, long entryId, long[] linkEntryIds, int typeId)
		throws PortalException {

		getService().updateLinks(userId, entryId, linkEntryIds, typeId);
	}

	public static AssetLinkLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetLinkLocalService> _serviceSnapshot =
		new Snapshot<>(
			AssetLinkLocalServiceUtil.class, AssetLinkLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1372008193