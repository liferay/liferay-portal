/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.navigation.web.internal.upgrade.v1_0_2;

import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikel Lorza
 */
public class UpgradePortletPreferences extends BaseUpgradePortletPreferences {

	public UpgradePortletPreferences(
		AssetVocabularyLocalService assetVocabularyLocalService) {

		_assetVocabularyLocalService = assetVocabularyLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION +
				"_INSTANCE_%"
		};
	}

	@Override
	protected void upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, PortletPreferences portletPreferences)
		throws Exception {

		long displayStyleGroupId = GetterUtil.getLong(
			portletPreferences.getValue("displayStyleGroupId", null));

		if (displayStyleGroupId > 0) {
			String groupExternalReferenceCode = getGroupExternalReferenceCode(
				companyId, displayStyleGroupId);

			if (Validator.isNotNull(groupExternalReferenceCode)) {
				portletPreferences.reset("displayStyleGroupId");
				portletPreferences.setValue(
					"displayStyleGroupExternalReferenceCode",
					groupExternalReferenceCode);
			}
		}

		String assetVocabularyIdsString = portletPreferences.getValue(
			"assetVocabularyIds", null);

		if (Validator.isNull(assetVocabularyIdsString)) {
			return;
		}

		long[] assetVocabularyIds = GetterUtil.getLongValues(
			StringUtil.split(assetVocabularyIdsString, ','));

		if (ArrayUtil.isEmpty(assetVocabularyIds)) {
			return;
		}

		Map<Long, List<String>> groupAssetVocabularyExternalReferenceCodesMap =
			_getGroupAssetVocabularyExternalReferenceCodesMap(
				assetVocabularyIds);

		if (groupAssetVocabularyExternalReferenceCodesMap.isEmpty()) {
			return;
		}

		portletPreferences.reset("assetVocabularyIds");

		List<String> groupExternalReferenceCodes = new ArrayList<>();

		for (Map.Entry<Long, List<String>> entries :
				groupAssetVocabularyExternalReferenceCodesMap.entrySet()) {

			String scopeExternalReferenceCode = getScopeExternalReferenceCode(
				companyId, ownerId, ownerType, plid, entries.getKey());

			if (Validator.isNull(scopeExternalReferenceCode)) {
				portletPreferences.setValues(
					"assetVocabularyExternalReferenceCodes",
					ArrayUtil.toStringArray(entries.getValue()));
			}
			else {
				groupExternalReferenceCodes.add(scopeExternalReferenceCode);

				portletPreferences.setValues(
					"assetVocabularyExternalReferenceCodes_" +
						scopeExternalReferenceCode,
					ArrayUtil.toStringArray(entries.getValue()));
			}
		}

		portletPreferences.setValues(
			"assetVocabularyGroupExternalReferenceCodes",
			ArrayUtil.toStringArray(groupExternalReferenceCodes));
	}

	private Map<Long, List<String>>
		_getGroupAssetVocabularyExternalReferenceCodesMap(
			long[] assetVocabularyIds) {

		Map<Long, List<String>> groupAssetVocabularyExternalReferenceCodesMap =
			new HashMap<>();

		for (long assetVocabularyId : assetVocabularyIds) {
			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.fetchAssetVocabulary(
					assetVocabularyId);

			if (assetVocabulary == null) {
				continue;
			}

			List<String> groupAssetVocabularyExternalReferenceCodes =
				groupAssetVocabularyExternalReferenceCodesMap.computeIfAbsent(
					assetVocabulary.getGroupId(), key -> new ArrayList<>());

			groupAssetVocabularyExternalReferenceCodes.add(
				assetVocabulary.getExternalReferenceCode());
		}

		return groupAssetVocabularyExternalReferenceCodesMap;
	}

	private final AssetVocabularyLocalService _assetVocabularyLocalService;

}