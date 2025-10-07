/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class GetLayoutsStrutsActionTest {

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
				PropsValues.class, "LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN",
				_PAGE_SIZE);
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

		_themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			_layoutLocalService.getLayout(
				_portal.getControlPanelPlid(_group.getCompanyId())));
		_user = UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER);
	}

	@Test
	@TestInfo("LPS-106110")
	public void testGetLayoutsStrutsActionWithPagination() throws Exception {
		Map<Long, List<Long>> layoutIdsMap = _getLayoutIdsMap(
			_COUNT_ROOT_LAYOUTS, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		int count = layoutIdsMap.size();

		int pagesCount = count / _PAGE_SIZE;
		int remainingItemsCount = count % _PAGE_SIZE;

		int lastPageIndex = pagesCount - 1;

		if (remainingItemsCount > 0) {
			lastPageIndex++;
		}

		for (int i = 0, offset = 0; i < pagesCount;
			 i++, offset = offset + _PAGE_SIZE) {

			_assertGetLayoutsStrutsAction(
				_PAGE_SIZE, i < lastPageIndex, layoutIdsMap, offset,
				offset + _PAGE_SIZE);
		}

		if (remainingItemsCount > 0) {
			_assertGetLayoutsStrutsAction(
				remainingItemsCount, false, layoutIdsMap,
				pagesCount * _PAGE_SIZE, count);
		}
	}

	private void _assertGetLayoutsStrutsAction(
			int count, boolean hasMoreElements,
			Map<Long, List<Long>> layoutIdsMap, int start, int end)
		throws Exception {

		JSONObject jsonObject = _getLayoutsStrutsActionResponseJSONObject(
			start, end);

		Assert.assertEquals(
			hasMoreElements, jsonObject.getBoolean("hasMoreElements"));

		JSONArray jsonArray = jsonObject.getJSONArray("items");

		Assert.assertEquals(count, jsonArray.length());

		for (int j = 0; j < jsonArray.length(); j++) {
			JSONObject layoutJSONObject = jsonArray.getJSONObject(j);

			Assert.assertTrue(layoutJSONObject.has("hasChildren"));
			Assert.assertTrue(layoutJSONObject.has("paginated"));

			long layoutId = layoutJSONObject.getLong("layoutId");

			List<Long> childrenLayoutIds = layoutIdsMap.remove(layoutId);

			Assert.assertNotNull(childrenLayoutIds);

			Assert.assertEquals(
				ListUtil.isNotEmpty(childrenLayoutIds),
				layoutJSONObject.getBoolean("hasChildren"));
			Assert.assertEquals(
				childrenLayoutIds.size() > GetterUtil.getInteger(
					PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN),
				layoutJSONObject.getBoolean("paginated"));
		}
	}

	private Map<Long, List<Long>> _getLayoutIdsMap(
			int count, long parentLayoutId)
		throws Exception {

		Map<Long, List<Long>> layoutIdsMap = new HashMap<>();

		for (int i = 0; i < count; i++) {
			Layout layout = LayoutLocalServiceUtil.addLayout(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				parentLayoutId, RandomTestUtil.randomString(), StringPool.BLANK,
				StringPool.BLANK, LayoutConstants.TYPE_CONTENT, false,
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

			if (_randomBooleanWithProbability(_DRAFT_LAYOUT_PROBABILITY)) {
				continue;
			}

			ContentLayoutTestUtil.publishLayout(
				layout.fetchDraftLayout(), layout);

			if (_randomBooleanWithProbability(_RESTRICTED_LAYOUT_PROBABILITY)) {
				RoleTestUtil.removeResourcePermission(
					RoleConstants.GUEST, Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(layout.getPlid()), ActionKeys.VIEW);
				RoleTestUtil.removeResourcePermission(
					RoleConstants.SITE_MEMBER, Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(layout.getPlid()), ActionKeys.VIEW);

				continue;
			}

			List<Long> childrenLayoutIds = new ArrayList<>();

			if ((parentLayoutId == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) &&
				_randomBooleanWithProbability(_CHILDREN_PROBABILITY)) {

				Map<Long, List<Long>> childrenLayoutIdsMap = _getLayoutIdsMap(
					_COUNT_CHILDREN_LAYOUTS, layout.getLayoutId());

				childrenLayoutIds.addAll(childrenLayoutIdsMap.keySet());
			}

			layoutIdsMap.put(layout.getLayoutId(), childrenLayoutIds);
		}

		return layoutIdsMap;
	}

	private JSONObject _getLayoutsStrutsActionResponseJSONObject(
			int start, int end)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(start, end);
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try {
			UserTestUtil.setUser(_user);

			_entityCache.clearCache();
			_multiVMPool.clear();

			try (LoggingTimer loggingTimer = new LoggingTimer()) {
				_getLayoutsStrutsAction.execute(
					mockHttpServletRequest, mockHttpServletResponse);
			}
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}

		return _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		int start, int end) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockHttpServletRequest.addParameter("start", String.valueOf(start));
		mockHttpServletRequest.addParameter("end", String.valueOf(end));
		mockHttpServletRequest.setAttribute(
			WebKeys.LAYOUT, _themeDisplay.getLayout());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		return mockHttpServletRequest;
	}

	private boolean _randomBooleanWithProbability(int probability) {
		if (RandomTestUtil.randomInt(1, 101) <= probability) {
			return true;
		}

		return false;
	}

	private static final int _CHILDREN_PROBABILITY = 50;

	private static final int _COUNT_CHILDREN_LAYOUTS = 5;

	private static final int _COUNT_ROOT_LAYOUTS = 5;

	private static final int _DRAFT_LAYOUT_PROBABILITY = 10;

	private static final int _PAGE_SIZE = 2;

	private static final int _RESTRICTED_LAYOUT_PROBABILITY = 10;

	private static int _originalLayoutManagePagesInitialChildren;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject(filter = "path=/portal/get_layouts")
	private StrutsAction _getLayoutsStrutsAction;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	private ThemeDisplay _themeDisplay;
	private User _user;

}