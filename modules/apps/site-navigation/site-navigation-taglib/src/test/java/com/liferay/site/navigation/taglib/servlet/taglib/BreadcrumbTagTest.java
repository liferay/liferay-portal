/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.servlet.taglib;

import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntryContributorUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class BreadcrumbTagTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() throws Exception {
		_breadcrumbEntryContributorUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-98412")
	public void testSetAttributes() throws Exception {
		BreadcrumbTag breadcrumbTag = new BreadcrumbTag();

		List<BreadcrumbEntry> breadcrumbEntries = Collections.singletonList(
			new BreadcrumbEntry());

		breadcrumbTag.setBreadcrumbEntries(breadcrumbEntries);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		breadcrumbTag.setAttributes(mockHttpServletRequest);

		_breadcrumbEntryContributorUtilMockedStatic.verify(
			() -> BreadcrumbEntryContributorUtil.contribute(
				breadcrumbEntries, mockHttpServletRequest),
			Mockito.times(1));

		_breadcrumbEntryContributorUtilMockedStatic.clearInvocations();

		breadcrumbTag.setSkipEntryContributors(true);

		breadcrumbTag.setAttributes(mockHttpServletRequest);

		_breadcrumbEntryContributorUtilMockedStatic.verifyNoInteractions();
	}

	private final MockedStatic<BreadcrumbEntryContributorUtil>
		_breadcrumbEntryContributorUtilMockedStatic = Mockito.mockStatic(
			BreadcrumbEntryContributorUtil.class);

}