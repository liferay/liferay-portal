/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetVocabularyLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyLocalService
 * @generated
 */
public class AssetVocabularyLocalServiceWrapper
	implements AssetVocabularyLocalService,
			   ServiceWrapper<AssetVocabularyLocalService> {

	public AssetVocabularyLocalServiceWrapper() {
		this(null);
	}

	public AssetVocabularyLocalServiceWrapper(
		AssetVocabularyLocalService assetVocabularyLocalService) {

		_assetVocabularyLocalService = assetVocabularyLocalService;
	}

	/**
	 * Adds the asset vocabulary to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabulary the asset vocabulary
	 * @return the asset vocabulary that was added
	 */
	@Override
	public AssetVocabulary addAssetVocabulary(AssetVocabulary assetVocabulary) {
		return _assetVocabularyLocalService.addAssetVocabulary(assetVocabulary);
	}

	@Override
	public AssetVocabulary addDefaultVocabulary(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.addDefaultVocabulary(groupId);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings, int visibilityType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.addVocabulary(
			userId, groupId, title, titleMap, descriptionMap, settings,
			visibilityType, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.addVocabulary(
			userId, groupId, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.addVocabulary(
			userId, groupId, title, serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			long userId, long groupId, String name, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.addVocabulary(
			userId, groupId, name, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	@Override
	public AssetVocabulary addVocabulary(
			String externalReferenceCode, long userId, long groupId,
			String name, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings, int visibilityType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.addVocabulary(
			externalReferenceCode, userId, groupId, name, title, titleMap,
			descriptionMap, settings, visibilityType, serviceContext);
	}

	@Override
	public void addVocabularyResources(
			AssetVocabulary vocabulary, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetVocabularyLocalService.addVocabularyResources(
			vocabulary, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addVocabularyResources(
			AssetVocabulary vocabulary,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetVocabularyLocalService.addVocabularyResources(
			vocabulary, modelPermissions);
	}

	/**
	 * Creates a new asset vocabulary with the primary key. Does not add the asset vocabulary to the database.
	 *
	 * @param vocabularyId the primary key for the new asset vocabulary
	 * @return the new asset vocabulary
	 */
	@Override
	public AssetVocabulary createAssetVocabulary(long vocabularyId) {
		return _assetVocabularyLocalService.createAssetVocabulary(vocabularyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the asset vocabulary from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabulary the asset vocabulary
	 * @return the asset vocabulary that was removed
	 */
	@Override
	public AssetVocabulary deleteAssetVocabulary(
		AssetVocabulary assetVocabulary) {

		return _assetVocabularyLocalService.deleteAssetVocabulary(
			assetVocabulary);
	}

	/**
	 * Deletes the asset vocabulary with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary that was removed
	 * @throws PortalException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary deleteAssetVocabulary(long vocabularyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.deleteAssetVocabulary(vocabularyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteVocabularies(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetVocabularyLocalService.deleteVocabularies(groupId);
	}

	@Override
	public AssetVocabulary deleteVocabulary(AssetVocabulary vocabulary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.deleteVocabulary(vocabulary);
	}

	@Override
	public void deleteVocabulary(long vocabularyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetVocabularyLocalService.deleteVocabulary(vocabularyId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetVocabularyLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetVocabularyLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetVocabularyLocalService.dynamicQuery();
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

		return _assetVocabularyLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl</code>.
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

		return _assetVocabularyLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl</code>.
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

		return _assetVocabularyLocalService.dynamicQuery(
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

		return _assetVocabularyLocalService.dynamicQueryCount(dynamicQuery);
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

		return _assetVocabularyLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetVocabulary fetchAssetVocabulary(long vocabularyId) {
		return _assetVocabularyLocalService.fetchAssetVocabulary(vocabularyId);
	}

	@Override
	public AssetVocabulary fetchAssetVocabularyByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _assetVocabularyLocalService.
			fetchAssetVocabularyByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset vocabulary matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary fetchAssetVocabularyByUuidAndGroupId(
		String uuid, long groupId) {

		return _assetVocabularyLocalService.
			fetchAssetVocabularyByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public AssetVocabulary fetchGroupVocabulary(long groupId, String name) {
		return _assetVocabularyLocalService.fetchGroupVocabulary(groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetVocabularyLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the asset vocabularies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @return the range of asset vocabularies
	 */
	@Override
	public java.util.List<AssetVocabulary> getAssetVocabularies(
		int start, int end) {

		return _assetVocabularyLocalService.getAssetVocabularies(start, end);
	}

	/**
	 * Returns all the asset vocabularies matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabularies
	 * @param companyId the primary key of the company
	 * @return the matching asset vocabularies, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetVocabulary>
		getAssetVocabulariesByUuidAndCompanyId(String uuid, long companyId) {

		return _assetVocabularyLocalService.
			getAssetVocabulariesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of asset vocabularies matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabularies
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset vocabularies
	 * @param end the upper bound of the range of asset vocabularies (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset vocabularies, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<AssetVocabulary>
		getAssetVocabulariesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<AssetVocabulary>
				orderByComparator) {

		return _assetVocabularyLocalService.
			getAssetVocabulariesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset vocabularies.
	 *
	 * @return the number of asset vocabularies
	 */
	@Override
	public int getAssetVocabulariesCount() {
		return _assetVocabularyLocalService.getAssetVocabulariesCount();
	}

	/**
	 * Returns the asset vocabulary with the primary key.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary
	 * @throws PortalException if a asset vocabulary with the primary key could not be found
	 */
	@Override
	public AssetVocabulary getAssetVocabulary(long vocabularyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getAssetVocabulary(vocabularyId);
	}

	@Override
	public AssetVocabulary getAssetVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.
			getAssetVocabularyByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset vocabulary matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary
	 * @throws PortalException if a matching asset vocabulary could not be found
	 */
	@Override
	public AssetVocabulary getAssetVocabularyByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getAssetVocabularyByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.List<AssetVocabulary> getCompanyVocabularies(
		long companyId) {

		return _assetVocabularyLocalService.getCompanyVocabularies(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _assetVocabularyLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds) {

		return _assetVocabularyLocalService.getGroupsVocabularies(groupIds);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className) {

		return _assetVocabularyLocalService.getGroupsVocabularies(
			groupIds, className);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className, long classTypePK) {

		return _assetVocabularyLocalService.getGroupsVocabularies(
			groupIds, className, classTypePK);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getGroupVocabularies(groupId);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean addDefaultVocabulary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getGroupVocabularies(
			groupId, addDefaultVocabulary);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
		long groupId, int visibilityType) {

		return _assetVocabularyLocalService.getGroupVocabularies(
			groupId, visibilityType);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds) {

		return _assetVocabularyLocalService.getGroupVocabularies(groupIds);
	}

	@Override
	public java.util.List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes) {

		return _assetVocabularyLocalService.getGroupVocabularies(
			groupIds, visibilityTypes);
	}

	@Override
	public int getGroupVocabulariesCount(long[] groupIds) {
		return _assetVocabularyLocalService.getGroupVocabulariesCount(groupIds);
	}

	@Override
	public AssetVocabulary getGroupVocabulary(long groupId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getGroupVocabulary(groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetVocabularyLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public AssetVocabulary getOrAddEmptyVocabulary(
			String externalReferenceCode, long userId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getOrAddEmptyVocabulary(
			externalReferenceCode, userId, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetVocabularyLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<AssetVocabulary> getVocabularies(
			com.liferay.portal.kernel.search.Hits hits)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getVocabularies(hits);
	}

	@Override
	public java.util.List<AssetVocabulary> getVocabularies(long[] vocabularyIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getVocabularies(vocabularyIds);
	}

	@Override
	public AssetVocabulary getVocabulary(long vocabularyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.getVocabulary(vocabularyId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<AssetVocabulary> searchVocabularies(
				long companyId, long[] groupIds, String title,
				int[] visibilityTypes, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.searchVocabularies(
			companyId, groupIds, title, visibilityTypes, start, end, sort);
	}

	/**
	 * Updates the asset vocabulary in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabulary the asset vocabulary
	 * @return the asset vocabulary that was updated
	 */
	@Override
	public AssetVocabulary updateAssetVocabulary(
		AssetVocabulary assetVocabulary) {

		return _assetVocabularyLocalService.updateAssetVocabulary(
			assetVocabulary);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings, int visibilityType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings, visibilityType);
	}

	@Override
	public AssetVocabulary updateVocabulary(
			long vocabularyId, String title,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetVocabularyLocalService.updateVocabulary(
			vocabularyId, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetVocabularyLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetVocabulary> getCTPersistence() {
		return _assetVocabularyLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetVocabulary> getModelClass() {
		return _assetVocabularyLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetVocabulary>, R, E>
				updateUnsafeFunction)
		throws E {

		return _assetVocabularyLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetVocabularyLocalService getWrappedService() {
		return _assetVocabularyLocalService;
	}

	@Override
	public void setWrappedService(
		AssetVocabularyLocalService assetVocabularyLocalService) {

		_assetVocabularyLocalService = assetVocabularyLocalService;
	}

	private AssetVocabularyLocalService _assetVocabularyLocalService;

}