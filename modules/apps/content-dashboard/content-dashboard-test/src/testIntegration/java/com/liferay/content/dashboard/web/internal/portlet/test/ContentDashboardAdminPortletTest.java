/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.constants.LanguageConstants;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletPreferences;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
@Sync
public class ContentDashboardAdminPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.fetchCompany(_group.getCompanyId());
	}

	@Test
	public void testGetAuditGraphTitle() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary audienceAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(),
			audienceAssetVocabulary.getVocabularyId(), serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "stage");

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(),
				childAssetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory.getCategoryId()
					}));

			Assert.assertEquals(
				String.format(
					"Content per %s and %s",
					audienceAssetVocabulary.getTitle(LocaleUtil.US),
					childAssetVocabulary.getTitle(LocaleUtil.US)),
				_getAuditGraphTitle(
					_getMockLiferayPortletRenderRequest(
						audienceAssetVocabulary, childAssetVocabulary)));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(assetCategory);
			_assetCategoryLocalService.deleteCategory(childAssetCategory);
		}
	}

	@Test
	public void testGetAuditGraphTitleWithMissingChildAssetVocabularies()
		throws Exception {

		JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		Assert.assertEquals(
			"Content",
			_getAuditGraphTitle(_getMockLiferayPortletRenderRequest()));
	}

	@Test
	public void testGetAuditGraphTitleWithMissingChildAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary audienceAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(),
			audienceAssetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory.getCategoryId()}));

			Assert.assertEquals(
				String.format(
					"Content per %s",
					audienceAssetVocabulary.getTitle(LocaleUtil.US)),
				_getAuditGraphTitle(_getMockLiferayPortletRenderRequest()));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(assetCategory);
		}
	}

	@Test
	public void testGetContextWithLtrLanguageDirection() throws Exception {
		Map<String, Object> data = _getData(
			_getMockLiferayPortletRenderRequest());

		Map<String, Object> context = (Map<String, Object>)data.get("context");

		Assert.assertNotNull(context);
		Assert.assertEquals(
			LanguageConstants.VALUE_LTR, context.get("languageDirection"));
	}

	@Test
	public void testGetContextWithRtlLanguageDirection() throws Exception {
		Map<String, Object> data = _getData(
			_getMockLiferayPortletRenderRequest(
				LocaleUtil.fromLanguageId("ar_SA")));

		Map<String, Object> context = (Map<String, Object>)data.get("context");

		Assert.assertNotNull(context);
		Assert.assertEquals(
			LanguageConstants.VALUE_RTL, context.get("languageDirection"));
	}

	@Test
	public void testGetSearchContainer() throws Exception {
		User user = UserTestUtil.addGroupAdminUser(_group);

		Group group = GroupTestUtil.addGroup();

		try {
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			SearchContainer<Object> searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			int initialCount = searchContainer.getTotal();

			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);
			JournalTestUtil.addArticle(
				user.getUserId(), _group.getGroupId(), 0);
			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), group.getGroupId(), 0);

			searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			int actualCount = searchContainer.getTotal();

			Assert.assertEquals(initialCount + 3, actualCount);
		}
		finally {
			GroupTestUtil.deleteGroup(group);
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetSearchContainerWithAssetCategory() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "topic");

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory.getCategoryId()}));

			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.setParameter(
				"assetCategoryId",
				String.valueOf(assetCategory.getCategoryId()));
			mockLiferayPortletRenderRequest.setParameter(
				"scopeId", String.valueOf(_group.getGroupId()));

			SearchContainer<Object> searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			Assert.assertEquals(1, searchContainer.getTotal());

			List<Object> results = searchContainer.getResults();

			Assert.assertEquals(
				journalArticle.getTitle(LocaleUtil.US),
				ReflectionTestUtil.invoke(
					results.get(0), "getTitle", new Class<?>[] {Locale.class},
					LocaleUtil.US));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(
				assetCategory.getCategoryId());
		}
	}

	@Test
	public void testGetSearchContainerWithAssetTag() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId(),
				new String[] {"tag1"}));

		JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId(),
				new String[] {"tag2"}));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter("assetTagId", "tag1");
		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			journalArticle1.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithAuthor() throws Exception {
		User user = UserTestUtil.addGroupAdminUser(_group);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				user.getUserId(), _group.getGroupId(), 0);

			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.setParameter(
				"authorIds", String.valueOf(user.getUserId()));
			mockLiferayPortletRenderRequest.setParameter(
				"scopeId", String.valueOf(_group.getGroupId()));

			SearchContainer<Object> searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			Assert.assertEquals(1, searchContainer.getTotal());

			List<Object> results = searchContainer.getResults();

			Assert.assertEquals(
				journalArticle.getTitle(LocaleUtil.US),
				ReflectionTestUtil.invoke(
					results.get(0), "getTitle", new Class<?>[] {Locale.class},
					LocaleUtil.US));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetSearchContainerWithContentDashboardItemSubtype()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(), 0,
			DDMStructureTestUtil.getSampleDDMForm(),
			LocaleUtil.getSiteDefault(),
			ServiceContextTestUtil.getServiceContext());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM,
			JournalTestUtil.getSampleTemplateVM(), LocaleUtil.getSiteDefault());

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0,
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureKey(), ddmTemplate.getTemplateKey(),
				LocaleUtil.getSiteDefault(), null,
				ServiceContextTestUtil.getServiceContext(
					_company.getCompanyId(), _group.getGroupId(),
					TestPropsValues.getUserId()));

		JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"contentDashboardItemSubtypePayload",
			JSONUtil.put(
				"className", DDMStructure.class.getName()
			).put(
				"classPK", ddmStructure.getStructureId()
			).toString());
		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithDefaultOrder() throws Exception {
		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletRenderRequest());

		int initialCount = searchContainer.getTotal();

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		journalArticle2.setModifiedDate(
			DateUtil.newDate(System.currentTimeMillis() + Time.SECOND));

		journalArticle2 = _journalArticleLocalService.updateJournalArticle(
			journalArticle2);

		searchContainer = _getSearchContainer(
			_getMockLiferayPortletRenderRequest());

		Assert.assertEquals(initialCount + 2, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			journalArticle2.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
		Assert.assertEquals(
			journalArticle1.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(1), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithDefaultOrderForTitle()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.addParameter(
			SearchContainer.DEFAULT_ORDER_BY_COL_PARAM, "title");
		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, "0title1",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.getSiteDefault(), false, false, serviceContext);

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, "0title2",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.getSiteDefault(), false, false, serviceContext);

		SearchContainer<Object> actualSearchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(
			searchContainer.getTotal() + 2, actualSearchContainer.getTotal());

		List<Object> results = actualSearchContainer.getResults();

		Assert.assertEquals(
			journalArticle1.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
		Assert.assertEquals(
			journalArticle2.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(1), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithExpiredJournalArticle()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		int initialCount = searchContainer.getTotal();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticle updateJournalArticle = JournalTestUtil.updateArticle(
			journalArticle, journalArticle.getTitleMap(),
			journalArticle.getContent(), true, true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		JournalTestUtil.expireArticle(
			_group.getGroupId(), updateJournalArticle,
			updateJournalArticle.getVersion());

		searchContainer = _getSearchContainer(mockLiferayPortletRenderRequest);

		Assert.assertEquals(initialCount + 1, searchContainer.getTotal());
	}

	@Test
	public void testGetSearchContainerWithFileExtension() throws Exception {
		JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId(),
				new String[] {"tag2"}));

		FileEntry gifFileEntry = _addFileEntry("gif");
		FileEntry jpgFileEntry = _addFileEntry("jpg");

		_addFileEntry("png");

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"fileExtension", new String[] {"jpg", "gif"});
		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(2, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			jpgFileEntry.getFileName(),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
		Assert.assertEquals(
			gifFileEntry.getFileName(),
			ReflectionTestUtil.invoke(
				results.get(1), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithInternalAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory.getCategoryId()}));

			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.setParameter(
				"assetCategoryId",
				String.valueOf(assetCategory.getCategoryId()));
			mockLiferayPortletRenderRequest.setParameter(
				"scopeId", String.valueOf(_group.getGroupId()));

			SearchContainer<Object> searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			Assert.assertEquals(1, searchContainer.getTotal());

			List<Object> results = searchContainer.getResults();

			Assert.assertEquals(
				journalArticle.getTitle(LocaleUtil.US),
				ReflectionTestUtil.invoke(
					results.get(0), "getTitle", new Class<?>[] {Locale.class},
					LocaleUtil.US));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(
				assetCategory.getCategoryId());
		}
	}

	@Test
	public void testGetSearchContainerWithKeywords() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"keywords", journalArticle.getTitle(LocaleUtil.getDefault()));
		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithMultipleAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "topic");

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory1.getCategoryId()}));

			JournalArticle journalArticle2 = JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory1.getCategoryId(),
						assetCategory2.getCategoryId()
					}));

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.addParameter(
				"assetCategoryId",
				String.valueOf(assetCategory1.getCategoryId()));
			mockLiferayPortletRenderRequest.addParameter(
				"assetCategoryId",
				String.valueOf(assetCategory2.getCategoryId()));
			mockLiferayPortletRenderRequest.setParameter(
				"scopeId", String.valueOf(_group.getGroupId()));

			SearchContainer<Object> searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			Assert.assertEquals(1, searchContainer.getTotal());

			List<Object> results = searchContainer.getResults();

			Assert.assertEquals(
				journalArticle2.getTitle(LocaleUtil.US),
				ReflectionTestUtil.invoke(
					results.get(0), "getTitle", new Class<?>[] {Locale.class},
					LocaleUtil.US));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(
				assetCategory1.getCategoryId());
			_assetCategoryLocalService.deleteCategory(
				assetCategory2.getCategoryId());
		}
	}

	@Test
	public void testGetSearchContainerWithMultipleAssetTags() throws Exception {
		JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId(),
				new String[] {"tag1"}));

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId(),
				new String[] {"tag1", "tag2"}));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.addParameter("assetTagId", "tag1");
		mockLiferayPortletRenderRequest.addParameter("assetTagId", "tag2");
		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			journalArticle2.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithMultipleAuthors() throws Exception {
		User user = UserTestUtil.addGroupAdminUser(_group);

		try {
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.setParameter(
				"authorIds",
				new String[] {
					String.valueOf(user.getUserId()),
					String.valueOf(TestPropsValues.getUserId())
				});
			mockLiferayPortletRenderRequest.setParameter(
				"scopeId", String.valueOf(_group.getGroupId()));

			SearchContainer<Object> searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			int initialCount = searchContainer.getTotal();

			JournalArticle journalArticle1 = JournalTestUtil.addArticle(
				user.getUserId(), _group.getGroupId(), 0);
			JournalArticle journalArticle2 = JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);

			searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			Assert.assertEquals(initialCount + 2, searchContainer.getTotal());

			List<Object> results = searchContainer.getResults();

			_assertContainsTitle(
				results, journalArticle1.getTitle(LocaleUtil.US));
			_assertContainsTitle(
				results, journalArticle2.getTitle(LocaleUtil.US));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testGetSearchContainerWithMultipleContentDashboardItemSubtype()
		throws Exception {

		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(), 0,
			DDMStructureTestUtil.getSampleDDMForm(),
			LocaleUtil.getSiteDefault(),
			ServiceContextTestUtil.getServiceContext());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure1.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM,
			JournalTestUtil.getSampleTemplateVM(), LocaleUtil.getSiteDefault());

		JournalArticle journalArticle1 =
			JournalTestUtil.addArticleWithXMLContent(
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0,
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure1.getStructureKey(), ddmTemplate.getTemplateKey(),
				LocaleUtil.getSiteDefault(), null,
				ServiceContextTestUtil.getServiceContext(
					_company.getCompanyId(), _group.getGroupId(),
					TestPropsValues.getUserId()));

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		DDMStructure ddmStructure2 = journalArticle2.getDDMStructure();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"contentDashboardItemSubtypePayload",
			new String[] {
				JSONUtil.put(
					"className", DDMStructure.class.getName()
				).put(
					"classPK", ddmStructure1.getStructureId()
				).toString(),
				JSONUtil.put(
					"className", DDMStructure.class.getName()
				).put(
					"classPK", ddmStructure2.getStructureId()
				).toString()
			});
		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(2, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		_assertContainsTitle(results, journalArticle1.getTitle(LocaleUtil.US));
		_assertContainsTitle(results, journalArticle2.getTitle(LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithMultipleInternalAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory1.getCategoryId()}));

			JournalArticle journalArticle2 = JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory1.getCategoryId(),
						assetCategory2.getCategoryId()
					}));

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.addParameter(
				"assetCategoryId",
				String.valueOf(assetCategory1.getCategoryId()));
			mockLiferayPortletRenderRequest.addParameter(
				"assetCategoryId",
				String.valueOf(assetCategory2.getCategoryId()));
			mockLiferayPortletRenderRequest.setParameter(
				"scopeId", String.valueOf(_group.getGroupId()));

			SearchContainer<Object> searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			Assert.assertEquals(1, searchContainer.getTotal());

			List<Object> results = searchContainer.getResults();

			Assert.assertEquals(
				journalArticle2.getTitle(LocaleUtil.US),
				ReflectionTestUtil.invoke(
					results.get(0), "getTitle", new Class<?>[] {Locale.class},
					LocaleUtil.US));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(
				assetCategory1.getCategoryId());
			_assetCategoryLocalService.deleteCategory(
				assetCategory2.getCategoryId());
		}
	}

	@Test
	public void testGetSearchContainerWithoutGoogleDriveShortcut()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		int initialCount = searchContainer.getTotal();

		JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		FileEntry gifFileEntry = _addFileEntry("gif");
		FileEntry jpgFileEntry = _addFileEntry("jpg");

		FileEntry futureGoogleDriveShortCutFileEntry = _addFileEntry("pdf");

		Object futureGoogleDriveShortModel =
			futureGoogleDriveShortCutFileEntry.getModel();

		DLFileEntry googleDriveShortcutFileEntry =
			(DLFileEntry)futureGoogleDriveShortModel;

		DLFileEntryType googleDocsDLFileEntryType =
			DLFileEntryTypeLocalServiceUtil.getFileEntryType(
				_company.getGroupId(), "GOOGLE_DOCS");

		googleDriveShortcutFileEntry.setFileEntryTypeId(
			googleDocsDLFileEntryType.getFileEntryTypeId());

		DLFileEntryLocalServiceUtil.updateDLFileEntry(
			googleDriveShortcutFileEntry);

		searchContainer = _getSearchContainer(mockLiferayPortletRenderRequest);

		int actualCount = searchContainer.getTotal();

		Assert.assertEquals(initialCount + 3, actualCount);

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			jpgFileEntry.getFileName(),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
		Assert.assertEquals(
			gifFileEntry.getFileName(),
			ReflectionTestUtil.invoke(
				results.get(1), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithPagination() throws Exception {
		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletRenderRequest());

		int initialCount = searchContainer.getTotal();

		for (int i = 0; i <= SearchContainer.DEFAULT_DELTA; i++) {
			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);
		}

		searchContainer = _getSearchContainer(
			_getMockLiferayPortletRenderRequest());

		Assert.assertEquals(
			initialCount + SearchContainer.DEFAULT_DELTA + 1,
			searchContainer.getTotal());

		List<Object> objects = searchContainer.getResults();

		Assert.assertEquals(
			objects.toString(), SearchContainer.DEFAULT_DELTA, objects.size());
	}

	@Test
	public void testGetSearchContainerWithScope() throws Exception {
		Group group = GroupTestUtil.addGroup();

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), group.getGroupId(), 0);

			JournalTestUtil.addArticle(
				TestPropsValues.getUserId(), _group.getGroupId(), 0);

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.setParameter(
				"scopeId", String.valueOf(group.getGroupId()));

			SearchContainer<Object> searchContainer = _getSearchContainer(
				mockLiferayPortletRenderRequest);

			Assert.assertEquals(1, searchContainer.getTotal());

			List<Object> results = searchContainer.getResults();

			Assert.assertEquals(
				journalArticle.getTitle(LocaleUtil.US),
				ReflectionTestUtil.invoke(
					results.get(0), "getTitle", new Class<?>[] {Locale.class},
					LocaleUtil.US));
		}
		finally {
			GroupTestUtil.deleteGroup(group);
		}
	}

	@Test
	public void testGetSearchContainerWithStatusAny() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _group.getGroupId(),
				TestPropsValues.getUserId());

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletRenderRequest.setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_ANY));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		int initialCount = searchContainer.getTotal();

		JournalArticle journalArticle1 = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), true, serviceContext);
		JournalArticle journalArticle2 = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, serviceContext);

		searchContainer = _getSearchContainer(mockLiferayPortletRenderRequest);

		Assert.assertEquals(initialCount + 2, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		_assertContainsTitle(results, journalArticle1.getTitle(LocaleUtil.US));
		_assertContainsTitle(results, journalArticle2.getTitle(LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithStatusApproved() throws Exception {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletRenderRequest.setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_APPROVED));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		int initialCount = searchContainer.getTotal();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _group.getGroupId(),
				TestPropsValues.getUserId());

		JournalArticle journalArticle = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), true, serviceContext);

		JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, serviceContext);

		searchContainer = _getSearchContainer(mockLiferayPortletRenderRequest);

		Assert.assertEquals(initialCount + 1, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithStatusDraft() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _group.getGroupId(),
				TestPropsValues.getUserId());

		JournalArticle journalArticle = JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, serviceContext);

		JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), true, serviceContext);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletRenderRequest.setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_DRAFT));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testGetSearchContainerWithStatusDraftAndAssetCategory()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		SearchContainer<Object> searchContainer = _getSearchContainer(
			_getMockLiferayPortletRenderRequest());

		int initialCount = searchContainer.getTotal();

		AssetVocabulary audienceAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(),
			audienceAssetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
				true, RandomTestUtil.randomLocaleStringMap(LocaleUtil.US),
				RandomTestUtil.randomLocaleStringMap(LocaleUtil.US),
				RandomTestUtil.randomLocaleStringMap(LocaleUtil.US), null,
				LocaleUtil.US, null, false, true, serviceContext);

			serviceContext = ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _group.getGroupId(),
				TestPropsValues.getUserId());

			serviceContext.setAssetCategoryIds(
				new long[] {assetCategory.getCategoryId()});

			JournalTestUtil.updateArticle(
				journalArticle, RandomTestUtil.randomString(),
				journalArticle.getContent(), false, false, serviceContext);

			searchContainer = _getSearchContainer(
				_getMockLiferayPortletRenderRequest());

			Assert.assertEquals(initialCount + 1, searchContainer.getTotal());

			List<Object> results = searchContainer.getResults();

			List<AssetCategory> assetCategories = ReflectionTestUtil.invoke(
				results.get(0), "getAssetCategories", new Class<?>[0]);

			Assert.assertEquals(
				String.valueOf(assetCategories), 1, assetCategories.size());
			Assert.assertEquals(assetCategory, assetCategories.get(0));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(
				assetCategory.getCategoryId());
		}
	}

	@Test
	public void testGetSearchContainerWithStatusDraftAndHasApprovedVersion()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletRenderRequest.setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_APPROVED));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		int initialCount = searchContainer.getTotal();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString(),
			journalArticle.getContent(), true, false,
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _group.getGroupId(),
				TestPropsValues.getUserId()));

		searchContainer = _getSearchContainer(mockLiferayPortletRenderRequest);

		Assert.assertEquals(initialCount + 1, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		List<Object> versions = ReflectionTestUtil.invoke(
			results.get(0), "getLatestContentDashboardItemVersions",
			new Class<?>[] {Locale.class}, LocaleUtil.US);

		Assert.assertEquals(versions.toString(), 2, versions.size());
		Assert.assertEquals(
			"Approved",
			ReflectionTestUtil.invoke(
				versions.get(0), "getLabel", new Class<?>[0]));
		Assert.assertEquals(
			"Draft",
			ReflectionTestUtil.invoke(
				versions.get(1), "getLabel", new Class<?>[0]));
	}

	@Test
	public void testGetSearchContainerWithStatusScheduled() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _group.getGroupId(),
				TestPropsValues.getUserId());

		LocalDateTime localDateTime = LocalDateTime.now();

		localDateTime = localDateTime.plusDays(1);

		ZonedDateTime zonedDateTime = localDateTime.atZone(
			ZoneId.systemDefault());

		Date displayDate = Date.from(zonedDateTime.toInstant());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), null,
			LocaleUtil.getSiteDefault(), displayDate, null, true, true,
			serviceContext);

		JournalTestUtil.addArticleWithWorkflow(
			_group.getGroupId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), true, serviceContext);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setParameter(
			"scopeId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletRenderRequest.setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_SCHEDULED));

		SearchContainer<Object> searchContainer = _getSearchContainer(
			mockLiferayPortletRenderRequest);

		Assert.assertEquals(1, searchContainer.getTotal());

		List<Object> results = searchContainer.getResults();

		Assert.assertEquals(
			journalArticle.getTitle(LocaleUtil.US),
			ReflectionTestUtil.invoke(
				results.get(0), "getTitle", new Class<?>[] {Locale.class},
				LocaleUtil.US));
	}

	@Test
	public void testIsSwapConfigurationEnabled() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "stage");

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(),
				childAssetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory.getCategoryId()
					}));

			Assert.assertTrue(
				_isSwapConfigurationEnabled(
					String.valueOf(assetVocabulary.getVocabularyId()),
					String.valueOf(childAssetVocabulary.getVocabularyId())));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(assetCategory);
			_assetCategoryLocalService.deleteCategory(childAssetCategory);
		}
	}

	@Test
	public void testIsSwapConfigurationEnabledWithMissingChildAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary audienceAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetVocabulary stageAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "stage");

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(),
			audienceAssetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {assetCategory.getCategoryId()}));

			Assert.assertFalse(
				_isSwapConfigurationEnabled(
					String.valueOf(audienceAssetVocabulary.getVocabularyId()),
					String.valueOf(stageAssetVocabulary.getVocabularyId())));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(assetCategory);
		}
	}

	@Test
	public void testIsSwapConfigurationEnabledWithOneAssetVocabularyName()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		AssetVocabulary childAssetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "stage");

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), _company.getGroupId(),
				RandomTestUtil.randomString(),
				childAssetVocabulary.getVocabularyId(), serviceContext);

		try {
			JournalTestUtil.addArticle(
				_group.getGroupId(), 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId(),
					new long[] {
						assetCategory.getCategoryId(),
						childAssetCategory.getCategoryId()
					}));

			Assert.assertFalse(
				_isSwapConfigurationEnabled(
					String.valueOf(assetVocabulary.getVocabularyId())));
		}
		finally {
			_assetCategoryLocalService.deleteCategory(assetCategory);
			_assetCategoryLocalService.deleteCategory(childAssetCategory);
		}
	}

	private FileEntry _addFileEntry(String fileExtension) throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + "." + fileExtension,
			MimeTypesUtil.getExtensionContentType(fileExtension), new byte[0],
			null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertContainsTitle(List<Object> results, String title) {
		boolean containsTitle = false;

		for (Object result : results) {
			if (Objects.equals(
					title,
					ReflectionTestUtil.invoke(
						result, "getTitle", new Class<?>[] {Locale.class},
						LocaleUtil.US))) {

				containsTitle = true;

				break;
			}
		}

		Assert.assertTrue(containsTitle);
	}

	private String _getAuditGraphTitle(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminDisplayContext"),
			"getAuditGraphTitle", new Class<?>[0]);
	}

	private Map<String, Object> _getData(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminDisplayContext"),
			"getData", new Class<?>[0]);
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		return _getMockLiferayPortletRenderRequest(LocaleUtil.US);
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			AssetVocabulary assetVocabulary1, AssetVocabulary assetVocabulary2)
		throws Exception {

		return _getMockLiferayPortletRenderRequest(
			new String[] {
				String.valueOf(assetVocabulary1.getVocabularyId()),
				String.valueOf(assetVocabulary2.getVocabularyId())
			},
			LocaleUtil.US);
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			Locale locale)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary1 =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "audience");

		AssetVocabulary assetVocabulary2 =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				serviceContext.getScopeGroupId(), "stage");

		return _getMockLiferayPortletRenderRequest(
			new String[] {
				String.valueOf(assetVocabulary1.getVocabularyId()),
				String.valueOf(assetVocabulary2.getVocabularyId())
			},
			locale);
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			String[] assetVocabularyIds, Locale locale)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());
		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(), "-",
				WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		String path = "/view.jsp";

		mockLiferayPortletRenderRequest.setParameter("mvcPath", path);
		mockLiferayPortletRenderRequest.setAttribute(
			MVCRenderConstants.
				PORTLET_CONTEXT_OVERRIDE_REQUEST_ATTIBUTE_NAME_PREFIX + path,
			new MockLiferayPortletContext(path));

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.PORTLET_ID,
			"com_liferay_content_dashboard_web_portlet_" +
				"ContentDashboardAdminPortlet");
		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(locale));

		PortletPreferences portletPreferences =
			mockLiferayPortletRenderRequest.getPreferences();

		portletPreferences.setValues("assetVocabularyIds", assetVocabularyIds);

		return mockLiferayPortletRenderRequest;
	}

	private SearchContainer<Object> _getSearchContainer(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminDisplayContext"),
			"getSearchContainer", new Class<?>[0]);
	}

	private ThemeDisplay _getThemeDisplay(Locale locale) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLanguageId(_language.getLanguageId(locale));

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CONTROL_PANEL);

		themeDisplay.setLayout(
			_layoutLocalService.fetchDefaultLayout(group.getGroupId(), true));

		themeDisplay.setLocale(locale);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Boolean _isSwapConfigurationEnabled(String... assetVocabularyIds)
		throws Exception {

		MVCPortlet mvcPortlet = (MVCPortlet)_portlet;

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				assetVocabularyIds, LocaleUtil.US);

		mvcPortlet.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.content.dashboard.web.internal.display.context." +
					"ContentDashboardAdminDisplayContext"),
			"isSwapConfigurationEnabled", new Class<?>[0]);
	}

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Company _company;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.dashboard.web.internal.portlet.ContentDashboardAdminPortlet"
	)
	private Portlet _portlet;

	@Inject
	private UserLocalService _userLocalService;

}