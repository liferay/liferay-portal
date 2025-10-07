/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.TreeModel;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.tree.TreeModelTasksAdapter;
import com.liferay.portal.kernel.tree.TreePathUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Ju
 * @author Máté Thurzó
 * @author Alexander Chow
 * @author Roberto Díaz
 */
public class UpgradeDocumentLibrary extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {

		// DLFileEntryType

		alterTableAddColumn(
			"DLFileEntryType", "fileEntryTypeKey", "VARCHAR(75) null");
		alterColumnType("DLFileEntryType", "name", "STRING null");

		updateFileEntryTypes();

		// DLFolder

		updateDLFolderUserName();

		// Tree path

		updateTreePath();
	}

	protected String getUserName(long userId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select firstName, middleName, lastName from User_ where " +
					"userId = ?")) {

			preparedStatement.setLong(1, userId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String firstName = resultSet.getString("firstName");
					String middleName = resultSet.getString("middleName");
					String lastName = resultSet.getString("lastName");

					FullNameGenerator fullNameGenerator =
						FullNameGeneratorFactory.getInstance();

					return fullNameGenerator.getFullName(
						firstName, middleName, lastName);
				}

				return StringPool.BLANK;
			}
		}
	}

	protected String localize(long companyId, String content, String key)
		throws Exception {

		String languageId = UpgradeProcessUtil.getDefaultLanguageId(companyId);

		return LocalizationUtil.updateLocalization(
			HashMapBuilder.put(
				LocaleUtil.fromLanguageId(languageId), content
			).build(),
			StringPool.BLANK, key, languageId);
	}

	protected void rebuildTree(
			long companyId, PreparedStatement folderPreparedStatement,
			PreparedStatement fileEntryPreparedStatement,
			PreparedStatement fileShortcutPreparedStatement,
			PreparedStatement fileVersionPreparedStatement)
		throws PortalException {

		TreePathUtil.rebuildTree(
			companyId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringPool.SLASH,
			new TreeModelTasksAdapter<DLFolderTreeModel>() {

				@Override
				public List<DLFolderTreeModel> findTreeModels(
					long previousId, long companyId, long parentPrimaryKey,
					int size) {

					List<DLFolderTreeModel> treeModels = new ArrayList<>();

					try (PreparedStatement preparedStatement =
							connection.prepareStatement(
								_SELECT_DLFOLDER_BY_PARENT)) {

						preparedStatement.setLong(1, previousId);
						preparedStatement.setLong(2, companyId);
						preparedStatement.setLong(3, parentPrimaryKey);
						preparedStatement.setInt(
							4, WorkflowConstants.STATUS_IN_TRASH);
						preparedStatement.setFetchSize(size);

						try (ResultSet resultSet =
								preparedStatement.executeQuery()) {

							while (resultSet.next()) {
								long folderId = resultSet.getLong(1);

								DLFolderTreeModel treeModel =
									new DLFolderTreeModel(
										folderPreparedStatement);

								treeModel.setPrimaryKeyObj(folderId);

								treeModels.add(treeModel);
							}
						}
					}
					catch (SQLException sqlException) {
						_log.error(
							"Unable to get folders with parent primary key " +
								parentPrimaryKey,
							sqlException);
					}

					return treeModels;
				}

				@Override
				public void rebuildDependentModelsTreePaths(
						long parentPrimaryKey, String treePath)
					throws PortalException {

					try {
						fileEntryPreparedStatement.setString(1, treePath);
						fileEntryPreparedStatement.setLong(2, parentPrimaryKey);

						fileEntryPreparedStatement.addBatch();
					}
					catch (SQLException sqlException) {
						_log.error(
							"Unable to update file entries with tree path " +
								treePath,
							sqlException);
					}

					try {
						fileShortcutPreparedStatement.setString(1, treePath);
						fileShortcutPreparedStatement.setLong(
							2, parentPrimaryKey);

						fileShortcutPreparedStatement.addBatch();
					}
					catch (SQLException sqlException) {
						_log.error(
							"Unable to update file shortcuts with tree path " +
								treePath,
							sqlException);
					}

					try {
						fileVersionPreparedStatement.setString(1, treePath);
						fileVersionPreparedStatement.setLong(
							2, parentPrimaryKey);

						fileVersionPreparedStatement.addBatch();
					}
					catch (SQLException sqlException) {
						_log.error(
							"Unable to update file versions with tree path " +
								treePath,
							sqlException);
					}
				}

			});
	}

	protected void updateDLFolderUserName() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select distinct userId from DLFolder where userName is null " +
					"or userName = ''");
			ResultSet resultSet = preparedStatement1.executeQuery();
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update DLFolder set userName = ? where userId = ? and " +
						"(userName is null or userName = '')")) {

			while (resultSet.next()) {
				long userId = resultSet.getLong("userId");

				String userName = getUserName(userId);

				if (Validator.isNotNull(userName)) {
					preparedStatement2.setString(1, userName);
					preparedStatement2.setLong(2, userId);

					preparedStatement2.addBatch();
				}
				else {
					if (_log.isInfoEnabled()) {
						_log.info("User " + userId + " does not exist");
					}
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	protected void updateFileEntryType(
			long fileEntryTypeId, long companyId, String fileEntryTypeKey,
			String name, String description)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update DLFileEntryType set fileEntryTypeKey = ?, name = ?, " +
					"description = ? where fileEntryTypeId = ?")) {

			preparedStatement.setString(1, fileEntryTypeKey);
			preparedStatement.setString(2, localize(companyId, name, "Name"));
			preparedStatement.setString(
				3, localize(companyId, description, "Description"));
			preparedStatement.setLong(4, fileEntryTypeId);

			preparedStatement.executeUpdate();
		}
	}

	protected void updateFileEntryTypes() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select fileEntryTypeId, companyId, name, description from " +
					"DLFileEntryType");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long fileEntryTypeId = resultSet.getLong("fileEntryTypeId");
				long companyId = resultSet.getLong("companyId");
				String name = GetterUtil.getString(resultSet.getString("name"));
				String description = resultSet.getString("description");

				if (fileEntryTypeId ==
						DLFileEntryTypeConstants.
							FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

					name = DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT;
				}

				updateFileEntryType(
					fileEntryTypeId, companyId, StringUtil.toUpperCase(name),
					name, description);
			}
		}
	}

	protected void updateTreePath() throws Exception {
		_runSQL("create index IX_LPP_41834_YGUA on DLFileEntry (folderId);");

		_runSQL("create index IX_LPP_41834_UQAZ on DLFileShortcut (folderId);");

		_runSQL("create index IX_LPP_41834_ABGC on DLFileVersion (folderId);");

		_runSQL(
			"create index IX_LPP_41834_PWBE on DLFolder (companyId, " +
				"parentFolderId, status);");
		_runSQL("create index IX_LPP_41834_EYIW on DLFolder (userId);");

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			long[] companyIds = PortalInstancePool.getCompanyIds();

			for (long companyId : companyIds) {
				try (PreparedStatement folderPreparedStatement =
						AutoBatchPreparedStatementUtil.concurrentAutoBatch(
							connection,
							"update DLFolder set treePath = ? where folderId " +
								"= ?");
					PreparedStatement fileEntryPreparedStatement =
						AutoBatchPreparedStatementUtil.concurrentAutoBatch(
							connection,
							"update DLFileEntry set treePath = ? where " +
								"folderId = ?");
					PreparedStatement fileShortcutPreparedStatement =
						AutoBatchPreparedStatementUtil.concurrentAutoBatch(
							connection,
							"update DLFileShortcut set treePath = ? where " +
								"folderId = ?");
					PreparedStatement fileVersionPreparedStatement =
						AutoBatchPreparedStatementUtil.concurrentAutoBatch(
							connection,
							"update DLFileVersion set treePath = ? where " +
								"folderId = ?")) {

					try {
						rebuildTree(
							companyId, folderPreparedStatement,
							fileEntryPreparedStatement,
							fileShortcutPreparedStatement,
							fileVersionPreparedStatement);
					}
					catch (PortalException portalException) {
						_log.error(
							"Unable to update tree paths for company " +
								companyId,
							portalException);
					}

					folderPreparedStatement.executeBatch();

					fileEntryPreparedStatement.executeBatch();

					fileShortcutPreparedStatement.executeBatch();

					fileVersionPreparedStatement.executeBatch();
				}
			}
		}
		catch (SQLException sqlException) {
			_log.error("Unable to update tree paths", sqlException);
		}
	}

	private void _runSQL(String sql) throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer(sql)) {
			runSQL(sql);
		}
	}

	private static final String _SELECT_DLFOLDER_BY_PARENT =
		"select folderId from DLFolder dlFolder where dlFolder.folderId > ? " +
			"and dlFolder.companyId = ? and dlFolder.parentFolderId = ? and " +
				"dlFolder.status != ?";

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradeDocumentLibrary.class);

	private class DLFolderTreeModel implements TreeModel {

		public DLFolderTreeModel(PreparedStatement preparedStatement) {
			_preparedStatement = preparedStatement;
		}

		@Override
		public String buildTreePath() throws PortalException {
			return null;
		}

		@Override
		public Serializable getPrimaryKeyObj() {
			return _folderId;
		}

		@Override
		public String getTreePath() {
			return null;
		}

		public void setPrimaryKeyObj(Serializable primaryKeyObj) {
			_folderId = (Long)primaryKeyObj;
		}

		@Override
		public void updateTreePath(String treePath) {
			try {
				_preparedStatement.setString(1, treePath);
				_preparedStatement.setLong(2, _folderId);

				_preparedStatement.addBatch();
			}
			catch (SQLException sqlException) {
				_log.error(
					"Unable to update tree path: " + treePath, sqlException);
			}
		}

		private long _folderId;
		private final PreparedStatement _preparedStatement;

	}

}