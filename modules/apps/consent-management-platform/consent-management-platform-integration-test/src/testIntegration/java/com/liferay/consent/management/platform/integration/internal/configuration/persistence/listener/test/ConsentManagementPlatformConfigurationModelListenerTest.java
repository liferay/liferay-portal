/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Jorge García Jiménez
 */
@RunWith(Arquillian.class)
public class ConsentManagementPlatformConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			ConsentManagementPlatformConfiguration.class.getName());
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		_saveConfiguration(_SCRIPT_TAG);

		Assert.assertEquals(_SCRIPT_TAG, _fetchScriptTag());
		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _saveConfiguration("<base href=\"https://liferay.com\">"));
		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _saveConfiguration(
				"<iframe src=\"https://liferay.com\"></iframe>"));
		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _saveConfiguration(
				"<meta content=\"0;url=https://liferay.com\" " +
					"http-equiv=\"refresh\">"));

		Assert.assertEquals(_SCRIPT_TAG, _fetchScriptTag());
	}

	private String _fetchScriptTag() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.pid=" +
				ConsentManagementPlatformConfiguration.class.getName() + ")");

		Configuration configuration = configurations[0];

		Dictionary<String, Object> properties = configuration.getProperties();

		return GetterUtil.getString(properties.get("scriptTag"));
	}

	private void _saveConfiguration(String scriptTag) throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			ConsentManagementPlatformConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"providerName", RandomTestUtil.randomString()
			).put(
				"scriptTag", scriptTag
			).build());
	}

	private static final String _SCRIPT_TAG = StringBundler.concat(
		"<link as=\"script\" href=\"https://consent.cookiebot.com/uc.js\" ",
		"rel=\"preload\"><script data-cbid=\"000000\" id=\"Cookiebot\" ",
		"src=\"https://consent.cookiebot.com/uc.js\" ",
		"type=\"text/javascript\"></script>");

	@Inject
	private ConfigurationAdmin _configurationAdmin;

}