/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.rel.service;

import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetEntryAssetCategoryRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetEntryAssetCategoryRelLocalService
 * @generated
 */
public class AssetEntryAssetCategoryRelLocalServiceWrapper
	implements AssetEntryAssetCategoryRelLocalService,
			   ServiceWrapper<AssetEntryAssetCategoryRelLocalService> {

	public AssetEntryAssetCategoryRelLocalServiceWrapper() {
		this(null);
	}

	public AssetEntryAssetCategoryRelLocalServiceWrapper(
		AssetEntryAssetCategoryRelLocalService
			assetEntryAssetCategoryRelLocalService) {

		_assetEntryAssetCategoryRelLocalService =
			assetEntryAssetCategoryRelLocalService;
	}

	/**
	 * Adds the asset entry asset category rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryAssetCategoryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntryAssetCategoryRel the asset entry asset category rel
	 * @return the asset entry asset category rel that was added
	 */
	@Override
	public AssetEntryAssetCategoryRel addAssetEntryAssetCategoryRel(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		return _assetEntryAssetCategoryRelLocalService.
			addAssetEntryAssetCategoryRel(assetEntryAssetCategoryRel);
	}

	@Override
	public AssetEntryAssetCategoryRel addAssetEntryAssetCategoryRel(
		long assetEntryId, long assetCategoryId) {

		return _assetEntryAssetCategoryRelLocalService.
			addAssetEntryAssetCategoryRel(assetEntryId, assetCategoryId);
	}

	@Override
	public AssetEntryAssetCategoryRel addAssetEntryAssetCategoryRel(
		long assetEntryId, long assetCategoryId, int priority) {

		return _assetEntryAssetCategoryRelLocalService.
			addAssetEntryAssetCategoryRel(
				assetEntryId, assetCategoryId, priority);
	}

	/**
	 * Creates a new asset entry asset category rel with the primary key. Does not add the asset entry asset category rel to the database.
	 *
	 * @param assetEntryAssetCategoryRelId the primary key for the new asset entry asset category rel
	 * @return the new asset entry asset category rel
	 */
	@Override
	public AssetEntryAssetCategoryRel createAssetEntryAssetCategoryRel(
		long assetEntryAssetCategoryRelId) {

		return _assetEntryAssetCategoryRelLocalService.
			createAssetEntryAssetCategoryRel(assetEntryAssetCategoryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetEntryAssetCategoryRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the asset entry asset category rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryAssetCategoryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntryAssetCategoryRel the asset entry asset category rel
	 * @return the asset entry asset category rel that was removed
	 */
	@Override
	public AssetEntryAssetCategoryRel deleteAssetEntryAssetCategoryRel(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		return _assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRel(assetEntryAssetCategoryRel);
	}

	/**
	 * Deletes the asset entry asset category rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryAssetCategoryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntryAssetCategoryRelId the primary key of the asset entry asset category rel
	 * @return the asset entry asset category rel that was removed
	 * @throws PortalException if a asset entry asset category rel with the primary key could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel deleteAssetEntryAssetCategoryRel(
			long assetEntryAssetCategoryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRel(assetEntryAssetCategoryRelId);
	}

	@Override
	public void deleteAssetEntryAssetCategoryRel(
		long assetEntryId, long assetCategoryId) {

		_assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRel(assetEntryId, assetCategoryId);
	}

	@Override
	public void deleteAssetEntryAssetCategoryRelByAssetCategoryId(
		long assetCategoryId) {

		_assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRelByAssetCategoryId(assetCategoryId);
	}

	@Override
	public void deleteAssetEntryAssetCategoryRelByAssetEntry(
		com.liferay.asset.kernel.model.AssetEntry assetEntry) {

		_assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRelByAssetEntry(assetEntry);
	}

	@Override
	public void deleteAssetEntryAssetCategoryRelByAssetEntryId(
		long assetEntryId) {

		_assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRelByAssetEntryId(assetEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetEntryAssetCategoryRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetEntryAssetCategoryRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetEntryAssetCategoryRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetEntryAssetCategoryRelLocalService.dynamicQuery();
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

		return _assetEntryAssetCategoryRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelModelImpl</code>.
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

		return _assetEntryAssetCategoryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelModelImpl</code>.
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

		return _assetEntryAssetCategoryRelLocalService.dynamicQuery(
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

		return _assetEntryAssetCategoryRelLocalService.dynamicQueryCount(
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

		return _assetEntryAssetCategoryRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetEntryAssetCategoryRel fetchAssetEntryAssetCategoryRel(
		long assetEntryAssetCategoryRelId) {

		return _assetEntryAssetCategoryRelLocalService.
			fetchAssetEntryAssetCategoryRel(assetEntryAssetCategoryRelId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetEntryAssetCategoryRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public long[] getAssetCategoryPrimaryKeys(long assetEntryId) {
		return _assetEntryAssetCategoryRelLocalService.
			getAssetCategoryPrimaryKeys(assetEntryId);
	}

	/**
	 * Returns the asset entry asset category rel with the primary key.
	 *
	 * @param assetEntryAssetCategoryRelId the primary key of the asset entry asset category rel
	 * @return the asset entry asset category rel
	 * @throws PortalException if a asset entry asset category rel with the primary key could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel getAssetEntryAssetCategoryRel(
			long assetEntryAssetCategoryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRel(assetEntryAssetCategoryRelId);
	}

	/**
	 * Returns a range of all the asset entry asset category rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset entry asset category rels
	 * @param end the upper bound of the range of asset entry asset category rels (not inclusive)
	 * @return the range of asset entry asset category rels
	 */
	@Override
	public java.util.List<AssetEntryAssetCategoryRel>
		getAssetEntryAssetCategoryRels(int start, int end) {

		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRels(start, end);
	}

	@Override
	public java.util.List<AssetEntryAssetCategoryRel>
		getAssetEntryAssetCategoryRelsByAssetCategoryId(long assetCategoryId) {

		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRelsByAssetCategoryId(assetCategoryId);
	}

	@Override
	public java.util.List<AssetEntryAssetCategoryRel>
		getAssetEntryAssetCategoryRelsByAssetEntryId(long assetEntryId) {

		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRelsByAssetEntryId(assetEntryId);
	}

	@Override
	public java.util.List<AssetEntryAssetCategoryRel>
		getAssetEntryAssetCategoryRelsByAssetEntryId(
			long assetEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetEntryAssetCategoryRel> orderByComparator) {

		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRelsByAssetEntryId(
				assetEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset entry asset category rels.
	 *
	 * @return the number of asset entry asset category rels
	 */
	@Override
	public int getAssetEntryAssetCategoryRelsCount() {
		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRelsCount();
	}

	@Override
	public int getAssetEntryAssetCategoryRelsCount(long assetEntryId) {
		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRelsCount(assetEntryId);
	}

	@Override
	public int getAssetEntryAssetCategoryRelsCountByAssetCategoryId(
		long assetCategoryId) {

		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRelsCountByAssetCategoryId(
				assetCategoryId);
	}

	@Override
	public int getAssetEntryAssetCategoryRelsCountByClassNameId(
		long assetCategoryId, long classNameId) {

		return _assetEntryAssetCategoryRelLocalService.
			getAssetEntryAssetCategoryRelsCountByClassNameId(
				assetCategoryId, classNameId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetEntryAssetCategoryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetEntryAssetCategoryRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetEntryAssetCategoryRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the asset entry asset category rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryAssetCategoryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntryAssetCategoryRel the asset entry asset category rel
	 * @return the asset entry asset category rel that was updated
	 */
	@Override
	public AssetEntryAssetCategoryRel updateAssetEntryAssetCategoryRel(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		return _assetEntryAssetCategoryRelLocalService.
			updateAssetEntryAssetCategoryRel(assetEntryAssetCategoryRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetEntryAssetCategoryRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetEntryAssetCategoryRel> getCTPersistence() {
		return _assetEntryAssetCategoryRelLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetEntryAssetCategoryRel> getModelClass() {
		return _assetEntryAssetCategoryRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetEntryAssetCategoryRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetEntryAssetCategoryRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetEntryAssetCategoryRelLocalService getWrappedService() {
		return _assetEntryAssetCategoryRelLocalService;
	}

	@Override
	public void setWrappedService(
		AssetEntryAssetCategoryRelLocalService
			assetEntryAssetCategoryRelLocalService) {

		_assetEntryAssetCategoryRelLocalService =
			assetEntryAssetCategoryRelLocalService;
	}

	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-561491171