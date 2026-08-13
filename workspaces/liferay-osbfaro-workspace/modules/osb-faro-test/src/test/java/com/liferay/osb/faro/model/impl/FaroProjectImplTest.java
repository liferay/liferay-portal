/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model.impl;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.internal.configuration.ConfigurationFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shinn Lok
 */
public class FaroProjectImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ConfigurationFactoryUtil.setConfigurationFactory(
			new ConfigurationFactoryImpl());

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testIsAllowedIPAddress() throws Exception {
		FaroProject faroProject = new FaroProjectImpl();

		faroProject.setIpAddresses(_getIpAddresses("192.168.0.159/0"));

		Assert.assertTrue(faroProject.isAllowedIPAddress("1.2.3.4"));
		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.0.159"));

		faroProject.setIpAddresses(_getIpAddresses("192.168.1.0/24"));

		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.1.104"));
		Assert.assertFalse(faroProject.isAllowedIPAddress("192.168.0.104"));

		faroProject.setIpAddresses(
			_getIpAddresses("192.168.1.159", "192.168.1.161"));

		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.1.159"));
		Assert.assertFalse(faroProject.isAllowedIPAddress("192.168.1.160"));
		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.1.161"));

		faroProject.setIpAddresses(
			_getIpAddresses("192.168.0.159", "192.168.1.0/24"));

		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.0.159"));
		Assert.assertFalse(faroProject.isAllowedIPAddress("192.168.0.160"));
		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.1.99"));
	}

	@Test
	public void testIsDataPlatform() throws Exception {
		FaroProject faroProject = new FaroProjectImpl();

		faroProject.setSubscription(
			_getSubscription("Liferay Analytics Cloud Business"));

		Assert.assertFalse(faroProject.isDataPlatform());

		faroProject.setSubscription(
			_getSubscription("Liferay Data Platform (Private Beta)"));

		Assert.assertTrue(faroProject.isDataPlatform());

		faroProject.setSubscription(
			_getSubscription("Liferay Data Platform Enterprise"));

		Assert.assertTrue(faroProject.isDataPlatform());

		faroProject.setSubscription(_getSubscription("Liferay Data Platform"));

		Assert.assertTrue(faroProject.isDataPlatform());
	}

	private String _getIpAddresses(String... ipAddresses) {
		JSONArray jsonArray = JSONUtil.putAll(ipAddresses);

		return jsonArray.toString();
	}

	private String _getSubscription(String name) {
		JSONObject jsonObject = JSONUtil.put("name", name);

		return jsonObject.toString();
	}

}