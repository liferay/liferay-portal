/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.info.collection.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.pagination.InfoPage;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class
	AssetEntriesWithSameAssetCategoryRelatedInfoItemCollectionProviderTest {

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
	public void testGetRelatedItemsInfoPage() throws Exception {
		ServiceContext serviceContext = _getServiceContext();

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.addVocabulary(
					TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(), serviceContext);

			AssetCategory assetCategory =
				_assetCategoryLocalService.addCategory(
					TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(),
					assetVocabulary.getVocabularyId(), serviceContext);

			serviceContext.setAssetCategoryIds(
				new long[] {assetCategory.getCategoryId()});

			JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				serviceContext);
			JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				serviceContext);

			RelatedInfoItemCollectionProvider<AssetCategory, AssetEntry>
				relatedInfoItemCollectionProvider =
					_infoItemServiceRegistry.getInfoItemService(
						RelatedInfoItemCollectionProvider.class,
						StringBundler.concat(
							"com.liferay.asset.categories.admin.web.internal.",
							"info.collection.provider.",
							"AssetEntriesWithSameAssetCategoryRelatedInfoItem",
							"CollectionProvider"));

			Assert.assertNotNull(relatedInfoItemCollectionProvider);

			CollectionQuery collectionQuery = new CollectionQuery();

			collectionQuery.setRelatedItemObject(assetCategory);

			InfoPage<? extends AssetEntry> relatedItemsInfoPage =
				relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

			Assert.assertNotNull(relatedItemsInfoPage);

			List<? extends AssetEntry> pageItems =
				relatedItemsInfoPage.getPageItems();

			Assert.assertEquals(pageItems.toString(), 2, pageItems.size());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetRelatedItemsInfoPageLatestVersionInDraftStatus()
		throws Exception {

		ServiceContext serviceContext = _getServiceContext();

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.addVocabulary(
					TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(), serviceContext);

			AssetCategory assetCategory =
				_assetCategoryLocalService.addCategory(
					TestPropsValues.getUserId(), _group.getGroupId(),
					RandomTestUtil.randomString(),
					assetVocabulary.getVocabularyId(), serviceContext);

			serviceContext.setAssetCategoryIds(
				new long[] {assetCategory.getCategoryId()});

			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				serviceContext);

			JournalArticle updateJournalArticle = JournalTestUtil.updateArticle(
				journalArticle, journalArticle.getTitleMap(),
				journalArticle.getContent(), true, false,
				ServiceContextTestUtil.getServiceContext());

			int compare = Double.compare(
				journalArticle.getVersion(), updateJournalArticle.getVersion());

			Assert.assertTrue(compare < 0);

			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT,
				updateJournalArticle.getStatus());

			RelatedInfoItemCollectionProvider<AssetCategory, AssetEntry>
				relatedInfoItemCollectionProvider =
					_infoItemServiceRegistry.getInfoItemService(
						RelatedInfoItemCollectionProvider.class,
						StringBundler.concat(
							"com.liferay.asset.categories.admin.web.internal.",
							"info.collection.provider.",
							"AssetEntriesWithSameAssetCategoryRelatedInfoItem",
							"CollectionProvider"));

			Assert.assertNotNull(relatedInfoItemCollectionProvider);

			CollectionQuery collectionQuery = new CollectionQuery();

			collectionQuery.setRelatedItemObject(assetCategory);

			InfoPage<? extends AssetEntry> relatedItemsInfoPage =
				relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

			Assert.assertNotNull(relatedItemsInfoPage);

			List<? extends AssetEntry> pageItems =
				relatedItemsInfoPage.getPageItems();

			Assert.assertEquals(pageItems.toString(), 1, pageItems.size());

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						JournalArticle.class.getName());

			Assert.assertEquals(
				assetRendererFactory.getAssetEntry(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()),
				pageItems.get(0));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private ServiceContext _getServiceContext() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(LayoutTestUtil.addTypePortletLayout(_group));

		return themeDisplay;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}