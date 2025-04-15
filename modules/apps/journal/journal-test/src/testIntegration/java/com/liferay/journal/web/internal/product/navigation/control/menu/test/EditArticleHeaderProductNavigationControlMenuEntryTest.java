/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.product.navigation.control.menu.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;

import jakarta.portlet.Portlet;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class EditArticleHeaderProductNavigationControlMenuEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		LayoutTestUtil.addTypePortletLayout(_group);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testIsShowJournalArticleHeaderInAnotherView() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertFalse(
			_productNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest(journalArticle)));
	}

	@Test
	public void testIsShowJournalArticleHeaderWithGuestPermissions()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(journalArticle);

		mockHttpServletRequest.addParameter(
			"mvcRenderCommandName", "/journal/edit_article");

		Assert.assertFalse(
			_productNavigationControlMenuEntry.isShow(mockHttpServletRequest));
	}

	@Test
	public void testIsShowJournalArticleHeaderWithoutGuestPermissions()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_resourcePermissionLocalService.deleteResourcePermissions(
			_group.getCompanyId(), JournalArticle.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			journalArticle.getResourcePrimKey());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(journalArticle);

		mockHttpServletRequest.addParameter(
			"mvcRenderCommandName", "/journal/edit_article");

		Assert.assertTrue(
			_productNavigationControlMenuEntry.isShow(mockHttpServletRequest));
	}

	@Test
	public void testIsShowJournalArticleHeaderWithoutJournalArticle()
		throws Exception {

		Assert.assertFalse(
			_productNavigationControlMenuEntry.isShow(
				_getMockHttpServletRequest()));
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(mockHttpServletRequest);

		mockLiferayPortletRenderRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST, mockHttpServletRequest);

		_portlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			JournalArticle journalArticle)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"articleId", journalArticle.getArticleId());

		return mockHttpServletRequest;
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			HttpServletRequest httpServletRequest)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(httpServletRequest);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _group.getCompanyId());

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletRenderRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setId(JournalPortletKeys.JOURNAL);

		Layout layout = new LayoutImpl();

		layout.setType(LayoutConstants.TYPE_CONTROL_PANEL);

		themeDisplay.setLayout(layout);

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.portlet.JournalPortlet"
	)
	private Portlet _portlet;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.product.navigation.control.menu.EditArticleHeaderProductNavigationControlMenuEntry"
	)
	private ProductNavigationControlMenuEntry
		_productNavigationControlMenuEntry;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private static class MockLiferayPortletRenderRequest
		extends com.liferay.portal.kernel.test.portlet.
					MockLiferayPortletRenderRequest {

		public MockLiferayPortletRenderRequest(
			HttpServletRequest httpServletRequest) {

			_httpServletRequest = httpServletRequest;
		}

		@Override
		public void setAttribute(String name, Object value) {
			super.setAttribute(name, value);

			_httpServletRequest.setAttribute(name, value);
		}

		private final HttpServletRequest _httpServletRequest;

	}

}