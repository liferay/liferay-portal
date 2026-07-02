/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

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
 * @author Balázs Sáfrány-Kovalik
 */
public class LayoutsAdminManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_portletURLUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_portletURLUtilMockedStatic.when(
			() -> PortletURLUtil.clone(
				Mockito.any(PortletURL.class),
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);

		_portletURLUtilMockedStatic.when(
			() -> PortletURLUtil.getCurrent(
				Mockito.any(LiferayPortletRequest.class),
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	@Test
	@TestInfo("LPD-96674")
	public void testGetSortingURL() throws Exception {
		_testGetSortingURLWhenNotSearching();
		_testGetSortingURLWhenOrderByRelevance();
		_testGetSortingURLWhenSearching();
		_testGetSortingURLWhenSearchingFirstColumn();
	}

	private LayoutsAdminManagementToolbarDisplayContext
			_createLayoutsAdminManagementToolbarDisplayContext(
				boolean firstColumn, String orderByCol, boolean search)
		throws Exception {

		LayoutsAdminDisplayContext layoutsAdminDisplayContext = Mockito.mock(
			LayoutsAdminDisplayContext.class);

		SearchContainer<Layout> searchContainer = _createSearchContainer(
			orderByCol);

		Mockito.when(
			layoutsAdminDisplayContext.getLayoutsSearchContainer()
		).thenReturn(
			searchContainer
		);

		Mockito.when(
			layoutsAdminDisplayContext.isFirstColumn()
		).thenReturn(
			firstColumn
		);

		Mockito.when(
			layoutsAdminDisplayContext.isSearch()
		).thenReturn(
			search
		);

		return new LayoutsAdminManagementToolbarDisplayContext(
			new MockHttpServletRequest(),
			Mockito.mock(LiferayPortletRequest.class),
			Mockito.mock(LiferayPortletResponse.class),
			layoutsAdminDisplayContext);
	}

	private SearchContainer<Layout> _createSearchContainer(String orderByCol) {
		SearchContainer<Layout> searchContainer =
			(SearchContainer<Layout>)Mockito.mock(SearchContainer.class);

		Mockito.when(
			searchContainer.getOrderByCol()
		).thenReturn(
			orderByCol
		);

		Mockito.when(
			searchContainer.getOrderByColParam()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			searchContainer.getOrderByTypeParam()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return searchContainer;
	}

	private void _testGetSortingURLWhenNotSearching() throws Exception {
		LayoutsAdminManagementToolbarDisplayContext
			layoutsAdminManagementToolbarDisplayContext =
				_createLayoutsAdminManagementToolbarDisplayContext(
					false, RandomTestUtil.randomString(), false);

		Assert.assertNull(
			layoutsAdminManagementToolbarDisplayContext.getSortingURL());
	}

	private void _testGetSortingURLWhenOrderByRelevance() throws Exception {
		LayoutsAdminManagementToolbarDisplayContext
			layoutsAdminManagementToolbarDisplayContext =
				_createLayoutsAdminManagementToolbarDisplayContext(
					false, "relevance", true);

		Assert.assertNull(
			layoutsAdminManagementToolbarDisplayContext.getSortingURL());
	}

	private void _testGetSortingURLWhenSearching() throws Exception {
		LayoutsAdminManagementToolbarDisplayContext
			layoutsAdminManagementToolbarDisplayContext =
				_createLayoutsAdminManagementToolbarDisplayContext(
					false, RandomTestUtil.randomString(), true);

		Assert.assertNotNull(
			layoutsAdminManagementToolbarDisplayContext.getSortingURL());
	}

	private void _testGetSortingURLWhenSearchingFirstColumn() throws Exception {
		LayoutsAdminManagementToolbarDisplayContext
			layoutsAdminManagementToolbarDisplayContext =
				_createLayoutsAdminManagementToolbarDisplayContext(
					true, RandomTestUtil.randomString(), true);

		Assert.assertNull(
			layoutsAdminManagementToolbarDisplayContext.getSortingURL());
	}

	private static final MockedStatic<PortletURLUtil>
		_portletURLUtilMockedStatic = Mockito.mockStatic(PortletURLUtil.class);

}