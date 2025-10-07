/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v2_5_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Danny Situ
 */
public class FriendlyURLEntryUpgradeProcess extends UpgradeProcess {

	public FriendlyURLEntryUpgradeProcess(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Override
	public void doUpgrade() throws Exception {
		if (!hasTable("CPFriendlyURLEntry")) {
			return;
		}

		String insertFriendlyUREntrySQL = StringBundler.concat(
			"insert into FriendlyURLEntry (mvccVersion, uuid_, ",
			"defaultLanguageId, friendlyURLEntryId, groupId, companyId, ",
			"createDate, modifiedDate, classNameId, classPK) values (?, ?, ?, ",
			"?, ?, ?, ?, ?, ?, ?)");

		String insertFriendlyUREntryMappingSQL = StringBundler.concat(
			"insert into FriendlyURLEntryMapping (mvccVersion, ",
			"friendlyURLEntryMappingId, classNameId, classPK, ",
			"friendlyURLEntryId) values (?, ?, ?, ?, ?)");

		String insertFriendlyUREntryLocalizationSQL = StringBundler.concat(
			"insert into FriendlyURLEntryLocalization (mvccVersion, ",
			"friendlyURLEntryLocalizationId, companyId, friendlyURLEntryId, ",
			"languageId, urlTitle, groupId, classNameId, classPK) values (?, ",
			"?, ?, ?, ?, ?, ?, ?, ?)");

		String selectCPFriendlyURLEntrySQL =
			"select * from CPFriendlyURLEntry order by main desc";

		try (PreparedStatement preparedStatement1 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, insertFriendlyUREntrySQL);
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection, insertFriendlyUREntryMappingSQL);
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				insertFriendlyUREntryLocalizationSQL);
			Statement s1 = connection.createStatement();
			ResultSet resultSet = s1.executeQuery(
				selectCPFriendlyURLEntrySQL)) {

			while (resultSet.next()) {
				long classNameId = resultSet.getLong("classNameId");
				long classPK = resultSet.getLong("classPK");
				long companyId = resultSet.getLong("companyId");
				Date date = new Date(System.currentTimeMillis());
				Group group = _groupLocalService.getCompanyGroup(companyId);
				String languageId = resultSet.getString("languageId");
				boolean main = resultSet.getBoolean("main");

				long friendlyURLEntryId = _getFriendlyURLEntryId(
					classNameId, classPK);

				if (friendlyURLEntryId <= 0) {
					friendlyURLEntryId = increment();

					preparedStatement1.setLong(1, 0);

					String uuid = resultSet.getString("uuid_");

					preparedStatement1.setString(2, uuid);

					preparedStatement1.setString(3, languageId);
					preparedStatement1.setLong(4, friendlyURLEntryId);
					preparedStatement1.setLong(5, group.getGroupId());
					preparedStatement1.setLong(6, companyId);
					preparedStatement1.setDate(7, date);
					preparedStatement1.setDate(8, date);
					preparedStatement1.setLong(9, classNameId);
					preparedStatement1.setLong(10, classPK);

					preparedStatement1.execute();
				}

				long friendlyURLEntryMappingId = _getFriendlyURLEntryMappingId(
					classNameId, classPK);

				if (main && (friendlyURLEntryMappingId <= 0)) {
					friendlyURLEntryMappingId = increment();

					preparedStatement2.setLong(1, 0);
					preparedStatement2.setLong(2, friendlyURLEntryMappingId);
					preparedStatement2.setLong(3, classNameId);
					preparedStatement2.setLong(4, classPK);
					preparedStatement2.setLong(5, friendlyURLEntryId);

					preparedStatement2.execute();
				}

				long friendlyURLEntryLocalizationId =
					_getFriendlyURLEntryLocalizationId(
						languageId, classNameId, classPK);

				if (friendlyURLEntryLocalizationId <= 0) {
					friendlyURLEntryLocalizationId = increment();

					preparedStatement3.setLong(1, 0);
					preparedStatement3.setLong(
						2, friendlyURLEntryLocalizationId);
					preparedStatement3.setLong(3, companyId);
					preparedStatement3.setLong(4, friendlyURLEntryId);
					preparedStatement3.setString(5, languageId);

					String urlTitle = resultSet.getString("urlTitle");

					urlTitle = _getUniqueURLTitle(
						group.getGroupId(), classNameId, urlTitle);

					preparedStatement3.setString(6, urlTitle);

					preparedStatement3.setLong(7, group.getGroupId());
					preparedStatement3.setLong(8, classNameId);
					preparedStatement3.setLong(9, classPK);

					preparedStatement3.execute();
				}
			}
		}
	}

	private long _getFriendlyURLEntryId(long classNameId, long classPK)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from FriendlyURLEntry where classNameId = ? and " +
					"classPK = ?")) {

			preparedStatement.setLong(1, classNameId);
			preparedStatement.setLong(2, classPK);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					return resultSet.getLong("friendlyURLEntryId");
				}

				return 0;
			}
		}
	}

	private long _getFriendlyURLEntryLocalizationId(
			String languageId, long classNameId, long classPK)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from FriendlyURLEntryLocalization where languageId " +
					"= ? and classNameId = ? and classPK = ?")) {

			preparedStatement.setString(1, languageId);
			preparedStatement.setLong(2, classNameId);
			preparedStatement.setLong(3, classPK);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					return resultSet.getLong("friendlyURLEntryLocalizationId");
				}

				return 0;
			}
		}
	}

	private long _getFriendlyURLEntryMappingId(long classNameId, long classPK)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from FriendlyURLEntryMapping where classNameId = ? " +
					"and classPK = ?")) {

			preparedStatement.setLong(1, classNameId);
			preparedStatement.setLong(2, classPK);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					return resultSet.getLong("friendlyURLEntryMappingId");
				}

				return 0;
			}
		}
	}

	private String _getUniqueURLTitle(
			long groupId, long classNameId, String urlTitle)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from FriendlyURLEntryLocalization where groupId = " +
					"? and classNameId = ? and urlTitle = ?")) {

			preparedStatement.setLong(1, groupId);
			preparedStatement.setLong(2, classNameId);
			preparedStatement.setString(3, urlTitle);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					return urlTitle + StringPool.DASH +
						PortalUUIDUtil.generate();
				}

				return urlTitle;
			}
		}
	}

	private final GroupLocalService _groupLocalService;

}