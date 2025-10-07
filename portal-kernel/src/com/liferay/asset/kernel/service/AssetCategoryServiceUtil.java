/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for AssetCategory. This utility wraps
 * <code>com.liferay.portlet.asset.service.impl.AssetCategoryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryService
 * @generated
 */
public class AssetCategoryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetCategoryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static AssetCategory addCategory(
			long groupId, long parentCategoryId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCategory(
			groupId, parentCategoryId, titleMap, descriptionMap, vocabularyId,
			categoryProperties, serviceContext);
	}

	public static AssetCategory addCategory(
			long groupId, String title, long vocabularyId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCategory(
			groupId, title, vocabularyId, serviceContext);
	}

	public static AssetCategory addCategory(
			String externalReferenceCode, long groupId, long parentCategoryId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCategory(
			externalReferenceCode, groupId, parentCategoryId, titleMap,
			descriptionMap, vocabularyId, categoryProperties, serviceContext);
	}

	public static void deleteCategories(long[] categoryIds)
		throws PortalException {

		getService().deleteCategories(categoryIds);
	}

	public static void deleteCategory(long categoryId) throws PortalException {
		getService().deleteCategory(categoryId);
	}

	public static AssetCategory deleteCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static AssetCategory fetchCategory(long categoryId)
		throws PortalException {

		return getService().fetchCategory(categoryId);
	}

	public static AssetCategory fetchCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static AssetCategory getAssetCategoryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().getAssetCategoryByExternalReferenceCode(
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
	public static List<AssetCategory> getCategories(
		long classNameId, long classPK, int start, int end) {

		return getService().getCategories(classNameId, classPK, start, end);
	}

	public static List<AssetCategory> getCategories(
			String className, long classPK)
		throws PortalException {

		return getService().getCategories(className, classPK);
	}

	/**
	 * Returns the number of assetCategories related to an AssetEntry with the
	 * given "classNameId-classPK".
	 *
	 * @param classNameId the className of the asset
	 * @param classPK the classPK of the asset
	 * @return the number of matching assetCategories
	 */
	public static int getCategoriesCount(long classNameId, long classPK) {
		return getService().getCategoriesCount(classNameId, classPK);
	}

	public static AssetCategory getCategory(long categoryId)
		throws PortalException {

		return getService().getCategory(categoryId);
	}

	public static String getCategoryPath(long categoryId)
		throws PortalException {

		return getService().getCategoryPath(categoryId);
	}

	public static List<AssetCategory> getChildCategories(long parentCategoryId)
		throws PortalException {

		return getService().getChildCategories(parentCategoryId);
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
	public static List<AssetCategory> getChildCategories(
			long parentCategoryId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		return getService().getChildCategories(
			parentCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of child categories
	 *
	 * @param parentCategoryId the parent category ID
	 * @return the number of child categories
	 * @throws PortalException
	 */
	public static int getChildCategoriesCount(long parentCategoryId)
		throws PortalException {

		return getService().getChildCategoriesCount(parentCategoryId);
	}

	public static AssetCategory getOrAddEmptyCategory(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getOrAddEmptyCategory(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<AssetCategory> getVocabularyCategories(
			long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		return getService().getVocabularyCategories(
			vocabularyId, start, end, orderByComparator);
	}

	public static List<AssetCategory> getVocabularyCategories(
			long parentCategoryId, long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		return getService().getVocabularyCategories(
			parentCategoryId, vocabularyId, start, end, orderByComparator);
	}

	public static List<AssetCategory> getVocabularyCategories(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator) {

		return getService().getVocabularyCategories(
			groupId, parentCategoryId, vocabularyId, start, end,
			orderByComparator);
	}

	public static List<AssetCategory> getVocabularyCategories(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getService().getVocabularyCategories(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	public static int getVocabularyCategoriesCount(
		long groupId, long vocabularyId) {

		return getService().getVocabularyCategoriesCount(groupId, vocabularyId);
	}

	public static int getVocabularyCategoriesCount(
		long groupId, long parentCategory, long vocabularyId) {

		return getService().getVocabularyCategoriesCount(
			groupId, parentCategory, vocabularyId);
	}

	public static int getVocabularyCategoriesCount(
		long groupId, String name, long vocabularyId) {

		return getService().getVocabularyCategoriesCount(
			groupId, name, vocabularyId);
	}

	public static com.liferay.asset.kernel.model.AssetCategoryDisplay
			getVocabularyCategoriesDisplay(
				long vocabularyId, int start, int end,
				OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		return getService().getVocabularyCategoriesDisplay(
			vocabularyId, start, end, orderByComparator);
	}

	public static com.liferay.asset.kernel.model.AssetCategoryDisplay
			getVocabularyCategoriesDisplay(
				long groupId, String name, long vocabularyId, int start,
				int end, OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		return getService().getVocabularyCategoriesDisplay(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	public static List<AssetCategory> getVocabularyRootCategories(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getService().getVocabularyRootCategories(
			groupId, vocabularyId, start, end, orderByComparator);
	}

	public static int getVocabularyRootCategoriesCount(
		long groupId, long vocabularyId) {

		return getService().getVocabularyRootCategoriesCount(
			groupId, vocabularyId);
	}

	public static AssetCategory moveCategory(
			long categoryId, long parentCategoryId, long vocabularyId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveCategory(
			categoryId, parentCategoryId, vocabularyId, serviceContext);
	}

	public static List<AssetCategory> search(
		long groupId, String keywords, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getService().search(
			groupId, keywords, vocabularyId, start, end, orderByComparator);
	}

	public static com.liferay.portal.kernel.json.JSONArray search(
			long groupId, String name, String[] categoryProperties, int start,
			int end)
		throws PortalException {

		return getService().search(
			groupId, name, categoryProperties, start, end);
	}

	public static com.liferay.portal.kernel.json.JSONArray search(
			long[] groupIds, String name, long[] vocabularyIds, int start,
			int end)
		throws PortalException {

		return getService().search(groupIds, name, vocabularyIds, start, end);
	}

	public static com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long groupId, String title, long vocabularyId, int start,
				int end)
		throws PortalException {

		return getService().searchCategoriesDisplay(
			groupId, title, vocabularyId, start, end);
	}

	public static com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long groupId, String title, long parentCategoryId,
				long vocabularyId, int start, int end)
		throws PortalException {

		return getService().searchCategoriesDisplay(
			groupId, title, parentCategoryId, vocabularyId, start, end);
	}

	public static com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long groupId, String title, long vocabularyId,
				long parentCategoryId, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().searchCategoriesDisplay(
			groupId, title, vocabularyId, parentCategoryId, start, end, sort);
	}

	public static com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long[] groupIds, String title, long[] vocabularyIds, int start,
				int end)
		throws PortalException {

		return getService().searchCategoriesDisplay(
			groupIds, title, vocabularyIds, start, end);
	}

	public static com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long[] groupIds, String title, long[] parentCategoryIds,
				long[] vocabularyIds, int start, int end)
		throws PortalException {

		return getService().searchCategoriesDisplay(
			groupIds, title, parentCategoryIds, vocabularyIds, start, end);
	}

	public static com.liferay.asset.kernel.model.AssetCategoryDisplay
			searchCategoriesDisplay(
				long[] groupIds, String title, long[] vocabularyIds,
				long[] parentCategoryIds, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().searchCategoriesDisplay(
			groupIds, title, vocabularyIds, parentCategoryIds, start, end,
			sort);
	}

	public static AssetCategory updateCategory(
			long categoryId, long parentCategoryId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCategory(
			categoryId, parentCategoryId, titleMap, descriptionMap,
			vocabularyId, categoryProperties, serviceContext);
	}

	public static AssetCategoryService getService() {
		return _service;
	}

	public static void setService(AssetCategoryService service) {
		_service = service;
	}

	private static volatile AssetCategoryService _service;

}