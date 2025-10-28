/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v0_0_8;

import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * @author Preston Crary
 * @author Alberto Chaparro
 */
public class ArticleExpirationDateUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateArticleExpirationDate();
	}

	private void _updateArticleExpirationDate() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			JournalServiceConfiguration journalServiceConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			if (!journalServiceConfiguration.
					expireAllArticleVersionsEnabled()) {

				return;
			}

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select JournalArticle.* from JournalArticle left ",
							"join JournalArticle tempJournalArticle on ",
							"(JournalArticle.groupId = tempJournalArticle.",
							"groupId) and (JournalArticle.articleId = ",
							"tempJournalArticle.articleId) and (",
							"JournalArticle.version < tempJournalArticle.",
							"version) and (JournalArticle.status = ",
							"tempJournalArticle.status) where (JournalArticle.",
							"classNameId = ?) and (tempJournalArticle.version ",
							"is null) and (JournalArticle.expirationDate is ",
							"not null) and (JournalArticle.status = ?)"))) {

				preparedStatement.setLong(
					1, JournalArticleConstants.CLASS_NAME_ID_DEFAULT);
				preparedStatement.setInt(2, WorkflowConstants.STATUS_APPROVED);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						long groupId = resultSet.getLong("groupId");
						String articleId = resultSet.getString("articleId");
						Timestamp expirationDate = resultSet.getTimestamp(
							"expirationDate");
						int status = resultSet.getInt("status");

						_updateExpirationDate(
							groupId, articleId, expirationDate, status);
					}
				}
			}
		}
	}

	private void _updateExpirationDate(
			long groupId, String articleId, Timestamp expirationDate,
			int status)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update JournalArticle set expirationDate = ? where groupId " +
					"= ? and articleId = ? and status = ?")) {

			preparedStatement.setTimestamp(1, expirationDate);
			preparedStatement.setLong(2, groupId);
			preparedStatement.setString(3, articleId);
			preparedStatement.setInt(4, status);

			preparedStatement.executeUpdate();
		}
	}

}