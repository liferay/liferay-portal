/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.exception.AssetCategoryNameException;
import com.liferay.asset.kernel.exception.AssetCategoryParentCategoryIdException;
import com.liferay.asset.kernel.exception.CategoryExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.DuplicateCategoryException;
import com.liferay.asset.kernel.exception.DuplicateCategoryExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.InvalidAssetCategoryException;
import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.exception.SystemCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyPersistence;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.asset.service.base.AssetCategoryLocalServiceBaseImpl;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Provides the local service for accessing, adding, deleting, merging, moving,
 * and updating asset categories.
 *
 * @author Brian Wing Shun Chan
 * @author Alvaro del Castillo
 * @author Jorge Ferrer
 * @author Bruno Farache
 */
public class AssetCategoryLocalServiceImpl
	extends AssetCategoryLocalServiceBaseImpl {

	@Override
	public AssetCategory addCategory(
			long userId, long groupId, String title, long vocabularyId,
			ServiceContext serviceContext)
		throws PortalException {

		Locale locale = PortalUtil.getSiteDefaultLocale(groupId);

		return assetCategoryLocalService.addCategory(
			null, userId, groupId,
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				locale, title
			).build(),
			HashMapBuilder.put(
				locale, StringPool.BLANK
			).build(),
			vocabularyId, false, null, serviceContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetCategory addCategory(
			String externalReferenceCode, long userId, long groupId,
			long parentCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, long vocabularyId,
			boolean system, String[] categoryProperties,
			ServiceContext serviceContext)
		throws PortalException {

		// Category

		User user = _userPersistence.findByPrimaryKey(userId);

		Map<Locale, String> trimmedTitleMap = _getTrimmedTitleMap(titleMap);

		if (categoryProperties == null) {
			categoryProperties = new String[0];
		}

		Locale defaultLocale = PortalUtil.getSiteDefaultLocale(groupId);

		String name = trimmedTitleMap.get(defaultLocale);

		validate(0, groupId, parentCategoryId, name, vocabularyId);

		_checkSystemParentCategory(parentCategoryId);

		AssetCategory parentCategory = null;

		if (parentCategoryId > 0) {
			parentCategory = assetCategoryPersistence.findByPrimaryKey(
				parentCategoryId);
		}

		if (vocabularyId != AssetVocabularyConstants.EMPTY_VOCABULARY_ID) {
			_assetVocabularyPersistence.findByPrimaryKey(vocabularyId);
		}

		long categoryId = counterLocalService.increment();

		_validateExternalReferenceCode(externalReferenceCode, groupId, 0);

		AssetCategory category = assetCategoryPersistence.create(categoryId);

		category.setUuid(serviceContext.getUuid());
		category.setExternalReferenceCode(externalReferenceCode);
		category.setGroupId(groupId);
		category.setCompanyId(user.getCompanyId());
		category.setUserId(user.getUserId());
		category.setUserName(user.getFullName());
		category.setParentCategoryId(parentCategoryId);

		if (parentCategory == null) {
			category.setTreePath("/" + categoryId + "/");
		}
		else {
			category.setTreePath(
				parentCategory.getTreePath() + categoryId + "/");
		}

		category.setName(name);
		category.setTitleMap(trimmedTitleMap);
		category.setDescriptionMap(descriptionMap);
		category.setVocabularyId(vocabularyId);
		category.setSystem(system);

		if (EmptyModelManagerUtil.isEmptyModel()) {
			category.setStatus(WorkflowConstants.STATUS_EMPTY);
		}
		else {
			category.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		category = assetCategoryPersistence.update(category);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addCategoryResources(
				category, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addCategoryResources(
				category, serviceContext.getModelPermissions());
		}

		return category;
	}

	@Override
	public void addCategoryResources(
			AssetCategory category, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			category.getCompanyId(), category.getGroupId(),
			category.getUserId(), AssetCategory.class.getName(),
			category.getCategoryId(), false, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addCategoryResources(
			AssetCategory category, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			category.getCompanyId(), category.getGroupId(),
			category.getUserId(), AssetCategory.class.getName(),
			category.getCategoryId(), modelPermissions);
	}

	@Override
	public void deleteCategories(List<AssetCategory> categories)
		throws PortalException {

		for (AssetCategory category : categories) {
			assetCategoryLocalService.deleteCategory(category, true);
		}
	}

	@Override
	public void deleteCategories(long[] categoryIds) throws PortalException {
		List<AssetCategory> categories = new ArrayList<>();

		for (long categoryId : categoryIds) {
			AssetCategory category = assetCategoryPersistence.findByPrimaryKey(
				categoryId);

			categories.add(category);
		}

		deleteCategories(categories);
	}

	@Override
	public AssetCategory deleteCategory(AssetCategory category)
		throws PortalException {

		return assetCategoryLocalService.deleteCategory(category, false);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AssetCategory deleteCategory(
			AssetCategory category, boolean skipRebuildTree)
		throws PortalException {

		if (FeatureFlagManagerUtil.isEnabled(
				category.getCompanyId(), "LPD-86291") &&
			category.isSystem() &&
			!ExportImportThreadLocal.isImportInProcess() &&
			!GroupThreadLocal.isDeleteInProcess()) {

			throw new SystemCategoryException.MustNotDelete(
				category.getCategoryId());
		}

		// Categories

		List<AssetCategory> categories =
			assetCategoryPersistence.findByParentCategoryId(
				category.getCategoryId());

		for (AssetCategory curCategory : categories) {
			assetCategoryLocalService.deleteCategory(curCategory, true);
		}

		// Category

		assetCategoryPersistence.remove(category);

		// Resources

		_resourceLocalService.deleteResource(
			category.getCompanyId(), AssetCategory.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, category.getCategoryId());

		return category;
	}

	@Override
	public AssetCategory deleteCategory(long categoryId)
		throws PortalException {

		AssetCategory category = assetCategoryPersistence.findByPrimaryKey(
			categoryId);

		return assetCategoryLocalService.deleteCategory(category);
	}

	@Override
	public void deleteVocabularyCategories(long vocabularyId)
		throws PortalException {

		List<AssetCategory> categories = assetCategoryPersistence.findByP_V(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, vocabularyId);

		assetCategoryLocalService.deleteCategories(categories);
	}

	@Override
	public AssetCategory fetchCategory(long categoryId) {
		return assetCategoryPersistence.fetchByPrimaryKey(categoryId);
	}

	@Override
	public AssetCategory fetchCategory(
		long groupId, long parentCategoryId, String name, long vocabularyId) {

		return assetCategoryPersistence.fetchByP_N_V(
			parentCategoryId, name, vocabularyId);
	}

	@Override
	public List<AssetCategory> getCategories() {
		return assetCategoryPersistence.findAll();
	}

	@Override
	public List<AssetCategory> getCategories(Hits hits) throws PortalException {
		List<Document> documents = hits.toList();

		List<AssetCategory> categories = new ArrayList<>(documents.size());

		for (Document document : documents) {
			long categoryId = GetterUtil.getLong(
				document.get(Field.ASSET_CATEGORY_ID));

			AssetCategory category = fetchCategory(categoryId);

			if (category == null) {
				categories = null;

				Indexer<AssetCategory> indexer = IndexerRegistryUtil.getIndexer(
					AssetCategory.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (categories != null) {
				categories.add(category);
			}
		}

		return categories;
	}

	@Override
	@ThreadLocalCachable
	public List<AssetCategory> getCategories(long classNameId, long classPK) {
		return Collections.emptyList();
	}

	@Override
	public List<AssetCategory> getCategories(
		long classNameId, long classPK, int start, int end) {

		throw new UnsupportedOperationException();
	}

	@Override
	public List<AssetCategory> getCategories(String className, long classPK) {
		return assetCategoryLocalService.getCategories(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public int getCategoriesCount(long classNameId, long classPK) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AssetCategory getCategory(long categoryId) throws PortalException {
		return assetCategoryPersistence.findByPrimaryKey(categoryId);
	}

	@Override
	public AssetCategory getCategory(String uuid, long groupId)
		throws PortalException {

		return assetCategoryPersistence.findByUUID_G(uuid, groupId);
	}

	@Override
	public long[] getCategoryIds(String className, long classPK) {
		return getCategoryIds(getCategories(className, classPK));
	}

	@Override
	public String[] getCategoryNames() {
		return getCategoryNames(getCategories());
	}

	@Override
	public String[] getCategoryNames(long classNameId, long classPK) {
		return getCategoryNames(getCategories(classNameId, classPK));
	}

	@Override
	public String[] getCategoryNames(String className, long classPK) {
		return getCategoryNames(getCategories(className, classPK));
	}

	@Override
	public List<AssetCategory> getChildCategories(long parentCategoryId) {
		return assetCategoryPersistence.findByParentCategoryId(
			parentCategoryId);
	}

	@Override
	public List<AssetCategory> getChildCategories(
		long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return assetCategoryPersistence.findByParentCategoryId(
			parentCategoryId, start, end, orderByComparator);
	}

	@Override
	public int getChildCategoriesCount(long parentCategoryId) {
		return assetCategoryPersistence.countByParentCategoryId(
			parentCategoryId);
	}

	@Override
	public List<AssetCategory> getDescendantCategories(AssetCategory category) {
		return assetCategoryPersistence.findByG_LikeT_V(
			category.getGroupId(), category.getTreePath() + "%",
			category.getVocabularyId());
	}

	@Override
	public List<AssetCategory> getEntryCategories(long entryId) {
		return Collections.emptyList();
	}

	@Override
	public AssetCategory getOrAddEmptyCategory(
			String externalReferenceCode, long userId, long groupId)
		throws PortalException {

		return EmptyModelManagerUtil.getOrAddEmptyModel(
			AssetCategory.class,
			() -> assetCategoryLocalService.addCategory(
				externalReferenceCode, userId, groupId,
				AssetCategoryConstants.EMPTY_PARENT_CATEGORY_ID,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), externalReferenceCode),
				null, AssetVocabularyConstants.EMPTY_VOCABULARY_ID, false,
				new String[0], new ServiceContext()),
			externalReferenceCode,
			this::fetchAssetCategoryByExternalReferenceCode,
			this::getAssetCategoryByExternalReferenceCode, groupId, "category");
	}

	@Override
	public AssetCategory getOrAddEmptyCategoryWithAncestors(
			String externalReferenceCode, long userId, long groupId,
			String parentCategoryExternalReferenceCode,
			String vocabularyExternalReferenceCode)
		throws PortalException {

		AssetCategory assetCategory =
			assetCategoryLocalService.getOrAddEmptyCategory(
				externalReferenceCode, userId, groupId);

		if ((assetCategory.getParentCategoryId() !=
				AssetCategoryConstants.EMPTY_PARENT_CATEGORY_ID) ||
			(assetCategory.getVocabularyId() !=
				AssetVocabularyConstants.EMPTY_VOCABULARY_ID)) {

			return assetCategory;
		}

		AssetVocabulary assetVocabulary = null;

		if (Validator.isNotNull(vocabularyExternalReferenceCode)) {
			assetVocabulary =
				_assetVocabularyLocalService.getOrAddEmptyVocabulary(
					vocabularyExternalReferenceCode, userId, groupId);

			assetCategory.setVocabularyId(assetVocabulary.getVocabularyId());
		}

		if (Validator.isNotNull(parentCategoryExternalReferenceCode)) {
			AssetCategory parentAssetCategory =
				assetCategoryLocalService.getOrAddEmptyCategory(
					parentCategoryExternalReferenceCode, userId, groupId);

			long parentVocabularyId = parentAssetCategory.getVocabularyId();

			if (assetVocabulary != null) {
				if ((parentVocabularyId !=
						AssetVocabularyConstants.EMPTY_VOCABULARY_ID) &&
					(parentVocabularyId != assetVocabulary.getVocabularyId())) {

					throw new AssetCategoryParentCategoryIdException(
						"Parent category exists in a different vocabulary");
				}

				parentAssetCategory.setVocabularyId(
					assetVocabulary.getVocabularyId());
			}
			else if ((parentVocabularyId !=
						AssetVocabularyConstants.EMPTY_VOCABULARY_ID) &&
					 (assetCategory.getVocabularyId() ==
						 AssetVocabularyConstants.EMPTY_VOCABULARY_ID)) {

				assetCategory.setVocabularyId(parentVocabularyId);
			}

			boolean emptyParentCategory = false;

			if (parentAssetCategory.getParentCategoryId() ==
					AssetCategoryConstants.EMPTY_PARENT_CATEGORY_ID) {

				emptyParentCategory = true;

				parentAssetCategory.setParentCategoryId(
					AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
			}

			if ((assetVocabulary != null) || emptyParentCategory) {
				parentAssetCategory =
					assetCategoryLocalService.updateAssetCategory(
						parentAssetCategory);
			}

			assetCategory.setParentCategoryId(
				parentAssetCategory.getCategoryId());
		}
		else {
			assetCategory.setParentCategoryId(
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		}

		return assetCategoryLocalService.updateAssetCategory(assetCategory);
	}

	@Override
	public List<Long> getSubcategoryIds(long parentCategoryId) {
		AssetCategory parentAssetCategory =
			assetCategoryPersistence.fetchByPrimaryKey(parentCategoryId);

		if (parentAssetCategory == null) {
			return Collections.emptyList();
		}

		return ListUtil.toList(
			getDescendantCategories(parentAssetCategory),
			AssetCategory.CATEGORY_ID_ACCESSOR);
	}

	@Override
	public long[] getViewableCategoryIds(
			String className, long classPK, long[] categoryIds)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return categoryIds;
		}

		List<AssetCategory> oldCategories =
			assetCategoryLocalService.getCategories(className, classPK);

		for (AssetCategory category : oldCategories) {
			if (!ArrayUtil.contains(categoryIds, category.getCategoryId()) &&
				!AssetCategoryPermission.contains(
					permissionChecker, category, ActionKeys.VIEW)) {

				categoryIds = ArrayUtil.append(
					categoryIds, category.getCategoryId());
			}
		}

		return categoryIds;
	}

	@Override
	public List<AssetCategory> getVocabularyCategories(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return assetCategoryPersistence.findByVocabularyId(
			vocabularyId, start, end, orderByComparator);
	}

	@Override
	public List<AssetCategory> getVocabularyCategories(
		long parentCategoryId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return assetCategoryPersistence.findByP_V(
			parentCategoryId, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public List<AssetCategory> getVocabularyCategories(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		if (Validator.isNull(name)) {
			return assetCategoryPersistence.findByG_V(
				groupId, vocabularyId, start, end, orderByComparator);
		}

		return assetCategoryPersistence.findByG_LikeN_V(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	@Override
	public int getVocabularyCategoriesCount(long vocabularyId) {
		return assetCategoryPersistence.countByVocabularyId(vocabularyId);
	}

	@Override
	public List<AssetCategory> getVocabularyRootCategories(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getVocabularyCategories(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, vocabularyId,
			start, end, orderByComparator);
	}

	@Override
	public int getVocabularyRootCategoriesCount(long vocabularyId) {
		return assetCategoryPersistence.countByP_V(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, vocabularyId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetCategory mergeCategories(long fromCategoryId, long toCategoryId)
		throws PortalException {

		assetCategoryLocalService.deleteCategory(fromCategoryId);

		return getCategory(toCategoryId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetCategory moveCategory(
			long categoryId, long parentCategoryId, long vocabularyId,
			ServiceContext serviceContext)
		throws PortalException {

		AssetCategory category = assetCategoryPersistence.findByPrimaryKey(
			categoryId);

		_checkSystemCategory(
			category.getExternalReferenceCode(), category.getTitleMap(),
			category.getDescriptionMap(), parentCategoryId, vocabularyId,
			category);

		return _moveCategory(category, parentCategoryId, vocabularyId);
	}

	@Override
	public List<AssetCategory> search(
		long groupId, String name, String[] categoryProperties, int start,
		int end) {

		throw new UnsupportedOperationException();
	}

	@Override
	public BaseModelSearchResult<AssetCategory> searchCategories(
			long companyId, long groupIds, String title, long vocabularyId,
			int start, int end)
		throws PortalException {

		return searchCategories(
			companyId, new long[] {groupIds}, title, new long[] {vocabularyId},
			start, end);
	}

	@Override
	public BaseModelSearchResult<AssetCategory> searchCategories(
			long companyId, long[] groupIds, String title, long[] vocabularyIds,
			int start, int end)
		throws PortalException {

		SearchContext searchContext = buildSearchContext(
			companyId, groupIds, title, new long[0], vocabularyIds, start, end,
			null);

		return searchCategories(searchContext);
	}

	@Override
	public BaseModelSearchResult<AssetCategory> searchCategories(
			long companyId, long[] groupIds, String title,
			long[] parentCategoryIds, long[] vocabularyIds, int start, int end)
		throws PortalException {

		SearchContext searchContext = buildSearchContext(
			companyId, groupIds, title, parentCategoryIds, vocabularyIds, start,
			end, null);

		return searchCategories(searchContext);
	}

	@Override
	public BaseModelSearchResult<AssetCategory> searchCategories(
			long companyId, long[] groupIds, String title, long[] vocabularyIds,
			long[] parentCategoryIds, int start, int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = buildSearchContext(
			companyId, groupIds, title, parentCategoryIds, vocabularyIds, start,
			end, sort);

		return searchCategories(searchContext);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AssetCategory updateCategory(
			String externalReferenceCode, long userId, long categoryId,
			long parentCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties, ServiceContext serviceContext)
		throws PortalException {

		// Category

		AssetCategory category = assetCategoryPersistence.findByPrimaryKey(
			categoryId);

		Map<Locale, String> trimmedTitleMap = _getTrimmedTitleMap(titleMap);

		_checkSystemCategory(
			externalReferenceCode, trimmedTitleMap, descriptionMap,
			parentCategoryId, vocabularyId, category);

		if (Validator.isNotNull(externalReferenceCode)) {
			_validateExternalReferenceCode(
				externalReferenceCode, category.getGroupId(), categoryId);

			category.setExternalReferenceCode(externalReferenceCode);
		}

		Locale defaultLocale = PortalUtil.getSiteDefaultLocale(
			category.getGroupId());

		String name = trimmedTitleMap.get(defaultLocale);

		if (categoryProperties == null) {
			categoryProperties = new String[0];
		}

		category.setName(name);
		category.setTitleMap(trimmedTitleMap);
		category.setDescriptionMap(descriptionMap);
		category.setStatus(
			EmptyModelManagerUtil.solveEmptyModel(
				externalReferenceCode, category.getModelClassName(),
				category.getCompanyId(), category.getGroupId(),
				category.getStatus(), () -> WorkflowConstants.STATUS_APPROVED));

		return _moveCategory(category, parentCategoryId, vocabularyId);
	}

	protected SearchContext buildSearchContext(
		long companyId, long[] groupIds, String title, long[] parentCategoryIds,
		long[] vocabularyIds, int start, int end, Sort sort) {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.ASSET_PARENT_CATEGORY_IDS, parentCategoryIds
			).put(
				Field.ASSET_VOCABULARY_IDS, vocabularyIds
			).put(
				Field.TITLE, title
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);
		searchContext.setGroupIds(groupIds);
		searchContext.setKeywords(title);
		searchContext.setSorts(sort);
		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	protected long[] getCategoryIds(List<AssetCategory> categories) {
		return ListUtil.toLongArray(
			categories, AssetCategory.CATEGORY_ID_ACCESSOR);
	}

	protected String[] getCategoryNames(List<AssetCategory> categories) {
		return ListUtil.toArray(categories, AssetCategory.NAME_ACCESSOR);
	}

	protected BaseModelSearchResult<AssetCategory> searchCategories(
			SearchContext searchContext)
		throws PortalException {

		Indexer<AssetCategory> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			AssetCategory.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext);

			List<AssetCategory> categories = getCategories(hits);

			if (categories != null) {
				return new BaseModelSearchResult<>(
					categories, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	protected void updateChildrenVocabularyId(
		AssetCategory category, long vocabularyId) {

		for (AssetCategory childCategory : getDescendantCategories(category)) {
			if (childCategory.getCategoryId() == category.getCategoryId()) {
				continue;
			}

			childCategory.setVocabularyId(vocabularyId);

			assetCategoryPersistence.update(childCategory);
		}
	}

	protected void validate(
			long categoryId, long groupId, long parentCategoryId, String name,
			long vocabularyId)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new AssetCategoryNameException(
				StringBundler.concat(
					"Category name cannot be null for category ", categoryId,
					" and vocabulary ", vocabularyId));
		}

		AssetCategory category = assetCategoryPersistence.fetchByP_N_V(
			parentCategoryId, name, vocabularyId);

		if ((category != null) && (category.getCategoryId() != categoryId)) {
			throw new DuplicateCategoryException(
				StringBundler.concat(
					"There is another category named ", name,
					" as a child of category ", parentCategoryId));
		}

		if (vocabularyId != AssetVocabularyConstants.EMPTY_VOCABULARY_ID) {
			AssetVocabulary assetVocabulary =
				_assetVocabularyPersistence.findByPrimaryKey(vocabularyId);

			if (assetVocabulary.getGroupId() != groupId) {
				throw new NoSuchVocabularyException(
					StringBundler.concat(
						"Vocabulary ", vocabularyId,
						" does not exist in group ", groupId));
			}
		}

		if (parentCategoryId > 0) {
			AssetCategory parentAssetCategory = getCategory(parentCategoryId);

			if (parentAssetCategory.getGroupId() != groupId) {
				throw new NoSuchCategoryException(
					StringBundler.concat(
						"Category ", parentCategoryId,
						" does not exist in group ", groupId));
			}
		}
	}

	private void _checkSystemCategory(
			String externalReferenceCode, Map<Locale, String> trimmedTitleMap,
			Map<Locale, String> descriptionMap, long parentCategoryId,
			long vocabularyId, AssetCategory category)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				category.getCompanyId(), "LPD-86291") ||
			!category.isSystem() ||
			ExportImportThreadLocal.isImportInProcess()) {

			return;
		}

		if ((Validator.isNotNull(externalReferenceCode) &&
			 !externalReferenceCode.equals(
				 category.getExternalReferenceCode())) ||
			!trimmedTitleMap.equals(category.getTitleMap())) {

			throw new SystemCategoryException.MustNotRename(
				category.getCategoryId());
		}

		if (!_equals(descriptionMap, category.getDescriptionMap()) ||
			(parentCategoryId != category.getParentCategoryId()) ||
			(vocabularyId != category.getVocabularyId())) {

			throw new SystemCategoryException.MustNotModify(
				category.getCategoryId());
		}
	}

	private void _checkSystemParentCategory(long parentCategoryId)
		throws PortalException {

		if (parentCategoryId <= 0) {
			return;
		}

		AssetCategory parentCategory =
			assetCategoryPersistence.findByPrimaryKey(parentCategoryId);

		if (!FeatureFlagManagerUtil.isEnabled(
				parentCategory.getCompanyId(), "LPD-86291") ||
			!parentCategory.isSystem() ||
			ExportImportThreadLocal.isImportInProcess()) {

			return;
		}

		throw new SystemCategoryException.MustNotAddChild(parentCategoryId);
	}

	private boolean _equals(
		Map<Locale, String> map1, Map<Locale, String> map2) {

		if (MapUtil.isEmpty(map1) && MapUtil.isEmpty(map2)) {
			return true;
		}

		return Objects.equals(map1, map2);
	}

	private Map<Locale, String> _getTrimmedTitleMap(
		Map<Locale, String> titleMap) {

		Map<Locale, String> trimmedTitleMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : titleMap.entrySet()) {
			trimmedTitleMap.put(
				entry.getKey(),
				ModelHintsUtil.trimString(
					AssetCategory.class.getName(), "name", entry.getValue()));
		}

		return trimmedTitleMap;
	}

	private AssetCategory _moveCategory(
			AssetCategory category, long parentCategoryId, long vocabularyId)
		throws PortalException {

		_checkSystemParentCategory(parentCategoryId);

		validate(
			category.getCategoryId(), category.getGroupId(), parentCategoryId,
			category.getName(), vocabularyId);

		if (category.getCategoryId() == parentCategoryId) {
			throw new InvalidAssetCategoryException(
				parentCategoryId,
				InvalidAssetCategoryException.CANNOT_MOVE_INTO_ITSELF);
		}

		AssetCategory parentCategory = null;

		if (parentCategoryId > 0) {
			parentCategory = assetCategoryPersistence.findByPrimaryKey(
				parentCategoryId);

			String treePath = parentCategory.getTreePath();

			if (treePath.startsWith(category.getTreePath())) {
				throw new InvalidAssetCategoryException(
					category.getCategoryId(),
					InvalidAssetCategoryException.
						CANNOT_MOVE_INTO_CHILD_CATEGORY);
			}
		}

		if (parentCategoryId != category.getParentCategoryId()) {
			_rebuildTreePath(category, parentCategory);

			category.setParentCategoryId(parentCategoryId);
		}

		if (vocabularyId != category.getVocabularyId()) {
			_assetVocabularyPersistence.findByPrimaryKey(vocabularyId);

			updateChildrenVocabularyId(category, vocabularyId);

			category.setVocabularyId(vocabularyId);
		}

		return assetCategoryPersistence.update(category);
	}

	private void _rebuildTreePath(
		AssetCategory category, AssetCategory parentCategory) {

		String oldTreePath = category.getTreePath();
		String newTreePath = null;

		long categoryId = category.getCategoryId();

		if (parentCategory == null) {
			newTreePath = "/" + categoryId + "/";
		}
		else {
			newTreePath = parentCategory.getTreePath() + categoryId + "/";
		}

		List<AssetCategory> childrenCategories = getDescendantCategories(
			category);

		for (AssetCategory childCategory : childrenCategories) {
			if (childCategory.getCategoryId() == category.getCategoryId()) {
				continue;
			}

			String childTreePath = childCategory.getTreePath();

			childCategory.setTreePath(
				newTreePath.concat(
					childTreePath.substring(oldTreePath.length())));

			assetCategoryPersistence.update(childCategory);
		}

		category.setTreePath(newTreePath);
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId, long categoryId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			AssetCategory.class.getName(), "externalReferenceCode");

		if (externalReferenceCode.length() > maxLength) {
			throw new CategoryExternalReferenceCodeException(
				StringBundler.concat(
					"External reference code length cannot exceed ", maxLength,
					" characters"));
		}

		AssetCategory assetCategory = assetCategoryPersistence.fetchByERC_G(
			externalReferenceCode, groupId);

		if ((assetCategory != null) &&
			(assetCategory.getCategoryId() != categoryId)) {

			throw new DuplicateCategoryExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate category external reference code ",
					externalReferenceCode, " in group ", groupId));
		}
	}

	@BeanReference(type = AssetVocabularyLocalService.class)
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@BeanReference(type = AssetVocabularyPersistence.class)
	private AssetVocabularyPersistence _assetVocabularyPersistence;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}