/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.events;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class KBServicePreActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		_authTokenSnapshot = ReflectionTestUtil.getFieldValue(
			AuthTokenUtil.class, "_authTokenSnapshot");

		ReflectionTestUtil.setFieldValue(
			AuthTokenUtil.class, "_authTokenSnapshot",
			new Snapshot<AuthToken>(AuthTokenUtil.class, AuthToken.class) {

				@Override
				public AuthToken get() {
					return _mockAuthToken();
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			AuthTokenUtil.class, "_authTokenSnapshot", _authTokenSnapshot);
	}

	@Test
	public void testRun() throws Exception {
		_testRunWhenGroupHasDisplayPortlet();
		_testRunWhenLayoutIsControlPanel();
		_testRunWhenPortletAuthenticationTokenIsNull();
		_testRunWhenPortletAuthenticationTokenIsStale();
		_testRunWhenPortletAuthenticationTokenIsValid();
		_testRunWhenPortletIdIsNotDefaultInstance();
	}

	private void _assertDoesNotRedirect(HttpServletResponse httpServletResponse)
		throws Exception {

		Mockito.verify(
			httpServletResponse, Mockito.never()
		).sendRedirect(
			Mockito.anyString()
		);
	}

	private AuthToken _mockAuthToken() {
		AuthToken authToken = Mockito.mock(AuthToken.class);

		Mockito.when(
			authToken.getToken(
				Mockito.any(HttpServletRequest.class), Mockito.eq(_PLID),
				Mockito.eq(
					KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE))
		).thenReturn(
			_TOKEN
		);

		return authToken;
	}

	private HttpServletRequest _mockHttpServletRequest(
		String portletAuthenticationToken, String portletId,
		ThemeDisplay themeDisplay) {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			httpServletRequest.getParameter("p_p_auth")
		).thenReturn(
			portletAuthenticationToken
		);

		Mockito.when(
			httpServletRequest.getParameter("p_p_id")
		).thenReturn(
			portletId
		);

		return httpServletRequest;
	}

	private Portal _mockPortal(long plid) throws Exception {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.escapeRedirect(_URL_CURRENT)
		).thenReturn(
			_URL_CURRENT
		);

		Mockito.when(
			portal.getPlidFromPortletId(
				_GROUP_ID, KBPortletKeys.KNOWLEDGE_BASE_DISPLAY)
		).thenReturn(
			plid
		);

		return portal;
	}

	private ThemeDisplay _mockThemeDisplay(boolean typeControlPanel) {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			layout.isTypeControlPanel()
		).thenReturn(
			typeControlPanel
		);

		Mockito.when(
			themeDisplay.getLayout()
		).thenReturn(
			layout
		);

		Mockito.when(
			themeDisplay.getPlid()
		).thenReturn(
			_PLID
		);

		Mockito.when(
			themeDisplay.getURLCurrent()
		).thenReturn(
			_URL_CURRENT
		);

		Mockito.when(
			themeDisplay.isLifecycleRender()
		).thenReturn(
			true
		);

		return themeDisplay;
	}

	private HttpServletResponse _run(
		HttpServletRequest httpServletRequest, Portal portal) {

		HttpServletResponse httpServletResponse = Mockito.mock(
			HttpServletResponse.class);

		KBServicePreAction kbServicePreAction = new KBServicePreAction();

		ReflectionTestUtil.setFieldValue(kbServicePreAction, "_portal", portal);

		kbServicePreAction.run(httpServletRequest, httpServletResponse);

		return httpServletResponse;
	}

	private void _testRunWhenGroupHasDisplayPortlet() throws Exception {
		_assertDoesNotRedirect(
			_run(
				_mockHttpServletRequest(
					RandomTestUtil.randomString(),
					KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE,
					_mockThemeDisplay(false)),
				_mockPortal(RandomTestUtil.randomLong())));
	}

	private void _testRunWhenLayoutIsControlPanel() throws Exception {
		_assertDoesNotRedirect(
			_run(
				_mockHttpServletRequest(
					RandomTestUtil.randomString(),
					KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE,
					_mockThemeDisplay(true)),
				_mockPortal(LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)));
	}

	private void _testRunWhenPortletAuthenticationTokenIsNull()
		throws Exception {

		_assertDoesNotRedirect(
			_run(
				_mockHttpServletRequest(
					null, KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE,
					_mockThemeDisplay(false)),
				_mockPortal(LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)));
	}

	private void _testRunWhenPortletAuthenticationTokenIsStale()
		throws Exception {

		HttpServletResponse httpServletResponse = _run(
			_mockHttpServletRequest(
				RandomTestUtil.randomString(),
				KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE,
				_mockThemeDisplay(false)),
			_mockPortal(LayoutConstants.DEFAULT_PARENT_LAYOUT_ID));

		Mockito.verify(
			httpServletResponse
		).sendRedirect(
			_URL_CURRENT + "&p_p_auth=" + _TOKEN
		);
	}

	private void _testRunWhenPortletAuthenticationTokenIsValid()
		throws Exception {

		_assertDoesNotRedirect(
			_run(
				_mockHttpServletRequest(
					_TOKEN,
					KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE,
					_mockThemeDisplay(false)),
				_mockPortal(LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)));
	}

	private void _testRunWhenPortletIdIsNotDefaultInstance() throws Exception {
		_assertDoesNotRedirect(
			_run(
				_mockHttpServletRequest(
					RandomTestUtil.randomString(),
					KBPortletKeys.KNOWLEDGE_BASE_DISPLAY,
					_mockThemeDisplay(false)),
				_mockPortal(LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)));
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _PLID = RandomTestUtil.randomLong();

	private static final String _TOKEN = RandomTestUtil.randomString();

	private static final String _URL_CURRENT =
		"http://localhost:8080/web/guest/home?p_p_id=" +
			KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE;

	private Snapshot<AuthToken> _authTokenSnapshot;

}