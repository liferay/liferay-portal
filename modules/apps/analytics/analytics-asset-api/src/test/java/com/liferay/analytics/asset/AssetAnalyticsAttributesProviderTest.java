/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.asset;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.journal.model.JournalArticle;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class AssetAnalyticsAttributesProviderTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_objectDefinitionLocalServiceUtilMockedStatic.close();
		_snapshotMockedConstruction.close();
	}

	@Test
	@TestInfo("LPD-83537")
	public void testBuildAttributes() {
		_testBuildAttributesForBlogsEntry();
		_testBuildAttributesForDLFileEntry();
		_testBuildAttributesForJournalArticle();
		_testBuildAttributesForObjectEntry();
		_testBuildAttributesWithoutAssetEntry();
		_testBuildAttributesWithoutAssetRenderer();
		_testBuildAttributesWithoutField();
		_testBuildAttributesWithoutLocale();
	}

	private void _assertObjectDefinitionName(
		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider,
		String expectedObjectDefinitionName) {

		String attributes = assetAnalyticsAttributesProvider.buildAttributes(
			AssetAnalyticsAttributesProvider.ACTION_VIEW,
			AssetAnalyticsAttributesProvider.FIELD_CONTENT);

		if (expectedObjectDefinitionName == null) {
			Assert.assertFalse(
				attributes.contains("analytics-object-definition-name="));
		}
		else {
			Assert.assertTrue(
				attributes.contains(
					"analytics-object-definition-name=\"" +
						expectedObjectDefinitionName + "\""));
		}
	}

	private void _assertType(
		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider,
		String expectedType) {

		String attributes = assetAnalyticsAttributesProvider.buildAttributes(
			AssetAnalyticsAttributesProvider.ACTION_VIEW,
			AssetAnalyticsAttributesProvider.FIELD_CONTENT);

		Assert.assertTrue(
			attributes.contains(
				"analytics-asset-type=\"" + expectedType + "\""));
	}

	private AssetEntry _mockAssetEntry(
		String className, long classPK, long companyId) {

		AssetEntry assetEntry = Mockito.mock(AssetEntry.class);

		Mockito.when(
			assetEntry.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			assetEntry.getClassPK()
		).thenReturn(
			classPK
		);

		Mockito.when(
			assetEntry.getCompanyId()
		).thenReturn(
			companyId
		);

		return assetEntry;
	}

	private AssetRenderer<?> _mockAssetRenderer(String title) {
		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.when(
			assetRenderer.getTitle(LocaleUtil.US)
		).thenReturn(
			title
		);

		return assetRenderer;
	}

	private void _testBuildAttributesForBlogsEntry() {
		AssetEntry assetEntry = _mockAssetEntry(
			"com.liferay.blogs.model.BlogsEntry", RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong());

		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider =
			new AssetAnalyticsAttributesProvider(assetEntry, null, null);

		String attributes = assetAnalyticsAttributesProvider.buildAttributes(
			AssetAnalyticsAttributesProvider.ACTION_IMPRESSION,
			AssetAnalyticsAttributesProvider.FIELD_TITLE);

		Assert.assertFalse(attributes.contains("analytics-asset-subtype="));
		Assert.assertTrue(attributes.contains("analytics-asset-type=\"blog\""));
	}

	private void _testBuildAttributesForDLFileEntry() {
		AssetEntry assetEntry = _mockAssetEntry(
			"com.liferay.document.library.kernel.model.DLFileEntry",
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider =
			new AssetAnalyticsAttributesProvider(assetEntry, null, null);

		String attributes = assetAnalyticsAttributesProvider.buildAttributes(
			AssetAnalyticsAttributesProvider.ACTION_IMPRESSION,
			AssetAnalyticsAttributesProvider.FIELD_TITLE);

		Assert.assertTrue(
			attributes.contains("analytics-asset-type=\"document\""));
	}

	private void _testBuildAttributesForJournalArticle() {
		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		JournalArticle journalArticle = Mockito.mock(JournalArticle.class);

		String structureKey = RandomTestUtil.randomString();

		Mockito.when(
			journalArticle.getDDMStructureKey()
		).thenReturn(
			structureKey
		);

		Mockito.when(
			assetRenderer.getAssetObject()
		).thenReturn(
			journalArticle
		);

		String title = "Title \"quoted\" and <b>bold</b>";

		Mockito.when(
			assetRenderer.getTitle(LocaleUtil.US)
		).thenReturn(
			title
		);

		long journalArticleClassPK = RandomTestUtil.randomLong();

		AssetEntry assetEntry = _mockAssetEntry(
			_CLASS_NAME_JOURNAL_ARTICLE, journalArticleClassPK,
			RandomTestUtil.randomLong());

		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider =
			new AssetAnalyticsAttributesProvider(
				assetEntry, assetRenderer, LocaleUtil.US);

		String attributes = assetAnalyticsAttributesProvider.buildAttributes(
			AssetAnalyticsAttributesProvider.ACTION_VIEW,
			AssetAnalyticsAttributesProvider.FIELD_CONTENT);

		Assert.assertTrue(
			attributes.contains(
				"analytics-asset-action=\"" +
					AssetAnalyticsAttributesProvider.ACTION_VIEW + "\""));
		Assert.assertTrue(
			attributes.contains(
				"analytics-asset-field=\"" +
					AssetAnalyticsAttributesProvider.FIELD_CONTENT + "\""));
		Assert.assertTrue(
			attributes.contains(
				"analytics-asset-id=\"" + journalArticleClassPK + "\""));
		Assert.assertTrue(
			attributes.contains(
				"analytics-asset-subtype=\"" +
					StringUtil.toLowerCase(structureKey) + "\""));
		Assert.assertFalse(attributes.contains("\"quoted\""));
		Assert.assertFalse(attributes.contains("<b>"));
		Assert.assertTrue(attributes.contains("analytics-asset-title=\""));
		Assert.assertTrue(
			attributes.contains("analytics-asset-type=\"web-content\""));
	}

	private void _testBuildAttributesForObjectEntry() {
		String className = "com.liferay.object.model.ObjectDefinition#42";
		long companyId = RandomTestUtil.randomLong();

		AssetEntry assetEntry = _mockAssetEntry(
			className, RandomTestUtil.randomLong(), companyId);

		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider =
			new AssetAnalyticsAttributesProvider(assetEntry, null, null);

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		_objectDefinitionLocalServiceUtilMockedStatic.when(
			() ->
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByClassName(companyId, className)
		).thenReturn(
			objectDefinition
		);

		Mockito.when(
			objectDefinition.getName()
		).thenReturn(
			"MyCMSType"
		);

		_assertObjectDefinitionName(
			assetAnalyticsAttributesProvider, "my-cms-type");
		_assertType(assetAnalyticsAttributesProvider, "object-entry");

		Mockito.when(
			objectDefinition.getName()
		).thenReturn(
			null
		);

		_assertObjectDefinitionName(assetAnalyticsAttributesProvider, null);
		_assertType(assetAnalyticsAttributesProvider, "object-entry");
	}

	private void _testBuildAttributesWithoutAssetEntry() {
		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider =
			new AssetAnalyticsAttributesProvider(null, null, null);

		Assert.assertEquals(
			StringPool.BLANK,
			assetAnalyticsAttributesProvider.buildAttributes(
				AssetAnalyticsAttributesProvider.ACTION_IMPRESSION,
				AssetAnalyticsAttributesProvider.FIELD_TITLE));
	}

	private void _testBuildAttributesWithoutAssetRenderer() {
		AssetEntry assetEntry = _mockAssetEntry(
			_CLASS_NAME_JOURNAL_ARTICLE, RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong());

		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider =
			new AssetAnalyticsAttributesProvider(assetEntry, null, null);

		String attributes = assetAnalyticsAttributesProvider.buildAttributes(
			AssetAnalyticsAttributesProvider.ACTION_IMPRESSION,
			AssetAnalyticsAttributesProvider.FIELD_TITLE);

		Assert.assertFalse(attributes.contains("analytics-asset-title="));
	}

	private void _testBuildAttributesWithoutField() {
		AssetEntry assetEntry = _mockAssetEntry(
			_CLASS_NAME_JOURNAL_ARTICLE, RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong());

		AssetRenderer<?> assetRenderer = _mockAssetRenderer(
			RandomTestUtil.randomString());

		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider =
			new AssetAnalyticsAttributesProvider(
				assetEntry, assetRenderer, LocaleUtil.US);

		String attributes = assetAnalyticsAttributesProvider.buildAttributes(
			AssetAnalyticsAttributesProvider.ACTION_IMPRESSION, null);

		Assert.assertFalse(attributes.contains("analytics-asset-field="));
	}

	private void _testBuildAttributesWithoutLocale() {
		AssetEntry assetEntry = _mockAssetEntry(
			_CLASS_NAME_JOURNAL_ARTICLE, RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong());

		AssetRenderer<?> assetRenderer = _mockAssetRenderer(
			RandomTestUtil.randomString());

		AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider =
			new AssetAnalyticsAttributesProvider(
				assetEntry, assetRenderer, null);

		String attributes = assetAnalyticsAttributesProvider.buildAttributes(
			AssetAnalyticsAttributesProvider.ACTION_IMPRESSION,
			AssetAnalyticsAttributesProvider.FIELD_TITLE);

		Assert.assertFalse(attributes.contains("analytics-asset-title="));
	}

	private static final String _CLASS_NAME_JOURNAL_ARTICLE =
		"com.liferay.journal.model.JournalArticle";

	private final MockedStatic<ObjectDefinitionLocalServiceUtil>
		_objectDefinitionLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectDefinitionLocalServiceUtil.class);

	private final MockedConstruction<Snapshot> _snapshotMockedConstruction =
		Mockito.mockConstruction(
			Snapshot.class,
			(snapshot, context) -> {
				AnalyticsSettingsManager analyticsSettingsManager =
					Mockito.mock(AnalyticsSettingsManager.class);

				Mockito.when(
					analyticsSettingsManager.isAnalyticsEnabled(
						Mockito.anyLong())
				).thenReturn(
					true
				);

				Mockito.when(
					snapshot.get()
				).thenReturn(
					analyticsSettingsManager
				);
			});

}