/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.upgrade.v1_1_0;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Adolfo Pérez
 */
public class BlogsEntryUpgradeProcess extends UpgradeProcess {

	public BlogsEntryUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService) {

		_classNameLocalService = classNameLocalService;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select groupId, entryId, urlTitle from BlogsEntry")) {

			ResultSet resultSet = preparedStatement1.executeQuery();

			while (resultSet.next()) {
				long groupId = resultSet.getLong("groupId");
				long classPK = resultSet.getLong("entryId");
				String urlTitle = resultSet.getString("urlTitle");

				long classNameId = PortalUtil.getClassNameId(
					BlogsEntry.class.getName());

				FriendlyURLEntry existingFriendlyURLEntry =
					_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
						groupId, classNameId,
						FriendlyURLEntryConstants.
							FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
						urlTitle);

				if (existingFriendlyURLEntry != null) {
					urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
						groupId, classNameId, classPK, urlTitle);
				}

				urlTitle = _getUniqueUrlTitle(classPK, groupId, urlTitle);

				_friendlyURLEntryLocalService.addFriendlyURLEntry(
					groupId, classNameId, classPK, urlTitle,
					new ServiceContext());
			}
		}
	}

	private String _getUniqueUrlTitle(
		long entryId, long groupId, String title) {

		String urlTitle = null;

		if (title == null) {
			urlTitle = String.valueOf(entryId);
		}
		else {
			urlTitle = StringUtil.toLowerCase(title.trim());

			if (Validator.isNull(urlTitle) || Validator.isNumber(urlTitle) ||
				urlTitle.equals("rss")) {

				urlTitle = String.valueOf(entryId);
			}
			else {
				urlTitle =
					FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
						urlTitle);
			}

			urlTitle = ModelHintsUtil.trimString(
				BlogsEntry.class.getName(), "urlTitle", urlTitle);
		}

		return _friendlyURLEntryLocalService.getUniqueUrlTitle(
			groupId, _classNameLocalService.getClassNameId(BlogsEntry.class),
			entryId, urlTitle);
	}

	private final ClassNameLocalService _classNameLocalService;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

}