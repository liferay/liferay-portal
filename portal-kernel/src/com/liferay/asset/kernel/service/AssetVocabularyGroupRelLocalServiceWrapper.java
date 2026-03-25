/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetVocabularyGroupRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyGroupRelLocalService
 * @generated
 */
public class AssetVocabularyGroupRelLocalServiceWrapper
	implements AssetVocabularyGroupRelLocalService,
			   ServiceWrapper<AssetVocabularyGroupRelLocalService> {

	public AssetVocabularyGroupRelLocalServiceWrapper() {
		this(null);
	}

	public AssetVocabularyGroupRelLocalServiceWrapper(
		AssetVocabularyGroupRelLocalService
			assetVocabularyGroupRelLocalService) {

		_assetVocabularyGroupRelLocalService =
			assetVocabularyGroupRelLocalService;
	}

	/**
	 * Adds the asset vocabulary group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was added
	 */
	@Override
	public AssetVocabularyGroupRel addAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		return _assetVocabularyGroupRelLocalService.addAssetVocabularyGroupRel(
			assetVocabularyGroupRel);
	}

	@Override
	public AssetVocabularyGroupRel addAssetVocabularyGroupRel(
			long groupId, long vocabularyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyGroupRelLocalService.addAssetVocabularyGroupRel(
			groupId, vocabularyId);
	}

	/**
	 * Creates a new asset vocabulary group rel with the primary key. Does not add the asset vocabulary group rel to the database.
	 *
	 * @param assetVocabularyGroupRelId the primary key for the new asset vocabulary group rel
	 * @return the new asset vocabulary group rel
	 */
	@Override
	public AssetVocabularyGroupRel createAssetVocabularyGroupRel(
		long assetVocabularyGroupRelId) {

		return _assetVocabularyGroupRelLocalService.
			createAssetVocabularyGroupRel(assetVocabularyGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyGroupRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the asset vocabulary group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was removed
	 */
	@Override
	public AssetVocabularyGroupRel deleteAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		return _assetVocabularyGroupRelLocalService.
			deleteAssetVocabularyGroupRel(assetVocabularyGroupRel);
	}

	/**
	 * Deletes the asset vocabulary group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was removed
	 * @throws PortalException if a asset vocabulary group rel with the primary key could not be found
	 */
	@Override
	public AssetVocabularyGroupRel deleteAssetVocabularyGroupRel(
			long assetVocabularyGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyGroupRelLocalService.
			deleteAssetVocabularyGroupRel(assetVocabularyGroupRelId);
	}

	@Override
	public void deleteAssetVocabularyGroupRelsByGroupId(long groupId) {
		_assetVocabularyGroupRelLocalService.
			deleteAssetVocabularyGroupRelsByGroupId(groupId);
	}

	@Override
	public void deleteAssetVocabularyGroupRelsByVocabularyId(
		long vocabularyId) {

		_assetVocabularyGroupRelLocalService.
			deleteAssetVocabularyGroupRelsByVocabularyId(vocabularyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyGroupRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetVocabularyGroupRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetVocabularyGroupRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetVocabularyGroupRelLocalService.dynamicQuery();
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

		return _assetVocabularyGroupRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
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

		return _assetVocabularyGroupRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
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

		return _assetVocabularyGroupRelLocalService.dynamicQuery(
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

		return _assetVocabularyGroupRelLocalService.dynamicQueryCount(
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

		return _assetVocabularyGroupRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetVocabularyGroupRel fetchAssetVocabularyGroupRel(
		long assetVocabularyGroupRelId) {

		return _assetVocabularyGroupRelLocalService.
			fetchAssetVocabularyGroupRel(assetVocabularyGroupRelId);
	}

	/**
	 * Returns the asset vocabulary group rel matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel fetchAssetVocabularyGroupRelByUuidAndGroupId(
		String uuid, long groupId) {

		return _assetVocabularyGroupRelLocalService.
			fetchAssetVocabularyGroupRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetVocabularyGroupRelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the asset vocabulary group rel with the primary key.
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel
	 * @throws PortalException if a asset vocabulary group rel with the primary key could not be found
	 */
	@Override
	public AssetVocabularyGroupRel getAssetVocabularyGroupRel(
			long assetVocabularyGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyGroupRelLocalService.getAssetVocabularyGroupRel(
			assetVocabularyGroupRelId);
	}

	/**
	 * Returns the asset vocabulary group rel matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary group rel
	 * @throws PortalException if a matching asset vocabulary group rel could not be found
	 */
	@Override
	public AssetVocabularyGroupRel getAssetVocabularyGroupRelByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyGroupRelLocalService.
			getAssetVocabularyGroupRelByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the asset vocabulary group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @return the range of asset vocabulary group rels
	 */
	@Override
	public java.util.List<AssetVocabularyGroupRel> getAssetVocabularyGroupRels(
		int start, int end) {

		return _assetVocabularyGroupRelLocalService.getAssetVocabularyGroupRels(
			start, end);
	}

	@Override
	public java.util.List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByGroupId(long groupId) {

		return _assetVocabularyGroupRelLocalService.
			getAssetVocabularyGroupRelsByGroupId(groupId);
	}

	/**
	 * Returns all the asset vocabulary group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabulary group rels
	 * @param companyId the primary key of the company
	 * @return the matching asset vocabulary group rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _assetVocabularyGroupRelLocalService.
			getAssetVocabularyGroupRelsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of asset vocabulary group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabulary group rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset vocabulary group rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetVocabularyGroupRel> orderByComparator) {

		return _assetVocabularyGroupRelLocalService.
			getAssetVocabularyGroupRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByVocabularyId(long vocabularyId) {

		return _assetVocabularyGroupRelLocalService.
			getAssetVocabularyGroupRelsByVocabularyId(vocabularyId);
	}

	/**
	 * Returns the number of asset vocabulary group rels.
	 *
	 * @return the number of asset vocabulary group rels
	 */
	@Override
	public int getAssetVocabularyGroupRelsCount() {
		return _assetVocabularyGroupRelLocalService.
			getAssetVocabularyGroupRelsCount();
	}

	@Override
	public int getAssetVocabularyGroupRelsCount(long vocabularyId) {
		return _assetVocabularyGroupRelLocalService.
			getAssetVocabularyGroupRelsCount(vocabularyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetVocabularyGroupRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetVocabularyGroupRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyGroupRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void setAssetVocabularyGroupRels(long vocabularyId, long[] groupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			vocabularyId, groupIds);
	}

	/**
	 * Updates the asset vocabulary group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was updated
	 */
	@Override
	public AssetVocabularyGroupRel updateAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		return _assetVocabularyGroupRelLocalService.
			updateAssetVocabularyGroupRel(assetVocabularyGroupRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetVocabularyGroupRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetVocabularyGroupRel> getCTPersistence() {
		return _assetVocabularyGroupRelLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetVocabularyGroupRel> getModelClass() {
		return _assetVocabularyGroupRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetVocabularyGroupRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetVocabularyGroupRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetVocabularyGroupRelLocalService getWrappedService() {
		return _assetVocabularyGroupRelLocalService;
	}

	@Override
	public void setWrappedService(
		AssetVocabularyGroupRelLocalService
			assetVocabularyGroupRelLocalService) {

		_assetVocabularyGroupRelLocalService =
			assetVocabularyGroupRelLocalService;
	}

	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1836138432