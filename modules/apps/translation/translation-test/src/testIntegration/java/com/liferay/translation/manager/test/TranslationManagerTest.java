/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.translation.exception.XLIFFFileException;
import com.liferay.translation.manager.Translation;
import com.liferay.translation.manager.TranslationManager;
import com.liferay.translation.test.util.TranslationTestUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class TranslationManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_journalArticle = TranslationTestUtil.getJournalArticle(
			_group, _ddmFormDeserializer);
	}

	@Test
	public void testGetXLIFFFile() throws Exception {
		File xliff12File = _translationManager.getXLIFFFile(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey(), _MIMETYPE_XLIFF_1_2,
			LocaleUtil.US, LocaleUtil.toLanguageId(LocaleUtil.US),
			_TARGET_LANGUAGE_IDS[0]);

		Assert.assertEquals(_getXLIFFFileName(), xliff12File.getName());

		_assertXLIFFFile(
			"test-journal-article-v12.xlf", new FileInputStream(xliff12File));

		File xliff20File = _translationManager.getXLIFFFile(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey(), _MIMETYPE_XLIFF_2_0,
			LocaleUtil.US, LocaleUtil.toLanguageId(LocaleUtil.US),
			_TARGET_LANGUAGE_IDS[0]);

		Assert.assertEquals(_getXLIFFFileName(), xliff20File.getName());

		_assertXLIFFFile(
			"test-journal-article.xlf", new FileInputStream(xliff20File));

		_testGetXLIFFFile("test-journal-article-v12.xlf", _MIMETYPE_XLIFF_1_2);
		_testGetXLIFFFile("test-journal-article.xlf", _MIMETYPE_XLIFF_2_0);
	}

	@Test(expected = XLIFFFileException.MustBeSupportedLanguage.class)
	@TestInfo("LPD-85963")
	public void testGetXLIFFFileFailsWithInvalidSourceLanguageId()
		throws Exception {

		_translationManager.getXLIFFFile(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey(), _MIMETYPE_XLIFF_1_2,
			LocaleUtil.US, _INVALID_LANGUAGE_ID, _TARGET_LANGUAGE_IDS[0]);
	}

	@Test(expected = XLIFFFileException.MustBeSupportedLanguage.class)
	@TestInfo("LPD-85963")
	public void testGetXLIFFFileFailsWithInvalidTargetLanguageId()
		throws Exception {

		_translationManager.getXLIFFFile(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey(), _MIMETYPE_XLIFF_1_2,
			LocaleUtil.US, LocaleUtil.toLanguageId(LocaleUtil.US),
			_INVALID_LANGUAGE_ID);
	}

	@Test(expected = XLIFFFileException.MustBeSupportedLanguage.class)
	@TestInfo("LPD-85963")
	public void testGetXLIFFZipFileFailsWithInvalidSourceLanguageId()
		throws Exception {

		_translationManager.getXLIFFZipFile(
			JournalArticle.class.getName(),
			new long[] {_journalArticle.getResourcePrimKey()},
			_MIMETYPE_XLIFF_1_2, LocaleUtil.US, _INVALID_LANGUAGE_ID,
			_TARGET_LANGUAGE_IDS);
	}

	@Test(expected = XLIFFFileException.MustBeSupportedLanguage.class)
	@TestInfo("LPD-85963")
	public void testGetXLIFFZipFileFailsWithInvalidTargetLanguageId()
		throws Exception {

		_translationManager.getXLIFFZipFile(
			JournalArticle.class.getName(),
			new long[] {_journalArticle.getResourcePrimKey()},
			_MIMETYPE_XLIFF_1_2, LocaleUtil.US,
			LocaleUtil.toLanguageId(LocaleUtil.US),
			new String[] {_INVALID_LANGUAGE_ID});
	}

	@Test(expected = XLIFFFileException.MustHaveValidParameter.class)
	@TestInfo("LPD-85963")
	public void testGetXLIFFZipFileFailsWithNullClassPKs() throws Exception {
		_translationManager.getXLIFFZipFile(
			JournalArticle.class.getName(), null, _MIMETYPE_XLIFF_1_2,
			LocaleUtil.US, LocaleUtil.toLanguageId(LocaleUtil.US),
			_TARGET_LANGUAGE_IDS);
	}

	@Test(expected = XLIFFFileException.MustHaveValidParameter.class)
	@TestInfo("LPD-85963")
	public void testGetXLIFFZipFileFailsWithNullTargetLanguageIds()
		throws Exception {

		_translationManager.getXLIFFZipFile(
			JournalArticle.class.getName(),
			new long[] {_journalArticle.getResourcePrimKey()},
			_MIMETYPE_XLIFF_1_2, LocaleUtil.US,
			LocaleUtil.toLanguageId(LocaleUtil.US), null);
	}

	@Test
	@TestInfo("LPD-90056")
	public void testGetXLIFFZipFileForObjectEntry() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						Collections.singletonMap(
							LocaleUtil.getDefault(), "Title")
					).localized(
						true
					).name(
						"title"
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		String englishTitle = RandomTestUtil.randomString();
		String spanishTitle = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			"en_US",
			HashMapBuilder.put(
				"title_i18n",
				(Serializable)HashMapBuilder.put(
					"en_US", englishTitle
				).put(
					"es_ES", spanishTitle
				).build()
			).build(),
			serviceContext);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			File xliffZipFile = _translationManager.getXLIFFZipFile(
				objectDefinition.getClassName(),
				new long[] {objectEntry.getObjectEntryId()},
				_MIMETYPE_XLIFF_1_2, LocaleUtil.US, "en_US",
				_TARGET_LANGUAGE_IDS);

			try (ZipFile zipFile = new ZipFile(xliffZipFile)) {
				Enumeration<? extends ZipEntry> zipEntriesEnumeration =
					zipFile.entries();

				ZipEntry zipEntry = zipEntriesEnumeration.nextElement();

				Assert.assertNotNull(zipEntry);

				String xliffContent = StringUtil.read(
					zipFile.getInputStream(zipEntry));

				Assert.assertTrue(
					xliffContent.contains("<![CDATA[" + englishTitle + "]]>"));
				Assert.assertTrue(
					xliffContent.contains("<![CDATA[" + spanishTitle + "]]>"));
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	@TestInfo("LPD-85323")
	public void testObjectEntryGetTitle() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						Collections.singletonMap(
							LocaleUtil.getDefault(), "Title")
					).localized(
						true
					).name(
						"title"
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		String spanishTitle = RandomTestUtil.randomString();

		Map<String, String> titles = HashMapBuilder.put(
			"es_ES", spanishTitle
		).build();

		_testObjectEntryGetTitle(
			"es_ES", spanishTitle, LocaleUtil.US, objectDefinition, titles);
		_testObjectEntryGetTitle(
			"es_ES", spanishTitle, LocaleUtil.SPAIN, objectDefinition, titles);

		String englishTitle = RandomTestUtil.randomString();

		titles.put("en_US", englishTitle);

		_testObjectEntryGetTitle(
			"es_ES", englishTitle, LocaleUtil.US, objectDefinition, titles);

		_testObjectEntryGetTitle(
			"es_ES", spanishTitle, LocaleUtil.SPAIN, objectDefinition, titles);
	}

	@Test
	public void testProcessXLIFFTranslation() throws Exception {
		File xliffFile = _translationManager.getXLIFFFile(
			JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey(), _MIMETYPE_XLIFF_1_2,
			LocaleUtil.US, LocaleUtil.toLanguageId(LocaleUtil.US),
			_TARGET_LANGUAGE_IDS[0]);

		_testProcessXLIFFTranslationFailureWithSingleFile(xliffFile);
		_testProcessXLIFFTranslationSuccessWithSingleFile(xliffFile);

		File xliffZipFile = _translationManager.getXLIFFZipFile(
			JournalArticle.class.getName(),
			new long[] {_journalArticle.getResourcePrimKey()},
			_MIMETYPE_XLIFF_1_2, LocaleUtil.US,
			LocaleUtil.toLanguageId(LocaleUtil.US), _TARGET_LANGUAGE_IDS);

		_testProcessXLIFFTranslationFailureWithZipFile(xliffZipFile);
		_testProcessXLIFFTranslationSuccessWithZipFile(xliffZipFile);
	}

	private void _assertProcessXLIFFTranslationFailure(
		List<Map<String, String>> failureMessages, List<String> successMessages,
		boolean container) {

		for (Map<String, String> failureMessage : failureMessages) {
			if (container) {
				Assert.assertEquals(
					"Test Article-en_US.zip", failureMessage.get("container"));
			}
			else {
				Assert.assertTrue(
					Validator.isNull(failureMessage.get("container")));
			}

			Assert.assertEquals(
				_getXLIFFFileName(), failureMessage.get("fileName"));
			Assert.assertEquals(
				"The translation file does not correspond to this web content.",
				failureMessage.get("errorMessage"));
		}

		Assert.assertEquals(
			failureMessages.toString(), 1, failureMessages.size());
		Assert.assertTrue(successMessages.isEmpty());
	}

	private void _assertProcessXLIFFTranslationSuccess(
		List<Map<String, String>> failureMessages,
		List<String> successMessages) {

		Assert.assertTrue(failureMessages.isEmpty());
		Assert.assertEquals(_getXLIFFFileName(), successMessages.get(0));
		Assert.assertEquals(
			successMessages.toString(), 1, successMessages.size());
	}

	private void _assertXLIFFFile(String expected, InputStream inputStream)
		throws Exception {

		Assert.assertEquals(
			TranslationTestUtil.toFormattedString(
				StringUtil.replace(
					TranslationTestUtil.readFileToString(expected),
					"[$JOURNAL_ARTICLE_ID$]",
					String.valueOf(_journalArticle.getResourcePrimKey()))),
			TranslationTestUtil.toFormattedString(
				StringUtil.read(inputStream)));
	}

	private String _getXLIFFFileName() {
		return String.format(
			"%s-%s-%s.xlf", _journalArticle.getTitle(),
			LocaleUtil.toLanguageId(LocaleUtil.US), _TARGET_LANGUAGE_IDS[0]);
	}

	private void _testGetXLIFFFile(String fileName, String xliffMimeType)
		throws Exception {

		File xliffZipFile = _translationManager.getXLIFFZipFile(
			JournalArticle.class.getName(),
			new long[] {_journalArticle.getResourcePrimKey()}, xliffMimeType,
			LocaleUtil.US, LocaleUtil.toLanguageId(LocaleUtil.US),
			_TARGET_LANGUAGE_IDS);

		try (ZipFile zipFile = new ZipFile(xliffZipFile)) {
			ZipEntry zipEntry = zipFile.getEntry(_getXLIFFFileName());

			Assert.assertNotNull(zipEntry);
			Assert.assertFalse(zipEntry.isDirectory());

			_assertXLIFFFile(fileName, zipFile.getInputStream(zipEntry));

			Assert.assertEquals(
				String.format(
					"%s-%s.zip", _journalArticle.getTitle(),
					LocaleUtil.toLanguageId(LocaleUtil.US)),
				xliffZipFile.getName());
		}
		finally {
			if ((xliffZipFile != null) && xliffZipFile.exists()) {
				xliffZipFile.delete();
			}
		}
	}

	private void _testObjectEntryGetTitle(
			String defaultLangueId, String expectedTitle, Locale locale,
			ObjectDefinition objectDefinition, Map<String, String> titles)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			defaultLangueId,
			HashMapBuilder.put(
				"title_i18n", (Serializable)titles
			).build(),
			serviceContext);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			Assert.assertEquals(
				expectedTitle,
				_translationManager.getTitle(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId(), locale));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _testProcessXLIFFTranslationFailureWithSingleFile(
			File xliffFile)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		List<Map<String, String>> failureMessages = new LinkedList<>();
		List<String> successMessages = new ArrayList<>();

		_translationManager.processXLIFFTranslation(
			_group.getGroupId(), JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey(),
			new Translation(
				() -> _MIMETYPE_XLIFF_1_2, xliffFile.getName(),
				() -> new FileInputStream(xliffFile)),
			successMessages, failureMessages, LocaleUtil.US,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertProcessXLIFFTranslationFailure(
			failureMessages, successMessages, false);
	}

	private void _testProcessXLIFFTranslationFailureWithZipFile(
			File xliffZipFile)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		List<Map<String, String>> failureMessages = new LinkedList<>();
		List<String> successMessages = new ArrayList<>();

		_translationManager.processXLIFFTranslation(
			_group.getGroupId(), JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey(),
			new Translation(
				() -> ContentTypes.APPLICATION_ZIP, xliffZipFile.getName(),
				() -> new FileInputStream(xliffZipFile)),
			successMessages, failureMessages, LocaleUtil.US,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertProcessXLIFFTranslationFailure(
			failureMessages, successMessages, true);
	}

	private void _testProcessXLIFFTranslationSuccessWithSingleFile(
			File xliffFile)
		throws Exception {

		List<Map<String, String>> failureMessages = new LinkedList<>();
		List<String> successMessages = new ArrayList<>();

		_translationManager.processXLIFFTranslation(
			_group.getGroupId(), JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey(),
			new Translation(
				() -> _MIMETYPE_XLIFF_1_2, xliffFile.getName(),
				() -> new FileInputStream(xliffFile)),
			successMessages, failureMessages, LocaleUtil.US,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertProcessXLIFFTranslationSuccess(failureMessages, successMessages);
	}

	private void _testProcessXLIFFTranslationSuccessWithZipFile(
			File xliffZipFile)
		throws Exception {

		List<Map<String, String>> failureMessages = new LinkedList<>();
		List<String> successMessages = new ArrayList<>();

		_translationManager.processXLIFFTranslation(
			_group.getGroupId(), JournalArticle.class.getName(),
			_journalArticle.getResourcePrimKey(),
			new Translation(
				() -> ContentTypes.APPLICATION_ZIP, xliffZipFile.getName(),
				() -> new FileInputStream(xliffZipFile)),
			successMessages, failureMessages, LocaleUtil.US,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_assertProcessXLIFFTranslationSuccess(failureMessages, successMessages);
	}

	private static final String _INVALID_LANGUAGE_ID = "xx_XX";

	private static final String _MIMETYPE_XLIFF_1_2 = "application/x-xliff+xml";

	private static final String _MIMETYPE_XLIFF_2_0 = "application/xliff+xml";

	private static final String[] _TARGET_LANGUAGE_IDS = {"es_ES"};

	@Inject(filter = "ddm.form.deserializer.type=json")
	private DDMFormDeserializer _ddmFormDeserializer;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private TranslationManager _translationManager;

}