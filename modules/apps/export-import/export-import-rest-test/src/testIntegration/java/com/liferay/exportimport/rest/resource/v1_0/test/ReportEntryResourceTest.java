/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.rest.client.dto.v1_0.ReportEntry;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@FeatureFlag("LPD-35914")
@RunWith(Arquillian.class)
public class ReportEntryResourceTest extends BaseReportEntryResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					testGroup.getCreatorUserId(), RandomTestUtil.randomString(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					Collections.emptyMap());

		_backgroundTask = _backgroundTaskLocalService.addBackgroundTask(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			RandomTestUtil.randomString(),
			BackgroundTaskExecutorNames.LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR,
			HashMapBuilder.<String, Serializable>put(
				"exportImportConfigurationId",
				_exportImportConfiguration.getExportImportConfigurationId()
			).build(),
			null);
	}

	@Override
	protected ReportEntry testGetImportProcessErrorsPage_addReportEntry(
			Long importProcessId, ReportEntry reportEntry)
		throws Exception {

		return _addReportEntry(reportEntry);
	}

	@Override
	protected Long testGetImportProcessErrorsPage_getImportProcessId()
		throws Exception {

		return _backgroundTask.getBackgroundTaskId();
	}

	@Override
	protected ReportEntry testGetReportEntry_addReportEntry() throws Exception {
		return _addReportEntry(randomReportEntry());
	}

	private ReportEntry _addReportEntry(ReportEntry reportEntry)
		throws Exception {

		ExportImportReportEntry exportImportReportEntry =
			_exportImportReportEntryLocalService.
				addErrorExportImportReportEntry(
					testGroup.getGroupId(), testCompany.getCompanyId(),
					reportEntry.getClassExternalReferenceCode(),
					reportEntry.getClassNameId(), reportEntry.getClassPK(),
					_exportImportConfiguration.getExportImportConfigurationId(),
					reportEntry.getErrorMessage(),
					reportEntry.getErrorStacktrace(),
					reportEntry.getModelName(),
					ExportImportReportEntryConstants.ORIGIN_BATCH, null,
					testGroup.getGroupKey());

		_exportImportReportEntries.add(exportImportReportEntry);

		return new ReportEntry() {
			{
				setClassExternalReferenceCode(
					exportImportReportEntry.getClassExternalReferenceCode());
				setClassNameId(exportImportReportEntry.getClassNameId());
				setClassPK(exportImportReportEntry.getClassPK());
				setConfigurationId(
					_exportImportConfiguration.
						getExportImportConfigurationId());
				setDateCreated(exportImportReportEntry.getCreateDate());
				setDateModified(exportImportReportEntry.getModifiedDate());
				setErrorMessage(exportImportReportEntry.getErrorMessage());
				setErrorStacktrace(
					exportImportReportEntry.getErrorStacktrace());
				setId(exportImportReportEntry.getExportImportReportEntryId());
				setModelName(exportImportReportEntry.getModelName());
			}
		};
	}

	@DeleteAfterTestRun
	private BackgroundTask _backgroundTask;

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@DeleteAfterTestRun
	private ExportImportConfiguration _exportImportConfiguration;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@DeleteAfterTestRun
	private final List<ExportImportReportEntry> _exportImportReportEntries =
		new ArrayList<>();

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

}