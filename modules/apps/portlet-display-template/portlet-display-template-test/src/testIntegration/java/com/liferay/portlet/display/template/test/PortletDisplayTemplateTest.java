/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
public class PortletDisplayTemplateTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetPortletDisplayTemplateHandlers() {
		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

		try {
			Bundle bundle = FrameworkUtil.getBundle(getClass());

			BundleContext bundleContext = bundle.getBundleContext();

			String className1 = RandomTestUtil.randomString();

			serviceRegistrations.add(
				bundleContext.registerService(
					TemplateHandler.class,
					new TestPortletDisplayTemplateHandler(className1, false),
					MapUtil.singletonDictionary(
						"jakarta.portlet.name",
						RandomTestUtil.randomString())));

			String className2 = RandomTestUtil.randomString();

			serviceRegistrations.add(
				bundleContext.registerService(
					TemplateHandler.class,
					new TestPortletDisplayTemplateHandler(className2, true),
					MapUtil.singletonDictionary(
						"jakarta.portlet.name",
						RandomTestUtil.randomString())));

			String[] clasNames = TransformUtil.transformToArray(
				_portletDisplayTemplate.getPortletDisplayTemplateHandlers(),
				templateHandler -> {
					Assert.assertNotEquals(
						templateHandler.getClassName(), className1);

					if (Objects.equals(
							templateHandler.getClassName(), className2)) {

						return templateHandler.getClassName();
					}

					return null;
				},
				String.class);

			Assert.assertEquals(clasNames.toString(), 1, clasNames.length);
			Assert.assertEquals(className2, clasNames[0]);
		}
		finally {
			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations) {

				serviceRegistration.unregister();
			}
		}
	}

	@Inject
	private PortletDisplayTemplate _portletDisplayTemplate;

	private class TestPortletDisplayTemplateHandler
		extends BasePortletDisplayTemplateHandler {

		@Override
		public String getClassName() {
			return _className;
		}

		@Override
		public String getName(Locale locale) {
			return StringPool.BLANK;
		}

		@Override
		public String getResourceName() {
			return StringPool.BLANK;
		}

		@Override
		public boolean isEnabled(long companyId) {
			return _enabled;
		}

		private TestPortletDisplayTemplateHandler(
			String className, boolean enabled) {

			_className = className;
			_enabled = enabled;
		}

		private final String _className;
		private final boolean _enabled;

	}

}