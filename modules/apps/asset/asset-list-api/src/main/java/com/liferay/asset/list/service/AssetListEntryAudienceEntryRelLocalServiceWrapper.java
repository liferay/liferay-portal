/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetListEntryAudienceEntryRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAudienceEntryRelLocalService
 * @generated
 */
public class AssetListEntryAudienceEntryRelLocalServiceWrapper
	implements AssetListEntryAudienceEntryRelLocalService,
			   ServiceWrapper<AssetListEntryAudienceEntryRelLocalService> {

	public AssetListEntryAudienceEntryRelLocalServiceWrapper() {
		this(null);
	}

	public AssetListEntryAudienceEntryRelLocalServiceWrapper(
		AssetListEntryAudienceEntryRelLocalService
			assetListEntryAudienceEntryRelLocalService) {

		_assetListEntryAudienceEntryRelLocalService =
			assetListEntryAudienceEntryRelLocalService;
	}

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
	@Override
	public AssetListEntryAudienceEntryRel addAssetListEntryAudienceEntryRel(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		return _assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(assetListEntryAudienceEntryRel);
	}

	@Override
	public AssetListEntryAudienceEntryRel addAssetListEntryAudienceEntryRel(
			long userId, long groupId, long assetListEntryId,
			String audienceEntryERC, int priority, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				userId, groupId, assetListEntryId, audienceEntryERC, priority,
				typeSettings, serviceContext);
	}

	@Override
	public AssetListEntryAudienceEntryRel addAssetListEntryAudienceEntryRel(
			long userId, long groupId, long assetListEntryId,
			String audienceEntryERC, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				userId, groupId, assetListEntryId, audienceEntryERC,
				typeSettings, serviceContext);
	}

	/**
	 * Creates a new asset list entry audience entry rel with the primary key. Does not add the asset list entry audience entry rel to the database.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key for the new asset list entry audience entry rel
	 * @return the new asset list entry audience entry rel
	 */
	@Override
	public AssetListEntryAudienceEntryRel createAssetListEntryAudienceEntryRel(
		long assetListEntryAudienceEntryRelId) {

		return _assetListEntryAudienceEntryRelLocalService.
			createAssetListEntryAudienceEntryRel(
				assetListEntryAudienceEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public AssetListEntryAudienceEntryRel deleteAssetListEntryAudienceEntryRel(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		return _assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRel(
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
	@Override
	public AssetListEntryAudienceEntryRel deleteAssetListEntryAudienceEntryRel(
			long assetListEntryAudienceEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRel(
				assetListEntryAudienceEntryRelId);
	}

	@Override
	public void deleteAssetListEntryAudienceEntryRel(
			long assetListEntryId, String audienceEntryERC)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRel(
				assetListEntryId, audienceEntryERC);
	}

	@Override
	public void deleteAssetListEntryAudienceEntryRelByAssetListEntryId(
		long assetListEntryId) {

		_assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRelByAssetListEntryId(
				assetListEntryId);
	}

	@Override
	public void deleteAssetListEntryAudienceEntryRelByAudienceEntryERC(
		long companyId, String audienceEntryERC) {

		_assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRelByAudienceEntryERC(
				companyId, audienceEntryERC);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetListEntryAudienceEntryRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetListEntryAudienceEntryRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetListEntryAudienceEntryRelLocalService.dynamicQuery();
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

		return _assetListEntryAudienceEntryRelLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _assetListEntryAudienceEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _assetListEntryAudienceEntryRelLocalService.dynamicQuery(
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

		return _assetListEntryAudienceEntryRelLocalService.dynamicQueryCount(
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

		return _assetListEntryAudienceEntryRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetListEntryAudienceEntryRel fetchAssetListEntryAudienceEntryRel(
		long assetListEntryAudienceEntryRelId) {

		return _assetListEntryAudienceEntryRelLocalService.
			fetchAssetListEntryAudienceEntryRel(
				assetListEntryAudienceEntryRelId);
	}

	@Override
	public AssetListEntryAudienceEntryRel fetchAssetListEntryAudienceEntryRel(
		long assetListEntryId, String audienceEntryERC) {

		return _assetListEntryAudienceEntryRelLocalService.
			fetchAssetListEntryAudienceEntryRel(
				assetListEntryId, audienceEntryERC);
	}

	@Override
	public AssetListEntryAudienceEntryRel fetchAssetListEntryAudienceEntryRel(
		long assetListEntryId, String[] audienceEntryERCs) {

		return _assetListEntryAudienceEntryRelLocalService.
			fetchAssetListEntryAudienceEntryRel(
				assetListEntryId, audienceEntryERCs);
	}

	/**
	 * Returns the asset list entry audience entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel
		fetchAssetListEntryAudienceEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return _assetListEntryAudienceEntryRelLocalService.
			fetchAssetListEntryAudienceEntryRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetListEntryAudienceEntryRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the asset list entry audience entry rel with the primary key.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel
	 * @throws PortalException if a asset list entry audience entry rel with the primary key could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel getAssetListEntryAudienceEntryRel(
			long assetListEntryAudienceEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRel(assetListEntryAudienceEntryRelId);
	}

	/**
	 * Returns the asset list entry audience entry rel matching the UUID and group.
	 *
	 * @param uuid the asset list entry audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset list entry audience entry rel
	 * @throws PortalException if a matching asset list entry audience entry rel could not be found
	 */
	@Override
	public AssetListEntryAudienceEntryRel
			getAssetListEntryAudienceEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRelByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRels(int start, int end) {

		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRels(start, end);
	}

	@Override
	public java.util.List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRels(
			long assetListEntryId, int start, int end) {

		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRels(assetListEntryId, start, end);
	}

	/**
	 * Returns all the asset list entry audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset list entry audience entry rels
	 * @param companyId the primary key of the company
	 * @return the matching asset list entry audience entry rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _assetListEntryAudienceEntryRelLocalService.
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
	@Override
	public java.util.List<AssetListEntryAudienceEntryRel>
		getAssetListEntryAudienceEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator) {

		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset list entry audience entry rels.
	 *
	 * @return the number of asset list entry audience entry rels
	 */
	@Override
	public int getAssetListEntryAudienceEntryRelsCount() {
		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRelsCount();
	}

	@Override
	public int getAssetListEntryAudienceEntryRelsCount(long assetListEntryId) {
		return _assetListEntryAudienceEntryRelLocalService.
			getAssetListEntryAudienceEntryRelsCount(assetListEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _assetListEntryAudienceEntryRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetListEntryAudienceEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetListEntryAudienceEntryRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public AssetListEntryAudienceEntryRel updateAssetListEntryAudienceEntryRel(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		return _assetListEntryAudienceEntryRelLocalService.
			updateAssetListEntryAudienceEntryRel(
				assetListEntryAudienceEntryRel);
	}

	@Override
	public AssetListEntryAudienceEntryRel
			updateAssetListEntryAudienceEntryRelTypeSettings(
				long assetListEntryId, String audienceEntryERC,
				String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryAudienceEntryRelLocalService.
			updateAssetListEntryAudienceEntryRelTypeSettings(
				assetListEntryId, audienceEntryERC, typeSettings);
	}

	@Override
	public void updatePriorities(long[] assetListEntryAudienceEntryRelIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryAudienceEntryRelLocalService.updatePriorities(
			assetListEntryAudienceEntryRelIds);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetListEntryAudienceEntryRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetListEntryAudienceEntryRel> getCTPersistence() {
		return _assetListEntryAudienceEntryRelLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetListEntryAudienceEntryRel> getModelClass() {
		return _assetListEntryAudienceEntryRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetListEntryAudienceEntryRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetListEntryAudienceEntryRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public AssetListEntryAudienceEntryRelLocalService getWrappedService() {
		return _assetListEntryAudienceEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		AssetListEntryAudienceEntryRelLocalService
			assetListEntryAudienceEntryRelLocalService) {

		_assetListEntryAudienceEntryRelLocalService =
			assetListEntryAudienceEntryRelLocalService;
	}

	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1769050348