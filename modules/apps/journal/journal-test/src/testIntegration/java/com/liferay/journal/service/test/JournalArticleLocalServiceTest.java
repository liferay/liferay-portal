/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.friendly.url.exception.FriendlyURLLocalizationUrlTitleException;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.exception.ArticleFriendlyURLException;
import com.liferay.journal.exception.DuplicateArticleExternalReferenceCodeException;
import com.liferay.journal.exception.DuplicateArticleIdException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchImageException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Michael C. Han
 */
@RunWith(Arquillian.class)
public class JournalArticleLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);
		_themeDisplay = _getThemeDisplay();
	}

	@Test(expected = AssetCategoryException.class)
	public void testAddArticleWithAssetCategoriesFromNonmultiValuedAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			_getAssetVocabularySettingsHelper(
				false, new long[] {AssetCategoryConstants.ALL_CLASS_NAME_ID},
				new long[] {AssetCategoryConstants.ALL_CLASS_TYPE_PK},
				new boolean[] {false}, new boolean[] {false});

		Assert.assertFalse(assetVocabularySettingsHelper.isMultiValued());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, assetVocabularySettingsHelper.toString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), LocaleUtil.getSiteDefault(), false,
			false, serviceContext);
	}

	@Test
	public void testAddArticleWithEmptyDefaultLanguageIdFriendlyURLWithAnotherLanguageIdFriendlyURL()
		throws Exception {

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		Locale defaultLocale = _portal.getSiteDefaultLocale(
			_group.getGroupId());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		JournalArticle journalArticle = _journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
			PortalUtil.getClassNameId(JournalArticle.class), StringPool.BLANK,
			true, 0,
			HashMapBuilder.put(
				defaultLocale, "title"
			).build(),
			Collections.emptyMap(),
			HashMapBuilder.put(
				defaultLocale, ""
			).put(
				LocaleUtil.SPAIN, "friendly-url-es"
			).build(),
			content, ddmStructure.getStructureId(), null, null, 1, 1, 1965, 0,
			0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true, false, 0, 0,
			null, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Map<Locale, String> friendlyURLMap = journalArticle.getFriendlyURLMap();

		Assert.assertNotNull(friendlyURLMap.get(defaultLocale));
	}

	@Test(expected = DuplicateArticleExternalReferenceCodeException.class)
	public void testAddArticleWithExistingExternalReferenceCode()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalTestUtil.addArticle(
			journalArticle.getExternalReferenceCode(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			journalArticle.getArticleId(), true);
	}

	@Test
	public void testAddArticleWithoutFriendlyURLWithTitleWithTrailingSlashes()
		throws Exception {

		Locale defaultLocale = _portal.getSiteDefaultLocale(
			_group.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			null, _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true,
			HashMapBuilder.put(
				defaultLocale, "test///"
			).build(),
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			RandomTestUtil.randomLocaleStringMap(), null, defaultLocale, null,
			null, true, true, serviceContext);

		Map<Locale, String> friendlyURLMap = journalArticle.getFriendlyURLMap();

		Assert.assertFalse(friendlyURLMap.isEmpty());

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			Assert.assertEquals("test", entry.getValue());
		}
	}

	@Test(
		expected = FriendlyURLLocalizationUrlTitleException.MustNotHaveTrailingSlash.class
	)
	public void testAddArticleWithURLWithStartingSlashThrowsMustNotHaveTrailingSlashException()
		throws Exception {

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			HashMapBuilder.put(
				_portal.getSiteDefaultLocale(_group), "test/"
			).build());
	}

	@Test
	public void testAddArticleWithURLWithURLWithConsecutiveSlashes()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "/test//////test",
			"test");

		Map<Locale, String> friendlyURLMap = journalArticle.getFriendlyURLMap();

		Assert.assertFalse(friendlyURLMap.isEmpty());

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			Assert.assertEquals("test/test", entry.getValue());
		}
	}

	@Test
	public void testAddArticleWithURLWithURLWithStartingSlash()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "/test", "test");

		Map<Locale, String> friendlyURLMap = journalArticle.getFriendlyURLMap();

		Assert.assertFalse(friendlyURLMap.isEmpty());

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			Assert.assertEquals("test", entry.getValue());
		}
	}

	@Test
	public void testArticleFriendlyURLValidation() throws Exception {
		_assertArticleFriendlyURLMap(_group);

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		_assertArticleFriendlyURLMap(companyGroup);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				companyGroup.getGroupId(), TestPropsValues.getUserId());

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), companyGroup, false, false,
				serviceContext);

			_assertArticleFriendlyURLMap(companyGroup.getStagingGroup());
		}
		finally {
			try {
				_stagingLocalService.disableStaging(
					companyGroup, serviceContext);
			}
			catch (Exception exception) {
			}

			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test(expected = ArticleFriendlyURLException.class)
	public void testArticleFriendlyURLValidationCompanyGroup()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			companyGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		_updateJournalArticle(Collections.emptyMap(), journalArticle);
	}

	@Test(expected = ArticleFriendlyURLException.class)
	public void testArticleFriendlyURLValidationCompanyGroupStagingEnabled()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				companyGroup.getGroupId(), TestPropsValues.getUserId());

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), companyGroup, false, false,
				serviceContext);

			Group stagingGroup = companyGroup.getStagingGroup();

			JournalArticle journalArticle = JournalTestUtil.addArticle(
				stagingGroup.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				Collections.emptyMap());

			_updateJournalArticle(Collections.emptyMap(), journalArticle);
		}
		finally {
			try {
				_stagingLocalService.disableStaging(
					companyGroup, serviceContext);
			}
			catch (Exception exception) {
			}

			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test(expected = ArticleFriendlyURLException.class)
	public void testArticleFriendlyURLValidationThrowsArticleFriendlyURLException()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		_updateJournalArticle(Collections.emptyMap(), journalArticle);
	}

	@Test
	public void testArticleSmallImageDocumentLibraryValidation()
		throws Exception {

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		JournalArticle journalArticle = _journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0, StringPool.BLANK,
			true, JournalArticleConstants.VERSION_DEFAULT,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), content,
			ddmStructure.getStructureId(), ddmTemplate.getTemplateKey(), null,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true, true,
			fileEntry.getFileEntryId(),
			JournalArticleConstants.SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA,
			null, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			fileEntry.getFileEntryId(), journalArticle.getSmallImageId());
		Assert.assertFalse(
			Validator.isNotNull(journalArticle.getSmallImageURL()));
	}

	@Test(expected = NoSuchImageException.class)
	public void testArticleSmallImageSourceDocumentsAndMediaValidation()
		throws Exception {

		String content = DDMStructureTestUtil.getSampleStructuredContent();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class));

		_journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0, StringPool.BLANK,
			true, JournalArticleConstants.VERSION_DEFAULT,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), content,
			ddmStructure.getStructureId(), ddmTemplate.getTemplateKey(), null,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true, true,
			0, JournalArticleConstants.SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA,
			null, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testArticleStatusHistoryAfterTrashAndRestore()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Date displayDate = _getDateWithOffset(1);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), null,
			LocaleUtil.getSiteDefault(), displayDate, null, true, true,
			serviceContext);

		displayDate = _getDateWithOffset(-1);

		journalArticle = JournalTestUtil.updateArticle(
			TestPropsValues.getUserId(), journalArticle,
			journalArticle.getTitleMap(), journalArticle.getContent(),
			displayDate, false, true, serviceContext);

		displayDate = _getDateWithOffset(2);

		journalArticle = JournalTestUtil.updateArticle(
			TestPropsValues.getUserId(), journalArticle,
			journalArticle.getTitleMap(), journalArticle.getContent(),
			displayDate, false, true, serviceContext);

		journalArticle = _journalArticleLocalService.moveArticleToTrash(
			TestPropsValues.getUserId(), journalArticle);

		journalArticle = _journalArticleLocalService.restoreArticleFromTrash(
			TestPropsValues.getUserId(), journalArticle);

		List<JournalArticle> journalArticles =
			_journalArticleLocalService.getArticles(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				ArticleVersionComparator.getInstance(false));

		journalArticle = journalArticles.get(0);

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, journalArticle.getStatus());

		journalArticle = journalArticles.get(1);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, journalArticle.getStatus());

		journalArticle = journalArticles.get(2);

		Assert.assertEquals(
			WorkflowConstants.STATUS_SCHEDULED, journalArticle.getStatus());
	}

	@Test
	public void testCopyArticle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test-1",
			RandomTestUtil.randomString());

		_testCopyArticle(journalArticle, " (Copy)", "-copy-");
		_testCopyArticle(journalArticle, " (Copy 1)", "-copy-1-");

		journalArticle = JournalTestUtil.updateArticle(
			journalArticle, "Test-2");

		_testCopyArticle(journalArticle, " (Copy)", "-copy-");
	}

	@Test
	public void testCopyArticleWithImages() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_readFileToString("ddm_form_with_images.json"),
				TestPropsValues.getUser());

		JournalArticle oldJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				StringUtil.replace(
					_readFileToString(
						"journal_article_content_with_images.xml"),
					new String[] {"[$IMAGE_JSON_1$]", "[$IMAGE_JSON_2$]"},
					new String[] {
						_toJSON(_addTempFileEntry("test_01.jpg")),
						_toJSON(_addTempFileEntry("test_02.jpg"))
					}),
				dataDefinition.getDataDefinitionKey(), null, LocaleUtil.US);

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				oldJournalArticle.getUserId(), oldJournalArticle.getGroupId(),
				oldJournalArticle.getArticleId(), null, true,
				oldJournalArticle.getVersion());

		Assert.assertEquals(
			oldJournalArticle.getImagesFileEntriesCount(),
			newJournalArticle.getImagesFileEntriesCount());

		_validateDDMFormValuesImages(newJournalArticle);
	}

	@Test
	public void testCopyArticleWithImagesAndNestedFields() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_readFileToString(
					"ddm_form_with_images_and_nested_fields.json"),
				TestPropsValues.getUser());

		JournalArticle oldJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				StringUtil.replace(
					_readFileToString(
						"journal_article_content_with_images_and_nested_" +
							"fields.xml"),
					new String[] {"[$IMAGE_JSON_1$]", "[$IMAGE_JSON_2$]"},
					new String[] {
						_toJSON(_addTempFileEntry("test_01.jpg")),
						_toJSON(_addTempFileEntry("test_02.jpg"))
					}),
				dataDefinition.getDataDefinitionKey(), null, LocaleUtil.US);

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				oldJournalArticle.getUserId(), oldJournalArticle.getGroupId(),
				oldJournalArticle.getArticleId(), null, true,
				oldJournalArticle.getVersion());

		Assert.assertEquals(
			oldJournalArticle.getImagesFileEntriesCount(),
			newJournalArticle.getImagesFileEntriesCount());

		_validateDDMFormValuesImages(newJournalArticle);
	}

	@Test
	public void testCopyArticleWithImagesAndRepeatableFields()
		throws Exception {

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_readFileToString(
					"ddm_form_with_images_and_repeatable_fields.json"),
				TestPropsValues.getUser());

		JournalArticle oldJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				StringUtil.replace(
					_readFileToString(
						"journal_article_content_with_images_and_repeatable_" +
							"fields.xml"),
					new String[] {
						"[$IMAGE_JSON_1$]", "[$IMAGE_JSON_2$]",
						"[$IMAGE_JSON_3$]", "[$IMAGE_JSON_4$]",
						"[$IMAGE_JSON_5$]", "[$IMAGE_JSON_6$]",
						"[$IMAGE_JSON_7$]", "[$IMAGE_JSON_8$]"
					},
					new String[] {
						_toJSON(_addTempFileEntry("test_01.jpg")),
						_toJSON(_addTempFileEntry("test_02.jpg")),
						_toJSON(_addTempFileEntry("test_03.jpg")),
						_toJSON(_addTempFileEntry("test_04.jpg")),
						_toJSON(_addTempFileEntry("test_05.jpg")),
						_toJSON(_addTempFileEntry("test_06.jpg")),
						_toJSON(_addTempFileEntry("test_07.jpg")),
						_toJSON(_addTempFileEntry("test_08.jpg"))
					}),
				dataDefinition.getDataDefinitionKey(), null, LocaleUtil.US);

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				oldJournalArticle.getUserId(), oldJournalArticle.getGroupId(),
				oldJournalArticle.getArticleId(), null, true,
				oldJournalArticle.getVersion());

		Assert.assertEquals(
			oldJournalArticle.getImagesFileEntriesCount(),
			newJournalArticle.getImagesFileEntriesCount());

		_validateDDMFormValuesImages(newJournalArticle);
	}

	@Test
	public void testCopyArticleWithMultipleImages() throws Exception {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			StringUtil.randomString(), "urltitle", StringUtil.randomString(),
			StringUtil.randomString(), new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		String fileEntryJSONString = _toJSON(fileEntry);

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_readFileToString("ddm_form_with_multiple_images.json"),
				TestPropsValues.getUser());

		JournalArticle oldJournalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				StringUtil.replace(
					_readFileToString(
						"journal_article_content_with_multiple_images.xml"),
					new String[] {"[$IMAGE_JSON_1$]", "[$IMAGE_JSON_2$]"},
					new String[] {fileEntryJSONString, fileEntryJSONString}),
				dataDefinition.getDataDefinitionKey(), null, LocaleUtil.US);

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				oldJournalArticle.getUserId(), oldJournalArticle.getGroupId(),
				oldJournalArticle.getArticleId(), null, true,
				oldJournalArticle.getVersion());

		Assert.assertEquals(0, newJournalArticle.getImagesFileEntriesCount());

		_validateDDMFormValuesImages(newJournalArticle);
	}

	@Test
	public void testCopyArticleWithSpecialCharacters() throws Exception {
		JournalArticle oldJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "hatékony",
			RandomTestUtil.randomString());

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				oldJournalArticle.getUserId(), oldJournalArticle.getGroupId(),
				oldJournalArticle.getArticleId(), null, true,
				oldJournalArticle.getVersion());

		Assert.assertNotEquals(oldJournalArticle, newJournalArticle);
		Assert.assertEquals(
			"hat%C3%A9kony-copy-", newJournalArticle.getUrlTitle());

		List<ResourcePermission> oldResourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				oldJournalArticle.getCompanyId(),
				JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(oldJournalArticle.getResourcePrimKey()));

		List<ResourcePermission> newResourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				newJournalArticle.getCompanyId(),
				JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(newJournalArticle.getResourcePrimKey()));

		Assert.assertEquals(
			StringBundler.concat(
				"Old resource permissions: ", oldResourcePermissions,
				", new resource permissions: ", newResourcePermissions),
			oldResourcePermissions.size(), newResourcePermissions.size());

		for (int i = 0; i < oldResourcePermissions.size(); i++) {
			ResourcePermission oldResourcePermission =
				oldResourcePermissions.get(i);
			ResourcePermission newResourcePermission =
				newResourcePermissions.get(i);

			Assert.assertNotEquals(
				oldResourcePermission, newResourcePermission);
			Assert.assertEquals(
				oldResourcePermission.getRoleId(),
				newResourcePermission.getRoleId());
			Assert.assertEquals(
				oldResourcePermission.getOwnerId(),
				newResourcePermission.getOwnerId());
			Assert.assertEquals(
				oldResourcePermission.getActionIds(),
				newResourcePermission.getActionIds());
			Assert.assertEquals(
				oldResourcePermission.isViewActionId(),
				newResourcePermission.isViewActionId());
		}
	}

	@Test
	public void testCopyDraftJournalArticleWithAssetCategories()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory1.getCategoryId()});

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle approvedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory2.getCategoryId()});

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalArticle draftJournalArticle = JournalTestUtil.updateArticle(
			approvedJournalArticle, RandomTestUtil.randomString(),
			approvedJournalArticle.getContent(), false, false, serviceContext);

		_assertAssetCategoryIds(
			approvedJournalArticle.getResourcePrimKey(),
			assetCategory1.getCategoryId());
		_assertAssetCategoryIds(
			draftJournalArticle.getId(), assetCategory2.getCategoryId());

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				draftJournalArticle.getUserId(),
				draftJournalArticle.getGroupId(),
				draftJournalArticle.getArticleId(), null, true,
				draftJournalArticle.getVersion());

		Assert.assertEquals(
			_getNewTitle(draftJournalArticle.getTitle(locale)),
			newJournalArticle.getTitle(locale));

		_assertAssetCategoryIds(
			newJournalArticle.getResourcePrimKey(),
			assetCategory2.getCategoryId());
	}

	@Test
	public void testCopyDraftJournalArticleWithAssetLinks() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle relatedJournalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		AssetEntry relatedJournalArticleAssetEntry1 =
			_assetEntryLocalService.getEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle1.getResourcePrimKey());

		serviceContext.setAssetLinkEntryIds(
			new long[] {relatedJournalArticleAssetEntry1.getEntryId()});

		JournalArticle approvedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		JournalArticle relatedJournalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		AssetEntry relatedJournalArticleAssetEntry2 =
			_assetEntryLocalService.getEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle2.getResourcePrimKey());

		serviceContext.setAssetLinkEntryIds(
			new long[] {relatedJournalArticleAssetEntry2.getEntryId()});

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalArticle draftJournalArticle = JournalTestUtil.updateArticle(
			approvedJournalArticle, RandomTestUtil.randomString(),
			approvedJournalArticle.getContent(), false, false, serviceContext);

		_assertAssetLinkEntryId(
			approvedJournalArticle.getResourcePrimKey(),
			relatedJournalArticleAssetEntry1.getEntryId());

		_assertAssetLinkEntryId(
			draftJournalArticle.getId(),
			relatedJournalArticleAssetEntry2.getEntryId());

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				draftJournalArticle.getUserId(),
				draftJournalArticle.getGroupId(),
				draftJournalArticle.getArticleId(), null, true,
				draftJournalArticle.getVersion());

		Assert.assertEquals(
			_getNewTitle(draftJournalArticle.getTitle(locale)),
			newJournalArticle.getTitle(locale));

		_assertAssetLinkEntryId(
			newJournalArticle.getResourcePrimKey(),
			relatedJournalArticleAssetEntry2.getEntryId());
	}

	@Test
	public void testCopyDraftJournalArticleWithAssetTags() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetTag assetTag1 = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		serviceContext.setAssetTagNames(new String[] {assetTag1.getName()});

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle approvedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		AssetTag assetTag2 = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		serviceContext.setAssetTagNames(new String[] {assetTag2.getName()});

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalArticle draftJournalArticle = JournalTestUtil.updateArticle(
			approvedJournalArticle, RandomTestUtil.randomString(),
			approvedJournalArticle.getContent(), false, false, serviceContext);

		_assertAssetTagIds(
			approvedJournalArticle.getResourcePrimKey(), assetTag1.getTagId());
		_assertAssetTagIds(draftJournalArticle.getId(), assetTag2.getTagId());

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				draftJournalArticle.getUserId(),
				draftJournalArticle.getGroupId(),
				draftJournalArticle.getArticleId(), null, true,
				draftJournalArticle.getVersion());

		Assert.assertEquals(
			_getNewTitle(draftJournalArticle.getTitle(locale)),
			newJournalArticle.getTitle(locale));

		_assertAssetTagIds(
			newJournalArticle.getResourcePrimKey(), assetTag2.getTagId());
	}

	@Test
	public void testCopyJournalArticleWithAssetCategories() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle sourceJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		_assertAssetCategoryIds(
			sourceJournalArticle.getResourcePrimKey(),
			assetCategory.getCategoryId());

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				sourceJournalArticle.getUserId(),
				sourceJournalArticle.getGroupId(),
				sourceJournalArticle.getArticleId(), null, true,
				sourceJournalArticle.getVersion());

		Assert.assertEquals(
			_getNewTitle(sourceJournalArticle.getTitle(locale)),
			newJournalArticle.getTitle(locale));

		_assertAssetCategoryIds(
			newJournalArticle.getResourcePrimKey(),
			assetCategory.getCategoryId());
	}

	@Test
	public void testCopyJournalArticleWithAssetLinks() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle relatedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		AssetEntry relatedJournalArticleAssetEntry =
			_assetEntryLocalService.getEntry(
				JournalArticle.class.getName(),
				relatedJournalArticle.getResourcePrimKey());

		serviceContext.setAssetLinkEntryIds(
			new long[] {relatedJournalArticleAssetEntry.getEntryId()});

		JournalArticle sourceJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		_assertAssetLinkEntryId(
			sourceJournalArticle.getResourcePrimKey(),
			relatedJournalArticleAssetEntry.getEntryId());

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				sourceJournalArticle.getUserId(),
				sourceJournalArticle.getGroupId(),
				sourceJournalArticle.getArticleId(), null, true,
				sourceJournalArticle.getVersion());

		Assert.assertEquals(
			_getNewTitle(sourceJournalArticle.getTitle(locale)),
			newJournalArticle.getTitle(locale));

		_assertAssetLinkEntryId(
			newJournalArticle.getResourcePrimKey(),
			relatedJournalArticleAssetEntry.getEntryId());
	}

	@Test
	public void testCopyJournalArticleWithAssetTags() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		serviceContext.setAssetTagNames(new String[] {assetTag.getName()});

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle sourceJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		_assertAssetTagIds(
			sourceJournalArticle.getResourcePrimKey(), assetTag.getTagId());

		JournalArticle newJournalArticle =
			_journalArticleLocalService.copyArticle(
				sourceJournalArticle.getUserId(),
				sourceJournalArticle.getGroupId(),
				sourceJournalArticle.getArticleId(), null, true,
				sourceJournalArticle.getVersion());

		Assert.assertEquals(
			_getNewTitle(sourceJournalArticle.getTitle(locale)),
			newJournalArticle.getTitle(locale));

		_assertAssetTagIds(
			newJournalArticle.getResourcePrimKey(), assetTag.getTagId());
	}

	@Test
	public void testDeleteDDMStructurePredefinedValues() throws Exception {
		Tuple tuple = _createJournalArticleWithPredefinedValues("Test Article");

		JournalArticle journalArticle = (JournalArticle)tuple.getObject(0);
		DDMStructure ddmStructure = (DDMStructure)tuple.getObject(1);

		DDMStructure actualDDMStructure = journalArticle.getDDMStructure();

		List<DDMFormField> ddmFormFields = actualDDMStructure.getDDMFormFields(
			false);

		for (DDMFormField ddmFormField : ddmFormFields) {
			LocalizedValue localizedValue = ddmFormField.getPredefinedValue();

			Map<Locale, String> values = localizedValue.getValues();

			for (Map.Entry<Locale, String> entry : values.entrySet()) {
				Assert.assertNotEquals(StringPool.BLANK, entry.getValue());
			}
		}

		Assert.assertEquals(
			actualDDMStructure.getStructureId(), ddmStructure.getStructureId());

		JournalArticle defaultJournalArticle =
			_journalArticleLocalService.getArticle(
				ddmStructure.getGroupId(), DDMStructure.class.getName(),
				ddmStructure.getStructureId());

		Assert.assertEquals(
			defaultJournalArticle.getTitle(), journalArticle.getTitle());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		_journalArticleLocalService.deleteArticleDefaultValues(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getDDMStructureId());

		Assert.assertNull(
			_journalArticleLocalService.fetchLatestArticle(
				defaultJournalArticle.getResourcePrimKey()));

		actualDDMStructure = _ddmStructureLocalService.getDDMStructure(
			actualDDMStructure.getStructureId());

		ddmFormFields = actualDDMStructure.getDDMFormFields(false);

		for (DDMFormField ddmFormField : ddmFormFields) {
			LocalizedValue localizedValue = ddmFormField.getPredefinedValue();

			Map<Locale, String> values = localizedValue.getValues();

			for (Map.Entry<Locale, String> entry : values.entrySet()) {
				Assert.assertEquals(StringPool.BLANK, entry.getValue());
			}
		}
	}

	@Test(expected = DuplicateArticleIdException.class)
	public void testDuplicatedArticleId() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			RandomTestUtil.randomString(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, null, true);

		JournalTestUtil.addArticle(
			RandomTestUtil.randomString(), _group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			journalArticle.getArticleId(), false);
	}

	@Test
	public void testDuplicatedAutoGeneratedArticleId() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			journalArticle.getArticleId(), true);
	}

	@Test
	public void testFetchLatestArticleByExternalReferenceCodeWithStatus()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertNotNull(
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					WorkflowConstants.STATUS_ANY, true));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString(),
			journalArticle.getContent(), false, false, serviceContext);

		journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					WorkflowConstants.STATUS_ANY, false);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, journalArticle.getStatus());
		Assert.assertEquals(1.1D, journalArticle.getVersion(), 0.0);

		journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					WorkflowConstants.STATUS_ANY, true);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, journalArticle.getStatus());
		Assert.assertEquals(1.0D, journalArticle.getVersion(), 0.0);

		journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					WorkflowConstants.STATUS_APPROVED, false);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, journalArticle.getStatus());
		Assert.assertEquals(1.0D, journalArticle.getVersion(), 0.0);

		journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					WorkflowConstants.STATUS_DRAFT, false);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, journalArticle.getStatus());
		Assert.assertEquals(1.1D, journalArticle.getVersion(), 0.0);
	}

	@Test
	public void testFetchLatestArticleByExternalReferenceCodeWithStatuses()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertNotNull(
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					new int[] {WorkflowConstants.STATUS_APPROVED}));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString(),
			journalArticle.getContent(), false, false, serviceContext);

		journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					new int[] {
						WorkflowConstants.STATUS_APPROVED,
						WorkflowConstants.STATUS_DRAFT
					});

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, journalArticle.getStatus());
		Assert.assertEquals(1.1D, journalArticle.getVersion(), 0.0);

		Assert.assertNull(
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					new int[] {WorkflowConstants.STATUS_PENDING}));

		journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					_group.getGroupId(),
					journalArticle.getExternalReferenceCode(),
					new int[] {
						WorkflowConstants.STATUS_APPROVED,
						WorkflowConstants.STATUS_PENDING
					});

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, journalArticle.getStatus());
		Assert.assertEquals(1.0D, journalArticle.getVersion(), 0.0);
	}

	@Test
	public void testFetchPersistedModelByResourcePrimKey() throws Exception {
		JournalArticle article = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		PersistedModel persistedModel =
			_journalArticleLocalService.fetchPersistedModel(
				article.getResourcePrimKey());

		Assert.assertNotNull(persistedModel);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalTestUtil.updateArticle(
			article, RandomTestUtil.randomString(), article.getContent(), false,
			false, serviceContext);

		JournalArticle persistedArticle =
			(JournalArticle)_journalArticleLocalService.fetchPersistedModel(
				article.getResourcePrimKey());

		Assert.assertEquals(persistedArticle.getId(), article.getId());
	}

	@Test
	public void testGetArticleDisplayFriendlyURLDisplayPageExists()
		throws Exception {

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String script = StringBundler.concat(
			"${friendlyURLs[\"", defaultLanguageId, "\"]}");

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, script,
			LocaleUtil.getSiteDefault());

		journalArticle.setDDMTemplateKey(ddmTemplate.getTemplateKey());

		journalArticle = _journalArticleLocalService.updateJournalArticle(
			journalArticle);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getDDMStructureId(), true,
				WorkflowConstants.STATUS_APPROVED);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			journalArticle.getUserId(), _group.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		JournalArticleDisplay journalArticleDisplay =
			_journalArticleLocalService.getArticleDisplay(
				_group.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, defaultLanguageId, _themeDisplay);

		String content = journalArticleDisplay.getContent();

		Assert.assertTrue(content.contains(_group.getFriendlyURL()));
		Assert.assertTrue(
			content.contains(
				journalArticle.getUrlTitle(LocaleUtil.getSiteDefault())));
	}

	@Test
	public void testGetArticleDisplayFriendlyURLWithoutDisplayPage()
		throws Exception {

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		String script = StringBundler.concat(
			"${friendlyURLs[\"", defaultLanguageId, "\"]!\"no-friendly-url\"}");

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, script,
			LocaleUtil.getSiteDefault());

		journalArticle.setDDMTemplateKey(ddmTemplate.getTemplateKey());

		journalArticle = _journalArticleLocalService.updateJournalArticle(
			journalArticle);

		JournalArticleDisplay journalArticleDisplay =
			_journalArticleLocalService.getArticleDisplay(
				_group.getGroupId(), journalArticle.getArticleId(),
				Constants.VIEW, defaultLanguageId, _themeDisplay);

		Assert.assertEquals(
			"no-friendly-url", journalArticleDisplay.getContent());
	}

	@Test
	public void testGetArticleDisplayWithComplexData() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_readFileToString("complex_data_definition.json"),
				TestPropsValues.getUser());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				StringUtil.replace(
					_readFileToString("complex_journal_article_content.xml"),
					"[$DOCUMENT_JSON$]", _toJSON(fileEntry)),
				dataDefinition.getDataDefinitionKey(), null, LocaleUtil.SPAIN);

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_group.getGroupId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			dataDefinition.getDataDefinitionKey());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL,
			_readFileToString("complex_template.ftl"), LocaleUtil.US);

		JournalArticleDisplay journalArticleDisplay = null;

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			journalArticleDisplay =
				_journalArticleLocalService.getArticleDisplay(
					journalArticle.getGroupId(), journalArticle.getArticleId(),
					ddmTemplate.getTemplateKey(), null, null, _themeDisplay);
		}

		String content = journalArticleDisplay.getContent();

		Assert.assertEquals(100, StringUtil.count(content, "<img alt="));
		Assert.assertTrue(content.contains("Web Content Render"));
	}

	@Test
	public void testGetArticleDisplayWithComplexDataAndTranslations()
		throws Exception {

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_readFileToString("complex_data_definition.json"),
				TestPropsValues.getUser());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
				StringUtil.replace(
					_readFileToString(
						"complex_with_translations_journal_article_content." +
							"xml"),
					"[$DOCUMENT_JSON$]", _toJSON(fileEntry)),
				dataDefinition.getDataDefinitionKey(), null, LocaleUtil.SPAIN);

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_group.getGroupId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			dataDefinition.getDataDefinitionKey());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL,
			_readFileToString("complex_template.ftl"), LocaleUtil.US);

		JournalArticleDisplay journalArticleDisplay = null;

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			journalArticleDisplay =
				_journalArticleLocalService.getArticleDisplay(
					journalArticle.getGroupId(), journalArticle.getArticleId(),
					ddmTemplate.getTemplateKey(), null, null, _themeDisplay);
		}

		String content = journalArticleDisplay.getContent();

		Assert.assertEquals(100, StringUtil.count(content, "<img alt="));
		Assert.assertTrue(content.contains("Web Content Render"));
	}

	@Test
	public void testGetArticleDisplayWithContentFromGlobalSite()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			company.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		DDMTemplate ddmTemplate1 = DDMTemplateTestUtil.addTemplate(
			company.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, "ddm template 1",
			LocaleUtil.getSiteDefault());

		DDMTemplate ddmTemplate2 = DDMTemplateTestUtil.addTemplate(
			company.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM, "ddm template 2",
			LocaleUtil.getSiteDefault());

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		JournalArticleDisplay journalArticleDisplay =
			_journalArticleLocalService.getArticleDisplay(
				journalArticle, ddmTemplate1.getTemplateKey(), Constants.VIEW,
				defaultLanguageId, 1, null, _themeDisplay);

		Assert.assertEquals(
			"ddm template 1", journalArticleDisplay.getContent());

		journalArticleDisplay = _journalArticleLocalService.getArticleDisplay(
			journalArticle, ddmTemplate2.getTemplateKey(), Constants.VIEW,
			defaultLanguageId, 1, null, _themeDisplay);

		Assert.assertEquals(
			"ddm template 2", journalArticleDisplay.getContent());
	}

	@Test
	public void testGetArticleDisplayWithSimpleData() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticleDisplay journalArticleDisplay =
			_journalArticleLocalService.getArticleDisplay(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				null, null, _themeDisplay);

		String content = journalArticleDisplay.getContent();

		Assert.assertFalse(content.contains("Web Content Render"));
	}

	@Test
	public void testGetArticleDisplayWithSmallImage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			FileUtil.getBytes(getClass(), "dependencies/liferay.png"), null,
			null, null, new ServiceContext());

		journalArticle.setSmallImage(true);
		journalArticle.setSmallImageId(fileEntry.getFileEntryId());
		journalArticle.setSmallImageSource(
			JournalArticleConstants.SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA);

		journalArticle = _journalArticleLocalService.updateJournalArticle(
			journalArticle);

		JournalArticleDisplay journalArticleDisplay =
			_journalArticleLocalService.getArticleDisplay(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				null, null, _themeDisplay);

		String articleDisplayImageURL =
			journalArticleDisplay.getArticleDisplayImageURL(_themeDisplay);

		Assert.assertNotNull(articleDisplayImageURL);
		Assert.assertTrue(
			articleDisplayImageURL.contains(fileEntry.getFileName()));
	}

	@Test
	public void testGetArticlesByReviewDate() throws Exception {
		JournalFolder folder = JournalTestUtil.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), folder.getFolderId());

		journalArticle.setUserId(RandomTestUtil.randomLong());

		Calendar calendar = new GregorianCalendar();

		calendar.add(Calendar.DATE, -1);

		journalArticle.setExpirationDate(calendar.getTime());
		journalArticle.setReviewDate(calendar.getTime());

		journalArticle = JournalArticleLocalServiceUtil.updateJournalArticle(
			journalArticle);

		JournalTestUtil.addArticle(_group.getGroupId(), folder.getFolderId());

		Company company = CompanyTestUtil.addCompany();

		Group group = GroupTestUtil.addGroup();

		JournalTestUtil.addArticle(
			group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		calendar.add(Calendar.DATE, -1);

		List<JournalArticle> journalArticles =
			_journalArticleLocalService.getArticlesByReviewDate(
				_group.getCompanyId(), calendar.getTime(), new Date());

		Assert.assertEquals(
			journalArticles.toString(), 1, journalArticles.size());
		Assert.assertEquals(journalArticle, journalArticles.get(0));

		_companyLocalService.deleteCompany(company);
	}

	@Test(expected = PortalException.class)
	public void testGetLatestArticleByExternalReferenceCodeWithStatus()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertNotNull(
			_journalArticleLocalService.getLatestArticleByExternalReferenceCode(
				_group.getGroupId(), journalArticle.getExternalReferenceCode(),
				WorkflowConstants.STATUS_ANY, true));

		_journalArticleLocalService.getLatestArticleByExternalReferenceCode(
			_group.getGroupId(), journalArticle.getExternalReferenceCode(),
			WorkflowConstants.STATUS_DRAFT, false);
	}

	@Test
	public void testGetNoAssetArticles() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		Assert.assertNotNull(assetEntry);

		_assetEntryLocalService.deleteAssetEntry(assetEntry);

		List<JournalArticle> journalArticles =
			_journalArticleLocalService.getNoAssetArticles();

		for (JournalArticle curArticle : journalArticles) {
			assetEntry = _assetEntryLocalService.fetchEntry(
				JournalArticle.class.getName(),
				curArticle.getResourcePrimKey());

			Assert.assertNull(assetEntry);

			_journalArticleLocalService.deleteJournalArticle(
				curArticle.getPrimaryKey());

			_resourceLocalService.deleteResource(
				curArticle.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				curArticle.getResourcePrimKey());

			_ddmTemplateLinkLocalService.deleteTemplateLink(
				PortalUtil.getClassNameId(JournalArticle.class),
				curArticle.getPrimaryKey());
		}
	}

	@Test
	public void testGetNoPermissionArticles() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				journalArticle.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalArticle.getResourcePrimKey()));

		for (ResourcePermission resourcePermission : resourcePermissions) {
			_resourcePermissionLocalService.deleteResourcePermission(
				resourcePermission.getResourcePermissionId());
		}

		List<JournalArticle> journalArticles =
			_journalArticleLocalService.getNoPermissionArticles();

		Assert.assertEquals(
			journalArticles.toString(), 1, journalArticles.size());
		Assert.assertEquals(journalArticle, journalArticles.get(0));
	}

	@Test
	public void testGetNotInTrashArticlesCount() throws Exception {
		JournalFolder journalFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());

		JournalTestUtil.addArticle(
			_group.getGroupId(), journalFolder.getFolderId(),
			Collections.emptyMap());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), journalFolder.getFolderId(),
			Collections.emptyMap());

		Assert.assertEquals(
			2,
			_journalArticleLocalService.getNotInTrashArticlesCount(
				_group.getGroupId(), journalFolder.getFolderId()));

		_journalArticleLocalService.moveArticleToTrash(
			TestPropsValues.getUserId(), journalArticle);

		Assert.assertEquals(
			1,
			_journalArticleLocalService.getNotInTrashArticlesCount(
				_group.getGroupId(), journalFolder.getFolderId()));
	}

	@Test
	public void testRemoveArticleLocale() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_readFileToString("ddm_form.json"), TestPropsValues.getUser());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			_portal.getSiteGroupId(_group.getGroupId()),
			_portal.getClassNameId(JournalArticle.class.getName()),
			dataDefinition.getDataDefinitionKey(), true);

		JournalArticle journalArticle = _journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
			PortalUtil.getClassNameId(JournalArticle.class), StringPool.BLANK,
			true, 0,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "title-es"
			).put(
				LocaleUtil.US, "title"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "description-es"
			).put(
				LocaleUtil.US, "description"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "friendly-url-es"
			).put(
				LocaleUtil.US, "friendly-url"
			).build(),
			StringUtil.replace(
				_readFileToString(
					"journal_article_content_with_different_locales.xml"),
				"[$DOCUMENT_JSON$]", _toJSON(fileEntry)),
			ddmStructure.getStructureId(), null, null, 1, 1, 1965, 0, 0, 0, 0,
			0, 0, 0, true, 0, 0, 0, 0, 0, true, true, false, 0, 0, null, null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		journalArticle = _journalArticleLocalService.removeArticleLocale(
			_group.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getVersion(),
			LocaleUtil.toLanguageId(LocaleUtil.SPAIN));

		Assert.assertEquals("title", journalArticle.getTitle(LocaleUtil.SPAIN));
		Assert.assertEquals(
			"description", journalArticle.getDescription(LocaleUtil.SPAIN));

		Map<Locale, String> friendlyURLMap = journalArticle.getFriendlyURLMap();

		Assert.assertNull(friendlyURLMap.get(LocaleUtil.SPAIN));

		DDMFormValues ddmFormValues = journalArticle.getDDMFormValues();

		Set<Locale> availableLocales = ddmFormValues.getAvailableLocales();

		Assert.assertEquals(
			availableLocales.toString(), 1, availableLocales.size());
		Assert.assertFalse(availableLocales.contains(LocaleUtil.SPAIN));

		_validateDDMFormFieldValues(ddmFormValues.getDDMFormFieldValues());
	}

	@Test
	public void testRemoveArticleLocaleWithNestedFields() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				_readFileToString("ddm_form_nested_fields.json"),
				TestPropsValues.getUser());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.IMAGE_JPEG,
			FileUtil.getBytes(getClass(), "dependencies/image.jpg"), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			_portal.getSiteGroupId(_group.getGroupId()),
			_portal.getClassNameId(JournalArticle.class.getName()),
			dataDefinition.getDataDefinitionKey(), true);

		JournalArticle journalArticle = _journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
			PortalUtil.getClassNameId(JournalArticle.class), StringPool.BLANK,
			true, 0,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "title-es"
			).put(
				LocaleUtil.US, "title"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "description-es"
			).put(
				LocaleUtil.US, "description"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "friendly-url-es"
			).put(
				LocaleUtil.US, "friendly-url"
			).build(),
			StringUtil.replace(
				_readFileToString(
					"journal_article_content_nested_fields" +
						"_with_different_locales.xml"),
				"[$DOCUMENT_JSON$]", _toJSON(fileEntry)),
			ddmStructure.getStructureId(), null, null, 1, 1, 1965, 0, 0, 0, 0,
			0, 0, 0, true, 0, 0, 0, 0, 0, true, true, false, 0, 0, null, null,
			null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		journalArticle = _journalArticleLocalService.removeArticleLocale(
			_group.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getVersion(),
			LocaleUtil.toLanguageId(LocaleUtil.SPAIN));

		Assert.assertEquals("title", journalArticle.getTitle(LocaleUtil.SPAIN));
		Assert.assertEquals(
			"description", journalArticle.getDescription(LocaleUtil.SPAIN));

		Map<Locale, String> friendlyURLMap = journalArticle.getFriendlyURLMap();

		Assert.assertNull(friendlyURLMap.get(LocaleUtil.SPAIN));

		DDMFormValues ddmFormValues = journalArticle.getDDMFormValues();

		Set<Locale> availableLocales = ddmFormValues.getAvailableLocales();

		Assert.assertEquals(
			availableLocales.toString(), 1, availableLocales.size());
		Assert.assertFalse(availableLocales.contains(LocaleUtil.SPAIN));

		_validateDDMFormFieldValues(ddmFormValues.getDDMFormFieldValues());
	}

	@Test
	public void testRevertArticle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		JournalArticle updatedJournalArticle = JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString());

		_journalArticleLocalService.revertArticle(
			journalArticle.getUserId(), _group.getGroupId(),
			journalArticle.getArticleId(), journalArticle.getVersion());

		JournalArticle latestArticle =
			_journalArticleLocalService.getLatestArticle(
				journalArticle.getGroupId(), journalArticle.getArticleId());

		Assert.assertEquals(
			journalArticle.getResourcePrimKey(),
			latestArticle.getResourcePrimKey());
		Assert.assertTrue(
			updatedJournalArticle.getVersion() < latestArticle.getVersion());
		Assert.assertEquals(
			journalArticle.getTitle(), latestArticle.getTitle());
		Assert.assertEquals(
			journalArticle.getContent(), latestArticle.getContent());
	}

	@Test
	public void testTrashArticleExternalReferenceCode() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		JournalArticle originalJournalArticle =
			JournalTestUtil.addArticleWithWorkflow(
				_group.getGroupId(), 0, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), true, serviceContext);

		JournalArticle latestJournalArticle =
			_journalArticleLocalService.updateArticle(
				originalJournalArticle.getUserId(),
				originalJournalArticle.getGroupId(),
				originalJournalArticle.getFolderId(),
				originalJournalArticle.getArticleId(),
				originalJournalArticle.getVersion(),
				originalJournalArticle.getTitleMap(),
				originalJournalArticle.getDescriptionMap(),
				originalJournalArticle.getContent(),
				originalJournalArticle.getLayoutUuid(), serviceContext);

		Map<Double, String> externalReferenceCodeMap = HashMapBuilder.put(
			originalJournalArticle.getVersion(),
			originalJournalArticle.getExternalReferenceCode()
		).put(
			latestJournalArticle.getVersion(),
			latestJournalArticle.getExternalReferenceCode()
		).build();

		latestJournalArticle = _journalArticleLocalService.moveArticleToTrash(
			TestPropsValues.getUserId(), latestJournalArticle);

		for (Map.Entry<Double, String> entry :
				externalReferenceCodeMap.entrySet()) {

			JournalArticle persistedJournalArticle =
				_journalArticleLocalService.fetchArticleByUrlTitle(
					originalJournalArticle.getGroupId(),
					originalJournalArticle.getUrlTitle(), entry.getKey());

			Assert.assertTrue(persistedJournalArticle.isInTrash());
			Assert.assertEquals(
				entry.getValue(),
				persistedJournalArticle.getExternalReferenceCode());
		}

		_journalArticleLocalService.restoreArticleFromTrash(
			TestPropsValues.getUserId(), latestJournalArticle);

		for (Map.Entry<Double, String> entry :
				externalReferenceCodeMap.entrySet()) {

			JournalArticle persistedJournalArticle =
				_journalArticleLocalService.fetchArticleByUrlTitle(
					originalJournalArticle.getGroupId(),
					originalJournalArticle.getUrlTitle(), entry.getKey());

			Assert.assertFalse(persistedJournalArticle.isInTrash());
			Assert.assertEquals(
				entry.getValue(),
				persistedJournalArticle.getExternalReferenceCode());
		}
	}

	@Test
	public void testUpdateArticleByNonownerUser() throws Exception {
		User ownerUser = UserTestUtil.addGroupUser(
			_group, RoleConstants.ADMINISTRATOR);

		PermissionChecker ownerPermissionChecker =
			_permissionCheckerFactory.create(ownerUser);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(ownerPermissionChecker);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getCompanyId(), _group.getGroupId(),
					ownerUser.getUserId());

			JournalArticle journalArticle =
				JournalTestUtil.addArticleWithWorkflow(
					_group.getGroupId(), 0, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), true, serviceContext);

			_assertArticleUser(journalArticle, ownerUser, ownerUser);

			Role siteMemberRole = _roleLocalService.getRole(
				_group.getCompanyId(), RoleConstants.SITE_MEMBER);

			_resourcePermissionLocalService.setResourcePermissions(
				_group.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalArticle.getResourcePrimKey()),
				siteMemberRole.getRoleId(),
				new String[] {
					ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW,
					ActionKeys.UPDATE
				});

			User nonownerUser = UserTestUtil.addGroupUser(
				_group, RoleConstants.SITE_MEMBER);

			PermissionChecker nonownerPermissionChecker =
				_permissionCheckerFactory.create(nonownerUser);

			Assert.assertFalse(
				nonownerPermissionChecker.hasPermission(
					_group.getGroupId(), JournalArticle.class.getName(),
					String.valueOf(journalArticle.getResourcePrimKey()),
					ActionKeys.PERMISSIONS));
			Assert.assertTrue(
				nonownerPermissionChecker.hasPermission(
					_group.getGroupId(), JournalArticle.class.getName(),
					String.valueOf(journalArticle.getResourcePrimKey()),
					ActionKeys.UPDATE));

			PermissionThreadLocal.setPermissionChecker(
				nonownerPermissionChecker);

			serviceContext = ServiceContextTestUtil.getServiceContext(
				_group.getCompanyId(), _group.getGroupId(),
				nonownerPermissionChecker.getUserId());

			serviceContext.setAddGroupPermissions(false);
			serviceContext.setAddGuestPermissions(false);

			Double originalArticleVersion = journalArticle.getVersion();

			journalArticle = _journalArticleLocalService.updateArticle(
				nonownerUser.getUserId(), journalArticle.getGroupId(),
				journalArticle.getFolderId(), journalArticle.getArticleId(),
				journalArticle.getVersion(), journalArticle.getTitleMap(),
				journalArticle.getDescriptionMap(), journalArticle.getContent(),
				journalArticle.getLayoutUuid(), serviceContext);

			int versionComparison = Double.compare(
				originalArticleVersion, journalArticle.getVersion());

			Assert.assertTrue(versionComparison < 0);

			_assertArticleUser(journalArticle, ownerUser, nonownerUser);

			Assert.assertFalse(
				nonownerPermissionChecker.hasPermission(
					_group.getGroupId(), JournalArticle.class.getName(),
					String.valueOf(journalArticle.getResourcePrimKey()),
					ActionKeys.PERMISSIONS));
			Assert.assertTrue(
				nonownerPermissionChecker.hasPermission(
					_group.getGroupId(), JournalArticle.class.getName(),
					String.valueOf(journalArticle.getResourcePrimKey()),
					ActionKeys.UPDATE));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test(expected = AssetCategoryException.class)
	public void testUpdateArticleWithAssetCategoriesFromNonmultiValuedAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			_getAssetVocabularySettingsHelper(
				false, new long[] {AssetCategoryConstants.ALL_CLASS_NAME_ID},
				new long[] {AssetCategoryConstants.ALL_CLASS_TYPE_PK},
				new boolean[] {false}, new boolean[] {false});

		Assert.assertFalse(assetVocabularySettingsHelper.isMultiValued());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, assetVocabularySettingsHelper.toString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory1.getCategoryId()});

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), LocaleUtil.getSiteDefault(), false,
			false, serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		JournalTestUtil.updateArticle(
			journalArticle, RandomTestUtil.randomString(),
			journalArticle.getContent(), true, true, serviceContext);
	}

	@Test
	public void testUpdateArticleWithoutDisplayDate() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertNotNull(journalArticle1.getDisplayDate());

		JournalArticle journalArticle2 = JournalTestUtil.updateArticle(
			journalArticle1);

		Assert.assertEquals(
			journalArticle1.getDisplayDate(), journalArticle2.getDisplayDate());

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date date = calendar.getTime();

		journalArticle2 = JournalTestUtil.updateArticle(
			journalArticle2.getUserId(), journalArticle2,
			journalArticle2.getTitleMap(), journalArticle2.getContent(), date,
			false, true, ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(date, journalArticle2.getDisplayDate());
	}

	@Test
	public void testUpdateDDMStructurePredefinedValues() throws Exception {
		Tuple tuple = _createJournalArticleWithPredefinedValues("Test Article");

		JournalArticle journalArticle = (JournalArticle)tuple.getObject(0);
		DDMStructure ddmStructure = (DDMStructure)tuple.getObject(1);

		DDMStructure actualDDMStructure = journalArticle.getDDMStructure();

		Assert.assertEquals(
			actualDDMStructure.getStructureId(), ddmStructure.getStructureId());

		Fields fields = _journalConverter.getDDMFields(
			ddmStructure, journalArticle.getContent());

		Field field = fields.get("name");

		Assert.assertNotNull(field);

		Assert.assertEquals(
			"Valor Predefinido", field.getValue(LocaleUtil.BRAZIL));
		Assert.assertEquals(
			"Valeur Prédéfinie", field.getValue(LocaleUtil.FRENCH));
		Assert.assertEquals(
			"Valor Predefinido", field.getValue(LocaleUtil.SPAIN));
		Assert.assertEquals("Predefined Value", field.getValue(LocaleUtil.US));

		Locale unavailableLocale = LocaleUtil.ITALY;

		Assert.assertEquals(
			"Predefined Value", field.getValue(unavailableLocale));
	}

	private FileEntry _addTempFileEntry(String fileName) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"/com/liferay/journal/service/test/dependencies/images/" +
				fileName);

		return TempFileEntryUtil.addTempFileEntry(
			String.valueOf(
				new UUID(
					SecureRandomUtil.nextLong(), SecureRandomUtil.nextLong())),
			_group.getGroupId(), TestPropsValues.getUserId(),
			JournalArticle.class.getName(), fileName, inputStream,
			ContentTypes.IMAGE_JPEG);
	}

	private void _assertArticleFriendlyURLMap(Group group) throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			Collections.emptyMap());

		Locale locale = _portal.getSiteDefaultLocale(group.getGroupId());

		String friendlyURL = _friendlyURLNormalizer.normalize(
			StringBundler.concat(
				RandomTestUtil.randomString(5), StringPool.PERIOD,
				RandomTestUtil.randomString(5), StringPool.SLASH,
				RandomTestUtil.randomString(5)));

		Assert.assertTrue(friendlyURL.contains(StringPool.PERIOD));
		Assert.assertTrue(friendlyURL.contains(StringPool.SLASH));

		Map<Locale, String> friendlyURLMap = journalArticle.getFriendlyURLMap();

		journalArticle = _updateJournalArticle(
			HashMapBuilder.put(
				locale, friendlyURL
			).build(),
			journalArticle);

		friendlyURLMap.put(locale, friendlyURL);

		Assert.assertEquals(friendlyURLMap, journalArticle.getFriendlyURLMap());
	}

	private void _assertArticleUser(
		JournalArticle journalArticle, User expectedOwnerUser,
		User expectedStatusByUser) {

		Assert.assertEquals(
			expectedOwnerUser.getUserId(), journalArticle.getUserId());
		Assert.assertEquals(
			expectedOwnerUser.getFullName(), journalArticle.getUserName());

		Assert.assertEquals(
			expectedStatusByUser.getUserId(),
			journalArticle.getStatusByUserId());
		Assert.assertEquals(
			expectedStatusByUser.getFullName(),
			journalArticle.getStatusByUserName());
	}

	private void _assertAssetCategoryIds(long classPK, long categoryId) {
		long[] assetCategoryIds = _assetCategoryLocalService.getCategoryIds(
			JournalArticle.class.getName(), classPK);

		Assert.assertArrayEquals(
			Arrays.toString(assetCategoryIds), new long[] {categoryId},
			assetCategoryIds);
	}

	private void _assertAssetLinkEntryId(long classPK, long assetLinkEntryId)
		throws Exception {

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(), classPK);

		List<AssetLink> assetLinks = _assetLinkLocalService.getDirectLinks(
			assetEntry.getEntryId(), false);

		Assert.assertEquals(assetLinks.toString(), 1, assetLinks.size());

		AssetLink assetLink = assetLinks.get(0);

		Assert.assertEquals(assetLinkEntryId, assetLink.getEntryId2());
	}

	private void _assertAssetTagIds(long classPK, long assetTagId) {
		List<AssetTag> assetTags = _assetTagLocalService.getTags(
			JournalArticle.class.getName(), classPK);

		Assert.assertEquals(assetTags.toString(), 1, assetTags.size());

		AssetTag assetTag = assetTags.get(0);

		Assert.assertEquals(assetTagId, assetTag.getTagId());
	}

	private Tuple _createJournalArticleWithPredefinedValues(String title)
		throws Exception {

		Set<Locale> availableLocales = DDMFormTestUtil.createAvailableLocales(
			LocaleUtil.BRAZIL, LocaleUtil.FRENCH, LocaleUtil.SPAIN,
			LocaleUtil.US);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			availableLocales, LocaleUtil.US);

		DDMFormField ddmFormField =
			DDMFormTestUtil.createLocalizableTextDDMFormField("name");

		LocalizedValue label = new LocalizedValue(LocaleUtil.US);

		label.addString(LocaleUtil.BRAZIL, "rótulo");
		label.addString(LocaleUtil.FRENCH, "étiquette");
		label.addString(LocaleUtil.SPAIN, "etiqueta");
		label.addString(LocaleUtil.US, "label");

		ddmFormField.setLabel(label);

		ddmForm.addDDMFormField(ddmFormField);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(), ddmForm);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL,
			JournalTestUtil.getSampleTemplateFTL(), LocaleUtil.US);

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			HashMapBuilder.put(
				LocaleUtil.BRAZIL, "Valor Predefinido"
			).put(
				LocaleUtil.FRENCH, "Valeur Prédéfinie"
			).put(
				LocaleUtil.SPAIN, "Valor Predefinido"
			).put(
				LocaleUtil.US, "Predefined Value"
			).build(),
			LocaleUtil.US.toString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		JournalArticle journalArticle =
			_journalArticleLocalService.addArticleDefaultValues(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				_classNameLocalService.getClassNameId(DDMStructure.class),
				ddmStructure.getStructureId(),
				HashMapBuilder.put(
					LocaleUtil.US, title
				).build(),
				null, content, ddmStructure.getStructureId(),
				ddmTemplate.getTemplateKey(), null, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, true, 0, 0, 0, 0, 0, true, true, false, 0, 0, null, null,
				serviceContext);

		return new Tuple(journalArticle, ddmStructure);
	}

	private AssetVocabularySettingsHelper _getAssetVocabularySettingsHelper(
		boolean multiValued, long[] classNameIds, long[] classTypePKs,
		boolean[] depotRequireds, boolean[] requireds) {

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			classNameIds, classTypePKs, depotRequireds, requireds);
		assetVocabularySettingsHelper.setMultiValued(multiValued);

		return assetVocabularySettingsHelper;
	}

	private Date _getDateWithOffset(int years) {
		LocalDateTime localDateTime = LocalDateTime.now();

		localDateTime = localDateTime.plusYears(years);

		ZonedDateTime zonedDateTime = localDateTime.atZone(
			ZoneId.systemDefault());

		return Date.from(zonedDateTime.toInstant());
	}

	private String _getNewTitle(String title) {
		return StringBundler.concat(
			title, StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
			_language.get(LocaleUtil.getSiteDefault(), "copy"),
			StringPool.CLOSE_PARENTHESIS);
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));

		Layout layout = _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, LayoutConstants.TYPE_PORTLET, false,
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		themeDisplay.setLayout(layout);

		LayoutSet layoutSet = layout.getLayoutSet();

		themeDisplay.setLayoutSet(layoutSet);

		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setRequest(httpServletRequest);

		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private String _readFileToString(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private void _testCopyArticle(
			JournalArticle journalArticle, String titleSuffix,
			String urlTitleSuffix)
		throws Exception {

		JournalArticle journalArticle1 =
			_journalArticleLocalService.copyArticle(
				journalArticle.getUserId(), journalArticle.getGroupId(),
				journalArticle.getArticleId(), null, true,
				journalArticle.getVersion());

		Assert.assertNotEquals(journalArticle, journalArticle1);
		Assert.assertEquals(
			journalArticle.getTitle() + titleSuffix,
			journalArticle1.getTitle());
		Assert.assertEquals(
			journalArticle.getUrlTitle() + urlTitleSuffix,
			journalArticle1.getUrlTitle());

		List<ResourcePermission> oldResourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				journalArticle.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalArticle.getResourcePrimKey()));

		List<ResourcePermission> newResourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				journalArticle1.getCompanyId(), JournalArticle.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(journalArticle1.getResourcePrimKey()));

		Assert.assertEquals(
			StringBundler.concat(
				"Old resource permissions: ", oldResourcePermissions,
				", new resource permissions: ", newResourcePermissions),
			oldResourcePermissions.size(), newResourcePermissions.size());

		for (int i = 0; i < oldResourcePermissions.size(); i++) {
			ResourcePermission oldResourcePermission =
				oldResourcePermissions.get(i);
			ResourcePermission newResourcePermission =
				newResourcePermissions.get(i);

			Assert.assertNotEquals(
				oldResourcePermission, newResourcePermission);

			Assert.assertEquals(
				oldResourcePermission.getRoleId(),
				newResourcePermission.getRoleId());
			Assert.assertEquals(
				oldResourcePermission.getOwnerId(),
				newResourcePermission.getOwnerId());
			Assert.assertEquals(
				oldResourcePermission.getActionIds(),
				newResourcePermission.getActionIds());
			Assert.assertEquals(
				oldResourcePermission.isViewActionId(),
				newResourcePermission.isViewActionId());
		}
	}

	private String _toJSON(FileEntry fileEntry) {
		return JSONUtil.put(
			"alt", StringPool.BLANK
		).put(
			"description", StringPool.BLANK
		).put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"groupId", fileEntry.getGroupId()
		).put(
			"name", fileEntry.getFileName()
		).put(
			"title", fileEntry.getTitle()
		).put(
			"type", "journal"
		).put(
			"uuid", fileEntry.getUuid()
		).toString();
	}

	private JournalArticle _updateJournalArticle(
			Map<Locale, String> friendlyURLMap, JournalArticle journalArticle)
		throws Exception {

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.setTime(journalArticle.getDisplayDate());

		int displayDateMonth = calendar.get(Calendar.MONTH);
		int displayDateDay = calendar.get(Calendar.DATE);
		int displayDateYear = calendar.get(Calendar.YEAR);
		int displayDateHour = calendar.get(Calendar.HOUR);
		int displayDateMinute = calendar.get(Calendar.MINUTE);

		if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
			displayDateHour += 12;
		}

		return _journalArticleLocalService.updateArticle(
			TestPropsValues.getUserId(), journalArticle.getGroupId(),
			journalArticle.getFolderId(), journalArticle.getArticleId(),
			journalArticle.getVersion(), journalArticle.getTitleMap(),
			journalArticle.getDescriptionMap(), friendlyURLMap,
			journalArticle.getContent(), journalArticle.getDDMTemplateKey(),
			journalArticle.getLayoutUuid(), displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute, 0, 0, 0, 0, 0,
			true, 0, 0, 0, 0, 0, true, journalArticle.isIndexable(), false, 0,
			0, null, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				journalArticle.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _validateDDMFormFieldValues(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			Value value = ddmFormFieldValue.getValue();

			if (value != null) {
				Set<Locale> valueAvailableLocales = value.getAvailableLocales();

				Assert.assertEquals(
					valueAvailableLocales.toString(), 1,
					valueAvailableLocales.size());
				Assert.assertFalse(
					valueAvailableLocales.contains(LocaleUtil.SPAIN));

				Assert.assertEquals(
					value.getString(value.getDefaultLocale()),
					value.getString(LocaleUtil.SPAIN));
			}

			if (ListUtil.isNotEmpty(
					ddmFormFieldValue.getNestedDDMFormFieldValues())) {

				_validateDDMFormFieldValues(
					ddmFormFieldValue.getNestedDDMFormFieldValues());
			}
		}
	}

	private void _validateDDMFormValuesImages(JournalArticle journalArticle)
		throws Exception {

		DDMFormValues ddmFormValues = journalArticle.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		for (Map.Entry<String, List<DDMFormFieldValue>> entry :
				ddmFormFieldValuesMap.entrySet()) {

			List<DDMFormFieldValue> ddmFormFieldValues = entry.getValue();

			for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
				if (!Objects.equals(
						DDMFormFieldTypeConstants.IMAGE,
						ddmFormFieldValue.getType())) {

					continue;
				}

				Value value = ddmFormFieldValue.getValue();

				for (Locale locale : value.getAvailableLocales()) {
					JSONObject jsonObject = _jsonFactory.createJSONObject(
						value.getString(locale));

					Assert.assertEquals(
						journalArticle.getResourcePrimKey(),
						jsonObject.getLong("resourcePrimKey"));
				}
			}
		}
	}

	@Inject(
		filter = "model.class.name=com.liferay.journal.model.JournalArticle"
	)
	private static ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetLinkLocalService _assetLinkLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalConverter _journalConverter;

	private JournalFolderFixture _journalFolderFixture;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private Portal _portal;

	@Inject
	private ResourceLocalService _resourceLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private ResourcePermissionService _resourcePermissionService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

	private ThemeDisplay _themeDisplay;

	@Inject
	private UserLocalService _userLocalService;

}