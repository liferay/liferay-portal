/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletResponse;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
@Sync
public class GetPagePreviewStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = new ServiceContext();

		_serviceContext.setScopeGroupId(_group.getGroupId());
		_serviceContext.setUserId(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_setUpThemeDisplay();
	}

	@Test
	public void testGetPagePreviewAssetDisplayPage() throws Exception {
		_serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		_addLayout(_group, false, LayoutConstants.TYPE_ASSET_DISPLAY);

		_assertContainsContent();
	}

	@Test
	public void testGetPagePreviewContentPage() throws Exception {
		_addLayout(_group, false, LayoutConstants.TYPE_CONTENT);

		_assertContainsContent();
	}

	@Test
	@TestInfo({"LPD-25388", "LPD-45279"})
	public void testGetPagePreviewContentPageLayoutSetNoDefaultTheme()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_addLayout(group, false, LayoutConstants.TYPE_CONTENT);

		_layoutSetLocalService.updateLookAndFeel(
			group.getGroupId(), false, "minium_WAR_miniumtheme", null, null);

		_assertContainsContent("minium_WAR_miniumtheme");

		_layoutSetLocalService.updateLookAndFeel(
			group.getGroupId(), false, "speedwell_WAR_speedwelltheme", null,
			null);

		_assertContainsContent("speedwell_WAR_speedwelltheme");
	}

	@Test
	@TestInfo("LPD-45260")
	public void testGetPagePreviewContentPageWithSpecificTheme()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		Layout layout = _addLayout(group, false, LayoutConstants.TYPE_CONTENT);

		_layoutSetLocalService.updateLookAndFeel(
			group.getGroupId(), false, "dialect_WAR_dialecttheme", null, null);

		_assertContainsContent("dialect_WAR_dialecttheme");

		_layoutLocalService.updateLookAndFeel(
			group.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			"classic_WAR_classictheme", "01", null);

		_assertContainsContent("classic_WAR_classictheme");
	}

	@Test
	public void testGetPagePreviewPageTemplate() throws Exception {
		_addLayout(_group, true, LayoutConstants.TYPE_CONTENT);

		_assertContainsContent();
	}

	@Test
	@TestInfo("LPS-158527")
	public void testGetPagePreviewWithoutPermissions() throws Exception {
		_addLayout(_group, false, LayoutConstants.TYPE_CONTENT);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_themeDisplay.setRealUser(company.getGuestUser());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_getPagePreviewStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());
	}

	private Layout _addLayout(Group group, boolean privateLayout, String type)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			privateLayout, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, type, false, privateLayout, StringPool.BLANK,
			serviceContext);

		_fragmentEntryLink = ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			null, layout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));

		return layout;
	}

	private void _assertContainsContent() throws Exception {
		_assertContainsContent(PropsValues.DEFAULT_REGULAR_THEME_ID);
	}

	private void _assertContainsContent(String expectedThemeId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						_fragmentEntryLink.getPlid())));
		mockHttpServletRequest.addParameter(
			"selPlid", String.valueOf(_fragmentEntryLink.getPlid()));
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		_serviceContext.setRequest(mockHttpServletRequest);

		_themeDisplay.setRequest(mockHttpServletRequest);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_getPagePreviewStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertThat(
			content, CoreMatchers.containsString(_fragmentEntryLink.getCss()));
		Assert.assertThat(
			content, CoreMatchers.containsString(_fragmentEntryLink.getHtml()));
		Assert.assertThat(
			content, CoreMatchers.containsString(_fragmentEntryLink.getJs()));
		Assert.assertThat(
			content, CoreMatchers.containsString("themeId=" + expectedThemeId));
	}

	private void _setUpThemeDisplay() throws Exception {
		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setLanguageId(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));

		Layout layout = _layoutLocalService.getLayout(
			_layoutService.getControlPanelLayoutPlid());

		_themeDisplay.setLayout(layout);
		_themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());

		_themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		_themeDisplay.setPlid(layout.getPlid());
		_themeDisplay.setRealUser(TestPropsValues.getUser());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private FragmentEntryLink _fragmentEntryLink;

	@Inject(
		filter = "component.name=com.liferay.layout.internal.struts.GetPagePreviewStrutsAction"
	)
	private StrutsAction _getPagePreviewStrutsAction;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutService _layoutService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;
	private ThemeDisplay _themeDisplay;

}