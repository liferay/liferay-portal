/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.internal.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
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
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class AssetPublisherExportImportPortletPreferencesProcessorTest {

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
			AssetPublisherPortletKeys.ASSET_PUBLISHER, "column-1",
			new HashMap<String, String[]>());

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getGroupId());

		_portletDataContextExport.setPlid(_layout.getPlid());
		_portletDataContextExport.setPortletId(
			AssetPublisherPortletKeys.ASSET_PUBLISHER);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPlid(_layout.getPlid());
		_portletDataContextImport.setPortletId(
			AssetPublisherPortletKeys.ASSET_PUBLISHER);

		_portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout, AssetPublisherPortletKeys.ASSET_PUBLISHER);

		_portletPreferences.setValue("selectionStyle", "manual");
	}

	@Test
	public void testProcessAssetVocabularyId() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_portletPreferences.setValue(
			"assetVocabularyId",
			String.valueOf(assetVocabulary.getVocabularyId()));

		_portletPreferences.store();

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		String exportedAssetVocabularyId = exportedPortletPreferences.getValue(
			"assetVocabularyId", "");

		Assert.assertEquals(
			assetVocabulary.getUuid() + StringPool.POUND + _group.getGroupId(),
			exportedAssetVocabularyId);

		// Update asset vocabulary to have a different primary key. We will swap
		// to the new one and verify it.

		AssetVocabularyLocalServiceUtil.deleteVocabulary(
			assetVocabulary.getVocabularyId());

		assetVocabulary = AssetTestUtil.addVocabulary(_group.getGroupId());

		assetVocabulary.setUuid(
			exportedAssetVocabularyId.substring(
				0, exportedAssetVocabularyId.indexOf(CharPool.POUND)));

		assetVocabulary = AssetVocabularyLocalServiceUtil.updateAssetVocabulary(
			assetVocabulary);

		// Test the import

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processImportPortletPreferences(
					_portletDataContextImport, exportedPortletPreferences);

		String importedVocabularyId = importedPortletPreferences.getValue(
			"assetVocabularyId", "");

		Assert.assertEquals(
			assetVocabulary.getVocabularyId(),
			GetterUtil.getLong(importedVocabularyId));
	}

	@Test
	public void testProcessQueryNames() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_portletPreferences.setValue("queryName0", "assetCategories");
		_portletPreferences.setValue(
			"queryValues0", String.valueOf(assetCategory.getCategoryId()));

		_portletPreferences.store();

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		String exportedAssetCategoryId = exportedPortletPreferences.getValue(
			"queryValues0", "");

		Assert.assertEquals(
			assetCategory.getUuid() + StringPool.POUND + _group.getGroupId(),
			exportedAssetCategoryId);

		// Update asset vocabulary to have a different primary key. We will swap
		// to the new one and verify it.

		AssetCategoryLocalServiceUtil.deleteCategory(
			assetCategory.getCategoryId());

		assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		assetCategory.setUuid(
			exportedAssetCategoryId.substring(
				0, exportedAssetCategoryId.indexOf(StringPool.POUND)));

		assetCategory = AssetCategoryLocalServiceUtil.updateAssetCategory(
			assetCategory);

		// Test the import

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processImportPortletPreferences(
					_portletDataContextImport, exportedPortletPreferences);

		String importedAssetCategoryId = importedPortletPreferences.getValue(
			"queryValues0", "");

		Assert.assertEquals(
			assetCategory.getCategoryId(),
			GetterUtil.getLong(importedAssetCategoryId));
	}

	@Inject(
		filter = "jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER
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