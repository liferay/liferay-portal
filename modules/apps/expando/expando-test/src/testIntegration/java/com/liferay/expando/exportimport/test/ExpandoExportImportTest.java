/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.expando.constants.ExpandoPortletKeys;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.adapter.StagedExpandoColumn;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.exportimport.staged.model.repository.StagedModelRepositoryRegistryUtil;
import com.liferay.exportimport.test.util.lar.BaseExportImportTestCase;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime León
 */
@RunWith(Arquillian.class)
@Sync
public class ExpandoExportImportTest extends BaseExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_stagedModelRepository =
			(StagedModelRepository<StagedExpandoColumn>)
				StagedModelRepositoryRegistryUtil.getStagedModelRepository(
					StagedExpandoColumn.class.getName());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		for (File larFile : _larFiles) {
			FileUtil.delete(larFile);
		}

		if (_expandoTable != null) {
			ExpandoColumn expandoColumn =
				ExpandoColumnLocalServiceUtil.fetchColumn(
					_expandoTable.getTableId(), _expandoColumnName);

			if (expandoColumn != null) {
				ExpandoColumnLocalServiceUtil.deleteColumn(expandoColumn);
			}

			if (_addedExpandoTable != null) {
				ExpandoTableLocalServiceUtil.deleteExpandoTable(
					_addedExpandoTable);
			}
		}

		super.tearDown();
	}

	@Test
	@TestInfo("LRQA-73362")
	public void testExportImportExpandoColumnDeletions() throws Exception {
		_expandoTable = _getDLFileEntryDefaultExpandoTable();

		_expandoColumnName = RandomTestUtil.randomString();

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			_expandoTable, _expandoColumnName, ExpandoColumnConstants.STRING);

		StagedExpandoColumn stagedExpandoColumn = ModelAdapterUtil.adapt(
			expandoColumn, ExpandoColumn.class, StagedExpandoColumn.class);

		String uuid = stagedExpandoColumn.getUuid();

		File expandoColumnLarFile = _exportPortlet(new LinkedHashMap<>());

		_stagedModelRepository.deleteStagedModel(stagedExpandoColumn);

		File deletionsLarFile = _exportPortlet(
			LinkedHashMapBuilder.put(
				PortletDataHandlerKeys.DELETIONS,
				new String[] {Boolean.TRUE.toString()}
			).build());

		_importPortlet(expandoColumnLarFile, new LinkedHashMap<>());

		List<StagedExpandoColumn> stagedExpandoColumns =
			_fetchStagedExpandoColumns(uuid);

		Assert.assertFalse(
			stagedExpandoColumns.toString(), stagedExpandoColumns.isEmpty());

		_importPortlet(
			deletionsLarFile,
			LinkedHashMapBuilder.put(
				PortletDataHandlerKeys.DELETIONS,
				new String[] {Boolean.TRUE.toString()}
			).build());

		stagedExpandoColumns = _fetchStagedExpandoColumns(uuid);

		Assert.assertTrue(
			stagedExpandoColumns.toString(), stagedExpandoColumns.isEmpty());
	}

	private File _exportPortlet(Map<String, String[]> parameterMap)
		throws Exception {

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		MapUtil.merge(parameterMap, exportParameterMap);

		User user = TestPropsValues.getUser();

		Map<String, Serializable> settingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportPortletSettingsMap(
					user, layout.getPlid(), layout.getGroupId(),
					ExpandoPortletKeys.EXPANDO, exportParameterMap,
					StringPool.BLANK);

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_LOCAL,
					settingsMap);

		File file = ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			exportImportConfiguration);

		_larFiles.add(file);

		return file;
	}

	private List<StagedExpandoColumn> _fetchStagedExpandoColumns(String uuid)
		throws Exception {

		return _stagedModelRepository.fetchStagedModelsByUuidAndCompanyId(
			uuid, TestPropsValues.getCompanyId());
	}

	private ExpandoTable _getDLFileEntryDefaultExpandoTable() throws Exception {
		ExpandoTable expandoTable =
			ExpandoTableLocalServiceUtil.fetchDefaultTable(
				TestPropsValues.getCompanyId(), DLFileEntry.class.getName());

		if (expandoTable != null) {
			return expandoTable;
		}

		_addedExpandoTable = ExpandoTableLocalServiceUtil.addDefaultTable(
			TestPropsValues.getCompanyId(), DLFileEntry.class.getName());

		return _addedExpandoTable;
	}

	private void _importPortlet(File file, Map<String, String[]> parameterMap)
		throws Exception {

		Map<String, String[]> importParameterMap = getImportParameterMap();

		MapUtil.merge(parameterMap, importParameterMap);

		User user = TestPropsValues.getUser();

		importedLayout = LayoutTestUtil.addTypePortletLayout(importedGroup);

		Map<String, Serializable> settingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildImportPortletSettingsMap(
					user, importedLayout.getPlid(), importedGroup.getGroupId(),
					ExpandoPortletKeys.EXPANDO, importParameterMap);

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_LOCAL,
					settingsMap);

		ExportImportLocalServiceUtil.importPortletDataDeletions(
			exportImportConfiguration, file);

		ExportImportLocalServiceUtil.importPortletInfo(
			exportImportConfiguration, file);
	}

	private ExpandoTable _addedExpandoTable;
	private String _expandoColumnName;
	private ExpandoTable _expandoTable;
	private final List<File> _larFiles = new ArrayList<>();
	private StagedModelRepository<StagedExpandoColumn> _stagedModelRepository;

}