/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.configuration.admin.definition.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.captcha.recaptcha.ReCaptchaImpl;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.configuration.admin.definition.ConfigurationFieldOptionsProvider;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class CaptchaConfigurationFieldOptionsProviderTest {

	@ClassRule
	@Rule
	public static final TestRule testRule = new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = _bundleContext.registerService(
			Captcha.class, new TestCaptcha(),
			HashMapDictionaryBuilder.<String, Object>put(
				"captcha.engine.impl",
				"com.liferay.captcha.configuration.admin.definition.test." +
					"CaptchaConfigurationFieldOptionsProviderTest$TestCaptcha"
			).build());

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			"com.liferay.captcha.internal.configuration." +
				"FunctionCaptchaImplConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"captchaName", RandomTestUtil.randomString()
			).put(
				"captchaResponseParameterName", RandomTestUtil.randomString()
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"customElementExternalReferenceCode",
				RandomTestUtil.randomString()
			).put(
				"oAuth2ApplicationExternalReferenceCode",
				RandomTestUtil.randomString()
			).put(
				"resourcePath", RandomTestUtil.randomString()
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_serviceRegistration.unregister();

		ConfigurationTestUtil.deleteConfiguration(_pid);
	}

	@Test
	public void testGetOptions() throws Exception {
		List<ConfigurationFieldOptionsProvider.Option> options =
			_configurationFieldOptionsProvider.getOptions();

		_assertContainsOption(new ReCaptchaImpl(), options);
		_assertContainsOption(new SimpleCaptchaImpl(), options);
		_assertContainsOption(new TestCaptcha(), options);

		ServiceReference<?>[] serviceReferences =
			_bundleContext.getServiceReferences(
				Captcha.class.getName(),
				"(component.name=com.liferay.captcha.internal.function." +
					"captcha.FunctionCaptchaImpl)");

		_assertContainsOption(
			(Captcha)_bundleContext.getService(serviceReferences[0]), options);
	}

	public static class TestCaptcha extends SimpleCaptchaImpl {

		@Override
		public String getName() {
			return "TestCaptcha";
		}

	}

	private void _assertContainsOption(
		Captcha captcha,
		List<ConfigurationFieldOptionsProvider.Option> options) {

		Assert.assertTrue(
			ListUtil.exists(
				options,
				option -> {
					Class<? extends Captcha> clazz = captcha.getClass();

					if (Objects.equals(
							captcha.getName(),
							option.getLabel(LocaleUtil.getDefault())) &&
						StringUtil.startsWith(
							option.getValue(), clazz.getName())) {

						return true;
					}

					return false;
				}));
	}

	private static BundleContext _bundleContext;
	private static String _pid;
	private static ServiceRegistration<Captcha> _serviceRegistration;

	@Inject(
		filter = "(&(configuration.pid=com.liferay.captcha.configuration.CaptchaConfiguration)(configuration.field.name=captchaEngine))"
	)
	private ConfigurationFieldOptionsProvider
		_configurationFieldOptionsProvider;

}