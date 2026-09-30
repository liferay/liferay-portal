/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.servlet.filter;

import com.liferay.digital.sales.room.util.DSRRoomUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.FilterChain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Matyas Wollner
 */
public class ArchivedRoomFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		_dsrRoomUtilMockedStatic.when(
			() -> DSRRoomUtil.isArchived(Mockito.<Group>any())
		).thenReturn(
			true
		);

		Mockito.when(
			_groupLocalService.fetchGroup(Mockito.anyLong())
		).thenReturn(
			_group
		);

		Mockito.when(
			_groupLocalService.isMaintenanceMode(Mockito.any())
		).thenReturn(
			true
		);

		Mockito.when(
			_layoutLocalService.fetchLayout(Mockito.anyLong())
		).thenReturn(
			_layout
		);

		_permissionThreadLocalMockedStatic.when(
			PermissionThreadLocal::getPermissionChecker
		).thenReturn(
			_permissionChecker
		);

		Mockito.when(
			_portal.getPortalURL(Mockito.<MockHttpServletRequest>any())
		).thenReturn(
			_PORTAL_URL
		);
	}

	@After
	public void tearDown() {
		_dsrRoomUtilMockedStatic.close();
		_permissionThreadLocalMockedStatic.close();
	}

	@Test
	public void testProcessFilter() throws Exception {
		_testProcessFilterWithActiveGroup();
		_testProcessFilterWithArchivedRoom();
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"p_l_id", String.valueOf(RandomTestUtil.randomLong()));

		return mockHttpServletRequest;
	}

	private void _testProcessFilterWithActiveGroup() throws Exception {
		Mockito.when(
			_group.isActive()
		).thenReturn(
			true
		);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		FilterChain filterChain = Mockito.mock(FilterChain.class);

		_archivedRoomFilter.processFilter(
			_getMockHttpServletRequest(), mockHttpServletResponse, filterChain);

		Assert.assertNull(mockHttpServletResponse.getRedirectedUrl());

		Mockito.verify(
			filterChain, Mockito.times(1)
		).doFilter(
			Mockito.any(), Mockito.any()
		);
	}

	private void _testProcessFilterWithArchivedRoom() throws Exception {
		Mockito.when(
			_group.isActive()
		).thenReturn(
			false
		);

		Mockito.when(
			_permissionChecker.isGroupAdmin(Mockito.anyLong())
		).thenReturn(
			true
		);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		FilterChain filterChain = Mockito.mock(FilterChain.class);

		_archivedRoomFilter.processFilter(
			_getMockHttpServletRequest(), mockHttpServletResponse, filterChain);

		Assert.assertNull(mockHttpServletResponse.getRedirectedUrl());

		Mockito.verify(
			filterChain, Mockito.times(1)
		).doFilter(
			Mockito.any(), Mockito.any()
		);

		Mockito.when(
			_permissionChecker.isGroupAdmin(Mockito.anyLong())
		).thenReturn(
			false
		);

		mockHttpServletResponse = new MockHttpServletResponse();

		filterChain = Mockito.mock(FilterChain.class);

		_archivedRoomFilter.processFilter(
			_getMockHttpServletRequest(), mockHttpServletResponse, filterChain);

		Assert.assertEquals(
			_PORTAL_URL, mockHttpServletResponse.getRedirectedUrl());

		Mockito.verify(
			filterChain, Mockito.never()
		).doFilter(
			Mockito.any(), Mockito.any()
		);

		_permissionThreadLocalMockedStatic.when(
			PermissionThreadLocal::getPermissionChecker
		).thenReturn(
			null
		);

		Mockito.when(
			_userLocalService.fetchGuestUser(Mockito.anyLong())
		).thenReturn(
			_user
		);

		Mockito.when(
			_permissionCheckerFactory.create(_user)
		).thenReturn(
			_permissionChecker
		);

		Mockito.when(
			_permissionChecker.isGroupAdmin(Mockito.anyLong())
		).thenReturn(
			false
		);

		mockHttpServletResponse = new MockHttpServletResponse();

		filterChain = Mockito.mock(FilterChain.class);

		_archivedRoomFilter.processFilter(
			_getMockHttpServletRequest(), mockHttpServletResponse, filterChain);

		Assert.assertEquals(
			_PORTAL_URL, mockHttpServletResponse.getRedirectedUrl());

		Mockito.verify(
			filterChain, Mockito.never()
		).doFilter(
			Mockito.any(), Mockito.any()
		);
	}

	private static final String _PORTAL_URL = RandomTestUtil.randomString();

	@InjectMocks
	private ArchivedRoomFilter _archivedRoomFilter;

	private final MockedStatic<DSRRoomUtil> _dsrRoomUtilMockedStatic =
		Mockito.mockStatic(DSRRoomUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final Layout _layout = Mockito.mock(Layout.class);
	private final LayoutLocalService _layoutLocalService = Mockito.mock(
		LayoutLocalService.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final PermissionCheckerFactory _permissionCheckerFactory =
		Mockito.mock(PermissionCheckerFactory.class);
	private final MockedStatic<PermissionThreadLocal>
		_permissionThreadLocalMockedStatic = Mockito.mockStatic(
			PermissionThreadLocal.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final User _user = Mockito.mock(User.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}