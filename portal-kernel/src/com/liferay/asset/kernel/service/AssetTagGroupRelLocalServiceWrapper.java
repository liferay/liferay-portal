/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetTagGroupRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetTagGroupRelLocalService
 * @generated
 */
public class AssetTagGroupRelLocalServiceWrapper
	implements AssetTagGroupRelLocalService,
			   ServiceWrapper<AssetTagGroupRelLocalService> {

	public AssetTagGroupRelLocalServiceWrapper() {
		this(null);
	}

	public AssetTagGroupRelLocalServiceWrapper(
		AssetTagGroupRelLocalService assetTagGroupRelLocalService) {

		_assetTagGroupRelLocalService = assetTagGroupRelLocalService;
	}

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
	@Override
	public AssetTagGroupRel addAssetTagGroupRel(
		AssetTagGroupRel assetTagGroupRel) {

		return _assetTagGroupRelLocalService.addAssetTagGroupRel(
			assetTagGroupRel);
	}

	@Override
	public AssetTagGroupRel addAssetTagGroupRel(
			long groupId, long tagId, int depotEntryType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetTagGroupRelLocalService.addAssetTagGroupRel(
			groupId, tagId, depotEntryType);
	}

	/**
	 * Creates a new asset tag group rel with the primary key. Does not add the asset tag group rel to the database.
	 *
	 * @param assetTagGroupRelId the primary key for the new asset tag group rel
	 * @return the new asset tag group rel
	 */
	@Override
	public AssetTagGroupRel createAssetTagGroupRel(long assetTagGroupRelId) {
		return _assetTagGroupRelLocalService.createAssetTagGroupRel(
			assetTagGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetTagGroupRelLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public AssetTagGroupRel deleteAssetTagGroupRel(
		AssetTagGroupRel assetTagGroupRel) {

		return _assetTagGroupRelLocalService.deleteAssetTagGroupRel(
			assetTagGroupRel);
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
	@Override
	public AssetTagGroupRel deleteAssetTagGroupRel(long assetTagGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetTagGroupRelLocalService.deleteAssetTagGroupRel(
			assetTagGroupRelId);
	}

	@Override
	public void deleteAssetTagGroupRelsByGroupId(long groupId) {
		_assetTagGroupRelLocalService.deleteAssetTagGroupRelsByGroupId(groupId);
	}

	@Override
	public void deleteAssetTagGroupRelsByTagId(long tagId) {
		_assetTagGroupRelLocalService.deleteAssetTagGroupRelsByTagId(tagId);
	}

	@Override
	public void deleteAssetTagGroupRelsByTagIdAndDepotEntryType(
			long tagId, int depotEntryType)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetTagGroupRelLocalService.
			deleteAssetTagGroupRelsByTagIdAndDepotEntryType(
				tagId, depotEntryType);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetTagGroupRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetTagGroupRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetTagGroupRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetTagGroupRelLocalService.dynamicQuery();
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

		return _assetTagGroupRelLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _assetTagGroupRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _assetTagGroupRelLocalService.dynamicQuery(
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

		return _assetTagGroupRelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _assetTagGroupRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetTagGroupRel fetchAssetTagGroupRel(long assetTagGroupRelId) {
		return _assetTagGroupRelLocalService.fetchAssetTagGroupRel(
			assetTagGroupRelId);
	}

	/**
	 * Returns the asset tag group rel matching the UUID and group.
	 *
	 * @param uuid the asset tag group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset tag group rel, or <code>null</code> if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel fetchAssetTagGroupRelByUuidAndGroupId(
		String uuid, long groupId) {

		return _assetTagGroupRelLocalService.
			fetchAssetTagGroupRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetTagGroupRelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the asset tag group rel with the primary key.
	 *
	 * @param assetTagGroupRelId the primary key of the asset tag group rel
	 * @return the asset tag group rel
	 * @throws PortalException if a asset tag group rel with the primary key could not be found
	 */
	@Override
	public AssetTagGroupRel getAssetTagGroupRel(long assetTagGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetTagGroupRelLocalService.getAssetTagGroupRel(
			assetTagGroupRelId);
	}

	/**
	 * Returns the asset tag group rel matching the UUID and group.
	 *
	 * @param uuid the asset tag group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset tag group rel
	 * @throws PortalException if a matching asset tag group rel could not be found
	 */
	@Override
	public AssetTagGroupRel getAssetTagGroupRelByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetTagGroupRelLocalService.
			getAssetTagGroupRelByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<AssetTagGroupRel> getAssetTagGroupRels(
		int start, int end) {

		return _assetTagGroupRelLocalService.getAssetTagGroupRels(start, end);
	}

	@Override
	public java.util.List<AssetTagGroupRel> getAssetTagGroupRelsByGroupId(
		long groupId) {

		return _assetTagGroupRelLocalService.getAssetTagGroupRelsByGroupId(
			groupId);
	}

	@Override
	public java.util.List<AssetTagGroupRel>
		getAssetTagGroupRelsByGroupIdAndDepotEntryType(
			long groupId, int depotEntryType) {

		return _assetTagGroupRelLocalService.
			getAssetTagGroupRelsByGroupIdAndDepotEntryType(
				groupId, depotEntryType);
	}

	@Override
	public java.util.List<AssetTagGroupRel> getAssetTagGroupRelsByTagId(
		long tagId) {

		return _assetTagGroupRelLocalService.getAssetTagGroupRelsByTagId(tagId);
	}

	@Override
	public java.util.List<AssetTagGroupRel>
		getAssetTagGroupRelsByTagIdAndDepotEntryType(
			long tagId, int depotEntryType) {

		return _assetTagGroupRelLocalService.
			getAssetTagGroupRelsByTagIdAndDepotEntryType(tagId, depotEntryType);
	}

	/**
	 * Returns all the asset tag group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset tag group rels
	 * @param companyId the primary key of the company
	 * @return the matching asset tag group rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetTagGroupRel>
		getAssetTagGroupRelsByUuidAndCompanyId(String uuid, long companyId) {

		return _assetTagGroupRelLocalService.
			getAssetTagGroupRelsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<AssetTagGroupRel>
		getAssetTagGroupRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<AssetTagGroupRel>
				orderByComparator) {

		return _assetTagGroupRelLocalService.
			getAssetTagGroupRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset tag group rels.
	 *
	 * @return the number of asset tag group rels
	 */
	@Override
	public int getAssetTagGroupRelsCount() {
		return _assetTagGroupRelLocalService.getAssetTagGroupRelsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetTagGroupRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetTagGroupRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetTagGroupRelLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void setAssetTagGroupRels(
			long tagId, long[] groupIds, int depotEntryType)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetTagGroupRelLocalService.setAssetTagGroupRels(
			tagId, groupIds, depotEntryType);
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
	@Override
	public AssetTagGroupRel updateAssetTagGroupRel(
		AssetTagGroupRel assetTagGroupRel) {

		return _assetTagGroupRelLocalService.updateAssetTagGroupRel(
			assetTagGroupRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetTagGroupRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetTagGroupRel> getCTPersistence() {
		return _assetTagGroupRelLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetTagGroupRel> getModelClass() {
		return _assetTagGroupRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetTagGroupRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetTagGroupRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetTagGroupRelLocalService getWrappedService() {
		return _assetTagGroupRelLocalService;
	}

	@Override
	public void setWrappedService(
		AssetTagGroupRelLocalService assetTagGroupRelLocalService) {

		_assetTagGroupRelLocalService = assetTagGroupRelLocalService;
	}

	private AssetTagGroupRelLocalService _assetTagGroupRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1188328177