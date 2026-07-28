/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v6_1_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Alberto Chaparro
 */
public class LayoutUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			StringBundler.concat(
				"select Layout1.externalReferenceCode, ",
				"Layout2.ctCollectionId, Layout2.plid from Group_ inner join ",
				"Layout Layout1 on Layout1.groupId = Group_.groupId inner ",
				"join Layout Layout2 on Layout2.ctCollectionId = ",
				"Layout1.ctCollectionId and Layout2.groupId = ",
				"Group_.liveGroupId and Layout2.privateLayout = ",
				"Layout1.privateLayout and Layout2.uuid_ = Layout1.uuid_ ",
				"where Group_.liveGroupId != 0 and ",
				"Layout1.externalReferenceCode is not null and ",
				"(Layout2.externalReferenceCode is null or ",
				"Layout1.externalReferenceCode != ",
				"Layout2.externalReferenceCode)"),
			"update Layout set externalReferenceCode = ? where " +
				"ctCollectionId = ? and plid = ?",
			resultSet -> new Object[] {
				resultSet.getString("externalReferenceCode"),
				resultSet.getLong("ctCollectionId"), resultSet.getLong("plid")
			},
			(values, preparedStatement) -> {
				preparedStatement.setString(1, (String)values[0]);
				preparedStatement.setLong(2, (long)values[1]);
				preparedStatement.setLong(3, (long)values[2]);

				preparedStatement.addBatch();
			},
			null);
	}

}