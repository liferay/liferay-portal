/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for AssetVocabulary. This utility wraps
 * <code>com.liferay.portlet.asset.service.impl.AssetVocabularyLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyLocalService
 * @generated
 */
public class AssetVocabularyLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetVocabularyLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static AssetVocabulary addAssetVocabulary(
		AssetVocabulary assetVocabulary) {

		return getService().addAssetVocabulary(assetVocabulary);
	}

	public static AssetVocabulary addDefaultVocabulary(long groupId)
		throws PortalException {

		return getService().addDefaultVocabulary(groupId);
	}

	public static AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			int visibilityType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			userId, groupId, title, titleMap, descriptionMap, settings,
			visibilityType, serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			userId, groupId, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			long userId, long groupId, String title,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			userId, groupId, title, serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			long userId, long groupId, String name, String title,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			userId, groupId, name, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	public static AssetVocabulary addVocabulary(
			String externalReferenceCode, long userId, long groupId,
			String name, String title, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			int visibilityType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addVocabulary(
			externalReferenceCode, userId, groupId, name, title, titleMap,
			descriptionMap, settings, visibilityType, serviceContext);
	}

	public static void addVocabularyResources(
			AssetVocabulary vocabulary, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addVocabularyResources(
			vocabulary, addGroupPermissions, addGuestPermissions);
	}

	public static void addVocabularyResources(
			AssetVocabulary vocabulary,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addVocabularyResources(vocabulary, modelPermissions);
	}

	/**
	 * Creates a new asset vocabulary with the primary key. Does not add the asset vocabulary to the database.
	 *
	 * @param vocabularyId the primary key for the new asset vocabulary
	 * @return the new asset vocabulary
	 */
	public static AssetVocabulary createAssetVocabulary(long vocabularyId) {
		return getService().createAssetVocabulary(vocabularyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static AssetVocabulary deleteAssetVocabulary(
		AssetVocabulary assetVocabulary) {

		return getService().deleteAssetVocabulary(assetVocabulary);
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
	public static AssetVocabulary deleteAssetVocabulary(long vocabularyId)
		throws PortalException {

		return getService().deleteAssetVocabulary(vocabularyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteVocabularies(long groupId) throws PortalException {
		getService().deleteVocabularies(groupId);
	}

	public static AssetVocabulary deleteVocabulary(AssetVocabulary vocabulary)
		throws PortalException {

		return getService().deleteVocabulary(vocabulary);
	}

	public static void deleteVocabulary(long vocabularyId)
		throws PortalException {

		getService().deleteVocabulary(vocabularyId);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static AssetVocabulary fetchAssetVocabulary(long vocabularyId) {
		return getService().fetchAssetVocabulary(vocabularyId);
	}

	public static AssetVocabulary fetchAssetVocabularyByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchAssetVocabularyByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset vocabulary matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary, or <code>null</code> if a matching asset vocabulary could not be found
	 */
	public static AssetVocabulary fetchAssetVocabularyByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchAssetVocabularyByUuidAndGroupId(uuid, groupId);
	}

	public static AssetVocabulary fetchGroupVocabulary(
		long groupId, String name) {

		return getService().fetchGroupVocabulary(groupId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	public static List<AssetVocabulary> getAssetVocabularies(
		int start, int end) {

		return getService().getAssetVocabularies(start, end);
	}

	/**
	 * Returns all the asset vocabularies matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabularies
	 * @param companyId the primary key of the company
	 * @return the matching asset vocabularies, or an empty list if no matches were found
	 */
	public static List<AssetVocabulary> getAssetVocabulariesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getAssetVocabulariesByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<AssetVocabulary> getAssetVocabulariesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetVocabulary> orderByComparator) {

		return getService().getAssetVocabulariesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset vocabularies.
	 *
	 * @return the number of asset vocabularies
	 */
	public static int getAssetVocabulariesCount() {
		return getService().getAssetVocabulariesCount();
	}

	/**
	 * Returns the asset vocabulary with the primary key.
	 *
	 * @param vocabularyId the primary key of the asset vocabulary
	 * @return the asset vocabulary
	 * @throws PortalException if a asset vocabulary with the primary key could not be found
	 */
	public static AssetVocabulary getAssetVocabulary(long vocabularyId)
		throws PortalException {

		return getService().getAssetVocabulary(vocabularyId);
	}

	public static AssetVocabulary getAssetVocabularyByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getAssetVocabularyByExternalReferenceCode(
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
	public static AssetVocabulary getAssetVocabularyByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getAssetVocabularyByUuidAndGroupId(uuid, groupId);
	}

	public static List<AssetVocabulary> getCompanyVocabularies(long companyId) {
		return getService().getCompanyVocabularies(companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static List<AssetVocabulary> getGroupsVocabularies(long[] groupIds) {
		return getService().getGroupsVocabularies(groupIds);
	}

	public static List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className) {

		return getService().getGroupsVocabularies(groupIds, className);
	}

	public static List<AssetVocabulary> getGroupsVocabularies(
		long[] groupIds, String className, long classTypePK) {

		return getService().getGroupsVocabularies(
			groupIds, className, classTypePK);
	}

	public static List<AssetVocabulary> getGroupVocabularies(long groupId)
		throws PortalException {

		return getService().getGroupVocabularies(groupId);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
			long groupId, boolean addDefaultVocabulary)
		throws PortalException {

		return getService().getGroupVocabularies(groupId, addDefaultVocabulary);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
		long groupId, int visibilityType) {

		return getService().getGroupVocabularies(groupId, visibilityType);
	}

	public static List<AssetVocabulary> getGroupVocabularies(long[] groupIds) {
		return getService().getGroupVocabularies(groupIds);
	}

	public static List<AssetVocabulary> getGroupVocabularies(
		long[] groupIds, int[] visibilityTypes) {

		return getService().getGroupVocabularies(groupIds, visibilityTypes);
	}

	public static int getGroupVocabulariesCount(long[] groupIds) {
		return getService().getGroupVocabulariesCount(groupIds);
	}

	public static AssetVocabulary getGroupVocabulary(long groupId, String name)
		throws PortalException {

		return getService().getGroupVocabulary(groupId, name);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static AssetVocabulary getOrAddEmptyVocabulary(
			String externalReferenceCode, long userId, long groupId)
		throws PortalException {

		return getService().getOrAddEmptyVocabulary(
			externalReferenceCode, userId, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<AssetVocabulary> getVocabularies(
			com.liferay.portal.kernel.search.Hits hits)
		throws PortalException {

		return getService().getVocabularies(hits);
	}

	public static List<AssetVocabulary> getVocabularies(long[] vocabularyIds)
		throws PortalException {

		return getService().getVocabularies(vocabularyIds);
	}

	public static AssetVocabulary getVocabulary(long vocabularyId)
		throws PortalException {

		return getService().getVocabulary(vocabularyId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AssetVocabulary> searchVocabularies(
				long companyId, long[] groupIds, String title,
				int[] visibilityTypes, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchVocabularies(
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
	public static AssetVocabulary updateAssetVocabulary(
		AssetVocabulary assetVocabulary) {

		return getService().updateAssetVocabulary(assetVocabulary);
	}

	public static AssetVocabulary updateVocabulary(
			long vocabularyId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings)
		throws PortalException {

		return getService().updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings);
	}

	public static AssetVocabulary updateVocabulary(
			long vocabularyId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			int visibilityType)
		throws PortalException {

		return getService().updateVocabulary(
			vocabularyId, titleMap, descriptionMap, settings, visibilityType);
	}

	public static AssetVocabulary updateVocabulary(
			long vocabularyId, String title,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, String settings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateVocabulary(
			vocabularyId, title, titleMap, descriptionMap, settings,
			serviceContext);
	}

	public static AssetVocabularyLocalService getService() {
		return _service;
	}

	public static void setService(AssetVocabularyLocalService service) {
		_service = service;
	}

	private static volatile AssetVocabularyLocalService _service;

}