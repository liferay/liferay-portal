/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetDisplayPageEntry. This utility wraps
 * <code>com.liferay.asset.display.page.service.impl.AssetDisplayPageEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetDisplayPageEntryLocalService
 * @generated
 */
public class AssetDisplayPageEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.display.page.service.impl.AssetDisplayPageEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset display page entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetDisplayPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetDisplayPageEntry the asset display page entry
	 * @return the asset display page entry that was added
	 */
	public static AssetDisplayPageEntry addAssetDisplayPageEntry(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		return getService().addAssetDisplayPageEntry(assetDisplayPageEntry);
	}

	public static AssetDisplayPageEntry addAssetDisplayPageEntry(
			long userId, long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetDisplayPageEntry(
			userId, groupId, classNameId, classPK, layoutPageTemplateEntryId,
			type, serviceContext);
	}

	public static AssetDisplayPageEntry addAssetDisplayPageEntry(
			long userId, long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAssetDisplayPageEntry(
			userId, groupId, classNameId, classPK, layoutPageTemplateEntryId,
			serviceContext);
	}

	/**
	 * Creates a new asset display page entry with the primary key. Does not add the asset display page entry to the database.
	 *
	 * @param assetDisplayPageEntryId the primary key for the new asset display page entry
	 * @return the new asset display page entry
	 */
	public static AssetDisplayPageEntry createAssetDisplayPageEntry(
		long assetDisplayPageEntryId) {

		return getService().createAssetDisplayPageEntry(
			assetDisplayPageEntryId);
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
	 * Deletes the asset display page entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetDisplayPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetDisplayPageEntry the asset display page entry
	 * @return the asset display page entry that was removed
	 */
	public static AssetDisplayPageEntry deleteAssetDisplayPageEntry(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		return getService().deleteAssetDisplayPageEntry(assetDisplayPageEntry);
	}

	/**
	 * Deletes the asset display page entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetDisplayPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetDisplayPageEntryId the primary key of the asset display page entry
	 * @return the asset display page entry that was removed
	 * @throws PortalException if a asset display page entry with the primary key could not be found
	 */
	public static AssetDisplayPageEntry deleteAssetDisplayPageEntry(
			long assetDisplayPageEntryId)
		throws PortalException {

		return getService().deleteAssetDisplayPageEntry(
			assetDisplayPageEntryId);
	}

	public static void deleteAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK)
		throws PortalException {

		getService().deleteAssetDisplayPageEntry(groupId, classNameId, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
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

	public static AssetDisplayPageEntry fetchAssetDisplayPageEntry(
		long assetDisplayPageEntryId) {

		return getService().fetchAssetDisplayPageEntry(assetDisplayPageEntryId);
	}

	public static AssetDisplayPageEntry fetchAssetDisplayPageEntry(
		long groupId, long classNameId, long classPK) {

		return getService().fetchAssetDisplayPageEntry(
			groupId, classNameId, classPK);
	}

	/**
	 * Returns the asset display page entry matching the UUID and group.
	 *
	 * @param uuid the asset display page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry
		fetchAssetDisplayPageEntryByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchAssetDisplayPageEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the asset display page entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of asset display page entries
	 */
	public static List<AssetDisplayPageEntry> getAssetDisplayPageEntries(
		int start, int end) {

		return getService().getAssetDisplayPageEntries(start, end);
	}

	public static List<AssetDisplayPageEntry> getAssetDisplayPageEntries(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getService().getAssetDisplayPageEntries(
			classNameId, classTypeId, layoutPageTemplateEntryId,
			defaultTemplate, start, end, orderByComparator);
	}

	public static List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId) {

		return getService().
			getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
				layoutPageTemplateEntryId);
	}

	public static List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId, int start, int end,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getService().
			getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
				layoutPageTemplateEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset display page entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset display page entries
	 * @param companyId the primary key of the company
	 * @return the matching asset display page entries, or an empty list if no matches were found
	 */
	public static List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getAssetDisplayPageEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of asset display page entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset display page entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset display page entries, or an empty list if no matches were found
	 */
	public static List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getService().getAssetDisplayPageEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset display page entries.
	 *
	 * @return the number of asset display page entries
	 */
	public static int getAssetDisplayPageEntriesCount() {
		return getService().getAssetDisplayPageEntriesCount();
	}

	public static int getAssetDisplayPageEntriesCount(
		long groupId, long classNameId) {

		return getService().getAssetDisplayPageEntriesCount(
			groupId, classNameId);
	}

	public static int getAssetDisplayPageEntriesCount(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate) {

		return getService().getAssetDisplayPageEntriesCount(
			classNameId, classTypeId, layoutPageTemplateEntryId,
			defaultTemplate);
	}

	public static int
		getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId) {

		return getService().
			getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
				layoutPageTemplateEntryId);
	}

	/**
	 * Returns the asset display page entry with the primary key.
	 *
	 * @param assetDisplayPageEntryId the primary key of the asset display page entry
	 * @return the asset display page entry
	 * @throws PortalException if a asset display page entry with the primary key could not be found
	 */
	public static AssetDisplayPageEntry getAssetDisplayPageEntry(
			long assetDisplayPageEntryId)
		throws PortalException {

		return getService().getAssetDisplayPageEntry(assetDisplayPageEntryId);
	}

	/**
	 * Returns the asset display page entry matching the UUID and group.
	 *
	 * @param uuid the asset display page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset display page entry
	 * @throws PortalException if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry
			getAssetDisplayPageEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return getService().getAssetDisplayPageEntryByUuidAndGroupId(
			uuid, groupId);
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
	 * Updates the asset display page entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetDisplayPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetDisplayPageEntry the asset display page entry
	 * @return the asset display page entry that was updated
	 */
	public static AssetDisplayPageEntry updateAssetDisplayPageEntry(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		return getService().updateAssetDisplayPageEntry(assetDisplayPageEntry);
	}

	public static AssetDisplayPageEntry updateAssetDisplayPageEntry(
			long assetDisplayPageEntryId, long layoutPageTemplateEntryId,
			int type)
		throws PortalException {

		return getService().updateAssetDisplayPageEntry(
			assetDisplayPageEntryId, layoutPageTemplateEntryId, type);
	}

	public static AssetDisplayPageEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetDisplayPageEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			AssetDisplayPageEntryLocalServiceUtil.class,
			AssetDisplayPageEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-712392838