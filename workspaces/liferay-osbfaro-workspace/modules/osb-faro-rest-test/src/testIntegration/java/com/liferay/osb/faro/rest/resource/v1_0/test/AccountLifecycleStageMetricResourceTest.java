/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.AccountLifecycle;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageMetric;
import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleStageMetricResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class AccountLifecycleStageMetricResourceTest
	extends BaseAccountLifecycleStageMetricResourceTestCase {

	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPage()
		throws Exception {

		_testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithAccountLifecycleOfAnotherChannel();
		_testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithFilters();
		_testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithoutAccountLifecycleStageMetrics();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithPagination()
		throws Exception {

		super.
			testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithPagination();
	}

	private Page<AccountLifecycleStageMetric>
			_getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
				AtomicReference<Object[]> atomicReference,
				boolean channelAccountLifecycle, String country,
				String industry, Long segmentId)
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

					atomicReference.set(arguments);

					return null;
				});

		AccountLifecycleStageMetricResource
			accountLifecycleStageMetricResource =
				ReflectionTestUtil.getFieldValue(
					this, "_accountLifecycleStageMetricResource");

		Object accountLifecycleHelper = ReflectionTestUtil.getFieldValue(
			accountLifecycleStageMetricResource, "_accountLifecycleHelper");

		ContactsEngineClient accountLifecycleHelperContactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				accountLifecycleHelper, "_contactsEngineClient",
				proxyContactsEngineClient);

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				accountLifecycleStageMetricResource, "_contactsEngineClient",
				proxyContactsEngineClient);
		FaroProjectLocalService faroProjectLocalService =
			ReflectionTestUtil.getAndSetFieldValue(
				accountLifecycleStageMetricResource, "_faroProjectLocalService",
				(FaroProjectLocalService)ProxyUtil.newProxyInstance(
					FaroProjectLocalService.class.getClassLoader(),
					new Class<?>[] {FaroProjectLocalService.class},
					(proxy, method, arguments) -> null));

		try {
			return accountLifecycleStageMetricResource.
				getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
					testGroup.getGroupId(),
					String.valueOf(RandomTestUtil.randomLong()),
					_ACCOUNT_LIFECYCLE_ID, country, industry, segmentId,
					Pagination.of(1, 20));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				accountLifecycleHelper, "_contactsEngineClient",
				accountLifecycleHelperContactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				accountLifecycleStageMetricResource, "_contactsEngineClient",
				contactsEngineClient);
			ReflectionTestUtil.setFieldValue(
				accountLifecycleStageMetricResource, "_faroProjectLocalService",
				faroProjectLocalService);
		}
	}

	private void _testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithAccountLifecycleOfAnotherChannel()
		throws Exception {

		try {
			_getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
				new AtomicReference<>(), false, null, null, null);

			Assert.fail();
		}
		catch (NotFoundException notFoundException) {
			Assert.assertEquals(
				"No account lifecycle was found with ID " +
					_ACCOUNT_LIFECYCLE_ID,
				notFoundException.getMessage());
		}
	}

	private void _testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithFilters()
		throws Exception {

		AtomicReference<Object[]> atomicReference = new AtomicReference<>();
		String country = RandomTestUtil.randomString();
		String industry = RandomTestUtil.randomString();
		long segmentId = RandomTestUtil.randomLong();

		_getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
			atomicReference, true, country, industry, segmentId);

		Object[] objects = atomicReference.get();

		Assert.assertEquals(country, objects[1]);
		Assert.assertEquals(_ACCOUNT_LIFECYCLE_ID, objects[2]);
		Assert.assertEquals(industry, objects[3]);
		Assert.assertEquals(segmentId, objects[4]);
	}

	private void _testGetWorkspaceGroupChannelAccountLifecycleStageMetricsPageWithoutAccountLifecycleStageMetrics()
		throws Exception {

		Page<AccountLifecycleStageMetric> accountLifecycleStageMetricsPage =
			_getWorkspaceGroupChannelAccountLifecycleStageMetricsPage(
				new AtomicReference<>(), true, null, null, null);

		Assert.assertEquals(
			Collections.emptyList(),
			(List<AccountLifecycleStageMetric>)
				accountLifecycleStageMetricsPage.getItems());
		Assert.assertEquals(
			0, accountLifecycleStageMetricsPage.getTotalCount());
	}

	private static final String _ACCOUNT_LIFECYCLE_ID = String.valueOf(
		RandomTestUtil.randomLong());

}