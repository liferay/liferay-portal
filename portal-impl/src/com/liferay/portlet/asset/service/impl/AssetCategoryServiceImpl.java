/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetCategoryDisplay;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Autocomplete;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.service.base.AssetCategoryServiceBaseImpl;
import com.liferay.portlet.asset.service.permission.AssetCategoriesPermission;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Provides the remote service for accessing, adding, deleting, moving, and
 * updating asset categories. Its methods include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Alvaro del Castillo
 * @author Eduardo Lundgren
 * @author Bruno Farache
 */
public class AssetCategoryServiceImpl extends AssetCategoryServiceBaseImpl {

	@Override
	public AssetCategory addCategory(
			long groupId, long parentCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties, ServiceContext serviceContext)
		throws PortalException {

		AssetCategoryPermission.check(
			getPermissionChecker(), groupId, parentCategoryId,
			ActionKeys.ADD_CATEGORY);

		return assetCategoryLocalService.addCategory(
			null, getUserId(), groupId, parentCategoryId, titleMap,
			descriptionMap, vocabularyId, categoryProperties, serviceContext);
	}

	@Override
	public AssetCategory addCategory(
			long groupId, String title, long vocabularyId,
			ServiceContext serviceContext)
		throws PortalException {

		AssetCategoryPermission.check(
			getPermissionChecker(), groupId,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			ActionKeys.ADD_CATEGORY);

		return assetCategoryLocalService.addCategory(
			getUserId(), groupId, title, vocabularyId, serviceContext);
	}

	@Override
	public AssetCategory addCategory(
			String externalReferenceCode, long groupId, long parentCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			ServiceContext serviceContext)
		throws PortalException {

		AssetCategoryPermission.check(
			getPermissionChecker(), groupId, parentCategoryId,
			ActionKeys.ADD_CATEGORY);

		return assetCategoryLocalService.addCategory(
			externalReferenceCode, getUserId(), groupId, parentCategoryId,
			titleMap, descriptionMap, vocabularyId, categoryProperties,
			serviceContext);
	}

	@Override
	public void deleteCategories(long[] categoryIds) throws PortalException {
		for (long categoryId : categoryIds) {
			AssetCategoryPermission.check(
				getPermissionChecker(), categoryId, ActionKeys.DELETE);
		}

		assetCategoryLocalService.deleteCategories(categoryIds);
	}

	@Override
	public void deleteCategory(long categoryId) throws PortalException {
		AssetCategoryPermission.check(
			getPermissionChecker(), categoryId, ActionKeys.DELETE);

		assetCategoryLocalService.deleteCategory(categoryId);
	}

	@Override
	public AssetCategory deleteCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetCategory assetCategory =
			assetCategoryLocalService.getAssetCategoryByExternalReferenceCode(
				externalReferenceCode, groupId);

		AssetCategoryPermission.check(
			getPermissionChecker(), assetCategory.getCategoryId(),
			ActionKeys.DELETE);

