/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.type.virtual.util.VirtualCPTypeURLValidatorUtil;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.configuration.URLFetchSecurityCompanyConfiguration;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class VirtualCPTypeURLValidatorUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testIsValidURL() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Assert.assertFalse(
			VirtualCPTypeURLValidatorUtil.isValidURL(
				companyId, "file:///" + RandomTestUtil.randomString()));
		Assert.assertFalse(
			VirtualCPTypeURLValidatorUtil.isValidURL(
				companyId,
				"http://localhost/" + RandomTestUtil.randomString()));
		Assert.assertTrue(
			VirtualCPTypeURLValidatorUtil.isValidURL(
				companyId, "https://8.8.8.8/" + RandomTestUtil.randomString()));

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					URLFetchSecurityCompanyConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"urlLocalNetworkAccessEnabled", true
					).build())) {

			Assert.assertTrue(
				VirtualCPTypeURLValidatorUtil.isValidURL(
					companyId,
					"http://localhost/" + RandomTestUtil.randomString()));
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					URLFetchSecurityCompanyConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"urlHostsAllowed", new String[] {"localhost"}
					).put(
						"urlLocalNetworkAccessEnabled", true
					).build())) {

			Assert.assertFalse(
				VirtualCPTypeURLValidatorUtil.isValidURL(
					companyId,
					"http://127.0.0.1/" + RandomTestUtil.randomString()));
			Assert.assertTrue(
				VirtualCPTypeURLValidatorUtil.isValidURL(
					companyId,
					"https://localhost/" + RandomTestUtil.randomString()));
		}
	}

}