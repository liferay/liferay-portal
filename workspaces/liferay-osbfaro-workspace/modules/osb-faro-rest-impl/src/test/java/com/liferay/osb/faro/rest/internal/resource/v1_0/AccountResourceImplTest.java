/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStage;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.lang.reflect.Field;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class AccountResourceImplTest {

	@Before
	public void setUp() throws Exception {
		_setField(
			"_contactsEngineClient",
			ProxyUtil.newProxyInstance(
				ContactsEngineClient.class.getClassLoader(),
				new Class<?>[] {ContactsEngineClient.class},
				(proxy, method, args) -> {
					String methodName = method.getName();

					if (methodName.equals("getAccountLifecycle")) {
						return _accountLifecycles.get((String)args[1]);
					}

					if (methodName.equals("getAccountLifecycles")) {
						return Collections.singletonList(
							_accountLifecycles.get(
								_FIRST_ACCOUNT_LIFECYCLE_ID));
					}

					if (methodName.equals("getAccounts")) {
						_accountLifecycleId = (String)args[1];
						_filterString = (String)args[3];

						return new Results<>(Collections.emptyList(), 0);
					}

					throw new UnsupportedOperationException(methodName);
				}));
		_setField(
			"_faroProjectLocalService",
			ProxyUtil.newProxyInstance(
				FaroProjectLocalService.class.getClassLoader(),
				new Class<?>[] {FaroProjectLocalService.class},
				(proxy, method, args) -> null));

		_addAccountLifecycle(_FIRST_ACCOUNT_LIFECYCLE_ID);
		_addAccountLifecycle(_SECOND_ACCOUNT_LIFECYCLE_ID);
	}

	@Test
	public void testGetWorkspaceGroupChannelAccountsPage() throws Exception {
		_accountResourceImpl.getWorkspaceGroupChannelAccountsPage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(), null,
			null, null, null, null, null, Pagination.of(1, 20), null);

		Assert.assertNull(_accountLifecycleId);
		Assert.assertNull(_filterString);

		_accountResourceImpl.getWorkspaceGroupChannelAccountsPage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(), null,
			"AT_RISK", null, null, null, null, Pagination.of(1, 20), null);

		Assert.assertNull(_accountLifecycleId);
		Assert.assertEquals(
			StringBundler.concat(
				"(lifecycleStatus@lifecycle-", _FIRST_ACCOUNT_LIFECYCLE_ID,
				" in ('stage-", _FIRST_ACCOUNT_LIFECYCLE_ID, "'))"),
			_filterString);

		_accountResourceImpl.getWorkspaceGroupChannelAccountsPage(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			_SECOND_ACCOUNT_LIFECYCLE_ID, "AT_RISK", null, null, null, null,
			Pagination.of(1, 20), null);

		Assert.assertEquals(_SECOND_ACCOUNT_LIFECYCLE_ID, _accountLifecycleId);
		Assert.assertEquals(
			StringBundler.concat(
				"(lifecycleStatus@lifecycle-", _SECOND_ACCOUNT_LIFECYCLE_ID,
				" in ('stage-", _SECOND_ACCOUNT_LIFECYCLE_ID, "'))"),
			_filterString);

		try {
			_accountResourceImpl.getWorkspaceGroupChannelAccountsPage(
				RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
				_SECOND_ACCOUNT_LIFECYCLE_ID, "PIPELINE", null, null, null,
				null, Pagination.of(1, 20), null);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"No lifecycle stage was found with the name \"PIPELINE\"",
				illegalArgumentException.getMessage());
		}
	}

	private void _addAccountLifecycle(String accountLifecycleId) {
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

		_accountLifecycles.put(accountLifecycleId, accountLifecycle);
	}

	private void _setField(String name, Object value) throws Exception {
		Field field = AccountResourceImpl.class.getDeclaredField(name);

		field.setAccessible(true);

		field.set(_accountResourceImpl, value);
	}

	private static final String _FIRST_ACCOUNT_LIFECYCLE_ID = "1";

	private static final String _SECOND_ACCOUNT_LIFECYCLE_ID = "2";

	private String _accountLifecycleId;
	private final Map<String, AccountLifecycle> _accountLifecycles =
		new HashMap<>();
	private final AccountResourceImpl _accountResourceImpl =
		new AccountResourceImpl();
	private String _filterString;

}