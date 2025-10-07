/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.item.selector.web.internal.layout.list.retriever;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.info.filter.CategoriesInfoFilter;
import com.liferay.info.filter.InfoFilter;
import com.liferay.info.filter.KeywordsInfoFilter;
import com.liferay.info.filter.TagsInfoFilter;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.layout.list.retriever.ClassedModelListObjectReference;
import com.liferay.layout.list.retriever.LayoutListRetriever;
import com.liferay.layout.list.retriever.LayoutListRetrieverContext;
import com.liferay.layout.list.retriever.SegmentsEntryLayoutListRetriever;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.constants.SegmentsEntryConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = LayoutListRetriever.class)
public class AssetEntryListLayoutListRetriever
	implements LayoutListRetriever
		<InfoListItemSelectorReturnType, ClassedModelListObjectReference>,
			   SegmentsEntryLayoutListRetriever
				   <ClassedModelListObjectReference> {

	@Override
	public long getDefaultVariationSegmentsEntryId(
		ClassedModelListObjectReference classedModelListObjectReference) {

		return SegmentsEntryConstants.ID_DEFAULT;
	}

	@Override
	public InfoPage<?> getInfoPage(
		ClassedModelListObjectReference classedModelListObjectReference,
		LayoutListRetrieverContext layoutListRetrieverContext) {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.fetchAssetListEntry(
				classedModelListObjectReference.getClassPK());

		if (assetListEntry == null) {
			return InfoPage.of(
				Collections.emptyList(),
				layoutListRetrieverContext.getPagination(), 0);
		}

		long[] segmentsEntryIds =
			layoutListRetrieverContext.getSegmentsEntryIds();

		if (segmentsEntryIds == null) {
			segmentsEntryIds = new long[] {0};
		}

		Pagination pagination = layoutListRetrieverContext.getPagination();

		if (pagination == null) {
			pagination = Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, segmentsEntryIds,
				_getAssetCategoryIds(layoutListRetrieverContext),
				_getAssetTagNames(layoutListRetrieverContext),
				_getKeywords(layoutListRetrieverContext), StringPool.BLANK,
				pagination.getStart(), pagination.getEnd());

		if (Objects.equals(
				AssetEntry.class.getName(),
				assetListEntry.getAssetEntryType())) {

			return infoPage;
		}

		return InfoPage.of(
			_toAssetObjects((List<AssetEntry>)infoPage.getPageItems()),
			layoutListRetrieverContext.getPagination(),
			infoPage.getTotalCount());
	}

	@Override
	public List<InfoFilter> getSupportedInfoFilters(
		ClassedModelListObjectReference classedModelListObjectReference) {

		return _supportedInfoFilters;
	}

	@Override
	public boolean hasSegmentsEntryVariation(
		ClassedModelListObjectReference classedModelListObjectReference,
		long segmentsEntryId) {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.fetchAssetListEntry(
				classedModelListObjectReference.getClassPK());

		if ((assetListEntry != null) &&
			Validator.isNotNull(
				_assetListEntrySegmentsEntryRelLocalService.
					fetchAssetListEntrySegmentsEntryRel(
						assetListEntry.getAssetListEntryId(),
						segmentsEntryId))) {

			return true;
		}

		return false;
	}

	private long[][] _getAssetCategoryIds(
		LayoutListRetrieverContext layoutListRetrieverContext) {

		CategoriesInfoFilter categoriesInfoFilter =
			layoutListRetrieverContext.getInfoFilter(
				CategoriesInfoFilter.class);

		if (categoriesInfoFilter == null) {
			return new long[0][];
		}

		return categoriesInfoFilter.getCategoryIds();
	}

	private String[][] _getAssetTagNames(
		LayoutListRetrieverContext layoutListRetrieverContext) {

		TagsInfoFilter tagsInfoFilter =
			layoutListRetrieverContext.getInfoFilter(TagsInfoFilter.class);

		if (tagsInfoFilter == null) {
			return new String[0][];
		}

		return tagsInfoFilter.getTagNames();
	}

	private String _getKeywords(
		LayoutListRetrieverContext layoutListRetrieverContext) {

		KeywordsInfoFilter keywordsInfoFilter =
			layoutListRetrieverContext.getInfoFilter(KeywordsInfoFilter.class);

		if (keywordsInfoFilter == null) {
			return StringPool.BLANK;
		}

		return keywordsInfoFilter.getKeywords();
	}

	private List<Object> _toAssetObjects(List<AssetEntry> assetEntries) {
		return TransformUtil.transform(
			assetEntries,
			assetEntry -> {
				AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

				return assetRenderer.getAssetObject();
			});
	}

	private static final List<InfoFilter> _supportedInfoFilters = Arrays.asList(
		new CategoriesInfoFilter(), new KeywordsInfoFilter(),
		new TagsInfoFilter());

	@Reference
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

}