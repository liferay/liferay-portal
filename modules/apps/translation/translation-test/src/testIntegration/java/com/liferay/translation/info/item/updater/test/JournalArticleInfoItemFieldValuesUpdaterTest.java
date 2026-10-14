/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.info.item.updater.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.translation.importer.TranslationInfoItemFieldValuesImporter;
import com.liferay.translation.service.TranslationEntryLocalService;
import com.liferay.translation.test.util.TranslationTestUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class JournalArticleInfoItemFieldValuesUpdaterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule testRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		PrincipalThreadLocal.setName(_originalName);
	}

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		_group = GroupTestUtil.addGroup();

		User user = TestPropsValues.getUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testUpdateJournalArticleFromInfoItemFieldValuesAddsTranslatedContent()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "<p>This is the content</p>"
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"test-journal-article-122.xlf"));

		journalArticle =
			_journalArticleInfoItemFieldValuesUpdater.
				updateFromInfoItemFieldValues(
					journalArticle, infoItemFieldValues);

		Assert.assertEquals(
			"Este es el titulo", journalArticle.getTitle(LocaleUtil.SPAIN));
		Assert.assertEquals(
			"Este es el resumen",
			journalArticle.getDescription(LocaleUtil.SPAIN));
		Assert.assertEquals(
			"<p>Este es el contenido</p>",
			_getContent(
				journalArticle, "name", LocaleUtil.US, LocaleUtil.SPAIN));
	}

	@Test
	public void testUpdateJournalArticleFromInfoItemFieldValuesDoesNotModifyOtherTranslations()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "Este es el titulo"
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "Esta es la descripcion"
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "Este es el contenido"
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"test-journal-article-122-ja-JP.xlf"));

		journalArticle =
			_journalArticleInfoItemFieldValuesUpdater.
				updateFromInfoItemFieldValues(
					journalArticle, infoItemFieldValues);

		Assert.assertEquals(
			"これはタイトルです", journalArticle.getTitle(LocaleUtil.JAPAN));
		Assert.assertEquals(
			"Este es el titulo", journalArticle.getTitle(LocaleUtil.SPAIN));
		Assert.assertEquals(
			"これは要約です", journalArticle.getDescription(LocaleUtil.JAPAN));
		Assert.assertEquals(
			"Esta es la descripcion",
			journalArticle.getDescription(LocaleUtil.SPAIN));
		Assert.assertEquals(
			"<p>これが内容です</p>",
			_getContent(
				journalArticle, "name", LocaleUtil.US, LocaleUtil.JAPAN));
		Assert.assertEquals(
			"Este es el contenido",
			_getContent(
				journalArticle, "name", LocaleUtil.US, LocaleUtil.SPAIN));
	}

	@Test
	public void testUpdateJournalArticleFromInfoItemFieldValuesPreservesEmptyIntermediateRepeatableField()
		throws Exception {

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				TranslationTestUtil.readFileToString(
					"test-ddm-structure-repeatable-html.json"));

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_ddmFormDeserializer.deserialize(builder.build());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(),
			ddmFormDeserializerDeserializeResponse.getDDMForm());

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				_group.getGroupId(),
				TranslationTestUtil.readFileToString(
					"test-journal-content-repeatable-html-three-fields.xml"),
				ddmStructure.getStructureKey(), null);

		_testUpdateJournalArticleFromInfoItemFieldValuesPreservesEmptyIntermediateRepeatableField(
			"RichText", journalArticle,
			"test-journal-repeatable-html-empty-v12.xlf");

		builder = DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
			TranslationTestUtil.readFileToString(
				"test-ddm-structure-repeatable-text.json"));

		ddmFormDeserializerDeserializeResponse =
			_ddmFormDeserializer.deserialize(builder.build());

		ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(),
			ddmFormDeserializerDeserializeResponse.getDDMForm());

		journalArticle = JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			TranslationTestUtil.readFileToString(
				"test-journal-content-repeatable-text-three-fields.xml"),
			ddmStructure.getStructureKey(), null);

		_testUpdateJournalArticleFromInfoItemFieldValuesPreservesEmptyIntermediateRepeatableField(
			"TextField", journalArticle,
			"test-journal-repeatable-text-empty-v12.xlf");
	}

	@Test
	public void testUpdateJournalArticleFromInfoItemFieldValuesUpdatesNewField()
		throws Exception {

		JournalArticle journalArticle = _getJournalArticle();

		_translationEntryLocalService.addOrUpdateTranslationEntry(
			_group.getGroupId(), JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey(),
			StringUtil.replace(
				TranslationTestUtil.readFileToString(
					"test-journal-article-new-field.xlf"),
				"[$JOURNAL_ARTICLE_ID$]",
				String.valueOf(journalArticle.getResourcePrimKey())),
			"application/xliff+xml", LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		journalArticle = _journalArticleLocalService.fetchLatestArticle(
			journalArticle.getResourcePrimKey());

		Assert.assertEquals(
			"Este campo es nuevo",
			_getContent(
				journalArticle, "NewText", LocaleUtil.US, LocaleUtil.SPAIN));
	}

	@Test
	public void testUpdateJournalArticleFromInfoItemFieldValuesUpdatesOnlyTheTitle()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, "Descripcion"
			).put(
				LocaleUtil.US, "description"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "content"
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"test-journal-article-122-only-title.xlf"));

		journalArticle =
			_journalArticleInfoItemFieldValuesUpdater.
				updateFromInfoItemFieldValues(
					journalArticle, infoItemFieldValues);

		Assert.assertEquals(
			"Este es el titulo", journalArticle.getTitle(LocaleUtil.SPAIN));
		Assert.assertEquals(
			"Descripcion", journalArticle.getDescription(LocaleUtil.SPAIN));
		Assert.assertEquals(
			StringPool.BLANK,
			_getContent(
				journalArticle, "name", LocaleUtil.US, LocaleUtil.SPAIN));
	}

	@Test
	public void testUpdateJournalArticleFromInfoItemFieldValuesUpdatesTranslations()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.JAPAN, "translate title to japanese"
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.JAPAN, "translate description to japanese"
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.JAPAN, "translate content to japanese"
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		Assert.assertEquals(
			"translate title to japanese",
			journalArticle.getTitle(LocaleUtil.JAPAN));
		Assert.assertEquals(
			"translate description to japanese",
			journalArticle.getDescription(LocaleUtil.JAPAN));
		Assert.assertEquals(
			"translate content to japanese",
			_getContent(
				journalArticle, "name", LocaleUtil.US, LocaleUtil.JAPAN));

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"test-journal-article-122-ja-JP.xlf"));

		journalArticle =
			_journalArticleInfoItemFieldValuesUpdater.
				updateFromInfoItemFieldValues(
					journalArticle, infoItemFieldValues);

		Assert.assertEquals(
			"これはタイトルです", journalArticle.getTitle(LocaleUtil.JAPAN));
		Assert.assertEquals(
			"これは要約です", journalArticle.getDescription(LocaleUtil.JAPAN));
		Assert.assertEquals(
			"<p>これが内容です</p>",
			_getContent(
				journalArticle, "name", LocaleUtil.US, LocaleUtil.JAPAN));
	}

	@Test
	public void testUpdateJournalArticleFromInfoItemFieldValuesXLIFFv12File()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"example-1_2-oasis.xlf"));

		journalArticle =
			_journalArticleInfoItemFieldValuesUpdater.
				updateFromInfoItemFieldValues(
					journalArticle, infoItemFieldValues);

		Assert.assertEquals(
			"Quetzal", journalArticle.getTitle(LocaleUtil.JAPAN));
		Assert.assertEquals(
			"XLIFF データ・マネージャ", journalArticle.getDescription(LocaleUtil.JAPAN));
		Assert.assertEquals(
			"<p>XLIFF 文書を編集、または処理 するアプリケーションです。</p>",
			_getContent(
				journalArticle, "name", LocaleUtil.US, LocaleUtil.JAPAN));
	}

	@Test
	public void testUpdateJournalArticleWithDeletedUser() throws Exception {
		User user = UserTestUtil.addCompanyAdminUser(_company);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0,
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "<p>This is the content</p>"
			).build(),
			LocaleUtil.getSiteDefault(), false, true, serviceContext);

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"test-journal-article-122.xlf"));

		_userLocalService.deleteUser(user);

		journalArticle =
			_journalArticleInfoItemFieldValuesUpdater.
				updateFromInfoItemFieldValues(
					journalArticle, infoItemFieldValues);

		Assert.assertEquals(
			TestPropsValues.getUserId(), journalArticle.getStatusByUserId());
	}

	private String _getContent(
		JournalArticle journalArticle, String fieldName, Locale sourceLocale,
		Locale targetLocale) {

		DDMFormValues ddmFormValues = journalArticle.getDDMFormValues();

		Set<Locale> availableLocales = ddmFormValues.getAvailableLocales();

		if (!availableLocales.contains(sourceLocale) ||
			!availableLocales.contains(targetLocale)) {

			return StringPool.BLANK;
		}

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			fieldName);

		if (ddmFormFieldValues.isEmpty()) {
			return null;
		}

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		return value.getString(targetLocale);
	}

	private JournalArticle _getJournalArticle() throws Exception {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				TranslationTestUtil.readFileToString(
					"test-ddm-structure.json"));

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_ddmFormDeserializer.deserialize(builder.build());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(),
			ddmFormDeserializerDeserializeResponse.getDDMForm());

		return JournalTestUtil.addArticleWithXMLContent(
			_group.getGroupId(),
			TranslationTestUtil.readFileToString(
				"test-journal-content-one-field.xml"),
			ddmStructure.getStructureKey(), null);
	}

	private void
			_testUpdateJournalArticleFromInfoItemFieldValuesPreservesEmptyIntermediateRepeatableField(
				String ddmFormFieldName, JournalArticle journalArticle,
				String xliffFileName)
		throws Exception {

		String xliff = StringUtil.replace(
			TranslationTestUtil.readFileToString(xliffFileName),
			"[$JOURNAL_ARTICLE_ID$]",
			String.valueOf(journalArticle.getResourcePrimKey()));

		_translationEntryLocalService.addOrUpdateTranslationEntry(
			_group.getGroupId(), JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey(),
			xliff.replaceAll(">\\s+<", "><"), "application/xliff+xml",
			LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		journalArticle = _journalArticleLocalService.fetchLatestArticle(
			journalArticle.getResourcePrimKey());

		DDMFormValues ddmFormValues = journalArticle.getDDMFormValues();

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(true);

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			ddmFormFieldName);

		Assert.assertEquals(
			ddmFormFieldValues.toString(), 3, ddmFormFieldValues.size());

		DDMFormFieldValue ddmFormFieldValue0 = ddmFormFieldValues.get(0);

		Value value0 = ddmFormFieldValue0.getValue();

		Assert.assertEquals("Valor A", value0.getString(LocaleUtil.SPAIN));

		DDMFormFieldValue ddmFormFieldValue1 = ddmFormFieldValues.get(1);

		Value value1 = ddmFormFieldValue1.getValue();

		Assert.assertEquals(
			StringPool.BLANK, value1.getString(LocaleUtil.SPAIN));

		DDMFormFieldValue ddmFormFieldValue2 = ddmFormFieldValues.get(2);

		Value value2 = ddmFormFieldValue2.getValue();

		Assert.assertEquals("Valor C", value2.getString(LocaleUtil.SPAIN));
	}

	private static String _originalName;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "ddm.form.deserializer.type=json")
	private DDMFormDeserializer _ddmFormDeserializer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "item.class.name=com.liferay.journal.model.JournalArticle")
	private InfoItemFieldValuesUpdater<JournalArticle>
		_journalArticleInfoItemFieldValuesUpdater;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private TranslationEntryLocalService _translationEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject(filter = "content.type=application/xliff+xml")
	private TranslationInfoItemFieldValuesImporter
		_xliffTranslationInfoItemFieldValuesImporter;

}