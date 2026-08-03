/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Metric;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Leslie Wong
 */
public class AccountFaroControllerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(Mockito.anyLong())
		).thenReturn(
			_faroProject
		);

		ReflectionTestUtils.setField(
			_accountFaroController, "contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtils.setField(
			_accountFaroController, "faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetAccountOverviewMetrics() throws Exception {
		long channelId = RandomTestUtil.randomLong();
		String id = RandomTestUtil.randomString();
		List<Metric> metrics = Collections.singletonList(new Metric());

		Mockito.when(
			_contactsEngineClient.getAccountOverviewMetrics(
				_faroProject, channelId, id)
		).thenReturn(
			metrics
		);

		long groupId = RandomTestUtil.randomLong();

		Assert.assertSame(
			metrics,
			_accountFaroController.getAccountOverviewMetrics(
				groupId, id, channelId));

		Mockito.verify(
			_faroProjectLocalService
		).getFaroProjectByGroupId(
			groupId
		);
	}

	private final AccountFaroController _accountFaroController =
		new AccountFaroController();
	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);

}