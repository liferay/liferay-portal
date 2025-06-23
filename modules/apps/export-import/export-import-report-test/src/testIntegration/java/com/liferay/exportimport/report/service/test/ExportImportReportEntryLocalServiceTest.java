/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.report.service.persistence.ExportImportReportEntryPersistence;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class ExportImportReportEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.exportimport.report.service"));

	@Test
	public void testAddErrorExportImportReportEntry() throws Exception {
		int count = _exportImportReportEntryPersistence.countAll();

		long groupId = RandomTestUtil.randomLong();
		long companyId = RandomTestUtil.randomLong();
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();
		String error = RandomTestUtil.randomString();
		String errorStacktrace = RandomTestUtil.randomString();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.
				addErrorExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					exportImportConfigurationId, error, errorStacktrace);

		Assert.assertEquals(groupId, exportImportReportEntry.getGroupId());
		Assert.assertEquals(companyId, exportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			classExternalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			classNameId, exportImportReportEntry.getClassNameId());
		Assert.assertEquals(
			exportImportConfigurationId,
			exportImportReportEntry.getExportImportConfigurationId());
		Assert.assertEquals(error, exportImportReportEntry.getError());
		Assert.assertEquals(
			errorStacktrace, exportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_ERROR,
			exportImportReportEntry.getType());

		Assert.assertEquals(
			count + 1, _exportImportReportEntryPersistence.countAll());
	}

	@Test
	public void testAddIncompleteExportImportReportEntry() throws Exception {
		int count = _exportImportReportEntryPersistence.countAll();

		long groupId = RandomTestUtil.randomLong();
		long companyId = RandomTestUtil.randomLong();
		String classExternalReferenceCode = RandomTestUtil.randomString();
		long classNameId = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.
				addIncompleteExportImportReportEntry(
					groupId, companyId, classExternalReferenceCode, classNameId,
					exportImportConfigurationId);

		Assert.assertEquals(groupId, exportImportReportEntry.getGroupId());
		Assert.assertEquals(companyId, exportImportReportEntry.getCompanyId());
		Assert.assertEquals(
			classExternalReferenceCode,
			exportImportReportEntry.getClassExternalReferenceCode());
		Assert.assertEquals(
			classNameId, exportImportReportEntry.getClassNameId());
		Assert.assertEquals(
			exportImportConfigurationId,
			exportImportReportEntry.getExportImportConfigurationId());
		Assert.assertNull(exportImportReportEntry.getError());
		Assert.assertNull(exportImportReportEntry.getErrorStacktrace());
		Assert.assertEquals(
			ExportImportReportEntryConstants.TYPE_INCOMPLETE,
			exportImportReportEntry.getType());

		Assert.assertEquals(
			count + 1, _exportImportReportEntryPersistence.countAll());
	}

	@Test
	public void testGetImportReportEntries() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		long exportImportConfigurationId = RandomTestUtil.randomLong();

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				companyId, exportImportConfigurationId);

		Assert.assertTrue(exportImportReportEntries.isEmpty());

		_exportImportReportEntryLocalService.
			addIncompleteExportImportReportEntry(
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong());
		_exportImportReportEntryLocalService.
			addIncompleteExportImportReportEntry(
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				exportImportConfigurationId);
		_exportImportReportEntryLocalService.
			addIncompleteExportImportReportEntry(
				RandomTestUtil.randomLong(), companyId,
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong());
		_exportImportReportEntryLocalService.
			addIncompleteExportImportReportEntry(
				RandomTestUtil.randomLong(), companyId,
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				exportImportConfigurationId);

		exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				companyId, exportImportConfigurationId);

		Assert.assertTrue(exportImportReportEntries.size() == 1);
	}

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private ExportImportReportEntryPersistence
		_exportImportReportEntryPersistence;

}