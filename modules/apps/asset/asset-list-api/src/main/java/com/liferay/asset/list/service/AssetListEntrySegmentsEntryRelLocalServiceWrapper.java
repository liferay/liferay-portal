/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetListEntrySegmentsEntryRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntrySegmentsEntryRelLocalService
 * @generated
 */
public class AssetListEntrySegmentsEntryRelLocalServiceWrapper
	implements AssetListEntrySegmentsEntryRelLocalService,
			   ServiceWrapper<AssetListEntrySegmentsEntryRelLocalService> {

	public AssetListEntrySegmentsEntryRelLocalServiceWrapper() {
		this(null);
	}

	public AssetListEntrySegmentsEntryRelLocalServiceWrapper(
		AssetListEntrySegmentsEntryRelLocalService
			assetListEntrySegmentsEntryRelLocalService) {

		_assetListEntrySegmentsEntryRelLocalService =
			assetListEntrySegmentsEntryRelLocalService;
	}

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
	@Override
	public AssetListEntrySegmentsEntryRel addAssetListEntrySegmentsEntryRel(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		return _assetListEntrySegmentsEntryRelLocalService.
			addAssetListEntrySegmentsEntryRel(assetListEntrySegmentsEntryRel);
	}

	@Override
	public AssetListEntrySegmentsEntryRel addAssetListEntrySegmentsEntryRel(
			long userId, long groupId, long assetListEntryId, int priority,
			long segmentsEntryId, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.
			addAssetListEntrySegmentsEntryRel(
				userId, groupId, assetListEntryId, priority, segmentsEntryId,
				typeSettings, serviceContext);
	}

	@Override
	public AssetListEntrySegmentsEntryRel addAssetListEntrySegmentsEntryRel(
			long userId, long groupId, long assetListEntryId,
			long segmentsEntryId, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.
			addAssetListEntrySegmentsEntryRel(
				userId, groupId, assetListEntryId, segmentsEntryId,
				typeSettings, serviceContext);
	}

	/**
	 * Creates a new asset list entry segments entry rel with the primary key. Does not add the asset list entry segments entry rel to the database.
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key for the new asset list entry segments entry rel
	 * @return the new asset list entry segments entry rel
	 */
	@Override
	public AssetListEntrySegmentsEntryRel createAssetListEntrySegmentsEntryRel(
		long assetListEntrySegmentsEntryRelId) {

		return _assetListEntrySegmentsEntryRelLocalService.
			createAssetListEntrySegmentsEntryRel(
				assetListEntrySegmentsEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public AssetListEntrySegmentsEntryRel deleteAssetListEntrySegmentsEntryRel(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		return _assetListEntrySegmentsEntryRelLocalService.
			deleteAssetListEntrySegmentsEntryRel(
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
	@Override
	public AssetListEntrySegmentsEntryRel deleteAssetListEntrySegmentsEntryRel(
			long assetListEntrySegmentsEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.
			deleteAssetListEntrySegmentsEntryRel(
				assetListEntrySegmentsEntryRelId);
	}

	@Override
	public void deleteAssetListEntrySegmentsEntryRel(
			long assetListEntryId, long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntrySegmentsEntryRelLocalService.
			deleteAssetListEntrySegmentsEntryRel(
				assetListEntryId, segmentsEntryId);
	}

	@Override
	public void deleteAssetListEntrySegmentsEntryRelByAssetListEntryId(
		long assetListEntryId) {

		_assetListEntrySegmentsEntryRelLocalService.
			deleteAssetListEntrySegmentsEntryRelByAssetListEntryId(
				assetListEntryId);
	}

	@Override
	public void deleteAssetListEntrySegmentsEntryRelBySegmentsEntryId(
		long segmentsEntryId) {

		_assetListEntrySegmentsEntryRelLocalService.
			deleteAssetListEntrySegmentsEntryRelBySegmentsEntryId(
				segmentsEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetListEntrySegmentsEntryRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetListEntrySegmentsEntryRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetListEntrySegmentsEntryRelLocalService.dynamicQuery();
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

		return _assetListEntrySegmentsEntryRelLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _assetListEntrySegmentsEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _assetListEntrySegmentsEntryRelLocalService.dynamicQuery(
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

		return _assetListEntrySegmentsEntryRelLocalService.dynamicQueryCount(
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

		return _assetListEntrySegmentsEntryRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetListEntrySegmentsEntryRel fetchAssetListEntrySegmentsEntryRel(
		long assetListEntrySegmentsEntryRelId) {

		return _assetListEntrySegmentsEntryRelLocalService.
			fetchAssetListEntrySegmentsEntryRel(
				assetListEntrySegmentsEntryRelId);
	}

	@Override
	public AssetListEntrySegmentsEntryRel fetchAssetListEntrySegmentsEntryRel(
		long assetListEntryId, long segmentsEntryId) {

		return _assetListEntrySegmentsEntryRelLocalService.
			fetchAssetListEntrySegmentsEntryRel(
				assetListEntryId, segmentsEntryId);
	}

	/**
	 * Returns the asset list entry segments entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry segments entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry segments entry rel, or <code>null</code> if a matching asset list entry segments entry rel could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel
		fetchAssetListEntrySegmentsEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return _assetListEntrySegmentsEntryRelLocalService.
			fetchAssetListEntrySegmentsEntryRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public java.util.List<AssetListEntrySegmentsEntryRel>
		fetchAssetListEntrySegmentsEntryRels(
			long assetListEntryId, long[] segmentsEntryIds) {

		return _assetListEntrySegmentsEntryRelLocalService.
			fetchAssetListEntrySegmentsEntryRels(
				assetListEntryId, segmentsEntryIds);
	}

	@Override
	public java.util.List<AssetListEntrySegmentsEntryRel>
		fetchDynamicAssetListEntrySegmentsEntryRels(long companyId) {

		return _assetListEntrySegmentsEntryRelLocalService.
			fetchDynamicAssetListEntrySegmentsEntryRels(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetListEntrySegmentsEntryRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the asset list entry segments entry rel with the primary key.
	 *
	 * @param assetListEntrySegmentsEntryRelId the primary key of the asset list entry segments entry rel
	 * @return the asset list entry segments entry rel
	 * @throws PortalException if a asset list entry segments entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntrySegmentsEntryRel getAssetListEntrySegmentsEntryRel(
			long assetListEntrySegmentsEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRel(assetListEntrySegmentsEntryRelId);
	}

	@Override
	public AssetListEntrySegmentsEntryRel getAssetListEntrySegmentsEntryRel(
			long assetListEntryId, long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRel(
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
	@Override
	public AssetListEntrySegmentsEntryRel
			getAssetListEntrySegmentsEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRelByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels(int start, int end) {

		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRels(start, end);
	}

	@Override
	public java.util.List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels(
			long assetListEntryId, int start, int end) {

		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRels(assetListEntryId, start, end);
	}

	@Override
	public java.util.List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRels(
			long assetListEntryId, long[] segmentsEntryIds, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntrySegmentsEntryRel> orderByComparator) {

		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRels(
				assetListEntryId, segmentsEntryIds, start, end,
				orderByComparator);
	}

	/**
	 * Returns all the asset list entry segments entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry segments entry rels
	 * @param companyId the primary key of the company
	 * @return the matching asset list entry segments entry rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _assetListEntrySegmentsEntryRelLocalService.
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
	@Override
	public java.util.List<AssetListEntrySegmentsEntryRel>
		getAssetListEntrySegmentsEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntrySegmentsEntryRel> orderByComparator) {

		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset list entry segments entry rels.
	 *
	 * @return the number of asset list entry segments entry rels
	 */
	@Override
	public int getAssetListEntrySegmentsEntryRelsCount() {
		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRelsCount();
	}

	@Override
	public int getAssetListEntrySegmentsEntryRelsCount(long assetListEntryId) {
		return _assetListEntrySegmentsEntryRelLocalService.
			getAssetListEntrySegmentsEntryRelsCount(assetListEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _assetListEntrySegmentsEntryRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetListEntrySegmentsEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetListEntrySegmentsEntryRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntrySegmentsEntryRelLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public AssetListEntrySegmentsEntryRel updateAssetListEntrySegmentsEntryRel(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		return _assetListEntrySegmentsEntryRelLocalService.
			updateAssetListEntrySegmentsEntryRel(
				assetListEntrySegmentsEntryRel);
	}

	@Override
	public AssetListEntrySegmentsEntryRel
		updateAssetListEntrySegmentsEntryRelTypeSettings(
			long assetListEntryId, long segmentsEntryId, String typeSettings) {

		return _assetListEntrySegmentsEntryRelLocalService.
			updateAssetListEntrySegmentsEntryRelTypeSettings(
				assetListEntryId, segmentsEntryId, typeSettings);
	}

	@Override
	public void updateVariationsPriority(long[] variationsPriority) {
		_assetListEntrySegmentsEntryRelLocalService.updateVariationsPriority(
			variationsPriority);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetListEntrySegmentsEntryRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetListEntrySegmentsEntryRel> getCTPersistence() {
		return _assetListEntrySegmentsEntryRelLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetListEntrySegmentsEntryRel> getModelClass() {
		return _assetListEntrySegmentsEntryRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetListEntrySegmentsEntryRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetListEntrySegmentsEntryRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public AssetListEntrySegmentsEntryRelLocalService getWrappedService() {
		return _assetListEntrySegmentsEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		AssetListEntrySegmentsEntryRelLocalService
			assetListEntrySegmentsEntryRelLocalService) {

		_assetListEntrySegmentsEntryRelLocalService =
			assetListEntrySegmentsEntryRelLocalService;
	}

	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

}