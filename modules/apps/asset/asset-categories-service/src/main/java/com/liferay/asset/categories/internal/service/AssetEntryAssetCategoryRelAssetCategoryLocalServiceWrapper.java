/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.service;

import com.liferay.asset.categories.internal.util.comparator.AssetEntryAssetCategoryRelAssetCategoryIdComparator;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceWrapper;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ServiceWrapper.class)
public class AssetEntryAssetCategoryRelAssetCategoryLocalServiceWrapper
	extends AssetCategoryLocalServiceWrapper {

	@Override
	public AssetCategory deleteCategory(
			AssetCategory category, boolean skipRebuildTree)
		throws PortalException {

		_assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRelByAssetCategoryId(
				category.getCategoryId());

		_reindexCategoryAssetEntries(category.getCategoryId());

		return super.deleteCategory(category, skipRebuildTree);
	}

	@Override
	public List<AssetCategory> getCategories(long classNameId, long classPK) {
		AssetEntry entry = _assetEntryLocalService.fetchEntry(
			classNameId, classPK);

		if (entry == null) {
			return Collections.emptyList();
		}

		return _getAssetCategoriesByEntryId(entry.getEntryId());
	}

	@Override
	public List<AssetCategory> getCategories(String className, long classPK) {
		return getCategories(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<AssetCategory> getEntryCategories(long entryId) {
		return _getAssetCategoriesByEntryId(entryId);
	}

	@Override
	public AssetCategory mergeCategories(long fromCategoryId, long toCategoryId)
		throws PortalException {

		List<AssetEntry> assetEntries = _getAssetEntriesByAssetCategoryId(
			fromCategoryId);

		for (AssetEntry assetEntry : assetEntries) {
			_assetEntryAssetCategoryRelLocalService.
				addAssetEntryAssetCategoryRel(
					assetEntry.getEntryId(), toCategoryId);
		}

		return super.mergeCategories(fromCategoryId, toCategoryId);
	}

	@Override
	public AssetCategory updateCategory(
			long userId, long categoryId, long parentCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			long vocabularyId, String[] categoryProperties,
			ServiceContext serviceContext)
		throws PortalException {

		String name = titleMap.get(LocaleUtil.getSiteDefault());

		name = ModelHintsUtil.trimString(
			AssetCategory.class.getName(), "name", name);

		AssetCategory category = _assetCategoryLocalService.getCategory(
			categoryId);

		if (!Objects.equals(category.getName(), name)) {
			_reindexCategoryAssetEntries(category.getCategoryId());
		}

		return super.updateCategory(
			userId, categoryId, parentCategoryId, titleMap, descriptionMap,
			vocabularyId, categoryProperties, serviceContext);
	}

	private List<AssetCategory> _getAssetCategoriesByEntryId(
		long assetEntryId) {

		List<AssetEntryAssetCategoryRel> assetEntryAssetCategoryRels =
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsByAssetEntryId(
					assetEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					AssetEntryAssetCategoryRelAssetCategoryIdComparator.
						getInstance(true));

		return TransformUtil.transform(
			assetEntryAssetCategoryRels,
			assetEntryAssetCategoryRel -> {
				AssetCategory category = fetchAssetCategory(
					assetEntryAssetCategoryRel.getAssetCategoryId());

				if (category != null) {
					return category;
				}

				return null;
			});
	}

	private List<AssetEntry> _getAssetEntriesByAssetCategoryId(
		long assetCategoryId) {

		List<AssetEntryAssetCategoryRel> assetEntryAssetCategoryRels =
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsByAssetCategoryId(
					assetCategoryId);

		return TransformUtil.transform(
			assetEntryAssetCategoryRels,
			assetEntryAssetCategoryRel -> {
				AssetEntry entry = _assetEntryLocalService.fetchEntry(
					assetEntryAssetCategoryRel.getAssetEntryId());

				if (entry != null) {
					return entry;
				}

				return null;
			});
	}

	private void _reindexCategoryAssetEntries(long categoryId) {
		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				Message message = new Message();

				message.put("categoryId", categoryId);

				_messageBus.sendMessage(
					DestinationNames.ASSET_CATEGORY_ASSET_ENTRIES_REINDEX,
					message);

				return null;
			});
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private MessageBus _messageBus;

}