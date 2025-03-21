/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletURL;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marco Galluzzi
 */
@RunWith(Arquillian.class)
public class JournalDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetPortletURLWithHighlightedDDMStructureIdParameter()
		throws Exception {

		PortletURL portletURL = _getPortletURL("123456", "tab1");

		Assert.assertNotNull(portletURL);

		String portletURLString = portletURL.toString();

		Assert.assertTrue(
			portletURLString.contains("highlightedDDMStructureId=123456"));
	}

	@Test
	public void testGetSearchContainer() throws Exception {
		int count = 5;

		for (int i = 0; i < count; i++) {
			_addJournalArticle(RandomTestUtil.randomString());
		}

		SearchContainer<Object> searchContainer = _getSearchContainer();

		Assert.assertEquals(count, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithKeywordsAndDelta() throws Exception {
		int count = 5;

		for (int i = 0; i < count; i++) {
			_addJournalArticle("Example " + i);
		}

		for (int i = 0; i < 3; i++) {
			_addJournalArticle("Title " + i);
		}

		SearchContainer<Object> searchContainer = _getSearchContainer(
			"Example", 1, 4);

		Assert.assertEquals(count, searchContainer.getTotal());
	}

	@Test
	public void testIsShowBreadcrumb() throws Exception {
		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		Assert.assertFalse(
			_isShowBreadcrumb(
				"",
				journalFolderFixture.addFolder(
					_group.getGroupId(), RandomTestUtil.randomString())));

		Assert.assertFalse(_isShowBreadcrumb("test", null));
		Assert.assertTrue(
			_isShowBreadcrumb(
				"test",
				journalFolderFixture.addFolder(
					_group.getGroupId(), RandomTestUtil.randomString())));
	}

	private void _addJournalArticle(String title) throws Exception {
		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, _getLocaleStringMap(title),
			_getLocaleStringMap("description"), _getLocaleStringMap("content"),
			null, LocaleUtil.getDefault(), null, false, false, _serviceContext);
	}

	private Map<Locale, String> _getLocaleStringMap(String value) {
		return HashMapBuilder.put(
			LocaleUtil.getDefault(), value
		).build();
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());
		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(), "-",
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());
		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		return mockLiferayPortletRenderRequest;
	}

	private PortletURL _getPortletURL(
			String highlightedDDMStructureId, String tab)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		mockLiferayPortletRenderRequest.setParameter(
			"highlightedDDMStructureId", highlightedDDMStructureId);

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.journal.web.internal.display.context." +
					"JournalDisplayContext"),
			"getPortletURL", new Class<?>[] {String.class}, tab);
	}

	private SearchContainer<Object> _getSearchContainer() throws Exception {
		return _getSearchContainer(
			StringPool.BLANK, SearchContainer.DEFAULT_CUR,
			SearchContainer.DEFAULT_DELTA);
	}

	private SearchContainer<Object> _getSearchContainer(
			String keywords, int cur, int delta)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		mockLiferayPortletRenderRequest.setParameter(
			SearchContainer.DEFAULT_CUR_PARAM, String.valueOf(cur));
		mockLiferayPortletRenderRequest.setParameter(
			SearchContainer.DEFAULT_DELTA_PARAM, String.valueOf(delta));

		if (Validator.isNotNull(keywords)) {
			mockLiferayPortletRenderRequest.setParameter("keywords", keywords);
		}

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.journal.web.internal.display.context." +
					"JournalDisplayContext"),
			"getSearchContainer", new Class<?>[0]);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(_user);

		return themeDisplay;
	}

	private boolean _isShowBreadcrumb(
			String keywords, JournalFolder journalFolder)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_renderPortlet();

		mockLiferayPortletRenderRequest.setParameter("keywords", keywords);

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.journal.web.internal.display.context." +
					"JournalDisplayContext"),
			"isShowBreadcrumb", new Class<?>[] {JournalFolder.class},
			journalFolder);
	}

	private MockLiferayPortletRenderRequest _renderPortlet() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.portlet.JournalPortlet"
	)
	private Portlet _portlet;

	private ServiceContext _serviceContext;
	private User _user;

}