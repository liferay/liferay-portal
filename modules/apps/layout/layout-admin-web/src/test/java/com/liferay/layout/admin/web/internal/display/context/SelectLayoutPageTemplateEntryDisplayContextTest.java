/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
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

	@AfterClass
	public static void tearDownClass() {
		_layoutPageTemplateCollectionLocalServiceUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_layoutPageTemplateCollection = Mockito.mock(
			LayoutPageTemplateCollection.class);

		_layoutPageTemplateCollectionLocalServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateCollectionLocalServiceUtil.
					fetchLayoutPageTemplateCollection(Mockito.anyLong())
		).thenReturn(
			_layoutPageTemplateCollection
		);

		_mockHttpServletRequest = new MockHttpServletRequest();

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, Mockito.mock(ThemeDisplay.class));
	}

	@Test
	@TestInfo("LPD-105566")
	public void testGetDesignLibraryGroup() throws Exception {
		try (MockedStatic<DesignLibraryUtil> designLibraryUtilMockedStatic =
				Mockito.mockStatic(DesignLibraryUtil.class);
			MockedStatic<GroupLocalServiceUtil>
				groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
					GroupLocalServiceUtil.class)) {

			long designLibraryGroupId = RandomTestUtil.randomLong();

			designLibraryUtilMockedStatic.when(
				() -> DesignLibraryUtil.isConnectedDesignLibraryGroupId(
					Mockito.anyLong(), Mockito.eq(designLibraryGroupId),
					Mockito.anyLong())
			).thenReturn(
				true
			);

			Group group = Mockito.mock(Group.class);

			groupLocalServiceUtilMockedStatic.when(
				() -> GroupLocalServiceUtil.getGroup(designLibraryGroupId)
			).thenReturn(
				group
			);

			SelectLayoutPageTemplateEntryDisplayContext
				selectLayoutPageTemplateEntryDisplayContext =
					_getSelectLayoutPageTemplateEntryDisplayContext();

			Assert.assertNull(
				selectLayoutPageTemplateEntryDisplayContext.
					getDesignLibraryGroup());

			Mockito.when(
				_layoutPageTemplateCollection.getGroupId()
			).thenReturn(
				designLibraryGroupId
			);

			selectLayoutPageTemplateEntryDisplayContext =
				_getSelectLayoutPageTemplateEntryDisplayContext();

			Assert.assertSame(
				group,
				selectLayoutPageTemplateEntryDisplayContext.
					getDesignLibraryGroup());
		}
	}

	@Test
	@TestInfo("LPD-89086")
	public void testGetLayoutPageTemplateEntries() throws Exception {
		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			Collections.singletonList(
				Mockito.mock(LayoutPageTemplateEntry.class));
		List<LayoutPageTemplateEntry> layoutPageTemplateEntriesByType =
			Collections.singletonList(
				Mockito.mock(LayoutPageTemplateEntry.class));
		SelectLayoutPageTemplateEntryDisplayContext
			selectLayoutPageTemplateEntryDisplayContext =
				_getSelectLayoutPageTemplateEntryDisplayContext();

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
	public void testGetLayoutPageTemplateEntriesCount() throws Exception {
		int count = RandomTestUtil.randomInt();
		int countByType = RandomTestUtil.randomInt();
		SelectLayoutPageTemplateEntryDisplayContext
			selectLayoutPageTemplateEntryDisplayContext =
				_getSelectLayoutPageTemplateEntryDisplayContext();

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

	@Test
	@TestInfo("LPD-105566")
	public void testGetLayoutPageTemplateEntriesWithDesignLibraryGroup()
		throws Exception {

		long designLibraryGroupId = RandomTestUtil.randomLong();

		Mockito.when(
			_layoutPageTemplateCollection.getGroupId()
		).thenReturn(
			designLibraryGroupId
		);

		try (MockedStatic<DesignLibraryUtil> designLibraryUtilMockedStatic =
				Mockito.mockStatic(DesignLibraryUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<LayoutPageTemplateEntryServiceUtil>
				layoutPageTemplateEntryServiceUtilMockedStatic =
					Mockito.mockStatic(
						LayoutPageTemplateEntryServiceUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-76864"))
			).thenReturn(
				true
			);

			designLibraryUtilMockedStatic.when(
				() -> DesignLibraryUtil.isConnectedDesignLibraryGroupId(
					Mockito.anyLong(), Mockito.eq(designLibraryGroupId),
					Mockito.anyLong())
			).thenReturn(
				true
			);

			int count = RandomTestUtil.randomInt();

			layoutPageTemplateEntryServiceUtilMockedStatic.when(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntriesCount(
							Mockito.eq(designLibraryGroupId), Mockito.anyLong(),
							Mockito.eq(WorkflowConstants.STATUS_APPROVED))
			).thenReturn(
				count
			);

			List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
				Collections.singletonList(
					Mockito.mock(LayoutPageTemplateEntry.class));

			layoutPageTemplateEntryServiceUtilMockedStatic.when(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntries(
							Mockito.eq(designLibraryGroupId), Mockito.anyLong(),
							Mockito.eq(WorkflowConstants.STATUS_APPROVED),
							Mockito.anyInt(), Mockito.anyInt())
			).thenReturn(
				layoutPageTemplateEntries
			);

			SelectLayoutPageTemplateEntryDisplayContext
				selectLayoutPageTemplateEntryDisplayContext =
					_getSelectLayoutPageTemplateEntryDisplayContext();

			Assert.assertEquals(
				count,
				selectLayoutPageTemplateEntryDisplayContext.
					getLayoutPageTemplateEntriesCount());
			Assert.assertSame(
				layoutPageTemplateEntries,
				selectLayoutPageTemplateEntryDisplayContext.
					getLayoutPageTemplateEntries(0, 10));
		}
	}

	private SelectLayoutPageTemplateEntryDisplayContext
		_getSelectLayoutPageTemplateEntryDisplayContext() {

		return new SelectLayoutPageTemplateEntryDisplayContext(
			_mockHttpServletRequest, null);
	}

	private static final MockedStatic
		<LayoutPageTemplateCollectionLocalServiceUtil>
			_layoutPageTemplateCollectionLocalServiceUtilMockedStatic =
				Mockito.mockStatic(
					LayoutPageTemplateCollectionLocalServiceUtil.class);

	private LayoutPageTemplateCollection _layoutPageTemplateCollection;
	private MockHttpServletRequest _mockHttpServletRequest;

}