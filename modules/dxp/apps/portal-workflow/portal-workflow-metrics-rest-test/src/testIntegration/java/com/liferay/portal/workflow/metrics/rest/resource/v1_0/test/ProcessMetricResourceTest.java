/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.ProcessMetric;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ProcessMetricResourceTest
	extends BaseProcessMetricResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseProcessMetricResourceTestCase.setUpClass();

		_documents = _workflowMetricsRESTTestHelper.getDocuments(
			TestPropsValues.getCompanyId());

		for (Document document : _documents) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				document.getLong("companyId"), document.getLong("processId"));
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		for (Document document : _documents) {
			_workflowMetricsRESTTestHelper.restoreProcess(document);
		}
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_deleteProcesses();
	}

	@Override
	@Test
	public void testGetProcessMetric() throws Exception {
		_testGetProcessMetric(
			true,
			(processMetric1, processMetric2) -> assertEquals(
				processMetric1, processMetric2));
		_testGetProcessMetric(
			false,
			(processMetric1, processMetric2) -> assertEquals(
				processMetric1, processMetric2));
	}

	@Override
	@Test
	public void testGetProcessMetricsPage() throws Exception {
		super.testGetProcessMetricsPage();

		_deleteProcesses();

		ProcessMetric processMetric = randomProcessMetric();

		testGetProcessMetricsPage_addProcessMetric(processMetric);

		testGetProcessMetricsPage_addProcessMetric(randomProcessMetric());

		Process process = processMetric.getProcess();

		Page<ProcessMetric> page = processMetricResource.getProcessMetricsPage(
			process.getTitle(), Pagination.of(1, 2), null);

		assertEquals(
			Collections.singletonList(processMetric),
			(List<ProcessMetric>)page.getItems());
	}

	@Override
	@Test
	public void testGetProcessMetricsPageWithSortInteger() throws Exception {
		testGetProcessMetricsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, processMetric1, processMetric2) -> {
				processMetric1.setInstanceCount(0L);
				processMetric1.setOnTimeInstanceCount(0L);
				processMetric1.setOverdueInstanceCount(0L);
				processMetric1.setUntrackedInstanceCount(0L);

				processMetric2.setInstanceCount(3L);
				processMetric2.setOnTimeInstanceCount(1L);
				processMetric2.setOverdueInstanceCount(1L);
				processMetric2.setUntrackedInstanceCount(1L);
			});
	}

	@Override
	@Test
	public void testGetProcessMetricsPageWithSortString() throws Exception {
		testGetProcessMetricsPageWithSort(
			EntityField.Type.STRING,
			(entityField, processMetric1, processMetric2) -> {
				if (Objects.equals(entityField.getName(), "title")) {
					Process process1 = processMetric1.getProcess();

					process1.setTitle("Aaa" + RandomTestUtil.randomString());
					process1.setTitle_i18n(
						HashMapBuilder.put(
							LocaleUtil.US.toLanguageTag(), process1.getTitle()
						).build());

					Process process2 = processMetric2.getProcess();

					process2.setTitle("Bbb" + RandomTestUtil.randomString());
					process2.setTitle_i18n(
						HashMapBuilder.put(
							LocaleUtil.US.toLanguageTag(), process2.getTitle()
						).build());
				}
				else {
					BeanTestUtil.setProperty(
						processMetric1, entityField.getName(),
						"Aaa" + RandomTestUtil.randomString());
					BeanTestUtil.setProperty(
						processMetric2, entityField.getName(),
						"Bbb" + RandomTestUtil.randomString());
				}
			});
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProcessMetric() throws Exception {
		super.testGraphQLGetProcessMetric();
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"instanceCount", "process", "onTimeInstanceCount",
			"overdueInstanceCount", "untrackedInstanceCount"
		};
	}

	@Override
	protected ProcessMetric randomProcessMetric() throws Exception {
		return new ProcessMetric() {
			{
				instanceCount = (long)RandomTestUtil.randomInt(0, 20);

				onTimeInstanceCount = (long)RandomTestUtil.randomInt(
					0, instanceCount.intValue());

				overdueInstanceCount = (long)RandomTestUtil.randomInt(
					0,
					instanceCount.intValue() - onTimeInstanceCount.intValue());

				process = new Process() {
					{
						active = true;
						dateCreated = RandomTestUtil.nextDate();
						dateModified = RandomTestUtil.nextDate();
						id = RandomTestUtil.randomLong();

						title = RandomTestUtil.randomString();

						title_i18n = HashMapBuilder.put(
							LocaleUtil.US.toLanguageTag(), title
						).build();
					}
				};
				untrackedInstanceCount =
					instanceCount - onTimeInstanceCount - overdueInstanceCount;
			}
		};
	}

	@Override
	protected ProcessMetric testGetProcessMetricsPage_addProcessMetric(
			ProcessMetric processMetric)
		throws Exception {

		processMetric = _workflowMetricsRESTTestHelper.addProcessMetric(
			testGroup.getCompanyId(), processMetric);

		_processes.add(processMetric.getProcess());

		return processMetric;
	}

	private void _deleteProcesses() throws Exception {
		for (Process process : _processes) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				testGroup.getCompanyId(), process);
		}
	}

	private void _testGetProcessMetric(
			Boolean completed,
			UnsafeBiConsumer<ProcessMetric, ProcessMetric, Exception>
				unsafeBiConsumer)
		throws Exception {

		_deleteProcesses();

		ProcessMetric postProcessMetric = randomProcessMetric();

		postProcessMetric.setInstanceCount(0L);
		postProcessMetric.setOnTimeInstanceCount(0L);
		postProcessMetric.setOverdueInstanceCount(0L);
		postProcessMetric.setUntrackedInstanceCount(0L);

		testGetProcessMetricsPage_addProcessMetric(postProcessMetric);

		Process postProcess = postProcessMetric.getProcess();

		Instance instance = _workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), completed, postProcess.getId());

		if (completed) {
			_workflowMetricsRESTTestHelper.completeInstance(
				testGroup.getCompanyId(), instance);
		}

		postProcessMetric.setInstanceCount(1L);
		postProcessMetric.setOnTimeInstanceCount(0L);
		postProcessMetric.setOverdueInstanceCount(0L);
		postProcessMetric.setUntrackedInstanceCount(1L);

		ProcessMetric getProcessMetric = processMetricResource.getProcessMetric(
			postProcess.getId(), completed, null, null);

		unsafeBiConsumer.accept(postProcessMetric, getProcessMetric);
	}

	private static Document[] _documents;

	@Inject
	private static WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

	private final List<Process> _processes = new ArrayList<>();

}