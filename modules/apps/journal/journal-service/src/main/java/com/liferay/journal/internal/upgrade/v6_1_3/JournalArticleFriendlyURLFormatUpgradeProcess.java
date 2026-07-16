/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_3;

import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author João Victor Alves
 */
public class JournalArticleFriendlyURLFormatUpgradeProcess
	extends UpgradeProcess {

	public JournalArticleFriendlyURLFormatUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService) {

		_classNameLocalService = classNameLocalService;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select distinct ctCollectionId, friendlyURLEntryId, ",
					"languageId, urlTitle, groupId, classPK from ",
					"FriendlyURLEntryLocalization where classNameId = ?"));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"select defaultLanguageId from JournalArticle where " +
					"resourcePrimKey = ?")) {

			long classNameId = _classNameLocalService.getClassNameId(
				JournalArticle.class);

			preparedStatement1.setLong(1, classNameId);

			ResultSet resultSet1 = preparedStatement1.executeQuery();

			while (resultSet1.next()) {
				long classPK = resultSet1.getLong("classPK");
				long groupId = resultSet1.getLong("groupId");
				String languageId = resultSet1.getString("languageId");

				String urlTitle = resultSet1.getString("urlTitle");

				while (urlTitle.endsWith(StringPool.SLASH)) {
					urlTitle = urlTitle.substring(0, urlTitle.length() - 1);
				}

				urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
					groupId, classNameId, classPK, urlTitle, languageId);

				preparedStatement2.setLong(1, classPK);

				ResultSet resultSet2 = preparedStatement2.executeQuery();

				if (resultSet2.next()) {
					String defaultLanguageId = resultSet2.getString(
						"defaultLanguageId");

					if (defaultLanguageId.equals(languageId)) {
						_updateURLTitle(
							classPK, resultSet1.getLong("ctCollectionId"),
							urlTitle);
					}

					_updateFriendlyURLEntry(
						resultSet1.getLong("friendlyURLEntryId"), languageId,
						urlTitle);
				}
			}
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
			long classPK, long ctCollectionId, String urlTitle)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update JournalArticle set urlTitle = ? where ctCollectionId " +
					"= ? and resourcePrimKey = ?")) {

			preparedStatement.setString(1, urlTitle);
			preparedStatement.setLong(2, ctCollectionId);
			preparedStatement.setLong(3, classPK);

			preparedStatement.executeUpdate();
		}
	}

	private final ClassNameLocalService _classNameLocalService;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}