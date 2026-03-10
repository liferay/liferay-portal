/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.consent.management.platform.integration.configuration.ConsentManagementPlatformConfiguration;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.net.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@FeatureFlag("LPD-65299")
@RunWith(Arquillian.class)
public class ConsentManagementPlatformTopHeadDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testIncludeScriptTag() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			ConsentManagementPlatformConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"enabled", true
			).put(
				"providerName", RandomTestUtil.randomString()
			).put(
				"scriptTag", _SCRIPT_TAG
			).build());

		Matcher matcher = _pattern.matcher(
			URLUtil.toString(new URL("http://localhost:8080")));

		Assert.assertTrue(matcher.find());

		String group = matcher.group(0);

		Assert.assertTrue(group.contains(_SCRIPT_TAG));
	}

	private static final String _SCRIPT_TAG =
		"<script data-cbid=\"000000\" id=\"Cookiebot\" " +
			"src=\"https://consent.cookiebot.com/uc.js\" " +
				"type=\"text/javascript\"></script>";

	private static final Pattern _pattern = Pattern.compile(
		"<script\\b[^>]*>(.*?)</script>", Pattern.DOTALL);

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.consent.management.platform.integration.internal.servlet.taglib.ConsentManagementPlatformTopHeadDynamicInclude"
	)
	private DynamicInclude _dynamicInclude;

}