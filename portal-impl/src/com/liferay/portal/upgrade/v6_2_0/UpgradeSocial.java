/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergio Sanchez
 * @author Zsolt Berentey
 * @author Daniel Sanz
 */
public class UpgradeSocial extends UpgradeProcess {

	protected String createExtraData(
			ExtraDataFactory extraDataFactory, long companyId, long groupId,
			long userId, long classNameId, long classPK, int type,
			String extraData)
		throws Exception {

		if (extraDataFactory == null) {
			return null;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				extraDataFactory.getSQL())) {

			extraDataFactory.setModelSQLParameters(
				preparedStatement, groupId, companyId, userId, classNameId,
				classPK, type, extraData);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					JSONObject extraDataJSONObject =
						extraDataFactory.createExtraDataJSONObject(
							resultSet, extraData);

					return extraDataJSONObject.toString();
				}

				return null;
			}
		}
	}

	protected Map<Long, String> createExtraDataMap(
			ExtraDataFactory extraDataFactory)
		throws Exception {

		Map<Long, String> extraDataMap = new HashMap<>();

		StringBundler sb = new StringBundler(4);

		sb.append("select activityId, groupId, companyId, userId, ");
		sb.append("classNameId, classPK, type_, extraData from ");
		sb.append("SocialActivity where ");
		sb.append(extraDataFactory.getActivitySQLWhereClause());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sb.toString())) {

			extraDataFactory.setActivitySQLParameters(preparedStatement);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long classNameId = resultSet.getLong("classNameId");
					long classPK = resultSet.getLong("classPK");
					long companyId = resultSet.getLong("companyId");
					String extraData = resultSet.getString("extraData");
					long groupId = resultSet.getLong("groupId");
					int type = resultSet.getInt("type_");
					long userId = resultSet.getLong("userId");

					String newExtraData = createExtraData(
						extraDataFactory, groupId, companyId, userId,
						classNameId, classPK, type, extraData);

					if (newExtraData == null) {
						continue;
					}

					long activityId = resultSet.getLong("activityId");

					extraDataMap.put(activityId, newExtraData);
				}
			}
		}

		return extraDataMap;
	}

	@Override
	protected void doUpgrade() throws Exception {
		updateJournalActivities();
		updateSOSocialActivities();

		updateActivities();
	}

	protected void updateActivities() throws Exception {
		ExtraDataFactory[] extraDataFactories = {
			new AddAssetCommentExtraDataFactory(),
			new AddMessageExtraDataFactory(), new BlogsEntryExtraDataFactory(),
			new BookmarksEntryExtraDataFactory(),
			new DLFileEntryExtraDataFactory(), new KBArticleExtraDataFactory(),
			new KBCommentExtraDataFactory(), new KBTemplateExtraDataFactory(),
			new WikiPageExtraDataFactory()
		};

		for (ExtraDataFactory extraDataFactory : extraDataFactories) {
			updateActivities(extraDataFactory);
		}
	}

	protected void updateActivities(ExtraDataFactory extraDataFactory)
		throws Exception {

		Map<Long, String> extraDataMap = createExtraDataMap(extraDataFactory);

		for (Map.Entry<Long, String> entry : extraDataMap.entrySet()) {
			long activityId = entry.getKey();
			String extraData = entry.getValue();

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"update SocialActivity set extraData = ? where " +
							"activityId = ?")) {

				preparedStatement.setString(1, extraData);
				preparedStatement.setLong(2, activityId);

				preparedStatement.executeUpdate();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to update activity " + activityId, exception);
				}
			}
		}
	}

	protected void updateJournalActivities() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			long classNameId = PortalUtil.getClassNameId(
				"com.liferay.portlet.journal.model.JournalArticle");

			String[] tableNames = {"SocialActivity", "SocialActivityCounter"};

			for (String tableName : tableNames) {
				StringBundler sb = new StringBundler(7);

				sb.append("update ");
				sb.append(tableName);
				sb.append(" set classPK = (select resourcePrimKey from ");
				sb.append("JournalArticle where id_ = ");
				sb.append(tableName);
				sb.append(".classPK) where classNameId = ");
				sb.append(classNameId);

				runSQL(sb.toString());
			}
		}
	}

	protected void updateSOSocialActivities() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			if (!hasTable("SO_SocialActivity")) {
				return;
			}

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select activityId, activitySetId from " +
							"SO_SocialActivity");

				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					long activityId = resultSet.getLong("activityId");
					long activitySetId = resultSet.getLong("activitySetId");

					StringBundler sb = new StringBundler(4);

					sb.append("update SocialActivity set activitySetId = ");
					sb.append(activitySetId);
					sb.append(" where activityId = ");
					sb.append(activityId);

					runSQL(sb.toString());
				}
			}

			dropTable("SO_SocialActivity");
		}
	}

	/**
	 * Defines the necessary methods to generate extra data from a set of social
	 * activities (<code>com.liferay.social.kernel.model.SocialActivity</code>
	 * instances) of any kind.
	 *
	 * <p>
	 * Implementations have to focus on:
	 * </p>
	 *
	 * <ol>
	 * <li>
	 * What is the set of social activities the factory generates extra data
	 * for. See {@link #getActivitySQLWhereClause()} and {@link
	 * #setActivitySQLParameters(PreparedStatement)}.
	 * </li>
	 * <li>
	 * How to obtain the model entities related to such activities. See
	 * {@link #getSQL()} and {@link
	 * #setModelSQLParameters(PreparedStatement, long, long, long, long,
	 * long, int, String)}.
	 * </li>
	 * <li>
	 * How to generate extra data from that model entity. See {@link
	 * #createExtraDataJSONObject(ResultSet, String)}.
	 * </li>
	 * </ol>
	 *
	 * <p>
	 * The ExtraData Framework works with a list of ExtraData factories. for
	 * each one, a query to SocialActivity table is built and run. The extra
	 * data for each <code>SocialActivity</code> tuple returned by the query is
	 * generated by querying the appropriate entity as dictated by the Factory
	 * and building the extra data from that entity tuple. Then the framework
	 * updates the extra data in the original <code>SocialActivity</code> tuple,
	 * so that the Social Activity Interpreters can find what they need after
	 * the upgrade.
	 * </p>
	 */
	protected interface ExtraDataFactory {

		/**
		 * Returns the JSON object containing the extra data. Given a result
		 * from the {@link #getSQL()} and the original extra data in the
		 * <code>SocialActivity</code> tuple pointing to that entity, this
		 * method computes the extra data that will be persisted in the
		 * <code>SocialActivity</code> tuple as a result of the upgrade process.
		 */
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException;

		/**
		 * Returns the class name of the entity whose activities the factory
		 * generates extra data for. The class name is useful if the factory
		 * needs to filter activities by class name ID or refer to other
		 * factories' class names.
		 */
		public String getActivityClassName();

		/**
		 * Returns the "where" clause in the social activity query to select the
		 * <code>SocialActivity</code> tuples the factory generates extra data
		 * for.
		 */
		public String getActivitySQLWhereClause();

		/**
		 * Returns the SQL query on any model entity which the selected
		 * <code>SocialActivity</code> tuples refer to. Extra data is generated
		 * from the entities returned by this query.
		 */
		public String getSQL();

		/**
		 * Sets parameters required to run the activity query returned by {@link
		 * #getActivitySQLWhereClause()} in the factory.
		 */
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException;

		/**
		 * Sets parameters required to run the entity query returned by {@link
		 * #getSQL()} in the factory, based on fields from the
		 * <code>SocialActivity</code> tuple.
		 */
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException;

	}

	private static final Log _log = LogFactoryUtil.getLog(UpgradeSocial.class);

	private class AddAssetCommentExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			long messageId = 0;

			try {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					extraData);

				messageId = jsonObject.getLong("messageId");
			}
			catch (JSONException jsonException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(jsonException);
				}
			}

			return JSONUtil.put(
				"messageId", messageId
			).put(
				"title", resultSet.getString("subject")
			);
		}

		@Override
		public String getActivityClassName() {
			return StringPool.BLANK;
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "type_ = ?";
		}

		@Override
		public String getSQL() {
			return "select subject from MBMessage where messageId = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setInt(1, _TYPE_ADD_COMMENT);
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			long messageId = 0;

			try {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					extraData);

				messageId = jsonObject.getLong("messageId");
			}
			catch (JSONException jsonException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(jsonException);
				}
			}

			preparedStatement.setLong(1, messageId);
		}

		private static final int _TYPE_ADD_COMMENT = 10005;

	}

	private class AddMessageExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			return JSONUtil.put("title", resultSet.getString("subject"));
		}

		@Override
		public String getActivityClassName() {
			return "com.liferay.portlet.messageboards.model.MBMessage";
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "classNameId = ? and (type_ = ? or type_ = ?)";
		}

		@Override
		public String getSQL() {
			return "select subject from MBMessage where messageId = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(getActivityClassName()));
			preparedStatement.setInt(2, _ADD_MESSAGE);
			preparedStatement.setInt(3, _REPLY_MESSAGE);
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			preparedStatement.setLong(1, classPK);
		}

		private static final int _ADD_MESSAGE = 1;

		private static final int _REPLY_MESSAGE = 2;

	}

	private class BlogsEntryExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			return JSONUtil.put("title", resultSet.getString("title"));
		}

		@Override
		public String getActivityClassName() {
			return "com.liferay.portlet.blogs.model.BlogsEntry";
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "classNameId = ? and (type_ = ? or type_ = ?)";
		}

		@Override
		public String getSQL() {
			return "select title from BlogsEntry where entryId = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(getActivityClassName()));
			preparedStatement.setInt(2, _ADD_ENTRY);
			preparedStatement.setInt(3, _UPDATE_ENTRY);
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			preparedStatement.setLong(1, classPK);
		}

		private static final int _ADD_ENTRY = 2;

		private static final int _UPDATE_ENTRY = 3;

	}

	private class BookmarksEntryExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			return JSONUtil.put("title", resultSet.getString("name"));
		}

		@Override
		public String getActivityClassName() {
			return "com.liferay.portlet.bookmarks.model.BookmarksEntry";
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "classNameId = ? and (type_ = ? or type_ = ?)";
		}

		@Override
		public String getSQL() {
			return "select name from BookmarksEntry where entryId = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(getActivityClassName()));
			preparedStatement.setInt(2, _ADD_ENTRY);
			preparedStatement.setInt(3, _UPDATE_ENTRY);
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			preparedStatement.setLong(1, classPK);
		}

		private static final int _ADD_ENTRY = 1;

		private static final int _UPDATE_ENTRY = 2;

	}

	private class DLFileEntryExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			return JSONUtil.put("title", resultSet.getString("title"));
		}

		@Override
		public String getActivityClassName() {
			return "com.liferay.portlet.documentlibrary.model.DLFileEntry";
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "classNameId = ?";
		}

		@Override
		public String getSQL() {
			return "select title from DLFileEntry where companyId = ? and " +
				"groupId = ? and fileEntryId = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(getActivityClassName()));
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setLong(2, groupId);
			preparedStatement.setLong(3, classPK);
		}

	}

	private class KBArticleExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			return JSONUtil.put("title", resultSet.getString("title"));
		}

		@Override
		public String getActivityClassName() {
			return "com.liferay.knowledgebase.model.KBArticle";
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "classNameId = ? and (type_ = ? or type_ = ? or type_ = ?)";
		}

		@Override
		public String getSQL() {
			return "select title from KBArticle where resourcePrimKey = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(getActivityClassName()));
			preparedStatement.setInt(2, _ADD_KB_ARTICLE);
			preparedStatement.setInt(3, _UPDATE_KB_ARTICLE);
			preparedStatement.setInt(4, _MOVE_KB_ARTICLE);
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			preparedStatement.setLong(1, classPK);
		}

		private static final int _ADD_KB_ARTICLE = 1;

		private static final int _MOVE_KB_ARTICLE = 7;

		private static final int _UPDATE_KB_ARTICLE = 3;

	}

	private class KBCommentExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			long classNameId = resultSet.getLong("classNameId");

			ExtraDataFactory extraDataFactory = null;

			if (classNameId == PortalUtil.getClassNameId(
					_kbArticleExtraDataFactory.getActivityClassName())) {

				extraDataFactory = _kbArticleExtraDataFactory;
			}
			else if (classNameId == PortalUtil.getClassNameId(
						_kbTemplateExtraDataFactory.getActivityClassName())) {

				extraDataFactory = _kbTemplateExtraDataFactory;
			}

			if (extraDataFactory == null) {
				return null;
			}

			long classPK = resultSet.getLong("classPK");

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(extraDataFactory.getSQL())) {

				preparedStatement.setLong(1, classPK);

				try (ResultSet curResultSet =
						preparedStatement.executeQuery()) {

					while (curResultSet.next()) {
						return extraDataFactory.createExtraDataJSONObject(
							curResultSet, StringPool.BLANK);
					}
				}
			}

			return null;
		}

		@Override
		public String getActivityClassName() {
			return "com.liferay.knowledgebase.model.KBComment";
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "classNameId = ? and (type_ = ? or type_ = ?)";
		}

		@Override
		public String getSQL() {
			return "select classNameId, classPK from KBComment where " +
				"kbCommentId = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(getActivityClassName()));
			preparedStatement.setInt(2, _ADD_KB_COMMENT);
			preparedStatement.setInt(3, _UPDATE_KB_COMMENT);
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			preparedStatement.setLong(1, classPK);
		}

		private static final int _ADD_KB_COMMENT = 5;

		private static final int _UPDATE_KB_COMMENT = 6;

		private final KBArticleExtraDataFactory _kbArticleExtraDataFactory =
			new KBArticleExtraDataFactory();
		private final KBTemplateExtraDataFactory _kbTemplateExtraDataFactory =
			new KBTemplateExtraDataFactory();

	}

	private class KBTemplateExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			return JSONUtil.put("title", resultSet.getString("title"));
		}

		@Override
		public String getActivityClassName() {
			return "com.liferay.knowledgebase.model.KBTemplate";
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "classNameId = ? and (type_ = ? or type_ = ?)";
		}

		@Override
		public String getSQL() {
			return "select title from KBTemplate where kbTemplateId = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(getActivityClassName()));
			preparedStatement.setInt(2, _ADD_KB_TEMPLATE);
			preparedStatement.setInt(3, _UPDATE_KB_TEMPLATE);
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			preparedStatement.setLong(1, classPK);
		}

		private static final int _ADD_KB_TEMPLATE = 2;

		private static final int _UPDATE_KB_TEMPLATE = 4;

	}

	private class WikiPageExtraDataFactory implements ExtraDataFactory {

		@Override
		public JSONObject createExtraDataJSONObject(
				ResultSet resultSet, String extraData)
			throws SQLException {

			return JSONUtil.put(
				"title", resultSet.getString("title")
			).put(
				"version", resultSet.getDouble("version")
			);
		}

		@Override
		public String getActivityClassName() {
			return "com.liferay.portlet.wiki.model.WikiPage";
		}

		@Override
		public String getActivitySQLWhereClause() {
			return "classNameId = ? and (type_ = ? or type_ = ?)";
		}

		@Override
		public String getSQL() {
			return "select title, version from WikiPage where companyId = ? " +
				"and groupId = ? and resourcePrimKey = ? and head = ?";
		}

		@Override
		public void setActivitySQLParameters(
				PreparedStatement preparedStatement)
			throws SQLException {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(getActivityClassName()));
			preparedStatement.setInt(2, _ADD_PAGE);
			preparedStatement.setInt(3, _UPDATE_PAGE);
		}

		@Override
		public void setModelSQLParameters(
				PreparedStatement preparedStatement, long companyId,
				long groupId, long userId, long classNameId, long classPK,
				int type, String extraData)
			throws SQLException {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setLong(2, groupId);
			preparedStatement.setLong(3, classPK);
			preparedStatement.setBoolean(4, true);
		}

		private static final int _ADD_PAGE = 1;

		private static final int _UPDATE_PAGE = 2;

	}

}