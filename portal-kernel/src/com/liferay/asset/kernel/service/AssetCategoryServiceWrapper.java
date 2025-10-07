/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AssetCategoryService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryService
 * @generated
 */
public class AssetCategoryServiceWrapper
	implements AssetCategoryService, ServiceWrapper<AssetCategoryService> {

	public AssetCategoryServiceWrapper() {
		this(null);
	}

	public AssetCategoryServiceWrapper(
		AssetCategoryService assetCategoryService) {

		_assetCategoryService = assetCategoryService;
	}

	@Override
	public AssetCategory addCategory(
			long groupId, long parentCategoryId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.addCategory(
			groupId, parentCategoryId, titleMap, descriptionMap, vocabularyId,
			categoryProperties, serviceContext);
	}

	@Override
	public AssetCategory addCategory(
			long groupId, String title, long vocabularyId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.addCategory(
			groupId, title, vocabularyId, serviceContext);
	}

	@Override
	public AssetCategory addCategory(
			String externalReferenceCode, long groupId, long parentCategoryId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.addCategory(
			externalReferenceCode, groupId, parentCategoryId, titleMap,
			descriptionMap, vocabularyId, categoryProperties, serviceContext);
	}

	@Override
	public void deleteCategories(long[] categoryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetCategoryService.deleteCategories(categoryIds);
	}

	@Override
	public void deleteCategory(long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetCategoryService.deleteCategory(categoryId);
	}

	@Override
	public AssetCategory deleteCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.deleteCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public AssetCategory fetchCategory(long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.fetchCategory(categoryId);
	}

	@Override
	public AssetCategory fetchCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.fetchCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public AssetCategory getAssetCategoryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getAssetCategoryByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	/**
	 * Returns a range of assetCategories related to an AssetEntry with the
	 * given "classNameId-classPK".
	 *
	 * @param classNameId the className of the asset
	 * @param classPK the classPK of the asset
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the matching assetCategories
	 */
	@Override
	public java.util.List<AssetCategory> getCategories(
		long classNameId, long classPK, int start, int end) {

		return _assetCategoryService.getCategories(
			classNameId, classPK, start, end);
	}

	@Override
	public java.util.List<AssetCategory> getCategories(
			String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getCategories(className, classPK);
	}

	/**
	 * Returns the number of assetCategories related to an AssetEntry with the
	 * given "classNameId-classPK".
	 *
	 * @param classNameId the className of the asset
	 * @param classPK the classPK of the asset
	 * @return the number of matching assetCategories
	 */
	@Override
	public int getCategoriesCount(long classNameId, long classPK) {
		return _assetCategoryService.getCategoriesCount(classNameId, classPK);
	}

	@Override
	public AssetCategory getCategory(long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getCategory(categoryId);
	}

	@Override
	public String getCategoryPath(long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getCategoryPath(categoryId);
	}

	@Override
	public java.util.List<AssetCategory> getChildCategories(
			long parentCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getChildCategories(parentCategoryId);
	}

	/**
	 * eturns a range of child assetCategories.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator
	 * @return the matching categories
	 * @throws PortalException
	 */
	@Override
	public java.util.List<AssetCategory> getChildCategories(
			long parentCategoryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getChildCategories(
			parentCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of child categories
	 *
	 * @param parentCategoryId the parent category ID
	 * @return the number of child categories
	 * @throws PortalException
	 */
	@Override
	public int getChildCategoriesCount(long parentCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getChildCategoriesCount(parentCategoryId);
	}

	@Override
	public AssetCategory getOrAddEmptyCategory(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getOrAddEmptyCategory(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetCategoryService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<AssetCategory> getVocabularyCategories(
			long vocabularyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getVocabularyCategories(
			vocabularyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetCategory> getVocabularyCategories(
			long parentCategoryId, long vocabularyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getVocabularyCategories(
			parentCategoryId, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetCategory> getVocabularyCategories(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
			orderByComparator) {

		return _assetCategoryService.getVocabularyCategories(
			groupId, parentCategoryId, vocabularyId, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<AssetCategory> getVocabularyCategories(
		long groupId, String name, long vocabularyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
			orderByComparator) {

		return _assetCategoryService.getVocabularyCategories(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public int getVocabularyCategoriesCount(long groupId, long vocabularyId) {
		return _assetCategoryService.getVocabularyCategoriesCount(
			groupId, vocabularyId);
	}

	@Override
	public int getVocabularyCategoriesCount(
		long groupId, long parentCategory, long vocabularyId) {

		return _assetCategoryService.getVocabularyCategoriesCount(
			groupId, parentCategory, vocabularyId);
	}

	@Override
	public int getVocabularyCategoriesCount(
		long groupId, String name, long vocabularyId) {

		return _assetCategoryService.getVocabularyCategoriesCount(
			groupId, name, vocabularyId);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetCategoryDisplay
			getVocabularyCategoriesDisplay(
				long vocabularyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getVocabularyCategoriesDisplay(
			vocabularyId, start, end, orderByComparator);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetCategoryDisplay
			getVocabularyCategoriesDisplay(
				long groupId, String name, long vocabularyId, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.getVocabularyCategoriesDisplay(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetCategory> getVocabularyRootCategories(
		long groupId, long vocabularyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
			orderByComparator) {

		return _assetCategoryService.getVocabularyRootCategories(
			groupId, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public int getVocabularyRootCategoriesCount(
		long groupId, long vocabularyId) {

		return _assetCategoryService.getVocabularyRootCategoriesCount(
			groupId, vocabularyId);
	}

	@Override
	public AssetCategory moveCategory(
			long categoryId, long parentCategoryId, long vocabularyId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.moveCategory(
			categoryId, parentCategoryId, vocabularyId, serviceContext);
	}

	@Override
	public java.util.List<AssetCategory> search(
		long groupId, String keywords, long vocabularyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetCategory>
			orderByComparator) {

		return _assetCategoryService.search(
			groupId, keywords, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public com.liferay.portal.kernel.json.JSONArray search(
			long groupId, String name, String[] categoryProperties, int start,
			int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.search(
			groupId, name, categoryProperties, start, end);
	}

	@Override
	public com.liferay.portal.kernel.json.JSONArray search(
			long[] groupIds, String name, long[] vocabularyIds, int start,
			int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.search(
			groupIds, name, vocabularyIds, start, end);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long groupId, String title, long vocabularyId, int start,
				int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.searchCategoriesDisplay(
			groupId, title, vocabularyId, start, end);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long groupId, String title, long parentCategoryId,
				long vocabularyId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.searchCategoriesDisplay(
			groupId, title, parentCategoryId, vocabularyId, start, end);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long groupId, String title, long vocabularyId,
				long parentCategoryId, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.searchCategoriesDisplay(
			groupId, title, vocabularyId, parentCategoryId, start, end, sort);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long[] groupIds, String title, long[] vocabularyIds, int start,
				int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.searchCategoriesDisplay(
			groupIds, title, vocabularyIds, start, end);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long[] groupIds, String title, long[] parentCategoryIds,
				long[] vocabularyIds, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.searchCategoriesDisplay(
			groupIds, title, parentCategoryIds, vocabularyIds, start, end);
	}

	@Override
	public com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long[] groupIds, String title, long[] vocabularyIds,
				long[] parentCategoryIds, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.searchCategoriesDisplay(
			groupIds, title, vocabularyIds, parentCategoryIds, start, end,
			sort);
	}

	@Override
	public AssetCategory updateCategory(
			long categoryId, long parentCategoryId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetCategoryService.updateCategory(
			categoryId, parentCategoryId, titleMap, descriptionMap,
			vocabularyId, categoryProperties, serviceContext);
	}

	@Override
	public AssetCategoryService getWrappedService() {
		return _assetCategoryService;
	}

	@Override
	public void setWrappedService(AssetCategoryService assetCategoryService) {
		_assetCategoryService = assetCategoryService;
	}

	private AssetCategoryService _assetCategoryService;

}