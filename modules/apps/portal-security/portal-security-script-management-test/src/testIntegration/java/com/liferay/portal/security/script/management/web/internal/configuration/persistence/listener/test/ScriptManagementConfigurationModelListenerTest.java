/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration;
import com.liferay.portal.security.script.management.groovy.script.use.GroovyScriptUse;
import com.liferay.portal.security.script.management.groovy.script.uses.factory.GroovyScriptUsesFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.ResourceRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Yuri Monteiro
 */
@RunWith(Arquillian.class)
public class ScriptManagementConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			ScriptManagementConfigurationModelListenerTest.class);

		_bundleContext = bundle.getBundleContext();

		_locale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);

		_serviceRegistration = _registerGroovyScriptUsesFactory();
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();

		LocaleThreadLocal.setThemeDisplayLocale(_locale);
	}

	@Test
	public void testOnBeforeDelete() throws Exception {
		try {
			_configurationModelListener.onBeforeDelete(
				ScriptManagementConfiguration.class.getName());

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {

			Assert.assertEquals(
				_language.get(LocaleUtil.US, _MESSAGE_KEY),
				configurationModelListenerException.causeMessage);
		}
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		_configurationModelListener.onBeforeSave(
			ScriptManagementConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"allowScriptContentToBeExecutedOrIncluded", true
			).build());

		try {
			_configurationModelListener.onBeforeSave(
				ScriptManagementConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"allowScriptContentToBeExecutedOrIncluded", false
				).build());

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {

			Assert.assertEquals(
				_language.get(LocaleUtil.US, _MESSAGE_KEY),
				configurationModelListenerException.causeMessage);
		}
	}

	private ServiceRegistration<GroovyScriptUsesFactory>
		_registerGroovyScriptUsesFactory() {

		return _bundleContext.registerService(
			GroovyScriptUsesFactory.class,
			new GroovyScriptUsesFactory() {

				@Override
				public List<GroovyScriptUse> create(
					ResourceRequest resourceRequest) {

					return Collections.emptyList();
				}

				@Override
				public boolean hasUses() {
					return true;
				}

			},
			null);
	}

	private static final String _MESSAGE_KEY =
		"resolve-all-active-scripting-uses-before-proceeding-you-can-" +
			"deactivate-the-source-entity-or-remove-the-script";

	private BundleContext _bundleContext;

	@Inject(
		filter = "model.class.name=com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration"
	)
	private ConfigurationModelListener _configurationModelListener;

	@Inject
	private Language _language;

	private Locale _locale;
	private ServiceRegistration<GroovyScriptUsesFactory> _serviceRegistration;

}