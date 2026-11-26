/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.model.GraphQLRequest;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.client.dto.v1_0.Acquisition;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class AcquisitionResourceTest extends BaseAcquisitionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_contactsEngineGraphQLStub = new ContactsEngineGraphQLStub(
			ReflectionTestUtil.getFieldValue(this, "_acquisitionResource"));

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
	public void testGetWorkspaceGroupChannelAcquisitionsPage()
		throws Exception {

		_testGetWorkspaceGroupChannelAcquisitionsPage();
		_testGetWorkspaceGroupChannelAcquisitionsPageWithoutCompositionBag();
		_testGetWorkspaceGroupChannelAcquisitionsPageWithoutOptionalParameters();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAcquisitionsPageWithPagination()
		throws Exception {

		super.testGetWorkspaceGroupChannelAcquisitionsPageWithPagination();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"count", "name"};
	}

	private void _setCompositionBag(Acquisition acquisition, Long totalCount) {
		_contactsEngineGraphQLStub.setData(
			HashMapBuilder.<String, Object>put(
				"compositionBag",
				HashMapBuilder.<String, Object>put(
					"compositions",
					List.of(
						HashMapBuilder.<String, Object>put(
							"count", acquisition.getCount()
						).put(
							"name", acquisition.getName()
						).build())
				).put(
					"totalCount", totalCount
				).build()
			).build());
	}

	private void _testGetWorkspaceGroupChannelAcquisitionsPage()
		throws Exception {

		Acquisition acquisition = randomAcquisition();
		long totalCount = RandomTestUtil.randomLong();

		_setCompositionBag(acquisition, totalCount);

		String accountId = RandomTestUtil.randomString();
		int pageSize = 10;
		String rangeEnd = "2026-01-31";
		String rangeStart = "2026-01-01";
		String segmentId = String.valueOf(RandomTestUtil.randomLong());
		String type = "REFERRER";

		Page<Acquisition> acquisitionsPage =
			acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
				_faroProject.getGroupId(), _CHANNEL_ID, accountId, rangeEnd,
				"LAST_7_DAYS", rangeStart, segmentId, type,
				Pagination.of(2, pageSize));

		assertEquals(
			List.of(acquisition),
			(List<Acquisition>)acquisitionsPage.getItems());
		Assert.assertEquals(totalCount, acquisitionsPage.getTotalCount());

		GraphQLRequest graphQLRequest =
			_contactsEngineGraphQLStub.getGraphQLRequest();

		Assert.assertEquals(
			"getWorkspaceGroupChannelAcquisitionsPage",
			graphQLRequest.getOperationName());

		Map<String, Object> variables = graphQLRequest.getVariables();

		Assert.assertEquals(accountId, variables.get("accountId"));
		Assert.assertEquals(type, variables.get("acquisitionType"));
		Assert.assertEquals(_CHANNEL_ID, variables.get("channelId"));
		Assert.assertEquals(rangeEnd, variables.get("rangeEnd"));
		Assert.assertEquals(7, variables.get("rangeKey"));
		Assert.assertEquals(rangeStart, variables.get("rangeStart"));
		Assert.assertEquals(segmentId, variables.get("segmentId"));
		Assert.assertEquals(pageSize, variables.get("size"));
		Assert.assertEquals(1, variables.get("start"));
	}

	private void _testGetWorkspaceGroupChannelAcquisitionsPageWithoutCompositionBag()
		throws Exception {

		_contactsEngineGraphQLStub.setData(Collections.emptyMap());

		Page<Acquisition> acquisitionsPage =
			acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
				_faroProject.getGroupId(), _CHANNEL_ID, null, null, null, null,
				null, null, Pagination.of(1, 20));

		Assert.assertEquals(
			Collections.emptyList(),
			(List<Acquisition>)acquisitionsPage.getItems());
		Assert.assertEquals(0, acquisitionsPage.getTotalCount());
	}

	private void _testGetWorkspaceGroupChannelAcquisitionsPageWithoutOptionalParameters()
		throws Exception {

		_setCompositionBag(randomAcquisition(), null);

		Page<Acquisition> acquisitionsPage =
			acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
				_faroProject.getGroupId(), _CHANNEL_ID, null, null, null, null,
				null, null, Pagination.of(1, 20));

		Assert.assertEquals(0, acquisitionsPage.getTotalCount());

		GraphQLRequest graphQLRequest =
			_contactsEngineGraphQLStub.getGraphQLRequest();

		Map<String, Object> variables = graphQLRequest.getVariables();

		Assert.assertEquals("CHANNEL", variables.get("acquisitionType"));
		Assert.assertNull(variables.get("rangeKey"));
		Assert.assertEquals(0, variables.get("start"));
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