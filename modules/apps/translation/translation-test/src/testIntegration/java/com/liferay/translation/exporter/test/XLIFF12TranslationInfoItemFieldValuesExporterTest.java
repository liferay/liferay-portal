/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.test.util.TranslationTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class XLIFF12TranslationInfoItemFieldValuesExporterTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testExportReturnsEmptyTargetForUntranslatedField()
		throws Exception {

		InfoItemFieldValuesProvider<JournalArticle>
			infoItemFieldValuesProvider =
				(InfoItemFieldValuesProvider<JournalArticle>)
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFieldValuesProvider.class,
						JournalArticle.class.getName());

		String xliff = StreamUtil.toString(
			_xliffTranslationInfoItemFieldValuesExporter.
				exportInfoItemFieldValues(
					infoItemFieldValuesProvider.getInfoItemFieldValues(
						TranslationTestUtil.getJournalArticle(
							_group, _ddmFormDeserializer)),
					LocaleUtil.getDefault(),
					LocaleUtil.fromLanguageId("es_ES")));

		Assert.assertTrue(
			xliff, xliff.contains("<![CDATA[Test Article]]></source>"));
		Assert.assertFalse(
			xliff, xliff.contains("<![CDATA[Test Article]]></target>"));
		Assert.assertTrue(
			xliff, xliff.contains("<target xml:lang=\"es-ES\"></target>"));
		Assert.assertTrue(
			xliff, xliff.contains("<![CDATA[Un poco de texto]]></target>"));
	}

	@Test
	public void testExportReturnsTheXLIFFRepresentationOfAJournalArticle()
		throws Exception {

		InfoItemFieldValuesProvider<JournalArticle>
			infoItemFieldValuesProvider =
				(InfoItemFieldValuesProvider<JournalArticle>)
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFieldValuesProvider.class,
						JournalArticle.class.getName());

		JournalArticle journalArticle = TranslationTestUtil.getJournalArticle(
			_group, _ddmFormDeserializer);

		Assert.assertEquals(
			TranslationTestUtil.toFormattedString(
				StringUtil.replace(
					TranslationTestUtil.readFileToString(
						"test-journal-article-v12.xlf"),
					"[$JOURNAL_ARTICLE_ID$]",
					String.valueOf(journalArticle.getResourcePrimKey()))),
			TranslationTestUtil.toFormattedString(
				StreamUtil.toString(
					_xliffTranslationInfoItemFieldValuesExporter.
						exportInfoItemFieldValues(
							infoItemFieldValuesProvider.getInfoItemFieldValues(
								journalArticle),
							LocaleUtil.getDefault(),
							LocaleUtil.fromLanguageId("es_ES")))));
	}

	@Inject(filter = "ddm.form.deserializer.type=json")
	private DDMFormDeserializer _ddmFormDeserializer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject(filter = "content.type=application/x-xliff+xml")
	private TranslationInfoItemFieldValuesExporter
		_xliffTranslationInfoItemFieldValuesExporter;

}