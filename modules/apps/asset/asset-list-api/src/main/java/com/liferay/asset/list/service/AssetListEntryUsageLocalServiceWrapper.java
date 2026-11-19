/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntryUsage;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetListEntryUsageLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryUsageLocalService
 * @generated
 */
public class AssetListEntryUsageLocalServiceWrapper
	implements AssetListEntryUsageLocalService,
			   ServiceWrapper<AssetListEntryUsageLocalService> {

	public AssetListEntryUsageLocalServiceWrapper() {
		this(null);
	}

	public AssetListEntryUsageLocalServiceWrapper(
		AssetListEntryUsageLocalService assetListEntryUsageLocalService) {

		_assetListEntryUsageLocalService = assetListEntryUsageLocalService;
	}

	/**
	 * Adds the asset list entry usage to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryUsage the asset list entry usage
	 * @return the asset list entry usage that was added
	 */
	@Override
	public AssetListEntryUsage addAssetListEntryUsage(
		AssetListEntryUsage assetListEntryUsage) {

		return _assetListEntryUsageLocalService.addAssetListEntryUsage(
			assetListEntryUsage);
	}

	@Override
	public AssetListEntryUsage addAssetListEntryUsage(
			long userId, long groupId, long classNameId, String containerKey,
			long containerType, String key, long plid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryUsageLocalService.addAssetListEntryUsage(
			userId, groupId, classNameId, containerKey, containerType, key,
			plid, serviceContext);
	}

	/**
	 * Creates a new asset list entry usage with the primary key. Does not add the asset list entry usage to the database.
	 *
	 * @param assetListEntryUsageId the primary key for the new asset list entry usage
	 * @return the new asset list entry usage
	 */
	@Override
	public AssetListEntryUsage createAssetListEntryUsage(
		long assetListEntryUsageId) {

		return _assetListEntryUsageLocalService.createAssetListEntryUsage(
			assetListEntryUsageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryUsageLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the asset list entry usage from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryUsage the asset list entry usage
	 * @return the asset list entry usage that was removed
	 */
	@Override
	public AssetListEntryUsage deleteAssetListEntryUsage(
		AssetListEntryUsage assetListEntryUsage) {

		return _assetListEntryUsageLocalService.deleteAssetListEntryUsage(
			assetListEntryUsage);
	}

	/**
	 * Deletes the asset list entry usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryUsageId the primary key of the asset list entry usage
	 * @return the asset list entry usage that was removed
	 * @throws PortalException if a asset list entry usage with the primary key could not be found
	 */
	@Override
	public AssetListEntryUsage deleteAssetListEntryUsage(
			long assetListEntryUsageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryUsageLocalService.deleteAssetListEntryUsage(
			assetListEntryUsageId);
	}

	@Override
	public void deleteAssetListEntryUsages(long containerType, long plid) {
		_assetListEntryUsageLocalService.deleteAssetListEntryUsages(
			containerType, plid);
	}

	@Override
	public void deleteAssetListEntryUsages(
		String containerKey, long containerType, long plid) {

		_assetListEntryUsageLocalService.deleteAssetListEntryUsages(
			containerKey, containerType, plid);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryUsageLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetListEntryUsageLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetListEntryUsageLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetListEntryUsageLocalService.dynamicQuery();
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

		return _assetListEntryUsageLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryUsageModelImpl</code>.
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

		return _assetListEntryUsageLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryUsageModelImpl</code>.
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

		return _assetListEntryUsageLocalService.dynamicQuery(
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

		return _assetListEntryUsageLocalService.dynamicQueryCount(dynamicQuery);
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

		return _assetListEntryUsageLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetListEntryUsage fetchAssetListEntryUsage(
		long assetListEntryUsageId) {

		return _assetListEntryUsageLocalService.fetchAssetListEntryUsage(
			assetListEntryUsageId);
	}

	/**
	 * Returns the asset list entry usage matching the UUID and group.
	 *
	 * @param uuid the asset list entry usage's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry usage, or <code>null</code> if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage fetchAssetListEntryUsageByUuidAndGroupId(
		String uuid, long groupId) {

		return _assetListEntryUsageLocalService.
			fetchAssetListEntryUsageByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetListEntryUsageLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<AssetListEntryUsage> getAssetEntryListUsagesByPlid(
		long plid) {

		return _assetListEntryUsageLocalService.getAssetEntryListUsagesByPlid(
			plid);
	}

	/**
	 * Returns the asset list entry usage with the primary key.
	 *
	 * @param assetListEntryUsageId the primary key of the asset list entry usage
	 * @return the asset list entry usage
	 * @throws PortalException if a asset list entry usage with the primary key could not be found
	 */
	@Override
	public AssetListEntryUsage getAssetListEntryUsage(
			long assetListEntryUsageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryUsageLocalService.getAssetListEntryUsage(
			assetListEntryUsageId);
	}

	/**
	 * Returns the asset list entry usage matching the UUID and group.
	 *
	 * @param uuid the asset list entry usage's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry usage
	 * @throws PortalException if a matching asset list entry usage could not be found
	 */
	@Override
	public AssetListEntryUsage getAssetListEntryUsageByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryUsageLocalService.
			getAssetListEntryUsageByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the asset list entry usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @return the range of asset list entry usages
	 */
	@Override
	public java.util.List<AssetListEntryUsage> getAssetListEntryUsages(
		int start, int end) {

		return _assetListEntryUsageLocalService.getAssetListEntryUsages(
			start, end);
	}

	@Override
	public java.util.List<AssetListEntryUsage> getAssetListEntryUsages(
		long groupId, long classNameId, String key) {

		return _assetListEntryUsageLocalService.getAssetListEntryUsages(
			groupId, classNameId, key);
	}

	@Override
	public java.util.List<AssetListEntryUsage> getAssetListEntryUsages(
		long groupId, long classNameId, String key, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntryUsage>
			orderByComparator) {

		return _assetListEntryUsageLocalService.getAssetListEntryUsages(
			groupId, classNameId, key, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntryUsage> getAssetListEntryUsages(
		long groupId, long classNameId, String key, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntryUsage>
			orderByComparator) {

		return _assetListEntryUsageLocalService.getAssetListEntryUsages(
			groupId, classNameId, key, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntryUsage> getAssetListEntryUsages(
		String containerKey, long containerType, long plid) {

		return _assetListEntryUsageLocalService.getAssetListEntryUsages(
			containerKey, containerType, plid);
	}

	/**
	 * Returns all the asset list entry usages matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry usages
	 * @param companyId the primary key of the company
	 * @return the matching asset list entry usages, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetListEntryUsage>
		getAssetListEntryUsagesByUuidAndCompanyId(String uuid, long companyId) {

		return _assetListEntryUsageLocalService.
			getAssetListEntryUsagesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of asset list entry usages matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry usages
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset list entry usages
	 * @param end the upper bound of the range of asset list entry usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset list entry usages, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetListEntryUsage>
		getAssetListEntryUsagesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryUsage> orderByComparator) {

		return _assetListEntryUsageLocalService.
			getAssetListEntryUsagesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset list entry usages.
	 *
	 * @return the number of asset list entry usages
	 */
	@Override
	public int getAssetListEntryUsagesCount() {
		return _assetListEntryUsageLocalService.getAssetListEntryUsagesCount();
	}

	@Override
	public int getAssetListEntryUsagesCount(
		long groupId, long classNameId, String key) {

		return _assetListEntryUsageLocalService.getAssetListEntryUsagesCount(
			groupId, classNameId, key);
	}

	@Override
	public int getAssetListEntryUsagesCount(
		long groupId, long classNameId, String key, int type) {

		return _assetListEntryUsageLocalService.getAssetListEntryUsagesCount(
			groupId, classNameId, key, type);
	}

	@Override
	public int getCompanyAssetListEntryUsagesCount(
		long companyId, long classNameId, String key) {

		return _assetListEntryUsageLocalService.
			getCompanyAssetListEntryUsagesCount(companyId, classNameId, key);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _assetListEntryUsageLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetListEntryUsageLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetListEntryUsageLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryUsageLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the asset list entry usage in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetListEntryUsageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetListEntryUsage the asset list entry usage
	 * @return the asset list entry usage that was updated
	 */
	@Override
	public AssetListEntryUsage updateAssetListEntryUsage(
		AssetListEntryUsage assetListEntryUsage) {

		return _assetListEntryUsageLocalService.updateAssetListEntryUsage(
			assetListEntryUsage);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetListEntryUsageLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetListEntryUsage> getCTPersistence() {
		return _assetListEntryUsageLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetListEntryUsage> getModelClass() {
		return _assetListEntryUsageLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetListEntryUsage>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetListEntryUsageLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetListEntryUsageLocalService getWrappedService() {
		return _assetListEntryUsageLocalService;
	}

	@Override
	public void setWrappedService(
		AssetListEntryUsageLocalService assetListEntryUsageLocalService) {

		_assetListEntryUsageLocalService = assetListEntryUsageLocalService;
	}

	private AssetListEntryUsageLocalService _assetListEntryUsageLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1349477410