/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.translation.exception.XLIFFFileException;
import com.liferay.translation.importer.TranslationInfoItemFieldValuesImporter;
import com.liferay.translation.test.util.TranslationTestUtil;

import java.io.ByteArrayInputStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class XLIFFTranslationInfoItemFieldValuesImporterTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test(expected = XLIFFFileException.MustHaveCorrectEncoding.class)
	public void testImportXLIFF2FailsFileIncorrectEncoding() throws Exception {
		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(JournalArticle.class.getName(), 122),
			TranslationTestUtil.readFileToInputStream(
				"test-journal-article-122-iso-8859-encoding.xlf"));
	}

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-102730"))
	@Test(expected = XLIFFFileException.MustBeValid.class)
	public void testImportXLIFF12FailsFileInlineCodeWithoutOriginalData()
		throws Exception {

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(JournalArticle.class.getName(), 122),
			new ByteArrayInputStream(
				_INLINE_CODES_NO_ORIGINAL_DATA_V12_XLIFF.getBytes()));
	}

	@Test(expected = XLIFFFileException.MustHaveValidId.class)
	public void testImportXLIFF12FailsFileInvalidId() throws Exception {
		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(
				JournalArticle.class.getName(), RandomTestUtil.randomInt(1, 3)),
			TranslationTestUtil.readFileToInputStream(
				"example-1_2-simple.xlf"));
	}

	@Test(expected = XLIFFFileException.MustBeWellFormed.class)
	public void testImportXLIFF12FailsFileInvalidVersion() throws Exception {
		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(JournalArticle.class.getName(), 122),
			TranslationTestUtil.readFileToInputStream(
				"example-1_2-bad-formed.xlf"));
	}

	@Test(expected = XLIFFFileException.MustBeWellFormed.class)
	public void testImportXLIFF12FailsFileNoTarget() throws Exception {
		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(JournalArticle.class.getName(), 122),
			TranslationTestUtil.readFileToInputStream(
				"example-1_2-no-target.xlf"));
	}

	@Test
	public void testImportXLIFF12IgnoresEmptyTarget() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"example-1_2-empty-target.xlf"));

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertEquals(
			infoFieldValues.toString(), 1, infoFieldValues.size());
	}

	@Test
	public void testImportXLIFF12PreservesInlineCodes() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					new ByteArrayInputStream(
						_INLINE_CODES_V12_XLIFF.getBytes()));

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue("content");

		Assert.assertEquals(
			"<p>Hola <b>mundo</b> &amp; mas</p>",
			infoFieldValue.getValue(LocaleUtil.SPAIN));
	}

	@Test
	public void testImportXLIFF12VersionDocument() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"example-1_2-oasis.xlf"));

		Assert.assertNotNull(infoItemFieldValues);
		Assert.assertNotNull(infoItemFieldValues.getInfoFieldValues());

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertFalse(infoFieldValues.isEmpty());
	}

	@Test
	public void testImportXLIFF12VersionSimpleDocument() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"example-1_2-simple.xlf"));

		Assert.assertNotNull(infoItemFieldValues);
		Assert.assertNotNull(infoItemFieldValues.getInfoFieldValues());

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertFalse(infoFieldValues.isEmpty());
	}

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-102730"))
	@Test(expected = XLIFFFileException.MustBeValid.class)
	public void testImportXLIFF20FailsFileInlineCodeWithoutOriginalData()
		throws Exception {

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(JournalArticle.class.getName(), 122),
			new ByteArrayInputStream(
				_INLINE_CODES_NO_ORIGINAL_DATA_XLIFF.getBytes()));
	}

	@Test(expected = XLIFFFileException.MustBeSupportedLanguage.class)
	public void testImportXLIFF20FailsFileInvalidGroupLanguage()
		throws Exception {

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), _locales, LocaleUtil.US);

		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(JournalArticle.class.getName(), 122),
			TranslationTestUtil.readFileToInputStream(
				"test-journal-article-122-ja-JP.xlf"));
	}

	@Test(expected = XLIFFFileException.MustHaveValidId.class)
	public void testImportXLIFF20FailsFileInvalidId() throws Exception {
		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(
				JournalArticle.class.getName(), RandomTestUtil.randomInt(1, 3)),
			TranslationTestUtil.readFileToInputStream(
				"test-journal-article-122.xlf"));
	}

	@Test(expected = XLIFFFileException.MustBeSupportedLanguage.class)
	public void testImportXLIFF20FailsFileInvalidLanguage() throws Exception {
		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(JournalArticle.class.getName(), 122),
			TranslationTestUtil.readFileToInputStream(
				"test-journal-article-122-pt-PT.xlf"));
	}

	@Test(expected = XLIFFFileException.MustBeWellFormed.class)
	public void testImportXLIFF20FailsFileNoTarget() throws Exception {
		_xliffTranslationInfoItemFieldValuesImporter.importInfoItemFieldValues(
			_group.getGroupId(),
			new InfoItemReference(JournalArticle.class.getName(), 122),
			TranslationTestUtil.readFileToInputStream(
				"test-journal-article-no-target.xlf"));
	}

	@Test
	public void testImportXLIFF20IgnoresEmptyTarget() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"test-journal-article-122-empty-target.xlf"));

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertEquals(
			infoFieldValues.toString(), 1, infoFieldValues.size());
	}

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-102730"))
	@Test
	public void testImportXLIFF20IgnoresInlineCodeOnlyTarget()
		throws Exception {

		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					new ByteArrayInputStream(
						_INLINE_CODE_ONLY_TARGET_XLIFF.getBytes()));

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertEquals(
			infoFieldValues.toString(), 0, infoFieldValues.size());
	}

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-102730"))
	@Test
	public void testImportXLIFF20PreservesInlineCodes() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					new ByteArrayInputStream(_INLINE_CODES_XLIFF.getBytes()));

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertEquals(
			infoFieldValues.toString(), 2, infoFieldValues.size());

		InfoFieldValue<Object> contentInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("content");

		Assert.assertEquals(
			"<p>Hola <b>mundo</b> &amp; mas</p>",
			contentInfoFieldValue.getValue(LocaleUtil.SPAIN));

		InfoFieldValue<Object> imageInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("image");

		Assert.assertEquals(
			"<img src=\"/images/logo.png\"/>",
			imageInfoFieldValue.getValue(LocaleUtil.SPAIN));
	}

	@Test
	public void testImportXLIFF20VersionDocument() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					TranslationTestUtil.readFileToInputStream(
						"test-journal-article-122.xlf"));

		Assert.assertNotNull(infoItemFieldValues);
		Assert.assertNotNull(infoItemFieldValues.getInfoFieldValues());

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertFalse(infoFieldValues.isEmpty());
	}

	@FeatureFlags(
		featureFlags = @FeatureFlag(enable = false, value = "LPD-102730")
	)
	@Test
	public void testImportXLIFF20WithFeatureFlagDisabled() throws Exception {
		_testImportXLIFF20WithInlineCodes();
		_testImportXLIFF20WithInvalidInlineCodes();
	}

	private void _testImportXLIFF20WithInlineCodes() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					new ByteArrayInputStream(_INLINE_CODES_XLIFF.getBytes()));

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		Assert.assertEquals(
			infoFieldValues.toString(), 2, infoFieldValues.size());

		InfoFieldValue<Object> contentInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("content");

		Assert.assertEquals(
			"<p>Hola <b>mundo</b> &amp; mas</p>",
			contentInfoFieldValue.getValue(LocaleUtil.SPAIN));

		InfoFieldValue<Object> imageInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("image");

		Assert.assertEquals(
			"<img src=\"/images/logo.png\"/>",
			imageInfoFieldValue.getValue(LocaleUtil.SPAIN));
	}

	private void _testImportXLIFF20WithInvalidInlineCodes() throws Exception {
		InfoItemFieldValues infoItemFieldValues =
			_xliffTranslationInfoItemFieldValuesImporter.
				importInfoItemFieldValues(
					_group.getGroupId(),
					new InfoItemReference(JournalArticle.class.getName(), 122),
					new ByteArrayInputStream(
						_INLINE_CODES_NO_ORIGINAL_DATA_XLIFF.getBytes()));

		InfoFieldValue<Object> contentInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("content");

		Assert.assertEquals(
			"Hola mundo", contentInfoFieldValue.getValue(LocaleUtil.SPAIN));
	}

	private static final String _INLINE_CODE_ONLY_TARGET_XLIFF =
		StringBundler.concat(
			"<?xml version=\"1.0\"?>\n\n<xliff srcLang=\"en-US\" trgLang=",
			"\"es-ES\" version=\"2.0\" xmlns=",
			"\"urn:oasis:names:tc:xliff:document:2.0\">\n\t<file id=",
			"\"com.liferay.journal.model.JournalArticle:122\">\n\t\t<unit id=",
			"\"JournalArticle_content\">\n\t\t\t<originalData>\n\t\t\t\t",
			"<data id=\"d1\">&lt;br/&gt;</data>\n\t\t\t</originalData>\n\t\t",
			"\t<segment>\n\t\t\t\t<source>Hello<ph dataRef=\"d1\" id=\"1\"/>",
			"</source>\n\t\t\t\t<target><ph dataRef=\"d1\" id=\"1\"/></target",
			">\n\t\t\t</segment>\n\t\t</unit>\n\t</file>\n</xliff>");

	private static final String _INLINE_CODES_NO_ORIGINAL_DATA_V12_XLIFF =
		StringBundler.concat(
			"<?xml version=\"1.0\"?>\n\n<xliff version=\"1.2\" xmlns=",
			"\"urn:oasis:names:tc:xliff:document:1.2\">\n\t<file datatype=",
			"\"html\" original=\"com.liferay.journal.model.JournalArticle:122",
			"\" source-language=\"en-US\" target-language=\"es-ES\" tool=",
			"\"Liferay\">\n\t\t<body>\n\t\t\t<trans-unit id=",
			"\"JournalArticle_content\">\n\t\t\t\t<source xml:lang=\"en-US\">",
			"<bpt id=\"1\" rid=\"1\"/>Hello<ept id=\"2\" rid=\"1\"/></source>",
			"\n\t\t\t\t<target xml:lang=\"es-ES\"><bpt id=\"1\" rid=\"1",
			"\"/>Hola<ept id=\"2\" rid=\"1\"/></target>\n\t\t\t</trans-unit>",
			"\n\t\t</body>\n\t</file>\n</xliff>");

	private static final String _INLINE_CODES_NO_ORIGINAL_DATA_XLIFF =
		StringBundler.concat(
			"<?xml version=\"1.0\"?>\n\n<xliff srcLang=\"en-US\" trgLang=",
			"\"es-ES\" version=\"2.0\" xmlns=",
			"\"urn:oasis:names:tc:xliff:document:2.0\">\n\t<file id=",
			"\"com.liferay.journal.model.JournalArticle:122\">\n\t\t<unit id=",
			"\"JournalArticle_content\">\n\t\t\t<segment>\n\t\t\t\t<source>",
			"<pc id=\"1\">Hello <pc id=\"2\">world</pc></pc></source>\n\t\t\t",
			"\t<target><pc id=\"1\">Hola <pc id=\"2\">mundo</pc></pc></target",
			">\n\t\t\t</segment>\n\t\t</unit>\n\t</file>\n</xliff>");

	private static final String _INLINE_CODES_V12_XLIFF = StringBundler.concat(
		"<?xml version=\"1.0\"?>\n\n<xliff version=\"1.2\" xmlns=",
		"\"urn:oasis:names:tc:xliff:document:1.2\">\n\t<file datatype=\"html",
		"\" original=\"com.liferay.journal.model.JournalArticle:122",
		"\" source-language=\"en-US\" target-language=\"es-ES\" tool=",
		"\"Liferay\">\n\t\t<body>\n\t\t\t<trans-unit id=",
		"\"JournalArticle_content\">\n\t\t\t\t<source xml:lang=\"en-US\">",
		"<bpt id=\"1\" rid=\"1\">&lt;p&gt;</bpt>Hello <bpt id=\"2\" rid=\"2\"",
		">&lt;b&gt;</bpt>world<ept id=\"3\" rid=\"2\">&lt;/b&gt;</ept",
		"> <ph id=\"4\">&amp;amp;</ph> more<ept id=\"5\" rid=\"1\">&lt;/p&gt;",
		"</ept></source>\n\t\t\t\t<target xml:lang=\"es-ES\"><bpt id=\"1",
		"\" rid=\"1\">&lt;p&gt;</bpt>Hola <bpt id=\"2\" rid=\"2\">&lt;b&gt;",
		"</bpt>mundo<ept id=\"3\" rid=\"2\">&lt;/b&gt;</ept> <ph id=\"4\">",
		"&amp;amp;</ph> mas<ept id=\"5\" rid=\"1\">&lt;/p&gt;</ept></target>",
		"\n\t\t\t</trans-unit>\n\t\t</body>\n\t</file>\n</xliff>");

	private static final String _INLINE_CODES_XLIFF = StringBundler.concat(
		"<?xml version=\"1.0\"?>\n\n<xliff srcLang=\"en-US\" trgLang=\"es-ES",
		"\" version=\"2.0\" xmlns=\"urn:oasis:names:tc:xliff:document:2.0\">",
		"\n\t<file id=\"com.liferay.journal.model.JournalArticle:122\">\n\t\t",
		"<unit id=\"JournalArticle_content\">\n\t\t\t<originalData>\n\t\t\t\t",
		"<data id=\"d1\">&lt;p&gt;</data>\n\t\t\t\t<data id=\"d2\">&lt;/p&gt;",
		"</data>\n\t\t\t\t<data id=\"d3\">&lt;b&gt;</data>\n\t\t\t\t<data id=",
		"\"d4\">&lt;/b&gt;</data>\n\t\t\t\t<data id=\"d5\">&amp;amp;</data>\n",
		"\t\t\t</originalData>\n\t\t\t<segment>\n\t\t\t\t<source>",
		"<pc dataRefEnd=\"d2\" dataRefStart=\"d1\" id=\"1\"",
		">Hello <pc dataRefEnd=\"d4\" dataRefStart=\"d3\" id=\"2\">world</pc",
		"> <ph dataRef=\"d5\" id=\"3\"/> more</pc></source>\n\t\t\t\t<target>",
		"<pc dataRefEnd=\"d2\" dataRefStart=\"d1\" id=\"1\"",
		">Hola <pc dataRefEnd=\"d4\" dataRefStart=\"d3\" id=\"2\">mundo</pc",
		"> <ph dataRef=\"d5\" id=\"3\"/> mas</pc></target>\n\t\t\t</segment>",
		"\n\t\t</unit>\n\t\t<unit id=\"JournalArticle_image\">\n\t\t\t",
		"<originalData>\n\t\t\t\t<data id=\"d1\">&lt;img src=",
		"\"/images/logo.png\"/&gt;</data>\n\t\t\t</originalData>\n\t\t\t",
		"<segment>\n\t\t\t\t<source><ph dataRef=\"d1\" id=\"1\"/></source>\n",
		"\t\t\t\t<target><ph dataRef=\"d1\" id=\"1\"/></target>\n\t\t\t",
		"</segment>\n\t\t</unit>\n\t</file>\n</xliff>");

	private static final Set<Locale> _locales = new HashSet<>(
		Arrays.asList(
			LocaleUtil.BRAZIL, LocaleUtil.HUNGARY, LocaleUtil.SPAIN,
			LocaleUtil.US));

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "content.type=application/xliff+xml")
	private TranslationInfoItemFieldValuesImporter
		_xliffTranslationInfoItemFieldValuesImporter;

}