/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v3_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			StringBundler.concat(
				"select layout.plid, styleBookEntry.externalReferenceCode ",
				"from Layout layout inner join StyleBookEntry styleBookEntry ",
				"on styleBookEntry.styleBookEntryId = layout.styleBookEntryId ",
				"where layout.styleBookEntryId > 0"),
			"update Layout set styleBookEntryERC = ? where plid = ?",
			resultSet -> new Object[] {
				resultSet.getLong("plid"),
				GetterUtil.getString(
					resultSet.getString("externalReferenceCode"))
			},
			(values, preparedStatement) -> {
				preparedStatement.setString(1, GetterUtil.getString(values[1]));
				preparedStatement.setLong(2, GetterUtil.getLong(values[0]));

				preparedStatement.addBatch();
			},
			null);
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropColumns("Layout", "styleBookEntryId")
		};
	}

}