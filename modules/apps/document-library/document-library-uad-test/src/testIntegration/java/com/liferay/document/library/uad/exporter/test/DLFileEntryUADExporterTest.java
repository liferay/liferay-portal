/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.uad.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.uad.test.util.DLFileEntryUADTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.ExpectedLog;
import com.liferay.portal.test.rule.ExpectedLogs;
import com.liferay.portal.test.rule.ExpectedType;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.user.associated.data.exporter.DynamicQueryUADExporter;
import com.liferay.user.associated.data.exporter.UADExporter;
import com.liferay.user.associated.data.test.util.BaseUADExporterTestCase;

import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class DLFileEntryUADExporterTest
	extends BaseUADExporterTestCase<DLFileEntry> {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_group = GroupTestUtil.addGroup();
	}

	@Override
	@Test
	public void testExportAll() throws Exception {
		addBaseModel(user.getUserId());

		File file = _uadExporter.exportAll(user.getUserId());

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			List<String> entries = zipReader.getEntries();

			Assert.assertEquals(entries.toString(), 2, entries.size());
		}
	}

	@ExpectedLogs(
		expectedLogs = {
			@ExpectedLog(
				expectedLog = "NoSuchFileException",
				expectedType = ExpectedType.CONTAINS
			)
		},
		level = "ERROR", loggerClass = DynamicQueryUADExporter.class
	)
	@Test
	public void testExportAllWithMissingBinary() throws Exception {
		DLFileEntry dlFileEntry = addBaseModel(user.getUserId());

		DLStoreUtil.deleteFile(
			dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
			dlFileEntry.getName());

		File file = _uadExporter.exportAll(user.getUserId());

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			List<String> entries = zipReader.getEntries();

			Assert.assertEquals(entries.toString(), 1, entries.size());
		}
	}

	@Override
	protected DLFileEntry addBaseModel(long userId) throws Exception {
		return DLFileEntryUADTestUtil.addDLFileEntry(
			_dlAppLocalService, _dlFileEntryLocalService, _dlFolderLocalService,
			userId, _group.getGroupId());
	}

	@Override
	protected UADExporter<DLFileEntry> getUADExporter() {
		return _uadExporter;
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.uad.exporter.DLFileEntryUADExporter"
	)
	private UADExporter<DLFileEntry> _uadExporter;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}