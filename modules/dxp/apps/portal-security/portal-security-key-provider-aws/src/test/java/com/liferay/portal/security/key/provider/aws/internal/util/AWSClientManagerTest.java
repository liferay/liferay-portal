/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.util;

import com.amazonaws.AmazonWebServiceClient;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Christopher Kian
 */
public class AWSClientManagerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCloseShutsDownClient() throws Exception {
		AmazonWebServiceClient amazonWebServiceClient = Mockito.mock(
			AmazonWebServiceClient.class);

		AWSClientManager<AmazonWebServiceClient> awsClientManager =
			new AWSClientManager<>(
				RandomTestUtil.randomString(),
				(awsCredentialsProvider, awsRegion, endpointConfiguration) ->
					amazonWebServiceClient,
				RandomTestUtil.randomString(), false);

		awsClientManager.execute(client -> client);

		awsClientManager.close();

		Mockito.verify(
			amazonWebServiceClient
		).shutdown();
	}

	@Test
	public void testConstructorResolvesRegionFromSupplier() throws Exception {
		String expectedAWSRegion = RandomTestUtil.randomString();

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			null, () -> expectedAWSRegion,
			(awsCredentialsProvider, awsRegion, endpointConfiguration) ->
				new Object(),
			RandomTestUtil.randomString(), false);

		Assert.assertEquals(expectedAWSRegion, awsClientManager.getAWSRegion());
	}

	@Test(expected = IllegalStateException.class)
	public void testExecuteAfterCloseThrows() throws Exception {
		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			RandomTestUtil.randomString(),
			(awsCredentialsProvider, awsRegion, endpointConfiguration) ->
				new Object(),
			RandomTestUtil.randomString(), false);

		awsClientManager.close();

		awsClientManager.execute(client -> client);
	}

	@Test
	public void testExecuteBuildsClientOnceAndReuses() throws Exception {
		AtomicInteger builds = new AtomicInteger();

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			RandomTestUtil.randomString(),
			(awsCredentialsProvider, awsRegion, endpointConfiguration) -> {
				builds.incrementAndGet();

				return new Object();
			},
			RandomTestUtil.randomString(), false);

		awsClientManager.execute(client -> client);
		awsClientManager.execute(client -> client);

		Assert.assertEquals(1, builds.get());
	}

	@Test
	public void testUpdateConfigurationRebuildsOnRegionChange()
		throws Exception {

		AtomicInteger builds = new AtomicInteger();

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			RandomTestUtil.randomString(),
			(awsCredentialsProvider, awsRegion, endpointConfiguration) -> {
				builds.incrementAndGet();

				return new Object();
			},
			RandomTestUtil.randomString(), false);

		awsClientManager.execute(client -> client);

		String updatedAWSRegion = RandomTestUtil.randomString();

		awsClientManager.updateConfiguration(updatedAWSRegion, false);

		awsClientManager.execute(client -> client);

		Assert.assertEquals(2, builds.get());
		Assert.assertEquals(updatedAWSRegion, awsClientManager.getAWSRegion());
	}

	@Test
	public void testUpdateConfigurationReusesClientOnSameRegion()
		throws Exception {

		AtomicInteger builds = new AtomicInteger();

		String unchangedAWSRegion = RandomTestUtil.randomString();

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			unchangedAWSRegion,
			(awsCredentialsProvider, awsRegion, endpointConfiguration) -> {
				builds.incrementAndGet();

				return new Object();
			},
			RandomTestUtil.randomString(), false);

		awsClientManager.execute(client -> client);

		awsClientManager.updateConfiguration(unchangedAWSRegion, false);

		awsClientManager.execute(client -> client);

		Assert.assertEquals(1, builds.get());
	}

	@Test
	public void testUpdateConfigurationToleratesUnresolvableRegion()
		throws Exception {

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			RandomTestUtil.randomString(), () -> null,
			(awsCredentialsProvider, awsRegion, endpointConfiguration) ->
				new Object(),
			RandomTestUtil.randomString(), false);

		awsClientManager.updateConfiguration(null, false);

		Assert.assertNull(awsClientManager.getAWSRegion());
	}

}