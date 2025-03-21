/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.item.selector.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.item.selector.BlogsItemSelectorCriterion;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.ServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class BlogsItemSelectorViewDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testShowDragAndDropZone() throws Exception {
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				_getBlogsItemSelectorViewDisplayContext(),
				"showDragAndDropZone", new Class<?>[] {ThemeDisplay.class},
				_getThemeDisplay()));
	}

	@Test
	public void testShowDragAndDropZoneWithWorkflowEnabled() throws Exception {
		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			_group.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			"Single Approver", 1);

		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				_getBlogsItemSelectorViewDisplayContext(),
				"showDragAndDropZone", new Class<?>[] {ThemeDisplay.class},
				_getThemeDisplay()));
	}

	@FeatureFlags("LPD-29516")
	@Test
	public void testShowDragAndDropZoneWithWorkflowEnabledAndFeatureFlagEnabled()
		throws Exception {

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			_group.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			"Single Approver", 1);

		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				_getBlogsItemSelectorViewDisplayContext(),
				"showDragAndDropZone", new Class<?>[] {ThemeDisplay.class},
				_getThemeDisplay()));
	}

	private Object _getBlogsItemSelectorViewDisplayContext() throws Exception {
		ServletRequest servletRequest = _getMockHttpServletRequest();

		_renderHTML(servletRequest);

		return servletRequest.getAttribute(
			"BLOGS_ITEM_SELECTOR_VIEW_DISPLAY_CONTEXT");
	}

	private ServletRequest _getMockHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _renderHTML(ServletRequest servletRequest) throws Exception {
		_itemSelectorView.renderHTML(
			servletRequest, new MockHttpServletResponse(),
			new BlogsItemSelectorCriterion(), new MockLiferayPortletURL(),
			RandomTestUtil.randomString(), true);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.blogs.item.selector.web.internal.BlogsItemSelectorView",
		type = ItemSelectorView.class
	)
	private ItemSelectorView<BlogsItemSelectorCriterion> _itemSelectorView;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}