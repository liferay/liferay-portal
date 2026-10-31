/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portlet.asset.service.base.AssetVocabularyGroupRelServiceBaseImpl;
import com.liferay.portlet.asset.service.permission.AssetVocabularyPermission;

import java.util.List;

/**
 * @author Pei-Jung Lan
 */
public class AssetVocabularyGroupRelServiceImpl
	extends AssetVocabularyGroupRelServiceBaseImpl {

	@Override
	public AssetVocabularyGroupRel addAssetVocabularyGroupRel(
			long groupId, long vocabularyId, int depotEntryType)
		throws PortalException {

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabularyId, ActionKeys.UPDATE);

		return assetVocabularyGroupRelLocalService.addAssetVocabularyGroupRel(
			groupId, vocabularyId, depotEntryType);
	}

	@Override
	public List<AssetVocabularyGroupRel>
			getAssetVocabularyGroupRelsByVocabularyId(long vocabularyId)
		throws PortalException {

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabularyId, ActionKeys.VIEW);

		return assetVocabularyGroupRelPersistence.findByVocabularyId(
			vocabularyId);
	}

	@Override
	public void setAssetVocabularyGroupRels(
			long vocabularyId, long[] groupIds, int depotEntryType)
		throws PortalException {

		AssetVocabularyPermission.check(
			getPermissionChecker(), vocabularyId, ActionKeys.UPDATE);

		assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			vocabularyId, groupIds, depotEntryType);
	}

}