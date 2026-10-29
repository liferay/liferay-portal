/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.asset.auto.tagger.AssetAutoTagger;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfigurationFactory;
import com.liferay.asset.auto.tagger.internal.util.AssetAutoTaggerUtil;
import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.auto.tagger.service.AssetAutoTaggerEntryLocalService;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	configurationPid = "com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration",
	service = AssetAutoTagger.class
)
public class AssetAutoTaggerImpl implements AssetAutoTagger {

	@Override
	public void tag(AssetEntry assetEntry) throws PortalException {
		AssetAutoTaggerConfiguration assetAutoTaggerConfiguration =
			_getAssetAutoTaggerConfiguration(assetEntry);

		if (!AssetAutoTaggerUtil.isAutoTaggable(
				assetAutoTaggerConfiguration, assetEntry)) {

			return;
		}

		List<String> assetTagNames = _getAutoAssetTagNames(
			assetEntry,
			assetAutoTaggerConfiguration.getMaximumNumberOfTagsPerAsset());

		if (assetTagNames.isEmpty()) {
			return;
		}

		_assetAutoTaggerEntryLocalService.addAssetAutoTaggerEntries(
			assetEntry, assetTagNames);

		_reindex(assetEntry);
	}

	@Override
	public void untag(AssetEntry assetEntry) throws PortalException {
		List<AssetAutoTaggerEntry> assetAutoTaggerEntries =
			_assetAutoTaggerEntryLocalService.deleteAssetAutoTaggerEntries(
				assetEntry);

		if (assetAutoTaggerEntries.isEmpty()) {
			return;
		}

		_reindex(assetEntry);
	}

	private AssetAutoTaggerConfiguration _getAssetAutoTaggerConfiguration(
			AssetEntry assetEntry)
		throws PortalException {

		return _assetAutoTaggerConfigurationFactory.
			getGroupAssetAutoTaggerConfiguration(
				_groupLocalService.getGroup(assetEntry.getGroupId()));
	}

	private List<String> _getAutoAssetTagNames(
		AssetEntry assetEntry, int maximumNumberOfTagsPerAsset) {

		AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

		Set<String> assetTagNames1 = new LinkedHashSet<>();

		for (AssetAutoTagProvider<?> assetAutoTagProvider :
				AssetAutoTaggerUtil.getAssetEntryAssetAutoTagProviders()) {

			AssetAutoTagProvider<AssetEntry> assetEntryAssetAutoTagProvider =
				(AssetAutoTagProvider<AssetEntry>)assetAutoTagProvider;

			assetTagNames1.addAll(
				assetEntryAssetAutoTagProvider.getTagNames(assetEntry));
		}

		if (assetRenderer != null) {
			List<AssetAutoTagProvider<?>> assetAutoTagProviders =
				AssetAutoTaggerUtil.getAssetAutoTagProviders(
					assetEntry.getClassName());

			for (AssetAutoTagProvider<?> assetAutoTagProvider :
					assetAutoTagProviders) {

				AssetAutoTagProvider<Object> objectAssetAutoTagProvider =
					(AssetAutoTagProvider<Object>)assetAutoTagProvider;

				assetTagNames1.addAll(
					objectAssetAutoTagProvider.getTagNames(
						assetRenderer.getAssetObject()));
			}
		}

		assetTagNames1.removeAll(Arrays.asList(assetEntry.getTagNames()));

		List<String> assetTagNames2 = new ArrayList<>(assetTagNames1);

		if (maximumNumberOfTagsPerAsset > 0) {
			return assetTagNames2.subList(
				0,
				Math.min(maximumNumberOfTagsPerAsset, assetTagNames2.size()));
		}

		return assetTagNames2;
	}

	private void _reindex(AssetEntry assetEntry) throws PortalException {
		Indexer<Object> indexer = _indexerRegistry.getIndexer(
			assetEntry.getClassName());

		if (indexer == null) {
			return;
		}

		AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

		if (assetRenderer != null) {
			Object assetObject = assetRenderer.getAssetObject();

			if (assetObject instanceof BaseModel) {
				indexer.reindex(assetObject);

				return;
			}
		}

		indexer.reindex(assetEntry.getClassName(), assetEntry.getClassPK());
	}

	@Reference
	private AssetAutoTaggerConfigurationFactory
		_assetAutoTaggerConfigurationFactory;

	@Reference
	private AssetAutoTaggerEntryLocalService _assetAutoTaggerEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

}