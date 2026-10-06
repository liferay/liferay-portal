/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.exception.AssetTagGroupRelGroupIdException;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portlet.asset.service.base.AssetTagGroupRelLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Gislayne Vitorino
 */
public class AssetTagGroupRelLocalServiceImpl
	extends AssetTagGroupRelLocalServiceBaseImpl {

	@Override
	public AssetTagGroupRel addAssetTagGroupRel(
			long groupId, long tagId, int depotEntryType)
		throws PortalException {

		AssetTagGroupRel assetTagGroupRel =
			assetTagGroupRelPersistence.fetchByG_T_D(
				groupId, tagId, depotEntryType);

		if (assetTagGroupRel != null) {
			return assetTagGroupRel;
		}

		assetTagGroupRel = assetTagGroupRelPersistence.create(
			counterLocalService.increment());

		assetTagGroupRel.setGroupId(groupId);
		assetTagGroupRel.setTagId(tagId);
		assetTagGroupRel.setDepotEntryType(depotEntryType);

		assetTagGroupRel = assetTagGroupRelPersistence.update(assetTagGroupRel);

		_reindexAssetTag(tagId);

		return assetTagGroupRel;
	}

	@Override
	public void deleteAssetTagGroupRelsByGroupId(long groupId) {
		assetTagGroupRelPersistence.removeByGroupId(groupId);
	}

	@Override
	public void deleteAssetTagGroupRelsByTagId(long tagId) {
		assetTagGroupRelPersistence.removeByTagId(tagId);
	}

	@Override
	public void deleteAssetTagGroupRelsByTagIdAndDepotEntryType(
			long tagId, int depotEntryType)
		throws PortalException {

		assetTagGroupRelPersistence.removeByT_D(tagId, depotEntryType);

		_reindexAssetTag(tagId);
	}

	@Override
	public List<AssetTagGroupRel> getAssetTagGroupRelsByGroupId(long groupId) {
		return assetTagGroupRelPersistence.findByGroupId(groupId);
	}

	@Override
	public List<AssetTagGroupRel>
		getAssetTagGroupRelsByGroupIdAndDepotEntryType(
			long groupId, int depotEntryType) {

		return assetTagGroupRelPersistence.findByG_D(groupId, depotEntryType);
	}

	@Override
	public List<AssetTagGroupRel> getAssetTagGroupRelsByTagId(long tagId) {
		return assetTagGroupRelPersistence.findByTagId(tagId);
	}

	@Override
	public List<AssetTagGroupRel> getAssetTagGroupRelsByTagIdAndDepotEntryType(
		long tagId, int depotEntryType) {

		return assetTagGroupRelPersistence.findByT_D(tagId, depotEntryType);
	}

	@Override
	public void setAssetTagGroupRels(
			long tagId, long[] groupIds, int depotEntryType)
		throws PortalException {

		if (ArrayUtil.isEmpty(groupIds)) {
			throw new AssetTagGroupRelGroupIdException();
		}

		assetTagGroupRelPersistence.removeByT_D(tagId, depotEntryType);

		for (long groupId : groupIds) {
			addAssetTagGroupRel(groupId, tagId, depotEntryType);
		}
	}

	private void _reindexAssetTag(long tagId) throws PortalException {
		Indexer<AssetTag> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			AssetTag.class);

		indexer.reindex(AssetTag.class.getName(), tagId);
	}

}