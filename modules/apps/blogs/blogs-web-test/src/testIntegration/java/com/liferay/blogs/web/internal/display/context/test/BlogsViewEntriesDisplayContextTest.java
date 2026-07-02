/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
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
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
@Sync
public class BlogsViewEntriesDisplayContextTest {

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
	public void testGetSearchContainer() throws Exception {
		List<BlogsEntry> blogsEntries = new ArrayList<>();

		for (int i = 0; i < SearchContainer.DEFAULT_DELTA; i++) {
			blogsEntries.add(
				_addBlogsEntry(RandomTestUtil.randomString(), 1900 + i));
		}

		_addBlogsEntry(
			RandomTestUtil.randomString(),
			1900 + SearchContainer.DEFAULT_DELTA);

		_assertSearchContainer(
			blogsEntries,
			_getSearchContainer(
				_getMockHttpServletRequestWithOrderBy("display-date", "asc")));
	}

	@Test
	public void testGetSearchContainerByComment() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(RandomTestUtil.randomString());

		String commentBody = RandomTestUtil.randomString();

		CommentManagerUtil.addComment(
			TestPropsValues.getUserId(), _group.getGroupId(),
			BlogsEntry.class.getName(), blogsEntry.getEntryId(), commentBody,
			new IdentityServiceContextFunction(
				ServiceContextTestUtil.getServiceContext()));

		_assertSearchContainer(
			Arrays.asList(blogsEntry),
			_getSearchContainer(
				_getMockHttpServletRequestWithSearch(commentBody)));
	}

	@Test
	public void testGetSearchContainerByContent() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(RandomTestUtil.randomString());

		_addBlogsEntry(RandomTestUtil.randomString());

		_assertSearchContainer(
			Arrays.asList(blogsEntry),
			_getSearchContainer(
				_getMockHttpServletRequestWithSearch(blogsEntry.getContent())));
	}

	@Test
	public void testGetSearchContainerByInternalAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL);

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		BlogsEntry blogsEntry = _addBlogsEntry(
			new long[] {assetCategory.getCategoryId()});

		_addBlogsEntry(RandomTestUtil.randomString());

		_assertSearchContainer(
			Arrays.asList(blogsEntry),
			_getSearchContainer(
				_getMockHttpServletRequestWithSearch(assetCategory.getName())));
	}

	@Test
	public void testGetSearchContainerByPublicAssetCategory() throws Exception {
		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC);

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		BlogsEntry blogsEntry = _addBlogsEntry(
			new long[] {assetCategory.getCategoryId()});

		_addBlogsEntry(RandomTestUtil.randomString());

		_assertSearchContainer(
			Arrays.asList(blogsEntry),
			_getSearchContainer(
				_getMockHttpServletRequestWithSearch(assetCategory.getName())));
	}

	@Test
	public void testGetSearchContainerByTitle() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(RandomTestUtil.randomString());

		_addBlogsEntry(RandomTestUtil.randomString());

		_assertSearchContainer(
			Arrays.asList(blogsEntry),
			_getSearchContainer(
				_getMockHttpServletRequestWithSearch(blogsEntry.getTitle())));
	}

	@Test
	public void testGetSearchContainerOrderByDisplayDate() throws Exception {
		BlogsEntry blogsEntry1 = _addBlogsEntry(
			RandomTestUtil.randomString(), 2000);
		BlogsEntry blogsEntry2 = _addBlogsEntry(
			RandomTestUtil.randomString(), 2001);
		BlogsEntry blogsEntry3 = _addBlogsEntry(
			RandomTestUtil.randomString(), 1999);

		_assertSearchContainer(
			Arrays.asList(blogsEntry3, blogsEntry1, blogsEntry2),
			_getSearchContainer(
				_getMockHttpServletRequestWithOrderBy("display-date", "asc")));
		_assertSearchContainer(
			Arrays.asList(blogsEntry2, blogsEntry1, blogsEntry3),
			_getSearchContainer(
				_getMockHttpServletRequestWithOrderBy("display-date", "desc")));
	}

	private AssetCategory _addAssetCategory(AssetVocabulary assetVocabulary)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, assetVocabulary.getVocabularyId(), false, null,
			new ServiceContext());
	}

	private AssetVocabulary _addAssetVocabulary(int visibilityTypePublic)
		throws Exception {

		return _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(), null,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			null, null, visibilityTypePublic, new ServiceContext());
	}

	private BlogsEntry _addBlogsEntry(long[] assetCategoryIds)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		return _addBlogsEntry(RandomTestUtil.randomString(), serviceContext);
	}

	private BlogsEntry _addBlogsEntry(String title) throws Exception {
		return _addBlogsEntry(
			title,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private BlogsEntry _addBlogsEntry(String title, int displayYear)
		throws Exception {

		return _addBlogsEntry(
			title, RandomTestUtil.randomString(), displayYear,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private BlogsEntry _addBlogsEntry(
			String title, ServiceContext serviceContext)
		throws Exception {

		return _addBlogsEntry(
			title, RandomTestUtil.randomString(), serviceContext);
	}

	private BlogsEntry _addBlogsEntry(
			String title, String content, int displayYear,
			ServiceContext serviceContext)
		throws Exception {

		return _blogsEntryService.addEntry(
			title, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			content, 1, 1, displayYear, 1, 1, true, false, new String[0],
			RandomTestUtil.randomString(), null, null, serviceContext);
	}

	private BlogsEntry _addBlogsEntry(
			String title, String content, ServiceContext serviceContext)
		throws Exception {

		return _addBlogsEntry(title, content, 1990, serviceContext);
	}

	private void _assertSearchContainer(
		List<BlogsEntry> expectedBlogsEntries,
		SearchContainer<BlogsEntry> searchContainer) {

		List<BlogsEntry> blogsEntries = searchContainer.getResults();

		Assert.assertEquals(
			blogsEntries.toString(), expectedBlogsEntries.size(),
			blogsEntries.size());

		for (int i = 0; i < expectedBlogsEntries.size(); i++) {
			Assert.assertEquals(
				expectedBlogsEntries.get(i), blogsEntries.get(i));
		}
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _getMockHttpServletRequestWithOrderBy(
			String orderByCol, String orderByType)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setParameter("orderByCol", orderByCol);
		mockHttpServletRequest.setParameter("orderByType", orderByType);

		return mockHttpServletRequest;
	}

	private MockHttpServletRequest _getMockHttpServletRequestWithSearch(
			String keywords)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"mvcRenderCommandName", "/blogs/search");
		mockHttpServletRequest.setParameter("keywords", keywords);

		return mockHttpServletRequest;
	}

	private SearchContainer<BlogsEntry> _getSearchContainer(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		Object blogsViewEntriesDisplayContext =
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.blogs.web.internal.display.context." +
					"BlogsViewEntriesDisplayContext");

		return ReflectionTestUtil.invoke(
			blogsViewEntriesDisplayContext, "getSearchContainer",
			new Class<?>[0], null);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_layout.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

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

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.portlet.action.ViewMVCRenderCommand"
	)
	private MVCRenderCommand _mvcRenderCommand;

}