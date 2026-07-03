/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRendererRegistryUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryServiceUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Balázs Sáfrány-Kovalik
 */
public class LayoutUtilityPageEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_layoutUtilityPageEntryServiceUtilMockedStatic = Mockito.mockStatic(
			LayoutUtilityPageEntryServiceUtil.class);

		_layoutUtilityPageEntryViewRendererRegistryUtilMockedStatic =
			Mockito.mockStatic(
				LayoutUtilityPageEntryViewRendererRegistryUtil.class);

		_layoutUtilityPageEntryViewRendererRegistryUtilMockedStatic.when(
			LayoutUtilityPageEntryViewRendererRegistryUtil::
				getLayoutUtilityPageEntryViewRenderers
		).thenReturn(
			Collections.emptyList()
		);

		_layoutUtilityPageEntryServiceUtilMockedStatic.when(
			() -> LayoutUtilityPageEntryServiceUtil.getLayoutUtilityPageEntries(
				Mockito.anyLong(), Mockito.any(String[].class),
				Mockito.anyInt(), Mockito.anyInt(),
				Mockito.<OrderByComparator<LayoutUtilityPageEntry>>any())
		).thenReturn(
			Collections.<LayoutUtilityPageEntry>emptyList()
		);

		_layoutUtilityPageEntryServiceUtilMockedStatic.when(
			() ->
				LayoutUtilityPageEntryServiceUtil.
					getLayoutUtilityPageEntriesCount(
						Mockito.anyLong(), Mockito.any(String[].class))
		).thenReturn(
			0
		);
	}

	@After
	public void tearDown() {
		_layoutUtilityPageEntryServiceUtilMockedStatic.close();
		_layoutUtilityPageEntryViewRendererRegistryUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-96674")
	public void testGetLayoutUtilityPageEntrySearchContainer() {
		_testGetLayoutUtilityPageEntrySearchContainerOrderByCreateDate();
		_testGetLayoutUtilityPageEntrySearchContainerOrderByName();
		_testGetLayoutUtilityPageEntrySearchContainerWithoutOrderByComparator();
	}

	private OrderByComparator<LayoutUtilityPageEntry> _getOrderByComparator(
		String orderByCol, String orderByType) {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, new ThemeDisplay());

		if (orderByCol != null) {
			mockLiferayPortletRenderRequest.setParameter(
				"orderByCol", orderByCol);
		}

		if (orderByType != null) {
			mockLiferayPortletRenderRequest.setParameter(
				"orderByType", orderByType);
		}

		LayoutUtilityPageEntryDisplayContext
			layoutUtilityPageEntryDisplayContext =
				new LayoutUtilityPageEntryDisplayContext(
					mockLiferayPortletRenderRequest,
					new MockLiferayPortletRenderResponse());

		SearchContainer<LayoutUtilityPageEntry> searchContainer =
			layoutUtilityPageEntryDisplayContext.
				getLayoutUtilityPageEntrySearchContainer();

		return searchContainer.getOrderByComparator();
	}

	private void _testGetLayoutUtilityPageEntrySearchContainerOrderByCreateDate() {
		Assert.assertEquals(
			"LayoutUtilityPageEntry.createDate ASC",
			_getOrderByComparator(
				"create-date", "asc"
			).getOrderBy());
	}

	private void _testGetLayoutUtilityPageEntrySearchContainerOrderByName() {
		Assert.assertEquals(
			"LayoutUtilityPageEntry.name DESC",
			_getOrderByComparator(
				"name", "desc"
			).getOrderBy());
	}

	private void _testGetLayoutUtilityPageEntrySearchContainerWithoutOrderByComparator() {
		Assert.assertNull(
			_getOrderByComparator(RandomTestUtil.randomString(), "asc"));
	}

	private MockedStatic<LayoutUtilityPageEntryServiceUtil>
		_layoutUtilityPageEntryServiceUtilMockedStatic;
	private MockedStatic<LayoutUtilityPageEntryViewRendererRegistryUtil>
		_layoutUtilityPageEntryViewRendererRegistryUtilMockedStatic;

}