		return assetCategoryLocalService.deleteCategory(assetCategory);
	}

	@Override
	public AssetCategory fetchCategory(long categoryId) throws PortalException {
		AssetCategory category = assetCategoryLocalService.fetchCategory(
			categoryId);

		if (category != null) {
			AssetCategoryPermission.check(
				getPermissionChecker(), category, ActionKeys.VIEW);
		}

		return category;
	}

	@Override
	public AssetCategory fetchCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetCategory category =
			assetCategoryLocalService.fetchAssetCategoryByExternalReferenceCode(
				externalReferenceCode, groupId);

		if (category != null) {
			AssetCategoryPermission.check(
				getPermissionChecker(), category, ActionKeys.VIEW);
		}

		return category;
	}

	@Override
	public AssetCategory getAssetCategoryByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		AssetCategory category =
			assetCategoryLocalService.getAssetCategoryByExternalReferenceCode(
				externalReferenceCode, groupId);

		AssetCategoryPermission.check(
			getPermissionChecker(), category.getCategoryId(), ActionKeys.VIEW);

		return category;
	}

	/**
	 * Returns a range of assetCategories related to an AssetEntry with the
	 * given "classNameId-classPK".
	 *
	 * @param  classNameId the className of the asset
	 * @param  classPK the classPK of the asset
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the matching assetCategories
	 */
	@Override
	public List<AssetCategory> getCategories(
		long classNameId, long classPK, int start, int end) {

		return assetCategoryLocalService.getCategories(
			classNameId, classPK, start, end);
	}

	@Override
	public List<AssetCategory> getCategories(String className, long classPK)
		throws PortalException {

		return filterCategories(
			assetCategoryLocalService.getCategories(className, classPK));
	}

	/**
	 * Returns the number of assetCategories related to an AssetEntry with the
	 * given "classNameId-classPK".
	 *
	 * @param  classNameId the className of the asset
	 * @param  classPK the classPK of the asset
	 * @return the number of matching assetCategories
	 */
	@Override
	public int getCategoriesCount(long classNameId, long classPK) {
		return assetCategoryLocalService.getCategoriesCount(
			classNameId, classPK);
	}

	@Override
	public AssetCategory getCategory(long categoryId) throws PortalException {
		AssetCategoryPermission.check(
			getPermissionChecker(), categoryId, ActionKeys.VIEW);

		return assetCategoryLocalService.getCategory(categoryId);
	}

	@Override
	public String getCategoryPath(long categoryId) throws PortalException {
		AssetCategoryPermission.check(
			getPermissionChecker(), categoryId, ActionKeys.VIEW);

		AssetCategory category = getCategory(categoryId);

		return category.getPath(LocaleUtil.getMostRelevantLocale());
	}

	@Override
	public List<AssetCategory> getChildCategories(long parentCategoryId)
		throws PortalException {

		return filterCategories(
			assetCategoryLocalService.getChildCategories(parentCategoryId));
	}

	/**
	 * eturns a range of child assetCategories.
	 *
	 * @param  parentCategoryId the parent category ID
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @param  orderByComparator the comparator
	 * @return the matching categories
	 * @throws PortalException
	 */
	@Override
	public List<AssetCategory> getChildCategories(
			long parentCategoryId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		if (parentCategoryId != 0) {
			AssetCategory parent = assetCategoryLocalService.fetchAssetCategory(
				parentCategoryId);

			if (parent != null) {
				return assetCategoryPersistence.filterFindByG_P(
					parent.getGroupId(), parentCategoryId, start, end,
					orderByComparator);
			}
		}

		return filterCategories(
			assetCategoryLocalService.getChildCategories(
				parentCategoryId, start, end, orderByComparator));
	}

	/**
	 * Returns the number of child categories
	 *
	 * @param  parentCategoryId the parent category ID
	 * @return the number of child categories
	 * @throws PortalException
	 */
	@Override
	public int getChildCategoriesCount(long parentCategoryId)
		throws PortalException {

		if (parentCategoryId != 0) {
			AssetCategory parent = assetCategoryLocalService.fetchAssetCategory(
				parentCategoryId);

			if (parent != null) {
				return assetCategoryPersistence.filterCountByG_P(
					parent.getGroupId(), parentCategoryId);
			}
		}

		return assetCategoryPersistence.countByParentCategoryId(
			parentCategoryId);
	}

	public AssetCategory getOrAddEmptyCategory(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetCategory category =
			assetCategoryService.fetchCategoryByExternalReferenceCode(
				externalReferenceCode, groupId);

		if (category != null) {
			return category;
		}

		AssetCategoriesPermission.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_CATEGORY);

		return assetCategoryLocalService.getOrAddEmptyCategory(
			externalReferenceCode, getUserId(), groupId);
	}

	@Override
	public List<AssetCategory> getVocabularyCategories(
			long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		return filterCategories(
			assetCategoryLocalService.getVocabularyCategories(
				vocabularyId, start, end, orderByComparator));
	}

	@Override
	public List<AssetCategory> getVocabularyCategories(
			long parentCategoryId, long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		return filterCategories(
			assetCategoryLocalService.getVocabularyCategories(
				parentCategoryId, vocabularyId, start, end, orderByComparator));
	}

	@Override
	public List<AssetCategory> getVocabularyCategories(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator) {

		return assetCategoryPersistence.filterFindByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end,
			orderByComparator);
	}

	@Override
	public List<AssetCategory> getVocabularyCategories(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		if (Validator.isNull(name)) {
			return assetCategoryPersistence.filterFindByG_V(
				groupId, vocabularyId, start, end, orderByComparator);
		}

		return assetCategoryPersistence.filterFindByG_LikeN_V(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public int getVocabularyCategoriesCount(long groupId, long vocabularyId) {
		return assetCategoryPersistence.filterCountByG_V(groupId, vocabularyId);
	}

	@Override
	public int getVocabularyCategoriesCount(
		long groupId, long parentCategory, long vocabularyId) {

		return assetCategoryPersistence.filterCountByG_P_V(
			groupId, parentCategory, vocabularyId);
	}

	@Override
	public int getVocabularyCategoriesCount(
		long groupId, String name, long vocabularyId) {

		if (Validator.isNull(name)) {
			return assetCategoryPersistence.filterCountByG_V(
				groupId, vocabularyId);
		}

		return assetCategoryPersistence.filterCountByG_LikeN_V(
			groupId, name, vocabularyId);
	}

	@Override
	public AssetCategoryDisplay getVocabularyCategoriesDisplay(
			long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		List<AssetCategory> categories = filterCategories(
			assetCategoryLocalService.getVocabularyCategories(
				vocabularyId, start, end, orderByComparator));

		return new AssetCategoryDisplay(
			categories, categories.size(), start, end);
	}

	@Override
	public AssetCategoryDisplay getVocabularyCategoriesDisplay(
			long groupId, String name, long vocabularyId, int start, int end,
			OrderByComparator<AssetCategory> orderByComparator)
		throws PortalException {

		List<AssetCategory> categories = null;
		int total = 0;

		if (Validator.isNotNull(name)) {
			name = CustomSQLUtil.keywords(name)[0];

			categories = getVocabularyCategories(
				groupId, name, vocabularyId, start, end, orderByComparator);
			total = getVocabularyCategoriesCount(groupId, name, vocabularyId);
		}
		else {
			categories = getVocabularyCategories(
				vocabularyId, start, end, orderByComparator);
			total = getVocabularyCategoriesCount(groupId, vocabularyId);
		}

		return new AssetCategoryDisplay(categories, total, start, end);
	}

	@Override
	public List<AssetCategory> getVocabularyRootCategories(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return assetCategoryPersistence.filterFindByG_P_V(
			groupId, AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			vocabularyId, start, end, orderByComparator);
	}

	@Override
	public int getVocabularyRootCategoriesCount(
		long groupId, long vocabularyId) {

		return assetCategoryPersistence.filterCountByG_P_V(
			groupId, AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			vocabularyId);
	}

	@Override
	public AssetCategory moveCategory(
			long categoryId, long parentCategoryId, long vocabularyId,
			ServiceContext serviceContext)
		throws PortalException {

		AssetCategoryPermission.check(
			getPermissionChecker(), categoryId, ActionKeys.UPDATE);

		return assetCategoryLocalService.moveCategory(
			categoryId, parentCategoryId, vocabularyId, serviceContext);
	}

	@Override
	public List<AssetCategory> search(
		long groupId, String keywords, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		String name = CustomSQLUtil.keywords(keywords)[0];

		if (Validator.isNull(name)) {
			return assetCategoryPersistence.filterFindByG_V(
				groupId, vocabularyId, start, end, orderByComparator);
		}

		return assetCategoryPersistence.filterFindByG_LikeN_V(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public JSONArray search(
			long groupId, String name, String[] categoryProperties, int start,
			int end)
		throws PortalException {

		List<AssetCategory> categories = assetCategoryLocalService.search(
			groupId, name, categoryProperties, start, end);

		categories = filterCategories(categories);

		return Autocomplete.arrayToJSONArray(categories, "name", "name");
	}

	@Override
	public JSONArray search(
			long[] groupIds, String name, long[] vocabularyIds, int start,
			int end)
		throws PortalException {

		JSONArray jsonArray = null;

		if (Validator.isNull(name)) {
			jsonArray = toJSONArray(
				assetCategoryPersistence.filterFindByG_V(
					groupIds, vocabularyIds, start, end));
		}
		else {
			jsonArray = toJSONArray(
				assetCategoryPersistence.filterFindByG_LikeN_V(
					groupIds, name, vocabularyIds, start, end));
		}

		return jsonArray;
	}

	@Override
	public AssetCategoryDisplay searchCategoriesDisplay(
			long groupId, String title, long vocabularyId, int start, int end)
		throws PortalException {

		return searchCategoriesDisplay(
			new long[] {groupId}, title, new long[] {vocabularyId}, start, end);
	}

	@Override
	public AssetCategoryDisplay searchCategoriesDisplay(
			long groupId, String title, long parentCategoryId,
			long vocabularyId, int start, int end)
		throws PortalException {

		return searchCategoriesDisplay(
			new long[] {groupId}, title, new long[] {parentCategoryId},
			new long[] {vocabularyId}, start, end);
	}

	@Override
	public AssetCategoryDisplay searchCategoriesDisplay(
			long groupId, String title, long vocabularyId,
			long parentCategoryId, int start, int end, Sort sort)
		throws PortalException {

		return searchCategoriesDisplay(
			new long[] {groupId}, title, new long[] {vocabularyId},
			new long[] {parentCategoryId}, start, end, sort);
	}

	@Override
	public AssetCategoryDisplay searchCategoriesDisplay(
			long[] groupIds, String title, long[] vocabularyIds, int start,
			int end)
		throws PortalException {

		User user = getUser();

		BaseModelSearchResult<AssetCategory> baseModelSearchResult =
			assetCategoryLocalService.searchCategories(
				user.getCompanyId(), groupIds, title, vocabularyIds, start,
				end);

		return new AssetCategoryDisplay(
			baseModelSearchResult.getBaseModels(),
			baseModelSearchResult.getLength(), start, end);
	}

	@Override
	public AssetCategoryDisplay searchCategoriesDisplay(
			long[] groupIds, String title, long[] parentCategoryIds,
			long[] vocabularyIds, int start, int end)
		throws PortalException {

		User user = getUser();

		BaseModelSearchResult<AssetCategory> baseModelSearchResult =
			assetCategoryLocalService.searchCategories(
				user.getCompanyId(), groupIds, title, parentCategoryIds,
				vocabularyIds, start, end);

		return new AssetCategoryDisplay(
			baseModelSearchResult.getBaseModels(),
			baseModelSearchResult.getLength(), start, end);
	}

	@Override
	public AssetCategoryDisplay searchCategoriesDisplay(
			long[] groupIds, String title, long[] vocabularyIds,
			long[] parentCategoryIds, int start, int end, Sort sort)
		throws PortalException {

		User user = getUser();

		BaseModelSearchResult<AssetCategory> baseModelSearchResult =
			assetCategoryLocalService.searchCategories(
				user.getCompanyId(), groupIds, title, vocabularyIds,
				parentCategoryIds, start, end, sort);

		return new AssetCategoryDisplay(
			baseModelSearchResult.getBaseModels(),
			baseModelSearchResult.getLength(), start, end);
	}

	@Override
	public AssetCategory updateCategory(
			long categoryId, long parentCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			ServiceContext serviceContext)
		throws PortalException {

		AssetCategoryPermission.check(
			getPermissionChecker(), categoryId, ActionKeys.UPDATE);

		return assetCategoryLocalService.updateCategory(
			getUserId(), categoryId, parentCategoryId, titleMap, descriptionMap,
			vocabularyId, categoryProperties, serviceContext);
	}

	protected List<AssetCategory> filterCategories(
			List<AssetCategory> categories)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		categories = ListUtil.copy(categories);

		Iterator<AssetCategory> iterator = categories.iterator();

		while (iterator.hasNext()) {
			AssetCategory category = iterator.next();

			if (!AssetCategoryPermission.contains(
					permissionChecker, category, ActionKeys.VIEW)) {

				iterator.remove();
			}
		}

		return categories;
	}

	protected JSONArray toJSONArray(List<AssetCategory> categories)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (AssetCategory category : categories) {
			String categoryJSON = JSONFactoryUtil.looseSerialize(category);

			JSONObject categoryJSONObject = JSONFactoryUtil.createJSONObject(
				categoryJSON);

			try {
				categoryJSONObject.put(
					"path", getCategoryPath(category.getCategoryId()));

				jsonArray.put(categoryJSONObject);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return jsonArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoryServiceImpl.class);

}