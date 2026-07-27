/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.upgrade.v3_1_1;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author João Victor Alves
 */
public class BlogsFriendlyURLFormatUpgradeProcess extends UpgradeProcess {

	public BlogsFriendlyURLFormatUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService) {

		_classNameLocalService = classNameLocalService;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select distinct ctCollectionId, friendlyURLEntryId, ",
					"languageId, urlTitle, groupId, classPK from ",
					"FriendlyURLEntryLocalization where classNameId = ?"));
			PreparedStatement updatePreparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update BlogsEntry set urlTitle = ? where ctCollectionId " +
						"= ? and entryId = ? and groupId = ?")) {

			long classNameId = _classNameLocalService.getClassNameId(
				BlogsEntry.class);

			preparedStatement.setLong(1, classNameId);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				long classPK = resultSet.getLong("classPK");
				long groupId = resultSet.getLong("groupId");
				String languageId = resultSet.getString("languageId");

				String urlTitle = resultSet.getString("urlTitle");

				String originalUrlTitle = urlTitle;

				while (urlTitle.endsWith(StringPool.SLASH)) {
					urlTitle = urlTitle.substring(0, urlTitle.length() - 1);
				}

				urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
					groupId, classNameId, classPK, urlTitle, languageId);

				if (StringUtil.equals(originalUrlTitle, urlTitle)) {
					continue;
				}

				_updateURLTitle(
					classPK, resultSet.getLong("ctCollectionId"), groupId,
					updatePreparedStatement, urlTitle);

				_updateFriendlyURLEntry(
					resultSet.getLong("friendlyURLEntryId"), languageId,
					urlTitle);
			}

			updatePreparedStatement.executeBatch();
		}
	}

	private void _updateFriendlyURLEntry(
			long friendlyURLEntryId, String languageId, String urlTitle)
		throws PortalException {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				friendlyURLEntryId);

		_friendlyURLEntryLocalService.updateFriendlyURLEntryLocalization(
			friendlyURLEntry, languageId, urlTitle);
	}

	private void _updateURLTitle(
			long classPK, long ctCollectionId, long groupId,
			PreparedStatement preparedStatement, String urlTitle)
		throws Exception {

		preparedStatement.setString(1, urlTitle);
		preparedStatement.setLong(2, ctCollectionId);
		preparedStatement.setLong(3, classPK);
		preparedStatement.setLong(4, groupId);

		preparedStatement.addBatch();
	}

	private final ClassNameLocalService _classNameLocalService;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}