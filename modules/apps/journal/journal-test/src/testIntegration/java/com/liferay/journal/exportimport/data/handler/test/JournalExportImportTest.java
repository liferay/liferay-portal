/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.exportimport.kernel.exception.MissingReferenceException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalContent;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.staging.configuration.StagingConfiguration;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Juan Fernández
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class JournalExportImportTest extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Override
	public String getNamespace() {
		return _journalPortletDataHandler.getNamespace();
	}

	@Override
	public String getPortletId() {
		return JournalPortletKeys.JOURNAL;
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testExportImportCompanyScopeStructuredJournalArticle()
		throws Exception {

		exportImportJournalArticle(true);
	}

	@FeatureFlags("LPS-199086")
	@Test
	@TestInfo("LPD-6808: AC15-AC16")
	public void testExportImportJournalArticleWithLayoutURLLayoutDoesNotExistOnImportSide()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			StagingConfiguration.class, CompanyThreadLocal.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishParentLayoutsByDefault", false
			).build());

		long groupId = group.getGroupId();

		Layout parentLayout = LayoutTestUtil.addTypePortletLayout(group);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, parentLayout.getPlid());

		String content = StringUtil.replace(
			_read("journal_article_content.xml"),
			new String[] {"[$GROUP_NAME$]", "[[$LAYOUT_FRIENDLY_URL$]$]"},
			new String[] {
				StringUtil.toLowerCase(group.getName("en_US")),
				StringUtil.toLowerCase(childLayout.getFriendlyURL())
			});

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, groupId,
				_read("data_definition.json"), TestPropsValues.getUser());

		JournalTestUtil.addArticleWithXMLContent(
			groupId, content, dataDefinition.getDataDefinitionKey(), null);

		exportPortlet(JournalPortletKeys.JOURNAL, parentLayout);

		_layoutService.deleteLayout(
			groupId, parentLayout.isPrivateLayout(), parentLayout.getLayoutId(),
			ServiceContextThreadLocal.getServiceContext());

		try {
			importPortlet(JournalPortletKeys.JOURNAL, parentLayout);
		}
		catch (MissingReferenceException missingReferenceException) {
			Assert.assertEquals(
				missingReferenceException.getClass(),
				MissingReferenceException.class);
		}
	}

	@FeatureFlags("LPS-199086")
	@Test
	@TestInfo("LPD-6808: AC15-AC17")
	public void testExportImportJournalArticleWithLayoutURLLayoutExistOnImportSide()
		throws Exception {

		_configurationProvider.saveCompanyConfiguration(
			StagingConfiguration.class, CompanyThreadLocal.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"publishParentLayoutsByDefault", false
			).build());

		long groupId = group.getGroupId();

		Layout parentLayout = LayoutTestUtil.addTypePortletLayout(group);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			group, parentLayout.getPlid());

		String content = StringUtil.replace(
			_read("journal_article_content.xml"),
			new String[] {"[$GROUP_NAME$]", "[[$LAYOUT_FRIENDLY_URL$]$]"},
			new String[] {
				StringUtil.toLowerCase(group.getName("en_US")),
				StringUtil.toLowerCase(childLayout.getFriendlyURL())
			});

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, groupId,
				_read("data_definition.json"), TestPropsValues.getUser());

		JournalArticle article = JournalTestUtil.addArticleWithXMLContent(
			groupId, content, dataDefinition.getDataDefinitionKey(), null);

		exportPortlet(JournalPortletKeys.JOURNAL, parentLayout);

		importPortlet(JournalPortletKeys.JOURNAL, parentLayout);

		Assert.assertEquals(
			1,
			JournalArticleLocalServiceUtil.getArticlesCount(
				importedGroup.getGroupId()));

		JournalArticle groupArticle =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				article.getUuid(), importedGroup.getGroupId());

		Assert.assertNotNull(groupArticle);
		Assert.assertEquals(content, groupArticle.getContent());
	}

	@Test
	public void testExportImportJournalArticleWithoutVersionHistory()
		throws Exception {

		JournalArticle article = (JournalArticle)addStagedModel(
			group.getGroupId());

		article = (JournalArticle)addVersion(article);

		Assert.assertEquals(
			2,
			JournalArticleLocalServiceUtil.getArticlesCount(
				group.getGroupId(), article.getArticleId()));

		Map<String, String[]> exportParameterMap = new HashMap<>();

		addParameter(exportParameterMap, "version-history", false);

		exportImportPortlet(
			JournalPortletKeys.JOURNAL, exportParameterMap,
			new HashMap<String, String[]>());

		JournalArticle importedArticle = (JournalArticle)getStagedModel(
			article.getUuid(), importedGroup.getGroupId());

		Assert.assertEquals(
			1,
			JournalArticleLocalServiceUtil.getArticlesCount(
				importedGroup.getGroupId(), importedArticle.getArticleId()));
	}

	@Test
	public void testExportImportJournalArticleWithRepeatableWebContentField()
		throws Exception {

		DataDefinition dataDefinition = DataDefinition.toDTO(
			_readFileToString(
				"dependencies" +
					"/repeatable_journal_article_field_data_definition.json"));

		dataDefinition.setName(
			HashMapBuilder.<String, Object>put(
				String.valueOf(LocaleUtil.US), "TestDataDef"
			).build());

		DataDefinitionResource.Builder dataDefinitionResourcedBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourcedBuilder.user(
				TestPropsValues.getUser()
			).build();

		dataDefinition =
			dataDefinitionResource.postSiteDataDefinitionByContentType(
				group.getGroupId(), "journal", dataDefinition);

		String xml = _readFileToString(
			"dependencies" +
				"/repeatable_journal_article_field_journal_content.xml");

		JournalArticle referencedArticle1 = JournalTestUtil.addArticle(
			group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		JournalArticle referencedArticle2 = JournalTestUtil.addArticle(
			group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticle article = JournalTestUtil.addArticleWithXMLContent(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			StringUtil.replace(
				xml,
				new String[] {
					"[$JOURNAL_REF_JSON_1$]", "[$JOURNAL_REF_JSON_2$]"
				},
				new String[] {
					_getArticleReferenceJSONObject(
						referencedArticle1
					).toString(),
					_getArticleReferenceJSONObject(
						referencedArticle2
					).toString()
				}),
			dataDefinition.getDataDefinitionKey(), null, LocaleUtil.US);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.journal.internal.dynamic.data.mapping.util." +
					"JournalArticleImportDDMFormFieldValueTransformer",
				LoggerTestUtil.WARN)) {

			exportImportPortlet(JournalPortletKeys.JOURNAL);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			int count = 0;

			for (LogEntry logEntry : logEntries) {
				String message = logEntry.getMessage();

				if (message.startsWith(
						"Unable to get journal article with primary key")) {

					count++;
				}
			}

			Assert.assertTrue("Unexpected log messages: " + count, count <= 1);
		}

		article =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				article.getUuid(), importedGroup.getGroupId());

		Assert.assertNotNull(article);

		referencedArticle1 =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				referencedArticle1.getUuid(), importedGroup.getGroupId());

		Assert.assertNotNull(referencedArticle1);

		referencedArticle2 =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				referencedArticle2.getUuid(), importedGroup.getGroupId());

		Assert.assertNotNull(referencedArticle2);

		String content = article.getContent();

		_assertContains(
			content,
			"\"classPK\":\"" + referencedArticle1.getResourcePrimKey() + "\"");
		_assertContains(
			content,
			"\"classPK\":\"" + referencedArticle2.getResourcePrimKey() + "\"");
	}

	@Test
	public void testExportImportStructuredJournalArticle() throws Exception {
		exportImportJournalArticle(false);
	}

	@Test
	public void testExportImportWithAssetCategory() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory parentAssetCategory =
			_assetCategoryLocalService.addCategory(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(),
				assetVocabulary.getVocabularyId(), serviceContext);

		AssetCategory childAssetCategory =
			_assetCategoryLocalService.addCategory(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				parentAssetCategory.getCategoryId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				null, assetVocabulary.getVocabularyId(), null, serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {parentAssetCategory.getCategoryId()});

		JournalArticle article1 = JournalTestUtil.addArticle(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), LocaleUtil.getSiteDefault(), false,
			false, serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {childAssetCategory.getCategoryId()});

		JournalArticle article2 = JournalTestUtil.addArticle(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), LocaleUtil.getSiteDefault(), false,
			false, serviceContext);

		exportImportPortlet(JournalPortletKeys.JOURNAL);

		Assert.assertEquals(
			2,
			JournalArticleLocalServiceUtil.getArticlesCount(
				importedGroup.getGroupId()));

		_assertAssetCategory(parentAssetCategory, article1.getUuid());
		_assertAssetCategory(childAssetCategory, article2.getUuid());
	}

	@Ignore
	@Test
	public void testExportImportWithComplexStructuredJournalArticle()
		throws Exception {

		DataDefinition dataDefinition = DataDefinition.toDTO(
			_readFileToString("dependencies/complex_data_definition.json"));

		dataDefinition.setName(
			HashMapBuilder.<String, Object>put(
				String.valueOf(LocaleUtil.SPAIN), "TMX_Main_Menu"
			).build());

		DataDefinitionResource.Builder dataDefinitionResourcedBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourcedBuilder.user(
				TestPropsValues.getUser()
			).build();

		dataDefinition =
			dataDefinitionResource.postSiteDataDefinitionByContentType(
				group.getGroupId(), "journal", dataDefinition);

		String xml = _readFileToString(
			"dependencies/complex_journal_article_content.xml");

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null, ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_jsonFactory.looseSerialize(fileEntry));

		JournalArticle article = JournalTestUtil.addArticleWithXMLContent(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			StringUtil.replace(xml, "[$DOCUMENT_JSON$]", jsonObject.toString()),
			dataDefinition.getDataDefinitionKey(), null, LocaleUtil.SPAIN);

		exportImportPortlet(JournalPortletKeys.JOURNAL);

		Assert.assertEquals(
			1,
			JournalArticleLocalServiceUtil.getArticlesCount(
				importedGroup.getGroupId()));

		Assert.assertNotNull(
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				article.getUuid(), importedGroup.getGroupId()));
	}

	@Override
	protected StagedModel addStagedModel(long groupId) throws Exception {
		return JournalTestUtil.addArticle(
			groupId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	@Override
	protected StagedModel addStagedModel(long groupId, Date createdDate)
		throws Exception {

		String title = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setCreateDate(createdDate);
		serviceContext.setLayoutFullURL("http://localhost");
		serviceContext.setModifiedDate(createdDate);

		return JournalTestUtil.addArticle(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, title, title,
			RandomTestUtil.randomString(), LocaleUtil.getSiteDefault(), false,
			false, serviceContext);
	}

	@Override
	protected StagedModel addVersion(StagedModel stagedModel) throws Exception {
		JournalArticle article = (JournalArticle)stagedModel;

		return JournalTestUtil.updateArticle(
			article, RandomTestUtil.randomString());
	}

	@Override
	protected void deleteFirstVersion(StagedModel stagedModel)
		throws Exception {

		JournalArticle article = (JournalArticle)stagedModel;

		List<JournalArticle> articles =
			JournalArticleLocalServiceUtil.getArticles(
				article.getGroupId(), article.getArticleId());

		JournalArticle firstArticle = null;

		for (JournalArticle journalArticle : articles) {
			if ((firstArticle == null) ||
				(journalArticle.getVersion() < firstArticle.getVersion())) {

				firstArticle = journalArticle;
			}
		}

		deleteStagedModel(firstArticle);
	}

	@Override
	protected void deleteLatestVersion(StagedModel stagedModel)
		throws Exception {

		JournalArticle article = (JournalArticle)stagedModel;

		deleteStagedModel(
			JournalArticleLocalServiceUtil.getLatestArticle(
				article.getGroupId(), article.getArticleId()));
	}

	@Override
	protected void deleteStagedModel(StagedModel stagedModel) throws Exception {
		JournalArticleLocalServiceUtil.deleteArticle(
			(JournalArticle)stagedModel);
	}

	protected void exportImportJournalArticle(boolean companyScopeDependencies)
		throws Exception {

		long groupId = group.getGroupId();

		Company company = CompanyLocalServiceUtil.fetchCompany(
			group.getCompanyId());

		Group companyGroup = company.getGroup();

		if (companyScopeDependencies) {
			groupId = companyGroup.getGroupId();
		}

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			groupId, JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			groupId, ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		JournalArticle article = JournalTestUtil.addArticleWithXMLContent(
			group.getGroupId(), content, ddmStructure.getStructureKey(),
			ddmTemplate.getTemplateKey());

		exportImportPortlet(JournalPortletKeys.JOURNAL);

		Assert.assertEquals(
			1,
			JournalArticleLocalServiceUtil.getArticlesCount(
				importedGroup.getGroupId()));

		JournalArticle groupArticle =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				article.getUuid(), importedGroup.getGroupId());

		Assert.assertNotNull(groupArticle);

		groupId = importedGroup.getGroupId();

		if (companyScopeDependencies) {
			DDMStructure importedDDMStructure =
				DDMStructureLocalServiceUtil.fetchDDMStructureByUuidAndGroupId(
					ddmStructure.getUuid(), groupId);

			Assert.assertNull(importedDDMStructure);

			DDMTemplate importedDDMTemplate =
				DDMTemplateLocalServiceUtil.fetchDDMTemplateByUuidAndGroupId(
					ddmTemplate.getUuid(), groupId);

			Assert.assertNull(importedDDMTemplate);

			groupId = companyGroup.getGroupId();
		}

		DDMStructure dependentDDMStructure =
			DDMStructureLocalServiceUtil.fetchDDMStructureByUuidAndGroupId(
				ddmStructure.getUuid(), groupId);

		Assert.assertNotNull(dependentDDMStructure);

		DDMTemplate dependentDDMTemplate =
			DDMTemplateLocalServiceUtil.fetchDDMTemplateByUuidAndGroupId(
				ddmTemplate.getUuid(), groupId);

		Assert.assertNotNull(dependentDDMTemplate);

		Assert.assertEquals(
			groupArticle.getDDMStructureId(),
			dependentDDMStructure.getStructureId());
		Assert.assertEquals(
			article.getDDMTemplateKey(), dependentDDMTemplate.getTemplateKey());
		Assert.assertEquals(
			companyScopeDependencies,
			Objects.equals(
				article.getDDMStructureId(), groupArticle.getDDMStructureId()));
		Assert.assertEquals(
			dependentDDMTemplate.getClassPK(),
			dependentDDMStructure.getStructureId());
	}

	@Override
	protected AssetEntry getAssetEntry(StagedModel stagedModel)
		throws PortalException {

		JournalArticle article = (JournalArticle)stagedModel;

		return AssetEntryLocalServiceUtil.getEntry(
			article.getGroupId(), article.getArticleResourceUuid());
	}

	protected Map<String, String[]> getBaseParameterMap(long groupId, long plid)
		throws Exception {

		Map<String, String[]> parameterMap = HashMapBuilder.put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
			new String[] {Boolean.FALSE.toString()}
		).build();

		addParameter(parameterMap, "doAsGroupId", String.valueOf(groupId));
		addParameter(parameterMap, "feeds", true);
		addParameter(parameterMap, "groupId", String.valueOf(groupId));
		addParameter(
			parameterMap, "permissionsAssignedToRoles",
			Boolean.TRUE.toString());
		addParameter(parameterMap, "plid", String.valueOf(plid));
		addParameter(
			parameterMap, "portletResource", JournalPortletKeys.JOURNAL);
		addParameter(parameterMap, "referenced-content", true);
		addParameter(parameterMap, "structures", true);
		addParameter(parameterMap, "version-history", true);
		addParameter(parameterMap, "web-content", true);

		return parameterMap;
	}

	@Override
	protected Map<String, String[]> getExportParameterMap() throws Exception {
		Map<String, String[]> parameterMap = super.getExportParameterMap();

		MapUtil.merge(
			parameterMap,
			getBaseParameterMap(group.getGroupId(), layout.getPlid()));

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
				JournalPortletKeys.JOURNAL,
			new String[] {Boolean.TRUE.toString()});

		return parameterMap;
	}

	@Override
	protected Map<String, String[]> getImportParameterMap() throws Exception {
		Map<String, String[]> parameterMap = super.getImportParameterMap();

		MapUtil.merge(
			parameterMap,
			getBaseParameterMap(
				importedGroup.getGroupId(), importedLayout.getPlid()));

		parameterMap.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR});
		parameterMap.put(
			PortletDataHandlerKeys.DELETE_PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.USER_ID_STRATEGY,
			new String[] {UserIdStrategy.CURRENT_USER_ID});

		return parameterMap;
	}

	@Override
	protected StagedModel getStagedModel(String uuid, long groupId) {
		return JournalArticleLocalServiceUtil.
			fetchJournalArticleByUuidAndGroupId(uuid, groupId);
	}

	@Override
	protected boolean isVersioningEnabled() {
		return true;
	}

	@Override
	protected void testExportImportDisplayStyle(long groupId, String scopeType)
		throws Exception {
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		DateTestUtil.assertEquals(
			stagedModel.getCreateDate(), importedStagedModel.getCreateDate());

		Assert.assertEquals(
			stagedModel.getUuid(), importedStagedModel.getUuid());

		JournalArticle article = (JournalArticle)stagedModel;
		JournalArticle importedArticle = (JournalArticle)importedStagedModel;

		Assert.assertEquals(
			(Double)article.getVersion(), (Double)importedArticle.getVersion());
		Assert.assertEquals(article.getTitle(), importedArticle.getTitle());
		Assert.assertEquals(
			article.getDescription(), importedArticle.getDescription());

		String content = _journalContent.getContent(
			article.getGroupId(), article.getArticleId(), Constants.VIEW,
			article.getDefaultLanguageId());

		String importedContent = _journalContent.getContent(
			importedArticle.getGroupId(), importedArticle.getArticleId(),
			Constants.VIEW, importedArticle.getDefaultLanguageId());

		Assert.assertEquals(content, importedContent);

		Assert.assertEquals(
			article.isSmallImage(), importedArticle.isSmallImage());
		Assert.assertEquals(
			article.getSmallImageURL(), importedArticle.getSmallImageURL());
		Assert.assertEquals(article.getStatus(), importedArticle.getStatus());

		DateTestUtil.assertEquals(
			article.getDisplayDate(), importedArticle.getDisplayDate());
		DateTestUtil.assertEquals(
			article.getExpirationDate(), importedArticle.getExpirationDate());
		DateTestUtil.assertEquals(
			article.getReviewDate(), importedArticle.getReviewDate());
		DateTestUtil.assertEquals(
			article.getStatusDate(), importedArticle.getStatusDate());

		JournalArticleResource articleResource = article.getArticleResource();
		JournalArticleResource importedArticleArticleResource =
			importedArticle.getArticleResource();

		Assert.assertEquals(
			articleResource.getUuid(),
			importedArticleArticleResource.getUuid());
	}

	@Override
	protected void validateVersions() throws Exception {
		List<JournalArticle> articles =
			JournalArticleLocalServiceUtil.getArticles(group.getGroupId());

		for (JournalArticle article : articles) {
			JournalArticle importedArticle = (JournalArticle)getStagedModel(
				article.getUuid(), importedGroup.getGroupId());

			validateImportedStagedModel(article, importedArticle);
		}
	}

	private void _assertAssetCategory(
		AssetCategory assetCategory, String uuid) {

		JournalArticle importedArticle =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				uuid, importedGroup.getGroupId());

		List<AssetCategory> assetCategories =
			_assetCategoryLocalService.getCategories(
				_portal.getClassNameId(JournalArticle.class.getName()),
				importedArticle.getResourcePrimKey(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertEquals(
			assetCategories.toString(), 1, assetCategories.size());

		AssetCategory importedAssetCategory = assetCategories.get(0);

		Assert.assertEquals(
			assetCategory.getUuid(), importedAssetCategory.getUuid());
		Assert.assertEquals(
			assetCategory.getName(), importedAssetCategory.getName());
	}

	private void _assertContains(String string, String substring) {
		Assert.assertTrue(
			StringBundler.concat(
				"The string \"", string, "\" should contain the substring \"",
				substring, "\""),
			string.contains(substring));
	}

	private JSONObject _getArticleReferenceJSONObject(JournalArticle article)
		throws Exception {

		AssetEntry assetEntry = getAssetEntry(article);

		return JSONUtil.put(
			"assetEntryId", assetEntry.getEntryId()
		).put(
			"className", JournalArticle.class.getName()
		).put(
			"classNameId", PortalUtil.getClassNameId(JournalArticle.class)
		).put(
			"classPK", article.getResourcePrimKey()
		).put(
			"type", "Web Content Article"
		);
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private String _readFileToString(String s) throws Exception {
		return new String(FileUtil.getBytes(getClass(), s));
	}

	@Inject
	private static ConfigurationProvider _configurationProvider;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private JournalContent _journalContent;

	@Inject(filter = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL)
	private PortletDataHandler _journalPortletDataHandler;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutService _layoutService;

	@Inject
	private Portal _portal;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}