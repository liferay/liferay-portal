/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.opensaml.integration.internal.bootstrap.ParserPoolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.shibboleth.utilities.java.support.xml.ParserPool;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.opensaml.core.xml.config.XMLObjectProviderRegistry;

/**
 * @author Jorge García Jiménez
 */
public class ConfigurationServiceBootstrapUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			ConfigurationServiceBootstrapUtil.class, "_initialized", false);
	}

	@Test
	public void testGet() throws Exception {
		int count = 10;

		CyclicBarrier cyclicBarrier = new CyclicBarrier(count);

		List<Callable<ParserPool>> callables = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			callables.add(
				() -> {
					cyclicBarrier.await();

					XMLObjectProviderRegistry xmlObjectProviderRegistry =
						ConfigurationServiceBootstrapUtil.get(
							XMLObjectProviderRegistry.class);

					return xmlObjectProviderRegistry.getParserPool();
				});
		}

		ExecutorService executorService = Executors.newFixedThreadPool(count);

		try {
			List<Future<ParserPool>> futures = executorService.invokeAll(
				callables);

			ParserPool parserPool = ParserPoolUtil.getParserPool();

			for (Future<ParserPool> future : futures) {
				Assert.assertSame(parserPool, future.get());
			}
		}
		finally {
			executorService.shutdownNow();
		}
	}

	@Test
	public void testRegister() {
		String configuration = RandomTestUtil.randomString();

		ConfigurationServiceBootstrapUtil.register(String.class, configuration);

		Assert.assertEquals(
			configuration, ConfigurationServiceBootstrapUtil.get(String.class));

		configuration = RandomTestUtil.randomString();

		ConfigurationServiceBootstrapUtil.register(String.class, configuration);

		Assert.assertEquals(
			configuration, ConfigurationServiceBootstrapUtil.get(String.class));
	}

}