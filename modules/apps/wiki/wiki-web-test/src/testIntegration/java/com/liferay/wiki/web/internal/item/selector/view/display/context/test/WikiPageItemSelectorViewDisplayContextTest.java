/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.item.selector.view.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
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
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.wiki.item.selector.WikiPageItemSelectorCriterion;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.test.util.WikiTestUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class WikiPageItemSelectorViewDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_serviceContext.setAddGuestPermissions(false);

		_node = _wikiNodeLocalService.addNode(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _serviceContext);

		_user = UserTestUtil.addUser();
	}

	@Test
	@TestInfo("LPD-115810")
	public void testGetNode() throws Exception {
		Assert.assertThrows(
			PrincipalException.MustHavePermission.class, () -> _getNode());

		_userLocalService.addGroupUser(_group.getGroupId(), _user);

		WikiNode node = _getNode();

		Assert.assertEquals(_node.getNodeId(), node.getNodeId());
	}

	@Test
	@TestInfo("LPD-115810")
	public void testGetSearchContainer() throws Exception {
		WikiPage page1 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			_serviceContext);
		WikiPage page2 = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), _node.getNodeId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			_serviceContext);

		Role role = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.SITE_MEMBER);

		_resourcePermissionLocalService.removeResourcePermission(
			_group.getCompanyId(), WikiPage.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(page2.getResourcePrimKey()), role.getRoleId(),
			ActionKeys.VIEW);

		_userLocalService.addGroupUser(_group.getGroupId(), _user);

		SearchContainer<WikiPage> searchContainer = _getSearchContainer();

		Assert.assertEquals(1, searchContainer.getTotal());
		Assert.assertEquals(
			Collections.singletonList(page1), searchContainer.getResults());
	}

	private WikiNode _getNode() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		_itemSelectorView.renderHTML(
			mockHttpServletRequest, new MockHttpServletResponse(),
			new WikiPageItemSelectorCriterion(
				_node.getNodeId(), WorkflowConstants.STATUS_APPROVED),
			new MockLiferayPortletURL(), RandomTestUtil.randomString(), false);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			return ReflectionTestUtil.invoke(
				mockHttpServletRequest.getAttribute(
					"WIKI_PAGE_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT"),
				"getNode", new Class<?>[0]);
		}
	}

	private SearchContainer<WikiPage> _getSearchContainer() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			_itemSelectorView.renderHTML(
				mockHttpServletRequest, new MockHttpServletResponse(),
				new WikiPageItemSelectorCriterion(
					_node.getNodeId(), WorkflowConstants.STATUS_APPROVED),
				new MockLiferayPortletURL(), RandomTestUtil.randomString(),
				false);

			return ReflectionTestUtil.invoke(
				mockHttpServletRequest.getAttribute(
					"WIKI_PAGE_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT"),
				"getSearchContainer",
				new Class<?>[] {
					HttpServletRequest.class, LiferayPortletResponse.class,
					RenderRequest.class
				},
				mockHttpServletRequest, new MockLiferayPortletRenderResponse(),
				new MockLiferayPortletRenderRequest());
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.wiki.web.internal.item.selector.view.WikiPageItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<WikiPageItemSelectorCriterion> _itemSelectorView;

	private WikiNode _node;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WikiNodeLocalService _wikiNodeLocalService;

}