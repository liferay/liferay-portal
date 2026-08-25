/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.model.listener;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ModelListener.class)
public class AssetCategoryChildCategoriesCountModelListener
	extends BaseModelListener<AssetCategory> {

	@Override
	public void onAfterCreate(AssetCategory assetCategory)
		throws ModelListenerException {

		_reindexParentAssetCategory(assetCategory.getParentCategoryId());
	}

	@Override
	public void onAfterRemove(AssetCategory assetCategory)
		throws ModelListenerException {

		_reindexParentAssetCategory(assetCategory.getParentCategoryId());
	}

	@Override
	public void onAfterUpdate(
			AssetCategory originalAssetCategory, AssetCategory assetCategory)
		throws ModelListenerException {

		if (originalAssetCategory.getParentCategoryId() ==
				assetCategory.getParentCategoryId()) {

			return;
		}

		_reindexParentAssetCategory(
			originalAssetCategory.getParentCategoryId());
		_reindexParentAssetCategory(assetCategory.getParentCategoryId());
	}

	private void _reindexParentAssetCategory(long parentCategoryId)
		throws ModelListenerException {

		if (parentCategoryId ==
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			return;
		}

		AssetCategory parentAssetCategory =
			_assetCategoryLocalService.fetchAssetCategory(parentCategoryId);

		if (parentAssetCategory == null) {
			return;
		}

		try {
			Indexer<AssetCategory> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(AssetCategory.class);

			indexer.reindex(parentAssetCategory);
		}
		catch (SearchException searchException) {
			throw new ModelListenerException(searchException);
		}
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

}