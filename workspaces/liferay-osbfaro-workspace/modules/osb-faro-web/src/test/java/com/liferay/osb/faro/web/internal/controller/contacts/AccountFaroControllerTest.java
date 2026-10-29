/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Account;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStage;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.Metric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Arrays;
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
	public void testSearch() throws Exception {
		String accountLifecycleId = RandomTestUtil.randomString();
		String channelId = RandomTestUtil.randomString();
		String filterString = RandomTestUtil.randomString();
		boolean includeAnonymousUsers = RandomTestUtil.randomBoolean();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String rangeEnd = RandomTestUtil.randomString();
		Integer rangeKey = RandomTestUtil.randomInt();
		String rangeStart = RandomTestUtil.randomString();
		String search = RandomTestUtil.randomString();
		String segmentId = RandomTestUtil.randomString();
		String sortString = RandomTestUtil.randomString();
		int total = RandomTestUtil.randomInt();

		Mockito.when(
			_contactsEngineClient.getAccounts(
				_faroProject, accountLifecycleId, channelId, filterString,
				includeAnonymousUsers, search, rangeEnd, rangeKey, rangeStart,
				segmentId, page, pageSize, sortString)
		).thenReturn(
			new Results<>(Collections.emptyList(), total)
		);

		long groupId = RandomTestUtil.randomLong();

		FaroFDSResultsDisplay<Account> faroFDSResultsDisplay =
			_accountFaroController.search(
				groupId, accountLifecycleId, channelId, filterString,
				includeAnonymousUsers, rangeEnd, rangeKey, rangeStart, search,
				segmentId, page, pageSize, sortString);

		Assert.assertEquals(total, faroFDSResultsDisplay.getTotalCount());

		Mockito.verify(
			_contactsEngineClient
		).getAccounts(
			_faroProject, accountLifecycleId, channelId, filterString,
			includeAnonymousUsers, search, rangeEnd, rangeKey, rangeStart,
			segmentId, page, pageSize, sortString
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

		String name = RandomTestUtil.randomString();

		accountLifecycleStage.setName(name);

		accountLifecycle.setAccountLifecycleStages(
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
		Assert.assertEquals(name, stageMap.get("name"));
	}

	@Test
	public void testSearchFDSFieldValuesWithLifecycleStatusAndNoAccountLifecycleId()
		throws Exception {

		List<AccountLifecycle> accountLifecycles = Arrays.asList(
			_getAccountLifecycle(), _getAccountLifecycle());
		long channelId = RandomTestUtil.randomLong();

		Mockito.when(
			_contactsEngineClient.getAccountLifecycles(
				_faroProject, channelId, null, null, null, 1, 10000, null)
		).thenReturn(
			new Results<>(accountLifecycles, accountLifecycles.size())
		);

		FaroFDSResultsDisplay<Object> faroFDSResultsDisplay =
			_accountFaroController.searchFDSFieldValues(
				RandomTestUtil.randomLong(), null, channelId, "lifecycleStatus",
				RandomTestUtil.randomString(), RandomTestUtil.randomInt(),
				RandomTestUtil.randomInt());

		List<?> items = faroFDSResultsDisplay.getItems();

		Assert.assertEquals(
			items.toString(), accountLifecycles.size(), items.size());

		_assertItem(
			accountLifecycles.get(0), (Map<String, String>)items.get(0));
		_assertItem(
			accountLifecycles.get(1), (Map<String, String>)items.get(1));
	}

	private void _assertItem(
		AccountLifecycle accountLifecycle, Map<String, String> stageMap) {

		List<AccountLifecycleStage> accountLifecycleStages =
			accountLifecycle.getAccountLifecycleStages();

		AccountLifecycleStage accountLifecycleStage =
			accountLifecycleStages.get(0);

		Assert.assertEquals(
			accountLifecycleStage.getExternalReferenceCode(),
			stageMap.get("externalReferenceCode"));
		Assert.assertEquals(accountLifecycleStage.getId(), stageMap.get("id"));

		Assert.assertEquals(
			accountLifecycle.getExternalReferenceCode(),
			stageMap.get("lifecycleExternalReferenceCode"));
		Assert.assertEquals(
			accountLifecycle.getName(), stageMap.get("lifecycleName"));
		Assert.assertEquals(
			accountLifecycleStage.getName(), stageMap.get("name"));
	}

	private AccountLifecycle _getAccountLifecycle() {
		AccountLifecycle accountLifecycle = new AccountLifecycle();

		AccountLifecycleStage accountLifecycleStage =
			new AccountLifecycleStage();

		accountLifecycleStage.setExternalReferenceCode(
			RandomTestUtil.randomString());
		accountLifecycleStage.setId(RandomTestUtil.randomString());
		accountLifecycleStage.setName(RandomTestUtil.randomString());

		accountLifecycle.setAccountLifecycleStages(
			Collections.singletonList(accountLifecycleStage));

		accountLifecycle.setExternalReferenceCode(
			RandomTestUtil.randomString());
		accountLifecycle.setName(RandomTestUtil.randomString());

		return accountLifecycle;
	}

	private final AccountFaroController _accountFaroController =
		new AccountFaroController();
	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);

}