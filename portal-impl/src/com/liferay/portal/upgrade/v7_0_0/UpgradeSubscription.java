/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Map;

/**
 * @author Eduardo García
 * @author Roberto Díaz
 * @author Iván Zaera
 */
public class UpgradeSubscription extends UpgradeProcess {

	protected void addClassName(long classNameId, String className)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into ClassName_ (mvccVersion, classNameId, value) " +
					"values (?, ?, ?)")) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, classNameId);
			preparedStatement.setString(3, className);

			preparedStatement.executeUpdate();
		}
	}

	protected void deleteOrphanedSubscriptions() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			runSQL(
				StringBundler.concat(
					"delete from Subscription where classNameId = ",
					PortalUtil.getClassNameId(
						PortletPreferences.class.getName()),
					" and classPK not in (select portletPreferencesId from ",
					"PortletPreferences)"));
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		deleteOrphanedSubscriptions();

		updateSubscriptionClassNames(
			Folder.class.getName(), DLFolder.class.getName());

		updateSubscriptionGroupIds();
	}

	protected long getClassNameId(String className) throws Exception {
		long classNameId = PortalUtil.getClassNameId(className);

		if (classNameId != 0) {
			return classNameId;
		}

		classNameId = increment();

		addClassName(classNameId, className);

		return classNameId;
	}

	protected long getGroupId(long classNameId, long classPK) throws Exception {
		String className = PortalUtil.getClassName(classNameId);

		String[] groupIdSQLParts = StringUtil.split(
			_getGroupIdSQLPartsMap.get(className));

		if (ArrayUtil.isEmpty(groupIdSQLParts)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to determine the group ID for the class name " +
						className);
			}

			return 0;
		}

		String tableName = groupIdSQLParts[0];

		String sql = StringBundler.concat(
			"select ", groupIdSQLParts[1], " from ", tableName, " where ",
			groupIdSQLParts[2], " = ?");

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				sql)) {

			preparedStatement1.setLong(1, classPK);

			try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
				if (resultSet1.next()) {
					if (tableName.equals("PortletPreferences")) {
						long plid = resultSet1.getLong("plid");

						try (PreparedStatement preparedStatement2 =
								connection.prepareStatement(
									"select groupId from Layout where plid = " +
										"?")) {

							preparedStatement2.setLong(1, plid);

							try (ResultSet resultSet2 =
									preparedStatement2.executeQuery()) {

								if (resultSet2.next()) {
									return resultSet2.getLong("groupId");
								}
							}
						}
					}
					else {
						return resultSet1.getLong("groupId");
					}
				}
			}
		}

		return 0;
	}

	protected boolean hasGroup(long groupId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) as count from Group_ where groupId = ?")) {

			preparedStatement.setLong(1, groupId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next() && (resultSet.getLong("count") > 0)) {
					return true;
				}
			}

			return false;
		}
	}

	protected void updateSubscriptionClassNames(
			String oldClassName, String newClassName)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer(oldClassName)) {
			runSQL(
				StringBundler.concat(
					"update Subscription set classNameId = ",
					getClassNameId(newClassName), " where classNameId = ",
					PortalUtil.getClassNameId(oldClassName)));
		}
	}

	protected void updateSubscriptionGroupIds() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select subscriptionId, classNameId, classPK from " +
					"Subscription");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update Subscription set groupId = ? where " +
						"subscriptionId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long classNameId = resultSet.getLong("classNameId");
				long classPK = resultSet.getLong("classPK");

				long groupId = getGroupId(classNameId, classPK);

				if ((groupId == 0) && hasGroup(classPK)) {
					groupId = classPK;
				}

				if (groupId == 0) {
					continue;
				}

				preparedStatement2.setLong(1, groupId);

				long subscriptionId = resultSet.getLong("subscriptionId");

				preparedStatement2.setLong(2, subscriptionId);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeSubscription.class);

	private static final Map<String, String> _getGroupIdSQLPartsMap =
		HashMapBuilder.put(
			DLFileEntry.class.getName(), "DLFileEntry,groupId,fileEntryId"
		).put(
			DLFileEntryType.class.getName(),
			"DLFileEntryType,groupId,fileEntryTypeId"
		).put(
			DLFolder.class.getName(), "DLFolder,groupId,folderId"
		).put(
			Layout.class.getName(), "Layout,groupId,plid"
		).put(
			"com.liferay.calendar.model.CalendarBooking",
			"CalendarBooking,groupId,calendarBookingId"
		).put(
			"com.liferay.knowledgebase.model.KBArticle",
			"KBArticle,groupId,kbArticleId"
		).put(
			"com.liferay.message.boards.kernel.model.MBCategory",
			"MBCategory,groupId,categoryId"
		).put(
			"com.liferay.message.boards.kernel.model.MBThread",
			"MBThread,groupId,threadId"
		).put(
			WorkflowInstanceLink.class.getName(),
			"WorkflowInstanceLink,groupId,workflowInstanceId"
		).put(
			"com.liferay.blogs.kernel.model.BlogsEntry",
			"BlogsEntry,groupId,entryId"
		).put(
			"com.liferay.journal.model.JournalArticle",
			"JournalArticle,groupId,resourcePrimKey"
		).put(
			"com.liferay.journal.model.JournalFolder",
			"JournalFolder,groupId,folderId"
		).put(
			"com.liferay.portal.kernel.model.PortletPreferences",
			"PortletPreferences,plid,portletPreferencesId"
		).put(
			"com.liferay.portlet.bookmarks.model.BookmarksEntry",
			"BookmarksEntry,groupId,entryId"
		).put(
			"com.liferay.portlet.bookmarks.model.BookmarksFolder",
			"BookmarksFolder,groupId,folderId"
		).put(
			"com.liferay.portlet.dynamic.data.mapping.kernel.DDMStructure",
			"DDMStructure,groupId,structureId"
		).put(
			"com.liferay.portlet.wiki.model.WikiNode", "WikiNode,groupId,nodeId"
		).put(
			"com.liferay.portlet.wiki.model.WikiPage",
			"WikiPage,groupId,resourcePrimKey"
		).build();

}