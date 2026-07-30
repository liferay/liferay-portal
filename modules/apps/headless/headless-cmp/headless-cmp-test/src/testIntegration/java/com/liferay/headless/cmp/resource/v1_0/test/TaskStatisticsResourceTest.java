/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.cmp.client.dto.v1_0.TaskStatistics;
import com.liferay.headless.cmp.client.resource.v1_0.TaskStatisticsResource;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import java.io.Serializable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class TaskStatisticsResourceTest
	extends BaseTaskStatisticsResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		CMPTestUtil.getOrAddGroup(TaskStatisticsResourceTest.class);

		_cmpProjectObjectEntry1 = CMPTestUtil.addCMPProjectObjectEntry();

		_partialUpdateObjectEntry(
			null, CMPTestUtil.addCMPTaskObjectEntry(_cmpProjectObjectEntry1),
			"blocked");
		_partialUpdateObjectEntry(
			"3000-01-31",
			CMPTestUtil.addCMPTaskObjectEntry(_cmpProjectObjectEntry1),
			"inProgress");

		_cmpProjectObjectEntry2 = CMPTestUtil.addCMPProjectObjectEntry();

		_partialUpdateObjectEntry(
			"2025-01-31",
			CMPTestUtil.addCMPTaskObjectEntry(_cmpProjectObjectEntry2),
			"inProgress");
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();

		_objectEntryLocalService.deleteObjectEntry(_cmpProjectObjectEntry1);
		_objectEntryLocalService.deleteObjectEntry(_cmpProjectObjectEntry2);
	}

	@Override
	@Test
	public void testGetProjectTaskStatistics() throws Exception {
		TaskStatistics taskStatistics1 =
			taskStatisticsResource.getProjectTaskStatistics(
				_cmpProjectObjectEntry1.getObjectEntryId());

		Assert.assertEquals(
			1, GetterUtil.getLong(taskStatistics1.getBlockedCount()));
		Assert.assertEquals(
			1, GetterUtil.getLong(taskStatistics1.getInProgressCount()));
		Assert.assertEquals(
			0, GetterUtil.getLong(taskStatistics1.getOverdueCount()));
		Assert.assertEquals(
			2, GetterUtil.getLong(taskStatistics1.getTotalCount()));

		TaskStatistics taskStatistics2 =
			taskStatisticsResource.getProjectTaskStatistics(
				_cmpProjectObjectEntry2.getObjectEntryId());

		Assert.assertEquals(
			0, GetterUtil.getLong(taskStatistics2.getBlockedCount()));
		Assert.assertEquals(
			1, GetterUtil.getLong(taskStatistics2.getInProgressCount()));
		Assert.assertEquals(
			1, GetterUtil.getLong(taskStatistics2.getOverdueCount()));
		Assert.assertEquals(
			1, GetterUtil.getLong(taskStatistics2.getTotalCount()));

		_testGetProjectTaskStatisticsWithoutViewPermission();
	}

	@Override
	@Test
	public void testGetTaskStatistics() throws Exception {
		TaskStatistics taskStatistics =
			taskStatisticsResource.getTaskStatistics();

		Assert.assertEquals(
			1, GetterUtil.getLong(taskStatistics.getBlockedCount()));
		Assert.assertEquals(
			2, GetterUtil.getLong(taskStatistics.getInProgressCount()));
		Assert.assertEquals(
			1, GetterUtil.getLong(taskStatistics.getOverdueCount()));
		Assert.assertEquals(
			3, GetterUtil.getLong(taskStatistics.getTotalCount()));
	}

	@Override
	@Test
	public void testGraphQLGetProjectTaskStatistics() throws Exception {
	}

	@Override
	@Test
	public void testGraphQLGetTaskStatistics() throws Exception {
	}

	private ObjectEntry _partialUpdateObjectEntry(
			String dueDate, ObjectEntry objectEntry, String state)
		throws Exception {

		return _objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"dueDate", dueDate
			).put(
				"state", state
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _testGetProjectTaskStatisticsWithoutViewPermission()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		TaskStatisticsResource userTaskStatisticsResource =
			TaskStatisticsResource.builder(
			).authentication(
				user.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		assertHttpResponseStatusCode(
			404,
			userTaskStatisticsResource.getProjectTaskStatisticsHttpResponse(
				_cmpProjectObjectEntry1.getObjectEntryId()));
	}

	private ObjectEntry _cmpProjectObjectEntry1;
	private ObjectEntry _cmpProjectObjectEntry2;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}