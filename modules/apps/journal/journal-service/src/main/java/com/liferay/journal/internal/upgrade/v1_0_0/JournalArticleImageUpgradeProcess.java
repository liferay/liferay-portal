/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v1_0_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Inácio Nery
 */
public class JournalArticleImageUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_deleteOrphanJournalArticleImages();

		_updateJournalArticleImagesInstanceId();

		_updateJournalArticleImagesName();
	}

	private void _deleteOrphanJournalArticleImages() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from JournalArticleImage where not exists (select 1 " +
					"from Image where JournalArticleImage.articleImageId = " +
						"Image.imageId)")) {

			preparedStatement.executeUpdate();
		}
	}

	private void _updateJournalArticleImagesInstanceId() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select articleId, elName from JournalArticleImage where " +
					"(elInstanceId = '' or elInstanceId is null) group by " +
						"articleId, elName");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			try (PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"update JournalArticleImage set elInstanceId = ? " +
							"where articleId = ? and elName = ?")) {

				while (resultSet.next()) {
					preparedStatement2.setString(1, StringUtil.randomString(4));
					preparedStatement2.setString(2, resultSet.getString(1));
					preparedStatement2.setString(3, resultSet.getString(2));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	private void _updateJournalArticleImagesName() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select articleImageId, elName from JournalArticleImage");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			try (PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"update JournalArticleImage set elName = ? where " +
							"articleImageId = ?")) {

				while (resultSet.next()) {
					String elName = resultSet.getString(2);

					int lastIndexOf = elName.lastIndexOf(StringPool.UNDERLINE);

					if (lastIndexOf < 1) {
						continue;
					}

					String index = elName.substring(lastIndexOf + 1);

					if (!Validator.isNumber(index)) {
						continue;
					}

					preparedStatement2.setString(
						1, elName.substring(0, lastIndexOf));
					preparedStatement2.setLong(2, resultSet.getLong(1));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

}