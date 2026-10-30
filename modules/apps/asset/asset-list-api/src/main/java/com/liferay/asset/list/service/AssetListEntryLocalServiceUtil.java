/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetListEntry. This utility wraps
 * <code>com.liferay.asset.list.service.impl.AssetListEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryLocalService
 * @generated
 */
public class AssetListEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.list.service.impl.AssetListEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addAssetEntrySelection(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addAssetEntrySelection(
			assetListEntryId, assetEntryId, segmentsEntryId, serviceContext);
	}

	public static void addAssetEntrySelections(
			long assetListEntryId, long[] assetEntryIds, long segmentsEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addAssetEntrySelections(
			assetListEntryId, assetEntryIds, segmentsEntryId, serviceContext);
	}

	public static void addAssetEntrySelections(
			long assetListEntryId, long[] assetEntryIds,
			String audienceEntryERC,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addAssetEntrySelections(
			assetListEntryId, assetEntryIds, audienceEntryERC, serviceContext);
	}

	/**
	 * Adds the asset list entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntry the asset list entry
	 * @return the asset list entry that was added
	 */
	public static AssetListEntry addAssetListEntry(
		AssetListEntry assetListEntry) {

		return getService().addAssetListEntry(assetListEntry);
	}

	public static AssetListEntry addAssetListEntry(
			String externalReferenceCode, long userId, long groupId,
			String title, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntry(
			externalReferenceCode, userId, groupId, title, type,
			serviceContext);
	}

	public static AssetListEntry addAssetListEntry(
			String externalReferenceCode, long userId, long groupId,
			String title, int type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetListEntry(
			externalReferenceCode, userId, groupId, title, type, typeSettings,
			serviceContext);
	}

	public static AssetListEntry addDynamicAssetListEntry(
			String externalReferenceCode, long userId, long groupId,
			String title, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDynamicAssetListEntry(
			externalReferenceCode, userId, groupId, title, typeSettings,
			serviceContext);
	}

	public static AssetListEntry addManualAssetListEntry(
			String externalReferenceCode, long userId, long groupId,
			String title, long[] assetEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addManualAssetListEntry(
			externalReferenceCode, userId, groupId, title, assetEntryIds,
			serviceContext);
	}

	/**
	 * Creates a new asset list entry with the primary key. Does not add the asset list entry to the database.
	 *
	 * @param assetListEntryId the primary key for the new asset list entry
	 * @return the new asset list entry
	 */
	public static AssetListEntry createAssetListEntry(long assetListEntryId) {
		return getService().createAssetListEntry(assetListEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position)
		throws PortalException {

		getService().deleteAssetEntrySelection(
			assetListEntryId, segmentsEntryId, position);
	}

	public static void deleteAssetEntrySelection(
			long assetListEntryId, String audienceEntryERC, int position)
		throws PortalException {

		getService().deleteAssetEntrySelection(
			assetListEntryId, audienceEntryERC, position);
	}

	/**
	 * Deletes the asset list entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntry the asset list entry
	 * @return the asset list entry that was removed
	 * @throws PortalException
	 */
	public static AssetListEntry deleteAssetListEntry(
			AssetListEntry assetListEntry)
		throws PortalException {

		return getService().deleteAssetListEntry(assetListEntry);
	}

	/**
	 * Deletes the asset list entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry that was removed
	 * @throws PortalException if a asset list entry with the primary key could not be found
	 */
	public static AssetListEntry deleteAssetListEntry(long assetListEntryId)
		throws PortalException {

		return getService().deleteAssetListEntry(assetListEntryId);
	}

	public static AssetListEntry deleteAssetListEntry(
			long assetListEntryId, long segmentsEntryId)
		throws PortalException {

		return getService().deleteAssetListEntry(
			assetListEntryId, segmentsEntryId);
	}

	public static AssetListEntry deleteAssetListEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteAssetListEntry(
			externalReferenceCode, groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
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

	public static AssetListEntry fetchAssetListEntry(long assetListEntryId) {
		return getService().fetchAssetListEntry(assetListEntryId);
	}

	public static AssetListEntry fetchAssetListEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchAssetListEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset list entry matching the UUID and group.
	 *
	 * @param uuid the asset list entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchAssetListEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchAssetListEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the asset list entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of asset list entries
	 */
	public static List<AssetListEntry> getAssetListEntries(int start, int end) {
		return getService().getAssetListEntries(start, end);
	}

	public static List<AssetListEntry> getAssetListEntries(long groupId) {
		return getService().getAssetListEntries(groupId);
	}

	/**
	 * Returns all the asset list entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entries
	 * @param companyId the primary key of the company
	 * @return the matching asset list entries, or an empty list if no matches were found
	 */
	public static List<AssetListEntry> getAssetListEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getAssetListEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of asset list entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset list entries, or an empty list if no matches were found
	 */
	public static List<AssetListEntry> getAssetListEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getService().getAssetListEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset list entries.
	 *
	 * @return the number of asset list entries
	 */
	public static int getAssetListEntriesCount() {
		return getService().getAssetListEntriesCount();
	}

	/**
	 * Returns the asset list entry with the primary key.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry
	 * @throws PortalException if a asset list entry with the primary key could not be found
	 */
	public static AssetListEntry getAssetListEntry(long assetListEntryId)
		throws PortalException {

		return getService().getAssetListEntry(assetListEntryId);
	}

	public static AssetListEntry getAssetListEntry(
			long groupId, String assetListEntryKey)
		throws PortalException {

		return getService().getAssetListEntry(groupId, assetListEntryKey);
	}

	public static AssetListEntry getAssetListEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getAssetListEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset list entry matching the UUID and group.
	 *
	 * @param uuid the asset list entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry
	 * @throws PortalException if a matching asset list entry could not be found
	 */
	public static AssetListEntry getAssetListEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getAssetListEntryByUuidAndGroupId(uuid, groupId);
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

	public static void moveAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position,
			int newPosition)
		throws PortalException {

		getService().moveAssetEntrySelection(
			assetListEntryId, segmentsEntryId, position, newPosition);
	}

	public static void moveAssetEntrySelection(
			long assetListEntryId, String audienceEntryERC, int position,
			int newPosition)
		throws PortalException {

		getService().moveAssetEntrySelection(
			assetListEntryId, audienceEntryERC, position, newPosition);
	}

	/**
	 * Updates the asset list entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntry the asset list entry
	 * @return the asset list entry that was updated
	 */
	public static AssetListEntry updateAssetListEntry(
		AssetListEntry assetListEntry) {

		return getService().updateAssetListEntry(assetListEntry);
	}

	public static AssetListEntry updateAssetListEntry(
			long assetListEntryId, long segmentsEntryId, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAssetListEntry(
			assetListEntryId, segmentsEntryId, typeSettings, serviceContext);
	}

	public static AssetListEntry updateAssetListEntry(
			long assetListEntryId, String title)
		throws PortalException {

		return getService().updateAssetListEntry(assetListEntryId, title);
	}

	public static AssetListEntry updateAssetListEntry(
			long assetListEntryId, String title,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAssetListEntry(
			assetListEntryId, title, serviceContext);
	}

	public static void updateAssetListEntryTypeSettings(
			long companyId, long classNameId)
		throws PortalException {

		getService().updateAssetListEntryTypeSettings(companyId, classNameId);
	}

	public static void updateAssetListEntryTypeSettings(
			long assetListEntryId, long segmentsEntryId, String typeSettings)
		throws PortalException {

		getService().updateAssetListEntryTypeSettings(
			assetListEntryId, segmentsEntryId, typeSettings);
	}

	public static AssetListEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetListEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			AssetListEntryLocalServiceUtil.class,
			AssetListEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1313650127