/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.model.GraphQLRequest;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.client.dto.v1_0.VisitFrequencyMetric;
import com.liferay.osb.faro.rest.resource.v1_0.test.util.ContactsEngineGraphQLStub;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class VisitFrequencyMetricResourceTest
	extends BaseVisitFrequencyMetricResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_contactsEngineGraphQLStub = new ContactsEngineGraphQLStub(
			ReflectionTestUtil.getFieldValue(
				this, "_visitFrequencyMetricResource"));

		_faroProject = _faroProjectLocalService.addFaroProject(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), null,
			null, null, null, Collections.emptyList(), null, null, null, null,
			null, FaroProjectConstants.STATE_READY, "{}", "UTC",
			RandomTestUtil.randomString());

		_faroChannelLocalService.addFaroChannel(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			_CHANNEL_ID, _faroProject.getGroupId());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		_contactsEngineGraphQLStub.close();

		_faroProjectLocalService.deleteFaroProjectByGroupId(
			_faroProject.getGroupId());

		super.tearDown();
	}

	@Override
	@Test
	public void testGetWorkspaceGroupChannelVisitFrequencyMetric()
		throws Exception {

		_testGetWorkspaceGroupChannelVisitFrequencyMetric();
		_testGetWorkspaceGroupChannelVisitFrequencyMetricWithoutVisitFrequencyBag();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"biWeeklyVisitors", "dailyVisitors", "monthlyVisitors", "visitors",
			"weeklyVisitors"
		};
	}

	private Map<String, Object> _createComposition(Long count, String name) {
		return HashMapBuilder.<String, Object>put(
			"count", count
		).put(
			"name", name
		).build();
	}

	private void _testGetWorkspaceGroupChannelVisitFrequencyMetric()
		throws Exception {

		VisitFrequencyMetric visitFrequencyMetric =
			randomVisitFrequencyMetric();

		_contactsEngineGraphQLStub.setData(
			HashMapBuilder.<String, Object>put(
				"visitFrequencyBag",
				HashMapBuilder.<String, Object>put(
					"compositions",
					List.of(
						_createComposition(
							visitFrequencyMetric.getBiWeeklyVisitors(),
							"bi-weekly"),
						_createComposition(
							visitFrequencyMetric.getDailyVisitors(), "daily"),
						_createComposition(
							visitFrequencyMetric.getMonthlyVisitors(),
							"monthly"),
						_createComposition(
							visitFrequencyMetric.getOccasionalVisitors(),
							"occasional"),
						_createComposition(
							visitFrequencyMetric.getWeeklyVisitors(), "weekly"))
				).put(
					"totalCount", visitFrequencyMetric.getVisitors()
				).build()
			).build());

		String rangeEnd = "2026-01-31";
		String rangeStart = "2026-01-01";

		VisitFrequencyMetric actualVisitFrequencyMetric =
			visitFrequencyMetricResource.
				getWorkspaceGroupChannelVisitFrequencyMetric(
					_faroProject.getGroupId(), _CHANNEL_ID, rangeEnd,
					"LAST_7_DAYS", rangeStart);

		assertEquals(visitFrequencyMetric, actualVisitFrequencyMetric);
		Assert.assertEquals(
			visitFrequencyMetric.getOccasionalVisitors(),
			actualVisitFrequencyMetric.getOccasionalVisitors());

		GraphQLRequest graphQLRequest =
			_contactsEngineGraphQLStub.getGraphQLRequest();

		Assert.assertEquals(
			"getWorkspaceGroupChannelVisitFrequencyMetric",
			graphQLRequest.getOperationName());

		Map<String, Object> variables = graphQLRequest.getVariables();

		Assert.assertEquals(_CHANNEL_ID, variables.get("channelId"));
		Assert.assertEquals(rangeEnd, variables.get("rangeEnd"));
		Assert.assertEquals(7, variables.get("rangeKey"));
		Assert.assertEquals(rangeStart, variables.get("rangeStart"));
	}

	private void _testGetWorkspaceGroupChannelVisitFrequencyMetricWithoutVisitFrequencyBag()
		throws Exception {

		_contactsEngineGraphQLStub.setData(Collections.emptyMap());

		VisitFrequencyMetric visitFrequencyMetric =
			visitFrequencyMetricResource.
				getWorkspaceGroupChannelVisitFrequencyMetric(
					_faroProject.getGroupId(), _CHANNEL_ID, null, null, null);

		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getBiWeeklyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getDailyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getMonthlyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getOccasionalVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getWeeklyVisitors());
	}

	private static final String _CHANNEL_ID = String.valueOf(
		RandomTestUtil.randomLong());

	private ContactsEngineGraphQLStub _contactsEngineGraphQLStub;

	@Inject
	private FaroChannelLocalService _faroChannelLocalService;

	private FaroProject _faroProject;

	@Inject
	private FaroProjectLocalService _faroProjectLocalService;

}