/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.notification.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.kaleo.definition.Task;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationMessageGenerationException;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationMessageGenerator;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;

import java.io.Serializable;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Anderson Luiz
 */
@RunWith(Arquillian.class)
public class TemplateNotificationMessageGeneratorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		_kaleoInstance = _kaleoInstanceLocalService.addKaleoInstance(
			1, 1, RandomTestUtil.randomString(), 1,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
				BlogsEntry.class.getName()
			).put(
				WorkflowConstants.CONTEXT_SERVICE_CONTEXT, serviceContext
			).build(),
			serviceContext);

		KaleoNode kaleoNode = _kaleoNodeLocalService.addKaleoNode(
			_kaleoInstance.getKaleoDefinitionId(),
			_kaleoInstance.getKaleoDefinitionVersionId(),
			new Task(RandomTestUtil.randomString(), StringPool.BLANK),
			serviceContext);

		_kaleoInstanceToken =
			_kaleoInstanceTokenLocalService.addKaleoInstanceToken(
				kaleoNode.getKaleoNodeId(),
				_kaleoInstance.getKaleoDefinitionId(),
				_kaleoInstance.getKaleoDefinitionVersionId(),
				_kaleoInstance.getKaleoInstanceId(), 0,
				WorkflowContextUtil.convert(
					_kaleoInstance.getWorkflowContext()),
				serviceContext);
	}

	@Test
	public void testGenerateMessage() throws Exception {
		String notificationTemplate =
			"${serviceLocator.findService(\"com.liferay.portal.kernel." +
				"service.CompanyLocalService\").getCompanyByWebId(" +
					"\"liferay.com\").getName()}";

		AssertUtils.assertFailure(
			NotificationMessageGenerationException.class,
			"Unable to generate notification message",
			() -> _notificationMessageGenerator.generateMessage(
				KaleoNode.class.getName(), RandomTestUtil.randomLong(),
				RandomTestUtil.randomString(), "freemarker",
				notificationTemplate,
				new ExecutionContext(
					_kaleoInstanceToken,
					WorkflowContextUtil.convert(
						_kaleoInstance.getWorkflowContext()),
					ServiceContextTestUtil.getServiceContext())));
	}

	private KaleoInstance _kaleoInstance;

	@Inject
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	private KaleoInstanceToken _kaleoInstanceToken;

	@Inject
	private KaleoInstanceTokenLocalService _kaleoInstanceTokenLocalService;

	@Inject
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Inject
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Inject(
		filter = "component.name=com.liferay.portal.workflow.kaleo.runtime.internal.notification.TemplateNotificationMessageGenerator"
	)
	private NotificationMessageGenerator _notificationMessageGenerator;

}