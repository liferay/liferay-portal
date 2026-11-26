/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.util;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class FaroNavigationUtilTest {

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testIsLDPEnabled() throws Exception {
		Assert.assertFalse(
			FaroNavigationUtil.isLDPEnabled(_mockFaroProject("")));
		Assert.assertFalse(
			FaroNavigationUtil.isLDPEnabled(
				_mockFaroProject(
					JSONUtil.put(
						"name", "Liferay Analytics Cloud"
					).toString())));
		Assert.assertTrue(
			FaroNavigationUtil.isLDPEnabled(
				_mockFaroProject(
					JSONUtil.put(
						"name", "Liferay Data Platform"
					).toString())));
		Assert.assertFalse(FaroNavigationUtil.isLDPEnabled((FaroProject)null));
	}

	private FaroProject _mockFaroProject(String subscription) {
		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getSubscription()
		).thenReturn(
			subscription
		);

		return faroProject;
	}

}