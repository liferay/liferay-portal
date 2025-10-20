/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.Route;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Connor McKay
 */
public class RouteTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testNonmatchingRoute() {
		Map<String, String> parameters = HashMapBuilder.put(
			"action", "view"
		).put(
			"id", "bob"
		).build();

		Map<String, String> originalParameters = new HashMap<>(parameters);

		Route route = new Route("{action}/{id:\\d+}");

		String url = route.parametersToUrl(parameters);

		Assert.assertNull(url);

		Assert.assertEquals(originalParameters, parameters);
	}

}