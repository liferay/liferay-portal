/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.batch.engine.pagination.Page;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntryBuilder;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.provisioning.client.model.display.main.FaroSubscriptionDisplay;
import com.liferay.osb.faro.service.FaroDataSourceUsageLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceUsageMetricDisplay;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Marcos Martins
 */
public class ProjectUsageHelperTest {

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(
			_projectUsageHelper, "_contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtils.setField(
			_projectUsageHelper, "_faroDataSourceUsageLocalService",
			_faroDataSourceUsageLocalService);
		ReflectionTestUtils.setField(
			_projectUsageHelper, "_faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetDataSourceUsageMetricDisplays() throws Exception {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getLastAnniversaryDate()
		).thenReturn(
			new Date()
		);

		OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

		osbOfferingEntry.setProductEntryId(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE);
		osbOfferingEntry.setQuantity(1);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			JSONUtil.writeValueAsString(
				new FaroSubscriptionDisplay(
					OSBAccountEntryBuilder.setName(
						RandomTestUtil.randomString()
					).setOfferingEntries(
						Collections.singletonList(osbOfferingEntry)
					).build()))
		);

		Mockito.when(
			_contactsEngineClient.getApiUsageMetrics(
				Mockito.any(), Mockito.any(), Mockito.any())
		).thenReturn(
			new Results<>(Collections.emptyList(), 0)
		);

		Mockito.when(
			_contactsEngineClient.getProjectMetrics(Mockito.any())
		).thenReturn(
			new Results<>(Collections.emptyList(), 0)
		);

		Mockito.when(
			_faroProjectLocalService.getFaroProjects(0, 15)
		).thenReturn(
			Collections.singletonList(faroProject)
		);

		Page<DataSourceUsageMetricDisplay> page =
			_projectUsageHelper.getDataSourceUsageMetricDisplays(
				null, 1, 15, null);

		List<DataSourceUsageMetricDisplay> dataSourceUsageMetricDisplays =
			new ArrayList<>(page.getItems());

		DataSourceUsageMetricDisplay dataSourceUsageMetricDisplay =
			dataSourceUsageMetricDisplays.get(0);

		Assert.assertEquals(
			ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE,
			dataSourceUsageMetricDisplay.getProductEntryId());
		Assert.assertEquals(
			ProductConstants.getProductName(
				ProductConstants.PRODUCT_ENTRY_ID_ENTERPRISE),
			dataSourceUsageMetricDisplay.getProductName());

		Assert.assertEquals(
			dataSourceUsageMetricDisplays.toString(), 1,
			dataSourceUsageMetricDisplays.size());
	}

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroDataSourceUsageLocalService
		_faroDataSourceUsageLocalService = Mockito.mock(
			FaroDataSourceUsageLocalService.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final ProjectUsageHelper _projectUsageHelper =
		new ProjectUsageHelper();

}