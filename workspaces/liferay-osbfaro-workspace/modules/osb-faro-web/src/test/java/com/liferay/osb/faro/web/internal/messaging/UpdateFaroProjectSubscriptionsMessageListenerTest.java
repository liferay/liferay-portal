/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.messaging;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.ProjectUsageMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroProjectUsage;
import com.liferay.osb.faro.provisioning.client.ProvisioningClient;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroProjectUsageLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Rachael Koestartyo
 */
public class UpdateFaroProjectSubscriptionsMessageListenerTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_contactsEngineClient.getProjectUsageMetrics(
				Mockito.any(FaroProject.class), Mockito.any(Date.class))
		).thenReturn(
			new Results<>(Collections.emptyList(), 0)
		);

		ReflectionTestUtils.setField(
			_updateFaroProjectSubscriptionsMessageListener,
			"_contactsEngineClient", _contactsEngineClient);
		ReflectionTestUtils.setField(
			_updateFaroProjectSubscriptionsMessageListener,
			"_faroProjectLocalService", _faroProjectLocalService);
		ReflectionTestUtils.setField(
			_updateFaroProjectSubscriptionsMessageListener,
			"_faroProjectUsageLocalService", _faroProjectUsageLocalService);
		ReflectionTestUtils.setField(
			_updateFaroProjectSubscriptionsMessageListener,
			"_provisioningClient", _provisioningClient);
	}

	@Test
	public void testDoReceive() throws Exception {
		FaroProject faroProject1 = _mockFaroProject(null);

		Mockito.when(
			_provisioningClient.getOSBAccountEntry(
				faroProject1.getCorpProjectUuid())
		).thenThrow(
			new Exception()
		);

		FaroProject faroProject2 = _mockFaroProject(null);

		String salesforceAccountId = RandomTestUtil.randomString();

		_mockOSBAccountEntry(faroProject2, salesforceAccountId);

		_receive(faroProject1, faroProject2);

		Mockito.verify(
			_faroProjectLocalService
		).updateState(
			faroProject1.getFaroProjectId(),
			FaroProjectConstants.STATE_UNAVAILABLE
		);

		Mockito.verify(
			_faroProjectLocalService, Mockito.never()
		).updateSubscription(
			Mockito.eq(faroProject1.getFaroProjectId()), Mockito.anyString()
		);

		Mockito.verify(
			_faroProjectLocalService
		).updateSalesforceAccountId(
			faroProject2.getFaroProjectId(), salesforceAccountId
		);

		Mockito.verify(
			_faroProjectLocalService
		).updateSubscription(
			Mockito.eq(faroProject2.getFaroProjectId()), Mockito.anyString()
		);
	}

	@Test
	public void testDoReceiveWithFaroProjectUsage() throws Exception {
		FaroProject faroProject = _mockFaroProject(null);

		FaroProjectUsage faroProjectUsage = Mockito.mock(
			FaroProjectUsage.class);

		Mockito.when(
			faroProjectUsage.getFaroProjectUsageId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_faroProjectUsageLocalService.fetchFaroProjectUsage(
				Mockito.eq(faroProject.getFaroProjectId()),
				Mockito.any(Date.class))
		).thenReturn(
			faroProjectUsage
		);

		_mockOSBAccountEntry(faroProject, null);

		ProjectUsageMetric projectUsageMetric = _mockProjectUsageMetric(
			faroProject);

		_receive(faroProject);

		Mockito.verify(
			_faroProjectUsageLocalService
		).updateFaroProjectUsage(
			faroProjectUsage.getFaroProjectUsageId(),
			projectUsageMetric.getActiveLogicalBytes(),
			projectUsageMetric.getAnonymousIndividualsCount(),
			projectUsageMetric.getEventsCount(),
			projectUsageMetric.getKnownIndividualsCount(),
			projectUsageMetric.getLongTermLogicalBytes(),
			projectUsageMetric.getOnDemandBytesBilled(),
			projectUsageMetric.getPageViewsCount(),
			projectUsageMetric.getReservationSlotMs()
		);
	}

	@Test
	public void testDoReceiveWithProjectUsageMetric() throws Exception {
		FaroProject faroProject = _mockFaroProject(null);

		_mockOSBAccountEntry(faroProject, null);

		ProjectUsageMetric projectUsageMetric = _mockProjectUsageMetric(
			faroProject);

		_receive(faroProject);

		long companyId = faroProject.getCompanyId();
		long faroProjectId = faroProject.getFaroProjectId();

		Mockito.verify(
			_faroProjectUsageLocalService
		).addFaroProjectUsage(
			Mockito.eq(companyId), Mockito.eq(0L),
			Mockito.eq(projectUsageMetric.getActiveLogicalBytes()),
			Mockito.eq(projectUsageMetric.getAnonymousIndividualsCount()),
			Mockito.eq(projectUsageMetric.getEventsCount()),
			Mockito.eq(faroProjectId),
			Mockito.eq(projectUsageMetric.getKnownIndividualsCount()),
			Mockito.eq(projectUsageMetric.getLongTermLogicalBytes()),
			Mockito.anyString(),
			Mockito.eq(projectUsageMetric.getOnDemandBytesBilled()),
			Mockito.eq(projectUsageMetric.getPageViewsCount()),
			Mockito.eq(projectUsageMetric.getReservationSlotMs()),
			Mockito.any(Date.class)
		);
	}

	@Test
	public void testDoReceiveWithoutSalesforceAccountIdChange()
		throws Exception {

		String salesforceAccountId = RandomTestUtil.randomString();

		FaroProject faroProject1 = _mockFaroProject(salesforceAccountId);

		_mockOSBAccountEntry(faroProject1, salesforceAccountId);

		FaroProject faroProject2 = _mockFaroProject(
			RandomTestUtil.randomString());

		_mockOSBAccountEntry(faroProject2, null);

		_receive(faroProject1, faroProject2);

		Mockito.verify(
			_faroProjectLocalService, Mockito.never()
		).updateSalesforceAccountId(
			Mockito.anyLong(), Mockito.anyString()
		);

		Mockito.verify(
			_faroProjectLocalService, Mockito.times(2)
		).updateSubscription(
			Mockito.anyLong(), Mockito.anyString()
		);
	}

	private FaroProject _mockFaroProject(String salesforceAccountId) {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getCorpProjectUuid()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			faroProject.getFaroProjectId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			faroProject.getSalesforceAccountId()
		).thenReturn(
			salesforceAccountId
		);

		Mockito.when(
			faroProject.getState()
		).thenReturn(
			FaroProjectConstants.STATE_UNCONFIGURED
		);

		Mockito.when(
			_faroProjectLocalService.updateSalesforceAccountId(
				Mockito.eq(faroProject.getFaroProjectId()), Mockito.anyString())
		).thenReturn(
			faroProject
		);

		return faroProject;
	}

	private void _mockOSBAccountEntry(
			FaroProject faroProject, String salesforceAccountId)
		throws Exception {

		OSBAccountEntry osbAccountEntry = new OSBAccountEntry();

		osbAccountEntry.setSalesforceAccountId(salesforceAccountId);

		Mockito.when(
			_provisioningClient.getOSBAccountEntry(
				faroProject.getCorpProjectUuid())
		).thenReturn(
			osbAccountEntry
		);
	}

	private ProjectUsageMetric _mockProjectUsageMetric(FaroProject faroProject)
		throws Exception {

		String projectId = RandomTestUtil.randomString();

		Mockito.when(
			faroProject.getProjectId()
		).thenReturn(
			projectId
		);

		ProjectUsageMetric projectUsageMetric = new ProjectUsageMetric();

		projectUsageMetric.setActiveLogicalBytes(RandomTestUtil.randomLong());
		projectUsageMetric.setAnonymousIndividualsCount(
			RandomTestUtil.randomLong());
		projectUsageMetric.setEventsCount(RandomTestUtil.randomLong());
		projectUsageMetric.setKnownIndividualsCount(
			RandomTestUtil.randomLong());
		projectUsageMetric.setLongTermLogicalBytes(RandomTestUtil.randomLong());
		projectUsageMetric.setOnDemandBytesBilled(RandomTestUtil.randomLong());
		projectUsageMetric.setPageViewsCount(RandomTestUtil.randomLong());
		projectUsageMetric.setProjectId(projectId);
		projectUsageMetric.setReservationSlotMs(RandomTestUtil.randomLong());

		Mockito.when(
			_contactsEngineClient.getProjectUsageMetrics(
				Mockito.eq(faroProject), Mockito.any(Date.class))
		).thenReturn(
			new Results<>(Collections.singletonList(projectUsageMetric), 1)
		);

		return projectUsageMetric;
	}

	private void _receive(FaroProject... faroProjects) {
		Mockito.when(
			_faroProjectLocalService.getFaroProjects(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS)
		).thenReturn(
			Arrays.asList(faroProjects)
		);

		ReflectionTestUtils.invokeMethod(
			_updateFaroProjectSubscriptionsMessageListener, "doReceive",
			new Message());
	}

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final FaroProjectUsageLocalService _faroProjectUsageLocalService =
		Mockito.mock(FaroProjectUsageLocalService.class);
	private final ProvisioningClient _provisioningClient = Mockito.mock(
		ProvisioningClient.class);
	private final UpdateFaroProjectSubscriptionsMessageListener
		_updateFaroProjectSubscriptionsMessageListener =
			new UpdateFaroProjectSubscriptionsMessageListener();

}