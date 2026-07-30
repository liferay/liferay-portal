/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v4_1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;

import java.util.Map;

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

			Map<String, String> rendererKeyResourceNames = HashMapBuilder.put(
				"com.liferay.fragment.internal.renderer." +
					"ContentFlagsFragmentRenderer",
				"dependencies/content_flags_configuration.json"
			).put(
				"com.liferay.fragment.internal.renderer." +
					"ContentObjectFragmentRenderer",
				"dependencies/content_object_configuration.json"
			).put(
				"com.liferay.fragment.internal.renderer." +
					"ContentRatingsFragmentRenderer",
				"dependencies/content_ratings_configuration.json"
			).put(
				"com.liferay.fragment.renderer.menu.display.internal." +
					"MenuDisplayFragmentRenderer",
				"dependencies/menu_display_configuration.json"
			).build();

			for (Map.Entry<String, String> entry :
					rendererKeyResourceNames.entrySet()) {

				preparedStatement.setString(
					1,
					StringUtil.read(
						FragmentEntryLinkConfigurationUpgradeProcess.class.
							getResourceAsStream(entry.getValue())));
				preparedStatement.setString(2, entry.getKey());

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

}