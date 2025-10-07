/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.SLAResult;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.SLAResultSerDes;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper.WorkflowMetricsRESTTestHelper;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class SLAResultResourceTest extends BaseSLAResultResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_process = _workflowMetricsRESTTestHelper.addProcess(
			testGroup.getCompanyId());

		_instance = _workflowMetricsRESTTestHelper.addInstance(
			testGroup.getCompanyId(), _process.getId());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		if (_process != null) {
			_workflowMetricsRESTTestHelper.deleteProcess(
				testGroup.getCompanyId(), _process);
		}

		if (_instance != null) {
			_workflowMetricsRESTTestHelper.deleteInstance(
				testGroup.getCompanyId(), _instance);
		}
	}

	@Override
	@Test
	public void testGetProcessLastSLAResult() throws Exception {
		Date dateModified = new Date(
			System.currentTimeMillis() / Time.SECOND * Time.SECOND);

		SLAResult slaResult1 = randomSLAResult();

		slaResult1.setDateModified(
			new Date(dateModified.getTime() - (2 * Time.DAY)));

		SLAResult slaResult2 = randomSLAResult();

		slaResult2.setDateModified(dateModified);

		_workflowMetricsRESTTestHelper.addSLAInstanceResults(
			testGroup.getCompanyId(), _instance, slaResult1, slaResult2);

		SLAResult getSLAResult = slaResultResource.getProcessLastSLAResult(
			_process.getId());

		Assert.assertEquals(
			slaResult2.getDateModified(), getSLAResult.getDateModified());
		Assert.assertEquals(slaResult2.getId(), getSLAResult.getId());

		assertEquals(slaResult2, getSLAResult);
		assertValid(getSLAResult);

		_workflowMetricsRESTTestHelper.blockSLAInstanceResults(
			testGroup.getCompanyId(), _process.getId(), slaResult2.getId());

		getSLAResult = slaResultResource.getProcessLastSLAResult(
			_process.getId());

		Assert.assertEquals(
			slaResult2.getDateModified(), getSLAResult.getDateModified());
		Assert.assertEquals(slaResult2.getId(), getSLAResult.getId());

		assertEquals(slaResult2, getSLAResult);
		assertValid(getSLAResult);
	}

	@Override
	@Test
	public void testGraphQLGetProcessLastSLAResult() throws Exception {
		SLAResult slaResult = testGraphQLGetProcessLastSLAResult_addSLAResult();

		Assert.assertTrue(
			equals(
				slaResult,
				SLAResultSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"processLastSLAResult",
								HashMapBuilder.<String, Object>put(
									"processId", _process.getId()
								).build(),
								getGraphQLFields())),
						"JSONObject/data", "Object/processLastSLAResult"))));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"onTime", "remainingTime", "status"};
	}

	@Override
	protected SLAResult randomSLAResult() throws Exception {
		boolean slaResultOntime = RandomTestUtil.randomBoolean();

		return new SLAResult() {
			{
				dateModified = new Date(
					System.currentTimeMillis() / Time.SECOND * Time.SECOND);
				dateOverdue = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				onTime = slaResultOntime;
				remainingTime = slaResultOntime ? 100L : -100L;
				status = Status.RUNNING;
			}
		};
	}

	@Override
	protected SLAResult testGetProcessLastSLAResult_addSLAResult()
		throws Exception {

		SLAResult slaResult = randomSLAResult();

		_workflowMetricsRESTTestHelper.addSLAInstanceResults(
			testGroup.getCompanyId(), _instance, slaResult);

		return slaResult;
	}

	@Override
	protected SLAResult testGraphQLGetProcessLastSLAResult_addSLAResult()
		throws Exception {

		return testGetProcessLastSLAResult_addSLAResult();
	}

	private Instance _instance;
	private Process _process;

	@Inject
	private WorkflowMetricsRESTTestHelper _workflowMetricsRESTTestHelper;

}