/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.filter.PropertyInfoItemServiceFilter;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;

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
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class InfoItemServiceRegistryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetFirstInfoItemService() throws Exception {
		String itemClassName = RandomTestUtil.randomString();

		ServiceRegistration<?> serviceRegistration1 =
			_registerInfoItemFieldValuesProvider(
				RandomTestUtil.nextLong(), itemClassName, object -> null);

		try {
			Assert.assertNull(
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class, itemClassName));

			InfoItemFieldValuesProvider<?> infoItemFieldValuesProvider =
				object -> null;

			ServiceRegistration<?> serviceRegistration2 =
				_registerInfoItemFieldValuesProvider(
					TestPropsValues.getCompanyId(), itemClassName,
					infoItemFieldValuesProvider);

			try {
				Assert.assertSame(
					infoItemFieldValuesProvider,
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFieldValuesProvider.class, itemClassName));
			}
			finally {
				serviceRegistration2.unregister();
			}
		}
		finally {
			serviceRegistration1.unregister();
		}
	}

	@Test
	public void testGetFirstInfoItemServiceWithInfoItemServiceFilter()
		throws Exception {

		String itemClassName = RandomTestUtil.randomString();
		String propertyValue1 = RandomTestUtil.randomString();

		ServiceRegistration<?> serviceRegistration1 =
			_registerInfoItemFieldValuesProvider(
				object -> null,
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", RandomTestUtil.nextLong()
				).put(
					"item.class.name", itemClassName
				).put(
					"service.ranking", 3
				).put(
					"test.property", propertyValue1
				).build());

		InfoItemFieldValuesProvider<?> infoItemFieldValuesProvider2 =
			object -> null;
		String propertyValue2 = RandomTestUtil.randomString();

		ServiceRegistration<?> serviceRegistration2 =
			_registerInfoItemFieldValuesProvider(
				infoItemFieldValuesProvider2,
				HashMapDictionaryBuilder.<String, Object>put(
					"item.class.name", itemClassName
				).put(
					"service.ranking", 2
				).put(
					"test.property", propertyValue2
				).build());

		InfoItemFieldValuesProvider<?> infoItemFieldValuesProvider3 =
			object -> null;

		ServiceRegistration<?> serviceRegistration3 =
			_registerInfoItemFieldValuesProvider(
				infoItemFieldValuesProvider3,
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", TestPropsValues.getCompanyId()
				).put(
					"item.class.name", itemClassName
				).put(
					"service.ranking", 1
				).put(
					"test.property", propertyValue1
				).build());

		try {
			Assert.assertSame(
				infoItemFieldValuesProvider2,
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class, itemClassName, null));
			Assert.assertSame(
				infoItemFieldValuesProvider3,
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class, itemClassName,
					new PropertyInfoItemServiceFilter(
						"test.property", propertyValue1)));
			Assert.assertSame(
				infoItemFieldValuesProvider2,
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class, itemClassName,
					new PropertyInfoItemServiceFilter(
						"test.property", propertyValue2)));
			Assert.assertNull(
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class, itemClassName,
					new PropertyInfoItemServiceFilter(
						"test.property", RandomTestUtil.randomString())));
			Assert.assertSame(
				infoItemFieldValuesProvider3,
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class, itemClassName,
					() -> "(test.property=" + propertyValue1 + ")"));
			Assert.assertNull(
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class, itemClassName,
					() ->
						"(test.property=" + RandomTestUtil.randomString() +
							")"));
		}
		finally {
			serviceRegistration1.unregister();
			serviceRegistration2.unregister();
			serviceRegistration3.unregister();
		}
	}

	private ServiceRegistration<?> _registerInfoItemFieldValuesProvider(
		InfoItemFieldValuesProvider<?> infoItemFieldValuesProvider,
		Dictionary<String, Object> properties) {

		Bundle bundle = FrameworkUtil.getBundle(
			InfoItemServiceRegistryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			InfoItemFieldValuesProvider.class, infoItemFieldValuesProvider,
			properties);
	}

	private ServiceRegistration<?> _registerInfoItemFieldValuesProvider(
		long companyId, String itemClassName,
		InfoItemFieldValuesProvider<?> infoItemFieldValuesProvider) {

		return _registerInfoItemFieldValuesProvider(
			infoItemFieldValuesProvider,
			HashMapDictionaryBuilder.<String, Object>put(
				"company.id", companyId
			).put(
				"item.class.name", itemClassName
			).build());
	}

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}