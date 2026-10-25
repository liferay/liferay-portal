/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.internal.info.item.provider;

import com.liferay.info.exception.CapabilityVerificationException;
import com.liferay.info.item.capability.InfoItemCapability;
import com.liferay.info.item.provider.InfoItemCapabilitiesProvider;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public class TemplateInfoItemCapabilitiesProviderTrackerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testActivate() throws Exception {
		InfoItemCapability infoItemCapability = new InfoItemCapability() {

			@Override
			public String getKey() {
				return RandomTestUtil.randomString();
			}

			@Override
			public String getLabel(Locale locale) {
				return RandomTestUtil.randomString();
			}

			@Override
			public void verify(String itemClassName)
				throws CapabilityVerificationException {
			}

		};

		InfoItemCapabilitiesProvider<Object> infoItemCapabilitiesProvider =
			() -> Collections.singletonList(infoItemCapability);

		ServiceRegistration<?> serviceRegistration =
			(ServiceRegistration<?>)ProxyUtil.newProxyInstance(
				ServiceRegistration.class.getClassLoader(),
				new Class<?>[] {ServiceRegistration.class},
				(proxy, method, arguments) -> null);

		List<Dictionary<String, Object>> dictionaries = new ArrayList<>();
		List<ServiceListener> serviceListeners = new ArrayList<>();

		BundleContext bundleContext = (BundleContext)ProxyUtil.newProxyInstance(
			BundleContext.class.getClassLoader(),
			new Class<?>[] {BundleContext.class},
			(proxy, method, arguments) -> {
				String methodName = method.getName();

				if (methodName.equals("addServiceListener")) {
					serviceListeners.add((ServiceListener)arguments[0]);

					return null;
				}

				if (methodName.equals("createFilter")) {
					return FrameworkUtil.createFilter((String)arguments[0]);
				}

				if (methodName.equals("getService")) {
					return infoItemCapabilitiesProvider;
				}

				if (methodName.equals("registerService")) {
					dictionaries.add((Dictionary<String, Object>)arguments[2]);

					return serviceRegistration;
				}

				return null;
			});

		TemplateInfoItemCapabilitiesProviderTracker
			templateInfoItemCapabilitiesProviderTracker =
				new TemplateInfoItemCapabilitiesProviderTracker();

		ReflectionTestUtil.setFieldValue(
			templateInfoItemCapabilitiesProviderTracker,
			"_templateInfoItemCapability", infoItemCapability);

		templateInfoItemCapabilitiesProviderTracker.activate(bundleContext);

		Assert.assertEquals(
			serviceListeners.toString(), 1, serviceListeners.size());

		ServiceListener serviceListener = serviceListeners.get(0);

		ServiceReference<?> serviceReference =
			(ServiceReference<?>)ProxyUtil.newProxyInstance(
				ServiceReference.class.getClassLoader(),
				new Class<?>[] {ServiceReference.class},
				(proxy, method, arguments) -> {
					String methodName = method.getName();

					if (methodName.equals("equals")) {
						return proxy == arguments[0];
					}

					if (methodName.equals("getProperty")) {
						if (arguments[0].equals("item.class.name")) {
							return RandomTestUtil.randomString();
						}

						return null;
					}

					if (methodName.equals("hashCode")) {
						return System.identityHashCode(proxy);
					}

					return null;
				});

		serviceListener.serviceChanged(
			new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));

		Assert.assertEquals(dictionaries.toString(), 1, dictionaries.size());

		Dictionary<String, Object> dictionary = dictionaries.get(0);

		Assert.assertEquals(
			dictionary.toString(), 200,
			dictionary.get(Constants.SERVICE_RANKING));
	}

}