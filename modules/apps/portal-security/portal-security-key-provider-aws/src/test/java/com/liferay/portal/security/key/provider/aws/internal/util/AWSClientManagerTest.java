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
				(awsCredentialsProvider, endpointConfiguration, region) ->
					amazonWebServiceClient,
				RandomTestUtil.randomString(), "us-east-1", false);

		awsClientManager.execute(client -> client);

		awsClientManager.close();

		Mockito.verify(
			amazonWebServiceClient
		).shutdown();
	}

	@Test
	public void testConstructorResolvesRegionFromSupplier() throws Exception {
		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			(awsCredentialsProvider, endpointConfiguration, region) ->
				new Object(),
			RandomTestUtil.randomString(), null, () -> "us-west-2", false);

		Assert.assertEquals("us-west-2", awsClientManager.getRegion());
	}

	@Test(expected = IllegalStateException.class)
	public void testExecuteAfterCloseThrows() throws Exception {
		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			(awsCredentialsProvider, endpointConfiguration, region) ->
				new Object(),
			RandomTestUtil.randomString(), "us-east-1", false);

		awsClientManager.close();

		awsClientManager.execute(client -> client);
	}

	@Test
	public void testExecuteBuildsClientOnceAndReuses() throws Exception {
		AtomicInteger builds = new AtomicInteger();

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			(awsCredentialsProvider, endpointConfiguration, region) -> {
				builds.incrementAndGet();

				return new Object();
			},
			RandomTestUtil.randomString(), "us-east-1", false);

		awsClientManager.execute(client -> client);
		awsClientManager.execute(client -> client);

		Assert.assertEquals(1, builds.get());
	}

	@Test
	public void testUpdateConfigurationRebuildsOnRegionChange()
		throws Exception {

		AtomicInteger builds = new AtomicInteger();

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			(awsCredentialsProvider, endpointConfiguration, region) -> {
				builds.incrementAndGet();

				return new Object();
			},
			RandomTestUtil.randomString(), "us-east-1", false);

		awsClientManager.execute(client -> client);

		awsClientManager.updateConfiguration("us-west-2", false);

		awsClientManager.execute(client -> client);

		Assert.assertEquals(2, builds.get());
		Assert.assertEquals("us-west-2", awsClientManager.getRegion());
	}

	@Test
	public void testUpdateConfigurationReusesClientOnSameRegion()
		throws Exception {

		AtomicInteger builds = new AtomicInteger();

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			(awsCredentialsProvider, endpointConfiguration, region) -> {
				builds.incrementAndGet();

				return new Object();
			},
			RandomTestUtil.randomString(), "us-east-1", false);

		awsClientManager.execute(client -> client);

		awsClientManager.updateConfiguration("us-east-1", false);

		awsClientManager.execute(client -> client);

		Assert.assertEquals(1, builds.get());
	}

	@Test
	public void testUpdateConfigurationToleratesUnresolvableRegion()
		throws Exception {

		AWSClientManager<Object> awsClientManager = new AWSClientManager<>(
			(awsCredentialsProvider, endpointConfiguration, region) ->
				new Object(),
			RandomTestUtil.randomString(), "us-east-1", () -> null, false);

		awsClientManager.updateConfiguration(null, false);

		Assert.assertNull(awsClientManager.getRegion());
	}

}