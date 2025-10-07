/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model.impl;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.internal.configuration.ConfigurationFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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

		faroProject.setIpAddresses(
			JSONUtil.put(
				"192.168.0.159/0"
			).toString());

		Assert.assertTrue(faroProject.isAllowedIPAddress("1.2.3.4"));
		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.0.159"));

		faroProject.setIpAddresses(
			JSONUtil.put(
				"192.168.1.0/24"
			).toString());

		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.1.104"));
		Assert.assertFalse(faroProject.isAllowedIPAddress("192.168.0.104"));

		faroProject.setIpAddresses(
			JSONUtil.putAll(
				"192.168.1.159", "192.168.1.161"
			).toString());

		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.1.159"));
		Assert.assertFalse(faroProject.isAllowedIPAddress("192.168.1.160"));
		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.1.161"));

		faroProject.setIpAddresses(
			JSONUtil.putAll(
				"192.168.0.159", "192.168.1.0/24"
			).toString());

		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.0.159"));
		Assert.assertFalse(faroProject.isAllowedIPAddress("192.168.0.160"));
		Assert.assertTrue(faroProject.isAllowedIPAddress("192.168.1.99"));
	}

}