/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.helper;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Pinheiro
 */
public class AccountLifecycleHelperTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_accountLifecycleHelper, "_contactsEngineClient",
			_contactsEngineClient);
	}

	@Test
	public void testGetAccountLifecycle() {
		AccountLifecycle accountLifecycle = _mockAccountLifecycle(
			RandomTestUtil.randomString());

		_mockAccountLifecycles(
			accountLifecycle, new AccountLifecycle[] {accountLifecycle},
			_CHANNEL_ID);

		Assert.assertSame(
			accountLifecycle,
			_accountLifecycleHelper.getAccountLifecycle(
				accountLifecycle.getId(), String.valueOf(_CHANNEL_ID),
				_faroProject));

		_testGetAccountLifecycleWithAnotherChannel();
		_testGetAccountLifecycleWithEngineError();
		_testGetAccountLifecycleWithNonexistentAccountLifecycle();
	}

	private void _assertNotFoundException(
		String accountLifecycleId, String channelId) {

		try {
			_accountLifecycleHelper.getAccountLifecycle(
				accountLifecycleId, channelId, _faroProject);

			Assert.fail();
		}
		catch (NotFoundException notFoundException) {
			String expectedTitle =
				"No account lifecycle was found with ID " + accountLifecycleId;

			Assert.assertEquals(expectedTitle, notFoundException.getMessage());

			Response response = notFoundException.getResponse();

			Problem problem = (Problem)response.getEntity();

			Assert.assertEquals(Response.Status.NOT_FOUND, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private AccountLifecycle _mockAccountLifecycle(String accountLifecycleId) {
		AccountLifecycle accountLifecycle = new AccountLifecycle();

		accountLifecycle.setExternalReferenceCode(
			RandomTestUtil.randomString());
		accountLifecycle.setId(accountLifecycleId);

		Mockito.when(
			_contactsEngineClient.getAccountLifecycle(
				_faroProject, accountLifecycleId)
		).thenReturn(
			accountLifecycle
		);

		return accountLifecycle;
	}

	private Results<AccountLifecycle> _mockAccountLifecycles(
		AccountLifecycle accountLifecycle, AccountLifecycle[] accountLifecycles,
		long channelId) {

		Results<AccountLifecycle> results = new Results<>(
			Arrays.asList(accountLifecycles), accountLifecycles.length);

		Mockito.when(
			_contactsEngineClient.getAccountLifecycles(
				_faroProject, channelId, null,
				accountLifecycle.getExternalReferenceCode(), null, 1, 1, null)
		).thenReturn(
			results
		);

		return results;
	}

	private void _testGetAccountLifecycleWithAnotherChannel() {
		AccountLifecycle accountLifecycle = _mockAccountLifecycle(
			RandomTestUtil.randomString());

		_mockAccountLifecycles(
			accountLifecycle, new AccountLifecycle[0], _CHANNEL_ID);

		_assertNotFoundException(
			accountLifecycle.getId(), String.valueOf(_CHANNEL_ID));

		_mockAccountLifecycles(
			accountLifecycle,
			new AccountLifecycle[] {
				_mockAccountLifecycle(RandomTestUtil.randomString())
			},
			_CHANNEL_ID);

		_assertNotFoundException(
			accountLifecycle.getId(), String.valueOf(_CHANNEL_ID));
	}

	private void _testGetAccountLifecycleWithEngineError() {
		String accountLifecycleId = RandomTestUtil.randomString();
		FaroEngineClientException faroEngineClientException1 =
			new FaroEngineClientException(RandomTestUtil.randomString(), 500);

		Mockito.when(
			_contactsEngineClient.getAccountLifecycle(
				_faroProject, accountLifecycleId)
		).thenThrow(
			faroEngineClientException1
		);

		try {
			_accountLifecycleHelper.getAccountLifecycle(
				accountLifecycleId, String.valueOf(_CHANNEL_ID), _faroProject);

			Assert.fail();
		}
		catch (FaroEngineClientException faroEngineClientException2) {
			Assert.assertSame(
				faroEngineClientException1, faroEngineClientException2);
		}
	}

	private void _testGetAccountLifecycleWithNonexistentAccountLifecycle() {
		String accountLifecycleId1 = RandomTestUtil.randomString();

		Mockito.when(
			_contactsEngineClient.getAccountLifecycle(
				_faroProject, accountLifecycleId1)
		).thenThrow(
			new NoSuchEntryException(RandomTestUtil.randomString())
		);

		_assertNotFoundException(
			accountLifecycleId1, String.valueOf(_CHANNEL_ID));

		String accountLifecycleId2 = RandomTestUtil.randomString();

		Mockito.when(
			_contactsEngineClient.getAccountLifecycle(
				_faroProject, accountLifecycleId2)
		).thenThrow(
			new FaroEngineClientException(RandomTestUtil.randomString(), 400)
		);

		_assertNotFoundException(
			accountLifecycleId2, String.valueOf(_CHANNEL_ID));
	}

	private static final long _CHANNEL_ID = RandomTestUtil.randomLong();

	private final AccountLifecycleHelper _accountLifecycleHelper =
		new AccountLifecycleHelper();
	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);

}