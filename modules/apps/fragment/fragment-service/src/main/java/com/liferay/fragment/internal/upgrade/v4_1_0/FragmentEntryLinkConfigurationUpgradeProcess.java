/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v4_1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentEntryLinkConfigurationUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update FragmentEntryLink set configuration = ? where ",
						"rendererKey = ? and (configuration is null or ",
						"configuration = '' or configuration = '{}' or ",
						"configuration = '[]')"))) {

			_addBatch(
				preparedStatement,
				"com.liferay.fragment.internal.renderer." +
					"ContentFlagsFragmentRenderer",
				"content_flags_configuration");
			_addBatch(
				preparedStatement,
				"com.liferay.fragment.internal.renderer." +
					"ContentObjectFragmentRenderer",
				"content_object_configuration");
			_addBatch(
				preparedStatement,
				"com.liferay.fragment.internal.renderer." +
					"ContentRatingsFragmentRenderer",
				"content_ratings_configuration");
			_addBatch(
				preparedStatement,
				"com.liferay.fragment.renderer.menu.display.internal." +
					"MenuDisplayFragmentRenderer",
				"menu_display_configuration");

			preparedStatement.executeBatch();
		}
	}

	private void _addBatch(
			PreparedStatement preparedStatement, String rendererKey,
			String resourceName)
		throws Exception {

		preparedStatement.setString(
			1,
			StringUtil.read(
				FragmentEntryLinkConfigurationUpgradeProcess.class.
					getResourceAsStream(
						"dependencies/" + resourceName + ".json")));
		preparedStatement.setString(2, rendererKey);

		preparedStatement.addBatch();
	}

}