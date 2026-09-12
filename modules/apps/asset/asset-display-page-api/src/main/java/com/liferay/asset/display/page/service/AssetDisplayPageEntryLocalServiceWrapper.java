/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetDisplayPageEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetDisplayPageEntryLocalService
 * @generated
 */
public class AssetDisplayPageEntryLocalServiceWrapper
	implements AssetDisplayPageEntryLocalService,
			   ServiceWrapper<AssetDisplayPageEntryLocalService> {

	public AssetDisplayPageEntryLocalServiceWrapper() {
		this(null);
	}

	public AssetDisplayPageEntryLocalServiceWrapper(
		AssetDisplayPageEntryLocalService assetDisplayPageEntryLocalService) {

		_assetDisplayPageEntryLocalService = assetDisplayPageEntryLocalService;
	}

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
	@Override
	public AssetDisplayPageEntry addAssetDisplayPageEntry(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		return _assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			assetDisplayPageEntry);
	}

	@Override
	public AssetDisplayPageEntry addAssetDisplayPageEntry(
			long userId, long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			userId, groupId, classNameId, classPK, layoutPageTemplateEntryId,
			type, serviceContext);
	}

	@Override
	public AssetDisplayPageEntry addAssetDisplayPageEntry(
			long userId, long groupId, long classNameId, long classPK,
			long layoutPageTemplateEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			userId, groupId, classNameId, classPK, layoutPageTemplateEntryId,
			serviceContext);
	}

	/**
	 * Creates a new asset display page entry with the primary key. Does not add the asset display page entry to the database.
	 *
	 * @param assetDisplayPageEntryId the primary key for the new asset display page entry
	 * @return the new asset display page entry
	 */
	@Override
	public AssetDisplayPageEntry createAssetDisplayPageEntry(
		long assetDisplayPageEntryId) {

		return _assetDisplayPageEntryLocalService.createAssetDisplayPageEntry(
			assetDisplayPageEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public AssetDisplayPageEntry deleteAssetDisplayPageEntry(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		return _assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
			assetDisplayPageEntry);
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
	@Override
	public AssetDisplayPageEntry deleteAssetDisplayPageEntry(
			long assetDisplayPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
			assetDisplayPageEntryId);
	}

	@Override
	public void deleteAssetDisplayPageEntry(
			long groupId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
			groupId, classNameId, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetDisplayPageEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetDisplayPageEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetDisplayPageEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _assetDisplayPageEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _assetDisplayPageEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _assetDisplayPageEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _assetDisplayPageEntryLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _assetDisplayPageEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetDisplayPageEntry fetchAssetDisplayPageEntry(
		long assetDisplayPageEntryId) {

		return _assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
			assetDisplayPageEntryId);
	}

	@Override
	public AssetDisplayPageEntry fetchAssetDisplayPageEntry(
		long groupId, long classNameId, long classPK) {

		return _assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
			groupId, classNameId, classPK);
	}

	/**
	 * Returns the asset display page entry matching the UUID and group.
	 *
	 * @param uuid the asset display page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	@Override
	public AssetDisplayPageEntry fetchAssetDisplayPageEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _assetDisplayPageEntryLocalService.
			fetchAssetDisplayPageEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetDisplayPageEntryLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List<AssetDisplayPageEntry> getAssetDisplayPageEntries(
		int start, int end) {

		return _assetDisplayPageEntryLocalService.getAssetDisplayPageEntries(
			start, end);
	}

	@Override
	public java.util.List<AssetDisplayPageEntry> getAssetDisplayPageEntries(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator) {

		return _assetDisplayPageEntryLocalService.getAssetDisplayPageEntries(
			classNameId, classTypeId, layoutPageTemplateEntryId,
			defaultTemplate, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId) {

		return _assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
				layoutPageTemplateEntryId);
	}

	@Override
	public java.util.List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetDisplayPageEntry> orderByComparator) {

		return _assetDisplayPageEntryLocalService.
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
	@Override
	public java.util.List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntriesByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<AssetDisplayPageEntry>
		getAssetDisplayPageEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetDisplayPageEntry> orderByComparator) {

		return _assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset display page entries.
	 *
	 * @return the number of asset display page entries
	 */
	@Override
	public int getAssetDisplayPageEntriesCount() {
		return _assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntriesCount();
	}

	@Override
	public int getAssetDisplayPageEntriesCount(long groupId, long classNameId) {
		return _assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntriesCount(groupId, classNameId);
	}

	@Override
	public int getAssetDisplayPageEntriesCount(
		long classNameId, long classTypeId, long layoutPageTemplateEntryId,
		boolean defaultTemplate) {

		return _assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntriesCount(
				classNameId, classTypeId, layoutPageTemplateEntryId,
				defaultTemplate);
	}

	@Override
	public int getAssetDisplayPageEntriesCountByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId) {

		return _assetDisplayPageEntryLocalService.
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
	@Override
	public AssetDisplayPageEntry getAssetDisplayPageEntry(
			long assetDisplayPageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.getAssetDisplayPageEntry(
			assetDisplayPageEntryId);
	}

	/**
	 * Returns the asset display page entry matching the UUID and group.
	 *
	 * @param uuid the asset display page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset display page entry
	 * @throws PortalException if a matching asset display page entry could not be found
	 */
	@Override
	public AssetDisplayPageEntry getAssetDisplayPageEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.
			getAssetDisplayPageEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _assetDisplayPageEntryLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetDisplayPageEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetDisplayPageEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public AssetDisplayPageEntry updateAssetDisplayPageEntry(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		return _assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
			assetDisplayPageEntry);
	}

	@Override
	public AssetDisplayPageEntry updateAssetDisplayPageEntry(
			long assetDisplayPageEntryId, long layoutPageTemplateEntryId,
			int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
			assetDisplayPageEntryId, layoutPageTemplateEntryId, type);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetDisplayPageEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetDisplayPageEntry> getCTPersistence() {
		return _assetDisplayPageEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetDisplayPageEntry> getModelClass() {
		return _assetDisplayPageEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetDisplayPageEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetDisplayPageEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetDisplayPageEntryLocalService getWrappedService() {
		return _assetDisplayPageEntryLocalService;
	}

	@Override
	public void setWrappedService(
		AssetDisplayPageEntryLocalService assetDisplayPageEntryLocalService) {

		_assetDisplayPageEntryLocalService = assetDisplayPageEntryLocalService;
	}

	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-217983123