/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetListEntryAudienceEntryRel. This utility wraps
 * <code>com.liferay.asset.list.service.impl.AssetListEntryAudienceEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAudienceEntryRelLocalService
 * @generated
 */
public class AssetListEntryAudienceEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.list.service.impl.AssetListEntryAudienceEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset list entry audience entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryAudienceEntryRel the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel that was added
	 */
	public static AssetListEntryAudienceEntryRel
		addAssetListEntryAudienceEntryRel(
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		return getService().addAssetListEntryAudienceEntryRel(
			assetListEntryAudienceEntryRel);
	}

	public static AssetListEntryAudienceEntryRel
			addAssetListEntryAudienceEntryRel(
				long userId, long groupId, long assetListEntryId,
				String audienceEntryERC, int priority, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntryAudienceEntryRel(
			userId, groupId, assetListEntryId, audienceEntryERC, priority,
			typeSettings, serviceContext);
	}

	public static AssetListEntryAudienceEntryRel
			addAssetListEntryAudienceEntryRel(
				long userId, long groupId, long assetListEntryId,
				String audienceEntryERC, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntryAudienceEntryRel(
			userId, groupId, assetListEntryId, audienceEntryERC, typeSettings,
			serviceContext);
	}

	/**
	 * Creates a new asset list entry audience entry rel with the primary key. Does not add the asset list entry audience entry rel to the database.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key for the new asset list entry audience entry rel
	 * @return the new asset list entry audience entry rel
	 */
	public static AssetListEntryAudienceEntryRel
		createAssetListEntryAudienceEntryRel(
			long assetListEntryAudienceEntryRelId) {

		return getService().createAssetListEntryAudienceEntryRel(
			assetListEntryAudienceEntryRelId);
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
	 * Deletes the asset list entry audience entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryAudienceEntryRel the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel that was removed
	 */
	public static AssetListEntryAudienceEntryRel
		deleteAssetListEntryAudienceEntryRel(
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		return getService().deleteAssetListEntryAudienceEntryRel(
			assetListEntryAudienceEntryRel);
	}

	/**
	 * Deletes the asset list entry audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel that was removed
	 * @throws PortalException if a asset list entry audience entry rel with the primary key could not be found
	 */
	public static AssetListEntryAudienceEntryRel
			deleteAssetListEntryAudienceEntryRel(
				long assetListEntryAudienceEntryRelId)
		throws PortalException {

		return getService().deleteAssetListEntryAudienceEntryRel(
			assetListEntryAudienceEntryRelId);
	}

	public static void deleteAssetListEntryAudienceEntryRel(
			long assetListEntryId, String audienceEntryERC)
		throws PortalException {

		getService().deleteAssetListEntryAudienceEntryRel(
			assetListEntryId, audienceEntryERC);
	}

	public static void deleteAssetListEntryAudienceEntryRelByAssetListEntryId(
		long assetListEntryId) {

		getService().deleteAssetListEntryAudienceEntryRelByAssetListEntryId(
			assetListEntryId);
	}

	public static void deleteAssetListEntryAudienceEntryRelByAudienceEntryERC(
		long companyId, String audienceEntryERC) {

		getService().deleteAssetListEntryAudienceEntryRelByAudienceEntryERC(
			companyId, audienceEntryERC);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
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

	public static AssetListEntryAudienceEntryRel
		fetchAssetListEntryAudienceEntryRel(
			long assetListEntryAudienceEntryRelId) {

		return getService().fetchAssetListEntryAudienceEntryRel(
			assetListEntryAudienceEntryRelId);
	}

	public static AssetListEntryAudienceEntryRel
		fetchAssetListEntryAudienceEntryRel(
			long assetListEntryId, String audienceEntryERC) {

		return getService().fetchAssetListEntryAudienceEntryRel(
			assetListEntryId, audienceEntryERC);
	}

	public static AssetListEntryAudienceEntryRel
		fetchAssetListEntryAudienceEntryRel(
			long assetListEntryId, String[] audienceEntryERCs) {

		return getService().fetchAssetListEntryAudienceEntryRel(
			assetListEntryId, audienceEntryERCs);
	}

	/**
	 * Returns the asset list entry audience entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel
		fetchAssetListEntryAudienceEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchAssetListEntryAudienceEntryRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the asset list entry audience entry rel with the primary key.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel
	 * @throws PortalException if a asset list entry audience entry rel with the primary key could not be found
	 */
	public static AssetListEntryAudienceEntryRel
			getAssetListEntryAudienceEntryRel(
				long assetListEntryAudienceEntryRelId)
		throws PortalException {

		return getService().getAssetListEntryAudienceEntryRel(
			assetListEntryAudienceEntryRelId);
	}

	/**
	 * Returns the asset list entry audience entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry audience entry rel
	 * @throws PortalException if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel
			getAssetListEntryAudienceEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getAssetListEntryAudienceEntryRelByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRels(int start, int end) {

		return getService().getAssetListEntryAudienceEntryRels(start, end);
	}

	public static List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRels(
			long assetListEntryId, int start, int end) {

		return getService().getAssetListEntryAudienceEntryRels(
			assetListEntryId, start, end);
	}

	/**
	 * Returns all the asset list entry audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry audience entry rels
	 * @param companyId the primary key of the company
	 * @return the matching asset list entry audience entry rels, or an empty list if no matches were found
	 */
	public static List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			getAssetListEntryAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of asset list entry audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry audience entry rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset list entry audience entry rels, or an empty list if no matches were found
	 */
	public static List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<AssetListEntryAudienceEntryRel>
				orderByComparator) {

		return getService().
			getAssetListEntryAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset list entry audience entry rels.
	 *
	 * @return the number of asset list entry audience entry rels
	 */
	public static int getAssetListEntryAudienceEntryRelsCount() {
		return getService().getAssetListEntryAudienceEntryRelsCount();
	}

	public static int getAssetListEntryAudienceEntryRelsCount(
		long assetListEntryId) {

		return getService().getAssetListEntryAudienceEntryRelsCount(
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
	 * Updates the asset list entry audience entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryAudienceEntryRel the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel that was updated
	 */
	public static AssetListEntryAudienceEntryRel
		updateAssetListEntryAudienceEntryRel(
			AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		return getService().updateAssetListEntryAudienceEntryRel(
			assetListEntryAudienceEntryRel);
	}

	public static AssetListEntryAudienceEntryRel
			updateAssetListEntryAudienceEntryRelTypeSettings(
				long assetListEntryId, String audienceEntryERC,
				String typeSettings)
		throws PortalException {

		return getService().updateAssetListEntryAudienceEntryRelTypeSettings(
			assetListEntryId, audienceEntryERC, typeSettings);
	}

	public static void updatePriorities(
			long[] assetListEntryAudienceEntryRelIds)
		throws PortalException {

		getService().updatePriorities(assetListEntryAudienceEntryRelIds);
	}

	public static AssetListEntryAudienceEntryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetListEntryAudienceEntryRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			AssetListEntryAudienceEntryRelLocalServiceUtil.class,
			AssetListEntryAudienceEntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1962540447