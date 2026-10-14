/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.service;

import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetAutoTaggerEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetAutoTaggerEntryLocalService
 * @generated
 */
public class AssetAutoTaggerEntryLocalServiceWrapper
	implements AssetAutoTaggerEntryLocalService,
			   ServiceWrapper<AssetAutoTaggerEntryLocalService> {

	public AssetAutoTaggerEntryLocalServiceWrapper() {
		this(null);
	}

	public AssetAutoTaggerEntryLocalServiceWrapper(
		AssetAutoTaggerEntryLocalService assetAutoTaggerEntryLocalService) {

		_assetAutoTaggerEntryLocalService = assetAutoTaggerEntryLocalService;
	}

	/**
	 * Adds the asset auto tagger entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetAutoTaggerEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetAutoTaggerEntry the asset auto tagger entry
	 * @return the asset auto tagger entry that was added
	 */
	@Override
	public AssetAutoTaggerEntry addAssetAutoTaggerEntry(
		AssetAutoTaggerEntry assetAutoTaggerEntry) {

		return _assetAutoTaggerEntryLocalService.addAssetAutoTaggerEntry(
			assetAutoTaggerEntry);
	}

	@Override
	public AssetAutoTaggerEntry addAssetAutoTaggerEntry(
		com.liferay.asset.kernel.model.AssetEntry assetEntry,
		com.liferay.asset.kernel.model.AssetTag assetTag) {

		return _assetAutoTaggerEntryLocalService.addAssetAutoTaggerEntry(
			assetEntry, assetTag);
	}

	@Override
	public AssetAutoTaggerEntry addAssetAutoTaggerEntry(
			com.liferay.asset.kernel.model.AssetEntry assetEntry,
			String assetTagName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetAutoTaggerEntryLocalService.addAssetAutoTaggerEntry(
			assetEntry, assetTagName);
	}

	/**
	 * Creates a new asset auto tagger entry with the primary key. Does not add the asset auto tagger entry to the database.
	 *
	 * @param assetAutoTaggerEntryId the primary key for the new asset auto tagger entry
	 * @return the new asset auto tagger entry
	 */
	@Override
	public AssetAutoTaggerEntry createAssetAutoTaggerEntry(
		long assetAutoTaggerEntryId) {

		return _assetAutoTaggerEntryLocalService.createAssetAutoTaggerEntry(
			assetAutoTaggerEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetAutoTaggerEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the asset auto tagger entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetAutoTaggerEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetAutoTaggerEntry the asset auto tagger entry
	 * @return the asset auto tagger entry that was removed
	 */
	@Override
	public AssetAutoTaggerEntry deleteAssetAutoTaggerEntry(
		AssetAutoTaggerEntry assetAutoTaggerEntry) {

		return _assetAutoTaggerEntryLocalService.deleteAssetAutoTaggerEntry(
			assetAutoTaggerEntry);
	}

	/**
	 * Deletes the asset auto tagger entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetAutoTaggerEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetAutoTaggerEntryId the primary key of the asset auto tagger entry
	 * @return the asset auto tagger entry that was removed
	 * @throws PortalException if a asset auto tagger entry with the primary key could not be found
	 */
	@Override
	public AssetAutoTaggerEntry deleteAssetAutoTaggerEntry(
			long assetAutoTaggerEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetAutoTaggerEntryLocalService.deleteAssetAutoTaggerEntry(
			assetAutoTaggerEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetAutoTaggerEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetAutoTaggerEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetAutoTaggerEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetAutoTaggerEntryLocalService.dynamicQuery();
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

		return _assetAutoTaggerEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.auto.tagger.model.impl.AssetAutoTaggerEntryModelImpl</code>.
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

		return _assetAutoTaggerEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.auto.tagger.model.impl.AssetAutoTaggerEntryModelImpl</code>.
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

		return _assetAutoTaggerEntryLocalService.dynamicQuery(
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

		return _assetAutoTaggerEntryLocalService.dynamicQueryCount(
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

		return _assetAutoTaggerEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetAutoTaggerEntry fetchAssetAutoTaggerEntry(
		long assetAutoTaggerEntryId) {

		return _assetAutoTaggerEntryLocalService.fetchAssetAutoTaggerEntry(
			assetAutoTaggerEntryId);
	}

	@Override
	public AssetAutoTaggerEntry fetchAssetAutoTaggerEntry(
		long assetEntryId, long assetTagId) {

		return _assetAutoTaggerEntryLocalService.fetchAssetAutoTaggerEntry(
			assetEntryId, assetTagId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetAutoTaggerEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<AssetAutoTaggerEntry> getAssetAutoTaggerEntries(
		com.liferay.asset.kernel.model.AssetEntry assetEntry) {

		return _assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
			assetEntry);
	}

	/**
	 * Returns a range of all the asset auto tagger entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.auto.tagger.model.impl.AssetAutoTaggerEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset auto tagger entries
	 * @param end the upper bound of the range of asset auto tagger entries (not inclusive)
	 * @return the range of asset auto tagger entries
	 */
	@Override
	public java.util.List<AssetAutoTaggerEntry> getAssetAutoTaggerEntries(
		int start, int end) {

		return _assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntries(
			start, end);
	}

	/**
	 * Returns the number of asset auto tagger entries.
	 *
	 * @return the number of asset auto tagger entries
	 */
	@Override
	public int getAssetAutoTaggerEntriesCount() {
		return _assetAutoTaggerEntryLocalService.
			getAssetAutoTaggerEntriesCount();
	}

	/**
	 * Returns the asset auto tagger entry with the primary key.
	 *
	 * @param assetAutoTaggerEntryId the primary key of the asset auto tagger entry
	 * @return the asset auto tagger entry
	 * @throws PortalException if a asset auto tagger entry with the primary key could not be found
	 */
	@Override
	public AssetAutoTaggerEntry getAssetAutoTaggerEntry(
			long assetAutoTaggerEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetAutoTaggerEntryLocalService.getAssetAutoTaggerEntry(
			assetAutoTaggerEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetAutoTaggerEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetAutoTaggerEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetAutoTaggerEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the asset auto tagger entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetAutoTaggerEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetAutoTaggerEntry the asset auto tagger entry
	 * @return the asset auto tagger entry that was updated
	 */
	@Override
	public AssetAutoTaggerEntry updateAssetAutoTaggerEntry(
		AssetAutoTaggerEntry assetAutoTaggerEntry) {

		return _assetAutoTaggerEntryLocalService.updateAssetAutoTaggerEntry(
			assetAutoTaggerEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetAutoTaggerEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetAutoTaggerEntry> getCTPersistence() {
		return _assetAutoTaggerEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetAutoTaggerEntry> getModelClass() {
		return _assetAutoTaggerEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetAutoTaggerEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetAutoTaggerEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetAutoTaggerEntryLocalService getWrappedService() {
		return _assetAutoTaggerEntryLocalService;
	}

	@Override
	public void setWrappedService(
		AssetAutoTaggerEntryLocalService assetAutoTaggerEntryLocalService) {

		_assetAutoTaggerEntryLocalService = assetAutoTaggerEntryLocalService;
	}

	private AssetAutoTaggerEntryLocalService _assetAutoTaggerEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2085012425