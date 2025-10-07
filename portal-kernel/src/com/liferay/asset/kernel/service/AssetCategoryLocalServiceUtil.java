/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for AssetCategory. This utility wraps
 * <code>com.liferay.portlet.asset.service.impl.AssetCategoryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryLocalService
 * @generated
 */
public class AssetCategoryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetCategoryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset category to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategory the asset category
	 * @return the asset category that was added
	 */
	public static AssetCategory addAssetCategory(AssetCategory assetCategory) {
		return getService().addAssetCategory(assetCategory);
	}

	public static AssetCategory addCategory(
			long userId, long groupId, String title, long vocabularyId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCategory(
			userId, groupId, title, vocabularyId, serviceContext);
	}

	public static AssetCategory addCategory(
			String externalReferenceCode, long userId, long groupId,
			long parentCategoryId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCategory(
			externalReferenceCode, userId, groupId, parentCategoryId, titleMap,
			descriptionMap, vocabularyId, categoryProperties, serviceContext);
	}

	public static void addCategoryResources(
			AssetCategory category, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addCategoryResources(
			category, addGroupPermissions, addGuestPermissions);
	}

	public static void addCategoryResources(
			AssetCategory category,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addCategoryResources(category, modelPermissions);
	}

	/**
	 * Creates a new asset category with the primary key. Does not add the asset category to the database.
	 *
	 * @param categoryId the primary key for the new asset category
	 * @return the new asset category
	 */
	public static AssetCategory createAssetCategory(long categoryId) {
		return getService().createAssetCategory(categoryId);
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
	 * Deletes the asset category from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategory the asset category
	 * @return the asset category that was removed
	 */
	public static AssetCategory deleteAssetCategory(
		AssetCategory assetCategory) {

		return getService().deleteAssetCategory(assetCategory);
	}

	/**
	 * Deletes the asset category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category that was removed
	 * @throws PortalException if a asset category with the primary key could not be found
	 */
	public static AssetCategory deleteAssetCategory(long categoryId)
		throws PortalException {

		return getService().deleteAssetCategory(categoryId);
	}

	public static void deleteCategories(List<AssetCategory> categories)
		throws PortalException {

		getService().deleteCategories(categories);
	}

	public static void deleteCategories(long[] categoryIds)
		throws PortalException {

		getService().deleteCategories(categoryIds);
	}

	public static AssetCategory deleteCategory(AssetCategory category)
		throws PortalException {

		return getService().deleteCategory(category);
	}

	public static AssetCategory deleteCategory(
			AssetCategory category, boolean skipRebuildTree)
		throws PortalException {

		return getService().deleteCategory(category, skipRebuildTree);
	}

	public static AssetCategory deleteCategory(long categoryId)
		throws PortalException {

		return getService().deleteCategory(categoryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteVocabularyCategories(long vocabularyId)
		throws PortalException {

		getService().deleteVocabularyCategories(vocabularyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
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

	public static AssetCategory fetchAssetCategory(long categoryId) {
		return getService().fetchAssetCategory(categoryId);
	}

	public static AssetCategory fetchAssetCategoryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchAssetCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset category matching the UUID and group.
	 *
	 * @param uuid the asset category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchAssetCategoryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchAssetCategoryByUuidAndGroupId(uuid, groupId);
	}

	public static AssetCategory fetchCategory(long categoryId) {
		return getService().fetchCategory(categoryId);
	}

	public static AssetCategory fetchCategory(
		long groupId, long parentCategoryId, String name, long vocabularyId) {

		return getService().fetchCategory(
			groupId, parentCategoryId, name, vocabularyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the asset categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of asset categories
	 */
	public static List<AssetCategory> getAssetCategories(int start, int end) {
		return getService().getAssetCategories(start, end);
	}

	/**
	 * Returns all the asset categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset categories
	 * @param companyId the primary key of the company
	 * @return the matching asset categories, or an empty list if no matches were found
	 */
	public static List<AssetCategory> getAssetCategoriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getAssetCategoriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of asset categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset categories
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset categories, or an empty list if no matches were found
	 */
	public static List<AssetCategory> getAssetCategoriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getService().getAssetCategoriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset categories.
	 *
	 * @return the number of asset categories
	 */
	public static int getAssetCategoriesCount() {
		return getService().getAssetCategoriesCount();
	}

	/**
	 * Returns the asset category with the primary key.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category
	 * @throws PortalException if a asset category with the primary key could not be found
	 */
	public static AssetCategory getAssetCategory(long categoryId)
		throws PortalException {

		return getService().getAssetCategory(categoryId);
	}

	public static AssetCategory getAssetCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getAssetCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset category matching the UUID and group.
	 *
	 * @param uuid the asset category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset category
	 * @throws PortalException if a matching asset category could not be found
	 */
	public static AssetCategory getAssetCategoryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getAssetCategoryByUuidAndGroupId(uuid, groupId);
	}

	public static List<AssetCategory> getCategories() {
		return getService().getCategories();
	}

	public static List<AssetCategory> getCategories(
			com.liferay.portal.kernel.search.Hits hits)
		throws PortalException {

		return getService().getCategories(hits);
	}

	public static List<AssetCategory> getCategories(
		long classNameId, long classPK) {

		return getService().getCategories(classNameId, classPK);
	}

	public static List<AssetCategory> getCategories(
		long classNameId, long classPK, int start, int end) {

		return getService().getCategories(classNameId, classPK, start, end);
	}

	public static List<AssetCategory> getCategories(
		String className, long classPK) {

		return getService().getCategories(className, classPK);
	}

	public static int getCategoriesCount(long classNameId, long classPK) {
		return getService().getCategoriesCount(classNameId, classPK);
	}

	public static AssetCategory getCategory(long categoryId)
		throws PortalException {

		return getService().getCategory(categoryId);
	}

	public static AssetCategory getCategory(String uuid, long groupId)
		throws PortalException {

		return getService().getCategory(uuid, groupId);
	}

	public static long[] getCategoryIds(String className, long classPK) {
		return getService().getCategoryIds(className, classPK);
	}

	public static String[] getCategoryNames() {
		return getService().getCategoryNames();
	}

	public static String[] getCategoryNames(long classNameId, long classPK) {
		return getService().getCategoryNames(classNameId, classPK);
	}

	public static String[] getCategoryNames(String className, long classPK) {
		return getService().getCategoryNames(className, classPK);
	}

	public static List<AssetCategory> getChildCategories(
		long parentCategoryId) {

		return getService().getChildCategories(parentCategoryId);
	}

	public static List<AssetCategory> getChildCategories(
		long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getService().getChildCategories(
			parentCategoryId, start, end, orderByComparator);
	}

	public static int getChildCategoriesCount(long parentCategoryId) {
		return getService().getChildCategoriesCount(parentCategoryId);
	}

	public static List<AssetCategory> getDescendantCategories(
		AssetCategory category) {

		return getService().getDescendantCategories(category);
	}

	public static List<AssetCategory> getEntryCategories(long entryId) {
		return getService().getEntryCategories(entryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static AssetCategory getOrAddEmptyCategory(
			String externalReferenceCode, long userId, long groupId)
		throws PortalException {

		return getService().getOrAddEmptyCategory(
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

	public static List<Long> getSubcategoryIds(long parentCategoryId) {
		return getService().getSubcategoryIds(parentCategoryId);
	}

	public static long[] getViewableCategoryIds(
			String className, long classPK, long[] categoryIds)
		throws PortalException {

		return getService().getViewableCategoryIds(
			className, classPK, categoryIds);
	}

	public static List<AssetCategory> getVocabularyCategories(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getService().getVocabularyCategories(
			vocabularyId, start, end, orderByComparator);
	}

	public static List<AssetCategory> getVocabularyCategories(
		long parentCategoryId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getService().getVocabularyCategories(
			parentCategoryId, vocabularyId, start, end, orderByComparator);
	}

	public static int getVocabularyCategoriesCount(long vocabularyId) {
		return getService().getVocabularyCategoriesCount(vocabularyId);
	}

	public static List<AssetCategory> getVocabularyRootCategories(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getService().getVocabularyRootCategories(
			vocabularyId, start, end, orderByComparator);
	}

	public static int getVocabularyRootCategoriesCount(long vocabularyId) {
		return getService().getVocabularyRootCategoriesCount(vocabularyId);
	}

	public static AssetCategory mergeCategories(
			long fromCategoryId, long toCategoryId)
		throws PortalException {

		return getService().mergeCategories(fromCategoryId, toCategoryId);
	}

	public static AssetCategory moveCategory(
			long categoryId, long parentCategoryId, long vocabularyId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveCategory(
			categoryId, parentCategoryId, vocabularyId, serviceContext);
	}

	public static List<AssetCategory> search(
		long groupId, String name, String[] categoryProperties, int start,
		int end) {

		return getService().search(
			groupId, name, categoryProperties, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AssetCategory> searchCategories(
				long companyId, long groupIds, String title, long vocabularyId,
				int start, int end)
			throws PortalException {

		return getService().searchCategories(
			companyId, groupIds, title, vocabularyId, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AssetCategory> searchCategories(
				long companyId, long[] groupIds, String title,
				long[] vocabularyIds, int start, int end)
			throws PortalException {

		return getService().searchCategories(
			companyId, groupIds, title, vocabularyIds, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AssetCategory> searchCategories(
				long companyId, long[] groupIds, String title,
				long[] parentCategoryIds, long[] vocabularyIds, int start,
				int end)
			throws PortalException {

		return getService().searchCategories(
			companyId, groupIds, title, parentCategoryIds, vocabularyIds, start,
			end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AssetCategory> searchCategories(
				long companyId, long[] groupIds, String title,
				long[] vocabularyIds, long[] parentCategoryIds, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCategories(
			companyId, groupIds, title, vocabularyIds, parentCategoryIds, start,
			end, sort);
	}

	/**
	 * Updates the asset category in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetCategory the asset category
	 * @return the asset category that was updated
	 */
	public static AssetCategory updateAssetCategory(
		AssetCategory assetCategory) {

		return getService().updateAssetCategory(assetCategory);
	}

	public static AssetCategory updateCategory(
			long userId, long categoryId, long parentCategoryId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCategory(
			userId, categoryId, parentCategoryId, titleMap, descriptionMap,
			vocabularyId, categoryProperties, serviceContext);
	}

	public static AssetCategoryLocalService getService() {
		return _service;
	}

	public static void setService(AssetCategoryLocalService service) {
		_service = service;
	}

	private static volatile AssetCategoryLocalService _service;

}