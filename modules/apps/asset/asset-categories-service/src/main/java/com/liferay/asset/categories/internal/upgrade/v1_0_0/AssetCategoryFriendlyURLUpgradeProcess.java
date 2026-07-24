/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.upgrade.v1_0_0;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class AssetCategoryFriendlyURLUpgradeProcess extends UpgradeProcess {

	public AssetCategoryFriendlyURLUpgradeProcess(
		AssetCategoryLocalService assetCategoryLocalService,
		ClassNameLocalService classNameLocalService,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService) {

		_assetCategoryLocalService = assetCategoryLocalService;
		_classNameLocalService = classNameLocalService;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(
			AssetCategory.class);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select categoryId from AssetCategory where ctCollectionId = " +
					"0");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				AssetCategory assetCategory =
					_assetCategoryLocalService.fetchAssetCategory(
						resultSet.getLong("categoryId"));

				if (assetCategory == null) {
					continue;
				}

				_migrate(assetCategory, classNameId);
			}
		}
	}

	private long _getParentClassPK(AssetCategory assetCategory) {
		if (assetCategory.getParentCategoryId() ==
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			return assetCategory.getVocabularyId();
		}

		return assetCategory.getParentCategoryId();
	}

	private void _migrate(AssetCategory assetCategory, long classNameId)
		throws PortalException {

		long parentClassPK = _getParentClassPK(assetCategory);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				classNameId, assetCategory.getCategoryId());

		if (friendlyURLEntry == null) {
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				assetCategory.getGroupId(), classNameId, parentClassPK,
				assetCategory.getCategoryId(),
				assetCategory.getDefaultLanguageId(),
				_friendlyURLEntryLocalService.getUniqueUrlTitleMap(
					assetCategory.getGroupId(), classNameId, parentClassPK,
					assetCategory.getCategoryId(), assetCategory.getTitleMap()),
				new ServiceContext());

			return;
		}

		if (friendlyURLEntry.getParentClassPK() == parentClassPK) {
			return;
		}

		Map<String, String> urlTitleMap = new HashMap<>();

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				_friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
					friendlyURLEntry.getFriendlyURLEntryId())) {

			urlTitleMap.put(
				friendlyURLEntryLocalization.getLanguageId(),
				friendlyURLEntryLocalization.getUrlTitle());
		}

		_friendlyURLEntryLocalService.updateFriendlyURLEntry(
			friendlyURLEntry.getFriendlyURLEntryId(), classNameId,
			parentClassPK, assetCategory.getCategoryId(),
			friendlyURLEntry.getDefaultLanguageId(), urlTitleMap,
			new ServiceContext());
	}

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final ClassNameLocalService _classNameLocalService;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}