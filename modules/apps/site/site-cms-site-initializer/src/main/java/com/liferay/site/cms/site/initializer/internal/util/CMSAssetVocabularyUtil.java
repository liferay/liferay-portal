/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alicia García
 */
public class CMSAssetVocabularyUtil {

	public static List<AssetVocabulary> getCMSAssetVocabularies(
		long[] groupIds) {

		Set<AssetVocabulary> assetVocabularies = new LinkedHashSet<>();

		boolean hasConnectedProjectDepotEntry = false;
		boolean hasConnectedSpaceDepotEntry = false;

		for (long groupId : groupIds) {
			Group group = GroupLocalServiceUtil.fetchGroup(groupId);

			if (group == null) {
				continue;
			}

			int depotEntryType = GetterUtil.getInteger(
				group.getTypeSettingsProperty("depotEntryType"));

			if (depotEntryType == DepotConstants.TYPE_PROJECT) {
				hasConnectedProjectDepotEntry = true;
			}
			else if (depotEntryType == DepotConstants.TYPE_SPACE) {
				hasConnectedSpaceDepotEntry = true;
			}

			if ((depotEntryType != DepotConstants.TYPE_PROJECT) &&
				(depotEntryType != DepotConstants.TYPE_SPACE)) {

				continue;
			}

			assetVocabularies.addAll(
				_getAssetVocabulariesByGroupRels(groupId, depotEntryType));
		}

		if (hasConnectedProjectDepotEntry) {
			assetVocabularies.addAll(
				_getAssetVocabulariesByGroupRels(
					GroupConstants.ANY_PARENT_GROUP_ID,
					DepotConstants.TYPE_PROJECT));
		}

		if (hasConnectedSpaceDepotEntry) {
			assetVocabularies.addAll(
				_getAssetVocabulariesByGroupRels(
					GroupConstants.ANY_PARENT_GROUP_ID,
					DepotConstants.TYPE_SPACE));
		}

		return new ArrayList<>(assetVocabularies);
	}

	public static long getCompanyId(long[] groupIds) {
		for (long groupId : groupIds) {
			Group group = GroupLocalServiceUtil.fetchGroup(groupId);

			if (group != null) {
				return group.getCompanyId();
			}
		}

		return CompanyConstants.SYSTEM;
	}

	private static List<AssetVocabulary> _getAssetVocabulariesByGroupRels(
		long groupId, int depotEntryType) {

		List<AssetVocabulary> assetVocabularies = new ArrayList<>();

		for (AssetVocabularyGroupRel assetVocabularyGroupRel :
				AssetVocabularyGroupRelLocalServiceUtil.
					getAssetVocabularyGroupRelsByGroupIdAndDepotEntryType(
						groupId, depotEntryType)) {

			AssetVocabulary assetVocabulary =
				AssetVocabularyLocalServiceUtil.fetchAssetVocabulary(
					assetVocabularyGroupRel.getVocabularyId());

			if (assetVocabulary == null) {
				continue;
			}

			assetVocabularies.add(assetVocabulary);
		}

		return assetVocabularies;
	}

}