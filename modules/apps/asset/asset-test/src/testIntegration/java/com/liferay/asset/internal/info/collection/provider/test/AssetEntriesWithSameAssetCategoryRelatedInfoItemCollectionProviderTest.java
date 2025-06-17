/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.info.collection.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.ConfigurableInfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.CategoriesInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.pagination.InfoPage;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
	public void testGetCollectionInfoPageWithSameAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _addAssetCategory(
			_group, serviceContext, assetVocabulary);

		long[] assetCategoryIds = {assetCategory.getCategoryId()};

		JournalArticle journalArticle = _addJournalArticle(
			assetCategoryIds, serviceContext);
		JournalArticle relatedJournalArticle = _addJournalArticle(
			assetCategoryIds, serviceContext);

		_reindex();

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setRelatedItemObject(
			_getAssetEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle.getResourcePrimKey()));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_assertInfoPage(
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery),
				_getAssetEntry(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetCollectionInfoPageWithSameAssetCategoryFilteringByAnyAssetCategoryOfTheSameAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _addAssetCategory(
			_group, serviceContext, assetVocabulary);
		AssetCategory assetCategory2 = _addAssetCategory(
			_group, serviceContext, assetVocabulary);
		AssetCategory assetCategory3 = _addAssetCategory(
			_group, serviceContext, assetVocabulary);

		_addJournalArticle(new long[0], serviceContext);
		_addJournalArticle(
			new long[] {assetCategory1.getCategoryId()}, serviceContext);
		_addJournalArticle(
			new long[] {
				assetCategory2.getCategoryId(), assetCategory3.getCategoryId()
			},
			serviceContext);

		JournalArticle expectedJournalArticle1 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory3.getCategoryId()
			},
			serviceContext);
		JournalArticle expectedJournalArticle2 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			},
			serviceContext);
		JournalArticle expectedJournalArticle3 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId(),
				assetCategory3.getCategoryId()
			},
			serviceContext);
		JournalArticle relatedJournalArticle = _addJournalArticle(
			new long[] {assetCategory1.getCategoryId()}, serviceContext);

		_reindex();

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setConfiguration(
			HashMapBuilder.put(
				"assetCategoryRule",
				new String[] {"anyAssetCategoryOfTheSameAssetVocabulary"}
			).build());
		collectionQuery.setRelatedItemObject(
			_getAssetEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle.getResourcePrimKey()));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_assertInfoPage(
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle1.getResourcePrimKey()),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle2.getResourcePrimKey()),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle3.getResourcePrimKey()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetCollectionInfoPageWithSameAssetCategoryFilteringByAnyAssetCategoryOfTheSameAssetVocabularyMultipleAssetVocabularies()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		AssetVocabulary assetVocabulary1 =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetVocabulary1AssetCategory1 = _addAssetCategory(
			_group, serviceContext, assetVocabulary1);
		AssetCategory assetVocabulary1AssetCategory2 = _addAssetCategory(
			_group, serviceContext, assetVocabulary1);
		AssetCategory assetVocabulary1AssetCategory3 = _addAssetCategory(
			_group, serviceContext, assetVocabulary1);

		AssetVocabulary assetVocabulary2 =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetVocabulary2AssetCategory1 = _addAssetCategory(
			_group, serviceContext, assetVocabulary2);
		AssetCategory assetVocabulary2AssetCategory2 = _addAssetCategory(
			_group, serviceContext, assetVocabulary2);
		AssetCategory assetVocabulary2AssetCategory3 = _addAssetCategory(
			_group, serviceContext, assetVocabulary2);

		_addJournalArticle(new long[0], serviceContext);
		_addJournalArticle(
			new long[] {
				assetVocabulary1AssetCategory1.getCategoryId(),
				assetVocabulary2AssetCategory1.getCategoryId()
			},
			serviceContext);
		_addJournalArticle(
			new long[] {
				assetVocabulary1AssetCategory2.getCategoryId(),
				assetVocabulary2AssetCategory1.getCategoryId()
			},
			serviceContext);
		_addJournalArticle(
			new long[] {
				assetVocabulary1AssetCategory2.getCategoryId(),
				assetVocabulary1AssetCategory3.getCategoryId(),
				assetVocabulary2AssetCategory2.getCategoryId(),
				assetVocabulary2AssetCategory3.getCategoryId()
			},
			serviceContext);

		JournalArticle expectedJournalArticle1 = _addJournalArticle(
			new long[] {
				assetVocabulary1AssetCategory1.getCategoryId(),
				assetVocabulary1AssetCategory2.getCategoryId()
			},
			serviceContext);
		JournalArticle expectedJournalArticle2 = _addJournalArticle(
			new long[] {
				assetVocabulary1AssetCategory1.getCategoryId(),
				assetVocabulary1AssetCategory3.getCategoryId()
			},
			serviceContext);
		JournalArticle expectedJournalArticle3 = _addJournalArticle(
			new long[] {
				assetVocabulary1AssetCategory1.getCategoryId(),
				assetVocabulary2AssetCategory1.getCategoryId(),
				assetVocabulary2AssetCategory3.getCategoryId()
			},
			serviceContext);
		JournalArticle expectedJournalArticle4 = _addJournalArticle(
			new long[] {
				assetVocabulary1AssetCategory1.getCategoryId(),
				assetVocabulary1AssetCategory2.getCategoryId(),
				assetVocabulary2AssetCategory1.getCategoryId(),
				assetVocabulary2AssetCategory3.getCategoryId()
			},
			serviceContext);
		JournalArticle relatedJournalArticle = _addJournalArticle(
			new long[] {
				assetVocabulary1AssetCategory1.getCategoryId(),
				assetVocabulary2AssetCategory1.getCategoryId()
			},
			serviceContext);

		_reindex();

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setConfiguration(
			HashMapBuilder.put(
				"assetCategoryRule",
				new String[] {"anyAssetCategoryOfTheSameAssetVocabulary"}
			).build());
		collectionQuery.setRelatedItemObject(
			_getAssetEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle.getResourcePrimKey()));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_assertInfoPage(
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle1.getResourcePrimKey()),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle2.getResourcePrimKey()),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle3.getResourcePrimKey()),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle4.getResourcePrimKey()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetCollectionInfoPageWithSameAssetCategoryFilteringByItemType()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _addAssetCategory(
			_group, serviceContext, assetVocabulary);

		BlogsEntry blogsEntry = _addBlogsEntry(
			new long[] {assetCategory.getCategoryId()}, serviceContext);

		long[] assetCategoryIds = {assetCategory.getCategoryId()};

		JournalArticle journalArticle = _addJournalArticle(
			assetCategoryIds, serviceContext);
		JournalArticle relatedJournalArticle = _addJournalArticle(
			assetCategoryIds, serviceContext);

		_reindex();

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setRelatedItemObject(
			_getAssetEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle.getResourcePrimKey()));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			InfoPage<AssetEntry> collectionInfoPage =
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

			AssetEntry blogsEntryAssetEntry = _getAssetEntry(
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

			_assertInfoPage(
				collectionInfoPage,
				_getAssetEntry(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()),
				blogsEntryAssetEntry);

			collectionQuery.setConfiguration(
				HashMapBuilder.put(
					"item_types", new String[] {BlogsEntry.class.getName()}
				).build());

			collectionInfoPage =
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

			_assertInfoPage(collectionInfoPage, blogsEntryAssetEntry);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetCollectionInfoPageWithSameAssetCategoryFilteringBySpecificAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _addAssetCategory(
			_group, serviceContext, assetVocabulary);
		AssetCategory assetCategory2 = _addAssetCategory(
			_group, serviceContext, assetVocabulary);
		AssetCategory assetCategory3 = _addAssetCategory(
			_group, serviceContext, assetVocabulary);

		_addJournalArticle(new long[0], serviceContext);
		_addJournalArticle(
			new long[] {assetCategory1.getCategoryId()}, serviceContext);
		_addJournalArticle(
			new long[] {
				assetCategory2.getCategoryId(), assetCategory3.getCategoryId()
			},
			serviceContext);

		JournalArticle expectedJournalArticle1 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			},
			serviceContext);
		JournalArticle expectedJournalArticle2 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId(),
				assetCategory3.getCategoryId()
			},
			serviceContext);
		JournalArticle relatedJournalArticle = _addJournalArticle(
			new long[] {assetCategory1.getCategoryId()}, serviceContext);

		_reindex();

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setConfiguration(
			HashMapBuilder.put(
				"assetCategoryRule", new String[] {"specificAssetCategory"}
			).put(
				"specificAssetCategoryJSONObject",
				new String[] {
					JSONUtil.put(
						"classPK", assetCategory2.getCategoryId()
					).toString()
				}
			).build());
		collectionQuery.setRelatedItemObject(
			_getAssetEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle.getResourcePrimKey()));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			_assertInfoPage(
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle1.getResourcePrimKey()),
				_getAssetEntry(
					JournalArticle.class.getName(),
					expectedJournalArticle2.getResourcePrimKey()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetCollectionInfoPageWithSameAssetCategoryLatestVersionInDraftStatus()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _addAssetCategory(
			_group, serviceContext, assetVocabulary);

		long[] assetCategoryIds = {assetCategory.getCategoryId()};

		JournalArticle journalArticle = _addJournalArticle(
			assetCategoryIds, serviceContext);
		JournalArticle relatedJournalArticle = _addJournalArticle(
			assetCategoryIds, serviceContext);

		_reindex();

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setRelatedItemObject(
			_getAssetEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle.getResourcePrimKey()));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
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

			_assertInfoPage(
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery),
				_getAssetEntry(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetConfigurationInfoForm() throws Exception {
		ConfigurableInfoCollectionProvider configurableInfoCollectionProvider =
			(ConfigurableInfoCollectionProvider)
				_relatedInfoItemCollectionProvider;

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			InfoForm configurationInfoForm =
				configurableInfoCollectionProvider.getConfigurationInfoForm();

			Assert.assertNotNull(configurationInfoForm);

			List<InfoField<?>> infoFields =
				configurationInfoForm.getAllInfoFields();

			Assert.assertEquals(infoFields.toString(), 3, infoFields.size());

			_assertInfoField(
				infoFields.get(0), MultiselectInfoFieldType.class,
				_language.get(LocaleUtil.US, "item-type"), "item_types");
			_assertInfoField(
				infoFields.get(1), SelectInfoFieldType.class,
				_language.get(LocaleUtil.US, "and-contains"),
				"assetCategoryRule");
			_assertInfoField(
				infoFields.get(2), CategoriesInfoFieldType.class,
				_language.get(LocaleUtil.US, "category"),
				"specificAssetCategoryJSONObject");
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetEmptyCollectionInfoPage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _addAssetCategory(
			_group, serviceContext, assetVocabulary);

		JournalArticle relatedJournalArticle = _addJournalArticle(
			new long[] {assetCategory.getCategoryId()}, serviceContext);

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setRelatedItemObject(
			_getAssetEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle.getResourcePrimKey()));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			InfoPage<AssetEntry> collectionInfoPage =
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

			List<? extends AssetEntry> pageItems =
				collectionInfoPage.getPageItems();

			Assert.assertEquals(pageItems.toString(), 0, pageItems.size());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private AssetCategory _addAssetCategory(
			Group group, ServiceContext serviceContext,
			AssetVocabulary assetVocabulary)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
	}

	private BlogsEntry _addBlogsEntry(
			long[] assetCategoryIds, ServiceContext serviceContext)
		throws Exception {

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		return _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);
	}

	private JournalArticle _addJournalArticle(
			long[] assetCategoryIds, ServiceContext serviceContext)
		throws Exception {

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		return JournalTestUtil.addArticle(
			serviceContext.getScopeGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);
	}

	private void _assertInfoField(
		InfoField<?> infoField, Class<?> infoFieldTypeClass, String label,
		String name) {

		Assert.assertTrue(
			infoFieldTypeClass.isInstance(infoField.getInfoFieldType()));
		Assert.assertEquals(label, infoField.getLabel(LocaleUtil.US));
		Assert.assertEquals(name, infoField.getName());
	}

	private void _assertInfoPage(
		InfoPage<AssetEntry> infoPage, AssetEntry... expectedAssetEntries) {

		List<? extends AssetEntry> pageItems = infoPage.getPageItems();

		Assert.assertEquals(
			pageItems.toString(), expectedAssetEntries.length,
			pageItems.size());

		for (AssetEntry expectedAssetEntry : expectedAssetEntries) {
			boolean found = false;

			for (AssetEntry assetEntry : pageItems) {
				if (!Objects.equals(assetEntry, expectedAssetEntry)) {
					continue;
				}

				found = true;

				break;
			}

			Assert.assertTrue(found);
		}
	}

	private AssetEntry _getAssetEntry(String className, long classPK)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		return assetRendererFactory.getAssetEntry(className, classPK);
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		Locale locale = LocaleUtil.getSiteDefault();

		themeDisplay.setLanguageId(LocaleUtil.toLanguageId(locale));
		themeDisplay.setLocale(locale);

		themeDisplay.setLayout(LayoutTestUtil.addTypeContentLayout(_group));
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private void _reindex() throws Exception {
		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			AssetEntry.class.getName());

		indexer.reindex(new String[] {String.valueOf(_group.getCompanyId())});
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject(
		filter = "component.name=com.liferay.asset.internal.info.collection.provider.AssetEntriesWithSameAssetCategoryRelatedInfoItemCollectionProvider"
	)
	private RelatedInfoItemCollectionProvider
		_relatedInfoItemCollectionProvider;

}