/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.liferay;

import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Christopher Kian
 */
public class ServerAuthorizationCodeGrantProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ClusterMasterExecutor clusterMasterExecutor = Mockito.mock(
			ClusterMasterExecutor.class);

		Mockito.when(
			clusterMasterExecutor.isEnabled()
		).thenReturn(
			false
		);

		ReflectionTestUtil.setFieldValue(
			_serverAuthorizationCodeGrantProvider, "_clusterMasterExecutor",
			clusterMasterExecutor);
	}

	@Test
	public void testRemoveServerAuthorizationCodeGrant() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(
			_THREADS_COUNT);

		try {
			for (int i = 0; i < _ATTEMPTS_COUNT; i++) {
				String code = RandomTestUtil.randomString();

				_serverAuthorizationCodeGrantProvider.
					putServerAuthorizationCodeGrant(
						new ServerAuthorizationCodeGrant(
							new Client(), code, 60,
							TimeUnit.MILLISECONDS.toSeconds(
								System.currentTimeMillis())));

				CyclicBarrier cyclicBarrier = new CyclicBarrier(_THREADS_COUNT);
				List<Future<ServerAuthorizationCodeGrant>> futures =
					new ArrayList<>();

				for (int j = 0; j < _THREADS_COUNT; j++) {
					futures.add(
						executorService.submit(
							() -> {
								cyclicBarrier.await();

								return _serverAuthorizationCodeGrantProvider.
									removeServerAuthorizationCodeGrant(code);
							}));
				}

				int removalsCount = 0;

				for (Future<ServerAuthorizationCodeGrant> future : futures) {
					if (future.get() == null) {
						continue;
					}

					removalsCount++;
				}

				Assert.assertEquals(1, removalsCount);
			}
		}
		finally {
			executorService.shutdownNow();
		}
	}

	private static final int _ATTEMPTS_COUNT = 1000;

	private static final int _THREADS_COUNT = 8;

	private final ServerAuthorizationCodeGrantProvider
		_serverAuthorizationCodeGrantProvider =
			new ServerAuthorizationCodeGrantProvider();

}