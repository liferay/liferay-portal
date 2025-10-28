/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AssetCategoriesNavigationPortletPreferencesProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout,
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION,
			"column-1", new HashMap<>());

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getGroupId());

		_portletDataContextExport.setPlid(_layout.getPlid());
		_portletDataContextExport.setPortletId(
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPlid(_layout.getPlid());
		_portletDataContextImport.setPortletId(
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION);

		_portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout,
				AssetCategoriesNavigationPortletKeys.
					ASSET_CATEGORIES_NAVIGATION);
	}

	@Test
	public void testProcessAssetVocabularyExternalReferenceCode()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_portletPreferences.setValue(
			"allAssetVocabularies", Boolean.FALSE.toString());
		_portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes",
			assetVocabulary.getExternalReferenceCode());

		_portletPreferences.store();

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		String[] exportedAssetVocabularyExternalReferenceCodes =
			exportedPortletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes", null);

		Assert.assertNotNull(exportedAssetVocabularyExternalReferenceCodes);

		Assert.assertEquals(
			assetVocabulary.getExternalReferenceCode(),
			exportedAssetVocabularyExternalReferenceCodes[0]);

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processImportPortletPreferences(
					_portletDataContextImport, exportedPortletPreferences);

		String[] importedAssetVocabularyExternalReferenceCodes =
			importedPortletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes", null);

		Assert.assertEquals(
			assetVocabulary.getExternalReferenceCode(),
			importedAssetVocabularyExternalReferenceCodes[0]);
	}

	@Inject(
		filter = "jakarta.portlet.name=" + AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION
	)
	private ExportImportPortletPreferencesProcessor
		_exportImportPortletPreferencesProcessor;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;
	private PortletPreferences _portletPreferences;

}