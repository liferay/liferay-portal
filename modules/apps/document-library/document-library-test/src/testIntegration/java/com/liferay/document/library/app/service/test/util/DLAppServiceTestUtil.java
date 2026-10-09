/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.app.service.test.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.security.permission.DoAsUserThread;

import java.util.Date;
import java.util.Dictionary;

import org.junit.Assert;

/**
 * @author Alexander Chow
 */
public class DLAppServiceTestUtil {

	public static final String FILE_NAME = "Title.txt";

	public static final String STRIPPED_FILE_NAME = "Title";

	public static FileEntry addFileEntry(long groupId, long folderId)
		throws Exception {

		return addFileEntry(
			RandomTestUtil.randomString(), groupId, folderId, FILE_NAME,
			FILE_NAME, null, null, null, null);
	}

	public static FileEntry addFileEntry(
			long groupId, long folderId, String fileName)
		throws Exception {

		return addFileEntry(
			RandomTestUtil.randomString(), groupId, folderId, fileName,
			fileName, null, null, null, null);
	}

	public static FileEntry addFileEntry(
			String externalReferenceCode, long groupId, long folderId,
			String fileName, String title, Date displayDate,
			Date expirationDate, Date reviewDate, String[] assetTagNames)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setAssetTagNames(assetTagNames);

		return DLAppServiceUtil.addFileEntry(
			externalReferenceCode, groupId, folderId, fileName,
			ContentTypes.TEXT_PLAIN, title, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, BaseDLAppTestCase.CONTENT.getBytes(), displayDate,
			expirationDate, reviewDate, serviceContext);
	}

	public static ConfigurationTemporarySwapper
			getConfigurationTemporarySwapper(String key, Object value)
		throws Exception {

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				key, value
			).build();

		return new ConfigurationTemporarySwapper(
			"com.liferay.document.library.configuration.DLConfiguration",
			dictionary);
	}

	public static int runUserThreads(DoAsUserThread[] doAsUserThreads)
		throws Exception {

		for (DoAsUserThread doAsUserThread : doAsUserThreads) {
			doAsUserThread.start();
		}

		for (DoAsUserThread doAsUserThread : doAsUserThreads) {
			doAsUserThread.join();
		}

		int successCount = 0;

		for (DoAsUserThread doAsUserThread : doAsUserThreads) {
			if (!doAsUserThread.isSuccess()) {
				continue;
			}

			successCount++;
		}

		return successCount;
	}

	public static void search(
			FileEntry fileEntry, String keywords, boolean expected)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute("paginationType", "regular");
		searchContext.setCompanyId(fileEntry.getCompanyId());
		searchContext.setFolderIds(new long[] {fileEntry.getFolderId()});
		searchContext.setGroupIds(new long[] {fileEntry.getRepositoryId()});
		searchContext.setKeywords(keywords);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.getIndexer(
			DLFileEntryConstants.getClassName());

		Hits hits = indexer.search(searchContext);

		boolean found = false;

		for (Document document : hits.getDocs()) {
			long fileEntryId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			if (fileEntryId == fileEntry.getFileEntryId()) {
				found = true;

				break;
			}
		}

		Assert.assertEquals(hits.toString(), expected, found);
	}

	public static void searchFile(long groupId, long folderId)
		throws Exception {

		FileEntry fileEntry = addFileEntry(groupId, folderId);

		search(fileEntry, "title", true);
		search(fileEntry, "content", true);

		DLAppServiceUtil.deleteFileEntry(fileEntry.getFileEntryId());
	}

	public static FileEntry updateFileEntry(
			long groupId, long fileEntryId, String fileName, Date displayDate,
			Date expirationDate, Date reviewDate, boolean majorVersion)
		throws Exception {

		return DLAppServiceUtil.updateFileEntry(
			fileEntryId, fileName, ContentTypes.TEXT_PLAIN, fileName,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			DLVersionNumberIncrease.fromMajorVersion(majorVersion),
			TestDataConstants.TEST_BYTE_ARRAY, displayDate, expirationDate,
			reviewDate, ServiceContextTestUtil.getServiceContext(groupId));
	}

}