/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStage;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.client.dto.v1_0.Account;
import com.liferay.osb.faro.rest.client.problem.Problem;
import com.liferay.osb.faro.rest.resource.v1_0.AccountResource;
import com.liferay.osb.faro.rest.resource.v1_0.test.util.ContactsEngineHttpServer;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.BadRequestException;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
public class AccountResourceTest extends BaseAccountResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_contactsEngineHttpServer = new ContactsEngineHttpServer(
			_contactsEngineClient, "/accounts", "account");
		_faroProject = _faroProjectLocalService.addFaroProject(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), null,
			null, null, null, Collections.emptyList(), null, null, null, null,
			null, FaroProjectConstants.STATE_READY, "{}", "UTC",
			RandomTestUtil.randomString());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		_contactsEngineHttpServer.close();
		_faroProjectLocalService.deleteFaroProjectByGroupId(
			_faroProject.getGroupId());

		super.tearDown();
	}

	@Override
	@Test
	public void testGetWorkspaceGroupAccount() throws Exception {
		super.testGetWorkspaceGroupAccount();

		_testGetWorkspaceGroupAccountWithEmptyAccount();
		_testGetWorkspaceGroupAccountWithMissingAccount();
	}

	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountsPage() throws Exception {
		_testGetWorkspaceGroupChannelAccountsPageWithAccountLifecycleId();
		_testGetWorkspaceGroupChannelAccountsPageWithEmptyAccountLifecycle();
		_testGetWorkspaceGroupChannelAccountsPageWithLifecycleStage();
		_testGetWorkspaceGroupChannelAccountsPageWithUnknownLifecycleStage();
		_testGetWorkspaceGroupChannelAccountsPageWithoutLifecycleStage();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountsPageWithPagination()
		throws Exception {

		super.testGetWorkspaceGroupChannelAccountsPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountsPageWithSortDateTime()
		throws Exception {

		super.testGetWorkspaceGroupChannelAccountsPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountsPageWithSortDouble()
		throws Exception {

		super.testGetWorkspaceGroupChannelAccountsPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountsPageWithSortInteger()
		throws Exception {

		super.testGetWorkspaceGroupChannelAccountsPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountsPageWithSortString()
		throws Exception {

		super.testGetWorkspaceGroupChannelAccountsPageWithSortString();
	}

	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignAccountsPage()
		throws Exception {

		AtomicReference<Object[]> atomicReference = new AtomicReference<>();

		AccountResource accountResource = ReflectionTestUtil.getFieldValue(
			this, "_accountResource");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				accountResource, "_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						atomicReference.set(arguments);

						return new Results<>();
					}));
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				accountResource, "_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		String campaignId = RandomTestUtil.randomString();
		long channelId = RandomTestUtil.randomLong();
		String search = RandomTestUtil.randomString();

		try {
			accountResource.getWorkspaceGroupChannelCampaignAccountsPage(
				testGroup.getGroupId(), String.valueOf(channelId), campaignId,
				null, "LAST_7_DAYS", null, search, Pagination.of(2, 10),
				new Sort[] {new Sort("lastActivityDate", true)});
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				accountResource, "_contactsEngineClient", contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				accountResource, "_faroProjectLocalService",
				faroProjectLocalService);
		}

		Object[] objects = atomicReference.get();

		Assert.assertNull(objects[1]);
		Assert.assertNull(objects[2]);
		Assert.assertEquals(channelId, objects[3]);
		Assert.assertNull(objects[4]);
		Assert.assertEquals(campaignId, objects[5]);
		Assert.assertEquals(search, objects[6]);
		Assert.assertEquals(7, objects[8]);
		Assert.assertEquals("lastActivityDate:desc", objects[10]);
		Assert.assertEquals(2, objects[11]);
		Assert.assertEquals(10, objects[12]);
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignAccountsPageWithPagination()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignAccountsPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignAccountsPageWithSortDateTime()
		throws Exception {

		super.
			testGetWorkspaceGroupChannelCampaignAccountsPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignAccountsPageWithSortDouble()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignAccountsPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignAccountsPageWithSortInteger()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignAccountsPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelCampaignAccountsPageWithSortString()
		throws Exception {

		super.testGetWorkspaceGroupChannelCampaignAccountsPageWithSortString();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"accountName", "accountType", "country", "industry", "website"
		};
	}

	@Override
	protected Account testGetWorkspaceGroupAccount_addAccount()
		throws Exception {

		Account account = randomAccount();

		Date dateModified = account.getDateModified();

		_contactsEngineHttpServer.addJSON(
			account.getId(),
			JSONUtil.put(
				"accountName", account.getAccountName()
			).put(
				"accountType", account.getAccountType()
			).put(
				"country", account.getCountry()
			).put(
				"id", account.getId()
			).put(
				"industry", account.getIndustry()
			).put(
				"modifiedDate", dateModified.getTime()
			).put(
				"website", account.getWebsite()
			).toString());

		return account;
	}

	@Override
	protected Long testGetWorkspaceGroupAccount_getGroupId() throws Exception {
		return _faroProject.getGroupId();
	}

	private AccountLifecycle _addAccountLifecycle(
		String accountLifecycleId,
		Map<String, AccountLifecycle> accountLifecycles) {

		AccountLifecycle accountLifecycle = new AccountLifecycle();

		accountLifecycle.setExternalReferenceCode(
			"lifecycle-" + accountLifecycleId);
		accountLifecycle.setId(accountLifecycleId);

		AccountLifecycleStage accountLifecycleStage =
			new AccountLifecycleStage();

		accountLifecycleStage.setAccountLifecycleId(accountLifecycleId);
		accountLifecycleStage.setExternalReferenceCode(
			"stage-" + accountLifecycleId);
		accountLifecycleStage.setId(accountLifecycleId + "_AT_RISK");
		accountLifecycleStage.setName("At Risk");

		accountLifecycle.setAccountLifecycleStages(
			Collections.singletonList(accountLifecycleStage));

		accountLifecycles.put(accountLifecycleId, accountLifecycle);

		return accountLifecycle;
	}

	private Object[] _getWorkspaceGroupChannelAccountsPage(
			String accountLifecycleId, String lifecycleStage)
		throws Exception {

		AtomicReference<Object[]> argumentsAtomicReference =
			new AtomicReference<>();

		Map<String, AccountLifecycle> accountLifecycles = new HashMap<>();

		AccountLifecycle accountLifecycle = _addAccountLifecycle(
			"1", accountLifecycles);

		_addAccountLifecycle("2", accountLifecycles);

		accountLifecycles.put("3", new AccountLifecycle());

		AccountResource accountResource = ReflectionTestUtil.getFieldValue(
			this, "_accountResource");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				accountResource, "_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						String methodName = method.getName();

						if (methodName.equals("getAccountLifecycle")) {
							return accountLifecycles.get((String)arguments[1]);
						}

						if (methodName.equals("getAccountLifecycles")) {
							return new Results<>(
								Collections.singletonList(accountLifecycle), 1);
						}

						argumentsAtomicReference.set(arguments);

						return new Results<>();
					}));

		try {
			accountResource.getWorkspaceGroupChannelAccountsPage(
				_faroProject.getGroupId(), RandomTestUtil.randomString(),
				accountLifecycleId, lifecycleStage, null, null, null, null,
				Pagination.of(1, 20), null);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				accountResource, "_contactsEngineClient", contactsEngineClient);
		}

		return argumentsAtomicReference.get();
	}

	private void _testGetWorkspaceGroupAccountWithEmptyAccount()
		throws Exception {

		String accountId = RandomTestUtil.randomString();

		_contactsEngineHttpServer.addJSON(accountId, "{}");

		try {
			accountResource.getWorkspaceGroupAccount(
				_faroProject.getGroupId(), accountId);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertEquals(
				"No account was found with ID " + accountId,
				problem.getTitle());
			Assert.assertNull(problem.getType());
		}
	}

	private void _testGetWorkspaceGroupAccountWithMissingAccount()
		throws Exception {

		String accountId = RandomTestUtil.randomString();

		try {
			accountResource.getWorkspaceGroupAccount(
				_faroProject.getGroupId(), accountId);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertEquals(
				"No account was found with ID " + accountId,
				problem.getTitle());
			Assert.assertNull(problem.getType());
		}
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithAccountLifecycleId()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelAccountsPage(
			"2", "AT_RISK");

		Assert.assertEquals("2", objects[1]);
		Assert.assertEquals(
			"(lifecycleStatus@lifecycle-2 in ('stage-2'))", objects[4]);
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithEmptyAccountLifecycle()
		throws Exception {

		try {
			_getWorkspaceGroupChannelAccountsPage("3", "PIPELINE");

			Assert.fail();
		}
		catch (BadRequestException badRequestException) {
			Assert.assertEquals(
				"No account lifecycle stage was found with the name " +
					"\"PIPELINE\"",
				badRequestException.getMessage());
		}
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithLifecycleStage()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelAccountsPage(
			null, "AT_RISK");

		Assert.assertNull(objects[1]);
		Assert.assertEquals(
			"(lifecycleStatus@lifecycle-1 in ('stage-1'))", objects[4]);
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithUnknownLifecycleStage()
		throws Exception {

		try {
			_getWorkspaceGroupChannelAccountsPage("2", "PIPELINE");

			Assert.fail();
		}
		catch (BadRequestException badRequestException) {
			Assert.assertEquals(
				"No account lifecycle stage was found with the name " +
					"\"PIPELINE\". Valid stages: At Risk.",
				badRequestException.getMessage());
		}
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithoutLifecycleStage()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelAccountsPage(null, null);

		Assert.assertNull(objects[1]);
		Assert.assertNull(objects[4]);
	}

	@Inject
	private ContactsEngineClient _contactsEngineClient;

	private ContactsEngineHttpServer _contactsEngineHttpServer;
	private FaroProject _faroProject;

	@Inject
	private FaroProjectLocalService _faroProjectLocalService;

}