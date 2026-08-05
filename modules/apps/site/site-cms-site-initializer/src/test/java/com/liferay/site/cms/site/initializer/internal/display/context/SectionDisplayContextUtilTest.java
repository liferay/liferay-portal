/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Veronica Gonzalez
 */
public class SectionDisplayContextUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtil.setFieldValue(
			RoleLocalServiceUtil.class, "_service", _roleLocalService);

		Mockito.when(
			_roleLocalService.hasUserRole(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyBoolean())
		).thenReturn(
			true
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
	}

	@Test
	public void testGetExpiringSoonFilterStringOnlyMatchesApprovedAssets() {
		String filterString =
			SectionDisplayContextUtil.getExpiringSoonFilterString(
				_mockHttpServletRequest);

		Assert.assertTrue(filterString.contains("dateExpiration gt now()"));
		Assert.assertTrue(filterString.contains("dateExpiration le "));
		Assert.assertTrue(
			filterString.contains(
				"status eq " + WorkflowConstants.STATUS_APPROVED));
	}

	@Test
	public void testGetUpcomingReviewsFilterStringBoundsTheReviewDate() {
		String filterString =
			SectionDisplayContextUtil.getUpcomingReviewsFilterString(
				_mockHttpServletRequest);

		Assert.assertTrue(filterString.contains("dateReview gt now()"));
		Assert.assertTrue(filterString.contains("dateReview le "));
	}

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

	@Mock
	private RoleLocalService _roleLocalService;

	@Mock
	private ThemeDisplay _themeDisplay;

}