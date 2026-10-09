/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test.util;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalServiceUtil;
import com.liferay.change.tracking.service.CTCollectionServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.List;

import org.junit.Assert;

/**
 * @author Brooke Dalton
 */
public class CTCollectionTestUtil {

	public static CTCollection createCTCollectionWithConflict(User user)
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			JournalFolderLocalServiceUtil.getService());

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			group.getGroupId(), RandomTestUtil.randomString());

		CTCollection ctCollection =
			CTCollectionLocalServiceUtil.addCTCollection(
				null, TestPropsValues.getCompanyId(), user.getUserId(), 0,
				RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			JournalTestUtil.addArticle(
				group.getGroupId(), journalFolder.getFolderId());
		}

		JournalFolderLocalServiceUtil.deleteFolder(journalFolder.getFolderId());

		return ctCollection;
	}

	public static CTCollection createCTCollectionWithIncompleteStatus(User user)
		throws Exception {

		CTCollection ctCollection =
			CTCollectionLocalServiceUtil.addCTCollection(
				null, TestPropsValues.getCompanyId(), user.getUserId(), 0,
				RandomTestUtil.randomString(), null);

		ctCollection.setStatus(WorkflowConstants.STATUS_INCOMPLETE);

		return CTCollectionLocalServiceUtil.updateCTCollection(ctCollection);
	}

	public static void publishCTCollectionWithError(long ctCollectionId)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.background.task.internal.messaging." +
					"BackgroundTaskMessageListener",
				LoggerTestUtil.ERROR)) {

			CTCollectionServiceUtil.publishCTCollection(
				TestPropsValues.getUserId(), ctCollectionId);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to execute background task", logEntry.getMessage());
		}
	}

	public static void updateCTCollectionSizeClassification(
			long ctCollectionId, long groupId, int score, User user)
		throws Exception {

		PortalCache<Long, Integer> portalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				"com.liferay.change.tracking.internal.score.CTScoreCache");

		portalCache.put(ctCollectionId, score);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			DDMStructureTestUtil.addStructure(
				groupId, DLFileEntryMetadata.class.getName());
		}
	}

}