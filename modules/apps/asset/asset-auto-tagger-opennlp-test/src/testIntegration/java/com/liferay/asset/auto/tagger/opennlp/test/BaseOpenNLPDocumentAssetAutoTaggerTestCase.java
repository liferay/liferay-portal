/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.opennlp.test;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Cristina González
 * @author Alejandro Tardín
 */
public abstract class BaseOpenNLPDocumentAssetAutoTaggerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAutoTagsAnAsset() throws Exception {
		testWithOpenNLPDocumentAssetAutoTagProviderEnabled(
			getClassName(),
			() -> {
				AssetEntry assetEntry = getAssetEntry(getTaggableText());

				Collection<String> actualTagNames = Arrays.asList(
					assetEntry.getTagNames());

				Collection<String> expectedTagNames = Arrays.asList(
					"ADVENTURES", "AT ALL.", "Adventures", "Ah", "Alice",
					"Alice .", "Archive Foundation", "Australia", "Bill",
					"CHAPTER", "Beau--ootiful", "Cheshire Cat",
					"Dr. Gregory B. Newby Chief Executive", "Edgar Atheling",
					"Foundation", "General Information About Project",
					"General Terms", "Geography", "Herald", "I", "IF", "IRS",
					"Internal Revenue Service", "King", "Latitude", "Laughing",
					"Lewis Carroll", "Lewis Carroll Posting Date",
					"Lewis Carroll This", "Lizard", "London", "MINE", "Ma !",
					"Mary Ann", "Michael Hart", "Michael S. Hart",
					"Mississippi", "NOT", "New Zealand", "Paris", "Pat",
					"Pat !", "Pepper", "Pray",
					"Project Gutenberg Literary Archive Foundation",
					"Project Gutenberg Literary Archive Foundation Project " +
						"Gutenberg-tm",
					"Project Gutenberg-tm", "Public Domain", "Queen", "Queens",
					"Rabbit", "Rome", "Salt Lake City", "Shakespeare", "Shark",
					"Soup", "THERE", "The", "United States", "VERY", "WOULD",
					"White Rabbit", "Whoever", "William", "YOU.--Come", "YOUR");

				Assert.assertEquals(
					actualTagNames.toString(), expectedTagNames.size(),
					actualTagNames.size());
				Assert.assertTrue(actualTagNames.containsAll(expectedTagNames));
			});
	}

	@Test
	public void testAutoTagsAnAssetInNotEnglishLanguage() throws Exception {
		testWithOpenNLPDocumentAssetAutoTagProviderEnabled(
			getClassName(),
			() -> {
				AssetEntry assetEntry = getAssetEntry(
					new String(
						FileUtil.getBytes(
							getClass(),
							"dependencies/" + _FILE_NAME_NOT_ENGLISH)));

				Collection<String> tagNames = Arrays.asList(
					assetEntry.getTagNames());

				Assert.assertEquals(tagNames.toString(), 0, tagNames.size());
			});
	}

	@Test
	public void testAutoTagsAnAssetReusingNameFinders() throws Exception {
		testWithOpenNLPDocumentAssetAutoTagProviderEnabled(
			getClassName(),
			() -> {
				getAssetEntry(getTaggableText());

				try (LogCapture logCapture =
						LoggerTestUtil.configureLog4JLogger(
							"opennlp.tools.util.XmlUtil",
							LoggerTestUtil.WARN)) {

					AssetEntry assetEntry = getAssetEntry(getTaggableText());

					Assert.assertTrue(
						ArrayUtil.isNotEmpty(assetEntry.getTagNames()));

					List<LogEntry> logEntries = logCapture.getLogEntries();

					Assert.assertEquals(
						logEntries.toString(), 0, logEntries.size());
				}
			});
	}

	@Test
	public void testDoesNotAutoTagAnAssetWhenNotEnabled() throws Exception {
		testWithOpenNLPDocumentAssetAutoTagProviderDisabled(
			() -> {
				AssetEntry assetEntry = getAssetEntry(getTaggableText());

				Collection<String> tagNames = Arrays.asList(
					assetEntry.getTagNames());

				Assert.assertEquals(tagNames.toString(), 0, tagNames.size());
			});
	}

	protected abstract AssetEntry getAssetEntry(String text) throws Exception;

	protected abstract String getClassName();

	protected final String getTaggableText() throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + _FILE_NAME));
	}

	protected void testWithOpenNLPDocumentAssetAutoTagProviderDisabled(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_OPEN_NLP_AUTO_TAG_CONFIGURATION_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"enabledClassNames", new String[0]
					).build())) {

			unsafeRunnable.run();
		}
	}

	protected void testWithOpenNLPDocumentAssetAutoTagProviderEnabled(
			String className, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					_OPEN_NLP_AUTO_TAG_CONFIGURATION_PID,
					HashMapDictionaryBuilder.<String, Object>put(
						"enabledClassNames", new String[] {className}
					).build())) {

			unsafeRunnable.run();
		}
	}

	@Inject
	protected AssetEntryLocalService assetEntryLocalService;

	@DeleteAfterTestRun
	protected Group group;

	private static final String _FILE_NAME =
		"Alice's Adventures in Wonderland, by Lewis Carroll.txt";

	private static final String _FILE_NAME_NOT_ENGLISH = "25328-0.txt";

	private static final String _OPEN_NLP_AUTO_TAG_CONFIGURATION_PID =
		"com.liferay.asset.auto.tagger.opennlp.internal.configuration." +
			"OpenNLPDocumentAssetAutoTaggerCompanyConfiguration";

}