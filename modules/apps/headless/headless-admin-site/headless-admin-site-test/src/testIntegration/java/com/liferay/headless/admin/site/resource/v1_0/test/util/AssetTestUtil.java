/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.Scope;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lourdes Fernández Besada
 */
public class AssetTestUtil {

	public static String[] randomKeywords(ServiceContext serviceContext)
		throws Exception {

		int length = RandomTestUtil.randomInt(0, 3);

		String[] keywords = new String[length];

		for (int i = 0; i < length; i++) {
			if (RandomTestUtil.randomBoolean()) {
				keywords[i] = StringUtil.toLowerCase(
					RandomTestUtil.randomString());

				continue;
			}

			AssetTag assetTag = AssetTagLocalServiceUtil.addTag(
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				serviceContext);

			keywords[i] = assetTag.getName();
		}

		Arrays.sort(keywords);

		return keywords;
	}

	public static ItemExternalReference[]
			randomTaxonomyCategoryItemExternalReferences(
				long companyGroupId, ServiceContext serviceContext)
		throws Exception {

		List<AssetCategory> assetCategories = _randomAssetCategories(
			companyGroupId, serviceContext);

		return TransformUtil.unsafeTransformToArray(
			assetCategories,
			assetCategory -> new ItemExternalReference() {
				{
					setClassName(AssetCategory.class.getName());
					setExternalReferenceCode(
						assetCategory.getExternalReferenceCode());
					setScope(
						() -> _getScope(
							serviceContext.getScopeGroupId(),
							assetCategory.getGroupId()));
				}
			},
			ItemExternalReference.class);
	}

	private static Scope _getScope(long groupId, long scopeGroupId)
		throws Exception {

		if (groupId == scopeGroupId) {
			return null;
		}

		Group group = GroupLocalServiceUtil.getGroup(scopeGroupId);

		return new Scope() {
			{
				setExternalReferenceCode(group::getExternalReferenceCode);
				setType(
					() -> {
						if (group.getType() == GroupConstants.TYPE_DEPOT) {
							return Scope.Type.ASSET_LIBRARY;
						}

						return Scope.Type.SITE;
					});
			}
		};
	}

	private static List<AssetCategory> _randomAssetCategories(
			AssetVocabulary assetVocabulary, ServiceContext serviceContext)
		throws Exception {

		List<AssetCategory> assetCategories = new ArrayList<>();

		for (int i = 0; i < RandomTestUtil.randomInt(1, 3); i++) {
			assetCategories.add(
				AssetCategoryLocalServiceUtil.addCategory(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					assetVocabulary.getGroupId(), 0,
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomLocaleStringMap(),
					assetVocabulary.getVocabularyId(), null, serviceContext));
		}

		return assetCategories;
	}

	private static List<AssetCategory> _randomAssetCategories(
			long companyGroupId, ServiceContext serviceContext)
		throws Exception {

		List<AssetCategory> assetCategories = new ArrayList<>();

		if (RandomTestUtil.randomBoolean()) {
			return assetCategories;
		}

		for (int i = 0; i < RandomTestUtil.randomInt(1, 3); i++) {
			AssetVocabulary assetVocabulary =
				AssetVocabularyLocalServiceUtil.addVocabulary(
					TestPropsValues.getUserId(),
					serviceContext.getScopeGroupId(),
					RandomTestUtil.randomString(), serviceContext);

			assetCategories = ListUtil.concat(
				assetCategories,
				_randomAssetCategories(assetVocabulary, serviceContext));
		}

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), companyGroupId,
				RandomTestUtil.randomString(), serviceContext);

		return ListUtil.concat(
			assetCategories,
			_randomAssetCategories(assetVocabulary, serviceContext));
	}

}