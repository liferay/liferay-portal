/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Igor Franca
 */
public class ViewProjectInfoSummarySectionDisplayContext
	extends BaseInfoSummarySectionDisplayContext {

	public ViewProjectInfoSummarySectionDisplayContext(
		AssetCategoryLocalService assetCategoryLocalService,
		AssetVocabularyLocalService assetVocabularyLocalService,
		ObjectEntry objectEntry, ThemeDisplay themeDisplay) {

		super(objectEntry, themeDisplay);

		_assetCategoryLocalService = assetCategoryLocalService;
		_assetVocabularyLocalService = assetVocabularyLocalService;
	}

	public Map<String, Object> getProperties() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"cmpProjectObjectEntryId", objectEntry.getObjectEntryId()
		).put(
			"funnelStages", _getAssetCategoryTitles("L_CMP_FUNNEL_STAGE")
		).put(
			"manager",
			_getUserInfoMap(
				GetterUtil.getLong(
					getFieldValue("r_userToCMPProjectManager_userId")))
		).put(
			"personas", _getAssetCategoryTitles("L_CMP_PERSONAS")
		).put(
			"sponsor",
			_getUserInfoMap(
				GetterUtil.getLong(
					getFieldValue("r_userToCMPProjectSponsor_userId")))
		).putAll(
			super.getProperties()
		).build();
	}

	private List<String> _getAssetCategoryTitles(
		String assetVocabularyExternalReferenceCode) {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					assetVocabularyExternalReferenceCode,
					themeDisplay.getSiteGroupId());

		if (assetVocabulary == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			_assetCategoryLocalService.getCategories(
				objectEntry.getModelClassName(),
				objectEntry.getObjectEntryId()),
			assetCategory -> {
				if (!Objects.equals(
						assetCategory.getVocabularyId(),
						assetVocabulary.getVocabularyId())) {

					return null;
				}

				return assetCategory.getTitle(themeDisplay.getLocale());
			});
	}

	private Map<String, String> _getUserInfoMap(long userId) throws Exception {
		User user = UserLocalServiceUtil.fetchUser(userId);

		if (user == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.put(
			"image", user.getPortraitURL(themeDisplay)
		).put(
			"name", user.getFullName()
		).build();
	}

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;

}