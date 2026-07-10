/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.unit.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ZipFileTestUtil;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class BatchEngineUnitProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_bundle = FrameworkUtil.getBundle(BatchEngineUnitProcessorTest.class);

		_bundleContext = _bundle.getBundleContext();
	}

	@Test
	public void testProcessBatchEngineUnitsAbortsRemainingUnitsAfterImportFailure()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			try {
				_processBatchEngineUnits("abort");

				Assert.fail();
			}
			catch (Exception exception) {
				String message = exception.getMessage();

				Assert.assertTrue(
					message,
					message.contains("Unable to deploy batch engine file"));
				Assert.assertTrue(
					message,
					message.contains(
						"01-invalid-list-type-definition.batch-engine-data." +
							"json"));
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry1 = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.ERROR, logEntry1.getPriority());

			String logEntryMessage1 = logEntry1.getMessage();

			Assert.assertTrue(
				logEntryMessage1, logEntryMessage1.contains("Name is null"));

			LogEntry logEntry2 = logEntries.get(1);

			Assert.assertEquals(LoggerTestUtil.ERROR, logEntry2.getPriority());

			String logEntryMessage2 = logEntry2.getMessage();

			Assert.assertTrue(
				logEntryMessage2,
				logEntryMessage2.startsWith(
					"Unable to update batch engine import task"));
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			Assert.assertNull(
				_listTypeDefinitionLocalService.
					fetchListTypeDefinitionByExternalReferenceCode(
						"TEST_ABORT_INVALID",
						PortalInstancePool.getDefaultCompanyId()));
			Assert.assertNull(
				_listTypeDefinitionLocalService.
					fetchListTypeDefinitionByExternalReferenceCode(
						"TEST_ABORT_VALID",
						PortalInstancePool.getDefaultCompanyId()));
		}
	}

	@Test
	public void testProcessBatchEngineUnitsProcessesAllValidUnits()
		throws Exception {

		_processBatchEngineUnits("control");

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			ListTypeDefinition listTypeDefinition1 = null;
			ListTypeDefinition listTypeDefinition2 = null;

			try {
				listTypeDefinition1 =
					_listTypeDefinitionLocalService.
						fetchListTypeDefinitionByExternalReferenceCode(
							"TEST_CONTROL_VALID_1",
							PortalInstancePool.getDefaultCompanyId());

				Assert.assertNotNull(listTypeDefinition1);

				listTypeDefinition2 =
					_listTypeDefinitionLocalService.
						fetchListTypeDefinitionByExternalReferenceCode(
							"TEST_CONTROL_VALID_2",
							PortalInstancePool.getDefaultCompanyId());

				Assert.assertNotNull(listTypeDefinition2);
			}
			finally {
				if (listTypeDefinition1 != null) {
					_listTypeDefinitionLocalService.deleteListTypeDefinition(
						listTypeDefinition1);
				}

				if (listTypeDefinition2 != null) {
					_listTypeDefinitionLocalService.deleteListTypeDefinition(
						listTypeDefinition2);
				}
			}
		}
	}

	private void _processBatchEngineUnits(String dirName) throws Exception {
		Bundle bundle = _bundleContext.installBundle(
			RandomTestUtil.randomString(), _toInputStream(dirName));

		try {
			_batchEngineUnitProcessor.processBatchEngineUnits(
				_batchEngineUnitReader.getBatchEngineUnits(bundle));
		}
		finally {
			bundle.uninstall();
		}
	}

	private InputStream _toInputStream(String dirName) throws Exception {
		String basePath = StringBundler.concat(
			"com/liferay/batch/engine/internal/unit/test/dependencies/",
			dirName, StringPool.SLASH);

		return ZipFileTestUtil.toInputStream(
			basePath, _bundle, _zipWriterFactory.getZipWriter());
	}

	private static final String
		_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL =
			"com.liferay.batch.engine.internal." +
				"BatchEngineImportTaskExecutorImpl";

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	private Bundle _bundle;
	private BundleContext _bundleContext;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}