/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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

	private ServiceRegistration<?> _registerInfoItemFieldValuesProvider(
		long companyId, String itemClassName,
		InfoItemFieldValuesProvider<?> infoItemFieldValuesProvider) {

		Bundle bundle = FrameworkUtil.getBundle(
			InfoItemServiceRegistryTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			InfoItemFieldValuesProvider.class, infoItemFieldValuesProvider,
			HashMapDictionaryBuilder.<String, Object>put(
				"company.id", companyId
			).put(
				"item.class.name", itemClassName
			).build());
	}

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}