/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.ImportPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.ImportProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.ImportProcessRequest;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportPreviewResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportProcessResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ImportPreviewResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ImportProcessResource;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Closeable;
import java.io.File;

import java.nio.file.Files;

import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Magdalena Jedraszak
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class ExportImportInstancePerformanceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_exportPreviewResource = ExportPreviewResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();
		_exportProcessResource = ExportProcessResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();
		_importPreviewResource = ImportPreviewResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();
		_importProcessResource = ImportProcessResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Test
	public void testPostExportProcess() throws Exception {
		try (Closeable closeable = new PerformanceTimer(
				_EXPORT_MAX_TIME, "Export a full unscoped instance")) {

			_postExportProcess(_EXPORT_BACKGROUND_TASK_MAX_WAIT_TIME);
		}
	}

	@Test
	public void testPostImportProcess() throws Exception {
		ExportProcess exportProcess = _postExportProcess(
			_EXPORT_BACKGROUND_TASK_MAX_WAIT_TIME);

		File file = FileUtil.createTempFile(
			RandomTestUtil.randomString(), "lar");

		file.deleteOnExit();

		HttpInvoker.HttpResponse httpResponse =
			_exportProcessResource.getExportProcessContentHttpResponse(
				exportProcess.getId());

		Files.write(file.toPath(), httpResponse.getBinaryContent());

		ImportPreview importPreview = _importPreviewResource.postImportPreview(
			null, null, null,
			HashMapBuilder.put(
				"file", file
			).build());

		ImportProcessRequest importProcessRequest = new ImportProcessRequest();

		importProcessRequest.setName(RandomTestUtil.randomString());
		importProcessRequest.setPortletDataHandlers(
			importPreview.getPortletDataHandlers());

		try (Closeable closeable = new PerformanceTimer(
				_IMPORT_MAX_TIME, "Import a full unscoped instance export")) {

			ImportProcess importProcess =
				_importProcessResource.postImportProcess(
					null, null, importProcessRequest);

			ExportImportTestUtil.assertBackgroundTaskSuccessful(
				importProcess.getId(), _IMPORT_BACKGROUND_TASK_MAX_WAIT_TIME,
				TimeUnit.MILLISECONDS);
		}
	}

	private ExportProcess _postExportProcess(long backgroundTaskMaxWaitTime)
		throws Exception {

		ExportPreview exportPreview = _exportPreviewResource.getExportPreview(
			null, null, null, null, null);

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest();

		exportProcessRequest.setName(RandomTestUtil.randomString());
		exportProcessRequest.setPortletDataHandlers(
			exportPreview.getPortletDataHandlers());

		ExportProcess exportProcess = _exportProcessResource.postExportProcess(
			null, null, exportProcessRequest);

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			exportProcess.getId(), backgroundTaskMaxWaitTime,
			TimeUnit.MILLISECONDS);

		return exportProcess;
	}

	private static final long _EXPORT_BACKGROUND_TASK_MAX_WAIT_TIME = 150000;

	private static final long _EXPORT_MAX_TIME = 60000;

	private static final long _IMPORT_BACKGROUND_TASK_MAX_WAIT_TIME = 600000;

	private static final long _IMPORT_MAX_TIME = 400000;

	private static ExportPreviewResource _exportPreviewResource;
	private static ExportProcessResource _exportProcessResource;
	private static ImportPreviewResource _importPreviewResource;
	private static ImportProcessResource _importProcessResource;

}