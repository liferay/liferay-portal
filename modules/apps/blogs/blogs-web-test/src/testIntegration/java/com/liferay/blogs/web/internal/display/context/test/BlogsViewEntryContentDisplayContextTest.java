/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
@Sync
public class BlogsViewEntryContentDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@Test
	public void testGetViewEntryURLWithAssetDisplayPage() throws Exception {
		BlogsEntry entry = _addBlogEntry(
			"beta",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_addAssetDisplayPage(entry);

		String viewEntryURL = _getViewEntryURL(entry);

		Assert.assertTrue(
			viewEntryURL.endsWith(_ENTRY_ASSET_DISPLAY_PAGE_FRIENDLY_URL));
	}

	@Test
	public void testGetViewEntryURLWithAssetDisplayPageAndURLAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_populateServiceContextWithCategory(serviceContext);

		BlogsEntry entry = _addBlogEntry("beta", serviceContext);

		_addAssetDisplayPage(entry);

		String viewEntryURL = _getViewEntryURL(entry);

		Assert.assertTrue(
			viewEntryURL.endsWith(
				_ENTRY_ASSET_DISPLAY_PAGE_CATEGORY_FRIENDLY_URL));
	}

	@Test
	public void testGetViewEntryURLWithoutAssetDisplayPage() throws Exception {
		BlogsEntry entry = _addBlogEntry(
			"alpha",
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			_ENTRY_WITHOUT_ASSET_DISPLAY_PAGE_FRIENDLY_URL,
			_getViewEntryURL(entry));
	}

	@Test
	public void testGetViewEntryURLWithoutAssetDisplayPageAndWithURLAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_populateServiceContextWithCategory(serviceContext);

		BlogsEntry entry = _addBlogEntry("alpha", serviceContext);

		Assert.assertEquals(
			_ENTRY_WITHOUT_ASSET_DISPLAY_PAGE_FRIENDLY_URL,
			_getViewEntryURL(entry));
	}

	private void _addAssetDisplayPage(BlogsEntry entry) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(BlogsEntry.class.getName()), 0, false,
				WorkflowConstants.STATUS_APPROVED);

		serviceContext.setAttribute(
			"assetDisplayPageId",
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		serviceContext.setAttribute(
			"displayPageType", AssetDisplayPageConstants.TYPE_SPECIFIC);

		_assetDisplayPageEntryFormProcessor.process(
			BlogsEntry.class.getName(), entry.getEntryId(), serviceContext);
	}

	private BlogsEntry _addBlogEntry(
			String title, ServiceContext serviceContext)
		throws Exception {

		return _blogsEntryService.addEntry(
			title, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), 1, 1, 1990, 1, 1, true, false,
			new String[0], RandomTestUtil.randomString(), null, null,
			serviceContext);
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new TestMockLiferayPortletRenderRequest(
				new MockHttpServletRequest());

		String path = "/blogs/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);

		ReflectionTestUtil.invoke(
			_portlet, "doDispatch",
			new Class<?>[] {RenderRequest.class, RenderResponse.class},
			mockLiferayPortletRenderRequest,
			new TestMockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setPortletName(BlogsPortletKeys.BLOGS);

		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_layout.getGroupId());
		themeDisplay.setSiteGroupId(_layout.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _getViewEntryURL(BlogsEntry entry) throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		Object blogsViewEntryContentDisplayContext =
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.blogs.web.internal.display.context." +
					"BlogsViewEntryContentDisplayContext");

		return ReflectionTestUtil.invoke(
			blogsViewEntryContentDisplayContext, "getViewEntryURL",
			new Class<?>[] {BlogsEntry.class}, entry);
	}

	private void _populateServiceContextWithCategory(
			ServiceContext serviceContext)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(), null,
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
				new ServiceContext());

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.US, "cat1"
			).build(),
			null, assetVocabulary.getVocabularyId(), null,
			new ServiceContext());

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});
		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds",
			new long[] {assetCategory.getCategoryId()});
	}

	private static final String
		_ENTRY_ASSET_DISPLAY_PAGE_CATEGORY_FRIENDLY_URL = "/b/cat1/beta";

	private static final String _ENTRY_ASSET_DISPLAY_PAGE_FRIENDLY_URL =
		"/b/beta";

	private static final String _ENTRY_WITHOUT_ASSET_DISPLAY_PAGE_FRIENDLY_URL =
		"http//localhost/test?param_mvcRenderCommandName=/blogs/view_entry;" +
			"param_redirect=;param_urlTitle=alpha";

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private BlogsEntryService _blogsEntryService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.portlet.BlogsPortlet"
	)
	private Portlet _portlet;

	private static class TestMockLiferayPortletRenderRequest
		extends MockLiferayPortletRenderRequest {

		public TestMockLiferayPortletRenderRequest(
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

	private static class TestMockLiferayPortletRenderResponse
		extends MockLiferayPortletRenderResponse {

		@Override
		public String getNamespace() {
			return BlogsPortletKeys.BLOGS;
		}

	}

}