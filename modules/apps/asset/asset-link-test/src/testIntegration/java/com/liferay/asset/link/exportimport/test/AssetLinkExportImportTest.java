/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.link.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.model.adapter.StagedAssetLink;
import com.liferay.asset.link.service.AssetLinkLocalServiceUtil;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.test.util.BookmarksTestUtil;
import com.liferay.exportimport.configuration.ExportImportServiceConfiguration;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.test.util.lar.BaseExportImportTestCase;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Akos Thurzo
 */
@RunWith(Arquillian.class)
public class AssetLinkExportImportTest extends BaseExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());

		_journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_bookmarksEntry = BookmarksTestUtil.addEntry(group.getGroupId(), true);

		addAssetLink(_journalArticle, _bookmarksEntry, 1);

		_configurationProvider.saveCompanyConfiguration(
			ExportImportServiceConfiguration.class, group.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"includeAllAssetLinks", true
			).build());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_configurationProvider.deleteCompanyConfiguration(
			ExportImportServiceConfiguration.class, group.getCompanyId());
	}

	@Test
	public void testBothAssetEntriesExported() throws Exception {
		exportImportLayouts(
			new long[] {layout.getLayoutId()}, getImportParameterMap());

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
			importedGroup.getGroupId(),
			_journalArticle.getArticleResourceUuid());

		List<AssetLink> assetLinks = AssetLinkLocalServiceUtil.getLinks(
			assetEntry.getEntryId());

		Assert.assertNotNull(assetLinks);
		Assert.assertTrue(!assetLinks.isEmpty());
	}

	@Test
	@TestInfo("LPS-83011")
	public void testNoAssetLinkExported() throws Exception {
		_configurationProvider.saveCompanyConfiguration(
			ExportImportServiceConfiguration.class, group.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"includeAllAssetLinks", false
			).build());

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()});
		exportParameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.FALSE.toString()});

		exportLayouts(new long[] {layout.getLayoutId()}, exportParameterMap);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			group.getGroupId(), _journalArticle.getArticleResourceUuid());

		List<AssetLink> assetLinks = AssetLinkLocalServiceUtil.getLinks(
			assetEntry.getEntryId());

		Assert.assertFalse(assetLinks.toString(), assetLinks.isEmpty());

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(larFile)) {
			PortletDataContext portletDataContext = getPortletDataContext(
				zipReader);

			for (AssetLink assetLink : assetLinks) {
				StagedAssetLink stagedAssetLink = ModelAdapterUtil.adapt(
					assetLink, AssetLink.class, StagedAssetLink.class);

				Assert.assertNull(
					portletDataContext.getZipEntryAsObject(
						ExportImportPathUtil.getModelPath(stagedAssetLink)));
			}
		}
	}

	@Test
	public void testOnlyAssetLinkExported() throws Exception {
		long[] layoutIds = {layout.getLayoutId()};

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()});
		exportParameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.FALSE.toString()});

		exportLayouts(layoutIds, exportParameterMap);

		checkAssetLinksInLar(_journalArticle.getArticleResourceUuid());

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(larFile)) {

			// Journal article should not be in the LAR

			PortletDataContext portletDataContext = getPortletDataContext(
				zipReader);

			_journalArticle =
				(JournalArticle)portletDataContext.getZipEntryAsObject(
					ExportImportPathUtil.getModelPath(_journalArticle));

			Assert.assertNull(_journalArticle);

			// BookmarksE etry should not be in the LAR

			_bookmarksEntry =
				(BookmarksEntry)portletDataContext.getZipEntryAsObject(
					ExportImportPathUtil.getModelPath(_bookmarksEntry));

			Assert.assertNull(_bookmarksEntry);

			importLayouts(getImportParameterMap());
		}
	}

	@Test
	public void testOnlyOneAssetEntryExported() throws Exception {
		long[] layoutIds = {layout.getLayoutId()};

		Map<String, String[]> exportParameterMap = getExportParameterMap();

		exportParameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});
		exportParameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
				JournalPortletKeys.JOURNAL,
			new String[] {Boolean.TRUE.toString()});
		exportParameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.FALSE.toString()});

		exportLayouts(layoutIds, exportParameterMap);

		checkAssetLinksInLar(_journalArticle.getArticleResourceUuid());

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(larFile)) {

			// Journal article should be in the LAR

			PortletDataContext portletDataContext = getPortletDataContext(
				zipReader);

			_journalArticle =
				(JournalArticle)portletDataContext.getZipEntryAsObject(
					ExportImportPathUtil.getModelPath(_journalArticle));

			Assert.assertNotNull(_journalArticle);

			// Bookmarks entry should not be in the LAR

			_bookmarksEntry =
				(BookmarksEntry)portletDataContext.getZipEntryAsObject(
					ExportImportPathUtil.getModelPath(_bookmarksEntry));

			Assert.assertNull(_bookmarksEntry);

			importLayouts(getImportParameterMap());
		}
	}

	@Test
	public void testStaging() throws Exception {
		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		Group stagingGroup = group.getStagingGroup();

		JournalArticle article = JournalTestUtil.addArticle(
			stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		BookmarksEntry bookmarksEntry = BookmarksTestUtil.addEntry(
			stagingGroup.getGroupId(), true);

		addAssetLink(article, bookmarksEntry, 1);

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.FALSE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE,
			new String[] {Boolean.FALSE.toString()});

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			group.getGroupId(), false, parameterMap);

		JournalArticle liveJournalArticle =
			JournalArticleLocalServiceUtil.fetchJournalArticleByUuidAndGroupId(
				article.getUuid(), group.getGroupId());

		Assert.assertNull(liveJournalArticle);

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()});

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			group.getGroupId(), false, parameterMap);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			group.getGroupId(), article.getArticleResourceUuid());

		Assert.assertNotNull(assetEntry);

		List<AssetLink> assetLinks = AssetLinkLocalServiceUtil.getLinks(
			assetEntry.getEntryId());

		Assert.assertNotNull(assetLinks);
		Assert.assertEquals(assetLinks.toString(), 2, assetLinks.size());
	}

	protected void checkAssetLinksInLar(String assetEntryClassUuid)
		throws Exception {

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			group.getGroupId(), assetEntryClassUuid);

		Assert.assertNotNull(assetEntry);

		List<AssetLink> assetLinks = AssetLinkLocalServiceUtil.getLinks(
			assetEntry.getEntryId());

		Assert.assertNotNull(assetLinks);
		Assert.assertTrue(assetLinks.size() == 2);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(larFile)) {
			PortletDataContext portletDataContext = getPortletDataContext(
				zipReader);

			for (AssetLink assetLink : assetLinks) {
				StagedAssetLink stagedAssetLink = ModelAdapterUtil.adapt(
					assetLink, AssetLink.class, StagedAssetLink.class);

				stagedAssetLink =
					(StagedAssetLink)portletDataContext.getZipEntryAsObject(
						ExportImportPathUtil.getModelPath(stagedAssetLink));

				Assert.assertNotNull(stagedAssetLink);
			}
		}
	}

	protected PortletDataContext getPortletDataContext(ZipReader zipReader)
		throws Exception {

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		String userIdStrategyString = MapUtil.getString(
			parameterMap, PortletDataHandlerKeys.USER_ID_STRATEGY);

		return PortletDataContextFactoryUtil.createImportPortletDataContext(
			group.getCompanyId(), importedGroup.getGroupId(), parameterMap,
			ExportImportHelperUtil.getUserIdStrategy(
				TestPropsValues.getUserId(), userIdStrategyString),
			zipReader);
	}

	private BookmarksEntry _bookmarksEntry;

	@Inject
	private ConfigurationProvider _configurationProvider;

	private JournalArticle _journalArticle;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}