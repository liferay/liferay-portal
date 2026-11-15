/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.scaler.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.scaler.AMImageScaledImage;
import com.liferay.adaptive.media.image.scaler.AMImageScaler;
import com.liferay.adaptive.media.image.scaler.AMImageScalerRegistry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class AMImageScalerRegistryImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAMImageScalerRegistryReturnsAMDefaultImageScalerWhenRandomMimeType()
		throws Exception {

		_assertAMDefaultImageScaler(
			_amImageScalerRegistry.getAMImageScaler(
				RandomTestUtil.randomString()));
	}

	@Test
	public void testAMImageScalerRegistryReturnsAMDefaultImageScalerWhenThereIsNoAMImageScalerEnabled()
		throws Exception {

		ServiceRegistration<AMImageScaler> amImageScalerServiceRegistration =
			null;

		try {
			AMImageScaler disabledAMImageScaler = new TestAMImageScaler(false);

			amImageScalerServiceRegistration = _registerAMImageScaler(
				disabledAMImageScaler, "image/test", 10);

			_assertAMDefaultImageScaler(
				_amImageScalerRegistry.getAMImageScaler("image/test"));
		}
		finally {
			_unregisterAMImageScaler(amImageScalerServiceRegistration);
		}
	}

	@Test
	public void testAMImageScalerRegistryReturnsAMImageScalerForMimeType()
		throws Exception {

		AMImageScaler testAMImageScaler = new TestAMImageScaler(true);

		ServiceRegistration<AMImageScaler> amImageScalerServiceRegistration =
			null;

		try {
			amImageScalerServiceRegistration = _registerAMImageScaler(
				testAMImageScaler, "image/test", 10);

			Assert.assertEquals(
				testAMImageScaler,
				_amImageScalerRegistry.getAMImageScaler("image/test"));
		}
		finally {
			_unregisterAMImageScaler(amImageScalerServiceRegistration);
		}
	}

	@Test
	public void testAMImageScalerRegistryReturnsAMImageScalerWithHigherServiceRankingIfEnabled()
		throws Exception {

		ServiceRegistration<AMImageScaler> amImageScalerServiceRegistration1 =
			null;
		ServiceRegistration<AMImageScaler> amImageScalerServiceRegistration2 =
			null;
		ServiceRegistration<AMImageScaler> amImageScalerServiceRegistration3 =
			null;
		ServiceRegistration<AMImageScaler> amImageScalerServiceRegistration4 =
			null;
		ServiceRegistration<AMImageScaler> amImageScalerServiceRegistration5 =
			null;

		try {
			AMImageScaler enabledAMImageScaler1 = new TestAMImageScaler(true);

			amImageScalerServiceRegistration1 = _registerAMImageScaler(
				enabledAMImageScaler1, "image/test", 10);

			AMImageScaler disabledAMImageScaler2 = new TestAMImageScaler(false);

			amImageScalerServiceRegistration2 = _registerAMImageScaler(
				disabledAMImageScaler2, "image/test", 20);

			AMImageScaler enabledAMImageScaler3 = new TestAMImageScaler(true);

			amImageScalerServiceRegistration3 = _registerAMImageScaler(
				enabledAMImageScaler3, "image/test", 50);

			AMImageScaler disabledAMImageScaler4 = new TestAMImageScaler(false);

			amImageScalerServiceRegistration4 = _registerAMImageScaler(
				disabledAMImageScaler4, "image/test", 30);

			AMImageScaler enabledAMImageScaler5 = new TestAMImageScaler(true);

			amImageScalerServiceRegistration5 = _registerAMImageScaler(
				enabledAMImageScaler5, "image/test", 40);

			Assert.assertEquals(
				enabledAMImageScaler3,
				_amImageScalerRegistry.getAMImageScaler("image/test"));
		}
		finally {
			_unregisterAMImageScaler(amImageScalerServiceRegistration1);
			_unregisterAMImageScaler(amImageScalerServiceRegistration2);
			_unregisterAMImageScaler(amImageScalerServiceRegistration3);
			_unregisterAMImageScaler(amImageScalerServiceRegistration4);
			_unregisterAMImageScaler(amImageScalerServiceRegistration5);
		}
	}

	private void _assertAMDefaultImageScaler(AMImageScaler amImageScaler) {
		Class<?> clazz = amImageScaler.getClass();

		Assert.assertEquals(
			"com.liferay.adaptive.media.image.internal.scaler." +
				"AMDefaultImageScaler",
			clazz.getName());
	}

	private ServiceRegistration<AMImageScaler> _registerAMImageScaler(
			AMImageScaler amImageScaler, String mimeType, int serviceRanking)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			_amImageScalerRegistry.getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("mimeTypes", mimeType);
		properties.put("service.ranking", serviceRanking);

		return bundleContext.registerService(
			AMImageScaler.class, amImageScaler, properties);
	}

	private void _unregisterAMImageScaler(
		ServiceRegistration<AMImageScaler> amImageScalerServiceRegistration) {

		if (amImageScalerServiceRegistration == null) {
			return;
		}

		amImageScalerServiceRegistration.unregister();
	}

	@Inject
	private AMImageScalerRegistry _amImageScalerRegistry;

	private class TestAMImageScaler implements AMImageScaler {

		public TestAMImageScaler(boolean enabled) {
			_enabled = enabled;
		}

		@Override
		public boolean isEnabled() {
			return _enabled;
		}

		@Override
		public AMImageScaledImage scaleImage(
			FileVersion fileVersion,
			AMImageConfigurationEntry amImageConfigurationEntry) {

			return null;
		}

		private final boolean _enabled;

	}

}