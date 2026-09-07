/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Account;
import com.liferay.osb.faro.engine.client.model.Campaign;
import com.liferay.osb.faro.engine.client.model.CampaignMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.model.display.FaroFDSResultsDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.AccountDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.CampaignDisplay;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Riccardo Ferrari
 */
public class CampaignFaroControllerTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(Mockito.anyLong())
		).thenReturn(
			_faroProject
		);

		ReflectionTestUtils.setField(
			_campaignFaroController, "contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtils.setField(
			_campaignFaroController, "faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetAccountsFaroFDSResultsDisplay() throws Exception {
		long channelId = RandomTestUtil.randomLong();
		String filterString = RandomTestUtil.randomString();
		String id = RandomTestUtil.randomString();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String search = RandomTestUtil.randomString();
		String sortString = RandomTestUtil.randomString();
		int total = RandomTestUtil.randomInt();

		Account account = new Account();

		account.setId(RandomTestUtil.randomString());

		Mockito.when(
			_contactsEngineClient.getCampaignAccounts(
				_faroProject, channelId, filterString, id, search, sortString,
				page, pageSize)
		).thenReturn(
			new Results<>(Collections.singletonList(account), total)
		);

		long groupId = RandomTestUtil.randomLong();

		FaroFDSResultsDisplay<Account> faroFDSResultsDisplay =
			_campaignFaroController.getAccountsFaroFDSResultsDisplay(
				groupId, id, channelId, filterString, page, pageSize, search,
				sortString);

		List<?> items = faroFDSResultsDisplay.getItems();

		AccountDisplay accountDisplay = (AccountDisplay)items.get(0);

		Assert.assertEquals(
			account.getId(),
			ReflectionTestUtil.getFieldValue(accountDisplay, "_id"));

		Assert.assertEquals(page, faroFDSResultsDisplay.getPage());
		Assert.assertEquals(pageSize, faroFDSResultsDisplay.getPageSize());
		Assert.assertEquals(total, faroFDSResultsDisplay.getTotalCount());

		Mockito.verify(
			_contactsEngineClient
		).getCampaignAccounts(
			_faroProject, channelId, filterString, id, search, sortString, page,
			pageSize
		);
	}

	@Test
	public void testGetCampaignDisplay() throws Exception {
		long channelId = RandomTestUtil.randomLong();
		String id = RandomTestUtil.randomString();

		Campaign campaign = new Campaign();

		campaign.setAccountsTouched(RandomTestUtil.randomLong());
		campaign.setCampaignName(RandomTestUtil.randomString());
		campaign.setId(id);

		Mockito.when(
			_contactsEngineClient.getCampaign(_faroProject, channelId, id)
		).thenReturn(
			campaign
		);

		long groupId = RandomTestUtil.randomLong();

		CampaignDisplay campaignDisplay =
			_campaignFaroController.getCampaignDisplay(groupId, id, channelId);

		Assert.assertEquals(
			campaign.getAccountsTouched(),
			ReflectionTestUtil.getFieldValue(
				campaignDisplay, "_accountsTouched"));
		Assert.assertEquals(
			campaign.getCampaignName(),
			ReflectionTestUtil.getFieldValue(campaignDisplay, "_campaignName"));
		Assert.assertEquals(
			id, ReflectionTestUtil.getFieldValue(campaignDisplay, "_id"));

		Mockito.verify(
			_faroProjectLocalService
		).getFaroProjectByGroupId(
			groupId
		);
	}

	@Test
	public void testGetCampaignMetrics() throws Exception {
		long channelId = RandomTestUtil.randomLong();
		List<CampaignMetric> campaignMetrics = Collections.singletonList(
			new CampaignMetric());

		Mockito.when(
			_contactsEngineClient.getCampaignMetrics(_faroProject, channelId)
		).thenReturn(
			campaignMetrics
		);

		long groupId = RandomTestUtil.randomLong();

		Assert.assertSame(
			campaignMetrics,
			_campaignFaroController.getCampaignMetrics(groupId, channelId));

		Mockito.verify(
			_faroProjectLocalService
		).getFaroProjectByGroupId(
			groupId
		);
	}

	@Test
	public void testGetCampaignsFaroFDSResultsDisplay() throws Exception {
		long channelId = RandomTestUtil.randomLong();
		String filterString = RandomTestUtil.randomString();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String search = RandomTestUtil.randomString();
		String sortString = RandomTestUtil.randomString();
		int total = RandomTestUtil.randomInt();

		Campaign campaign = new Campaign();

		campaign.setId(RandomTestUtil.randomString());

		Mockito.when(
			_contactsEngineClient.getCampaigns(
				_faroProject, channelId, filterString, search, sortString, page,
				pageSize)
		).thenReturn(
			new Results<>(Collections.singletonList(campaign), total)
		);

		long groupId = RandomTestUtil.randomLong();

		FaroFDSResultsDisplay<Campaign> faroFDSResultsDisplay =
			_campaignFaroController.getCampaignsFaroFDSResultsDisplay(
				groupId, channelId, filterString, page, pageSize, search,
				sortString);

		List<?> items = faroFDSResultsDisplay.getItems();

		CampaignDisplay campaignDisplay = (CampaignDisplay)items.get(0);

		Assert.assertEquals(
			campaign.getId(),
			ReflectionTestUtil.getFieldValue(campaignDisplay, "_id"));

		Assert.assertEquals(page, faroFDSResultsDisplay.getPage());
		Assert.assertEquals(pageSize, faroFDSResultsDisplay.getPageSize());
		Assert.assertEquals(total, faroFDSResultsDisplay.getTotalCount());

		Mockito.verify(
			_contactsEngineClient
		).getCampaigns(
			_faroProject, channelId, filterString, search, sortString, page,
			pageSize
		);
	}

	private final CampaignFaroController _campaignFaroController =
		new CampaignFaroController();
	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);

}