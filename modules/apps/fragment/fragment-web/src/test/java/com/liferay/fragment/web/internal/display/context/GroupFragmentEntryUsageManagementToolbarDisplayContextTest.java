/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alvaro Fernandez Gonzalez
 */
public class GroupFragmentEntryUsageManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-114647")
	public void testIsSelectable() {
		Mockito.when(
			_searchContainer.getRowChecker()
		).thenReturn(
			Mockito.mock(RowChecker.class)
		);

		Mockito.when(
			_searchContainer.getTotal()
		).thenReturn(
			RandomTestUtil.randomInt(1, 100)
		);

		Assert.assertTrue(
			_getGroupFragmentEntryUsageManagementToolbarDisplayContext().
				isSelectable());
	}

	@Test
	@TestInfo("LPD-114647")
	public void testIsSelectableWhenSearchContainerRowCheckerIsNull() {
		Mockito.when(
			_searchContainer.getTotal()
		).thenReturn(
			RandomTestUtil.randomInt(1, 100)
		);

		Assert.assertFalse(
			_getGroupFragmentEntryUsageManagementToolbarDisplayContext().
				isSelectable());
	}

	@Test
	@TestInfo("LPD-114647")
	public void testIsSelectableWhenSearchContainerTotalIsZero() {
		Mockito.when(
			_searchContainer.getRowChecker()
		).thenReturn(
			Mockito.mock(RowChecker.class)
		);

		Assert.assertFalse(
			_getGroupFragmentEntryUsageManagementToolbarDisplayContext().
				isSelectable());
	}

	private GroupFragmentEntryUsageManagementToolbarDisplayContext
		_getGroupFragmentEntryUsageManagementToolbarDisplayContext() {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(),
				StringPool.DASH, WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		return new GroupFragmentEntryUsageManagementToolbarDisplayContext(
			Mockito.mock(HttpServletRequest.class),
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse(), _searchContainer);
	}

	private final SearchContainer<Group> _searchContainer = Mockito.mock(
		SearchContainer.class);

}