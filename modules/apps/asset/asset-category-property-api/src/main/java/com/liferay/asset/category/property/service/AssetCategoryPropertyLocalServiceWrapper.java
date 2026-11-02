/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.service;

import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetCategoryPropertyLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryPropertyLocalService
 * @generated
 */
public class AssetCategoryPropertyLocalServiceWrapper
	implements AssetCategoryPropertyLocalService,
			   ServiceWrapper<AssetCategoryPropertyLocalService> {

	public AssetCategoryPropertyLocalServiceWrapper() {
		this(null);
	}

	public AssetCategoryPropertyLocalServiceWrapper(
		AssetCategoryPropertyLocalService assetCategoryPropertyLocalService) {

		_assetCategoryPropertyLocalService = assetCategoryPropertyLocalService;
	}

	/**
	 * Adds the asset category property to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryPropertyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategoryProperty the asset category property
	 * @return the asset category property that was added
	 */
	@Override
	public AssetCategoryProperty addAssetCategoryProperty(
		AssetCategoryProperty assetCategoryProperty) {

		return _assetCategoryPropertyLocalService.addAssetCategoryProperty(
			assetCategoryProperty);
	}

	@Override
	public AssetCategoryProperty addCategoryProperty(
			long userId, long categoryId, String key, String value)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryPropertyLocalService.addCategoryProperty(
			userId, categoryId, key, value);
	}

	/**
	 * Creates a new asset category property with the primary key. Does not add the asset category property to the database.
	 *
	 * @param categoryPropertyId the primary key for the new asset category property
	 * @return the new asset category property
	 */
	@Override
	public AssetCategoryProperty createAssetCategoryProperty(
		long categoryPropertyId) {

		return _assetCategoryPropertyLocalService.createAssetCategoryProperty(
			categoryPropertyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryPropertyLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the asset category property from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryPropertyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategoryProperty the asset category property
	 * @return the asset category property that was removed
	 */
	@Override
	public AssetCategoryProperty deleteAssetCategoryProperty(
		AssetCategoryProperty assetCategoryProperty) {

		return _assetCategoryPropertyLocalService.deleteAssetCategoryProperty(
			assetCategoryProperty);
	}

	/**
	 * Deletes the asset category property with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryPropertyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property that was removed
	 * @throws PortalException if a asset category property with the primary key could not be found
	 */
	@Override
	public AssetCategoryProperty deleteAssetCategoryProperty(
			long categoryPropertyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryPropertyLocalService.deleteAssetCategoryProperty(
			categoryPropertyId);
	}

	@Override
	public void deleteCategoryProperties(long entryId) {
		_assetCategoryPropertyLocalService.deleteCategoryProperties(entryId);
	}

	@Override
	public void deleteCategoryProperty(AssetCategoryProperty categoryProperty) {
		_assetCategoryPropertyLocalService.deleteCategoryProperty(
			categoryProperty);
	}

	@Override
	public void deleteCategoryProperty(long categoryPropertyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetCategoryPropertyLocalService.deleteCategoryProperty(
			categoryPropertyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryPropertyLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetCategoryPropertyLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetCategoryPropertyLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetCategoryPropertyLocalService.dynamicQuery();
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

		return _assetCategoryPropertyLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
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

		return _assetCategoryPropertyLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
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

		return _assetCategoryPropertyLocalService.dynamicQuery(
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

		return _assetCategoryPropertyLocalService.dynamicQueryCount(
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

		return _assetCategoryPropertyLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetCategoryProperty fetchAssetCategoryProperty(
		long categoryPropertyId) {

		return _assetCategoryPropertyLocalService.fetchAssetCategoryProperty(
			categoryPropertyId);
	}

	@Override
	public AssetCategoryProperty
		fetchAssetCategoryPropertyByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _assetCategoryPropertyLocalService.
			fetchAssetCategoryPropertyByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public AssetCategoryProperty fetchCategoryProperty(
		long categoryId, String key) {

		return _assetCategoryPropertyLocalService.fetchCategoryProperty(
			categoryId, key);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetCategoryPropertyLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the asset category properties.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @return the range of asset category properties
	 */
	@Override
	public java.util.List<AssetCategoryProperty> getAssetCategoryProperties(
		int start, int end) {

		return _assetCategoryPropertyLocalService.getAssetCategoryProperties(
			start, end);
	}

	/**
	 * Returns the number of asset category properties.
	 *
	 * @return the number of asset category properties
	 */
	@Override
	public int getAssetCategoryPropertiesCount() {
		return _assetCategoryPropertyLocalService.
			getAssetCategoryPropertiesCount();
	}

	/**
	 * Returns the asset category property with the primary key.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property
	 * @throws PortalException if a asset category property with the primary key could not be found
	 */
	@Override
	public AssetCategoryProperty getAssetCategoryProperty(
			long categoryPropertyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryPropertyLocalService.getAssetCategoryProperty(
			categoryPropertyId);
	}

	@Override
	public AssetCategoryProperty
			getAssetCategoryPropertyByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryPropertyLocalService.
			getAssetCategoryPropertyByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<AssetCategoryProperty> getCategoryProperties(
		long entryId) {

		return _assetCategoryPropertyLocalService.getCategoryProperties(
			entryId);
	}

	@Override
	public java.util.List<AssetCategoryProperty> getCategoryPropertyValues(
		long groupId, String key) {

		return _assetCategoryPropertyLocalService.getCategoryPropertyValues(
			groupId, key);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetCategoryPropertyLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetCategoryPropertyLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryPropertyLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the asset category property in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryPropertyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategoryProperty the asset category property
	 * @return the asset category property that was updated
	 */
	@Override
	public AssetCategoryProperty updateAssetCategoryProperty(
		AssetCategoryProperty assetCategoryProperty) {

		return _assetCategoryPropertyLocalService.updateAssetCategoryProperty(
			assetCategoryProperty);
	}

	@Override
	public AssetCategoryProperty updateCategoryProperty(
			long userId, long categoryPropertyId, String key, String value)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryPropertyLocalService.updateCategoryProperty(
			userId, categoryPropertyId, key, value);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetCategoryPropertyLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetCategoryProperty> getCTPersistence() {
		return _assetCategoryPropertyLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetCategoryProperty> getModelClass() {
		return _assetCategoryPropertyLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetCategoryProperty>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetCategoryPropertyLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetCategoryPropertyLocalService getWrappedService() {
		return _assetCategoryPropertyLocalService;
	}

	@Override
	public void setWrappedService(
		AssetCategoryPropertyLocalService assetCategoryPropertyLocalService) {

		_assetCategoryPropertyLocalService = assetCategoryPropertyLocalService;
	}

	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:732291467