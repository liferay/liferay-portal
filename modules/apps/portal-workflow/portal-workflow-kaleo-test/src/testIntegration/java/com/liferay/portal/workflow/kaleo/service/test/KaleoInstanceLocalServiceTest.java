/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.portal.kernel.exception.NoSuchWorkflowInstanceLinkException;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.FutureTask;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mateus Xavier
 */
@RunWith(Arquillian.class)
public class KaleoInstanceLocalServiceTest
	extends BaseKaleoLocalServiceTestCase {

	@Test
	public void testDeleteKaleoInstance() throws Exception {
		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			BlogsEntry.class.getName(), 0, 0, "Single Approver", 1);

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				blogsEntry.getCompanyId(), blogsEntry.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

		_kaleoInstanceLocalService.deleteKaleoInstance(
			workflowInstanceLink.getWorkflowInstanceId());

		Assert.assertThrows(
			NoSuchWorkflowInstanceLinkException.class,
			() -> _workflowInstanceLinkLocalService.getWorkflowInstanceLink(
				blogsEntry.getCompanyId(), blogsEntry.getGroupId(),
				BlogsEntry.class.getName(), blogsEntry.getEntryId()));
	}

	@Test
	public void testUpdateKaleoInstance() throws Exception {
		int parties = 5;

		CyclicBarrier cyclicBarrier = new CyclicBarrier(parties);

		List<FutureTask<KaleoInstance>> futureTasks = new ArrayList<>();

		KaleoInstance kaleoInstance = addKaleoInstance();

		long kaleoInstanceId = kaleoInstance.getKaleoInstanceId();

		for (int i = 0; i < parties; i++) {
			FutureTask<KaleoInstance> futureTask = new FutureTask<>(
				new CompanyInheritableThreadLocalCallable<>(
					() -> {
						cyclicBarrier.await();

						return _kaleoInstanceLocalService.updateKaleoInstance(
							kaleoInstanceId,
							HashMapBuilder.<String, Serializable>put(
								RandomTestUtil.randomString(),
								RandomTestUtil.randomString()
							).build());
					}));

			futureTasks.add(futureTask);
		}

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.engine.jdbc.batch.internal.BatchingBatch",
				LoggerTestUtil.ERROR);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.workflow.metrics.internal.petra.executor." +
					"WorkflowMetricsPortalExecutor",
				LoggerTestUtil.ERROR)) {

			for (FutureTask<KaleoInstance> futureTask : futureTasks) {
				Thread thread = new Thread(futureTask);

				thread.start();
			}

			for (FutureTask<KaleoInstance> futureTask : futureTasks) {
				futureTask.get();
			}
		}

		kaleoInstance = _kaleoInstanceLocalService.getKaleoInstance(
			kaleoInstanceId);

		Assert.assertEquals(parties, kaleoInstance.getMvccVersion());
	}

	@Inject
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}