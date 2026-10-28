/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetListEntryAssetEntryRel. This utility wraps
 * <code>com.liferay.asset.list.service.impl.AssetListEntryAssetEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAssetEntryRelLocalService
 * @generated
 */
public class AssetListEntryAssetEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.list.service.impl.AssetListEntryAssetEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset list entry asset entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryAssetEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryAssetEntryRel the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel that was added
	 */
	public static AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel) {

		return getService().addAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRel);
	}

	public static AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			int position,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntryAssetEntryRel(
			assetListEntryId, assetEntryId, segmentsEntryId, position,
			serviceContext);
	}

	public static AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntryAssetEntryRel(
			assetListEntryId, assetEntryId, segmentsEntryId, serviceContext);
	}

	public static AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, String audienceEntryERC,
			long segmentsEntryId, int position,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntryAssetEntryRel(
			assetListEntryId, assetEntryId, audienceEntryERC, segmentsEntryId,
			position, serviceContext);
	}

	public static AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel(
			long assetListEntryId, long assetEntryId, String audienceEntryERC,
			long segmentsEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntryAssetEntryRel(
			assetListEntryId, assetEntryId, audienceEntryERC, segmentsEntryId,
			serviceContext);
	}

	/**
	 * Creates a new asset list entry asset entry rel with the primary key. Does not add the asset list entry asset entry rel to the database.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key for the new asset list entry asset entry rel
	 * @return the new asset list entry asset entry rel
	 */
	public static AssetListEntryAssetEntryRel createAssetListEntryAssetEntryRel(
		long assetListEntryAssetEntryRelId) {

		return getService().createAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelId);
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
	 * Deletes the asset list entry asset entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryAssetEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryAssetEntryRel the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel that was removed
	 * @throws PortalException
	 */
	public static AssetListEntryAssetEntryRel deleteAssetListEntryAssetEntryRel(
			AssetListEntryAssetEntryRel assetListEntryAssetEntryRel)
		throws PortalException {

		return getService().deleteAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRel);
	}

	/**
	 * Deletes the asset list entry asset entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryAssetEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryAssetEntryRelId the primary key of the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel that was removed
	 * @throws PortalException if a asset list entry asset entry rel with the primary key could not be found
	 */
	public static AssetListEntryAssetEntryRel deleteAssetListEntryAssetEntryRel(
			long assetListEntryAssetEntryRelId)
		throws PortalException {

		return getService().deleteAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelId);
	}

	public static AssetListEntryAssetEntryRel deleteAssetListEntryAssetEntryRel(
			long assetListEntryId, long segmentsEntryId, int position)
		throws PortalException {

		return getService().deleteAssetListEntryAssetEntryRel(
			assetListEntryId, segmentsEntryId, position);
	}

	public static AssetListEntryAssetEntryRel deleteAssetListEntryAssetEntryRel(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId, int position)
		throws PortalException {

		return getService().deleteAssetListEntryAssetEntryRel(
			assetListEntryId, audienceEntryERC, segmentsEntryId, position);
	}

	public static void deleteAssetListEntryAssetEntryRelByAssetEntryId(
			long assetEntryId)
		throws PortalException {

		getService().deleteAssetListEntryAssetEntryRelByAssetEntryId(
			assetEntryId);
	}

	public static void deleteAssetListEntryAssetEntryRelByAssetListEntryId(
		long assetListEntryId) {

		getService().deleteAssetListEntryAssetEntryRelByAssetListEntryId(
			assetListEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
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

	public static AssetListEntryAssetEntryRel fetchAssetListEntryAssetEntryRel(
		long assetListEntryAssetEntryRelId) {

		return getService().fetchAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelId);
	}

	/**
	 * Returns the asset list entry asset entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry asset entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public static AssetListEntryAssetEntryRel
		fetchAssetListEntryAssetEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchAssetListEntryAssetEntryRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the asset list entry asset entry rel with the primary key.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key of the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel
	 * @throws PortalException if a asset list entry asset entry rel with the primary key could not be found
	 */
	public static AssetListEntryAssetEntryRel getAssetListEntryAssetEntryRel(
			long assetListEntryAssetEntryRelId)
		throws PortalException {

		return getService().getAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelId);
	}

	public static List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRelByAssetEntryId(long assetEntryId) {

		return getService().getAssetListEntryAssetEntryRelByAssetEntryId(
			assetEntryId);
	}

	/**
	 * Returns the asset list entry asset entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry asset entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry asset entry rel
	 * @throws PortalException if a matching asset list entry asset entry rel could not be found
	 */
	public static AssetListEntryAssetEntryRel
			getAssetListEntryAssetEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getAssetListEntryAssetEntryRelByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the asset list entry asset entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of asset list entry asset entry rels
	 */
	public static List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRels(int start, int end) {

		return getService().getAssetListEntryAssetEntryRels(start, end);
	}

	public static List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRels(
			long assetListEntryId, int start, int end) {

		return getService().getAssetListEntryAssetEntryRels(
			assetListEntryId, start, end);
	}

	public static List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRels(
			long assetListEntryId, long segmentsEntryId, int start, int end) {

		return getService().getAssetListEntryAssetEntryRels(
			assetListEntryId, segmentsEntryId, start, end);
	}

	public static List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRels(
			long assetListEntryId, long[] segmentsEntryIds, int start,
			int end) {

		return getService().getAssetListEntryAssetEntryRels(
			assetListEntryId, segmentsEntryIds, start, end);
	}

	public static List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRels(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId, int start, int end) {

		return getService().getAssetListEntryAssetEntryRels(
			assetListEntryId, audienceEntryERC, segmentsEntryId, start, end);
	}

	/**
	 * Returns all the asset list entry asset entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry asset entry rels
	 * @param companyId the primary key of the company
	 * @return the matching asset list entry asset entry rels, or an empty list if no matches were found
	 */
	public static List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getAssetListEntryAssetEntryRelsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of asset list entry asset entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry asset entry rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset list entry asset entry rels, or an empty list if no matches were found
	 */
	public static List<AssetListEntryAssetEntryRel>
		getAssetListEntryAssetEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<AssetListEntryAssetEntryRel> orderByComparator) {

		return getService().getAssetListEntryAssetEntryRelsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset list entry asset entry rels.
	 *
	 * @return the number of asset list entry asset entry rels
	 */
	public static int getAssetListEntryAssetEntryRelsCount() {
		return getService().getAssetListEntryAssetEntryRelsCount();
	}

	public static int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId) {

		return getService().getAssetListEntryAssetEntryRelsCount(
			assetListEntryId);
	}

	public static int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId, long segmentsEntryId) {

		return getService().getAssetListEntryAssetEntryRelsCount(
			assetListEntryId, segmentsEntryId);
	}

	public static int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId, long[] segmentsEntryIds) {

		return getService().getAssetListEntryAssetEntryRelsCount(
			assetListEntryId, segmentsEntryIds);
	}

	public static int getAssetListEntryAssetEntryRelsCount(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId) {

		return getService().getAssetListEntryAssetEntryRelsCount(
			assetListEntryId, audienceEntryERC, segmentsEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
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

	public static AssetListEntryAssetEntryRel moveAssetListEntryAssetEntryRel(
			long assetListEntryId, long segmentsEntryId, int position,
			int newPosition)
		throws PortalException {

		return getService().moveAssetListEntryAssetEntryRel(
			assetListEntryId, segmentsEntryId, position, newPosition);
	}

	public static AssetListEntryAssetEntryRel moveAssetListEntryAssetEntryRel(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId, int position, int newPosition)
		throws PortalException {

		return getService().moveAssetListEntryAssetEntryRel(
			assetListEntryId, audienceEntryERC, segmentsEntryId, position,
			newPosition);
	}

	/**
	 * Updates the asset list entry asset entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryAssetEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryAssetEntryRel the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel that was updated
	 */
	public static AssetListEntryAssetEntryRel updateAssetListEntryAssetEntryRel(
		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel) {

		return getService().updateAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRel);
	}

	public static AssetListEntryAssetEntryRel updateAssetListEntryAssetEntryRel(
			long assetListEntryAssetEntryRelId, long assetListEntryId,
			long assetEntryId, long segmentsEntryId, int position)
		throws PortalException {

		return getService().updateAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelId, assetListEntryId, assetEntryId,
			segmentsEntryId, position);
	}

	public static AssetListEntryAssetEntryRel updateAssetListEntryAssetEntryRel(
			long assetListEntryAssetEntryRelId, long assetListEntryId,
			long assetEntryId, String audienceEntryERC, long segmentsEntryId,
			int position)
		throws PortalException {

		return getService().updateAssetListEntryAssetEntryRel(
			assetListEntryAssetEntryRelId, assetListEntryId, assetEntryId,
			audienceEntryERC, segmentsEntryId, position);
	}

	public static AssetListEntryAssetEntryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetListEntryAssetEntryRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			AssetListEntryAssetEntryRelLocalServiceUtil.class,
			AssetListEntryAssetEntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-863187978