/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockedStatic;
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

		Mockito.when(
			_themeDisplay.getTimeZone()
		).thenReturn(
			TimeZone.getTimeZone(_TIME_ZONE_ID)
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
	}

	@Test
	public void testGetExpiringSoonFilterStringBoundsTheExpirationDate() {
		String filterString =
			SectionDisplayContextUtil.getExpiringSoonFilterString(
				_mockHttpServletRequest);

		Assert.assertTrue(filterString.contains("dateExpiration gt now()"));

		Instant instant = Instant.now();

		_assertThresholdInstant(
			instant.plus(7, ChronoUnit.DAYS), filterString,
			"dateExpiration le ");
	}

	@Test
	public void testGetExpiringSoonFilterStringOnlyMatchesApprovedAssets() {
		String filterString =
			SectionDisplayContextUtil.getExpiringSoonFilterString(
				_mockHttpServletRequest);

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

		TimeZone timeZone = TimeZone.getTimeZone(_TIME_ZONE_ID);

		ZonedDateTime zonedDateTime = ZonedDateTime.now(timeZone.toZoneId());

		ZonedDateTime thresholdZonedDateTime = zonedDateTime.plusMonths(1);

		_assertThresholdInstant(
			thresholdZonedDateTime.toInstant(), filterString, "dateReview le ");
	}

	@Test
	public void testGetUpcomingReviewsFilterStringScopesNonadministrators()
		throws Exception {

		Mockito.when(
			_roleLocalService.hasUserRole(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
				Mockito.anyBoolean())
		).thenReturn(
			false
		);

		try (MockedStatic<DepotEntryLocalServiceUtil>
				depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					DepotEntryLocalServiceUtil.class)) {

			depotEntryLocalServiceUtilMockedStatic.when(
				() -> DepotEntryLocalServiceUtil.getDepotEntryGroupIds(
					Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt())
			).thenReturn(
				List.of(42L, 7L)
			);

			String filterString =
				SectionDisplayContextUtil.getUpcomingReviewsFilterString(
					_mockHttpServletRequest);

			Assert.assertTrue(
				filterString.contains("groupIds/any(g:g in (42,7))"));
		}
	}

	private void _assertThresholdInstant(
		Instant expectedInstant, String filterString, String prefix) {

		int index = filterString.indexOf(prefix);

		Assert.assertTrue(index >= 0);

		String thresholdDateString = filterString.substring(
			index + prefix.length());

		index = thresholdDateString.indexOf(' ');

		if (index >= 0) {
			thresholdDateString = thresholdDateString.substring(0, index);
		}

		Duration duration = Duration.between(
			expectedInstant, Instant.parse(thresholdDateString));

		Assert.assertTrue(Math.abs(duration.getSeconds()) < 60);
	}

	private static final String _TIME_ZONE_ID = "America/Los_Angeles";

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

	@Mock
	private RoleLocalService _roleLocalService;

	@Mock
	private ThemeDisplay _themeDisplay;

}