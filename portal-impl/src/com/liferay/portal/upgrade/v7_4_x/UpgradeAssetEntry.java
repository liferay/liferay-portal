/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Alberto Chaparro
 */
public class UpgradeAssetEntry extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		StringBundler sb = new StringBundler(_CLASS_NAMES.length * 2);

		for (String className : _CLASS_NAMES) {
			long classNameId = _getClassNameId(
				"com.liferay.message.boards.model.MBDiscussion_" + className);

			if (classNameId == 0) {
				continue;
			}

			sb.append(classNameId);
			sb.append(StringPool.COMMA);
		}

		if (sb.length() == 0) {
			return;
		}

		sb.setIndex(sb.index() - 1);

		runSQL(
			"delete from AssetEntry where classNameId in (" + sb.toString() +
				")");
	}

	private long _getClassNameId(String className) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select classNameId from ClassName_ where value = ?")) {

			preparedStatement.setString(1, className);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("classNameId");
				}

				return 0;
			}
		}
	}

	private static final String[] _CLASS_NAMES = {
		"com.liferay.blogs.model.BlogsEntry",
		"com.liferay.calendar.model.CalendarBooking",
		"com.liferay.portal.kernel.model.Layout",
		"com.liferay.document.library.kernel.model.DLFileEntry",
		"com.liferay.journal.model.JournalArticle",
		"com.liferay.wiki.model.WikiPage"
	};

}