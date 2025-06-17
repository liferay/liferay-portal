/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetListEntrySegmentsEntryRel. This utility wraps
 * <code>com.liferay.asset.list.service.impl.AssetListEntrySegmentsEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntrySegmentsEntryRelLocalService
 * @generated
 */
public class AssetListEntrySegmentsEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.list.service.impl.AssetListEntrySegmentsEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset list entry segments entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntrySegmentsEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntrySegmentsEntryRel the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel that was added
	 */
	public static AssetListEntrySegmentsEntryRel
		addAssetListEntrySegmentsEntryRel(
			AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		return getService().addAssetListEntrySegmentsEntryRel(
			assetListEntrySegmentsEntryRel);
	}

	public static AssetListEntrySegmentsEntryRel
			addAssetListEntrySegmentsEntryRel(
				long userId, long groupId, long assetListEntryId, int priority,
				long segmentsEntryId, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntrySegmentsEntryRel(
			userId, groupId, assetListEntryId, priority, segmentsEntryId,
			typeSettings, serviceContext);
	}

	public static AssetListEntrySegmentsEntryRel
			addAssetListEntrySegmentsEntryRel(
				long userId, long groupId, long assetListEntryId,
				long segmentsEntryId, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntrySegmentsEntryRel(
			userId, groupId, assetListEntryId, segmentsEntryId, typeSettings,
			serviceContext);
	}

	/**
	 * Creates a new asset list entry segments entry rel with the primary key. Does not add the asset list entry segments entry rel to the database.
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key for the new asset list entry segments entry rel
	 * @return the new asset list entry segments entry rel
	 */
	public static AssetListEntrySegmentsEntryRel
		createAssetListEntrySegmentsEntryRel(
			long assetListEntrySegmentsEntryRelId) {

		return getService().createAssetListEntrySegmentsEntryRel(
			assetListEntrySegmentsEntryRelId);
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
	 * Deletes the asset list entry segments entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntrySegmentsEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntrySegmentsEntryRel the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel that was removed
	 */
	public static AssetListEntrySegmentsEntryRel
		deleteAssetListEntrySegmentsEntryRel(
			AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		return getService().deleteAssetListEntrySegmentsEntryRel(
			assetListEntrySegmentsEntryRel);
	}

	/**
	 * Deletes the asset list entry segments entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntrySegmentsEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key of the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel that was removed
	 * @throws PortalException if a asset list entry segments entry rel with the primary key could not be found
	 */
	public static AssetListEntrySegmentsEntryRel
			deleteAssetListEntrySegmentsEntryRel(
				long assetListEntrySegmentsEntryRelId)
		throws PortalException {

		return getService().deleteAssetListEntrySegmentsEntryRel(
			assetListEntrySegmentsEntryRelId);
	}

	public static void deleteAssetListEntrySegmentsEntryRel(
			long assetListEntryId, long segmentsEntryId)
		throws PortalException {

		getService().deleteAssetListEntrySegmentsEntryRel(
			assetListEntryId, segmentsEntryId);
	}

	public static void deleteAssetListEntrySegmentsEntryRelByAssetListEntryId(
		long assetListEntryId) {

		getService().deleteAssetListEntrySegmentsEntryRelByAssetListEntryId(
			assetListEntryId);
	}

	public static void deleteAssetListEntrySegmentsEntryRelBySegmentsEntryId(
		long segmentsEntryId) {

		getService().deleteAssetListEntrySegmentsEntryRelBySegmentsEntryId(
			segmentsEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntrySegmentsEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntrySegmentsEntryRelModelImpl</code>.
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

	public static AssetListEntrySegmentsEntryRel
		fetchAssetListEntrySegmentsEntryRel(
			long assetListEntrySegmentsEntryRelId) {

		return getService().fetchAssetListEntrySegmentsEntryRel(
			assetListEntrySegmentsEntryRelId);
	}

	public static AssetListEntrySegmentsEntryRel
		fetchAssetListEntrySegmentsEntryRel(
			long assetListEntryId, long segmentsEntryId) {

		return getService().fetchAssetListEntrySegmentsEntryRel(
			assetListEntryId, segmentsEntryId);
	}

	/**
	 * Returns the asset list entry segments entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry segments entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	public static AssetListEntrySegmentsEntryRel
		fetchAssetListEntrySegmentsEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchAssetListEntrySegmentsEntryRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static List<AssetListEntrySegmentsEntryRel>
		fetchAssetListEntrySegmentsEntryRels(
			long assetListEntryId, long[] segmentsEntryIds) {

		return getService().fetchAssetListEntrySegmentsEntryRels(
			assetListEntryId, segmentsEntryIds);
	}

	public static List<AssetListEntrySegmentsEntryRel>
		fetchDynamicAssetListEntrySegmentsEntryRels(long companyId) {

		return getService().fetchDynamicAssetListEntrySegmentsEntryRels(
			companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the asset list entry segments entry rel with the primary key.
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key of the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel
	 * @throws PortalException if a asset list entry segments entry rel with the primary key could not be found
	 */
	public static AssetListEntrySegmentsEntryRel
			getAssetListEntrySegmentsEntryRel(
				long assetListEntrySegmentsEntryRelId)
		throws PortalException {

		return getService().getAssetListEntrySegmentsEntryRel(
			assetListEntrySegmentsEntryRelId);
	}

	public static AssetListEntrySegmentsEntryRel
			getAssetListEntrySegmentsEntryRel(
				long assetListEntryId, long segmentsEntryId)
		throws PortalException {

		return getService().getAssetListEntrySegmentsEntryRel(
			assetListEntryId, segmentsEntryId);
	}

	/**
	 * Returns the asset list entry segments entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry segments entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry segments entry rel
	 * @throws PortalException if a matching asset list entry segments entry rel could not be found
	 */
	public static AssetListEntrySegmentsEntryRel
			getAssetListEntrySegmentsEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getAssetListEntrySegmentsEntryRelByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the asset list entry segments entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntrySegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset list entry segments entry rels
	 * @param end the upper bound of the range of asset list entry segments entry rels (not inclusive)
	 * @return the range of asset list entry segments entry rels
	 */
	public static List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels(int start, int end) {

		return getService().getAssetListEntrySegmentsEntryRels(start, end);
	}

	public static List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels(
			long assetListEntryId, int start, int end) {

		return getService().getAssetListEntrySegmentsEntryRels(
			assetListEntryId, start, end);
	}

	public static List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels(
			long assetListEntryId, long[] segmentsEntryIds, int start, int end,
			OrderByComparator<AssetListEntrySegmentsEntryRel>
				orderByComparator) {

		return getService().getAssetListEntrySegmentsEntryRels(
			assetListEntryId, segmentsEntryIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entry segments entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry segments entry rels
	 * @param companyId the primary key of the company
	 * @return the matching asset list entry segments entry rels, or an empty list if no matches were found
	 */
	public static List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			getAssetListEntrySegmentsEntryRelsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of asset list entry segments entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry segments entry rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset list entry segments entry rels
	 * @param end the upper bound of the range of asset list entry segments entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset list entry segments entry rels, or an empty list if no matches were found
	 */
	public static List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<AssetListEntrySegmentsEntryRel>
				orderByComparator) {

		return getService().
			getAssetListEntrySegmentsEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset list entry segments entry rels.
	 *
	 * @return the number of asset list entry segments entry rels
	 */
	public static int getAssetListEntrySegmentsEntryRelsCount() {
		return getService().getAssetListEntrySegmentsEntryRelsCount();
	}

	public static int getAssetListEntrySegmentsEntryRelsCount(
		long assetListEntryId) {

		return getService().getAssetListEntrySegmentsEntryRelsCount(
			assetListEntryId);
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

	/**
	 * Updates the asset list entry segments entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntrySegmentsEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntrySegmentsEntryRel the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel that was updated
	 */
	public static AssetListEntrySegmentsEntryRel
		updateAssetListEntrySegmentsEntryRel(
			AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		return getService().updateAssetListEntrySegmentsEntryRel(
			assetListEntrySegmentsEntryRel);
	}

	public static AssetListEntrySegmentsEntryRel
		updateAssetListEntrySegmentsEntryRelTypeSettings(
			long assetListEntryId, long segmentsEntryId, String typeSettings) {

		return getService().updateAssetListEntrySegmentsEntryRelTypeSettings(
			assetListEntryId, segmentsEntryId, typeSettings);
	}

	public static void updateVariationsPriority(long[] variationsPriority) {
		getService().updateVariationsPriority(variationsPriority);
	}

	public static AssetListEntrySegmentsEntryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetListEntrySegmentsEntryRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			AssetListEntrySegmentsEntryRelLocalServiceUtil.class,
			AssetListEntrySegmentsEntryRelLocalService.class);

}