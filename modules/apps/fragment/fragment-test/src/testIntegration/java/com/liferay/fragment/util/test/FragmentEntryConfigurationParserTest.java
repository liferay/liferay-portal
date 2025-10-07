/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class FragmentEntryConfigurationParserTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetConfigurationDefaultValuesJSONObject() throws Exception {
		JSONObject configurationDefaultValuesJSONObject =
			_fragmentEntryConfigurationParser.
				getConfigurationDefaultValuesJSONObject(
					_readJSONObject("configuration.json"));

		JSONObject expectedConfigurationDefaultValuesJSONObject =
			_readJSONObject("expected-configuration-default-values.json");

		Assert.assertEquals(
			expectedConfigurationDefaultValuesJSONObject.toString(),
			configurationDefaultValuesJSONObject.toString());
	}

	@Test
	public void testTranslateConfigurationEn() throws Exception {
		_testTranslateConfiguration("en");
	}

	@Test
	public void testTranslateConfigurationEs() throws Exception {
		_testTranslateConfiguration("es");
	}

	private ResourceBundle _getResourceBundle(String language) {
		Class<?> clazz = getClass();

		Package pkg = clazz.getPackage();

		return ResourceBundleUtil.getBundle(
			pkg.getName() + ".dependencies.content.Language",
			new Locale(language), clazz);
	}

	private JSONObject _readJSONObject(String fileName) throws Exception {
		return JSONFactoryUtil.createJSONObject(
			new String(
				FileUtil.getBytes(getClass(), "dependencies/" + fileName)));
	}

	private void _testTranslateConfiguration(String language) throws Exception {
		JSONObject configurationJSONObject = _readJSONObject(
			"configuration_untranslated.json");

		JSONObject expectedConfigurationTranslatedJSONObject = _readJSONObject(
			String.format(
				"expected_configuration_translated_%s.json", language));

		Assert.assertEquals(
			expectedConfigurationTranslatedJSONObject.toString(),
			String.valueOf(
				_fragmentEntryConfigurationParser.translateConfiguration(
					configurationJSONObject, _getResourceBundle(language))));
	}

	@Inject
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

}