/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Georgel Pop
 */
public class SelectLayoutPageTemplateEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_mockHttpServletRequest = new MockHttpServletRequest();

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));
	}

	@Test
	@TestInfo("LPD-89086")
	public void testGetLayoutPageTemplateEntries() {
		SelectLayoutPageTemplateEntryDisplayContext
			selectLayoutPageTemplateEntryDisplayContext =
				new SelectLayoutPageTemplateEntryDisplayContext(
					_mockHttpServletRequest, null);

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			Collections.singletonList(
				Mockito.mock(LayoutPageTemplateEntry.class));
		List<LayoutPageTemplateEntry> layoutPageTemplateEntriesByType =
			Collections.singletonList(
				Mockito.mock(LayoutPageTemplateEntry.class));

		try (MockedStatic<LayoutPageTemplateEntryServiceUtil>
				layoutPageTemplateEntryServiceUtilMockedStatic =
					Mockito.mockStatic(
						LayoutPageTemplateEntryServiceUtil.class)) {

			layoutPageTemplateEntryServiceUtilMockedStatic.when(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntriesByType(
							Mockito.anyLong(), Mockito.anyLong(),
							Mockito.eq(
								LayoutPageTemplateEntryTypeConstants.BASIC),
							Mockito.anyInt(), Mockito.anyInt(), Mockito.any())
			).thenReturn(
				layoutPageTemplateEntriesByType
			);

			layoutPageTemplateEntryServiceUtilMockedStatic.when(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntries(
							Mockito.anyLong(), Mockito.anyLong(),
							Mockito.eq(WorkflowConstants.STATUS_APPROVED),
							Mockito.anyInt(), Mockito.anyInt())
			).thenReturn(
				layoutPageTemplateEntries
			);

			for (boolean featureFlagEnabled : new boolean[] {false, true}) {
				try (MockedStatic<FeatureFlagManagerUtil>
						featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
							FeatureFlagManagerUtil.class)) {

					featureFlagManagerUtilMockedStatic.when(
						() -> FeatureFlagManagerUtil.isEnabled(
							Mockito.anyLong(), Mockito.eq("LPD-76864"))
					).thenReturn(
						featureFlagEnabled
					);

					if (featureFlagEnabled) {
						Assert.assertSame(
							layoutPageTemplateEntries,
							selectLayoutPageTemplateEntryDisplayContext.
								getLayoutPageTemplateEntries(0, 10));
					}
					else {
						Assert.assertSame(
							layoutPageTemplateEntriesByType,
							selectLayoutPageTemplateEntryDisplayContext.
								getLayoutPageTemplateEntries(0, 10));
					}
				}
			}
		}
	}

	@Test
	@TestInfo("LPD-89086")
	public void testGetLayoutPageTemplateEntriesCount() {
		SelectLayoutPageTemplateEntryDisplayContext
			selectLayoutPageTemplateEntryDisplayContext =
				new SelectLayoutPageTemplateEntryDisplayContext(
					_mockHttpServletRequest, null);

		int count = RandomTestUtil.randomInt();
		int countByType = RandomTestUtil.randomInt();

		try (MockedStatic<LayoutPageTemplateEntryServiceUtil>
				layoutPageTemplateEntryServiceUtilMockedStatic =
					Mockito.mockStatic(
						LayoutPageTemplateEntryServiceUtil.class)) {

			layoutPageTemplateEntryServiceUtilMockedStatic.when(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntriesCountByType(
							Mockito.anyLong(), Mockito.anyLong(),
							Mockito.eq(
								LayoutPageTemplateEntryTypeConstants.BASIC))
			).thenReturn(
				countByType
			);

			layoutPageTemplateEntryServiceUtilMockedStatic.when(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntriesCount(
							Mockito.anyLong(), Mockito.anyLong(),
							Mockito.eq(WorkflowConstants.STATUS_APPROVED))
			).thenReturn(
				count
			);

			for (boolean featureFlagEnabled : new boolean[] {false, true}) {
				try (MockedStatic<FeatureFlagManagerUtil>
						featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
							FeatureFlagManagerUtil.class)) {

					featureFlagManagerUtilMockedStatic.when(
						() -> FeatureFlagManagerUtil.isEnabled(
							Mockito.anyLong(), Mockito.eq("LPD-76864"))
					).thenReturn(
						featureFlagEnabled
					);

					if (featureFlagEnabled) {
						Assert.assertEquals(
							count,
							selectLayoutPageTemplateEntryDisplayContext.
								getLayoutPageTemplateEntriesCount());
					}
					else {
						Assert.assertEquals(
							countByType,
							selectLayoutPageTemplateEntryDisplayContext.
								getLayoutPageTemplateEntriesCount());
					}
				}
			}
		}
	}

	private MockHttpServletRequest _mockHttpServletRequest;

}