/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.events;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez Álvarez
 */
public class CMSServicePreActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRun() throws Exception {
		_testRunRedirectsToCMS();
		_testRunWhenRequestURIIsNotLayout();
		_testRunWhenScopeGroupIsNotGuest();
		_testRunWhenSendRedirectFails();
		_testRunWhenThemeDisplayIsNull();
		_testRunWhenUserIsNotSignedIn();
	}

	private void _assertRunDoesNotRedirect() throws Exception {
		_cmsServicePreAction.run(_httpServletRequest, _httpServletResponse);

		Mockito.verify(
			_httpServletResponse, Mockito.never()
		).sendRedirect(
			Mockito.anyString()
		);
	}

	private void _setUpMocks() {
		_group = Mockito.mock(Group.class);

		Mockito.when(
			_group.isGuest()
		).thenReturn(
			true
		);

		_themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_themeDisplay.isSignedIn()
		).thenReturn(
			true
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/c" + Portal.PATH_PORTAL_LAYOUT
		);

		_httpServletResponse = Mockito.mock(HttpServletResponse.class);

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			""
		);

		_cmsServicePreAction = new CMSServicePreAction();

		ReflectionTestUtil.setFieldValue(
			_cmsServicePreAction, "_portal", portal);
	}

	private void _testRunRedirectsToCMS() throws Exception {
		_setUpMocks();

		_cmsServicePreAction.run(_httpServletRequest, _httpServletResponse);

		Mockito.verify(
			_httpServletResponse
		).sendRedirect(
			"/web/cms"
		);
	}

	private void _testRunWhenRequestURIIsNotLayout() throws Exception {
		_setUpMocks();

		Mockito.when(
			_httpServletRequest.getRequestURI()
		).thenReturn(
			"/c/portal/logout"
		);

		_assertRunDoesNotRedirect();
	}

	private void _testRunWhenScopeGroupIsNotGuest() throws Exception {
		_setUpMocks();

		Mockito.when(
			_group.isGuest()
		).thenReturn(
			false
		);

		_assertRunDoesNotRedirect();
	}

	private void _testRunWhenSendRedirectFails() throws Exception {
		_setUpMocks();

		Mockito.doThrow(
			new IOException()
		).when(
			_httpServletResponse
		).sendRedirect(
			Mockito.anyString()
		);

		Assert.assertThrows(
			ActionException.class,
			() -> _cmsServicePreAction.run(
				_httpServletRequest, _httpServletResponse));
	}

	private void _testRunWhenThemeDisplayIsNull() throws Exception {
		_setUpMocks();

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			null
		);

		_assertRunDoesNotRedirect();
	}

	private void _testRunWhenUserIsNotSignedIn() throws Exception {
		_setUpMocks();

		Mockito.when(
			_themeDisplay.isSignedIn()
		).thenReturn(
			false
		);

		_assertRunDoesNotRedirect();
	}

	private CMSServicePreAction _cmsServicePreAction;
	private Group _group;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ThemeDisplay _themeDisplay;

}