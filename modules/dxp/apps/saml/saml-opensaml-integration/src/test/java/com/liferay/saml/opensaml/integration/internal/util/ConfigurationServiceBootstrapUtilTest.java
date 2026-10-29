/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.opensaml.integration.internal.bootstrap.ParserPoolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.shibboleth.utilities.java.support.xml.ParserPool;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;

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

	@After
	public void tearDown() {
		Map<Class<?>, Object> configurations = ReflectionTestUtil.getFieldValue(
			ConfigurationServiceBootstrapUtil.class, "_configurations");

		configurations.remove(SignatureSigningConfiguration.class);

		ReflectionTestUtil.setFieldValue(
			ConfigurationServiceBootstrapUtil.class, "_initialized", false);
	}

	@Test
	public void testGet() throws Exception {
		List<Callable<ParserPool>> callables = new ArrayList<>();

		int count = 10;

		CyclicBarrier cyclicBarrier = new CyclicBarrier(count);

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
				callables, 1, TimeUnit.MINUTES);

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
		SignatureSigningConfiguration signatureSigningConfiguration =
			new BasicSignatureSigningConfiguration();

		ConfigurationServiceBootstrapUtil.register(
			SignatureSigningConfiguration.class, signatureSigningConfiguration);

		Assert.assertSame(
			signatureSigningConfiguration,
			ConfigurationServiceBootstrapUtil.get(
				SignatureSigningConfiguration.class));

		signatureSigningConfiguration =
			new BasicSignatureSigningConfiguration();

		ConfigurationServiceBootstrapUtil.register(
			SignatureSigningConfiguration.class, signatureSigningConfiguration);

		Assert.assertSame(
			signatureSigningConfiguration,
			ConfigurationServiceBootstrapUtil.get(
				SignatureSigningConfiguration.class));
	}

}