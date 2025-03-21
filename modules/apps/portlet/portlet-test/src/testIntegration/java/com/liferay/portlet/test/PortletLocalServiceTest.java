/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.BaseCustomAttributesDisplay;
import com.liferay.expando.kernel.model.CustomAttributesDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.Portlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class PortletLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetCustomAttributesDisplaysWithCustomAttributesDisplayDisabled()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		TestCustomAttributesDisplay disabledFFCustomAttributesDisplay =
			new TestCustomAttributesDisplay(RandomTestUtil.randomString());
		String portletName = RandomTestUtil.randomString();
		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

		try {
			serviceRegistrations.add(
				bundleContext.registerService(
					CustomAttributesDisplay.class,
					disabledFFCustomAttributesDisplay,
					MapUtil.singletonDictionary(
						"jakarta.portlet.name", portletName)));

			String enabledFFKey = RandomTestUtil.randomString();

			PropsTestUtil.setProps(
				HashMapBuilder.<String, Object>put(
					PropsKeys.COMPANY_DEFAULT_WEB_ID,
					PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID)
				).put(
					"feature.flag." + enabledFFKey, Boolean.TRUE.toString()
				).build());

			TestCustomAttributesDisplay enabledFFCustomAttributesDisplay =
				new TestCustomAttributesDisplay(enabledFFKey);

			serviceRegistrations.add(
				bundleContext.registerService(
					CustomAttributesDisplay.class,
					enabledFFCustomAttributesDisplay,
					MapUtil.singletonDictionary(
						"jakarta.portlet.name", portletName)));

			TestCustomAttributesDisplay nullFFCustomAttributesDisplay =
				new TestCustomAttributesDisplay(null);

			serviceRegistrations.add(
				bundleContext.registerService(
					CustomAttributesDisplay.class,
					nullFFCustomAttributesDisplay,
					MapUtil.singletonDictionary(
						"jakarta.portlet.name", portletName)));

			serviceRegistrations.add(
				bundleContext.registerService(
					Portlet.class, new TestPortlet(),
					MapUtil.singletonDictionary(
						"jakarta.portlet.name", portletName)));

			Thread.sleep(200);

			List<CustomAttributesDisplay> customAttributesDisplays =
				TransformUtil.transform(
					_portletLocalService.getCustomAttributesDisplays(),
					customAttributesDisplay -> {
						if (Objects.equals(
								TestCustomAttributesDisplay.class.getName(),
								customAttributesDisplay.getClassName())) {

							return customAttributesDisplay;
						}

						return null;
					});

			Assert.assertFalse(
				customAttributesDisplays.contains(
					disabledFFCustomAttributesDisplay));
			Assert.assertTrue(
				customAttributesDisplays.contains(
					enabledFFCustomAttributesDisplay));
			Assert.assertTrue(
				customAttributesDisplays.contains(
					nullFFCustomAttributesDisplay));
			Assert.assertEquals(
				customAttributesDisplays.toString(), 2,
				customAttributesDisplays.size());
		}
		finally {
			PropsUtil.setProps(_props);

			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations) {

				serviceRegistration.unregister();
			}
		}
	}

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private Props _props;

	private class TestCustomAttributesDisplay
		extends BaseCustomAttributesDisplay {

		@Override
		public String getClassName() {
			return TestCustomAttributesDisplay.class.getName();
		}

		@Override
		public String getFeatureFlagKey() {
			return _featureFlagKey;
		}

		private TestCustomAttributesDisplay(String featureFlagKey) {
			_featureFlagKey = featureFlagKey;
		}

		private final String _featureFlagKey;

	}

	private class TestPortlet extends MVCPortlet {
	}

}