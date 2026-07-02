/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.internal.upgrade.v2_4_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.service.AssetCategoryPropertyLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseExternalReferenceCodeUpgradeProcessTestCase;

import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class AssetCategoryPropertyExternalReferenceCodeUpgradeProcessTest
	extends BaseExternalReferenceCodeUpgradeProcessTestCase {

	@Override
	protected ExternalReferenceCodeModel[] addExternalReferenceCodeModels(
			String tableName)
		throws PortalException {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				group.getCreatorUserId(), group.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			RandomTestUtil.randomString(), group.getCreatorUserId(),
			group.getGroupId(), 0, RandomTestUtil.randomLocaleStringMap(), null,
			assetVocabulary.getVocabularyId(), false, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		return new ExternalReferenceCodeModel[] {
			_assetCategoryPropertyLocalService.addCategoryProperty(
				TestPropsValues.getUserId(), assetCategory.getCategoryId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString())
		};
	}

	@Override
	protected ExternalReferenceCodeModel fetchExternalReferenceCodeModel(
			ExternalReferenceCodeModel externalReferenceCodeModel,
			String tableName)
		throws PortalException {

		AssetCategoryProperty assetCategoryProperty =
			(AssetCategoryProperty)externalReferenceCodeModel;

		return _assetCategoryPropertyLocalService.fetchAssetCategoryProperty(
			assetCategoryProperty.getCategoryPropertyId());
	}

	@Override
	protected String getExternalReferenceCode(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		if (tableName.equals("AssetCategoryProperty")) {
			AssetCategoryProperty assetCategoryProperty =
				(AssetCategoryProperty)externalReferenceCodeModel;

			return String.valueOf(
				assetCategoryProperty.getCategoryPropertyId());
		}

		return super.getExternalReferenceCode(
			externalReferenceCodeModel, tableName);
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"AssetCategoryProperty"};
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return _upgradeStepRegistrator;
	}

	@Override
	protected Version getVersion() {
		return new Version(2, 4, 0);
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.asset.category.property.internal.upgrade.registry.AssetCategoryPropertyServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}