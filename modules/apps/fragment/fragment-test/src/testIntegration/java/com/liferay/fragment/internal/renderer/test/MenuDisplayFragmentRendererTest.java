/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class MenuDisplayFragmentRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			TestPropsValues.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPD-66793")
	public void testRenderWithExternalReferenceCodeReference()
		throws Exception {

		_testRenderWithExternalReferenceCodeReference(_group, StringPool.BLANK);

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_testRenderWithExternalReferenceCodeReference(
			companyGroup, companyGroup.getExternalReferenceCode());
	}

	@Test
	@TestInfo("LPD-66793")
	public void testRenderWithIdReference() throws Exception {
		_testRenderWithIdReference(_group);
		_testRenderWithIdReference(
			_groupLocalService.getCompanyGroup(TestPropsValues.getCompanyId()));
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String editableValues, long plid)
		throws Exception {

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), null, null,
			null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid),
			plid, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, editableValues, StringPool.BLANK, 0,
			"com.liferay.fragment.renderer.menu.display.internal." +
				"MenuDisplayFragmentRenderer",
			FragmentConstants.TYPE_COMPONENT, _serviceContext);
	}

	private SiteNavigationMenu _addSiteNavigationMenu(long groupId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return _siteNavigationMenuLocalService.addSiteNavigationMenu(
			null, TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), SiteNavigationConstants.TYPE_DEFAULT,
			true, serviceContext);
	}

	private SiteNavigationMenuItem _addURLSiteNavigationMenuItem(
			long groupId, long parentSiteNavigationMenuItemId,
			long siteNavigationMenuId, String url)
		throws Exception {

		return _siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			null, TestPropsValues.getUserId(), groupId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId,
			SiteNavigationMenuItemTypeConstants.URL,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"url", url
			).buildString(),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			TilesUtil.DEFINITION,
			new Definition(StringPool.BLANK, new HashMap<>()));

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group, _layout);

		themeDisplay.setRequest(httpServletRequest);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		_serviceContext.setRequest(httpServletRequest);

		return httpServletRequest;
	}

	private String _render(FragmentEntryLink fragmentEntryLink)
		throws Exception {

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setMode(FragmentEntryLinkConstants.VIEW);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_fragmentRenderer.render(
			defaultFragmentRendererContext, _getHttpServletRequest(),
			mockHttpServletResponse);

		return mockHttpServletResponse.getContentAsString();
	}

	private void _testRenderWithExternalReferenceCodeReference(
			Group group, String siteNavigationMenuScopeExternalReferenceCode)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu = _addSiteNavigationMenu(
			group.getGroupId());

		String url1 = RandomTestUtil.randomString();

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addURLSiteNavigationMenuItem(
				group.getGroupId(), 0,
				siteNavigationMenu.getSiteNavigationMenuId(), url1);

		String url2 = RandomTestUtil.randomString();

		_addURLSiteNavigationMenuItem(
			group.getGroupId(),
			siteNavigationMenuItem1.getSiteNavigationMenuItemId(),
			siteNavigationMenu.getSiteNavigationMenuId(), url2);

		String content = _render(
			_addFragmentEntryLink(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"displayStyle", "stacked"
					).put(
						"source",
						JSONUtil.put(
							"siteNavigationMenuExternalReferenceCode",
							siteNavigationMenu.getExternalReferenceCode()
						).put(
							"siteNavigationMenuScopeExternalReferenceCode",
							siteNavigationMenuScopeExternalReferenceCode
						)
					).put(
						"sublevels", "-1"
					)
				).toString(),
				_layout.getPlid()));

		Assert.assertTrue(content.contains(url1));
		Assert.assertTrue(content.contains(url2));

		content = _render(
			_addFragmentEntryLink(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"displayStyle", "stacked"
					).put(
						"source",
						JSONUtil.put(
							"parentSiteNavigationMenuItemExternalReferenceCode",
							siteNavigationMenuItem1.getExternalReferenceCode()
						).put(
							"siteNavigationMenuExternalReferenceCode",
							siteNavigationMenu.getExternalReferenceCode()
						).put(
							"siteNavigationMenuScopeExternalReferenceCode",
							siteNavigationMenuScopeExternalReferenceCode
						)
					).put(
						"sublevels", "-1"
					)
				).toString(),
				_layout.getPlid()));

		Assert.assertFalse(content.contains(url1));
		Assert.assertTrue(content.contains(url2));

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group, _layout.getPlid());

		content = _render(
			_addFragmentEntryLink(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"displayStyle", "stacked"
					).put(
						"source",
						JSONUtil.put(
							"parentSiteNavigationMenuItemExternalReferenceCode",
							_layout.getExternalReferenceCode())
					).put(
						"sublevels", "-1"
					)
				).toString(),
				_layout.getPlid()));

		Assert.assertFalse(content.contains(url1));
		Assert.assertFalse(content.contains(url2));
		Assert.assertTrue(
			content.contains(
				layout.getTitle(
					_portal.getSiteDefaultLocale(_group.getGroupId()))));
	}

	private void _testRenderWithIdReference(Group group) throws Exception {
		SiteNavigationMenu siteNavigationMenu = _addSiteNavigationMenu(
			group.getGroupId());

		String url1 = RandomTestUtil.randomString();

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addURLSiteNavigationMenuItem(
				group.getGroupId(), 0,
				siteNavigationMenu.getSiteNavigationMenuId(), url1);

		String url2 = RandomTestUtil.randomString();

		_addURLSiteNavigationMenuItem(
			group.getGroupId(),
			siteNavigationMenuItem1.getSiteNavigationMenuItemId(),
			siteNavigationMenu.getSiteNavigationMenuId(), url2);

		String content = _render(
			_addFragmentEntryLink(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"displayStyle", "stacked"
					).put(
						"source",
						JSONUtil.put(
							"siteNavigationMenuId",
							String.valueOf(
								siteNavigationMenu.getSiteNavigationMenuId()))
					).put(
						"sublevels", "-1"
					)
				).toString(),
				_layout.getPlid()));

		Assert.assertTrue(content.contains(url1));
		Assert.assertTrue(content.contains(url2));

		content = _render(
			_addFragmentEntryLink(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"displayStyle", "stacked"
					).put(
						"source",
						JSONUtil.put(
							"parentSiteNavigationMenuItemId",
							siteNavigationMenuItem1.
								getSiteNavigationMenuItemId()
						).put(
							"siteNavigationMenuId",
							String.valueOf(
								siteNavigationMenu.getSiteNavigationMenuId())
						)
					).put(
						"sublevels", "-1"
					)
				).toString(),
				_layout.getPlid()));

		Assert.assertFalse(content.contains(url1));
		Assert.assertTrue(content.contains(url2));

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group, _layout.getPlid());

		content = _render(
			_addFragmentEntryLink(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"displayStyle", "stacked"
					).put(
						"source",
						JSONUtil.put(
							"parentSiteNavigationMenuItemId", _layout.getPlid())
					).put(
						"sublevels", "-1"
					)
				).toString(),
				_layout.getPlid()));

		Assert.assertFalse(content.contains(url1));
		Assert.assertFalse(content.contains(url2));
		Assert.assertTrue(
			content.contains(
				layout.getTitle(
					_portal.getSiteDefaultLocale(_group.getGroupId()))));
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.renderer.menu.display.internal.MenuDisplayFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}