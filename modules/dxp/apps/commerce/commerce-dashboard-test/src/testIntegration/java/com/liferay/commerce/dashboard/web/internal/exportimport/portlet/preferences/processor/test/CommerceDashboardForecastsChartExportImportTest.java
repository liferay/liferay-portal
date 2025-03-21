/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.dashboard.web.internal.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class CommerceDashboardForecastsChartExportImportTest
	extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public String getPortletId() throws Exception {
		return PortletIdCodec.encode(
			"com_liferay_commerce_dashboard_web_internal_portlet_" +
				"CommerceDashboardForecastsChartPortlet",
			RandomTestUtil.randomString());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		super.setUp();
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

	@Test
	public void testExportImportPortletPreferencesFromCurrentGroup()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"assetCategoryExternalReferenceCodes",
				() -> new String[] {assetCategory.getExternalReferenceCode()}
			).build());

		AssetCategory importedAssetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					assetCategory.getExternalReferenceCode(),
					layout.getGroupId());

		Assert.assertEquals(assetCategory, importedAssetCategory);
		Assert.assertEquals(
			importedAssetCategory.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetCategoryExternalReferenceCodes", null));

		Assert.assertEquals(
			group.getGroupId(), importedAssetCategory.getGroupId());
	}

	@Test
	public void testExportImportPortletPreferencesFromDifferentGroup()
		throws Exception {

		Group group2 = GroupTestUtil.addGroup();

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			group2.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			group2.getGroupId(), assetVocabulary.getVocabularyId());

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"assetCategoryExternalReferenceCodes",
				() -> new String[] {assetCategory.getExternalReferenceCode()}
			).build());

		AssetCategory importedAssetCategory =
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					assetCategory.getExternalReferenceCode(),
					layout.getGroupId());

		Assert.assertNull(importedAssetCategory);

		Assert.assertEquals(
			assetCategory.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetCategoryExternalReferenceCodes", null));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

}