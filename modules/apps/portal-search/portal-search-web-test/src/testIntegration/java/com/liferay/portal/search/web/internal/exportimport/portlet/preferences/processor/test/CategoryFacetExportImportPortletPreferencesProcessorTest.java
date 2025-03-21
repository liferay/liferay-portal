/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.search.test.util.exportimport.BaseExportImportPortletPreferencesProcessorTestCase;
import com.liferay.portal.search.web.internal.category.facet.constants.CategoryFacetPortletKeys;
import com.liferay.portal.test.rule.ExpectedLog;
import com.liferay.portal.test.rule.ExpectedLogs;
import com.liferay.portal.test.rule.ExpectedType;
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
 * @author Gustavo Lima
 * @author Joshua Cords
 */
@RunWith(Arquillian.class)
public class CategoryFacetExportImportPortletPreferencesProcessorTest
	extends BaseExportImportPortletPreferencesProcessorTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId());

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout,
			CategoryFacetPortletKeys.CATEGORY_FACET, "column-1",
			new HashMap<String, String[]>());

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getCompanyId(), _group.getGroupId(),
				new HashMap<String, String[]>(), null, null);

		_portletDataContextExport.setPlid(layout.getPlid());
		_portletDataContextExport.setPortletId(
			CategoryFacetPortletKeys.CATEGORY_FACET);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPlid(layout.getPlid());
		_portletDataContextImport.setPortletId(
			CategoryFacetPortletKeys.CATEGORY_FACET);

		_portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				layout, CategoryFacetPortletKeys.CATEGORY_FACET);
	}

	@ExpectedLogs(
		expectedLogs = {
			@ExpectedLog(
				expectedLog = "Unable to get a staged model data handler for a null value because a model was not exported properly",
				expectedType = ExpectedType.EXACT
			)
		},
		level = "ERROR", loggerClass = StagedModelDataHandlerUtil.class
	)
	@Test
	public void testProcessAssetVocabularyIdWithMissingReference()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_setCategoryFacetPortletPreferences(assetVocabulary);

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		String exportedAssetVocabularyId = exportedPortletPreferences.getValue(
			"vocabularyIds", "");

		Assert.assertEquals(
			StringBundler.concat(
				assetVocabulary.getExternalReferenceCode(), StringPool.POUND,
				_group.getGroupId(), StringPool.POUND,
				_group.getExternalReferenceCode()),
			exportedAssetVocabularyId);

		AssetVocabularyLocalServiceUtil.deleteVocabulary(
			assetVocabulary.getVocabularyId());

		_portletDataContextImport.setImportDataRootElement(
			_portletDataContextExport.getExportDataRootElement());

		_exportImportPortletPreferencesProcessor.
			processImportPortletPreferences(
				_portletDataContextImport, exportedPortletPreferences);
	}

	@Test
	public void testProcessAssetVocabularyIdWithReference() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_setCategoryFacetPortletPreferences(assetVocabulary);

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		String exportedAssetVocabularyId = exportedPortletPreferences.getValue(
			"vocabularyIds", "");

		Assert.assertEquals(
			StringBundler.concat(
				assetVocabulary.getExternalReferenceCode(), StringPool.POUND,
				_group.getGroupId(), StringPool.POUND,
				_group.getExternalReferenceCode()),
			exportedAssetVocabularyId);

		_portletDataContextImport.setImportDataRootElement(
			_portletDataContextExport.getExportDataRootElement());

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processImportPortletPreferences(
					_portletDataContextImport, exportedPortletPreferences);

		String importedVocabularyId = importedPortletPreferences.getValue(
			"vocabularyIds", "");

		Assert.assertEquals(
			assetVocabulary.getVocabularyId(),
			GetterUtil.getLong(importedVocabularyId));
	}

	@Test
	public void testProcessAssetVocabularyIdWithReplacedReference()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_setCategoryFacetPortletPreferences(assetVocabulary);

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		String exportedAssetVocabularyId = exportedPortletPreferences.getValue(
			"vocabularyIds", "");

		Assert.assertEquals(
			StringBundler.concat(
				assetVocabulary.getExternalReferenceCode(), StringPool.POUND,
				_group.getGroupId(), StringPool.POUND,
				_group.getExternalReferenceCode()),
			exportedAssetVocabularyId);

		AssetVocabularyLocalServiceUtil.deleteVocabulary(
			assetVocabulary.getVocabularyId());

		AssetVocabulary importedAssetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		importedAssetVocabulary.setExternalReferenceCode(
			exportedAssetVocabularyId.substring(
				0, exportedAssetVocabularyId.indexOf(CharPool.POUND)));

		importedAssetVocabulary =
			AssetVocabularyLocalServiceUtil.updateAssetVocabulary(
				importedAssetVocabulary);

		_portletDataContextImport.setImportDataRootElement(
			_portletDataContextExport.getExportDataRootElement());

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processImportPortletPreferences(
					_portletDataContextImport, exportedPortletPreferences);

		String importedVocabularyId = importedPortletPreferences.getValue(
			"vocabularyIds", "");

		Assert.assertEquals(
			importedAssetVocabulary.getVocabularyId(),
			GetterUtil.getLong(importedVocabularyId));
	}

	@Override
	protected ExportImportPortletPreferencesProcessor
		getExportImportPortletPreferencesProcessor() {

		return _exportImportPortletPreferencesProcessor;
	}

	private void _setCategoryFacetPortletPreferences(
			AssetVocabulary assetVocabulary)
		throws Exception {

		_portletPreferences.setValue(
			"vocabularyIds", String.valueOf(assetVocabulary.getVocabularyId()));

		_portletPreferences.store();
	}

	@Inject(
		filter = "jakarta.portlet.name=" + CategoryFacetPortletKeys.CATEGORY_FACET
	)
	private ExportImportPortletPreferencesProcessor
		_exportImportPortletPreferencesProcessor;

	@DeleteAfterTestRun
	private Group _group;

	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;
	private PortletPreferences _portletPreferences;

}