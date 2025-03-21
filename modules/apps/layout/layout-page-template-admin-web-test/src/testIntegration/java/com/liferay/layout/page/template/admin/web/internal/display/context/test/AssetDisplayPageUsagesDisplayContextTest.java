/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class AssetDisplayPageUsagesDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testSearchContainerResultsOrderedByModifiedDate()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(BlogsEntry.class.getName()), 0, true,
				WorkflowConstants.STATUS_APPROVED);

		BlogsEntry blogsEntry1 = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			serviceContext);

		serviceContext.setModifiedDate(new Date());

		AssetDisplayPageEntry assetDisplayPageEntry1 =
			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				blogsEntry1.getUserId(), blogsEntry1.getGroupId(),
				_portal.getClassNameId(BlogsEntry.class.getName()),
				blogsEntry1.getEntryId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC, serviceContext);

		BlogsEntry blogsEntry2 = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			serviceContext);

		serviceContext.setModifiedDate(new Date());

		AssetDisplayPageEntry assetDisplayPageEntry2 =
			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				blogsEntry2.getUserId(), blogsEntry2.getGroupId(),
				_portal.getClassNameId(BlogsEntry.class.getName()),
				blogsEntry2.getEntryId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC, serviceContext);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		SearchContainer<AssetDisplayPageEntry> searchContainer =
			_getSearchContainer(
				mockLiferayPortletRenderRequest, layoutPageTemplateEntry,
				"asc");

		Assert.assertEquals(2, searchContainer.getTotal());

		List<AssetDisplayPageEntry> assetDisplayPageEntries =
			searchContainer.getResults();

		AssetDisplayPageEntry actualAssetDisplayPageEntry1 =
			assetDisplayPageEntries.get(0);
		AssetDisplayPageEntry actualAssetDisplayPageEntry2 =
			assetDisplayPageEntries.get(1);

		Assert.assertEquals(
			assetDisplayPageEntry1.getAssetDisplayPageEntryId(),
			actualAssetDisplayPageEntry1.getAssetDisplayPageEntryId());
		Assert.assertEquals(
			assetDisplayPageEntry2.getAssetDisplayPageEntryId(),
			actualAssetDisplayPageEntry2.getAssetDisplayPageEntryId());

		searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest, layoutPageTemplateEntry, "desc");

		Assert.assertEquals(2, searchContainer.getTotal());

		assetDisplayPageEntries = searchContainer.getResults();

		actualAssetDisplayPageEntry1 = assetDisplayPageEntries.get(0);
		actualAssetDisplayPageEntry2 = assetDisplayPageEntries.get(1);

		Assert.assertEquals(
			assetDisplayPageEntry2.getAssetDisplayPageEntryId(),
			actualAssetDisplayPageEntry1.getAssetDisplayPageEntryId());
		Assert.assertEquals(
			assetDisplayPageEntry1.getAssetDisplayPageEntryId(),
			actualAssetDisplayPageEntry2.getAssetDisplayPageEntryId());
	}

	private Object _getAssetDisplayPageUsagesDisplayContext(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest,
			LayoutPageTemplateEntry layoutPageTemplateEntry, String orderByType)
		throws Exception {

		mockLiferayPortletRenderRequest.setParameter(
			SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM, orderByType);
		mockLiferayPortletRenderRequest.setParameter(
			"defaultTemplate",
			String.valueOf(layoutPageTemplateEntry.isDefaultTemplate()));
		mockLiferayPortletRenderRequest.setParameter(
			"classNameId",
			String.valueOf(
				_classNameLocalService.getClassNameId(BlogsEntry.class)));
		mockLiferayPortletRenderRequest.setParameter(
			"layoutPageTemplateEntryId",
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			"ASSET_DISPLAY_PAGE_USAGES_DISPLAY_CONTEXT");
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(), "-",
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _group.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletRenderRequest;
	}

	private SearchContainer<AssetDisplayPageEntry> _getSearchContainer(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest,
			LayoutPageTemplateEntry layoutPageTemplateEntry, String orderByType)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getAssetDisplayPageUsagesDisplayContext(
				mockLiferayPortletRenderRequest, layoutPageTemplateEntry,
				orderByType),
			"getSearchContainer", new Class<?>[0]);
	}

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "component.name=com.liferay.layout.page.template.admin.web.internal.portlet.LayoutPageTemplatesPortlet"
	)
	private Portlet _portlet;

}