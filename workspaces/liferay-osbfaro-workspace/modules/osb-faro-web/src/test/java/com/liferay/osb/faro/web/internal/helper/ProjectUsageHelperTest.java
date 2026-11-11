/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.batch.engine.pagination.Page;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.ProjectUsageMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntryBuilder;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.provisioning.client.model.display.main.FaroSubscriptionDisplay;
import com.liferay.osb.faro.service.FaroDataSourceUsageLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroProjectUsageLocalService;
import com.liferay.osb.faro.web.internal.model.display.contacts.DataSourceUsageMetricDisplay;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Marcos Martins
 */
public class ProjectUsageHelperTest {

	@Before
	public void setUp() {
		Mockito.when(
			_contactsEngineClient.getProjectUsageMetrics(
				Mockito.any(FaroProject.class), Mockito.any(Date.class))
		).thenReturn(
			new Results<>(Collections.emptyList(), 0)
		);

		ReflectionTestUtils.setField(
			_projectUsageHelper, "_contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtils.setField(
			_projectUsageHelper, "_faroDataSourceUsageLocalService",
			_faroDataSourceUsageLocalService);
		ReflectionTestUtils.setField(
			_projectUsageHelper, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtils.setField(
			_projectUsageHelper, "_faroProjectUsageLocalService",
			_faroProjectUsageLocalService);
	}

	@Test
	public void testAddOrUpdateFaroProjectUsages() {
		String serverLocation = RandomTestUtil.randomString();

		FaroProject faroProject1 = _mockFaroProject(serverLocation);
		FaroProject faroProject2 = _mockFaroProject(serverLocation);

		Date date = new Date();

		_mockProjectUsageMetrics(date, faroProject1, faroProject2);

		_projectUsageHelper.addOrUpdateFaroProjectUsages(
			date, Arrays.asList(faroProject1, faroProject2));

		Mockito.verify(
			_contactsEngineClient
		).getProjectUsageMetrics(
			Mockito.any(FaroProject.class), Mockito.eq(date)
		);

		_verifyAddFaroProjectUsage(faroProject1, Mockito.times(1));
		_verifyAddFaroProjectUsage(faroProject2, Mockito.times(1));
	}

	@Test
	public void testAddOrUpdateFaroProjectUsagesWithUnavailableServerLocation() {
		FaroProject faroProject1 = _mockFaroProject(
			RandomTestUtil.randomString());
		FaroProject faroProject2 = _mockFaroProject(
			RandomTestUtil.randomString());

		Date date = new Date();

		_mockProjectUsageMetrics(date, faroProject2);

		Mockito.when(
			_contactsEngineClient.getProjectUsageMetrics(faroProject1, date)
		).thenThrow(
			new RuntimeException(RandomTestUtil.randomString())
		);

		_projectUsageHelper.addOrUpdateFaroProjectUsages(
			date, Arrays.asList(faroProject1, faroProject2));

		_verifyAddFaroProjectUsage(faroProject1, Mockito.never());
		_verifyAddFaroProjectUsage(faroProject2, Mockito.times(1));
	}

	@Test
	public void testAddOrUpdateFaroProjectUsagesWithoutProjectUsageMetric() {
		FaroProject faroProject = _mockFaroProject(
			RandomTestUtil.randomString());

		_projectUsageHelper.addOrUpdateFaroProjectUsages(
			new Date(), Collections.singletonList(faroProject));

		_verifyAddFaroProjectUsage(faroProject, Mockito.never());

		Mockito.verify(
			_faroProjectUsageLocalService, Mockito.never()
		).fetchFaroProjectUsage(
			Mockito.anyLong(), Mockito.any(Date.class)
		);
	}

	@Test
	public void testBackfillFaroProjectUsages() {
		FaroProject faroProject = _mockFaroProject(
			RandomTestUtil.randomString());

		Mockito.when(
			_faroProjectLocalService.getFaroProjects(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS)
		).thenReturn(
			Collections.singletonList(faroProject)
		);

		LocalDate endLocalDate = LocalDate.of(2026, 3, 1);
		LocalDate startLocalDate = LocalDate.of(2026, 2, 27);

		_projectUsageHelper.backfillFaroProjectUsages(
			endLocalDate, startLocalDate);

		Mockito.verify(
			_contactsEngineClient, Mockito.times(3)
		).getProjectUsageMetrics(
			Mockito.eq(faroProject), Mockito.any(Date.class)
		);

		_verifyGetProjectUsageMetrics(faroProject, LocalDate.of(2026, 2, 27));
		_verifyGetProjectUsageMetrics(faroProject, LocalDate.of(2026, 2, 28));
		_verifyGetProjectUsageMetrics(faroProject, LocalDate.of(2026, 3, 1));
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
			faroProject.isDataPlatform()
		).thenReturn(
			true
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

		Page<DataSourceUsageMetricDisplay> dataSourceUsageMetricDisplaysPage =
			_projectUsageHelper.getDataSourceUsageMetricDisplays(
				null, 1, 15, null);

		List<DataSourceUsageMetricDisplay> dataSourceUsageMetricDisplays =
			new ArrayList<>(dataSourceUsageMetricDisplaysPage.getItems());

		DataSourceUsageMetricDisplay dataSourceUsageMetricDisplay =
			dataSourceUsageMetricDisplays.get(0);

		Assert.assertTrue(dataSourceUsageMetricDisplay.isDataPlatform());
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

	private Date _getDate(LocalDate localDate) {
		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC);

		return Date.from(zonedDateTime.toInstant());
	}

	private FaroProject _mockFaroProject(String serverLocation) {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			faroProject.getFaroProjectId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			faroProject.getProjectId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			faroProject.getServerLocation()
		).thenReturn(
			serverLocation
		);

		return faroProject;
	}

	private void _mockProjectUsageMetrics(
		Date date, FaroProject... faroProjects) {

		List<ProjectUsageMetric> projectUsageMetrics = TransformUtil.transform(
			Arrays.asList(faroProjects),
			faroProject -> {
				ProjectUsageMetric projectUsageMetric =
					new ProjectUsageMetric();

				projectUsageMetric.setProjectId(faroProject.getProjectId());

				return projectUsageMetric;
			});

		Mockito.when(
			_contactsEngineClient.getProjectUsageMetrics(
				Mockito.any(FaroProject.class), Mockito.eq(date))
		).thenReturn(
			new Results<>(projectUsageMetrics, projectUsageMetrics.size())
		);
	}

	private void _verifyAddFaroProjectUsage(
		FaroProject faroProject, VerificationMode verificationMode) {

		long companyId = faroProject.getCompanyId();
		long faroProjectId = faroProject.getFaroProjectId();

		Mockito.verify(
			_faroProjectUsageLocalService, verificationMode
		).addFaroProjectUsage(
			Mockito.eq(companyId), Mockito.eq(0L), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.eq(faroProjectId), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.anyLong(), Mockito.any(Date.class)
		);
	}

	private void _verifyGetProjectUsageMetrics(
		FaroProject faroProject, LocalDate localDate) {

		Mockito.verify(
			_contactsEngineClient
		).getProjectUsageMetrics(
			faroProject, _getDate(localDate)
		);
	}

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroDataSourceUsageLocalService
		_faroDataSourceUsageLocalService = Mockito.mock(
			FaroDataSourceUsageLocalService.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final FaroProjectUsageLocalService _faroProjectUsageLocalService =
		Mockito.mock(FaroProjectUsageLocalService.class);
	private final ProjectUsageHelper _projectUsageHelper =
		new ProjectUsageHelper();

}