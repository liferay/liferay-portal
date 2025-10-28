/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

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
 * @author Jürgen Kappler
 */
public class JournalHistoryManagementToolbarDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_journalArticleLocalServiceUtilMockedStatic.close();
		_journalArticlePermissionMockedStatic.close();
		_portletURLUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_journalArticle = _setUpJournalArticle();
		_permissionChecker = Mockito.mock(PermissionChecker.class);

		_setUpJournalArticlePermission(_journalArticle, _permissionChecker);

		_setUpPortletURLUtil();
	}

	@Test
	public void testGetAvailableActions() throws PortalException {
		JournalHistoryDisplayContext journalHistoryDisplayContext =
			Mockito.mock(JournalHistoryDisplayContext.class);

		Mockito.when(
			journalHistoryDisplayContext.getArticleSearchContainer()
		).thenReturn(
			null
		);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPermissionChecker(_permissionChecker);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		JournalHistoryManagementToolbarDisplayContext
			journalHistoryManagementToolbarDisplayContext =
				new JournalHistoryManagementToolbarDisplayContext(
					httpServletRequest,
					Mockito.mock(LiferayPortletRequest.class),
					Mockito.mock(LiferayActionResponse.class), _journalArticle,
					journalHistoryDisplayContext);

		_journalArticleLocalServiceUtilMockedStatic.when(
			() -> JournalArticleLocalServiceUtil.getArticlesCount(
				_journalArticle.getGroupId(), _journalArticle.getArticleId())
		).thenReturn(
			1
		);

		Assert.assertEquals(
			"deleteArticles",
			journalHistoryManagementToolbarDisplayContext.getAvailableActions(
				_journalArticle));
	}

	private JournalArticle _setUpJournalArticle() {
		JournalArticle journalArticle = Mockito.mock(JournalArticle.class);

		Mockito.when(
			journalArticle.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			journalArticle.getArticleId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return journalArticle;
	}

	private void _setUpJournalArticlePermission(
		JournalArticle journalArticle, PermissionChecker permissionChecker) {

		_journalArticlePermissionMockedStatic.when(
			() -> JournalArticlePermission.contains(
				permissionChecker, journalArticle, ActionKeys.DELETE)
		).thenReturn(
			true
		);

		_journalArticlePermissionMockedStatic.when(
			() -> JournalArticlePermission.contains(
				permissionChecker, journalArticle, ActionKeys.EXPIRE)
		).thenReturn(
			false
		);
	}

	private void _setUpPortletURLUtil() {
		_portletURLUtilMockedStatic.when(
			() -> PortletURLUtil.getCurrent(
				Mockito.any(LiferayPortletRequest.class),
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private static final MockedStatic<JournalArticleLocalServiceUtil>
		_journalArticleLocalServiceUtilMockedStatic = Mockito.mockStatic(
			JournalArticleLocalServiceUtil.class);
	private static final MockedStatic<JournalArticlePermission>
		_journalArticlePermissionMockedStatic = Mockito.mockStatic(
			JournalArticlePermission.class);
	private static final MockedStatic<PortletURLUtil>
		_portletURLUtilMockedStatic = Mockito.mockStatic(PortletURLUtil.class);

	private JournalArticle _journalArticle;
	private PermissionChecker _permissionChecker;

}