/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleHistoryResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import jakarta.ws.rs.NotFoundException;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class AccountLifecycleHistoryResourceTest
	extends BaseAccountLifecycleHistoryResourceTestCase {

	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountAccountLifecycleHistory()
		throws Exception {

		AtomicReference<Object[]> atomicReference = new AtomicReference<>();

		_getWorkspaceGroupChannelAccountAccountLifecycleHistory(
			atomicReference, true, null);

		Object[] objects = atomicReference.get();

		Assert.assertEquals(_ACCOUNT_LIFECYCLE_ID, objects[1]);
		Assert.assertEquals(_ACCOUNT_ID, objects[2]);

		_assertNotFound(
			false,
			"No account lifecycle was found with ID " + _ACCOUNT_LIFECYCLE_ID,
			null);
		_assertNotFound(
			true, "No account was found with ID " + _ACCOUNT_ID,
			new FaroEngineClientException(RandomTestUtil.randomString(), 400));

		FaroEngineClientException faroEngineClientException1 =
			new FaroEngineClientException(RandomTestUtil.randomString(), 500);

		try {
			_getWorkspaceGroupChannelAccountAccountLifecycleHistory(
				new AtomicReference<>(), true, faroEngineClientException1);

			Assert.fail();
		}
		catch (FaroEngineClientException faroEngineClientException2) {
			Assert.assertSame(
				faroEngineClientException1, faroEngineClientException2);
		}
	}

	private void _assertNotFound(
			boolean channelAccountLifecycle, String expectedMessage,
			FaroEngineClientException faroEngineClientException)
		throws Exception {

		try {
			_getWorkspaceGroupChannelAccountAccountLifecycleHistory(
				new AtomicReference<>(), channelAccountLifecycle,
				faroEngineClientException);

			Assert.fail();
		}
		catch (NotFoundException notFoundException) {
			Assert.assertEquals(
				expectedMessage, notFoundException.getMessage());
		}
	}

	private void _getWorkspaceGroupChannelAccountAccountLifecycleHistory(
			AtomicReference<Object[]> atomicReference,
			boolean channelAccountLifecycle,
			FaroEngineClientException faroEngineClientException)
		throws Exception {

		AccountLifecycle accountLifecycle = new AccountLifecycle();

		accountLifecycle.setExternalReferenceCode(
			RandomTestUtil.randomString());
		accountLifecycle.setId(_ACCOUNT_LIFECYCLE_ID);

		ContactsEngineClient proxyContactsEngineClient =
			(ContactsEngineClient)ProxyUtil.newProxyInstance(
				ContactsEngineClient.class.getClassLoader(),
				new Class<?>[] {ContactsEngineClient.class},
				(proxy, method, arguments) -> {
					String methodName = method.getName();

					if (methodName.equals("getAccountLifecycle")) {
						return accountLifecycle;
					}

					if (methodName.equals("getAccountLifecycles")) {
						if (channelAccountLifecycle) {
							return new Results<>(
								Collections.singletonList(accountLifecycle), 1);
						}

						return new Results<>();
					}

					if (faroEngineClientException != null) {
						throw faroEngineClientException;
					}

					atomicReference.set(arguments);

					return null;
				});

		AccountLifecycleHistoryResource accountLifecycleHistoryResource =
			ReflectionTestUtil.getFieldValue(
				this, "_accountLifecycleHistoryResource");

		Object accountLifecycleHelper = ReflectionTestUtil.getFieldValue(
			accountLifecycleHistoryResource, "_accountLifecycleHelper");

		ContactsEngineClient accountLifecycleHelperContactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				accountLifecycleHelper, "_contactsEngineClient",
				proxyContactsEngineClient);

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				accountLifecycleHistoryResource, "_contactsEngineClient",
				proxyContactsEngineClient);
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				accountLifecycleHistoryResource, "_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		accountLifecycleHistoryResource.setContextAcceptLanguage(
			(AcceptLanguage)ProxyUtil.newProxyInstance(
				AcceptLanguage.class.getClassLoader(),
				new Class<?>[] {AcceptLanguage.class},
				(proxy, method, arguments) -> {
					if (method.getReturnType() == boolean.class) {
						return false;
					}

					return null;
				}));

		try {
			accountLifecycleHistoryResource.
				getWorkspaceGroupChannelAccountAccountLifecycleHistory(
					testGroup.getGroupId(),
					String.valueOf(RandomTestUtil.randomLong()), _ACCOUNT_ID,
					_ACCOUNT_LIFECYCLE_ID);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				accountLifecycleHelper, "_contactsEngineClient",
				accountLifecycleHelperContactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				accountLifecycleHistoryResource, "_contactsEngineClient",
				contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				accountLifecycleHistoryResource, "_faroProjectLocalService",
				faroProjectLocalService);

			accountLifecycleHistoryResource.setContextAcceptLanguage(null);
		}
	}

	private static final String _ACCOUNT_ID = RandomTestUtil.randomString();

	private static final String _ACCOUNT_LIFECYCLE_ID = String.valueOf(
		RandomTestUtil.randomLong());

}