/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.headless.delivery.client.custom.field.CustomField;
import com.liferay.headless.delivery.client.custom.field.CustomValue;
import com.liferay.headless.delivery.client.dto.v1_0.ContentDocument;
import com.liferay.headless.delivery.client.dto.v1_0.ContentField;
import com.liferay.headless.delivery.client.dto.v1_0.ContentFieldValue;
import com.liferay.headless.delivery.client.dto.v1_0.Geo;
import com.liferay.headless.delivery.client.dto.v1_0.RelatedContent;
import com.liferay.headless.delivery.client.dto.v1_0.StructuredContent;
import com.liferay.headless.delivery.client.dto.v1_0.StructuredContentLink;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.problem.Problem;
import com.liferay.headless.delivery.client.resource.v1_0.StructuredContentResource;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class StructuredContentResourceTest
	extends BaseStructuredContentResourceTestCase {

	@ClassRule
	@Rule
	public static final SynchronousMailTestRule synchronousMailTestRule =
		SynchronousMailTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		_blogsEntry = BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		_ddmStructure = _addDDMStructure(testGroup, "test-ddm-structure.json");

		_ddmTemplate = _addDDMTemplate(_ddmStructure);

		_depotDDMStructure = _addDDMStructure(
			testDepotEntryGroup, "test-ddm-structure.json");

		DLFolder dlFolder = DLTestUtil.addDLFolder(testGroup.getGroupId());

		_dlFileEntry = DLTestUtil.addDLFileEntry(dlFolder.getFolderId());

		_expandoTable = ExpandoTestUtil.addTable(
			_portal.getClassNameId(JournalArticle.class.getName()),
			"CUSTOM_FIELDS");

		_irrelevantDDMStructure = _addDDMStructure(
			irrelevantGroup, "test-ddm-structure.json");

		_irrelevantDepotDDMStructure = _addDDMStructure(
			irrelevantDepotEntryGroup, "test-ddm-structure.json");

		_addDDMTemplate(_irrelevantDDMStructure);

		_irrelevantJournalFolder = JournalTestUtil.addFolder(
			irrelevantGroup.getGroupId(), RandomTestUtil.randomString());
		_journalFolder = JournalTestUtil.addFolder(
			testGroup.getGroupId(), RandomTestUtil.randomString());
		_layout = LayoutTestUtil.addTypeContentLayout(testGroup);
		_localizedDDMStructure = _addDDMStructure(
			testGroup, "test-localized-ddm-structure.json");
		_unlocalizedDDMStructure = _addDDMStructure(
			testGroup, "test-unlocalized-ddm-structure.json");
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		PrincipalThreadLocal.setName(_originalName);
	}

	@Override
	@Test
	public void testDeleteAssetLibraryStructuredContentByExternalReferenceCode()
		throws Exception {

		_useDepotDDMStructureStructureId = true;

		super.testDeleteAssetLibraryStructuredContentByExternalReferenceCode();

		StructuredContent randomStructuredContent = randomStructuredContent();

		randomStructuredContent.setExternalReferenceCode("");

		StructuredContent postStructuredContent =
			structuredContentResource.postAssetLibraryStructuredContent(
				testDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
				randomStructuredContent);

		assertHttpResponseStatusCode(
			204,
			structuredContentResource.
				deleteAssetLibraryStructuredContentByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContent.getUuid()));
		assertHttpResponseStatusCode(
			404,
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContent.getExternalReferenceCode()));

		String externalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		try {
			structuredContentResource.
				deleteAssetLibraryStructuredContentByExternalReferenceCode(
					testDeleteAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					externalReferenceCode);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testDeleteStructuredContentMyRating() throws Exception {
		super.testDeleteStructuredContentMyRating();

		StructuredContent structuredContent =
			testDeleteStructuredContentMyRating_addStructuredContent();

		assertHttpResponseStatusCode(
			204,
			structuredContentResource.
				deleteStructuredContentMyRatingHttpResponse(
					structuredContent.getId()));
		assertHttpResponseStatusCode(
			404,
			structuredContentResource.
				deleteStructuredContentMyRatingHttpResponse(
					structuredContent.getId()));

		StructuredContent irrelevantStructuredContent =
			randomIrrelevantStructuredContent();

		assertHttpResponseStatusCode(
			404,
			structuredContentResource.
				deleteStructuredContentMyRatingHttpResponse(
					irrelevantStructuredContent.getId()));
	}

	@Override
	@Test
	public void testGetAssetLibraryStructuredContentByExternalReferenceCode()
		throws Exception {

		_useDepotDDMStructureStructureId = true;

		super.testGetAssetLibraryStructuredContentByExternalReferenceCode();

		// Blank external reference code

		StructuredContent randomStructuredContent = randomStructuredContent();

		randomStructuredContent.setExternalReferenceCode("");

		StructuredContent postStructuredContent =
			structuredContentResource.postAssetLibraryStructuredContent(
				testGetAssetLibraryStructuredContentsPage_getAssetLibraryId(),
				randomStructuredContent);

		StructuredContent getStructuredContent =
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCode(
					testGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					postStructuredContent.getUuid());

		assertEquals(postStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);

		// Nonexistent asset library ID

		long assetLibraryId = RandomTestUtil.randomLong();

		try {
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCode(
					assetLibraryId,
					postStructuredContent.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}

		// Nonexistent external reference code

		String externalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		try {
			structuredContentResource.
				getAssetLibraryStructuredContentByExternalReferenceCode(
					testGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					externalReferenceCode);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testGetContentStructureStructuredContentsPage()
		throws Exception {

		super.testGetContentStructureStructuredContentsPage();

		_testGetContentStructureStructuredContentsPageLocalizedWithFilter();
		_testGetContentStructureStructuredContentsPageUnlocalizedWithFilter();
		_testGetContentStructureStructuredContentsPageWithSortDateTimeField();
	}

	@Override
	@Test
	public void testGetSiteStructuredContentsPage() throws Exception {
		super.testGetSiteStructuredContentsPage();

		_testGetSiteStructuredContentsPageByDefaultPriority();
		_testGetSiteStructuredContentsPageByGivenPriority();
		_testGetSiteStructuredContentsPageFilteredByDateField();
		_testGetSiteStructuredContentsPageOrderedByDescendingPriority();
	}

	@Override
	@Test
	public void testGetSiteStructuredContentsPageWithSortInteger()
		throws Exception {

		super.testGetSiteStructuredContentsPageWithSortInteger();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), testGroup.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), testGroup.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		Assert.assertTrue(
			assetCategory1.getCategoryId() < assetCategory2.getCategoryId());

		StructuredContent structuredContent1 = randomStructuredContent();

		structuredContent1.setTaxonomyCategoryIds(
			new Long[] {assetCategory2.getCategoryId()});

		structuredContent1 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				testGroup.getGroupId(), structuredContent1);

		StructuredContent structuredContent2 = randomStructuredContent();

		structuredContent2.setTaxonomyCategoryIds(
			new Long[] {assetCategory1.getCategoryId()});

		structuredContent2 =
			testGetSiteStructuredContentsPage_addStructuredContent(
				testGroup.getGroupId(), structuredContent2);

		Page<StructuredContent> page =
			structuredContentResource.getSiteStructuredContentsPage(
				testGroup.getGroupId(), null, null, null,
				String.format(
					"taxonomyCategoryIds/any(k:k in (%d,%d))",
					assetCategory1.getCategoryId(),
					assetCategory2.getCategoryId()),
				null, "taxonomyCategoryIds:asc");

		assertEquals(
			Arrays.asList(structuredContent2, structuredContent1),
			(List<StructuredContent>)page.getItems());

		page = structuredContentResource.getSiteStructuredContentsPage(
			testGroup.getGroupId(), null, null, null,
			String.format(
				"taxonomyCategoryIds/any(k:k in (%d,%d))",
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()),
			null, "taxonomyCategoryIds:desc");

		assertEquals(
			Arrays.asList(structuredContent1, structuredContent2),
			(List<StructuredContent>)page.getItems());
	}

	@Override
	@Test
	public void testGetStructuredContent() throws Exception {

		// Get structured content

		super.testGetStructuredContent();

		_testGetStructuredContentAssetLibrary();
		_testGetStructuredContentWithAllTypesOfContentFields(false);
		_testGetStructuredContentWithAllTypesOfContentFields(true);
		_testGetStructuredContentWithAllTypesOfContentFieldsAndAcceptAllLanguagesHeader(
			false);
		_testGetStructuredContentWithAllTypesOfContentFieldsAndAcceptAllLanguagesHeader(
			true);
		_testGetStructuredContentWithArticleFieldWithDifferentLocale();
		_testGetStructuredContentWithDataDefinitionEmptyDefaultValue();
		_testGetStructuredContentWithDateExpired();
		_testGetStructuredContentWithDateExpiredNeverExpire();
		_testGetStructuredContentWithDifferentFolder();
		_testGetStructuredContentWithDifferentLocale();
		_testGetStructuredContentWithDifferentTimeZone();
		_testGetStructuredContentWithImageContentURL();
		_testGetStructuredContentWithInvalidImage();
		_testGetStructuredContentWithRadioField();
		_testGetStructuredContentWithRoleAdministrator();
		_testGetStructuredContentWithRoleOwner();
		_testGetStructuredContentWithRoleRegularUser();
	}

	@Override
	@Test
	public void testGetStructuredContentRenderedContentByDisplayPageDisplayPageKey()
		throws Exception {
	}

	@Override
	@Test
	public void testGetStructuredContentRenderedContentContentTemplate()
		throws Exception {

		StructuredContent structuredContent =
			testGetSiteStructuredContentByKey_addStructuredContent();

		ContentField[] contentFields = structuredContent.getContentFields();

		ContentFieldValue contentFieldValue =
			contentFields[0].getContentFieldValue();

		Assert.assertEquals(
			"<div>" + contentFieldValue.getData() + "</div>",
			structuredContentResource.
				getStructuredContentRenderedContentContentTemplate(
					structuredContent.getId(), _ddmTemplate.getTemplateKey()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteStructuredContentMyRating() throws Exception {
		super.testGraphQLDeleteStructuredContentMyRating();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostStructuredContentFolderStructuredContent()
		throws Exception {

		super.testGraphQLPostStructuredContentFolderStructuredContent();
	}

	@Override
	@Test
	public void testPatchStructuredContent() throws Exception {
		super.testPatchStructuredContent();

		_testPatchStructuredContentWithDateExpired();
		_testPatchStructuredContentWithRandomTitle();
	}

	@Override
	@Test
	public void testPostAssetLibraryStructuredContent() throws Exception {
		super.testPostAssetLibraryStructuredContent();

		// Default external reference code and UUID

		StructuredContent randomStructuredContent1 = randomStructuredContent();

		randomStructuredContent1.setExternalReferenceCode("");
		randomStructuredContent1.setUuid("");

		StructuredContent postStructuredContent1 =
			testPostAssetLibraryStructuredContent_addStructuredContent(
				randomStructuredContent1);

		Assert.assertNotNull(postStructuredContent1.getExternalReferenceCode());
		Assert.assertNotNull(postStructuredContent1.getUuid());
		Assert.assertEquals(
			postStructuredContent1.getExternalReferenceCode(),
			postStructuredContent1.getUuid());
		assertValid(postStructuredContent1);

		// External reference code

		_testPostAssetLibraryStructuredContent(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		StructuredContent postStructuredContent2 =
			testPostAssetLibraryStructuredContent_addStructuredContent(
				randomStructuredContent());

		_testPostAssetLibraryStructuredContent(
			String.valueOf(postStructuredContent2.getId()));

		// Duplicate external reference code

		StructuredContent postStructuredContent3 =
			testPostAssetLibraryStructuredContent_addStructuredContent(
				randomStructuredContent());

		StructuredContent randomStructuredContent2 = randomStructuredContent();

		randomStructuredContent2.setContentStructureId(
			_depotDDMStructure.getStructureId());
		randomStructuredContent2.setExternalReferenceCode(
			postStructuredContent3.getExternalReferenceCode());

		try {
			structuredContentResource.postAssetLibraryStructuredContent(
				testDepotEntry.getDepotEntryId(), randomStructuredContent2);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				_language.get(
					LocaleUtil.getDefault(),
					"this-external-reference-code-is-already-in-use"),
				problem.getTitle());
		}
	}

	@Override
	@Test
	public void testPostSiteStructuredContent() throws Exception {
		super.testPostSiteStructuredContent();

		// Localized structured content with the default language

		Locale locale = LocaleUtil.getDefault();

		StructuredContent randomLocalizedStructuredContent1 =
			_randomStructuredContent(locale);

		StructuredContentResource englishStructuredContentResource =
			_buildStructureContentResource(locale);

		StructuredContent postStructuredContent1 =
			englishStructuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				randomLocalizedStructuredContent1);

		_assertLocalizedValues(
			postStructuredContent1, LocaleUtil.toW3cLanguageId(locale));
		assertEquals(randomLocalizedStructuredContent1, postStructuredContent1);
		assertValid(postStructuredContent1);

		// Localized structured content with a different language from the
		// default language

		locale = LocaleUtil.fromLanguageId("es-ES");

		StructuredContent randomLocalizedStructuredContent2 =
			_randomStructuredContent(locale);

		StructuredContentResource spanishStructuredContentResource =
			_buildStructureContentResource(locale);

		StructuredContent postStructuredContent2 =
			spanishStructuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				randomLocalizedStructuredContent2);

		_assertLocalizedValues(
			postStructuredContent2, LocaleUtil.toW3cLanguageId(locale));
		assertEquals(randomLocalizedStructuredContent2, postStructuredContent2);
		assertValid(postStructuredContent2);

		// Structured content with the default priority

		locale = LocaleUtil.getDefault();

		StructuredContent randomStructuredContent = _randomStructuredContent(
			locale);

		StructuredContentResource structuredContentResource =
			_buildStructureContentResource(locale);

		randomStructuredContent.setPriority((Double)null);

		StructuredContent postStructuredContent3 =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				randomStructuredContent);

		Assert.assertEquals(
			Double.valueOf(0.0), postStructuredContent3.getPriority());
		assertValid(postStructuredContent3);

		_testPostSiteStructuredContentBatch();
	}

	@Override
	@Test
	public void testPostStructuredContentFolderStructuredContent()
		throws Exception {

		super.testPostStructuredContentFolderStructuredContent();

		_testPostStructuredContentFolderStructuredContentWithDisplayPageTemplate();
		_testPostStructuredContentFolderStructuredContentWithImageContentField();
	}

	@Override
	@Test
	public void testPutAssetLibraryStructuredContentByExternalReferenceCode()
		throws Exception {

		_useDepotDDMStructureStructureId = true;

		super.testPutAssetLibraryStructuredContentByExternalReferenceCode();

		// Different external reference code in payload

		StructuredContent randomStructuredContent1 = randomStructuredContent();

		StructuredContent putStructuredContent1 =
			structuredContentResource.
				putAssetLibraryStructuredContentByExternalReferenceCode(
					testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					randomStructuredContent1.getExternalReferenceCode(),
					randomStructuredContent1);

		StructuredContent randomStructuredContent2 =
			testPutAssetLibraryStructuredContentByExternalReferenceCode_createStructuredContent();

		String externalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		randomStructuredContent2.setTitle(putStructuredContent1.getTitle());

		StructuredContent putStructuredContent2 =
			structuredContentResource.
				putAssetLibraryStructuredContentByExternalReferenceCode(
					testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					externalReferenceCode, randomStructuredContent2);

		Assert.assertNotEquals(
			putStructuredContent1.getId(), putStructuredContent2.getId());
		Assert.assertEquals(
			putStructuredContent1.getTitle(), putStructuredContent2.getTitle());
		Assert.assertEquals(
			externalReferenceCode,
			putStructuredContent2.getExternalReferenceCode());

		StructuredContent randomStructuredContent3 = randomStructuredContent();

		StructuredContent putStructuredContent3 =
			structuredContentResource.
				putAssetLibraryStructuredContentByExternalReferenceCode(
					testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					randomStructuredContent3.getExternalReferenceCode(),
					randomStructuredContent3);

		assertEquals(
			putStructuredContent3,
			structuredContentResource.
				putAssetLibraryStructuredContentByExternalReferenceCode(
					testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId(),
					putStructuredContent3.getExternalReferenceCode(),
					randomStructuredContent3));
	}

	@Override
	@Test
	public void testPutSiteStructuredContentByExternalReferenceCode()
		throws Exception {

		super.testPutSiteStructuredContentByExternalReferenceCode();

		_testPutSiteStructuredContentByExternalReferenceCodeWithCustomField();
	}

	@Override
	@Test
	public void testPutStructuredContent() throws Exception {
		super.testPutStructuredContent();

		_testPutStructuredContent(false);
		_testPutStructuredContent(true);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"contentStructureId", "description", "priority", "title"
		};
	}

	@Override
	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = super.getGraphQLFields();

		graphQLFields.add(new GraphQLField("key"));
		graphQLFields.add(new GraphQLField("uuid"));

		return graphQLFields;
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"assetLibraryId", "contentStructureId", "creatorId", "siteId"
		};
	}

	@Override
	protected StructuredContent randomIrrelevantStructuredContent()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setContentStructureId(
			_irrelevantDDMStructure.getStructureId());

		return structuredContent;
	}

	@Override
	protected StructuredContent randomStructuredContent() throws Exception {
		return _randomStructuredContent(RandomTestUtil.randomString(10));
	}

	@Override
	protected StructuredContent
			testDeleteAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setContentStructureId(
			_depotDDMStructure.getStructureId());

		return structuredContentResource.postAssetLibraryStructuredContent(
			testDepotEntry.getDepotEntryId(), structuredContent);
	}

	@Override
	protected StructuredContent
			testDeleteStructuredContentMyRating_addStructuredContent()
		throws Exception {

		StructuredContent structuredContent =
			super.testDeleteStructuredContentMyRating_addStructuredContent();

		structuredContentResource.putStructuredContentMyRatingHttpResponse(
			structuredContent.getId(), randomRating());

		return structuredContent;
	}

	@Override
	protected StructuredContent
			testGetAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return testPostAssetLibraryStructuredContent_addStructuredContent(
			randomStructuredContent());
	}

	@Override
	protected Long
			testGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected StructuredContent
			testGetAssetLibraryStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		return testPostAssetLibraryStructuredContent_addStructuredContent(
			randomStructuredContent());
	}

	@Override
	protected StructuredContent
			testGetAssetLibraryStructuredContentsPage_addStructuredContent(
				Long assetLibraryId, StructuredContent structuredContent)
		throws Exception {

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.getDepotEntry(
			assetLibraryId);

		List<DDMStructure> ddmStructures =
			DDMStructureLocalServiceUtil.getStructures(depotEntry.getGroupId());

		structuredContent.setContentStructureId(
			ddmStructures.get(
				0
			).getStructureId());

		return structuredContentResource.postAssetLibraryStructuredContent(
			assetLibraryId, structuredContent);
	}

	@Override
	protected StructuredContent
			testGetContentStructureStructuredContentsPage_addStructuredContent(
				Long contentStructureId, StructuredContent structuredContent)
		throws Exception {

		return structuredContentResource.postSiteStructuredContent(
			testGroup.getGroupId(), structuredContent);
	}

	@Override
	protected Long
		testGetContentStructureStructuredContentsPage_getContentStructureId() {

		return _ddmStructure.getStructureId();
	}

	@Override
	protected Long
		testGetStructuredContentFolderStructuredContentsPage_getIrrelevantStructuredContentFolderId() {

		return _irrelevantJournalFolder.getFolderId();
	}

	@Override
	protected Long
		testGetStructuredContentFolderStructuredContentsPage_getStructuredContentFolderId() {

		return _journalFolder.getFolderId();
	}

	@Override
	protected StructuredContent
			testGraphQLAssetLibraryStructuredContent_addStructuredContent(
				Long assetLibraryId, StructuredContent structuredContent)
		throws Exception {

		structuredContent.setContentStructureId(
			_depotDDMStructure.getStructureId());

		return structuredContentResource.postAssetLibraryStructuredContent(
			assetLibraryId, structuredContent);
	}

	@Override
	protected StructuredContent
			testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		_useDepotDDMStructureStructureId = true;

		return testPostAssetLibraryStructuredContent_addStructuredContent(
			randomStructuredContent());
	}

	@Override
	protected Long
			testGraphQLGetAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected StructuredContent
			testGraphQLStructuredContent_addStructuredContent()
		throws Exception {

		return testPostSiteStructuredContent_addStructuredContent(
			randomStructuredContent());
	}

	@Override
	protected StructuredContent
			testPostAssetLibraryStructuredContent_addStructuredContent(
				StructuredContent structuredContent)
		throws Exception {

		structuredContent.setContentStructureId(
			_depotDDMStructure.getStructureId());

		return super.testPostAssetLibraryStructuredContent_addStructuredContent(
			structuredContent);
	}

	@Override
	protected StructuredContent
			testPutAssetLibraryStructuredContentByExternalReferenceCode_addStructuredContent()
		throws Exception {

		return testPostAssetLibraryStructuredContent_addStructuredContent(
			randomStructuredContent());
	}

	@Override
	protected Long
			testPutAssetLibraryStructuredContentByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Override
	protected StructuredContent
			testPutAssetLibraryStructuredContentPermissionsPage_addStructuredContent()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setContentStructureId(
			_depotDDMStructure.getStructureId());

		return structuredContentResource.postAssetLibraryStructuredContent(
			testDepotEntry.getDepotEntryId(), structuredContent);
	}

	private DDMStructure _addDDMStructure(Group group, String fileName)
		throws Exception {

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(JournalArticle.class), group);

		return ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_deserialize(_read(fileName)), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT);
	}

	private DDMTemplate _addDDMTemplate(DDMStructure ddmStructure)
		throws Exception {

		return DDMTemplateTestUtil.addTemplate(
			ddmStructure.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_VM,
			_read("test-structured-content-template.vm"), LocaleUtil.US);
	}

	private ExpandoColumn _addExpandoColumn(
			Object defaultData, String displayType, ExpandoTable expandoTable,
			int type)
		throws Exception {

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, "A" + RandomTestUtil.randomString(), type,
			defaultData);

		if (displayType != null) {
			UnicodeProperties unicodeProperties =
				expandoColumn.getTypeSettingsProperties();

			unicodeProperties.putAll(
				HashMapBuilder.put(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE, displayType
				).build());

			expandoColumn.setTypeSettingsProperties(unicodeProperties);
		}

		return _expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	private void _assertFilterSiteStructuredContentsPageFilteredByDateField(
			Locale locale)
		throws Exception {

		Locale currentLocale = LocaleThreadLocal.getDefaultLocale();

		try {
			LocaleThreadLocal.setSiteDefaultLocale(locale);

			DDMFormField ddmFormField = _createDDMFormField(
				false, "Date", DDMFormFieldTypeConstants.DATE);

			String dateString = DateUtil.getDate(
				new Date(), "yyyy-MM-dd", LocaleUtil.US);

			JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
				_dataDefinitionResourceFactory, ddmFormField,
				_ddmFormValuesToFieldsConverter, locale, dateString,
				testGroup.getGroupId(), _journalConverter);

			DDMStructure ddmStructure = journalArticle.getDDMStructure();

			Page<StructuredContent> page =
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						ddmStructure.getStructureId(), null, null,
						"contentFields/Date eq " + dateString,
						Pagination.of(1, 10), null);

			Assert.assertNotNull(page);

			List<StructuredContent> items =
				(List<StructuredContent>)page.getItems();

			Assert.assertEquals(items.toString(), 1, items.size());

			StructuredContent structuredContent = items.get(0);

			Assert.assertEquals(
				String.valueOf(journalArticle.getResourcePrimKey()),
				String.valueOf(structuredContent.getId()));

			structuredContentResource.deleteStructuredContent(
				structuredContent.getId());
		}
		finally {
			LocaleThreadLocal.setSiteDefaultLocale(currentLocale);
		}
	}

	private void _assertLocalizedValue(
		Map<String, String> localizedValues, String value, String w3cLanguageId,
		Set<String> w3cLanguageIds) {

		Assert.assertEquals(w3cLanguageIds, localizedValues.keySet());
		Assert.assertEquals(value, localizedValues.get(w3cLanguageId));
	}

	private void _assertLocalizedValues(
		StructuredContent structuredContent, String w3cLanguageId) {

		Set<String> w3cLanguageIds = SetUtil.fromArray("es-ES", "en-US");

		Assert.assertEquals(
			w3cLanguageIds,
			SetUtil.fromArray(structuredContent.getAvailableLanguages()));

		_assertLocalizedValue(
			structuredContent.getDescription_i18n(),
			structuredContent.getDescription(), w3cLanguageId, w3cLanguageIds);
		_assertLocalizedValue(
			structuredContent.getTitle_i18n(), structuredContent.getTitle(),
			w3cLanguageId, w3cLanguageIds);
		_assertLocalizedValue(
			structuredContent.getFriendlyUrlPath_i18n(),
			structuredContent.getFriendlyUrlPath(), w3cLanguageId,
			w3cLanguageIds);
		_assertLocalizedValue(
			structuredContent.getDescription_i18n(),
			structuredContent.getDescription(), w3cLanguageId, w3cLanguageIds);

		for (ContentField contentField : structuredContent.getContentFields()) {
			Map<String, ContentFieldValue> contentFieldValue_i18n =
				contentField.getContentFieldValue_i18n();

			Assert.assertEquals(
				w3cLanguageIds, contentFieldValue_i18n.keySet());
		}
	}

	private StructuredContentResource _buildStructureContentResource(
		Locale locale) {

		StructuredContentResource.Builder builder =
			StructuredContentResource.builder();

		return builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			locale
		).header(
			"X-Accept-All-Languages", "true"
		).build();
	}

	private DDMFormField _createDDMFormField(
		boolean localizable, String name, String type) {

		DDMFormField ddmFormField = new DDMFormField(name, type);

		ddmFormField.setDataType(type);
		ddmFormField.setIndexType("text");

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(
			LocaleUtil.US, RandomTestUtil.randomString(10));

		ddmFormField.setLabel(localizedValue);

		ddmFormField.setLocalizable(localizable);

		return ddmFormField;
	}

	private DDMForm _deserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private boolean _equals(
		StructuredContent structuredContent1,
		StructuredContent structuredContent2) {

		assertEquals(structuredContent1, structuredContent2);

		if (!Objects.deepEquals(
				structuredContent1.getDescription(),
				structuredContent2.getDescription()) ||
			!equals(
				(Map)structuredContent1.getDescription_i18n(),
				(Map)structuredContent2.getDescription_i18n()) ||
			!Objects.deepEquals(
				structuredContent1.getFriendlyUrlPath(),
				structuredContent2.getFriendlyUrlPath()) ||
			!equals(
				(Map)structuredContent1.getFriendlyUrlPath_i18n(),
				(Map)structuredContent2.getFriendlyUrlPath_i18n()) ||
			!Objects.deepEquals(
				structuredContent1.getTitle(), structuredContent2.getTitle()) ||
			!equals(
				(Map)structuredContent1.getTitle_i18n(),
				(Map)structuredContent2.getTitle_i18n())) {

			return false;
		}

		ContentField[] contentFields1 = structuredContent1.getContentFields();
		ContentField[] contentFields2 = structuredContent1.getContentFields();

		if (contentFields1.length != contentFields2.length) {
			return false;
		}

		for (int i = 0; i < contentFields1.length; i++) {
			ContentField contentField1 = contentFields1[i];
			ContentField contentField2 = contentFields2[i];

			if (!Objects.equals(
					contentField1.getName(), contentField2.getName()) ||
				!Objects.equals(
					contentField1.getContentFieldValue(),
					contentField2.getContentFieldValue()) ||
				!equals(
					(Map)contentField1.getContentFieldValue_i18n(),
					(Map)contentField2.getContentFieldValue_i18n())) {

				return false;
			}
		}

		return true;
	}

	private CustomField _getCustomField(
		CustomField[] customFields, String name) {

		for (CustomField customField : customFields) {
			if (Objects.equals(customField.getName(), name)) {
				return customField;
			}
		}

		return null;
	}

	private Date _getDate(int year) {
		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.add(Calendar.YEAR, year);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	private String _randomColor() {
		return String.format(
			"#%02d%02d%02d", RandomTestUtil.randomInt(0, 100),
			RandomTestUtil.randomInt(0, 100), RandomTestUtil.randomInt(0, 100));
	}

	private StructuredContent _randomCompleteStructuredContent(
			long dlFileEntryId, boolean localizable)
		throws Exception {

		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				PortalUtil.getClassNameId(JournalArticle.class), testGroup);

		DDMStructure complexDDMStructure = ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_deserialize(
				StringUtil.replace(
					_read("test-complex-ddm-structure.json"), "\"[#", "#]\"",
					HashMapBuilder.put(
						"LOCALIZABLE", String.valueOf(localizable)
					).build())),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).put(
			LocaleUtil.FRANCE, _JOURNAL_ARTICLE_TITLE_FR
		).build();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(), _journalFolder.getFolderId(),
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, titleMap, null,
			titleMap, LocaleUtil.getSiteDefault(), false, true,
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				TestPropsValues.getUserId()));

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setContentFields(
			_randomContentFields(dlFileEntryId, journalArticle, localizable));
		structuredContent.setContentStructureId(
			complexDDMStructure.getStructureId());
		structuredContent.setRelatedContents(
			new RelatedContent[] {
				new RelatedContent() {
					{
						contentType = "BlogPosting";
						id = _blogsEntry.getEntryId();
					}
				}
			});
		structuredContent.setStructuredContentFolderId(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return structuredContent;
	}

	private ContentField[] _randomContentFields(
		long dlFileEntryId, JournalArticle journalArticle,
		boolean localizable) {

		return new ContentField[] {
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data = RandomTestUtil.randomString(10);
						}
					};
					name = "Text";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data = _COMPLETE_STRUCTURED_CONTENT_OPTIONS
								[RandomTestUtil.randomInt(0, 2)];

							setValue(
								() -> {
									if (localizable) {
										return null;
									}

									return _COMPLETE_STRUCTURED_CONTENT_OPTIONS
										[RandomTestUtil.randomInt(0, 2)];
								});
						}
					};
					name = "SelectFromList";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data = _COMPLETE_STRUCTURED_CONTENT_OPTIONS
								[RandomTestUtil.randomInt(0, 2)];

							setValue(
								() -> {
									if (localizable) {
										return null;
									}

									return _COMPLETE_STRUCTURED_CONTENT_OPTIONS
										[RandomTestUtil.randomInt(0, 2)];
								});
						}
					};
					name = "SingleSelection";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data =
								"[" +
									_COMPLETE_STRUCTURED_CONTENT_OPTIONS
										[RandomTestUtil.randomInt(0, 2)] + "]";

							setValue(
								() -> {
									if (localizable) {
										return null;
									}

									return "[" +
										_COMPLETE_STRUCTURED_CONTENT_OPTIONS
											[RandomTestUtil.randomInt(0, 2)] +
												"]";
								});
						}
					};
					name = "MultipleSelection";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data = _randomGrid();
						}
					};
					name = "Grid";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data = _randomDate();
						}
					};
					name = "Date";
				}
			},
			new ContentField() {
				{
					name = "Fieldset";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data = String.valueOf(RandomTestUtil.randomInt());
						}
					};
					name = "Numeric";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							image = new ContentDocument() {
								{
									id = dlFileEntryId;
								}
							};
						}
					};
					name = "Image";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data = RandomTestUtil.randomString(500);
						}
					};
					name = "RichText";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							document = new ContentDocument() {
								{
									id = _dlFileEntry.getFileEntryId();
								}
							};
						}
					};
					name = "Upload";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							data = _randomColor();
						}
					};
					name = "Color";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							structuredContentLink =
								new StructuredContentLink() {
									{
										id =
											journalArticle.getResourcePrimKey();
									}
								};
						}
					};
					name = "WebContent";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							geo = new Geo() {
								{
									latitude = RandomTestUtil.randomDouble();
									longitude = RandomTestUtil.randomDouble();
								}
							};
						}
					};
					name = "Geolocation";
				}
			},
			new ContentField() {
				{
					contentFieldValue = new ContentFieldValue() {
						{
							link = _layout.getFriendlyURL();
						}
					};
					name = "LinkToPage";
				}
			}
		};
	}

	private String _randomDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		return simpleDateFormat.format(new Date());
	}

	private String _randomGrid() {
		return StringBundler.concat(
			"{", _COMPLETE_STRUCTURED_CONTENT_OPTIONS[0], ":",
			_COMPLETE_STRUCTURED_CONTENT_OPTIONS
				[RandomTestUtil.randomInt(0, 2)],
			",", _COMPLETE_STRUCTURED_CONTENT_OPTIONS[1], ":",
			_COMPLETE_STRUCTURED_CONTENT_OPTIONS
				[RandomTestUtil.randomInt(0, 2)],
			",", _COMPLETE_STRUCTURED_CONTENT_OPTIONS[2], ":",
			_COMPLETE_STRUCTURED_CONTENT_OPTIONS
				[RandomTestUtil.randomInt(0, 2)],
			"}");
	}

	private StructuredContent _randomStructuredContent(Locale locale)
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		String w3cLanguageId = LocaleUtil.toW3cLanguageId(locale);

		Map<String, ContentFieldValue> contentFieldValues = HashMapBuilder.put(
			"en-US",
			(ContentFieldValue)new ContentFieldValue() {

				{
					data = RandomTestUtil.randomString(10);
				}
			}
		).put(
			"es-ES",
			(ContentFieldValue)new ContentFieldValue() {

				{
					data = RandomTestUtil.randomString(10);
				}
			}
		).build();

		structuredContent.setContentFields(
			new ContentField[] {
				new ContentField() {
					{
						contentFieldValue = contentFieldValues.get(
							w3cLanguageId);
						contentFieldValue_i18n = contentFieldValues;
						name = "MyText";
					}
				}
			});

		structuredContent.setContentStructureId(
			_localizedDDMStructure.getStructureId());

		Map<String, String> description_i18n = HashMapBuilder.put(
			"en-US", RandomTestUtil.randomString()
		).put(
			"es-ES", RandomTestUtil.randomString()
		).build();

		structuredContent.setDescription(description_i18n.get(w3cLanguageId));
		structuredContent.setDescription_i18n(description_i18n);

		Map<String, String> friendlyUrlPath_i18n = HashMapBuilder.put(
			"en-US", StringUtil.toLowerCase(RandomTestUtil.randomString())
		).put(
			"es-ES", StringUtil.toLowerCase(RandomTestUtil.randomString())
		).build();

		structuredContent.setFriendlyUrlPath(
			friendlyUrlPath_i18n.get(w3cLanguageId));
		structuredContent.setFriendlyUrlPath_i18n(friendlyUrlPath_i18n);

		structuredContent.setRelatedContents(
			new RelatedContent[] {
				new RelatedContent() {
					{
						contentType = "BlogPosting";
						id = _blogsEntry.getEntryId();
					}
				}
			});
		structuredContent.setStructuredContentFolderId(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Map<String, String> title_i18n = HashMapBuilder.put(
			"en-US", RandomTestUtil.randomString()
		).put(
			"es-ES", RandomTestUtil.randomString()
		).build();

		structuredContent.setTitle(title_i18n.get(w3cLanguageId));
		structuredContent.setTitle_i18n(title_i18n);

		return structuredContent;
	}

	private StructuredContent _randomStructuredContent(
			String contentFieldValueData)
		throws Exception {

		StructuredContent structuredContent = super.randomStructuredContent();

		structuredContent.setContentFields(
			new ContentField[] {
				new ContentField() {
					{
						contentFieldValue = new ContentFieldValue() {
							{
								data = contentFieldValueData;
							}
						};
						name = "Foo";
					}
				}
			});
		structuredContent.setContentStructureId(
			_useDepotDDMStructureStructureId ?
				_depotDDMStructure.getStructureId() :
					_ddmStructure.getStructureId());
		structuredContent.setDateExpired(() -> null);
		structuredContent.setNeverExpire(true);
		structuredContent.setRelatedContents(
			new RelatedContent[] {
				new RelatedContent() {
					{
						contentType = "BlogPosting";
						id = _blogsEntry.getEntryId();
					}
				}
			});
		structuredContent.setStructuredContentFolderId(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		return structuredContent;
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private void _testGetContentStructureStructuredContentsPageLocalizedWithFilter()
		throws Exception {

		Long contentStructureId =
			testGetContentStructureStructuredContentsPage_getContentStructureId();

		StructuredContent structuredContent1 = _randomStructuredContent(
			"first second");
		StructuredContent structuredContent2 = _randomStructuredContent(
			"second");

		testGetContentStructureStructuredContentsPage_addStructuredContent(
			contentStructureId, structuredContent1);
		testGetContentStructureStructuredContentsPage_addStructuredContent(
			contentStructureId, structuredContent2);

		Page<StructuredContent> page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null, "contentFields/Foo eq 'second'",
				Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		List<StructuredContent> items =
			(List<StructuredContent>)page.getItems();

		assertEquals(structuredContent2, items.get(0));

		page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null,
				"contains(contentFields/Foo,'first')", Pagination.of(1, 10),
				null);

		Assert.assertEquals(1, page.getTotalCount());

		items = (List<StructuredContent>)page.getItems();

		assertEquals(structuredContent1, items.get(0));

		page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null,
				"contains(contentFields/Foo,'second')", Pagination.of(1, 10),
				null);

		Assert.assertEquals(2, page.getTotalCount());

		items = (List<StructuredContent>)page.getItems();

		assertEqualsIgnoringOrder(
			Arrays.asList(structuredContent1, structuredContent2), items);
	}

	private void _testGetContentStructureStructuredContentsPageUnlocalizedWithFilter()
		throws Exception {

		Long contentStructureId = _unlocalizedDDMStructure.getStructureId();

		StructuredContent structuredContent1 = _randomStructuredContent(
			"first second");

		structuredContent1.setContentStructureId(contentStructureId);

		StructuredContent structuredContent2 = _randomStructuredContent(
			"second");

		structuredContent2.setContentStructureId(contentStructureId);

		testGetContentStructureStructuredContentsPage_addStructuredContent(
			contentStructureId, structuredContent1);
		testGetContentStructureStructuredContentsPage_addStructuredContent(
			contentStructureId, structuredContent2);

		Page<StructuredContent> page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null, "contentFields/Foo eq 'second'",
				Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		List<StructuredContent> items =
			(List<StructuredContent>)page.getItems();

		assertEquals(structuredContent2, items.get(0));

		page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				contentStructureId, null, null,
				"contains(contentFields/Foo,'first')", Pagination.of(1, 10),
				null);

		Assert.assertEquals(1, page.getTotalCount());

		items = (List<StructuredContent>)page.getItems();

		assertEquals(structuredContent1, items.get(0));
	}

	private void _testGetContentStructureStructuredContentsPageWithSortDateTimeField()
		throws Exception {

		DDMStructure ddmStructure = _addDDMStructure(
			testGroup, "test-ddm-structure-datetime.json");

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0,
				_read("test-journal-article-datetime.xml"),
				ddmStructure.getStructureKey(), null, LocaleUtil.US, null,
				ServiceContextTestUtil.getServiceContext(
					testCompany.getCompanyId(), testGroup.getGroupId(),
					TestPropsValues.getUserId()));

		Page<StructuredContent> page =
			structuredContentResource.getContentStructureStructuredContentsPage(
				ddmStructure.getStructureId(), null, null, null,
				Pagination.of(1, 10), "contentFields/DateTime90775749:asc'");

		Assert.assertNotNull(page);

		List<StructuredContent> items =
			(List<StructuredContent>)page.getItems();

		Assert.assertEquals(items.toString(), 1, items.size());

		StructuredContent structuredContent = items.get(0);

		Assert.assertEquals(
			String.valueOf(journalArticle.getResourcePrimKey()),
			String.valueOf(structuredContent.getId()));

		structuredContentResource.deleteStructuredContent(
			structuredContent.getId());
	}

	private void _testGetSiteStructuredContentsPageByDefaultPriority()
		throws Exception {

		StructuredContent irrelevantStructuredContent =
			randomIrrelevantStructuredContent();

		StructuredContent irrelevantPostStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getIrrelevantSiteId(),
				irrelevantStructuredContent);

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setPriority((Double)null);

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				structuredContent);

		Page<StructuredContent> page =
			structuredContentResource.getSiteStructuredContentsPage(
				testGetSiteStructuredContentsPage_getSiteId(), true, null, null,
				"priority eq 0.0", Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(postStructuredContent),
			(List<StructuredContent>)page.getItems());

		assertValid(page);

		structuredContentResource.deleteStructuredContent(
			irrelevantPostStructuredContent.getId());
		structuredContentResource.deleteStructuredContent(
			postStructuredContent.getId());
	}

	private void _testGetSiteStructuredContentsPageByGivenPriority()
		throws Exception {

		StructuredContent irrelevantStructuredContent =
			randomIrrelevantStructuredContent();

		StructuredContent irrelevantPostStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getIrrelevantSiteId(),
				irrelevantStructuredContent);

		StructuredContent structuredContent = randomStructuredContent();

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				structuredContent);

		StructuredContent patchStructuredContent =
			structuredContentResource.patchStructuredContent(
				postStructuredContent.getId(),
				new StructuredContent() {
					{
						priority = Double.valueOf(1.3);
					}
				});

		Page<StructuredContent> page =
			structuredContentResource.getSiteStructuredContentsPage(
				testGetSiteStructuredContentsPage_getSiteId(), true, null, null,
				"priority eq 1.3", Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertEqualsIgnoringOrder(
			Arrays.asList(patchStructuredContent),
			(List<StructuredContent>)page.getItems());

		assertValid(page);

		structuredContentResource.deleteStructuredContent(
			irrelevantPostStructuredContent.getId());
		structuredContentResource.deleteStructuredContent(
			postStructuredContent.getId());
	}

	private void _testGetSiteStructuredContentsPageFilteredByDateField()
		throws Exception {

		_assertFilterSiteStructuredContentsPageFilteredByDateField(
			LocaleUtil.fromLanguageId("ar_SA"));
		_assertFilterSiteStructuredContentsPageFilteredByDateField(
			LocaleUtil.US);
	}

	private void _testGetSiteStructuredContentsPageOrderedByDescendingPriority()
		throws Exception {

		StructuredContent irrelevantStructuredContent =
			randomIrrelevantStructuredContent();

		StructuredContent irrelevantPostStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getIrrelevantSiteId(),
				irrelevantStructuredContent);

		StructuredContent structuredContent1 = randomStructuredContent();

		structuredContent1.setPriority(Double.valueOf(1.2));

		StructuredContent postStructuredContent1 =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				structuredContent1);

		StructuredContent structuredContent2 = randomStructuredContent();

		structuredContent2.setPriority(Double.valueOf(1.1));

		StructuredContent postStructuredContent2 =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				structuredContent2);

		StructuredContent structuredContent3 = randomStructuredContent();

		structuredContent3.setPriority(Double.valueOf(1.3));

		StructuredContent postStructuredContent3 =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				structuredContent3);

		Page<StructuredContent> page =
			structuredContentResource.getSiteStructuredContentsPage(
				testGetSiteStructuredContentsPage_getSiteId(), true, null, null,
				null, Pagination.of(1, 10), "priority:desc");

		Assert.assertEquals(3, page.getTotalCount());

		assertEquals(
			Arrays.asList(
				postStructuredContent3, postStructuredContent1,
				postStructuredContent2),
			(List<StructuredContent>)page.getItems());

		assertValid(page);

		structuredContentResource.deleteStructuredContent(
			irrelevantPostStructuredContent.getId());
		structuredContentResource.deleteStructuredContent(
			postStructuredContent1.getId());
		structuredContentResource.deleteStructuredContent(
			postStructuredContent2.getId());
		structuredContentResource.deleteStructuredContent(
			postStructuredContent3.getId());
	}

	private void _testGetStructuredContentAssetLibrary() throws Exception {

		// Get structured content inside folder in asset library

		JournalFolder journalFolder1 = JournalTestUtil.addFolder(
			testDepotEntry.getGroupId(), RandomTestUtil.randomString());

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setContentStructureId(
			_depotDDMStructure.getStructureId());

		StructuredContent postStructuredContent =
			structuredContentResource.
				postStructuredContentFolderStructuredContent(
					journalFolder1.getFolderId(), structuredContent);

		StructuredContent getStructuredContent1 =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Assert.assertEquals(
			journalFolder1.getFolderId(),
			GetterUtil.getLong(
				getStructuredContent1.getStructuredContentFolderId()));

		// Get structured content inside current folder in asset library

		JournalFolder journalFolder2 = JournalTestUtil.addFolder(
			testDepotEntry.getGroupId(), RandomTestUtil.randomString());

		_journalArticleLocalService.moveArticle(
			testDepotEntry.getGroupId(), postStructuredContent.getKey(),
			journalFolder2.getFolderId(),
			ServiceContextTestUtil.getServiceContext(
				testDepotEntry.getGroupId()));

		StructuredContent getStructuredContent2 =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Assert.assertEquals(
			journalFolder2.getFolderId(),
			GetterUtil.getLong(
				getStructuredContent2.getStructuredContentFolderId()));

		// Get structured content inside current subfolder in asset library

		JournalFolder journalFolder3 = JournalTestUtil.addFolder(
			testDepotEntry.getGroupId(), journalFolder2.getFolderId(),
			RandomTestUtil.randomString());

		_journalArticleLocalService.moveArticle(
			testDepotEntry.getGroupId(), postStructuredContent.getKey(),
			journalFolder3.getFolderId(),
			ServiceContextTestUtil.getServiceContext(
				testDepotEntry.getGroupId()));

		StructuredContent getStructuredContent3 =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Assert.assertEquals(
			journalFolder3.getFolderId(),
			GetterUtil.getLong(
				getStructuredContent3.getStructuredContentFolderId()));
	}

	private void _testGetStructuredContentWithAllTypesOfContentFields(
			boolean localizable)
		throws Exception {

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(),
				_randomCompleteStructuredContent(
					_dlFileEntry.getFileEntryId(), localizable));

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		assertEquals(postStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	private void
			_testGetStructuredContentWithAllTypesOfContentFieldsAndAcceptAllLanguagesHeader(
				boolean localizable)
		throws Exception {

		StructuredContentResource acceptAllLanguagesStructuredContentResource =
			_buildStructureContentResource(LocaleUtil.getDefault());

		StructuredContent postStructuredContent =
			acceptAllLanguagesStructuredContentResource.
				postSiteStructuredContent(
					testGroup.getGroupId(),
					_randomCompleteStructuredContent(
						_dlFileEntry.getFileEntryId(), localizable));

		StructuredContent getStructuredContent =
			acceptAllLanguagesStructuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		assertEquals(postStructuredContent, getStructuredContent);
		assertValid(getStructuredContent);
	}

	private void _testGetStructuredContentWithArticleFieldWithDifferentLocale()
		throws Exception {

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(),
				_randomCompleteStructuredContent(
					_dlFileEntry.getFileEntryId(), false));

		StructuredContentResource.Builder builder =
			StructuredContentResource.builder();

		StructuredContentResource frenchStructuredContentResource =
			builder.authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.FRANCE
			).build();

		StructuredContent getStructuredContent =
			frenchStructuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		ContentField articleSelector = null;
		String fieldName = "WebContent";

		for (ContentField contentField :
				getStructuredContent.getContentFields()) {

			if (fieldName.equals(contentField.getName())) {
				articleSelector = contentField;

				break;
			}
		}

		ContentFieldValue articleValue = articleSelector.getContentFieldValue();

		StructuredContentLink structuredContent =
			articleValue.getStructuredContentLink();

		Assert.assertEquals(
			_JOURNAL_ARTICLE_TITLE_FR, structuredContent.getTitle());
	}

	private void _testGetStructuredContentWithDataDefinitionEmptyDefaultValue()
		throws Exception {

		Class<?> clazz = StructuredContentResourceTest.class;

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/test-data-definition.json");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			StringUtil.read(inputStream));

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory,
				testGroup.getGroupId(), jsonObject.toString(),
				TestPropsValues.getUser());

		DDMStructure ddmStructure =
			DDMStructureLocalServiceUtil.getDDMStructure(
				dataDefinition.getId());

		DDMFormValues ddmFormValues = new DDMFormValues(
			ddmStructure.getDDMForm());

		Fields fields = _ddmFormValuesToFieldsConverter.convert(
			ddmStructure, ddmFormValues);

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				testGroup.getGroupId(),
				_journalConverter.getContent(
					ddmStructure, fields, testGroup.getGroupId()),
				ddmStructure.getStructureKey(), null);

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				journalArticle.getResourcePrimKey());

		ContentField[] contentFields = getStructuredContent.getContentFields();

		ContentFieldValue contentFieldValue =
			contentFields[0].getContentFieldValue();

		Assert.assertEquals(StringPool.BLANK, contentFieldValue.getData());
	}

	private void _testGetStructuredContentWithDateExpired() throws Exception {
		StructuredContent structuredContent = randomStructuredContent();

		Date dateExpired = _getDate(1);

		structuredContent.setDateExpired(dateExpired);

		structuredContent.setNeverExpire(false);

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), structuredContent);

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Assert.assertFalse(getStructuredContent.getNeverExpire());
		Assert.assertEquals(dateExpired, getStructuredContent.getDateExpired());
	}

	private void _testGetStructuredContentWithDateExpiredNeverExpire()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setDateExpired(_getDate(1));

		structuredContent.setNeverExpire(true);

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), structuredContent);

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Assert.assertTrue(getStructuredContent.getNeverExpire());
		Assert.assertNull(getStructuredContent.getDateExpired());
	}

	private void _testGetStructuredContentWithDifferentFolder()
		throws Exception {

		StructuredContent postStructuredContent =
			structuredContentResource.
				postStructuredContentFolderStructuredContent(
					_journalFolder.getFolderId(),
					_randomCompleteStructuredContent(
						_dlFileEntry.getFileEntryId(), true));

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Assert.assertEquals(
			_journalFolder.getFolderId(),
			(long)getStructuredContent.getStructuredContentFolderId());
	}

	private void _testGetStructuredContentWithDifferentLocale()
		throws Exception {

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), randomStructuredContent());

		String title = postStructuredContent.getTitle();

		StructuredContentResource.Builder builder =
			StructuredContentResource.builder();

		StructuredContentResource frenchStructuredContentResource =
			builder.authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.FRANCE
			).build();

		String frenchTitle = RandomTestUtil.randomString();

		postStructuredContent.setTitle(frenchTitle);

		frenchStructuredContentResource.putStructuredContent(
			postStructuredContent.getId(), postStructuredContent);

		StructuredContent getStructuredContent =
			frenchStructuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Assert.assertEquals(frenchTitle, getStructuredContent.getTitle());

		getStructuredContent = structuredContentResource.getStructuredContent(
			getStructuredContent.getId());

		Assert.assertEquals(title, getStructuredContent.getTitle());
	}

	private void _testGetStructuredContentWithDifferentTimeZone()
		throws Exception {

		User user = UserTestUtil.addGroupAdminUser(testGroup);

		user.setTimeZoneId("America/Sao_Paulo");

		user = _userLocalService.updateUser(user);

		user = _userLocalService.updatePassword(
			user.getUserId(), PropsValues.DEFAULT_ADMIN_PASSWORD,
			PropsValues.DEFAULT_ADMIN_PASSWORD, false, true);

		try {
			StructuredContent postStructuredContent =
				structuredContentResource.postSiteStructuredContent(
					testGroup.getGroupId(), randomStructuredContent());

			StructuredContentResource.Builder builder =
				StructuredContentResource.builder();

			StructuredContentResource structuredContentResource =
				builder.authentication(
					user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
				).locale(
					LocaleUtil.getDefault()
				).build();

			postStructuredContent.setDatePublished(new Date());

			StructuredContent putStructuredContent =
				structuredContentResource.putStructuredContent(
					postStructuredContent.getId(), postStructuredContent);

			JournalArticle journalArticle =
				_journalArticleLocalService.fetchLatestArticle(
					putStructuredContent.getId());

			Assert.assertEquals(
				journalArticle.getDisplayDate(),
				putStructuredContent.getDatePublished());
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	private void _testGetStructuredContentWithImageContentURL()
		throws Exception {

		DLFolder dlFolder = DLTestUtil.addDLFolder(testGroup.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			dlFileEntry.getFileEntryId());

		String previewURL = _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, "", true, false);

		StructuredContent postStructuredContent =
			structuredContentResource.
				postStructuredContentFolderStructuredContent(
					_journalFolder.getFolderId(),
					_randomCompleteStructuredContent(
						dlFileEntry.getFileEntryId(), true));

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		ContentField[] contentFields = getStructuredContent.getContentFields();

		ContentFieldValue contentFieldValue =
			contentFields[8].getContentFieldValue();

		ContentDocument contentDocument = contentFieldValue.getImage();

		Assert.assertEquals(contentDocument.getContentUrl(), previewURL);
	}

	private void _testGetStructuredContentWithInvalidImage() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(testGroup.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		StructuredContent postStructuredContent =
			structuredContentResource.
				postStructuredContentFolderStructuredContent(
					_journalFolder.getFolderId(),
					_randomCompleteStructuredContent(
						dlFileEntry.getFileEntryId(), true));

		DLFileEntryLocalServiceUtil.deleteFileEntry(dlFileEntry);

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		assertValid(getStructuredContent);
	}

	private void _testGetStructuredContentWithRadioField() throws Exception {
		DDMStructure ddmStructure = _addDDMStructure(
			testGroup, "test-ddm-structure-radio.json");

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0,
				_read("test-journal-article-radio.xml"),
				ddmStructure.getStructureKey(), null, LocaleUtil.US, null,
				ServiceContextTestUtil.getServiceContext(
					testCompany.getCompanyId(), testGroup.getGroupId(),
					TestPropsValues.getUserId()));

		StructuredContent structuredContent =
			structuredContentResource.getStructuredContent(
				journalArticle.getResourcePrimKey());

		Assert.assertNotNull(structuredContent.getContentStructureId());
	}

	private void _testGetStructuredContentWithRoleAdministrator()
		throws Exception {

		StructuredContent postStructuredContent =
			testGetStructuredContent_addStructuredContent();

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Map<String, Map<String, String>> actions =
			getStructuredContent.getActions();

		Assert.assertTrue(actions.containsKey("delete"));
		Assert.assertTrue(actions.containsKey("get"));
		Assert.assertTrue(actions.containsKey("get-rendered-content"));
		Assert.assertTrue(actions.containsKey("replace"));
		Assert.assertTrue(actions.containsKey("subscribe"));
		Assert.assertTrue(actions.containsKey("unsubscribe"));
		Assert.assertTrue(actions.containsKey("update"));
	}

	private void _testGetStructuredContentWithRoleOwner() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		RoleTestUtil.addResourcePermission(
			role.getName(), "com.liferay.journal",
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()), ActionKeys.ADD_ARTICLE);

		String password = RandomTestUtil.randomString();

		User ownerUser = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		UserLocalServiceUtil.updateEmailAddressVerified(
			ownerUser.getUserId(), true);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			new long[] {ownerUser.getUserId()}, testGroup.getGroupId(),
			role.getRoleId());

		StructuredContentResource.Builder builder =
			StructuredContentResource.builder();

		StructuredContentResource ownerUserStructuredContentResource =
			builder.authentication(
				ownerUser.getLogin(), password
			).locale(
				LocaleUtil.getDefault()
			).build();

		StructuredContent postStructuredContent =
			ownerUserStructuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), randomStructuredContent());

		StructuredContent getStructuredContent =
			ownerUserStructuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		try {
			Map<String, Map<String, String>> actions =
				getStructuredContent.getActions();

			Assert.assertTrue(actions.containsKey("delete"));
			Assert.assertTrue(actions.containsKey("get"));
			Assert.assertTrue(actions.containsKey("get-rendered-content"));
			Assert.assertTrue(actions.containsKey("replace"));
			Assert.assertTrue(actions.containsKey("subscribe"));
			Assert.assertTrue(actions.containsKey("unsubscribe"));
			Assert.assertTrue(actions.containsKey("update"));
		}
		finally {
			_roleLocalService.deleteRole(role);
			_userLocalService.deleteUser(ownerUser);
		}
	}

	private void _testGetStructuredContentWithRoleRegularUser()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		RoleTestUtil.addResourcePermission(
			role.getName(), "com.liferay.journal",
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()), ActionKeys.ADD_ARTICLE);

		String password = RandomTestUtil.randomString();

		User ownerUser = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		UserLocalServiceUtil.updateEmailAddressVerified(
			ownerUser.getUserId(), true);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			new long[] {ownerUser.getUserId()}, testGroup.getGroupId(),
			role.getRoleId());

		RoleTestUtil.addResourcePermission(
			role.getName(), JournalArticle.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()), ActionKeys.VIEW);

		StructuredContentResource.Builder builder =
			StructuredContentResource.builder();

		StructuredContentResource ownerUserStructuredContentResource =
			builder.authentication(
				ownerUser.getLogin(), password
			).locale(
				LocaleUtil.getDefault()
			).build();

		password = RandomTestUtil.randomString();

		User regularUser = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			password, RandomTestUtil.randomString() + "@liferay.com",
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			ServiceContextTestUtil.getServiceContext());

		UserLocalServiceUtil.updateEmailAddressVerified(
			regularUser.getUserId(), true);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			new long[] {regularUser.getUserId()}, testGroup.getGroupId(),
			role.getRoleId());

		builder = StructuredContentResource.builder();

		StructuredContentResource regularUserStructuredContentResource =
			builder.authentication(
				regularUser.getLogin(), password
			).locale(
				LocaleUtil.getDefault()
			).build();

		StructuredContent postStructuredContent =
			ownerUserStructuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), randomStructuredContent());

		StructuredContent getStructuredContent =
			regularUserStructuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		try {
			Map<String, Map<String, String>> actions =
				getStructuredContent.getActions();

			Assert.assertFalse(actions.containsKey("delete"));
			Assert.assertTrue(actions.containsKey("get"));
			Assert.assertTrue(actions.containsKey("get-rendered-content"));
			Assert.assertFalse(actions.containsKey("replace"));
			Assert.assertFalse(actions.containsKey("subscribe"));
			Assert.assertFalse(actions.containsKey("unsubscribe"));
			Assert.assertFalse(actions.containsKey("update"));
		}
		finally {
			_roleLocalService.deleteRole(role);
			_userLocalService.deleteUser(ownerUser);
			_userLocalService.deleteUser(regularUser);
		}
	}

	private void _testPatchStructuredContentWithDateExpired() throws Exception {
		_testPatchStructuredContentWithDateExpiredExpire1();
		_testPatchStructuredContentWithDateExpiredExpire2();
		_testPatchStructuredContentWithDateExpiredExpireBadRequest();
		_testPatchStructuredContentWithDateExpiredNeverExpire();
	}

	private void _testPatchStructuredContentWithDateExpiredExpire1()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), structuredContent);

		Date date = _getDate(1);

		StructuredContent patchStructuredContent =
			structuredContentResource.patchStructuredContent(
				postStructuredContent.getId(),
				new StructuredContent() {
					{
						dateExpired = date;
						neverExpire = false;
					}
				});

		Assert.assertFalse(patchStructuredContent.getNeverExpire());
		Assert.assertEquals(date, patchStructuredContent.getDateExpired());
	}

	private void _testPatchStructuredContentWithDateExpiredExpire2()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), structuredContent);

		StructuredContent patchStructuredContent =
			structuredContentResource.patchStructuredContent(
				postStructuredContent.getId(),
				new StructuredContent() {
					{
						neverExpire = false;
					}
				});

		Assert.assertFalse(patchStructuredContent.getNeverExpire());

		Date patchDate = patchStructuredContent.getDateExpired();

		Date date = _getDate(1);

		Assert.assertEquals(date.getYear(), patchDate.getYear());
		Assert.assertEquals(date.getMonth(), patchDate.getMonth());
		Assert.assertEquals(date.getDay(), patchDate.getDay());
		Assert.assertEquals(date.getHours(), patchDate.getHours());
	}

	private void _testPatchStructuredContentWithDateExpiredExpireBadRequest()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), structuredContent);

		Date date = _getDate(-1);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.OFF)) {

			assertHttpResponseStatusCode(
				400,
				structuredContentResource.patchStructuredContentHttpResponse(
					postStructuredContent.getId(),
					new StructuredContent() {
						{
							dateExpired = date;
							neverExpire = false;
						}
					}));
		}

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		Assert.assertTrue(getStructuredContent.getNeverExpire());
		Assert.assertNull(getStructuredContent.getDateExpired());
	}

	private void _testPatchStructuredContentWithDateExpiredNeverExpire()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), structuredContent);

		Date date = _getDate(1);

		StructuredContent patchStructuredContent =
			structuredContentResource.patchStructuredContent(
				postStructuredContent.getId(),
				new StructuredContent() {
					{
						dateExpired = date;
						neverExpire = true;
					}
				});

		Assert.assertTrue(patchStructuredContent.getNeverExpire());
		Assert.assertNull(patchStructuredContent.getDateExpired());
	}

	private void _testPatchStructuredContentWithRandomTitle() throws Exception {
		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setPriority(1.0);

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGroup.getGroupId(), structuredContent);

		StructuredContent patchStructuredContent =
			structuredContentResource.patchStructuredContent(
				postStructuredContent.getId(),
				new StructuredContent() {
					{
						title = RandomTestUtil.randomString();
					}
				});

		Assert.assertEquals(
			Double.valueOf(1.0), patchStructuredContent.getPriority());
	}

	private void _testPostAssetLibraryStructuredContent(
			String externalReferenceCode)
		throws Exception {

		StructuredContent randomStructuredContent = randomStructuredContent();

		randomStructuredContent.setExternalReferenceCode(externalReferenceCode);

		StructuredContent postStructuredContent =
			testPostAssetLibraryStructuredContent_addStructuredContent(
				randomStructuredContent);

		Assert.assertNotNull(postStructuredContent.getExternalReferenceCode());
		Assert.assertEquals(
			externalReferenceCode,
			postStructuredContent.getExternalReferenceCode());
		assertValid(postStructuredContent);
	}

	private void _testPostSiteStructuredContentBatch() throws Exception {
		JSONObject jsonObject = _waitForFinish(
			"COMPLETED", true,
			JSONFactoryUtil.createJSONObject(
				structuredContentResource.
					postSiteStructuredContentBatchHttpResponse(
						testGroup.getGroupId(), null,
						JSONUtil.putAll(
							JSONFactoryUtil.createJSONObject(
								String.valueOf(
									_randomStructuredContent(
										LocaleUtil.getDefault()))))
					).getContent()));

		Assert.assertEquals(1, jsonObject.getLong("processedItemsCount"));
		Assert.assertEquals(1, jsonObject.getLong("totalItemsCount"));
	}

	private void _testPostStructuredContentFolderStructuredContentWithDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			testGroup.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			_localizedDDMStructure.getStructureId(), true,
			WorkflowConstants.STATUS_APPROVED);

		Locale locale = LocaleUtil.getDefault();

		StructuredContent randomStructuredContent = _randomStructuredContent(
			locale);

		StructuredContentResource structuredContentResource =
			_buildStructureContentResource(locale);

		StructuredContent postStructuredContent =
			structuredContentResource.
				postStructuredContentFolderStructuredContent(
					_journalFolder.getFolderId(), randomStructuredContent);

		Assert.assertTrue(
			postStructuredContent.getRenderedContents()[0].
				getMarkedAsDefault());
	}

	private void _testPostStructuredContentFolderStructuredContentWithImageContentField()
		throws Exception {

		StructuredContent structuredContent = randomStructuredContent();

		structuredContent.setContentFields(
			new ContentField[] {
				new ContentField() {
					{
						contentFieldValue = new ContentFieldValue() {
							{
								image = new ContentDocument() {
									{
										description =
											RandomTestUtil.randomString();
										id = _dlFileEntry.getFileEntryId();
									}
								};
							}
						};
						name = "image";
					}
				}
			});

		DDMStructure imageDDMStructure = _addDDMStructure(
			testGroup, "test-ddm-structure-image.json");

		structuredContent.setContentStructureId(
			imageDDMStructure.getStructureId());

		StructuredContent postStructuredContent =
			structuredContentResource.
				postStructuredContentFolderStructuredContent(
					_journalFolder.getFolderId(), structuredContent);

		assertValid(postStructuredContent);

		StructuredContent getStructuredContent =
			structuredContentResource.getStructuredContent(
				postStructuredContent.getId());

		ContentField contentField = getStructuredContent.getContentFields()[0];

		ContentFieldValue contentFieldValue =
			contentField.getContentFieldValue();

		ContentDocument contentDocument = contentFieldValue.getImage();

		Assert.assertEquals(
			_dlFileEntry.getFileEntryId(), (long)contentDocument.getId());

		Assert.assertEquals("image", contentField.getName());
	}

	private void _testPutSiteStructuredContentByExternalReferenceCodeWithCustomField()
		throws Exception {

		StructuredContent postStructuredContent =
			testPutSiteStructuredContentByExternalReferenceCode_addStructuredContent();

		long randomLong = RandomTestUtil.randomLong(
			Long.MIN_VALUE, Long.MAX_VALUE);
		short randomShort1 = (short)RandomTestUtil.randomInt(
			Short.MIN_VALUE, Short.MAX_VALUE);
		short randomShort2 = (short)RandomTestUtil.randomInt(
			Short.MIN_VALUE, Short.MAX_VALUE);

		ExpandoColumn longExpandoColumn = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.LONG);

		CustomField longCustomField = new CustomField() {
			{
				customValue = new CustomValue() {
					{
						data = randomLong;
					}
				};
				dataType = "Integer";
				name = longExpandoColumn.getName();
			}
		};

		ExpandoColumn shortExpandoColumn = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.SHORT);

		CustomField shortCustomField = new CustomField() {
			{
				customValue = new CustomValue() {
					{
						data = randomShort1;
					}
				};
				dataType = "Integer";
				name = shortExpandoColumn.getName();
			}
		};

		ExpandoColumn shortArrayExpandoColumn = _addExpandoColumn(
			null, null, _expandoTable, ExpandoColumnConstants.SHORT_ARRAY);

		CustomField shortArrayCustomField = new CustomField() {
			{
				customValue = new CustomValue() {
					{
						data = Arrays.asList(randomShort2);
					}
				};
				dataType = "Integer";
				name = shortArrayExpandoColumn.getName();
			}
		};

		StructuredContent randomStructuredContent = new StructuredContent() {
			{
				contentStructureId = _ddmStructure.getStructureId();
				customFields = new CustomField[] {
					longCustomField, shortCustomField, shortArrayCustomField
				};
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};

		StructuredContent putStructuredContent =
			structuredContentResource.
				putSiteStructuredContentByExternalReferenceCode(
					postStructuredContent.getSiteId(),
					postStructuredContent.getExternalReferenceCode(),
					randomStructuredContent);

		assertValid(putStructuredContent);

		CustomField[] customFields = putStructuredContent.getCustomFields();

		Assert.assertNotNull(customFields);

		CustomField customField = _getCustomField(
			customFields, longExpandoColumn.getName());

		Assert.assertNotNull(customField);

		customField = _getCustomField(
			customFields, shortExpandoColumn.getName());

		Assert.assertNotNull(customField);

		customField = _getCustomField(
			customFields, shortArrayCustomField.getName());

		Assert.assertNotNull(customField);
	}

	private void _testPutStructuredContent(boolean containsI18nMap)
		throws Exception {

		StructuredContent structuredContent1 = _randomStructuredContent(
			LocaleUtil.getDefault());

		StructuredContent postStructuredContent =
			structuredContentResource.postSiteStructuredContent(
				testGetSiteStructuredContentsPage_getSiteId(),
				structuredContent1);

		StructuredContent structuredContent2 = _randomStructuredContent(
			LocaleUtil.getDefault());

		ContentFieldValue englishContentFieldValue = new ContentFieldValue() {
			{
				data = RandomTestUtil.randomString(10);
			}
		};

		if (!containsI18nMap) {
			structuredContent2.setContentFields(
				new ContentField[] {
					new ContentField() {
						{
							contentFieldValue = englishContentFieldValue;
							name = "MyText";
						}
					}
				});
		}

		StructuredContentResource structuredContentResource =
			_buildStructureContentResource(LocaleUtil.getDefault());

		StructuredContent putStructuredContent =
			structuredContentResource.putStructuredContent(
				postStructuredContent.getId(), structuredContent2);

		if (!containsI18nMap) {
			structuredContent2.setContentFields(
				new ContentField[] {
					new ContentField() {
						{
							contentFieldValue = englishContentFieldValue;
							contentFieldValue_i18n = HashMapBuilder.put(
								"en-US", () -> englishContentFieldValue
							).put(
								"es-ES",
								() -> {
									ContentField initialContentField =
										structuredContent1.getContentFields()
											[0];

									return initialContentField.
										getContentFieldValue_i18n(
										).get(
											"es-ES"
										);
								}
							).build();
							name = "MyText";
						}
					}
				});
		}

		Assert.assertTrue(_equals(structuredContent2, putStructuredContent));

		_assertLocalizedValues(
			putStructuredContent,
			LocaleUtil.toW3cLanguageId(LocaleUtil.getDefault()));
		assertValid(putStructuredContent);
	}

	private JSONObject _waitForFinish(
			String expectedExecuteStatus, boolean importTask,
			JSONObject jsonObject)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-batch-engine/v1.0/",
			importTask ? "import-task" : "export-task",
			"/by-external-reference-code/");

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}
		}
	}

	private static final String[] _COMPLETE_STRUCTURED_CONTENT_OPTIONS = {
		"Option1", "Option2", "Option3"
	};

	private static final String _JOURNAL_ARTICLE_TITLE_FR =
		RandomTestUtil.randomString();

	@Inject(filter = "ddm.form.deserializer.type=json")
	private static DDMFormDeserializer _jsonDDMFormDeserializer;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private BlogsEntry _blogsEntry;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	private DDMStructure _ddmStructure;
	private DDMTemplate _ddmTemplate;
	private DDMStructure _depotDDMStructure;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private DLFileEntry _dlFileEntry;

	@Inject
	private DLURLHelper _dlURLHelper;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@DeleteAfterTestRun
	private ExpandoTable _expandoTable;

	private DDMStructure _irrelevantDDMStructure;
	private DDMStructure _irrelevantDepotDDMStructure;
	private JournalFolder _irrelevantJournalFolder;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalConverter _journalConverter;

	private JournalFolder _journalFolder;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	private DDMStructure _localizedDDMStructure;
	private String _originalName;

	@Inject
	private Portal _portal;

	@Inject
	private RoleLocalService _roleLocalService;

	private DDMStructure _unlocalizedDDMStructure;
	private boolean _useDepotDDMStructureStructureId;

	@Inject
	private UserLocalService _userLocalService;

}