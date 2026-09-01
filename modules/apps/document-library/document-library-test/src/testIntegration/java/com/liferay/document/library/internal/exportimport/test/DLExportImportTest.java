/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.test.util.lar.BaseExportImportTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import java.util.Collections;
import java.util.Map;

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
public class DLExportImportTest extends BaseExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		super.setUp();
	}

	@Test
	@TestInfo("LPS-50760")
	public void testExportFileEntryExceedingFileMaxSize() throws Exception {
		DLAppTestUtil.addFileEntry(group.getGroupId());

		_dlSizeLimitConfigurationProvider.updateGroupSizeLimit(
			group.getGroupId(), 1, 0, Collections.emptyMap());

		try {
			larFile = _exportPortletInfo();

			Assert.assertTrue(larFile.length() > 0);
		}
		finally {
			_dlSizeLimitConfigurationProvider.updateGroupSizeLimit(
				group.getGroupId(), 0, 0, Collections.emptyMap());
		}
	}

	@Test
	@TestInfo("LPS-141587")
	public void testImportFileEntriesWithDeletePortletData() throws Exception {
		FileEntry fileEntry1 = DLAppTestUtil.addFileEntry(group.getGroupId());
		FileEntry fileEntry2 = DLAppTestUtil.addFileEntry(group.getGroupId());

		larFile = _exportPortletInfo();

		_importPortletInfo();

		Assert.assertEquals(
			2,
			_dlFileEntryLocalService.getFileEntriesCount(
				group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID));

		FileEntry importedFileEntry1 =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry1.getUuid(), group.getGroupId());

		Assert.assertEquals(fileEntry1.getSize(), importedFileEntry1.getSize());

		FileEntry importedFileEntry2 =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry2.getUuid(), group.getGroupId());

		Assert.assertEquals(fileEntry2.getSize(), importedFileEntry2.getSize());
	}

	private File _exportPortletInfo() throws Exception {
		return ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportPortletSettingsMap(
							TestPropsValues.getUser(), layout.getPlid(),
							group.getGroupId(), DLPortletKeys.DOCUMENT_LIBRARY,
							getExportParameterMap(), StringPool.BLANK)));
	}

	private void _importPortletInfo() throws Exception {
		Map<String, String[]> importParameterMap = getImportParameterMap();

		importParameterMap.put(
			PortletDataHandlerKeys.DELETE_PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});

		ExportImportLocalServiceUtil.importPortletInfo(
			ExportImportConfigurationLocalServiceUtil.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportPortletSettingsMap(
							TestPropsValues.getUser(), layout.getPlid(),
							group.getGroupId(), DLPortletKeys.DOCUMENT_LIBRARY,
							importParameterMap)),
			larFile);
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLSizeLimitConfigurationProvider _dlSizeLimitConfigurationProvider;

}