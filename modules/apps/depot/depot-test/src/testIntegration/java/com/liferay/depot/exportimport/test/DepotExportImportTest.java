/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;

import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime Leon
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class DepotExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotGroup = _depotEntry.getGroup();

		_siteGroup = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		if ((_larFile != null) && _larFile.exists()) {
			FileUtil.delete(_larFile);
		}
	}

	@Test
	public void testExportImportDepotWithDocumentToSite() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			_depotGroup.getGroupId());

		_larFile = _export(_depotGroup.getGroupId());

		_import(_siteGroup.getGroupId(), _larFile);

		FileEntry importedFileEntry =
			DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), _siteGroup.getGroupId());

		Assert.assertEquals(fileEntry.getTitle(), importedFileEntry.getTitle());
	}

	@Test
	public void testExportImportDepotWithWebContentToSite() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_depotGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_larFile = _export(_depotGroup.getGroupId());

		_import(_siteGroup.getGroupId(), _larFile);

		JournalArticle importedJournalArticle =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), _siteGroup.getGroupId());

		Assert.assertEquals(
			journalArticle.getTitle(), importedJournalArticle.getTitle());
	}

	@Test
	public void testExportImportSiteWithDocumentToDepot() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			_siteGroup.getGroupId());

		_larFile = _export(_siteGroup.getGroupId());

		_import(_depotGroup.getGroupId(), _larFile);

		FileEntry importedFileEntry =
			DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), _depotGroup.getGroupId());

		Assert.assertEquals(fileEntry.getTitle(), importedFileEntry.getTitle());
	}

	@Test
	public void testExportImportSiteWithWebContentToDepot() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_siteGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_larFile = _export(_siteGroup.getGroupId());

		_import(_depotGroup.getGroupId(), _larFile);

		JournalArticle importedJournalArticle =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				journalArticle.getUuid(), _depotGroup.getGroupId());

		Assert.assertEquals(
			journalArticle.getTitle(), importedJournalArticle.getTitle());
	}

	private File _export(long groupId) throws Exception {
		return _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false,
							new long[0], _getParameterMap())));
	}

	private Map<String, String[]> _getParameterMap() {
		return HashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE}
		).put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	private void _import(long groupId, File larFile) throws Exception {
		_exportImportLocalService.importLayouts(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, false, null,
							_getParameterMap())),
			larFile);
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _depotGroup;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	private File _larFile;

	@DeleteAfterTestRun
	private Group _siteGroup;

}