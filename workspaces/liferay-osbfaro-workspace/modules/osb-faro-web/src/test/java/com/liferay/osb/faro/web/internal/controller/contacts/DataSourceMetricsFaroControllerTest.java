/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Eudaldo Alonso
 */
public class DataSourceMetricsFaroControllerTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(Mockito.anyLong())
		).thenReturn(
			_faroProject
		);

		ReflectionTestUtils.setField(
			_dataSourceMetricsFaroController, "contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtils.setField(
			_dataSourceMetricsFaroController, "faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetCampaignsCount() throws Exception {
		long campaignsCount = RandomTestUtil.randomLong();
		String dataSourceId = RandomTestUtil.randomString();

		Mockito.when(
			_contactsEngineClient.getDataSourceMetricsCampaignsCount(
				_faroProject, dataSourceId)
		).thenReturn(
			campaignsCount
		);

		long groupId = RandomTestUtil.randomLong();

		Assert.assertEquals(
			Long.valueOf(campaignsCount),
			_dataSourceMetricsFaroController.getCampaignsCount(
				groupId, dataSourceId));

		Mockito.verify(
			_faroProjectLocalService
		).getFaroProjectByGroupId(
			groupId
		);
	}

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final DataSourceMetricsFaroController
		_dataSourceMetricsFaroController =
			new DataSourceMetricsFaroController();
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);

}