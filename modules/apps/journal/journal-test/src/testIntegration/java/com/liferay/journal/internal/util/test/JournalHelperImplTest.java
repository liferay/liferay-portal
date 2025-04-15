/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalHelper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Noor Najjar
 */
@RunWith(Arquillian.class)
public class JournalHelperImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		_themeDisplay.setLayout(layout);
		_themeDisplay.setLocale(LocaleUtil.US);
		_themeDisplay.setLookAndFeel(
			layout.getTheme(), layout.getColorScheme());

		User user = _userLocalService.getUser(_group.getCreatorUserId());

		_themeDisplay.setRealUser(user);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, _themeDisplay);

		_themeDisplay.setRequest(httpServletRequest);

		_themeDisplay.setResponse(new MockHttpServletResponse());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(user);
	}

	@Test
	public void testcreateURLPattern() throws Exception {
		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test",
			RandomTestUtil.randomString());

		Layout layout = _themeDisplay.getLayout();

		Assert.assertEquals(
			getArticleURL(),
			_journalHelper.createURLPattern(
				article, _themeDisplay.getLocale(), layout.isPrivateLayout(),
				FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE,
				_themeDisplay));
	}

	protected String getArticleURL() {
		return StringBundler.concat(
			"/web", _group.getFriendlyURL(),
			FriendlyURLResolverConstants.URL_SEPARATOR_JOURNAL_ARTICLE, "test");
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalHelper _journalHelper;

	private ThemeDisplay _themeDisplay;

	@Inject
	private UserLocalService _userLocalService;

}