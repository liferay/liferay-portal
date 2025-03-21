/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.taglib.servlet.taglib.react.SelectLayoutTag;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class SelectLayoutTagTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws PortalException {
		_originalLayoutManagePagesInitialChildren =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN", 2);
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN",
			_originalLayoutManagePagesInitialChildren);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testUnpublishedChildLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		_layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			layout.getLayoutId(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, LayoutConstants.TYPE_CONTENT,
			false, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SelectLayoutTag selectLayoutTag = new SelectLayoutTag();

		HttpServletRequest httpServletRequest = _getMockHttpServletRequest();

		selectLayoutTag.doTag(
			httpServletRequest, new MockHttpServletResponse());

		Map<String, Object> data =
			(Map<String, Object>)httpServletRequest.getAttribute(
				"liferay-layout:select-layout:data");

		JSONArray nodesJSONArray = (JSONArray)data.get("nodes");

		JSONObject nodeJSONObject = nodesJSONArray.getJSONObject(0);

		JSONArray childrenJSONArray = nodeJSONObject.getJSONArray("children");

		JSONObject childrenJSONObject = childrenJSONArray.getJSONObject(0);

		Assert.assertFalse(childrenJSONObject.getBoolean("hasChildren"));
	}

	@Test
	public void testUnpublishedLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		LayoutTestUtil.addTypeContentLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);

		SelectLayoutTag selectLayoutTag = new SelectLayoutTag();

		HttpServletRequest httpServletRequest = _getMockHttpServletRequest();

		selectLayoutTag.doTag(
			httpServletRequest, new MockHttpServletResponse());

		Map<String, Object> data =
			(Map<String, Object>)httpServletRequest.getAttribute(
				"liferay-layout:select-layout:data");

		JSONArray nodesJSONArray = (JSONArray)data.get("nodes");

		JSONObject nodeJSONObject = nodesJSONArray.getJSONObject(0);

		Assert.assertFalse(nodeJSONObject.getBoolean("paginated"));

		JSONArray childrenJSONArray = nodeJSONObject.getJSONArray("children");

		Assert.assertEquals(1, childrenJSONArray.length());
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setRequest(mockHttpServletRequest);

		return mockHttpServletRequest;
	}

	private static int _originalLayoutManagePagesInitialChildren;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

}