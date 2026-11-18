/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.upgrade.v6_6_1;

import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Mikel Lorza
 */
public class DLFileEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select DLFileEntry.ctCollectionId, ",
					"DLFileEntry.fileEntryId, DLFileEntry.groupId, ",
					"DLFolder.name from DLFileEntry inner join DLFolder on ",
					"DLFolder.ctCollectionId = DLFileEntry.ctCollectionId and ",
					"DLFolder.folderId = DLFileEntry.folderId where ",
					"DLFileEntry.classNameId = 0 and DLFileEntry.classPK = 0 ",
					"and DLFileEntry.repositoryId in (select repositoryId ",
					"from Repository where portletId = ?)"));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select messageId from MBMessage where ctCollectionId = ? " +
					"and messageId = ? and groupId = ?");
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update DLFileEntry set classNameId = ?, classPK = ? " +
						"where ctCollectionId = ? and fileEntryId = ?")) {

			preparedStatement1.setString(1, MBConstants.SERVICE_NAME);

			try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
				while (resultSet1.next()) {
					long messageId = GetterUtil.getLong(
						resultSet1.getString("name"));

					if (messageId == 0) {
						continue;
					}

					preparedStatement2.setLong(
						1, resultSet1.getLong("ctCollectionId"));
					preparedStatement2.setLong(2, messageId);
					preparedStatement2.setLong(
						3, resultSet1.getLong("groupId"));

					try (ResultSet resultSet2 =
							preparedStatement2.executeQuery()) {

						if (!resultSet2.next()) {
							continue;
						}
					}

					preparedStatement3.setLong(
						1,
						PortalUtil.getClassNameId(MBMessage.class.getName()));
					preparedStatement3.setLong(2, messageId);
					preparedStatement3.setLong(
						3, resultSet1.getLong("ctCollectionId"));
					preparedStatement3.setLong(
						4, resultSet1.getLong("fileEntryId"));

					preparedStatement3.addBatch();
				}
			}

			preparedStatement3.executeBatch();
		}
	}

}