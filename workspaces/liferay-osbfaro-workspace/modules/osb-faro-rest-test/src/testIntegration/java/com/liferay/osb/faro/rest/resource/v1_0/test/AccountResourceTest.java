/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStage;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.resource.v1_0.AccountResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class AccountResourceTest extends BaseAccountResourceTestCase {

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupAccount() throws Exception {
		super.testGetWorkspaceGroupAccount();
	}

	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountsPage() throws Exception {
		_testGetWorkspaceGroupChannelAccountsPageWithAccountLifecycleId();
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

	@Ignore
	@Override
	@Test
	public void testGraphQLGetWorkspaceGroupAccount() throws Exception {
		super.testGraphQLGetWorkspaceGroupAccount();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetWorkspaceGroupAccountNotFound() throws Exception {
		super.testGraphQLGetWorkspaceGroupAccountNotFound();
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
		accountLifecycleStage.setStageType("AT_RISK");

		accountLifecycle.setStages(
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
							return Collections.singletonList(accountLifecycle);
						}

						argumentsAtomicReference.set(arguments);

						return new Results<>();
					}));
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				accountResource, "_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		try {
			accountResource.getWorkspaceGroupChannelAccountsPage(
				testGroup.getGroupId(), RandomTestUtil.randomString(),
				accountLifecycleId, lifecycleStage, null, null, null, null,
				Pagination.of(1, 20), null);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				accountResource, "_contactsEngineClient", contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				accountResource, "_faroProjectLocalService",
				faroProjectLocalService);
		}

		return argumentsAtomicReference.get();
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithAccountLifecycleId()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelAccountsPage(
			"2", "AT_RISK");

		Assert.assertEquals("2", objects[1]);
		Assert.assertEquals(
			"(lifecycleStatus@lifecycle-2 in ('stage-2'))", objects[3]);
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithLifecycleStage()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelAccountsPage(
			null, "AT_RISK");

		Assert.assertNull(objects[1]);
		Assert.assertEquals(
			"(lifecycleStatus@lifecycle-1 in ('stage-1'))", objects[3]);
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithUnknownLifecycleStage()
		throws Exception {

		try {
			_getWorkspaceGroupChannelAccountsPage("2", "PIPELINE");

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"No lifecycle stage was found with the name \"PIPELINE\"",
				illegalArgumentException.getMessage());
		}
	}

	private void _testGetWorkspaceGroupChannelAccountsPageWithoutLifecycleStage()
		throws Exception {

		Object[] objects = _getWorkspaceGroupChannelAccountsPage(null, null);

		Assert.assertNull(objects[1]);
		Assert.assertNull(objects[3]);
	}

}