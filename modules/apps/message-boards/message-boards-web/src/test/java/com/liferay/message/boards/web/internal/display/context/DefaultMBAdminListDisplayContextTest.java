/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Roberto Díaz
 */
public class DefaultMBAdminListDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetEmptyResultsMessage() throws Exception {
		DefaultMBAdminListDisplayContext defaultMBAdminListDisplayContext =
			new DefaultMBAdminListDisplayContext(
				new MockHttpServletRequest(), 0);

		Assert.assertEquals(
			"there-are-no-threads-or-categories",
			defaultMBAdminListDisplayContext.getEmptyResultsMessage());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("keywords", "test");

		defaultMBAdminListDisplayContext = new DefaultMBAdminListDisplayContext(
			mockHttpServletRequest, 0);

		Assert.assertEquals(
			"there-are-no-threads",
			defaultMBAdminListDisplayContext.getEmptyResultsMessage());
	}

}