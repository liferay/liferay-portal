/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.messaging;

import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.sso.openid.connect.internal.constants.OpenIdConnectDestinationNames;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Jorge García Jiménez
 */
public class OpenIdConnectMessagingConfiguratorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testActivate() {
		OpenIdConnectMessagingConfigurator openIdConnectMessagingConfigurator =
			new OpenIdConnectMessagingConfigurator();

		DestinationFactory destinationFactory = Mockito.mock(
			DestinationFactory.class);

		Destination destination = Mockito.mock(Destination.class);
		String destinationName = RandomTestUtil.randomString();

		Mockito.when(
			destination.getName()
		).thenReturn(
			destinationName
		);

		Mockito.when(
			destinationFactory.createDestination(
				Mockito.any(DestinationConfiguration.class))
		).thenReturn(
			destination
		);

		ReflectionTestUtil.setFieldValue(
			openIdConnectMessagingConfigurator, "_destinationFactory",
			destinationFactory);

		BundleContext bundleContext = Mockito.mock(BundleContext.class);

		openIdConnectMessagingConfigurator.activate(bundleContext);

		ArgumentCaptor<DestinationConfiguration> argumentCaptor =
			ArgumentCaptor.forClass(DestinationConfiguration.class);

		Mockito.verify(
			destinationFactory
		).createDestination(
			argumentCaptor.capture()
		);

		DestinationConfiguration destinationConfiguration =
			argumentCaptor.getValue();

		Assert.assertEquals(1, destinationConfiguration.getMaximumQueueSize());
		Assert.assertEquals(
			DestinationConfiguration.DESTINATION_TYPE_SERIAL,
			destinationConfiguration.getDestinationType());
		Assert.assertEquals(
			OpenIdConnectDestinationNames.OPENID_CONNECT_TOKEN_REFRESH_BATCH,
			destinationConfiguration.getDestinationName());
		Assert.assertTrue(
			destinationConfiguration.getRejectedExecutionHandler() instanceof
				ThreadPoolExecutor.DiscardPolicy);

		Mockito.verify(
			bundleContext
		).registerService(
			Mockito.eq(Destination.class), Mockito.eq(destination),
			Mockito.argThat(
				dictionary -> Objects.equals(
					destinationName, dictionary.get("destination.name")))
		);
	}

}