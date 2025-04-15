/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.PortletConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class PortletConfigImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(PortletConfigImplTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testGetResourceBundleWithoutResourceBundleProperty() {
		_testGetResourceBundle(null);
	}

	@Test
	public void testGetResourceBundleWithResourceBundleProperty() {
		_testGetResourceBundle("content.Language");
	}

	private void _assertResourceBundle(
		ResourceBundle resourceBundle, Locale locale, String expectedValue) {

		Assert.assertEquals(locale, resourceBundle.getLocale());
		Assert.assertEquals(
			expectedValue,
			ResourceBundleUtil.getString(
				resourceBundle, TestResourceBundle.class.getName()));
	}

	private void _registerResourceBundle(Locale locale, String value) {
		_serviceRegistrations.add(
			_bundleContext.registerService(
				ResourceBundle.class, new TestResourceBundle(locale, value),
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, 1
				).put(
					"language.id", _language.getLanguageId(locale)
				).build()));
	}

	private void _testGetResourceBundle(String resourceBundle) {
		String portletId = RandomTestUtil.randomString();

		_serviceRegistrations.add(
			_bundleContext.registerService(
				jakarta.portlet.Portlet.class, new TestPortlet(),
				HashMapDictionaryBuilder.put(
					"jakarta.portlet.name", portletId
				).put(
					"jakarta.portlet.resource-bundle", resourceBundle
				).build()));

		Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);

		Assert.assertEquals(resourceBundle, portlet.getResourceBundle());

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, null);

		_assertResourceBundle(
			portletConfig.getResourceBundle(LocaleUtil.US), LocaleUtil.US,
			null);
		_assertResourceBundle(
			portletConfig.getResourceBundle(LocaleUtil.SPAIN), LocaleUtil.SPAIN,
			null);

		_registerResourceBundle(LocaleUtil.SPAIN, "value1");
		_registerResourceBundle(LocaleUtil.US, "value2");

		_assertResourceBundle(
			portletConfig.getResourceBundle(LocaleUtil.SPAIN), LocaleUtil.SPAIN,
			"value1");
		_assertResourceBundle(
			portletConfig.getResourceBundle(LocaleUtil.US), LocaleUtil.US,
			"value2");
	}

	private static BundleContext _bundleContext;

	@Inject
	private Language _language;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	private static class TestPortlet extends GenericPortlet {
	}

	private static class TestResourceBundle extends ResourceBundle {

		@Override
		public Enumeration<String> getKeys() {
			return Collections.enumeration(handleKeySet());
		}

		@Override
		public Locale getLocale() {
			return _locale;
		}

		@Override
		protected Object handleGetObject(String key) {
			if (key.equals(TestResourceBundle.class.getName())) {
				return _value;
			}

			return null;
		}

		@Override
		protected Set<String> handleKeySet() {
			return Collections.singleton(TestResourceBundle.class.getName());
		}

		private TestResourceBundle(Locale locale, String value) {
			_locale = locale;
			_value = value;
		}

		private final Locale _locale;
		private final String _value;

	}

}