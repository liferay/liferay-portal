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
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSection;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSetting;
import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandlerControl;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportPreviewResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportProcessResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ImportPreviewResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ImportProcessResource;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Closeable;
import java.io.File;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
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
	public void testImportFullUnscopedInstanceExport() throws Exception {
		File file = _exportInstanceAsFile();

		try {
			ImportPreview importPreview =
				_importPreviewResource.postImportPreview(
					null, null, null,
					HashMapBuilder.put(
						"file", file
					).build());

			ImportProcessRequest importProcessRequest =
				new ImportProcessRequest();

			importProcessRequest.setName(RandomTestUtil.randomString());
			importProcessRequest.setRequestPortletDataHandlers(
				_toRequestPortletDataHandlers(
					importPreview.getPreviewPortletDataHandlerSections()));

			ImportProcess importProcess;

			try (Closeable closeable = new PerformanceTimer(
					_IMPORT_MAX_TIME,
					"Import a full unscoped instance export")) {

				importProcess = _importProcessResource.postImportProcess(
					null, null, importProcessRequest);

				ExportImportTestUtil.retryAssert(
					1, TimeUnit.SECONDS, 300, TimeUnit.SECONDS,
					() -> {
						BackgroundTask backgroundTask =
							_backgroundTaskLocalService.getBackgroundTask(
								importProcess.getId());

						if (backgroundTask.getStatus() ==
								BackgroundTaskConstants.STATUS_FAILED) {

							throw new IllegalStateException(
								backgroundTask.getStatusMessage());
						}

						Assert.assertEquals(
							BackgroundTaskConstants.STATUS_SUCCESSFUL,
							backgroundTask.getStatus());
					});
			}
		}
		finally {
			file.delete();
		}
	}

	private File _exportInstanceAsFile() throws Exception {
		ExportPreview exportPreview = _exportPreviewResource.getExportPreview(
			null, null, null, null, null);

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest();

		exportProcessRequest.setName(RandomTestUtil.randomString());
		exportProcessRequest.setRequestPortletDataHandlers(
			_toRequestPortletDataHandlers(
				exportPreview.getPreviewPortletDataHandlerSections()));

		ExportProcess exportProcess = _exportProcessResource.postExportProcess(
			null, null, exportProcessRequest);

		File file = File.createTempFile(
			"export-import-instance-performance", ".lar");

		file.deleteOnExit();

		try {
			ExportImportTestUtil.retryAssert(
				1, TimeUnit.SECONDS, 300, TimeUnit.SECONDS,
				() -> {
					BackgroundTask backgroundTask =
						_backgroundTaskLocalService.getBackgroundTask(
							exportProcess.getId());

					if (backgroundTask.getStatus() ==
							BackgroundTaskConstants.STATUS_FAILED) {

						throw new IllegalStateException(
							backgroundTask.getStatusMessage());
					}

					Assert.assertEquals(
						BackgroundTaskConstants.STATUS_SUCCESSFUL,
						backgroundTask.getStatus());
				});

			HttpInvoker.HttpResponse httpResponse =
				_exportProcessResource.getExportProcessContentHttpResponse(
					exportProcess.getId());

			Files.write(file.toPath(), httpResponse.getBinaryContent());

			return file;
		}
		catch (Throwable throwable) {
			file.delete();

			throw throwable;
		}
	}

	private RequestPortletDataHandler _toRequestPortletDataHandler(
		PreviewPortletDataHandler previewPortletDataHandler) {

		RequestPortletDataHandler requestPortletDataHandler =
			new RequestPortletDataHandler();

		requestPortletDataHandler.setName(previewPortletDataHandler.getName());

		requestPortletDataHandler.setRequestPortletDataHandlerControls(
			_toRequestPortletDataHandlerControls(
				previewPortletDataHandler.
					getPreviewPortletDataHandlerControls()));

		return requestPortletDataHandler;
	}

	private RequestPortletDataHandlerControl
		_toRequestPortletDataHandlerControl(
			PreviewPortletDataHandlerControl previewPortletDataHandlerControl) {

		RequestPortletDataHandlerControl requestPortletDataHandlerControl =
			new RequestPortletDataHandlerControl();

		requestPortletDataHandlerControl.setName(
			previewPortletDataHandlerControl.getName());

		if (previewPortletDataHandlerControl instanceof
				PreviewPortletDataHandlerBoolean) {

			PreviewPortletDataHandlerBoolean previewPortletDataHandlerBoolean =
				(PreviewPortletDataHandlerBoolean)
					previewPortletDataHandlerControl;

			Boolean defaultState =
				previewPortletDataHandlerBoolean.getDefaultState();

			if (defaultState != null) {
				requestPortletDataHandlerControl.setValues(
					new String[] {String.valueOf(defaultState)});
			}

			requestPortletDataHandlerControl.
				setRequestPortletDataHandlerControls(
					_toRequestPortletDataHandlerControls(
						previewPortletDataHandlerBoolean.
							getPreviewPortletDataHandlerControls()));
		}
		else if (previewPortletDataHandlerControl instanceof
					PreviewPortletDataHandlerChoice) {

			PreviewPortletDataHandlerChoice previewPortletDataHandlerChoice =
				(PreviewPortletDataHandlerChoice)
					previewPortletDataHandlerControl;

			String defaultChoice =
				previewPortletDataHandlerChoice.getDefaultChoice();

			if (defaultChoice != null) {
				requestPortletDataHandlerControl.setValues(
					new String[] {defaultChoice});
			}
		}

		return requestPortletDataHandlerControl;
	}

	private RequestPortletDataHandlerControl[]
		_toRequestPortletDataHandlerControls(
			PreviewPortletDataHandlerControl[]
				previewPortletDataHandlerControls) {

		if (previewPortletDataHandlerControls == null) {
			return null;
		}

		List<RequestPortletDataHandlerControl>
			requestPortletDataHandlerControls = new ArrayList<>();

		for (PreviewPortletDataHandlerControl previewPortletDataHandlerControl :
				previewPortletDataHandlerControls) {

			if (previewPortletDataHandlerControl instanceof
					PreviewPortletDataHandlerSetting) {

				continue;
			}

			requestPortletDataHandlerControls.add(
				_toRequestPortletDataHandlerControl(
					previewPortletDataHandlerControl));
		}

		if (requestPortletDataHandlerControls.isEmpty()) {
			return null;
		}

		return requestPortletDataHandlerControls.toArray(
			new RequestPortletDataHandlerControl[0]);
	}

	private RequestPortletDataHandler[] _toRequestPortletDataHandlers(
		PreviewPortletDataHandlerSection[] previewPortletDataHandlerSections) {

		List<RequestPortletDataHandler> requestPortletDataHandlers =
			new ArrayList<>();

		for (PreviewPortletDataHandlerSection previewPortletDataHandlerSection :
				previewPortletDataHandlerSections) {

			for (PreviewPortletDataHandler previewPortletDataHandler :
					previewPortletDataHandlerSection.
						getPreviewPortletDataHandlers()) {

				requestPortletDataHandlers.add(
					_toRequestPortletDataHandler(previewPortletDataHandler));
			}
		}

		return requestPortletDataHandlers.toArray(
			new RequestPortletDataHandler[0]);
	}

	private static final long _IMPORT_MAX_TIME = 400000;

	private static ExportPreviewResource _exportPreviewResource;
	private static ExportProcessResource _exportProcessResource;
	private static ImportPreviewResource _importPreviewResource;
	private static ImportProcessResource _importProcessResource;

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

}