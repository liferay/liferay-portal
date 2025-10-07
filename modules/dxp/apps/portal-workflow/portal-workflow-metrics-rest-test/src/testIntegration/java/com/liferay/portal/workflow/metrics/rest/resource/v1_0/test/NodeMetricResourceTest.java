/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Assignee;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Node;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.NodeMetric;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class NodeMetricResourceTest extends BaseNodeMetricResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_process = _workflowMetricsRESTTestHelper.addProcess(
			testGroup.getCompanyId());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_process != null) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				testGroup.getCompanyId(), _process);
		}

		_deleteTasks();
	}

	@Override
	@Test
	public void testGetProcessNodeMetricsPage() throws Exception {
		super.testGetProcessNodeMetricsPage();

		_deleteTasks();

		_workflowMetricsRESTTestHelper.updateProcess(
			testGroup.getCompanyId(), _process.getId(), "2.0");

		NodeMetric nodeMetric1 = randomNodeMetric();

		nodeMetric1.setBreachedInstanceCount(0L);
		nodeMetric1.setDurationAvg(1000L);
		nodeMetric1.setInstanceCount(1L);
		nodeMetric1.setOnTimeInstanceCount(0L);
		nodeMetric1.setOverdueInstanceCount(0L);

		_addNodeMetric(_process.getId(), "COMPLETED", nodeMetric1, "2.0");

		NodeMetric nodeMetric2 = randomNodeMetric();

		nodeMetric2.setBreachedInstanceCount(0L);
		nodeMetric2.setDurationAvg(2000L);
		nodeMetric2.setInstanceCount(1L);
		nodeMetric2.setOnTimeInstanceCount(0L);
		nodeMetric2.setOverdueInstanceCount(0L);

		_addNodeMetric(_process.getId(), "COMPLETED", nodeMetric2, "2.0");

		Page<NodeMetric> page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null, null, null, Pagination.of(1, 2),
			"durationAvg:asc");

		assertEquals(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric1.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric1.getBreachedInstancePercentage();
						durationAvg = nodeMetric1.getDurationAvg();
						instanceCount = nodeMetric1.getInstanceCount();
						node = nodeMetric1.getNode();
					}
				},
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric2.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric2.getBreachedInstancePercentage();
						durationAvg = nodeMetric2.getDurationAvg();
						instanceCount = nodeMetric2.getInstanceCount();
						node = nodeMetric2.getNode();
					}
				}),
			(List<NodeMetric>)page.getItems());

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null, null, null, Pagination.of(1, 2),
			"overdueInstanceCount:asc");

		assertEqualsIgnoringOrder(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric1.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric1.getBreachedInstancePercentage();
						durationAvg = nodeMetric1.getDurationAvg();
						instanceCount = nodeMetric1.getInstanceCount();
						node = nodeMetric1.getNode();
					}
				},
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric2.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric2.getBreachedInstancePercentage();
						durationAvg = nodeMetric2.getDurationAvg();
						instanceCount = nodeMetric2.getInstanceCount();
						node = nodeMetric2.getNode();
					}
				}),
			(List<NodeMetric>)page.getItems());

		Node node1 = nodeMetric1.getNode();

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null, node1.getName(), null,
			Pagination.of(1, 2), null);

		assertEquals(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric1.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric1.getBreachedInstancePercentage();
						durationAvg = nodeMetric1.getDurationAvg();
						instanceCount = nodeMetric1.getInstanceCount();
						node = nodeMetric1.getNode();
					}
				}),
			(List<NodeMetric>)page.getItems());

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, RandomTestUtil.nextDate(),
			new Date(System.currentTimeMillis() - (2 * Time.MINUTE)), null,
			null, Pagination.of(1, 2), "durationAvg:desc");

		assertEquals(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric2.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric2.getBreachedInstancePercentage();
						durationAvg = nodeMetric2.getDurationAvg();
						instanceCount = nodeMetric2.getInstanceCount();
						node = nodeMetric2.getNode();
					}
				},
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric1.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric1.getBreachedInstancePercentage();
						durationAvg = nodeMetric1.getDurationAvg();
						instanceCount = nodeMetric1.getInstanceCount();
						node = nodeMetric1.getNode();
					}
				}),
			(List<NodeMetric>)page.getItems());

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true,
			new Date(System.currentTimeMillis() - (1 * Time.HOUR)),
			new Date(System.currentTimeMillis() - (2 * Time.HOUR)), null, null,
			Pagination.of(1, 2), "durationAvg:desc");

		Assert.assertEquals(2, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount = 0L;
						durationAvg = 0L;
						instanceCount = 0L;
						node = nodeMetric1.getNode();
					}
				},
				new NodeMetric() {
					{
						breachedInstanceCount = 0L;
						durationAvg = 0L;
						instanceCount = 0L;
						node = nodeMetric2.getNode();
					}
				}),
			(List<NodeMetric>)page.getItems());

		NodeMetric nodeMetric3 = randomNodeMetric();

		nodeMetric3.setBreachedInstanceCount(2L);
		nodeMetric3.setBreachedInstancePercentage(100.0);
		nodeMetric3.setDurationAvg(3000L);
		nodeMetric3.setInstanceCount(2L);
		nodeMetric3.setOnTimeInstanceCount(0L);
		nodeMetric3.setOverdueInstanceCount(2L);

		_addNodeMetric(_process.getId(), "COMPLETED", nodeMetric3, "2.0");

		NodeMetric nodeMetric4 = randomNodeMetric();

		nodeMetric4.setBreachedInstanceCount(1L);
		nodeMetric4.setBreachedInstancePercentage(50.0);
		nodeMetric4.setDurationAvg(4000L);
		nodeMetric4.setInstanceCount(2L);
		nodeMetric4.setOnTimeInstanceCount(1L);
		nodeMetric4.setOverdueInstanceCount(1L);

		_addNodeMetric(_process.getId(), "COMPLETED", nodeMetric4, "2.0");

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null, null, null, Pagination.of(1, 2),
			"breachedInstancePercentage:desc");

		Node node3 = nodeMetric3.getNode();

		Node node4 = nodeMetric4.getNode();

		assertEquals(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric3.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric3.getBreachedInstancePercentage();
						durationAvg = nodeMetric3.getDurationAvg();
						instanceCount = nodeMetric3.getInstanceCount();
						node = new Node() {
							{
								label = node3.getLabel();
								name = node3.getName();
							}
						};
					}
				},
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric4.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric4.getBreachedInstancePercentage();
						durationAvg = nodeMetric4.getDurationAvg();
						instanceCount = nodeMetric4.getInstanceCount();
						node = new Node() {
							{
								label = node4.getLabel();
								name = node4.getName();
							}
						};
					}
				}),
			(List<NodeMetric>)page.getItems());

		_workflowMetricsRESTTestHelper.updateProcess(
			testGroup.getCompanyId(), _process.getId(), "3.0");

		NodeMetric nodeMetric5 = randomNodeMetric();

		nodeMetric5.setBreachedInstanceCount(0L);
		nodeMetric5.setDurationAvg(1000L);
		nodeMetric5.setInstanceCount(1L);
		nodeMetric5.setOnTimeInstanceCount(0L);
		nodeMetric5.setOverdueInstanceCount(0L);

		_addNodeMetric(_process.getId(), "COMPLETED", nodeMetric5, "3.0");

		NodeMetric nodeMetric6 = randomNodeMetric();

		nodeMetric6.setBreachedInstanceCount(0L);
		nodeMetric6.setDurationAvg(1000L);
		nodeMetric6.setInstanceCount(1L);
		nodeMetric6.setOnTimeInstanceCount(0L);
		nodeMetric6.setOverdueInstanceCount(0L);

		_addNodeMetric(_process.getId(), "COMPLETED", nodeMetric6, "3.0");

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null, null, "3.0",
			Pagination.of(1, 2), null);

		Node node5 = nodeMetric5.getNode();
		Node node6 = nodeMetric6.getNode();

		assertEqualsIgnoringOrder(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric5.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric5.getBreachedInstancePercentage();
						durationAvg = nodeMetric5.getDurationAvg();
						instanceCount = nodeMetric5.getInstanceCount();
						node = new Node() {
							{
								label = node5.getLabel();
								name = node5.getName();
							}
						};
					}
				},
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric6.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric6.getBreachedInstancePercentage();
						durationAvg = nodeMetric6.getDurationAvg();
						instanceCount = nodeMetric6.getInstanceCount();
						node = new Node() {
							{
								label = node6.getLabel();
								name = node6.getName();
							}
						};
					}
				}),
			(List<NodeMetric>)page.getItems());

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null, node5.getName(), "3.0",
			Pagination.of(1, 2), null);

		assertEquals(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric5.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric5.getBreachedInstancePercentage();
						durationAvg = nodeMetric5.getDurationAvg();
						instanceCount = nodeMetric5.getInstanceCount();
						node = new Node() {
							{
								label = node5.getLabel();
								name = node5.getName();
							}
						};
					}
				}),
			(List<NodeMetric>)page.getItems());

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null,
			StringUtil.toLowerCase(node5.getName()), "3.0", Pagination.of(1, 2),
			null);

		assertEquals(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric5.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric5.getBreachedInstancePercentage();
						durationAvg = nodeMetric5.getDurationAvg();
						instanceCount = nodeMetric5.getInstanceCount();
						node = new Node() {
							{
								label = node5.getLabel();
								name = node5.getName();
							}
						};
					}
				}),
			(List<NodeMetric>)page.getItems());

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null,
			StringUtil.toUpperCase(node5.getName()), "3.0", Pagination.of(1, 2),
			null);

		assertEquals(
			Arrays.asList(
				new NodeMetric() {
					{
						breachedInstanceCount =
							nodeMetric5.getBreachedInstanceCount();
						breachedInstancePercentage =
							nodeMetric5.getBreachedInstancePercentage();
						durationAvg = nodeMetric5.getDurationAvg();
						instanceCount = nodeMetric5.getInstanceCount();
						node = new Node() {
							{
								label = node5.getLabel();
								name = node5.getName();
							}
						};
					}
				}),
			(List<NodeMetric>)page.getItems());

		_workflowMetricsRESTTestHelper.updateProcess(
			testGroup.getCompanyId(), _process.getId(), "4.0");

		NodeMetric nodeMetric7 = randomNodeMetric();

		nodeMetric7.setBreachedInstanceCount(0L);
		nodeMetric7.setDurationAvg(1000L);
		nodeMetric7.setInstanceCount(1L);
		nodeMetric7.setOnTimeInstanceCount(0L);
		nodeMetric7.setOverdueInstanceCount(0L);

		_addNodeMetric(_process.getId(), nodeMetric7, "4.0");

		page = nodeMetricResource.getProcessNodeMetricsPage(
			_process.getId(), true, null, null, null, "4.0",
			Pagination.of(1, 2), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	@Override
	@Test
	public void testGetProcessNodeMetricsPageWithSortInteger()
		throws Exception {

		testGetProcessNodeMetricsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, nodeMetric1, nodeMetric2) -> {
				nodeMetric1.setInstanceCount(0L);
				nodeMetric1.setOnTimeInstanceCount(0L);
				nodeMetric1.setOverdueInstanceCount(0L);

				nodeMetric2.setInstanceCount(3L);
				nodeMetric2.setOnTimeInstanceCount(1L);
				nodeMetric2.setOverdueInstanceCount(1L);
			});
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Override
	protected boolean equals(NodeMetric nodeMetric1, NodeMetric nodeMetric2) {
		Node node1 = nodeMetric1.getNode();
		Node node2 = nodeMetric2.getNode();

		if (super.equals(nodeMetric1, nodeMetric2) &&
			Objects.equals(node1.getName(), node2.getName())) {

			return true;
		}

		return false;
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"durationAvg", "instanceCount", "onTimeInstanceCount",
			"overdueInstanceCount"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"durationAvg"};
	}

	@Override
	protected NodeMetric randomNodeMetric() throws Exception {
		return new NodeMetric() {
			{
				breachedInstanceCount = 0L;
				breachedInstancePercentage = 0.0;
				durationAvg = 0L;

				instanceCount = (long)RandomTestUtil.randomInt(0, 20);

				String randomString = RandomTestUtil.randomString();

				node = new Node() {
					{
						id = RandomTestUtil.randomLong();
						label = randomString;
						name = randomString;
					}
				};

				onTimeInstanceCount = (long)RandomTestUtil.randomInt(
					0, instanceCount.intValue());

				overdueInstanceCount = (long)RandomTestUtil.randomInt(
					0,
					instanceCount.intValue() - onTimeInstanceCount.intValue());
			}
		};
	}

	@Override
	protected NodeMetric testGetProcessNodeMetricsPage_addNodeMetric(
			Long processId, NodeMetric nodeMetric)
		throws Exception {

		return _addNodeMetric(processId, nodeMetric, "1.0");
	}

	@Override
	protected Long testGetProcessNodeMetricsPage_getProcessId()
		throws Exception {

		return _process.getId();
	}

	private NodeMetric _addNodeMetric(
			Long processId, NodeMetric nodeMetric, String version)
		throws Exception {

		return _addNodeMetric(processId, "RUNNING", nodeMetric, version);
	}

	private NodeMetric _addNodeMetric(
			Long processId, String status, NodeMetric nodeMetric,
			String version)
		throws Exception {

		User adminUser = UserTestUtil.getAdminUser(testGroup.getCompanyId());

		return _workflowMetricsRESTTestHelper.addNodeMetric(
			new Assignee() {
				{
					id = adminUser.getUserId();
				}
			},
			testGroup.getCompanyId(),
			() -> _workflowMetricsRESTTestHelper.addInstance(
				testGroup.getCompanyId(), Objects.equals(status, "COMPLETED"),
				processId),
			nodeMetric, processId, status, TestPropsValues.getUser(), version);
	}

	private void _deleteTasks() throws Exception {
		_workflowMetricsRESTTestHelper.deleteTasks(
			testGroup.getCompanyId(), _process.getId());
	}

	private Process _process;

	@Inject
	private WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

}