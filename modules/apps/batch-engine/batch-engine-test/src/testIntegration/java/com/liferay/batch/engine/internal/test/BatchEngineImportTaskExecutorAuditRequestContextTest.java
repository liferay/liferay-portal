/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Álvaro Saugar
 */
@RunWith(Arquillian.class)
public class BatchEngineImportTaskExecutorAuditRequestContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlag("LPD-6417")
	@Test
	public void testExecute() throws Exception {
		AuditRequestContext auditRequestContext =
			AuditRequestContextThreadLocal.getAuditRequestContext();

		String requestId = auditRequestContext.getRequestId();

		Bundle bundle = FrameworkUtil.getBundle(
			BatchEngineImportTaskExecutorAuditRequestContextTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<AuditMessage> auditMessages = new CopyOnWriteArrayList<>();

		ServiceRegistration<AuditMessageProcessor> serviceRegistration =
			bundleContext.registerService(
				AuditMessageProcessor.class, auditMessages::add,
				HashMapDictionaryBuilder.<String, Object>put(
					"eventTypes", EventTypes.ADD
				).build());

		try {
			_importUserAccount();
		}
		finally {
			serviceRegistration.unregister();

			AuditRequestContextThreadLocal.removeAuditRequestContext();
		}

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.COMPLETED.toString(),
			_batchEngineImportTask.getExecuteStatus());

		AuditMessage auditMessage = _getUserAuditMessage(auditMessages);

		Assert.assertEquals(requestId, auditMessage.getRequestId());
		Assert.assertTrue(auditMessage.isRequestIdGenerated());
	}

	private byte[] _getContent(String json) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				unsyncByteArrayOutputStream)) {

			zipOutputStream.putNextEntry(new ZipEntry("import.json"));

			zipOutputStream.write(json.getBytes(StandardCharsets.UTF_8));

			zipOutputStream.closeEntry();
		}

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private AuditMessage _getUserAuditMessage(
		List<AuditMessage> auditMessages) {

		for (AuditMessage auditMessage : auditMessages) {
			String className = auditMessage.getClassName();

			if (className.equals(User.class.getName())) {
				return auditMessage;
			}
		}

		return null;
	}

	private void _importUserAccount() throws Exception {
		_batchEngineImportTask =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), 10, null,
				"com.liferay.headless.admin.user.dto.v1_0.UserAccount",
				_getContent(
					JSONUtil.putAll(
						JSONUtil.put(
							"alternateName", RandomTestUtil.randomString()
						).put(
							"emailAddress",
							RandomTestUtil.randomString() + "@liferay.com"
						).put(
							"familyName", RandomTestUtil.randomString()
						).put(
							"givenName", RandomTestUtil.randomString()
						)
					).toString()),
				"JSON", BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null);

		_batchEngineImportTaskExecutor.execute(_batchEngineImportTask);

		_batchEngineImportTask =
			_batchEngineImportTaskLocalService.getBatchEngineImportTask(
				_batchEngineImportTask.getBatchEngineImportTaskId());
	}

	@DeleteAfterTestRun
	private BatchEngineImportTask _batchEngineImportTask;

	@Inject
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

}