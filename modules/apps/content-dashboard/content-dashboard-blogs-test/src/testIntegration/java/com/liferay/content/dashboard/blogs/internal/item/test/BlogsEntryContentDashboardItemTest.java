/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.blogs.internal.item.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
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

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class BlogsEntryContentDashboardItemTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), 0);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testGetAssetCategories() throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			_serviceContext);

		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			Collections.singletonList(assetCategory),
			contentDashboardItem.getAssetCategories());
	}

	@Test
	public void testGetAssetCategoriesByAssetVocabulary() throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			_serviceContext);

		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			Collections.singletonList(assetCategory),
			contentDashboardItem.getAssetCategories(
				assetCategory.getVocabularyId()));
	}

	@Test
	public void testGetAssetCategoriesByAssetVocabularyWithEmptyAssetCategories()
		throws Exception {

		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			Collections.emptyList(),
			contentDashboardItem.getAssetCategories(
				RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetCategoriesWithNoAssetCategoriesInAssetVocabulary()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			_serviceContext);

		_serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			Collections.emptyList(),
			contentDashboardItem.getAssetCategories(
				RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetTags() throws Exception {
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), _serviceContext);

		_serviceContext.setAssetTagNames(new String[] {assetTag.getName()});

		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			Collections.singletonList(assetTag),
			contentDashboardItem.getAssetTags());
	}

	@Test
	public void testGetAvailableLocales() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		List<Locale> availableLocales =
			contentDashboardItem.getAvailableLocales();

		Assert.assertEquals(
			availableLocales.toString(), 0, availableLocales.size());
	}

	@Test
	public void testGetContentDashboardItemSubtype() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertNull(
			contentDashboardItem.getContentDashboardItemSubtype());
	}

	@Test
	public void testGetCreateDate() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			blogsEntry.getCreateDate(), contentDashboardItem.getCreateDate());
	}

	@Test
	public void testGetDescription() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			blogsEntry.getSubtitle(),
			contentDashboardItem.getDescription(LocaleUtil.US));
	}

	@Test
	public void testGetInfoItemReference() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		InfoItemReference infoItemReference = new InfoItemReference(
			BlogsEntry.class.getName(), blogsEntry.getEntryId());

		Assert.assertEquals(
			infoItemReference, contentDashboardItem.getInfoItemReference());
	}

	@Test
	public void testGetLatestContentDashboardItemVersions() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		List<ContentDashboardItemVersion> latestContentDashboardItemVersions =
			contentDashboardItem.getLatestContentDashboardItemVersions(
				LocaleUtil.US);

		Assert.assertEquals(
			latestContentDashboardItemVersions.toString(), 1,
			latestContentDashboardItemVersions.size());

		ContentDashboardItemVersion contentDashboardItemVersion =
			latestContentDashboardItemVersions.get(0);

		Assert.assertNull(contentDashboardItemVersion.getChangeLog());
		Assert.assertEquals(
			blogsEntry.getCreateDate(),
			contentDashboardItemVersion.getCreateDate());
		Assert.assertEquals(
			_language.get(LocaleUtil.US, "approved"),
			contentDashboardItemVersion.getLabel());
		Assert.assertEquals(
			WorkflowConstants.getStatusStyle(blogsEntry.getStatus()),
			contentDashboardItemVersion.getStyle());
		Assert.assertEquals(
			blogsEntry.getUserName(),
			contentDashboardItemVersion.getUserName());
		Assert.assertEquals("1.0", contentDashboardItemVersion.getVersion());
	}

	@Test
	public void testGetModifiedDate() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			blogsEntry.getModifiedDate(),
			contentDashboardItem.getModifiedDate());
	}

	@Test
	public void testGetScopeName() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			_group.getDescriptiveName(LocaleUtil.US),
			contentDashboardItem.getScopeName(LocaleUtil.US));
	}

	@Test
	public void testGetSpecificInformationList() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		List<ContentDashboardItem.SpecificInformation<?>> specificInformations =
			contentDashboardItem.getSpecificInformationList(LocaleUtil.US);

		Assert.assertEquals(
			specificInformations.toString(), 1, specificInformations.size());

		ContentDashboardItem.SpecificInformation<?> specificInformation =
			specificInformations.get(0);

		Assert.assertEquals(
			specificInformation.getValue(), blogsEntry.getDisplayDate());
	}

	@Test
	public void testGetSubtype() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		ContentDashboardItemSubtype contentDashboardItemType =
			contentDashboardItem.getContentDashboardItemSubtype();

		Assert.assertNull(contentDashboardItemType);
	}

	@Test
	public void testGetTitle() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			blogsEntry.getTitle(),
			contentDashboardItem.getTitle(LocaleUtil.US));
	}

	@Test
	public void testGetUserId() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			blogsEntry.getUserId(), contentDashboardItem.getUserId());
	}

	@Test
	public void testGetUserName() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		Assert.assertEquals(
			blogsEntry.getUserName(), contentDashboardItem.getUserName());
	}

	@Test
	public void testIsViewable() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				blogsEntry.getGroupId(),
				_portal.getClassNameId(BlogsEntry.class.getName()), 0, true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			blogsEntry.getUserId(), blogsEntry.getGroupId(),
			_portal.getClassNameId(BlogsEntry.class.getName()),
			blogsEntry.getEntryId(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_DEFAULT, _serviceContext);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		Assert.assertTrue(contentDashboardItem.isViewable(httpServletRequest));
	}

	@Test
	public void testIsViewableWithNoDisplayPage() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);

		ContentDashboardItem contentDashboardItem =
			_contentDashboardItemFactory.create(blogsEntry.getEntryId());

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(LocaleUtil.US));

		Assert.assertFalse(contentDashboardItem.isViewable(httpServletRequest));
	}

	private ThemeDisplay _getThemeDisplay(Locale locale) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(locale);
		themeDisplay.setSiteGroupId(_group.getGroupId());

		return themeDisplay;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.blogs.internal.item.BlogsEntryContentDashboardItemFactory"
	)
	private ContentDashboardItemFactory _contentDashboardItemFactory;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}