/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetTagGroupRel. This utility wraps
 * <code>com.liferay.portlet.asset.service.impl.AssetTagGroupRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetTagGroupRelLocalService
 * @generated
 */
public class AssetTagGroupRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetTagGroupRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset tag group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTagGroupRel the asset tag group rel
	 * @return the asset tag group rel that was added
	 */
	public static AssetTagGroupRel addAssetTagGroupRel(
		AssetTagGroupRel assetTagGroupRel) {

		return getService().addAssetTagGroupRel(assetTagGroupRel);
	}

	public static AssetTagGroupRel addAssetTagGroupRel(
			long groupId, long tagId, int depotEntryType)
		throws PortalException {

		return getService().addAssetTagGroupRel(groupId, tagId, depotEntryType);
	}

	/**
	 * Creates a new asset tag group rel with the primary key. Does not add the asset tag group rel to the database.
	 *
	 * @param assetTagGroupRelId the primary key for the new asset tag group rel
	 * @return the new asset tag group rel
	 */
	public static AssetTagGroupRel createAssetTagGroupRel(
		long assetTagGroupRelId) {

		return getService().createAssetTagGroupRel(assetTagGroupRelId);
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
	 * Deletes the asset tag group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTagGroupRel the asset tag group rel
	 * @return the asset tag group rel that was removed
	 */
	public static AssetTagGroupRel deleteAssetTagGroupRel(
		AssetTagGroupRel assetTagGroupRel) {

		return getService().deleteAssetTagGroupRel(assetTagGroupRel);
	}

	/**
	 * Deletes the asset tag group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTagGroupRelId the primary key of the asset tag group rel
	 * @return the asset tag group rel that was removed
	 * @throws PortalException if a asset tag group rel with the primary key could not be found
	 */
	public static AssetTagGroupRel deleteAssetTagGroupRel(
			long assetTagGroupRelId)
		throws PortalException {

		return getService().deleteAssetTagGroupRel(assetTagGroupRelId);
	}

	public static void deleteAssetTagGroupRelsByGroupId(long groupId) {
		getService().deleteAssetTagGroupRelsByGroupId(groupId);
	}

	public static void deleteAssetTagGroupRelsByTagId(long tagId) {
		getService().deleteAssetTagGroupRelsByTagId(tagId);
	}

	public static void deleteAssetTagGroupRelsByTagIdAndDepotEntryType(
			long tagId, int depotEntryType)
		throws PortalException {

		getService().deleteAssetTagGroupRelsByTagIdAndDepotEntryType(
			tagId, depotEntryType);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagGroupRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagGroupRelModelImpl</code>.
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

	public static AssetTagGroupRel fetchAssetTagGroupRel(
		long assetTagGroupRelId) {

		return getService().fetchAssetTagGroupRel(assetTagGroupRelId);
	}

	/**
	 * Returns the asset tag group rel matching the UUID and group.
	 *
	 * @param uuid the asset tag group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset tag group rel, or <code>null</code> if a matching asset tag group rel could not be found
	 */
	public static AssetTagGroupRel fetchAssetTagGroupRelByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchAssetTagGroupRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the asset tag group rel with the primary key.
	 *
	 * @param assetTagGroupRelId the primary key of the asset tag group rel
	 * @return the asset tag group rel
	 * @throws PortalException if a asset tag group rel with the primary key could not be found
	 */
	public static AssetTagGroupRel getAssetTagGroupRel(long assetTagGroupRelId)
		throws PortalException {

		return getService().getAssetTagGroupRel(assetTagGroupRelId);
	}

	/**
	 * Returns the asset tag group rel matching the UUID and group.
	 *
	 * @param uuid the asset tag group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset tag group rel
	 * @throws PortalException if a matching asset tag group rel could not be found
	 */
	public static AssetTagGroupRel getAssetTagGroupRelByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getAssetTagGroupRelByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the asset tag group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetTagGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset tag group rels
	 * @param end the upper bound of the range of asset tag group rels (not inclusive)
	 * @return the range of asset tag group rels
	 */
	public static List<AssetTagGroupRel> getAssetTagGroupRels(
		int start, int end) {

		return getService().getAssetTagGroupRels(start, end);
	}

	public static List<AssetTagGroupRel> getAssetTagGroupRelsByGroupId(
		long groupId) {

		return getService().getAssetTagGroupRelsByGroupId(groupId);
	}

	public static List<AssetTagGroupRel>
		getAssetTagGroupRelsByGroupIdAndDepotEntryType(
			long groupId, int depotEntryType) {

		return getService().getAssetTagGroupRelsByGroupIdAndDepotEntryType(
			groupId, depotEntryType);
	}

	public static List<AssetTagGroupRel> getAssetTagGroupRelsByTagId(
		long tagId) {

		return getService().getAssetTagGroupRelsByTagId(tagId);
	}

	public static List<AssetTagGroupRel>
		getAssetTagGroupRelsByTagIdAndDepotEntryType(
			long tagId, int depotEntryType) {

		return getService().getAssetTagGroupRelsByTagIdAndDepotEntryType(
			tagId, depotEntryType);
	}

	/**
	 * Returns all the asset tag group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset tag group rels
	 * @param companyId the primary key of the company
	 * @return the matching asset tag group rels, or an empty list if no matches were found
	 */
	public static List<AssetTagGroupRel> getAssetTagGroupRelsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getAssetTagGroupRelsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of asset tag group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset tag group rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset tag group rels
	 * @param end the upper bound of the range of asset tag group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset tag group rels, or an empty list if no matches were found
	 */
	public static List<AssetTagGroupRel> getAssetTagGroupRelsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetTagGroupRel> orderByComparator) {

		return getService().getAssetTagGroupRelsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset tag group rels.
	 *
	 * @return the number of asset tag group rels
	 */
	public static int getAssetTagGroupRelsCount() {
		return getService().getAssetTagGroupRelsCount();
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

	public static void setAssetTagGroupRels(
			long tagId, long[] groupIds, int depotEntryType)
		throws PortalException {

		getService().setAssetTagGroupRels(tagId, groupIds, depotEntryType);
	}

	/**
	 * Updates the asset tag group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetTagGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetTagGroupRel the asset tag group rel
	 * @return the asset tag group rel that was updated
	 */
	public static AssetTagGroupRel updateAssetTagGroupRel(
		AssetTagGroupRel assetTagGroupRel) {

		return getService().updateAssetTagGroupRel(assetTagGroupRel);
	}

	public static AssetTagGroupRelLocalService getService() {
		return _service;
	}

	public static void setService(AssetTagGroupRelLocalService service) {
		_service = service;
	}

	private static volatile AssetTagGroupRelLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:139069871