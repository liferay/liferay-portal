/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
@Sync
public class EditSEOMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testCanonicalURLWithMoreThan75Characters() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_setUpMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"canonicalURLEnabled", Boolean.TRUE.toString());

		String url = RandomTestUtil.randomString(76);

		mockLiferayPortletActionRequest.addParameter(
			"canonicalURL_" +
				_language.getLanguageId(LocaleUtil.getSiteDefault()),
			url);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		LayoutSEOEntry layoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				_layout.getGroupId(), _layout.isPrivateLayout(),
				_layout.getLayoutId());

		Assert.assertEquals(
			url, layoutSEOEntry.getCanonicalURL(LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testSitemapIncludeWithEmptyRobots() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_setUpMockLiferayPortletActionRequest();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertNull(
			layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.SITEMAP_INCLUDE));
	}

	@Test
	public void testSitemapIncludeWithNoFollowAndTestRobots() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_setUpMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"robots_en_US", "nofollow\ntest");

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertEquals(
			"0",
			layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.SITEMAP_INCLUDE));
	}

	@Test
	public void testSitemapIncludeWithNoFollowRobots() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_setUpMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"robots_en_US", "nofollow");

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertEquals(
			"0",
			layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.SITEMAP_INCLUDE));
	}

	@Test
	public void testSitemapIncludeWithNoIndexAndTestRobots() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_setUpMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"robots_en_US", "noindex\ntest");

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertEquals(
			"0",
			layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.SITEMAP_INCLUDE));
	}

	@Test
	public void testSitemapIncludeWithNoIndexRobots() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_setUpMockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter("robots_en_US", "noindex");

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(_layout.getPlid());

		Assert.assertEquals(
			"0",
			layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.SITEMAP_INCLUDE));
	}

	@Test
	@TestInfo("LPS-131982")
	public void testUpdateSEOWithMasterLayout() throws Exception {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_layout = _layoutLocalService.updateMasterLayoutPlid(
			_group.getGroupId(), _layout.isPrivateLayout(),
			_layout.getLayoutId(), masterLayoutPageTemplateEntry.getPlid());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_setUpMockLiferayPortletActionRequest();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.fetchLayout(_layout.getPlid());

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			layout.getMasterLayoutPlid());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private MockLiferayPortletActionRequest
			_setUpMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.addParameter(
			"layoutId", String.valueOf(_layout.getLayoutId()));
		mockLiferayPortletActionRequest.addParameter(
			"privateLayout", String.valueOf(_layout.isPrivateLayout()));
		mockLiferayPortletActionRequest.addParameter("redirect", "fakeURL");
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private static Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Inject(filter = "mvc.command.name=/layout/edit_seo")
	private MVCActionCommand _mvcActionCommand;

}