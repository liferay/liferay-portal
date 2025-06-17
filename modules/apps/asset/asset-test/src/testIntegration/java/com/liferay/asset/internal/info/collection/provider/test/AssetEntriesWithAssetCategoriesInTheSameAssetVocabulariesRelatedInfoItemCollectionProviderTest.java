/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.info.collection.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.ConfigurableInfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.pagination.InfoPage;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
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
	AssetEntriesWithAssetCategoriesInTheSameAssetVocabulariesRelatedInfoItemCollectionProviderTest {

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
	public void testGetCollectionInfoPageWithDifferentAssetCategorySameAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setRequest(_getHttpServletRequest());

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory relatedAssetCategory = _addAssetCategory(
			_group, serviceContext, assetVocabulary);

		JournalArticle relatedJournalArticle = _addJournalArticle(
			relatedAssetCategory, serviceContext);

		AssetCategory assetCategory = _addAssetCategory(
			_group, serviceContext, assetVocabulary);

		JournalArticle journalArticle = _addJournalArticle(
			assetCategory, serviceContext);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			CollectionQuery collectionQuery = new CollectionQuery();

			collectionQuery.setRelatedItemObject(
				_getAssetEntry(relatedJournalArticle));

			InfoPage<AssetEntry> collectionInfoPage =
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

			List<? extends AssetEntry> pageItems =
				collectionInfoPage.getPageItems();

			Assert.assertEquals(pageItems.toString(), 1, pageItems.size());

			AssetEntry assetEntry = pageItems.get(0);

			Assert.assertEquals(_getAssetEntry(journalArticle), assetEntry);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
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

		JournalArticle journalArticle = _addJournalArticle(
			assetCategory, serviceContext);
		JournalArticle relatedJournalArticle = _addJournalArticle(
			assetCategory, serviceContext);

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setRelatedItemObject(
			_getAssetEntry(relatedJournalArticle));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			InfoPage<AssetEntry> collectionInfoPage =
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

			List<? extends AssetEntry> pageItems =
				collectionInfoPage.getPageItems();

			Assert.assertEquals(pageItems.toString(), 1, pageItems.size());

			AssetEntry assetEntry = pageItems.get(0);

			Assert.assertEquals(_getAssetEntry(journalArticle), assetEntry);
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

		JournalArticle journalArticle = _addJournalArticle(
			assetCategory, serviceContext);
		JournalArticle relatedJournalArticle = _addJournalArticle(
			assetCategory, serviceContext);

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setRelatedItemObject(
			_getAssetEntry(relatedJournalArticle));

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

			InfoPage<AssetEntry> collectionInfoPage =
				_relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

			List<? extends AssetEntry> pageItems =
				collectionInfoPage.getPageItems();

			Assert.assertEquals(pageItems.toString(), 1, pageItems.size());

			AssetEntry assetEntry = pageItems.get(0);

			Assert.assertEquals(_getAssetEntry(journalArticle), assetEntry);
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

			Assert.assertEquals(infoFields.toString(), 1, infoFields.size());

			InfoField<?> infoField = infoFields.get(0);

			Assert.assertTrue(
				infoField.getInfoFieldType() instanceof
					MultiselectInfoFieldType);
			Assert.assertEquals(
				_language.get(LocaleUtil.US, "item-type"),
				infoField.getLabel(LocaleUtil.US));
			Assert.assertEquals("item_types", infoField.getName());
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
			assetCategory, serviceContext);

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setRelatedItemObject(
			_getAssetEntry(relatedJournalArticle));

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

	private JournalArticle _addJournalArticle(
			AssetCategory assetCategory, ServiceContext serviceContext)
		throws Exception {

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		return JournalTestUtil.addArticle(
			serviceContext.getScopeGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);
	}

	private AssetEntry _getAssetEntry(JournalArticle journalArticle)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				JournalArticle.class.getName());

		return assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());
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

		themeDisplay.setLayout(LayoutTestUtil.addTypeContentLayout(_group));
		themeDisplay.setLocale(locale);
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject(
		filter = "component.name=com.liferay.asset.internal.info.collection.provider.AssetEntriesWithAssetCategoriesInTheSameAssetVocabulariesRelatedInfoItemCollectionProvider"
	)
	private RelatedInfoItemCollectionProvider
		_relatedInfoItemCollectionProvider;

}