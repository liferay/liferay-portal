/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.status.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class StatusStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-52041")
	public void testExecute() throws Exception {
		_testExecute(TestPropsValues.getUser(), " signed-in ");
		_testExecute(
			_userLocalService.getGuestUser(TestPropsValues.getCompanyId()),
			" signed-out ");

		String originalName = PrincipalThreadLocal.getName();

		try {
			PrincipalThreadLocal.setName(null);

			_testExecute(TestPropsValues.getUser(), " signed-in ");

			PrincipalThreadLocal.setName(null);

			_testExecute(
				_userLocalService.getGuestUser(TestPropsValues.getCompanyId()),
				" signed-out ");
		}
		finally {
			PrincipalThreadLocal.setName(originalName);
		}
	}

	private void _processEvents(
			MockHttpServletRequest mockHttpServletRequest,
			MockHttpServletResponse mockHttpServletResponse, User user)
		throws Exception {

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL,
			StringPool.SLASH +
				StringUtil.toLowerCase(RandomTestUtil.randomString()));
		mockHttpServletRequest.setAttribute(WebKeys.USER, user);

		EventsProcessorUtil.process(
			PropsKeys.SERVLET_SERVICE_EVENTS_PRE,
			PropsValues.SERVLET_SERVICE_EVENTS_PRE, mockHttpServletRequest,
			mockHttpServletResponse);
	}

	private void _testExecute(User user, String expectedContent)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_groupLocalService.getGroup(TestPropsValues.getGroupId()),
				_layoutLocalService.getLayout(TestPropsValues.getPlid()));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setUser(user);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_processEvents(mockHttpServletRequest, mockHttpServletResponse, user);

		_statusStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			content,
			content.contains(
				_language.format(
					LocaleUtil.US, "powered-by-x",
					"<a class=\"text-decoration-underline text-white\" " +
						"href=\"http://www.liferay.com\" rel=\"external\">" +
							"Liferay</a>")));
		Assert.assertTrue(content, content.contains(expectedContent));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "path=/portal/status")
	private StrutsAction _statusStrutsAction;

	@Inject
	private UserLocalService _userLocalService;

}