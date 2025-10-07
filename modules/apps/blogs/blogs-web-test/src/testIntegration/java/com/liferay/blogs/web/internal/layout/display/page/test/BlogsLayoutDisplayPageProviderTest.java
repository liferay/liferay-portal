/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class BlogsLayoutDisplayPageProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntry(
			ServiceContextTestUtil.getServiceContext());

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				blogsEntry.getGroupId(),
				new InfoItemReference(
					BlogsEntry.class.getName(),
					new ERCInfoItemIdentifier(
						blogsEntry.getExternalReferenceCode())));

		Assert.assertEquals(
			blogsEntry, layoutDisplayPageObjectProvider.getDisplayObject());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					BlogsEntry.class.getName(),
					new ERCInfoItemIdentifier(
						blogsEntry.getExternalReferenceCode(),
						group.getExternalReferenceCode())));

		Assert.assertEquals(
			blogsEntry, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					BlogsEntry.class.getName(),
					new ERCInfoItemIdentifier(
						blogsEntry.getExternalReferenceCode())));

		Assert.assertNull(layoutDisplayPageObjectProvider);
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderLatestUrlTitle()
		throws Exception {

		BlogsEntry blogsEntry = _addBlogsEntry(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		BlogsEntry updatedBlogsEntry = _updateBlogsEntry(
			blogsEntry, RandomTestUtil.randomString());

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				TestPropsValues.getGroupId(), updatedBlogsEntry.getUrlTitle());

		Assert.assertEquals(
			updatedBlogsEntry,
			layoutDisplayPageObjectProvider.getDisplayObject());
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderPreviousUrlTitle()
		throws Exception {

		BlogsEntry blogsEntry = _addBlogsEntry(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		String previousUrlTitle = blogsEntry.getUrlTitle();

		BlogsEntry updatedBlogsEntry = _updateBlogsEntry(
			blogsEntry, RandomTestUtil.randomString());

		Assert.assertNotEquals(
			previousUrlTitle, updatedBlogsEntry.getUrlTitle());

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				TestPropsValues.getGroupId(), previousUrlTitle);

		Assert.assertEquals(
			updatedBlogsEntry,
			layoutDisplayPageObjectProvider.getDisplayObject());
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderSingleUrlTitle()
		throws Exception {

		BlogsEntry blogsEntry = _addBlogsEntry(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				TestPropsValues.getGroupId(), blogsEntry.getUrlTitle());

		Assert.assertEquals(
			blogsEntry, layoutDisplayPageObjectProvider.getDisplayObject());
	}

	@Test
	public void testGetLayoutDisplayPageObjectProviderWithCategoriesInUrlTitle()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds",
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		BlogsEntry blogsEntry = _addBlogsEntry(serviceContext);

		String urlTitle = StringBundler.concat(
			assetCategory1.getTitle(LocaleUtil.getDefault()), StringPool.SLASH,
			assetCategory2.getTitle(LocaleUtil.getDefault()), StringPool.SLASH,
			blogsEntry.getUrlTitle());

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				TestPropsValues.getGroupId(), urlTitle);

		Assert.assertEquals(
			blogsEntry, layoutDisplayPageObjectProvider.getDisplayObject());
	}

	private BlogsEntry _addBlogsEntry(ServiceContext serviceContext)
		throws Exception {

		return _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);
	}

	private BlogsEntry _updateBlogsEntry(BlogsEntry blogsEntry, String urlTitle)
		throws Exception {

		return _blogsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), blogsEntry.getEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			urlTitle, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), false, false, null, null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.layout.display.page.BlogsLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider _layoutDisplayPageProvider;

}