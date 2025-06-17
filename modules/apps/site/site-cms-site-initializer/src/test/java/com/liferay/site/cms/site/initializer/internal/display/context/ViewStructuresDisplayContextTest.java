/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class ViewStructuresDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAPIURL() {
		String apiURL = _viewStructuresDisplayContext.getAPIURL();

		int start = apiURL.indexOf("filter=");

		String filterString = apiURL.substring(start + 7);

		Assert.assertTrue(filterString.startsWith(StringPool.OPEN_PARENTHESIS));
		Assert.assertTrue(filterString.endsWith(StringPool.CLOSE_PARENTHESIS));
	}

	private final ViewStructuresDisplayContext _viewStructuresDisplayContext =
		new ViewStructuresDisplayContext(new MockHttpServletRequest());

}