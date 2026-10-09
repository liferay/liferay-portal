/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStage;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.Metric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Leslie Wong
 */
public class AccountFaroControllerTest {

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

	@Test
	public void testGetIndividualsFaroFDSResultsDisplay() throws Exception {
		String channelId = RandomTestUtil.randomString();
		String id = RandomTestUtil.randomString();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String rangeEnd = RandomTestUtil.randomString();
		Integer rangeKey = RandomTestUtil.randomInt();
		String rangeStart = RandomTestUtil.randomString();
		String search = RandomTestUtil.randomString();
		String sortString = RandomTestUtil.randomString();
		int total = RandomTestUtil.randomInt();

		Mockito.when(
			_contactsEngineClient.getAccountIndividuals(
				_faroProject, id, channelId, search, rangeEnd, rangeKey,
				rangeStart, page, pageSize, sortString)
		).thenReturn(
			new Results<>(Collections.emptyList(), total)
		);

		long groupId = RandomTestUtil.randomLong();

		FaroFDSResultsDisplay<Individual> faroFDSResultsDisplay =
			_accountFaroController.getIndividualsFaroFDSResultsDisplay(
				groupId, id, channelId, rangeEnd, rangeKey, rangeStart, search,
				page, pageSize, sortString);

		Assert.assertEquals(total, faroFDSResultsDisplay.getTotalCount());

		Mockito.verify(
			_contactsEngineClient
		).getAccountIndividuals(
			_faroProject, id, channelId, search, rangeEnd, rangeKey, rangeStart,
			page, pageSize, sortString
		);
	}

	@Test
	public void testSearchFDSFieldValuesWithLifecycleStatus() throws Exception {
		AccountLifecycle accountLifecycle = new AccountLifecycle();

		AccountLifecycleStage accountLifecycleStage =
			new AccountLifecycleStage();

		String externalReferenceCode = RandomTestUtil.randomString();

		accountLifecycleStage.setExternalReferenceCode(externalReferenceCode);

		String id = RandomTestUtil.randomString();

		accountLifecycleStage.setId(id);

		String stageType = RandomTestUtil.randomString();

		accountLifecycleStage.setStageType(stageType);

		accountLifecycle.setStages(
			Collections.singletonList(accountLifecycleStage));

		String accountLifecycleId = RandomTestUtil.randomString();

		Mockito.when(
			_contactsEngineClient.getAccountLifecycle(
				_faroProject, accountLifecycleId)
		).thenReturn(
			accountLifecycle
		);

		long channelId = RandomTestUtil.randomLong();
		long groupId = RandomTestUtil.randomLong();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String query = RandomTestUtil.randomString();

		FaroFDSResultsDisplay<Object> faroFDSResultsDisplay =
			_accountFaroController.searchFDSFieldValues(
				groupId, accountLifecycleId, channelId, "lifecycleStatus",
				query, page, pageSize);

		List<?> items = faroFDSResultsDisplay.getItems();

		Assert.assertEquals(items.toString(), 1, items.size());

		Map<String, String> stageMap = (Map<String, String>)items.get(0);

		Assert.assertEquals(
			externalReferenceCode, stageMap.get("externalReferenceCode"));
		Assert.assertEquals(id, stageMap.get("id"));
		Assert.assertEquals(stageType, stageMap.get("stageType"));
	}

	private final AccountFaroController _accountFaroController =
		new AccountFaroController();
	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);

}