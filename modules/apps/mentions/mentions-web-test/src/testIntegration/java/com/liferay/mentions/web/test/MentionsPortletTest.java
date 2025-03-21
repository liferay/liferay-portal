/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mentions.constants.MentionsPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.Portlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class MentionsPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = _addLayout(_group.getGroupId(), TestPropsValues.getUserId());
	}

	@Test
	public void testServletResponseWithoutQuery() throws Exception {
		User user = _addUser("example");

		try {
			JSONArray jsonArray = _getServletResponseJSONArray(null);

			Assert.assertEquals(1, jsonArray.length());

			JSONObject jsonObject = jsonArray.getJSONObject(0);

			Assert.assertEquals("example", jsonObject.getString("screenName"));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testServletResponseWithQueryWithFullScreenName()
		throws Exception {

		User user = _addUser("example");

		try {
			JSONArray jsonArray = _getServletResponseJSONArray("example");

			Assert.assertEquals(1, jsonArray.length());

			JSONObject jsonObject = jsonArray.getJSONObject(0);

			Assert.assertEquals("example", jsonObject.getString("screenName"));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testServletResponseWithQueryWithPartialScreenName()
		throws Exception {

		User user = _addUser("example");

		try {
			JSONArray jsonArray = _getServletResponseJSONArray("exa");

			Assert.assertEquals(1, jsonArray.length());

			JSONObject jsonObject = jsonArray.getJSONObject(0);

			Assert.assertEquals("example", jsonObject.getString("screenName"));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testServletResponseWithQueryWithWildard() throws Exception {
		User user = _addUser("example");

		try {
			JSONArray jsonArray = _getServletResponseJSONArray("");

			Assert.assertEquals(1, jsonArray.length());

			JSONObject jsonObject = jsonArray.getJSONObject(0);

			Assert.assertEquals("example", jsonObject.getString("screenName"));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testServletResponseWithQueryWithWildcardAndNoResults()
		throws Exception {

		JSONArray jsonArray = _getServletResponseJSONArray("");

		Assert.assertEquals(0, jsonArray.length());
	}

	private Layout _addLayout(long groupId, long userId) throws Exception {
		String name = RandomTestUtil.randomString();

		String friendlyURL =
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name);

		return _layoutLocalService.addLayout(
			null, userId, groupId, false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, null,
			RandomTestUtil.randomString(), LayoutConstants.TYPE_PORTLET, false,
			friendlyURL, ServiceContextTestUtil.getServiceContext());
	}

	private User _addUser(String screenName) throws Exception {
		return UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			screenName, LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[] {_group.getGroupId()},
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			String query)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockLiferayResourceRequest.setParameter(
			"discussionPortletId", themeDisplay.getPpid());

		if (query != null) {
			mockLiferayResourceRequest.setParameter("query", query);
		}

		return mockLiferayResourceRequest;
	}

	private JSONArray _getServletResponseJSONArray(String query)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		mvcPortlet.serveResource(
			_getMockLiferayResourceRequest(query), mockLiferayResourceResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayResourceResponse.getHttpServletResponse();

		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON,
			mockHttpServletResponse.getContentType());

		return JSONFactoryUtil.createJSONArray(
			mockHttpServletResponse.getContentAsString());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setPpid(MentionsPortletKeys.MENTIONS);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(filter = "jakarta.portlet.name=" + MentionsPortletKeys.MENTIONS)
	private Portlet _portlet;

	@Inject
	private UserLocalService _userLocalService;

}